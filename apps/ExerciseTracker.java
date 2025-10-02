import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.*;
import java.util.List;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🏃 运动记录";

    // 界面标签
    static final String EXERCISE_NAME_LABEL = "运动名称:";
    static final String TYPE_LABEL = "类型:";
    static final String DURATION_LABEL = "时长(分钟):";
    static final String INTENSITY_LABEL = "强度:";
    static final String CALORIES_LABEL = "卡路里:";
    static final String NOTES_LABEL = "备注:";
    static final String ADD_BUTTON = "添加记录";
    static final String DELETE_BUTTON = "删除记录";
    static final String SAVE_BUTTON = "保存数据";
    static final String LOAD_BUTTON = "加载数据";

    // 表格列名
    static final String[] TABLE_COLUMNS = {"日期", "运动名称", "类型", "时长", "强度", "卡路里", "备注"};
    static final String[] HISTORY_COLUMNS = {"日期", "运动次数", "总时长", "总卡路里"};

    // 状态消息
    static final String STATUS_RECORD_ADDED = "运动记录已添加";
    static final String STATUS_RECORD_DELETED = "运动记录已删除";
    static final String STATUS_DATA_SAVED = "数据已保存";
    static final String STATUS_DATA_LOADED = "数据已加载";
    static final String STATUS_GOAL_UPDATED = "目标已更新";

    // 帮助信息
    static final String HELP_MESSAGE = """
        运动记录应用使用说明：

        • 功能概述：记录和管理您的运动数据，追踪健身进度
        • 数据安全：支持本地数据保存和加载功能
        • 目标管理：可设置周运动目标并追踪完成度

        使用说明：
        • 添加记录：输入运动信息，点击添加记录
        • 删除记录：选择表格中的记录，点击删除
        • 数据管理：使用保存/加载功能备份数据
        • 目标设置：在目标面板设置周运动目标

        功能特点：
        • 支持多种运动类型和强度等级
        • 自动计算每日和每周统计数据
        • 可视化进度条显示目标完成度
        • 支持运动历史记录查询

        快捷键：
        Ctrl+S - 保存数据
        Ctrl+L - 加载数据
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new ExerciseTracker().setVisible(true);
    });
}

static class ExerciseTracker extends JFrame {
    private JTable exerciseTable;
    private DefaultTableModel tableModel;
    private JTextField exerciseNameField, durationField, caloriesField;
    private JComboBox<String> typeCombo, intensityCombo, exerciseTypeCombo;
    private JTextArea notesArea;
    private JLabel statusLabel;
    private JLabel todayStatsLabel;
    private JLabel weeklyStatsLabel;
    private JProgressBar weeklyProgressBar;
    private JCheckBox goalCheckBox;
    private JSpinner goalDurationSpinner, goalCaloriesSpinner;
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private JComboBox<String> dateRangeCombo;

    private List<Exercise> exercises = new ArrayList<>();
    private Map<LocalDate, List<Exercise>> dailyExercises = new HashMap<>();
    private Map<LocalDate, int[]> dailyHistory = new HashMap<>();
    private static final String[] EXERCISE_TYPES = {"跑步", "步行", "游泳", "骑行", "健身", "瑜伽", "球类", "其他"};
    private static final String[] INTENSITY_LEVELS = {"低", "中", "高"};
    private static final String[] DATE_RANGES = {"今天", "本周", "本月", "全部"};
    private static final String DATA_FILE = "exercise_tracker.dat";

    private int weeklyGoalDuration = 300; // 分钟
    private int weeklyGoalCalories = 2000; // 卡路里

    public ExerciseTracker() {
        initializeGUI();
        loadData();
        updateDisplay();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle("运动记录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("🏃 运动记录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        // 统计面板
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 10));
        statsPanel.setBackground(new Color(245, 247, 250));
        statsPanel.setBorder(BorderFactory.createTitledBorder("运动统计"));

        todayStatsLabel = new JLabel("今日: 0分钟/0卡路里", SwingConstants.CENTER);
        todayStatsLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        todayStatsLabel.setForeground(new Color(39, 174, 96));

        weeklyStatsLabel = new JLabel("本周: 0分钟/0卡路里", SwingConstants.CENTER);
        weeklyStatsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        weeklyStatsLabel.setForeground(new Color(127, 140, 141));

        JLabel exerciseCountLabel = new JLabel("运动次数: 0", SwingConstants.CENTER);
        exerciseCountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exerciseCountLabel.setForeground(new Color(127, 140, 141));

        weeklyProgressBar = new JProgressBar(0, 100);
        weeklyProgressBar.setStringPainted(true);
        weeklyProgressBar.setString("周目标进度");
        weeklyProgressBar.setForeground(new Color(46, 204, 113));

        statsPanel.add(todayStatsLabel);
        statsPanel.add(weeklyStatsLabel);
        statsPanel.add(exerciseCountLabel);
        statsPanel.add(weeklyProgressBar);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // 左侧面板 - 运动输入
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("添加运动"));
        leftPanel.setBackground(new Color(245, 247, 250));

        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBackground(new Color(245, 247, 250));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        exerciseNameField = new JTextField();
        exerciseNameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        durationField = new JTextField("30");
        durationField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        caloriesField = new JTextField("200");
        caloriesField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        typeCombo = new JComboBox<>(EXERCISE_TYPES);
        intensityCombo = new JComboBox<>(INTENSITY_LEVELS);

        inputPanel.add(new JLabel("运动名称:"));
        inputPanel.add(exerciseNameField);
        inputPanel.add(new JLabel("运动类型:"));
        inputPanel.add(typeCombo);
        inputPanel.add(new JLabel("时长(分钟):"));
        inputPanel.add(durationField);
        inputPanel.add(new JLabel("强度:"));
        inputPanel.add(intensityCombo);
        inputPanel.add(new JLabel("消耗卡路里:"));
        inputPanel.add(caloriesField);

        // 备注区域
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBackground(new Color(245, 247, 250));
        notesPanel.setBorder(BorderFactory.createTitledBorder("备注"));

        notesArea = new JTextArea(3, 20);
        notesArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addButton = createButton("➕ 添加", new Color(39, 174, 96), e -> addExercise());
        JButton editButton = createButton("✏️ 编辑", new Color(52, 152, 219), e -> editExercise());
        JButton deleteButton = createButton("🗑️ 删除", new Color(231, 76, 60), e -> deleteExercise());
        JButton goalButton = createButton("🎯 目标设置", new Color(241, 196, 15), e -> setGoals());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(goalButton);

        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(notesPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 右侧面板 - 运动列表和历史
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("运动记录"));
        rightPanel.setBackground(new Color(245, 247, 250));

        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(new Color(245, 247, 250));
        filterPanel.setBorder(BorderFactory.createTitledBorder("筛选"));

        dateRangeCombo = new JComboBox<>(DATE_RANGES);
        dateRangeCombo.setSelectedIndex(0);
        var typeOptions = new String[EXERCISE_TYPES.length + 1];
        typeOptions[0] = "全部";
        System.arraycopy(EXERCISE_TYPES, 0, typeOptions, 1, EXERCISE_TYPES.length);
        var typeFilterCombo = new JComboBox<>(typeOptions);
        var intensityOptions = new String[INTENSITY_LEVELS.length + 1];
        intensityOptions[0] = "全部";
        System.arraycopy(INTENSITY_LEVELS, 0, intensityOptions, 1, INTENSITY_LEVELS.length);
        var intensityFilterCombo = new JComboBox<>(intensityOptions);

        filterPanel.add(new JLabel("时间范围:"));
        filterPanel.add(dateRangeCombo);
        filterPanel.add(new JLabel("运动类型:"));
        filterPanel.add(typeFilterCombo);
        filterPanel.add(new JLabel("强度:"));
        filterPanel.add(intensityFilterCombo);

        // 添加筛选监听器
        dateRangeCombo.addActionListener(e -> filterExercises());
        typeFilterCombo.addActionListener(e -> filterExercises());
        intensityFilterCombo.addActionListener(e -> filterExercises());

        // 运动表格
        String[] columnNames = {"日期", "运动名称", "类型", "时长(分钟)", "强度", "卡路里", "备注"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        exerciseTable = new JTable(tableModel);
        exerciseTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        exerciseTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        exerciseTable.setRowHeight(25);
        exerciseTable.setSelectionBackground(new Color(173, 216, 230));

        // 设置列宽
        exerciseTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        exerciseTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        exerciseTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        exerciseTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        exerciseTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        exerciseTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        exerciseTable.getColumnModel().getColumn(6).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(exerciseTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部面板 - 目标设置和历史统计
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("目标与统计"));
        bottomPanel.setBackground(new Color(245, 247, 250));

        // 目标面板
        JPanel goalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        goalPanel.setBackground(new Color(245, 247, 250));
        goalPanel.setBorder(BorderFactory.createTitledBorder("周目标"));

        goalCheckBox = new JCheckBox("启用目标");
        goalCheckBox.setSelected(true);
        goalDurationSpinner = new JSpinner(new SpinnerNumberModel(300, 30, 1440, 30));
        goalCaloriesSpinner = new JSpinner(new SpinnerNumberModel(2000, 100, 10000, 100));

        goalPanel.add(goalCheckBox);
        goalPanel.add(new JLabel("目标时长(分钟):"));
        goalPanel.add(goalDurationSpinner);
        goalPanel.add(new JLabel("目标卡路里:"));
        goalPanel.add(goalCaloriesSpinner);

        // 历史统计表格
        String[] historyColumns = {"日期", "总时长(分钟)", "总卡路里", "达标"};
        historyModel = new DefaultTableModel(historyColumns, 0);
        historyTable = new JTable(historyModel);
        historyTable.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        historyTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 11));
        historyTable.setRowHeight(20);

        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setPreferredSize(new Dimension(400, 150));
        historyScrollPane.setBorder(BorderFactory.createTitledBorder("历史统计"));

        bottomPanel.add(goalPanel, BorderLayout.WEST);
        bottomPanel.add(historyScrollPane, BorderLayout.CENTER);

        // 状态栏
        statusLabel = new JLabel("准备就绪");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(127, 140, 141));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 组装界面
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        splitPane.setOneTouchExpandable(true);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.PAGE_END);

        setSize(1000, 800);
    }

    private JButton createButton(String text, Color bgColor, java.util.function.Consumer<ActionEvent> action) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action::accept);
        return button;
    }

    private void addExercise() {
        String name = exerciseNameField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String intensity = (String) intensityCombo.getSelectedItem();
        String notes = notesArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "运动名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int duration = Integer.parseInt(durationField.getText().trim());
            int calories = Integer.parseInt(caloriesField.getText().trim());

            if (duration <= 0 || calories < 0) {
                JOptionPane.showMessageDialog(this, "时长和卡路里必须为正数", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Exercise exercise = new Exercise(name, type, duration, intensity, calories, notes);
            exercises.add(exercise);

            LocalDate today = LocalDate.now();
            if (!dailyExercises.containsKey(today)) {
                dailyExercises.put(today, new ArrayList<>());
            }
            dailyExercises.get(today).add(exercise);

            tableModel.addRow(new Object[]{
                today.format(DateTimeFormatter.ofPattern("MM-dd")),
                name, type, duration, intensity, calories, notes
            });

            updateDisplay();
            updateHistory();
            saveData();

            // 清空输入
            exerciseNameField.setText("");
            durationField.setText("30");
            caloriesField.setText("200");
            notesArea.setText("");

            statusLabel.setText("成功添加运动记录");
            statusLabel.setForeground(new Color(39, 174, 96));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editExercise() {
        int selectedRow = exerciseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的运动记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel editPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField editNameField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField editDurationField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        JTextField editCaloriesField = new JTextField(tableModel.getValueAt(selectedRow, 5).toString());
        JTextArea editNotesArea = new JTextArea((String) tableModel.getValueAt(selectedRow, 6));
        JComboBox<String> editTypeCombo = new JComboBox<>(EXERCISE_TYPES);
        editTypeCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 2));
        JComboBox<String> editIntensityCombo = new JComboBox<>(INTENSITY_LEVELS);
        editIntensityCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 4));

        editPanel.add(new JLabel("运动名称:"));
        editPanel.add(editNameField);
        editPanel.add(new JLabel("运动类型:"));
        editPanel.add(editTypeCombo);
        editPanel.add(new JLabel("时长(分钟):"));
        editPanel.add(editDurationField);
        editPanel.add(new JLabel("强度:"));
        editPanel.add(editIntensityCombo);
        editPanel.add(new JLabel("消耗卡路里:"));
        editPanel.add(editCaloriesField);
        editPanel.add(new JLabel("备注:"));
        editPanel.add(new JScrollPane(editNotesArea));

        int result = JOptionPane.showConfirmDialog(this, editPanel, "编辑运动记录", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = editNameField.getText().trim();
                int newDuration = Integer.parseInt(editDurationField.getText().trim());
                int newCalories = Integer.parseInt(editCaloriesField.getText().trim());
                String newType = (String) editTypeCombo.getSelectedItem();
                String newIntensity = (String) editIntensityCombo.getSelectedItem();
                String newNotes = editNotesArea.getText().trim();

                if (newName.isEmpty() || newDuration <= 0 || newCalories < 0) {
                    JOptionPane.showMessageDialog(this, "输入数据无效", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 更新表格
                tableModel.setValueAt(newName, selectedRow, 1);
                tableModel.setValueAt(newType, selectedRow, 2);
                tableModel.setValueAt(newDuration, selectedRow, 3);
                tableModel.setValueAt(newIntensity, selectedRow, 4);
                tableModel.setValueAt(newCalories, selectedRow, 5);
                tableModel.setValueAt(newNotes, selectedRow, 6);

                // 更新内存数据
                updateExerciseData(selectedRow, newName, newType, newDuration, newIntensity, newCalories, newNotes);

                updateDisplay();
                updateHistory();
                saveData();

                statusLabel.setText("成功更新运动记录");
                statusLabel.setForeground(new Color(39, 174, 96));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteExercise() {
        int selectedRow = exerciseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的运动记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String exerciseName = (String) tableModel.getValueAt(selectedRow, 1);
        int result = JOptionPane.showConfirmDialog(this,
                String.format("确定要删除运动记录 '%s' 吗？", exerciseName),
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            exercises.remove(selectedRow);
            tableModel.removeRow(selectedRow);

            updateDisplay();
            updateHistory();
            saveData();

            statusLabel.setText("成功删除运动记录");
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    private void setGoals() {
        JPanel goalPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        goalPanel.setBackground(new Color(245, 247, 250));

        JTextField durationField = new JTextField(String.valueOf(weeklyGoalDuration));
        JTextField caloriesField = new JTextField(String.valueOf(weeklyGoalCalories));
        JCheckBox enableGoalsCheckBox = new JCheckBox("启用目标");
        enableGoalsCheckBox.setSelected(goalCheckBox.isSelected());

        goalPanel.add(new JLabel("周目标时长(分钟):"));
        goalPanel.add(durationField);
        goalPanel.add(new JLabel("周目标卡路里:"));
        goalPanel.add(caloriesField);
        goalPanel.add(enableGoalsCheckBox);

        int result = JOptionPane.showConfirmDialog(this, goalPanel, "设置目标", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int newDuration = Integer.parseInt(durationField.getText().trim());
                int newCalories = Integer.parseInt(caloriesField.getText().trim());

                if (newDuration > 0 && newCalories > 0) {
                    weeklyGoalDuration = newDuration;
                    weeklyGoalCalories = newCalories;
                    goalCheckBox.setSelected(enableGoalsCheckBox.isSelected());

                    updateDisplay();
                    saveData();

                    statusLabel.setText("目标设置已更新");
                    statusLabel.setForeground(new Color(39, 174, 96));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterExercises() {
        String selectedRange = (String) dateRangeCombo.getSelectedItem();
        String selectedType = (String) exerciseTypeCombo.getSelectedItem();
        String selectedIntensity = (String) intensityCombo.getSelectedItem();

        tableModel.setRowCount(0);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today;
        LocalDate endDate = today;

        switch (selectedRange) {
            case "今天":
                startDate = today;
                endDate = today;
                break;
            case "本周":
                startDate = today.with(java.time.DayOfWeek.MONDAY);
                endDate = today.with(java.time.DayOfWeek.SUNDAY);
                break;
            case "本月":
                startDate = today.withDayOfMonth(1);
                endDate = today.withDayOfMonth(today.lengthOfMonth());
                break;
            case "全部":
                startDate = LocalDate.MIN;
                endDate = LocalDate.MAX;
                break;
        }

        for (Exercise exercise : exercises) {
            boolean matchesType = "全部".equals(selectedType) || exercise.type.equals(selectedType);
            boolean matchesIntensity = "全部".equals(selectedIntensity) || exercise.intensity.equals(selectedIntensity);

            if (matchesType && matchesIntensity) {
                tableModel.addRow(new Object[]{
                    exercise.date.format(DateTimeFormatter.ofPattern("MM-dd")),
                    exercise.name, exercise.type, exercise.duration,
                    exercise.intensity, exercise.calories, exercise.notes
                });
            }
        }
    }

    private void updateExerciseData(int row, String name, String type, int duration, String intensity, int calories, String notes) {
        Exercise exercise = exercises.get(row);
        exercise.name = name;
        exercise.type = type;
        exercise.duration = duration;
        exercise.intensity = intensity;
        exercise.calories = calories;
        exercise.notes = notes;
    }

    private void updateDisplay() {
        // 计算今日统计
        LocalDate today = LocalDate.now();
        int todayDuration = 0;
        int todayCalories = 0;

        if (dailyExercises.containsKey(today)) {
            for (Exercise exercise : dailyExercises.get(today)) {
                todayDuration += exercise.duration;
                todayCalories += exercise.calories;
            }
        }

        todayStatsLabel.setText(String.format("今日: %d分钟/%d卡路里", todayDuration, todayCalories));

        // 计算本周统计
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
        int weekDuration = 0;
        int weekCalories = 0;

        for (Map.Entry<LocalDate, List<Exercise>> entry : dailyExercises.entrySet()) {
            LocalDate date = entry.getKey();
            if (!date.isBefore(weekStart) && !date.isAfter(today)) {
                for (Exercise exercise : entry.getValue()) {
                    weekDuration += exercise.duration;
                    weekCalories += exercise.calories;
                }
            }
        }

        weeklyStatsLabel.setText(String.format("本周: %d分钟/%d卡路里", weekDuration, weekCalories));

        // 更新目标进度
        if (goalCheckBox.isSelected()) {
            int durationProgress = Math.min(100, (weekDuration * 100) / weeklyGoalDuration);
            int caloriesProgress = Math.min(100, (weekCalories * 100) / weeklyGoalCalories);
            int overallProgress = (durationProgress + caloriesProgress) / 2;

            weeklyProgressBar.setValue(overallProgress);
            weeklyProgressBar.setString(String.format("周目标 %d%%", overallProgress));
        } else {
            weeklyProgressBar.setValue(0);
            weeklyProgressBar.setString("未启用目标");
        }

        // 更新运动次数
        JPanel statsPanel = (JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1);
        ((JLabel) statsPanel.getComponent(2)).setText("运动次数: " + exercises.size());
    }

    private void updateHistory() {
        historyModel.setRowCount(0);
        Map<LocalDate, int[]> weeklyData = new TreeMap<>(Collections.reverseOrder());

        for (Exercise exercise : exercises) {
            LocalDate date = exercise.date;
            if (!weeklyData.containsKey(date)) {
                weeklyData.put(date, new int[]{0, 0}); // [duration, calories]
            }
            int[] data = weeklyData.get(date);
            data[0] += exercise.duration;
            data[1] += exercise.calories;
        }

        int count = 0;
        for (Map.Entry<LocalDate, int[]> entry : weeklyData.entrySet()) {
            if (count >= 7) break; // 只显示最近7天

            LocalDate date = entry.getKey();
            int[] data = entry.getValue();
            boolean goalReached = goalCheckBox.isSelected() &&
                    data[0] >= weeklyGoalDuration / 7 && data[1] >= weeklyGoalCalories / 7;

            historyModel.addRow(new Object[]{
                date.format(DateTimeFormatter.ofPattern("MM-dd")),
                data[0] + "分钟",
                data[1] + "卡路里",
                goalReached ? "✅" : "❌"
            });
            count++;
        }
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(exercises);
            out.writeObject(dailyExercises);
            out.writeInt(weeklyGoalDuration);
            out.writeInt(weeklyGoalCalories);
            out.writeObject(dailyHistory);
        } catch (IOException e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            exercises = (List<Exercise>) in.readObject();
            dailyExercises = (Map<LocalDate, List<Exercise>>) in.readObject();
            weeklyGoalDuration = in.readInt();
            weeklyGoalCalories = in.readInt();
            dailyHistory = (Map<LocalDate, int[]>) in.readObject();

            // 填充表格
            tableModel.setRowCount(0);
            for (Exercise exercise : exercises) {
                tableModel.addRow(new Object[]{
                    exercise.date.format(DateTimeFormatter.ofPattern("MM-dd")),
                    exercise.name, exercise.type, exercise.duration,
                    exercise.intensity, exercise.calories, exercise.notes
                });
            }

            updateDisplay();
            updateHistory();
        } catch (Exception e) {
            System.err.println("加载数据失败: " + e.getMessage());
        }
    }
}

static class Exercise implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    String type;
    int duration;
    String intensity;
    int calories;
    String notes;
    LocalDate date;
    LocalTime time;

    Exercise(String name, String type, int duration, String intensity, int calories, String notes) {
        this.name = name;
        this.type = type;
        this.duration = duration;
        this.intensity = intensity;
        this.calories = calories;
        this.notes = notes;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }
}

class DailyRecord {
    LocalDate date;
    int totalDuration;
    int totalCalories;
    int exerciseCount;

    DailyRecord(LocalDate date) {
        this.date = date;
        this.totalDuration = 0;
        this.totalCalories = 0;
        this.exerciseCount = 0;
    }
}