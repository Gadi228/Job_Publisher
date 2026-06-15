package app.ui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsPanel extends JPanel {

/*
    CONFIG COMBOBOXES
 */

    private final JComboBox<String> applicComboBox;

    private final JComboBox<String> formatComboBox;

    private final JComboBox<String> checksumTypeComboBox;

    private final JComboBox<String> noticeTypeComboBox;

/*
    CHECKBOXES
 */

    private final JCheckBox pmPreviousGenerateCheckBox;

    private final JCheckBox fullDocumentCheckBox;

    private final JCheckBox onlyChangedCheckBox;

    private final JCheckBox ignoreExtensionCheckBox;

    private final JCheckBox createFolderCheckBox;

    private final JCheckBox checksumCheckBox;

    private final JCheckBox exportPackageCheckBox;

    private final JCheckBox zipCheckBox;

    private final JCheckBox svgCheckBox;

    private final JCheckBox svgImageCheckBox;

    private final JCheckBox ramanCheckBox;

    private final JCheckBox docNumberCheckBox;

    private final JCheckBox checkIssueCheckBox;

    private final JCheckBox copyDclCheckBox;

    private final JCheckBox createTempNoticeCheckBox;

    private final JCheckBox checkApplicRFUCheckBox;

    private final Font uiFont =
            new Font("Arial", Font.PLAIN, 18);

    public SettingsPanel() {

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

        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1.0;

        int row = 0;

    /*
        CONFIGS
     */

        applicComboBox =
                createComboBox(
                        new String[]{"pm"}
                );

        addRow(
                contentPanel,
                gbc,
                row++,
                "applic",
                applicComboBox
        );

        formatComboBox =
                createComboBox(
                        new String[]{"A4"}
                );

        addRow(
                contentPanel,
                gbc,
                row++,
                "format",
                formatComboBox
        );

        checksumTypeComboBox =
                createComboBox(
                        new String[]{
                                "streebogCpverify",
                                "md5",
                                "cpverify"
                        }
                );

        addRow(
                contentPanel,
                gbc,
                row++,
                "checksumType",
                checksumTypeComboBox
        );

        noticeTypeComboBox =
                createComboBox(
                        new String[]{"general"}
                );

        addRow(
                contentPanel,
                gbc,
                row++,
                "noticeType",
                noticeTypeComboBox
        );

    /*
        CHECKBOXES
     */

        pmPreviousGenerateCheckBox =
                createCheckBox(true);

        addRow(
                contentPanel,
                gbc,
                row++,
                "pmPreviousGenerate",
                pmPreviousGenerateCheckBox
        );

        fullDocumentCheckBox =
                createCheckBox(true);

        addRow(
                contentPanel,
                gbc,
                row++,
                "fullDocument",
                fullDocumentCheckBox
        );

        onlyChangedCheckBox =
                createCheckBox(false);

        addRow(
                contentPanel,
                gbc,
                row++,
                "onlyChanged",
                onlyChangedCheckBox
        );

        ignoreExtensionCheckBox =
                createCheckBox(false);

        addRow(
                contentPanel,
                gbc,
                row++,
                "ignoreExtension",
                ignoreExtensionCheckBox
        );

        createFolderCheckBox =
                createCheckBox(true);

        addRow(
                contentPanel,
                gbc,
                row++,
                "createFolder",
                createFolderCheckBox
        );

        checksumCheckBox =
                createCheckBox(true);

        addRow(
                contentPanel,
                gbc,
                row++,
                "checksum",
                checksumCheckBox
        );

        exportPackageCheckBox =
                createCheckBox(false);

        addRow(
                contentPanel,
                gbc,
                row++,
                "exportPackage",
                exportPackageCheckBox
        );

        zipCheckBox =
                createCheckBox(true);

        addRow(
                contentPanel,
                gbc,
                row++,
                "zip",
                zipCheckBox
        );

        svgCheckBox =
                createCheckBox(false);

        addRow(
                contentPanel,
                gbc,
                row++,
                "svg",
                svgCheckBox
        );

        svgImageCheckBox =
                createCheckBox(true);

        addRow(
                contentPanel,
                gbc,
                row++,
                "svgImage",
                svgImageCheckBox
        );

        ramanCheckBox =
                createCheckBox(false);

        addRow(
                contentPanel,
                gbc,
                row++,
                "raman",
                ramanCheckBox
        );

        docNumberCheckBox =
                createCheckBox(true);

        addRow(
                contentPanel,
                gbc,
                row++,
                "docNumber",
                docNumberCheckBox
        );

        checkIssueCheckBox =
                createCheckBox(false);

        addRow(
                contentPanel,
                gbc,
                row++,
                "checkIssue",
                checkIssueCheckBox
        );

        copyDclCheckBox =
                createCheckBox(false);

        addRow(
                contentPanel,
                gbc,
                row++,
                "copyDcl",
                copyDclCheckBox
        );

        createTempNoticeCheckBox =
                createCheckBox(false);

        addRow(
                contentPanel,
                gbc,
                row++,
                "createTempNotice",
                createTempNoticeCheckBox
        );

        checkApplicRFUCheckBox =
                createCheckBox(false);

        addRow(
                contentPanel,
                gbc,
                row++,
                "checkApplicRFU",
                checkApplicRFUCheckBox
        );

        JScrollPane scrollPane =
                new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JComboBox<String> createComboBox(
            String[] values
    ) {

        JComboBox<String> comboBox =
                new JComboBox<>(values);

        comboBox.setFont(uiFont);

        return comboBox;
    }

    private JCheckBox createCheckBox(
            boolean selected
    ) {

        JCheckBox checkBox =
                new JCheckBox();

        checkBox.setSelected(selected);

        checkBox.setFont(uiFont);

        return checkBox;
    }

    private void addRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            JComponent component
    ) {

        JLabel label =
                new JLabel(labelText);

        label.setFont(uiFont);

        gbc.gridx = 0;
        gbc.gridy = row;

        panel.add(label, gbc);

        gbc.gridx = 1;

        panel.add(component, gbc);
    }

/*
    GET CONFIG VALUES
 */

    public Map<String, String> getConfigValues() {

        Map<String, String> values =
                new LinkedHashMap<>();

        values.put(
                "applic",
                (String) applicComboBox.getSelectedItem()
        );

        values.put(
                "format",
                (String) formatComboBox.getSelectedItem()
        );

        values.put(
                "checksumType",
                (String) checksumTypeComboBox
                        .getSelectedItem()
        );

        values.put(
                "noticeType",
                (String) noticeTypeComboBox
                        .getSelectedItem()
        );

        return values;
    }

/*
    GET ENABLED FLAGS
 */

    public Map<String, Boolean> getFlagValues() {

        Map<String, Boolean> flags =
                new LinkedHashMap<>();

        flags.put(
                "pmPreviousGenerate",
                pmPreviousGenerateCheckBox.isSelected()
        );

        flags.put(
                "fullDocument",
                fullDocumentCheckBox.isSelected()
        );

        flags.put(
                "onlyChanged",
                onlyChangedCheckBox.isSelected()
        );

        flags.put(
                "ignoreExtension",
                ignoreExtensionCheckBox.isSelected()
        );

        flags.put(
                "createFolder",
                createFolderCheckBox.isSelected()
        );

        flags.put(
                "checksum",
                checksumCheckBox.isSelected()
        );

        flags.put(
                "exportPackage",
                exportPackageCheckBox.isSelected()
        );

        flags.put(
                "zip",
                zipCheckBox.isSelected()
        );

        flags.put(
                "svg",
                svgCheckBox.isSelected()
        );

        flags.put(
                "svgImage",
                svgImageCheckBox.isSelected()
        );

        flags.put(
                "raman",
                ramanCheckBox.isSelected()
        );

        flags.put(
                "docNumber",
                docNumberCheckBox.isSelected()
        );

        flags.put(
                "checkIssue",
                checkIssueCheckBox.isSelected()
        );

        flags.put(
                "copyDcl",
                copyDclCheckBox.isSelected()
        );

        flags.put(
                "createTempNotice",
                createTempNoticeCheckBox.isSelected()
        );

        flags.put(
                "checkApplicRFU",
                checkApplicRFUCheckBox.isSelected()
        );

        return flags;
    }
}
