import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new WorkdayCalculator().setVisible(true));
}

static class WorkdayCalculator extends JFrame {
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
    private JTextField startDateField;
    private JTextField endDateField;
    private JCheckBox includeStartCheck;
    private JCheckBox includeEndCheck;
    private JCheckBox[] holidayChecks;
    private JLabel workdaysLabel;
    private JLabel totalDaysLabel;
    private JLabel weekendsLabel;
    private JTextArea detailsArea;
    private JButton calculateButton;
    private JButton clearButton;
    private JButton todayButton;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 中国法定节假日（示例数据，实际使用时应该更新为当年的实际节假日）
    private Set<LocalDate> holidays;

    public WorkdayCalculator() {
        initializeHolidays();
        initializeGUI();
    }

    private void initializeHolidays() {
        holidays = new HashSet<>();
        // 2024年节假日示例
        addHoliday("2024-01-01"); // 元旦
        addHoliday("2024-02-10"); addHoliday("2024-02-11"); addHoliday("2024-02-12");
        addHoliday("2024-02-13"); addHoliday("2024-02-14"); addHoliday("2024-02-15");
        addHoliday("2024-02-16"); addHoliday("2024-02-17"); // 春节
        addHoliday("2024-04-04"); addHoliday("2024-04-05"); addHoliday("2024-04-06"); // 清明节
        addHoliday("2024-05-01"); addHoliday("2024-05-02"); addHoliday("2024-05-03"); addHoliday("2024-05-04"); addHoliday("2024-05-05"); // 劳动节
        addHoliday("2024-06-10"); // 端午节
        addHoliday("2024-09-15"); addHoliday("2024-09-16"); addHoliday("2024-09-17"); // 中秋节
        addHoliday("2024-10-01"); addHoliday("2024-10-02"); addHoliday("2024-10-03");
        addHoliday("2024-10-04"); addHoliday("2024-10-05"); addHoliday("2024-10-06"); addHoliday("2024-10-07"); // 国庆节
    }

