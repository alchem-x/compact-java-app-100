import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ScientificCalculator().setVisible(true));
}

static class ScientificCalculator extends JFrame {
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

    // ===== 应用状态 =====
    private JTextField display;
    private double num1 = 0, num2 = 0, result = 0;
    private char operator;
    private boolean start = true;
    private final DecimalFormat df = new DecimalFormat("#.##########");
    private boolean isRadians = true;

    public ScientificCalculator() {
        this.initializeGUI();
    }

    private void initializeGUI() {
        setTitle("🔬 科学计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 主面板 - 使用设计系统颜色
        var mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_12, SPACING_12, SPACING_12, SPACING_12));

        // 创建显示屏 - 苹果风格
        display = new JTextField("0");
        display.setFont(new Font("SF Pro Display", Font.PLAIN, 40));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(BLACK);
        display.setForeground(WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(SPACING_20, SPACING_20, SPACING_20, SPACING_20));
        display.setPreferredSize(new Dimension(480, 80));

        // 创建按钮面板
        var buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(BLACK);
        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(SPACING_4, SPACING_4, SPACING_4, SPACING_4);

        // 科学计算器按钮布局
        var buttonLabels = new String[][]{
            {"(", ")", "mc", "m+", "m-", "mr", "AC", "±", "%", "÷"},
            {"2nd", "x²", "x³", "xʸ", "eˣ", "10ˣ", "7", "8", "9", "×"},
            {"1/x", "²√x", "³√x", "ʸ√x", "ln", "log₁₀", "4", "5", "6", "−"},
            {"x!", "sin", "cos", "tan", "e", "EE", "1", "2", "3", "+"},
            {"Rad", "sinh", "cosh", "tanh", "π", "Rand", "0", ".", "="}
        };

        // 创建按钮
        for (int i = 0; i < buttonLabels.length; i++) {
            for (int j = 0; j < buttonLabels[i].length; j++) {
                var label = buttonLabels[i][j];
                var button = this.createStyledButton(label);

                gbc.gridx = j;
                gbc.gridy = i;
                gbc.gridwidth = 1;
                gbc.gridheight = 1;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;

                // 特殊处理0按钮
                if (label.equals("0")) {
                    gbc.gridwidth = 2;
                    buttonPanel.add(button, gbc);
                    j++; // 跳过下一个位置
                } else {
                    buttonPanel.add(button, gbc);
                }
            }
        }

        // 布局
        mainPanel.add(display, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);

        // 设置窗口大小和位置
        setSize(600, 500);
        setLocationRelativeTo(null);

