import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new RegexTester().setVisible(true);
    });
}

static class RegexTester extends JFrame {
    private final JTextField regexField;
    private final JTextArea testTextArea;
    private final JTextArea resultArea;
    private final JButton testButton;
    private final JButton clearButton;
    private final JLabel statusLabel;
    private final JCheckBox caseSensitiveBox;
    private final JCheckBox multilineBox;
    private final JCheckBox dotallBox;
    private final JComboBox<String> commonPatternsCombo;
    
    private final Highlighter highlighter;
    private final Highlighter.HighlightPainter painter;
    
    public RegexTester() {
        regexField = new JTextField();
        testTextArea = new JTextArea();
        resultArea = new JTextArea();
        testButton = new JButton("测试匹配");
        clearButton = new JButton("清空");
        statusLabel = new JLabel("就绪");
        caseSensitiveBox = new JCheckBox("区分大小写", true);
        multilineBox = new JCheckBox("多行模式");
        dotallBox = new JCheckBox("点号匹配所有");
        
        // 常用正则表达式
        var commonPatterns = new String[]{
            "选择常用模式...",
            "\\d+ - 匹配数字",
            "[a-zA-Z]+ - 匹配字母",
            "\\w+ - 匹配单词字符",
            "\\s+ - 匹配空白字符",
            "^\\w+@\\w+\\.\\w+$ - 邮箱格式",
            "^1[3-9]\\d{9}$ - 手机号码",
            "^\\d{4}-\\d{2}-\\d{2}$ - 日期格式",
            "^https?://\\S+$ - URL格式",
            "\\b\\w{4,}\\b - 4个字符以上的单词"
        };
        commonPatternsCombo = new JComboBox<>(commonPatterns);
        
        highlighter = testTextArea.getHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        
        initializeGUI();
        setupEventHandlers();
        loadSampleData();
    }
    
    private void initializeGUI() {
        setTitle("🔍 正则表达式测试器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 创建顶部输入面板
        createInputPanel();
        
        // 创建主工作区
        createWorkArea();
        
        // 创建状态栏
        createStatusBar();
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void createInputPanel() {
        var inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createTitledBorder("正则表达式"));
        
        // 常用模式选择
        var patternPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        patternPanel.add(new JLabel("常用模式:"));
        commonPatternsCombo.setPreferredSize(new Dimension(300, 25));
        patternPanel.add(commonPatternsCombo);
        
        // 正则表达式输入
        var regexPanel = new JPanel(new BorderLayout());
        regexPanel.add(new JLabel("正则表达式:"), BorderLayout.WEST);
        regexField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        regexField.setPreferredSize(new Dimension(0, 30));
        regexPanel.add(regexField, BorderLayout.CENTER);
        
        // 选项面板
        var optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.add(caseSensitiveBox);
        optionsPanel.add(multilineBox);
        optionsPanel.add(dotallBox);
        optionsPanel.add(testButton);
        optionsPanel.add(clearButton);
        
        inputPanel.add(patternPanel);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(regexPanel);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(optionsPanel);
        
        add(inputPanel, BorderLayout.NORTH);
    }
    
    private void createWorkArea() {
        var workPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        workPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 测试文本面板
        var testPanel = new JPanel(new BorderLayout());
        testPanel.setBorder(BorderFactory.createTitledBorder("测试文本"));
        
        testTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        testTextArea.setLineWrap(true);
        testTextArea.setWrapStyleWord(true);
        
        var testScrollPane = new JScrollPane(testTextArea);
        testPanel.add(testScrollPane, BorderLayout.CENTER);
        
        // 结果面板
        var resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("匹配结果"));
        
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(248, 248, 248));
        
