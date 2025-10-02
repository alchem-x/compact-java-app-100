import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new WorldClock().setVisible(true));
}

static class WorldClock extends JFrame {
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
    private final Timer updateTimer;
    private final Map<String, String> timeZones;
    private final JPanel clockPanel;
    private final JLabel statusLabel;

    public WorldClock() {
        timeZones = Map.of(
            "北京", "Asia/Shanghai",
            "东京", "Asia/Tokyo",
            "纽约", "America/New_York",
            "伦敦", "Europe/London",
            "巴黎", "Europe/Paris",
            "悉尼", "Australia/Sydney",
            "洛杉矶", "America/Los_Angeles",
            "莫斯科", "Europe/Moscow"
        );

        clockPanel = new JPanel();
        statusLabel = new JLabel("世界时钟");
        updateTimer = new Timer(1000, (e) -> updateClocks());

        this.initializeGUI();
        this.updateClocks();
        updateTimer.start();
    }

    private void initializeGUI() {
        setTitle("🌍 世界时钟");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板 - 使用苹果风格
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel("🌍 世界时钟", SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_24, 0));

        // 创建时钟面板 - 使用苹果风格卡片
        clockPanel.setLayout(new GridLayout(4, 2, SPACING_16, SPACING_16));
        clockPanel.setBackground(SYSTEM_BACKGROUND);
        clockPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 为每个时区创建时钟显示
        for (var city : timeZones.keySet()) {
            var clockCard = this.createClockCard(city);
            clockPanel.add(clockCard);
        }

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(clockPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // 创建状态栏 - 使用苹果风格
        var statusPanel = this.createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private JPanel createClockCard(String city) {
        var card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        card.setBackground(WHITE);

        // 城市名称
        var cityLabel = new JLabel(city, JLabel.CENTER);
        cityLabel.setFont(TITLE3);
        cityLabel.setForeground(LABEL);
        cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 时间显示
        var timeLabel = new JLabel("--:--:--", JLabel.CENTER);
        timeLabel.setFont(new Font("SF Mono", Font.BOLD, 24));
        timeLabel.setForeground(BLUE);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 日期显示
        var dateLabel = new JLabel("----/--/--", JLabel.CENTER);
        dateLabel.setFont(CAPTION1);
        dateLabel.setForeground(SECONDARY_LABEL);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 时区显示
        var zoneLabel = new JLabel("", JLabel.CENTER);
        zoneLabel.setFont(CAPTION2);
        zoneLabel.setForeground(TERTIARY_LABEL);
        zoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(cityLabel);
        card.add(Box.createRigidArea(new Dimension(0, SPACING_8)));
        card.add(timeLabel);
        card.add(Box.createRigidArea(new Dimension(0, SPACING_4)));
        card.add(dateLabel);
        card.add(Box.createRigidArea(new Dimension(0, SPACING_2)));
        card.add(zoneLabel);
        card.add(Box.createVerticalGlue());

        // 存储标签引用以便更新
        card.putClientProperty("timeLabel", timeLabel);
        card.putClientProperty("dateLabel", dateLabel);
        card.putClientProperty("zoneLabel", zoneLabel);
        card.putClientProperty("city", city);

        return card;
    }

    private JPanel createStatusPanel() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_24, SPACING_12, SPACING_24)
        ));

        statusLabel.setFont(FOOTNOTE);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        return statusPanel;
    }

    // ===== 按钮创建方法 =====
    private JButton createPrimaryButton(String text) {
        return this.createStyledButton(text, BLUE, WHITE);
    }

    private JButton createSecondaryButton(String text) {
        return this.createStyledButton(text, GRAY6, LABEL);
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
    private void updateClocks() {
        var components = clockPanel.getComponents();

        for (var component : components) {
            if (component instanceof JPanel card) {
                var city = (String) card.getClientProperty("city");
                var timeLabel = (JLabel) card.getClientProperty("timeLabel");
                var dateLabel = (JLabel) card.getClientProperty("dateLabel");
                var zoneLabel = (JLabel) card.getClientProperty("zoneLabel");

                if (city != null && timeZones.containsKey(city)) {
                    try {
                        var zoneId = ZoneId.of(timeZones.get(city));
                        var now = LocalDateTime.now(zoneId);

                        // 格式化时间
                        var timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                        var dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

                        timeLabel.setText(now.format(timeFormatter));
                        dateLabel.setText(now.format(dateFormatter));

                        // 显示时区偏移
                        var offset = zoneId.getRules().getOffset(now);
                        var offsetHours = offset.getTotalSeconds() / 3600;
                        var offsetString = String.format("UTC%+d", offsetHours);
                        zoneLabel.setText(offsetString);

                        // 根据时间设置不同的背景色
                        var hour = now.getHour();
                        Color bgColor;
                        if (hour >= 6 && hour < 12) {
                            bgColor = new Color(255, 248, 220); // 早晨 - 浅黄
                        } else if (hour >= 12 && hour < 18) {
                            bgColor = new Color(240, 248, 255); // 下午 - 浅蓝
                        } else if (hour >= 18 && hour < 22) {
                            bgColor = new Color(255, 240, 245); // 傍晚 - 浅粉
                        } else {
                            bgColor = new Color(230, 230, 250); // 夜晚 - 浅紫
                        }
                        card.setBackground(bgColor);

                    } catch (Exception e) {
                        timeLabel.setText("错误");
                        dateLabel.setText("--/--/--");
                        zoneLabel.setText("");
                    }
                }
            }
        }

        // 更新状态栏
        var localTime = LocalDateTime.now();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        statusLabel.setText("本地时间: " + localTime.format(formatter));
    }

    @Override
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        super.dispose();
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