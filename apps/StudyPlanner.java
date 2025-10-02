import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new StudyPlanner().setVisible(true);
    });
}

static class StudyPlanner extends JFrame {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField subjectField, taskField;
    private JComboBox<String> priorityCombo;
    private JSpinner startTimeSpinner, endTimeSpinner;
    private JCheckBox completedCheckBox;
    private JLabel statusLabel;
    private JLabel todayProgressLabel;
    private JProgressBar overallProgressBar;
    private JComboBox<String> subjectFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JTable scheduleTable;
    private DefaultTableModel scheduleModel;

    private List<StudyTask> tasks = new ArrayList<>();
    private Map<String, List<StudyTask>> subjectTasks = new HashMap<>();
    private static final String[] PRIORITIES = {"高", "中", "低"};
    private static final String[] SUBJECTS = {"数学", "语文", "英语", "物理", "化学", "生物", "历史", "地理", "政治", "其他"};
    private static final String DATA_FILE = "study_planner.dat";

    public StudyPlanner() {
        initializeGUI();
        loadData();
        updateDisplay();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle("学习计划");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("📖 学习计划", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        // 统计面板
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        statsPanel.setBackground(new Color(245, 247, 250));
        statsPanel.setBorder(BorderFactory.createTitledBorder("今日统计"));

        todayProgressLabel = new JLabel("今日完成: 0/0", SwingConstants.CENTER);
        todayProgressLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        todayProgressLabel.setForeground(new Color(39, 174, 96));

        JLabel totalTasksLabel = new JLabel("总任务: 0", SwingConstants.CENTER);
        totalTasksLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        totalTasksLabel.setForeground(new Color(127, 140, 141));

        overallProgressBar = new JProgressBar(0, 100);
        overallProgressBar.setStringPainted(true);
        overallProgressBar.setString("总体进度");
        overallProgressBar.setForeground(new Color(46, 204, 113));

        statsPanel.add(todayProgressLabel);
        statsPanel.add(totalTasksLabel);
        statsPanel.add(overallProgressBar);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // 左侧面板 - 任务管理
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("任务管理"));
        leftPanel.setBackground(new Color(245, 247, 250));

        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBackground(new Color(245, 247, 250));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        subjectField = new JTextField();
        subjectField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        taskField = new JTextField();
        taskField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        priorityCombo = new JComboBox<>(PRIORITIES);

        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startEditor);

        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endEditor);

        completedCheckBox = new JCheckBox("已完成");

        inputPanel.add(new JLabel("科目:"));
        inputPanel.add(subjectField);
        inputPanel.add(new JLabel("任务:"));
        inputPanel.add(taskField);
        inputPanel.add(new JLabel("优先级:"));
        inputPanel.add(priorityCombo);
        inputPanel.add(new JLabel("开始时间:"));
        inputPanel.add(startTimeSpinner);
        inputPanel.add(new JLabel("结束时间:"));
        inputPanel.add(endTimeSpinner);
        inputPanel.add(completedCheckBox);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addButton = createButton("➕ 添加", new Color(39, 174, 96), e -> addTask());
        JButton editButton = createButton("✏️ 编辑", new Color(52, 152, 219), e -> editTask());
        JButton deleteButton = createButton("🗑️ 删除", new Color(231, 76, 60), e -> deleteTask());
        JButton completeButton = createButton("✅ 完成", new Color(241, 196, 15), e -> completeTask());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);

        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(buttonPanel, BorderLayout.CENTER);

        // 右侧面板 - 任务列表和筛选
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("任务列表"));
        rightPanel.setBackground(new Color(245, 247, 250));

        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(new Color(245, 247, 250));
        filterPanel.setBorder(BorderFactory.createTitledBorder("筛选"));

        var subjectOptions = new String[SUBJECTS.length + 1];
        subjectOptions[0] = "全部";
        System.arraycopy(SUBJECTS, 0, subjectOptions, 1, SUBJECTS.length);
        subjectFilterCombo = new JComboBox<>(subjectOptions);
        statusFilterCombo = new JComboBox<>(new String[]{"全部", "未完成", "已完成"});
        var priorityOptions = new String[PRIORITIES.length + 1];
        priorityOptions[0] = "全部";
        System.arraycopy(PRIORITIES, 0, priorityOptions, 1, PRIORITIES.length);
        var priorityFilterCombo = new JComboBox<>(priorityOptions);

        filterPanel.add(new JLabel("科目:"));
        filterPanel.add(subjectFilterCombo);
        filterPanel.add(new JLabel("状态:"));
        filterPanel.add(statusFilterCombo);
        filterPanel.add(new JLabel("优先级:"));
        filterPanel.add(priorityFilterCombo);

        // 添加筛选监听器
        subjectFilterCombo.addActionListener(e -> filterTasks());
        statusFilterCombo.addActionListener(e -> filterTasks());
        priorityFilterCombo.addActionListener(e -> filterTasks());

        // 任务表格
        String[] columnNames = {"科目", "任务", "优先级", "开始时间", "结束时间", "状态", "进度"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        taskTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        taskTable.setRowHeight(25);
        taskTable.setSelectionBackground(new Color(173, 216, 230));

        // 设置列宽
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        taskTable.getColumnModel().getColumn(6).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部面板 - 时间表格
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("今日时间安排"));
        bottomPanel.setBackground(new Color(245, 247, 250));

        // 时间表格
        String[] timeColumns = {"时间", "任务", "科目"};
        scheduleModel = new DefaultTableModel(timeColumns, 0);
        scheduleTable = new JTable(scheduleModel);
        scheduleTable.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        scheduleTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 11));
        scheduleTable.setRowHeight(20);

        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        scheduleScrollPane.setPreferredSize(new Dimension(400, 200));

        bottomPanel.add(scheduleScrollPane, BorderLayout.CENTER);

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

        setSize(1200, 800);
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

    private void addTask() {
        String subject = subjectField.getText().trim();
        String task = taskField.getText().trim();
        String priority = (String) priorityCombo.getSelectedItem();
        boolean completed = completedCheckBox.isSelected();

        if (subject.isEmpty() || task.isEmpty()) {
            JOptionPane.showMessageDialog(this, "科目和任务不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date startTime = (Date) startTimeSpinner.getValue();
        Date endTime = (Date) endTimeSpinner.getValue();

        if (startTime.after(endTime)) {
            JOptionPane.showMessageDialog(this, "开始时间不能晚于结束时间", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StudyTask newTask = new StudyTask(subject, task, priority, startTime, endTime, completed);
        tasks.add(newTask);

        if (!subjectTasks.containsKey(subject)) {
            subjectTasks.put(subject, new ArrayList<>());
        }
        subjectTasks.get(subject).add(newTask);

        tableModel.addRow(new Object[]{
            subject, task, priority,
            formatTime(startTime), formatTime(endTime),
            completed ? "已完成" : "未完成",
            getProgressText(newTask)
        });

        updateDisplay();
        updateSchedule();
        saveData();

        // 清空输入
        subjectField.setText("");
        taskField.setText("");
        completedCheckBox.setSelected(false);

        statusLabel.setText("成功添加任务");
        statusLabel.setForeground(new Color(39, 174, 96));
    }

    private void editTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的任务", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudyTask task = tasks.get(selectedRow);
        if (task == null) return;

        JPanel editPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField editSubjectField = new JTextField(task.subject);
        JTextField editTaskField = new JTextField(task.task);
        JComboBox<String> editPriorityCombo = new JComboBox<>(PRIORITIES);
        editPriorityCombo.setSelectedItem(task.priority);
        JCheckBox editCompletedCheckBox = new JCheckBox("已完成");
        editCompletedCheckBox.setSelected(task.completed);

        editPanel.add(new JLabel("科目:"));
        editPanel.add(editSubjectField);
        editPanel.add(new JLabel("任务:"));
        editPanel.add(editTaskField);
        editPanel.add(new JLabel("优先级:"));
        editPanel.add(editPriorityCombo);
        editPanel.add(editCompletedCheckBox);

        int result = JOptionPane.showConfirmDialog(this, editPanel, "编辑任务", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newSubject = editSubjectField.getText().trim();
            String newTask = editTaskField.getText().trim();

            if (newSubject.isEmpty() || newTask.isEmpty()) {
                JOptionPane.showMessageDialog(this, "科目和任务不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            task.subject = newSubject;
            task.task = newTask;
            task.priority = (String) editPriorityCombo.getSelectedItem();
            task.completed = editCompletedCheckBox.isSelected();

            tableModel.setValueAt(newSubject, selectedRow, 0);
            tableModel.setValueAt(newTask, selectedRow, 1);
            tableModel.setValueAt(task.priority, selectedRow, 2);
            tableModel.setValueAt(task.completed ? "已完成" : "未完成", selectedRow, 5);
            tableModel.setValueAt(getProgressText(task), selectedRow, 6);

            updateDisplay();
            updateSchedule();
            saveData();

            statusLabel.setText("成功更新任务");
            statusLabel.setForeground(new Color(39, 174, 96));
        }
    }

    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的任务", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudyTask task = tasks.get(selectedRow);
        int result = JOptionPane.showConfirmDialog(this,
                String.format("确定要删除任务 '%s' 吗？", task.task),
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            tasks.remove(selectedRow);
            subjectTasks.get(task.subject).remove(task);
            tableModel.removeRow(selectedRow);

            updateDisplay();
            updateSchedule();
            saveData();

            statusLabel.setText("成功删除任务");
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    private void completeTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要完成的任务", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudyTask task = tasks.get(selectedRow);
        task.completed = true;
        tableModel.setValueAt("已完成", selectedRow, 5);
        tableModel.setValueAt(getProgressText(task), selectedRow, 6);

        updateDisplay();
        updateSchedule();
        saveData();

        statusLabel.setText("恭喜完成任务！");
        statusLabel.setForeground(new Color(39, 174, 96));
        Toolkit.getDefaultToolkit().beep();
    }

    private void filterTasks() {
        String selectedSubject = (String) subjectFilterCombo.getSelectedItem();
        String selectedStatus = (String) statusFilterCombo.getSelectedItem();

        tableModel.setRowCount(0);

        for (StudyTask task : tasks) {
            boolean matchesSubject = "全部".equals(selectedSubject) || task.subject.equals(selectedSubject);
            boolean matchesStatus = "全部".equals(selectedStatus) ||
                    ("未完成".equals(selectedStatus) && !task.completed) ||
                    ("已完成".equals(selectedStatus) && task.completed);

            if (matchesSubject && matchesStatus) {
                tableModel.addRow(new Object[]{
                    task.subject, task.task, task.priority,
                    formatTime(task.startTime), formatTime(task.endTime),
                    task.completed ? "已完成" : "未完成",
                    getProgressText(task)
                });
            }
        }
    }

    private void updateSchedule() {
        scheduleModel.setRowCount(0);
        LocalDate today = LocalDate.now();

        // 按时间排序的任务
        List<StudyTask> todayTasks = new ArrayList<>();
        for (StudyTask task : tasks) {
            if (!task.completed && task.startTime.getTime() >= today.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                todayTasks.add(task);
            }
        }

        todayTasks.sort(Comparator.comparing(t -> t.startTime));

        // 生成时间段
        for (int hour = 8; hour <= 22; hour++) {
            String timeSlot = String.format("%02d:00-%02d:00", hour, hour + 1);
            String taskName = "";
            String subject = "";

            for (StudyTask task : todayTasks) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(task.startTime);
                int taskHour = cal.get(Calendar.HOUR_OF_DAY);

                if (taskHour == hour) {
                    taskName = task.task;
                    subject = task.subject;
                    break;
                }
            }

            scheduleModel.addRow(new Object[]{timeSlot, taskName, subject});
        }
    }

    private void updateDisplay() {
        // 计算今日完成数量
        int todayCompleted = 0;
        int todayTotal = 0;
        LocalDate today = LocalDate.now();

        for (StudyTask task : tasks) {
            if (task.startTime.getTime() >= today.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                todayTotal++;
                if (task.completed) todayCompleted++;
            }
        }

        todayProgressLabel.setText(String.format("今日完成: %d/%d", todayCompleted, todayTotal));

        // 更新总体进度
        int totalCompleted = 0;
        for (StudyTask task : tasks) {
            if (task.completed) totalCompleted++;
        }

        if (!tasks.isEmpty()) {
            int progress = (totalCompleted * 100) / tasks.size();
            overallProgressBar.setValue(progress);
            overallProgressBar.setString(String.format("总体进度 %d%%", progress));
        } else {
            overallProgressBar.setValue(0);
            overallProgressBar.setString("总体进度 0%");
        }

        // 更新总任务数
        JPanel statsPanel = (JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1);
        ((JLabel) statsPanel.getComponent(1)).setText("总任务: " + tasks.size());
    }

    private String formatTime(Date time) {
        return new java.text.SimpleDateFormat("HH:mm").format(time);
    }

    private String getProgressText(StudyTask task) {
        if (task.completed) return "100%";
        return "0%";
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(tasks);
            out.writeObject(subjectTasks);
        } catch (IOException e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            tasks = (List<StudyTask>) in.readObject();
            subjectTasks = (Map<String, List<StudyTask>>) in.readObject();

            // 填充表格
            tableModel.setRowCount(0);
            for (StudyTask task : tasks) {
                tableModel.addRow(new Object[]{
                    task.subject, task.task, task.priority,
                    formatTime(task.startTime), formatTime(task.endTime),
                    task.completed ? "已完成" : "未完成",
                    getProgressText(task)
                });
            }

            updateDisplay();
            updateSchedule();
        } catch (Exception e) {
            System.err.println("加载数据失败: " + e.getMessage());
        }
    }
}

static class StudyTask implements Serializable {
    private static final long serialVersionUID = 1L;
    String subject;
    String task;
    String priority;
    Date startTime;
    Date endTime;
    boolean completed;
    LocalDate createdDate;

    StudyTask(String subject, String task, String priority, Date startTime, Date endTime, boolean completed) {
        this.subject = subject;
        this.task = task;
        this.priority = priority;
        this.startTime = startTime;
        this.endTime = endTime;
        this.completed = completed;
        this.createdDate = LocalDate.now();
    }
}