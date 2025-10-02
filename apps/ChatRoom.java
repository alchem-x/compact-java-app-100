import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * 聊天室应用
 * 模拟多用户聊天环境，支持消息发送、用户列表和简单的机器人回复
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new ChatRoom().setVisible(true);
    });
}

class ChatRoom extends JFrame {
    private List<ChatMessage> messages = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private User currentUser;
    
    private JTextArea chatArea;
    private JTextField messageField;
    private JList<User> userList;
    private DefaultListModel<User> userListModel;
    private JLabel statusLabel;
    
    private javax.swing.Timer botTimer;
    private Random random = new Random();
    
    public ChatRoom() {
        initializeUsers();
        initializeUI();
        startBotTimer();
        addWelcomeMessage();
    }
    
    private void initializeUsers() {
        currentUser = new User("我", Color.BLUE, true);
        users.add(currentUser);
        users.add(new User("小助手", Color.GREEN, false));
        users.add(new User("访客1", Color.RED, false));
        users.add(new User("访客2", Color.ORANGE, false));
    }
    
    private void initializeUI() {
        setTitle("聊天室 - Chat Room");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 创建菜单栏
        createMenuBar();
        
        // 主面板
        var mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        
        // 中央聊天面板
        var chatPanel = createChatPanel();
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        
        // 右侧用户列表
        var userPanel = createUserPanel();
        mainPanel.add(userPanel, BorderLayout.EAST);
        
        // 底部输入面板
        var inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        // 状态栏
        createStatusBar();
        mainPanel.add(statusLabel, BorderLayout.NORTH);
    }
    
