import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new CodeEditor().setVisible(true);
    });
}

static class CodeEditor extends JFrame {
    private final JTextPane codeArea;
    private final JLabel statusLabel;
    private final JLabel lineColumnLabel;
    private final JComboBox<String> languageCombo;
    private File currentFile;
    private boolean isModified = false;
    
    // 语法高亮样式
    private final Map<String, Style> styles = new HashMap<>();
    
    public CodeEditor() {
        codeArea = new JTextPane();
        statusLabel = new JLabel("就绪");
        lineColumnLabel = new JLabel("行: 1, 列: 1");
        languageCombo = new JComboBox<>(new String[]{"Java", "Python", "JavaScript", "HTML", "CSS", "Plain Text"});
        
        initializeGUI();
        setupSyntaxHighlighting();
        setupEventHandlers();
        updateTitle();
    }
    
    private void initializeGUI() {
        setTitle("💻 代码编辑器");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 创建菜单栏
        createMenuBar();
        
        // 创建工具栏
        createToolBar();
        
        // 创建编辑区域
        createEditorArea();
        
        // 创建状态栏
        createStatusBar();
        
        // 设置关闭操作
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void createMenuBar() {
        var menuBar = new JMenuBar();
        
        // 文件菜单
        var fileMenu = new JMenu("文件");
        
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
        saveAsItem.addActionListener(e -> saveAsFile());
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        
        // 编辑菜单
        var editMenu = new JMenu("编辑");
        
        var cutItem = new JMenuItem("剪切");
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(e -> codeArea.cut());
        
        var copyItem = new JMenuItem("复制");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> codeArea.copy());
        
        var pasteItem = new JMenuItem("粘贴");
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> codeArea.paste());
        
        var findItem = new JMenuItem("查找");
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        findItem.addActionListener(e -> showFindDialog());
        
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(findItem);
        
        // 视图菜单
        var viewMenu = new JMenu("视图");
        
        var fontItem = new JMenuItem("字体设置");
        fontItem.addActionListener(e -> showFontDialog());
        
        var themeMenu = new JMenu("主题");
        var lightTheme = new JMenuItem("浅色主题");
        var darkTheme = new JMenuItem("深色主题");
        
        lightTheme.addActionListener(e -> applyLightTheme());
        darkTheme.addActionListener(e -> applyDarkTheme());
        
        themeMenu.add(lightTheme);
        themeMenu.add(darkTheme);
        
        viewMenu.add(fontItem);
        viewMenu.add(themeMenu);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        
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
        
