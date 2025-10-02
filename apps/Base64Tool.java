import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

// Apple 设计系统常量
static class AppleDesign {
    // 颜色系统
    static final Color SYSTEM_BLUE = new Color(0, 122, 255);
    static final Color SYSTEM_GREEN = new Color(52, 199, 89);
    static final Color SYSTEM_INDIGO = new Color(88, 86, 214);
    static final Color SYSTEM_ORANGE = new Color(255, 149, 0);
    static final Color SYSTEM_PINK = new Color(255, 45, 85);
    static final Color SYSTEM_PURPLE = new Color(175, 82, 222);
    static final Color SYSTEM_RED = new Color(255, 59, 48);
    static final Color SYSTEM_TEAL = new Color(90, 200, 250);
    static final Color SYSTEM_YELLOW = new Color(255, 204, 0);

    // 灰色系统
    static final Color SYSTEM_GRAY = new Color(142, 142, 147);
    static final Color SYSTEM_GRAY2 = new Color(174, 174, 178);
    static final Color SYSTEM_GRAY3 = new Color(199, 199, 204);
    static final Color SYSTEM_GRAY4 = new Color(209, 209, 214);
    static final Color SYSTEM_GRAY5 = new Color(229, 229, 234);
    static final Color SYSTEM_GRAY6 = new Color(242, 242, 247);

    // 背景色
    static final Color SECONDARY_SYSTEM_BACKGROUND = new Color(247, 247, 247);
    static final Color TERTIARY_SYSTEM_BACKGROUND = new Color(255, 255, 255);

    // 字体系统
    static final Font HEADLINE_FONT = new Font("SF Pro Display", Font.BOLD, 24);
    static final Font TITLE_FONT = new Font("SF Pro Display", Font.BOLD, 18);
    static final Font BODY_FONT = new Font("SF Pro Text", Font.PLAIN, 14);
    static final Font CALLOUT_FONT = new Font("SF Pro Text", Font.PLAIN, 12);
    static final Font CAPTION_FONT = new Font("SF Pro Text", Font.PLAIN, 11);
    static final Font MONO_FONT = new Font("SF Mono", Font.PLAIN, 12);

    // 间距系统
    static final int SMALL_SPACING = 4;
    static final int MEDIUM_SPACING = 8;
    static final int LARGE_SPACING = 16;
    static final int EXTRA_LARGE_SPACING = 20;

    // 圆角系统
    static final int SMALL_RADIUS = 4;
    static final int MEDIUM_RADIUS = 8;
    static final int LARGE_RADIUS = 12;
    static final int EXTRA_LARGE_RADIUS = 16;

    // 按钮样式
    static void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(BODY_FONT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    // 创建圆角面板
    static JPanel createRoundedPanel(int radius, Color bgColor) {
        var panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                var g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Base64Tool().setVisible(true));
}

static class Base64Tool extends JFrame {
    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JButton encodeButton;
    private final JButton decodeButton;
    private final JButton copyButton;
    private final JButton clearButton;
    private final JButton swapButton;
    private final JLabel statusLabel;
    private final JComboBox<String> encodingCombo;
    private final JCheckBox urlSafeBox;
    
    public Base64Tool() {
        inputArea = new JTextArea();
        outputArea = new JTextArea();
        encodeButton = new JButton("编码");
        decodeButton = new JButton("解码");
        copyButton = new JButton("复制结果");
        clearButton = new JButton("清空");
        swapButton = new JButton("交换");
        statusLabel = new JLabel("就绪");
        encodingCombo = new JComboBox<>(new String[]{"UTF-8", "GBK", "ISO-8859-1"});
        urlSafeBox = new JCheckBox("URL安全编码");
        
        this.initializeGUI();
        this.setupEventHandlers();
        this.setupKeyboardShortcuts();
        this.loadSampleData();
    }
    
    private void initializeGUI() {
        setTitle("🔐 Base64编解码工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        
        // 创建控制面板
        this.createControlPanel();

        // 创建主工作区
        this.createWorkArea();

        // 创建状态栏
        this.createStatusBar();
        
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    private void createControlPanel() {
        var controlPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        // 选项面板
        var optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.setOpaque(false);
        var encodingLabel = new JLabel("字符编码:");
        encodingLabel.setFont(AppleDesign.CALLOUT_FONT);
        encodingLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        optionsPanel.add(encodingLabel);
        encodingCombo.setFont(AppleDesign.CALLOUT_FONT);
        encodingCombo.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        optionsPanel.add(encodingCombo);
        urlSafeBox.setFont(AppleDesign.CALLOUT_FONT);
        urlSafeBox.setForeground(AppleDesign.SYSTEM_GRAY);
        optionsPanel.add(urlSafeBox);

        // 按钮面板
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, AppleDesign.SMALL_SPACING, 0));
        buttonPanel.setOpaque(false);

        AppleDesign.styleButton(encodeButton, AppleDesign.SYSTEM_BLUE, Color.WHITE);
        AppleDesign.styleButton(decodeButton, AppleDesign.SYSTEM_GREEN, Color.WHITE);
        AppleDesign.styleButton(swapButton, AppleDesign.SYSTEM_ORANGE, Color.WHITE);
        AppleDesign.styleButton(copyButton, AppleDesign.SYSTEM_INDIGO, Color.WHITE);
        AppleDesign.styleButton(clearButton, AppleDesign.SYSTEM_GRAY, Color.WHITE);

        buttonPanel.add(encodeButton);
        buttonPanel.add(decodeButton);
        buttonPanel.add(new JLabel("  "));
        buttonPanel.add(swapButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(clearButton);

        controlPanel.add(optionsPanel);
        controlPanel.add(buttonPanel);

        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void createWorkArea() {
        var workPanel = new JPanel(new GridLayout(2, 1, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));
        workPanel.setOpaque(false);
        workPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        // 输入面板
        var inputPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        inputPanel.setLayout(new BorderLayout());
        var inputTitle = new JLabel("输入文本");
        inputTitle.setFont(AppleDesign.TITLE_FONT);
        inputTitle.setForeground(AppleDesign.SYSTEM_BLUE);
        inputTitle.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING));
        inputPanel.add(inputTitle, BorderLayout.NORTH);

        inputArea.setFont(AppleDesign.MONO_FONT);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setRows(8);
        inputArea.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        inputArea.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        var inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        inputScrollPane.setBorder(BorderFactory.createEmptyBorder());
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        // 输出面板
        var outputPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        outputPanel.setLayout(new BorderLayout());
        var outputTitle = new JLabel("输出结果");
        outputTitle.setFont(AppleDesign.TITLE_FONT);
        outputTitle.setForeground(AppleDesign.SYSTEM_GREEN);
        outputTitle.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING));
        outputPanel.add(outputTitle, BorderLayout.NORTH);

