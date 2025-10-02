import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new UnitConverter().setVisible(true));
}

static class UnitConverter extends JFrame {
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
    private JComboBox<String> categoryCombo;
    private JComboBox<String> fromUnitCombo;
    private JComboBox<String> toUnitCombo;
    private JTextField inputField;
    private JTextField outputField;
    private Map<String, Map<String, Double>> conversions;
    private final DecimalFormat df = new DecimalFormat("#.##########");

    public UnitConverter() {
        this.initializeConversions();
        this.initializeGUI();
    }

    private void initializeConversions() {
        conversions = new HashMap<>();

        // 长度单位 (以米为基准)
        var length = new HashMap<String, Double>();
        length.put("毫米", 0.001);
        length.put("厘米", 0.01);
        length.put("米", 1.0);
        length.put("千米", 1000.0);
        length.put("英寸", 0.0254);
        length.put("英尺", 0.3048);
        length.put("码", 0.9144);
        length.put("英里", 1609.344);
        conversions.put("长度", length);

        // 重量单位 (以克为基准)
        var weight = new HashMap<String, Double>();
        weight.put("毫克", 0.001);
        weight.put("克", 1.0);
        weight.put("千克", 1000.0);
        weight.put("吨", 1000000.0);
        weight.put("盎司", 28.3495);
        weight.put("磅", 453.592);
        weight.put("石", 6350.29);
        conversions.put("重量", weight);

        // 温度单位 (特殊处理)
        var temperature = new HashMap<String, Double>();
        temperature.put("摄氏度", 1.0);
        temperature.put("华氏度", 1.0);
        temperature.put("开尔文", 1.0);
        conversions.put("温度", temperature);

        // 面积单位 (以平方米为基准)
        var area = new HashMap<String, Double>();
        area.put("平方毫米", 0.000001);
        area.put("平方厘米", 0.0001);
        area.put("平方米", 1.0);
        area.put("平方千米", 1000000.0);
        area.put("平方英寸", 0.00064516);
        area.put("平方英尺", 0.092903);
        area.put("英亩", 4046.86);
        conversions.put("面积", area);

        // 体积单位 (以升为基准)
        var volume = new HashMap<String, Double>();
        volume.put("毫升", 0.001);
        volume.put("升", 1.0);
        volume.put("立方米", 1000.0);
        volume.put("加仑(美)", 3.78541);
        volume.put("加仑(英)", 4.54609);
        volume.put("品脱", 0.473176);
        volume.put("夸脱", 0.946353);
        conversions.put("体积", volume);

        // 速度单位 (以米/秒为基准)
        var speed = new HashMap<String, Double>();
        speed.put("米/秒", 1.0);
        speed.put("千米/时", 0.277778);
        speed.put("英里/时", 0.44704);
        speed.put("节", 0.514444);
        speed.put("马赫", 343.0);
        conversions.put("速度", speed);
    }

