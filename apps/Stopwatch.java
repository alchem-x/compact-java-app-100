import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Stopwatch().setVisible(true));
}

static class Stopwatch extends JFrame {
    // ===== 设计系统常量 =====
    // 主要颜色
    private static final Color BLUE = new Color(0, 122, 255);
    private static final Color GREEN = new Color(52, 199, 89);
    private static final Color RED = new Color(255, 59, 48);
    private static final Color ORANGE = new Color(255, 149, 0);
    private static final Color PURPLE = new Color(175, 82, 222);
    private static final Color TEAL = new Color(48, 176, 199);

    // 中性色
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color GRAY = new Color(142, 142, 147);
    private static final Color GRAY2 = new Color(174, 174, 178);
    private static final Color GRAY3 = new Color(199, 199, 204);
    private static final Color GRAY4 = new Color(209, 209, 214);
    private static final Color GRAY5 = new Color(229, 229, 234);
    private static final Color GRAY6 = new Color(242, 242, 247);

    // 语义颜色
    private static final Color LABEL = new Color(0, 0, 0);
    private static final Color SECONDARY_LABEL = new Color(60, 60, 67, 153);
    private static final Color TERTIARY_LABEL = new Color(60, 60, 67, 76);

    // 背景
    private static final Color SYSTEM_BACKGROUND = new Color(255, 255, 255);
    private static final Color SECONDARY_SYSTEM_BACKGROUND = new Color(242, 242, 247);

    // 字体
    private static final Font LARGE_TITLE = new Font("SF Pro Display", Font.BOLD, 34);
    private static final Font TITLE1 = new Font("SF Pro Display", Font.BOLD, 28);
    private static final Font TITLE2 = new Font("SF Pro Display", Font.BOLD, 22);
    private static final Font TITLE3 = new Font("SF Pro Display", Font.BOLD, 20);
    private static final Font HEADLINE = new Font("SF Pro Display", Font.BOLD, 17);
    private static final Font SUBHEADLINE = new Font("SF Pro Display", Font.PLAIN, 15);
    private static final Font BODY = new Font("SF Pro Text", Font.PLAIN, 17);
    private static final Font CALLOUT = new Font("SF Pro Text", Font.PLAIN, 16);
    private static final Font FOOTNOTE = new Font("SF Pro Text", Font.PLAIN, 13);
    private static final Font CAPTION1 = new Font("SF Pro Text", Font.PLAIN, 12);
    private static final Font CAPTION2 = new Font("SF Pro Text", Font.PLAIN, 11);

    // 间距
    private static final int SPACING_2 = 2;
    private static final int SPACING_4 = 4;
    private static final int SPACING_8 = 8;
    private static final int SPACING_12 = 12;
    private static final int SPACING_16 = 16;
    private static final int SPACING_20 = 20;
    private static final int SPACING_24 = 24;
    private static final int SPACING_32 = 32;

    // 圆角
    private static final int RADIUS_4 = 4;
    private static final int RADIUS_8 = 8;
    private static final int RADIUS_12 = 12;
    private static final int RADIUS_16 = 16;
    private static final int RADIUS_20 = 20;

    // 按钮尺寸
    private static final Dimension BUTTON_REGULAR = new Dimension(120, 44);
    private static final Dimension BUTTON_LARGE = new Dimension(160, 50);

    // ===== 应用状态 =====
    private JLabel timeLabel;
    private JLabel statusLabel;
    private JButton startButton;
    private JButton stopButton;
    private JButton resetButton;
    private JButton lapButton;
    private JList<String> lapList;
    private DefaultListModel<String> lapListModel;

    private Timer timer;
    private long startTime;
    private long elapsedTime;
    private boolean isRunning = false;
    private List<Long> lapTimes;
    private final DecimalFormat timeFormat = new DecimalFormat("00");

    public Stopwatch() {
        lapTimes = new ArrayList<>();
        this.initializeGUI();
        this.resetStopwatch();
    }

    private void initializeGUI() {
        setTitle("⏱️ 秒表");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板 - 使用苹果风格
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel("⏱️ 秒表", SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_24, 0));

        // 时间显示面板 - 使用苹果风格卡片
        var timePanel = this.createTimePanel();

