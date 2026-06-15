package app.service;

import app.ui.LogsPanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class CommandService {

    private static Process currentProcess;

    public static void stopCurrentProcess() {

        try {

            if (currentProcess != null) {

                currentProcess.destroyForcibly();
            }

        } catch (Exception ignored) {
        }
    }

    public static void runCommand(
            List<String> command,
            LogsPanel logsPanel
    ) {

        Thread thread =
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        try {

                            ProcessBuilder processBuilder =
                                    new ProcessBuilder(command);
                            File publicatorDir =
                                    new File("./Publisher");

                            if (!publicatorDir.exists()) {
                                logsPanel.appendLog("Publisher folder not found: " + publicatorDir.getAbsolutePath());
                                return;
                            }
                            else {
                                logsPanel.appendLog("Publisher folder found!!!");
                            }

                            processBuilder.directory(publicatorDir);
                            processBuilder.redirectErrorStream(
                                    true
                            );

                            Process process = processBuilder.start();
                            currentProcess = process;

                            logsPanel.setProcessRunning(true);

                            InputStream inputStream =
                                    process.getInputStream();

                            BufferedReader reader =
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    inputStream,
                                                    "CP866"
                                            )
                                    );

                            String line;

                            while ((line =
                                    reader.readLine()) != null) {

                                logsPanel.appendLog(line);
                            }

                            int exitCode =
                                    process.waitFor();

                            logsPanel.appendLog("");

                            logsPanel.appendLog(
                                    "Process finished with code: "
                                            + exitCode
                            );

                            currentProcess = null;
                            logsPanel.setProcessRunning(false);

                            logsPanel.enablePdfButton();

                        } catch (Exception ex) {
                            currentProcess = null;

                            logsPanel.setProcessRunning(false);
                            logsPanel.appendLog("");

                            logsPanel.appendLog(
                                    "ERROR: "
                                            + ex.getMessage()
                            );

                            ex.printStackTrace();
                        }

                    }
                });

        thread.start();
    }

}