        var resultScrollPane = new JScrollPane(resultArea);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);
        
        workPanel.add(testPanel);
        workPanel.add(resultPanel);
        
        add(workPanel, BorderLayout.CENTER);
    }
    
    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        testButton.addActionListener(this::performTest);
        clearButton.addActionListener(this::clearAll);
        regexField.addActionListener(this::performTest);
        
        commonPatternsCombo.addActionListener(e -> {
            var selected = (String) commonPatternsCombo.getSelectedItem();
            if (selected != null && !selected.startsWith("选择")) {
                var pattern = selected.split(" - ")[0];
                regexField.setText(pattern);
            }
        });
    }
    
    private void loadSampleData() {
        regexField.setText("\\b\\w+@\\w+\\.\\w+\\b");
        testTextArea.setText("""
            这是一些测试文本，包含各种内容：
            
            邮箱地址：
            zhang.san@example.com
            li_si@company.org
            wangwu123@test.net
            invalid-email
            
            电话号码：
            13812345678
            15987654321
            400-123-4567
            
            日期：
            2024-03-15
            2024/12/31
            March 15, 2024
            
            网址：
            https://www.example.com
            http://test.org/path
            ftp://files.server.com
            
            其他文本内容...
            """);
    }
    
    private void performTest(ActionEvent e) {
        var regex = regexField.getText().trim();
        var testText = testTextArea.getText();
        
        if (regex.isEmpty()) {
            statusLabel.setText("请输入正则表达式");
            return;
        }
        
        try {
            // 清除之前的高亮
            highlighter.removeAllHighlights();
            
            // 构建Pattern标志
            var flags = 0;
            if (!caseSensitiveBox.isSelected()) {
                flags |= Pattern.CASE_INSENSITIVE;
            }
            if (multilineBox.isSelected()) {
                flags |= Pattern.MULTILINE;
            }
            if (dotallBox.isSelected()) {
                flags |= Pattern.DOTALL;
            }
            
            var pattern = Pattern.compile(regex, flags);
            var matcher = pattern.matcher(testText);
            
            var result = new StringBuilder();
            result.append("正则表达式: ").append(regex).append("\n");
            result.append("选项: ");
            if (!caseSensitiveBox.isSelected()) result.append("忽略大小写 ");
            if (multilineBox.isSelected()) result.append("多行模式 ");
            if (dotallBox.isSelected()) result.append("点号匹配所有 ");
            result.append("\n\n");
            
            var matchCount = 0;
            while (matcher.find()) {
                matchCount++;
                var match = matcher.group();
                var start = matcher.start();
                var end = matcher.end();
                
                result.append("匹配 ").append(matchCount).append(": \"").append(match).append("\"\n");
                result.append("  位置: ").append(start).append("-").append(end).append("\n");
                
                // 获取捕获组
                var groupCount = matcher.groupCount();
                if (groupCount > 0) {
                    result.append("  捕获组:\n");
                    for (int i = 1; i <= groupCount; i++) {
                        var group = matcher.group(i);
                        result.append("    组").append(i).append(": \"").append(group != null ? group : "null").append("\"\n");
                    }
                }
                result.append("\n");
                
                // 高亮匹配的文本
                try {
                    highlighter.addHighlight(start, end, painter);
                } catch (BadLocationException ex) {
                    // 忽略高亮错误
                }
            }
            
            if (matchCount == 0) {
                result.append("没有找到匹配项");
            } else {
                result.append("总共找到 ").append(matchCount).append(" 个匹配项");
            }
            
            resultArea.setText(result.toString());
            statusLabel.setText("匹配完成，找到 " + matchCount + " 个匹配项");
            
        } catch (PatternSyntaxException ex) {
            resultArea.setText("正则表达式语法错误:\n" + ex.getMessage());
            statusLabel.setText("语法错误");
        } catch (Exception ex) {
            resultArea.setText("测试失败: " + ex.getMessage());
            statusLabel.setText("测试失败");
        }
    }
    
    private void clearAll(ActionEvent e) {
        regexField.setText("");
        testTextArea.setText("");
        resultArea.setText("");
        highlighter.removeAllHighlights();
        statusLabel.setText("已清空");
    }
}