        outputArea.setFont(AppleDesign.MONO_FONT);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        outputArea.setRows(8);
        outputArea.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        var outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        outputScrollPane.setBorder(BorderFactory.createEmptyBorder());
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        workPanel.add(inputPanel);
        workPanel.add(outputPanel);

        add(workPanel, BorderLayout.CENTER);
    }
    
    private void createStatusBar() {
        var statusPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING));
        statusLabel.setFont(AppleDesign.CAPTION_FONT);
        statusLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        encodeButton.addActionListener((ev) -> this.encodeText());
        decodeButton.addActionListener((ev) -> this.decodeText());
        copyButton.addActionListener((ev) -> this.copyResult());
        clearButton.addActionListener((ev) -> this.clearAll());
        swapButton.addActionListener((ev) -> this.swapContent());
    }
    
    private void loadSampleData() {
        inputArea.setText("Hello, World! 你好，世界！\n这是一个Base64编解码示例。");
    }
    
    private void encodeText() {
        var input = inputArea.getText();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要编码的文本", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            var encoding = (String) encodingCombo.getSelectedItem();
            var bytes = input.getBytes(encoding);
            
            String encoded;
            if (urlSafeBox.isSelected()) {
                encoded = Base64.getUrlEncoder().encodeToString(bytes);
            } else {
                encoded = Base64.getEncoder().encodeToString(bytes);
            }
            
            outputArea.setText(encoded);
            
            var info = String.format("编码完成 - 原文: %d字符, 编码后: %d字符, 编码: %s", 
                input.length(), encoded.length(), encoding);
            statusLabel.setText(info);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "编码失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("编码失败");
        }
    }
    
    private void decodeText() {
        var input = inputArea.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要解码的Base64文本", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            byte[] bytes;
            if (urlSafeBox.isSelected()) {
                bytes = Base64.getUrlDecoder().decode(input);
            } else {
                bytes = Base64.getDecoder().decode(input);
            }
            
            var encoding = (String) encodingCombo.getSelectedItem();
            var decoded = new String(bytes, encoding);
            
            outputArea.setText(decoded);
            
            var info = String.format("解码完成 - Base64: %d字符, 解码后: %d字符, 编码: %s", 
                input.length(), decoded.length(), encoding);
            statusLabel.setText(info);
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "解码失败: 无效的Base64格式", "错误", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("解码失败 - 格式错误");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "解码失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("解码失败");
        }
    }
    
    private void copyResult() {
        var result = outputArea.getText();
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可复制的内容", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(result);
        clipboard.setContents(selection, null);
        statusLabel.setText("已复制到剪贴板");
    }
    
    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
        statusLabel.setText("已清空");
    }
    
    private void swapContent() {
        var inputText = inputArea.getText();
        var outputText = outputArea.getText();
        
        inputArea.setText(outputText);
        outputArea.setText(inputText);
        statusLabel.setText("内容已交换");
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_E:
                        // E键编码
                        if (ev.isControlDown()) {
                            encodeText();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_D:
                        // D键解码
                        if (ev.isControlDown()) {
                            decodeText();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_C:
                        // C键复制结果
                        if (ev.isControlDown()) {
                            copyResult();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_L:
                        // L键清空
                        if (ev.isControlDown()) {
                            clearAll();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_X:
                        // X键交换
                        if (ev.isControlDown()) {
                            swapContent();
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
            Base64编解码工具使用说明：

            • 在上方的输入框中输入要编码或解码的文本
            • 选择字符编码格式（UTF-8、GBK、ISO-8859-1）
            • 可选择是否使用URL安全编码
            • 点击"编码"按钮将文本编码为Base64格式
            • 点击"解码"按钮将Base64格式的文本解码
            • 点击"复制结果"按钮将输出内容复制到剪贴板

            功能说明：
            • 编码：将普通文本转换为Base64格式
            • 解码：将Base64格式的文本转换回普通文本
            • URL安全编码：生成适用于URL的Base64编码
            • 交换内容：交换输入和输出框的内容

            使用技巧：
            • 支持多种字符编码格式
            • 可以处理包含中文、特殊字符的文本
            • 生成的Base64编码可以用于网络传输或数据存储

            快捷键：
            Ctrl+E - 编码
            Ctrl+D - 解码
            Ctrl+C - 复制结果
            Ctrl+L - 清空
            Ctrl+X - 交换内容
            Ctrl+H - 显示帮助
            F1 - 显示帮助
            """;
        JOptionPane.showMessageDialog(this, helpMessage, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }
}
