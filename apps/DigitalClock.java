import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new DigitalClock().setVisible(true));
}

static class DigitalClock extends JFrame {
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

    // ===== 文本管理静态内部类 =====
    private static class Texts {
        // 窗口标题
        static final String WINDOW_TITLE = "🕐 数字时钟";

        // 主界面文本
        static final String MAIN_TITLE = "🕐 数字时钟";
        static final String TIMEZONE_LABEL = "时区:";
        static final String FORMAT_12_HOUR = "12小时制";
        static final String FORMAT_24_HOUR = "24小时制";
        static final String ALARM_BUTTON = "⏰ 设置闹钟";
        static final String FULLSCREEN_BUTTON = "🔍 全屏";

        // 闹钟对话框文本
        static final String ALARM_DIALOG_TITLE = "⏰ 设置闹钟";
        static final String HOUR_LABEL = "小时:";
        static final String MINUTE_LABEL = "分钟:";
        static final String MESSAGE_LABEL = "消息:";
        static final String DEFAULT_ALARM_MESSAGE = "该起床了!";
        static final String SET_BUTTON = "设置";
        static final String CANCEL_BUTTON = "取消";

        // 闹钟通知文本
        static final String ALARM_SET_SUCCESS = "闹钟设置成功";
        static final String ALARM_NOTIFICATION_TITLE = "⏰ 闹钟提醒";
        static final String STOP_ALARM_BUTTON = "停止闹钟";

        // 状态消息
        static final String ALARM_SET_MESSAGE = "闹钟设置时间: %02d:%02d\n消息: %s";

        // 错误消息
        static final String PLEASE_SET_TIME = "请先设置倒计时时间!";
        static final String WARNING_TITLE = "警告";
    }

    // ===== 应用状态 =====
    private JLabel timeLabel;
    private JLabel dateLabel;
    private JComboBox<String> timezoneCombo;
    private Timer timer;
    private Map<String, ZoneId> timezones;
    private boolean is24Hour = true;
    private Timer alarmTimer;
    private boolean isFullscreen = false;

    public DigitalClock() {
        this.initializeTimezones();
        this.initializeGUI();
        this.startClock();
        this.setupKeyboardShortcuts();
    }