        // 控制按钮面板 - 使用苹果风格组件
        var controlPanel = this.createControlPanel();

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(timePanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // 分段时间面板 - 使用苹果风格
        var lapPanel = this.createLapPanel();
        add(lapPanel, BorderLayout.EAST);

        // 创建定时器
        timer = new Timer(10, this::updateDisplay);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private JPanel createTimePanel() {
        var timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        timePanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_16),
            BorderFactory.createEmptyBorder(SPACING_32, SPACING_32, SPACING_32, SPACING_32)
        ));

        // 主时间显示 - 大字体
        timeLabel = new JLabel("00:00:00.000");
        timeLabel.setFont(new Font("SF Pro Display", Font.BOLD, 56));
        timeLabel.setForeground(BLUE);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setHorizontalAlignment(JLabel.CENTER);

        // 状态标签
        statusLabel = new JLabel("准备就绪");
        statusLabel.setFont(SUBHEADLINE);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        timePanel.add(Box.createVerticalGlue());
        timePanel.add(timeLabel);
        timePanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        timePanel.add(statusLabel);
        timePanel.add(Box.createVerticalGlue());

        return timePanel;
    }

    private JPanel createControlPanel() {
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, 0));
        controlPanel.setBackground(SYSTEM_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, 0, 0, 0));

        startButton = this.createSuccessButton("▶️ 开始");
        startButton.setPreferredSize(BUTTON_REGULAR);
        startButton.addActionListener(this::startStopwatch);

        stopButton = this.createWarningButton("⏸️ 停止");
        stopButton.setPreferredSize(BUTTON_REGULAR);
        stopButton.addActionListener(this::stopStopwatch);
        stopButton.setEnabled(false);

        resetButton = this.createSecondaryButton("🔄 重置");
        resetButton.setPreferredSize(BUTTON_REGULAR);
        resetButton.addActionListener(this::resetStopwatch);

        lapButton = this.createPrimaryButton("🏁 分段");
        lapButton.setPreferredSize(BUTTON_REGULAR);
        lapButton.addActionListener(this::recordLap);
        lapButton.setEnabled(false);

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(resetButton);
        controlPanel.add(lapButton);

        return controlPanel;
    }

    private JPanel createLapPanel() {
        var lapPanel = new JPanel(new BorderLayout());
        lapPanel.setBackground(SYSTEM_BACKGROUND);
        lapPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder("分段记录")),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        lapPanel.setPreferredSize(new Dimension(300, 0));

        // 分段列表
        lapListModel = new DefaultListModel<>();
        lapList = new JList<>(lapListModel);
        lapList.setFont(new Font("SF Mono", Font.PLAIN, 14));
        lapList.setBackground(GRAY6);
        lapList.setForeground(LABEL);
        lapList.setSelectionBackground(new Color(0, 122, 255, 30));
        lapList.setSelectionForeground(BLUE);
        lapList.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
        ));

        var lapScrollPane = new JScrollPane(lapList);
        lapScrollPane.setPreferredSize(new Dimension(0, 200));
        lapScrollPane.setBorder(BorderFactory.createEmptyBorder());
        lapScrollPane.getViewport().setBackground(GRAY6);

        // 分段统计面板
        var lapStatsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_8, 0));
        lapStatsPanel.setBackground(SYSTEM_BACKGROUND);

        var clearLapsButton = this.createSecondaryButton("清空记录");
        clearLapsButton.addActionListener(this::clearLaps);

        var exportButton = this.createSecondaryButton("导出记录");
        exportButton.addActionListener(this::exportLaps);

        lapStatsPanel.add(clearLapsButton);
        lapStatsPanel.add(exportButton);

        lapPanel.add(lapScrollPane, BorderLayout.CENTER);
        lapPanel.add(lapStatsPanel, BorderLayout.SOUTH);

        return lapPanel;
    }

    // ===== 按钮创建方法 =====
    private JButton createPrimaryButton(String text) {
        return this.createStyledButton(text, BLUE, WHITE);
    }

    private JButton createSecondaryButton(String text) {
        return this.createStyledButton(text, GRAY6, LABEL);
    }

    private JButton createSuccessButton(String text) {
        return this.createStyledButton(text, GREEN, WHITE);
    }

    private JButton createWarningButton(String text) {
        return this.createStyledButton(text, ORANGE, WHITE);
    }

    private JButton createDangerButton(String text) {
        return this.createStyledButton(text, RED, WHITE);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        var button = new JButton(text);
        button.setFont(HEADLINE);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        button.setPreferredSize(BUTTON_REGULAR);

        // 设置悬停效果
        this.setupButtonHoverEffect(button, backgroundColor);

        return button;
    }

    private void setupButtonHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent ev) {
                if (button.isEnabled()) {
                    button.setBackground(darkenColor(originalColor, 0.1f));
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent ev) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor);
                }
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private Color darkenColor(Color color, float factor) {
        return new Color(
            Math.max(0, (int) (color.getRed() * (1 - factor))),
            Math.max(0, (int) (color.getGreen() * (1 - factor))),
            Math.max(0, (int) (color.getBlue() * (1 - factor))),
            color.getAlpha()
        );
    }

    private javax.swing.border.Border createTitledBorder(String title) {
        var border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(HEADLINE);
        border.setTitleColor(SECONDARY_LABEL);
        return border;
    }

    // ===== 业务逻辑方法 =====
    private void startStopwatch(ActionEvent e) {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            isRunning = true;
            timer.start();

            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            lapButton.setEnabled(true);
            resetButton.setEnabled(false);
        }
    }

    private void stopStopwatch(ActionEvent e) {
        if (isRunning) {
            isRunning = false;
            timer.stop();

            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            lapButton.setEnabled(false);
            resetButton.setEnabled(true);
        }
    }

    private void resetStopwatch(ActionEvent e) {
        this.resetStopwatch();
    }

    private void resetStopwatch() {
        isRunning = false;
        elapsedTime = 0;
        if (timer != null) {
            timer.stop();
        }

        updateTimeDisplay(0);

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        lapButton.setEnabled(false);
        resetButton.setEnabled(true);

        lapTimes.clear();
        lapListModel.clear();
    }

    private void recordLap(ActionEvent e) {
        if (isRunning) {
            long currentTime = System.currentTimeMillis() - startTime;
            lapTimes.add(currentTime);

            String lapTimeStr = formatTime(currentTime);
            String lapDiff = "";

            if (lapTimes.size() > 1) {
                long prevLap = lapTimes.get(lapTimes.size() - 2);
                long diff = currentTime - prevLap;
                lapDiff = " (+" + formatTime(diff) + ")";
            }

            String lapEntry = String.format("第 %d 段: %s%s",
                lapTimes.size(), lapTimeStr, lapDiff);
            lapListModel.addElement(lapEntry);

            // Auto-scroll to latest record
            lapList.ensureIndexIsVisible(lapListModel.getSize() - 1);
        }
    }

    private void clearLaps(ActionEvent e) {
        lapTimes.clear();
        lapListModel.clear();
    }

    private void exportLaps(ActionEvent e) {
        if (lapTimes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可导出的分段记录！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        var fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("秒表分段记录.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                var sb = new StringBuilder();
                sb.append("秒表分段记录\n");
                sb.append("================\n");
                sb.append("总时间: ").append(formatTime(elapsedTime)).append("\n");
                sb.append("分段数量: ").append(lapTimes.size()).append("\n\n");

                for (int i = 0; i < lapTimes.size(); i++) {
                    sb.append(String.format("第 %d 段: %s\n", i + 1, formatTime(lapTimes.get(i))));
                }

                // Statistics
                if (lapTimes.size() > 1) {
                    sb.append("\n统计信息:\n");
                    long fastest = Long.MAX_VALUE;
                    long slowest = 0;
                    long totalLapTime = 0;

                    for (int i = 1; i < lapTimes.size(); i++) {
                        long lapTime = lapTimes.get(i) - lapTimes.get(i - 1);
                        totalLapTime += lapTime;
                        fastest = Math.min(fastest, lapTime);
                        slowest = Math.max(slowest, lapTime);
                    }

                    long avgLapTime = totalLapTime / (lapTimes.size() - 1);
                    sb.append("最快分段: ").append(formatTime(fastest)).append("\n");
                    sb.append("最慢分段: ").append(formatTime(slowest)).append("\n");
                    sb.append("平均分段: ").append(formatTime(avgLapTime)).append("\n");
                }

                Files.write(file.toPath(), sb.toString().getBytes());
                JOptionPane.showMessageDialog(this, "分段记录已成功导出到文件！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDisplay(ActionEvent e) {
        if (isRunning) {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        updateTimeDisplay(elapsedTime);

        // Update status
        if (isRunning) {
            statusLabel.setText("运行中...");
            statusLabel.setForeground(GREEN);
        } else if (elapsedTime > 0) {
            statusLabel.setText("已停止");
            statusLabel.setForeground(ORANGE);
        } else {
            statusLabel.setText("准备就绪");
            statusLabel.setForeground(SECONDARY_LABEL);
        }
    }

    private void updateTimeDisplay(long timeMs) {
        timeLabel.setText(formatTime(timeMs));
    }

    private String formatTime(long timeMs) {
        long hours = timeMs / 3600000;
        long minutes = (timeMs % 3600000) / 60000;
        long seconds = (timeMs % 60000) / 1000;
        long milliseconds = timeMs % 1000;

        if (hours > 0) {
            return String.format("%s:%s:%s.%03d",
                timeFormat.format(hours),
                timeFormat.format(minutes),
                timeFormat.format(seconds),
                milliseconds);
        } else {
            return String.format("%s:%s.%03d",
                timeFormat.format(minutes),
                timeFormat.format(seconds),
                milliseconds);
        }
    }

    /**
     * 圆角边框类
     */
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;
        private final int thickness;

        public RoundedBorder(int radius) {
            this(radius, GRAY3, 1);
        }

        public RoundedBorder(int radius, Color borderColor, int thickness) {
            this.radius = radius;
            this.borderColor = borderColor;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            var g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width - thickness, height - thickness, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }
}