import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new HabitTracker().setVisible(true));
}

static class HabitTracker extends JFrame {
    private JList<String> habitList;
    private DefaultListModel<String> habitListModel;
    private JTextField habitNameField;
    private JTextArea habitDescriptionArea;
    private JComboBox<String> frequencyCombo;
    private JComboBox<String> categoryCombo;
    private JCheckBox reminderCheckBox;
    private JSpinner reminderTimeSpinner;
    private JTable progressTable;
    private DefaultTableModel progressModel;
    private JLabel statusLabel;
    private JLabel completionRateLabel;
    private JProgressBar overallProgressBar;

    private Map<String, Habit> habits = new HashMap<>();
    private Map<String, List<Boolean>> habitProgress = new HashMap<>();
    private static final String[] CATEGORIES = {"健康", "学习", "工作", "生活", "运动", "其他"};
    private static final String[] FREQUENCIES = {"每天", "每周", "每月"};
    private static final String DATA_FILE = "habits.dat";

    public HabitTracker() {
        initializeGUI();
        loadData();
        updateDisplay();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle("习惯追踪器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("习惯追踪器", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 152, 219));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createButton("➕ 添加习惯", new Color(39, 174, 96), this::addHabit));
        buttonPanel.add(createButton("✏️ 编辑", new Color(243, 156, 18), this::editHabit));
        buttonPanel.add(createButton("🗑️ 删除", new Color(231, 76, 60), this::deleteHabit));
        buttonPanel.add(createButton("📊 统计", new Color(155, 89, 182), this::showStatistics));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 左侧面板
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("习惯列表"));

        habitListModel = new DefaultListModel<>();
        habitList = new JList<>(habitListModel);
        habitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        habitList.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        habitList.setBackground(new Color(248, 249, 250));
        habitList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateHabitDetails();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(habitList);
        listScrollPane.setPreferredSize(new Dimension(280, 400));

        // 进度显示
        JPanel progressPanel = new JPanel(new GridLayout(2, 1));
        completionRateLabel = new JLabel("完成率: 0%", SwingConstants.CENTER);
        completionRateLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        overallProgressBar = new JProgressBar();
        overallProgressBar.setStringPainted(true);
        overallProgressBar.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        progressPanel.add(completionRateLabel);
        progressPanel.add(overallProgressBar);

        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        leftPanel.add(progressPanel, BorderLayout.SOUTH);

        // 右侧面板
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("习惯详情"));

        // 习惯详情
        JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        habitNameField = new JTextField();
        habitNameField.setEditable(false);
        habitNameField.setFont(new Font("微软雅黑", Font.BOLD, 16));
        habitNameField.setBackground(new Color(248, 249, 250));

        habitDescriptionArea = new JTextArea(3, 20);
        habitDescriptionArea.setEditable(false);
        habitDescriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        habitDescriptionArea.setLineWrap(true);
        habitDescriptionArea.setWrapStyleWord(true);
        habitDescriptionArea.setBackground(new Color(248, 249, 250));

        frequencyCombo = new JComboBox<>(FREQUENCIES);
        frequencyCombo.setEnabled(false);
        frequencyCombo.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setEnabled(false);
        categoryCombo.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        reminderCheckBox = new JCheckBox("启用提醒");
        reminderCheckBox.setEnabled(false);
        reminderCheckBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        reminderTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(reminderTimeSpinner, "HH:mm");
        reminderTimeSpinner.setEditor(timeEditor);
        reminderTimeSpinner.setEnabled(false);
        reminderTimeSpinner.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        detailsPanel.add(new JLabel("习惯名称:"));
        detailsPanel.add(habitNameField);
        detailsPanel.add(new JLabel("描述:"));
        detailsPanel.add(new JScrollPane(habitDescriptionArea));
        detailsPanel.add(new JLabel("频率:"));
        detailsPanel.add(frequencyCombo);
        detailsPanel.add(new JLabel("类别:"));
        detailsPanel.add(categoryCombo);
        detailsPanel.add(new JLabel("提醒:"));
        detailsPanel.add(reminderCheckBox);
        detailsPanel.add(new JLabel("提醒时间:"));
        detailsPanel.add(reminderTimeSpinner);

