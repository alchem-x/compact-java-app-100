import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new CalendarApp().setVisible(true));
}

static class CalendarApp extends JFrame {
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
    private final JLabel monthYearLabel;
    private final JPanel calendarPanel;
    private JButton prevButton;
    private JButton nextButton;
    private JButton todayButton;
    private JLabel selectedDateLabel;

    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private LocalDate today;

    public CalendarApp() {
        today = LocalDate.now();
        currentMonth = YearMonth.now();
        selectedDate = today;

        monthYearLabel = new JLabel();
        calendarPanel = new JPanel();
        prevButton = null; // 将在initializeGUI中创建
        nextButton = null; // 将在initializeGUI中创建
        todayButton = null; // 将在initializeGUI中创建

        this.initializeGUI();
        this.updateCalendar();
        this.setupKeyboardShortcuts();
    }

    private void initializeGUI() {
        setTitle("📅 日历");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板 - 使用苹果风格
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel("📅 日历", SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_24, 0));

        // 顶部导航面板 - 使用苹果风格
        var navPanel = this.createNavigationPanel();

        // 日历主面板 - 使用苹果风格
        var calendarContainerPanel = this.createCalendarContainerPanel();

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.CENTER);
        mainPanel.add(calendarContainerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // 底部信息面板 - 使用苹果风格
        var infoPanel = this.createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);

        setSize(600, 550);
        setLocationRelativeTo(null);
    }

    private JPanel createNavigationPanel() {
        var navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(SYSTEM_BACKGROUND);
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_16, 0));

        // 月份年份标签 - 使用苹果风格标题字体
        monthYearLabel.setFont(TITLE3);
        monthYearLabel.setForeground(LABEL);
        monthYearLabel.setHorizontalAlignment(JLabel.CENTER);

        // 导航按钮面板 - 使用苹果风格按钮
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_12, 0));
        buttonPanel.setBackground(SYSTEM_BACKGROUND);

        prevButton = this.createSecondaryButton("◀");
        prevButton.setPreferredSize(new Dimension(44, 44));
        prevButton.addActionListener((ev) -> this.previousMonth());

        todayButton = this.createPrimaryButton("今天");
        todayButton.setPreferredSize(BUTTON_REGULAR);
        todayButton.addActionListener((ev) -> this.goToToday());

        nextButton = this.createSecondaryButton("▶");
        nextButton.setPreferredSize(new Dimension(44, 44));
        nextButton.addActionListener((ev) -> this.nextMonth());

        buttonPanel.add(prevButton);
        buttonPanel.add(todayButton);
        buttonPanel.add(nextButton);

        navPanel.add(monthYearLabel, BorderLayout.CENTER);
        navPanel.add(buttonPanel, BorderLayout.EAST);

        return navPanel;
    }

    private JPanel createCalendarContainerPanel() {
        var containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(SYSTEM_BACKGROUND);
        containerPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 星期标题面板
        var weekDaysPanel = this.createWeekDaysPanel();
        containerPanel.add(weekDaysPanel, BorderLayout.NORTH);

        // 日历网格面板
        calendarPanel.setLayout(new GridLayout(6, 7, SPACING_4, SPACING_4));
        calendarPanel.setBackground(SYSTEM_BACKGROUND);
        containerPanel.add(calendarPanel, BorderLayout.CENTER);

        return containerPanel;
    }

    private JPanel createWeekDaysPanel() {
        var weekDaysPanel = new JPanel(new GridLayout(1, 7, SPACING_4, SPACING_4));
        weekDaysPanel.setBackground(SYSTEM_BACKGROUND);

        var weekDays = new String[]{"日", "一", "二", "三", "四", "五", "六"};
        for (var day : weekDays) {
            var label = new JLabel(day, JLabel.CENTER);
            label.setFont(HEADLINE);
            label.setForeground(SECONDARY_LABEL);
            label.setOpaque(true);
            label.setBackground(SECONDARY_SYSTEM_BACKGROUND);
            label.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(RADIUS_4),
                BorderFactory.createEmptyBorder(SPACING_8, SPACING_4, SPACING_8, SPACING_4)
            ));
            weekDaysPanel.add(label);
        }

        return weekDaysPanel;
    }

    private JPanel createInfoPanel() {
        var infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, SPACING_12));
        infoPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_24, SPACING_16, SPACING_24)
        ));

        selectedDateLabel = new JLabel();
        selectedDateLabel.setFont(CALLOUT);
        selectedDateLabel.setForeground(LABEL);

        infoPanel.add(selectedDateLabel);

        return infoPanel;
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
    private void updateCalendar() {
        // 更新月份年份标签
        var monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.CHINESE);
        monthYearLabel.setText(currentMonth.getYear() + "年 " + monthName);

        // 清除现有的日期按钮（保留星期标题）
        var components = calendarPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            calendarPanel.remove(components[i]);
        }

        // 获取月份信息
        var firstDay = currentMonth.atDay(1);
        var daysInMonth = currentMonth.lengthOfMonth();
        var startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // 转换为0=周日的格式

        // 添加星期标题
        var weekDays = new String[]{"日", "一", "二", "三", "四", "五", "六"};
        for (var day : weekDays) {
            var label = new JLabel(day, JLabel.CENTER);
            label.setFont(HEADLINE);
            label.setForeground(SECONDARY_LABEL);
            label.setOpaque(true);
            label.setBackground(SECONDARY_SYSTEM_BACKGROUND);
            label.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(RADIUS_4),
                BorderFactory.createEmptyBorder(SPACING_8, SPACING_4, SPACING_8, SPACING_4)
            ));
            calendarPanel.add(label);
        }

        // 添加空白天数
        for (int i = 0; i < startDayOfWeek; i++) {
            var emptyLabel = new JLabel("");
            emptyLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(RADIUS_4),
                BorderFactory.createEmptyBorder(SPACING_8, SPACING_4, SPACING_8, SPACING_4)
            ));
            calendarPanel.add(emptyLabel);
        }

        // 添加月份中的天数
        for (int day = 1; day <= daysInMonth; day++) {
            var date = currentMonth.atDay(day);
            var dayButton = createDayButton(date);
            calendarPanel.add(dayButton);
        }

        // 填充剩余格子
        int totalCells = calendarPanel.getComponentCount();
        int remainingCells = 42 - totalCells; // 6行 × 7列 = 42格
        for (int i = 0; i < remainingCells; i++) {
            var emptyLabel = new JLabel("");
            emptyLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(RADIUS_4),
                BorderFactory.createEmptyBorder(SPACING_8, SPACING_4, SPACING_8, SPACING_4)
            ));
            calendarPanel.add(emptyLabel);
        }

        // 刷新显示
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private JButton createDayButton(LocalDate date) {
        var button = new JButton(String.valueOf(date.getDayOfMonth()));
        button.setFont(BODY);
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_4, SPACING_8, SPACING_4)
        ));
        button.setFocusPainted(false);

        // 设置按钮样式
        if (date.equals(today)) {
            // 今天
            button.setBackground(BLUE);
            button.setForeground(WHITE);
        } else if (date.equals(selectedDate)) {
            // 选中的日期
            button.setBackground(PURPLE);
            button.setForeground(WHITE);
        } else if (date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7) {
            // 周末
            button.setBackground(new Color(255, 240, 240));
            button.setForeground(RED);
        } else {
            // 普通工作日
            button.setBackground(WHITE);
            button.setForeground(LABEL);
        }

        // 添加悬停效果
        this.setupDayButtonHoverEffect(button, date);

        // 添加点击事件
        button.addActionListener((ev) -> {
            selectedDate = date;
            this.updateCalendar();
            this.updateSelectedDateInfo();
        });

        return button;
    }

    private void setupDayButtonHoverEffect(JButton button, LocalDate date) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color originalBackground = button.getBackground();
            private final Color originalForeground = button.getForeground();

            @Override
            public void mouseEntered(java.awt.event.MouseEvent ev) {
                if (button.isEnabled()) {
                    if (date.equals(today) || date.equals(selectedDate)) {
                        button.setBackground(darkenColor(originalBackground, 0.1f));
                    } else {
                        button.setBackground(SECONDARY_SYSTEM_BACKGROUND);
                        button.setForeground(LABEL);
                    }
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent ev) {
                if (button.isEnabled()) {
                    button.setBackground(originalBackground);
                    button.setForeground(originalForeground);
                }
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void updateSelectedDateInfo() {
        // 更新底部选中日期信息
        var formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEEE", Locale.CHINESE);
        var dayInfo = selectedDate.format(formatter);

        // 添加一些有趣的信息
        var dayOfYear = selectedDate.getDayOfYear();
        var isLeapYear = selectedDate.isLeapYear();

        var extraInfo = String.format(" (第%d天%s)", dayOfYear, isLeapYear ? ", 闰年" : "");

        selectedDateLabel.setText("选中日期: " + dayInfo + extraInfo);
    }

    private void previousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
    }

    private void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
    }

    private void goToToday() {
        currentMonth = YearMonth.from(today);
        selectedDate = today;
        updateCalendar();
        updateSelectedDateInfo();
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘导航支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_LEFT:
                        selectedDate = selectedDate.minusDays(1);
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        selectedDate = selectedDate.plusDays(1);
                        break;
                    case java.awt.event.KeyEvent.VK_UP:
                        selectedDate = selectedDate.minusWeeks(1);
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        selectedDate = selectedDate.plusWeeks(1);
                        break;
                    case java.awt.event.KeyEvent.VK_PAGE_UP:
                        currentMonth = currentMonth.minusMonths(1);
                        break;
                    case java.awt.event.KeyEvent.VK_PAGE_DOWN:
                        currentMonth = currentMonth.plusMonths(1);
                        break;
                    case java.awt.event.KeyEvent.VK_HOME:
                        selectedDate = today;
                        currentMonth = YearMonth.from(today);
                        break;
                    case java.awt.event.KeyEvent.VK_SPACE:
                        // 显示选中日期详情
                        JOptionPane.showMessageDialog(CalendarApp.this,
                            "选中日期: " + selectedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEEE", Locale.CHINA)),
                            "日期详情", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    default:
                        return;
                }

                updateCalendar();
                updateSelectedDateInfo();
            }
        });

        // 确保窗口可以获得焦点
        this.setFocusable(true);
        this.requestFocusInWindow();
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