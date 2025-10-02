import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "📝 Markdown查看器";

    // 工具栏按钮
    static final String TOOLBAR_OPEN = "打开";
    static final String TOOLBAR_SAVE = "保存";
    static final String TOOLBAR_REFRESH_PREVIEW = "刷新预览";

    // 面板标题
    static final String EDITOR_PANEL_TITLE = "Markdown编辑器";
    static final String PREVIEW_PANEL_TITLE = "预览";

    // 文件对话框
    static final String FILE_CHOOSER_TITLE_OPEN = "打开Markdown文件";
    static final String FILE_CHOOSER_TITLE_SAVE = "保存Markdown文件";

    // 文件过滤器
    static final String FILE_FILTER_MARKDOWN = "Markdown文件";
    static final String FILE_FILTER_EXTENSIONS = "md,markdown,txt";

    // 状态消息
    static final String STATUS_FILE_OPENED = "文件已打开: %s";
    static final String STATUS_FILE_SAVED = "文件已保存";
    static final String STATUS_PREVIEW_UPDATED = "预览已更新";

    // 错误消息
    static final String ERROR_OPEN_FILE = "无法打开文件: %s";
    static final String ERROR_SAVE_FILE = "无法保存文件: %s";

    // 示例内容
    static final String SAMPLE_TITLE = "Markdown查看器示例";
    static final String SAMPLE_HEADER = "标题";
    static final String SAMPLE_TEXT = "这是一个**粗体**和*斜体*的示例。";
    static final String SAMPLE_LIST_HEADER = "列表";
    static final String SAMPLE_LIST_ITEMS = "项目 1\n项目 2\n项目 3";
    static final String SAMPLE_CODE_HEADER = "代码";
    static final String SAMPLE_CODE = """
        ```java
        public class Hello {
            public static void main(String[] args) {
                System.out.println("Hello, World!");
            }
        }
        ```
        """;
}