        // 进度表格
        progressModel = new DefaultTableModel();
        progressModel.setColumnIdentifiers(new String[]{"日期", "完成状态"});
        progressTable = new JTable(progressModel);
        progressTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        progressTable.setRowHeight(25);
        progressTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        progressTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(progressTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 300));

        // 打卡按钮
        JPanel checkInPanel = new JPanel(new FlowLayout());
        JButton checkInButton = createButton("✅ 今日打卡", new Color(46, 204, 113), e -> checkInToday());
        checkInPanel.add(checkInButton);

        rightPanel.add(detailsPanel, BorderLayout.NORTH);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        rightPanel.add(checkInPanel, BorderLayout.SOUTH);

        // 状态栏
        statusLabel = new JLabel("选择一个习惯查看详情");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(127, 140, 141));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 组装界面
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        splitPane.setOneTouchExpandable(true);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(900, 700);
    }

    private JButton createButton(String text, Color bgColor, java.util.function.Consumer<ActionEvent> action) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.accept(e));
        return button;
    }

    private void addHabit(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        JComboBox<String> freqCombo = new JComboBox<>(FREQUENCIES);
        JComboBox<String> catCombo = new JComboBox<>(CATEGORIES);
        JCheckBox reminderBox = new JCheckBox("启用提醒");

        panel.add(new JLabel("习惯名称:"));
        panel.add(nameField);
        panel.add(new JLabel("描述:"));
        panel.add(new JScrollPane(descArea));
        panel.add(new JLabel("频率:"));
        panel.add(freqCombo);
        panel.add(new JLabel("类别:"));
        panel.add(catCombo);
        panel.add(reminderBox);

        int result = JOptionPane.showConfirmDialog(HabitTracker.this, panel, "添加新习惯", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(HabitTracker.this, "习惯名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Habit habit = new Habit(
                    name,
                    descArea.getText().trim(),
                    (String) freqCombo.getSelectedItem(),
                    (String) catCombo.getSelectedItem(),
                    reminderBox.isSelected()
            );

            habits.put(name, habit);
            habitListModel.addElement(name);
            habitProgress.put(name, new ArrayList<>());

            updateDisplay();
            saveData();

            statusLabel.setText("成功添加习惯: " + name);
            statusLabel.setForeground(new Color(39, 174, 96));
        }
    }

    private void editHabit(ActionEvent e) {
        String selectedHabit = habitList.getSelectedValue();
        if (selectedHabit == null) {
            JOptionPane.showMessageDialog(HabitTracker.this, "请选择一个习惯", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Habit habit = habits.get(selectedHabit);
        if (habit == null) return;

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField nameField = new JTextField(habit.name);
        JTextArea descArea = new JTextArea(habit.description, 3, 20);
        JComboBox<String> freqCombo = new JComboBox<>(FREQUENCIES);
        freqCombo.setSelectedItem(habit.frequency);
        JComboBox<String> catCombo = new JComboBox<>(CATEGORIES);
        catCombo.setSelectedItem(habit.category);

        panel.add(new JLabel("习惯名称:"));
        panel.add(nameField);
        panel.add(new JLabel("描述:"));
        panel.add(new JScrollPane(descArea));
        panel.add(new JLabel("频率:"));
        panel.add(freqCombo);
        panel.add(new JLabel("类别:"));
        panel.add(catCombo);

        int result = JOptionPane.showConfirmDialog(HabitTracker.this, panel, "编辑习惯", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(HabitTracker.this, "习惯名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 如果名称改变了，需要更新哈希表和列表
            if (!newName.equals(selectedHabit)) {
                habits.remove(selectedHabit);
                habitProgress.remove(selectedHabit);

                // 更新列表中的名称
                int selectedIndex = habitList.getSelectedIndex();
                habitListModel.setElementAt(newName, selectedIndex);

                habits.put(newName, new Habit(
                    newName,
                    descArea.getText().trim(),
                    (String) freqCombo.getSelectedItem(),
                    (String) catCombo.getSelectedItem(),
                    habits.get(selectedHabit).reminderEnabled
                ));
                habitProgress.put(newName, new ArrayList<>());
            } else {
                // 名称未变，只更新其他信息
                habit.description = descArea.getText().trim();
                habit.frequency = (String) freqCombo.getSelectedItem();
                habit.category = (String) catCombo.getSelectedItem();
            }

            updateDisplay();
            saveData();
            statusLabel.setText("习惯已更新: " + newName);
            statusLabel.setForeground(new Color(243, 156, 18));
        }
    }

    private void deleteHabit(ActionEvent e) {
        String selectedHabit = habitList.getSelectedValue();
        if (selectedHabit == null) {
            JOptionPane.showMessageDialog(HabitTracker.this, "请选择一个习惯", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(HabitTracker.this,
            "确定要删除习惯 '" + selectedHabit + "' 吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            habits.remove(selectedHabit);
            habitProgress.remove(selectedHabit);
            habitListModel.removeElement(selectedHabit);

            updateDisplay();
            saveData();
            statusLabel.setText("习惯已删除: " + selectedHabit);
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    private void showStatistics(ActionEvent e) {
        if (habits.isEmpty()) {
            JOptionPane.showMessageDialog(HabitTracker.this, "暂无习惯数据", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int totalHabits = habits.size();
        int completedToday = 0;
        double totalCompletionRate = 0;

        for (String habitName : habits.keySet()) {
            List<Boolean> progress = habitProgress.get(habitName);
            if (progress != null && !progress.isEmpty()) {
                if (progress.get(progress.size() - 1)) {
                    completedToday++;
                }

                long completed = progress.stream().filter(Boolean::booleanValue).count();
                totalCompletionRate += (double) completed / progress.size() * 100;
            }
        }

        double averageCompletionRate = totalCompletionRate / totalHabits;

        String stats = String.format(
            "习惯统计\n\n" +
            "总习惯数: %d\n" +
            "今日完成: %d\n" +
            "平均完成率: %.1f%%\n\n" +
            "坚持就是胜利！",
            totalHabits, completedToday, averageCompletionRate
        );

        JOptionPane.showMessageDialog(HabitTracker.this, stats, "统计信息", JOptionPane.INFORMATION_MESSAGE);
    }

    private void checkInToday() {
        String selectedHabit = habitList.getSelectedValue();
        if (selectedHabit == null) {
            JOptionPane.showMessageDialog(HabitTracker.this, "请选择一个习惯", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Boolean> progress = habitProgress.get(selectedHabit);
        if (progress == null) {
            progress = new ArrayList<>();
            habitProgress.put(selectedHabit, progress);
        }

        // 检查今天是否已经打卡
        if (!progress.isEmpty() && progress.get(progress.size() - 1)) {
            JOptionPane.showMessageDialog(HabitTracker.this, "今天已经打卡了！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        progress.add(true);
        updateDisplay();
        saveData();
        statusLabel.setText("打卡成功: " + selectedHabit);
        statusLabel.setForeground(new Color(46, 204, 113));
    }

    private void updateHabitDetails() {
        String selectedHabit = habitList.getSelectedValue();
        if (selectedHabit == null) {
            habitNameField.setText("");
            habitDescriptionArea.setText("");
            frequencyCombo.setSelectedIndex(0);
            categoryCombo.setSelectedIndex(0);
            reminderCheckBox.setSelected(false);
            progressModel.setRowCount(0);
            return;
        }

        Habit habit = habits.get(selectedHabit);
        if (habit != null) {
            habitNameField.setText(habit.name);
            habitDescriptionArea.setText(habit.description);
            frequencyCombo.setSelectedItem(habit.frequency);
            categoryCombo.setSelectedItem(habit.category);
            reminderCheckBox.setSelected(habit.reminderEnabled);

            // 更新进度表格
            updateProgressTable(selectedHabit);
        }
    }

    private void updateProgressTable(String habitName) {
        progressModel.setRowCount(0);
        List<Boolean> progress = habitProgress.get(habitName);
        if (progress != null) {
            for (int i = 0; i < progress.size(); i++) {
                String date = LocalDate.now().minusDays(progress.size() - i - 1).format(DateTimeFormatter.ofPattern("MM-dd"));
                String status = progress.get(i) ? "✅ 已完成" : "❌ 未完成";
                progressModel.addRow(new Object[]{date, status});
            }
        }
    }

    private void updateDisplay() {
        if (habits.isEmpty()) {
            completionRateLabel.setText("完成率: 0%");
            overallProgressBar.setValue(0);
            return;
        }

        double totalCompletionRate = 0;
        for (String habitName : habits.keySet()) {
            List<Boolean> progress = habitProgress.get(habitName);
            if (progress != null && !progress.isEmpty()) {
                long completed = progress.stream().filter(Boolean::booleanValue).count();
                totalCompletionRate += (double) completed / progress.size() * 100;
            }
        }

        double averageCompletionRate = totalCompletionRate / habits.size();
        completionRateLabel.setText(String.format("完成率: %.1f%%", averageCompletionRate));
        overallProgressBar.setValue((int) averageCompletionRate);
    }

    private void saveData() {
        try {
            var file = new File(DATA_FILE);
            var oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(habits);
            oos.writeObject(habitProgress);
            oos.close();
        } catch (Exception e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try {
            var file = new File(DATA_FILE);
            if (file.exists()) {
                var ois = new ObjectInputStream(new FileInputStream(file));
                habits = (Map<String, Habit>) ois.readObject();
                habitProgress = (Map<String, List<Boolean>>) ois.readObject();
                ois.close();

                // 更新列表
                habitListModel.clear();
                for (String habitName : habits.keySet()) {
                    habitListModel.addElement(habitName);
                }
            }
        } catch (Exception e) {
            System.err.println("加载数据失败: " + e.getMessage());
        }
    }
}

static class Habit implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    String description;
    String frequency;
    String category;
    boolean reminderEnabled;

    public Habit(String name, String description, String frequency, String category, boolean reminderEnabled) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.category = category;
        this.reminderEnabled = reminderEnabled;
    }
}