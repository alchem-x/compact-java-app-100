import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new SimpleNotepad().setVisible(true);
    });
}

static class SimpleNotepad extends JFrame {
    private final JTextArea textArea;
    private final JLabel statusLabel;
    private final JLabel wordCountLabel;
    private File currentFile;
    private boolean isModified = false;
    
    public SimpleNotepad() {
        textArea = new JTextArea();
        statusLabel = new JLabel("就绪");
        wordCountLabel = new JLabel("字数: 0");
        
        initializeGUI();
        setupEventHandlers();
        updateTitle();
    }
    
    private void initializeGUI() {
        setTitle("📝 简易记事本");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 创建菜单栏
        createMenuBar();
        
        // 创建工具栏
        createToolBar();
        
        // 创建文本编辑区域
        createTextArea();
        
        // 创建状态栏
        createStatusBar();
        
        // 设置关闭操作
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
        
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    private void createMenuBar() {
        var menuBar = new JMenuBar();
        
        // 文件菜单
        var fileMenu = new JMenu("文件");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        var newItem = new JMenuItem("新建");
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> newFile());
        
        var openItem = new JMenuItem("打开");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openFile());
        
        var saveItem = new JMenuItem("保存");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveFile());
        
        var saveAsItem = new JMenuItem("另存为");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(e -> saveAsFile());
        
        var exitItem = new JMenuItem("退出");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> exitApplication());
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // 编辑菜单
        var editMenu = new JMenu("编辑");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        var undoItem = new JMenuItem("撤销");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> textArea.requestFocus());
        
        var cutItem = new JMenuItem("剪切");
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(e -> textArea.cut());
        
        var copyItem = new JMenuItem("复制");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> textArea.copy());
        
        var pasteItem = new JMenuItem("粘贴");
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> textArea.paste());
        
        var selectAllItem = new JMenuItem("全选");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        selectAllItem.addActionListener(e -> textArea.selectAll());
        
        editMenu.add(undoItem);
        editMenu.addSeparator();
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);
        
        // 插入菜单
        var insertMenu = new JMenu("插入");
        
        var dateTimeItem = new JMenuItem("日期和时间");
        dateTimeItem.addActionListener(e -> insertDateTime());
        
        var separatorItem = new JMenuItem("分隔线");
        separatorItem.addActionListener(e -> insertSeparator());
        
        insertMenu.add(dateTimeItem);
        insertMenu.add(separatorItem);
        
        // 格式菜单
        var formatMenu = new JMenu("格式");
        
        var fontItem = new JMenuItem("字体");
        fontItem.addActionListener(e -> chooseFont());
        
        var wrapItem = new JCheckBoxMenuItem("自动换行", true);
        wrapItem.addActionListener(e -> textArea.setLineWrap(wrapItem.isSelected()));
        
        formatMenu.add(fontItem);
        formatMenu.add(wrapItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(insertMenu);
        menuBar.add(formatMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createToolBar() {
        var toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        var newButton = new JButton("📄");
        newButton.setToolTipText("新建");
        newButton.addActionListener(e -> newFile());
        
        var openButton = new JButton("📁");
        openButton.setToolTipText("打开");
        openButton.addActionListener(e -> openFile());
        
        var saveButton = new JButton("💾");
        saveButton.setToolTipText("保存");
        saveButton.addActionListener(e -> saveFile());
        
        var cutButton = new JButton("✂️");
        cutButton.setToolTipText("剪切");
        cutButton.addActionListener(e -> textArea.cut());
        
        var copyButton = new JButton("📋");
        copyButton.setToolTipText("复制");
        copyButton.addActionListener(e -> textArea.copy());
        
        var pasteButton = new JButton("📌");
        pasteButton.setToolTipText("粘贴");
        pasteButton.addActionListener(e -> textArea.paste());
        
        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.addSeparator();
        toolBar.add(cutButton);
        toolBar.add(copyButton);
        toolBar.add(pasteButton);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void createTextArea() {
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        
        var scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        wordCountLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(wordCountLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // 监听文本变化
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { textChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { textChanged(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { textChanged(); }
        });
    }
    
    private void textChanged() {
        isModified = true;
        updateTitle();
        updateWordCount();
    }
    
    private void updateTitle() {
        var title = "简易记事本";
        if (currentFile != null) {
            title += " - " + currentFile.getName();
        } else {
            title += " - 未命名";
        }
        if (isModified) {
            title += " *";
        }
        setTitle(title);
    }
    
    private void updateWordCount() {
        var text = textArea.getText();
        var charCount = text.length();
        var lineCount = text.split("\n").length;
        var wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        
        wordCountLabel.setText(String.format("字符: %d | 行数: %d | 单词: %d", 
            charCount, lineCount, wordCount));
    }
    
    private void newFile() {
        if (checkSaveBeforeAction()) {
            textArea.setText("");
            currentFile = null;
            isModified = false;
            updateTitle();
            statusLabel.setText("新建文件");
        }
    }
    
    private void openFile() {
        if (!checkSaveBeforeAction()) return;
        
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "文本文件", "txt", "md", "java", "py", "js", "html", "css"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            try {
                var content = Files.readString(Path.of(file.getAbsolutePath()));
                textArea.setText(content);
                currentFile = file;
                isModified = false;
                updateTitle();
                statusLabel.setText("已打开: " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "无法打开文件: " + e.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveFile() {
        if (currentFile == null) {
            saveAsFile();
        } else {
            try {
                Files.writeString(Path.of(currentFile.getAbsolutePath()), textArea.getText());
                isModified = false;
                updateTitle();
                statusLabel.setText("已保存: " + currentFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "无法保存文件: " + e.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveAsFile() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "文本文件", "txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            
            try {
                Files.writeString(Path.of(file.getAbsolutePath()), textArea.getText());
                currentFile = file;
                isModified = false;
                updateTitle();
                statusLabel.setText("已保存为: " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "无法保存文件: " + e.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean checkSaveBeforeAction() {
        if (isModified) {
            var result = JOptionPane.showConfirmDialog(this, 
                "文档已修改，是否保存？", 
                "确认", 
                JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                saveFile();
                return !isModified; // 如果保存失败，isModified仍为true
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }
    
    private void exitApplication() {
        if (checkSaveBeforeAction()) {
            System.exit(0);
        }
    }
    
    private void insertDateTime() {
        var now = LocalDateTime.now();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var dateTime = now.format(formatter);
        
        var caretPosition = textArea.getCaretPosition();
        textArea.insert(dateTime, caretPosition);
    }
    
    private void insertSeparator() {
        var separator = "\n" + "=".repeat(50) + "\n";
        var caretPosition = textArea.getCaretPosition();
        textArea.insert(separator, caretPosition);
    }
    
    private void chooseFont() {
        var currentFont = textArea.getFont();
        var fontDialog = new FontChooser(this, currentFont);
        var newFont = fontDialog.showDialog();
        if (newFont != null) {
            textArea.setFont(newFont);
        }
    }
    
    // 简单的字体选择对话框
    static class FontChooser extends JDialog {
        private Font selectedFont;
        private final JList<String> fontList;
        private final JList<Integer> sizeList;
        private final JCheckBox boldBox;
        private final JCheckBox italicBox;
        private final JLabel previewLabel;
        
        public FontChooser(Frame parent, Font currentFont) {
            super(parent, "选择字体", true);
            
            var fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            fontList = new JList<>(fontNames);
            fontList.setSelectedValue(currentFont.getFontName(), true);
            
            var sizes = new Integer[]{8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
            sizeList = new JList<>(sizes);
            sizeList.setSelectedValue(currentFont.getSize(), true);
            
            boldBox = new JCheckBox("粗体", currentFont.isBold());
            italicBox = new JCheckBox("斜体", currentFont.isItalic());
            
            previewLabel = new JLabel("预览文本 AaBbCc 123", JLabel.CENTER);
            previewLabel.setFont(currentFont);
            previewLabel.setBorder(BorderFactory.createLoweredBevelBorder());
            previewLabel.setPreferredSize(new Dimension(200, 50));
            
            initDialog();
            updatePreview();
        }
        
        private void initDialog() {
            setLayout(new BorderLayout());
            
            var mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));
            
            // 字体列表
            var fontPanel = new JPanel(new BorderLayout());
            fontPanel.add(new JLabel("字体"), BorderLayout.NORTH);
            fontPanel.add(new JScrollPane(fontList), BorderLayout.CENTER);
            
            // 大小列表
            var sizePanel = new JPanel(new BorderLayout());
            sizePanel.add(new JLabel("大小"), BorderLayout.NORTH);
            sizePanel.add(new JScrollPane(sizeList), BorderLayout.CENTER);
            
            // 样式选项
            var stylePanel = new JPanel();
            stylePanel.setLayout(new BoxLayout(stylePanel, BoxLayout.Y_AXIS));
            stylePanel.add(new JLabel("样式"));
            stylePanel.add(boldBox);
            stylePanel.add(italicBox);
            
            mainPanel.add(fontPanel);
            mainPanel.add(sizePanel);
            mainPanel.add(stylePanel);
            
            // 预览面板
            var previewPanel = new JPanel(new BorderLayout());
            previewPanel.add(new JLabel("预览"), BorderLayout.NORTH);
            previewPanel.add(previewLabel, BorderLayout.CENTER);
            
            // 按钮面板
            var buttonPanel = new JPanel(new FlowLayout());
            var okButton = new JButton("确定");
            var cancelButton = new JButton("取消");
            
            okButton.addActionListener(e -> {
                selectedFont = createFont();
                dispose();
            });
            
            cancelButton.addActionListener(e -> {
                selectedFont = null;
                dispose();
            });
            
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            
            add(mainPanel, BorderLayout.CENTER);
            add(previewPanel, BorderLayout.SOUTH);
            add(buttonPanel, BorderLayout.PAGE_END);
            
            // 添加监听器
            fontList.addListSelectionListener(e -> updatePreview());
            sizeList.addListSelectionListener(e -> updatePreview());
            boldBox.addActionListener(e -> updatePreview());
            italicBox.addActionListener(e -> updatePreview());
            
            setSize(500, 300);
            setLocationRelativeTo(getParent());
        }
        
        private void updatePreview() {
            var font = createFont();
            previewLabel.setFont(font);
        }
        
        private Font createFont() {
            var fontName = fontList.getSelectedValue();
            var fontSize = sizeList.getSelectedValue();
            var style = Font.PLAIN;
            
            if (boldBox.isSelected()) style |= Font.BOLD;
            if (italicBox.isSelected()) style |= Font.ITALIC;
            
            return new Font(fontName != null ? fontName : "Dialog", 
                          style, 
                          fontSize != null ? fontSize : 12);
        }
        
        public Font showDialog() {
            setVisible(true);
            return selectedFont;
        }
    }
}
