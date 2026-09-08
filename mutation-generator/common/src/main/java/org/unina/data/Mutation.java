package org.unina.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;import java.util.List;

public class Mutation {
    public String uuid;
    public String element;
    public String name;
    public String mutation_type;
    public String mutation_id;
    public String error_log;

    public List<MutatedFile> mutatedFiles = new ArrayList<>();

    public Mutation(String uuid, String element, String name, String mutation_type, String mutation_id) {
        this.uuid = uuid;
        this.element = element;
        this.name = name;
        this.mutation_type = mutation_type;
        this.mutation_id = mutation_id;
    }

    public void applyMutationToRepository() throws IOException {
        for (MutatedFile file : mutatedFiles) {
            Path filePath = Paths.get(file.filePath);
            Path tempFile = Files.createTempFile(filePath.getParent(), "java_edit_", ".tmp");
            try {
                file.originalCode = Files.readString(filePath, StandardCharsets.UTF_8);
                Files.writeString(tempFile, file.mutatedCode, StandardCharsets.UTF_8);
                moveWithRetry(tempFile, filePath);
            } catch (IOException e) {
                Files.deleteIfExists(tempFile);
                throw e;
            }
        }
    }

    /**
     * Su Windows, un file appena scritto in una cartella sincronizzata (es. OneDrive) o sotto
     * osservazione di un watcher (Angular dev server / antivirus) puo' risultare brevemente
     * bloccato, facendo fallire lo spostamento atomico. Si ritenta poche volte con una breve
     * attesa prima di rinunciare: se il file resta bloccato, la destinazione NON viene toccata
     * (garanzia di Files.move), quindi non c'e' rischio di corruzione, solo di fallimento.
     */
    private static void moveWithRetry(Path source, Path destination) throws IOException {
        final int maxAttempts = 5;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Files.move(source, destination,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
                return;
            } catch (IOException e) {
                if (attempt == maxAttempts) throw e;
                try {
                    Thread.sleep(300L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }

    public void revertMutations() throws IOException {
        for (MutatedFile file : mutatedFiles) {
            Files.writeString(Paths.get(file.filePath), file.originalCode, StandardCharsets.UTF_8);
        }
    }
}
