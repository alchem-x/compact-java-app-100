import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.List;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "📋 文本比较工具";

    // 按钮文本
    static final String COMPARE_BUTTON = "比较";
    static final String CLEAR_BUTTON = "清空";
    static final String SWAP_BUTTON = "交换";

    // 面板标题
    static final String TEXT_A_PANEL_TITLE = "文本A";
    static final String TEXT_B_PANEL_TITLE = "文本B";
    static final String RESULT_PANEL_TITLE = "比较结果";

    // 复选框文本
    static final String IGNORE_CASE_CHECKBOX = "忽略大小写";
    static final String IGNORE_WHITESPACE_CHECKBOX = "忽略空白字符";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_BOTH_EMPTY = "两个文本都为空";
    static final String STATUS_COMPARISON_COMPLETE = "比较完成";
    static final String STATUS_CLEARED = "已清空";
    static final String STATUS_SWAPPED = "文本已交换";

    // 比较结果
    static final String RESULT_HEADER = "=== 文本比较结果 ===";
    static final String RESULT_BASIC_STATS = "基本统计";
    static final String RESULT_LINE_BY_LINE_COMPARISON = "逐行比较";
    static final String RESULT_STATISTICS = "统计信息";
    static final String RESULT_SIMILARITY = "相似度";

    // 状态格式
    static final String STATUS_LINES_CHARS = "%s: %d 行, %d 字符";
    static final String STATUS_LINE_NUMBER = "行%d";
    static final String STATUS_LINE_CONTENT = ": %s";
    static final String STATUS_MODIFIED_LINE = "! %s:\n  A: %s\n  B: %s";
    static final String STATUS_ADDED_LINE = "+ %s: %s";
    static final String STATUS_DELETED_LINE = "- %s: %s";
    static final String STATUS_DIFFERENCES_COUNT = "%d 处差异";

    // 比较标记
    static final String MARKER_ADDED = "+";
    static final String MARKER_DELETED = "-";
    static final String MARKER_MODIFIED = "!";
    static final String MARKER_SAME = " ";

    // 结果状态
    static final String RESULT_IDENTICAL = "✅ 两个文本完全相同！";
    static final String RESULT_DIFFERENCES_FOUND = "发现差异";

    // 统计信息格式
    static final String STATS_MODIFIED_LINES = "修改行数: %d";
    static final String STATS_ADDED_LINES = "新增行数: %d";
    static final String STATS_DELETED_LINES = "删除行数: %d";
    static final String STATS_TOTAL_CHANGES = "总变更: %d 行";
    static final String STATS_SIMILARITY_PERCENTAGE = "相似度: %.1f%%";

    // 示例文本
    static final String SAMPLE_TEXT_A = """
        Hello World!
        This is line 2.
        This is line 3.
        Different line here.
        Common line 5.
        """;

    static final String SAMPLE_TEXT_B = """
        Hello World!
        This is line 2.
        This is line 3 modified.
        New line inserted.
        Common line 5.
        Extra line at end.
        """;

    // 错误消息
    static final String ERROR_NO_TEXT = "请输入要比较的文本";
}

