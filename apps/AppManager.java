import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new AppManager().setVisible(true);
    });
}

static class AppManager extends JFrame {
    private JList<AppInfo> appList;
    private DefaultListModel<AppInfo> listModel;
    private JTextArea descriptionArea;
    private JButton launchButton;
    private JTextField searchField;
    private Map<String, AppInfo> apps;
    
    // 使用record记录类 - 简单数据载体
    record AppInfo(String name, String fileName, String category, String description, String emoji) {
        @Override
        public String toString() {
            return emoji + " " + name + " (" + category + ")";
        }
    }
    
    public AppManager() {
        this.initializeApps();
        this.initializeGUI();
        this.loadAppList();
    }
    
    private void initializeApps() {
        apps = new HashMap<>();
        
        // 基础工具类 (35个)
        this.addApp("计算器", "Calculator.java", "基础工具", "苹果风格的计算器，支持基本运算功能", "🧮");
        this.addApp("科学计算器", "ScientificCalculator.java", "基础工具", "支持三角函数、对数、幂运算的科学计算器", "🔬");
        this.addApp("单位转换器", "UnitConverter.java", "基础工具", "长度、重量、温度、货币等单位转换工具", "📏");
        this.addApp("BMI计算器", "BMICalculator.java", "基础工具", "计算身体质量指数并提供健康建议", "⚖️");
        this.addApp("表达式计算器", "ExpressionCalculator.java", "基础工具", "支持括号和四则运算的表达式计算器", "🧮");
        this.addApp("数字时钟", "DigitalClock.java", "基础工具", "多时区显示和闹钟功能的数字时钟", "🕐");
        this.addApp("秒表计时器", "Stopwatch.java", "基础工具", "精确计时和分段记录的秒表", "⏱️");
        this.addApp("倒计时器", "CountdownTimer.java", "基础工具", "自定义倒计时和提醒功能", "⏰");
        this.addApp("日历应用", "CalendarApp.java", "基础工具", "月视图日历和事件标记功能", "📅");
        this.addApp("世界时钟", "WorldClock.java", "基础工具", "显示全球主要城市的当前时间", "🌍");
        this.addApp("文本编辑器", "TextEditor.java", "基础工具", "支持基础编辑和查找替换的文本编辑器", "📝");
        this.addApp("字符统计器", "TextCounter.java", "基础工具", "统计字数、行数、字符频率", "🔢");
        this.addApp("文本比较器", "TextComparator.java", "基础工具", "文本差异对比分析工具", "🔄");
        this.addApp("密码生成器", "PasswordGenerator.java", "基础工具", "自定义规则生成安全密码", "🔐");
        this.addApp("二维码生成器", "QRCodeGenerator.java", "基础工具", "文本转二维码生成工具", "📱");
        this.addApp("Base64编解码器", "Base64Tool.java", "基础工具", "Base64编码解码工具", "🔠");
        this.addApp("URL编解码器", "UrlEncoder.java", "基础工具", "URL编码解码工具", "🌐");
        this.addApp("正则表达式测试器", "RegexTester.java", "基础工具", "正则表达式匹配测试和验证工具", "🔍");
        this.addApp("进制转换器", "NumberBaseConverter.java", "基础工具", "十进制、二进制、八进制、十六进制转换", "🔢");
        this.addApp("ASCII码表", "AsciiTable.java", "基础工具", "ASCII码表查看和字符转换工具", "📊");
        this.addApp("文件管理器", "FileManager.java", "基础工具", "现代化文件浏览和管理工具", "📁");
        this.addApp("系统监视器", "SystemMonitor.java", "基础工具", "实时监控CPU、内存、磁盘使用情况", "📊");
        this.addApp("网络工具", "NetworkTools.java", "基础工具", "Ping、端口扫描、IP查询等网络工具", "🌐");
        this.addApp("代码编辑器", "CodeEditor.java", "基础工具", "支持语法高亮的代码编辑器", "💻");
        this.addApp("数据库浏览器", "DatabaseBrowser.java", "基础工具", "SQLite数据库浏览和查询工具", "🗄");
        this.addApp("JSON格式化器", "JsonFormatter.java", "基础工具", "JSON格式化、压缩和验证工具", "📦");
        this.addApp("哈希计算器", "HashTool.java", "基础工具", "MD5/SHA文本哈希计算工具", "🔒");
        this.addApp("简易加密工具", "SimpleEncryptor.java", "基础工具", "凯撒密码、替换密码、异或加密工具", "🔐");
        this.addApp("IP地址工具", "IPAddressTool.java", "基础工具", "IP地址分析、子网计算工具", "🌐");
        this.addApp("随机生成器", "RandomGenerator.java", "基础工具", "随机数字、字符串、UUID生成工具", "🎲");
        this.addApp("温度转换器", "TemperatureConverter.java", "基础工具", "摄氏度、华氏度、开尔文等温度转换", "🌡️");
        this.addApp("工作日计算器", "WorkdayCalculator.java", "基础工具", "计算两日期间工作日数量", "📅");
        this.addApp("贷款计算器", "LoanCalculator.java", "基础工具", "计算月供、利息、还款计划的贷款工具", "🏦");
        this.addApp("税务计算器", "TaxCalculator.java", "基础工具", "个人所得税计算工具", "💰");
        this.addApp("汇率转换器", "CurrencyConverter.java", "基础工具", "实时汇率查询和货币转换", "💱");
        
        // 游戏娱乐类 (20个)
        this.addApp("俄罗斯方块", "Tetris.java", "游戏娱乐", "经典的俄罗斯方块游戏，支持旋转、消行、计分等功能", "🧩");
        this.addApp("贪吃蛇", "Snake.java", "游戏娱乐", "经典的贪吃蛇游戏", "🐍");
        this.addApp("井字棋", "TicTacToe.java", "游戏娱乐", "3x3棋盘对战游戏", "⭕");
        this.addApp("扫雷游戏", "Minesweeper.java", "游戏娱乐", "数字逻辑推理的扫雷游戏", "💣");
        this.addApp("数独游戏", "Sudoku.java", "游戏娱乐", "经典的9x9数独益智游戏", "🔢");
        this.addApp("打砖块", "Breakout.java", "游戏娱乐", "经典的打砖块游戏", "🧩");
        this.addApp("飞机大战", "SpaceShooter.java", "游戏娱乐", "太空射击游戏", "🚀");
        this.addApp("记忆翻牌", "MemoryCard.java", "游戏娱乐", "记忆力训练翻牌游戏", "🃏");
        this.addApp("猜数字", "NumberGuess.java", "游戏娱乐", "数字猜测益智游戏", "🎲");
        this.addApp("2048游戏", "Game2048.java", "游戏娱乐", "数字合并益智游戏", "🔢");
        this.addApp("五子棋", "Gomoku.java", "游戏娱乐", "15x15棋盘策略游戏", "⚫");
        this.addApp("中国象棋", "ChineseChess.java", "游戏娱乐", "传统中国象棋游戏", "♟️");
        this.addApp("跳棋", "PegSolitaire.java", "游戏娱乐", "单人跳棋益智游戏", "🔴");
        this.addApp("推箱子", "Sokoban.java", "游戏娱乐", "经典推箱子益智游戏", "📦");
        this.addApp("纸牌接龙", "Solitaire.java", "游戏娱乐", "经典纸牌接龙游戏", "🃏");
        this.addApp("21点", "Blackjack.java", "游戏娱乐", "21点纸牌游戏", "🎰");
        this.addApp("斗地主", "FightLandlord.java", "游戏娱乐", "三人斗地主纸牌游戏", "🀄");
        this.addApp("坦克大战", "TankBattle.java", "游戏娱乐", "经典坦克对战游戏", "🚗");
        this.addApp("贪吃蛇对战", "SnakeBattle.java", "游戏娱乐", "双人贪吃蛇对战游戏", "🐍");
        this.addApp("华容道", "KlotskiPuzzle.java", "游戏娱乐", "传统华容道拼图游戏", "🧩");
        
        // 生活实用类 (15个)
        this.addApp("待办事项", "TodoList.java", "生活实用", "任务管理、优先级设置的待办事项应用", "📝");
        this.addApp("简易记事本", "SimpleNotepad.java", "生活实用", "功能完整的文本编辑器", "📄");
        this.addApp("密码管理器", "PasswordManager.java", "生活实用", "安全的密码存储和管理工具", "🔐");
        this.addApp("通讯录", "AddressBook.java", "生活实用", "联系人信息管理工具", "📞");
        this.addApp("番茄钟", "PomodoroTimer.java", "生活实用", "专注时间管理工具", "🍅");
        this.addApp("记事本", "Notepad.java", "生活实用", "高级文本编辑器", "📓");
        this.addApp("个人账本", "ExpenseTracker.java", "生活实用", "收支记录和分析工具", "💰");
        this.addApp("习惯追踪器", "HabitTracker.java", "生活实用", "日常习惯养成追踪工具", "✅");
        this.addApp("单词本", "Vocabulary.java", "生活实用", "英语单词学习管理工具", "📖");
        this.addApp("抽认卡", "Flashcard.java", "生活实用", "记忆卡片学习工具", "🃏");
        this.addApp("学习计划", "StudyPlanner.java", "生活实用", "学习任务规划管理工具", "📚");
        this.addApp("考试倒计时", "ExamCountdown.java", "生活实用", "考试时间倒计时提醒工具", "📅");
        this.addApp("饮水提醒", "WaterReminder.java", "生活实用", "健康饮水提醒工具", "💧");
        this.addApp("用药提醒", "MedicineReminder.java", "生活实用", "药物服用提醒管理工具", "💊");
        this.addApp("随机决策器", "RandomDecision.java", "生活实用", "帮助做决定的随机选择工具", "🎲");
        
        // 开发工具类 (10个)
        this.addApp("ASCII艺术", "ASCIIArt.java", "开发工具", "文本转ASCII艺术生成器", "🎨");
        this.addApp("Markdown预览器", "MarkdownViewer.java", "开发工具", "Markdown文档预览工具", "📄");
        this.addApp("代码格式化器", "CodeFormatter.java", "开发工具", "多语言代码格式化工具", "📝");
        this.addApp("日志分析器", "LogAnalyzer.java", "开发工具", "日志文件分析和过滤工具", "📈");
        this.addApp("网络测速", "NetworkSpeedTest.java", "开发工具", "网络速度测试工具", "📶");
        this.addApp("简单邮件客户端", "SimpleEmailClient.java", "开发工具", "基础邮件发送工具", "📧");
        this.addApp("文件压缩工具", "FileCompressor.java", "开发工具", "ZIP文件压缩解压工具", "🗜️");
        this.addApp("进程监视器", "ProcessMonitor.java", "开发工具", "系统进程监控工具", "📊");
        this.addApp("Base64增强版", "Base64Encoder.java", "开发工具", "增强版Base64编解码工具", "🔠");
        this.addApp("联系人对话框", "ContactDialog.java", "开发工具", "联系人信息编辑对话框", "📞");
        
        // 创意设计类 (10个)
        this.addApp("简单图片编辑器", "SimpleImageEditor.java", "创意设计", "基础图片编辑和滤镜工具", "🖼️");
        this.addApp("像素编辑器", "PixelEditor.java", "创意设计", "像素画创作工具", "🎨");
        this.addApp("动画制作器", "AnimationMaker.java", "创意设计", "简单动画制作工具", "🎥");
        this.addApp("图标生成器", "IconGenerator.java", "创意设计", "各种形状图标生成工具", "🖼️");
        this.addApp("条形码生成器", "BarcodeGenerator.java", "创意设计", "条形码生成工具", "📊");
        this.addApp("颜色选择器", "ColorPicker.java", "创意设计", "RGB颜色调节和HEX值获取工具", "🎨");
        this.addApp("音乐播放器", "MusicPlayer.java", "创意设计", "支持WAV/AIFF格式的音乐播放器", "🎵");
        this.addApp("图片浏览器", "ImageViewer.java", "创意设计", "简洁优雅的图片查看工具", "🖼️");
        this.addApp("简易视频播放器", "SimpleVideoPlayer.java", "创意设计", "基础视频播放界面", "🎥");
        this.addApp("拼图游戏", "JigsawPuzzle.java", "创意设计", "图片拼图游戏", "🧩");
        
        // 网络通信类 (10个)
        this.addApp("聊天室", "ChatRoom.java", "网络通信", "简单的聊天室应用", "💬");
        this.addApp("文件传输", "FileTransfer.java", "网络通信", "文件传输工具", "📤");
        this.addApp("RSS阅读器", "RSSReader.java", "网络通信", "RSS订阅和阅读工具", "📰");
        this.addApp("天气应用", "WeatherApp.java", "网络通信", "天气预报查询工具", "☁️");
        this.addApp("运动记录", "ExerciseTracker.java", "网络通信", "运动数据记录和分析工具", "🏃‍♂️");
        this.addApp("睡眠记录", "SleepTracker.java", "网络通信", "睡眠质量记录分析工具", "😴");
        this.addApp("卡路里计算", "CalorieCounter.java", "网络通信", "食物卡路里记录计算工具", "🍎");
        this.addApp("联系人", "Contact.java", "网络通信", "联系人信息数据模型", "📞");
        this.addApp("迷宫游戏", "MazeGame.java", "网络通信", "迷宫探索游戏", "🌿");
        this.addApp("连连看", "LinkGame.java", "网络通信", "图案连接消除游戏", "🔗");
    }
    
