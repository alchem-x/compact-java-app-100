import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

// Apple 设计系统常量
class AppleDesign {
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
    SwingUtilities.invokeLater(() -> {
        new UrlEncoder().setVisible(true);
    });
}

static class UrlEncoder extends JFrame {
    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JButton encodeButton;
    private final JButton decodeButton;
    private final JButton copyButton;
    private final JButton clearButton;
    private final JButton swapButton;
    private final JLabel statusLabel;
    private final JComboBox<String> encodingCombo;
    
    public UrlEncoder() {
        inputArea = new JTextArea();
        outputArea = new JTextArea();
        encodeButton = new JButton("URL编码");
        decodeButton = new JButton("URL解码");
        copyButton = new JButton("复制结果");
        clearButton = new JButton("清空");
        swapButton = new JButton("交换");
        statusLabel = new JLabel("就绪");
        encodingCombo = new JComboBox<>(new String[]{"UTF-8", "GBK", "ISO-8859-1"});
        
        initializeGUI();
        setupEventHandlers();
        loadSampleData();
    }
    
    private void initializeGUI() {
        setTitle("🌐 URL编解码工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        
        // 创建控制面板
        createControlPanel();
        
        // 创建主工作区
        createWorkArea();
        
        // 创建状态栏
        createStatusBar();
        
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
        encodingCombo.setSelectedItem("UTF-8");
        optionsPanel.add(encodingCombo);

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
        encodeButton.addActionListener(this::encodeUrl);
        decodeButton.addActionListener(this::decodeUrl);
        copyButton.addActionListener(this::copyResult);
        clearButton.addActionListener(this::clearAll);
        swapButton.addActionListener(this::swapContent);
    }
    
    private void loadSampleData() {
        inputArea.setText("https://example.com/search?q=Java编程&type=教程&page=1");
    }
    
    private void encodeUrl(ActionEvent e) {
        var input = inputArea.getText();
        if (input.isEmpty()) {
            statusLabel.setText("请输入要编码的URL");
            return;
        }
        
        try {
            var encoding = (String) encodingCombo.getSelectedItem();
            var encoded = URLEncoder.encode(input, encoding);
            
            outputArea.setText(encoded);
            
            var info = String.format("URL编码完成 - 原文: %d字符, 编码后: %d字符, 编码: %s", 
                input.length(), encoded.length(), encoding);
            statusLabel.setText(info);
            
        } catch (Exception ex) {
            outputArea.setText("编码失败: " + ex.getMessage());
            statusLabel.setText("编码失败");
        }
    }
    
    private void decodeUrl(ActionEvent e) {
        var input = inputArea.getText().trim();
        if (input.isEmpty()) {
            statusLabel.setText("请输入要解码的URL");
            return;
        }
        
        try {
            var encoding = (String) encodingCombo.getSelectedItem();
            var decoded = URLDecoder.decode(input, encoding);
            
            outputArea.setText(decoded);
            
            var info = String.format("URL解码完成 - 编码文本: %d字符, 解码后: %d字符, 编码: %s", 
                input.length(), decoded.length(), encoding);
            statusLabel.setText(info);
            
        } catch (Exception ex) {
            outputArea.setText("解码失败: " + ex.getMessage());
            statusLabel.setText("解码失败");
        }
    }
    
    private void copyResult(ActionEvent e) {
        var result = outputArea.getText();
        if (result.isEmpty()) {
            statusLabel.setText("没有可复制的内容");
            return;
        }
        
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(result);
        clipboard.setContents(selection, null);
        statusLabel.setText("已复制到剪贴板");
    }
    
    private void clearAll(ActionEvent e) {
        inputArea.setText("");
        outputArea.setText("");
        statusLabel.setText("已清空");
    }
    
    private void swapContent(ActionEvent e) {
        var inputText = inputArea.getText();
        var outputText = outputArea.getText();
        
        inputArea.setText(outputText);
        outputArea.setText(inputText);
        statusLabel.setText("内容已交换");
    }
}
