import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Base64;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🔤 Base64编码工具";

    // 主界面标题
    static final String MAIN_TITLE = "🔤 Base64编码解码工具";

    // 按钮文本
    static final String ENCODE_BUTTON = "编码";
    static final String DECODE_BUTTON = "解码";
    static final String CLEAR_BUTTON = "清空";
    static final String COPY_BUTTON = "复制结果";
    static final String PASTE_BUTTON = "粘贴";
    static final String SWAP_BUTTON = "交换";

    // 面板标题
    static final String INPUT_PANEL_TITLE = "输入文本";
    static final String OUTPUT_PANEL_TITLE = "输出结果";
    static final String CONTROL_PANEL_TITLE = "操作控制";

    // 标签文本
    static final String INPUT_PLACEHOLDER = "在此输入要编码或解码的文本...";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_ENCODED = "编码完成";
    static final String STATUS_DECODED = "解码完成";
    static final String STATUS_CLEARED = "内容已清空";
    static final String STATUS_COPIED = "结果已复制到剪贴板";
    static final String STATUS_SWAPPED = "内容已交换";

    // 错误消息
    static final String ERROR_INVALID_BASE64 = "无效的Base64格式";
    static final String ERROR_ENCODING_FAILED = "编码失败: %s";
    static final String ERROR_DECODING_FAILED = "解码失败: %s";
    static final String ERROR_COPY_FAILED = "复制到剪贴板失败";

    // 示例内容
    static final String SAMPLE_INPUT = """
        Hello, World! 你好，世界！

        这是一个Base64编码工具的示例。
        Base64是一种基于64个可打印字符来表示二进制数据的编码方式。

        常用于：
        • 在HTTP环境下传递较长的标识信息
        • 在电子邮件中传输二进制数据
        • 将二进制数据嵌入到文本文件中

        编码后的数据只包含ASCII字符，可以安全地在文本协议中传输。
        """;

    // 编码结果示例
    static final String SAMPLE_ENCODED = """
        SGVsbG8sIFdvcmxkISA你好，世界！

        6L+Z5piv5LiA5LqbQmFzZTY05Yqg6L295a6a5L2T5LiA6L2m5Lmg6L2m6L29Lg==
        """;

    // 帮助信息
    static final String HELP_MESSAGE = """
        Base64编码工具使用说明：

        • 在输入框中输入要编码或解码的文本
        • 点击"编码"按钮将文本转换为Base64格式
        • 点击"解码"按钮将Base64格式的文本还原
        • 使用"交换"按钮可以交换输入和输出内容
        • 点击"复制结果"按钮将输出内容复制到剪贴板
        • 点击"清空"按钮清除所有内容

        快捷键：
        Ctrl+E - 编码
        Ctrl+D - 解码
        Ctrl+L - 清空
        Ctrl+C - 复制结果
        Ctrl+X - 交换内容
        F1 - 显示帮助
        """;
}