    private void initializeGUI() {
        setTitle("🔄 单位转换器");
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
        var titleLabel = new JLabel("🔄 单位转换器", JLabel.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // 类别选择 - 使用苹果风格标签和下拉框
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        var categoryLabel = new JLabel("转换类别:");
        categoryLabel.setFont(HEADLINE);
        categoryLabel.setForeground(LABEL);
        mainPanel.add(categoryLabel, gbc);

        categoryCombo = new JComboBox<>(conversions.keySet().toArray(new String[0]));
        categoryCombo.setFont(BODY);
        categoryCombo.setBackground(WHITE);
        categoryCombo.setForeground(LABEL);
        categoryCombo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        categoryCombo.addActionListener((ev) -> this.updateUnitCombos());
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(categoryCombo, gbc);

        // 输入区域 - 使用苹果风格组件
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        var fromLabel = new JLabel("从:");
        fromLabel.setFont(HEADLINE);
        fromLabel.setForeground(LABEL);
        mainPanel.add(fromLabel, gbc);

        fromUnitCombo = new JComboBox<>();
        fromUnitCombo.setFont(BODY);
        fromUnitCombo.setBackground(WHITE);
        fromUnitCombo.setForeground(LABEL);
        fromUnitCombo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        fromUnitCombo.addActionListener((ev) -> this.convert());
        gbc.gridx = 1;
        mainPanel.add(fromUnitCombo, gbc);

        inputField = new JTextField("1", 15);
        inputField.setFont(BODY);
        inputField.setBackground(GRAY6);
        inputField.setForeground(LABEL);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        inputField.addActionListener((ev) -> this.convert());
        inputField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { convert(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { convert(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { convert(); }
        });
        gbc.gridx = 2;
        mainPanel.add(inputField, gbc);

        // 输出区域
        gbc.gridx = 0; gbc.gridy = 3;
        var toLabel = new JLabel("到:");
        toLabel.setFont(HEADLINE);
        toLabel.setForeground(LABEL);
        mainPanel.add(toLabel, gbc);

        toUnitCombo = new JComboBox<>();
        toUnitCombo.setFont(BODY);
        toUnitCombo.setBackground(WHITE);
        toUnitCombo.setForeground(LABEL);
        toUnitCombo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        toUnitCombo.addActionListener((ev) -> this.convert());
        gbc.gridx = 1;
        mainPanel.add(toUnitCombo, gbc);

        outputField = new JTextField(15);
        outputField.setFont(BODY);
        outputField.setEditable(false);
        outputField.setBackground(GRAY6);
        outputField.setForeground(LABEL);
        outputField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        gbc.gridx = 2;
        mainPanel.add(outputField, gbc);

        // 交换按钮 - 使用苹果风格
        var swapButton = this.createSecondaryButton("⇄ 交换");
        swapButton.addActionListener((ev) -> this.swapUnits());
        gbc.gridx = 1; gbc.gridy = 4;
        mainPanel.add(swapButton, gbc);

        // 清除按钮 - 使用苹果风格
        var clearButton = this.createSecondaryButton("🗑️ 清除");
        clearButton.addActionListener((ev) -> {
            inputField.setText("1");
            outputField.setText("");
        });
        gbc.gridx = 2;
        mainPanel.add(clearButton, gbc);

        // 常用转换快捷按钮面板 - 使用苹果风格
        var shortcutPanel = this.createShortcutPanel();
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(shortcutPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 初始化单位下拉框
        this.updateUnitCombos();

        setSize(500, 450);
        setLocationRelativeTo(null);
    }

    private JPanel createShortcutPanel() {
        var panel = new JPanel();
        panel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder("常用转换")),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        panel.setLayout(new GridLayout(2, 3, SPACING_8, SPACING_8));

        var shortcuts = new String[][]{
            {"长度", "米", "英尺"},
            {"重量", "千克", "磅"},
            {"温度", "摄氏度", "华氏度"},
            {"面积", "平方米", "平方英尺"},
            {"体积", "升", "加仑(美)"},
            {"速度", "千米/时", "英里/时"}
        };

        for (var shortcut : shortcuts) {
            var button = this.createSecondaryButton(shortcut[1] + " → " + shortcut[2]);
            button.setFont(CAPTION1);
            button.addActionListener((ev) -> {
                categoryCombo.setSelectedItem(shortcut[0]);
                fromUnitCombo.setSelectedItem(shortcut[1]);
                toUnitCombo.setSelectedItem(shortcut[2]);
                this.convert();
            });
            panel.add(button);
        }

        return panel;
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
    private void updateUnitCombos() {
        var category = (String) categoryCombo.getSelectedItem();
        if (category != null) {
            var units = conversions.get(category);
            var unitNames = units.keySet().toArray(new String[0]);

            fromUnitCombo.removeAllItems();
            toUnitCombo.removeAllItems();

            for (var unit : unitNames) {
                fromUnitCombo.addItem(unit);
                toUnitCombo.addItem(unit);
            }

            // 设置默认选择
            if (unitNames.length > 1) {
                toUnitCombo.setSelectedIndex(1);
            }

            this.convert();
        }
    }

    private void swapUnits() {
        var fromUnit = (String) fromUnitCombo.getSelectedItem();
        var toUnit = (String) toUnitCombo.getSelectedItem();
        var inputValue = inputField.getText();
        var outputValue = outputField.getText();

        fromUnitCombo.setSelectedItem(toUnit);
        toUnitCombo.setSelectedItem(fromUnit);
        inputField.setText(outputValue.isEmpty() ? "1" : outputValue);

        this.convert();
    }

    private void convert() {
        try {
            var category = (String) categoryCombo.getSelectedItem();
            var fromUnit = (String) fromUnitCombo.getSelectedItem();
            var toUnit = (String) toUnitCombo.getSelectedItem();
            var inputText = inputField.getText().trim();

            if (category == null || fromUnit == null || toUnit == null || inputText.isEmpty()) {
                outputField.setText("");
                return;
            }

            var inputValue = Double.parseDouble(inputText);
            double result;

            if (category.equals("温度")) {
                result = this.convertTemperature(inputValue, fromUnit, toUnit);
            } else {
                var units = conversions.get(category);
                var fromFactor = units.get(fromUnit);
                var toFactor = units.get(toUnit);

                // 转换为基准单位，再转换为目标单位
                var baseValue = inputValue * fromFactor;
                result = baseValue / toFactor;
            }

            outputField.setText(df.format(result));

        } catch (NumberFormatException ex) {
            outputField.setText("输入错误");
        } catch (Exception ex) {
            outputField.setText("转换错误");
        }
    }

    private double convertTemperature(double value, String fromUnit, String toUnit) {
        // 先转换为摄氏度
        double celsius;
        celsius = switch (fromUnit) {
            case "摄氏度" -> value;
            case "华氏度" -> (value - 32) * 5.0 / 9.0;
            case "开尔文" -> value - 273.15;
            default -> throw new IllegalArgumentException("未知的温度单位: " + fromUnit);
        };

        // 再从摄氏度转换为目标单位
        return switch (toUnit) {
            case "摄氏度" -> celsius;
            case "华氏度" -> celsius * 9.0 / 5.0 + 32;
            case "开尔文" -> celsius + 273.15;
            default -> throw new IllegalArgumentException("未知的温度单位: " + toUnit);
        };
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