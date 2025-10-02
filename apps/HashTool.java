import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.security.MessageDigest;

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
        new HashTool().setVisible(true);
    });
}

static class HashTool extends JFrame {
    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JButton md5Button;
    private final JButton sha1Button;
    private final JButton sha256Button;
    private final JButton copyButton;
    private final JButton clearButton;
    private final JLabel statusLabel;
    
    public HashTool() {
        inputArea = new JTextArea();
        outputArea = new JTextArea();
        md5Button = new JButton("MD5");
        sha1Button = new JButton("SHA-1");
        sha256Button = new JButton("SHA-256");
        copyButton = new JButton("复制");
        clearButton = new JButton("清空");
        statusLabel = new JLabel("就绪");
        
        initializeGUI();
        setupEventHandlers();
        loadSampleData();
    }
    
    private void initializeGUI() {
        setTitle("🔐 哈希工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        
        // 控制面板
        var controlPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        AppleDesign.styleButton(md5Button, AppleDesign.SYSTEM_BLUE, Color.WHITE);
        AppleDesign.styleButton(sha1Button, AppleDesign.SYSTEM_GREEN, Color.WHITE);
        AppleDesign.styleButton(sha256Button, AppleDesign.SYSTEM_ORANGE, Color.WHITE);
        AppleDesign.styleButton(copyButton, AppleDesign.SYSTEM_INDIGO, Color.WHITE);
        AppleDesign.styleButton(clearButton, AppleDesign.SYSTEM_GRAY, Color.WHITE);

        controlPanel.add(md5Button);
        controlPanel.add(sha1Button);
        controlPanel.add(sha256Button);
        controlPanel.add(new JLabel("  "));
        controlPanel.add(copyButton);
        controlPanel.add(clearButton);

        add(controlPanel, BorderLayout.NORTH);
        
        // 主工作区
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
        inputArea.setRows(6);
        inputArea.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        inputArea.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        var inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setBorder(BorderFactory.createEmptyBorder());
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        // 输出面板
        var outputPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        outputPanel.setLayout(new BorderLayout());
        var outputTitle = new JLabel("哈希结果");
        outputTitle.setFont(AppleDesign.TITLE_FONT);
        outputTitle.setForeground(AppleDesign.SYSTEM_GREEN);
        outputTitle.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING));
        outputPanel.add(outputTitle, BorderLayout.NORTH);

        outputArea.setFont(AppleDesign.MONO_FONT);
        outputArea.setEditable(false);
        outputArea.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        outputArea.setRows(6);
        outputArea.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        var outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createEmptyBorder());
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        workPanel.add(inputPanel);
        workPanel.add(outputPanel);

        add(workPanel, BorderLayout.CENTER);

        // 状态栏
        var statusPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING));
        statusLabel.setFont(AppleDesign.CAPTION_FONT);
        statusLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
        
        setSize(600, 450);
        setLocationRelativeTo(null);
    }
    
    private void setupEventHandlers() {
        md5Button.addActionListener(e -> calculateHash("MD5"));
        sha1Button.addActionListener(e -> calculateHash("SHA-1"));
        sha256Button.addActionListener(e -> calculateHash("SHA-256"));
        copyButton.addActionListener(this::copyResult);
        clearButton.addActionListener(this::clearAll);
    }
    
    private void loadSampleData() {
        inputArea.setText("Hello World! 你好世界！");
    }
    
    private void calculateHash(String algorithm) {
        var input = inputArea.getText();
        if (input.isEmpty()) {
            statusLabel.setText("请输入要计算哈希的文本");
            return;
        }
        
        try {
            var digest = MessageDigest.getInstance(algorithm);
            var hashBytes = digest.digest(input.getBytes("UTF-8"));
            
            var hexString = new StringBuilder();
            for (var b : hashBytes) {
                var hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            var result = new StringBuilder();
            result.append("算法: ").append(algorithm).append("\n");
            result.append("输入长度: ").append(input.length()).append(" 字符\n");
            result.append("哈希值: ").append(hexString.toString().toUpperCase()).append("\n");
            
            outputArea.setText(result.toString());
            statusLabel.setText(algorithm + " 计算完成");
            
        } catch (Exception ex) {
            outputArea.setText("计算失败: " + ex.getMessage());
            statusLabel.setText("计算失败");
        }
    }
    
    private void copyResult(ActionEvent e) {
        var result = outputArea.getText();
        if (result.isEmpty()) {
            statusLabel.setText("没有可复制的内容");
            return;
        }
        
        // 提取哈希值
        var lines = result.split("\n");
        var hashValue = "";
        for (var line : lines) {
            if (line.startsWith("哈希值: ")) {
                hashValue = line.substring("哈希值: ".length());
                break;
            }
        }
        
        if (!hashValue.isEmpty()) {
            var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            var selection = new StringSelection(hashValue);
            clipboard.setContents(selection, null);
            statusLabel.setText("哈希值已复制到剪贴板");
        }
    }
    
    private void clearAll(ActionEvent e) {
        inputArea.setText("");
        outputArea.setText("");
        statusLabel.setText("已清空");
    }
}