/**
 * Base64编码解码工具
 * 支持文本的Base64编码和解码
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Base64Encoder().setVisible(true);
    });
}


static class Base64Encoder extends JFrame {
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

    // 文本颜色
    private static final Color LABEL = new Color(0, 0, 0);
    private static final Color SECONDARY_LABEL = new Color(60, 60, 67, 153);
    private static final Color TERTIARY_LABEL = new Color(60, 60, 67, 76);

    // 背景颜色
    private static final Color SYSTEM_BACKGROUND = new Color(255, 255, 255);
    private static final Color SECONDARY_SYSTEM_BACKGROUND = new Color(242, 242, 247);
    private static final Color TERTIARY_SYSTEM_BACKGROUND = new Color(255, 255, 255);

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
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JComboBox<String> operationCombo;
    private JComboBox<String> encodingCombo;
    private JLabel statusLabel;
    private JButton processButton;
    private JButton clearButton;
    private JButton copyButton;
    private JButton pasteButton;
    private JButton saveButton;
    private JButton loadButton;
    private JCheckBox urlSafeCheck;
    private JCheckBox lineWrapCheck;
    private JPanel mainPanel;

    public Base64Encoder() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        this.initializeGUI();
        this.setupEventHandlers();
        this.setupKeyboardShortcuts();

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private JButton createPrimaryButton(String text) {
        var button = new JButton(text);
        button.setFont(HEADLINE);
        button.setBackground(BLUE);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 32));
        button.setOpaque(true);
        this.setupButtonHoverEffect(button, BLUE, new Color(0, 100, 235));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        var button = new JButton(text);
        button.setFont(HEADLINE);
        button.setBackground(GRAY6);
        button.setForeground(LABEL);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 32));
        button.setOpaque(true);
        this.setupButtonHoverEffect(button, GRAY6, GRAY5);
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

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // 主面板 - 使用设计系统
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_20, SPACING_20, SPACING_20, SPACING_20));

        // 标题面板
        var titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(SYSTEM_BACKGROUND);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_16, 0));

        var titleLabel = new JLabel("🔤 Base64 编解码器", SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 控制面板
        var controlPanel = this.createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 输入输出面板
        var contentPanel = this.createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 状态栏
        var statusPanel = this.createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 设置窗口
        setSize(700, 800);
        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_16, SPACING_12));
        controlPanel.setBackground(SYSTEM_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));

        // 操作类型
        var operationLabel = new JLabel("操作:");
        operationLabel.setFont(CAPTION1);
        operationLabel.setForeground(SECONDARY_LABEL);
        controlPanel.add(operationLabel);

        operationCombo = new JComboBox<>(new String[]{"编码 (Encode)", "解码 (Decode)"});
        operationCombo.setFont(CAPTION1);
        operationCombo.setBackground(WHITE);
        operationCombo.setBorder(new RoundedBorder(RADIUS_4));
        operationCombo.addActionListener((ev) -> this.updateButtonText());
        controlPanel.add(operationCombo);

        // 编码类型
        var encodingLabel = new JLabel("编码类型:");
        encodingLabel.setFont(CAPTION1);
        encodingLabel.setForeground(SECONDARY_LABEL);
        controlPanel.add(encodingLabel);

        encodingCombo = new JComboBox<>(new String[]{"标准 Base64", "URL 安全 Base64", "MIME Base64"});
        encodingCombo.setFont(CAPTION1);
        encodingCombo.setBackground(WHITE);
        encodingCombo.setBorder(new RoundedBorder(RADIUS_4));
        controlPanel.add(encodingCombo);

        // 选项
        urlSafeCheck = new JCheckBox("URL安全模式");
        urlSafeCheck.setFont(CAPTION1);
        urlSafeCheck.setBackground(SYSTEM_BACKGROUND);
        urlSafeCheck.addActionListener((ev) -> {
            if (urlSafeCheck.isSelected()) {
                encodingCombo.setSelectedIndex(1); // URL安全Base64
            } else {
                encodingCombo.setSelectedIndex(0); // 标准Base64
            }
        });
        controlPanel.add(urlSafeCheck);

        lineWrapCheck = new JCheckBox("自动换行");
        lineWrapCheck.setFont(CAPTION1);
        lineWrapCheck.setBackground(SYSTEM_BACKGROUND);
        lineWrapCheck.setSelected(true);
        lineWrapCheck.addActionListener((ev) -> {
            inputTextArea.setLineWrap(lineWrapCheck.isSelected());
            outputTextArea.setLineWrap(lineWrapCheck.isSelected());
        });
        controlPanel.add(lineWrapCheck);

        return controlPanel;
    }

    private JPanel createContentPanel() {
        var contentPanel = new JPanel(new BorderLayout(SPACING_16, SPACING_16));
        contentPanel.setBackground(SYSTEM_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_16, 0, SPACING_16, 0));

        // 输入区域
        var inputPanel = this.createInputPanel();
        contentPanel.add(inputPanel, BorderLayout.NORTH);

        // 按钮面板
        var buttonPanel = this.createButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        // 输出区域
        var outputPanel = this.createOutputPanel();
        contentPanel.add(outputPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private JPanel createInputPanel() {
        var inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 标题
        var titleLabel = new JLabel("输入文本");
        titleLabel.setFont(HEADLINE);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_12, 0));
        inputPanel.add(titleLabel, BorderLayout.NORTH);

        // 文本区域
        inputTextArea = new JTextArea(6, 40);
        inputTextArea.setFont(new Font("SF Mono", Font.PLAIN, 12));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputTextArea.setBackground(WHITE);
        inputTextArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
        ));

        var scrollPane = new JScrollPane(inputTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(WHITE);
        inputPanel.add(scrollPane, BorderLayout.CENTER);

        // 输入按钮面板
        var inputButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_12, 0));
        inputButtonPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        inputButtonPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_12, 0, 0, 0));

        pasteButton = this.createSecondaryButton("📋 " + Texts.PASTE_BUTTON);
        pasteButton.addActionListener(e -> pasteFromClipboard());
        clearButton = this.createSecondaryButton("🗑️ " + Texts.CLEAR_BUTTON);
        clearButton.addActionListener(e -> clearAll());

        inputButtonPanel.add(pasteButton);
        inputButtonPanel.add(clearButton);
        inputPanel.add(inputButtonPanel, BorderLayout.SOUTH);

        return inputPanel;
    }

    private JPanel createButtonPanel() {
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, 0));
        buttonPanel.setBackground(SYSTEM_BACKGROUND);

        processButton = this.createPrimaryButton("🔒 " + Texts.ENCODE_BUTTON);
        processButton.addActionListener(e -> processText());

        copyButton = this.createSecondaryButton("📋 " + Texts.COPY_BUTTON);
        copyButton.addActionListener(e -> copyResult());

        buttonPanel.add(processButton);
        buttonPanel.add(copyButton);

        return buttonPanel;
    }

    private JPanel createOutputPanel() {
        var outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        outputPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 标题
        var titleLabel = new JLabel("输出结果");
        titleLabel.setFont(HEADLINE);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_12, 0));
        outputPanel.add(titleLabel, BorderLayout.NORTH);

        // 文本区域
        outputTextArea = new JTextArea(6, 40);
        outputTextArea.setFont(new Font("SF Mono", Font.PLAIN, 12));
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setEditable(false);
        outputTextArea.setBackground(WHITE);
        outputTextArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
        ));

        var scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(WHITE);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        // 输出按钮面板
        var outputButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_12, 0));
        outputButtonPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        outputButtonPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_12, 0, 0, 0));

        loadButton = this.createSecondaryButton("📁 加载");
        loadButton.addActionListener((ev) -> this.loadFromFile());
        saveButton = this.createSecondaryButton("💾 保存");
        saveButton.addActionListener((ev) -> this.saveToFile());

        outputButtonPanel.add(loadButton);
        outputButtonPanel.add(saveButton);
        outputPanel.add(outputButtonPanel, BorderLayout.SOUTH);

        return outputPanel;
    }

    private JPanel createStatusPanel() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(GRAY6);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));

        statusLabel = new JLabel("就绪");
        statusLabel.setFont(CAPTION1);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        return statusPanel;
    }

    private void setupEventHandlers() {
        // 事件处理器将在各个组件创建时设置
    }

    private void updateButtonText() {
        String operation = (String) operationCombo.getSelectedItem();
        if ("编码 (Encode)".equals(operation)) {
            processButton.setText("🔒 编码");
            processButton.setBackground(BLUE);
        } else {
            processButton.setText("🔓 解码");
            processButton.setBackground(GREEN);
        }
    }

    private void pasteFromClipboard() {
        try {
            var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            var clipboardText = (String) clipboard.getData(java.awt.datatransfer.DataFlavor.stringFlavor);
            if (clipboardText != null) {
                inputTextArea.setText(clipboardText);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "无法读取剪贴板: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processText() {
        String inputText = inputTextArea.getText().trim();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要处理的文本！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String operation = (String) operationCombo.getSelectedItem();
            String encodingType = (String) encodingCombo.getSelectedItem();
            String result;

            if ("编码 (Encode)".equals(operation)) {
                result = this.encodeText(inputText, encodingType);
                statusLabel.setText(Texts.STATUS_ENCODED);
            } else {
                result = this.decodeText(inputText, encodingType);
                statusLabel.setText(Texts.STATUS_DECODED);
            }

            outputTextArea.setText(result);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "处理失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText(String.format(Texts.ERROR_ENCODING_FAILED, ex.getMessage()));
        }
    }

    private void copyResult() {
        String outputText = outputTextArea.getText();
        if (!outputText.isEmpty()) {
            this.copyToClipboard(outputText);
            statusLabel.setText(Texts.STATUS_COPIED);
        }
    }

    private void clearAll() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        statusLabel.setText(Texts.STATUS_CLEARED);
    }

    private void saveToFile() {
        var fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存结果");
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.nio.file.Files.write(file.toPath(), outputTextArea.getText().getBytes());
                statusLabel.setText("文件已保存: " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "保存文件失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadFromFile() {
        var fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("加载文件");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                inputTextArea.setText(content);
                statusLabel.setText("文件已加载: " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "加载文件失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void copyToClipboard(String text) {
        try {
            var selection = new java.awt.datatransfer.StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "复制到剪贴板失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String encodeText(String text, String encodingType) {
        byte[] bytes = text.getBytes();
        Base64.Encoder encoder;

        switch (encodingType) {
            case "URL 安全 Base64":
                encoder = Base64.getUrlEncoder();
                break;
            case "MIME Base64":
                encoder = Base64.getMimeEncoder();
                break;
            default:
                encoder = Base64.getEncoder();
        }

        return encoder.encodeToString(bytes);
    }

    private String decodeText(String encodedText, String encodingType) {
        Base64.Decoder decoder;

        switch (encodingType) {
            case "URL 安全 Base64":
                decoder = Base64.getUrlDecoder();
                break;
            case "MIME Base64":
                decoder = Base64.getMimeDecoder();
                break;
            default:
                decoder = Base64.getDecoder();
        }

        byte[] decodedBytes = decoder.decode(encodedText);
        return new String(decodedBytes);
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
            g2d.setColor(GRAY4);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_E:
                        // E键编码
                        if (ev.isControlDown()) {
                            processText();
                        }
                        break;
                    case KeyEvent.VK_D:
                        // D键解码
                        if (ev.isControlDown()) {
                            processText();
                        }
                        break;
                    case KeyEvent.VK_L:
                        // L键清空
                        if (ev.isControlDown()) {
                            clearAll();
                        }
                        break;
                    case KeyEvent.VK_C:
                        // C键复制结果
                        if (ev.isControlDown()) {
                            copyResult();
                        }
                        break;
                    case KeyEvent.VK_X:
                        // X键交换内容
                        if (ev.isControlDown()) {
                            swapContent();
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

    private void swapContent() {
        String inputText = inputTextArea.getText();
        String outputText = outputTextArea.getText();
        inputTextArea.setText(outputText);
        outputTextArea.setText(inputText);
        statusLabel.setText(Texts.STATUS_SWAPPED);
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this, Texts.HELP_MESSAGE, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }
}