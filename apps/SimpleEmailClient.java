import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new SimpleEmailClient().setVisible(true);
    });
}

static class SimpleEmailClient extends JFrame {
    private JTextField smtpField, portField, usernameField;
    private JPasswordField passwordField;
    private JTextField toField, subjectField;
    private JTextArea messageArea;
    private JButton sendButton, configButton;
    private JTextArea logArea;
    
    private String smtpServer = "";
    private String smtpPort = "587";
    private String username = "";
    private String password = "";
    
    public SimpleEmailClient() {
        setTitle("简易邮件客户端");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        
        initializeUI();
        showConfigDialog();
        setLocationRelativeTo(null);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 顶部配置面板
        var topPanel = new JPanel(new FlowLayout());
        configButton = new JButton("邮件配置");
        configButton.addActionListener(this::showConfigDialog);
        topPanel.add(configButton);
        
        // 邮件编写面板
        var composePanel = new JPanel(new BorderLayout());
        composePanel.setBorder(BorderFactory.createTitledBorder("编写邮件"));
        
        var fieldsPanel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("收件人:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        toField = new JTextField();
        fieldsPanel.add(toField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("主题:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        subjectField = new JTextField();
        fieldsPanel.add(subjectField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        fieldsPanel.add(new JLabel("内容:"), gbc);
        
        messageArea = new JTextArea(10, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        var messageScrollPane = new JScrollPane(messageArea);
        
        var buttonPanel = new JPanel(new FlowLayout());
        sendButton = new JButton("发送邮件");
        sendButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        sendButton.addActionListener(this::sendEmail);
        
        var clearButton = new JButton("清空");
        clearButton.addActionListener(e -> clearFields());
        
        var templateButton = new JButton("模板");
        templateButton.addActionListener(this::showTemplates);
        
        buttonPanel.add(sendButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(templateButton);
        
        composePanel.add(fieldsPanel, BorderLayout.NORTH);
        composePanel.add(messageScrollPane, BorderLayout.CENTER);
        composePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 日志面板
        var logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("发送日志"));
        
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        var logScrollPane = new JScrollPane(logArea);
        
        var logButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        var clearLogButton = new JButton("清空日志");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        logButtonPanel.add(clearLogButton);
        
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        logPanel.add(logButtonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(composePanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
        
        addLog("简易邮件客户端已启动");
        addLog("请先配置SMTP服务器信息");
    }
    
    private void showConfigDialog(ActionEvent e) {
        showConfigDialog();
    }
    
    private void showConfigDialog() {
        var dialog = new JDialog(this, "邮件服务器配置", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        var panel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("SMTP服务器:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        smtpField = new JTextField(smtpServer, 20);
        panel.add(smtpField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        portField = new JTextField(smtpPort, 20);
        panel.add(portField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        usernameField = new JTextField(username, 20);
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        passwordField = new JPasswordField(password, 20);
        panel.add(passwordField, gbc);
        
        // 预设配置
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        var presetPanel = new JPanel(new FlowLayout());
        presetPanel.setBorder(BorderFactory.createTitledBorder("常用配置"));
        
        var gmailButton = new JButton("Gmail");
        gmailButton.addActionListener(ev -> {
            smtpField.setText("smtp.gmail.com");
            portField.setText("587");
        });
        
        var qqButton = new JButton("QQ邮箱");
        qqButton.addActionListener(ev -> {
            smtpField.setText("smtp.qq.com");
            portField.setText("587");
        });
        
        var outlookButton = new JButton("Outlook");
        outlookButton.addActionListener(ev -> {
            smtpField.setText("smtp-mail.outlook.com");
            portField.setText("587");
        });
        
        presetPanel.add(gmailButton);
        presetPanel.add(qqButton);
        presetPanel.add(outlookButton);
        panel.add(presetPanel, gbc);
        
        // 按钮面板
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        var buttonPanel = new JPanel(new FlowLayout());
        
        var saveButton = new JButton("保存");
        saveButton.addActionListener(ev -> {
            smtpServer = smtpField.getText().trim();
            smtpPort = portField.getText().trim();
            username = usernameField.getText().trim();
            password = new String(passwordField.getPassword());
            
            if (smtpServer.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请填写完整的配置信息！");
                return;
            }
            
            addLog("SMTP配置已保存: " + smtpServer + ":" + smtpPort);
            dialog.dispose();
        });
        
        var cancelButton = new JButton("取消");
        cancelButton.addActionListener(ev -> dialog.dispose());
        
        var testButton = new JButton("测试连接");
        testButton.addActionListener(ev -> testConnection());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(testButton);
        panel.add(buttonPanel, gbc);
        
        // 说明文本
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        var infoLabel = new JLabel("<html><small>注意：某些邮件服务商需要使用应用专用密码而非登录密码</small></html>");
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void testConnection() {
        var testSmtp = smtpField.getText().trim();
        var testPort = portField.getText().trim();
        var testUser = usernameField.getText().trim();
        var testPass = new String(passwordField.getPassword());
        
        if (testSmtp.isEmpty() || testUser.isEmpty() || testPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整的配置信息！");
            return;
        }
        
        new Thread(() -> {
            try {
                // 模拟连接测试
                Thread.sleep(1000); // 模拟网络延迟

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "连接测试成功！(模拟模式)", "测试结果", JOptionPane.INFORMATION_MESSAGE);
                    addLog("SMTP连接测试成功 (模拟模式): " + testSmtp);
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "连接测试失败：\n" + ex.getMessage(), "测试结果", JOptionPane.ERROR_MESSAGE);
                    addLog("SMTP连接测试失败: " + ex.getMessage());
                });
            }
        }).start();
    }
    
    private void sendEmail(ActionEvent e) {
        if (smtpServer.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先配置SMTP服务器信息！");
            showConfigDialog();
            return;
        }
        
        var to = toField.getText().trim();
        var subject = subjectField.getText().trim();
        var message = messageArea.getText().trim();
        
        if (to.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入收件人邮箱地址！");
            return;
        }
        
        if (subject.isEmpty()) {
            subject = "(无主题)";
        }
        
        if (message.isEmpty()) {
            message = "(空内容)";
        }
        
        sendEmailAsync(to, subject, message);
    }
    
    private void sendEmailAsync(String to, String subject, String messageText) {
        sendButton.setEnabled(false);
        addLog("正在发送邮件到: " + to);
        
        new Thread(() -> {
            try {
                // 模拟邮件发送过程
                Thread.sleep(2000); // 模拟网络延迟

                SwingUtilities.invokeLater(() -> {
                    addLog("邮件发送成功！ (模拟模式)");
                    JOptionPane.showMessageDialog(SimpleEmailClient.this, "邮件发送成功！(模拟模式 - 实际功能需要JavaMail库)", "发送结果", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    addLog("邮件发送失败: " + ex.getMessage());
                    JOptionPane.showMessageDialog(this, "邮件发送失败：\n" + ex.getMessage(), "发送失败", JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    sendButton.setEnabled(true);
                });
            }
        }).start();
    }
    
    private void showTemplates(ActionEvent e) {
        var templates = new String[]{
            "工作汇报",
            "会议邀请", 
            "感谢信",
            "请假申请",
            "自定义"
        };
        
        var choice = (String) JOptionPane.showInputDialog(this,
            "选择邮件模板:", "邮件模板",
            JOptionPane.QUESTION_MESSAGE, null, templates, templates[0]);
        
        if (choice != null) {
            applyTemplate(choice);
        }
    }
    
    private void applyTemplate(String templateName) {
        switch (templateName) {
            case "工作汇报" -> {
                subjectField.setText("工作汇报 - " + java.time.LocalDate.now());
                messageArea.setText("尊敬的领导：\n\n" +
                    "以下是我今日的工作汇报：\n\n" +
                    "1. 完成的工作：\n" +
                    "   - \n\n" +
                    "2. 遇到的问题：\n" +
                    "   - \n\n" +
                    "3. 明日计划：\n" +
                    "   - \n\n" +
                    "谢谢！\n\n" +
                    "此致\n敬礼！");
            }
            case "会议邀请" -> {
                subjectField.setText("会议邀请 - " + java.time.LocalDate.now());
                messageArea.setText("您好：\n\n" +
                    "诚邀您参加以下会议：\n\n" +
                    "会议主题：\n" +
                    "会议时间：\n" +
                    "会议地点：\n" +
                    "会议议程：\n\n" +
                    "请确认是否能够参加，谢谢！\n\n" +
                    "最好的问候");
            }
            case "感谢信" -> {
                subjectField.setText("感谢信");
                messageArea.setText("亲爱的朋友：\n\n" +
                    "非常感谢您的帮助和支持！\n\n" +
                    "您的帮助对我来说意义重大，让我能够...\n\n" +
                    "再次表示诚挚的谢意！\n\n" +
                    "此致\n敬礼！");
            }
            case "请假申请" -> {
                subjectField.setText("请假申请");
                messageArea.setText("尊敬的领导：\n\n" +
                    "由于个人原因，需要请假，具体情况如下：\n\n" +
                    "请假时间：从 年 月 日 到 年 月 日\n" +
                    "请假原因：\n" +
                    "工作安排：\n\n" +
                    "请批准，谢谢！\n\n" +
                    "此致\n敬礼！");
            }
        }
    }
    
    private void clearFields() {
        toField.setText("");
        subjectField.setText("");
        messageArea.setText("");
    }
    
    private void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            var timestamp = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.append(String.format("[%s] %s%n", timestamp, message));
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
