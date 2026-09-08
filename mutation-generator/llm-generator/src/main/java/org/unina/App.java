package org.unina;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.unina.data.Config;
import org.unina.data.ElementExtension;
import org.unina.data.MutationConfig;
import org.unina.data.MutationDatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LLM mutation generator (Groq / Llama, or pre-generated ingest files).
 *
 * INDEPENDENT generation: the LLM receives only the full template and decides on its own
 * what to change and where — no target element, no list of edit types is dictated (so the
 * mutations are genuinely model-generated, not rule-driven). Generation is per TEMPLATE FILE;
 * the association to a test target emerges later from which locators break.
 *
 * Each surviving variant is written into the SAME schema as the static generator so the
 * mutation-tester runs them identically. Compilation is NOT verified here: the mutation-tester
 * marks non-recompiling mutants as NOT_APPLICABLE downstream.
 *
 * Generates ONLY the LLM set, then stops (see HANDOFF checkpoint).
 */
public class App {

    private static final String MUTATION_TYPE = "LLM_GENERATED";
    private static final String MUTATION_TYPE_PARAM = "LLM_PARAMETRIC";
    private static final String MUTATION_TYPE_ROLE = "LLM_ROLE";
    private static final String DB_URL = "jdbc:sqlite:mutations.db";

    // The 5 structural roles of the 11x5 taxonomy (same names/definitions as the static generator,
    // see MutationEngine.initializeTargets + ElementExtension). Short codes go into mutation ids.
    private static final String[][] ROLES = {
            {"alpha-target", "alf"},
            {"beta-parent", "bet"},
            {"gamma-ancestor", "gam"},
            {"delta-sibling", "del"},
            {"epsilon-component", "eps"},
    };

    // ######### START {n} - {label} #########  ...  ######### END {n} - {label} #########
    private static final Pattern BLOCK = Pattern.compile(
            "#{3,}\\s*START\\s+(\\d+)\\s*-\\s*([^\\n#]*?)\\s*#{3,}(.*?)#{3,}\\s*END\\s+\\1\\b[^#]*#{3,}",
            Pattern.DOTALL);

    private final Parser parser;
    private final MutationDatabase db;
    private final GroqClient groq;        // null in ingest mode
    private final String ingestDir;       // null in Groq mode
    private final int variantsPerFile;
    private final int variantsPerCall;
    private final StringBuilder csv = new StringBuilder("File,MutationId,Label,Status,Reason\n");

    public App(GroqClient groq, String ingestDir, int variantsPerFile, int variantsPerCall) {
        this.parser = Parser.htmlParser();
        this.parser.settings(new ParseSettings(true, true)); // preserve tag/attr case, like the static generator
        this.db = new MutationDatabase();
        this.groq = groq;
        this.ingestDir = ingestDir;
        this.variantsPerFile = variantsPerFile;
        this.variantsPerCall = variantsPerCall;
    }

    public static void main(String[] args) throws IOException {
        String configPath = args.length > 0 ? args[0] : "generator-config.json";
        if (!Files.exists(Paths.get(configPath))) {
            throw new RuntimeException("Config file not found: " + configPath);
        }

        int perFile = Integer.parseInt(envOr("LLM_VARIANTS_PER_FILE", envOr("LLM_VARIANTS_PER_TARGET", "70")));
        int perCall = Integer.parseInt(envOr("LLM_VARIANTS_PER_CALL", "6"));

        // H (between-templates) cross-template ingest: builds MULTI-FILE mutants that move an element
        // from one component template into a related one. Reads pre-authored source/dest template pairs.
        String hIngestDir = System.getenv("LLM_H_INGEST_DIR");
        if (hIngestDir != null && !hIngestDir.isBlank()) {
            App hApp = new App(null, null, perFile, perCall);
            hApp.runHIngest(hIngestDir);
            return;
        }

        // H AUTO: systematically generate ALL between-templates mutants — for every target x role,
        // move that role-element into a related component template (h is a pure move, no content to
        // invent, so it is generated mechanically, exactly like the static generator's h rule).
        if (envFlag("LLM_H_AUTO")) {
            App hApp = new App(null, null, perFile, perCall);
            hApp.runAutoH(envOr("LLM_ROLE_TARGETS", "role-targets.json"));
            return;
        }

        // F/G AUTO: structural operators f (reorder among siblings) and g (move to a different
        // parent) are PURE MOVES with no content to invent — same nature as h — so they are
        // generated mechanically (jsoup), exactly like the static generator's f/g rules.
        if (envFlag("LLM_FG_AUTO")) {
            App fgApp = new App(null, null, perFile, perCall);
            fgApp.runAutoFG(envOr("LLM_ROLE_TARGETS", "role-targets.json"));
            return;
        }

        // ROLE TAXONOMY mode (RQ 11x5): anchor mutations to the test targets. For each target the
        // LLM is told BOTH the mutation type (a-k) AND the exact element (one of 5 structural roles:
        // template/ancestor/parent/target/sibling). Reads role-targets.json (NOT generator-config.json).
        // Tagged in DB as mutation_type='LLM_ROLE', mutation_name=<type id>, mutation_id encodes target+role+op.
        // LLM_ROLE_DRYRUN=1 computes only the applicability matrix (no LLM calls, no API key needed).
        if (envFlag("LLM_ROLE")) {
            boolean dryRun = envFlag("LLM_ROLE_DRYRUN");
            boolean dump = envFlag("LLM_ROLE_DUMP");
            String roleIngestDir = System.getenv("LLM_ROLE_INGEST_DIR");
            boolean roleIngest = roleIngestDir != null && !roleIngestDir.isBlank();
            String targetsPath = envOr("LLM_ROLE_TARGETS", "role-targets.json");
            if (!Files.exists(Paths.get(targetsPath))) {
                throw new RuntimeException("Role targets file not found: " + targetsPath);
            }
            List<MutationType> types = loadMutationTypes(envOr("LLM_MUTATION_TYPES", "mutation-types.json"));
            String model = envOr("LLM_MODEL", "llama-3.3-70b-versatile");
            // Groq is only needed for live API generation: dry-run, dump (write prompts) and
            // ingest (splice pre-generated snippets) all work offline, without a key.
            boolean needGroq = !dryRun && !dump && !roleIngest;
            GroqClient roleGroq = null;
            if (needGroq) {
                List<String> apiKeys = readApiKeys();
                if (apiKeys.isEmpty()) {
                    System.err.println("ERROR: set GROQ_API_KEYS for LLM_ROLE, or use LLM_ROLE_DRYRUN=1 / LLM_ROLE_DUMP=1 / LLM_ROLE_INGEST_DIR.");
                    System.exit(1);
                }
                double temperature = Double.parseDouble(envOr("LLM_TEMPERATURE", "1.0"));
                int maxTokens = Integer.parseInt(envOr("LLM_MAX_TOKENS", "9000"));
                roleGroq = new GroqClient(apiKeys, model, temperature, maxTokens, 6);
            }
            App roleApp = new App(roleGroq, roleIngest ? roleIngestDir : null, perFile, perCall);
            String rolePrompt = needGroq ? roleApp.loadResource("/role-taxonomy-prompt.txt") : null;
            roleApp.runRoleMode(targetsPath, rolePrompt, types, dryRun, model);
            return;
        }

        // Ingest mode: read pre-generated variant blocks from files instead of calling the LLM.
        // One file per template named <fileSlug>.txt under LLM_INGEST_DIR.
        String ingestDir = System.getenv("LLM_INGEST_DIR");
        boolean ingest = ingestDir != null && !ingestDir.isBlank();

        Config config = new ObjectMapper().readValue(new File(configPath), Config.class);

        // Independent generation works per distinct template file, not per target.
        Set<String> files = new LinkedHashSet<>();
        for (MutationConfig m : config.mutations) {
            files.add(Paths.get(m.filePath).toAbsolutePath().toString());
        }

        GroqClient groq = null;
        String model = envOr("LLM_MODEL", "llama-3.3-70b-versatile");
        if (ingest) {
            System.out.printf("LLM generator | INGEST mode | dir=%s | up to %d variants/file x %d files%n",
                    ingestDir, perFile, files.size());
        } else {
            // Accept either a single key (GROQ_API_KEY) or a comma-separated pool (GROQ_API_KEYS).
            List<String> apiKeys = readApiKeys();
            if (apiKeys.isEmpty()) {
                System.err.println("ERROR: set GROQ_API_KEY (or GROQ_API_KEYS), or use LLM_INGEST_DIR for ingest mode.");
                System.exit(1);
            }
            double temperature = Double.parseDouble(envOr("LLM_TEMPERATURE", "1.0"));
            // Groq free tier caps tokens-per-minute (e.g. 12000): prompt + max_tokens must stay under it.
            int maxTokens = Integer.parseInt(envOr("LLM_MAX_TOKENS", "9000"));
            groq = new GroqClient(apiKeys, model, temperature, maxTokens, 6);
            System.out.printf("LLM generator | GROQ mode | model=%s | %d variants/file x %d files | %d API key(s)%n",
                    model, perFile, files.size(), apiKeys.size());
        }

        App app = new App(groq, ingest ? ingestDir : null, perFile, perCall);

        // PARAMETRIC mode (RQ 1.1): generate, per template, each mutation TYPE (a-k) of the
        // previous thesis model via a type-driven prompt. The LLM is told WHICH type to apply
        // but still chooses WHERE. Tagged in DB as mutation_type='LLM_PARAMETRIC',
        // mutation_name=<type id>. Measures the approach's ability to produce valid mutants per type.
        boolean parametric = "1".equals(System.getenv("LLM_PARAMETRIC")) || "true".equalsIgnoreCase(System.getenv("LLM_PARAMETRIC"));
        if (parametric) {
            if (ingest) {
                System.err.println("ERROR: LLM_PARAMETRIC and LLM_INGEST_DIR are mutually exclusive.");
                System.exit(1);
            }
            int perType = Integer.parseInt(envOr("LLM_VARIANTS_PER_TYPE", "10"));
            String typesPath = envOr("LLM_MUTATION_TYPES", "mutation-types.json");
            List<MutationType> types = loadMutationTypes(typesPath);
            String paramPrompt = app.loadResource("/parametric-prompt.txt");
            System.out.printf("LLM generator | PARAMETRIC mode | %d types x %d files x %d variants/type | model=%s%n",
                    types.size(), files.size(), perType, model);
            int total = 0;
            for (String filePath : files) {
                total += app.parametricForFile(Paths.get(filePath), paramPrompt, types, perType);
            }
            app.exportCsv();
            System.out.println("\n==============================================");
            System.out.println("DONE (parametric). LLM mutants saved to mutations.db: " + total);
            System.out.println("CSV: output/mutations/llm-mutations.csv");
            System.out.println("Checkpoint: NOT running the mutation-tester. Stop for review.");
            return;
        }

        String prompt = ingest ? null : app.loadPrompt();
        int total = 0;
        for (String filePath : files) {
            total += app.generateForFile(Paths.get(filePath), prompt);
        }

        app.exportCsv();
        System.out.println("\n==============================================");
        System.out.println("DONE. LLM mutants saved to mutations.db: " + total);
        System.out.println("CSV: output/mutations/llm-mutations.csv");
        System.out.println("Checkpoint: NOT running the mutation-tester. Stop for review.");
    }

