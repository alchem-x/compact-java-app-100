import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.Timer;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new SleepTracker().setVisible(true));
}

static class SleepTracker extends JFrame {
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
    private final List<SleepRecord> sleepRecords;
    private final DefaultTableModel tableModel;
    private final JTable sleepTable;
    private final JTextField bedTimeField;
    private final JTextField wakeTimeField;
    private final JComboBox<String> qualityCombo;
    private final JTextArea notesArea;
    private final JLabel statusLabel;
    private final JLabel averageSleepLabel;
    private final JLabel sleepDebtLabel;
    private final JLabel weeklyAverageLabel;
    private final JProgressBar sleepGoalProgress;
    private final JTextArea recommendationsArea;
    private final JButton addRecordButton;
    private JButton deleteRecordButton;
    private JButton analyzeButton;
    private JButton exportButton;
    private JButton importButton;
    private final JCheckBox reminderCheck;
    private final JSpinner reminderHourSpinner;
    private final JSpinner reminderMinuteSpinner;

    private Timer reminderTimer;
    private static final int SLEEP_GOAL_HOURS = 8;
    private static final String DATA_FILE = "sleep_data.dat";

    public SleepTracker() {
        sleepRecords = new ArrayList<>();
        tableModel = new DefaultTableModel();
        sleepTable = new JTable(tableModel);
        bedTimeField = new JTextField();
        wakeTimeField = new JTextField();
        qualityCombo = new JComboBox<>(new String[]{"优秀", "良好", "一般", "较差", "很差"});
        notesArea = new JTextArea();
        statusLabel = new JLabel("就绪");
        averageSleepLabel = new JLabel("平均睡眠: 0h 0m");
        sleepDebtLabel = new JLabel("睡眠债务: 0h 0m");
        weeklyAverageLabel = new JLabel("本周平均: 0h 0m");
        sleepGoalProgress = new JProgressBar();
        recommendationsArea = new JTextArea();
        addRecordButton = new JButton("➕ 添加记录");
        deleteRecordButton = new JButton("🗑️ 删除记录");
        analyzeButton = new JButton("📊 分析");
        exportButton = new JButton("💾 导出");
        importButton = new JButton("📁 导入");
        reminderCheck = new JCheckBox("启用睡眠提醒");
        reminderHourSpinner = new JSpinner(new SpinnerNumberModel(22, 0, 23, 1));
        reminderMinuteSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 59, 1));

        initializeGUI();
        setupTable();
        setupEventHandlers();
        loadData();
        setupReminder();
    }

    private void initializeGUI() {
        setTitle("😴 睡眠追踪器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板 - 使用苹果风格
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel("😴 睡眠追踪器", SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_24, 0));

        // 输入面板 - 使用苹果风格卡片
        var inputPanel = createInputPanel();

        // 统计面板 - 使用苹果风格卡片
        var statsPanel = createStatsPanel();

        // 控制面板 - 使用苹果风格组件
        var controlPanel = createControlPanel();

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.WEST);
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // 表格面板 - 使用苹果风格
        var tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.EAST);

        // 状态栏 - 使用苹果风格
        var statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private JPanel createInputPanel() {
        var inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        inputPanel.setPreferredSize(new Dimension(300, 0));

        // 输入标题
        var inputTitle = new JLabel("📝 添加睡眠记录");
        inputTitle.setFont(TITLE3);
        inputTitle.setForeground(LABEL);
        inputTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 就寝时间
        var bedTimeLabel = new JLabel("就寝时间 (HH:MM):");
        bedTimeLabel.setFont(HEADLINE);
        bedTimeLabel.setForeground(LABEL);
        bedTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bedTimeField.setFont(new Font("SF Mono", Font.PLAIN, 14));
        bedTimeField.setText("23:00");
        bedTimeField.setBackground(GRAY6);
        bedTimeField.setForeground(LABEL);
        bedTimeField.setMaximumSize(new Dimension(200, 30));
        bedTimeField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));

        // 起床时间
        var wakeTimeLabel = new JLabel("起床时间 (HH:MM):");
        wakeTimeLabel.setFont(HEADLINE);
        wakeTimeLabel.setForeground(LABEL);
        wakeTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        wakeTimeField.setFont(new Font("SF Mono", Font.PLAIN, 14));
        wakeTimeField.setText("07:00");
        wakeTimeField.setBackground(GRAY6);
        wakeTimeField.setForeground(LABEL);
        wakeTimeField.setMaximumSize(new Dimension(200, 30));
        wakeTimeField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));

        // 睡眠质量
        var qualityLabel = new JLabel("睡眠质量:");
        qualityLabel.setFont(HEADLINE);
        qualityLabel.setForeground(LABEL);
        qualityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        qualityCombo.setFont(BODY);
        qualityCombo.setBackground(WHITE);
        qualityCombo.setForeground(LABEL);
        qualityCombo.setMaximumSize(new Dimension(200, 30));

        // 备注
        var notesLabel = new JLabel("备注:");
        notesLabel.setFont(HEADLINE);
        notesLabel.setForeground(LABEL);
        notesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        notesArea.setFont(BODY);
        notesArea.setBackground(GRAY6);
        notesArea.setForeground(LABEL);
        notesArea.setRows(3);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));

        var notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setMaximumSize(new Dimension(200, 80));
        notesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        notesScrollPane.getViewport().setBackground(GRAY6);

        // 添加记录按钮
        addRecordButton.setFont(BODY);
        addRecordButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(Box.createVerticalGlue());
        inputPanel.add(inputTitle);
        inputPanel.add(Box.createRigidArea(new Dimension(0, SPACING_16)));
        inputPanel.add(bedTimeLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, SPACING_4)));
        inputPanel.add(bedTimeField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        inputPanel.add(wakeTimeLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, SPACING_4)));
        inputPanel.add(wakeTimeField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        inputPanel.add(qualityLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, SPACING_4)));
        inputPanel.add(qualityCombo);
        inputPanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        inputPanel.add(notesLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, SPACING_4)));
        inputPanel.add(notesScrollPane);
        inputPanel.add(Box.createRigidArea(new Dimension(0, SPACING_16)));
        inputPanel.add(addRecordButton);
        inputPanel.add(Box.createVerticalGlue());

        return inputPanel;
    }

    private JPanel createStatsPanel() {
        var statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 统计标题
        var statsTitle = new JLabel("📊 睡眠统计");
        statsTitle.setFont(TITLE3);
        statsTitle.setForeground(LABEL);
        statsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 平均睡眠
        averageSleepLabel.setFont(TITLE3);
        averageSleepLabel.setForeground(BLUE);
        averageSleepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 睡眠债务
        sleepDebtLabel.setFont(TITLE3);
        sleepDebtLabel.setForeground(RED);
        sleepDebtLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 本周平均
        weeklyAverageLabel.setFont(TITLE3);
        weeklyAverageLabel.setForeground(GREEN);
        weeklyAverageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 睡眠目标进度
        var sleepGoalLabel = new JLabel("睡眠目标进度 (8小时)");
        sleepGoalLabel.setFont(HEADLINE);
        sleepGoalLabel.setForeground(LABEL);
        sleepGoalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sleepGoalProgress.setFont(BODY);
        sleepGoalProgress.setStringPainted(true);
        sleepGoalProgress.setBackground(GRAY6);
        sleepGoalProgress.setForeground(GREEN);
        sleepGoalProgress.setMaximumSize(new Dimension(250, 25));
        sleepGoalProgress.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_2, SPACING_2, SPACING_2, SPACING_2)
        ));

        // 提醒设置
        var reminderTitle = new JLabel("⏰ 睡眠提醒");
        reminderTitle.setFont(TITLE3);
        reminderTitle.setForeground(LABEL);
        reminderTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        reminderCheck.setFont(BODY);
        reminderCheck.setForeground(LABEL);
        reminderCheck.setAlignmentX(Component.CENTER_ALIGNMENT);

        var reminderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_8, 0));
        reminderPanel.setBackground(WHITE);
        reminderPanel.setMaximumSize(new Dimension(250, 30));

        var reminderLabel = new JLabel("提醒时间:");
        reminderLabel.setFont(BODY);
        reminderLabel.setForeground(LABEL);

        reminderHourSpinner.setFont(BODY);
        reminderMinuteSpinner.setFont(BODY);

        reminderPanel.add(reminderLabel);
        reminderPanel.add(reminderHourSpinner);
        reminderPanel.add(new JLabel(":"));
        reminderPanel.add(reminderMinuteSpinner);

        statsPanel.add(Box.createVerticalGlue());
        statsPanel.add(statsTitle);
        statsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_16)));
        statsPanel.add(averageSleepLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_8)));
        statsPanel.add(sleepDebtLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_8)));
        statsPanel.add(weeklyAverageLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_16)));
        statsPanel.add(sleepGoalLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_8)));
        statsPanel.add(sleepGoalProgress);
        statsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_24)));
        statsPanel.add(reminderTitle);
        statsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_8)));
        statsPanel.add(reminderCheck);
        statsPanel.add(Box.createRigidArea(new Dimension(0, SPACING_8)));
        statsPanel.add(reminderPanel);
        statsPanel.add(Box.createVerticalGlue());

        return statsPanel;
    }

    private JPanel createControlPanel() {
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, 0));
        controlPanel.setBackground(SYSTEM_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_16, 0, 0, 0));

        deleteRecordButton = this.createWarningButton("删除记录");
        analyzeButton = this.createPrimaryButton("分析");
        exportButton = this.createSuccessButton("导出");
        importButton = this.createSecondaryButton("导入");

        controlPanel.add(deleteRecordButton);
        controlPanel.add(analyzeButton);
        controlPanel.add(exportButton);
        controlPanel.add(importButton);

        return controlPanel;
    }

    private JPanel createTablePanel() {
        var tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_12),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        tablePanel.setPreferredSize(new Dimension(400, 0));

        // 表格标题
        var tableTitle = new JLabel("📋 睡眠记录");
        tableTitle.setFont(TITLE3);
        tableTitle.setForeground(LABEL);
        tableTitle.setHorizontalAlignment(SwingConstants.CENTER);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_16, 0));

        // 表格设置
        tableModel.setColumnIdentifiers(new String[]{"日期", "就寝", "起床", "时长", "质量", "效率"});
        sleepTable.setFont(CAPTION1);
        sleepTable.setBackground(GRAY6);
        sleepTable.setForeground(LABEL);
        sleepTable.setSelectionBackground(new Color(0, 122, 255, 30));
        sleepTable.setSelectionForeground(BLUE);
        sleepTable.setRowHeight(25);
        sleepTable.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_4, SPACING_4, SPACING_4, SPACING_4)
        ));

        var tableScrollPane = new JScrollPane(sleepTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(GRAY6);

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createStatusPanel() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_24, SPACING_12, SPACING_24)
        ));

        statusLabel.setFont(FOOTNOTE);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        return statusPanel;
    }

    private void setupTable() {
        tableModel.setColumnIdentifiers(new String[]{"日期", "就寝", "起床", "时长", "质量", "效率"});
    }

    private void setupEventHandlers() {
        addRecordButton.addActionListener(this::addRecord);
        deleteRecordButton.addActionListener(this::deleteRecord);
        analyzeButton.addActionListener(this::analyzeSleep);
        exportButton.addActionListener(this::exportData);
        importButton.addActionListener(this::importData);
        reminderCheck.addActionListener(this::toggleReminder);

        // 表格选择监听
        sleepTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && sleepTable.getSelectedRow() != -1) {
                int selectedRow = sleepTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < sleepRecords.size()) {
                    SleepRecord record = sleepRecords.get(selectedRow);
                    bedTimeField.setText(record.bedTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                    wakeTimeField.setText(record.wakeTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                    qualityCombo.setSelectedItem(record.quality);
                    notesArea.setText(record.notes);
                }
            }
        });
    }

    private void addRecord(ActionEvent e) {
        try {
            LocalTime bedTime = LocalTime.parse(bedTimeField.getText().trim());
            LocalTime wakeTime = LocalTime.parse(wakeTimeField.getText().trim());
            String quality = (String) qualityCombo.getSelectedItem();
            String notes = notesArea.getText().trim();
            LocalDate date = LocalDate.now();

            // 计算睡眠时长
            long sleepMinutes = calculateSleepDuration(bedTime, wakeTime);
            if (sleepMinutes <= 0) {
                JOptionPane.showMessageDialog(this, "睡眠时长必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 检查是否已存在今天的记录
            for (int i = 0; i < sleepRecords.size(); i++) {
                if (sleepRecords.get(i).date.equals(date)) {
                    // 更新现有记录
                    sleepRecords.set(i, new SleepRecord(date, bedTime, wakeTime, quality, notes));
                    updateTable();
                    updateDisplay();
                    statusLabel.setText("已更新今天的睡眠记录");
                    return;
                }
            }

            // 添加新记录
            sleepRecords.add(new SleepRecord(date, bedTime, wakeTime, quality, notes));
            updateTable();
            updateDisplay();
            statusLabel.setText("睡眠记录已添加");

            // 清空输入
            notesArea.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "时间格式错误！请使用 HH:MM 格式", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord(ActionEvent e) {
        int selectedRow = sleepTable.getSelectedRow();
        if (selectedRow != -1) {
            sleepRecords.remove(selectedRow);
            updateTable();
            updateDisplay();
            statusLabel.setText("睡眠记录已删除");
        } else {
            JOptionPane.showMessageDialog(this, "请先选择要删除的记录！", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void analyzeSleep(ActionEvent e) {
        if (sleepRecords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有睡眠记录可供分析！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        var analysis = performSleepAnalysis();
        showAnalysisDialog(analysis);
    }

    private void exportData(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("sleep_data_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".dat"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                saveDataToFile(file);
                statusLabel.setText("数据已导出到: " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importData(ActionEvent e) {
        var fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                loadDataFromFile(file);
                updateTable();
                updateDisplay();
                statusLabel.setText("数据已从: " + file.getName() + " 导入");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "导入失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleReminder(ActionEvent e) {
        if (reminderCheck.isSelected()) {
            setupReminder();
            statusLabel.setText("睡眠提醒已启用");
        } else {
            if (reminderTimer != null) {
                reminderTimer.cancel();
                reminderTimer = null;
            }
            statusLabel.setText("睡眠提醒已禁用");
        }
    }

    private void setupReminder() {
        if (reminderTimer != null) {
            reminderTimer.cancel();
        }

        reminderTimer = new Timer();
        reminderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (reminderCheck.isSelected()) {
                    LocalTime now = LocalTime.now();
                    int reminderHour = (Integer) reminderHourSpinner.getValue();
                    int reminderMinute = (Integer) reminderMinuteSpinner.getValue();

                    if (now.getHour() == reminderHour && now.getMinute() == reminderMinute) {
                        SwingUtilities.invokeLater(() -> {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(SleepTracker.this,
                                "⏰ 该准备睡觉啦！\n良好的睡眠习惯有助于保持健康。",
                                "睡眠提醒",
                                JOptionPane.INFORMATION_MESSAGE);
                        });
                    }
                }
            }
        }, 60000, 60000); // 每分钟检查一次
    }

    private long calculateSleepDuration(LocalTime bedTime, LocalTime wakeTime) {
        if (wakeTime.isAfter(bedTime)) {
            return bedTime.until(wakeTime, ChronoUnit.MINUTES);
        } else {
            // 跨天的情况
            return bedTime.until(LocalTime.MAX, ChronoUnit.MINUTES) +
                   LocalTime.MIN.until(wakeTime, ChronoUnit.MINUTES) + 1;
        }
    }

    private double calculateSleepEfficiency(SleepRecord record) {
        long sleepMinutes = calculateSleepDuration(record.bedTime, record.wakeTime);
        long totalMinutes = record.bedTime.until(record.wakeTime, ChronoUnit.MINUTES);
        if (totalMinutes <= 0) return 0;
        return Math.min(100.0, (sleepMinutes * 100.0) / totalMinutes);
    }

    private SleepAnalysis performSleepAnalysis() {
        if (sleepRecords.isEmpty()) return null;

        double totalMinutes = 0;
        Map<String, Integer> qualityCount = new HashMap<>();
        LocalTime totalBedTime = LocalTime.MIN;
        LocalTime totalWakeTime = LocalTime.MIN;
        int recordsCount = sleepRecords.size();

        for (SleepRecord record : sleepRecords) {
            long minutes = calculateSleepDuration(record.bedTime, record.wakeTime);
            totalMinutes += minutes;

            qualityCount.put(record.quality, qualityCount.getOrDefault(record.quality, 0) + 1);

            // 累加就寝和起床时间用于计算规律性
            totalBedTime = totalBedTime.plusHours(record.bedTime.getHour())
                .plusMinutes(record.bedTime.getMinute());
            totalWakeTime = totalWakeTime.plusHours(record.wakeTime.getHour())
                .plusMinutes(record.wakeTime.getMinute());
        }

        // 计算平均睡眠时长
        double averageMinutes = totalMinutes / recordsCount;
        int avgHours = (int) (averageMinutes / 60);
        int avgMinutes = (int) (averageMinutes % 60);

        // 计算最常见的睡眠质量
        String mostCommonQuality = qualityCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("一般");

        // 计算平均效率
        double avgEfficiency = sleepRecords.stream()
            .mapToDouble(this::calculateSleepEfficiency)
            .average()
            .orElse(0.0);

        // 计算睡眠规律性（标准差）
        double bedTimeVariance = calculateBedTimeVariance();
        double sleepRegularity = Math.max(0, 100 - bedTimeVariance * 2);

        // 计算睡眠债务
        double sleepDebtMinutes = Math.max(0, (SLEEP_GOAL_HOURS * 60 - averageMinutes) * recordsCount);
        int sleepDebtHours = (int) (sleepDebtMinutes / 60);
        int sleepDebtMins = (int) (sleepDebtMinutes % 60);

        // 找出最佳睡眠
        String bestSleep = findBestSleep();

        // 生成建议
        String recommendations = generateRecommendations(avgHours, avgMinutes, mostCommonQuality, avgEfficiency, sleepRegularity);

        var analysis = new SleepAnalysis();
        analysis.averageDuration = averageMinutes;
        analysis.averageQuality = mostCommonQuality;
        analysis.averageEfficiency = avgEfficiency;
        analysis.sleepRegularity = sleepRegularity;
        analysis.sleepDebtHours = sleepDebtHours;
        analysis.sleepDebtMinutes = sleepDebtMins;
        analysis.bestSleep = bestSleep;
        analysis.recommendations = recommendations;

        return analysis;
    }

    private double calculateBedTimeVariance() {
        if (sleepRecords.size() < 2) return 0;

        double meanMinutes = sleepRecords.stream()
            .mapToInt(r -> r.bedTime.getHour() * 60 + r.bedTime.getMinute())
            .average()
            .orElse(0.0);

        double variance = sleepRecords.stream()
            .mapToDouble(r -> {
                double minutes = r.bedTime.getHour() * 60 + r.bedTime.getMinute();
                return Math.pow(minutes - meanMinutes, 2);
            })
            .average()
            .orElse(0.0);

        return Math.sqrt(variance);
    }

    private String findBestSleep() {
        return sleepRecords.stream()
            .max(Comparator.comparingDouble(r -> {
                long duration = calculateSleepDuration(r.bedTime, r.wakeTime);
                double efficiency = calculateSleepEfficiency(r);

                // 睡眠质量评分
                int qualityScore = switch (r.quality) {
                    case "优秀" -> 5;
                    case "良好" -> 4;
                    case "一般" -> 3;
                    case "较差" -> 2;
                    case "很差" -> 1;
                    default -> 3;
                };

                return duration * 0.4 + efficiency * 0.4 + qualityScore * 12;
            }))
            .map(r -> r.date.format(DateTimeFormatter.ofPattern("MM月dd日")) + " " +
                     r.bedTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "-" +
                     r.wakeTime.format(DateTimeFormatter.ofPattern("HH:mm")))
            .orElse("暂无数据");
    }

    private String generateRecommendations(int avgHours, int avgMinutes, String quality, double efficiency, double regularity) {
        var recommendations = new StringBuilder();

        // 睡眠时长建议
        if (avgHours < 7) {
            recommendations.append("• 睡眠时间偏短，建议增加睡眠时长至7-9小时\n");
        } else if (avgHours > 9) {
            recommendations.append("• 睡眠时间较长，如无特殊原因可适当调整\n");
        } else {
            recommendations.append("• 睡眠时长适中，保持良好习惯\n");
        }

        // 睡眠质量建议
        switch (quality) {
            case "很差", "较差" -> recommendations.append("• 睡眠质量较差，建议改善睡眠环境\n");
            case "一般" -> recommendations.append("• 睡眠质量一般，可尝试调整作息规律\n");
            case "良好", "优秀" -> recommendations.append("• 睡眠质量良好，继续保持\n");
        }

        // 睡眠效率建议
        if (efficiency < 85) {
            recommendations.append("• 睡眠效率偏低，建议睡前避免使用电子设备\n");
        } else {
            recommendations.append("• 睡眠效率良好\n");
        }

        // 睡眠规律性建议
        if (regularity < 70) {
            recommendations.append("• 睡眠规律性较差，建议固定作息时间\n");
        } else {
            recommendations.append("• 睡眠规律性良好\n");
        }

        return recommendations.toString();
    }

    private void showAnalysisDialog(SleepAnalysis analysis) {
        if (analysis == null) return;

        var dialog = new JDialog(this, "📊 睡眠分析报告", true);
        dialog.setLayout(new BorderLayout());

        var analysisPanel = new JPanel();
        analysisPanel.setLayout(new BoxLayout(analysisPanel, BoxLayout.Y_AXIS));
        analysisPanel.setBackground(WHITE);
        analysisPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));

        // 标题
        var titleLabel = new JLabel("睡眠分析报告");
        titleLabel.setFont(TITLE1);
        titleLabel.setForeground(LABEL);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 统计信息
        var avgSleepLabel = new JLabel(String.format("平均睡眠时长: %dh %dm",
            (int)(analysis.averageDuration / 60), (int)(analysis.averageDuration % 60)));
        avgSleepLabel.setFont(TITLE3);
        avgSleepLabel.setForeground(BLUE);
        avgSleepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        var qualityLabel = new JLabel("平均睡眠质量: " + analysis.averageQuality);
        qualityLabel.setFont(TITLE3);
        qualityLabel.setForeground(GREEN);
        qualityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        var efficiencyLabel = new JLabel(String.format("平均睡眠效率: %.1f%%", analysis.averageEfficiency));
        efficiencyLabel.setFont(TITLE3);
        efficiencyLabel.setForeground(ORANGE);
        efficiencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        var regularityLabel = new JLabel(String.format("睡眠规律性: %.1f%%", analysis.sleepRegularity));
        regularityLabel.setFont(TITLE3);
        regularityLabel.setForeground(PURPLE);
        regularityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        var sleepDebtLabel = new JLabel(String.format("睡眠债务: %dh %dm", analysis.sleepDebtHours, analysis.sleepDebtMinutes));
        sleepDebtLabel.setFont(TITLE3);
        sleepDebtLabel.setForeground(RED);
        sleepDebtLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        var bestSleepLabel = new JLabel("最佳睡眠: " + analysis.bestSleep);
        bestSleepLabel.setFont(TITLE3);
        bestSleepLabel.setForeground(TEAL);
        bestSleepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 建议
        var recommendationsTitle = new JLabel("📝 改善建议");
        recommendationsTitle.setFont(TITLE2);
        recommendationsTitle.setForeground(LABEL);
        recommendationsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        var recommendationsArea = new JTextArea(analysis.recommendations);
        recommendationsArea.setFont(BODY);
        recommendationsArea.setEditable(false);
        recommendationsArea.setBackground(GRAY6);
        recommendationsArea.setForeground(LABEL);
        recommendationsArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        recommendationsArea.setRows(8);

        var recommendationsScrollPane = new JScrollPane(recommendationsArea);
        recommendationsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        recommendationsScrollPane.getViewport().setBackground(GRAY6);

        analysisPanel.add(Box.createVerticalGlue());
        analysisPanel.add(titleLabel);
        analysisPanel.add(Box.createRigidArea(new Dimension(0, SPACING_24)));
        analysisPanel.add(avgSleepLabel);
        analysisPanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        analysisPanel.add(qualityLabel);
        analysisPanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        analysisPanel.add(efficiencyLabel);
        analysisPanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        analysisPanel.add(regularityLabel);
        analysisPanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        analysisPanel.add(sleepDebtLabel);
        analysisPanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        analysisPanel.add(bestSleepLabel);
        analysisPanel.add(Box.createRigidArea(new Dimension(0, SPACING_24)));
        analysisPanel.add(recommendationsTitle);
        analysisPanel.add(Box.createRigidArea(new Dimension(0, SPACING_12)));
        analysisPanel.add(recommendationsScrollPane);
        analysisPanel.add(Box.createVerticalGlue());

        // 按钮面板
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_16, 0));
        buttonPanel.setBackground(WHITE);

        var closeButton = this.createPrimaryButton("关闭");
        closeButton.addActionListener(ev -> dialog.dispose());

        buttonPanel.add(closeButton);

        dialog.add(analysisPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateTable() {
        tableModel.setRowCount(0);

        // 按日期排序（最新的在前）
        var sortedRecords = new ArrayList<SleepRecord>(sleepRecords);
        sortedRecords.sort((a, b) -> b.date.compareTo(a.date));

        for (SleepRecord record : sortedRecords) {
            long sleepMinutes = calculateSleepDuration(record.bedTime, record.wakeTime);
            int hours = (int) (sleepMinutes / 60);
            int minutes = (int) (sleepMinutes % 60);
            double efficiency = calculateSleepEfficiency(record);

            tableModel.addRow(new Object[]{
                record.date.format(DateTimeFormatter.ofPattern("MM-dd")),
                record.bedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                record.wakeTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                String.format("%dh %dm", hours, minutes),
                record.quality,
                String.format("%.1f%%", efficiency)
            });
        }
    }

    private void updateDisplay() {
        if (sleepRecords.isEmpty()) {
            averageSleepLabel.setText("平均睡眠: 0h 0m");
            sleepDebtLabel.setText("睡眠债务: 0h 0m");
            weeklyAverageLabel.setText("本周平均: 0h 0m");
            sleepGoalProgress.setValue(0);
            sleepGoalProgress.setString("0%");
            return;
        }

        // 计算平均睡眠时长
        double totalMinutes = 0;
        for (SleepRecord record : sleepRecords) {
            totalMinutes += calculateSleepDuration(record.bedTime, record.wakeTime);
        }
        double averageMinutes = totalMinutes / sleepRecords.size();
        int avgHours = (int) (averageMinutes / 60);
        int avgMinutes = (int) (averageMinutes % 60);
        averageSleepLabel.setText(String.format("平均睡眠: %dh %dm", avgHours, avgMinutes));

        // 计算本周平均
        LocalDate weekStart = LocalDate.now().minusDays(7);
        double weeklyMinutes = 0;
        int weeklyCount = 0;
        for (SleepRecord record : sleepRecords) {
            if (record.date.isAfter(weekStart) || record.date.isEqual(weekStart)) {
                weeklyMinutes += calculateSleepDuration(record.bedTime, record.wakeTime);
                weeklyCount++;
            }
        }
        if (weeklyCount > 0) {
            double weeklyAvgMinutes = weeklyMinutes / weeklyCount;
            int weeklyAvgHours = (int) (weeklyAvgMinutes / 60);
            int weeklyAvgMins = (int) (weeklyAvgMinutes % 60);
            weeklyAverageLabel.setText(String.format("本周平均: %dh %dm", weeklyAvgHours, weeklyAvgMins));
        } else {
            weeklyAverageLabel.setText("本周平均: 0h 0m");
        }

        // 计算睡眠债务
        double sleepDebtMinutes = Math.max(0, (SLEEP_GOAL_HOURS * 60 - averageMinutes) * sleepRecords.size());
        int sleepDebtHours = (int) (sleepDebtMinutes / 60);
        int sleepDebtMins = (int) (sleepDebtMinutes % 60);
        sleepDebtLabel.setText(String.format("睡眠债务: %dh %dm", sleepDebtHours, sleepDebtMins));

        // 更新进度条
        int progress = Math.min(100, (int) ((averageMinutes / (SLEEP_GOAL_HOURS * 60.0)) * 100));
        sleepGoalProgress.setValue(progress);
        sleepGoalProgress.setString(progress + "%");
    }

    private void updateStatistics() {
        updateDisplay();
    }

    private void saveData() {
        try {
            var file = new File(DATA_FILE);
            var oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(sleepRecords);
            oos.close();
        } catch (Exception e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            var file = new File(DATA_FILE);
            if (file.exists()) {
                var ois = new ObjectInputStream(new FileInputStream(file));
                var loadedRecords = (List<SleepRecord>) ois.readObject();
                sleepRecords.clear();
                sleepRecords.addAll(loadedRecords);
                ois.close();
                updateTable();
                updateDisplay();
                statusLabel.setText("数据已加载");
            }
        } catch (Exception e) {
            System.err.println("加载数据失败: " + e.getMessage());
        }
    }

    private void saveDataToFile(File file) throws IOException {
        var oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(sleepRecords);
        oos.close();
    }

    private void loadDataFromFile(File file) throws IOException, ClassNotFoundException {
        var ois = new ObjectInputStream(new FileInputStream(file));
        var loadedRecords = (List<SleepRecord>) ois.readObject();
        sleepRecords.clear();
        sleepRecords.addAll(loadedRecords);
        ois.close();
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

    private JButton createDangerButton(String text) {
        return this.createStyledButton(text, RED, WHITE);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        var button = new JButton(text);
        button.setFont(BODY);
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

    @Override
    public void dispose() {
        if (reminderTimer != null) reminderTimer.cancel();
        super.dispose();
    }
}

// 睡眠记录类
static class SleepRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    LocalDate date;
    LocalTime bedTime;
    LocalTime wakeTime;
    String quality;
    String notes;

    SleepRecord(LocalDate date, LocalTime bedTime, LocalTime wakeTime, String quality, String notes) {
        this.date = date;
        this.bedTime = bedTime;
        this.wakeTime = wakeTime;
        this.quality = quality;
        this.notes = notes;
    }
}

// 睡眠分析类
static class SleepAnalysis implements Serializable {
    private static final long serialVersionUID = 1L;
    double averageDuration;
    String averageQuality;
    double averageEfficiency;
    double sleepRegularity;
    int sleepDebtHours;
    int sleepDebtMinutes;
    String bestSleep;
    String recommendations;
}