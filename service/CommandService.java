package app.service;

import app.ui.LogsPanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class CommandService {

    /**
     * Callback interface that receives the process exit code.
     * Using a custom interface instead of java.util.function for Java 1.8 compatibility.
     */
    public interface ExitCallback {
        void onExit(int exitCode);
    }

    private static Process currentProcess;

    public static void stopCurrentProcess() {
        try {
            if (currentProcess != null) currentProcess.destroyForcibly();
        } catch (Exception ignored) {}
    }

    /** Run a command with no callback. */
    public static void runCommand(List<String> command, LogsPanel logsPanel) {
        runCommandWithCallback(command, logsPanel, null);
    }

    /** Run a command; onFinished (if non-null) is called with exit code when done. */
    public static void runCommandWithCallback(
            final List<String> command,
            final LogsPanel logsPanel,
            final ExitCallback onFinished
    ) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                int exitCode = -1;

                try {

                    ProcessBuilder processBuilder = new ProcessBuilder(command);
                    File publicatorDir = new File("./Publisher");

                    if (!publicatorDir.exists()) {
                        logsPanel.appendLog("Publisher folder not found: "
                                + publicatorDir.getAbsolutePath());
                        return;
                    } else {
                        logsPanel.appendLog("Publisher folder found!!!");
                    }

                    processBuilder.directory(publicatorDir);
                    processBuilder.redirectErrorStream(true);

                    Process process = processBuilder.start();
                    currentProcess = process;
                    logsPanel.setProcessRunning(true);

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream(), "CP866"));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        logsPanel.appendLog(line);
                    }

                    exitCode = process.waitFor();

                    logsPanel.appendLog("");
                    logsPanel.appendLog("Process finished with code: " + exitCode);

                    currentProcess = null;
                    logsPanel.setProcessRunning(false);
                    logsPanel.enablePdfButton();

                } catch (Exception ex) {

                    currentProcess = null;
                    logsPanel.setProcessRunning(false);
                    logsPanel.appendLog("");
                    logsPanel.appendLog("ERROR: " + ex.getMessage());
                    ex.printStackTrace();

                } finally {

                    if (onFinished != null) {
                        onFinished.onExit(exitCode);
                    }
                }
            }
        });

        thread.start();
    }
}