    private int parametricForFile(Path filePath, String systemPrompt, List<MutationType> types, int perType) throws IOException {
        Path abs = filePath.toAbsolutePath();
        String baseUri = abs.toString();
        String originalRaw = Files.readString(abs, StandardCharsets.UTF_8);
        Document originalDoc = parse(originalRaw, baseUri);
        String fileSlug = slug(abs.getFileName().toString());
        String originalBody = bodyHtml(originalDoc);
        Set<String> requiredTestAttrs = collectTestAttributes(originalDoc);

        System.out.println("\n============= " + abs.getFileName() + " (parametric) =============");
        int fileNew = 0;

        for (MutationType type : types) {
            Set<String> seen = loadExistingBodiesParam(fileSlug, type.id);
            int existing = seen.size();
            int nextIndex = maxExistingIndexParam(fileSlug, type.id);
            int saved = existing;
            int newThisType = 0;
            int calls = 0;
            int consecutiveFailures = 0;
            int maxCalls = (perType / Math.max(1, variantsPerCall)) * 3 + 4;

            while (saved < perType && calls < maxCalls) {
                calls++;
                String userPrompt = buildParametricUserPrompt(originalRaw, type, variantsPerCall);
                String content = groq.chat(systemPrompt, userPrompt);
                if (content == null) {
                    if (++consecutiveFailures >= 4) break;
                    continue;
                }
                consecutiveFailures = 0;
                List<Variant> variants = parseVariants(content);
                for (Variant v : variants) {
                    if (saved >= perType) break;
                    String reject = validate(v, baseUri, originalBody, requiredTestAttrs, seen);
                    if (reject == null) {
                        String mutationId = "LLMP_" + fileSlug + "_" + type.id + "_" + (++nextIndex);
                        db.saveMutation(fileSlug, type.id, MUTATION_TYPE_PARAM, mutationId, List.of(v.doc));
                        seen.add(v.bodyHtml);
                        saved++; newThisType++; fileNew++;
                        csvRow(fileSlug, mutationId, type.id, "SAVED", "");
                    } else {
                        csvRow(fileSlug, "-", type.id, "REJECTED", reject);
                    }
                }
            }
            System.out.printf("  type %s (%s): %d/%d (%d new this run)%n", type.id, type.name, saved, perType, newThisType);
        }
        System.out.printf("  File done (parametric): %d new mutants this run.%n", fileNew);
        return fileNew;
    }

    private String buildParametricUserPrompt(String originalRaw, MutationType type, int k) {
        return "Apply ONLY the following mutation type to the template below.\n\n"
                + "Mutation type id: " + type.id + "\n"
                + "Name: " + type.name + "\n"
                + "Definition: " + type.description + "\n"
                + "Generic example:\n  Before: " + type.before + "\n  After:  " + type.after + "\n\n"
                + "Produce exactly " + k + " variants, numbered 1.." + k + ", each applying THIS mutation type "
                + "to a DIFFERENT element you choose. Output ONLY the delimited blocks, with {type_id} = "
                + type.id + ".\n\n"
                + "----- ORIGINAL TEMPLATE -----\n"
                + originalRaw + "\n"
                + "----- END ORIGINAL TEMPLATE -----\n";
    }

