package app.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final JTabbedPane tabbedPane;

    private final LogsPanel logsPanel;
    private final SettingsPanel settingsPanel;
    private final LaunchPanel launchPanel;

    public MainFrame() {

        setTitle("Publisher");

        setSize(700, 500);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

    /*
        BIGGER TAB FONT
     */

        tabbedPane.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        logsPanel = new LogsPanel();

        settingsPanel = new SettingsPanel();

        launchPanel = new LaunchPanel(
                logsPanel,
                settingsPanel,
                tabbedPane
        );

        tabbedPane.addTab(
                "Launch",
                launchPanel
        );

        tabbedPane.addTab(
                "Settings",
                settingsPanel
        );

        tabbedPane.addTab(
                "Logs",
                logsPanel
        );

        setLayout(new BorderLayout());

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

}