        var runButton = new JButton("▶️");
        runButton.setToolTipText("运行");
        runButton.addActionListener(e -> runCode());
        
        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.addSeparator();
        toolBar.add(runButton);
        toolBar.addSeparator();
        toolBar.add(new JLabel("语言:"));
        toolBar.add(languageCombo);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void createEditorArea() {
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeArea.setBackground(Color.WHITE);
        codeArea.setForeground(Color.BLACK);
        codeArea.setCaretColor(Color.BLACK);
        codeArea.setMargin(new Insets(10, 10, 10, 10));
        
        // 添加行号
        var lineNumbers = new LineNumberArea(codeArea);
        var scrollPane = new JScrollPane(codeArea);
        scrollPane.setRowHeaderView(lineNumbers);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        lineColumnLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(lineColumnLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupSyntaxHighlighting() {
        var doc = codeArea.getStyledDocument();
        
        // 创建样式
        var defaultStyle = doc.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);
        
        var keywordStyle = doc.addStyle("keyword", null);
        StyleConstants.setForeground(keywordStyle, new Color(0, 0, 255));
        StyleConstants.setBold(keywordStyle, true);
        
        var commentStyle = doc.addStyle("comment", null);
        StyleConstants.setForeground(commentStyle, new Color(0, 128, 0));
        StyleConstants.setItalic(commentStyle, true);
        
        var stringStyle = doc.addStyle("string", null);
        StyleConstants.setForeground(stringStyle, new Color(163, 21, 21));
        
        styles.put("default", defaultStyle);
        styles.put("keyword", keywordStyle);
        styles.put("comment", commentStyle);
        styles.put("string", stringStyle);
    }
    
    private void setupEventHandlers() {
        // 监听文本变化
        codeArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { textChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { textChanged(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { textChanged(); }
        });
        
        // 监听光标位置变化
        codeArea.addCaretListener(e -> updateLineColumn());
        
        // 语言选择变化
        languageCombo.addActionListener(e -> applySyntaxHighlighting());
    }
    
    private void textChanged() {
        isModified = true;
        updateTitle();
        applySyntaxHighlighting();
    }
    
    private void updateTitle() {
        var title = "代码编辑器";
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
    
    private void updateLineColumn() {
        try {
            var caretPos = codeArea.getCaretPosition();
            var line = codeArea.getDocument().getDefaultRootElement().getElementIndex(caretPos) + 1;
            var lineStart = codeArea.getDocument().getDefaultRootElement().getElement(line - 1).getStartOffset();
            var column = caretPos - lineStart + 1;
            lineColumnLabel.setText(String.format("行: %d, 列: %d", line, column));
        } catch (Exception e) {
            lineColumnLabel.setText("行: 1, 列: 1");
        }
    }
    
    private void applySyntaxHighlighting() {
        var language = (String) languageCombo.getSelectedItem();
        var text = codeArea.getText();
        var doc = codeArea.getStyledDocument();
        
        // 清除现有样式
        doc.setCharacterAttributes(0, text.length(), styles.get("default"), true);
        
        // 根据语言应用语法高亮
        switch (language) {
            case "Java" -> highlightJava(text, doc);
            case "Python" -> highlightPython(text, doc);
            case "JavaScript" -> highlightJavaScript(text, doc);
            // 其他语言可以继续添加
        }
    }
    
    private void highlightJava(String text, StyledDocument doc) {
        var javaKeywords = new String[]{
            "public", "private", "protected", "static", "final", "abstract",
            "class", "interface", "extends", "implements", "package", "import",
            "if", "else", "for", "while", "do", "switch", "case", "default",
            "try", "catch", "finally", "throw", "throws", "return", "break", "continue",
            "int", "long", "double", "float", "boolean", "char", "byte", "short",
            "String", "void", "null", "true", "false", "new", "this", "super"
        };
        
        highlightKeywords(text, doc, javaKeywords);
        highlightComments(text, doc, "//", "/*", "*/");
        highlightStrings(text, doc);
    }
    
    private void highlightPython(String text, StyledDocument doc) {
        var pythonKeywords = new String[]{
            "def", "class", "if", "elif", "else", "for", "while", "try", "except",
            "finally", "import", "from", "as", "return", "break", "continue",
            "pass", "lambda", "with", "yield", "global", "nonlocal",
            "True", "False", "None", "and", "or", "not", "in", "is"
        };
        
        highlightKeywords(text, doc, pythonKeywords);
        highlightComments(text, doc, "#", null, null);
        highlightStrings(text, doc);
    }
    
    private void highlightJavaScript(String text, StyledDocument doc) {
        var jsKeywords = new String[]{
            "function", "var", "let", "const", "if", "else", "for", "while", "do",
            "switch", "case", "default", "try", "catch", "finally", "return",
            "break", "continue", "true", "false", "null", "undefined",
            "new", "this", "typeof", "instanceof", "class", "extends"
        };
        
        highlightKeywords(text, doc, jsKeywords);
        highlightComments(text, doc, "//", "/*", "*/");
        highlightStrings(text, doc);
    }
    
    private void highlightKeywords(String text, StyledDocument doc, String[] keywords) {
        for (var keyword : keywords) {
            var index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) {
                // 检查是否是完整单词
                var isWord = (index == 0 || !Character.isLetterOrDigit(text.charAt(index - 1))) &&
                           (index + keyword.length() == text.length() || 
                            !Character.isLetterOrDigit(text.charAt(index + keyword.length())));
                
                if (isWord) {
                    doc.setCharacterAttributes(index, keyword.length(), styles.get("keyword"), false);
                }
                index += keyword.length();
            }
        }
    }
    
    private void highlightComments(String text, StyledDocument doc, String singleLine, String multiStart, String multiEnd) {
        // 单行注释
        if (singleLine != null) {
            var lines = text.split("\n");
            var offset = 0;
            for (var line : lines) {
                var commentIndex = line.indexOf(singleLine);
                if (commentIndex != -1) {
                    doc.setCharacterAttributes(offset + commentIndex, 
                        line.length() - commentIndex, styles.get("comment"), false);
                }
                offset += line.length() + 1; // +1 for newline
            }
        }
        
        // 多行注释
        if (multiStart != null && multiEnd != null) {
            var startIndex = 0;
            while ((startIndex = text.indexOf(multiStart, startIndex)) != -1) {
                var endIndex = text.indexOf(multiEnd, startIndex + multiStart.length());
                if (endIndex != -1) {
                    doc.setCharacterAttributes(startIndex, 
                        endIndex - startIndex + multiEnd.length(), styles.get("comment"), false);
                    startIndex = endIndex + multiEnd.length();
                } else {
                    break;
                }
            }
        }
    }
    
    private void highlightStrings(String text, StyledDocument doc) {
        // 双引号字符串
        highlightStringType(text, doc, '"');
        // 单引号字符串
        highlightStringType(text, doc, '\'');
    }
    
    private void highlightStringType(String text, StyledDocument doc, char quote) {
        var inString = false;
        var start = 0;
        
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == quote && (i == 0 || text.charAt(i - 1) != '\\')) {
                if (!inString) {
                    start = i;
                    inString = true;
                } else {
                    doc.setCharacterAttributes(start, i - start + 1, styles.get("string"), false);
                    inString = false;
                }
            }
        }
    }
    
