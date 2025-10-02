import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "📋 JSON格式化工具";

    // 面板标题
    static final String INPUT_PANEL_TITLE = "输入JSON";
    static final String OUTPUT_PANEL_TITLE = "格式化结果";

    // 按钮文本
    static final String FORMAT_BUTTON = "格式化";
    static final String MINIFY_BUTTON = "压缩";
    static final String VALIDATE_BUTTON = "验证";
    static final String COPY_BUTTON = "复制结果";
    static final String CLEAR_BUTTON = "清空";

    // 按钮提示
    static final String FORMAT_TOOLTIP = "格式化JSON，增加缩进和换行";
    static final String MINIFY_TOOLTIP = "压缩JSON，移除空白字符";
    static final String VALIDATE_TOOLTIP = "验证JSON格式是否正确";
    static final String COPY_TOOLTIP = "复制格式化结果到剪贴板";
    static final String CLEAR_TOOLTIP = "清空输入和输出";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_PLEASE_ENTER_JSON = "请输入JSON内容";
    static final String STATUS_FORMAT_COMPLETE = "格式化完成";
    static final String STATUS_FORMAT_FAILED = "格式化失败";
    static final String STATUS_MINIFY_COMPLETE = "压缩完成";
    static final String STATUS_MINIFY_FAILED = "压缩失败";
    static final String STATUS_VALIDATE_PASSED = "验证通过";
    static final String STATUS_VALIDATE_FAILED = "验证失败";
    static final String STATUS_NO_CONTENT_TO_COPY = "没有可复制的内容";
    static final String STATUS_COPIED_TO_CLIPBOARD = "已复制到剪贴板";
    static final String STATUS_CLEARED = "已清空";

    // 错误消息
    static final String ERROR_FORMAT_FAILED = "格式化失败: ";
    static final String ERROR_MINIFY_FAILED = "压缩失败: ";
    static final String ERROR_JSON_INCOMPLETE = "JSON不完整，缺少结束符";
    static final String ERROR_STRING_NOT_CLOSED = "字符串未正确结束";
    static final String ERROR_UNEXPECTED_CHAR = "位置 ";
    static final String ERROR_EXPECTED_BUT_FOUND = ": 期望 '";
    static final String ERROR_BUT_FOUND = "' 但找到 '";
    static final String ERROR_QUOTE = "'";

    // 验证结果
    static final String VALIDATE_SUCCESS_PREFIX = "✅ JSON格式正确！\n\n";
    static final String VALIDATE_ERROR_PREFIX = "❌ JSON格式错误:\n";

    // JSON信息
    static final String JSON_INFO_HEADER = "JSON信息:\n";
    static final String JSON_INFO_TOTAL_CHARS = "- 总字符数: ";
    static final String JSON_INFO_OBJECTS = "- 对象数量: ";
    static final String JSON_INFO_ARRAYS = "- 数组数量: ";
    static final String JSON_INFO_STRINGS = "- 字符串数量: ";

    // 示例JSON
    static final String SAMPLE_JSON = """
        {"name":"张三","age":25,"city":"北京","hobbies":["读书","游泳","编程"],"address":{"street":"中关村大街","number":123,"zipcode":"100080"},"married":false,"salary":null}
        """;

    // 缩进
    static final String INDENT = "  "; // 2个空格
}

// 设计系统常量
static class DesignSystem {
    // 主要颜色
    static final Color BLUE = new Color(0, 122, 255);
    static final Color GREEN = new Color(52, 199, 89);
    static final Color RED = new Color(255, 59, 48);
    static final Color ORANGE = new Color(255, 149, 0);
    static final Color PURPLE = new Color(175, 82, 222);
    static final Color TEAL = new Color(48, 176, 199);

