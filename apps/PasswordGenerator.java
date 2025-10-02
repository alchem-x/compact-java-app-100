import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🔐 密码生成器";

    // 主界面标题
    static final String MAIN_TITLE = "🔐 安全密码生成器";

    // 按钮文本
    static final String GENERATE_BUTTON = "重新生成";
    static final String COPY_BUTTON = "复制密码";
    static final String SAVE_BUTTON = "保存到文件";
    static final String BATCH_BUTTON = "批量生成";
    static final String SIMPLE_PRESET = "简单 (8位数字字母)";
    static final String COMPLEX_PRESET = "复杂 (16位全字符)";
    static final String PIN_PRESET = "PIN码 (6位数字)";
    static final String SECURE_PRESET = "安全 (20位全字符)";

    // 面板标题
    static final String PRESET_PANEL_TITLE = "预设模板";

    // 标签文本
    static final String PASSWORD_LABEL = "生成的密码:";
    static final String LENGTH_LABEL = "密码长度:";
    static final String CHAR_TYPE_LABEL = "包含字符类型:";
    static final String UPPERCASE_LABEL = "大写字母 (A-Z)";
    static final String LOWERCASE_LABEL = "小写字母 (a-z)";
    static final String NUMBERS_LABEL = "数字 (0-9)";
    static final String SYMBOLS_LABEL = "特殊符号";
    static final String ADVANCED_LABEL = "高级选项:";
    static final String EXCLUDE_SIMILAR_LABEL = "排除相似字符 (il1Lo0O)";
    static final String EXCLUDE_CHARS_LABEL = "排除字符:";
    static final String STRENGTH_LABEL = "密码强度:";

    // 密码强度等级
    static final String STRENGTH_WEAK = "弱";
    static final String STRENGTH_MEDIUM = "中等";
    static final String STRENGTH_STRONG = "强";
    static final String STRENGTH_VERY_STRONG = "很强";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_GENERATED = "密码已生成";
    static final String STATUS_COPIED = "密码已复制到剪贴板";
    static final String STATUS_SAVED = "密码已保存到文件";
    static final String STATUS_BATCH_GENERATED = "批量密码已生成";

    // 错误消息
    static final String ERROR_NO_CHAR_TYPE = "请至少选择一种字符类型";
    static final String ERROR_EMPTY_CHARSET = "可用字符集为空";
    static final String ERROR_COPY_FAILED = "复制到剪贴板失败";
    static final String ERROR_SAVE_FAILED = "保存文件失败: %s";

    // 文件对话框
    static final String FILE_CHOOSER_SAVE_TITLE = "保存密码";
    static final String FILE_CHOOSER_BATCH_TITLE = "保存批量密码";
    static final String FILE_FILTER_TEXT = "文本文件 (*.txt)";

    // 批量生成对话框
    static final String BATCH_DIALOG_TITLE = "批量生成密码";
    static final String BATCH_COUNT_LABEL = "生成数量:";
    static final String BATCH_GENERATE_BUTTON = "开始生成";

    // 工具提示
    static final String TOOLTIP_EXCLUDE_CHARS = "输入要排除的字符";

    // 帮助信息
    static final String HELP_MESSAGE = """
        密码生成器使用说明：

        • 设置密码长度（建议12位以上）
        • 选择要包含的字符类型（大写字母、小写字母、数字、特殊符号）
        • 可选择排除相似字符（如il1Lo0O）避免混淆
        • 可在排除字符框中输入不想出现的特定字符
        • 使用预设模板快速设置常用密码类型
        • 密码强度指示器会实时显示密码安全等级

        密码安全建议：
        • 长度至少12位，包含多种字符类型
        • 避免使用个人信息或常见单词
        • 为不同账户使用不同的密码
        • 定期更换重要账户的密码

        预设模板说明：
        • 简单：8位字母数字，适合一般用途
        • 复杂：16位全字符，适合重要账户
        • PIN码：6位数字，适合手机解锁等
        • 安全：20位全字符，最高安全等级

        快捷键：
        Ctrl+G - 重新生成密码
        Ctrl+C - 复制密码
        Ctrl+S - 保存密码
        Ctrl+B - 批量生成
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new PasswordGenerator().setVisible(true));
}

static class PasswordGenerator extends JFrame {
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
    private static final Dimension BUTTON_LARGE = new Dimension(160, 50);

    // ===== 应用状态 =====
    private JTextField passwordField;
    private JSpinner lengthSpinner;
    private JCheckBox uppercaseBox;
    private JCheckBox lowercaseBox;
    private JCheckBox numbersBox;
    private JCheckBox symbolsBox;
    private JCheckBox excludeSimilarBox;
    private JTextArea excludeCharsArea;
    private JProgressBar strengthBar;
    private JLabel strengthLabel;
    private SecureRandom random;

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String SIMILAR_CHARS = "il1Lo0O";

    public PasswordGenerator() {
        random = new SecureRandom();
        this.initializeGUI();
        this.generatePassword();
        this.setupKeyboardShortcuts();
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板
        var mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_8, SPACING_8, SPACING_8, SPACING_8);

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel(Texts.MAIN_TITLE, JLabel.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // 生成的密码显示 - 使用苹果风格文本框
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        var passwordLabel = new JLabel(Texts.PASSWORD_LABEL);
        passwordLabel.setFont(HEADLINE);
        passwordLabel.setForeground(LABEL);
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JTextField(20);
        passwordField.setFont(new Font("SF Mono", Font.PLAIN, 16));
        passwordField.setEditable(false);
        passwordField.setBackground(GRAY6);
        passwordField.setForeground(LABEL);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);

        // 密码长度 - 使用苹果风格标签和微调器
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        var lengthLabel = new JLabel(Texts.LENGTH_LABEL);
        lengthLabel.setFont(HEADLINE);
        lengthLabel.setForeground(LABEL);
        mainPanel.add(lengthLabel, gbc);

        lengthSpinner = new JSpinner(new SpinnerNumberModel(12, 4, 128, 1));
        lengthSpinner.setFont(BODY);
        lengthSpinner.addChangeListener((ev) -> this.generatePassword());
        gbc.gridx = 1;
        mainPanel.add(lengthSpinner, gbc);

        // 字符类型选择 - 使用苹果风格复选框
        gbc.gridx = 0; gbc.gridy = 3;
        var charTypeLabel = new JLabel(Texts.CHAR_TYPE_LABEL);
        charTypeLabel.setFont(HEADLINE);
        charTypeLabel.setForeground(LABEL);
        mainPanel.add(charTypeLabel, gbc);

        var checkBoxPanel = new JPanel(new GridLayout(2, 2, SPACING_8, SPACING_8));
        checkBoxPanel.setBackground(SYSTEM_BACKGROUND);

        uppercaseBox = new JCheckBox(Texts.UPPERCASE_LABEL, true);
        uppercaseBox.setFont(BODY);
        uppercaseBox.setForeground(LABEL);
        uppercaseBox.setBackground(SYSTEM_BACKGROUND);
        uppercaseBox.addActionListener((ev) -> this.generatePassword());

        lowercaseBox = new JCheckBox(Texts.LOWERCASE_LABEL, true);
        lowercaseBox.setFont(BODY);
        lowercaseBox.setForeground(LABEL);
        lowercaseBox.setBackground(SYSTEM_BACKGROUND);
        lowercaseBox.addActionListener((ev) -> this.generatePassword());

        numbersBox = new JCheckBox(Texts.NUMBERS_LABEL, true);
        numbersBox.setFont(BODY);
        numbersBox.setForeground(LABEL);
        numbersBox.setBackground(SYSTEM_BACKGROUND);
        numbersBox.addActionListener((ev) -> this.generatePassword());

        symbolsBox = new JCheckBox(Texts.SYMBOLS_LABEL, false);
        symbolsBox.setFont(BODY);
        symbolsBox.setForeground(LABEL);
        symbolsBox.setBackground(SYSTEM_BACKGROUND);
        symbolsBox.addActionListener((ev) -> this.generatePassword());

        checkBoxPanel.add(uppercaseBox);
        checkBoxPanel.add(lowercaseBox);
        checkBoxPanel.add(numbersBox);
        checkBoxPanel.add(symbolsBox);

        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(checkBoxPanel, gbc);

        // 高级选项
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        var advancedLabel = new JLabel(Texts.ADVANCED_LABEL);
        advancedLabel.setFont(HEADLINE);
        advancedLabel.setForeground(LABEL);
        mainPanel.add(advancedLabel, gbc);

        excludeSimilarBox = new JCheckBox(Texts.EXCLUDE_SIMILAR_LABEL, true);
        excludeSimilarBox.setFont(BODY);
        excludeSimilarBox.setForeground(LABEL);
        excludeSimilarBox.setBackground(SYSTEM_BACKGROUND);
        excludeSimilarBox.addActionListener((ev) -> this.generatePassword());
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(excludeSimilarBox, gbc);

        // 排除字符 - 使用苹果风格文本区域
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        var excludeLabel = new JLabel(Texts.EXCLUDE_CHARS_LABEL);
        excludeLabel.setFont(HEADLINE);
        excludeLabel.setForeground(LABEL);
        mainPanel.add(excludeLabel, gbc);

        excludeCharsArea = new JTextArea(2, 15);
        excludeCharsArea.setFont(CALLOUT);
        excludeCharsArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        excludeCharsArea.setToolTipText(Texts.TOOLTIP_EXCLUDE_CHARS);
        excludeCharsArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { generatePassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { generatePassword(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { generatePassword(); }
        });

        var excludeScrollPane = new JScrollPane(excludeCharsArea);
        excludeScrollPane.setPreferredSize(new Dimension(0, 60));
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(excludeScrollPane, gbc);

        // 密码强度 - 使用苹果风格进度条
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        var strengthLabelTitle = new JLabel(Texts.STRENGTH_LABEL);
        strengthLabelTitle.setFont(HEADLINE);
        strengthLabelTitle.setForeground(LABEL);
        mainPanel.add(strengthLabelTitle, gbc);

        strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setPreferredSize(new Dimension(200, 25));
        strengthBar.setFont(CAPTION1);
        strengthBar.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_2, SPACING_4, SPACING_2, SPACING_4)
        ));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(strengthBar, gbc);

        strengthLabel = new JLabel(Texts.STRENGTH_MEDIUM);
        strengthLabel.setFont(HEADLINE);
        strengthLabel.setForeground(ORANGE);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(strengthLabel, gbc);

        // 按钮面板 - 使用苹果风格按钮
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_12, 0));
        buttonPanel.setBackground(SYSTEM_BACKGROUND);

        var generateButton = this.createPrimaryButton("🎲 " + Texts.GENERATE_BUTTON);
        generateButton.addActionListener((ev) -> this.generatePassword());

        var copyButton = this.createSecondaryButton("📋 " + Texts.COPY_BUTTON);
        copyButton.addActionListener(this::copyPassword);

        var saveButton = this.createSecondaryButton("💾 " + Texts.SAVE_BUTTON);
        saveButton.addActionListener(this::savePassword);

        var batchButton = this.createSecondaryButton("📝 " + Texts.BATCH_BUTTON);
        batchButton.addActionListener(this::showBatchDialog);

        buttonPanel.add(generateButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(batchButton);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 预设模板面板 - 使用苹果风格
        this.createPresetPanel();

        setSize(500, 650);
        setLocationRelativeTo(null);
    }

    private void createPresetPanel() {
        var presetPanel = new JPanel();
        presetPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        presetPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder(Texts.PRESET_PANEL_TITLE)),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        presetPanel.setLayout(new GridLayout(2, 2, SPACING_12, SPACING_12));

        var simpleButton = this.createSecondaryButton(Texts.SIMPLE_PRESET);
        simpleButton.addActionListener((ev) -> this.applyPreset(8, true, true, false, false));

        var complexButton = this.createSecondaryButton(Texts.COMPLEX_PRESET);
        complexButton.addActionListener((ev) -> this.applyPreset(16, true, true, true, true));

        var pinButton = this.createSecondaryButton(Texts.PIN_PRESET);
        pinButton.addActionListener((ev) -> this.applyPreset(6, false, false, true, false));

        var secureButton = this.createSecondaryButton(Texts.SECURE_PRESET);
        secureButton.addActionListener((ev) -> this.applyPreset(20, true, true, true, true));

        presetPanel.add(simpleButton);
        presetPanel.add(complexButton);
        presetPanel.add(pinButton);
        presetPanel.add(secureButton);

        add(presetPanel, BorderLayout.SOUTH);
    }

    // ===== 按钮创建方法 =====
    private JButton createPrimaryButton(String text) {
        return this.createStyledButton(text, BLUE, WHITE);
    }

    private JButton createSecondaryButton(String text) {
        return this.createStyledButton(text, GRAY6, LABEL);
    }

    private JButton createSuccessButton(String text) {
        return this.createStyledButton(text, GREEN, WHITE);
    }

    private JButton createWarningButton(String text) {
        return this.createStyledButton(text, ORANGE, WHITE);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        var button = new JButton(text);
        button.setFont(HEADLINE);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));
        button.setPreferredSize(BUTTON_REGULAR);

        // 设置悬停效果
        this.setupButtonHoverEffect(button, backgroundColor);

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

    private javax.swing.border.Border createTitledBorder(String title) {
        var border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(HEADLINE);
        border.setTitleColor(SECONDARY_LABEL);
        return border;
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_G:
                        // G键重新生成密码
                        if (ev.isControlDown()) {
                            generatePassword();
                        }
                        break;
                    case KeyEvent.VK_C:
                        // C键复制密码
                        if (ev.isControlDown()) {
                            copyPassword(null);
                        }
                        break;
                    case KeyEvent.VK_S:
                        // S键保存密码
                        if (ev.isControlDown()) {
                            savePassword(null);
                        }
                        break;
                    case KeyEvent.VK_B:
                        // B键批量生成
                        if (ev.isControlDown()) {
                            showBatchDialog(null);
                        }
                        break;
                    case KeyEvent.VK_F1:
                        // F1键显示帮助
                        showHelp();
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

    private void showHelp() {
        JOptionPane.showMessageDialog(this, Texts.HELP_MESSAGE, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== 业务逻辑方法 =====
    private void applyPreset(int length, boolean upper, boolean lower, boolean numbers, boolean symbols) {
        lengthSpinner.setValue(length);
        uppercaseBox.setSelected(upper);
        lowercaseBox.setSelected(lower);
        numbersBox.setSelected(numbers);
        symbolsBox.setSelected(symbols);
        this.generatePassword();
    }

    private void generatePassword() {
        var length = (Integer) lengthSpinner.getValue();

        // 构建字符集
        var charSet = new StringBuilder();
        var requiredChars = new ArrayList<String>();

        if (uppercaseBox.isSelected()) {
            charSet.append(UPPERCASE);
            requiredChars.add(String.valueOf(UPPERCASE.charAt(random.nextInt(UPPERCASE.length()))));
        }
        if (lowercaseBox.isSelected()) {
            charSet.append(LOWERCASE);
            requiredChars.add(String.valueOf(LOWERCASE.charAt(random.nextInt(LOWERCASE.length()))));
        }
        if (numbersBox.isSelected()) {
            charSet.append(NUMBERS);
            requiredChars.add(String.valueOf(NUMBERS.charAt(random.nextInt(NUMBERS.length()))));
        }
        if (symbolsBox.isSelected()) {
            charSet.append(SYMBOLS);
            requiredChars.add(String.valueOf(SYMBOLS.charAt(random.nextInt(SYMBOLS.length()))));
        }

        if (charSet.length() == 0) {
            passwordField.setText(Texts.ERROR_NO_CHAR_TYPE);
            return;
        }

        // 排除相似字符
        if (excludeSimilarBox.isSelected()) {
            for (char c : SIMILAR_CHARS.toCharArray()) {
                int index;
                while ((index = charSet.indexOf(String.valueOf(c))) != -1) {
                    charSet.deleteCharAt(index);
                }
            }
        }

        // 排除用户指定字符
        var excludeChars = excludeCharsArea.getText();
        for (char c : excludeChars.toCharArray()) {
            int index;
            while ((index = charSet.indexOf(String.valueOf(c))) != -1) {
                charSet.deleteCharAt(index);
            }
        }

        if (charSet.length() == 0) {
            passwordField.setText(Texts.ERROR_EMPTY_CHARSET);
            return;
        }

        // 生成密码
        var password = new ArrayList<Character>();

        // 确保包含每种选中的字符类型
        for (var req : requiredChars) {
            if (password.size() < length) {
                password.add(req.charAt(0));
            }
        }

        // 填充剩余位置
        while (password.size() < length) {
            char c = charSet.charAt(random.nextInt(charSet.length()));
            password.add(c);
        }

        // 打乱顺序
        Collections.shuffle(password, random);

        // 转换为字符串
        var result = new StringBuilder();
        for (char c : password) {
            result.append(c);
        }

        passwordField.setText(result.toString());
        this.updatePasswordStrength(result.toString());
    }

    private void updatePasswordStrength(String password) {
        var score = this.calculatePasswordStrength(password);
        strengthBar.setValue(score);

        String strength;
        Color color;

        if (score < 30) {
            strength = Texts.STRENGTH_WEAK;
            color = RED;
        } else if (score < 60) {
            strength = Texts.STRENGTH_MEDIUM;
            color = ORANGE;
        } else if (score < 80) {
            strength = Texts.STRENGTH_STRONG;
            color = BLUE;
        } else {
            strength = Texts.STRENGTH_VERY_STRONG;
            color = GREEN;
        }

        strengthLabel.setText(strength);
        strengthLabel.setForeground(color);
        strengthBar.setForeground(color);
        strengthBar.setString(strength + " (" + score + "%)");
    }

    private int calculatePasswordStrength(String password) {
        int score = 0;

        // 长度评分
        if (password.length() >= 8) score += 20;
        if (password.length() >= 12) score += 10;
        if (password.length() >= 16) score += 10;

        // 字符类型评分
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSymbol = password.matches(".*[^A-Za-z0-9].*");

        if (hasUpper) score += 15;
        if (hasLower) score += 15;
        if (hasDigit) score += 15;
        if (hasSymbol) score += 15;

        // 复杂度评分
        if (hasUpper && hasLower && hasDigit) score += 10;
        if (hasUpper && hasLower && hasDigit && hasSymbol) score += 10;

        return Math.min(100, score);
    }

    private void copyPassword(ActionEvent e) {
        var password = passwordField.getText();
        if (!password.isEmpty() && !password.contains("请至少选择") && !password.contains("可用字符集")) {
            var selection = new StringSelection(password);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "密码已复制到剪贴板！", "复制成功",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void savePassword(ActionEvent e) {
        var password = passwordField.getText();
        if (password.isEmpty() || password.contains("请至少选择") || password.contains("可用字符集")) return;

        var fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("passwords.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                java.nio.file.Files.write(file.toPath(),
                    (password + "\n").getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND);
                JOptionPane.showMessageDialog(this, "密码已保存到文件！", "成功",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage(), "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showBatchDialog(ActionEvent e) {
        var dialog = new JDialog(this, "批量生成密码", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(SYSTEM_BACKGROUND);

        var inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, SPACING_8));
        inputPanel.setBackground(SYSTEM_BACKGROUND);
        inputPanel.add(new JLabel("生成数量:"));
        var countSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        countSpinner.setFont(BODY);
        inputPanel.add(countSpinner);

        var resultArea = new JTextArea(15, 40);
        resultArea.setFont(new Font("SF Mono", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(GRAY6);
        resultArea.setForeground(LABEL);
        resultArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        var scrollPane = new JScrollPane(resultArea);

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_12, SPACING_8));
        buttonPanel.setBackground(SYSTEM_BACKGROUND);

        var generateBatchButton = this.createPrimaryButton("生成");
        var copyAllButton = this.createSuccessButton("复制全部");
        var closeButton = this.createSecondaryButton("关闭");

        generateBatchButton.addActionListener((ev) -> {
            var count = (Integer) countSpinner.getValue();
            var sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                this.generatePassword();
                sb.append(passwordField.getText()).append("\n");
            }
            resultArea.setText(sb.toString());
        });

        copyAllButton.addActionListener((ev) -> {
            var selection = new StringSelection(resultArea.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(dialog, "所有密码已复制到剪贴板！", "成功",
                JOptionPane.INFORMATION_MESSAGE);
        });

        closeButton.addActionListener((ev) -> dialog.dispose());

        buttonPanel.add(generateBatchButton);
        buttonPanel.add(copyAllButton);
        buttonPanel.add(closeButton);

        dialog.add(inputPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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