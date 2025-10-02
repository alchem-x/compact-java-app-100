import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "📋 日志分析器";

    // 菜单项
    static final String MENU_FILE = "文件";
    static final String MENU_OPEN_LOG_FILE = "打开日志文件";
    static final String MENU_EXPORT_RESULTS = "导出结果";
    static final String MENU_EXIT = "退出";
    static final String MENU_VIEW = "视图";
    static final String MENU_REFRESH = "刷新";
    static final String MENU_CLEAR = "清空";
    static final String MENU_TOOLS = "工具";
    static final String MENU_STATISTICS = "统计信息";
    static final String MENU_ERROR_SUMMARY = "错误汇总";

    // 工具栏
    static final String TOOLBAR_OPEN_FILE = "打开文件";
    static final String TOOLBAR_SEARCH = "搜索:";
    static final String TOOLBAR_SEARCH_BUTTON = "搜索";
    static final String TOOLBAR_LEVEL = "级别:";
    static final String TOOLBAR_CLEAR_FILTER = "清空过滤";

    // 表格列名
    static final String TABLE_COLUMN_TIME = "时间";
    static final String TABLE_COLUMN_LEVEL = "级别";
    static final String TABLE_COLUMN_MESSAGE = "消息";
    static final String TABLE_COLUMN_SOURCE = "来源";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_LOADED_LOGS = "已加载 %d 条日志记录";
    static final String STATUS_SHOWING_RECORDS = "显示 %d / %d 条记录";
    static final String STATUS_SHOWING_ALL_RECORDS = "显示 %d 条记录";
    static final String STATUS_EXPORTED_TO = "已导出到: %s";

    // 文件过滤器
    static final String FILE_FILTER_LOG = "日志文件";
    static final String FILE_FILTER_CSV = "CSV文件";
    static final String FILE_EXTENSIONS_LOG = "log,txt";
    static final String FILE_EXTENSIONS_CSV = "csv";

    // 统计信息
    static final String DIALOG_STATISTICS_TITLE = "统计信息";
    static final String STATISTICS_HEADER = "日志统计信息\n\n";
    static final String STATISTICS_TOTAL_RECORDS = "总记录数: %d\n\n";
    static final String STATISTICS_BY_LEVEL = "按级别统计:\n";
    static final String STATISTICS_BY_SOURCE = "按来源统计:\n";

    // 错误汇总
    static final String DIALOG_ERROR_SUMMARY_TITLE = "错误汇总";
    static final String ERROR_SUMMARY_NO_ERRORS = "没有发现错误记录";
    static final String ERROR_SUMMARY_HEADER = "错误汇总 (%d 条)\n\n";

    // 导出功能
    static final String CSV_HEADER = "时间,级别,消息,来源";
    static final String EXPORT_COMPLETE = "导出完成";

    // 错误消息
    static final String ERROR_LOAD_LOG_FAILED = "加载日志文件失败: %s";
    static final String ERROR_EXPORT_FAILED = "导出失败: %s";

    // 日志级别
    static final String LOG_LEVEL_ALL = "全部";

    // 示例日志
    static final String SAMPLE_LOG_APP_START = "应用程序启动";
    static final String SAMPLE_LOG_DB_INIT = "初始化数据库连接";
    static final String SAMPLE_LOG_USER_LOGIN = "用户登录: admin";
    static final String SAMPLE_LOG_MEMORY_WARN = "内存使用率达到80%";
    static final String SAMPLE_LOG_DB_ERROR = "数据库连接失败";
    static final String SAMPLE_LOG_DB_RECONNECT = "重新连接数据库成功";
    static final String SAMPLE_LOG_HANDLE_REQUEST = "处理用户请求";
    static final String SAMPLE_LOG_RESPONSE_WARN = "响应时间超过阈值";
    static final String SAMPLE_LOG_FILE_ERROR = "文件读取失败";
    static final String SAMPLE_LOG_SCHEDULER_COMPLETE = "定时任务执行完成";

    // 来源名称
    static final String SOURCE_MAIN = "Main";
    static final String SOURCE_DATABASE = "Database";
    static final String SOURCE_AUTH = "Auth";
    static final String SOURCE_SYSTEM = "System";
    static final String SOURCE_HANDLER = "Handler";
    static final String SOURCE_PERFORMANCE = "Performance";
    static final String SOURCE_FILESYSTEM = "FileSystem";
    static final String SOURCE_SCHEDULER = "Scheduler";
    static final String SOURCE_UNKNOWN = "Unknown";
}