    // 中性色
    static final Color BLACK = new Color(0, 0, 0);
    static final Color WHITE = new Color(255, 255, 255);
    static final Color GRAY = new Color(142, 142, 147);
    static final Color GRAY2 = new Color(174, 174, 178);
    static final Color GRAY3 = new Color(199, 199, 204);
    static final Color GRAY4 = new Color(209, 209, 214);
    static final Color GRAY5 = new Color(229, 229, 234);
    static final Color GRAY6 = new Color(242, 242, 247);

    // 语义颜色
    static final Color LABEL = new Color(0, 0, 0);
    static final Color SECONDARY_LABEL = new Color(60, 60, 67, 153);
    static final Color TERTIARY_LABEL = new Color(60, 60, 67, 76);

    // 背景
    static final Color SYSTEM_BACKGROUND = new Color(255, 255, 255);
    static final Color SECONDARY_SYSTEM_BACKGROUND = new Color(242, 242, 247);

    // 字体
    static final Font LARGE_TITLE = new Font("SF Pro Display", Font.BOLD, 34);
    static final Font TITLE1 = new Font("SF Pro Display", Font.BOLD, 28);
    static final Font TITLE2 = new Font("SF Pro Display", Font.BOLD, 22);
    static final Font TITLE3 = new Font("SF Pro Display", Font.BOLD, 20);
    static final Font HEADLINE = new Font("SF Pro Display", Font.BOLD, 17);
    static final Font SUBHEADLINE = new Font("SF Pro Display", Font.PLAIN, 15);
    static final Font BODY = new Font("SF Pro Text", Font.PLAIN, 17);
    static final Font CALLOUT = new Font("SF Pro Text", Font.PLAIN, 16);
    static final Font FOOTNOTE = new Font("SF Pro Text", Font.PLAIN, 13);
    static final Font CAPTION1 = new Font("SF Pro Text", Font.PLAIN, 12);
    static final Font CAPTION2 = new Font("SF Pro Text", Font.PLAIN, 11);
    static final Font MONO = new Font("SF Mono", Font.PLAIN, 12);

    // 间距
    static final int SPACING_2 = 2;
    static final int SPACING_4 = 4;
    static final int SPACING_8 = 8;
    static final int SPACING_12 = 12;
    static final int SPACING_16 = 16;
    static final int SPACING_20 = 20;
    static final int SPACING_24 = 24;
    static final int SPACING_32 = 32;

    // 圆角
    static final int RADIUS_4 = 4;
    static final int RADIUS_8 = 8;
    static final int RADIUS_12 = 12;
    static final int RADIUS_16 = 16;
    static final int RADIUS_20 = 20;

    // 按钮尺寸
    static final Dimension BUTTON_REGULAR = new Dimension(120, 44);
    static final Dimension BUTTON_LARGE = new Dimension(160, 50);
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new JsonFormatter().setVisible(true);
    });
}

