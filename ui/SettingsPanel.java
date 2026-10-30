package app.ui;

import app.service.CommandService;
import app.service.SettingsService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SettingsPanel extends JPanel {

    private final JComboBox<String> languageComboBox;
    private final JComboBox<String> pubSourceComboBox;
    private final JComboBox<String> xmlFolderComboBox;
    private final JComboBox<String> fontComboBox;
    private final JComboBox<String> stylesComboBox;

    private final LogsPanel     logsPanel;
    private final AdvancedPanel advancedPanel;
    private final StartPanel    startPanel;
    private       SignPanel     signPanel; // set after construction via setSignPanel()

    private final Font uiFont = new Font("Arial", Font.PLAIN, 18);

    public SettingsPanel(
            LogsPanel    logsPanel,
            AdvancedPanel advancedPanel,
            StartPanel   startPanel
    ) {
        this.logsPanel     = logsPanel;
        this.advancedPanel = advancedPanel;
        this.startPanel    = startPanel;

        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(10, 10, 10, 10);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Язык
        JLabel languageLabel = new JLabel("\u042f\u0437\u044b\u043a"); // Язык
        languageLabel.setFont(uiFont);
        languageComboBox = new JComboBox<>(new String[]{"\u0420\u0443\u0441\u0441\u043a\u0438\u0439", "English"});
        languageComboBox.setFont(uiFont);
        languageComboBox.setSelectedIndex(0);
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(languageLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(languageComboBox, gbc);

        // PubSource
        JLabel pubSourceLabel = new JLabel("PubSource");
        pubSourceLabel.setFont(uiFont);
        pubSourceComboBox = new JComboBox<>();
        pubSourceComboBox.setFont(uiFont);
        loadPubSourceFiles();
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(pubSourceLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(pubSourceComboBox, gbc);

        // Исходные файлы
        JLabel xmlFolderLabel = new JLabel("\u0418\u0441\u0445\u043e\u0434\u043d\u044b\u0435 \u0444\u0430\u0439\u043b\u044b"); // Исходные файлы
        xmlFolderLabel.setFont(uiFont);
        xmlFolderComboBox = new JComboBox<>();
        xmlFolderComboBox.setFont(uiFont);
        loadXmlFolders();
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(xmlFolderLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(xmlFolderComboBox, gbc);

        // Шрифт
        JLabel fontLabel = new JLabel("\u0428\u0440\u0438\u0444\u0442"); // Шрифт
        fontLabel.setFont(uiFont);
        fontComboBox = new JComboBox<>(new String[]{
                "Arial", "Times New Roman", "Courier New",
                "Calibri", "Verdana", "Tahoma"});
        fontComboBox.setFont(uiFont);
        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(fontLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(fontComboBox, gbc);

        // Стили
        JLabel stylesLabel = new JLabel("\u0421\u0442\u0438\u043b\u0438"); // Стили
        stylesLabel.setFont(uiFont);
        stylesComboBox = new JComboBox<>();
        stylesComboBox.setFont(uiFont);
        loadStylesFolders();
        gbc.gridx = 0; gbc.gridy = 4;
        contentPanel.add(stylesLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(stylesComboBox, gbc);

        add(contentPanel, BorderLayout.CENTER);

        loadSettings();

        startPanel.setOnRun(new Runnable() {
            @Override
            public void run() {
                runCommand();
            }
        });
    }

    /** Must be called by MainFrame after SignPanel is created. */
    public void setSignPanel(SignPanel sp) {
        this.signPanel = sp;
    }

    // ---------------------------------------------------------------
    // Loaders
    // ---------------------------------------------------------------

    private void loadPubSourceFiles() {

        File folder = new File("./Publisher/pubSources/");
        File[] files = folder.listFiles();
        if (files == null) return;
        int defaultIndex = -1;
        for (File file : files) {
            if (file.isFile()) {
                pubSourceComboBox.addItem(file.getName());
                if (file.getName().equals("pubSource_issue_114300.xml"))
                    defaultIndex = pubSourceComboBox.getItemCount() - 1;
            }
        }
        if (defaultIndex != -1) pubSourceComboBox.setSelectedIndex(defaultIndex);
    }

    private void loadXmlFolders() {

        File folder = new File("./Publisher/XML/");
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files)
            if (file.isDirectory()) xmlFolderComboBox.addItem(file.getName());
    }

    private void loadStylesFolders() {

        File folder = new File("./Publisher/Styles/");
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files)
            if (file.isDirectory()) stylesComboBox.addItem(file.getName());
    }

    // ---------------------------------------------------------------
    // Settings persistence
    // ---------------------------------------------------------------

    public void loadSettings() {

        Properties props = SettingsService.load();

        selectComboItem(languageComboBox,  props.getProperty("language"));
        selectComboItem(pubSourceComboBox, props.getProperty("pubSource"));
        selectComboItem(xmlFolderComboBox, props.getProperty("xmlFolder"));
        selectComboItem(fontComboBox,      props.getProperty("font"));
        selectComboItem(stylesComboBox,    props.getProperty("styles"));
    }

    private void saveSettings() {

        Properties props = new Properties();

        props.setProperty("language",  getSelected(languageComboBox));
        props.setProperty("pubSource", getSelected(pubSourceComboBox));
        props.setProperty("xmlFolder", getSelected(xmlFolderComboBox));
        props.setProperty("font",      getSelected(fontComboBox));
        props.setProperty("styles",    getSelected(stylesComboBox));

        for (Map.Entry<String, String> e : advancedPanel.getConfigValues().entrySet())
            props.setProperty("adv.cfg." + e.getKey(), e.getValue());

        for (Map.Entry<String, Boolean> e : advancedPanel.getFlagValues().entrySet())
            props.setProperty("adv.flag." + e.getKey(), String.valueOf(e.getValue()));

        // Sign panel settings
        if (signPanel != null) signPanel.saveSettings(props);

        SettingsService.save(props);
    }

    private void selectComboItem(JComboBox<String> combo, String value) {
        if (value == null || value.isEmpty()) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (value.equals(combo.getItemAt(i))) { combo.setSelectedIndex(i); return; }
        }
    }

    private String getSelected(JComboBox<String> combo) {
        Object item = combo.getSelectedItem();
        return item != null ? item.toString() : "";
    }

    // ---------------------------------------------------------------
    // Run command
    // ---------------------------------------------------------------

    private void runCommand() {

        logsPanel.clearLogs();
        startPanel.onProcessStarted();

        // Styles copy
        String selectedStyles = getSelected(stylesComboBox);
        if (!selectedStyles.isEmpty()) {
            File src  = new File("./Publisher/Styles/" + selectedStyles);
            File dest = new File("./Publisher/res/xsl/dm-to-pdf");
            if (dest.exists()) {
                if (!deleteDirectory(dest)) {
                    logsPanel.appendLog("ERROR: Failed to delete existing styles folder: " + dest.getAbsolutePath());
                    startPanel.onProcessFinished(false);
                    return;
                }
                logsPanel.appendLog("Deleted existing styles: " + dest.getAbsolutePath());
            }
            if (!copyDirectory(src, dest)) {
                logsPanel.appendLog("ERROR: Failed to copy styles from: " + src.getAbsolutePath());
                startPanel.onProcessFinished(false);
                return;
            }
            logsPanel.appendLog("Styles copied: " + src.getName() + " -> " + dest.getAbsolutePath());
            logsPanel.appendLog("");
        }

        // Build command
        List<String> command = new ArrayList<String>();
        command.add("cscript");
        command.add("./res/script/test.js");

        command.add("-language");
        command.add("\u0420\u0443\u0441\u0441\u043a\u0438\u0439".equals(getSelected(languageComboBox)) ? "ru" : "en");

        String pubSource = getSelected(pubSourceComboBox);
        if (!pubSource.isEmpty()) { command.add("-pubSource"); command.add("\"./pubSources/" + pubSource + "\""); }

        String xmlFolder = getSelected(xmlFolderComboBox);
        if (!xmlFolder.isEmpty()) { command.add("-xml"); command.add("\"./XML/" + xmlFolder + "/\""); }

        String font = getSelected(fontComboBox);
        if (!font.isEmpty()) { command.add("-font"); command.add("\"" + font + "\""); }

        for (Map.Entry<String, String> entry : advancedPanel.getConfigValues().entrySet()) {
            command.add("-" + entry.getKey());
            command.add(entry.getValue());
        }
        for (Map.Entry<String, Boolean> entry : advancedPanel.getFlagValues().entrySet()) {
            if (entry.getValue()) command.add("-" + entry.getKey());
        }

        // Log command
        logsPanel.appendLog("Running command:");
        StringBuilder sb = new StringBuilder();
        for (String part : command) {
            if (part.contains(" ")) sb.append('"').append(part).append('"');
            else                    sb.append(part);
            sb.append(' ');
        }
        logsPanel.appendLog(sb.toString());
        logsPanel.appendLog("");

        saveSettings();

        // After publish finishes, trigger sign if enabled
        CommandService.runCommandWithCallback(command, logsPanel,
                new CommandService.ExitCallback() {
                    @Override
                    public void onExit(int exitCode) {
                        if (signPanel != null) {
                            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                            signPanel.onPublishFinished();
                        }
                    }
                });
    }

    // ---------------------------------------------------------------
    // File helpers
    // ---------------------------------------------------------------

    private boolean deleteDirectory(File dir) {
        File[] children = dir.listFiles();
        if (children != null) for (File child : children) if (!deleteDirectory(child)) return false;
        return dir.delete();
    }

    private boolean copyDirectory(File src, File dest) {
        if (!dest.mkdirs()) return false;
        File[] children = src.listFiles();
        if (children == null) return true;
        for (File child : children) {
            File destChild = new File(dest, child.getName());
            if (child.isDirectory()) {
                if (!copyDirectory(child, destChild)) return false;
            } else {
                try {
                    java.io.InputStream  in  = new java.io.FileInputStream(child);
                    java.io.OutputStream out = new java.io.FileOutputStream(destChild);
                    byte[] buf = new byte[8192]; int len;
                    while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
                    in.close(); out.close();
                } catch (java.io.IOException ex) {
                    logsPanel.appendLog("ERROR copying file: " + child.getName() + " — " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }
}