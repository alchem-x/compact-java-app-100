import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 记事本应用
 * 功能完整的文本编辑器，支持文件操作、查找替换、字体设置等
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Notepad().setVisible(true);
    });
}

class Notepad extends JFrame {
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
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private File currentFile;
    private boolean isModified = false;
    private JLabel statusLabel;
    private JCheckBoxMenuItem wordWrapItem;
    private JCheckBoxMenuItem statusBarItem;
    private int zoomLevel = 100;
    private String currentFontFamily = "SF Pro Text";
    private int currentFontSize = 14;
    private int currentFontStyle = Font.PLAIN;
    
    // 撤销重做管理器
    private UndoManager undoManager;
    
    // 查找替换相关
    private String lastSearchText = "";
    private int lastSearchIndex = 0;
    
    public Notepad() {
        initializeUI();
        setupKeyBindings();
        newDocument();
    }
    
    private void initializeUI() {
        setTitle("记事本 - Notepad");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 处理窗口关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        // 创建菜单栏
        createMenuBar();
        
        // 创建工具栏
        createToolBar();
        
        // 创建文本区域
        textArea = new JTextArea();
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textArea.setTabSize(4);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        // 设置撤销重做管理器
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);
        
        // 添加文档监听器
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
        });
        
        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        // 创建状态栏
        createStatusBar();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 文件菜单
        JMenu fileMenu = new JMenu("文件(F)");
        fileMenu.setMnemonic('F');
        
        addMenuItem(fileMenu, "新建(N)", 'N', KeyEvent.VK_N, e -> newDocument());
        addMenuItem(fileMenu, "打开(O)", 'O', KeyEvent.VK_O, e -> openFile());
        addMenuItem(fileMenu, "保存(S)", 'S', KeyEvent.VK_S, e -> saveFile());
        addMenuItem(fileMenu, "另存为(A)", 'A', 0, e -> saveAsFile());
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "退出(X)", 'X', 0, e -> exitApplication());
        
        // 编辑菜单
        JMenu editMenu = new JMenu("编辑(E)");
        editMenu.setMnemonic('E');
        
        addMenuItem(editMenu, "撤销(U)", 'U', KeyEvent.VK_Z, (ev) -> this.undo());
        addMenuItem(editMenu, "重做(R)", 'R', KeyEvent.VK_Y, (ev) -> this.redo());
        editMenu.addSeparator();
        addMenuItem(editMenu, "剪切(T)", 'T', KeyEvent.VK_X, e -> textArea.cut());
        addMenuItem(editMenu, "复制(C)", 'C', KeyEvent.VK_C, e -> textArea.copy());
        addMenuItem(editMenu, "粘贴(P)", 'P', KeyEvent.VK_V, e -> textArea.paste());
        editMenu.addSeparator();
        addMenuItem(editMenu, "全选(A)", 'A', KeyEvent.VK_A, e -> textArea.selectAll());
        addMenuItem(editMenu, "查找(F)", 'F', KeyEvent.VK_F, e -> showFindDialog());
        addMenuItem(editMenu, "替换(H)", 'H', KeyEvent.VK_H, e -> showReplaceDialog());
        
        // 格式菜单
        JMenu formatMenu = new JMenu("格式(O)");
        formatMenu.setMnemonic('O');
        
        addMenuItem(formatMenu, "字体(F)", 'F', 0, e -> showFontDialog());
        JCheckBoxMenuItem wrapMenuItem = new JCheckBoxMenuItem("自动换行(W)", true);
        wrapMenuItem.setMnemonic('W');
        wrapMenuItem.addActionListener(e -> {
            boolean wrap = wrapMenuItem.isSelected();
            textArea.setLineWrap(wrap);
            textArea.setWrapStyleWord(wrap);
        });
        formatMenu.add(wrapMenuItem);
        
        // 插入菜单
        JMenu insertMenu = new JMenu("插入(I)");
        insertMenu.setMnemonic('I');
        
        addMenuItem(insertMenu, "日期时间(D)", 'D', KeyEvent.VK_F5, e -> insertDateTime());
        
        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助(H)");
        helpMenu.setMnemonic('H');
        
        addMenuItem(helpMenu, "关于(A)", 'A', 0, e -> showAboutDialog());
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(insertMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void addMenuItem(JMenu menu, String text, char mnemonic, int accelerator, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setMnemonic(mnemonic);
        
        if (accelerator != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_DOWN_MASK));
        }
        
        item.addActionListener(action);
        menu.add(item);
    }
    
    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        addToolBarButton(toolBar, "新建", "创建新文档", e -> newDocument());
        addToolBarButton(toolBar, "打开", "打开文件", e -> openFile());
        addToolBarButton(toolBar, "保存", "保存文件", e -> saveFile());
        toolBar.addSeparator();
        addToolBarButton(toolBar, "剪切", "剪切选中文本", e -> textArea.cut());
        addToolBarButton(toolBar, "复制", "复制选中文本", e -> textArea.copy());
        addToolBarButton(toolBar, "粘贴", "粘贴文本", e -> textArea.paste());
        toolBar.addSeparator();
        addToolBarButton(toolBar, "查找", "查找文本", e -> showFindDialog());
        addToolBarButton(toolBar, "替换", "替换文本", e -> showReplaceDialog());
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void addToolBarButton(JToolBar toolBar, String text, String tooltip, ActionListener action) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(action);
        button.setFocusable(false);
        toolBar.add(button);
    }
    
    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));

        statusLabel = new JLabel("就绪");
        statusLabel.setFont(CAPTION1);
        statusLabel.setForeground(SECONDARY_LABEL);

        JLabel positionLabel = new JLabel();
        positionLabel.setFont(CAPTION1);
        positionLabel.setForeground(SECONDARY_LABEL);

        // 添加光标位置监听器
        textArea.addCaretListener(e -> {
            try {
                int pos = textArea.getCaretPosition();
                int line = textArea.getLineOfOffset(pos) + 1;
                int col = pos - textArea.getLineStartOffset(line - 1) + 1;
                positionLabel.setText(String.format("行 %d, 列 %d", line, col));
            } catch (Exception ex) {
                positionLabel.setText("");
            }
        });

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(positionLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupKeyBindings() {
        // 设置快捷键
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        
        // Ctrl+N - 新建
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "new");
        actionMap.put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { newDocument(); }
        });
        
        // F5 - 插入日期时间
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "datetime");
        actionMap.put("datetime", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { insertDateTime(); }
        });
    }
    
    private void newDocument() {
        if (checkSaveChanges()) {
            textArea.setText("");
            currentFile = null;
            setModified(false);
            updateTitle();
            statusLabel.setText("就绪");
        }
    }
    
    private void openFile() {
        if (!checkSaveChanges()) return;
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.setText("");
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append(line + "\n");
                }
                
                currentFile = file;
                setModified(false);
                updateTitle();
                statusLabel.setText(String.format("已打开: %s", file.getName()));
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "读取文件失败: " + e.getMessage(),
                    "读取文件失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveFile() {
        if (currentFile == null) {
            saveAsFile();
        } else {
            saveToFile(currentFile);
        }
    }
    
    private void saveAsFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // 确保文件有.txt扩展名
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            
            saveToFile(file);
        }
    }
    
    private void saveToFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
            
            currentFile = file;
            setModified(false);
            updateTitle();
            statusLabel.setText("已保存: " + file.getName());
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "无法保存文件: " + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean checkSaveChanges() {
        if (isModified) {
            String fileName = currentFile != null ? currentFile.getName() : "无标题";
            int result = JOptionPane.showConfirmDialog(this,
                "文件 \"" + fileName + "\" 已被修改。\n是否保存更改？",
                "记事本", JOptionPane.YES_NO_CANCEL_OPTION);
            
            switch (result) {
                case JOptionPane.YES_OPTION:
                    saveFile();
                    return !isModified; // 如果保存失败，isModified仍为true
                case JOptionPane.NO_OPTION:
                    return true;
                case JOptionPane.CANCEL_OPTION:
                default:
                    return false;
            }
        }
        return true;
    }
    
    private void exitApplication() {
        if (checkSaveChanges()) {
            System.exit(0);
        }
    }
    
    private void setModified(boolean modified) {
        this.isModified = modified;
        updateTitle();
    }
    
    private void updateTitle() {
        String title = "记事本";
        if (currentFile != null) {
            title += " - " + currentFile.getName();
        } else {
            title += " - 无标题";
        }
        if (isModified) {
            title += " *";
        }
        setTitle(title);
    }
    
    private void showFindDialog() {
        String searchText = JOptionPane.showInputDialog(this, 
            "查找内容:", "查找", JOptionPane.QUESTION_MESSAGE);
        
        if (searchText != null && !searchText.isEmpty()) {
            findText(searchText, true);
        }
    }
    
    private void showReplaceDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField findField = new JTextField(lastSearchText);
        JTextField replaceField = new JTextField();
        
        panel.add(new JLabel("查找:"));
        panel.add(findField);
        panel.add(new JLabel("替换为:"));
        panel.add(replaceField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "替换", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String findText = findField.getText();
            String replaceText = replaceField.getText();
            
            if (!findText.isEmpty()) {
                replaceText(findText, replaceText);
            }
        }
    }
    
    private void findText(String searchText, boolean showMessage) {
        String content = textArea.getText();
        lastSearchText = searchText;
        
        int index = content.indexOf(searchText, lastSearchIndex);
        if (index >= 0) {
            textArea.setSelectionStart(index);
            textArea.setSelectionEnd(index + searchText.length());
            textArea.requestFocus();
            lastSearchIndex = index + 1;
            statusLabel.setText("找到: " + searchText);
        } else {
            // 从头开始查找
            index = content.indexOf(searchText, 0);
            if (index >= 0) {
                textArea.setSelectionStart(index);
                textArea.setSelectionEnd(index + searchText.length());
                textArea.requestFocus();
                lastSearchIndex = index + 1;
                statusLabel.setText("找到: " + searchText + " (从头开始)");
            } else {
                if (showMessage) {
                    JOptionPane.showMessageDialog(this, 
                        "找不到 \"" + searchText + "\"", 
                        "查找", JOptionPane.INFORMATION_MESSAGE);
                }
                statusLabel.setText("未找到: " + searchText);
                lastSearchIndex = 0;
            }
        }
    }
    
    private void replaceText(String findText, String replaceText) {
        String content = textArea.getText();
        String newContent = content.replace(findText, replaceText);
        
        if (!content.equals(newContent)) {
            textArea.setText(newContent);
            statusLabel.setText("已替换: " + findText + " -> " + replaceText);
        } else {
            statusLabel.setText("未找到要替换的文本: " + findText);
        }
    }
    
    private void showFontDialog() {
        Font currentFont = textArea.getFont();
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                               .getAvailableFontFamilyNames();
        JComboBox<String> fontCombo = new JComboBox<>(fontNames);
        fontCombo.setSelectedItem(currentFont.getName());
        
        String[] fontSizes = {"8", "9", "10", "11", "12", "14", "16", "18", "20", "24", "28", "32"};
        JComboBox<String> sizeCombo = new JComboBox<>(fontSizes);
        sizeCombo.setSelectedItem(String.valueOf(currentFont.getSize()));
        
        String[] fontStyles = {"常规", "粗体", "斜体", "粗斜体"};
        JComboBox<String> styleCombo = new JComboBox<>(fontStyles);
        int style = currentFont.getStyle();
        styleCombo.setSelectedIndex(style);
        
        panel.add(new JLabel("字体:"));
        panel.add(fontCombo);
        panel.add(new JLabel("大小:"));
        panel.add(sizeCombo);
        panel.add(new JLabel("样式:"));
        panel.add(styleCombo);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "字体", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String fontName = (String) fontCombo.getSelectedItem();
            int fontSize = Integer.parseInt((String) sizeCombo.getSelectedItem());
            int fontStyle = styleCombo.getSelectedIndex();
            
            Font newFont = new Font(fontName, fontStyle, fontSize);
            textArea.setFont(newFont);
            statusLabel.setText("字体已更改");
        }
    }
    
    private void insertDateTime() {
        String dateTime = LocalDateTime.now()
                                      .format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss"));
        textArea.insert(dateTime, textArea.getCaretPosition());
    }
    
    private void showAboutDialog() {
        String message = """
            记事本 v1.0
            
            一个功能完整的文本编辑器
            
            功能特性:
            • 文件的新建、打开、保存
            • 文本的剪切、复制、粘贴
            • 查找和替换功能
            • 字体设置
            • 自动换行
            • 插入日期时间
            • 状态栏显示
            
            快捷键:
            Ctrl+N - 新建
            Ctrl+O - 打开
            Ctrl+S - 保存
            Ctrl+F - 查找
            Ctrl+H - 替换
            F5 - 插入日期时间
            """;
        
        JOptionPane.showMessageDialog(this, message, "关于", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // 撤销操作
    private void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }
    
    // 重做操作
    private void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    // ActionEvent包装方法（用于键盘快捷键）
    private void newFile(ActionEvent e) {
        newDocument();
    }

    private void openFile(ActionEvent e) {
        openFile();
    }

    private void saveFile(ActionEvent e) {
        saveFile();
    }

    private void find(ActionEvent e) {
        showFindDialog();
    }

    private void replace(ActionEvent e) {
        showReplaceDialog();
    }

    private void showAbout(ActionEvent e) {
        // 创建关于对话框内容
        JTextArea aboutText = new JTextArea("""
        📝 简易记事本

        版本: 1.0
        作者: Java开发团队

        功能特点:
        • 文本编辑和保存
        • 查找和替换
        • 字体设置
        • 缩放功能
        • 自动换行

        基于Java Swing开发
        """);
        aboutText.setEditable(false);
        aboutText.setBackground(WHITE);
        aboutText.setForeground(LABEL);
        aboutText.setFont(BODY);
        aboutText.setBorder(BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16));

        JScrollPane scrollPane = new JScrollPane(aboutText);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        JOptionPane.showMessageDialog(this, scrollPane, "关于记事本", JOptionPane.INFORMATION_MESSAGE);
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
                    case KeyEvent.VK_N:
                        // N键新建文件
                        if (ev.isControlDown()) {
                            newFile(new ActionEvent(Notepad.this, ActionEvent.ACTION_PERFORMED, "new"));
                        }
                        break;
                    case KeyEvent.VK_O:
                        // O键打开文件
                        if (ev.isControlDown()) {
                            openFile(new ActionEvent(Notepad.this, ActionEvent.ACTION_PERFORMED, "open"));
                        }
                        break;
                    case KeyEvent.VK_S:
                        // S键保存文件
                        if (ev.isControlDown()) {
                            saveFile(new ActionEvent(Notepad.this, ActionEvent.ACTION_PERFORMED, "save"));
                        }
                        break;
                    case KeyEvent.VK_F:
                        // F键查找
                        if (ev.isControlDown()) {
                            find(new ActionEvent(Notepad.this, ActionEvent.ACTION_PERFORMED, "find"));
                        }
                        break;
                    case KeyEvent.VK_H:
                        // H键替换
                        if (ev.isControlDown()) {
                            replace(new ActionEvent(Notepad.this, ActionEvent.ACTION_PERFORMED, "replace"));
                        }
                        break;
                    case KeyEvent.VK_F5:
                        // F5插入日期时间
                        insertDateTime();
                        break;
                    case KeyEvent.VK_F1:
                        // F1显示帮助
                        showAbout(new ActionEvent(Notepad.this, ActionEvent.ACTION_PERFORMED, "about"));
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
}
