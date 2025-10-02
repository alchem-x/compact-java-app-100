import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new SimpleEncryptor().setVisible(true);
    });
}

static class SimpleEncryptor extends JFrame {
    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JTextField keyField;
    private final JButton encryptButton;
    private final JButton decryptButton;
    private final JButton copyButton;
    private final JButton clearButton;
    private final JLabel statusLabel;
    private final JComboBox<String> methodCombo;
    
    public SimpleEncryptor() {
        inputArea = new JTextArea();
        outputArea = new JTextArea();
        keyField = new JTextField();
        encryptButton = new JButton("加密");
        decryptButton = new JButton("解密");
        copyButton = new JButton("复制结果");
        clearButton = new JButton("清空");
        statusLabel = new JLabel("就绪");
        methodCombo = new JComboBox<>(new String[]{"凯撒密码", "简单替换", "异或加密"});
        
        initializeGUI();
        setupEventHandlers();
        loadSampleData();
    }
    
    private void initializeGUI() {
        setTitle("🔐 简易加密工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 创建控制面板
        createControlPanel();
        
        // 创建主工作区
        createWorkArea();
        
        // 创建状态栏
        createStatusBar();
        
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    private void createControlPanel() {
        var controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("加密设置"));
        
        // 方法选择
        var methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodPanel.add(new JLabel("加密方法:"));
        methodCombo.setPreferredSize(new Dimension(120, 25));
        methodPanel.add(methodCombo);
        
        // 密钥输入
        var keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.add(new JLabel("密钥:"));
        keyField.setPreferredSize(new Dimension(200, 25));
        keyField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        keyPanel.add(keyField);
        
        // 按钮面板
        var buttonPanel = new JPanel(new FlowLayout());
        encryptButton.setPreferredSize(new Dimension(80, 30));
        decryptButton.setPreferredSize(new Dimension(80, 30));
        copyButton.setPreferredSize(new Dimension(100, 30));
        clearButton.setPreferredSize(new Dimension(80, 30));
        
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(clearButton);
        
        controlPanel.add(methodPanel);
        controlPanel.add(keyPanel);
        controlPanel.add(buttonPanel);
        
        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void createWorkArea() {
        var workPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        workPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 输入面板
        var inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入文本"));
        
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        
        var inputScrollPane = new JScrollPane(inputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出面板
        var outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("输出结果"));
        
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 248, 248));
        
        var outputScrollPane = new JScrollPane(outputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        workPanel.add(inputPanel);
        workPanel.add(outputPanel);
        
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
        encryptButton.addActionListener(this::performEncrypt);
        decryptButton.addActionListener(this::performDecrypt);
        copyButton.addActionListener(this::copyResult);
        clearButton.addActionListener(this::clearAll);
        
        methodCombo.addActionListener(e -> updateKeyHint());
        updateKeyHint();
    }
    
    private void updateKeyHint() {
        var method = (String) methodCombo.getSelectedItem();
        var hint = switch (method) {
            case "凯撒密码" -> "输入偏移量 (1-25)";
            case "简单替换" -> "输入替换密钥";
            case "异或加密" -> "输入异或密钥";
            default -> "输入密钥";
        };
        keyField.setToolTipText(hint);
    }
    
    private void loadSampleData() {
        inputArea.setText("Hello World! 这是一个加密测试。");
        keyField.setText("3");
    }
    
    private void performEncrypt(ActionEvent e) {
        var input = inputArea.getText();
        var key = keyField.getText().trim();
        var method = (String) methodCombo.getSelectedItem();
        
        if (input.isEmpty()) {
            statusLabel.setText("请输入要加密的文本");
            return;
        }
        
        if (key.isEmpty()) {
            statusLabel.setText("请输入密钥");
            return;
        }
        
        try {
            var result = switch (method) {
                case "凯撒密码" -> caesarCipher(input, Integer.parseInt(key), true);
                case "简单替换" -> simpleSubstitution(input, key, true);
                case "异或加密" -> xorEncrypt(input, key);
                default -> throw new Exception("未知的加密方法");
            };
            
            outputArea.setText(result);
            statusLabel.setText("加密完成 - " + method);
            
        } catch (NumberFormatException ex) {
            statusLabel.setText("密钥格式错误");
        } catch (Exception ex) {
            statusLabel.setText("加密失败: " + ex.getMessage());
        }
    }
    
    private void performDecrypt(ActionEvent e) {
        var input = inputArea.getText();
        var key = keyField.getText().trim();
        var method = (String) methodCombo.getSelectedItem();
        
        if (input.isEmpty()) {
            statusLabel.setText("请输入要解密的文本");
            return;
        }
        
        if (key.isEmpty()) {
            statusLabel.setText("请输入密钥");
            return;
        }
        
        try {
            var result = switch (method) {
                case "凯撒密码" -> caesarCipher(input, Integer.parseInt(key), false);
                case "简单替换" -> simpleSubstitution(input, key, false);
                case "异或加密" -> xorEncrypt(input, key); // 异或加密解密相同
                default -> throw new Exception("未知的解密方法");
            };
            
            outputArea.setText(result);
            statusLabel.setText("解密完成 - " + method);
            
        } catch (NumberFormatException ex) {
            statusLabel.setText("密钥格式错误");
        } catch (Exception ex) {
            statusLabel.setText("解密失败: " + ex.getMessage());
        }
    }
    
    private String caesarCipher(String text, int shift, boolean encrypt) {
        if (shift < 1 || shift > 25) {
            throw new IllegalArgumentException("偏移量必须在1-25之间");
        }
        
        if (!encrypt) {
            shift = 26 - shift; // 解密时反向偏移
        }
        
        var result = new StringBuilder();
        for (var ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                var base = Character.isUpperCase(ch) ? 'A' : 'a';
                var shifted = (char) ((ch - base + shift) % 26 + base);
                result.append(shifted);
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
    
    private String simpleSubstitution(String text, String key, boolean encrypt) {
        var alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var keyUpper = key.toUpperCase();
        
        if (keyUpper.length() != 26) {
            throw new IllegalArgumentException("替换密钥必须是26个字符");
        }
        
        var result = new StringBuilder();
        for (var ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                var isUpper = Character.isUpperCase(ch);
                var upperCh = Character.toUpperCase(ch);
                
                char newCh;
                if (encrypt) {
                    var index = alphabet.indexOf(upperCh);
                    newCh = index >= 0 ? keyUpper.charAt(index) : upperCh;
                } else {
                    var index = keyUpper.indexOf(upperCh);
                    newCh = index >= 0 ? alphabet.charAt(index) : upperCh;
                }
                
                result.append(isUpper ? newCh : Character.toLowerCase(newCh));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
    
    private String xorEncrypt(String text, String key) {
        var result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            var textChar = text.charAt(i);
            var keyChar = key.charAt(i % key.length());
            var encrypted = (char) (textChar ^ keyChar);
            result.append(encrypted);
        }
        return result.toString();
    }
    
    private void copyResult(ActionEvent e) {
        var result = outputArea.getText();
        if (result.isEmpty()) {
            statusLabel.setText("没有可复制的内容");
            return;
        }
        
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(result);
        clipboard.setContents(selection, null);
        statusLabel.setText("结果已复制到剪贴板");
    }
    
    private void clearAll(ActionEvent e) {
        inputArea.setText("");
        outputArea.setText("");
        statusLabel.setText("已清空");
    }
}