        // 设置背景色
        getContentPane().setBackground(BLACK);
    }

    private JButton createStyledButton(String text) {
        var button = new JButton(text);
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        button.addActionListener((ev) -> this.handleButtonClick(text));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(55, 55));

        // 设置按钮样式
        Color backgroundColor, textColor, hoverColor;
        if (this.isOperator(text)) {
            // 橙色运算符按钮
            backgroundColor = ORANGE;
            textColor = WHITE;
            hoverColor = new Color(255, 179, 64);
        } else if (this.isFunction(text)) {
            // 深蓝色科学函数按钮
            backgroundColor = new Color(48, 48, 48);
            textColor = WHITE;
            hoverColor = new Color(80, 80, 80);
        } else if (text.equals("AC") || text.equals("±") || text.equals("%")) {
            // 浅灰色功能按钮
            backgroundColor = new Color(165, 165, 165);
            textColor = BLACK;
            hoverColor = new Color(200, 200, 200);
        } else {
            // 深灰色数字按钮
            backgroundColor = new Color(51, 51, 51);
            textColor = WHITE;
            hoverColor = new Color(80, 80, 80);
        }

        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setOpaque(true);

        // 添加鼠标悬停效果
        this.setupButtonHoverEffect(button, backgroundColor, hoverColor);

        return button;
    }

    private void setupButtonHoverEffect(JButton button, Color originalColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent ev) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent ev) {
                button.setBackground(originalColor);
            }
        });
    }

    private boolean isOperator(String text) {
        return text.equals("+") || text.equals("−") || text.equals("×") ||
               text.equals("÷") || text.equals("=");
    }

    private boolean isFunction(String text) {
        return text.equals("sin") || text.equals("cos") || text.equals("tan") ||
               text.equals("sinh") || text.equals("cosh") || text.equals("tanh") ||
               text.equals("ln") || text.equals("log₁₀") || text.equals("x²") ||
               text.equals("x³") || text.equals("xʸ") || text.equals("²√x") ||
               text.equals("³√x") || text.equals("ʸ√x") || text.equals("1/x") ||
               text.equals("x!") || text.equals("eˣ") || text.equals("10ˣ") ||
               text.equals("2nd") || text.equals("Rad");
    }

    private void handleButtonClick(String command) {
        try {
            if (command.charAt(0) >= '0' && command.charAt(0) <= '9' || command.equals(".")) {
                this.handleNumber(command);
            } else if (command.equals("AC")) {
                this.handleClear();
            } else if (command.equals("±")) {
                this.handlePlusMinus();
            } else if (command.equals("%")) {
                this.handlePercent();
            } else if (command.equals("=")) {
                this.handleEquals();
            } else if (this.isOperator(command)) {
                this.handleOperator(command.charAt(0));
            } else if (this.isFunction(command)) {
                this.handleFunction(command);
            } else if (command.equals("π")) {
                this.handleConstant(Math.PI);
            } else if (command.equals("e")) {
                this.handleConstant(Math.E);
            } else if (command.equals("Rand")) {
                this.handleConstant(Math.random());
            }
        } catch (Exception ex) {
            display.setText("错误");
            start = true;
        }
    }

    private void handleNumber(String num) {
        if (start) {
            display.setText("");
            start = false;
        }

        var currentText = display.getText();
        if (num.equals(".") && currentText.contains(".")) {
            return;
        }

        display.setText(currentText + num);
    }

    private void handleClear() {
        display.setText("0");
        num1 = 0;
        num2 = 0;
        result = 0;
        operator = ' ';
        start = true;
    }

    private void handlePlusMinus() {
        var temp = Double.parseDouble(display.getText());
        temp = -temp;
        display.setText(df.format(temp));
    }

    private void handlePercent() {
        var temp = Double.parseDouble(display.getText());
        temp = temp / 100;
        display.setText(df.format(temp));
    }

    private void handleConstant(double value) {
        display.setText(df.format(value));
        start = true;
    }

    private void handleFunction(String func) {
        var value = Double.parseDouble(display.getText());
        var result = 0.0;

        result = switch (func) {
            case "sin" -> isRadians ? Math.sin(value) : Math.sin(Math.toRadians(value));
            case "cos" -> isRadians ? Math.cos(value) : Math.cos(Math.toRadians(value));
            case "tan" -> isRadians ? Math.tan(value) : Math.tan(Math.toRadians(value));
            case "sinh" -> Math.sinh(value);
            case "cosh" -> Math.cosh(value);
            case "tanh" -> Math.tanh(value);
            case "ln" -> Math.log(value);
            case "log₁₀" -> Math.log10(value);
            case "x²" -> value * value;
            case "x³" -> value * value * value;
            case "²√x" -> Math.sqrt(value);
            case "³√x" -> Math.cbrt(value);
            case "1/x" -> 1.0 / value;
            case "x!" -> this.factorial(value);
            case "eˣ" -> Math.exp(value);
            case "10ˣ" -> Math.pow(10, value);
            case "Rad" -> {
                isRadians = !isRadians;
                var button = (JButton) ((ActionEvent) null).getSource();
                button.setText(isRadians ? "Rad" : "Deg");
                yield value;
            }
            default -> value;
        };

        if (!func.equals("Rad")) {
            display.setText(df.format(result));
            start = true;
        }
    }

    private double factorial(double n) {
        if (n < 0 || n != Math.floor(n)) {
            throw new IllegalArgumentException("阶乘只能计算非负整数");
        }
        if (n > 170) {
            return Double.POSITIVE_INFINITY;
        }

        var result = 1.0;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private void handleOperator(char op) {
        if (!start) {
            if (operator != ' ') {
                this.handleEquals();
            } else {
                result = Double.parseDouble(display.getText());
            }

            operator = op;
            num1 = result;
            start = true;
        }
    }

    private void handleEquals() {
        if (operator != ' ' && !start) {
            num2 = Double.parseDouble(display.getText());

            result = switch (operator) {
                case '+' -> num1 + num2;
                case '−' -> num1 - num2;
                case '×' -> num1 * num2;
                case '÷' -> {
                    if (num2 != 0) {
                        yield num1 / num2;
                    } else {
                        display.setText("错误");
                        start = true;
                        yield 0.0;
                    }
                }
                default -> result;
            };

            display.setText(df.format(result));
            operator = ' ';
            start = true;
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