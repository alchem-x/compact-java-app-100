import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.datatransfer.DataFlavor;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🎨 ASCII艺术生成器";

    // 界面标签
    static final String ORIGINAL_IMAGE_TITLE = "原始图片";
    static final String ASCII_ART_TITLE = "ASCII艺术";
    static final String WIDTH_LABEL = "宽度:";
    static final String CHAR_SET_LABEL = "字符集:";
    static final String INVERT_LABEL = "反转";
    static final String DRAG_DROP_HINT = "拖拽图片到此处或点击加载图片";
    static final String STATUS_READY = "请选择图片或输入文本";
    static final String STATUS_SAMPLE_TEXT = "显示示例文本";
    static final String STATUS_IMAGE_LOADED = "已加载图片: ";
    static final String STATUS_CONVERSION_COMPLETE = "转换完成 - 宽度: %d, 字符集: %s";
    static final String STATUS_TEXT_CONVERSION_COMPLETE = "文本转换完成";
    static final String STATUS_SAVED_TO = "已保存到: ";
    static final String STATUS_COPIED_TO_CLIPBOARD = "已复制到剪贴板";
    static final String STATUS_LOAD_IMAGE_FAILED = "加载图片失败";
    static final String STATUS_CONVERSION_FAILED = "转换失败";
    static final String STATUS_TEXT_CONVERSION_FAILED = "文本转换失败";
    static final String STATUS_SAVE_FAILED = "保存失败";
    static final String STATUS_COPY_FAILED = "复制失败";

    // 按钮文本
    static final String LOAD_IMAGE_BUTTON = "📷 加载图片";
    static final String TEXT_TO_ASCII_BUTTON = "📝 文本转ASCII";
    static final String CONVERT_BUTTON = "🔄 转换";
    static final String SAVE_BUTTON = "💾 保存";
    static final String COPY_BUTTON = "📋 复制";

    // 文件对话框
    static final String IMAGE_FILE_FILTER = "图片文件";
    static final String TEXT_FILE_FILTER = "文本文件";
    static final String SUPPORTED_FORMATS = "jpg,jpeg,png,gif,bmp";

    // 输入对话框
    static final String TEXT_INPUT_TITLE = "文本转ASCII";
    static final String TEXT_INPUT_MESSAGE = "输入要转换的文本:";

    // 错误消息
    static final String ERROR_LOAD_IMAGE_FAILED = "无法加载图片: ";
    static final String ERROR_CONVERSION_FAILED = "转换失败: ";
    static final String ERROR_TEXT_CONVERSION_FAILED = "文本转换失败: ";
    static final String ERROR_SAVE_FAILED = "保存失败: ";
    static final String ERROR_COPY_FAILED = "复制失败: ";
    static final String ERROR_NO_ASCII_TO_SAVE = "没有ASCII艺术可保存";
    static final String ERROR_NO_ASCII_TO_COPY = "没有ASCII艺术可复制";
    static final String ERROR_DRAG_DROP_FAILED = "无法加载图片: ";

    // 成功消息
    static final String SUCCESS_FILE_SAVED = "文件已保存";
    static final String SUCCESS_COPIED = "已复制到剪贴板";

    // 帮助信息
    static final String HELP_MESSAGE = """
        ASCII艺术生成器使用说明：

        • 加载图片：点击"加载图片"按钮选择图片文件，或拖拽图片到左侧区域
        • 文本转换：点击"文本转ASCII"按钮输入文本生成大字体ASCII艺术
        • 调整参数：使用宽度滑块调整输出宽度，选择不同的字符集
        • 反转效果：勾选反转选项可以反转黑白效果
        • 保存结果：点击"保存"按钮将ASCII艺术保存为文本文件
        • 复制结果：点击"复制"按钮将结果复制到剪贴板

        字符集说明：
        • 简单：使用基本字符，适合初学者
        • 详细：使用丰富的字符，效果更精细
        • 方块：使用方块字符，适合像素风格
        • 数字：使用数字字符，独特风格
        • 二进制：使用0和1，科技感十足

        使用技巧：
        • 支持拖拽图片文件直接加载
        • 可以调整输出宽度来控制细节程度
        • 不同的字符集会产生不同的视觉效果
        • 反转选项可以增强对比度

        快捷键：
        Ctrl+L - 加载图片
        Ctrl+T - 文本转ASCII
        Ctrl+C - 转换
        Ctrl+S - 保存
        Ctrl+P - 复制到剪贴板
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ASCIIArt().setVisible(true));
}

static class ASCIIArt extends JFrame {
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
    private BufferedImage originalImage;
    private String asciiResult = "";

    private JLabel imageLabel;
    private JTextArea asciiArea;
    private JSlider widthSlider;
    private JComboBox<CharacterSet> charSetCombo;
    private JCheckBox invertCheckBox;
    private JLabel statusLabel;

    public ASCIIArt() {
        this.initializeGUI();
        this.loadSampleText();
        this.setupKeyboardShortcuts();
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // 主面板 - 使用设计系统
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_20, SPACING_20, SPACING_20, SPACING_20));

        // 标题面板
        var titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(SYSTEM_BACKGROUND);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_16, 0));

        var titleLabel = new JLabel(Texts.WINDOW_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 控制面板
        var controlPanel = this.createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 中央分割面板
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(SPACING_4);

        // 左侧图片面板
        var imagePanel = this.createImagePanel();
        splitPane.setLeftComponent(imagePanel);

        // 右侧ASCII面板
        var asciiPanel = this.createASCIIPanel();
        splitPane.setRightComponent(asciiPanel);

        splitPane.setDividerLocation(400);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // 底部状态栏
        statusLabel = new JLabel(Texts.STATUS_READY);
        statusLabel.setFont(CAPTION1);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(SPACING_12, 0, 0, 0));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);

        // 设置窗口
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    private JPanel createControlPanel() {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_12, SPACING_8));
        panel.setBackground(SYSTEM_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));

        // 主要操作按钮
        var loadImageBtn = this.createPrimaryButton(Texts.LOAD_IMAGE_BUTTON);
        loadImageBtn.addActionListener((ev) -> this.loadImage());

        var textToAsciiBtn = this.createSecondaryButton(Texts.TEXT_TO_ASCII_BUTTON);
        textToAsciiBtn.addActionListener((ev) -> this.showTextDialog());

        panel.add(loadImageBtn);
        panel.add(textToAsciiBtn);
        panel.add(Box.createHorizontalStrut(SPACING_16));

        // 宽度控制
        var widthLabel = new JLabel(Texts.WIDTH_LABEL);
        widthLabel.setFont(CAPTION1);
        widthLabel.setForeground(SECONDARY_LABEL);
        panel.add(widthLabel);

        widthSlider = new JSlider(20, 200, 80);
        widthSlider.setPreferredSize(new Dimension(120, 20));
        widthSlider.setBackground(SYSTEM_BACKGROUND);
        widthSlider.addChangeListener((ev) -> {
            if (originalImage != null) {
                this.convertToASCII();
            }
        });
        panel.add(widthSlider);

        var widthValueLabel = new JLabel("80");
        widthValueLabel.setFont(CAPTION1);
        widthValueLabel.setForeground(LABEL);
        widthSlider.addChangeListener((ev) -> widthValueLabel.setText(String.valueOf(widthSlider.getValue())));
        panel.add(widthValueLabel);

        panel.add(Box.createHorizontalStrut(SPACING_16));

        // 字符集选择
        var charSetLabel = new JLabel(Texts.CHAR_SET_LABEL);
        charSetLabel.setFont(CAPTION1);
        charSetLabel.setForeground(SECONDARY_LABEL);
        panel.add(charSetLabel);

        charSetCombo = new JComboBox<>(CharacterSet.values());
        charSetCombo.setFont(CAPTION1);
        charSetCombo.setBackground(WHITE);
        charSetCombo.setBorder(new RoundedBorder(RADIUS_4));
        charSetCombo.addActionListener((ev) -> {
            if (originalImage != null) {
                this.convertToASCII();
            }
        });
        panel.add(charSetCombo);

        // 反转选项
        invertCheckBox = new JCheckBox(Texts.INVERT_LABEL);
        invertCheckBox.setFont(CAPTION1);
        invertCheckBox.setBackground(SYSTEM_BACKGROUND);
        invertCheckBox.addActionListener((ev) -> {
            if (originalImage != null) {
                this.convertToASCII();
            }
        });
        panel.add(invertCheckBox);

        panel.add(Box.createHorizontalStrut(SPACING_16));

        // 操作按钮
        var convertBtn = this.createPrimaryButton(Texts.CONVERT_BUTTON);
        convertBtn.addActionListener((ev) -> {
            if (originalImage != null) {
                this.convertToASCII();
            }
        });

        var saveBtn = this.createSecondaryButton(Texts.SAVE_BUTTON);
        saveBtn.addActionListener((ev) -> this.saveASCII());

        var copyBtn = this.createSecondaryButton(Texts.COPY_BUTTON);
        copyBtn.addActionListener((ev) -> this.copyToClipboard());

        panel.add(convertBtn);
        panel.add(saveBtn);
        panel.add(copyBtn);

        return panel;
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
    
    private JPanel createImagePanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 标题
        var titleLabel = new JLabel(Texts.ORIGINAL_IMAGE_TITLE);
        titleLabel.setFont(HEADLINE);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_12, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        imageLabel = new JLabel(Texts.DRAG_DROP_HINT, SwingConstants.CENTER);
        imageLabel.setFont(CALLOUT);
        imageLabel.setForeground(TERTIARY_LABEL);
        imageLabel.setPreferredSize(new Dimension(380, 300));
        imageLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createDashedBorder(GRAY3, 3, 3),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        imageLabel.setBackground(WHITE);
        imageLabel.setOpaque(true);

        // 添加拖拽支持
        this.setupDragAndDrop();

        var scrollPane = new JScrollPane(imageLabel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createASCIIPanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 标题
        var titleLabel = new JLabel(Texts.ASCII_ART_TITLE);
        titleLabel.setFont(HEADLINE);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_12, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        asciiArea = new JTextArea();
        asciiArea.setFont(new Font("Courier New", Font.PLAIN, 8));
        asciiArea.setEditable(false);
        asciiArea.setBackground(BLACK);
        asciiArea.setForeground(new Color(0, 255, 0)); // 亮绿色
        asciiArea.setBorder(BorderFactory.createEmptyBorder(SPACING_12, SPACING_12, SPACING_12, SPACING_12));

        var scrollPane = new JScrollPane(asciiArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(580, 400));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private void setupDragAndDrop() {
        imageLabel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    var files = (java.util.List<?>) support.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);

                    if (!files.isEmpty()) {
                        var file = (File) files.get(0);
                        ASCIIArt.this.loadImageFromFile(file);
                        return true;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ASCIIArt.this,
                        Texts.ERROR_DRAG_DROP_FAILED + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }
        });
    }
    
    private void loadSampleText() {
        asciiResult = generateTextASCII("ASCII\nART");
        asciiArea.setText(asciiResult);
        statusLabel.setText(Texts.STATUS_SAMPLE_TEXT);
    }
    
    private void loadImage() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            Texts.IMAGE_FILE_FILTER, Texts.SUPPORTED_FORMATS.split(",")));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.loadImageFromFile(fileChooser.getSelectedFile());
        }
    }

    private void loadImageFromFile(File file) {
        try {
            originalImage = ImageIO.read(file);

            // 缩放图片以适应显示
            var scaledImage = this.scaleImageForDisplay(originalImage, 380, 300);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setText("");

            this.convertToASCII();
            statusLabel.setText(Texts.STATUS_IMAGE_LOADED + file.getName());

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                Texts.ERROR_LOAD_IMAGE_FAILED + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText(Texts.STATUS_LOAD_IMAGE_FAILED);
        }
    }
    
    private BufferedImage scaleImageForDisplay(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        // 计算缩放比例
        double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
        
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);
        
        var scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        var g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return scaled;
    }
    
    private void convertToASCII() {
        if (originalImage == null) return;

        try {
            int asciiWidth = widthSlider.getValue();
            var charSet = (CharacterSet) charSetCombo.getSelectedItem();
            boolean invert = invertCheckBox.isSelected();

            asciiResult = this.imageToASCII(originalImage, asciiWidth, charSet, invert);
            asciiArea.setText(asciiResult);

            statusLabel.setText(String.format(Texts.STATUS_CONVERSION_COMPLETE,
                                            asciiWidth, charSet.name));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                Texts.ERROR_CONVERSION_FAILED + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText(Texts.STATUS_CONVERSION_FAILED);
        }
    }
    
    private String imageToASCII(BufferedImage image, int width, CharacterSet charSet, boolean invert) {
        // 计算高度，保持宽高比
        double aspectRatio = (double) image.getHeight() / image.getWidth();
        int height = (int) (width * aspectRatio * 0.5); // 0.5是字符宽高比调整
        
        // 缩放图片
        var scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        
        var result = new StringBuilder();
        String chars = charSet.characters;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = scaledImage.getRGB(x, y);
                int gray = getGrayValue(rgb);
                
                if (invert) {
                    gray = 255 - gray;
                }
                
                // 将灰度值映射到字符
                int charIndex = (gray * (chars.length() - 1)) / 255;
                result.append(chars.charAt(charIndex));
            }
            result.append('\n');
        }
        
        return result.toString();
    }
    
    private int getGrayValue(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        
        // 使用加权平均计算灰度
        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }
    
    private void showTextDialog() {
        String text = JOptionPane.showInputDialog(this,
            Texts.TEXT_INPUT_MESSAGE, Texts.TEXT_INPUT_TITLE, JOptionPane.QUESTION_MESSAGE);

        if (text != null && !text.trim().isEmpty()) {
            try {
                asciiResult = this.generateTextASCII(text.trim());
                asciiArea.setText(asciiResult);
                statusLabel.setText(Texts.STATUS_TEXT_CONVERSION_COMPLETE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    Texts.ERROR_TEXT_CONVERSION_FAILED + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText(Texts.STATUS_TEXT_CONVERSION_FAILED);
            }
        }
    }
    
    private String generateTextASCII(String text) {
        var result = new StringBuilder();
        String[] lines = text.split("\n");
        
        for (String line : lines) {
            // 生成大字体效果
            result.append(generateBigText(line)).append("\n");
        }
        
        return result.toString();
    }
    
    private String generateBigText(String text) {
        var result = new StringBuilder();
        
        // 简单的大字体生成（3行高度）
        String[] patterns = new String[3];
        patterns[0] = "";
        patterns[1] = "";
        patterns[2] = "";
        
        for (char c : text.toCharArray()) {
            String[] charPattern = getCharPattern(c);
            for (int i = 0; i < 3; i++) {
                patterns[i] += charPattern[i] + " ";
            }
        }
        
        for (String pattern : patterns) {
            result.append(pattern).append("\n");
        }
        
        return result.toString();
    }
    
    private String[] getCharPattern(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'A' -> new String[]{"███", "█ █", "███"};
            case 'B' -> new String[]{"██ ", "██ ", "██ "};
            case 'C' -> new String[]{"███", "█  ", "███"};
            case 'D' -> new String[]{"██ ", "█ █", "██ "};
            case 'E' -> new String[]{"███", "██ ", "███"};
            case 'F' -> new String[]{"███", "██ ", "█  "};
            case 'G' -> new String[]{"███", "█ █", "███"};
            case 'H' -> new String[]{"█ █", "███", "█ █"};
            case 'I' -> new String[]{"███", " █ ", "███"};
            case 'J' -> new String[]{"███", "  █", "██ "};
            case 'K' -> new String[]{"█ █", "██ ", "█ █"};
            case 'L' -> new String[]{"█  ", "█  ", "███"};
            case 'M' -> new String[]{"█ █", "███", "█ █"};
            case 'N' -> new String[]{"██ ", "█ █", " ██"};
            case 'O' -> new String[]{"███", "█ █", "███"};
            case 'P' -> new String[]{"███", "██ ", "█  "};
            case 'Q' -> new String[]{"███", "█ █", "██ "};
            case 'R' -> new String[]{"███", "██ ", "█ █"};
            case 'S' -> new String[]{"███", " ██", "███"};
            case 'T' -> new String[]{"███", " █ ", " █ "};
            case 'U' -> new String[]{"█ █", "█ █", "███"};
            case 'V' -> new String[]{"█ █", "█ █", " █ "};
            case 'W' -> new String[]{"█ █", "███", "█ █"};
            case 'X' -> new String[]{"█ █", " █ ", "█ █"};
            case 'Y' -> new String[]{"█ █", " █ ", " █ "};
            case 'Z' -> new String[]{"███", " █ ", "███"};
            case ' ' -> new String[]{"   ", "   ", "   "};
            default -> new String[]{"█ █", " █ ", "█ █"};
        };
    }
    
    private void saveASCII() {
        if (asciiResult.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                Texts.ERROR_NO_ASCII_TO_SAVE, "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            Texts.TEXT_FILE_FILTER, "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (var writer = new FileWriter(fileChooser.getSelectedFile())) {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                }

                writer.write(asciiResult);
                statusLabel.setText(Texts.STATUS_SAVED_TO + file.getName());
                JOptionPane.showMessageDialog(this,
                    Texts.SUCCESS_FILE_SAVED, "成功", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    Texts.ERROR_SAVE_FAILED + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText(Texts.STATUS_SAVE_FAILED);
            }
        }
    }
    
    private void copyToClipboard() {
        if (asciiResult.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                Texts.ERROR_NO_ASCII_TO_COPY, "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            var stringSelection = new java.awt.datatransfer.StringSelection(asciiResult);
            clipboard.setContents(stringSelection, null);

            statusLabel.setText(Texts.STATUS_COPIED_TO_CLIPBOARD);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                Texts.ERROR_COPY_FAILED + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText(Texts.STATUS_COPY_FAILED);
        }
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_L:
                        // Ctrl+L 加载图片
                        if (ev.isControlDown()) {
                            loadImage();
                        }
                        break;
                    case KeyEvent.VK_T:
                        // Ctrl+T 文本转ASCII
                        if (ev.isControlDown()) {
                            showTextDialog();
                        }
                        break;
                    case KeyEvent.VK_C:
                        // Ctrl+C 转换
                        if (ev.isControlDown()) {
                            if (originalImage != null) {
                                convertToASCII();
                            }
                        }
                        break;
                    case KeyEvent.VK_S:
                        // Ctrl+S 保存
                        if (ev.isControlDown()) {
                            saveASCII();
                        }
                        break;
                    case KeyEvent.VK_P:
                        // Ctrl+P 复制到剪贴板
                        if (ev.isControlDown()) {
                            copyToClipboard();
                        }
                        break;
                    case KeyEvent.VK_H:
                        // Ctrl+H 显示帮助
                        if (ev.isControlDown()) {
                            showHelp();
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

    // 字符集枚举
    enum CharacterSet {
        SIMPLE("简单", " .:-=+*#%@"),
        DETAILED("详细", " .'`^\",:;Il!i><~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$"),
        BLOCKS("方块", " ░▒▓█"),
        NUMBERS("数字", " 123456789"),
        BINARY("二进制", " 01");
        
        final String name;
        final String characters;
        
        CharacterSet(String name, String characters) {
            this.name = name;
            this.characters = characters;
        }
        
        @Override
        public String toString() {
            return name;
        }
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
            return new Insets(1, 1, 1, 1);
        }
    }
}
