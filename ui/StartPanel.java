package app.ui;

import app.model.ProgressStep;
import app.service.ProgressConfigService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StartPanel extends JPanel {

    private static final String LABEL_IDLE = "Готов к запуску";
    private static final String LABEL_INIT = "Настройка публикатора";
    private static final String LABEL_DONE = "Успешно выполнено";
    private static final String LABEL_ERROR = "ОШИБКА ПУБЛИКАЦИИ";

    private final JButton runButton;
    private final SmoothBar progressBar;
    private final JLabel statusLabel;
    private final JButton openPdfButton;
    private final JButton stopButton;

    private List<ProgressStep> steps;
    private int currentStepIndex = 0;
    private int totalStages;

    private Runnable onRun;

    public StartPanel() {

        steps = ProgressConfigService.loadSteps();
        totalStages = steps.size() + 2;

        setLayout(new BorderLayout());

        // ── CENTER: run button ──────────────────────────────────────
        runButton = new JButton("\u0417\u0410\u041f\u0423\u0421\u041a");
        runButton.setFont(new Font("Arial", Font.BOLD, 28));
        runButton.setPreferredSize(new Dimension(260, 80));
        runButton.setBackground(new Color(34, 139, 34));
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);
        runButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onRun != null) onRun.run();
            }
        });

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(runButton);
        add(centerPanel, BorderLayout.CENTER);

        // ── SOUTH: bar + label + buttons ───────────────────────────
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new EmptyBorder(10, 24, 12, 24));

        progressBar = new SmoothBar();
        progressBar.setPreferredSize(new Dimension(0, 34));

        statusLabel = new JLabel(LABEL_IDLE, SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(new EmptyBorder(6, 0, 8, 0));

        JPanel barPanel = new JPanel(new BorderLayout());
        barPanel.add(progressBar, BorderLayout.CENTER);
        barPanel.add(statusLabel, BorderLayout.SOUTH);

        openPdfButton = new JButton(
                "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0439 PDF \u0444\u0430\u0439\u043b");
        openPdfButton.setFont(new Font("Arial", Font.BOLD, 14));
        openPdfButton.setEnabled(false);

        stopButton = new JButton("Stop Script");
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setEnabled(false);
        stopButton.setBackground(new Color(180, 30, 30));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.service.CommandService.stopCurrentProcess();
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.add(openPdfButton);
        btnRow.add(stopButton);

        southPanel.add(barPanel, BorderLayout.CENTER);
        southPanel.add(btnRow, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    // ── Public API ─────────────────────────────────────────────────

    public void setOnRun(Runnable r) {
        this.onRun = r;
    }

    public void wireOpenPdfButton(LogsPanel logsPanel) {
        openPdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logsPanel.triggerOpenPdf();
            }
        });
    }

    public void onLogLine(final String line) {

        if (line == null) return;

        if (line.contains("===> Error:")) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    statusLabel.setText(LABEL_ERROR);
                    statusLabel.setForeground(new Color(210, 30, 30));
                }
            });
            return;
        }

        while (currentStepIndex < steps.size()) {
            ProgressStep step = steps.get(currentStepIndex);
            if (line.contains(step.getTrigger())) {
                currentStepIndex++;
                final String lbl = step.getLabel();
                final int percent = (currentStepIndex * 100) / totalStages;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        statusLabel.setForeground(UIManager.getColor("Label.foreground"));
                        statusLabel.setText(lbl);
                        progressBar.setTarget(percent);
                    }
                });
                break;
            } else {
                break;
            }
        }
    }

    public void onProcessStarted() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                steps = ProgressConfigService.loadSteps();
                totalStages = steps.size() + 2;
                currentStepIndex = 0;

                progressBar.reset();
                progressBar.setTarget(Math.max(1, 100 / (totalStages * 2)));

                statusLabel.setForeground(UIManager.getColor("Label.foreground"));
                statusLabel.setText(LABEL_INIT);

                openPdfButton.setEnabled(false);
                stopButton.setEnabled(true);
                stopButton.setBackground(new Color(180, 30, 30));
                runButton.setEnabled(false);
            }
        });
    }

    public void onProcessFinished(boolean success) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (success && !LABEL_ERROR.equals(statusLabel.getText())) {
                    progressBar.setTarget(100);
                    statusLabel.setForeground(new Color(20, 150, 20));
                    statusLabel.setText(LABEL_DONE);
                    openPdfButton.setEnabled(true);
                }
                stopButton.setEnabled(false);
                stopButton.setBackground(UIManager.getColor("Button.background"));
                runButton.setEnabled(true);
            }
        });
    }

    public void enablePdfButton() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                openPdfButton.setEnabled(true);
            }
        });
    }

    public void setProcessRunning(final boolean running) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                stopButton.setEnabled(running);
                runButton.setEnabled(!running);
                stopButton.setBackground(running
                        ? new Color(180, 30, 30)
                        : UIManager.getColor("Button.background"));
            }
        });
    }

    // ── SmoothBar ──────────────────────────────────────────────────
    // Single rounded-rectangle bar that fills from left to right.
    // Matches the style in the screenshot: green pill on dark track.
    // ──────────────────────────────────────────────────────────────
    private static final class SmoothBar extends JComponent {

        private static final Color TRACK_BG = new Color(220, 220, 220);
        private static final Color TRACK_BORDER = new Color(180, 180, 180);
        private static final Color FILL_COLOR = new Color(34, 139, 34);   // flat dark green

        private int current = 0;
        private int target = 0;

        private final Timer ticker;

        SmoothBar() {
            setOpaque(false);
            ticker = new Timer(14, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (current < target) {
                        int delta = Math.max(1, (target - current) / 5);
                        current = Math.min(current + delta, target);
                        repaint();
                    } else {
                        ticker.stop();
                    }
                }
            });
        }

        void setTarget(int pct) {
            target = Math.min(100, Math.max(0, pct));
            if (!ticker.isRunning()) ticker.start();
        }

        void reset() {
            ticker.stop();
            current = 0;
            target = 0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            int w = getWidth();
            int h = getHeight();
            int arc = h;   // full pill

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Track
            g2.setColor(TRACK_BG);
            g2.fillRoundRect(0, 0, w, h, arc, arc);
            g2.setColor(TRACK_BORDER);
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

            // Fill — flat single colour
            if (current > 0) {
                Shape clip = new java.awt.geom.RoundRectangle2D.Float(
                        0, 0, w, h, arc, arc);
                g2.setClip(clip);

                int fillW = (int) ((long) w * current / 100);
                fillW = Math.max(fillW, h);

                g2.setColor(FILL_COLOR);
                g2.fillRoundRect(0, 0, fillW, h, arc, arc);

                g2.setClip(null);
            }

            g2.dispose();
        }
    }
}