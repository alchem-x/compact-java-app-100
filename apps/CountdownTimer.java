import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new CountdownTimer().setVisible(true));
}

static class CountdownTimer extends JFrame {
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
    private JSpinner hoursSpinner;
    private JSpinner minutesSpinner;
    private JSpinner secondsSpinner;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JTextField messageField;
    private JList<String> presetList;
    private DefaultListModel<String> presetModel;

    private Timer timer;
    private long totalSeconds;
    private long remainingSeconds;
    private boolean isRunning = false;
    private final DecimalFormat timeFormat = new DecimalFormat("00");

    public CountdownTimer() {
        this.initializeGUI();
        this.setupPresets();
        this.setupKeyboardShortcuts();
    }

    private void initializeGUI() {
        setTitle("⏰ Countdown Timer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板 - 使用苹果风格
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel("⏰ Countdown Timer", SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_24, 0));

        // 时间显示面板 - 使用苹果风格卡片
        var timePanel = this.createTimePanel();

        // 设置面板 - 使用苹果风格组件
        var settingPanel = this.createSettingPanel();

        // 控制按钮面板
        var controlPanel = this.createControlPanel();

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(timePanel, BorderLayout.CENTER);
        mainPanel.add(settingPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // 预设面板 - 使用苹果风格
        var presetPanel = this.createPresetPanel();
        add(presetPanel, BorderLayout.EAST);

        // 创建定时器
        timer = new Timer(1000, (ev) -> this.updateTimer());

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

        // 时间显示 - 大字体
        timeLabel = new JLabel("00:00:00");
        timeLabel.setFont(new Font("SF Pro Display", Font.BOLD, 56));
        timeLabel.setForeground(ORANGE);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setHorizontalAlignment(JLabel.CENTER);

        // 状态标签
        statusLabel = new JLabel("Set Countdown");
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

    private JPanel createSettingPanel() {
        var settingPanel = new JPanel(new GridBagLayout());
        settingPanel.setBackground(SYSTEM_BACKGROUND);
        settingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder("Time Settings")),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_8, SPACING_8, SPACING_8, SPACING_8);

        // Time input
        gbc.gridx = 0; gbc.gridy = 0;
        var hoursLabel = new JLabel("Hours:");
        hoursLabel.setFont(HEADLINE);
        hoursLabel.setForeground(LABEL);
        settingPanel.add(hoursLabel, gbc);

        hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        hoursSpinner.setFont(BODY);
        hoursSpinner.setPreferredSize(new Dimension(60, 30));
        gbc.gridx = 1;
        settingPanel.add(hoursSpinner, gbc);

        gbc.gridx = 2;
        var minutesLabel = new JLabel("Minutes:");
        minutesLabel.setFont(HEADLINE);
        minutesLabel.setForeground(LABEL);
        settingPanel.add(minutesLabel, gbc);

        minutesSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 59, 1));
        minutesSpinner.setFont(BODY);
        minutesSpinner.setPreferredSize(new Dimension(60, 30));
        gbc.gridx = 3;
        settingPanel.add(minutesSpinner, gbc);

        gbc.gridx = 4;
        var secondsLabel = new JLabel("Seconds:");
        secondsLabel.setFont(HEADLINE);
        secondsLabel.setForeground(LABEL);
        settingPanel.add(secondsLabel, gbc);

        secondsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        secondsSpinner.setFont(BODY);
        secondsSpinner.setPreferredSize(new Dimension(60, 30));
        gbc.gridx = 5;
        settingPanel.add(secondsSpinner, gbc);

        // Reminder message
        gbc.gridx = 0; gbc.gridy = 1;
        var messageLabel = new JLabel("Message:");
        messageLabel.setFont(HEADLINE);
        messageLabel.setForeground(LABEL);
        settingPanel.add(messageLabel, gbc);

        messageField = new JTextField("Time's up!", 20);
        messageField.setFont(BODY);
        messageField.setBackground(GRAY6);
        messageField.setForeground(LABEL);
        messageField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        gbc.gridx = 1; gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingPanel.add(messageField, gbc);

        return settingPanel;
    }

    private JPanel createControlPanel() {
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_12, 0));
        controlPanel.setBackground(SYSTEM_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_16, 0, 0, 0));

        startButton = this.createSuccessButton("▶️ Start");
        startButton.setPreferredSize(BUTTON_REGULAR);
        startButton.addActionListener((ev) -> this.startTimer());

        pauseButton = this.createWarningButton("⏸️ Pause");
        pauseButton.setPreferredSize(BUTTON_REGULAR);
        pauseButton.addActionListener((ev) -> this.pauseTimer());
        pauseButton.setEnabled(false);

        resetButton = this.createSecondaryButton("🔄 Reset");
        resetButton.setPreferredSize(BUTTON_REGULAR);
        resetButton.addActionListener((ev) -> this.resetTimer());

        var addPresetButton = this.createSecondaryButton("💾 Save Preset");
        addPresetButton.addActionListener((ev) -> this.addPreset());

        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resetButton);
        controlPanel.add(addPresetButton);

        return controlPanel;
    }

    private JPanel createPresetPanel() {
        var presetPanel = new JPanel(new BorderLayout());
        presetPanel.setBackground(SYSTEM_BACKGROUND);
        presetPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder("Preset Timers")),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        presetPanel.setPreferredSize(new Dimension(250, 0));

        presetModel = new DefaultListModel<>();
        presetList = new JList<>(presetModel);
        presetList.setFont(CALLOUT);
        presetList.setBackground(GRAY6);
        presetList.setForeground(LABEL);
        presetList.setSelectionBackground(new Color(0, 122, 255, 30));
        presetList.setSelectionForeground(BLUE);
        presetList.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
        ));
        presetList.addListSelectionListener((e) -> {
            if (!e.getValueIsAdjusting()) {
                loadPreset();
            }
        });

        var presetScrollPane = new JScrollPane(presetList);
        presetScrollPane.setPreferredSize(new Dimension(0, 200));
        presetScrollPane.setBorder(BorderFactory.createEmptyBorder());
        presetScrollPane.getViewport().setBackground(GRAY6);

        var presetButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_8, 0));
        presetButtonPanel.setBackground(SYSTEM_BACKGROUND);

        var deletePresetButton = this.createSecondaryButton("Delete");
        deletePresetButton.addActionListener((ev) -> this.deletePreset());

        presetButtonPanel.add(deletePresetButton);

        presetPanel.add(presetScrollPane, BorderLayout.CENTER);
        presetPanel.add(presetButtonPanel, BorderLayout.SOUTH);

        return presetPanel;
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
    private void setupPresets() {
        // 添加一些默认预设
        presetModel.addElement("Pomodoro - 25:00");
        presetModel.addElement("Short Break - 05:00");
        presetModel.addElement("Long Break - 15:00");
        presetModel.addElement("Boil Egg - 03:00");
        presetModel.addElement("Brew Tea - 02:00");
    }

    private void startTimer() {
        if (!isRunning) {
            if (remainingSeconds == 0) {
                // 设置新的倒计时
                int hours = (Integer) hoursSpinner.getValue();
                int minutes = (Integer) minutesSpinner.getValue();
                int seconds = (Integer) secondsSpinner.getValue();

                totalSeconds = hours * 3600 + minutes * 60 + seconds;
                remainingSeconds = totalSeconds;

                if (totalSeconds == 0) {
                    JOptionPane.showMessageDialog(this, "Please set a countdown time!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            isRunning = true;
            timer.start();

            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            resetButton.setEnabled(false);
        }
    }

    private void pauseTimer() {
        if (isRunning) {
            isRunning = false;
            timer.stop();

            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            resetButton.setEnabled(true);
        }
    }

    private void resetTimer() {
        isRunning = false;
        remainingSeconds = 0;
        if (timer != null) {
            timer.stop();
        }

        updateTimeDisplay();

        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(true);
    }

    private void updateTimer() {
        if (isRunning && remainingSeconds > 0) {
            remainingSeconds--;
            updateTimeDisplay();

            if (remainingSeconds == 0) {
                // 倒计时结束
                isRunning = false;
                timer.stop();

                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                resetButton.setEnabled(true);

                showTimeUpNotification();
            }
        }
    }

    private void updateTimeDisplay() {
        long hours = remainingSeconds / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;

        String timeText = String.format("%s:%s:%s",
            timeFormat.format(hours),
            timeFormat.format(minutes),
            timeFormat.format(seconds));

        timeLabel.setText(timeText);

        // 根据剩余时间改变颜色
        if (remainingSeconds <= 10 && remainingSeconds > 0) {
            timeLabel.setForeground(RED);
        } else if (remainingSeconds <= 60) {
            timeLabel.setForeground(ORANGE);
        } else {
            timeLabel.setForeground(ORANGE);
        }
    }

    private void showTimeUpNotification() {
        // 播放系统提示音
        Toolkit.getDefaultToolkit().beep();

        // 显示通知对话框
        String message = messageField.getText();
        if (message.trim().isEmpty()) {
            message = "Time's up!";
        }

        var notification = new JDialog(this, "⏰ Countdown Complete", true);
        notification.setLayout(new BorderLayout());

        var messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setFont(TITLE3);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_16, SPACING_24));

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_12, 0));

        var okButton = this.createPrimaryButton("OK");
        okButton.addActionListener((ev) -> notification.dispose());

        var restartButton = this.createSecondaryButton("Restart");
        restartButton.addActionListener((ev) -> {
            notification.dispose();
            this.startTimer();
        });

        buttonPanel.add(okButton);
        buttonPanel.add(restartButton);

        notification.add(messageLabel, BorderLayout.CENTER);
        notification.add(buttonPanel, BorderLayout.SOUTH);

        notification.setSize(350, 200);
        notification.setLocationRelativeTo(this);
        notification.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        notification.setVisible(true);
    }

    private void addPreset() {
        int hours = (Integer) hoursSpinner.getValue();
        int minutes = (Integer) minutesSpinner.getValue();
        int seconds = (Integer) secondsSpinner.getValue();

        if (hours == 0 && minutes == 0 && seconds == 0) {
            JOptionPane.showMessageDialog(this, "Please set a time first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Enter preset name:");
        if (name != null && !name.trim().isEmpty()) {
            String preset = String.format("%s - %02d:%02d:%02d",
                name.trim(), hours, minutes, seconds);
            presetModel.addElement(preset);
        }
    }

    private void loadPreset() {
        String selected = presetList.getSelectedValue();
        if (selected != null) {
            // 解析预设时间
            String[] parts = selected.split(" - ");
            if (parts.length == 2) {
                String[] timeParts = parts[1].split(":");
                if (timeParts.length == 3) {
                    try {
                        int hours = Integer.parseInt(timeParts[0]);
                        int minutes = Integer.parseInt(timeParts[1]);
                        int seconds = Integer.parseInt(timeParts[2]);

                        hoursSpinner.setValue(hours);
                        minutesSpinner.setValue(minutes);
                        secondsSpinner.setValue(seconds);

                        // 设置消息
                        messageField.setText(parts[0] + " time's up!");
                    } catch (NumberFormatException ex) {
                        // 忽略解析错误
                    }
                }
            }
        }
    }

    private void deletePreset() {
        int selectedIndex = presetList.getSelectedIndex();
        if (selectedIndex != -1) {
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this preset?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                presetModel.removeElementAt(selectedIndex);
            }
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

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_SPACE:
                        // 空格键开始/暂停
                        if (isRunning) {
                            pauseTimer();
                        } else {
                            startTimer();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_R:
                        // R键重置
                        resetTimer();
                        break;
                    case java.awt.event.KeyEvent.VK_ENTER:
                        // 回车键开始（如果未运行）
                        if (!isRunning) {
                            startTimer();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_ESCAPE:
                        // ESC键停止并重置
                        if (isRunning) {
                            pauseTimer();
                        }
                        resetTimer();
                        break;
                    case java.awt.event.KeyEvent.VK_UP:
                        // 上箭头增加小时
                        if (!isRunning) {
                            int current = (Integer) hoursSpinner.getValue();
                            if (current < 99) {
                                hoursSpinner.setValue(current + 1);
                            }
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        // 下箭头减少小时
                        if (!isRunning) {
                            int current = (Integer) hoursSpinner.getValue();
                            if (current > 0) {
                                hoursSpinner.setValue(current - 1);
                            }
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_LEFT:
                        // 左箭头减少分钟
                        if (!isRunning) {
                            int current = (Integer) minutesSpinner.getValue();
                            if (current > 0) {
                                minutesSpinner.setValue(current - 1);
                            }
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        // 右箭头增加分钟
                        if (!isRunning) {
                            int current = (Integer) minutesSpinner.getValue();
                            if (current < 59) {
                                minutesSpinner.setValue(current + 1);
                            }
                        }
                        break;
                    default:
                        return;
                }
            }
        });

        // 确保窗口可以获得焦点
        this.setFocusable(true);
        this.requestFocusInWindow();
    }
}