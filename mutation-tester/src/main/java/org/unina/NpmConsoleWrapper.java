package org.unina;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NpmConsoleWrapper {
    private Process process;
    private Thread outputThread;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private volatile CountDownLatch compilationLatch;
    private volatile boolean lastCompilationSuccess = false;

    // "Compiled successfully" = webpack-based Angular builder (angular-spotify).
    // "Application bundle generation complete" = esbuild-based Angular 19+ builder (CineLib and
    // other newer apps). Both are checked so the same wrapper works across builder generations.
    private static final String[] SUCCESS_MSGS = {"Compiled successfully", "Application bundle generation complete"};
    private static final String ERROR_MSG = "Error:";

    private final boolean debug;

    public NpmConsoleWrapper(boolean debug) {
        this.debug = debug;
    }

    public void start(String workingDir, String runCommand) throws IOException {
        this.compilationLatch = new CountDownLatch(1);
        this.lastCompilationSuccess = false;

        File nxBin = new File(workingDir, "node_modules/nx/bin/nx.js");

        if (!nxBin.exists()) {
            nxBin = new File(workingDir, "node_modules/@nx/workspace/bin/nx.js");
        }

        ProcessBuilder pb = new ProcessBuilder();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            pb.command("cmd.exe", "/c", runCommand);
        } else {
            pb.command("bash", "-c", runCommand);
        }
        pb.environment().put("FORCE_COLOR", "1");
        pb.environment().put("NPM_CONFIG_COLOR", "always");
        pb.environment().put("CI", "true");
        pb.environment().put("CHOKIDAR_USEPOLLING", "true");
        pb.directory(new File(workingDir));
        pb.redirectErrorStream(true);

        this.process = pb.start();
        this.isRunning.set(true);
        this.outputThread = new Thread(this::listenToConsole);
        this.outputThread.setDaemon(true);
        this.outputThread.start();
    }

    public void resetForNextBuild() {
        this.compilationLatch = new CountDownLatch(1);
        this.lastCompilationSuccess = false;
    }

    private void listenToConsole() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while (isRunning.get() && (line = reader.readLine()) != null) {
                String cleanLine = line.replaceAll("\u001B\\[[;\\d]*m", "");
                if (debug)
                    System.out.println("[DEBUG NX] " + cleanLine);
                boolean isSuccess = false;
                for (String msg : SUCCESS_MSGS) {
                    if (cleanLine.contains(msg)) { isSuccess = true; break; }
                }
                if (isSuccess) {
                    notifyCompilation(true);
                } else if (cleanLine.contains(ERROR_MSG)) {
                    notifyCompilation(false);
                }
            }
        } catch (IOException e) {
            if (isRunning.get()) e.printStackTrace();
        }
    }

    private void notifyCompilation(boolean success) {
        if (compilationLatch != null && compilationLatch.getCount() > 0) {
            this.lastCompilationSuccess = success;
            compilationLatch.countDown();
        }
    }

    public boolean waitForRebuild(int timeoutSeconds) {
        compilationLatch = new CountDownLatch(1);
        try {
            if (compilationLatch == null) return false;
            boolean completed = compilationLatch.await(timeoutSeconds, TimeUnit.SECONDS);
            if (!completed) {
                System.err.println("Timeout: typescript application did not recompile in time.");
                return false;
            }
            return lastCompilationSuccess;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void stop() {
        isRunning.set(false);
        if (process != null) process.destroy();
    }
}