/**
 * Markdown预览器
 * 支持实时预览Markdown文档
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new MarkdownViewer().setVisible(true);
    });
}

class MarkdownViewer extends JFrame {
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
    private static final Font MONO = new Font("SF Mono", Font.PLAIN, 12);

    // 间距
    private static final int SPACING_2 = 2;
    private static final int SPACING_4 = 4;
    private static final int SPACING_8 = 8;
    private static final int SPACING_12 = 12;
    private static final int SPACING_16 = 16;
    private static final int SPACING_20 = 20;
    private static final int SPACING_24 = 24;
    private static final int SPACING_32 = 32;

    // 按钮尺寸
    private static final Dimension BUTTON_REGULAR = new Dimension(120, 44);
    private static final Dimension BUTTON_LARGE = new Dimension(160, 50);

    // 圆角
    private static final int RADIUS_4 = 4;
    private static final int RADIUS_8 = 8;
    private static final int RADIUS_12 = 12;
    private static final int RADIUS_16 = 16;

    // ===== 应用组件 =====
    private JTextArea markdownArea;
    private JEditorPane previewPane;
    private File currentFile;
    private JLabel statusLabel;
    
    public MarkdownViewer() {
        this.initializeUI();
        this.setupEventHandlers();
        this.loadSampleMarkdown();
        this.setupKeyboardShortcuts();
    }
    
    private void initializeUI() {
        setTitle(Texts.WINDOW_TITLE);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 创建状态栏
        statusLabel = new JLabel();
        statusLabel.setFont(CAPTION1);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16));
        add(statusLabel, BorderLayout.SOUTH);

        // 创建主面板
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);

        // 创建工具栏 - 使用苹果风格
        var toolBar = this.createToolBar();
        mainPanel.add(toolBar, BorderLayout.NORTH);

        // 创建分割面板 - 使用苹果风格
        var splitPane = this.createSplitPane();
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createToolBar() {
        var toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_12, SPACING_8));
        toolBar.setBackground(SYSTEM_BACKGROUND);
        toolBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));

        var openBtn = this.createPrimaryButton(Texts.TOOLBAR_OPEN, this::openFile);
        var saveBtn = this.createSecondaryButton(Texts.TOOLBAR_SAVE, this::saveFile);
        var previewBtn = this.createSecondaryButton(Texts.TOOLBAR_REFRESH_PREVIEW, this::updatePreview);

        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.add(previewBtn);

        return toolBar;
    }

    private JSplitPane createSplitPane() {
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        // 左侧编辑器 - 使用苹果风格
        markdownArea = new JTextArea();
        markdownArea.setFont(MONO);
        markdownArea.setBackground(GRAY6);
        markdownArea.setForeground(LABEL);
        markdownArea.setCaretColor(LABEL);
        markdownArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        markdownArea.setLineWrap(true);
        markdownArea.setWrapStyleWord(true);
        markdownArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });

        var editorScrollPane = new JScrollPane(markdownArea);
        editorScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder(Texts.EDITOR_PANEL_TITLE)),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        editorScrollPane.getViewport().setBackground(GRAY6);

        // 右侧预览器 - 使用苹果风格
        previewPane = new JEditorPane();
        previewPane.setContentType("text/html");
        previewPane.setEditorKit(new HTMLEditorKit());
        previewPane.setEditable(false);
        previewPane.setBackground(WHITE);
        previewPane.setForeground(LABEL);
        previewPane.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));

        var previewScrollPane = new JScrollPane(previewPane);
        previewScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder(Texts.PREVIEW_PANEL_TITLE)),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        previewScrollPane.getViewport().setBackground(WHITE);

        splitPane.setLeftComponent(editorScrollPane);
        splitPane.setRightComponent(previewScrollPane);

        return splitPane;
    }

    // ===== 按钮创建方法 =====
    private JButton createPrimaryButton(String text, ActionListener action) {
        return this.createStyledButton(text, BLUE, WHITE, action);
    }

    private JButton createSecondaryButton(String text, ActionListener action) {
        return this.createStyledButton(text, GRAY6, LABEL, action);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor, ActionListener action) {
        var button = new JButton(text);
        button.setFont(BODY);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16),
            BorderFactory.createLineBorder(GRAY4, 1)
        ));
        button.setPreferredSize(BUTTON_REGULAR);

        // 设置悬停效果
        this.setupButtonHoverEffect(button, backgroundColor);

        // 添加动作监听器
        button.addActionListener(action);

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
    
    // ===== 事件处理方法 =====
    private void setupEventHandlers() {
        // 添加键盘快捷键
        markdownArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_S:
                        // Ctrl+S 保存
                        if (ev.isControlDown()) {
                            saveFile(new ActionEvent(MarkdownViewer.this, ActionEvent.ACTION_PERFORMED, "save"));
                        }
                        break;
                    case KeyEvent.VK_O:
                        // Ctrl+O 打开
                        if (ev.isControlDown()) {
                            openFile(new ActionEvent(MarkdownViewer.this, ActionEvent.ACTION_PERFORMED, "open"));
                        }
                        break;
                    case KeyEvent.VK_R:
                        // Ctrl+R 刷新预览
                        if (ev.isControlDown()) {
                            updatePreview(new ActionEvent(MarkdownViewer.this, ActionEvent.ACTION_PERFORMED, "refresh"));
                        }
                        break;
                    default:
                        return;
                }
            }
        });
    }

    private void setupKeyboardShortcuts() {
        // 添加窗口级别的键盘快捷键
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_F5:
                        // F5 刷新预览
                        updatePreview(new ActionEvent(MarkdownViewer.this, ActionEvent.ACTION_PERFORMED, "refresh"));
                        break;
                    case KeyEvent.VK_F1:
                        // F1 显示帮助
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
        var help = """
            Markdown查看器快捷键:

            Ctrl+O - 打开文件
            Ctrl+S - 保存文件
            Ctrl+R - 刷新预览
            F5   - 刷新预览
            F1   - 显示帮助
            """;
        JOptionPane.showMessageDialog(this, help, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadSampleMarkdown() {
        var sample = String.format("""
            # %s

            ## %s
            %s

            ## %s
            - %s
            - %s
            - %s

            ## %s
            %s
            """,
            Texts.SAMPLE_TITLE,
            Texts.SAMPLE_HEADER,
            Texts.SAMPLE_TEXT,
            Texts.SAMPLE_LIST_HEADER,
            Texts.SAMPLE_LIST_ITEMS.split("\\n")[0],
            Texts.SAMPLE_LIST_ITEMS.split("\\n")[1],
            Texts.SAMPLE_LIST_ITEMS.split("\\n")[2],
            Texts.SAMPLE_CODE_HEADER,
            Texts.SAMPLE_CODE
        );

        markdownArea.setText(sample);
        this.updatePreview();
        this.statusLabel.setText(Texts.STATUS_PREVIEW_UPDATED);
    }
    
    private void openFile(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Texts.FILE_CHOOSER_TITLE_OPEN);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            Texts.FILE_FILTER_MARKDOWN, Texts.FILE_FILTER_EXTENSIONS.split(",")));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                currentFile = fileChooser.getSelectedFile();
                var content = Files.readString(currentFile.toPath());
                markdownArea.setText(content);
                this.updatePreview();
                this.statusLabel.setText(String.format(Texts.STATUS_FILE_OPENED, currentFile.getName()));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, String.format(Texts.ERROR_OPEN_FILE, ex.getMessage()));
            }
        }
    }
    
    private void saveFile(ActionEvent e) {
        if (currentFile == null) {
            var fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(Texts.FILE_CHOOSER_TITLE_SAVE);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                Texts.FILE_FILTER_MARKDOWN, Texts.FILE_FILTER_EXTENSIONS.split(",")));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
            } else {
                return;
            }
        }

        try {
            Files.writeString(currentFile.toPath(), markdownArea.getText());
            this.statusLabel.setText(Texts.STATUS_FILE_SAVED);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, String.format(Texts.ERROR_SAVE_FILE, ex.getMessage()));
        }
    }
    
    private void updatePreview(ActionEvent e) {
        this.updatePreview();
    }

    private void updatePreview() {
        String markdown = markdownArea.getText();
        String html = markdownToHTML(markdown);
        previewPane.setText(html);
        this.statusLabel.setText(Texts.STATUS_PREVIEW_UPDATED);
    }
    
    private String markdownToHTML(String markdown) {
        var html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: 'SF Pro Text', -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif; margin: 20px; background-color: ").append(String.format("#%06x", WHITE.getRGB() & 0x00FFFFFF)).append("; color: ").append(String.format("#%06x", LABEL.getRGB() & 0x00FFFFFF)).append("; line-height: 1.6; }");
        html.append("h1, h2, h3, h4, h5, h6 { font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif; font-weight: 600; margin-top: 24px; margin-bottom: 16px; }");
        html.append("h1 { font-size: 32px; color: ").append(String.format("#%06x", LABEL.getRGB() & 0x00FFFFFF)).append("; }");
        html.append("h2 { font-size: 24px; color: ").append(String.format("#%06x", SECONDARY_LABEL.getRGB() & 0x00FFFFFF)).append("; }");
        html.append("p { margin-bottom: 16px; }");
        html.append("code { background-color: ").append(String.format("#%06x", GRAY6.getRGB() & 0x00FFFFFF)).append("; padding: 2px 6px; border-radius: 4px; font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, monospace; font-size: 14px; }");
        html.append("pre { background-color: ").append(String.format("#%06x", GRAY6.getRGB() & 0x00FFFFFF)).append("; padding: 16px; border-radius: 8px; overflow-x: auto; }");
        html.append("pre code { background-color: transparent; padding: 0; }");
        html.append("li { margin-bottom: 8px; }");
        html.append("strong { font-weight: 600; }");
        html.append("em { font-style: italic; }");
        html.append("</style></head><body>");
        
        String[] lines = markdown.split("\n");
        boolean inCodeBlock = false;
        
        for (String line : lines) {
            if (line.trim().startsWith("```")) {
                if (inCodeBlock) {
                    html.append("</pre>");
                    inCodeBlock = false;
                } else {
                    html.append("<pre><code>");
                    inCodeBlock = true;
                }
                continue;
            }
            
            if (inCodeBlock) {
                html.append(escapeHtml(line)).append("\n");
                continue;
            }
            
            line = line.trim();
            if (line.isEmpty()) {
                html.append("<br>");
                continue;
            }
            
            // 标题
            if (line.startsWith("#")) {
                int level = 0;
                while (level < line.length() && line.charAt(level) == '#') {
                    level++;
                }
                String headerText = line.substring(level).trim();
                headerText = processInlineMarkdown(headerText);
                html.append("<h").append(level).append(">")
                    .append(headerText)
                    .append("</h").append(level).append(">");
                continue;
            }
            
            // 列表
            if (line.startsWith("- ")) {
                String listItem = line.substring(2).trim();
                listItem = processInlineMarkdown(listItem);
                html.append("<li>").append(listItem).append("</li>");
                continue;
            }
            
            // 普通段落
            line = processInlineMarkdown(line);
            html.append("<p>").append(line).append("</p>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }
    
    private String processInlineMarkdown(String text) {
        text = text.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        text = text.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        text = text.replaceAll("`(.*?)`", "<code>$1</code>");
        return text;
    }
    
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;");
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
