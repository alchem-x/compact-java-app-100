import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

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

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new TextCounter().setVisible(true);
    });
}

static class TextCounter extends JFrame {
    private JTextArea textArea;
    private JLabel charCountLabel;
    private JLabel wordCountLabel;
    private JLabel lineCountLabel;
    private JLabel paragraphCountLabel;
    private JTextArea statsArea;
    
    public TextCounter() {
        initializeGUI();
        updateStats();
    }
    
    private void initializeGUI() {
        setTitle("文本统计器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        
        // 顶部工具栏
        var toolPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        toolPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        var openButton = new JButton("📁 打开文件");
        AppleDesign.styleButton(openButton, AppleDesign.SYSTEM_BLUE, Color.WHITE);
        openButton.addActionListener(this::openFile);

        var clearButton = new JButton("🗑️ 清空");
        AppleDesign.styleButton(clearButton, AppleDesign.SYSTEM_RED, Color.WHITE);
        clearButton.addActionListener(e -> {
            textArea.setText("");
            updateStats();
        });

        var updateButton = new JButton("🔄 更新统计");
        AppleDesign.styleButton(updateButton, AppleDesign.SYSTEM_GREEN, Color.WHITE);
        updateButton.addActionListener(e -> updateStats());

        toolPanel.add(openButton);
        toolPanel.add(clearButton);
        toolPanel.add(updateButton);
        
        // 主要内容区域
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // 左侧文本输入区域
        var textPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        textPanel.setLayout(new BorderLayout());
        textPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        var titleLabel = new JLabel("文本输入");
        titleLabel.setFont(AppleDesign.TITLE_FONT);
        titleLabel.setForeground(AppleDesign.SYSTEM_BLUE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, AppleDesign.MEDIUM_SPACING, 0));
        textPanel.add(titleLabel, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setFont(AppleDesign.MONO_FONT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText("在这里输入或粘贴您要统计的文本...");
        textArea.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        var textScrollPane = new JScrollPane(textArea);
        textScrollPane.setBorder(BorderFactory.createEmptyBorder());
        textPanel.add(textScrollPane, BorderLayout.CENTER);
        
        // 添加文档监听器实现实时更新
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStats(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStats(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStats(); }
        });
        
        
        // 右侧统计区域
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // 基本统计面板
        var basicStatsPanel = AppleDesign.createRoundedPanel(AppleDesign.SMALL_RADIUS, AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        basicStatsPanel.setLayout(new GridLayout(4, 1, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        basicStatsPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        charCountLabel = new JLabel("字符数: 0");
        charCountLabel.setFont(AppleDesign.TITLE_FONT);
        charCountLabel.setForeground(AppleDesign.SYSTEM_BLUE);

        wordCountLabel = new JLabel("单词数: 0");
        wordCountLabel.setFont(AppleDesign.TITLE_FONT);
        wordCountLabel.setForeground(AppleDesign.SYSTEM_GREEN);

        lineCountLabel = new JLabel("行数: 0");
        lineCountLabel.setFont(AppleDesign.TITLE_FONT);
        lineCountLabel.setForeground(AppleDesign.SYSTEM_ORANGE);

        paragraphCountLabel = new JLabel("段落数: 0");
        paragraphCountLabel.setFont(AppleDesign.TITLE_FONT);
        paragraphCountLabel.setForeground(AppleDesign.SYSTEM_PURPLE);

        basicStatsPanel.add(charCountLabel);
        basicStatsPanel.add(wordCountLabel);
        basicStatsPanel.add(lineCountLabel);
        basicStatsPanel.add(paragraphCountLabel);
        
        // 详细统计区域
        var detailPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        detailPanel.setLayout(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        var detailTitle = new JLabel("详细分析");
        detailTitle.setFont(AppleDesign.TITLE_FONT);
        detailTitle.setForeground(AppleDesign.SYSTEM_INDIGO);
        detailTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, AppleDesign.MEDIUM_SPACING, 0));
        detailPanel.add(detailTitle, BorderLayout.NORTH);

        statsArea = new JTextArea();
        statsArea.setFont(AppleDesign.MONO_FONT);
        statsArea.setEditable(false);
        statsArea.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        statsArea.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        var statsScrollPane = new JScrollPane(statsArea);
        statsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        detailPanel.add(statsScrollPane, BorderLayout.CENTER);
        
        rightPanel.add(basicStatsPanel, BorderLayout.NORTH);
        rightPanel.add(detailPanel, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(textPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(400);
        
        add(splitPane, BorderLayout.CENTER);
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void updateStats() {
        String text = textArea.getText();
        
        // 基本统计
        int charCount = text.length();
        int charCountNoSpace = text.replaceAll("\\s", "").length();
        int lineCount = text.isEmpty() ? 0 : text.split("\n").length;
        
        // 单词统计
        String[] words = text.trim().split("\\s+");
        int wordCount = text.trim().isEmpty() ? 0 : words.length;
        
        // 段落统计
        String[] paragraphs = text.split("\n\\s*\n");
        int paragraphCount = text.trim().isEmpty() ? 0 : paragraphs.length;
        
        // 更新基本统计标签
        charCountLabel.setText("字符数: " + charCount + " (不含空格: " + charCountNoSpace + ")");
        wordCountLabel.setText("单词数: " + wordCount);
        lineCountLabel.setText("行数: " + lineCount);
        paragraphCountLabel.setText("段落数: " + paragraphCount);
        
        // 更新详细统计
        updateDetailedStats(text, words);
    }
    
    private void updateDetailedStats(String text, String[] words) {
        if (text.isEmpty()) {
            statsArea.setText("");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        
        // 字符分析
        sb.append("=== 字符分析 ===\n");
        long letterCount = text.chars().filter(Character::isLetter).count();
        long digitCount = text.chars().filter(Character::isDigit).count();
        long spaceCount = text.chars().filter(Character::isWhitespace).count();
        long punctCount = text.length() - letterCount - digitCount - spaceCount;
        
        sb.append(String.format("字母: %d\n", letterCount));
        sb.append(String.format("数字: %d\n", digitCount));
        sb.append(String.format("空白字符: %d\n", spaceCount));
        sb.append(String.format("标点符号: %d\n", punctCount));
        
        // 单词分析
        sb.append("\n=== 单词分析 ===\n");
        if (words.length > 0) {
            // 平均词长
            double avgWordLength = 0;
            String longestWord = "";
            String shortestWord = words[0];
            
            for (String word : words) {
                String cleanWord = word.replaceAll("[^a-zA-Z\\u4e00-\\u9fa5]", "");
                avgWordLength += cleanWord.length();
                
                if (cleanWord.length() > longestWord.length()) {
                    longestWord = cleanWord;
                }
                if (cleanWord.length() < shortestWord.length() && !cleanWord.isEmpty()) {
                    shortestWord = cleanWord;
                }
            }
            avgWordLength /= words.length;
            
            sb.append(String.format("平均词长: %.1f\n", avgWordLength));
            sb.append(String.format("最长单词: %s (%d字符)\n", longestWord, longestWord.length()));
            sb.append(String.format("最短单词: %s (%d字符)\n", shortestWord, shortestWord.length()));
        }
        
        // 阅读时间估算
        sb.append("\n=== 阅读时间估算 ===\n");
        int readingTimeMinutes = words.length / 200; // 每分钟200字
        int readingTimeSeconds = (words.length % 200) * 60 / 200;
        sb.append(String.format("预计阅读时间: %d分%d秒\n", readingTimeMinutes, readingTimeSeconds));
        
        // 字符频率统计（前10个）
        sb.append("\n=== 字符频率 (前10) ===\n");
        Map<Character, Integer> charFreq = new HashMap<>();
        for (char c : text.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                charFreq.put(c, charFreq.getOrDefault(c, 0) + 1);
            }
        }
        
        charFreq.entrySet().stream()
            .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> 
                sb.append(String.format("'%c': %d次\n", entry.getKey(), entry.getValue())));
        
        // 单词频率统计（前10个）
        sb.append("\n=== 单词频率 (前10) ===\n");
        Map<String, Integer> wordFreq = new HashMap<>();
        for (String word : words) {
            String cleanWord = word.toLowerCase().replaceAll("[^a-zA-Z\\u4e00-\\u9fa5]", "");
            if (!cleanWord.isEmpty()) {
                wordFreq.put(cleanWord, wordFreq.getOrDefault(cleanWord, 0) + 1);
            }
        }
        
        wordFreq.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> 
                sb.append(String.format("%s: %d次\n", entry.getKey(), entry.getValue())));
        
        statsArea.setText(sb.toString());
    }
    
    private void openFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "文本文件", "txt", "java", "py", "js", "html", "css", "md"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String content = new String(Files.readAllBytes(file.toPath()));
                textArea.setText(content);
                
                JOptionPane.showMessageDialog(this, 
                    "文件已加载: " + file.getName(), 
                    "成功", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "无法读取文件: " + ex.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
