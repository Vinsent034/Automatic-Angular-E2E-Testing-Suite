package org.unina.data;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/**
 * Import/export di insiemi di mutanti fra la base di dati e il file system.
 *
 * La persistenza su SQLite rende i mutanti opachi: per ispezionarli, correggerli o
 * personalizzarli occorre passare per una interrogazione SQL. Questa classe disaccoppia
 * il contenuto dal supporto, esportando ogni mutazione in un file di testo autodescrittivo
 * che puo' essere aperto in un qualsiasi editor, modificato e reimportato.
 *
 * Il formato riusa la convenzione a blocchi delimitati gia' adottata dal generatore LLM,
 * cosi' da restare leggibile sia da un essere umano sia da uno script:
 *
 * <pre>
 * #mutation-id: LLMR_board_component_html_board_title_alf_a
 * #element: board_component_html
 * #name: a
 * #type: LLM_ROLE
 * ######### FILE C:/path/to/component.html #########
 * ...codice mutato...
 * ######### END FILE #########
 * </pre>
 *
 * Uso da riga di comando (la directory di lavoro deve contenere mutations.db):
 * <pre>
 *   java -cp common.jar org.unina.data.MutationPorter export &lt;dir&gt; [tipo]
 *   java -cp common.jar org.unina.data.MutationPorter import &lt;dir&gt;
 * </pre>
 */
public final class MutationPorter {

    private static final String DB_URL = "jdbc:sqlite:mutations.db";
    private static final String FILE_START = "######### FILE ";
    private static final String FILE_END = "######### END FILE #########";
    private static final String DELIM_SUFFIX = " #########";
    private static final String MANIFEST = "manifest.csv";

    private MutationPorter() {}

    // ---------------------------------------------------------------- export

    /** Esporta tutte le mutazioni presenti nel database. */
    public static int export(Path outDir) throws IOException {
        return export(outDir, null);
    }

