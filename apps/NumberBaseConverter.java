import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

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

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🔢 进制转换器";

    // 主界面标题
    static final String MAIN_TITLE = "🔢 数字进制转换器";

    // 按钮文本
    static final String CONVERT_BUTTON = "转换";
    static final String CLEAR_BUTTON = "清空";
    static final String COPY_ALL_BUTTON = "复制全部";
    static final String COPY_BINARY_BUTTON = "复制二进制";
    static final String COPY_OCTAL_BUTTON = "复制八进制";
    static final String COPY_DECIMAL_BUTTON = "复制十进制";
    static final String COPY_HEX_BUTTON = "复制十六进制";

    // 面板标题
    static final String INPUT_PANEL_TITLE = "输入数字";
    static final String OUTPUT_PANEL_TITLE = "转换结果";
    static final String BIT_PANEL_TITLE = "位表示";
    static final String HISTORY_PANEL_TITLE = "历史记录";

    // 标签文本
    static final String DECIMAL_LABEL = "十进制:";
    static final String BINARY_LABEL = "二进制:";
    static final String OCTAL_LABEL = "八进制:";
    static final String HEX_LABEL = "十六进制:";
    static final String STATUS_READY = "就绪";
    static final String STATUS_CONVERTED = "转换完成";
    static final String STATUS_CLEARED = "内容已清空";
    static final String STATUS_COPIED = "已复制到剪贴板";

    // 错误消息
    static final String ERROR_INVALID_INPUT = "请输入有效的数字";
    static final String ERROR_NUMBER_TOO_LARGE = "数字太大，超出处理范围";
    static final String ERROR_COPY_FAILED = "复制到剪贴板失败";

    // 示例数据
    static final String SAMPLE_DECIMAL = "255";
    static final String SAMPLE_BINARY = "11111111";
    static final String SAMPLE_OCTAL = "377";
    static final String SAMPLE_HEX = "FF";

    // 进制说明
    static final String BINARY_DESCRIPTION = "二进制 (基数2): 使用0和1表示数字";
    static final String OCTAL_DESCRIPTION = "八进制 (基数8): 使用0-7表示数字";
    static final String DECIMAL_DESCRIPTION = "十进制 (基数10): 使用0-9表示数字";
    static final String HEX_DESCRIPTION = "十六进制 (基数16): 使用0-9和A-F表示数字";

    // 位表示说明
    static final String BIT_DESCRIPTION = "位表示显示了数字在内存中的二进制形式";
    static final String BIT_PATTERN_DESCRIPTION = "每个位代表一个2的幂次方";

    // 文件对话框
    static final String FILE_CHOOSER_SAVE_TITLE = "保存进制转换结果";
    static final String FILE_FILTER_TEXT = "文本文件 (*.txt)";

    // 帮助信息
    static final String HELP_MESSAGE = """
        数字进制转换器使用说明：

        • 在任意进制输入框中输入数字
        • 点击"转换"按钮或按回车键进行转换
        • 支持二进制、八进制、十进制、十六进制之间的相互转换
        • 位表示面板显示数字的二进制位模式
        • 使用复制按钮可以快速复制转换结果

        进制说明：
        • 二进制 (Base 2): 使用0和1，计算机的基础进制
        • 八进制 (Base 8): 使用0-7，早期计算机常用
        • 十进制 (Base 10): 使用0-9，人类常用进制
        • 十六进制 (Base 16): 使用0-9和A-F，编程常用

        使用技巧：
        • 输入十进制255可以查看其二进制、八进制、十六进制表示
        • 输入二进制11111111可以查看其他进制表示
        • 位表示有助于理解数字的存储方式

        快捷键：
        Ctrl+C - 转换数字
        Ctrl+L - 清空
        Ctrl+B - 复制二进制
        Ctrl+O - 复制八进制
        Ctrl+D - 复制十进制
        Ctrl+H - 复制十六进制
        Ctrl+A - 复制全部
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new NumberBaseConverter().setVisible(true));
}

static class NumberBaseConverter extends JFrame {
    private final JTextField decimalField;
    private final JTextField binaryField;
    private final JTextField octalField;
    private final JTextField hexField;
    private final JButton convertButton;
    private final JButton clearButton;
    private final JButton copyAllButton;
    private final JLabel statusLabel;
    private final JTextArea bitRepresentationArea;
    
    public NumberBaseConverter() {
        decimalField = new JTextField();
        binaryField = new JTextField();
        octalField = new JTextField();
        hexField = new JTextField();
        convertButton = new JButton(Texts.CONVERT_BUTTON);
        clearButton = new JButton(Texts.CLEAR_BUTTON);
        copyAllButton = new JButton(Texts.COPY_ALL_BUTTON);
        statusLabel = new JLabel(Texts.STATUS_READY);
        bitRepresentationArea = new JTextArea();

        initializeGUI();
        setupEventHandlers();
        loadSampleData();
        setupKeyboardShortcuts();
    }
    
    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        
        // 创建主面板
        createMainPanel();
        
        // 创建控制面板
        createControlPanel();
        
        // 创建状态栏
        createStatusBar();
        
        setSize(500, 400);
        setLocationRelativeTo(null);
    }
    
    private void createMainPanel() {
        var mainPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.EXTRA_LARGE_SPACING, AppleDesign.EXTRA_LARGE_SPACING, AppleDesign.EXTRA_LARGE_SPACING, AppleDesign.EXTRA_LARGE_SPACING));

        // 标题
        var titleLabel = new JLabel(Texts.MAIN_TITLE);
        titleLabel.setFont(AppleDesign.HEADLINE_FONT);
        titleLabel.setForeground(AppleDesign.SYSTEM_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, AppleDesign.LARGE_SPACING, 0));
        mainPanel.add(titleLabel);

        // 进制输入面板
        var inputPanel = AppleDesign.createRoundedPanel(AppleDesign.SMALL_RADIUS, AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        inputPanel.setLayout(new GridLayout(4, 2, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        // 十进制
        var decLabel = new JLabel(Texts.DECIMAL_LABEL + " (DEC):");
        decLabel.setFont(AppleDesign.BODY_FONT);
        decLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        inputPanel.add(decLabel);
        decimalField.setFont(AppleDesign.MONO_FONT);
        decimalField.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        decimalField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppleDesign.SYSTEM_GRAY4),
            BorderFactory.createEmptyBorder(AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING)
        ));
        inputPanel.add(decimalField);

        // 二进制
        var binLabel = new JLabel(Texts.BINARY_LABEL + " (BIN):");
        binLabel.setFont(AppleDesign.BODY_FONT);
        binLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        inputPanel.add(binLabel);
        binaryField.setFont(AppleDesign.MONO_FONT);
        binaryField.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        binaryField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppleDesign.SYSTEM_GRAY4),
            BorderFactory.createEmptyBorder(AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING)
        ));
        inputPanel.add(binaryField);

        // 八进制
        var octLabel = new JLabel(Texts.OCTAL_LABEL + " (OCT):");
        octLabel.setFont(AppleDesign.BODY_FONT);
        octLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        inputPanel.add(octLabel);
        octalField.setFont(AppleDesign.MONO_FONT);
        octalField.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        octalField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppleDesign.SYSTEM_GRAY4),
            BorderFactory.createEmptyBorder(AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING)
        ));
        inputPanel.add(octalField);

        // 十六进制
        var hexLabel = new JLabel(Texts.HEX_LABEL + " (HEX):");
        hexLabel.setFont(AppleDesign.BODY_FONT);
        hexLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        inputPanel.add(hexLabel);
        hexField.setFont(AppleDesign.MONO_FONT);
        hexField.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        hexField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppleDesign.SYSTEM_GRAY4),
            BorderFactory.createEmptyBorder(AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.MEDIUM_SPACING)
        ));
        inputPanel.add(hexField);

        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(AppleDesign.LARGE_SPACING));

        // 位表示面板
        var bitPanel = AppleDesign.createRoundedPanel(AppleDesign.SMALL_RADIUS, AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        bitPanel.setLayout(new BorderLayout());
        bitPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        var bitTitle = new JLabel(Texts.BIT_PANEL_TITLE);
        bitTitle.setFont(AppleDesign.TITLE_FONT);
        bitTitle.setForeground(AppleDesign.SYSTEM_GREEN);
        bitTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, AppleDesign.MEDIUM_SPACING, 0));
        bitPanel.add(bitTitle, BorderLayout.NORTH);

        bitRepresentationArea.setFont(AppleDesign.MONO_FONT);
        bitRepresentationArea.setEditable(false);
        bitRepresentationArea.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        bitRepresentationArea.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        bitRepresentationArea.setRows(3);

        var bitScrollPane = new JScrollPane(bitRepresentationArea);
        bitScrollPane.setBorder(BorderFactory.createEmptyBorder());
        bitPanel.add(bitScrollPane, BorderLayout.CENTER);

        mainPanel.add(bitPanel);

        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void createControlPanel() {
        var controlPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.EXTRA_LARGE_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.EXTRA_LARGE_SPACING));

        AppleDesign.styleButton(convertButton, AppleDesign.SYSTEM_BLUE, Color.WHITE);
        AppleDesign.styleButton(clearButton, AppleDesign.SYSTEM_GRAY, Color.WHITE);
        AppleDesign.styleButton(copyAllButton, AppleDesign.SYSTEM_INDIGO, Color.WHITE);

        controlPanel.add(convertButton);
        controlPanel.add(clearButton);
        controlPanel.add(copyAllButton);

        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void createStatusBar() {
        var statusPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.SMALL_SPACING, AppleDesign.EXTRA_LARGE_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.EXTRA_LARGE_SPACING));
        statusLabel.setFont(AppleDesign.CAPTION_FONT);
        statusLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_C:
                        // C键转换数字
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
                    case KeyEvent.VK_B:
                        // B键复制二进制
                        if (ev.isControlDown()) {
                            copyBinary(null);
                        }
                        break;
                    case KeyEvent.VK_O:
                        // O键复制八进制
                        if (ev.isControlDown()) {
                            copyOctal(null);
                        }
                        break;
                    case KeyEvent.VK_D:
                        // D键复制十进制
                        if (ev.isControlDown()) {
                            copyDecimal(null);
                        }
                        break;
                    case KeyEvent.VK_H:
                        // H键复制十六进制
                        if (ev.isControlDown()) {
                            copyHex(null);
                        }
                        break;
                    case KeyEvent.VK_A:
                        // A键复制全部
                        if (ev.isControlDown()) {
                            copyAllResults(null);
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

    private void showHelp() {
        JOptionPane.showMessageDialog(this, Texts.HELP_MESSAGE, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupEventHandlers() {
        convertButton.addActionListener(this::performConversion);
        clearButton.addActionListener(this::clearAll);
        copyAllButton.addActionListener(this::copyAllResults);
        
        // 为每个输入框添加回车键监听
        decimalField.addActionListener(this::performConversion);
        binaryField.addActionListener(this::performConversion);
        octalField.addActionListener(this::performConversion);
        hexField.addActionListener(this::performConversion);
    }
    
    private void loadSampleData() {
        decimalField.setText(Texts.SAMPLE_DECIMAL);
        performConversion(null);
    }
    
    private void performConversion(ActionEvent e) {
        try {
            long value = 0;
            var sourceField = "";
            
            // 确定输入源
            if (!decimalField.getText().trim().isEmpty()) {
                value = Long.parseLong(decimalField.getText().trim());
                sourceField = "十进制";
            } else if (!binaryField.getText().trim().isEmpty()) {
                value = Long.parseLong(binaryField.getText().trim(), 2);
                sourceField = "二进制";
            } else if (!octalField.getText().trim().isEmpty()) {
                value = Long.parseLong(octalField.getText().trim(), 8);
                sourceField = "八进制";
            } else if (!hexField.getText().trim().isEmpty()) {
                value = Long.parseLong(hexField.getText().trim(), 16);
                sourceField = "十六进制";
            } else {
                statusLabel.setText(Texts.ERROR_INVALID_INPUT);
                return;
            }
            
            // 更新所有字段
            updateAllFields(value);
            
            // 更新位表示
            updateBitRepresentation(value);
            
            statusLabel.setText(Texts.STATUS_CONVERTED + " - 源: " + sourceField + " (" + value + ")");
            
        } catch (NumberFormatException ex) {
            statusLabel.setText(Texts.ERROR_INVALID_INPUT);
            JOptionPane.showMessageDialog(this, 
                "数值格式错误，请检查输入", 
                "格式错误", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            statusLabel.setText("转换失败");
            JOptionPane.showMessageDialog(this, 
                "转换失败: " + ex.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateAllFields(long value) {
        decimalField.setText(String.valueOf(value));
        binaryField.setText(Long.toBinaryString(value));
        octalField.setText(Long.toOctalString(value));
        hexField.setText(Long.toHexString(value).toUpperCase());
    }
    
    private void updateBitRepresentation(long value) {
        var binaryStr = String.format("%32s", Long.toBinaryString(value)).replace(' ', '0');
        
        var representation = new StringBuilder();
        representation.append(Texts.BIT_PANEL_TITLE + ":\n");
        
        // 分组显示，每4位一组
        for (int i = 0; i < 32; i += 4) {
            if (i > 0 && i % 16 == 0) {
                representation.append("\n");
            }
            representation.append(binaryStr.substring(i, i + 4)).append(" ");
        }
        
        representation.append("\n\n");
        representation.append("数值信息:\n");
        representation.append("有符号32位整数: ").append((int) value).append("\n");
        representation.append("无符号32位整数: ").append(value & 0xFFFFFFFFL).append("\n");
        
        // 字节表示
        representation.append("字节表示: ");
        for (int i = 24; i >= 0; i -= 8) {
            var byteValue = (value >> i) & 0xFF;
            representation.append(String.format("%02X ", byteValue));
        }
        
        bitRepresentationArea.setText(representation.toString());
    }
    
    private void clearAll(ActionEvent e) {
        decimalField.setText("");
        binaryField.setText("");
        octalField.setText("");
        hexField.setText("");
        bitRepresentationArea.setText("");
        statusLabel.setText(Texts.STATUS_CLEARED);
    }
    
    private void copyAllResults(ActionEvent e) {
        if (decimalField.getText().trim().isEmpty()) {
            statusLabel.setText("没有可复制的内容");
            return;
        }
        
        var result = new StringBuilder();
        result.append("进制转换结果:\n");
        result.append(Texts.DECIMAL_LABEL + " ").append(decimalField.getText()).append("\n");
        result.append(Texts.BINARY_LABEL + " ").append(binaryField.getText()).append("\n");
        result.append(Texts.OCTAL_LABEL + " ").append(octalField.getText()).append("\n");
        result.append(Texts.HEX_LABEL + " ").append(hexField.getText()).append("\n");
        result.append("\n").append(bitRepresentationArea.getText());
        
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(result.toString());
        clipboard.setContents(selection, null);
        
        statusLabel.setText(Texts.STATUS_COPIED);
    }

    private void copyBinary(ActionEvent e) {
        copyIndividualResult(binaryField.getText(), "二进制");
    }

    private void copyOctal(ActionEvent e) {
        copyIndividualResult(octalField.getText(), "八进制");
    }

    private void copyDecimal(ActionEvent e) {
        copyIndividualResult(decimalField.getText(), "十进制");
    }

    private void copyHex(ActionEvent e) {
        copyIndividualResult(hexField.getText(), "十六进制");
    }

    private void copyIndividualResult(String text, String type) {
        if (text.trim().isEmpty()) {
            statusLabel.setText("没有可复制的" + type + "内容");
            return;
        }

        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(text);
        clipboard.setContents(selection, null);

        statusLabel.setText(type + "已复制到剪贴板");
    }
}
