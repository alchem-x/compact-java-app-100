import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.util.Random;
import java.util.UUID;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new RandomGenerator().setVisible(true);
    });
}

static class RandomGenerator extends JFrame {
    private final JTabbedPane tabbedPane;
    
    // 数字生成器组件
    private final JTextField minField;
    private final JTextField maxField;
    private final JTextField countField;
    private final JTextArea numberResultArea;
    private final JButton generateNumberButton;
    private final JCheckBox uniqueNumberBox;
    
    // 字符串生成器组件
    private final JTextField lengthField;
    private final JTextField stringCountField;
    private final JTextArea stringResultArea;
    private final JButton generateStringButton;
    private final JCheckBox includeUpperBox;
    private final JCheckBox includeLowerBox;
    private final JCheckBox includeDigitsBox;
    private final JCheckBox includeSymbolsBox;
    
    // UUID生成器组件
    private final JTextField uuidCountField;
    private final JTextArea uuidResultArea;
    private final JButton generateUuidButton;
    private final JCheckBox upperCaseUuidBox;
    private final JCheckBox removeDashesBox;
    
    private final JLabel statusLabel;
    private final Random random = new Random();
    
    public RandomGenerator() {
        tabbedPane = new JTabbedPane();
        
        // 数字生成器
        minField = new JTextField("1");
        maxField = new JTextField("100");
        countField = new JTextField("10");
        numberResultArea = new JTextArea();
        generateNumberButton = new JButton("生成数字");
        uniqueNumberBox = new JCheckBox("不重复", true);
        
        // 字符串生成器
        lengthField = new JTextField("8");
        stringCountField = new JTextField("5");
        stringResultArea = new JTextArea();
        generateStringButton = new JButton("生成字符串");
        includeUpperBox = new JCheckBox("大写字母", true);
        includeLowerBox = new JCheckBox("小写字母", true);
        includeDigitsBox = new JCheckBox("数字", true);
        includeSymbolsBox = new JCheckBox("符号");
        
        // UUID生成器
        uuidCountField = new JTextField("5");
        uuidResultArea = new JTextArea();
        generateUuidButton = new JButton("生成UUID");
        upperCaseUuidBox = new JCheckBox("大写");
        removeDashesBox = new JCheckBox("移除连字符");
        
        statusLabel = new JLabel("就绪");
        
        initializeGUI();
        setupEventHandlers();
    }
    
