import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🌡️ 温度转换器";

    // 主界面标题
    static final String MAIN_TITLE = "🌡️ 温度单位转换器";

    // 按钮文本
    static final String CONVERT_BUTTON = "转换";
    static final String CLEAR_BUTTON = "清空";
    static final String COPY_BUTTON = "复制结果";
    static final String SWAP_BUTTON = "交换";

    // 面板标题
    static final String INPUT_PANEL_TITLE = "输入温度";
    static final String OUTPUT_PANEL_TITLE = "转换结果";
    static final String CONTROL_PANEL_TITLE = "控制选项";

    // 标签文本
    static final String INPUT_LABEL = "输入温度:";
    static final String FROM_UNIT_LABEL = "从:";
    static final String TO_UNIT_LABEL = "到:";
    static final String RESULT_LABEL = "结果:";
    static final String FORMULA_LABEL = "转换公式:";

    // 温度单位
    static final String UNIT_CELSIUS = "摄氏度 (°C)";
    static final String UNIT_FAHRENHEIT = "华氏度 (°F)";
    static final String UNIT_KELVIN = "开尔文 (K)";
    static final String UNIT_RANKINE = "兰金度 (°R)";
    static final String UNIT_REAUMUR = "列氏度 (°Ré)";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_CONVERTED = "转换完成";
    static final String STATUS_CLEARED = "内容已清空";
    static final String STATUS_COPIED = "结果已复制";
    static final String STATUS_SWAPPED = "单位已交换";

    // 错误消息
    static final String ERROR_INVALID_INPUT = "请输入有效的数字";
    static final String ERROR_OUT_OF_RANGE = "温度值超出有效范围";
    static final String ERROR_COPY_FAILED = "复制失败";

    // 提示信息
    static final String TIP_ABSOLUTE_ZERO = "提示：绝对零度为 -273.15°C";
    static final String TIP_WATER_FREEZING = "水的冰点：0°C = 32°F = 273.15K";
    static final String TIP_WATER_BOILING = "水的沸点：100°C = 212°F = 373.15K";

    // 示例温度
    static final String SAMPLE_TEMPERATURE = "25";

    // 温度范围
    static final String RANGE_CELSIUS = "-273.15 到 1,000,000";
    static final String RANGE_FAHRENHEIT = "-459.67 到 1,832,000";
    static final String RANGE_KELVIN = "0 到 1,000,273.15";

    // 帮助信息
    static final String HELP_MESSAGE = """
        温度转换器使用说明：

        • 支持摄氏度、华氏度、开尔文、兰金度、列氏度之间的相互转换
        • 输入温度值并选择源单位和目标单位
        • 点击"转换"按钮或按回车键进行转换
        • 使用"交换"按钮可以快速交换源单位和目标单位
        • 转换结果可以复制到剪贴板

        温度单位说明：
        • 摄氏度 (°C)：最常用的温度单位
        • 华氏度 (°F)：主要在美国使用
        • 开尔文 (K)：科学计算使用的绝对温度单位
        • 兰金度 (°R)：工程领域使用的绝对温度单位
        • 列氏度 (°Ré)：历史上使用的温度单位

        快捷键：
        Ctrl+C - 转换温度
        Ctrl+L - 清空
        Ctrl+P - 复制结果
        Ctrl+X - 交换单位
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TemperatureConverter().setVisible(true));
}

static class TemperatureConverter extends JFrame {
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
    private final JTextField celsiusField;
    private final JTextField fahrenheitField;
    private final JTextField kelvinField;
    private final JTextField rankineField;
    private final JButton convertButton;
    private final JButton clearButton;
    private final JLabel statusLabel;
    private final JTextArea infoArea;

    private final DecimalFormat df = new DecimalFormat("#.##");

    public TemperatureConverter() {
        celsiusField = new JTextField();
        fahrenheitField = new JTextField();
        kelvinField = new JTextField();
        rankineField = new JTextField();
        convertButton = this.createSuccessButton(Texts.CONVERT_BUTTON);
        clearButton = this.createWarningButton(Texts.CLEAR_BUTTON);
        statusLabel = new JLabel(Texts.STATUS_READY);
        infoArea = new JTextArea();

        initializeGUI();
        setupEventHandlers();
        loadSampleData();
        setupKeyboardShortcuts();
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 创建主面板
        createMainPanel();

        // 创建信息面板
        createInfoPanel();

        // 创建控制面板
        createControlPanel();

        // 创建状态栏
        createStatusBar();

        setSize(500, 400);
        setLocationRelativeTo(null);
    }

    private void createMainPanel() {
        var mainPanel = new JPanel(new GridLayout(4, 2, SPACING_12, SPACING_12));
        mainPanel.setBackground(WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 摄氏度
        var celsiusLabel = new JLabel(Texts.UNIT_CELSIUS + ":");
        celsiusLabel.setFont(HEADLINE);
        celsiusLabel.setForeground(LABEL);
        mainPanel.add(celsiusLabel);

        celsiusField.setFont(new Font("SF Mono", Font.PLAIN, 14));
        celsiusField.setBackground(GRAY6);
        celsiusField.setForeground(LABEL);
        celsiusField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        mainPanel.add(celsiusField);

        // 华氏度
        var fahrenheitLabel = new JLabel(Texts.UNIT_FAHRENHEIT + ":");
        fahrenheitLabel.setFont(HEADLINE);
        fahrenheitLabel.setForeground(LABEL);
        mainPanel.add(fahrenheitLabel);

        fahrenheitField.setFont(new Font("SF Mono", Font.PLAIN, 14));
        fahrenheitField.setBackground(GRAY6);
        fahrenheitField.setForeground(LABEL);
        fahrenheitField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        mainPanel.add(fahrenheitField);

        // 开尔文
        var kelvinLabel = new JLabel(Texts.UNIT_KELVIN + ":");
        kelvinLabel.setFont(HEADLINE);
        kelvinLabel.setForeground(LABEL);
        mainPanel.add(kelvinLabel);

        kelvinField.setFont(new Font("SF Mono", Font.PLAIN, 14));
        kelvinField.setBackground(GRAY6);
        kelvinField.setForeground(LABEL);
        kelvinField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        mainPanel.add(kelvinField);

        // 兰金度
        var rankineLabel = new JLabel(Texts.UNIT_RANKINE + ":");
        rankineLabel.setFont(HEADLINE);
        rankineLabel.setForeground(LABEL);
        mainPanel.add(rankineLabel);

        rankineField.setFont(new Font("SF Mono", Font.PLAIN, 14));
        rankineField.setBackground(GRAY6);
        rankineField.setForeground(LABEL);
        rankineField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        mainPanel.add(rankineField);

        var wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(SYSTEM_BACKGROUND);
        wrapperPanel.add(mainPanel, BorderLayout.NORTH);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        add(wrapperPanel, BorderLayout.NORTH);
    }

    private void createInfoPanel() {
        var infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        var infoTitleLabel = new JLabel(Texts.CONTROL_PANEL_TITLE, SwingConstants.CENTER);
        infoTitleLabel.setFont(TITLE3);
        infoTitleLabel.setForeground(LABEL);
        infoTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_12, 0));
        infoPanel.add(infoTitleLabel, BorderLayout.NORTH);

        infoArea.setFont(new Font("SF Mono", Font.PLAIN, 12));
        infoArea.setEditable(false);
        infoArea.setBackground(GRAY6);
        infoArea.setForeground(LABEL);
        infoArea.setRows(6);
        infoArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
        ));

        var scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(GRAY6);
        infoPanel.add(scrollPane, BorderLayout.CENTER);

        add(infoPanel, BorderLayout.CENTER);
    }

    private void createControlPanel() {
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, SPACING_16));
        controlPanel.setBackground(SYSTEM_BACKGROUND);

        convertButton.addActionListener(this::performConversion);
        clearButton.addActionListener(this::clearAll);

        controlPanel.add(convertButton);
        controlPanel.add(clearButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));

        statusLabel.setFont(FOOTNOTE);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        add(statusPanel, BorderLayout.PAGE_END);
    }

    private void setupEventHandlers() {
        convertButton.addActionListener(this::performConversion);
        clearButton.addActionListener(this::clearAll);

        // 为每个输入框添加回车键监听 - 使用方法引用
        celsiusField.addActionListener(this::performConversion);
        fahrenheitField.addActionListener(this::performConversion);
        kelvinField.addActionListener(this::performConversion);
        rankineField.addActionListener(this::performConversion);
    }

    private void loadSampleData() {
        celsiusField.setText(Texts.SAMPLE_TEMPERATURE);
        performConversion(null);
    }

    private void performConversion(ActionEvent e) {
        try {
            double celsius = 0;
            var sourceUnit = "";

            // 确定输入源并转换为摄氏度
            if (!celsiusField.getText().trim().isEmpty()) {
                celsius = Double.parseDouble(celsiusField.getText().trim());
                sourceUnit = "摄氏度";
            } else if (!fahrenheitField.getText().trim().isEmpty()) {
                var fahrenheit = Double.parseDouble(fahrenheitField.getText().trim());
                celsius = (fahrenheit - 32) * 5.0 / 9.0;
                sourceUnit = "华氏度";
            } else if (!kelvinField.getText().trim().isEmpty()) {
                var kelvin = Double.parseDouble(kelvinField.getText().trim());
                celsius = kelvin - 273.15;
                sourceUnit = "开尔文";
            } else if (!rankineField.getText().trim().isEmpty()) {
                var rankine = Double.parseDouble(rankineField.getText().trim());
                celsius = (rankine - 491.67) * 5.0 / 9.0;
                sourceUnit = "兰金度";
            } else {
                statusLabel.setText(Texts.ERROR_INVALID_INPUT);
                return;
            }

            // 计算所有温度单位
            var fahrenheit = celsius * 9.0 / 5.0 + 32;
            var kelvin = celsius + 273.15;
            var rankine = (celsius + 273.15) * 9.0 / 5.0;

            // 更新所有字段
            celsiusField.setText(df.format(celsius));
            fahrenheitField.setText(df.format(fahrenheit));
            kelvinField.setText(df.format(kelvin));
            rankineField.setText(df.format(rankine));

            // 更新信息面板
            updateTemperatureInfo(celsius);

            statusLabel.setText(Texts.STATUS_CONVERTED + " - 源: " + sourceUnit);

        } catch (NumberFormatException ex) {
            statusLabel.setText(Texts.ERROR_INVALID_INPUT);
            JOptionPane.showMessageDialog(this,
                Texts.ERROR_INVALID_INPUT,
                "格式错误",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            statusLabel.setText(Texts.ERROR_OUT_OF_RANGE);
            JOptionPane.showMessageDialog(this,
                Texts.ERROR_OUT_OF_RANGE + ": " + ex.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTemperatureInfo(double celsius) {
        var info = new StringBuilder();
        info.append("=== 温度转换结果 ===\n\n");

        info.append("摄氏度: ").append(df.format(celsius)).append(" °C\n");
        info.append("华氏度: ").append(df.format(celsius * 9.0 / 5.0 + 32)).append(" °F\n");
        info.append("开尔文: ").append(df.format(celsius + 273.15)).append(" K\n");
        info.append("兰金度: ").append(df.format((celsius + 273.15) * 9.0 / 5.0)).append(" °R\n\n");

        info.append("=== 温度参考 ===\n");

        // 添加一些有趣的温度参考点
        if (celsius <= -273.15) {
            info.append("⚠️ 低于绝对零度！");
        } else if (celsius < -40) {
            info.append("🥶 极地温度");
        } else if (celsius < 0) {
            info.append("❄️ 冰点以下");
        } else if (celsius == 0) {
            info.append("🧊 水的冰点");
        } else if (celsius < 10) {
            info.append("🌨️ 寒冷");
        } else if (celsius < 20) {
            info.append("😊 凉爽");
        } else if (celsius < 30) {
            info.append("🌤️ 舒适");
        } else if (celsius < 40) {
            info.append("🌞 温暖");
        } else if (celsius < 50) {
            info.append("🔥 炎热");
        } else if (celsius >= 100) {
            info.append("💨 水的沸点");
        } else {
            info.append("🌡️ 高温");
        }

        info.append("\n\n=== 常见温度参考 ===\n");
        info.append("人体体温: 37°C (98.6°F)\n");
        info.append("室温: 20-25°C (68-77°F)\n");
        info.append("水的冰点: 0°C (32°F)\n");
        info.append("水的沸点: 100°C (212°F)\n");
        info.append("绝对零度: -273.15°C (-459.67°F)");

        infoArea.setText(info.toString());
    }

    private void clearAll(ActionEvent e) {
        celsiusField.setText("");
        fahrenheitField.setText("");
        kelvinField.setText("");
        rankineField.setText("");
        infoArea.setText("");
        statusLabel.setText(Texts.STATUS_CLEARED);
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

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_C:
                        // C键转换温度
                        if (ev.isControlDown()) {
                            performConversion(null);
                        }
                        break;
                    case KeyEvent.VK_L:
                        // L键清空
                        if (ev.isControlDown()) {
                            clearAll(null);
                        }
                        break;
                    case KeyEvent.VK_P:
                        // P键复制结果
                        if (ev.isControlDown()) {
                            copyResults();
                        }
                        break;
                    case KeyEvent.VK_X:
                        // X键交换单位
                        if (ev.isControlDown()) {
                            swapUnits();
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
        String celsius = celsiusField.getText();
        String fahrenheit = fahrenheitField.getText();
        String kelvin = kelvinField.getText();
        String rankine = rankineField.getText();

        if (!celsius.isEmpty() || !fahrenheit.isEmpty() || !kelvin.isEmpty() || !rankine.isEmpty()) {
            String result = String.format("温度转换结果:\n摄氏度: %s°C\n华氏度: %s°F\n开尔文: %sK\n兰金度: %s°R",
                celsius, fahrenheit, kelvin, rankine);
            var stringSelection = new java.awt.datatransfer.StringSelection(result);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            statusLabel.setText(Texts.STATUS_COPIED);
        }
    }

    private void swapUnits() {
        // 交换摄氏度和华氏度的值
        String celsius = celsiusField.getText();
        String fahrenheit = fahrenheitField.getText();
        celsiusField.setText(fahrenheit);
        fahrenheitField.setText(celsius);
        statusLabel.setText(Texts.STATUS_SWAPPED);
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this, Texts.HELP_MESSAGE, "帮助", JOptionPane.INFORMATION_MESSAGE);
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