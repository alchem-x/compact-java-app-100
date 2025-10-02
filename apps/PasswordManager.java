import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🔒 密码管理器";

    // 主界面标题
    static final String MAIN_TITLE = "🔒 安全密码管理器";

    // 按钮文本
    static final String ADD_BUTTON = "添加";
    static final String EDIT_BUTTON = "编辑";
    static final String DELETE_BUTTON = "删除";
    static final String SHOW_BUTTON = "显示密码";
    static final String GENERATE_BUTTON = "生成密码";
    static final String EXPORT_BUTTON = "导出";
    static final String CHANGE_PASSWORD_BUTTON = "更改主密码";
    static final String CONFIRM_BUTTON = "确认";
    static final String CANCEL_BUTTON = "取消";
    static final String GENERATE_PASSWORD_BUTTON = "生成";

    // 面板标题
    static final String ADD_EDIT_PANEL_TITLE = "添加/编辑密码";
    static final String PASSWORD_TABLE_TITLE = "保存的密码";
    static final String GENERATE_DIALOG_TITLE = "生成密码";

    // 标签文本
    static final String SITE_LABEL = "网站:";
    static final String USERNAME_LABEL = "用户名:";
    static final String PASSWORD_LABEL = "密码:";
    static final String LENGTH_LABEL = "长度:";
    static final String UPPERCASE_LABEL = "大写";
    static final String LOWERCASE_LABEL = "小写";
    static final String NUMBERS_LABEL = "数字";
    static final String SYMBOLS_LABEL = "符号";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_PASSWORD_ADDED = "密码已添加";
    static final String STATUS_PASSWORD_EDITED = "密码已编辑";
    static final String STATUS_PASSWORD_DELETED = "密码已删除";
    static final String STATUS_PASSWORD_GENERATED = "密码已生成";
    static final String STATUS_PASSWORD_EXPORTED = "密码已导出";
    static final String STATUS_MASTER_PASSWORD_CHANGED = "主密码已更改";

    // 错误消息
    static final String ERROR_EMPTY_SITE = "请输入网站名称！";
    static final String ERROR_EMPTY_USERNAME = "请输入用户名！";
    static final String ERROR_EMPTY_PASSWORD = "请输入密码！";
    static final String ERROR_INVALID_PASSWORD = "密码不能为空！";
    static final String ERROR_PASSWORD_MISMATCH = "两次密码不一致！";
    static final String ERROR_WRONG_PASSWORD = "密码错误！";
    static final String ERROR_NO_CHAR_TYPE = "请至少选择一种字符类型！";
    static final String ERROR_EXPORT_FAILED = "导出失败: ";

    // 确认对话框
    static final String CONFIRM_DELETE_TITLE = "确认删除";
    static final String CONFIRM_DELETE_MESSAGE = "确定要删除这个密码吗？";
    static final String CONFIRM_CHANGE_PASSWORD_TITLE = "确认更改主密码";

    // 输入对话框
    static final String AUTH_DIALOG_TITLE = "身份验证";
    static final String AUTH_DIALOG_MESSAGE = "请输入主密码:";
    static final String SET_MASTER_PASSWORD_TITLE = "设置主密码";
    static final String SET_MASTER_PASSWORD_MESSAGE = "确认主密码:";
    static final String ENTER_CURRENT_PASSWORD_TITLE = "请输入当前主密码:";
    static final String ENTER_NEW_PASSWORD_TITLE = "请输入新主密码:";
    static final String CONFIRM_NEW_PASSWORD_TITLE = "确认新主密码:";

    // 文件对话框
    static final String EXPORT_FILE_NAME = "passwords_export.txt";
    static final String EXPORT_DIALOG_TITLE = "密码导出 - ";
    static final String EXPORT_SUCCESS_MESSAGE = "导出成功！";

    // 表格列名
    static final String TABLE_COLUMN_SITE = "网站";
    static final String TABLE_COLUMN_USERNAME = "用户名";
    static final String TABLE_COLUMN_PASSWORD = "密码";
    static final String HIDDEN_PASSWORD_DISPLAY = "••••••••";

    // 帮助信息
    static final String HELP_MESSAGE = """
        密码管理器使用说明：

        • 首次使用需要设置主密码，用于加密保护所有存储的密码
        • 添加密码：输入网站、用户名和密码，点击"添加"按钮
        • 编辑密码：在表格中选择密码，修改信息后点击"编辑"按钮
        • 删除密码：在表格中选择密码，点击"删除"按钮
        • 显示密码：选择密码后点击"显示密码"按钮查看明文密码
        • 生成密码：点击"生成密码"按钮使用内置密码生成器
        • 导出密码：点击"导出"按钮将所有密码导出到文本文件
        • 更改主密码：点击"更改主密码"按钮修改主密码

        安全提示：
        • 主密码是访问所有存储密码的关键，请妥善保管
        • 建议定期更换主密码
        • 不要在公共计算机上使用密码管理器
        • 导出功能会生成明文文件，请妥善保管导出文件

        快捷键：
        Ctrl+A - 添加密码
        Ctrl+E - 编辑密码
        Ctrl+D - 删除密码
        Ctrl+S - 显示密码
        Ctrl+G - 生成密码
        Ctrl+X - 导出密码
        Ctrl+P - 更改主密码
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new PasswordManager().setVisible(true);
    });
}

static class PasswordManager extends JFrame {
    private final String DATA_FILE = "passwords.dat";
    private String masterPassword = "";
    private boolean isAuthenticated = false;
    
    private DefaultTableModel tableModel;
    private JTable passwordTable;
    private JTextField siteField, usernameField;
    private JPasswordField passwordField;
    private JButton addButton, editButton, deleteButton, showButton;
    
    public PasswordManager() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);

        if (authenticateUser()) {
            initializeUI();
            loadPasswords();
            setupKeyboardShortcuts();
        } else {
            System.exit(0);
        }

        setLocationRelativeTo(null);
    }
    
    private boolean authenticateUser() {
        while (true) {
            var password = JOptionPane.showInputDialog(this,
                Texts.AUTH_DIALOG_MESSAGE, Texts.AUTH_DIALOG_TITLE, JOptionPane.QUESTION_MESSAGE);
            
            if (password == null) {
                return false;
            }
            
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, Texts.ERROR_INVALID_PASSWORD);
                continue;
            }
            
            // 首次使用或验证密码
            if (!Files.exists(Paths.get(DATA_FILE))) {
                var confirm = JOptionPane.showInputDialog(this,
                    Texts.SET_MASTER_PASSWORD_MESSAGE, Texts.SET_MASTER_PASSWORD_TITLE, JOptionPane.QUESTION_MESSAGE);
                
                if (confirm != null && confirm.equals(password)) {
                    masterPassword = password;
                    isAuthenticated = true;
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, Texts.ERROR_PASSWORD_MISMATCH);
                }
            } else {
                masterPassword = password;
                if (verifyMasterPassword()) {
                    isAuthenticated = true;
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, Texts.ERROR_WRONG_PASSWORD);
                }
            }
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 顶部面板
        var topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder(Texts.ADD_EDIT_PANEL_TITLE));
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel(Texts.SITE_LABEL), gbc);
        gbc.gridx = 1;
        siteField = new JTextField(15);
        topPanel.add(siteField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel(Texts.USERNAME_LABEL), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        topPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel(Texts.PASSWORD_LABEL), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        topPanel.add(passwordField, gbc);
        
        // 按钮面板
        var buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton(Texts.ADD_BUTTON);
        editButton = new JButton(Texts.EDIT_BUTTON);
        deleteButton = new JButton(Texts.DELETE_BUTTON);
        showButton = new JButton(Texts.SHOW_BUTTON);

        var generateButton = new JButton(Texts.GENERATE_BUTTON);
        var exportButton = new JButton(Texts.EXPORT_BUTTON);
        var changePasswordButton = new JButton(Texts.CHANGE_PASSWORD_BUTTON);
        
        addButton.addActionListener(this::addPassword);
        editButton.addActionListener(this::editPassword);
        deleteButton.addActionListener(this::deletePassword);
        showButton.addActionListener(this::showPassword);
        generateButton.addActionListener(this::generatePassword);
        exportButton.addActionListener(this::exportPasswords);
        changePasswordButton.addActionListener(this::changeMasterPassword);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(showButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(changePasswordButton);
        
        // 表格
        String[] columns = {Texts.TABLE_COLUMN_SITE, Texts.TABLE_COLUMN_USERNAME, Texts.TABLE_COLUMN_PASSWORD};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        passwordTable = new JTable(tableModel);
        passwordTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
                loadSelectedRow();
            }
        });
        
        // 隐藏密码列
        passwordTable.getColumnModel().getColumn(2).setCellRenderer(
            (table, value, isSelected, hasFocus, row, column) -> {
                var label = new JLabel(Texts.HIDDEN_PASSWORD_DISPLAY);
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setOpaque(true);
                }
                return label;
            }
        );

        var scrollPane = new JScrollPane(passwordTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(Texts.PASSWORD_TABLE_TITLE));
        
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        boolean hasSelection = passwordTable.getSelectedRow() != -1;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        showButton.setEnabled(hasSelection);
    }
    
    private void loadSelectedRow() {
        int row = passwordTable.getSelectedRow();
        if (row != -1) {
            siteField.setText((String) tableModel.getValueAt(row, 0));
            usernameField.setText((String) tableModel.getValueAt(row, 1));
            passwordField.setText((String) tableModel.getValueAt(row, 2));
        }
    }
    
    private void addPassword(ActionEvent e) {
        if (validateInput()) {
            tableModel.addRow(new Object[]{
                siteField.getText(),
                usernameField.getText(),
                new String(passwordField.getPassword())
            });
            clearFields();
            savePasswords();
        }
    }
    
    private void editPassword(ActionEvent e) {
        int row = passwordTable.getSelectedRow();
        if (row != -1 && validateInput()) {
            tableModel.setValueAt(siteField.getText(), row, 0);
            tableModel.setValueAt(usernameField.getText(), row, 1);
            tableModel.setValueAt(new String(passwordField.getPassword()), row, 2);
            clearFields();
            savePasswords();
        }
    }
    
    private void deletePassword(ActionEvent e) {
        int row = passwordTable.getSelectedRow();
        if (row != -1) {
            int result = JOptionPane.showConfirmDialog(this,
                Texts.CONFIRM_DELETE_MESSAGE, Texts.CONFIRM_DELETE_TITLE, JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                tableModel.removeRow(row);
                clearFields();
                savePasswords();
            }
        }
    }
    
    private void showPassword(ActionEvent e) {
        int row = passwordTable.getSelectedRow();
        if (row != -1) {
            String password = (String) tableModel.getValueAt(row, 2);
            JOptionPane.showMessageDialog(this,
                Texts.PASSWORD_LABEL + " " + password, Texts.SHOW_BUTTON, JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void generatePassword(ActionEvent e) {
        var dialog = new PasswordGeneratorDialog(this);
        dialog.setVisible(true);
        
        if (dialog.getGeneratedPassword() != null) {
            passwordField.setText(dialog.getGeneratedPassword());
        }
    }
    
    private void exportPasswords(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(Texts.EXPORT_FILE_NAME));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (var writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.println(Texts.EXPORT_DIALOG_TITLE + new java.util.Date());
                writer.println("=" .repeat(50));

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    writer.printf(Texts.TABLE_COLUMN_SITE + ": %s%n", tableModel.getValueAt(i, 0));
                    writer.printf(Texts.TABLE_COLUMN_USERNAME + ": %s%n", tableModel.getValueAt(i, 1));
                    writer.printf(Texts.TABLE_COLUMN_PASSWORD + ": %s%n", tableModel.getValueAt(i, 2));
                    writer.println("-".repeat(30));
                }

                JOptionPane.showMessageDialog(this, Texts.EXPORT_SUCCESS_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, Texts.ERROR_EXPORT_FAILED + ex.getMessage());
            }
        }
    }
    
    private void changeMasterPassword(ActionEvent e) {
        var oldPassword = JOptionPane.showInputDialog(this, Texts.ENTER_CURRENT_PASSWORD_TITLE);
        if (oldPassword != null && oldPassword.equals(masterPassword)) {
            var newPassword = JOptionPane.showInputDialog(this, Texts.ENTER_NEW_PASSWORD_TITLE);
            if (newPassword != null && !newPassword.isEmpty()) {
                var confirm = JOptionPane.showInputDialog(this, Texts.CONFIRM_NEW_PASSWORD_TITLE);
                if (confirm != null && confirm.equals(newPassword)) {
                    masterPassword = newPassword;
                    savePasswords();
                    JOptionPane.showMessageDialog(this, Texts.STATUS_MASTER_PASSWORD_CHANGED);
                } else {
                    JOptionPane.showMessageDialog(this, Texts.ERROR_PASSWORD_MISMATCH);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, Texts.ERROR_WRONG_PASSWORD);
        }
    }
    
    private boolean validateInput() {
        if (siteField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, Texts.ERROR_EMPTY_SITE);
            return false;
        }
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, Texts.ERROR_EMPTY_USERNAME);
            return false;
        }
        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, Texts.ERROR_EMPTY_PASSWORD);
            return false;
        }
        return true;
    }
    
    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_A:
                        // A键添加密码
                        if (ev.isControlDown()) {
                            addPassword(null);
                        }
                        break;
                    case KeyEvent.VK_E:
                        // E键编辑密码
                        if (ev.isControlDown()) {
                            editPassword(null);
                        }
                        break;
                    case KeyEvent.VK_D:
                        // D键删除密码
                        if (ev.isControlDown()) {
                            deletePassword(null);
                        }
                        break;
                    case KeyEvent.VK_S:
                        // S键显示密码
                        if (ev.isControlDown()) {
                            showPassword(null);
                        }
                        break;
                    case KeyEvent.VK_G:
                        // G键生成密码
                        if (ev.isControlDown()) {
                            generatePassword(null);
                        }
                        break;
                    case KeyEvent.VK_X:
                        // X键导出密码
                        if (ev.isControlDown()) {
                            exportPasswords(null);
                        }
                        break;
                    case KeyEvent.VK_P:
                        // P键更改主密码
                        if (ev.isControlDown()) {
                            changeMasterPassword(null);
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

    private void clearFields() {
        siteField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }
    
    private void savePasswords() {
        try {
            var data = new ArrayList<String>();
            data.add(hashPassword(masterPassword)); // 保存主密码哈希
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                var site = (String) tableModel.getValueAt(i, 0);
                var username = (String) tableModel.getValueAt(i, 1);
                var password = (String) tableModel.getValueAt(i, 2);
                
                data.add(encrypt(site + "|" + username + "|" + password));
            }
            
            Files.write(Paths.get(DATA_FILE), data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage());
        }
    }
    
    private void loadPasswords() {
        try {
            if (!Files.exists(Paths.get(DATA_FILE))) {
                return;
            }
            
            var lines = Files.readAllLines(Paths.get(DATA_FILE));
            if (lines.isEmpty()) return;
            
            // 跳过第一行（主密码哈希）
            for (int i = 1; i < lines.size(); i++) {
                var decrypted = decrypt(lines.get(i));
                var parts = decrypted.split("\\|");
                
                if (parts.length == 3) {
                    tableModel.addRow(new Object[]{parts[0], parts[1], parts[2]});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载失败: " + e.getMessage());
        }
    }
    
    private boolean verifyMasterPassword() {
        try {
            if (!Files.exists(Paths.get(DATA_FILE))) {
                return true;
            }
            
            var lines = Files.readAllLines(Paths.get(DATA_FILE));
            if (lines.isEmpty()) return true;
            
            return hashPassword(masterPassword).equals(lines.get(0));
        } catch (Exception e) {
            return false;
        }
    }
    
    private String hashPassword(String password) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return password;
        }
    }
    
    private String encrypt(String data) {
        try {
            var key = new SecretKeySpec(masterPassword.getBytes(), 0, 16, "AES");
            var cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            return data;
        }
    }
    
    private String decrypt(String encryptedData) {
        try {
            var key = new SecretKeySpec(masterPassword.getBytes(), 0, 16, "AES");
            var cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
        } catch (Exception e) {
            return encryptedData;
        }
    }
    
    private static class PasswordGeneratorDialog extends JDialog {
        private String generatedPassword;
        private JSpinner lengthSpinner;
        private JCheckBox uppercaseBox, lowercaseBox, numbersBox, symbolsBox;
        
        public PasswordGeneratorDialog(JFrame parent) {
            super(parent, Texts.GENERATE_DIALOG_TITLE, true);
            setSize(300, 250);
            setLocationRelativeTo(parent);
            
            initializeUI();
        }
        
        private void initializeUI() {
            setLayout(new BorderLayout());
            
            var panel = new JPanel(new GridBagLayout());
            var gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel(Texts.LENGTH_LABEL), gbc);
            gbc.gridx = 1;
            lengthSpinner = new JSpinner(new SpinnerNumberModel(12, 4, 50, 1));
            panel.add(lengthSpinner, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
            uppercaseBox = new JCheckBox(Texts.UPPERCASE_LABEL, true);
            panel.add(uppercaseBox, gbc);

            gbc.gridy = 2;
            lowercaseBox = new JCheckBox(Texts.LOWERCASE_LABEL, true);
            panel.add(lowercaseBox, gbc);

            gbc.gridy = 3;
            numbersBox = new JCheckBox(Texts.NUMBERS_LABEL, true);
            panel.add(numbersBox, gbc);

            gbc.gridy = 4;
            symbolsBox = new JCheckBox(Texts.SYMBOLS_LABEL, false);
            panel.add(symbolsBox, gbc);
            
            var buttonPanel = new JPanel();
            var generateBtn = new JButton(Texts.GENERATE_PASSWORD_BUTTON);
            var cancelBtn = new JButton(Texts.CANCEL_BUTTON);
            
            generateBtn.addActionListener(e -> {
                generatePassword();
                dispose();
            });
            
            cancelBtn.addActionListener(e -> dispose());
            
            buttonPanel.add(generateBtn);
            buttonPanel.add(cancelBtn);
            
            add(panel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void generatePassword() {
            var chars = new StringBuilder();
            if (uppercaseBox.isSelected()) chars.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            if (lowercaseBox.isSelected()) chars.append("abcdefghijklmnopqrstuvwxyz");
            if (numbersBox.isSelected()) chars.append("0123456789");
            if (symbolsBox.isSelected()) chars.append("!@#$%^&*()_+-=[]{}|;:,.<>?");
            
            if (chars.length() == 0) {
                JOptionPane.showMessageDialog(this, Texts.ERROR_NO_CHAR_TYPE);
                return;
            }
            
            var random = new java.util.Random();
            var password = new StringBuilder();
            int length = (Integer) lengthSpinner.getValue();
            
            for (int i = 0; i < length; i++) {
                password.append(chars.charAt(random.nextInt(chars.length())));
            }
            
            generatedPassword = password.toString();
        }
        
        public String getGeneratedPassword() {
            return generatedPassword;
        }
    }
}
