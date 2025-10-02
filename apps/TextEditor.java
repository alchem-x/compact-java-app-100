import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TextEditor().setVisible(true));
}

static class TextEditor extends JFrame {
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
    private static final int RADIUS_20 = 20;

    // 按钮尺寸
    private static final Dimension BUTTON_REGULAR = new Dimension(120, 44);

    // ===== 应用状态 =====
    private JTextArea textArea;
    private JLabel statusLabel;
    private JLabel positionLabel;
    private File currentFile;
    private boolean isModified = false;

    public TextEditor() {
        this.initializeGUI();
        this.newFile();
    }

    private void initializeGUI() {
        setTitle("📝 文本编辑器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 创建菜单栏
        this.createMenuBar();

        // 创建工具栏
        this.createToolBar();

        // 创建主编辑区域 - 使用苹果风格
        textArea = new JTextArea();
        textArea.setFont(new Font("SF Mono", Font.PLAIN, 14));
        textArea.setTabSize(4);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(WHITE);
        textArea.setForeground(LABEL);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 添加文档监听器
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
        });

        // 添加光标位置监听器
        textArea.addCaretListener((ev) -> this.updateCursorPosition());

        var scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // 创建状态栏 - 使用苹果风格
        this.createStatusBar();

        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        var menuBar = new JMenuBar();
        menuBar.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY3));

        // 文件菜单
        var fileMenu = new JMenu("文件");
        fileMenu.setFont(BODY);
        fileMenu.setForeground(LABEL);

        var newItem = new JMenuItem("🆕 新建");
        newItem.setFont(CALLOUT);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newItem.addActionListener((ev) -> this.newFile());

        var openItem = new JMenuItem("📂 打开");
        openItem.setFont(CALLOUT);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        openItem.addActionListener((ev) -> this.openFile());

        var saveItem = new JMenuItem("💾 保存");
        saveItem.setFont(CALLOUT);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener((ev) -> this.saveFile());

        var saveAsItem = new JMenuItem("📝 另存为");
        saveAsItem.setFont(CALLOUT);
        saveAsItem.addActionListener((ev) -> this.saveAsFile());

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);

        // 编辑菜单
        var editMenu = new JMenu("编辑");
        editMenu.setFont(BODY);
        editMenu.setForeground(LABEL);

        var cutItem = new JMenuItem("✂️ 剪切");
        cutItem.setFont(CALLOUT);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener((ev) -> textArea.cut());

        var copyItem = new JMenuItem("📋 复制");
        copyItem.setFont(CALLOUT);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener((ev) -> textArea.copy());

        var pasteItem = new JMenuItem("📄 粘贴");
        pasteItem.setFont(CALLOUT);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener((ev) -> textArea.paste());

        var findItem = new JMenuItem("🔍 查找");
        findItem.setFont(CALLOUT);
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        findItem.addActionListener((ev) -> this.showFindDialog());

        var selectAllItem = new JMenuItem("全选");
        selectAllItem.setFont(CALLOUT);
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        selectAllItem.addActionListener((ev) -> textArea.selectAll());

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(findItem);
        editMenu.add(selectAllItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    private void createToolBar() {
        var toolBar = new JToolBar();
        toolBar.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY3));
        toolBar.setFloatable(false);

        var newButton = this.createToolBarButton("🆕 新建");
        newButton.addActionListener((ev) -> this.newFile());

        var openButton = this.createToolBarButton("📂 打开");
        openButton.addActionListener((ev) -> this.openFile());

        var saveButton = this.createToolBarButton("💾 保存");
        saveButton.addActionListener((ev) -> this.saveFile());

        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.addSeparator();

        var cutButton = this.createToolBarButton("✂️ 剪切");
        cutButton.addActionListener((ev) -> textArea.cut());

        var copyButton = this.createToolBarButton("📋 复制");
        copyButton.addActionListener((ev) -> textArea.copy());

        var pasteButton = this.createToolBarButton("📄 粘贴");
        pasteButton.addActionListener((ev) -> textArea.paste());

        toolBar.add(cutButton);
        toolBar.add(copyButton);
        toolBar.add(pasteButton);

        add(toolBar, BorderLayout.NORTH);
    }

    private JButton createToolBarButton(String text) {
        var button = new JButton(text);
        button.setFont(CAPTION1);
        button.setBackground(GRAY6);
        button.setForeground(LABEL);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_4),
            BorderFactory.createEmptyBorder(SPACING_4, SPACING_8, SPACING_4, SPACING_8)
        ));

        // 添加悬停效果
        this.setupButtonHoverEffect(button, GRAY6);

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

    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY3),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));

        statusLabel = new JLabel("就绪");
        statusLabel.setFont(CAPTION1);
        statusLabel.setForeground(SECONDARY_LABEL);

        positionLabel = new JLabel("行 1, 列 1");
        positionLabel.setFont(CAPTION1);
        positionLabel.setForeground(SECONDARY_LABEL);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(positionLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    private void updateCursorPosition() {
        try {
            var caretPos = textArea.getCaretPosition();
            var line = textArea.getLineOfOffset(caretPos) + 1;
            var column = caretPos - textArea.getLineStartOffset(line - 1) + 1;
            positionLabel.setText(String.format("行 %d, 列 %d", line, column));
        } catch (Exception e) {
            positionLabel.setText("行 1, 列 1");
        }
    }

    // ===== 业务逻辑方法 =====
    private void newFile() {
        if (isModified && !this.confirmDiscard()) {
            return;
        }

        textArea.setText("");
        currentFile = null;
        this.setModified(false);
        this.updateTitle();
        statusLabel.setText("新建文件");
        this.updateCursorPosition();
    }

    private void openFile() {
        if (isModified && !this.confirmDiscard()) {
            return;
        }

        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("文本文件", "txt", "java", "py", "md", "html", "css", "js"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            try {
                var content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                textArea.setText(content);
                currentFile = file;
                this.setModified(false);
                this.updateTitle();
                statusLabel.setText("文件已打开: " + file.getName());
                this.updateCursorPosition();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法打开文件: " + e.getMessage(), "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            this.saveAsFile();
        } else {
            try {
                Files.write(Paths.get(currentFile.getAbsolutePath()), textArea.getText().getBytes());
                this.setModified(false);
                statusLabel.setText("文件已保存: " + currentFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法保存文件: " + e.getMessage(), "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAsFile() {
        var fileChooser = new JFileChooser();

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            try {
                Files.write(Paths.get(file.getAbsolutePath()), textArea.getText().getBytes());
                currentFile = file;
                this.setModified(false);
                this.updateTitle();
                statusLabel.setText("文件已保存: " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法保存文件: " + e.getMessage(), "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFindDialog() {
        var searchText = JOptionPane.showInputDialog(this, "查找内容:", "查找",
            JOptionPane.QUESTION_MESSAGE);
        if (searchText != null && !searchText.isEmpty()) {
            var text = textArea.getText();
            var index = text.indexOf(searchText);

            if (index != -1) {
                textArea.setCaretPosition(index);
                textArea.select(index, index + searchText.length());
                statusLabel.setText("找到: " + searchText);
            } else {
                statusLabel.setText("未找到: " + searchText);
            }
        }
    }

    private boolean confirmDiscard() {
        var result = JOptionPane.showConfirmDialog(this,
            "文档已修改，是否保存？",
            "确认",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            this.saveFile();
            return !isModified;
        } else if (result == JOptionPane.NO_OPTION) {
            return true;
        }
        return false;
    }

    private void setModified(boolean modified) {
        this.isModified = modified;
        this.updateTitle();
    }

    private void updateTitle() {
        var title = "📝 文本编辑器";
        if (currentFile != null) {
            title += " - " + currentFile.getName();
        }
        if (isModified) {
            title += " *";
        }
        setTitle(title);
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