import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Calculator().setVisible(true));
}

static class Calculator extends JFrame {
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
    private StringBuilder currentExpression; // 当前显示的表达式
    private double currentValue = 0;
    private double previousValue = 0;
    private String currentOperator = "";
    private boolean startNewNumber = true;
    private boolean hasDecimal = false;
    private boolean justCalculated = false; // 刚刚计算完成
    private final DecimalFormat df = new DecimalFormat("#.##########");

    public Calculator() {
        this.initializeGUI();
        this.setupKeyboardShortcuts();
    }

    private void initializeGUI() {
        setTitle("🧮 计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 主面板 - 使用设计系统颜色
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_12, SPACING_12, SPACING_12, SPACING_12));

        // 创建显示屏 - 苹果风格显示完整表达式
        display = new JTextField("0");
        display.setFont(new Font("SF Pro Display", Font.PLAIN, 48)); // 稍小字体以适应长表达式
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(BLACK);
        display.setForeground(WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(SPACING_32, SPACING_20, SPACING_20, SPACING_20));
        display.setPreferredSize(new Dimension(340, 120));

        // 创建按钮面板
        var buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(BLACK);
        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3, 3, 3, 3);

        // 按钮布局 - 苹果计算器标准布局
        var buttonLabels = new String[][]{
            {"AC", "±", "%", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "−"},
            {"1", "2", "3", "+"},
            {"0", "0_spacer", ".", "="}
        };

        // 创建按钮
        for (int i = 0; i < buttonLabels.length; i++) {
            for (int j = 0; j < buttonLabels[i].length; j++) {
                String label = buttonLabels[i][j];
                if (label.equals("0_spacer")) continue;

                var button = this.createStyledButton(label);

                gbc.gridx = j;
                gbc.gridy = i;
                gbc.gridwidth = label.equals("0") ? 2 : 1;
                gbc.gridheight = 1;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;

                buttonPanel.add(button, gbc);
            }
        }

        // 布局
        mainPanel.add(display, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);

        // 设置窗口
        setSize(360, 520);
        setLocationRelativeTo(null);

        // 初始化表达式
        currentExpression = new StringBuilder("0");
    }

    private JButton createStyledButton(String text) {
        var button = new JButton(text);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 28));
        button.addActionListener((ev) -> this.handleButtonClick(text));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(80, 80));
        button.setOpaque(true);

        // 按钮颜色设置
        Color backgroundColor, textColor, hoverColor;
        if (this.isOperator(text)) {
            backgroundColor = ORANGE;
            textColor = WHITE;
            hoverColor = new Color(255, 179, 64);
        } else if (text.equals("AC") || text.equals("±") || text.equals("%")) {
            backgroundColor = new Color(165, 165, 165);
            textColor = BLACK;
            hoverColor = new Color(200, 200, 200);
        } else {
            backgroundColor = new Color(51, 51, 51);
            textColor = WHITE;
            hoverColor = new Color(80, 80, 80);
        }

        button.setBackground(backgroundColor);
        button.setForeground(textColor);

        // 添加悬停效果
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

    private void handleButtonClick(String command) {
        try {
            switch (command) {
                case "AC" -> this.handleClear();
                case "±" -> this.handlePlusMinus();
                case "%" -> this.handlePercent();
                case "=" -> this.handleEquals();
                case "+", "−", "×", "÷" -> this.handleOperator(command);
                default -> {
                    if (command.matches("[0-9.]")) {
                        this.handleNumber(command);
                    }
                }
            }
        } catch (Exception ex) {
            display.setText("错误");
            this.resetState();
        }
    }

    private void handleNumber(String num) {
        // 如果刚刚计算完成，开始新的计算
        if (justCalculated) {
            currentExpression = new StringBuilder("0");
            justCalculated = false;
            currentOperator = "";
            previousValue = 0;
        }

        // 处理小数点
        if (num.equals(".")) {
            if (!hasDecimal) {
                currentExpression.append(num);
                hasDecimal = true;
            }
        } else {
            // 处理数字
            if (currentExpression.toString().equals("0")) {
                currentExpression = new StringBuilder(num);
            } else {
                // 如果上一个字符是运算符，添加新数字
                var lastChar = currentExpression.charAt(currentExpression.length() - 1);
                if (this.isOperator(String.valueOf(lastChar))) {
                    currentExpression.append(num);
                } else {
                    // 继续当前数字
                    currentExpression.append(num);
                }
            }
        }

        display.setText(currentExpression.toString());
        startNewNumber = false;
    }

    private void handleOperator(String op) {
        // 如果刚刚计算完成，使用结果作为新的起始值
        if (justCalculated) {
            justCalculated = false;
            currentExpression = new StringBuilder(display.getText());
        }

        // 如果表达式以运算符结尾，替换它
        if (currentExpression.length() > 0) {
            var lastChar = currentExpression.charAt(currentExpression.length() - 1);
            if (this.isOperator(String.valueOf(lastChar))) {
                currentExpression.setCharAt(currentExpression.length() - 1, op.charAt(0));
            } else {
                currentExpression.append(op);
            }
        }

        display.setText(currentExpression.toString());
        currentOperator = op;
        startNewNumber = true;
        hasDecimal = false;
    }

    private void handleEquals() {
        if (currentExpression.length() == 0) return;

        try {
            // 解析并计算表达式
            var result = this.evaluateExpression(currentExpression.toString());

            // 格式化结果
            var resultText = this.formatResult(result);
            display.setText(resultText);

            // 设置状态
            currentExpression = new StringBuilder(resultText);
            justCalculated = true;
            startNewNumber = true;
            hasDecimal = resultText.contains(".");
            currentOperator = "";

        } catch (Exception e) {
            display.setText("错误");
            this.resetState();
        }
    }

    private double evaluateExpression(String expression) {
        // 简单的表达式解析和计算
        // 移除空格
        expression = expression.replaceAll("\\s+", "");

        if (expression.isEmpty()) return 0;

        // 解析数字和运算符
        var numbers = new ArrayList<Double>();
        var operators = new ArrayList<Character>();

        var i = 0;
        while (i < expression.length()) {
            var ch = expression.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                // 解析数字
                var numStr = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numStr.append(expression.charAt(i));
                    i++;
                }
                numbers.add(Double.parseDouble(numStr.toString()));
            } else if (this.isOperator(String.valueOf(ch))) {
                operators.add(ch);
                i++;
            } else {
                i++;
            }
        }

        if (numbers.isEmpty()) return 0;

        // 执行计算
        var result = numbers.get(0);
        for (int j = 0; j < operators.size(); j++) {
            var operator = operators.get(j);
            var nextNumber = numbers.get(j + 1);

            result = switch (operator) {
                case '+' -> result + nextNumber;
                case '−' -> result - nextNumber;
                case '×' -> result * nextNumber;
                case '÷' -> result / nextNumber;
                default -> result;
            };
        }

        return result;
    }

    private String formatResult(double result) {
        var resultText = df.format(result);
        // 移除不必要的.0结尾
        if (resultText.endsWith(".0")) {
            resultText = resultText.substring(0, resultText.length() - 2);
        }
        return resultText;
    }

    private void handleClear() {
        display.setText("0");
        currentExpression = new StringBuilder("0");
        this.resetState();
    }

    private void handlePlusMinus() {
        if (justCalculated) return;

        try {
            // 找到当前数字并取反
            var expression = currentExpression.toString();
            var lastNumber = this.extractLastNumber(expression);
            var negatedNumber = -lastNumber;

            // 替换最后一个数字
            var newExpression = this.replaceLastNumber(expression, negatedNumber);
            currentExpression = new StringBuilder(newExpression);
            display.setText(newExpression);

        } catch (Exception e) {
            // 如果无法解析，忽略操作
        }
    }

    private void handlePercent() {
        if (justCalculated) return;

        try {
            // 找到当前数字并转换为百分比
            var expression = currentExpression.toString();
            var lastNumber = this.extractLastNumber(expression);
            var percentNumber = lastNumber / 100;

            // 替换最后一个数字
            var newExpression = this.replaceLastNumber(expression, percentNumber);
            currentExpression = new StringBuilder(newExpression);
            display.setText(newExpression);

        } catch (Exception e) {
            // 如果无法解析，忽略操作
        }
    }

    private double extractLastNumber(String expression) {
        // 从表达式末尾提取最后一个数字
        var i = expression.length() - 1;
        while (i >= 0 && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
            i--;
        }

        if (i < expression.length() - 1) {
            return Double.parseDouble(expression.substring(i + 1));
        }

        return 0;
    }

    private String replaceLastNumber(String expression, double newNumber) {
        var newNumberStr = this.formatResult(newNumber);

        // 找到最后一个数字的位置
        var i = expression.length() - 1;
        while (i >= 0 && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
            i--;
        }

        if (i < expression.length() - 1) {
            return expression.substring(0, i + 1) + newNumberStr;
        } else {
            return newNumberStr;
        }
    }

    private void resetState() {
        currentValue = 0;
        previousValue = 0;
        currentOperator = "";
        startNewNumber = true;
        hasDecimal = false;
        justCalculated = false;
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_C:
                        // C键清除
                        if (ev.isControlDown()) {
                            handleButtonClick("AC");
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_Z:
                        // Z键撤销
                        if (ev.isControlDown()) {
                            handleButtonClick("DEL");
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_H:
                        // H键显示帮助
                        if (ev.isControlDown()) {
                            showHelp();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_F1:
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

    private void showHelp() {
        String helpMessage = """
            科学计算器使用说明：

            • 基本运算：支持加、减、乘、除四则运算
            • 高级运算：支持平方、开方、百分比、倒数等
            • 内存功能：MC(清除内存)、MR(读取内存)、MS(存储内存)、M+(内存加)、M-(内存减)
            • 清除功能：C(清除当前)、DEL(删除最后一位)

            运算说明：
            • 平方(x²)：计算当前数值的平方
            • 开方(√)：计算当前数值的平方根
            • 百分比(%)：将当前数值转换为百分比
            • 倒数(1/x)：计算当前数值的倒数

            内存功能：
            • MC：清除内存中的数值
            • MR：读取内存中的数值
            • MS：将当前数值存储到内存
            • M+：将当前数值加到内存
            • M-：从内存中减去当前数值

            快捷键：
            Ctrl+C - 清除
            Ctrl+Z - 撤销
            Ctrl+H - 显示帮助
            F1 - 显示帮助
            """;
        JOptionPane.showMessageDialog(this, helpMessage, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 圆角边框类
     */
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            var g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
    }
}
