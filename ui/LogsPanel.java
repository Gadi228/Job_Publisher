package app.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LogsPanel extends JPanel {

    private final JTextArea textArea;

    private final JButton openPdfButton;
    private final JButton stopButton;

    /** Optional StartPanel to forward progress events to. */
    private StartPanel startPanel = null;

    /** Set to true when an ===> Error: line is seen in the current run. */
    private volatile boolean errorSeen   = false;

    /** Set to true only when "Process finished with code: 100" is seen. */
    private volatile boolean successSeen = false;

    public LogsPanel() {

        setLayout(new BorderLayout());

        textArea = new JTextArea();

        textArea.setFont(
                new Font("Consolas", Font.PLAIN, 18)
        );

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        openPdfButton = new JButton("Открыть последний PDF файл");
        openPdfButton.setEnabled(false);
        openPdfButton.setFont(new Font("Arial", Font.BOLD, 18));
        openPdfButton.addActionListener(e -> triggerOpenPdf());

        stopButton = new JButton("Stop Script");
        stopButton.setFont(new Font("Arial", Font.BOLD, 18));
        stopButton.setEnabled(false);
        stopButton.setBackground(Color.RED);
        stopButton.setForeground(Color.WHITE);
        stopButton.addActionListener(
                e -> app.service.CommandService.stopCurrentProcess()
        );

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(openPdfButton);
        bottomPanel.add(stopButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------

    /** Wire the StartPanel so log lines drive the progress bar. */
    public void setStartPanel(StartPanel sp) {
        this.startPanel = sp;

        // Wire the StartPanel's openPdf button to this panel's logic
        sp.wireOpenPdfButton(this);
    }

    // ---------------------------------------------------------------

    public void appendLog(String text) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                textArea.append(text + "\n");
                textArea.setCaretPosition(
                        textArea.getDocument().getLength()
                );

                // Detect error line
                if (text.contains("===> Error:")) {
                    errorSeen = true;
                }

                // Detect success line
                if (text.contains("Process finished with code: 100")) {
                    successSeen = true;
                }

                // Forward to StartPanel progress tracker
                if (startPanel != null) {
                    startPanel.onLogLine(text);
                }
            }
        });
    }

    public void clearLogs() {
        errorSeen   = false;
        successSeen = false;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.setText("");
            }
        });
    }

    public void enablePdfButton() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                openPdfButton.setEnabled(true);
                if (startPanel != null) {
                    startPanel.enablePdfButton();
                }
            }
        });
    }

    public void setProcessRunning(final boolean running) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                stopButton.setEnabled(running);

                if (running) {
                    stopButton.setBackground(Color.RED);
                } else {
                    stopButton.setBackground(
                            UIManager.getColor("Button.background")
                    );
                    openPdfButton.setEnabled(true);
                }

                if (startPanel != null) {
                    startPanel.setProcessRunning(running);
                    if (!running) {
                        startPanel.onProcessFinished(successSeen && !errorSeen);
                    }
                }
            }
        });
    }

    // ---------------------------------------------------------------

    /** Called directly by StartPanel's openPdf button. */
    public void triggerOpenPdf() {
        openLatestPdf();
    }

    private void openLatestPdf() {

        try {

            File xmlDoneFolder = new File("./Publisher/XMLDone/");
            File[] folders = xmlDoneFolder.listFiles();

            if (folders == null || folders.length == 0) {
                appendLog("No folders found in XMLDone.");
                return;
            }

            File latestFolder    = null;
            long latestFolderTime = 0;

            for (File folder : folders) {
                if (folder.isDirectory()
                        && folder.lastModified() > latestFolderTime) {
                    latestFolderTime = folder.lastModified();
                    latestFolder     = folder;
                }
            }

            if (latestFolder == null) {
                appendLog("No subfolders found in XMLDone.");
                return;
            }

            File[] files = latestFolder.listFiles();

            if (files == null || files.length == 0) {
                appendLog("No files found in folder: " + latestFolder.getName());
                return;
            }

            File latestPdf    = null;
            long latestPdfTime = 0;

            for (File file : files) {
                if (file.isFile()
                        && file.getName().toLowerCase().endsWith(".pdf")
                        && file.lastModified() > latestPdfTime) {
                    latestPdfTime = file.lastModified();
                    latestPdf     = file;
                }
            }

            if (latestPdf == null) {
                appendLog("No PDF found in folder: " + latestFolder.getName());
                return;
            }

            appendLog("Opening PDF: " + latestPdf.getAbsolutePath());
            Desktop.getDesktop().open(latestPdf);

        } catch (Exception ex) {
            appendLog("Failed to open PDF: " + ex.getMessage());
        }
    }
}