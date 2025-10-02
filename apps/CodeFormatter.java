import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 代码格式化工具
 * 支持多种编程语言的代码格式化
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new CodeFormatter().setVisible(true);
    });
}

class CodeFormatter extends JFrame {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> languageCombo;
    
    public CodeFormatter() {
        initializeUI();
        loadSampleCode();
    }
    
    private void initializeUI() {
        setTitle("代码格式化工具 - Code Formatter");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 顶部控制面板
        var controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("语言:"));
        
        languageCombo = new JComboBox<>(new String[]{"Java", "JavaScript", "JSON", "XML", "CSS"});
        controlPanel.add(languageCombo);
        
        var formatBtn = new JButton("格式化");
        formatBtn.addActionListener(e -> formatCode());
        controlPanel.add(formatBtn);
        
        var clearBtn = new JButton("清空");
        clearBtn.addActionListener(e -> clearAll());
        controlPanel.add(clearBtn);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // 中央分割面板
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // 左侧输入
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        var inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("输入代码"));
        
        // 右侧输出
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 248, 248));
        var outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("格式化结果"));
        
        splitPane.setLeftComponent(inputScrollPane);
        splitPane.setRightComponent(outputScrollPane);
        splitPane.setDividerLocation(500);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void loadSampleCode() {
        String sample = "public class Hello{public static void main(String[]args){System.out.println(\"Hello World\");}}";
        inputArea.setText(sample);
        formatCode();
    }
    
    private void formatCode() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            outputArea.setText("");
            return;
        }
        
        String language = (String) languageCombo.getSelectedItem();
        String formatted = switch (language) {
            case "Java" -> formatJava(input);
            case "JavaScript" -> formatJavaScript(input);
            case "JSON" -> formatJSON(input);
            case "XML" -> formatXML(input);
            case "CSS" -> formatCSS(input);
            default -> input;
        };
        
        outputArea.setText(formatted);
    }
    
    private String formatJava(String code) {
        var result = new StringBuilder();
        int level = 0;
        boolean inString = false;
        
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            
            if (!inString && c == '"') {
                inString = true;
                result.append(c);
            } else if (inString && c == '"') {
                inString = false;
                result.append(c);
            } else if (inString) {
                result.append(c);
            } else {
                switch (c) {
                    case '{' -> {
                        result.append(c).append('\n');
                        level++;
                        result.append("    ".repeat(level));
                    }
                    case '}' -> {
                        if (result.length() > 0 && result.charAt(result.length() - 1) != '\n') {
                            result.append('\n');
                        }
                        level--;
                        result.append("    ".repeat(level)).append(c).append('\n');
                        if (level > 0) {
                            result.append("    ".repeat(level));
                        }
                    }
                    case ';' -> {
                        result.append(c).append('\n');
                        if (level > 0) {
                            result.append("    ".repeat(level));
                        }
                    }
                    default -> result.append(c);
                }
            }
        }
        
        return result.toString().trim();
    }
    
    private String formatJavaScript(String code) {
        return formatJava(code); // 简化处理，使用Java格式化逻辑
    }
    
    private String formatJSON(String code) {
        var result = new StringBuilder();
        int level = 0;
        boolean inString = false;
        
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            
            if (!inString && c == '"') {
                inString = true;
                result.append(c);
            } else if (inString && c == '"') {
                inString = false;
                result.append(c);
            } else if (inString) {
                result.append(c);
            } else {
                switch (c) {
                    case '{', '[' -> {
                        result.append(c).append('\n');
                        level++;
                        result.append("  ".repeat(level));
                    }
                    case '}', ']' -> {
                        if (result.length() > 0 && result.charAt(result.length() - 1) != '\n') {
                            result.append('\n');
                        }
                        level--;
                        result.append("  ".repeat(level)).append(c);
                    }
                    case ',' -> {
                        result.append(c).append('\n').append("  ".repeat(level));
                    }
                    case ':' -> {
                        result.append(c).append(' ');
                    }
                    case ' ' -> {
                        // 忽略多余空格
                    }
                    default -> result.append(c);
                }
            }
        }
        
        return result.toString().trim();
    }
    
    private String formatXML(String code) {
        var result = new StringBuilder();
        int level = 0;
        boolean inTag = false;
        
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            
            if (c == '<') {
                if (!inTag) {
                    if (result.length() > 0 && result.charAt(result.length() - 1) != '\n') {
                        result.append('\n');
                    }
                    result.append("  ".repeat(level));
                }
                inTag = true;
                result.append(c);
            } else if (c == '>') {
                result.append(c);
                inTag = false;
                
                if (i > 1 && code.charAt(i - 1) != '/') {
                    level++;
                }
            } else {
                result.append(c);
            }
        }
        
        return result.toString().trim();
    }
    
    private String formatCSS(String code) {
        var result = new StringBuilder();
        int level = 0;
        
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            
            switch (c) {
                case '{' -> {
                    result.append(" ").append(c).append('\n');
                    level++;
                }
                case '}' -> {
                    if (result.length() > 0 && result.charAt(result.length() - 1) != '\n') {
                        result.append('\n');
                    }
                    level--;
                    result.append("  ".repeat(level)).append(c).append('\n');
                }
                case ';' -> {
                    result.append(c).append('\n');
                    if (level > 0) {
                        result.append("  ".repeat(level));
                    }
                }
                case ':' -> {
                    result.append(c).append(' ');
                }
                default -> result.append(c);
            }
        }
        
        return result.toString().trim();
    }
    
    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
    }
}
