package app.ui;

import app.service.CommandService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignPanel extends JPanel {

    private final Font uiFont     = new Font("Arial", Font.PLAIN, 18);
    private final Font uiFontBold = new Font("Arial", Font.BOLD,  18);

    /* Master toggle */
    private final JCheckBox enabledCheckBox;

    /* PDF selection */
    private final TruncatedPathField pdfPathField;
    private final JButton            browsePdfButton;

    /* Fields */
    private final JTextField aliasField;
    private final JSpinner   pageSpinner;
    private final JSpinner   x1Spinner;
    private final JSpinner   x2Spinner;
    private final JSpinner   y1Spinner;
    private final JSpinner   y2Spinner;

    /* Bottom controls */
    private final JButton runButton;
    private final JLabel  statusLabel;

    private final LogsPanel logsPanel;

    public SignPanel(LogsPanel logsPanel) {

        this.logsPanel = logsPanel;

        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8, 8, 8, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // ── Подпись (master toggle) ─────────────────────────────────
        enabledCheckBox = new JCheckBox(
                "\u041f\u043e\u0434\u043f\u0438\u0441\u044c"); // Подпись
        enabledCheckBox.setFont(uiFontBold);
        enabledCheckBox.setSelected(false);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        contentPanel.add(enabledCheckBox, gbc);
        gbc.gridwidth = 1;
        row++;

        // ── PDF файл ────────────────────────────────────────────────
        JLabel pdfLabel = new JLabel("PDF \u0444\u0430\u0439\u043b"); // PDF файл
        pdfLabel.setFont(uiFont);

        pdfPathField = new TruncatedPathField();
        pdfPathField.setFont(new Font("Arial", Font.PLAIN, 14));

        browsePdfButton = new JButton("...");
        browsePdfButton.setFont(uiFontBold);
        browsePdfButton.setPreferredSize(new Dimension(46, 30));
        browsePdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browsePdf();
            }
        });

        // PDF row: label | path field (grows) | browse button
        JPanel pdfRow = new JPanel(new BorderLayout(6, 0));
        pdfRow.add(pdfPathField,   BorderLayout.CENTER);
        pdfRow.add(browsePdfButton, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(pdfLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(pdfRow, gbc);
        row++;

        // ── Сертификат ──────────────────────────────────────────────
        aliasField = new JTextField();
        aliasField.setFont(uiFont);
        addRow(contentPanel, gbc, row++,
                "\u0421\u0435\u0440\u0442\u0438\u0444\u0438\u043a\u0430\u0442", // Сертификат
                aliasField);

        // ── Страница ────────────────────────────────────────────────
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        pageSpinner.setFont(uiFont);
        ((JSpinner.DefaultEditor) pageSpinner.getEditor())
                .getTextField().setFont(uiFont);
        addRow(contentPanel, gbc, row++,
                "\u0421\u0442\u0440\u0430\u043d\u0438\u0446\u0430", // Страница
                pageSpinner);

        // ── Координаты подписи ──────────────────────────────────────
        JLabel coordsHeader = new JLabel(
                "\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u043f\u043e\u0434\u043f\u0438\u0441\u0438"); // Координаты подписи
        coordsHeader.setFont(uiFontBold);
        coordsHeader.setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        contentPanel.add(coordsHeader, gbc);
        gbc.gridwidth = 1;
        row++;

        x1Spinner = makeDoubleSpinner();
        addRow(contentPanel, gbc, row++, "x1", x1Spinner);

        x2Spinner = makeDoubleSpinner();
        addRow(contentPanel, gbc, row++, "x2", x2Spinner);

        y1Spinner = makeDoubleSpinner();
        addRow(contentPanel, gbc, row++, "y1", y1Spinner);

        y2Spinner = makeDoubleSpinner();
        addRow(contentPanel, gbc, row++, "y2", y2Spinner);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ── SOUTH: run button + status ──────────────────────────────
        runButton = new JButton(
                "\u041f\u043e\u0434\u043f\u0438\u0441\u0430\u0442\u044c"); // Подписать
        runButton.setFont(uiFontBold);
        runButton.setPreferredSize(new Dimension(180, 46));
        runButton.setBackground(new Color(34, 100, 180));
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runSign();
            }
        });

        statusLabel = new JLabel(" ", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel southPanel = new JPanel(new BorderLayout(12, 0));
        southPanel.setBorder(BorderFactory.createEmptyBorder(8, 24, 12, 24));
        southPanel.add(runButton,   BorderLayout.WEST);
        southPanel.add(statusLabel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // ── Wire master toggle ──────────────────────────────────────
        enabledCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                syncFieldsEnabled();
            }
        });
        syncFieldsEnabled();

        // ── Auto-detect latest PDF on startup ───────────────────────
        autoDetectPdf();
    }

    // ---------------------------------------------------------------
    // Called by MainFrame / SettingsPanel after publish finishes
    // If the checkbox is on, signs automatically
    // ---------------------------------------------------------------
    public void onPublishFinished() {

        // Refresh the PDF path to the freshly-published file
        autoDetectPdf();

        if (enabledCheckBox.isSelected()) {
            runSign();
        }
    }

    // ---------------------------------------------------------------
    // Auto-detect latest PDF
    // ---------------------------------------------------------------
    private void autoDetectPdf() {

        File latest = findLatestPdf();
        if (latest != null) {
            pdfPathField.setText(latest.getAbsolutePath());
        }
    }

    // ---------------------------------------------------------------
    // Browse for PDF
    // ---------------------------------------------------------------
    private void browsePdf() {

        JFileChooser chooser = new JFileChooser();

        // Default directory: Publisher/XMLDone
        File defaultDir = new File("./Publisher/XMLDone/");
        if (defaultDir.exists()) {
            chooser.setCurrentDirectory(defaultDir);
        }

        chooser.setDialogTitle("PDF \u0444\u0430\u0439\u043b"); // PDF файл
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PDF \u0444\u0430\u0439\u043b\u044b (*.pdf)", "pdf")); // PDF файлы

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            pdfPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    // ---------------------------------------------------------------
    // Enable / disable fields
    // ---------------------------------------------------------------
    private void syncFieldsEnabled() {

        boolean on = enabledCheckBox.isSelected();

        pdfPathField.setEnabled(on);
        browsePdfButton.setEnabled(on);
        aliasField.setEnabled(on);
        pageSpinner.setEnabled(on);
        x1Spinner.setEnabled(on);
        x2Spinner.setEnabled(on);
        y1Spinner.setEnabled(on);
        y2Spinner.setEnabled(on);
        runButton.setEnabled(on);
    }

    // ---------------------------------------------------------------
    // Build and run the sign command
    // ---------------------------------------------------------------
    public void runSign() {

        if (!enabledCheckBox.isSelected()) return;

        String inPath = pdfPathField.getFullPath().trim();
        if (inPath.isEmpty()) {
            setStatus(
                    "\u041d\u0435 \u0443\u043a\u0430\u0437\u0430\u043d PDF \u0444\u0430\u0439\u043b", // Не указан PDF файл
                    Color.RED);
            return;
        }

        File inPdf  = new File(inPath);
        File outPdf = buildSignedPath(inPdf);

        List<String> command = new ArrayList<String>();

        command.add("java");
        command.add("-jar");
        command.add("./res/lib/CryptoPro/SignPDF.jar");

        command.add("-in");
        command.add(inPdf.getAbsolutePath());

        command.add("-out");
        command.add(outPdf.getAbsolutePath());

        String alias = aliasField.getText().trim();
        if (!alias.isEmpty()) {
            command.add("-alias");
            command.add(alias);
        }

        command.add("-page");
        command.add(String.valueOf(pageSpinner.getValue()));

        command.add("-x1");
        command.add(formatDouble(x1Spinner.getValue()));

        command.add("-x2");
        command.add(formatDouble(x2Spinner.getValue()));

        command.add("-y1");
        command.add(formatDouble(y1Spinner.getValue()));

        command.add("-y2");
        command.add(formatDouble(y2Spinner.getValue()));

        // Log the command
        logsPanel.appendLog(
                "\u0417\u0430\u043f\u0443\u0441\u043a \u043f\u043e\u0434\u043f\u0438\u0441\u0438:"); // Запуск подписи:
        StringBuilder sb = new StringBuilder();
        for (String part : command) {
            if (part.contains(" ")) sb.append('"').append(part).append('"');
            else                    sb.append(part);
            sb.append(' ');
        }
        logsPanel.appendLog(sb.toString());
        logsPanel.appendLog("");

        setStatus(
                "\u0412\u044b\u043f\u043e\u043b\u043d\u044f\u0435\u0442\u0441\u044f...", // Выполняется...
                Color.DARK_GRAY);
        runButton.setEnabled(false);

        CommandService.runCommandWithCallback(command, logsPanel,
                new CommandService.ExitCallback() {
                    @Override
                    public void onExit(final int exitCode) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                runButton.setEnabled(enabledCheckBox.isSelected());
                                if (exitCode == 0) {
                                    setStatus(
                                            "\u0413\u043e\u0442\u043e\u0432\u043e", // Готово
                                            new Color(20, 140, 20));
                                } else {
                                    setStatus(
                                            "\u041e\u0448\u0438\u0431\u043a\u0430 (\u043a\u043e\u0434 " + exitCode + ")", // Ошибка (код N)
                                            Color.RED);
                                }
                            }
                        });
                    }
                });
    }

    // ---------------------------------------------------------------
    // Settings persistence
    // ---------------------------------------------------------------
    public void applySettings(Properties props) {

        String enabled = props.getProperty("sign.enabled");
        if (enabled != null) {
            enabledCheckBox.setSelected(Boolean.parseBoolean(enabled));
        }

        String alias = props.getProperty("sign.alias");
        if (alias != null) aliasField.setText(alias);

        String page = props.getProperty("sign.page");
        if (page != null) {
            try { pageSpinner.setValue(Integer.parseInt(page)); }
            catch (NumberFormatException ignored) {}
        }

        applyDouble(x1Spinner, props.getProperty("sign.x1"));
        applyDouble(x2Spinner, props.getProperty("sign.x2"));
        applyDouble(y1Spinner, props.getProperty("sign.y1"));
        applyDouble(y2Spinner, props.getProperty("sign.y2"));

        syncFieldsEnabled();
    }

    public void saveSettings(Properties props) {

        props.setProperty("sign.enabled", String.valueOf(enabledCheckBox.isSelected()));
        props.setProperty("sign.alias",   aliasField.getText().trim());
        props.setProperty("sign.page",    String.valueOf(pageSpinner.getValue()));
        props.setProperty("sign.x1",      formatDouble(x1Spinner.getValue()));
        props.setProperty("sign.x2",      formatDouble(x2Spinner.getValue()));
        props.setProperty("sign.y1",      formatDouble(y1Spinner.getValue()));
        props.setProperty("sign.y2",      formatDouble(y2Spinner.getValue()));
    }

    // ---------------------------------------------------------------
    // Static utils (also used externally for PDF search)
    // ---------------------------------------------------------------
    public static File findLatestPdf() {

        File xmlDoneFolder = new File("./Publisher/XMLDone/");
        File[] folders = xmlDoneFolder.listFiles();
        if (folders == null || folders.length == 0) return null;

        File latestFolder    = null;
        long latestFolderTime = 0;

        for (File f : folders) {
            if (f.isDirectory() && f.lastModified() > latestFolderTime) {
                latestFolderTime = f.lastModified();
                latestFolder     = f;
            }
        }

        if (latestFolder == null) return null;

        File[] files = latestFolder.listFiles();
        if (files == null) return null;

        File latestPdf    = null;
        long latestPdfTime = 0;

        for (File f : files) {
            if (f.isFile()
                    && f.getName().toLowerCase().endsWith(".pdf")
                    && f.lastModified() > latestPdfTime) {
                latestPdfTime = f.lastModified();
                latestPdf     = f;
            }
        }

        return latestPdf;
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------
    /**
     * Builds the output path for a signed PDF.
     * Rules:
     *  - "report.pdf"         → "report.signed.pdf"
     *  - "report.signed.pdf"  → "report.signed1.pdf"
     *  - "report.signed1.pdf" → "report.signed2.pdf"  … and so on
     * The first available (non-existing) filename is returned.
     */
    private static File buildSignedPath(File src) {

        String name = src.getName();
        int dot = name.lastIndexOf('.');
        String base = (dot >= 0) ? name.substring(0, dot) : name;
        String ext  = (dot >= 0) ? name.substring(dot)    : "";

        // Detect whether base already ends with ".signed" or ".signedN"
        Pattern p = Pattern.compile("^(.*)\\.signed(\\d*)$");
        Matcher m = p.matcher(base);

        String coreBase;
        int counter;

        if (m.matches()) {
            coreBase = m.group(1);
            String digits = m.group(2);
            counter = digits.isEmpty() ? 1 : Integer.parseInt(digits) + 1;
        } else {
            coreBase = base;
            counter  = 0; // 0 means first attempt → ".signed" (no number)
        }

        // Find a filename that does not yet exist on disk
        File candidate;
        while (true) {
            String suffix = (counter == 0) ? ".signed" : ".signed" + counter;
            candidate = new File(src.getParentFile(), coreBase + suffix + ext);
            if (!candidate.exists()) break;
            counter++;
        }

        return candidate;
    }

    private JSpinner makeDoubleSpinner() {

        JSpinner s = new JSpinner(
                new SpinnerNumberModel(0.0, -9999.0, 9999.0, 0.1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(s, "0.##");
        editor.getTextField().setFont(uiFont);
        s.setEditor(editor);
        s.setFont(uiFont);
        return s;
    }

    private String formatDouble(Object val) {

        double d = ((Number) val).doubleValue();
        if (d == Math.floor(d) && !Double.isInfinite(d)) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }

    private void applyDouble(JSpinner spinner, String value) {

        if (value == null) return;
        try { spinner.setValue(Double.parseDouble(value)); }
        catch (NumberFormatException ignored) {}
    }

    private void addRow(JPanel panel, GridBagConstraints gbc,
                        int row, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(uiFont);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    // ---------------------------------------------------------------
    // TruncatedPathField
    // A non-editable text field that paints the path right-aligned
    // with "..." prepended when it does not fit — so the user always
    // sees the filename end (the most useful part).
    // ---------------------------------------------------------------
    private static final class TruncatedPathField extends JTextField {

        private String fullPath = "";

        TruncatedPathField() {
            super();
            setEditable(false);
            // Prevent the field from forcing the panel to grow
            setMinimumSize(new Dimension(40, 24));
            setPreferredSize(new Dimension(40, 24));
        }

        @Override
        public void setText(String t) {
            this.fullPath = (t == null) ? "" : t;
            // We will do our own painting; store full path but pass
            // something short to super so layout doesn't grow.
            super.setText(fullPath);
        }

        /** Returns the full untruncated path (used when building the command). */
        public String getFullPath() {
            return fullPath;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (fullPath.isEmpty()) return;

            FontMetrics fm = g.getFontMetrics();
            int availW = getWidth() - getInsets().left - getInsets().right - 6;

            String display;
            if (fm.stringWidth(fullPath) <= availW) {
                display = fullPath;
            } else {
                // Trim from the left until it fits with "..." prefix
                String ellipsis = "...";
                int ellipsisW = fm.stringWidth(ellipsis);
                int start = 0;
                while (start < fullPath.length()
                        && fm.stringWidth(fullPath.substring(start)) + ellipsisW > availW) {
                    start++;
                }
                display = ellipsis + fullPath.substring(start);
            }

            // Paint background first (handled by super), then our text
            Insets ins = getInsets();
            int x = ins.left + 3;
            int y = ins.top + fm.getAscent()
                    + (getHeight() - ins.top - ins.bottom - fm.getHeight()) / 2;

            // Clear the text area painted by super, then redraw ours
            g.setColor(getBackground());
            g.fillRect(ins.left, ins.top,
                    getWidth() - ins.left - ins.right,
                    getHeight() - ins.top - ins.bottom);

            g.setColor(isEnabled() ? getForeground() : Color.GRAY);
            g.drawString(display, x, y);
        }
    }
}