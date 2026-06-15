package app.ui;

import app.service.CommandService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LaunchPanel extends JPanel {

    private final JComboBox<String> languageComboBox;

    private final JComboBox<String> pubSourceComboBox;

    private final JComboBox<String> xmlFolderComboBox;

    private final JButton runButton;

    private final LogsPanel logsPanel;

    private final SettingsPanel settingsPanel;

    private final JTabbedPane tabbedPane;


    private final Font uiFont =
            new Font("Arial", Font.PLAIN, 18);

    public LaunchPanel(
            LogsPanel logsPanel,
            SettingsPanel settingsPanel,
            JTabbedPane tabbedPane
    ) {

        this.logsPanel = logsPanel;
        this.settingsPanel = settingsPanel;
        this.tabbedPane = tabbedPane;

        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();

        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1.0;

    /*
        LANGUAGE
     */

        JLabel languageLabel =
                new JLabel("Language");

        languageLabel.setFont(uiFont);

        languageComboBox = new JComboBox<>(
                new String[]{
                        "Русский",
                        "English"
                }
        );

        languageComboBox.setFont(uiFont);

        languageComboBox.setSelectedIndex(0);

        gbc.gridx = 0;
        gbc.gridy = 0;

        contentPanel.add(languageLabel, gbc);

        gbc.gridx = 1;

        contentPanel.add(languageComboBox, gbc);

    /*
        PUB SOURCE
     */

        JLabel pubSourceLabel =
                new JLabel("PubSource");

        pubSourceLabel.setFont(uiFont);

        pubSourceComboBox = new JComboBox<>();

        pubSourceComboBox.setFont(uiFont);

        loadPubSourceFiles();

        gbc.gridx = 0;
        gbc.gridy = 1;

        contentPanel.add(pubSourceLabel, gbc);

        gbc.gridx = 1;

        contentPanel.add(pubSourceComboBox, gbc);

    /*
        XML FOLDER
     */

        JLabel xmlFolderLabel =
                new JLabel("XML Folder");

        xmlFolderLabel.setFont(uiFont);

        xmlFolderComboBox = new JComboBox<>();

        xmlFolderComboBox.setFont(uiFont);

        loadXmlFolders();

        gbc.gridx = 0;
        gbc.gridy = 2;

        contentPanel.add(xmlFolderLabel, gbc);

        gbc.gridx = 1;

        contentPanel.add(xmlFolderComboBox, gbc);

        add(contentPanel, BorderLayout.CENTER);

    /*
        BUTTON
     */

        JPanel bottomPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER
                        )
                );

        runButton = new JButton("Запуск");

        runButton.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        runButton.setPreferredSize(
                new Dimension(220, 50)
        );

        bottomPanel.add(runButton);

        add(bottomPanel, BorderLayout.SOUTH);

        runButton.addActionListener(e -> runCommand());
    }

    private void loadPubSourceFiles() {

        File folder =
                new File("./Publisher/pubSources/");

        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        int defaultIndex = -1;

        for (File file : files) {

            if (file.isFile()) {

                pubSourceComboBox.addItem(
                        file.getName()
                );

                if (file.getName().equals(
                        "pubSource_issue_114300.xml"
                )) {

                    defaultIndex =
                            pubSourceComboBox.getItemCount() - 1;
                }
            }
        }

        if (defaultIndex != -1) {
            pubSourceComboBox.setSelectedIndex(
                    defaultIndex
            );
        }
    }

    private void loadXmlFolders() {

        File folder =
                new File("./Publisher/XML/");

        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            if (file.isDirectory()) {

                xmlFolderComboBox.addItem(
                        file.getName()
                );
            }
        }
    }

    private void runCommand() {

        logsPanel.clearLogs();

        List<String> command =
                new ArrayList<String>();

    /*
        MAIN COMMAND
     */

        command.add("cscript");

        command.add("./res/script/test.js");

    /*
        LANGUAGE
     */

        String selectedLanguage =
                (String) languageComboBox
                        .getSelectedItem();

        command.add("-language");

        if ("Русский".equals(selectedLanguage)) {
            command.add("ru");
        } else {
            command.add("en");
        }

    /*
        PUB SOURCE
     */

        String selectedPubSource =
                (String) pubSourceComboBox
                        .getSelectedItem();

        if (selectedPubSource != null
                && !selectedPubSource.isEmpty()) {

            command.add("-pubSource");

            command.add(
                    "\"./pubSources/"
                           + selectedPubSource + '\"'
            );
        }

    /*
        XML FOLDER
     */

        String selectedXmlFolder =
                (String) xmlFolderComboBox
                        .getSelectedItem();

        if (selectedXmlFolder != null
                && !selectedXmlFolder.isEmpty()) {

            command.add("-xml");

            command.add("\"" + "./XML/" + selectedXmlFolder + "/\"");
        }
        /*
    SETTINGS CONFIGS
 */

        for (Map.Entry<String, String> entry :
                settingsPanel.getConfigValues().entrySet()) {

            command.add("-" + entry.getKey());

            command.add(entry.getValue());
        }

/*
    SETTINGS FLAGS
 */

        for (Map.Entry<String, Boolean> entry :
                settingsPanel.getFlagValues().entrySet()) {

            if (entry.getValue()) {

                command.add("-" + entry.getKey());
            }
        }

    /*
        LOGGING
     */

        logsPanel.appendLog("Running command:");

        StringBuilder fullCommand =
                new StringBuilder();

        for (String part : command) {

            if (part.contains(" ")) {

                fullCommand
                        .append("\"")
                        .append(part)
                        .append("\"");

            } else {

                fullCommand.append(part);
            }

            fullCommand.append(" ");
        }

        logsPanel.appendLog(
                fullCommand.toString()
        );

        logsPanel.appendLog("");

    /*
        SWITCH TO LOGS TAB
     */

        tabbedPane.setSelectedIndex(2);

    /*
        RUN
     */

        CommandService.runCommand(
                command,
                logsPanel
        );
    }

}
