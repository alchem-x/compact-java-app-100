import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "⚖️ BMI计算器";

    // 主界面标题
    static final String MAIN_TITLE = "⚖️ BMI身体质量指数计算器";

    // 按钮文本
    static final String CALCULATE_BUTTON = "计算BMI";
    static final String CLEAR_BUTTON = "清空";
    static final String COPY_BUTTON = "复制结果";

    // 面板标题
    static final String INPUT_PANEL_TITLE = "输入信息";
    static final String RESULT_PANEL_TITLE = "计算结果";
    static final String HISTORY_PANEL_TITLE = "历史记录";

    // 标签文本
    static final String HEIGHT_LABEL = "身高 (cm):";
    static final String WEIGHT_LABEL = "体重 (kg):";
    static final String AGE_LABEL = "年龄:";
    static final String GENDER_LABEL = "性别:";
    static final String BMI_LABEL = "BMI指数:";
    static final String CATEGORY_LABEL = "体重分类:";
    static final String IDEAL_WEIGHT_LABEL = "理想体重范围:";

    // 性别选项
    static final String GENDER_MALE = "男性";
    static final String GENDER_FEMALE = "女性";

    // 状态消息
    static final String STATUS_READY = "请输入身高和体重";
    static final String STATUS_CALCULATED = "BMI计算完成";
    static final String STATUS_CLEARED = "内容已清空";
    static final String STATUS_COPIED = "结果已复制";

    // 错误消息
    static final String ERROR_INVALID_HEIGHT = "请输入有效的身高 (50-250 cm)";
    static final String ERROR_INVALID_WEIGHT = "请输入有效的体重 (20-300 kg)";
    static final String ERROR_COPY_FAILED = "复制失败";

    // BMI分类
    static final String BMI_UNDERWEIGHT = "偏瘦";
    static final String BMI_NORMAL = "正常";
    static final String BMI_OVERWEIGHT = "超重";
    static final String BMI_OBESE_I = "肥胖I度";
    static final String BMI_OBESE_II = "肥胖II度";

    // BMI范围
    static final String BMI_UNDERWEIGHT_RANGE = "BMI < 18.5";
    static final String BMI_NORMAL_RANGE = "18.5 ≤ BMI < 24";
    static final String BMI_OVERWEIGHT_RANGE = "24 ≤ BMI < 28";
    static final String BMI_OBESE_RANGE = "BMI ≥ 28";

    // 健康建议
    static final String ADVICE_UNDERWEIGHT = "建议增加营养摄入，适当运动增重";
    static final String ADVICE_NORMAL = "保持良好的饮食和运动习惯";
    static final String ADVICE_OVERWEIGHT = "建议控制饮食，增加有氧运动";
    static final String ADVICE_OBESE = "建议咨询医生，制定减重计划";

    // 文件对话框
    static final String FILE_CHOOSER_TITLE = "保存BMI记录";
    static final String FILE_FILTER_TEXT = "文本文件 (*.txt)";

    // 示例数据
    static final String SAMPLE_HEIGHT = "170";
    static final String SAMPLE_WEIGHT = "65";
    static final String SAMPLE_AGE = "25";

    // 帮助信息
    static final String HELP_MESSAGE = """
        BMI计算器使用说明：

        • BMI（身体质量指数）是衡量体重是否健康的常用指标
        • 输入身高（厘米）和体重（公斤）
        • 可选择输入年龄和性别获得更准确的评估
        • 点击"计算BMI"按钮查看结果
        • 结果包括BMI指数、体重分类和健康建议
        • 历史记录功能可保存多次计算结果

        BMI分类标准：
        • 偏瘦：BMI < 18.5
        • 正常：18.5 ≤ BMI < 24
        • 超重：24 ≤ BMI < 28
        • 肥胖：BMI ≥ 28

        快捷键：
        Ctrl+C - 计算BMI
        Ctrl+L - 清空
        Ctrl+P - 复制结果
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new BMICalculator().setVisible(true));
}

static class BMICalculator extends JFrame {
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

    // ===== 应用状态 =====
    private JTextField heightField;
    private JTextField weightField;
    private JLabel bmiLabel;
    private JLabel categoryLabel;
    private JTextArea adviceArea;
    private JProgressBar bmiBar;
    private JComboBox<String> unitCombo;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public BMICalculator() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        this.initializeGUI();
        this.setupKeyboardShortcuts();

        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板 - 使用苹果风格
        var mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_12, SPACING_12, SPACING_12, SPACING_12);

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel(Texts.MAIN_TITLE);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // 单位选择 - 使用苹果风格下拉框
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        var unitLabel = new JLabel("单位制:");
        unitLabel.setFont(HEADLINE);
        unitLabel.setForeground(LABEL);
        mainPanel.add(unitLabel, gbc);

        unitCombo = new JComboBox<>(new String[]{"公制 (cm/kg)", "英制 (ft/lbs)"});
        unitCombo.setFont(BODY);
        unitCombo.setBackground(WHITE);
        unitCombo.setForeground(LABEL);
        unitCombo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        unitCombo.addActionListener(e -> updateLabels());
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(unitCombo, gbc);

        // 身高输入 - 使用苹果风格文本框
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        var heightLabel = new JLabel(Texts.HEIGHT_LABEL);
        heightLabel.setFont(HEADLINE);
        heightLabel.setForeground(LABEL);
        mainPanel.add(heightLabel, gbc);

        heightField = new JTextField(10);
        heightField.setFont(BODY);
        heightField.setBackground(GRAY6);
        heightField.setForeground(LABEL);
        heightField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        heightField.addActionListener(e -> calculateBMI());
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(heightField, gbc);

        // 体重输入
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        var weightLabel = new JLabel(Texts.WEIGHT_LABEL);
        weightLabel.setFont(HEADLINE);
        weightLabel.setForeground(LABEL);
        mainPanel.add(weightLabel, gbc);

        weightField = new JTextField(10);
        weightField.setFont(BODY);
        weightField.setBackground(GRAY6);
        weightField.setForeground(LABEL);
        weightField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        weightField.addActionListener((ev) -> this.calculateBMI());

        // 添加实时输入验证
        this.setupInputValidation();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(weightField, gbc);

        // 计算按钮 - 使用苹果风格主要按钮
        var calculateButton = this.createPrimaryButton("🧮 " + Texts.CALCULATE_BUTTON);
        calculateButton.addActionListener(e -> calculateBMI());
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(calculateButton, gbc);

        // BMI结果显示
        gbc.gridheight = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        var bmiTitleLabel = new JLabel(Texts.BMI_LABEL);
        bmiTitleLabel.setFont(HEADLINE);
        bmiTitleLabel.setForeground(LABEL);
        mainPanel.add(bmiTitleLabel, gbc);

        bmiLabel = new JLabel("--");
        bmiLabel.setFont(TITLE3);
        bmiLabel.setForeground(BLUE);
        gbc.gridx = 1;
        mainPanel.add(bmiLabel, gbc);

        // BMI分类
        gbc.gridx = 0; gbc.gridy = 5;
        var categoryTitleLabel = new JLabel(Texts.CATEGORY_LABEL);
        categoryTitleLabel.setFont(HEADLINE);
        categoryTitleLabel.setForeground(LABEL);
        mainPanel.add(categoryTitleLabel, gbc);

        categoryLabel = new JLabel("--");
        categoryLabel.setFont(HEADLINE);
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(categoryLabel, gbc);

        // BMI指示条 - 使用苹果风格进度条
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        var bmiRangeLabel = new JLabel("BMI范围:");
        bmiRangeLabel.setFont(HEADLINE);
        bmiRangeLabel.setForeground(LABEL);
        mainPanel.add(bmiRangeLabel, gbc);

        bmiBar = new JProgressBar(0, 40);
        bmiBar.setStringPainted(true);
        bmiBar.setPreferredSize(new Dimension(200, 25));
        bmiBar.setFont(CAPTION1);
        bmiBar.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_2, SPACING_4, SPACING_2, SPACING_4)
        ));
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(bmiBar, gbc);

        // 健康建议 - 使用苹果风格文本区域
        gbc.gridwidth = 3;
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        var adviceTitleLabel = new JLabel("健康建议:");
        adviceTitleLabel.setFont(HEADLINE);
        adviceTitleLabel.setForeground(LABEL);
        mainPanel.add(adviceTitleLabel, gbc);

        adviceArea = new JTextArea(6, 30);
        adviceArea.setFont(CALLOUT);
        adviceArea.setEditable(false);
        adviceArea.setLineWrap(true);
        adviceArea.setWrapStyleWord(true);
        adviceArea.setBackground(GRAY6);
        adviceArea.setForeground(LABEL);
        adviceArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));

        var scrollPane = new JScrollPane(adviceArea);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(GRAY6);
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // BMI参考表 - 使用苹果风格
        this.createReferencePanel();

        setSize(500, 650);
        setLocationRelativeTo(null);
    }

    private void createReferencePanel() {
        var referencePanel = new JPanel();
        referencePanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        referencePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder(Texts.RESULT_PANEL_TITLE)),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        referencePanel.setLayout(new GridLayout(5, 2, SPACING_8, SPACING_8));

        var ranges = new String[][]{
            {"偏瘦", "< 18.5"},
            {"正常", "18.5 - 24.9"},
            {"超重", "25.0 - 29.9"},
            {"肥胖I度", "30.0 - 34.9"},
            {"肥胖II度", "≥ 35.0"}
        };

        var colors = new Color[]{
            new Color(135, 206, 250),  // 浅蓝色
            new Color(144, 238, 144),  // 浅绿色
            new Color(255, 255, 0),    // 黄色
            new Color(255, 165, 0),    // 橙色
            new Color(255, 99, 71)     // 红色
        };

        for (var i = 0; i < ranges.length; i++) {
            var categoryLabel = new JLabel(ranges[i][0], JLabel.CENTER);
            categoryLabel.setOpaque(true);
            categoryLabel.setBackground(colors[i]);
            categoryLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(RADIUS_4),
                BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
            ));

            var rangeLabel = new JLabel(ranges[i][1], JLabel.CENTER);
            rangeLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(RADIUS_4),
                BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
            ));

            referencePanel.add(categoryLabel);
            referencePanel.add(rangeLabel);
        }

        add(referencePanel, BorderLayout.SOUTH);
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
    private void updateLabels() {
        var isMetric = unitCombo.getSelectedIndex() == 0;

        var components = ((JPanel) getContentPane().getComponent(0)).getComponents();
        for (var comp : components) {
            if (comp instanceof JLabel) {
                var label = (JLabel) comp;
                if (label.getText().contains("身高")) {
                    label.setText(isMetric ? "身高 (cm):" : "身高 (ft):");
                } else if (label.getText().contains("体重")) {
                    label.setText(isMetric ? "体重 (kg):" : "体重 (lbs):");
                }
            }
        }

        // 清空输入和结果
        heightField.setText("");
        weightField.setText("");
        this.resetResults();
    }

    private void calculateBMI() {
        try {
            var heightText = heightField.getText().trim();
            var weightText = weightField.getText().trim();

            if (heightText.isEmpty() || weightText.isEmpty()) {
                this.resetResults();
                return;
            }

            var height = Double.parseDouble(heightText);
            var weight = Double.parseDouble(weightText);

            if (height <= 0 || weight <= 0) {
                JOptionPane.showMessageDialog(this, Texts.ERROR_INVALID_HEIGHT, "输入错误", JOptionPane.WARNING_MESSAGE);
                this.resetResults();
                return;
            }

            // 转换为公制单位
            var isMetric = unitCombo.getSelectedIndex() == 0;
            if (!isMetric) {
                // 英制转公制
                height = height * 30.48; // 英尺转厘米
                weight = weight * 0.453592; // 磅转千克
            }

            // 计算BMI
            var heightInMeters = height / 100.0;
            var bmi = weight / (heightInMeters * heightInMeters);

            // 显示结果
            this.displayResults(bmi);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, Texts.ERROR_INVALID_WEIGHT, "输入错误", JOptionPane.WARNING_MESSAGE);
            this.resetResults();
        }
    }

    private void setupInputValidation() {
        // 为文本字段添加焦点监听器，当失去焦点时自动计算
        heightField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent ev) {
                if (!heightField.getText().trim().isEmpty() && !weightField.getText().trim().isEmpty()) {
                    calculateBMI();
                }
            }
        });

        weightField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent ev) {
                if (!heightField.getText().trim().isEmpty() && !weightField.getText().trim().isEmpty()) {
                    calculateBMI();
                }
            }
        });
    }

    private void displayResults(double bmi) {
        bmiLabel.setText(df.format(bmi));

        // 设置BMI指示条
        var barValue = Math.min(40, (int) bmi);
        bmiBar.setValue(barValue);
        bmiBar.setString(df.format(bmi));

        // 确定BMI分类和颜色
        String category;
        Color color;
        String advice;

        if (bmi < 18.5) {
            category = Texts.BMI_UNDERWEIGHT;
            color = new Color(135, 206, 250);
            advice = "您的体重偏轻。建议：\n" +
                    "• 增加营养摄入，多吃高蛋白食物\n" +
                    "• 进行适量的力量训练增加肌肉量\n" +
                    "• 保证充足的睡眠和休息\n" +
                    "• 如有疑虑，请咨询营养师或医生";
        } else if (bmi < 25.0) {
            category = Texts.BMI_NORMAL;
            color = new Color(144, 238, 144);
            advice = "恭喜！您的体重在正常范围内。建议：\n" +
                    "• 保持现有的健康生活方式\n" +
                    "• 均衡饮食，适量运动\n" +
                    "• 定期监测体重变化\n" +
                    "• 继续保持良好的作息习惯";
        } else if (bmi < 30.0) {
            category = Texts.BMI_OVERWEIGHT;
            color = new Color(255, 255, 0);
            advice = "您的体重超出正常范围。建议：\n" +
                    "• 控制饮食，减少高热量食物摄入\n" +
                    "• 增加有氧运动，如快走、游泳\n" +
                    "• 制定合理的减重计划\n" +
                    "• 考虑咨询专业的营养师";
        } else if (bmi < 35.0) {
            category = Texts.BMI_OBESE_I;
            color = new Color(255, 165, 0);
            advice = "您属于轻度肥胖。建议：\n" +
                    "• 严格控制饮食，减少热量摄入\n" +
                    "• 制定规律的运动计划\n" +
                    "• 监控血压、血糖等健康指标\n" +
                    "• 强烈建议咨询医生制定减重方案";
        } else {
            category = Texts.BMI_OBESE_II;
            color = new Color(255, 99, 71);
            advice = "您属于重度肥胖，存在健康风险。建议：\n" +
                    "• 立即咨询医生，制定专业减重计划\n" +
                    "• 可能需要药物或手术治疗\n" +
                    "• 定期监测心血管健康状况\n" +
                    "• 寻求专业的医疗和营养指导";
        }

        categoryLabel.setText(category);
        categoryLabel.setForeground(color);
        bmiBar.setForeground(color);

        adviceArea.setText(advice);
    }

    private void resetResults() {
        bmiLabel.setText("--");
        categoryLabel.setText("--");
        categoryLabel.setForeground(LABEL);
        bmiBar.setValue(0);
        bmiBar.setString("");
        adviceArea.setText("");
    }

    private void clearAll() {
        heightField.setText("");
        weightField.setText("");
        this.resetResults();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "输入错误", JOptionPane.ERROR_MESSAGE);
        this.resetResults();
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
                    case KeyEvent.VK_C:
                        // C键计算BMI
                        if (ev.isControlDown()) {
                            calculateBMI();
                        }
                        break;
                    case KeyEvent.VK_L:
                        // L键清空
                        if (ev.isControlDown()) {
                            clearAll();
                        }
                        break;
                    case KeyEvent.VK_P:
                        // P键复制结果
                        if (ev.isControlDown()) {
                            copyResults();
                        }
                        break;
                    case KeyEvent.VK_F1:
                        // F1键显示帮助
                        showHelp();
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

    private void copyResults() {
        String bmi = bmiLabel.getText();
        String category = categoryLabel.getText();
        String advice = adviceArea.getText();

        if (!"--".equals(bmi) && !"--".equals(category)) {
            String result = String.format("BMI: %s\n分类: %s\n建议: %s", bmi, category, advice);
            var stringSelection = new java.awt.datatransfer.StringSelection(result);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            // Could show status message here if we had a status label
        }
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this, Texts.HELP_MESSAGE, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }
}