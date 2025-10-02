import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "📱 二维码生成器";

    // 主界面标题
    static final String MAIN_TITLE = "📱 二维码生成器";

    // 按钮文本
    static final String GENERATE_BUTTON = "生成二维码";
    static final String SAVE_BUTTON = "保存图片";
    static final String CLEAR_BUTTON = "清空";

    // 面板标题
    static final String INPUT_PANEL_TITLE = "输入内容";
    static final String DISPLAY_PANEL_TITLE = "二维码预览";
    static final String CONTROL_PANEL_TITLE = "控制选项";

    // 标签文本
    static final String SIZE_LABEL = "大小: %dpx";
    static final String INPUT_PLACEHOLDER = "在此输入要生成二维码的文本...";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_GENERATED = "二维码已生成";
    static final String STATUS_CLEARED = "内容已清空";
    static final String STATUS_SAVED = "图片已保存";

    // 错误消息
    static final String ERROR_NO_CONTENT = "请输入要生成二维码的内容";
    static final String ERROR_GENERATION_FAILED = "二维码生成失败";
    static final String ERROR_SAVE_FAILED = "保存图片失败: %s";
    static final String ERROR_NO_QR_CODE = "请先生成二维码";

    // 文件对话框
    static final String FILE_CHOOSER_TITLE = "保存二维码图片";
    static final String FILE_FILTER_PNG = "PNG图片 (*.png)";
    static final String FILE_FILTER_JPG = "JPEG图片 (*.jpg)";

    // 示例内容
    static final String SAMPLE_CONTENT = """
        https://github.com/your-repo

        这是一个二维码生成器示例。
        您可以输入任何文本、网址、联系信息等，
        然后生成对应的二维码。

        支持的功能：
        • 文本转二维码
        • 网址转二维码
        • 调整二维码大小
        • 保存为图片文件
        """;

    // 帮助信息
    static final String HELP_MESSAGE = """
        二维码生成器使用说明：

        • 在左侧文本框中输入要生成二维码的内容
        • 可以输入网址、文本、联系信息等
        • 使用滑块调整二维码图片的大小
        • 点击"生成二维码"按钮生成二维码
        • 点击"保存图片"按钮将二维码保存为图片文件
        • 点击"清空"按钮清除输入内容

        快捷键：
        Ctrl+G - 生成二维码
        Ctrl+S - 保存图片
        Ctrl+L - 清空内容
        F5 - 重新生成
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new QRCodeGenerator().setVisible(true);
    });
}

static class QRCodeGenerator extends JFrame {
    // ===== Apple设计系统常量 =====
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

    // 按钮尺寸
    private static final Dimension BUTTON_REGULAR = new Dimension(120, 44);
    private static final Dimension BUTTON_LARGE = new Dimension(160, 50);

    // ===== 应用组件 =====
    private final JTextArea inputArea;
    private final JPanel qrPanel;
    private final JButton generateButton;
    private final JButton saveButton;
    private final JButton clearButton;
    private final JLabel statusLabel;
    private final JSlider sizeSlider;
    private final JLabel sizeLabel;

    private BufferedImage qrImage;

    public QRCodeGenerator() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        inputArea = new JTextArea();
        qrPanel = new JPanel();
        generateButton = this.createPrimaryButton(Texts.GENERATE_BUTTON, this::generateQR);
        saveButton = this.createSecondaryButton(Texts.SAVE_BUTTON, this::saveQR);
        clearButton = this.createSecondaryButton(Texts.CLEAR_BUTTON, this::clearAll);
        statusLabel = new JLabel(Texts.STATUS_READY);
        sizeSlider = new JSlider(100, 500, 200);
        sizeLabel = new JLabel(String.format(Texts.SIZE_LABEL, 200));

        initializeGUI();
        setupEventHandlers();
        loadSampleData();
        setupKeyboardShortcuts();

        setSize(900, 700);
        setLocationRelativeTo(null);
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        createInputPanel();
        
        // 创建显示面板
        createDisplayPanel();
        
        // 创建控制面板
        createControlPanel();
        
        // 创建状态栏
        createStatusBar();
        
        setSize(600, 500);
        setLocationRelativeTo(null);
    }
    
    private void createInputPanel() {
        var inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入文本"));
        inputPanel.setPreferredSize(new Dimension(0, 120));
        
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setRows(4);
        
        var scrollPane = new JScrollPane(inputArea);
        inputPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(inputPanel, BorderLayout.NORTH);
    }
    
    private void createDisplayPanel() {
        var displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("二维码预览"));
        
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setPreferredSize(new Dimension(300, 300));
        qrPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // 添加自定义绘制
        qrPanel.add(new JLabel("请输入文本并点击生成", JLabel.CENTER));
        
        displayPanel.add(qrPanel, BorderLayout.CENTER);
        add(displayPanel, BorderLayout.CENTER);
    }
    
    private void createControlPanel() {
        var controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEtchedBorder());
        
        // 大小控制
        var sizePanel = new JPanel(new FlowLayout());
        sizePanel.add(new JLabel("二维码大小:"));
        sizeSlider.setMajorTickSpacing(100);
        sizeSlider.setMinorTickSpacing(50);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizePanel.add(sizeSlider);
        sizePanel.add(sizeLabel);
        
        // 按钮面板
        var buttonPanel = new JPanel(new FlowLayout());
        generateButton.setPreferredSize(new Dimension(120, 30));
        saveButton.setPreferredSize(new Dimension(100, 30));
        saveButton.setEnabled(false);
        clearButton.setPreferredSize(new Dimension(80, 30));
        
        buttonPanel.add(generateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        
        controlPanel.add(sizePanel);
        controlPanel.add(buttonPanel);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private void setupEventHandlers() {
        generateButton.addActionListener(this::generateQRCode);
        saveButton.addActionListener(this::saveQRCode);
        clearButton.addActionListener(this::clearAll);
        
        sizeSlider.addChangeListener(e -> {
            var size = sizeSlider.getValue();
            sizeLabel.setText("大小: " + size + "px");
        });
    }
    
    private void loadSampleData() {
        inputArea.setText(Texts.SAMPLE_CONTENT);
    }
    
    private void generateQRCode(ActionEvent e) {
        var text = inputArea.getText().trim();
        if (text.isEmpty()) {
            statusLabel.setText(Texts.ERROR_NO_CONTENT);
            return;
        }
        
        try {
            var size = sizeSlider.getValue();
            qrImage = createSimpleQRCode(text, size);
            
            // 更新显示面板
            qrPanel.removeAll();
            var imageLabel = new JLabel(new ImageIcon(qrImage));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            qrPanel.add(imageLabel);
            qrPanel.revalidate();
            qrPanel.repaint();
            
            saveButton.setEnabled(true);
            statusLabel.setText(Texts.STATUS_GENERATED + " - " + size + "x" + size + "px");
            
        } catch (Exception ex) {
            statusLabel.setText(Texts.ERROR_GENERATION_FAILED + ": " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "生成二维码失败: " + ex.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private BufferedImage createSimpleQRCode(String text, int size) {
        // 创建一个简单的模拟二维码图像
        // 注意：这是一个简化的实现，实际的二维码需要专门的库
        var image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        var g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size, size);
        
        // 黑色边框
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, size - 1, size - 1);
        
        // 生成基于文本的伪随机模式
        var random = new java.util.Random(text.hashCode());
        var moduleSize = Math.max(1, size / 25); // 25x25 模块
        
        // 绘制定位标记（三个角）
        drawPositionMarker(g2d, 0, 0, moduleSize * 7);
        drawPositionMarker(g2d, size - moduleSize * 7, 0, moduleSize * 7);
        drawPositionMarker(g2d, 0, size - moduleSize * 7, moduleSize * 7);
        
        // 绘制数据模块
        g2d.setColor(Color.BLACK);
        for (int x = moduleSize * 9; x < size - moduleSize * 9; x += moduleSize) {
            for (int y = moduleSize * 9; y < size - moduleSize * 9; y += moduleSize) {
                if (random.nextBoolean()) {
                    g2d.fillRect(x, y, moduleSize - 1, moduleSize - 1);
                }
            }
        }
        
        // 在中心添加文本信息
        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(8, size / 20)));
        var fm = g2d.getFontMetrics();
        var displayText = text.length() > 10 ? text.substring(0, 10) + "..." : text;
        var textWidth = fm.stringWidth(displayText);
        var textHeight = fm.getHeight();
        
        // 白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(size/2 - textWidth/2 - 5, size/2 - textHeight/2 - 5, 
                    textWidth + 10, textHeight + 10);
        
        // 蓝色文本
        g2d.setColor(Color.BLUE);
        g2d.drawString(displayText, size/2 - textWidth/2, size/2 + textHeight/4);
        
        g2d.dispose();
        return image;
    }
    
    private void drawPositionMarker(Graphics2D g2d, int x, int y, int size) {
        g2d.setColor(Color.BLACK);
        
        // 外框
        g2d.fillRect(x, y, size, size);
        
        // 内部白色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + size/7, y + size/7, size - 2*size/7, size - 2*size/7);
        
        // 中心黑点
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x + 2*size/7, y + 2*size/7, 3*size/7, 3*size/7);
    }
    
    private void saveQRCode(ActionEvent e) {
        if (qrImage == null) {
            statusLabel.setText(Texts.ERROR_NO_QR_CODE);
            return;
        }
        
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "PNG图片", "png"));
        fileChooser.setSelectedFile(new java.io.File("qrcode.png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new java.io.File(file.getAbsolutePath() + ".png");
                }
                
                javax.imageio.ImageIO.write(qrImage, "PNG", file);
                statusLabel.setText(Texts.STATUS_SAVED + ": " + file.getName());
                
            } catch (Exception ex) {
                statusLabel.setText(String.format(Texts.ERROR_SAVE_FAILED, ex.getMessage()));
                JOptionPane.showMessageDialog(this, 
                    "保存失败: " + ex.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearAll(ActionEvent e) {
        inputArea.setText("");
        qrPanel.removeAll();
        qrPanel.add(new JLabel("请输入文本并点击生成", JLabel.CENTER));
        qrPanel.revalidate();
        qrPanel.repaint();
        
        qrImage = null;
        saveButton.setEnabled(false);
        statusLabel.setText(Texts.STATUS_CLEARED);
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
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
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

    /**
     * 圆角边框类
     */
    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
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
                    case KeyEvent.VK_G:
                        // G键生成二维码
                        if (ev.isControlDown()) {
                            generateQR(new ActionEvent(QRCodeGenerator.this, ActionEvent.ACTION_PERFORMED, "generate"));
                        }
                        break;
                    case KeyEvent.VK_S:
                        // S键保存图片
                        if (ev.isControlDown()) {
                            saveQR(new ActionEvent(QRCodeGenerator.this, ActionEvent.ACTION_PERFORMED, "save"));
                        }
                        break;
                    case KeyEvent.VK_L:
                        // L键清空内容
                        if (ev.isControlDown()) {
                            clearAll(new ActionEvent(QRCodeGenerator.this, ActionEvent.ACTION_PERFORMED, "clear"));
                        }
                        break;
                    case KeyEvent.VK_F5:
                        // F5键重新生成
                        generateQR(new ActionEvent(QRCodeGenerator.this, ActionEvent.ACTION_PERFORMED, "generate"));
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

    // ActionEvent包装方法（用于键盘快捷键）
    private void generateQR(ActionEvent e) {
        generateQRCode(e);
    }

    private void saveQR(ActionEvent e) {
        saveQRCode(e);
    }
}