    private void initializeGUI() {
        setTitle("🎲 随机生成器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 创建各个标签页
        createNumberTab();
        createStringTab();
        createUuidTab();
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // 创建状态栏
        createStatusBar();
        
        setSize(600, 500);
        setLocationRelativeTo(null);
    }
    
    private void createNumberTab() {
        var numberPanel = new JPanel(new BorderLayout());
        
        // 设置面板
        var settingsPanel = new JPanel();
        settingsPanel.setBorder(BorderFactory.createTitledBorder("设置"));
        settingsPanel.setLayout(new GridLayout(2, 4, 10, 5));
        
        settingsPanel.add(new JLabel("最小值:"));
        minField.setPreferredSize(new Dimension(80, 25));
        settingsPanel.add(minField);
        
        settingsPanel.add(new JLabel("最大值:"));
        maxField.setPreferredSize(new Dimension(80, 25));
        settingsPanel.add(maxField);
        
        settingsPanel.add(new JLabel("生成数量:"));
        countField.setPreferredSize(new Dimension(80, 25));
        settingsPanel.add(countField);
        
        settingsPanel.add(uniqueNumberBox);
        settingsPanel.add(generateNumberButton);
        
        // 结果面板
        var resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("生成结果"));
        
        numberResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        numberResultArea.setEditable(false);
        numberResultArea.setBackground(new Color(248, 248, 248));
        
        var scrollPane = new JScrollPane(numberResultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        var copyButton = new JButton("复制结果");
        copyButton.addActionListener(e -> copyToClipboard(numberResultArea.getText()));
        resultPanel.add(copyButton, BorderLayout.SOUTH);
        
        numberPanel.add(settingsPanel, BorderLayout.NORTH);
        numberPanel.add(resultPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("🔢 随机数字", numberPanel);
    }
    
    private void createStringTab() {
        var stringPanel = new JPanel(new BorderLayout());
        
        // 设置面板
        var settingsPanel = new JPanel();
        settingsPanel.setBorder(BorderFactory.createTitledBorder("设置"));
        settingsPanel.setLayout(new GridLayout(3, 4, 10, 5));
        
        settingsPanel.add(new JLabel("字符串长度:"));
        lengthField.setPreferredSize(new Dimension(80, 25));
        settingsPanel.add(lengthField);
        
        settingsPanel.add(new JLabel("生成数量:"));
        stringCountField.setPreferredSize(new Dimension(80, 25));
        settingsPanel.add(stringCountField);
        
        settingsPanel.add(includeUpperBox);
        settingsPanel.add(includeLowerBox);
        settingsPanel.add(includeDigitsBox);
        settingsPanel.add(includeSymbolsBox);
        
        settingsPanel.add(generateStringButton);
        settingsPanel.add(new JLabel()); // 占位
        
        // 结果面板
        var resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("生成结果"));
        
        stringResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        stringResultArea.setEditable(false);
        stringResultArea.setBackground(new Color(248, 248, 248));
        
        var scrollPane = new JScrollPane(stringResultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        var copyButton = new JButton("复制结果");
        copyButton.addActionListener(e -> copyToClipboard(stringResultArea.getText()));
        resultPanel.add(copyButton, BorderLayout.SOUTH);
        
        stringPanel.add(settingsPanel, BorderLayout.NORTH);
        stringPanel.add(resultPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("🔤 随机字符串", stringPanel);
    }
    
    private void createUuidTab() {
        var uuidPanel = new JPanel(new BorderLayout());
        
        // 设置面板
        var settingsPanel = new JPanel(new FlowLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("设置"));
        
        settingsPanel.add(new JLabel("生成数量:"));
        uuidCountField.setPreferredSize(new Dimension(80, 25));
        settingsPanel.add(uuidCountField);
        
        settingsPanel.add(upperCaseUuidBox);
        settingsPanel.add(removeDashesBox);
        settingsPanel.add(generateUuidButton);
        
        // 结果面板
        var resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("生成结果"));
        
        uuidResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        uuidResultArea.setEditable(false);
        uuidResultArea.setBackground(new Color(248, 248, 248));
        
        var scrollPane = new JScrollPane(uuidResultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        var copyButton = new JButton("复制结果");
        copyButton.addActionListener(e -> copyToClipboard(uuidResultArea.getText()));
        resultPanel.add(copyButton, BorderLayout.SOUTH);
        
        uuidPanel.add(settingsPanel, BorderLayout.NORTH);
        uuidPanel.add(resultPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("🆔 UUID", uuidPanel);
    }
    
    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        generateNumberButton.addActionListener(this::generateNumbers);
        generateStringButton.addActionListener(this::generateStrings);
        generateUuidButton.addActionListener(this::generateUuids);
    }
    
    private void generateNumbers(ActionEvent e) {
        try {
            var min = Integer.parseInt(minField.getText().trim());
            var max = Integer.parseInt(maxField.getText().trim());
            var count = Integer.parseInt(countField.getText().trim());
            
            if (min >= max) {
                statusLabel.setText("最小值必须小于最大值");
                return;
            }
            
            if (count <= 0 || count > 10000) {
                statusLabel.setText("生成数量应在1-10000之间");
                return;
            }
            
            var result = new StringBuilder();
            
            if (uniqueNumberBox.isSelected()) {
                // 生成不重复的数字
                var range = max - min + 1;
                if (count > range) {
                    statusLabel.setText("不重复模式下，数量不能超过范围大小");
                    return;
                }
                
                var numbers = new java.util.ArrayList<Integer>();
                for (int i = min; i <= max; i++) {
                    numbers.add(i);
                }
                java.util.Collections.shuffle(numbers, random);
                
                for (int i = 0; i < count; i++) {
                    result.append(numbers.get(i));
                    if (i < count - 1) result.append("\n");
                }
            } else {
                // 生成可重复的数字
                for (int i = 0; i < count; i++) {
                    var number = random.nextInt(max - min + 1) + min;
                    result.append(number);
                    if (i < count - 1) result.append("\n");
                }
            }
            
            numberResultArea.setText(result.toString());
            statusLabel.setText("生成了 " + count + " 个随机数字");
            
        } catch (NumberFormatException ex) {
            statusLabel.setText("请输入有效的数字");
        }
    }
    
    private void generateStrings(ActionEvent e) {
        try {
            var length = Integer.parseInt(lengthField.getText().trim());
            var count = Integer.parseInt(stringCountField.getText().trim());
            
            if (length <= 0 || length > 1000) {
                statusLabel.setText("字符串长度应在1-1000之间");
                return;
            }
            
            if (count <= 0 || count > 1000) {
                statusLabel.setText("生成数量应在1-1000之间");
                return;
            }
            
            var charset = buildCharset();
            if (charset.isEmpty()) {
                statusLabel.setText("请至少选择一种字符类型");
                return;
            }
            
            var result = new StringBuilder();
            for (int i = 0; i < count; i++) {
                var randomString = generateRandomString(length, charset);
                result.append(randomString);
                if (i < count - 1) result.append("\n");
            }
            
            stringResultArea.setText(result.toString());
            statusLabel.setText("生成了 " + count + " 个随机字符串");
            
        } catch (NumberFormatException ex) {
            statusLabel.setText("请输入有效的数字");
        }
    }
    
    private String buildCharset() {
        var charset = new StringBuilder();
        
        if (includeUpperBox.isSelected()) {
            charset.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }
        if (includeLowerBox.isSelected()) {
            charset.append("abcdefghijklmnopqrstuvwxyz");
        }
        if (includeDigitsBox.isSelected()) {
            charset.append("0123456789");
        }
        if (includeSymbolsBox.isSelected()) {
            charset.append("!@#$%^&*()_+-=[]{}|;:,.<>?");
        }
        
        return charset.toString();
    }
    
    private String generateRandomString(int length, String charset) {
        var result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            var index = random.nextInt(charset.length());
            result.append(charset.charAt(index));
        }
        return result.toString();
    }
    
    private void generateUuids(ActionEvent e) {
        try {
            var count = Integer.parseInt(uuidCountField.getText().trim());
            
            if (count <= 0 || count > 1000) {
                statusLabel.setText("生成数量应在1-1000之间");
                return;
            }
            
            var result = new StringBuilder();
            for (int i = 0; i < count; i++) {
                var uuid = UUID.randomUUID().toString();
                
                if (removeDashesBox.isSelected()) {
                    uuid = uuid.replace("-", "");
                }
                
                if (upperCaseUuidBox.isSelected()) {
                    uuid = uuid.toUpperCase();
                }
                
                result.append(uuid);
                if (i < count - 1) result.append("\n");
            }
            
            uuidResultArea.setText(result.toString());
            statusLabel.setText("生成了 " + count + " 个UUID");
            
        } catch (NumberFormatException ex) {
            statusLabel.setText("请输入有效的数字");
        }
    }
    
    private void copyToClipboard(String text) {
        if (text.isEmpty()) {
            statusLabel.setText("没有可复制的内容");
            return;
        }
        
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(text);
        clipboard.setContents(selection, null);
        statusLabel.setText("结果已复制到剪贴板");
    }
}