    /**
     * Esporta le mutazioni, opzionalmente filtrando per tipo (es. "LLM_ROLE", "STATIC").
     *
     * @return il numero di mutazioni esportate
     */
    public static int export(Path outDir, String mutationType) throws IOException {
        Files.createDirectories(outDir);

        String sql = "SELECT uuid, element, mutation_name, mutation_type, mutation_id, status FROM mutations"
                + (mutationType != null ? " WHERE mutation_type = ?" : "")
                + " ORDER BY element, mutation_id";

        StringBuilder manifest = new StringBuilder("MutationId,Element,Name,Type,Status,Files,ExportFile\n");
        int exported = 0;
        // Nella generazione statica lo stesso mutation_id ricorre su bersagli diversi:
        // il nome del file include quindi l’elemento, e un contatore risolve i casi residui.
        Set<String> usedNames = new HashSet<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (mutationType != null) ps.setString(1, mutationType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String uuid = rs.getString("uuid");
                    String element = nz(rs.getString("element"));
                    String name = nz(rs.getString("mutation_name"));
                    String type = nz(rs.getString("mutation_type"));
                    String mid = nz(rs.getString("mutation_id"));
                    String status = nz(rs.getString("status"));

                    List<MutatedFile> files = filesOf(conn, uuid);

                    StringBuilder body = new StringBuilder();
                    body.append("#mutation-id: ").append(mid).append('\n');
                    body.append("#element: ").append(element).append('\n');
                    body.append("#name: ").append(name).append('\n');
                    body.append("#type: ").append(type).append('\n');
                    body.append("#status: ").append(status).append('\n');
                    for (MutatedFile f : files) {
                        body.append(FILE_START).append(f.filePath).append(DELIM_SUFFIX).append('\n');
                        body.append(f.mutatedCode);
                        if (!f.mutatedCode.endsWith("\n")) body.append('\n');
                        body.append(FILE_END).append('\n');
                    }

                    String base = slug((element.isEmpty() ? "mutation" : element)
                            + "__" + (mid.isEmpty() ? uuid : mid));
                    String fileName = base + ".mut";
                    for (int dup = 2; !usedNames.add(fileName); dup++) {
                        fileName = base + "__" + dup + ".mut";
                    }
                    Files.writeString(outDir.resolve(fileName), body.toString(), StandardCharsets.UTF_8);

                    manifest.append(csv(mid)).append(',').append(csv(element)).append(',')
                            .append(csv(name)).append(',').append(csv(type)).append(',')
                            .append(csv(status)).append(',').append(files.size()).append(',')
                            .append(csv(fileName)).append('\n');
                    exported++;
                }
            }
        } catch (SQLException e) {
            throw new UncheckedIOException(new IOException("Export fallito: " + e.getMessage(), e));
        }

        Files.writeString(outDir.resolve(MANIFEST), manifest.toString(), StandardCharsets.UTF_8);
        return exported;
    }

    private static List<MutatedFile> filesOf(Connection conn, String mutationUuid) throws SQLException {
        List<MutatedFile> out = new ArrayList<>();
        String sql = "SELECT target_file_path, mutated_code FROM mutated_files WHERE mutation_uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mutationUuid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MutatedFile f = new MutatedFile();
                    f.filePath = rs.getString("target_file_path");
                    f.mutatedCode = rs.getString("mutated_code");
                    out.add(f);
                }
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- import

    /**
     * Reimporta le mutazioni contenute nella directory indicata (file con estensione .mut).
     * Una mutazione il cui mutation_id sia gia' presente viene sostituita, cosi' che il ciclo
     * esporta - modifica - reimporta aggiorni il contenuto senza generare duplicati.
     *
     * @return il numero di mutazioni importate o aggiornate
     */
    public static int importFrom(Path inDir) throws IOException {
        if (!Files.isDirectory(inDir)) {
            throw new IOException("Directory non trovata: " + inDir);
        }

        List<Path> sources;
        try (var stream = Files.list(inDir)) {
            sources = stream.filter(p -> p.toString().endsWith(".mut")).sorted().toList();
        }

        int imported = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try {
                for (Path p : sources) {
                    if (upsert(conn, parse(Files.readString(p, StandardCharsets.UTF_8)))) imported++;
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new IOException("Import fallito: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new IOException("Import fallito: " + e.getMessage(), e);
        }
        return imported;
    }

    /** Rappresentazione in memoria di un file .mut. */
    private record Parsed(String mutationId, String element, String name, String type,
                          String status, List<MutatedFile> files) {}

    private static Parsed parse(String text) throws IOException {
        String mid = "", element = "", name = "", type = "", status = "PENDING";
        List<MutatedFile> files = new ArrayList<>();

        // Gli editor Windows (Blocco note, PowerShell) salvano spesso in UTF-8 con BOM: poiche'
        // questi file sono pensati per essere modificati a mano, il marcatore va tollerato.
        if (!text.isEmpty() && text.charAt(0) == '﻿') text = text.substring(1);

        String[] lines = text.split("\r?\n", -1);
        int i = 0;
        for (; i < lines.length; i++) {
            String l = lines[i];
            if (l.startsWith("#mutation-id:")) mid = l.substring(13).trim();
            else if (l.startsWith("#element:")) element = l.substring(9).trim();
            else if (l.startsWith("#name:")) name = l.substring(6).trim();
            else if (l.startsWith("#type:")) type = l.substring(6).trim();
            else if (l.startsWith("#status:")) status = l.substring(8).trim();
            else if (l.startsWith(FILE_START)) break;
        }

        while (i < lines.length) {
            if (!lines[i].startsWith(FILE_START)) { i++; continue; }
            String header = lines[i];
            String path = header.substring(FILE_START.length());
            if (path.endsWith(DELIM_SUFFIX)) path = path.substring(0, path.length() - DELIM_SUFFIX.length());
            path = path.trim();

            StringBuilder code = new StringBuilder();
            i++;
            boolean closed = false;
            while (i < lines.length) {
                if (lines[i].equals(FILE_END)) { closed = true; i++; break; }
                code.append(lines[i]).append('\n');
                i++;
            }
            if (!closed) throw new IOException("Blocco FILE non chiuso per " + path);

            MutatedFile f = new MutatedFile();
            f.filePath = path;
            f.mutatedCode = code.toString();
            files.add(f);
        }

        if (mid.isEmpty()) throw new IOException("Manca l'intestazione #mutation-id");
        return new Parsed(mid, element, name, type, status, files);
    }

    private static boolean upsert(Connection conn, Parsed p) throws SQLException {
        String uuid = null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT uuid FROM mutations WHERE mutation_id = ?")) {
            ps.setString(1, p.mutationId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) uuid = rs.getString(1);
            }
        }

        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO mutations(uuid, element, mutation_name, mutation_type, mutation_id, status)"
                            + " VALUES(?,?,?,?,?,?)")) {
                ps.setString(1, uuid);
                ps.setString(2, p.element());
                ps.setString(3, p.name());
                ps.setString(4, p.type());
                ps.setString(5, p.mutationId());
                ps.setString(6, p.status());
                ps.executeUpdate();
            }
        } else {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE mutations SET element=?, mutation_name=?, mutation_type=?, status=? WHERE uuid=?")) {
                ps.setString(1, p.element());
                ps.setString(2, p.name());
                ps.setString(3, p.type());
                ps.setString(4, p.status());
                ps.setString(5, uuid);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM mutated_files WHERE mutation_uuid = ?")) {
                ps.setString(1, uuid);
                ps.executeUpdate();
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO mutated_files(uuid, target_file_path, mutated_code, mutation_uuid) VALUES(?,?,?,?)")) {
            for (MutatedFile f : p.files()) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, f.filePath);
                ps.setString(3, f.mutatedCode);
                ps.setString(4, uuid);
                ps.executeUpdate();
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- utility

    private static String nz(String s) { return s == null ? "" : s; }

    private static String slug(String s) {
        String out = s.replaceAll("[^A-Za-z0-9._-]", "_");
        return out.length() > 120 ? out.substring(0, 120) : out;
    }

    private static String csv(String s) {
        if (s == null) return "";
        return s.contains(",") || s.contains("\"") ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }

    // ---------------------------------------------------------------- CLI

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Uso: MutationPorter export <dir> [tipo] | import <dir>");
            System.exit(2);
        }
        Path dir = Paths.get(args[1]);
        switch (args[0]) {
            case "export" -> {
                int n = export(dir, args.length > 2 ? args[2] : null);
                System.out.printf("Esportate %d mutazioni in %s%n", n, dir.toAbsolutePath());
            }
            case "import" -> {
                int n = importFrom(dir);
                System.out.printf("Importate/aggiornate %d mutazioni da %s%n", n, dir.toAbsolutePath());
            }
            default -> {
                System.err.println("Comando non riconosciuto: " + args[0]);
                System.exit(2);
            }
        }
    }
}