static class JsonFormatter extends JFrame {
    // 设计系统常量快捷引用
    private static final Color BLUE = DesignSystem.BLUE;
    private static final Color GREEN = DesignSystem.GREEN;
    private static final Color RED = DesignSystem.RED;
    private static final Color ORANGE = DesignSystem.ORANGE;
    private static final Color PURPLE = DesignSystem.PURPLE;
    private static final Color TEAL = DesignSystem.TEAL;
    private static final Color BLACK = DesignSystem.BLACK;
    private static final Color WHITE = DesignSystem.WHITE;
    private static final Color GRAY = DesignSystem.GRAY;
    private static final Color GRAY2 = DesignSystem.GRAY2;
    private static final Color GRAY3 = DesignSystem.GRAY3;
    private static final Color GRAY4 = DesignSystem.GRAY4;
    private static final Color GRAY5 = DesignSystem.GRAY5;
    private static final Color GRAY6 = DesignSystem.GRAY6;
    private static final Color LABEL = DesignSystem.LABEL;
    private static final Color SECONDARY_LABEL = DesignSystem.SECONDARY_LABEL;
    private static final Color TERTIARY_LABEL = DesignSystem.TERTIARY_LABEL;
    private static final Color SYSTEM_BACKGROUND = DesignSystem.SYSTEM_BACKGROUND;
    private static final Color SECONDARY_SYSTEM_BACKGROUND = DesignSystem.SECONDARY_SYSTEM_BACKGROUND;
    private static final Font HEADLINE = DesignSystem.HEADLINE;
    private static final Font BODY = DesignSystem.BODY;
    private static final Font CALLOUT = DesignSystem.CALLOUT;
    private static final Font FOOTNOTE = DesignSystem.FOOTNOTE;
    private static final Font CAPTION1 = DesignSystem.CAPTION1;
    private static final Font CAPTION2 = DesignSystem.CAPTION2;
    private static final Font MONO = DesignSystem.MONO;
    private static final int SPACING_2 = DesignSystem.SPACING_2;
    private static final int SPACING_4 = DesignSystem.SPACING_4;
    private static final int SPACING_8 = DesignSystem.SPACING_8;
    private static final int SPACING_12 = DesignSystem.SPACING_12;
    private static final int SPACING_16 = DesignSystem.SPACING_16;
    private static final int SPACING_20 = DesignSystem.SPACING_20;
    private static final int SPACING_24 = DesignSystem.SPACING_24;
    private static final int SPACING_32 = DesignSystem.SPACING_32;
    private static final int RADIUS_4 = DesignSystem.RADIUS_4;
    private static final int RADIUS_8 = DesignSystem.RADIUS_8;
    private static final int RADIUS_12 = DesignSystem.RADIUS_12;
    private static final int RADIUS_16 = DesignSystem.RADIUS_16;
    private static final int RADIUS_20 = DesignSystem.RADIUS_20;
    private static final Dimension BUTTON_REGULAR = DesignSystem.BUTTON_REGULAR;
    private static final Dimension BUTTON_LARGE = DesignSystem.BUTTON_LARGE;

    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JButton formatButton;
    private final JButton minifyButton;
    private final JButton validateButton;
    private final JButton copyButton;
    private final JButton clearButton;
    private final JLabel statusLabel;
    
    public JsonFormatter() {
        inputArea = new JTextArea();
        outputArea = new JTextArea();
        formatButton = new JButton(Texts.FORMAT_BUTTON);
        minifyButton = new JButton(Texts.MINIFY_BUTTON);
        validateButton = new JButton(Texts.VALIDATE_BUTTON);
        copyButton = new JButton(Texts.COPY_BUTTON);
        clearButton = new JButton(Texts.CLEAR_BUTTON);
        statusLabel = new JLabel(Texts.STATUS_READY);
        
        initializeGUI();
        setupEventHandlers();
        loadSampleJson();
        setupKeyboardShortcuts();
    }
    
    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 创建主面板
        createMainPanel();
        
        // 创建控制面板
        createControlPanel();
        
