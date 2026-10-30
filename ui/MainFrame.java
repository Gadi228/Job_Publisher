package app.ui;

import app.service.SettingsService;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class MainFrame extends JFrame {

    public MainFrame() {

        setTitle("Publisher");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 18));

        // 1. Independent panels
        LogsPanel     logsPanel     = new LogsPanel();
        StartPanel    startPanel    = new StartPanel();
        AdvancedPanel advancedPanel = new AdvancedPanel();
        SignPanel     signPanel     = new SignPanel(logsPanel);

        // 2. SettingsPanel needs the above
        SettingsPanel settingsPanel = new SettingsPanel(
                logsPanel, advancedPanel, startPanel);

        // 3. Wire SignPanel into SettingsPanel for auto-sign after publish
        settingsPanel.setSignPanel(signPanel);

        // 4. Wire StartPanel <-> LogsPanel for progress tracking
        logsPanel.setStartPanel(startPanel);

        // 5. Apply saved settings
        Properties saved = SettingsService.load();
        advancedPanel.applySettings(saved);
        signPanel.applySettings(saved);

        // Tab order: Start | Settings | Advanced | Sign | Logs
        tabbedPane.addTab("Start",    startPanel);
        tabbedPane.addTab("Settings", settingsPanel);
        tabbedPane.addTab("Advanced", advancedPanel);
        tabbedPane.addTab("Sign",     signPanel);
        tabbedPane.addTab("Logs",     logsPanel);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }
}