/**
 * 文本比较工具
 * 比较两个文本文件的差异，支持忽略大小写和空白字符
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new TextComparator().setVisible(true);
    });
}

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

class TextComparator extends JFrame {
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

    // 圆角
    private static final int RADIUS_4 = 4;
    private static final int RADIUS_8 = 8;
    private static final int RADIUS_12 = 12;
    private static final int RADIUS_16 = 16;

    // 按钮尺寸
    private static final Dimension BUTTON_REGULAR = new Dimension(120, 44);
    private static final Dimension BUTTON_LARGE = new Dimension(160, 50);

    // ===== 应用组件 =====
    private final JTextArea leftArea;
    private final JTextArea rightArea;
    private final JTextArea resultArea;
    private final JButton compareButton;
    private final JButton clearButton;
    private final JButton swapButton;
    private final JLabel statusLabel;
    private final JCheckBox ignoreCaseBox;
    private final JCheckBox ignoreWhitespaceBox;
    
    public TextComparator() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        leftArea = new JTextArea();
        rightArea = new JTextArea();
        resultArea = new JTextArea();
        compareButton = this.createPrimaryButton(Texts.COMPARE_BUTTON, this::compareTexts);
        clearButton = this.createSecondaryButton(Texts.CLEAR_BUTTON, this::clearAll);
        swapButton = this.createSecondaryButton(Texts.SWAP_BUTTON, this::swapTexts);
        statusLabel = new JLabel(Texts.STATUS_READY);
        ignoreCaseBox = new JCheckBox(Texts.IGNORE_CASE_CHECKBOX);
        ignoreWhitespaceBox = new JCheckBox(Texts.IGNORE_WHITESPACE_CHECKBOX);

        initializeGUI();
        setupEventHandlers();
        loadSampleData();
        setupKeyboardShortcuts();
        setSize(900, 700);
        setLocationRelativeTo(null);
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());

        // 控制面板 - 使用苹果风格
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_16, SPACING_12));
        controlPanel.setBackground(SYSTEM_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_24, SPACING_16, SPACING_24)
        ));

        // 设置复选框样式
        ignoreCaseBox.setFont(BODY);
        ignoreCaseBox.setBackground(SYSTEM_BACKGROUND);
        ignoreCaseBox.setForeground(LABEL);

        ignoreWhitespaceBox.setFont(BODY);
        ignoreWhitespaceBox.setBackground(SYSTEM_BACKGROUND);
        ignoreWhitespaceBox.setForeground(LABEL);

        controlPanel.add(ignoreCaseBox);
        controlPanel.add(ignoreWhitespaceBox);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(compareButton);
        controlPanel.add(Box.createHorizontalStrut(SPACING_12));
        controlPanel.add(swapButton);
        controlPanel.add(Box.createHorizontalStrut(SPACING_8));
        controlPanel.add(clearButton);

        add(controlPanel, BorderLayout.NORTH);

        // 主工作区 - 使用苹果风格卡片
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        // 上半部分：两个文本输入区域
        var inputPanel = new JPanel(new GridLayout(1, 2, SPACING_16, 0));
        inputPanel.setBackground(SYSTEM_BACKGROUND);

        // 左侧文本 - 使用苹果风格卡片
        var leftPanel = this.createStyledPanel(Texts.TEXT_A_PANEL_TITLE);
        leftArea.setFont(MONO);
        leftArea.setLineWrap(true);
        leftArea.setWrapStyleWord(true);
        leftArea.setBackground(WHITE);
        leftArea.setForeground(LABEL);
        leftArea.setCaretColor(LABEL);
        leftArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_12, SPACING_12, SPACING_12)
        ));

        var leftScrollPane = new JScrollPane(leftArea);
        leftScrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftScrollPane.getViewport().setBackground(WHITE);
        leftPanel.add(leftScrollPane, BorderLayout.CENTER);

        // 右侧文本 - 使用苹果风格卡片
        var rightPanel = this.createStyledPanel(Texts.TEXT_B_PANEL_TITLE);
        rightArea.setFont(MONO);
        rightArea.setLineWrap(true);
        rightArea.setWrapStyleWord(true);
        rightArea.setBackground(WHITE);
        rightArea.setForeground(LABEL);
        rightArea.setCaretColor(LABEL);
        rightArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_12, SPACING_12, SPACING_12)
        ));

        var rightScrollPane = new JScrollPane(rightArea);
        rightScrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightScrollPane.getViewport().setBackground(WHITE);
        rightPanel.add(rightScrollPane, BorderLayout.CENTER);

        inputPanel.add(leftPanel);
        inputPanel.add(rightPanel);

        // 下半部分：比较结果 - 使用苹果风格卡片
        var resultPanel = this.createStyledPanel(Texts.RESULT_PANEL_TITLE);
        resultArea.setFont(MONO);
        resultArea.setEditable(false);
        resultArea.setBackground(GRAY6);
        resultArea.setForeground(LABEL);
        resultArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_12, SPACING_12, SPACING_12)
        ));

        var resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setBorder(BorderFactory.createEmptyBorder());
        resultScrollPane.getViewport().setBackground(GRAY6);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // 状态栏 - 使用苹果风格
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_24, SPACING_8, SPACING_24)
        ));

        statusLabel.setFont(CAPTION1);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        compareButton.addActionListener(this::compareTexts);
        clearButton.addActionListener(this::clearAll);
        swapButton.addActionListener(this::swapTexts);
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

    /**
     * 圆角边框类
     */
    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
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

    private void setupKeyboardShortcuts() {
        // 添加窗口级别的键盘快捷键
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_ENTER:
                        // 回车键比较文本
                        if (ev.isControlDown()) {
                            compareTexts(new ActionEvent(TextComparator.this, ActionEvent.ACTION_PERFORMED, "compare"));
                        }
                        break;
                    case KeyEvent.VK_C:
                        // C键清空（如果按下Ctrl+C则让系统处理复制）
                        if (ev.isControlDown()) {
                            // 让系统处理复制
                            return;
                        } else {
                            clearAll(new ActionEvent(TextComparator.this, ActionEvent.ACTION_PERFORMED, "clear"));
                        }
                        break;
                    case KeyEvent.VK_S:
                        // S键交换文本
                        if (!ev.isControlDown()) {
                            swapTexts(new ActionEvent(TextComparator.this, ActionEvent.ACTION_PERFORMED, "swap"));
                        }
                        break;
                    case KeyEvent.VK_F5:
                        // F5键重新比较
                        compareTexts(new ActionEvent(TextComparator.this, ActionEvent.ACTION_PERFORMED, "compare"));
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

    private void loadSampleData() {
        leftArea.setText(Texts.SAMPLE_TEXT_A);
        rightArea.setText(Texts.SAMPLE_TEXT_B);
    }
    
    private void compareTexts(ActionEvent e) {
        var textA = leftArea.getText();
        var textB = rightArea.getText();
        
        if (textA.isEmpty() && textB.isEmpty()) {
            statusLabel.setText(Texts.STATUS_BOTH_EMPTY);
            resultArea.setText(Texts.STATUS_BOTH_EMPTY + "，无需比较。");
            return;
        }
        
        // 预处理文本
        var processedA = preprocessText(textA);
        var processedB = preprocessText(textB);
        
        // 按行分割
        var linesA = processedA.split("\n");
        var linesB = processedB.split("\n");
        
        var result = new StringBuilder();
        result.append(Texts.RESULT_HEADER).append("\n\n");

        // 基本统计
        result.append(String.format(Texts.STATUS_LINES_CHARS, "文本A", linesA.length, textA.length())).append("\n");
        result.append(String.format(Texts.STATUS_LINES_CHARS, "文本B", linesB.length, textB.length())).append("\n\n");

        // 简单的行比较
        var maxLines = Math.max(linesA.length, linesB.length);
        var differences = 0;
        var additions = 0;
        var deletions = 0;

        result.append("=== ").append(Texts.RESULT_LINE_BY_LINE_COMPARISON).append(" ===\n");
        
        for (int i = 0; i < maxLines; i++) {
            var lineA = i < linesA.length ? linesA[i] : null;
            var lineB = i < linesB.length ? linesB[i] : null;
            
            if (lineA == null) {
                // B中有新增行
                result.append(String.format(Texts.STATUS_ADDED_LINE,
                    String.format(Texts.STATUS_LINE_NUMBER, i + 1), lineB)).append("\n");
                additions++;
            } else if (lineB == null) {
                // A中有删除行
                result.append(String.format(Texts.STATUS_DELETED_LINE,
                    String.format(Texts.STATUS_LINE_NUMBER, i + 1), lineA)).append("\n");
                deletions++;
            } else if (!lineA.equals(lineB)) {
                // 行内容不同
                result.append(String.format(Texts.STATUS_MODIFIED_LINE,
                    String.format(Texts.STATUS_LINE_NUMBER, i + 1), lineA, lineB)).append("\n");
                differences++;
            }
        }
        
        if (differences == 0 && additions == 0 && deletions == 0) {
            result.append(Texts.RESULT_IDENTICAL).append("\n");
        } else {
            result.append("\n=== ").append(Texts.RESULT_STATISTICS).append(" ===\n");
            result.append(String.format(Texts.STATS_MODIFIED_LINES, differences)).append("\n");
            result.append(String.format(Texts.STATS_ADDED_LINES, additions)).append("\n");
            result.append(String.format(Texts.STATS_DELETED_LINES, deletions)).append("\n");
            result.append(String.format(Texts.STATS_TOTAL_CHANGES, differences + additions + deletions)).append("\n");
        }

        // 相似度计算
        var similarity = calculateSimilarity(processedA, processedB);
        result.append(String.format(Texts.STATS_SIMILARITY_PERCENTAGE, similarity * 100)).append("\n");

        resultArea.setText(result.toString());
        statusLabel.setText(String.format(Texts.STATUS_DIFFERENCES_COUNT, differences + additions + deletions));
    }
    
    private String preprocessText(String text) {
        var processed = text;
        
        if (ignoreWhitespaceBox.isSelected()) {
            processed = processed.replaceAll("\\s+", " ").trim();
        }
        
        if (ignoreCaseBox.isSelected()) {
            processed = processed.toLowerCase();
        }
        
        return processed;
    }
    
    private double calculateSimilarity(String textA, String textB) {
        if (textA.isEmpty() && textB.isEmpty()) {
            return 1.0;
        }
        
        if (textA.isEmpty() || textB.isEmpty()) {
            return 0.0;
        }
        
        // 简单的字符级相似度计算
        var maxLength = Math.max(textA.length(), textB.length());
        var minLength = Math.min(textA.length(), textB.length());
        
        var matches = 0;
        for (int i = 0; i < minLength; i++) {
            if (textA.charAt(i) == textB.charAt(i)) {
                matches++;
            }
        }
        
        return (double) matches / maxLength;
    }
    
    private void clearAll(ActionEvent e) {
        leftArea.setText("");
        rightArea.setText("");
        resultArea.setText("");
        statusLabel.setText(Texts.STATUS_CLEARED);
    }

    private void swapTexts(ActionEvent e) {
        var tempText = leftArea.getText();
        leftArea.setText(rightArea.getText());
        rightArea.setText(tempText);
        statusLabel.setText(Texts.STATUS_SWAPPED);
    }
}