        // 创建状态栏
        createStatusBar();
        
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void createMainPanel() {
        var mainPanel = new JPanel(new GridLayout(1, 2, SPACING_12, 0));
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16));

        // 输入面板 - 使用苹果风格卡片
        var inputPanel = this.createStyledPanel(Texts.INPUT_PANEL_TITLE);
        inputArea.setFont(DesignSystem.MONO);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setTabSize(2);
        inputArea.setBackground(GRAY6);
        inputArea.setForeground(LABEL);
        inputArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_12, SPACING_12, SPACING_12)
        ));

        var inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        inputScrollPane.setBorder(BorderFactory.createEmptyBorder());
        inputScrollPane.getViewport().setBackground(GRAY6);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        // 输出面板 - 使用苹果风格卡片
        var outputPanel = this.createStyledPanel(Texts.OUTPUT_PANEL_TITLE);
        outputArea.setFont(DesignSystem.MONO);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setBackground(GRAY6);
        outputArea.setForeground(LABEL);
        outputArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_12, SPACING_12, SPACING_12)
        ));

        var outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        outputScrollPane.setBorder(BorderFactory.createEmptyBorder());
        outputScrollPane.getViewport().setBackground(GRAY6);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        mainPanel.add(inputPanel);
        mainPanel.add(outputPanel);

        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void createControlPanel() {
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_12, SPACING_12));
        controlPanel.setBackground(SYSTEM_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_24, SPACING_16, SPACING_24)
        ));

        formatButton.setToolTipText(Texts.FORMAT_TOOLTIP);
        minifyButton.setToolTipText(Texts.MINIFY_TOOLTIP);
        validateButton.setToolTipText(Texts.VALIDATE_TOOLTIP);
        copyButton.setToolTipText(Texts.COPY_TOOLTIP);
        clearButton.setToolTipText(Texts.CLEAR_TOOLTIP);

        controlPanel.add(this.createPrimaryButton(Texts.FORMAT_BUTTON, this::formatJson));
        controlPanel.add(this.createSecondaryButton(Texts.MINIFY_BUTTON, this::minifyJson));
        controlPanel.add(this.createSecondaryButton(Texts.VALIDATE_BUTTON, this::validateJson));
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(this.createSecondaryButton(Texts.COPY_BUTTON, this::copyResult));
        controlPanel.add(this.createSecondaryButton(Texts.CLEAR_BUTTON, this::clearAll));

        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_24, SPACING_12, SPACING_24)
        ));
        statusLabel.setFont(CAPTION1);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        formatButton.addActionListener(this::formatJson);
        minifyButton.addActionListener(this::minifyJson);
        validateButton.addActionListener(this::validateJson);
        copyButton.addActionListener(this::copyResult);
        clearButton.addActionListener(this::clearAll);
    }

    // ===== 按钮创建方法 =====
    private JButton createPrimaryButton(String text, java.util.function.Consumer<ActionEvent> action) {
        return this.createStyledButton(text, BLUE, WHITE, action);
    }

    private JButton createSecondaryButton(String text, java.util.function.Consumer<ActionEvent> action) {
        return this.createStyledButton(text, GRAY6, LABEL, action);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor, java.util.function.Consumer<ActionEvent> action) {
        var button = new JButton(text);
        button.setFont(BODY);
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

        // 添加动作监听器
        button.addActionListener(action::accept);

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

    private JPanel createStyledPanel(String title) {
        var panel = new JPanel(new BorderLayout());
        panel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 添加标题
        var titleLabel = new JLabel(title);
        titleLabel.setFont(HEADLINE);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_12, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }
    
    private void loadSampleJson() {
        var sampleJson = """
            {"name":"张三","age":25,"city":"北京","hobbies":["读书","游泳","编程"],"address":{"street":"中关村大街","number":123,"zipcode":"100080"},"married":false,"salary":null}
            """;
        inputArea.setText(sampleJson);
    }
    
    private void formatJson(ActionEvent e) {
        var input = inputArea.getText().trim();
        if (input.isEmpty()) {
            statusLabel.setText(Texts.STATUS_PLEASE_ENTER_JSON);
            return;
        }

        try {
            var formatted = formatJsonString(input);
            outputArea.setText(formatted);
            statusLabel.setText(Texts.STATUS_FORMAT_COMPLETE);
        } catch (Exception ex) {
            outputArea.setText(Texts.ERROR_FORMAT_FAILED + ex.getMessage());
            statusLabel.setText(Texts.STATUS_FORMAT_FAILED);
        }
    }
    
    private void minifyJson(ActionEvent e) {
        var input = inputArea.getText().trim();
        if (input.isEmpty()) {
            statusLabel.setText(Texts.STATUS_PLEASE_ENTER_JSON);
            return;
        }

        try {
            var minified = minifyJsonString(input);
            outputArea.setText(minified);
            statusLabel.setText(Texts.STATUS_MINIFY_COMPLETE);
        } catch (Exception ex) {
            outputArea.setText(Texts.ERROR_MINIFY_FAILED + ex.getMessage());
            statusLabel.setText(Texts.STATUS_MINIFY_FAILED);
        }
    }
    
    private void validateJson(ActionEvent e) {
        var input = inputArea.getText().trim();
        if (input.isEmpty()) {
            statusLabel.setText(Texts.STATUS_PLEASE_ENTER_JSON);
            return;
        }

        try {
            validateJsonString(input);
            outputArea.setText(Texts.VALIDATE_SUCCESS_PREFIX + getJsonInfo(input));
            statusLabel.setText(Texts.STATUS_VALIDATE_PASSED);
        } catch (Exception ex) {
            outputArea.setText(Texts.VALIDATE_ERROR_PREFIX + ex.getMessage());
            statusLabel.setText(Texts.STATUS_VALIDATE_FAILED);
        }
    }
    
    private void copyResult(ActionEvent e) {
        var result = outputArea.getText();
        if (result.isEmpty()) {
            statusLabel.setText(Texts.STATUS_NO_CONTENT_TO_COPY);
            return;
        }

        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(result);
        clipboard.setContents(selection, null);
        statusLabel.setText(Texts.STATUS_COPIED_TO_CLIPBOARD);
    }
    
    private void clearAll(ActionEvent e) {
        inputArea.setText("");
        outputArea.setText("");
        statusLabel.setText(Texts.STATUS_CLEARED);
    }
    
    // 简单的JSON格式化实现
    private String formatJsonString(String json) throws Exception {
        validateJsonString(json); // 先验证
        
        var result = new StringBuilder();
        var indent = 0;
        var inString = false;
        var escape = false;
        
        for (int i = 0; i < json.length(); i++) {
            var ch = json.charAt(i);
            
            if (escape) {
                result.append(ch);
                escape = false;
                continue;
            }
            
            if (ch == '\\' && inString) {
                result.append(ch);
                escape = true;
                continue;
            }
            
            if (ch == '"') {
                inString = !inString;
                result.append(ch);
                continue;
            }
            
            if (inString) {
                result.append(ch);
                continue;
            }
            
            switch (ch) {
                case '{', '[' -> {
                    result.append(ch);
                    result.append('\n');
                    indent++;
                    appendIndent(result, indent);
                }
                case '}', ']' -> {
                    result.append('\n');
                    indent--;
                    appendIndent(result, indent);
                    result.append(ch);
                }
                case ',' -> {
                    result.append(ch);
                    result.append('\n');
                    appendIndent(result, indent);
                }
                case ':' -> {
                    result.append(ch);
                    result.append(' ');
                }
                case ' ', '\t', '\n', '\r' -> {
                    // 忽略原有的空白字符
                }
                default -> result.append(ch);
            }
        }
        
        return result.toString();
    }
    
    private String minifyJsonString(String json) throws Exception {
        validateJsonString(json); // 先验证
        
        var result = new StringBuilder();
        var inString = false;
        var escape = false;
        
        for (int i = 0; i < json.length(); i++) {
            var ch = json.charAt(i);
            
            if (escape) {
                result.append(ch);
                escape = false;
                continue;
            }
            
            if (ch == '\\' && inString) {
                result.append(ch);
                escape = true;
                continue;
            }
            
            if (ch == '"') {
                inString = !inString;
                result.append(ch);
                continue;
            }
            
            if (inString) {
                result.append(ch);
                continue;
            }
            
            // 在非字符串内容中，跳过空白字符
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                continue;
            }
            
            result.append(ch);
        }
        
        return result.toString();
    }
    
    private void validateJsonString(String json) throws Exception {
        var stack = new java.util.Stack<Character>();
        var inString = false;
        var escape = false;
        
        for (int i = 0; i < json.length(); i++) {
            var ch = json.charAt(i);
            
            if (escape) {
                escape = false;
                continue;
            }
            
            if (ch == '\\' && inString) {
                escape = true;
                continue;
            }
            
            if (ch == '"') {
                inString = !inString;
                continue;
            }
            
            if (inString) {
                continue;
            }
            
            switch (ch) {
                case '{' -> stack.push('}');
                case '[' -> stack.push(']');
                case '}', ']' -> {
                    if (stack.isEmpty()) {
                        throw new Exception(Texts.ERROR_UNEXPECTED_CHAR + i + ": " + Texts.ERROR_EXPECTED_BUT_FOUND + ch + Texts.ERROR_QUOTE);
                    }
                    var expected = stack.pop();
                    if (ch != expected) {
                        throw new Exception(Texts.ERROR_UNEXPECTED_CHAR + i + ": " + Texts.ERROR_EXPECTED_BUT_FOUND + expected + Texts.ERROR_BUT_FOUND + ch + Texts.ERROR_QUOTE);
                    }
                }
            }
        }
        
        if (!stack.isEmpty()) {
            throw new Exception(Texts.ERROR_JSON_INCOMPLETE);
        }

        if (inString) {
            throw new Exception(Texts.ERROR_STRING_NOT_CLOSED);
        }
    }
    
    private String getJsonInfo(String json) {
        var info = new StringBuilder();
        info.append(Texts.JSON_INFO_HEADER);
        info.append(Texts.JSON_INFO_TOTAL_CHARS).append(json.length()).append("\n");
        
        var objectCount = 0;
        var arrayCount = 0;
        var stringCount = 0;
        var inString = false;
        var escape = false;
        
        for (int i = 0; i < json.length(); i++) {
            var ch = json.charAt(i);
            
            if (escape) {
                escape = false;
                continue;
            }
            
            if (ch == '\\' && inString) {
                escape = true;
                continue;
            }
            
            if (ch == '"') {
                if (!inString) {
                    stringCount++;
                }
                inString = !inString;
                continue;
            }
            
            if (!inString) {
                if (ch == '{') objectCount++;
                else if (ch == '[') arrayCount++;
            }
        }
        
        info.append(Texts.JSON_INFO_OBJECTS).append(objectCount).append("\n");
        info.append(Texts.JSON_INFO_ARRAYS).append(arrayCount).append("\n");
        info.append(Texts.JSON_INFO_STRINGS).append(stringCount / 2).append("\n"); // 除以2因为每个字符串有开始和结束引号
        
        return info.toString();
    }
    
    private void appendIndent(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("  "); // 2个空格作为缩进
        }
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_ENTER:
                        // 回车键格式化
                        if (ev.isControlDown()) {
                            // Ctrl+Enter 验证JSON
                            validateJson(new ActionEvent(JsonFormatter.this, ActionEvent.ACTION_PERFORMED, "validate"));
                        } else {
                            formatJson(new ActionEvent(JsonFormatter.this, ActionEvent.ACTION_PERFORMED, "format"));
                        }
                        break;
                    case KeyEvent.VK_M:
                        // M键压缩
                        if (!ev.isControlDown()) {
                            minifyJson(new ActionEvent(JsonFormatter.this, ActionEvent.ACTION_PERFORMED, "minify"));
                        }
                        break;
                    case KeyEvent.VK_C:
                        // C键复制结果（如果按下Ctrl+C则让系统处理复制）
                        if (ev.isControlDown()) {
                            // 让系统处理复制
                            return;
                        } else {
                            copyResult(new ActionEvent(JsonFormatter.this, ActionEvent.ACTION_PERFORMED, "copy"));
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        // ESC键清空
                        clearAll(new ActionEvent(JsonFormatter.this, ActionEvent.ACTION_PERFORMED, "clear"));
                        break;
                    case KeyEvent.VK_V:
                        // V键验证
                        if (!ev.isControlDown()) {
                            validateJson(new ActionEvent(JsonFormatter.this, ActionEvent.ACTION_PERFORMED, "validate"));
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