    private void addApp(String name, String fileName, String category, String description, String emoji) {
        var app = new AppInfo(name, fileName, category, description, emoji);
        apps.put(fileName, app);
    }
    
    private void initializeGUI() {
        setTitle("Java应用管理器 - 100个应用集合");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 苹果风格背景色
        getContentPane().setBackground(Color.WHITE);
        
        // 创建顶部面板 - 使用苹果风格设计
        this.createTopPanel();
        
        // 创建中间面板
        this.createCenterPanel();
        
        // 创建底部面板
        this.createBottomPanel();
        
        // 设置窗口属性
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void createTopPanel() {
        var topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        // 标题标签 - 苹果风格 (更新为更大的标题)
        var titleLabel = new JLabel("🚀 Java应用管理器", JLabel.CENTER);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 34)); // 使用超大标题
        titleLabel.setForeground(new Color(28, 28, 30));
        
        // 搜索面板
        var searchPanel = this.createSearchPanel();
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
    }
    
    private JPanel createSearchPanel() {
        var searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        var searchLabel = new JLabel("🔍 搜索应用: ");
        searchLabel.setFont(new Font("SF Pro Display", Font.BOLD, 17)); // 使用副标题字体
        searchLabel.setForeground(new Color(99, 99, 102));
        
        searchField = this.createStyledTextField();
        // 添加实时搜索监听器
        this.setupRealTimeSearch();
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        return searchPanel;
    }
    
    private JTextField createStyledTextField() {
        var textField = new JTextField();
        textField.setFont(new Font("SF Pro Display", Font.PLAIN, 15)); // 使用正文字体
        textField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12), // 使用中圆角
            BorderFactory.createEmptyBorder(16, 20, 16, 20) // 更大的内边距
        ));
        textField.setBackground(new Color(242, 242, 247));
        textField.setForeground(new Color(28, 28, 30));
        return textField;
    }
    
    private void setupRealTimeSearch() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // 使用SwingUtilities.invokeLater确保在UI线程中执行
                SwingUtilities.invokeLater(() -> AppManager.this.filterApps());
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> AppManager.this.filterApps());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> AppManager.this.filterApps());
            }
        });
    }
    
    private void createCenterPanel() {
        var centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24)); // 更新间距
        
        // 创建应用列表
        this.createAppList();
        var listScrollPane = new JScrollPane(appList);
        listScrollPane.setPreferredSize(new Dimension(400, 0));
        listScrollPane.setBorder(this.createTitledBorder("应用列表"));
        listScrollPane.getViewport().setBackground(Color.WHITE);
        
        // 创建描述区域
        this.createDescriptionArea();
        var descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setBorder(this.createTitledBorder("应用详情"));
        descScrollPane.getViewport().setBackground(Color.WHITE);
        
        centerPanel.add(listScrollPane, BorderLayout.WEST);
        centerPanel.add(descScrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void createAppList() {
        listModel = new DefaultListModel<>();
        appList = new JList<>(listModel);
        appList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appList.setFont(new Font("SF Pro Display", Font.PLAIN, 15)); // 使用正文字体
        appList.setBackground(Color.WHITE);
        appList.setSelectionBackground(new Color(0, 122, 255, 30));
        appList.setSelectionForeground(new Color(0, 122, 255));
        appList.setCellRenderer(new AppListCellRenderer());
        appList.addListSelectionListener((ev) -> {
            if (!ev.getValueIsAdjusting()) {
                this.updateDescription();
            }
        });
    }
    
    private void createDescriptionArea() {
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("SF Pro Display", Font.PLAIN, 15));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setForeground(new Color(28, 28, 30));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }
    
    private javax.swing.border.Border createTitledBorder(String title) {
        var border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(new Font("SF Pro Display", Font.BOLD, 16));
        border.setTitleColor(new Color(99, 99, 102));
        return BorderFactory.createCompoundBorder(
            border,
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );
    }
    
    private void createBottomPanel() {
        var bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); // 适中间距
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20)); // 适中边距
        
        // 创建苹果风格按钮
        launchButton = this.createPrimaryButton("🚀 启动应用");
        launchButton.addActionListener(this::launchSelectedApp);
        launchButton.setEnabled(false);
        
        var refreshButton = this.createSecondaryButton("🔄 刷新列表");
        refreshButton.addActionListener((ev) -> this.loadAppList());
        
        var openFolderButton = this.createSecondaryButton("📁 打开目录");
        openFolderButton.addActionListener(this::openSourceFolder);
        
        // 统计标签
        var statsLabel = new JLabel();
        statsLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 13)); // 使用标注字体
        statsLabel.setForeground(new Color(99, 99, 102));
        this.updateStatsLabel(statsLabel);
        
        bottomPanel.add(launchButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(openFolderButton);
        bottomPanel.add(Box.createHorizontalStrut(20)); // 适中间距
        bottomPanel.add(statsLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // 苹果风格按钮创建方法
    private JButton createPrimaryButton(String text) {
        return this.createStyledButton(text, new Color(0, 122, 255), Color.WHITE);
    }
    
    private JButton createSecondaryButton(String text) {
        return this.createStyledButton(text, new Color(242, 242, 247), new Color(28, 28, 30));
    }
    
    private JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        var button = new JButton(text);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 15));
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setPreferredSize(new Dimension(120, 36));
        
        // 设置苹果风格的disabled状态
        this.setupDisabledStyle(button, backgroundColor, textColor);
        
        // 添加悬停效果 - 符合苹果设计规范
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color originalColor = backgroundColor;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent ev) {
                // 只有enabled状态才响应hover效果
                if (button.isEnabled()) {
                    button.setBackground(AppManager.this.darkenColor(originalColor, 0.1f));
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    // disabled状态保持默认鼠标指针
                    button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent ev) {
                // 只有enabled状态才恢复原色
                if (button.isEnabled()) {
                    button.setBackground(originalColor);
                }
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    // 设置苹果风格的disabled状态
    private void setupDisabledStyle(JButton button, Color backgroundColor, Color textColor) {
        // 监听enabled状态变化
        button.addPropertyChangeListener("enabled", (ev) -> {
            if (button.isEnabled()) {
                // 启用状态：恢复原始颜色
                button.setBackground(backgroundColor);
                button.setForeground(textColor);
            } else {
                // 禁用状态：苹果风格的淡化效果
                if (backgroundColor.equals(new Color(0, 122, 255))) {
                    // 主要按钮禁用：淡蓝色背景 + 淡灰色文字
                    button.setBackground(new Color(174, 199, 255)); // 淡蓝色
                    button.setForeground(new Color(174, 174, 178)); // 淡灰色文字
                } else {
                    // 次要按钮禁用：更淡的灰色背景 + 淡灰色文字
                    button.setBackground(new Color(248, 248, 248));
                    button.setForeground(new Color(174, 174, 178)); // 苹果的占位符颜色
                }
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
    
    // 自定义列表单元格渲染器
    private static class AppListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            setFont(new Font("SF Pro Display", Font.PLAIN, 15));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            
            if (isSelected) {
                setBackground(new Color(0, 122, 255, 30));
                setForeground(new Color(0, 122, 255));
            } else {
                setBackground(Color.WHITE);
                setForeground(new Color(28, 28, 30));
            }
            
            return this;
        }
    }
    
    // 圆角边框类
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            var g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
    }
    
    private void loadAppList() {
        listModel.clear();
        
        // 所有应用都已实现，无需检查文件状态
        
        // 添加到列表模型
        for (AppInfo app : apps.values()) {
            listModel.addElement(app);
        }
        
        this.filterApps();
    }
    
    private void filterApps() {
        String searchText = searchField.getText().toLowerCase().trim();
        listModel.clear();
        
        for (var app : apps.values()) {
            if (searchText.isEmpty() || 
                app.name().toLowerCase().contains(searchText) ||
                app.category().toLowerCase().contains(searchText) ||
                app.description().toLowerCase().contains(searchText)) {
                listModel.addElement(app);
            }
        }
    }
    
    private void updateDescription() {
        var selected = appList.getSelectedValue();
        if (selected != null) {
            var sb = new StringBuilder();
            sb.append("应用名称: ").append(selected.name()).append("\n\n");
            sb.append("文件名: ").append(selected.fileName()).append("\n\n");
            sb.append("分类: ").append(selected.category()).append("\n\n");
            sb.append("描述: ").append(selected.description()).append("\n\n");
            sb.append("启动命令: java apps/").append(selected.fileName()).append("\n\n");
            sb.append("💡 Java 25现代化启动方式，无需预编译");
            
            descriptionArea.setText(sb.toString());
            launchButton.setEnabled(true);
        } else {
            descriptionArea.setText("请选择一个应用查看详细信息");
            launchButton.setEnabled(false);
        }
    }
    
    private void updateStatsLabel(JLabel label) {
        long total = apps.size();
        label.setText(String.format("📊 应用总数: %d", total));
    }
    
    private void launchSelectedApp(ActionEvent e) {
        var selected = appList.getSelectedValue();
        if (selected != null) {
            try {
                // Java 25直接运行源文件 - 现代化方式
                var pb = new ProcessBuilder("java", "apps/" + selected.fileName());
                pb.directory(new File("."));
                pb.inheritIO(); // 继承父进程的IO，这样可以看到应用的输出
                
                var process = pb.start();
                
                // 显示Toast提示
                this.showToast("🚀 应用 \"" + selected.name() + "\" 启动成功！", ToastType.SUCCESS);
                
                // 在后台检查进程状态
                SwingUtilities.invokeLater(() -> {
                    try {
                        // 等待一小段时间检查进程是否正常启动
                        Thread.sleep(500);
                        if (!process.isAlive()) {
                            int exitCode = process.exitValue();
                            if (exitCode != 0) {
                                this.showToast("❌ 应用启动失败，退出码: " + exitCode, ToastType.ERROR);
                            }
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                });
                    
            } catch (IOException ex) {
                this.showToast("❌ 启动失败: " + ex.getMessage(), ToastType.ERROR);
            }
        } else if (selected != null) {
            this.showToast("⚠️ 该应用尚未实现", ToastType.WARNING);
        } else {
            this.showToast("⚠️ 请先选择一个应用", ToastType.WARNING);
        }
    }
    
    private void openSourceFolder(ActionEvent e) {
        try {
            Desktop.getDesktop().open(new File("apps"));
            this.showToast("📁 应用目录已打开", ToastType.SUCCESS);
        } catch (IOException ex) {
            this.showToast("❌ 无法打开应用目录: " + ex.getMessage(), ToastType.ERROR);
        }
    }
    
    // Toast提示类型枚举
    enum ToastType {
        SUCCESS(new Color(52, 199, 89), "✅"),
        ERROR(new Color(255, 59, 48), "❌"),
        WARNING(new Color(255, 149, 0), "⚠️"),
        INFO(new Color(0, 122, 255), "ℹ️");
        
        final Color color;
        final String icon;
        
        ToastType(Color color, String icon) {
            this.color = color;
            this.icon = icon;
        }
    }
    
    // Toast提示方法
    private void showToast(String message, ToastType type) {
        var toast = new JWindow(this);
        toast.setAlwaysOnTop(true);
        
        // 创建Toast内容面板 - 使用更现代的设计
        var contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(28, 28, 30, 240)); // 半透明深色背景
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(16), // 使用大圆角
            BorderFactory.createEmptyBorder(16, 20, 16, 20) // 更大内边距
        ));
        
        // 创建消息标签
        var messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        contentPanel.add(messageLabel, BorderLayout.CENTER);
        toast.setContentPane(contentPanel);
        
        // 设置Toast位置和大小
        toast.pack();
        var parentBounds = this.getBounds();
        var toastBounds = toast.getBounds();
        
        // 在父窗口顶部居中显示
        int x = parentBounds.x + (parentBounds.width - toastBounds.width) / 2;
        int y = parentBounds.y + 80; // 距离顶部80px
        toast.setLocation(x, y);
        
        // 显示Toast
        toast.setVisible(true);
        
        // 3秒后自动隐藏
        var timer = new javax.swing.Timer(3000, (ev) -> {
            // 淡出动画效果
            this.fadeOutToast(toast);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    // Toast淡出动画
    private void fadeOutToast(JWindow toast) {
        var timer = new javax.swing.Timer(50, null);
        final float[] opacity = {1.0f};
        
        timer.addActionListener((ev) -> {
            opacity[0] -= 0.1f;
            if (opacity[0] <= 0) {
                toast.dispose();
                timer.stop();
            } else {
                toast.setOpacity(opacity[0]);
            }
        });
        
        timer.start();
    }
}