    private Set<String> loadExistingBodiesParam(String fileSlug, String typeId) {
        Set<String> bodies = new HashSet<>();
        String sql = "SELECT mf.mutated_code FROM mutations m JOIN mutated_files mf ON mf.mutation_uuid = m.uuid "
                + "WHERE m.element = ? AND m.mutation_type = ? AND m.mutation_name = ?";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(DB_URL);
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fileSlug); ps.setString(2, MUTATION_TYPE_PARAM); ps.setString(3, typeId);
            try (java.sql.ResultSet rs = ps.executeQuery()) { while (rs.next()) bodies.add(rs.getString(1)); }
        } catch (java.sql.SQLException e) { System.err.println("  Cannot load existing: " + e.getMessage()); }
        return bodies;
    }

    private int maxExistingIndexParam(String fileSlug, String typeId) {
        int max = 0;
        String prefix = "LLMP_" + fileSlug + "_" + typeId + "_";
        String sql = "SELECT mutation_id FROM mutations WHERE element = ? AND mutation_type = ? AND mutation_name = ?";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(DB_URL);
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fileSlug); ps.setString(2, MUTATION_TYPE_PARAM); ps.setString(3, typeId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString(1);
                    if (id != null && id.startsWith(prefix)) {
                        try { max = Math.max(max, Integer.parseInt(id.substring(prefix.length()))); } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } catch (java.sql.SQLException e) { System.err.println("  Cannot read indices: " + e.getMessage()); }
        return max;
    }

    // ============================ ROLE TAXONOMY MODE (11x5) ============================

    /**
     * Drives the 11x5 generation: for each target (from role-targets.json) it resolves the 5
     * structural roles, decides which operator x role combinations are applicable, and either
     * (dry-run) just records the applicability matrix, or generates one mutant per applicable
     * combination via the LLM. Writes a per-combination matrix CSV and a per-test report CSV.
     */
    private void runRoleMode(String targetsPath, String systemPrompt, List<MutationType> types,
                             boolean dryRun, String model) throws IOException {
        ObjectMapper m = new ObjectMapper();
        m.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RoleTargetsFile tf = m.readValue(new File(targetsPath), RoleTargetsFile.class);

        boolean dumpMode = envFlag("LLM_ROLE_DUMP");
        String dumpDir = envOr("LLM_ROLE_DUMP_DIR", "output/mutations/role-prompts");
        String mode = dryRun ? " (DRY-RUN, no LLM)" : dumpMode ? " (DUMP prompts)" : ingestDir != null ? " (INGEST snippets)" : " (GROQ)";
        System.out.printf("LLM generator | ROLE 11x5 mode%s | %d targets x %d operators x %d roles | model=%s%n",
                mode, tf.targets.size(), types.size(), ROLES.length, model);

        StringBuilder matrix = new StringBuilder("Target,TargetAttr,File,Role,Operator,OpName,Decision,Reason,MutationId\n");
        List<TestStat> testStats = new ArrayList<>();
        int saved = 0, na = 0, rejected = 0, applicable = 0;

        // Dedup of bodies is per FILE (overlapping roles across targets can yield identical mutants).
        Map<String, Set<String>> seenByFile = new HashMap<>();
        Map<String, Set<String>> idsByFile = new HashMap<>();

        for (RoleTarget t : tf.targets) {
            Path abs = Paths.get(t.componentHtml).toAbsolutePath();
            int tApplicable = 0, tSaved = 0, tNa = 0, tRejected = 0;
            if (!Files.exists(abs)) {
                System.out.println("  [" + t.id + "] MISSING file: " + abs);
                continue;
            }
            String originalRaw = Files.readString(abs, StandardCharsets.UTF_8);
            Document doc = parse(originalRaw, abs.toString());
            String fileSlug = slug(abs.getFileName().toString());
            String originalBody = bodyHtml(doc);
            Set<String> requiredTestAttrs = collectTestAttributes(doc);

            Element target = doc.getElementsByAttribute(t.targetAttr).first();
            System.out.println("\n============= " + t.id + " (" + t.targetAttr + " in " + abs.getFileName() + ") =============");
            if (target == null) {
                System.out.println("  TARGET NOT FOUND (" + t.targetAttr + ") -> all 55 NA.");
                for (String[] role : ROLES) {
                    for (MutationType type : types) {
                        matrix.append(row(t.id, t.targetAttr, fileSlug, role[0], type.id, type.name, "NA", "target-not-found", "-"));
                        na++; tNa++;
                    }
                }
                testStats.add(new TestStat(t, 0, 0, 0, 55));
                continue;
            }

            Map<String, Element> roles = resolveRoles(doc, target);
            Set<String> seen = seenByFile.computeIfAbsent(fileSlug, this::loadExistingBodiesRole);
            Set<String> ids = idsByFile.computeIfAbsent(fileSlug, this::loadExistingIdsRole);

            for (String[] role : ROLES) {
                String roleName = role[0], roleCode = role[1];
                Element rel = roles.get(roleName);

                // Which operators are applicable on this role element?
                List<MutationType> applicableTypes = new ArrayList<>();
                for (MutationType type : types) {
                    String reason = roleApplicability(roleName, rel, type, requiredTestAttrs);
                    if (reason != null) {
                        matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "NA", reason, "-"));
                        na++; tNa++;
                    } else {
                        applicable++; tApplicable++;
                        applicableTypes.add(type);
                    }
                }
                if (applicableTypes.isEmpty()) continue;

                if (dryRun) {
                    for (MutationType type : applicableTypes) {
                        matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "APPLICABLE", "", "-"));
                    }
                    continue;
                }

                // DUMP mode: write one prompt file per (target, role) listing the exact role element to
                // mutate and the applicable operators. These files are authored by an LLM (Claude/ChatGPT/
                // Gemini) into snippet files, then fed back via ingest. No API needed.
                if (dumpMode) {
                    writeRoleDump(dumpDir, t, roleName, roleCode, rel, applicableTypes);
                    for (MutationType type : applicableTypes) {
                        matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "DUMP", "", "-"));
                    }
                    continue;
                }

                // INGEST mode: read the mutated-element snippets for this (target, role) and splice each
                // back into the original template (so only that element changes). No API needed.
                if (ingestDir != null) {
                    int roleIdx = doc.getAllElements().indexOf(rel);
                    Path inFile = Paths.get(ingestDir).resolve(slug(t.id) + "_" + roleCode + ".txt");
                    Map<String, String> blocks = readSnippetBlocks(inFile);
                    for (MutationType type : applicableTypes) {
                        String mid = "LLMR_" + fileSlug + "_" + slug(t.id) + "_" + roleCode + "_" + type.id;
                        if (ids.contains(mid)) {
                            matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "SAVED", "resumed", mid));
                            saved++; tSaved++; continue;
                        }
                        String snippet = blocks.get(type.id);
                        if (snippet == null || snippet.isBlank()) {
                            matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "REJECTED", "missing-in-ingest", "-"));
                            rejected++; tRejected++; continue;
                        }
                        // f (reorder) and g (move) are STRUCTURAL: they change the element's position, not just
                        // its own markup, so the snippet is the FULL mutated template. All other operators are
                        // local: the snippet is just the mutated element, spliced back in place.
                        boolean structural = "f".equals(type.id) || "g".equals(type.id);
                        Document mutated = structural ? parse(snippet, abs.toString())
                                : spliceElement(doc, roleIdx, snippet, abs.toString());
                        String reason = (mutated == null) ? "splice-failed"
                                : validateSpliced(mutated, originalBody, requiredTestAttrs, seen);
                        if (reason != null) {
                            matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "REJECTED", reason, "-"));
                            rejected++; tRejected++; continue;
                        }
                        db.saveMutation(fileSlug, type.id, MUTATION_TYPE_ROLE, mid, List.of(mutated));
                        seen.add(bodyHtml(mutated));
                        ids.add(mid);
                        matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "SAVED", "", mid));
                        saved++; tSaved++;
                    }
                    continue;
                }

                // Generation: ask the LLM to apply the applicable operators to THIS role element.
                Map<String, MutationType> pending = new LinkedHashMap<>();
                for (MutationType type : applicableTypes) {
                    String mid = "LLMR_" + fileSlug + "_" + slug(t.id) + "_" + roleCode + "_" + type.id;
                    if (ids.contains(mid)) { // resume: already generated in a previous run
                        matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "SAVED", "resumed", mid));
                        saved++; tSaved++;
                    } else {
                        pending.put(type.id, type);
                    }
                }
                if (pending.isEmpty()) continue;

                int chunk = Math.max(1, variantsPerCall);
                int calls = 0, maxCalls = (applicableTypes.size() / chunk) * 3 + 4;
                while (!pending.isEmpty() && calls < maxCalls) {
                    calls++;
                    List<MutationType> batch = new ArrayList<>(pending.values());
                    if (batch.size() > chunk) batch = batch.subList(0, chunk);
                    String userPrompt = buildRoleUserPrompt(originalRaw, rel, roleName, batch);
                    String content = groq.chat(systemPrompt, userPrompt);
                    if (content == null) continue;
                    for (Variant v : parseVariants(content)) {
                        MutationType type = pending.get(v.label);
                        if (type == null) continue; // block for a type we didn't ask / already done
                        String reject = validate(v, abs.toString(), originalBody, requiredTestAttrs, seen);
                        if (reject != null) {
                            matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "REJECTED", reject, "-"));
                            // keep it pending for a retry on the next call
                            continue;
                        }
                        String mid = "LLMR_" + fileSlug + "_" + slug(t.id) + "_" + roleCode + "_" + type.id;
                        db.saveMutation(fileSlug, type.id, MUTATION_TYPE_ROLE, mid, List.of(v.doc));
                        seen.add(v.bodyHtml);
                        ids.add(mid);
                        pending.remove(v.label);
                        matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "SAVED", "", mid));
                        saved++; tSaved++;
                    }
                }
                // Whatever is still pending after maxCalls failed to produce a valid mutant.
                for (MutationType type : pending.values()) {
                    matrix.append(row(t.id, t.targetAttr, fileSlug, roleName, type.id, type.name, "REJECTED", "no-valid-output", "-"));
                    rejected++; tRejected++;
                }
                System.out.printf("  role %-16s: %d applicable, %d still pending after %d call(s)%n",
                        roleName, applicableTypes.size(), pending.size(), calls);
            }
            testStats.add(new TestStat(t, tApplicable, tSaved, tRejected, tNa));
        }

        writeRoleCsv("output/mutations/role-mutations-matrix.csv", matrix.toString());
        writeRolePerTestReport("output/mutations/role-per-test-report.csv", testStats);

        System.out.println("\n==============================================");
        if (dryRun) {
            System.out.printf("DONE (DRY-RUN). Applicable combinations: %d | NA: %d | (max theoretical %d)%n",
                    applicable, na, tf.targets.size() * types.size() * ROLES.length);
            System.out.println("Matrix: output/mutations/role-mutations-matrix.csv");
        } else {
            System.out.printf("DONE (role 11x5). Saved: %d | Rejected: %d | NA: %d | Applicable: %d%n",
                    saved, rejected, na, applicable);
            System.out.println("Matrix: output/mutations/role-mutations-matrix.csv");
            System.out.println("Per-test report: output/mutations/role-per-test-report.csv");
            System.out.println("Checkpoint: NOT running the mutation-tester. Stop for review.");
        }
    }

    /**
     * Resolves the 5 structural roles of a target EXACTLY as the static generator does
     * (MutationEngine.initializeTargets + ElementExtension), so LLM and static target the same
     * elements. Missing roles map to null (reported as NA downstream).
     *   alpha   = the target itself
     *   beta    = target.parent() (raw, as the static; may be the <body> wrapper)
     *   gamma   = ElementExtension.getAncestor  (grandparent, unless it is html/body/head/#root)
     *   delta   = ElementExtension.getSibling   (next, else previous element sibling)
     *   epsilon = ElementExtension.getContainingComponent (nearest custom-component ancestor, often null per-file)
     */
    private Map<String, Element> resolveRoles(Document doc, Element target) {
        Map<String, Element> r = new LinkedHashMap<>();
        r.put("alpha-target", target);
        r.put("beta-parent", target.parent());
        r.put("gamma-ancestor", ElementExtension.getAncestor(target));
        r.put("delta-sibling", ElementExtension.getSibling(target));
        r.put("epsilon-component", ElementExtension.getContainingComponent(target));
        return r;
    }

    /** True when {@code el} is null or the jsoup <body>/document wrapper (i.e. outside the template markup). */
    private boolean isTemplateBoundary(Element el) {
        return el == null || el instanceof Document || "body".equalsIgnoreCase(el.tagName()) || "html".equalsIgnoreCase(el.tagName());
    }

    /**
     * Static pre-check: returns null if {@code type} can be applied to the {@code rel} element in this
     * role, otherwise a short NA reason. Conservative — the LLM + validator are the final arbiters.
     */
    private String roleApplicability(String roleName, Element rel, MutationType type, Set<String> requiredTestAttrs) {
        if (rel == null) return "no-such-role";
        if (isTemplateBoundary(rel)) return "boundary-element"; // e.g. beta == <body> for a root target
        switch (type.id) {
            case "h": // move between two templates: impossible in per-file generation
                return "between-templates-na-perfile";
            case "a": // attribute value modification
            case "b": // attribute removal
            case "c": // attribute identifier (rename)
                return hasEditableAttribute(rel) ? null : "no-editable-attribute";
            case "d": // text content modification
            case "e": // text content removal
                return hasOwnText(rel) ? null : "no-text-content";
            case "f": // reorder among siblings
                return (rel.parent() != null && rel.parent().children().size() >= 2) ? null : "no-sibling-to-reorder";
            case "g": // move elsewhere in the tree (attributes, incl. x-test, travel with the element)
                return isTemplateBoundary(rel.parent()) ? "root-element" : null;
            case "i": // unwrap (remove tag, keep children) -> drops the element's own attributes incl. x-test
                if (isTemplateBoundary(rel.parent())) return "root-element";
                return hasTestAttr(rel) ? "would-drop-x-test" : null;
            case "j": // tag type modification
            case "k": // wrapper insertion
                return null;
            default:
                return null;
        }
    }

    /** An attribute that is not an x-test ground-truth marker and not an Angular structural artifact. */
    private boolean hasEditableAttribute(Element el) {
        for (Attribute a : el.attributes()) {
            if (!a.getKey().startsWith("x-test")) return true;
        }
        return false;
    }

    private boolean hasOwnText(Element el) {
        return el.ownText() != null && !el.ownText().isBlank();
    }

    private boolean hasTestAttr(Element el) {
        for (Attribute a : el.attributes()) {
            if (a.getKey().startsWith("x-test")) return true;
        }
        return false;
    }

    private String buildRoleUserPrompt(String originalRaw, Element roleEl, String roleName, List<MutationType> applicableTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append("You must mutate ONE specific element of the Angular template below.\n\n");
        sb.append("ELEMENT TO MUTATE (structural role: ").append(roleName)
                .append("). Apply every change ONLY to this exact element, nothing else:\n");
        sb.append(roleEl.outerHtml()).append("\n\n");
        sb.append("Produce ONE variant per mutation type listed below. In each variant apply ONLY that one "
                + "mutation type, ONLY to the element above, and keep the entire rest of the template identical.\n\n");
        sb.append("Mutation types to apply:\n");
        for (MutationType t : applicableTypes) {
            sb.append("- id ").append(t.id).append(" (").append(t.name).append("): ").append(t.description)
                    .append("  [before: ").append(t.before).append(" -> after: ").append(t.after).append("]\n");
        }
        sb.append("\nNEVER add, remove, rename, or alter any attribute whose name starts with x-test.\n");
        sb.append("Output ONLY the delimited blocks, one per type, with {type_id} set to the mutation type id.\n\n");
        sb.append("----- ORIGINAL TEMPLATE -----\n").append(originalRaw).append("\n----- END ORIGINAL TEMPLATE -----\n");
        return sb.toString();
    }

    /**
     * DUMP: writes one prompt file per (target, role) with the exact role element to mutate and the
     * applicable operators. An LLM authors the mutated element of each operator into a snippet file
     * (same name) under the ingest dir; ingest then splices them in.
     */
    private void writeRoleDump(String dumpDir, RoleTarget t, String roleName, String roleCode,
                               Element rel, List<MutationType> applicableTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append("# TARGET: ").append(t.id).append("  (").append(t.targetAttr).append(")\n");
        sb.append("# ROLE: ").append(roleName).append("   FILE: ").append(t.componentHtml).append("\n");
        sb.append("# Mutate ONLY this element. For each operator below, output the element's MUTATED outerHTML.\n");
        sb.append("# Rules: result valid Angular 19; NEVER add/remove/rename/alter any x-test* attribute;\n");
        sb.append("#        apply ONLY the requested operator; the element must really differ from the original.\n\n");
        sb.append("ROLE ELEMENT (original):\n").append(rel.outerHtml()).append("\n\n");
        sb.append("OPERATORS TO APPLY (one block each, body = mutated element outerHTML):\n");
        for (MutationType t2 : applicableTypes) {
            sb.append("- ").append(t2.id).append(" (").append(t2.name).append("): ").append(t2.description)
                    .append("  [before: ").append(t2.before).append(" -> after: ").append(t2.after).append("]\n");
        }
        sb.append("\nOUTPUT FORMAT (only these blocks, {op} = operator id):\n");
        sb.append("######### START 1 - {op} #########\n<mutated element>\n######### END 1 - {op} #########\n");
        writeRoleCsv(Paths.get(dumpDir, slug(t.id) + "_" + roleCode + ".txt").toString(), sb.toString());
    }

    /** Reads pre-generated snippet blocks (op id -> mutated element outerHTML) for a (target, role). */
    private Map<String, String> readSnippetBlocks(Path file) {
        Map<String, String> out = new LinkedHashMap<>();
        if (!Files.exists(file)) return out;
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            for (Variant v : parseVariants(content)) {
                if (v.label != null && !v.rawBody.isBlank()) out.put(v.label, v.rawBody.trim());
            }
        } catch (IOException e) {
            System.err.println("  Cannot read ingest file " + file + ": " + e.getMessage());
        }
        return out;
    }

    /** Replaces the role element (by its index in the original) with the parsed snippet, returns the mutated doc. */
    private Document spliceElement(Document original, int roleIdx, String snippet, String baseUri) {
        if (roleIdx < 0) return null;
        Document clone = original.clone();
        if (roleIdx >= clone.getAllElements().size()) return null;
        Element relClone = clone.getAllElements().get(roleIdx);
        Document frag = parse(snippet, baseUri);
        List<org.jsoup.nodes.Node> nodes = new ArrayList<>(frag.body().childNodes());
        if (nodes.isEmpty()) return null;
        for (org.jsoup.nodes.Node n : nodes) {
            relClone.before(n.clone());
        }
        relClone.remove();
        return clone;
    }

    /** Same checks as {@link #validate} but for an already-spliced document. Null if valid. */
    private String validateSpliced(Document mutated, String originalBody, Set<String> requiredTestAttrs, Set<String> seen) {
        String body = bodyHtml(mutated);
        if (body.isBlank()) return "empty-body";
        if (body.equals(originalBody)) return "no-op";
        if (seen.contains(body)) return "duplicate";
        for (String attr : requiredTestAttrs) {
            if (!body.contains(attr)) return "dropped-" + attr;
        }
        return null;
    }

    /**
     * H (between-templates) cross-template ingest. Each .txt file defines ONE multi-file mutant that
     * MOVES an element from one component template into a related one. Format:
     *   @@@ SOURCE &lt;absolute source .html path&gt;
     *   &lt;full source template with the element REMOVED&gt;
     *   @@@ DEST &lt;absolute dest .html path&gt;
     *   &lt;full dest template with the element INSERTED&gt;
     * Saved as a 2-file mutant (mutation_type=LLM_ROLE, mutation_name='h').
     */
    private void runHIngest(String dir) throws IOException {
        System.out.println("LLM generator | H cross-template INGEST | dir=" + dir);
        Set<String> ids = loadExistingHIds();
        Set<String> seen = new HashSet<>();
        StringBuilder matrix = new StringBuilder("Mutant,SourceFile,DestFile,Decision,Reason\n");
        int saved = 0, rejected = 0;
        List<Path> files = new ArrayList<>();
        try (java.util.stream.Stream<Path> s = Files.list(Paths.get(dir))) {
            s.filter(p -> p.toString().endsWith(".txt")).sorted().forEach(files::add);
        }
        for (Path f : files) {
            String name = f.getFileName().toString().replaceFirst("\\.txt$", "");
            String mid = "LLMR_h_" + slug(name);
            if (ids.contains(mid)) { matrix.append(hRow(name, "-", "-", "SAVED", "resumed")); saved++; continue; }
            String[] sec = parseHSections(Files.readString(f, StandardCharsets.UTF_8));
            if (sec == null) { matrix.append(hRow(name, "-", "-", "REJECTED", "bad-format")); rejected++; continue; }
            Path srcAbs = Paths.get(sec[0]).toAbsolutePath(), destAbs = Paths.get(sec[2]).toAbsolutePath();
            if (!Files.exists(srcAbs) || !Files.exists(destAbs)) { matrix.append(hRow(name, sec[0], sec[2], "REJECTED", "file-not-found")); rejected++; continue; }
            Document srcOrig = parse(Files.readString(srcAbs, StandardCharsets.UTF_8), srcAbs.toString());
            Document destOrig = parse(Files.readString(destAbs, StandardCharsets.UTF_8), destAbs.toString());
            Set<String> req = new HashSet<>(collectTestAttributes(srcOrig));
            req.addAll(collectTestAttributes(destOrig));
            Document srcMut, destMut;
            try { srcMut = parse(sec[1], srcAbs.toString()); destMut = parse(sec[3], destAbs.toString()); }
            catch (Exception e) { matrix.append(hRow(name, sec[0], sec[2], "REJECTED", "unparseable")); rejected++; continue; }
            String srcB = bodyHtml(srcMut), destB = bodyHtml(destMut);
            String reason = null;
            if (srcB.equals(bodyHtml(srcOrig))) reason = "source-unchanged";       // the element must LEAVE the source
            else if (destB.equals(bodyHtml(destOrig))) reason = "dest-unchanged";   // ...and ARRIVE in the dest
            else { String both = srcB + destB; for (String a : req) if (!both.contains(a)) { reason = "dropped-" + a; break; } }
            String combined = srcB + " " + destB;
            if (reason == null && seen.contains(combined)) reason = "duplicate";
            if (reason != null) { matrix.append(hRow(name, sec[0], sec[2], "REJECTED", reason)); rejected++; continue; }
            db.saveMutation(slug(srcAbs.getFileName().toString()), "h", MUTATION_TYPE_ROLE, mid, List.of(srcMut, destMut));
            seen.add(combined);
            matrix.append(hRow(name, sec[0], sec[2], "SAVED", ""));
            saved++;
        }
        writeRoleCsv("output/mutations/role-h-matrix.csv", matrix.toString());
        System.out.println("\nDONE (h cross-template). Saved: " + saved + " | Rejected: " + rejected);
        System.out.println("Matrix: output/mutations/role-h-matrix.csv");
    }

    /**
     * H AUTO: for every target x role, move that role-element from its source template into a related
     * component template, producing a 2-file mutant. h has no content to generate (pure structural move),
     * so it is done mechanically — same nature as the static generator's TagMovementBetweenTemplatesRule.
     */
    private void runAutoH(String targetsPath) throws IOException {
        ObjectMapper m = new ObjectMapper();
        m.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RoleTargetsFile tf = m.readValue(new File(targetsPath), RoleTargetsFile.class);
        String base = tf.appRoot + "/src/app/";
        Map<String, String> destMap = new HashMap<>();
        if ("flowboard".equalsIgnoreCase(tf.app)) {
            // FlowBoard (Kanban board) cross-template map.
            destMap.put("board.component.html", base + "card/card.component.html");
            destMap.put("card.component.html", base + "board/board.component.html");
            destMap.put("column.component.html", base + "board/board.component.html");
            destMap.put("card-form.component.html", base + "card-detail/card-detail.component.html");
            destMap.put("card-detail.component.html", base + "card-form/card-form.component.html");
            destMap.put("stats.component.html", base + "board/board.component.html");
            destMap.put("header.component.html", base + "app.component.html");
            destMap.put("app.component.html", base + "header/header.component.html");
            destMap.put("toast.component.html", base + "header/header.component.html");
        } else {
            destMap.put("catalog.component.html", base + "movie-card/movie-card.component.html");
            destMap.put("movie-card.component.html", base + "catalog/catalog.component.html");
            destMap.put("movie-detail.component.html", base + "cast-row/cast-row.component.html");
            destMap.put("cast-row.component.html", base + "movie-detail/movie-detail.component.html");
            destMap.put("reviews.component.html", base + "movie-detail/movie-detail.component.html");
            destMap.put("stats.component.html", base + "catalog/catalog.component.html");
            destMap.put("movie-form.component.html", base + "catalog/catalog.component.html");
        }

        System.out.println("LLM generator | H AUTO cross-template | " + tf.targets.size() + " targets");
        Set<String> ids = loadExistingHIds();
        Set<String> seen = new HashSet<>();
        int saved = 0, dup = 0, skipped = 0;
        Map<String, Document> destCache = new HashMap<>();

        for (RoleTarget t : tf.targets) {
            Path srcAbs = Paths.get(t.componentHtml).toAbsolutePath();
            String srcName = srcAbs.getFileName().toString();
            String destPathStr = destMap.get(srcName);
            if (destPathStr == null) { skipped++; continue; }
            Path destAbs = Paths.get(destPathStr).toAbsolutePath();
            Document srcOrig = parse(Files.readString(srcAbs, StandardCharsets.UTF_8), srcAbs.toString());
            if (!destCache.containsKey(destPathStr))
                destCache.put(destPathStr, parse(Files.readString(destAbs, StandardCharsets.UTF_8), destAbs.toString()));
            Document destOrig = destCache.get(destPathStr);
            Element target = srcOrig.getElementsByAttribute(t.targetAttr).first();
            if (target == null) { skipped++; continue; }
            Map<String, Element> roles = resolveRoles(srcOrig, target);
            for (String[] role : ROLES) {
                if ("epsilon-component".equals(role[0])) continue; // ε never exists per-file
                Element rel = roles.get(role[0]);
                if (rel == null || isTemplateBoundary(rel)) continue;
                int idx = srcOrig.getAllElements().indexOf(rel);
                if (idx < 0) continue;
                String id = "LLMR_h_" + slug(t.id) + "_" + role[1];
                if (ids.contains(id)) { saved++; continue; } // resume
                Document srcClone = srcOrig.clone();
                Element relInClone = srcClone.getAllElements().get(idx);
                Element moved = relInClone.clone();
                relInClone.remove();
                Document destClone = destOrig.clone();
                destClone.body().appendChild(moved);
                String srcB = bodyHtml(srcClone), destB = bodyHtml(destClone);
                if (srcB.equals(bodyHtml(srcOrig))) continue; // element didn't leave
                String combined = srcB + " " + destB;
                if (seen.contains(combined)) { dup++; continue; }
                db.saveMutation(slug(srcName), "h", MUTATION_TYPE_ROLE, id, List.of(srcClone, destClone));
                seen.add(combined); ids.add(id); saved++;
            }
        }
        System.out.println("DONE (h auto). Saved (+resumed): " + saved + " | duplicati saltati: " + dup + " | target saltati: " + skipped);
    }

    /**
     * F/G AUTO: mechanical structural mutants (same nature as auto-H). For every target x role
     * element, produce f (reorder: move the element after its next sibling) and g (move the element
     * up into its grandparent). Each mutant is the FULL mutated template, saved single-file with
     * mutation_name 'f'/'g'. Dedup per file; x-test preserved by construction (pure move).
     */
    private void runAutoFG(String targetsPath) throws IOException {
        ObjectMapper m = new ObjectMapper();
        m.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RoleTargetsFile tf = m.readValue(new File(targetsPath), RoleTargetsFile.class);
        System.out.println("LLM generator | F/G AUTO structural | " + tf.targets.size() + " targets");
        int saved = 0, dup = 0, skipped = 0;
        Map<String, Set<String>> seenByFile = new HashMap<>();
        Map<String, Set<String>> idsByFile = new HashMap<>();

        for (RoleTarget t : tf.targets) {
            Path abs = Paths.get(t.componentHtml).toAbsolutePath();
            if (!Files.exists(abs)) { skipped++; continue; }
            Document doc = parse(Files.readString(abs, StandardCharsets.UTF_8), abs.toString());
            String fileSlug = slug(abs.getFileName().toString());
            String originalBody = bodyHtml(doc);
            Set<String> requiredTestAttrs = collectTestAttributes(doc);
            Element target = doc.getElementsByAttribute(t.targetAttr).first();
            if (target == null) { skipped++; continue; }
            Map<String, Element> roles = resolveRoles(doc, target);
            Set<String> seen = seenByFile.computeIfAbsent(fileSlug, this::loadExistingBodiesRole);
            Set<String> ids = idsByFile.computeIfAbsent(fileSlug, this::loadExistingIdsRole);

            for (String[] role : ROLES) {
                if ("epsilon-component".equals(role[0])) continue;
                Element rel = roles.get(role[0]);
                if (rel == null || isTemplateBoundary(rel)) continue;
                int idx = doc.getAllElements().indexOf(rel);
                if (idx < 0) continue;

                for (String op : new String[]{"f", "g"}) {
                    String mid = "LLMR_" + fileSlug + "_" + slug(t.id) + "_" + role[1] + "_" + op;
                    if (ids.contains(mid)) { saved++; continue; } // resume
                    Document c = doc.clone();
                    Element r = c.getAllElements().get(idx);
                    boolean moved;
                    if ("f".equals(op)) {
                        Element sib = r.nextElementSibling() != null ? r.nextElementSibling() : r.previousElementSibling();
                        if (sib == null) continue;
                        if (r.nextElementSibling() != null) sib.after(r); else sib.before(r);
                        moved = true;
                    } else {
                        Element gp = (r.parent() != null) ? r.parent().parent() : null;
                        if (gp == null || isTemplateBoundary(gp)) continue;
                        gp.appendChild(r);
                        moved = true;
                    }
                    if (!moved) continue;
                    String body = bodyHtml(c);
                    if (body.equals(originalBody)) continue;              // no-op
                    if (seen.contains(body)) { dup++; continue; }         // duplicate
                    boolean dropped = false;
                    for (String a : requiredTestAttrs) if (!body.contains(a)) { dropped = true; break; }
                    if (dropped) continue;                                // would drop ground truth
                    db.saveMutation(fileSlug, op, MUTATION_TYPE_ROLE, mid, List.of(c));
                    seen.add(body); ids.add(mid); saved++;
                }
            }
        }
        System.out.println("DONE (f/g auto). Saved (+resumed): " + saved + " | duplicati saltati: " + dup + " | target saltati: " + skipped);
    }

    /** Splits an h-mutant file into [srcPath, srcHtml, destPath, destHtml] or null if malformed. */
    private String[] parseHSections(String content) {
        int si = content.indexOf("@@@ SOURCE");
        int di = content.indexOf("@@@ DEST");
        if (si < 0 || di < 0 || di < si) return null;
        String[] src = splitHeader(content.substring(si, di), "@@@ SOURCE");
        String[] dest = splitHeader(content.substring(di), "@@@ DEST");
        if (src == null || dest == null) return null;
        return new String[]{src[0], src[1], dest[0], dest[1]};
    }

    private String[] splitHeader(String section, String tag) {
        int nl = section.indexOf('\n');
        if (nl < 0) return null;
        String path = section.substring(tag.length(), nl).trim();
        String html = section.substring(nl + 1);
        int end = html.indexOf("\n@@@"); // drop any trailing marker line (e.g. "@@@ END")
        if (end >= 0) html = html.substring(0, end);
        html = html.trim();
        if (path.isEmpty() || html.isEmpty()) return null;
        return new String[]{path, html};
    }

    private String hRow(String m, String s, String d, String dec, String r) {
        return String.format("%s,%s,%s,%s,%s%n", csv(m), csv(s), csv(d), dec, csv(r));
    }

    private Set<String> loadExistingHIds() {
        Set<String> ids = new HashSet<>();
        String sql = "SELECT mutation_id FROM mutations WHERE mutation_type = ? AND mutation_name = 'h'";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(DB_URL);
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, MUTATION_TYPE_ROLE);
            try (java.sql.ResultSet rs = ps.executeQuery()) { while (rs.next()) ids.add(rs.getString(1)); }
        } catch (java.sql.SQLException e) { System.err.println("  Cannot load existing h ids: " + e.getMessage()); }
        return ids;
    }

    /** Bodies of role mutants already stored for this file, for dedup on resume. */
    private Set<String> loadExistingBodiesRole(String fileSlug) {
        Set<String> bodies = new HashSet<>();
        String sql = "SELECT mf.mutated_code FROM mutations m JOIN mutated_files mf ON mf.mutation_uuid = m.uuid "
                + "WHERE m.element = ? AND m.mutation_type = ?";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(DB_URL);
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fileSlug); ps.setString(2, MUTATION_TYPE_ROLE);
            try (java.sql.ResultSet rs = ps.executeQuery()) { while (rs.next()) bodies.add(rs.getString(1)); }
        } catch (java.sql.SQLException e) { System.err.println("  Cannot load existing role bodies: " + e.getMessage()); }
        return bodies;
    }

    /** Mutation ids already stored for this file, so a re-run skips combinations already done. */
    private Set<String> loadExistingIdsRole(String fileSlug) {
        Set<String> existing = new HashSet<>();
        String sql = "SELECT mutation_id FROM mutations WHERE element = ? AND mutation_type = ?";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(DB_URL);
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fileSlug); ps.setString(2, MUTATION_TYPE_ROLE);
            try (java.sql.ResultSet rs = ps.executeQuery()) { while (rs.next()) existing.add(rs.getString(1)); }
        } catch (java.sql.SQLException e) { System.err.println("  Cannot load existing role ids: " + e.getMessage()); }
        return existing;
    }

    private String row(String target, String attr, String file, String role, String op, String opName,
                       String decision, String reason, String mid) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                csv(target), csv(attr), csv(file), csv(role), csv(op), csv(opName), decision, csv(reason), csv(mid));
    }

    private static String csv(String s) {
        if (s == null) return "";
        return s.contains(",") || s.contains("\"") ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }

    private void writeRoleCsv(String path, String content) {
        try {
            Path out = Paths.get(path);
            Files.createDirectories(out.getParent());
            Files.writeString(out, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Cannot write " + path + ": " + e.getMessage());
        }
    }

    private void writeRolePerTestReport(String path, List<TestStat> stats) {
        StringBuilder sb = new StringBuilder("Test,Target,Applicable,Saved,Rejected,NA\n");
        for (TestStat s : stats) {
            for (String testName : s.target.tests) {
                sb.append(String.format("%s,%s,%d,%d,%d,%d%n",
                        csv(testName), csv(s.target.id), s.applicable, s.saved, s.rejected, s.na));
            }
        }
        writeRoleCsv(path, sb.toString());
    }

    private static final class TestStat {
        final RoleTarget target;
        final int applicable, saved, rejected, na;
        TestStat(RoleTarget target, int applicable, int saved, int rejected, int na) {
            this.target = target; this.applicable = applicable; this.saved = saved; this.rejected = rejected; this.na = na;
        }
    }

    public static final class RoleTargetsFile {
        public String app;
        public String appRoot;
        public List<RoleTarget> targets = new ArrayList<>();
    }

    public static final class RoleTarget {
        public String id;
        public String targetAttr;
        public String componentHtml;
        public List<String> tests = new ArrayList<>();
    }

    private static List<MutationType> loadMutationTypes(String path) throws IOException {
        ObjectMapper m = new ObjectMapper();
        m.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypesFile tf = m.readValue(new File(path), TypesFile.class);
        return tf.types;
    }

    public static final class TypesFile {
        public List<MutationType> types = new ArrayList<>();
    }

    public static final class MutationType {
        public String id;
        public String name;
        public String description;
        public String before;
        public String after;
        public String note;
    }

    private int generateForFile(Path filePath, String systemPrompt) throws IOException {
        Path abs = filePath.toAbsolutePath();
        String baseUri = abs.toString();
        String originalRaw = Files.readString(abs, StandardCharsets.UTF_8);

        Document originalDoc = parse(originalRaw, baseUri);
        Ctx ctx = new Ctx();
        ctx.fileSlug = slug(abs.getFileName().toString());
        ctx.baseUri = baseUri;
        ctx.originalBody = bodyHtml(originalDoc);
        ctx.requiredTestAttrs = collectTestAttributes(originalDoc);

        System.out.println("\n============= " + abs.getFileName() + " =============");

        // Resume/top-up: load mutants already saved for this file so a re-run only fills the gap.
        ctx.seenBodies = loadExistingBodies(ctx.fileSlug);
        int existing = ctx.seenBodies.size();
        ctx.nextIndex = maxExistingIndex(ctx.fileSlug);
        ctx.saved = existing;
        if (existing > 0) {
            System.out.printf("  Resuming: %d existing LLM mutants, topping up to %d.%n", existing, variantsPerFile);
        }

        if (ingestDir != null) {
            ingestForFile(ctx);
        } else {
            groqForFile(ctx, systemPrompt, originalRaw);
        }

        System.out.printf("  File done: %d total mutants (%d new this run).%n", ctx.saved, ctx.newlySaved);
        return ctx.newlySaved;
    }

    private void groqForFile(Ctx ctx, String systemPrompt, String originalRaw) {
        int calls = 0;
        int consecutiveFailures = 0;
        int maxCalls = (variantsPerFile / Math.max(1, variantsPerCall)) * 3 + 4;

        while (ctx.saved < variantsPerFile && calls < maxCalls) {
            calls++;
            String userPrompt = buildUserPrompt(originalRaw, variantsPerCall);
            String content = groq.chat(systemPrompt, userPrompt);

            if (content == null) {
                if (++consecutiveFailures >= 4) {
                    System.out.println("  Too many consecutive LLM failures, moving on.");
                    break;
                }
                continue;
            }
            consecutiveFailures = 0;

            List<Variant> variants = parseVariants(content);
            if (variants.isEmpty()) {
                System.out.println("  Call " + calls + ": no parseable blocks in response.");
                continue;
            }
            consume(variants, ctx);
            System.out.printf("  Call %d: %d/%d total (%d new this run).%n",
                    calls, ctx.saved, variantsPerFile, ctx.newlySaved);
        }
    }

    private void ingestForFile(Ctx ctx) throws IOException {
        Path dir = Paths.get(ingestDir);
        Path file = dir.resolve(ctx.fileSlug + ".txt");
        if (!Files.exists(file)) {
            System.out.println("  Ingest file not found (" + ctx.fileSlug + ".txt). Skipping.");
            return;
        }
        String content = Files.readString(file, StandardCharsets.UTF_8);
        List<Variant> variants = parseVariants(content);
        System.out.printf("  Ingest: %d blocks parsed from %s.%n", variants.size(), file.getFileName());
        consume(variants, ctx);
    }

    /** Validates and stores variants into the DB, updating progress in {@code ctx}. */
    private void consume(List<Variant> variants, Ctx ctx) {
        for (Variant v : variants) {
            if (ctx.saved >= variantsPerFile) break;
            String reject = validate(v, ctx.baseUri, ctx.originalBody, ctx.requiredTestAttrs, ctx.seenBodies);
            if (reject == null) {
                String mutationId = "LLM_" + ctx.fileSlug + "_" + (++ctx.nextIndex);
                db.saveMutation(ctx.fileSlug, v.label, MUTATION_TYPE, mutationId, List.of(v.doc));
                ctx.seenBodies.add(v.bodyHtml);
                ctx.saved++;
                ctx.newlySaved++;
                csvRow(ctx.fileSlug, mutationId, v.label, "SAVED", "");
            } else {
                csvRow(ctx.fileSlug, "-", v.label, "REJECTED", reject);
            }
        }
    }

    /** Per-file mutable progress shared between the LLM loop and ingest mode. */
    private static final class Ctx {
        String fileSlug;
        String baseUri;
        String originalBody;
        Set<String> requiredTestAttrs;
        Set<String> seenBodies;
        int nextIndex;
        int saved;
        int newlySaved;
    }

    /** Bodies of LLM mutants already stored for this file (so resume skips duplicates). */
    private Set<String> loadExistingBodies(String fileSlug) {
        Set<String> bodies = new HashSet<>();
        String sql = "SELECT mf.mutated_code FROM mutations m "
                + "JOIN mutated_files mf ON mf.mutation_uuid = m.uuid "
                + "WHERE m.element = ? AND m.mutation_type = ?";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(DB_URL);
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fileSlug);
            ps.setString(2, MUTATION_TYPE);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) bodies.add(rs.getString(1));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("  Cannot load existing mutants: " + e.getMessage());
        }
        return bodies;
    }

    /** Highest trailing index among existing mutation_ids for this file (0 if none). */
    private int maxExistingIndex(String fileSlug) {
        int max = 0;
        String prefix = "LLM_" + fileSlug + "_";
        String sql = "SELECT mutation_id FROM mutations WHERE element = ? AND mutation_type = ?";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(DB_URL);
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fileSlug);
            ps.setString(2, MUTATION_TYPE);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString(1);
                    if (id != null && id.startsWith(prefix)) {
                        try {
                            max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("  Cannot read existing indices: " + e.getMessage());
        }
        return max;
    }

    /** Returns null if the variant is valid, otherwise a short rejection reason. */
    private String validate(Variant v, String baseUri, String originalBody,
                            Set<String> requiredTestAttrs, Set<String> seenBodies) {
        if (v.rawBody.isBlank()) return "empty";

        Document doc;
        try {
            doc = parse(v.rawBody, baseUri);
        } catch (Exception e) {
            return "unparseable";
        }
        String body = bodyHtml(doc);
        if (body.isBlank()) return "empty-body";
        if (body.equals(originalBody)) return "no-op";
        if (seenBodies.contains(body)) return "duplicate";
        for (String attr : requiredTestAttrs) {
            if (!body.contains(attr)) return "dropped-" + attr;
        }

        v.doc = doc;
        v.bodyHtml = body;
        return null;
    }

    private List<Variant> parseVariants(String content) {
        List<Variant> out = new ArrayList<>();
        Matcher m = BLOCK.matcher(content);
        while (m.find()) {
            String label = sanitizeLabel(m.group(2));
            String body = stripFences(m.group(3)).trim();
            out.add(new Variant(label, body));
        }
        return out;
    }

    /** The LLM chooses the label freely; we only sanitize it for storage. */
    private String sanitizeLabel(String raw) {
        String c = raw == null ? "" : raw.trim().toLowerCase().replaceAll("\\s+", "-");
        if (c.isEmpty()) return "llm-edit";
        return c.length() > 40 ? c.substring(0, 40) : c;
    }

    private String buildUserPrompt(String originalRaw, int k) {
        return "Here is the complete source of one Angular template.\n"
                + "Produce exactly " + k + " variants of it, numbered 1.." + k + ", each in its own "
                + "delimited block exactly as specified in your instructions. Decide entirely on your "
                + "own what to change in each variant. Output ONLY the blocks, nothing else.\n\n"
                + "----- ORIGINAL TEMPLATE -----\n"
                + originalRaw + "\n"
                + "----- END ORIGINAL TEMPLATE -----\n";
    }

    private Document parse(String html, String baseUri) {
        Document doc = Jsoup.parse(html, baseUri, parser);
        doc.outputSettings().prettyPrint(false); // serialize verbatim, like the static path
        return doc;
    }

    // Angular 17+ control-flow blocks (@if (...), @for (... ; track ...)) are not real HTML: jsoup
    // parses their content as plain text and, correctly per the HTML spec, escapes '<'/'>'/'&' in it
    // (jsoup has no setting to turn this off). That turns "@if (x > 0)" into "@if (x &gt; 0)", which
    // breaks the Angular template compiler (NG5002) regardless of what the actual mutation touched.
    // Fix: after serialization, un-escape entities ONLY inside the "@if (...)"/"@for (...)" condition
    // span (up to the block's opening '{'), leaving every other entity in the document untouched.
    private static final Pattern CONTROL_FLOW_HEAD = Pattern.compile("@(?:if|for)\\s*\\([^{]*?\\{");

    private static String fixAngularControlFlowEntities(String html) {
        Matcher m = CONTROL_FLOW_HEAD.matcher(html);
        StringBuilder out = new StringBuilder();
        int last = 0;
        while (m.find()) {
            out.append(html, last, m.start());
            String head = m.group();
            head = head.replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&");
            out.append(head);
            last = m.end();
        }
        out.append(html.substring(last));
        return out.toString();
    }

    /** Wraps {@code doc.body().html()}, applying {@link #fixAngularControlFlowEntities}. Use this
     *  (not doc.body().html() directly) for any HTML that will be compared, stored or persisted. */
    private static String bodyHtml(Document doc) {
        return fixAngularControlFlowEntities(doc.body().html());
    }

    private Set<String> collectTestAttributes(Document doc) {
        Set<String> attrs = new HashSet<>();
        for (Element el : doc.getAllElements()) {
            for (Attribute a : el.attributes()) {
                if (a.getKey().startsWith("x-test")) {
                    attrs.add(a.getKey());
                }
            }
        }
        return attrs;
    }

    private String loadPrompt() throws IOException {
        String override = System.getenv("LLM_PROMPT_FILE");
        if (override != null && !override.isBlank()) {
            return Files.readString(Paths.get(override), StandardCharsets.UTF_8);
        }
        return loadResource("/realistic-prompt.txt");
    }

    private String loadResource(String name) throws IOException {
        try (InputStream in = App.class.getResourceAsStream(name)) {
            if (in == null) throw new IOException(name + " not found on classpath");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void exportCsv() {
        Path out = Paths.get("output/mutations/llm-mutations.csv");
        try {
            Files.createDirectories(out.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
                w.write(csv.toString());
            }
        } catch (IOException e) {
            System.err.println("Cannot write CSV: " + e.getMessage());
        }
    }

    private void csvRow(String file, String id, String label, String status, String reason) {
        csv.append(String.format("%s,%s,%s,%s,%s%n", file, id, label, status, reason));
    }

    private static String stripFences(String s) {
        return s.replaceAll("(?m)^\\s*```[a-zA-Z]*\\s*$", "");
    }

    private static String slug(String s) {
        return s.replaceAll("[^A-Za-z0-9]+", "_");
    }

    private static String envOr(String key, String fallback) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? fallback : v;
    }

    private static boolean envFlag(String key) {
        String v = System.getenv(key);
        return "1".equals(v) || "true".equalsIgnoreCase(v);
    }

    /** Reads the API key pool: GROQ_API_KEYS (comma-separated) takes priority, else GROQ_API_KEY. */
    private static List<String> readApiKeys() {
        List<String> keys = new ArrayList<>();
        String pool = System.getenv("GROQ_API_KEYS");
        String single = System.getenv("GROQ_API_KEY");
        String source = (pool != null && !pool.isBlank()) ? pool : single;
        if (source != null) {
            for (String k : source.split(",")) {
                String t = k.trim();
                if (!t.isEmpty()) keys.add(t);
            }
        }
        return keys;
    }

    private static final class Variant {
        final String label;
        final String rawBody;
        Document doc;
        String bodyHtml;

        Variant(String label, String rawBody) {
            this.label = label;
            this.rawBody = rawBody;
        }
    }
}