    private void newFile() {
        if (checkSaveBeforeAction()) {
            codeArea.setText("");
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
            "代码文件", "java", "py", "js", "html", "css", "txt"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            try {
                var content = Files.readString(Path.of(file.getAbsolutePath()));
                codeArea.setText(content);
                currentFile = file;
                isModified = false;
                updateTitle();
                statusLabel.setText("已打开: " + file.getName());
                
                // 根据文件扩展名设置语言
                var extension = getFileExtension(file.getName());
                setLanguageByExtension(extension);
                
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
                Files.writeString(Path.of(currentFile.getAbsolutePath()), codeArea.getText());
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
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            saveFile();
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
                return !isModified;
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
    
    private void showFindDialog() {
        var searchText = JOptionPane.showInputDialog(this, "查找:", "查找", JOptionPane.QUESTION_MESSAGE);
        if (searchText != null && !searchText.isEmpty()) {
            var text = codeArea.getText();
            var index = text.indexOf(searchText, codeArea.getCaretPosition());
            if (index == -1) {
                index = text.indexOf(searchText, 0);
            }
            
            if (index != -1) {
                codeArea.setCaretPosition(index);
                codeArea.select(index, index + searchText.length());
            } else {
                JOptionPane.showMessageDialog(this, "未找到: " + searchText);
            }
        }
    }
    
    private void showFontDialog() {
        var currentFont = codeArea.getFont();
        var fontName = JOptionPane.showInputDialog(this, "字体名称:", currentFont.getName());
        if (fontName != null) {
            var sizeStr = JOptionPane.showInputDialog(this, "字体大小:", currentFont.getSize());
            try {
                var size = Integer.parseInt(sizeStr);
                codeArea.setFont(new Font(fontName, Font.PLAIN, size));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "无效的字体大小");
            }
        }
    }
    
    private void applyLightTheme() {
        codeArea.setBackground(Color.WHITE);
        codeArea.setForeground(Color.BLACK);
        codeArea.setCaretColor(Color.BLACK);
    }
    
    private void applyDarkTheme() {
        codeArea.setBackground(new Color(43, 43, 43));
        codeArea.setForeground(Color.WHITE);
        codeArea.setCaretColor(Color.WHITE);
    }
    
    private void runCode() {
        if (currentFile == null) {
            JOptionPane.showMessageDialog(this, "请先保存文件");
            return;
        }
        
        var extension = getFileExtension(currentFile.getName());
        var command = switch (extension.toLowerCase()) {
            case "java" -> "java " + currentFile.getAbsolutePath();
            case "py" -> "python " + currentFile.getAbsolutePath();
            case "js" -> "node " + currentFile.getAbsolutePath();
            default -> null;
        };
        
        if (command != null) {
            try {
                // 使用ProcessBuilder替代已过时的Runtime.exec()
                var commandParts = command.split(" ");
                var processBuilder = new ProcessBuilder(commandParts);
                processBuilder.start();
                JOptionPane.showMessageDialog(this, "程序已启动");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "运行失败: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "不支持运行此类型文件");
        }
    }
    
    private String getFileExtension(String fileName) {
        var lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }
    
    private void setLanguageByExtension(String extension) {
        var language = switch (extension.toLowerCase()) {
            case "java" -> "Java";
            case "py" -> "Python";
            case "js" -> "JavaScript";
            case "html", "htm" -> "HTML";
            case "css" -> "CSS";
            default -> "Plain Text";
        };
        languageCombo.setSelectedItem(language);
    }
    
    // 简单的行号组件
    static class LineNumberArea extends JComponent {
        private final JTextPane textPane;
        
        public LineNumberArea(JTextPane textPane) {
            this.textPane = textPane;
            setPreferredSize(new Dimension(50, 0));
            setFont(new Font("Monospaced", Font.PLAIN, 12));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            g.setColor(new Color(240, 240, 240));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.GRAY);
            g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            
            g.setColor(Color.BLACK);
            var doc = textPane.getDocument();
            var root = doc.getDefaultRootElement();
            var lineCount = root.getElementCount();
            
            var fontMetrics = g.getFontMetrics();
            var lineHeight = fontMetrics.getHeight();
            
            for (int i = 1; i <= lineCount; i++) {
                var y = i * lineHeight - fontMetrics.getDescent();
                g.drawString(String.valueOf(i), 5, y);
            }
        }
    }
}
