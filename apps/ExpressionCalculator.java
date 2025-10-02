import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Stack;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ExpressionCalculator().setVisible(true));
}

static class ExpressionCalculator extends JFrame {
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
        static final String WINDOW_TITLE = "🧮 表达式计算器";

        // 主界面文本
        static final String MAIN_TITLE = "🧮 表达式计算器";
        static final String EXPRESSION_LABEL = "表达式:";
        static final String RESULT_LABEL = "结果:";
        static final String CALCULATE_BUTTON = "计算";
        static final String CLEAR_BUTTON = "清除";
        static final String HELP_TEXT = "<html><small>支持: +, -, *, /, (, ), 数字<br>示例: 2 * (3 + 4) - 5</small></html>";
        static final String HISTORY_TITLE = "计算历史";
        static final String INPUT_TITLE = "表达式计算器";

        // 状态消息
        static final String STATUS_READY = "就绪";
        static final String STATUS_CALCULATED = "计算完成";
        static final String STATUS_CLEARED = "已清除";
        static final String STATUS_EMPTY_EXPRESSION = "请输入表达式";
        static final String STATUS_CALCULATION_ERROR = "计算错误: ";

        // 错误消息
        static final String ERROR_INVALID_EXPRESSION = "无效表达式";
        static final String ERROR_DIVISION_BY_ZERO = "除数为零";
        static final String ERROR_UNKNOWN_OPERATOR = "未知运算符: ";
        static final String ERROR_RESULT = "错误";