    private void addHoliday(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            holidays.add(date);
        } catch (Exception e) {
            System.err.println("添加节假日失败: " + dateStr);
        }
    }

    private void initializeGUI() {
        setTitle("📅 工作日计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板 - 使用苹果风格
        var mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_8, SPACING_8, SPACING_8, SPACING_8);

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel("📅 工作日计算器", SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // 开始日期
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        var startDateLabel = new JLabel("开始日期:");
        startDateLabel.setFont(BODY);
        startDateLabel.setForeground(LABEL);
        mainPanel.add(startDateLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        startDateField = new JTextField(15);
        startDateField.setFont(new Font("SF Mono", Font.PLAIN, 14));
        startDateField.setText(LocalDate.now().format(dateFormatter));
        startDateField.setBackground(WHITE);
        startDateField.setForeground(LABEL);
        startDateField.setEditable(true);
        startDateField.setEnabled(true);
        startDateField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        mainPanel.add(startDateField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        todayButton = this.createSecondaryButton("今天");
        todayButton.addActionListener(e -> {
            startDateField.setText(LocalDate.now().format(dateFormatter));
            endDateField.setText(LocalDate.now().format(dateFormatter));
        });
        mainPanel.add(todayButton, gbc);

        // 结束日期
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        var endDateLabel = new JLabel("结束日期:");
        endDateLabel.setFont(BODY);
        endDateLabel.setForeground(LABEL);
        mainPanel.add(endDateLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        endDateField = new JTextField(15);
        endDateField.setFont(new Font("SF Mono", Font.PLAIN, 14));
        endDateField.setText(LocalDate.now().plusDays(30).format(dateFormatter));
        endDateField.setBackground(WHITE);
        endDateField.setForeground(LABEL);
        endDateField.setEditable(true);
        endDateField.setEnabled(true);
        endDateField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        mainPanel.add(endDateField, gbc);

        // 选项面板 - 使用苹果风格卡片
        var optionsPanel = new JPanel(new GridLayout(2, 2, SPACING_8, SPACING_4));
        optionsPanel.setBackground(WHITE);
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        includeStartCheck = new JCheckBox("包含开始日期", true);
        includeStartCheck.setFont(BODY);
        includeStartCheck.setForeground(LABEL);
        includeEndCheck = new JCheckBox("包含结束日期", true);
        includeEndCheck.setFont(BODY);
        includeEndCheck.setForeground(LABEL);

        optionsPanel.add(includeStartCheck);
        optionsPanel.add(includeEndCheck);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(optionsPanel, gbc);

        // 节假日面板 - 使用苹果风格卡片
        var holidayPanel = new JPanel(new GridLayout(2, 2, SPACING_8, SPACING_4));
        holidayPanel.setBackground(WHITE);
        holidayPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        String[] holidayOptions = {"周六", "周日", "元旦", "春节", "清明节", "劳动节", "端午节", "中秋节", "国庆节"};
        holidayChecks = new JCheckBox[holidayOptions.length];

        for (int i = 0; i < holidayOptions.length; i++) {
            holidayChecks[i] = new JCheckBox(holidayOptions[i], i < 2); // 默认选中周末
            holidayChecks[i].setFont(BODY);
            holidayChecks[i].setForeground(LABEL);
            if (i < 2) {
                holidayPanel.add(holidayChecks[i]);
            }
        }

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(holidayPanel, gbc);

        // 按钮面板
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, 0));
        buttonPanel.setBackground(SYSTEM_BACKGROUND);

        calculateButton = this.createSuccessButton("📊 计算工作日");
        calculateButton.addActionListener(new CalculateListener());

        clearButton = this.createWarningButton("🗑️ 清空");
        clearButton.addActionListener(e -> clearFields());

        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        // 结果显示区域 - 使用苹果风格卡片
        var resultPanel = new JPanel(new GridLayout(3, 1, SPACING_8, SPACING_8));
        resultPanel.setBackground(WHITE);
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        workdaysLabel = new JLabel("工作日: 0 天");
        workdaysLabel.setFont(TITLE3);
        workdaysLabel.setForeground(GREEN);

        totalDaysLabel = new JLabel("总天数: 0 天");
        totalDaysLabel.setFont(BODY);
        totalDaysLabel.setForeground(LABEL);

        weekendsLabel = new JLabel("休息: 0 天");
        weekendsLabel.setFont(BODY);
        weekendsLabel.setForeground(RED);

        resultPanel.add(workdaysLabel);
        resultPanel.add(totalDaysLabel);
        resultPanel.add(weekendsLabel);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(resultPanel, gbc);

        // 详细列表 - 使用苹果风格卡片
        detailsArea = new JTextArea(10, 30);
        detailsArea.setFont(new Font("SF Mono", Font.PLAIN, 12));
        detailsArea.setEditable(false);
        detailsArea.setBackground(GRAY6);
        detailsArea.setForeground(LABEL);
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
        ));

        var scrollPane = new JScrollPane(detailsArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        scrollPane.getViewport().setBackground(GRAY6);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(scrollPane, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 设置窗口
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void clearFields() {
        startDateField.setText(LocalDate.now().format(dateFormatter));
        endDateField.setText(LocalDate.now().plusDays(30).format(dateFormatter));
        workdaysLabel.setText("工作日: 0 天");
        totalDaysLabel.setText("总天数: 0 天");
        weekendsLabel.setText("休息: 0 天");
        detailsArea.setText("");
    }

    private boolean isWeekend(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // 周六或周日
    }

    private boolean isHoliday(LocalDate date) {
        // 检查是否为法定节假日
        if (holidays.contains(date)) {
            return true;
        }

        // 检查是否为周末且周末选项被选中
        if (isWeekend(date)) {
            int dayOfWeek = date.getDayOfWeek().getValue();
            if (dayOfWeek == 6 && holidayChecks[0].isSelected()) return true; // 周六
            if (dayOfWeek == 7 && holidayChecks[1].isSelected()) return true; // 周日
        }

        return false;
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
        button.setFont(BODY); // 使用较小的字体避免显示不全
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        button.setPreferredSize(new Dimension(100, 36)); // 较小的按钮尺寸

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

    // ===== 业务逻辑方法 =====
    private class CalculateListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText(), dateFormatter);
                LocalDate endDate = LocalDate.parse(endDateField.getText(), dateFormatter);

                if (startDate.isAfter(endDate)) {
                    JOptionPane.showMessageDialog(WorkdayCalculator.this,
                        "开始日期不能晚于结束日期！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (startDate.isAfter(LocalDate.now().plusYears(5))) {
                    JOptionPane.showMessageDialog(WorkdayCalculator.this,
                        "日期范围不能超过5年！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int workdays = 0;
                int totalDays = 0;
                int weekends = 0;
                var details = new StringBuilder();
                details.append("日期\t\t星期\t状态\n");
                details.append("-".repeat(50)).append("\n");

                LocalDate currentDate = startDate;
                while (!currentDate.isAfter(endDate)) {
                    boolean isInclude = true;

                    // 检查是否包含边界日期
                    if (currentDate.equals(startDate) && !includeStartCheck.isSelected()) {
                        isInclude = false;
                    }
                    if (currentDate.equals(endDate) && !includeEndCheck.isSelected()) {
                        isInclude = false;
                    }

                    if (isInclude) {
                        totalDays++;
                        String dayOfWeek = getDayOfWeekName(currentDate.getDayOfWeek().getValue());

                        if (isHoliday(currentDate)) {
                            weekends++;
                            details.append(String.format("%s\t%s\t%s\n",
                                currentDate.format(dateFormatter), dayOfWeek, "休息"));
                        } else {
                            workdays++;
                            details.append(String.format("%s\t%s\t%s\n",
                                currentDate.format(dateFormatter), dayOfWeek, "工作"));
                        }
                    }

                    currentDate = currentDate.plusDays(1);
                }

                // 更新显示
                workdaysLabel.setText(String.format("工作日: %d 天", workdays));
                totalDaysLabel.setText(String.format("总天数: %d 天", totalDays));
                weekendsLabel.setText(String.format("休息: %d 天", weekends));
                detailsArea.setText(details.toString());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(WorkdayCalculator.this,
                    "请输入有效的日期格式（yyyy-MM-dd）！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        private String getDayOfWeekName(int dayOfWeek) {
            return switch (dayOfWeek) {
                case 1 -> "周一";
                case 2 -> "周二";
                case 3 -> "周三";
                case 4 -> "周四";
                case 5 -> "周五";
                case 6 -> "周六";
                case 7 -> "周日";
                default -> "";
            };
        }
    }
}