package app.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class AdvancedPanel extends JPanel {

    private final JComboBox<String> formatComboBox;
    private final JComboBox<String> checksumTypeComboBox;
    private final JComboBox<String> noticeTypeComboBox;

    private final JCheckBox fullDocumentCheckBox;
    private final JCheckBox onlyChangedCheckBox;
    private final JCheckBox createFolderCheckBox;
    private final JCheckBox checksumCheckBox;
    private final JCheckBox zipCheckBox;
    private final JCheckBox svgCheckBox;
    private final JCheckBox svgImageCheckBox;
    private final JCheckBox ramanCheckBox;
    private final JCheckBox docNumberCheckBox;
    private final JCheckBox checkIssueCheckBox;
    private final JCheckBox createTempNoticeCheckBox;
    private final JCheckBox copyDclCheckBox;
    private final JCheckBox pmPreviousGenerateCheckBox;
    private final JCheckBox checkApplicRFUCheckBox;
    private final JCheckBox exportPackageCheckBox;

    private final Font uiFont = new Font("Arial", Font.PLAIN, 18);

    public AdvancedPanel() {

        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 6, 6, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Формат
        formatComboBox = createComboBox(new String[]{"A4", "A5", "A4 A5"});
        addRow(contentPanel, gbc, row++,
                "\u0424\u043e\u0440\u043c\u0430\u0442", formatComboBox);

        // Чексумма
        checksumTypeComboBox = createComboBox(
                new String[]{"streebogCpverify", "md5", "cpverify"});
        addRow(contentPanel, gbc, row++,
                "\u0427\u0435\u043a\u0441\u0443\u043c\u043c\u0430", checksumTypeComboBox);

        // Сформировать извещение
        createTempNoticeCheckBox = createCheckBox(false);
        addRow(contentPanel, gbc, row++,
                "\u0421\u0444\u043e\u0440\u043c\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0438\u0437\u0432\u0435\u0449\u0435\u043d\u0438\u0435",
                createTempNoticeCheckBox);

        // Тип извещения — disabled when checkbox is off
        noticeTypeComboBox = createComboBox(new String[]{"general", "new"});
        noticeTypeComboBox.setEnabled(false);
        addRow(contentPanel, gbc, row++,
                "\u0422\u0438\u043f \u0438\u0437\u0432\u0435\u0449\u0435\u043d\u0438\u044f",
                noticeTypeComboBox);

        createTempNoticeCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                noticeTypeComboBox.setEnabled(createTempNoticeCheckBox.isSelected());
            }
        });

        // Генерировать полный pdf
        fullDocumentCheckBox = createCheckBox(true);
        addRow(contentPanel, gbc, row++,
                "\u0413\u0435\u043d\u0435\u0440\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043f\u043e\u043b\u043d\u044b\u0439 pdf",
                fullDocumentCheckBox);

        // Только изменённые DM
        onlyChangedCheckBox = createCheckBox(false);
        addRow(contentPanel, gbc, row++,
                "\u0422\u043e\u043b\u044c\u043a\u043e \u0438\u0437\u043c\u0435\u043d\u0451\u043d\u043d\u044b\u0435 DM",
                onlyChangedCheckBox);

        // Создать отдельную папку
        createFolderCheckBox = createCheckBox(true);
        addRow(contentPanel, gbc, row++,
                "\u0421\u043e\u0437\u0434\u0430\u0442\u044c \u043e\u0442\u0434\u0435\u043b\u044c\u043d\u0443\u044e \u043f\u0430\u043f\u043a\u0443",
                createFolderCheckBox);

        // Проверить чексуммы
        checksumCheckBox = createCheckBox(true);
        addRow(contentPanel, gbc, row++,
                "\u041f\u0440\u043e\u0432\u0435\u0440\u0438\u0442\u044c \u0447\u0435\u043a\u0441\u0443\u043c\u043c\u044b",
                checksumCheckBox);

        // Архив
        zipCheckBox = createCheckBox(true);
        addRow(contentPanel, gbc, row++,
                "\u0410\u0440\u0445\u0438\u0432", zipCheckBox);

        // Сформировать архив с SVG
        svgCheckBox = createCheckBox(false);
        addRow(contentPanel, gbc, row++,
                "\u0421\u0444\u043e\u0440\u043c\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0430\u0440\u0445\u0438\u0432 \u0441 SVG",
                svgCheckBox);

        // Сформировать архив изображений
        svgImageCheckBox = createCheckBox(true);
        addRow(contentPanel, gbc, row++,
                "\u0421\u0444\u043e\u0440\u043c\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0430\u0440\u0445\u0438\u0432 \u0438\u0437\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u0439",
                svgImageCheckBox);

        // Сформировать пакет для RAMAN
        ramanCheckBox = createCheckBox(false);
        addRow(contentPanel, gbc, row++,
                "\u0421\u0444\u043e\u0440\u043c\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043f\u0430\u043a\u0435\u0442 \u0434\u043b\u044f RAMAN",
                ramanCheckBox);

        // Сформировать строку с полным именем документа
        docNumberCheckBox = createCheckBox(true);
        addRow(contentPanel, gbc, row++,
                "\u0421\u0444\u043e\u0440\u043c\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0441\u0442\u0440\u043e\u043a\u0443 \u0441 \u043f\u043e\u043b\u043d\u044b\u043c \u0438\u043c\u0435\u043d\u0435\u043c \u0434\u043e\u043a\u0443\u043c\u0435\u043d\u0442\u0430",
                docNumberCheckBox);

        // Проверка ошибок МД
        checkIssueCheckBox = createCheckBox(false);
        addRow(contentPanel, gbc, row++,
                "\u041f\u0440\u043e\u0432\u0435\u0440\u043a\u0430 \u043e\u0448\u0438\u0431\u043e\u043a \u041c\u0414",
                checkIssueCheckBox);

        // Сохранить dcl в dclPrevious
        copyDclCheckBox = createCheckBox(false);
        addRow(contentPanel, gbc, row++,
                "\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c dcl \u0432 dclPrevious",
                copyDclCheckBox);

        // Сохранить pm в pmPrevious
        pmPreviousGenerateCheckBox = createCheckBox(true);
        addRow(contentPanel, gbc, row++,
                "\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c pm \u0432 pmPrevious",
                pmPreviousGenerateCheckBox);

        // Управление применимостью
        checkApplicRFUCheckBox = createCheckBox(false);
        addRow(contentPanel, gbc, row++,
                "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043f\u0440\u0438\u043c\u0435\u043d\u0438\u043c\u043e\u0441\u0442\u044c\u044e",
                checkApplicRFUCheckBox);

        // Сформировать DDN — последний
        exportPackageCheckBox = createCheckBox(false);
        addRow(contentPanel, gbc, row++,
                "\u0421\u0444\u043e\u0440\u043c\u0438\u0440\u043e\u0432\u0430\u0442\u044c DDN",
                exportPackageCheckBox);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------
    // Settings persistence
    // ---------------------------------------------------------------

    public void applySettings(Properties props) {

        selectComboItem(formatComboBox,       props.getProperty("adv.cfg.format"));
        selectComboItem(checksumTypeComboBox, props.getProperty("adv.cfg.checksumType"));
        selectComboItem(noticeTypeComboBox,   props.getProperty("adv.cfg.noticeType"));

        applyFlag(fullDocumentCheckBox,       props.getProperty("adv.flag.fullDocument"));
        applyFlag(onlyChangedCheckBox,        props.getProperty("adv.flag.onlyChanged"));
        applyFlag(createFolderCheckBox,       props.getProperty("adv.flag.createFolder"));
        applyFlag(checksumCheckBox,           props.getProperty("adv.flag.checksum"));
        applyFlag(zipCheckBox,                props.getProperty("adv.flag.zip"));
        applyFlag(svgCheckBox,                props.getProperty("adv.flag.svg"));
        applyFlag(svgImageCheckBox,           props.getProperty("adv.flag.svgImage"));
        applyFlag(ramanCheckBox,              props.getProperty("adv.flag.raman"));
        applyFlag(docNumberCheckBox,          props.getProperty("adv.flag.docNumber"));
        applyFlag(checkIssueCheckBox,         props.getProperty("adv.flag.checkIssue"));
        applyFlag(createTempNoticeCheckBox,   props.getProperty("adv.flag.createTempNotice"));
        applyFlag(copyDclCheckBox,            props.getProperty("adv.flag.copyDcl"));
        applyFlag(pmPreviousGenerateCheckBox, props.getProperty("adv.flag.pmPreviousGenerate"));
        applyFlag(checkApplicRFUCheckBox,     props.getProperty("adv.flag.checkApplicRFU"));
        applyFlag(exportPackageCheckBox,      props.getProperty("adv.flag.exportPackage"));

        noticeTypeComboBox.setEnabled(createTempNoticeCheckBox.isSelected());
    }

    private void selectComboItem(JComboBox<String> combo, String value) {
        if (value == null || value.isEmpty()) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (value.equals(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void applyFlag(JCheckBox box, String value) {
        if (value == null) return;
        box.setSelected(Boolean.parseBoolean(value));
    }

    // ---------------------------------------------------------------
    // UI helpers
    // ---------------------------------------------------------------

    private JComboBox<String> createComboBox(String[] values) {
        JComboBox<String> combo = new JComboBox<>(values);
        combo.setFont(uiFont);
        return combo;
    }

    private JCheckBox createCheckBox(boolean selected) {
        JCheckBox box = new JCheckBox();
        box.setSelected(selected);
        box.setFont(uiFont);
        return box;
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

    // ---------------------------------------------------------------
    // Getters for command building
    // ---------------------------------------------------------------

    public Map<String, String> getConfigValues() {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put("format",       (String) formatComboBox.getSelectedItem());
        values.put("checksumType", (String) checksumTypeComboBox.getSelectedItem());
        if (createTempNoticeCheckBox.isSelected()) {
            values.put("noticeType", (String) noticeTypeComboBox.getSelectedItem());
        }
        return values;
    }

    public Map<String, Boolean> getFlagValues() {
        Map<String, Boolean> flags = new LinkedHashMap<String, Boolean>();
        flags.put("fullDocument",       fullDocumentCheckBox.isSelected());
        flags.put("onlyChanged",        onlyChangedCheckBox.isSelected());
        flags.put("createFolder",       createFolderCheckBox.isSelected());
        flags.put("checksum",           checksumCheckBox.isSelected());
        flags.put("zip",                zipCheckBox.isSelected());
        flags.put("svg",                svgCheckBox.isSelected());
        flags.put("svgImage",           svgImageCheckBox.isSelected());
        flags.put("raman",              ramanCheckBox.isSelected());
        flags.put("docNumber",          docNumberCheckBox.isSelected());
        flags.put("checkIssue",         checkIssueCheckBox.isSelected());
        flags.put("createTempNotice",   createTempNoticeCheckBox.isSelected());
        flags.put("copyDcl",            copyDclCheckBox.isSelected());
        flags.put("pmPreviousGenerate", pmPreviousGenerateCheckBox.isSelected());
        flags.put("checkApplicRFU",     checkApplicRFUCheckBox.isSelected());
        flags.put("exportPackage",      exportPackageCheckBox.isSelected());
        return flags;
    }
}