        // 示例表达式
        static final String SAMPLE_EXPRESSION = "2 * (3 + 4) - 5";
    }

    // ===== 应用状态 =====
    private final JTextField expressionField;
    private final JTextField resultField;
    private JButton calculateButton;
    private JButton clearButton;
    private final JTextArea historyArea;
    private final JLabel statusLabel;

    public ExpressionCalculator() {
        expressionField = new JTextField();
        resultField = new JTextField();
        historyArea = new JTextArea();
        statusLabel = new JLabel(Texts.STATUS_READY);

        this.initializeGUI();
        this.setupEventHandlers();
        this.loadSampleData();
        this.setupKeyboardShortcuts();
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 创建主面板
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel(Texts.MAIN_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_24, 0));

        // 创建输入面板
        var inputPanel = this.createInputPanel();

        // 创建历史面板
        var historyPanel = this.createHistoryPanel();

        // 创建控制面板
        var controlPanel = this.createControlPanel();

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(historyPanel, BorderLayout.SOUTH);
        mainPanel.add(controlPanel, BorderLayout.PAGE_END);

        add(mainPanel, BorderLayout.CENTER);

        // 状态栏 - 使用苹果风格
        var statusPanel = this.createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        setSize(600, 500);
        setLocationRelativeTo(null);
    }

    private JPanel createInputPanel() {
        var inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(SYSTEM_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder(Texts.INPUT_TITLE)),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 表达式输入
        var exprPanel = new JPanel(new BorderLayout());
        exprPanel.setBackground(SYSTEM_BACKGROUND);
        var exprLabel = new JLabel(Texts.EXPRESSION_LABEL);
        exprLabel.setFont(HEADLINE);
        exprLabel.setForeground(LABEL);
        exprPanel.add(exprLabel, BorderLayout.WEST);

        expressionField.setFont(new Font("SF Mono", Font.PLAIN, 16));
        expressionField.setBackground(GRAY6);
        expressionField.setForeground(LABEL);
        expressionField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        exprPanel.add(expressionField, BorderLayout.CENTER);

        // 结果显示
        var resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(SYSTEM_BACKGROUND);
        var resultLabel = new JLabel(Texts.RESULT_LABEL);
        resultLabel.setFont(HEADLINE);
        resultLabel.setForeground(LABEL);
        resultPanel.add(resultLabel, BorderLayout.WEST);

        resultField.setFont(new Font("SF Mono", Font.BOLD, 16));
        resultField.setEditable(false);
        resultField.setBackground(GRAY6);
        resultField.setForeground(BLUE);
        resultField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        resultPanel.add(resultField, BorderLayout.CENTER);

        inputPanel.add(exprPanel);
        inputPanel.add(Box.createVerticalStrut(SPACING_16));
        inputPanel.add(resultPanel);

        // 添加说明
        var helpLabel = new JLabel(Texts.HELP_TEXT);
        helpLabel.setFont(CAPTION1);
        helpLabel.setForeground(SECONDARY_LABEL);
        inputPanel.add(Box.createVerticalStrut(SPACING_8));
        inputPanel.add(helpLabel);

        return inputPanel;
    }

    private JPanel createHistoryPanel() {
        var historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(SYSTEM_BACKGROUND);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder(Texts.HISTORY_TITLE)),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        historyArea.setFont(new Font("SF Mono", Font.PLAIN, 14));
        historyArea.setEditable(false);
        historyArea.setBackground(GRAY6);
        historyArea.setForeground(LABEL);
        historyArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));

        var scrollPane = new JScrollPane(historyArea);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(GRAY6);

        historyPanel.add(scrollPane, BorderLayout.CENTER);

        return historyPanel;
    }

    private JPanel createControlPanel() {
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_12, 0));
        controlPanel.setBackground(SYSTEM_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, 0, 0, 0));

        calculateButton = this.createPrimaryButton("Calculate");
        clearButton = this.createSecondaryButton("Clear");

        controlPanel.add(calculateButton);
        controlPanel.add(clearButton);

        return controlPanel;
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
    private void setupEventHandlers() {
        calculateButton.addActionListener((ev) -> this.calculateExpression(ev));
        clearButton.addActionListener((ev) -> this.clearAll(ev));
        expressionField.addActionListener((ev) -> this.calculateExpression(ev));
    }

    private void loadSampleData() {
        expressionField.setText(Texts.SAMPLE_EXPRESSION);
    }

    private void calculateExpression(ActionEvent e) {
        var expression = expressionField.getText().trim();
        if (expression.isEmpty()) {
            statusLabel.setText(Texts.STATUS_EMPTY_EXPRESSION);
            return;
        }

        try {
            var result = evaluateExpression(expression);
            resultField.setText(String.valueOf(result));

            // Add to history
            var historyEntry = expression + " = " + result + "\n";
            historyArea.append(historyEntry);
            historyArea.setCaretPosition(historyArea.getDocument().getLength());

            statusLabel.setText(Texts.STATUS_CALCULATED);

        } catch (Exception ex) {
            resultField.setText(Texts.ERROR_RESULT);
            statusLabel.setText(Texts.STATUS_CALCULATION_ERROR + ex.getMessage());
        }
    }

    private double evaluateExpression(String expression) throws Exception {
        // Remove spaces
        expression = expression.replaceAll("\\s+", "");

        // Validate expression
        if (!isValidExpression(expression)) {
            throw new Exception(Texts.ERROR_INVALID_EXPRESSION);
        }

        // Use double-stack algorithm to calculate expression
        var numbers = new Stack<Double>();
        var operators = new Stack<Character>();

        for (int i = 0; i < expression.length(); i++) {
            var ch = expression.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                // Parse number
                var numStr = new StringBuilder();
                while (i < expression.length() &&
                       (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numStr.append(expression.charAt(i));
                    i++;
                }
                i--; // Step back
                numbers.push(Double.parseDouble(numStr.toString()));

            } else if (ch == '(') {
                operators.push(ch);

            } else if (ch == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop(); // Remove '('

            } else if (isOperator(ch)) {
                while (!operators.isEmpty() && hasPrecedence(ch, operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
        }

        // Process remaining operators
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean isValidExpression(String expression) {
        // Simple expression validation
        if (expression.isEmpty()) return false;

        var parentheses = 0;
        for (var ch : expression.toCharArray()) {
            if (ch == '(') {
                parentheses++;
            } else if (ch == ')') {
                parentheses--;
                if (parentheses < 0) return false;
            } else if (!Character.isDigit(ch) && ch != '.' && !isOperator(ch)) {
                return false;
            }
        }

        return parentheses == 0;
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private double applyOperation(char op, double b, double a) throws Exception {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new Exception(Texts.ERROR_DIVISION_BY_ZERO);
                yield a / b;
            }
            default -> throw new Exception(Texts.ERROR_UNKNOWN_OPERATOR + op);
        };
    }

    private void clearAll(ActionEvent e) {
        expressionField.setText("");
        resultField.setText("");
        historyArea.setText("");
        statusLabel.setText(Texts.STATUS_CLEARED);
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_ENTER:
                        // 回车键计算
                        calculateExpression(new ActionEvent(ExpressionCalculator.this, ActionEvent.ACTION_PERFORMED, "calculate"));
                        break;
                    case java.awt.event.KeyEvent.VK_ESCAPE:
                        // ESC键清除
                        clearAll(new ActionEvent(ExpressionCalculator.this, ActionEvent.ACTION_PERFORMED, "clear"));
                        break;
                    case java.awt.event.KeyEvent.VK_C:
                        // C键清除（如果按下Ctrl+C则复制）
                        if (ev.isControlDown()) {
                            // 让系统处理复制
                            return;
                        } else {
                            clearAll(new ActionEvent(ExpressionCalculator.this, ActionEvent.ACTION_PERFORMED, "clear"));
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_H:
                        // H键显示/隐藏历史记录
                        if (historyArea.isVisible()) {
                            historyArea.setVisible(false);
                            statusLabel.setText("历史记录已隐藏");
                        } else {
                            historyArea.setVisible(true);
                            statusLabel.setText("历史记录已显示");
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