    private void createMenuBar() {
        var menuBar = new JMenuBar();
        
        var chatMenu = new JMenu("聊天");
        addMenuItem(chatMenu, "更改昵称", e -> changeNickname());
        addMenuItem(chatMenu, "更改颜色", e -> changeColor());
        addMenuItem(chatMenu, "清空聊天", e -> clearChat());
        chatMenu.addSeparator();
        addMenuItem(chatMenu, "保存聊天记录", e -> saveChatLog());
        
        var settingsMenu = new JMenu("设置");
        var autoScrollItem = new JCheckBoxMenuItem("自动滚动", true);
        var timestampItem = new JCheckBoxMenuItem("显示时间戳", true);
        var soundItem = new JCheckBoxMenuItem("消息提示音", true);
        
        settingsMenu.add(autoScrollItem);
        settingsMenu.add(timestampItem);
        settingsMenu.add(soundItem);
        
        var helpMenu = new JMenu("帮助");
        addMenuItem(helpMenu, "聊天命令", e -> showCommands());
        addMenuItem(helpMenu, "关于", e -> showAbout());
        
        menuBar.add(chatMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void addMenuItem(JMenu menu, String text, ActionListener action) {
        var item = new JMenuItem(text);
        item.addActionListener(action);
        menu.add(item);
    }
    
    private JPanel createChatPanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("聊天区域"));
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        var scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createUserPanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(150, 400));
        panel.setBorder(BorderFactory.createTitledBorder("在线用户"));
        
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setCellRenderer(new UserListCellRenderer());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 双击私聊
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var selectedUser = userList.getSelectedValue();
                    if (selectedUser != null && selectedUser != currentUser) {
                        startPrivateChat(selectedUser);
                    }
                }
            }
        });
        
        updateUserList();
        
        var scrollPane = new JScrollPane(userList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInputPanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        messageField = new JTextField();
        messageField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        messageField.addActionListener(e -> sendMessage());
        
        var sendButton = new JButton("发送");
        sendButton.addActionListener(e -> sendMessage());
        
        var emojiButton = new JButton("😊");
        emojiButton.addActionListener(e -> showEmojiPanel());
        
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(emojiButton, BorderLayout.WEST);
        panel.add(sendButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private void createStatusBar() {
        statusLabel = new JLabel("欢迎来到聊天室！在线用户: " + users.size());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    
    private void startBotTimer() {
        // 机器人定期发送消息
        botTimer = new javax.swing.Timer(30000 + random.nextInt(30000), e -> {
            if (random.nextBoolean()) {
                sendBotMessage();
            }
        });
        botTimer.start();
    }
    
    private void addWelcomeMessage() {
        var welcomeMsg = new ChatMessage(
            new User("系统", Color.GRAY, false),
            "欢迎来到聊天室！输入 /help 查看可用命令。",
            LocalDateTime.now(),
            MessageType.SYSTEM
        );
        addMessage(welcomeMsg);
    }
    
    private void sendMessage() {
        String text = messageField.getText().trim();
        if (text.isEmpty()) return;
        
        messageField.setText("");
        
        // 处理命令
        if (text.startsWith("/")) {
            handleCommand(text);
            return;
        }
        
        // 发送普通消息
        var message = new ChatMessage(currentUser, text, LocalDateTime.now(), MessageType.USER);
        addMessage(message);
        
        // 触发机器人回复
        if (random.nextInt(5) == 0) {
            javax.swing.Timer timer = new javax.swing.Timer(1000 + random.nextInt(2000), e -> sendBotReply(text));
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void handleCommand(String command) {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case "/help" -> showCommandHelp();
            case "/clear" -> clearChat();
            case "/nick" -> {
                if (parts.length > 1) {
                    changeNickname(parts[1]);
                } else {
                    addSystemMessage("用法: /nick 新昵称");
                }
            }
            case "/color" -> changeColor();
            case "/time" -> addSystemMessage("当前时间: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            case "/users" -> showUserList();
            case "/roll" -> {
                int dice = 1 + random.nextInt(6);
                addSystemMessage(currentUser.name + " 掷骰子得到: " + dice);
            }
            default -> addSystemMessage("未知命令: " + cmd + "，输入 /help 查看帮助");
        }
    }
    
    private void sendBotReply(String originalMessage) {
        var botUser = users.stream()
            .filter(u -> u.name.equals("小助手"))
            .findFirst()
            .orElse(null);
        
        if (botUser == null) return;
        
        String[] replies = {
            "有趣的观点！",
            "我同意你的看法。",
            "说得很好！",
            "这让我想到了...",
            "确实如此。",
            "你觉得呢？",
            "这个话题很有意思。"
        };
        
        String reply = replies[random.nextInt(replies.length)];
        var message = new ChatMessage(botUser, reply, LocalDateTime.now(), MessageType.BOT);
        addMessage(message);
    }
    
    private void sendBotMessage() {
        var botUsers = users.stream()
            .filter(u -> !u.isCurrentUser && !u.name.equals("系统"))
            .toList();
        
        if (botUsers.isEmpty()) return;
        
        var botUser = botUsers.get(random.nextInt(botUsers.size()));
        
        String[] messages = {
            "大家好！",
            "今天天气不错呢。",
            "有人在吗？",
            "聊天室好安静啊。",
            "最近有什么有趣的事情吗？",
            "时间过得真快。"
        };
        
        String text = messages[random.nextInt(messages.length)];
        var message = new ChatMessage(botUser, text, LocalDateTime.now(), MessageType.BOT);
        addMessage(message);
    }
    
    private void addMessage(ChatMessage message) {
        messages.add(message);
        updateChatDisplay();
        
        // 播放提示音
        if (message.type != MessageType.SYSTEM) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private void addSystemMessage(String text) {
        var systemUser = new User("系统", Color.GRAY, false);
        var message = new ChatMessage(systemUser, text, LocalDateTime.now(), MessageType.SYSTEM);
        addMessage(message);
    }
    
    private void updateChatDisplay() {
        var sb = new StringBuilder();
        
        for (var message : messages) {
            String timestamp = message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String prefix = switch (message.type) {
                case SYSTEM -> "[系统] ";
                case PRIVATE -> "[私聊] ";
                default -> "";
            };
            
            sb.append(String.format("[%s] %s%s: %s\n", 
                timestamp, prefix, message.user.name, message.text));
        }
        
        chatArea.setText(sb.toString());
        
        // 自动滚动到底部
        SwingUtilities.invokeLater(() -> {
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    private void updateUserList() {
        userListModel.clear();
        users.forEach(userListModel::addElement);
        if (statusLabel != null) {
            statusLabel.setText("在线用户: " + users.size());
        }
    }
    
    private void changeNickname() {
        String newName = JOptionPane.showInputDialog(this, 
            "请输入新昵称:", "更改昵称", JOptionPane.QUESTION_MESSAGE);
        
        if (newName != null && !newName.trim().isEmpty()) {
            changeNickname(newName.trim());
        }
    }
    
    private void changeNickname(String newName) {
        String oldName = currentUser.name;
        currentUser.name = newName;
        updateUserList();
        addSystemMessage(oldName + " 更改昵称为 " + newName);
    }
    
    private void changeColor() {
        var color = JColorChooser.showDialog(this, "选择用户颜色", currentUser.color);
        if (color != null) {
            currentUser.color = color;
            updateUserList();
            addSystemMessage(currentUser.name + " 更改了颜色");
        }
    }
    
    private void clearChat() {
        int result = JOptionPane.showConfirmDialog(this,
            "确定要清空聊天记录吗？", "清空聊天", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            messages.clear();
            chatArea.setText("");
            addWelcomeMessage();
        }
    }
    
    private void saveChatLog() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "文本文件", "txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".txt")) {
                    file = new java.io.File(file.getAbsolutePath() + ".txt");
                }
                
                java.nio.file.Files.writeString(file.toPath(), chatArea.getText());
                addSystemMessage("聊天记录已保存到: " + file.getName());
                
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage());
            }
        }
    }
    
    private void startPrivateChat(User user) {
        String message = JOptionPane.showInputDialog(this,
            "发送私聊消息给 " + user.name + ":", "私聊", JOptionPane.QUESTION_MESSAGE);
        
        if (message != null && !message.trim().isEmpty()) {
            var privateMsg = new ChatMessage(currentUser, 
                "对 " + user.name + " 说: " + message.trim(), 
                LocalDateTime.now(), MessageType.PRIVATE);
            addMessage(privateMsg);
        }
    }
    
    private void showEmojiPanel() {
        String[] emojis = {"😊", "😂", "😍", "😢", "😡", "👍", "👎", "❤️", "🎉", "🔥"};
        
        var emojiPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        for (String emoji : emojis) {
            var button = new JButton(emoji);
            button.addActionListener(e -> {
                messageField.setText(messageField.getText() + emoji);
                SwingUtilities.getWindowAncestor(emojiPanel).dispose();
            });
            emojiPanel.add(button);
        }
        
        JOptionPane.showMessageDialog(this, emojiPanel, "选择表情", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void showCommandHelp() {
        String help = """
            可用命令:
            /help - 显示此帮助
            /clear - 清空聊天记录
            /nick 昵称 - 更改昵称
            /color - 更改用户颜色
            /time - 显示当前时间
            /users - 显示在线用户
            /roll - 掷骰子
            
            其他功能:
            - 双击用户名可发送私聊
            - 点击表情按钮添加表情
            """;
        
        JOptionPane.showMessageDialog(this, help, "聊天命令", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showCommands() {
        showCommandHelp();
    }
    
    private void showUserList() {
        var sb = new StringBuilder("在线用户列表:\n");
        for (int i = 0; i < users.size(); i++) {
            var user = users.get(i);
            sb.append((i + 1)).append(". ").append(user.name);
            if (user.isCurrentUser) {
                sb.append(" (我)");
            }
            sb.append("\n");
        }
        addSystemMessage(sb.toString());
    }
    
    private void showAbout() {
        String about = """
            聊天室 v1.0
            
            功能特性:
            • 实时消息发送
            • 用户列表显示
            • 私聊功能
            • 表情支持
            • 聊天命令
            • 机器人互动
            • 聊天记录保存
            
            使用说明:
            在输入框中输入消息并按回车发送
            输入 /help 查看所有可用命令
            双击用户名可发送私聊消息
            """;
        
        JOptionPane.showMessageDialog(this, about, "关于聊天室", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // 用户类
    static class User {
        String name;
        Color color;
        boolean isCurrentUser;
        
        User(String name, Color color, boolean isCurrentUser) {
            this.name = name;
            this.color = color;
            this.isCurrentUser = isCurrentUser;
        }
        
        @Override
        public String toString() {
            return name + (isCurrentUser ? " (我)" : "");
        }
    }
    
    // 聊天消息类
    record ChatMessage(User user, String text, LocalDateTime timestamp, MessageType type) {}
    
    // 消息类型枚举
    enum MessageType {
        USER, BOT, SYSTEM, PRIVATE
    }
    
    // 用户列表渲染器
    class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof User user) {
                setText(user.toString());
                if (!isSelected) {
                    setForeground(user.color);
                }
                if (user.isCurrentUser) {
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            }
            
            return this;
        }
    }
}