/**
 * 日志分析器
 * 分析和查看日志文件，支持过滤、搜索和统计
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new LogAnalyzer().setVisible(true);
    });
}

class LogAnalyzer extends JFrame {
    // ===== Apple设计系统常量 =====
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
    private static final Font MONO = new Font("SF Mono", Font.PLAIN, 12);

    // 间距
    private static final int SPACING_2 = 2;
    private static final int SPACING_4 = 4;
    private static final int SPACING_8 = 8;
    private static final int SPACING_12 = 12;
    private static final int SPACING_16 = 16;
    private static final int SPACING_20 = 20;
    private static final int SPACING_24 = 24;
    private static final int SPACING_32 = 32;

    // 按钮尺寸
    private static final Dimension BUTTON_REGULAR = new Dimension(120, 44);
    private static final Dimension BUTTON_LARGE = new Dimension(160, 50);

    // ===== 应用组件 =====
    private JTable logTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<LogLevel> levelFilter;
    private JLabel statusLabel;
    private List<LogEntry> allLogs = new ArrayList<>();
    private List<LogEntry> filteredLogs = new ArrayList<>();
    
    public LogAnalyzer() {
        this.initializeUI();
        this.setupEventHandlers();
        this.loadSampleLogs();
        this.setupKeyboardShortcuts();
    }
    
    private void initializeUI() {
        setTitle(Texts.WINDOW_TITLE);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 创建菜单栏
        this.createMenuBar();

        // 创建工具栏
        var toolBar = this.createToolBar();
        add(toolBar, BorderLayout.NORTH);

        // 创建主面板
        var mainPanel = this.createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // 创建状态栏
        statusLabel = new JLabel(Texts.STATUS_READY);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16));
        statusLabel.setFont(CAPTION1);
        statusLabel.setForeground(SECONDARY_LABEL);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void createMenuBar() {
        var menuBar = new JMenuBar();
        menuBar.setBackground(SYSTEM_BACKGROUND);

        var fileMenu = new JMenu(Texts.MENU_FILE);
        fileMenu.setFont(BODY);
        this.addMenuItem(fileMenu, Texts.MENU_OPEN_LOG_FILE, this::openLogFile);
        this.addMenuItem(fileMenu, Texts.MENU_EXPORT_RESULTS, this::exportResults);
        fileMenu.addSeparator();
        this.addMenuItem(fileMenu, Texts.MENU_EXIT, e -> System.exit(0));

        var viewMenu = new JMenu(Texts.MENU_VIEW);
        viewMenu.setFont(BODY);
        this.addMenuItem(viewMenu, Texts.MENU_REFRESH, this::refreshView);
        this.addMenuItem(viewMenu, Texts.MENU_CLEAR, this::clearLogs);

        var toolsMenu = new JMenu(Texts.MENU_TOOLS);
        toolsMenu.setFont(BODY);
        this.addMenuItem(toolsMenu, Texts.MENU_STATISTICS, this::showStatistics);
        this.addMenuItem(toolsMenu, Texts.MENU_ERROR_SUMMARY, this::showErrorSummary);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);

        setJMenuBar(menuBar);
    }
    
    private void addMenuItem(JMenu menu, String text, ActionListener action) {
        var item = new JMenuItem(text);
        item.setFont(BODY);
        item.setBackground(WHITE);
        item.setForeground(LABEL);
        item.addActionListener(action);
        menu.add(item);
    }
    
    private JPanel createToolBar() {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_12, SPACING_8));
        panel.setBackground(SYSTEM_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));

        var openBtn = this.createPrimaryButton(Texts.TOOLBAR_OPEN_FILE, this::openLogFile);
        panel.add(openBtn);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        // 搜索框 - 使用苹果风格
        var searchLabel = new JLabel(Texts.TOOLBAR_SEARCH);
        searchLabel.setFont(BODY);
        searchLabel.setForeground(LABEL);
        panel.add(searchLabel);

        searchField = new JTextField(20);
        searchField.setFont(MONO);
        searchField.setBackground(GRAY6);
        searchField.setForeground(LABEL);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY4),
            BorderFactory.createEmptyBorder(SPACING_4, SPACING_8, SPACING_4, SPACING_8)
        ));
        searchField.addActionListener(this::filterLogs);
        panel.add(searchField);

        var searchBtn = this.createPrimaryButton(Texts.TOOLBAR_SEARCH_BUTTON, this::filterLogs);
        panel.add(searchBtn);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        // 级别过滤 - 使用苹果风格
        var levelLabel = new JLabel(Texts.TOOLBAR_LEVEL);
        levelLabel.setFont(BODY);
        levelLabel.setForeground(LABEL);
        panel.add(levelLabel);

        levelFilter = new JComboBox<>();
        levelFilter.addItem(null); // 全部
        levelFilter.setFont(BODY);
        levelFilter.setBackground(WHITE);
        levelFilter.setForeground(LABEL);
        levelFilter.setBorder(BorderFactory.createLineBorder(GRAY4));
        for (var level : LogLevel.values()) {
            levelFilter.addItem(level);
        }
        levelFilter.addActionListener(this::filterLogs);
        panel.add(levelFilter);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        var clearBtn = this.createSecondaryButton(Texts.TOOLBAR_CLEAR_FILTER, this::clearFilters);
        panel.add(clearBtn);

        return panel;
    }

    // ===== 按钮创建方法 =====
    private JButton createPrimaryButton(String text, ActionListener action) {
        return this.createStyledButton(text, BLUE, WHITE, action);
    }

    private JButton createSecondaryButton(String text, ActionListener action) {
        return this.createStyledButton(text, GRAY6, LABEL, action);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor, ActionListener action) {
        var button = new JButton(text);
        button.setFont(BODY);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16),
            BorderFactory.createLineBorder(GRAY4, 1)
        ));
        button.setPreferredSize(BUTTON_REGULAR);

        // 设置悬停效果
        this.setupButtonHoverEffect(button, backgroundColor);

        // 添加动作监听器
        button.addActionListener(action);

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
    
    private JPanel createMainPanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBackground(SYSTEM_BACKGROUND);

        // 创建表格 - 使用中文列名
        String[] columns = {Texts.TABLE_COLUMN_TIME, Texts.TABLE_COLUMN_LEVEL, Texts.TABLE_COLUMN_MESSAGE, Texts.TABLE_COLUMN_SOURCE};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logTable = new JTable(tableModel);
        logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logTable.setFont(MONO);
        logTable.setBackground(WHITE);
        logTable.setForeground(LABEL);
        logTable.setRowHeight(24);
        logTable.getTableHeader().setFont(HEADLINE);
        logTable.getTableHeader().setBackground(SECONDARY_SYSTEM_BACKGROUND);
        logTable.getTableHeader().setForeground(LABEL);

        // 设置列宽
        logTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        logTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        logTable.getColumnModel().getColumn(2).setPreferredWidth(400);
        logTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        // 设置行渲染器
        logTable.setDefaultRenderer(Object.class, new LogTableCellRenderer());

        var scrollPane = new JScrollPane(logTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));
        scrollPane.getViewport().setBackground(WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private void loadSampleLogs() {
        // 生成示例日志 - 使用中文文本
        var now = LocalDateTime.now();

        allLogs.add(new LogEntry(now.minusMinutes(10), LogLevel.INFO, Texts.SAMPLE_LOG_APP_START, Texts.SOURCE_MAIN));
        allLogs.add(new LogEntry(now.minusMinutes(9), LogLevel.DEBUG, Texts.SAMPLE_LOG_DB_INIT, Texts.SOURCE_DATABASE));
        allLogs.add(new LogEntry(now.minusMinutes(8), LogLevel.INFO, Texts.SAMPLE_LOG_USER_LOGIN, Texts.SOURCE_AUTH));
        allLogs.add(new LogEntry(now.minusMinutes(7), LogLevel.WARN, Texts.SAMPLE_LOG_MEMORY_WARN, Texts.SOURCE_SYSTEM));
        allLogs.add(new LogEntry(now.minusMinutes(6), LogLevel.ERROR, Texts.SAMPLE_LOG_DB_ERROR, Texts.SOURCE_DATABASE));
        allLogs.add(new LogEntry(now.minusMinutes(5), LogLevel.INFO, Texts.SAMPLE_LOG_DB_RECONNECT, Texts.SOURCE_DATABASE));
        allLogs.add(new LogEntry(now.minusMinutes(4), LogLevel.DEBUG, Texts.SAMPLE_LOG_HANDLE_REQUEST, Texts.SOURCE_HANDLER));
        allLogs.add(new LogEntry(now.minusMinutes(3), LogLevel.WARN, Texts.SAMPLE_LOG_RESPONSE_WARN, Texts.SOURCE_PERFORMANCE));
        allLogs.add(new LogEntry(now.minusMinutes(2), LogLevel.ERROR, Texts.SAMPLE_LOG_FILE_ERROR, Texts.SOURCE_FILESYSTEM));
        allLogs.add(new LogEntry(now.minusMinutes(1), LogLevel.INFO, Texts.SAMPLE_LOG_SCHEDULER_COMPLETE, Texts.SOURCE_SCHEDULER));

        this.refreshView();
    }

    // ===== 事件处理方法 =====
    private void setupEventHandlers() {
        // 表格双击事件
        logTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var selectedRow = logTable.getSelectedRow();
                    if (selectedRow != -1 && selectedRow < filteredLogs.size()) {
                        var log = filteredLogs.get(selectedRow);
                        showLogDetails(log);
                    }
                }
            }
        });
    }

    private void showLogDetails(LogEntry log) {
        var details = String.format("时间: %s\n级别: %s\n来源: %s\n\n消息:\n%s",
            log.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            log.level.name(),
            log.source,
            log.message);

        JOptionPane.showMessageDialog(this, details, "日志详情", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openLogFile(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            Texts.FILE_FILTER_LOG, Texts.FILE_EXTENSIONS_LOG.split(",")));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            this.loadLogFile(file);
        }
    }
    
    private void loadLogFile(File file) {
        SwingWorker<List<LogEntry>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<LogEntry> doInBackground() throws Exception {
                var logs = new ArrayList<LogEntry>();
                
                try (var reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        var entry = parseLogLine(line);
                        if (entry != null) {
                            logs.add(entry);
                        }
                    }
                }
                
                return logs;
            }
            
            @Override
            protected void done() {
                try {
                    allLogs = get();
                    refreshView();
                    statusLabel.setText(String.format(Texts.STATUS_LOADED_LOGS, allLogs.size()));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LogAnalyzer.this,
                        String.format(Texts.ERROR_LOAD_LOG_FAILED, e.getMessage()));
                }
            }
        };
        
        worker.execute();
    }
    
    private LogEntry parseLogLine(String line) {
        // 简单的日志解析，支持常见格式
        // 格式: [时间] [级别] 消息 - 来源
        
        try {
            if (line.trim().isEmpty()) return null;
            
            // 尝试解析时间戳
            var timePattern = Pattern.compile("\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\]");
            var timeMatcher = timePattern.matcher(line);
            
            LocalDateTime timestamp = LocalDateTime.now();
            if (timeMatcher.find()) {
                var timeStr = timeMatcher.group(1);
                timestamp = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            
            // 尝试解析级别
            LogLevel level = LogLevel.INFO;
            for (var l : LogLevel.values()) {
                if (line.toUpperCase().contains(l.name())) {
                    level = l;
                    break;
                }
            }
            
            // 提取消息和来源
            String message = line;
            String source = "Unknown";
            
            int dashIndex = line.lastIndexOf(" - ");
            if (dashIndex > 0) {
                message = line.substring(0, dashIndex);
                source = line.substring(dashIndex + 3);
            }
            
            // 清理消息中的时间戳和级别标记
            message = message.replaceAll("\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\]", "");
            message = message.replaceAll("\\[(INFO|DEBUG|WARN|ERROR|FATAL)\\]", "");
            message = message.trim();
            
            return new LogEntry(timestamp, level, message, source);
            
        } catch (Exception e) {
            // 如果解析失败，创建一个简单的条目
            return new LogEntry(LocalDateTime.now(), LogLevel.INFO, line, "Unknown");
        }
    }
    
    private void filterLogs(ActionEvent e) {
        this.filterLogs();
    }

    private void filterLogs() {
        String searchText = searchField.getText().toLowerCase();
        var selectedLevel = (LogLevel) levelFilter.getSelectedItem();
        
        filteredLogs.clear();
        
        for (var log : allLogs) {
            boolean matchesSearch = searchText.isEmpty() || 
                log.message.toLowerCase().contains(searchText) ||
                log.source.toLowerCase().contains(searchText);
            
            boolean matchesLevel = selectedLevel == null || log.level == selectedLevel;
            
            if (matchesSearch && matchesLevel) {
                filteredLogs.add(log);
            }
        }
        
        updateTable();
        statusLabel.setText(String.format(Texts.STATUS_SHOWING_RECORDS, filteredLogs.size(), allLogs.size()));
    }
    
    private void clearFilters(ActionEvent e) {
        this.clearFilters();
    }

    private void clearFilters() {
        searchField.setText("");
        levelFilter.setSelectedIndex(0);
        refreshView();
    }
    
    private void refreshView(ActionEvent e) {
        this.refreshView();
    }

    private void refreshView() {
        filteredLogs.clear();
        filteredLogs.addAll(allLogs);
        updateTable();
        statusLabel.setText(String.format(Texts.STATUS_SHOWING_ALL_RECORDS, filteredLogs.size()));
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (var log : filteredLogs) {
            Object[] row = {
                log.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                log.level.name(),
                log.message,
                log.source
            };
            tableModel.addRow(row);
        }
    }
    
    private void clearLogs(ActionEvent e) {
        this.clearLogs();
    }

    private void clearLogs() {
        allLogs.clear();
        refreshView();
    }
    
    private void showStatistics(ActionEvent e) {
        var stats = new StringBuilder();
        stats.append(Texts.STATISTICS_HEADER);
        stats.append(String.format(Texts.STATISTICS_TOTAL_RECORDS, allLogs.size()));

        // 按级别统计
        var levelCounts = new EnumMap<LogLevel, Integer>(LogLevel.class);
        for (var log : allLogs) {
            levelCounts.merge(log.level, 1, Integer::sum);
        }

        stats.append(Texts.STATISTICS_BY_LEVEL);
        for (var entry : levelCounts.entrySet()) {
            stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        // 按来源统计
        var sourceCounts = new HashMap<String, Integer>();
        for (var log : allLogs) {
            sourceCounts.merge(log.source, 1, Integer::sum);
        }

        stats.append("\n").append(Texts.STATISTICS_BY_SOURCE);
        sourceCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));

        JOptionPane.showMessageDialog(this, stats.toString(), Texts.DIALOG_STATISTICS_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorSummary(ActionEvent e) {
        var errors = allLogs.stream()
            .filter(log -> log.level == LogLevel.ERROR || log.level == LogLevel.FATAL)
            .toList();

        if (errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, Texts.ERROR_SUMMARY_NO_ERRORS, Texts.DIALOG_ERROR_SUMMARY_TITLE, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        var summary = new StringBuilder();
        summary.append(String.format(Texts.ERROR_SUMMARY_HEADER, errors.size()));

        for (var error : errors) {
            summary.append("[").append(error.timestamp.format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"))).append("] ");
            summary.append(error.level).append(" - ");
            summary.append(error.message).append(" (").append(error.source).append(")\n");
        }

        var textArea = new JTextArea(summary.toString());
        textArea.setEditable(false);
        textArea.setFont(MONO);
        textArea.setBackground(GRAY6);
        textArea.setForeground(LABEL);

        var scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        scrollPane.getViewport().setBackground(GRAY6);

        JOptionPane.showMessageDialog(this, scrollPane, Texts.DIALOG_ERROR_SUMMARY_TITLE, JOptionPane.ERROR_MESSAGE);
    }
    
    private void exportResults(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            Texts.FILE_FILTER_CSV, Texts.FILE_EXTENSIONS_CSV));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (var writer = new PrintWriter(file)) {
                writer.println(Texts.CSV_HEADER);
                
                for (var log : filteredLogs) {
                    writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        log.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        log.level.name(),
                        log.message.replace("\"", "\"\""),
                        log.source
                    );
                }
                
                statusLabel.setText(String.format(Texts.STATUS_EXPORTED_TO, file.getName()));
                JOptionPane.showMessageDialog(this, Texts.EXPORT_COMPLETE);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, String.format(Texts.ERROR_EXPORT_FAILED, ex.getMessage()));
            }
        }
    }
    
    enum LogLevel {
        DEBUG, INFO, WARN, ERROR, FATAL
    }
    
    record LogEntry(LocalDateTime timestamp, LogLevel level, String message, String source) {}
    
    class LogTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected && row < filteredLogs.size()) {
                var log = filteredLogs.get(row);
                
                switch (log.level) {
                    case ERROR, FATAL -> setBackground(new Color(255, 230, 230));
                    case WARN -> setBackground(new Color(255, 255, 230));
                    case DEBUG -> setBackground(new Color(240, 240, 240));
                    default -> setBackground(Color.WHITE);
                }
            }
            
            return this;
        }
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_O:
                        // O键打开文件
                        if (ev.isControlDown()) {
                            openLogFile(new ActionEvent(LogAnalyzer.this, ActionEvent.ACTION_PERFORMED, "open"));
                        }
                        break;
                    case KeyEvent.VK_F:
                        // F键聚焦搜索框
                        if (!ev.isControlDown()) {
                            searchField.requestFocus();
                        }
                        break;
                    case KeyEvent.VK_R:
                        // R键刷新
                        if (!ev.isControlDown()) {
                            refreshView();
                        }
                        break;
                    case KeyEvent.VK_S:
                        // S键统计信息
                        if (ev.isControlDown()) {
                            showStatistics(new ActionEvent(LogAnalyzer.this, ActionEvent.ACTION_PERFORMED, "statistics"));
                        }
                        break;
                    case KeyEvent.VK_E:
                        // E键错误汇总
                        if (!ev.isControlDown()) {
                            showErrorSummary(new ActionEvent(LogAnalyzer.this, ActionEvent.ACTION_PERFORMED, "errorSummary"));
                        }
                        break;
                    case KeyEvent.VK_X:
                        // X键导出
                        if (ev.isControlDown()) {
                            exportResults(new ActionEvent(LogAnalyzer.this, ActionEvent.ACTION_PERFORMED, "export"));
                        }
                        break;
                    case KeyEvent.VK_C:
                        // C键清空
                        if (!ev.isControlDown()) {
                            clearLogs();
                        }
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
}
