import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new ExamCountdown().setVisible(true);
    });
}

static class ExamCountdown extends JFrame {
    private JTable examTable;
    private DefaultTableModel tableModel;
    private JTextField examNameField, examDateField, examTimeField, locationField;
    private JTextArea notesArea;
    private JComboBox<String> subjectCombo;
    private JComboBox<String> importanceCombo;
    private JLabel countdownLabel;
    private JLabel statusLabel;
    private JProgressBar countdownProgressBar;
    private JCheckBox reminderCheckBox;
    private JSpinner reminderDaysSpinner;
    private javax.swing.Timer countdownTimer;
    private javax.swing.Timer clockTimer;

    private List<Exam> exams = new ArrayList<>();
    private Exam currentExam = null;
    private static final String[] SUBJECTS = {"数学", "语文", "英语", "物理", "化学", "生物", "历史", "地理", "政治", "其他"};
    private static final String[] IMPORTANCE_LEVELS = {"重要", "中等", "一般"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String DATA_FILE = "exams.dat";

    public ExamCountdown() {
        initializeGUI();
        loadData();
        startTimers();
        updateDisplay();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle("考试倒计时");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("⏰ 考试倒计时", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        // 倒计时面板
        JPanel countdownPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        countdownPanel.setBackground(new Color(245, 247, 250));
        countdownPanel.setBorder(BorderFactory.createTitledBorder("最近考试倒计时"));

        countdownLabel = new JLabel("暂无考试", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        countdownLabel.setForeground(new Color(231, 76, 60));

        countdownProgressBar = new JProgressBar(0, 100);
        countdownProgressBar.setStringPainted(true);
        countdownProgressBar.setString("进度");
        countdownProgressBar.setForeground(new Color(46, 204, 113));

        countdownPanel.add(countdownLabel);
        countdownPanel.add(countdownProgressBar);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(countdownPanel, BorderLayout.CENTER);

        // 左侧面板 - 考试输入
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("添加考试"));
        leftPanel.setBackground(new Color(245, 247, 250));

        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        inputPanel.setBackground(new Color(245, 247, 250));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        examNameField = new JTextField();
        examNameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        examDateField = new JTextField(LocalDate.now().plusDays(7).format(DATE_FORMATTER));
        examDateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        examTimeField = new JTextField("09:00");
        examTimeField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        locationField = new JTextField();
        locationField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subjectCombo = new JComboBox<>(SUBJECTS);
        importanceCombo = new JComboBox<>(IMPORTANCE_LEVELS);

        reminderCheckBox = new JCheckBox("启用提醒");
        reminderDaysSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 30, 1));

        inputPanel.add(new JLabel("考试名称:"));
        inputPanel.add(examNameField);
        inputPanel.add(new JLabel("科目:"));
        inputPanel.add(subjectCombo);
        inputPanel.add(new JLabel("考试日期:"));
        inputPanel.add(examDateField);
        inputPanel.add(new JLabel("考试时间:"));
        inputPanel.add(examTimeField);
        inputPanel.add(new JLabel("考试地点:"));
        inputPanel.add(locationField);
        inputPanel.add(new JLabel("重要程度:"));
        inputPanel.add(importanceCombo);
        inputPanel.add(reminderCheckBox);
        inputPanel.add(reminderDaysSpinner);

        // 备注区域
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBackground(new Color(245, 247, 250));
        notesPanel.setBorder(BorderFactory.createTitledBorder("备注"));

        notesArea = new JTextArea(4, 20);
        notesArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addButton = createButton("➕ 添加", new Color(39, 174, 96), e -> addExam());
        JButton editButton = createButton("✏️ 编辑", new Color(52, 152, 219), e -> editExam());
        JButton deleteButton = createButton("🗑️ 删除", new Color(231, 76, 60), e -> deleteExam());
        JButton clearButton = createButton("🔄 清空", new Color(243, 156, 18), e -> clearAll());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(notesPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 右侧面板 - 考试列表
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("考试列表"));
        rightPanel.setBackground(new Color(245, 247, 250));

        // 筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(new Color(245, 247, 250));
        filterPanel.setBorder(BorderFactory.createTitledBorder("筛选"));

        var subjectOptions = new String[SUBJECTS.length + 1];
        subjectOptions[0] = "全部";
        System.arraycopy(SUBJECTS, 0, subjectOptions, 1, SUBJECTS.length);
        var subjectFilterCombo = new JComboBox<>(subjectOptions);
        JComboBox<String> statusFilterCombo = new JComboBox<>(new String[]{"全部", "未开始", "进行中", "已结束"});
        JCheckBox showPastCheckBox = new JCheckBox("显示已过期");

        filterPanel.add(new JLabel("科目:"));
        filterPanel.add(subjectFilterCombo);
        filterPanel.add(new JLabel("状态:"));
        filterPanel.add(statusFilterCombo);
        filterPanel.add(showPastCheckBox);

        // 添加筛选监听器
        subjectFilterCombo.addActionListener(e -> filterExams());
        statusFilterCombo.addActionListener(e -> filterExams());
        showPastCheckBox.addActionListener(e -> filterExams());

        // 考试表格
        String[] columnNames = {"考试名称", "科目", "日期", "时间", "地点", "重要程度", "剩余天数", "状态"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        examTable = new JTable(tableModel);
        examTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        examTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        examTable.setRowHeight(25);
        examTable.setSelectionBackground(new Color(173, 216, 230));

        // 设置列宽
        examTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        examTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        examTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        examTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        examTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        examTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        examTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        examTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(examTable);
        scrollPane.setPreferredSize(new Dimension(700, 400));

        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

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

    private void addExam() {
        String examName = examNameField.getText().trim();
        String subject = (String) subjectCombo.getSelectedItem();
        String dateStr = examDateField.getText().trim();
        String timeStr = examTimeField.getText().trim();
        String location = locationField.getText().trim();
        String importance = (String) importanceCombo.getSelectedItem();
        boolean hasReminder = reminderCheckBox.isSelected();
        int reminderDays = (Integer) reminderDaysSpinner.getValue();
        String notes = notesArea.getText().trim();

        if (examName.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "考试名称、日期和时间不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate examDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            LocalTime examTime = LocalTime.parse(timeStr, TIME_FORMATTER);

            if (examDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "考试日期不能早于今天", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Exam exam = new Exam(examName, subject, examDate, examTime, location, importance, hasReminder, reminderDays, notes);
            exams.add(exam);

            addExamToTable(exam);
            updateDisplay();
            saveData();

            // 清空输入
            examNameField.setText("");
            examDateField.setText(LocalDate.now().plusDays(7).format(DATE_FORMATTER));
            examTimeField.setText("09:00");
            locationField.setText("");
            notesArea.setText("");
            reminderCheckBox.setSelected(false);

            statusLabel.setText("成功添加考试: " + examName);
            statusLabel.setForeground(new Color(39, 174, 96));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期或时间格式错误，请使用 yyyy-MM-dd 和 HH:mm 格式", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editExam() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的考试", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Exam exam = exams.get(selectedRow);
        if (exam == null) return;

        JPanel editPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField editNameField = new JTextField(exam.name);
        JTextField editDateField = new JTextField(exam.date.format(DATE_FORMATTER));
        JTextField editTimeField = new JTextField(exam.time.format(TIME_FORMATTER));
        JTextField editLocationField = new JTextField(exam.location);
        JComboBox<String> editSubjectCombo = new JComboBox<>(SUBJECTS);
        editSubjectCombo.setSelectedItem(exam.subject);
        JComboBox<String> editImportanceCombo = new JComboBox<>(IMPORTANCE_LEVELS);
        editImportanceCombo.setSelectedItem(exam.importance);

        editPanel.add(new JLabel("考试名称:"));
        editPanel.add(editNameField);
        editPanel.add(new JLabel("科目:"));
        editPanel.add(editSubjectCombo);
        editPanel.add(new JLabel("考试日期:"));
        editPanel.add(editDateField);
        editPanel.add(new JLabel("考试时间:"));
        editPanel.add(editTimeField);
        editPanel.add(new JLabel("考试地点:"));
        editPanel.add(editLocationField);
        editPanel.add(new JLabel("重要程度:"));
        editPanel.add(editImportanceCombo);

        int result = JOptionPane.showConfirmDialog(this, editPanel, "编辑考试", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = editNameField.getText().trim();
                String newDateStr = editDateField.getText().trim();
                String newTimeStr = editTimeField.getText().trim();
                String newLocation = editLocationField.getText().trim();

                if (newName.isEmpty() || newDateStr.isEmpty() || newTimeStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "考试名称、日期和时间不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate newDate = LocalDate.parse(newDateStr, DATE_FORMATTER);
                LocalTime newTime = LocalTime.parse(newTimeStr, TIME_FORMATTER);

                if (newDate.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "考试日期不能早于今天", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                exam.name = newName;
                exam.subject = (String) editSubjectCombo.getSelectedItem();
                exam.date = newDate;
                exam.time = newTime;
                exam.location = newLocation;
                exam.importance = (String) editImportanceCombo.getSelectedItem();

                updateExamInTable(selectedRow, exam);
                updateDisplay();
                saveData();

                statusLabel.setText("成功更新考试: " + newName);
                statusLabel.setForeground(new Color(39, 174, 96));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "日期或时间格式错误", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteExam() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的考试", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Exam exam = exams.get(selectedRow);
        int result = JOptionPane.showConfirmDialog(this,
                String.format("确定要删除考试 '%s' 吗？", exam.name),
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            exams.remove(selectedRow);
            updateDisplay();
            saveData();

            statusLabel.setText("成功删除考试: " + exam.name);
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    private void clearAll() {
        int result = JOptionPane.showConfirmDialog(this, "确定要清空所有考试吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            exams.clear();
            updateDisplay();
            saveData();
        }
    }

    private void filterExams() {
        updateDisplay();
    }

    private void addExamToTable(Exam exam) {
        DefaultTableModel model = (DefaultTableModel) examTable.getModel();
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), exam.date);
        String status = getExamStatus(exam);

        model.addRow(new Object[]{
            exam.name, exam.subject, exam.date.format(DATE_FORMATTER),
            exam.time.format(TIME_FORMATTER), exam.location, exam.importance,
            daysRemaining + "天", status
        });
    }

    private void updateExamInTable(int row, Exam exam) {
        DefaultTableModel model = (DefaultTableModel) examTable.getModel();
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), exam.date);
        String status = getExamStatus(exam);

        model.setValueAt(exam.name, row, 0);
        model.setValueAt(exam.subject, row, 1);
        model.setValueAt(exam.date.format(DATE_FORMATTER), row, 2);
        model.setValueAt(exam.time.format(TIME_FORMATTER), row, 3);
        model.setValueAt(exam.location, row, 4);
        model.setValueAt(exam.importance, row, 5);
        model.setValueAt(daysRemaining + "天", row, 6);
        model.setValueAt(status, row, 7);
    }

    private String getExamStatus(Exam exam) {
        LocalDateTime examDateTime = LocalDateTime.of(exam.date, exam.time);
        LocalDateTime now = LocalDateTime.now();

        if (examDateTime.isBefore(now)) {
            return "已结束";
        } else if (examDateTime.isBefore(now.plusHours(1))) {
            return "即将开始";
        } else if (examDateTime.toLocalDate().equals(now.toLocalDate())) {
            return "今天";
        } else if (examDateTime.isBefore(now.plusDays(7))) {
            return "本周";
        } else {
            return "未开始";
        }
    }

    private void updateDisplay() {
        DefaultTableModel model = (DefaultTableModel) examTable.getModel();
        model.setRowCount(0);

        // 排序考试（按日期升序）
        List<Exam> sortedExams = new ArrayList<>(exams);
        sortedExams.sort(Comparator.comparing(e -> e.date));

        // 找到最近的考试
        currentExam = null;
        for (Exam exam : sortedExams) {
            if (exam.date.isAfter(LocalDate.now()) || exam.date.equals(LocalDate.now())) {
                currentExam = exam;
                break;
            }
        }

        // 更新倒计时
        if (currentExam != null) {
            updateCountdown(currentExam);
        } else {
            countdownLabel.setText("暂无即将到来的考试");
            countdownProgressBar.setValue(0);
        }

        // 填充表格
        for (Exam exam : sortedExams) {
            addExamToTable(exam);
        }
    }

    private void updateCountdown(Exam exam) {
        LocalDateTime examDateTime = LocalDateTime.of(exam.date, exam.time);
        LocalDateTime now = LocalDateTime.now();

        if (examDateTime.isAfter(now)) {
            long days = ChronoUnit.DAYS.between(now.toLocalDate(), examDateTime.toLocalDate());
            long hours = ChronoUnit.HOURS.between(now, examDateTime) % 24;
            long minutes = ChronoUnit.MINUTES.between(now, examDateTime) % 60;

            countdownLabel.setText(String.format("%s: %d天 %d小时 %d分钟", exam.name, days, hours, minutes));

            // 计算进度（假设准备期为30天）
            long totalMinutes = 30 * 24 * 60;
            long remainingMinutes = ChronoUnit.MINUTES.between(now, examDateTime);
            int progress = (int) Math.max(0, Math.min(100, (totalMinutes - remainingMinutes) * 100 / totalMinutes));
            countdownProgressBar.setValue(progress);
        } else {
            countdownLabel.setText(exam.name + ": 考试已开始");
            countdownProgressBar.setValue(100);
        }
    }

    private void startTimers() {
        // 倒计时更新定时器（每分钟更新）
        countdownTimer = new javax.swing.Timer(60000, e -> updateDisplay());
        countdownTimer.start();

        // 时钟定时器（每秒更新）
        clockTimer = new javax.swing.Timer(1000, e -> {
            if (currentExam != null) {
                updateCountdown(currentExam);
            }
        });
        clockTimer.start();
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(exams);
        } catch (IOException e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            exams = (List<Exam>) in.readObject();
            updateDisplay();
        } catch (Exception e) {
            System.err.println("加载数据失败: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        if (countdownTimer != null) countdownTimer.stop();
        if (clockTimer != null) clockTimer.stop();
        super.dispose();
    }
}

static class Exam implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    String subject;
    LocalDate date;
    LocalTime time;
    String location;
    String importance;
    boolean hasReminder;
    int reminderDays;
    String notes;
    LocalDateTime createdTime;

    Exam(String name, String subject, LocalDate date, LocalTime time, String location,
         String importance, boolean hasReminder, int reminderDays, String notes) {
        this.name = name;
        this.subject = subject;
        this.date = date;
        this.time = time;
        this.location = location;
        this.importance = importance;
        this.hasReminder = hasReminder;
        this.reminderDays = reminderDays;
        this.notes = notes;
        this.createdTime = LocalDateTime.now();
    }
}