    private void initializeTimezones() {
        var timezoneMap = new HashMap<String, ZoneId>();
        timezoneMap.put("Beijing", ZoneId.of("Asia/Shanghai"));
        timezoneMap.put("New York", ZoneId.of("America/New_York"));
        timezoneMap.put("London", ZoneId.of("Europe/London"));
        timezoneMap.put("Tokyo", ZoneId.of("Asia/Tokyo"));
        timezoneMap.put("Sydney", ZoneId.of("Australia/Sydney"));
        timezoneMap.put("Los Angeles", ZoneId.of("America/Los_Angeles"));
        timezoneMap.put("Paris", ZoneId.of("Europe/Paris"));
        timezoneMap.put("Moscow", ZoneId.of("Europe/Moscow"));
        this.timezones = timezoneMap;
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板 - 使用苹果风格
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_32, SPACING_32, SPACING_32, SPACING_32));

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel(Texts.MAIN_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_24, 0));

        // 时间显示面板 - 使用苹果风格卡片
        var timePanel = this.createTimePanel();

        // 控制面板 - 使用苹果风格组件
        var controlPanel = this.createControlPanel();

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(timePanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        setSize(700, 400);
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

        // 时间标签 - 大字体显示
        timeLabel = new JLabel("00:00:00");
        timeLabel.setFont(new Font("SF Pro Display", Font.BOLD, 64));
        timeLabel.setForeground(BLUE);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 日期标签
        dateLabel = new JLabel("Monday, January 1, 2024");
        dateLabel.setFont(TITLE3);
        dateLabel.setForeground(SECONDARY_LABEL);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timePanel.add(Box.createVerticalGlue());
        timePanel.add(timeLabel);
        timePanel.add(Box.createRigidArea(new Dimension(0, SPACING_16)));
        timePanel.add(dateLabel);
        timePanel.add(Box.createVerticalGlue());

        return timePanel;
    }

    private JPanel createControlPanel() {
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, 0));
        controlPanel.setBackground(SYSTEM_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, 0, 0, 0));

        // 时区选择
        var timezoneLabel = new JLabel(Texts.TIMEZONE_LABEL);
        timezoneLabel.setFont(HEADLINE);
        timezoneLabel.setForeground(LABEL);

        timezoneCombo = new JComboBox<>(timezones.keySet().toArray(new String[0]));
        timezoneCombo.setSelectedItem("Beijing");
        timezoneCombo.setFont(BODY);
        timezoneCombo.setBackground(WHITE);
        timezoneCombo.setForeground(LABEL);
        timezoneCombo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        timezoneCombo.addActionListener((ev) -> updateTime());

        // 12/24小时制切换
        var formatButton = this.createSecondaryButton(Texts.FORMAT_12_HOUR);
        formatButton.addActionListener((ev) -> {
            is24Hour = !is24Hour;
            formatButton.setText(is24Hour ? Texts.FORMAT_12_HOUR : Texts.FORMAT_24_HOUR);
            updateTime();
        });

        // 闹钟按钮
        var alarmButton = this.createPrimaryButton(Texts.ALARM_BUTTON);
        alarmButton.addActionListener((ev) -> this.showAlarmDialog());

        // 全屏按钮
        var fullscreenButton = this.createSecondaryButton(Texts.FULLSCREEN_BUTTON);
        fullscreenButton.addActionListener((ev) -> toggleFullscreen());

        controlPanel.add(timezoneLabel);
        controlPanel.add(timezoneCombo);
        controlPanel.add(formatButton);
        controlPanel.add(alarmButton);
        controlPanel.add(fullscreenButton);

        return controlPanel;
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
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
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
    private void startClock() {
        timer = new Timer(1000, (ev) -> updateTime());
        timer.start();
        updateTime(); // Update immediately
    }

    private void updateTime() {
        String selectedTimezone = (String) timezoneCombo.getSelectedItem();
        ZoneId zoneId = timezones.get(selectedTimezone);

        LocalDateTime now = LocalDateTime.now(zoneId);

        // Format time
        String timePattern = is24Hour ? "HH:mm:ss" : "hh:mm:ss a";
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timePattern);
        String timeText = now.format(timeFormatter);

        // Format date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String dateText = now.format(dateFormatter);

        timeLabel.setText(timeText);
        dateLabel.setText(dateText);
    }

    private void showAlarmDialog() {
        var alarmDialog = new JDialog(this, Texts.ALARM_DIALOG_TITLE, true);
        alarmDialog.setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_12, SPACING_12, SPACING_12, SPACING_12);

        // Hour selection
        gbc.gridx = 0; gbc.gridy = 0;
        alarmDialog.add(new JLabel(Texts.HOUR_LABEL), gbc);

        var hourSpinner = new JSpinner(new SpinnerNumberModel(8, 0, 23, 1));
        hourSpinner.setFont(BODY);
        gbc.gridx = 1;
        alarmDialog.add(hourSpinner, gbc);

        // Minute selection
        gbc.gridx = 0; gbc.gridy = 1;
        alarmDialog.add(new JLabel(Texts.MINUTE_LABEL), gbc);

        var minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        minuteSpinner.setFont(BODY);
        gbc.gridx = 1;
        alarmDialog.add(minuteSpinner, gbc);

        // Alarm message
        gbc.gridx = 0; gbc.gridy = 2;
        alarmDialog.add(new JLabel(Texts.MESSAGE_LABEL), gbc);

        var messageField = new JTextField(Texts.DEFAULT_ALARM_MESSAGE, 15);
        messageField.setFont(BODY);
        messageField.setBackground(GRAY6);
        messageField.setForeground(LABEL);
        messageField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        gbc.gridx = 1;
        alarmDialog.add(messageField, gbc);

        // Buttons
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_12, 0));
        buttonPanel.setBackground(SYSTEM_BACKGROUND);

        var setButton = this.createSuccessButton(Texts.SET_BUTTON);
        var cancelButton = this.createSecondaryButton(Texts.CANCEL_BUTTON);

        setButton.addActionListener((ev) -> {
            int hour = (Integer) hourSpinner.getValue();
            int minute = (Integer) minuteSpinner.getValue();
            String message = messageField.getText();
            setAlarm(hour, minute, message);
            alarmDialog.dispose();
        });

        cancelButton.addActionListener((ev) -> alarmDialog.dispose());

        buttonPanel.add(setButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        alarmDialog.add(buttonPanel, gbc);

        alarmDialog.pack();
        alarmDialog.setLocationRelativeTo(this);
        alarmDialog.setVisible(true);
    }

    private void setAlarm(int hour, int minute, String message) {
        if (alarmTimer != null) {
            alarmTimer.stop();
        }

        alarmTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime now = LocalDateTime.now();
                if (now.getHour() == hour && now.getMinute() == minute && now.getSecond() == 0) {
                    showAlarmNotification(message);
                    ((javax.swing.Timer) e.getSource()).stop();
                }
            }
        });
        alarmTimer.start();

        JOptionPane.showMessageDialog(this,
            String.format("Alarm set for: %02d:%02d\nMessage: %s", hour, minute, message),
            "Alarm Set Successfully",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAlarmNotification(String message) {
        // Play system beep
        Toolkit.getDefaultToolkit().beep();

        // Show alarm dialog
        var alarmNotification = new JDialog(this, Texts.ALARM_NOTIFICATION_TITLE, true);
        alarmNotification.setLayout(new BorderLayout());

        var messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setFont(TITLE3);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_16, SPACING_24));

        var stopButton = this.createPrimaryButton(Texts.STOP_ALARM_BUTTON);
        stopButton.addActionListener((ev) -> alarmNotification.dispose());

        var buttonPanel = new JPanel();
        buttonPanel.add(stopButton);

        alarmNotification.add(messageLabel, BorderLayout.CENTER);
        alarmNotification.add(buttonPanel, BorderLayout.SOUTH);

        alarmNotification.setSize(350, 200);
        alarmNotification.setLocationRelativeTo(this);
        alarmNotification.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        alarmNotification.setVisible(true);
    }

    private void toggleFullscreen() {
        var device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (device.getFullScreenWindow() == null) {
            // Enter fullscreen
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            dispose();
            setUndecorated(true);
            setVisible(true);
            device.setFullScreenWindow(this);

            // Adjust font sizes
            timeLabel.setFont(new Font("SF Pro Display", Font.BOLD, 120));
            dateLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 36));
            isFullscreen = true;
        } else {
            // Exit fullscreen
            device.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setVisible(true);
            setExtendedState(JFrame.NORMAL);

            // Restore font sizes
            timeLabel.setFont(new Font("SF Pro Display", Font.BOLD, 64));
            dateLabel.setFont(TITLE3);
            isFullscreen = false;
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

    private void toggleFormat() {
        is24Hour = !is24Hour;
        updateTime();
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_F:
                        // F键切换时间格式
                        toggleFormat();
                        break;
                    case java.awt.event.KeyEvent.VK_A:
                        // A键显示闹钟设置
                        showAlarmDialog();
                        break;
                    case java.awt.event.KeyEvent.VK_F11:
                        // F11键切换全屏
                        toggleFullscreen();
                        break;
                    case java.awt.event.KeyEvent.VK_ESCAPE:
                        // ESC键退出全屏
                        if (isFullscreen) {
                            toggleFullscreen();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_UP:
                        // 上箭头选择上一个时区
                        int currentIndex = timezoneCombo.getSelectedIndex();
                        if (currentIndex > 0) {
                            timezoneCombo.setSelectedIndex(currentIndex - 1);
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        // 下箭头选择下一个时区
                        int downIndex = timezoneCombo.getSelectedIndex();
                        if (downIndex < timezoneCombo.getItemCount() - 1) {
                            timezoneCombo.setSelectedIndex(downIndex + 1);
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_SPACE:
                        // 空格键暂停/恢复时钟
                        if (timer.isRunning()) {
                            timer.stop();
                        } else {
                            timer.start();
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