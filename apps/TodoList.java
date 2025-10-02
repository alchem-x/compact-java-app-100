import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TodoList().setVisible(true));
}

static class TodoList extends JFrame {
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

    // ===== 应用状态 =====
    private DefaultListModel<TodoItem> todoModel;
    private JList<TodoItem> todoJList;
    private JTextField taskField;
    private JComboBox<Priority> priorityCombo;
    private JComboBox<Category> categoryCombo;
    private JTextArea descriptionArea;
    private JLabel statsLabel;

    enum Priority {
        LOW("低", GREEN),
        MEDIUM("中", ORANGE),
        HIGH("高", RED);

        private final String name;
        private final Color color;

        Priority(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        @Override
        public String toString() {
            return name;
        }

        public Color getColor() {
            return color;
        }
    }

    enum Category {
        WORK("工作"), PERSONAL("个人"), STUDY("学习"),
        HEALTH("健康"), SHOPPING("购物"), OTHER("其他");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class TodoItem {
        private String task;
        private String description;
        private Priority priority;
        private Category category;
        private boolean completed;
        private LocalDateTime createdTime;
        private LocalDateTime completedTime;

        public TodoItem(String task, String description, Priority priority, Category category) {
            this.task = task;
            this.description = description;
            this.priority = priority;
            this.category = category;
            this.completed = false;
            this.createdTime = LocalDateTime.now();
        }

        @Override
        public String toString() {
            String status = completed ? "✅" : "⭕";
            return String.format("%s [%s] %s - %s",
                status, priority.toString(), task, category.toString());
        }

        // Getters and setters
        public String getTask() { return task; }
        public void setTask(String task) { this.task = task; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }

        public Category getCategory() { return category; }
        public void setCategory(Category category) { this.category = category; }

        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) {
            this.completed = completed;
            if (completed && completedTime == null) {
                completedTime = LocalDateTime.now();
            } else if (!completed) {
                completedTime = null;
            }
        }

        public LocalDateTime getCreatedTime() { return createdTime; }
        public LocalDateTime getCompletedTime() { return completedTime; }
    }

    public TodoList() {
        this.initializeGUI();
        this.loadSampleData();
    }

    private void initializeGUI() {
        setTitle("📝 待办事项管理器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 顶部输入面板
        this.createInputPanel();

        // 中央列表面板
        this.createListPanel();

        // 右侧操作面板
        this.createActionPanel();

        // 底部统计面板
        this.createStatsPanel();

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void createInputPanel() {
        var inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder("添加新任务")),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_4, SPACING_4, SPACING_4, SPACING_4);
        gbc.anchor = GridBagConstraints.WEST;

        // 任务名称
        gbc.gridx = 0; gbc.gridy = 0;
        var taskLabel = new JLabel("任务:");
        taskLabel.setFont(BODY);
        inputPanel.add(taskLabel, gbc);

        taskField = new JTextField(20);
        taskField.setFont(BODY);
        taskField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        taskField.setBackground(GRAY6);
        taskField.addActionListener((ev) -> this.addTask());
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(taskField, gbc);

        // 优先级
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        var priorityLabel = new JLabel("优先级:");
        priorityLabel.setFont(BODY);
        inputPanel.add(priorityLabel, gbc);

        priorityCombo = new JComboBox<>(Priority.values());
        priorityCombo.setSelectedItem(Priority.MEDIUM);
        priorityCombo.setFont(BODY);
        priorityCombo.setBackground(WHITE);
        gbc.gridx = 1;
        inputPanel.add(priorityCombo, gbc);

        // 分类
        gbc.gridx = 2;
        var categoryLabel = new JLabel("分类:");
        categoryLabel.setFont(BODY);
        inputPanel.add(categoryLabel, gbc);

        categoryCombo = new JComboBox<>(Category.values());
        categoryCombo.setFont(BODY);
        categoryCombo.setBackground(WHITE);
        gbc.gridx = 3;
        inputPanel.add(categoryCombo, gbc);

        // 描述
        gbc.gridx = 0; gbc.gridy = 2;
        var descLabel = new JLabel("描述:");
        descLabel.setFont(BODY);
        inputPanel.add(descLabel, gbc);

        descriptionArea = new JTextArea(2, 30);
        descriptionArea.setFont(CALLOUT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        var descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(0, 60));
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(descScrollPane, gbc);

        // 添加按钮 - 使用苹果风格
        var addButton = this.createPrimaryButton("➕ 添加任务");
        addButton.addActionListener((ev) -> this.addTask());
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(addButton, gbc);

        add(inputPanel, BorderLayout.NORTH);
    }

    private void createListPanel() {
        var listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(SYSTEM_BACKGROUND);
        listPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder("任务列表")),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
        ));

        todoModel = new DefaultListModel<>();
        todoJList = new JList<>(todoModel);
        todoJList.setFont(BODY);
        todoJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todoJList.setBackground(WHITE);
        todoJList.setSelectionBackground(new Color(0, 122, 255, 30));
        todoJList.setSelectionForeground(BLUE);

        // 自定义渲染器
        todoJList.setCellRenderer(new TodoItemRenderer());

        var scrollPane = new JScrollPane(todoJList);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        listPanel.add(scrollPane, BorderLayout.CENTER);
        add(listPanel, BorderLayout.CENTER);
    }

    private void createStatsPanel() {
        var statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(GRAY6);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8, GRAY3, 1),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));

        statsLabel = new JLabel();
        statsLabel.setFont(FOOTNOTE);
        statsLabel.setForeground(SECONDARY_LABEL);
        this.updateStats();

        statsPanel.add(statsLabel);
        add(statsPanel, BorderLayout.SOUTH);
    }

    private void createActionPanel() {
        var actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(this.createTitledBorder("操作")),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        actionPanel.setPreferredSize(new Dimension(180, 0));

        // 创建操作按钮
        var completeButton = this.createSuccessButton("✅ 完成");
        completeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        completeButton.addActionListener((ev) -> this.toggleComplete());

        var editButton = this.createSecondaryButton("✏️ 编辑");
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton.addActionListener((ev) -> this.editTask());

        var deleteButton = this.createDangerButton("🗑️ 删除");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.addActionListener((ev) -> this.deleteTask());

        var clearCompletedButton = this.createWarningButton("🧹 清除已完成");
        clearCompletedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearCompletedButton.addActionListener((ev) -> this.clearCompleted());

        var sortButton = this.createSecondaryButton("🔄 排序");
        sortButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sortButton.addActionListener((ev) -> this.sortTasks());

        var exportButton = this.createSecondaryButton("💾 导出");
        exportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportButton.addActionListener((ev) -> this.exportTasks());

        // 添加按钮到面板
        actionPanel.add(Box.createVerticalStrut(SPACING_16));
        actionPanel.add(completeButton);
        actionPanel.add(Box.createVerticalStrut(SPACING_8));
        actionPanel.add(editButton);
        actionPanel.add(Box.createVerticalStrut(SPACING_8));
        actionPanel.add(deleteButton);
        actionPanel.add(Box.createVerticalStrut(SPACING_16));
        actionPanel.add(clearCompletedButton);
        actionPanel.add(Box.createVerticalStrut(SPACING_8));
        actionPanel.add(sortButton);
        actionPanel.add(Box.createVerticalStrut(SPACING_8));
        actionPanel.add(exportButton);
        actionPanel.add(Box.createVerticalGlue());

        add(actionPanel, BorderLayout.EAST);
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

    private JButton createDangerButton(String text) {
        return this.createStyledButton(text, RED, WHITE);
    }

    private JButton createWarningButton(String text) {
        return this.createStyledButton(text, ORANGE, WHITE);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        var button = new JButton(text);
        button.setFont(HEADLINE);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(RADIUS_8),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        button.setPreferredSize(BUTTON_REGULAR);
        button.setMaximumSize(BUTTON_REGULAR);
        button.setMinimumSize(BUTTON_REGULAR);

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

    private javax.swing.border.Border createTitledBorder(String title) {
        var border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(HEADLINE);
        border.setTitleColor(SECONDARY_LABEL);
        return border;
    }

    // ===== 业务逻辑方法 =====
    private void addTask() {
        String task = taskField.getText().trim();
        if (task.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入任务名称！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        var priority = (Priority) priorityCombo.getSelectedItem();
        var category = (Category) categoryCombo.getSelectedItem();
        var description = descriptionArea.getText().trim();

        var item = new TodoItem(task, description, priority, category);
        todoModel.addElement(item);

        // 清空输入
        taskField.setText("");
        descriptionArea.setText("");
        priorityCombo.setSelectedItem(Priority.MEDIUM);
        categoryCombo.setSelectedIndex(0);

        this.updateStats();
        taskField.requestFocus();
    }

    private void toggleComplete() {
        var selected = todoJList.getSelectedValue();
        if (selected != null) {
            selected.setCompleted(!selected.isCompleted());
            todoJList.repaint();
            this.updateStats();
        }
    }

    private void editTask() {
        var selected = todoJList.getSelectedValue();
        if (selected == null) return;

        var editDialog = new JDialog(this, "编辑任务", true);
        editDialog.setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_8, SPACING_8, SPACING_8, SPACING_8);

        var editTaskField = new JTextField(selected.getTask(), 20);
        var editPriorityCombo = new JComboBox<>(Priority.values());
        editPriorityCombo.setSelectedItem(selected.getPriority());
        var editCategoryCombo = new JComboBox<>(Category.values());
        editCategoryCombo.setSelectedItem(selected.getCategory());
        var editDescArea = new JTextArea(selected.getDescription(), 3, 20);
        editDescArea.setLineWrap(true);
        editDescArea.setWrapStyleWord(true);

        // 添加组件到对话框
        gbc.gridx = 0; gbc.gridy = 0;
        editDialog.add(new JLabel("任务:"), gbc);
        gbc.gridx = 1;
        editDialog.add(editTaskField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        editDialog.add(new JLabel("优先级:"), gbc);
        gbc.gridx = 1;
        editDialog.add(editPriorityCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        editDialog.add(new JLabel("分类:"), gbc);
        gbc.gridx = 1;
        editDialog.add(editCategoryCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        editDialog.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1;
        editDialog.add(new JScrollPane(editDescArea), gbc);

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_8, 0));
        var saveButton = this.createPrimaryButton("保存");
        var cancelButton = this.createSecondaryButton("取消");

        saveButton.addActionListener((ev) -> {
            selected.setTask(editTaskField.getText().trim());
            selected.setPriority((Priority) editPriorityCombo.getSelectedItem());
            selected.setCategory((Category) editCategoryCombo.getSelectedItem());
            selected.setDescription(editDescArea.getText().trim());
            todoJList.repaint();
            editDialog.dispose();
        });

        cancelButton.addActionListener((ev) -> editDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        editDialog.add(buttonPanel, gbc);

        editDialog.pack();
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void deleteTask() {
        int selectedIndex = todoJList.getSelectedIndex();
        if (selectedIndex != -1) {
            int result = JOptionPane.showConfirmDialog(this,
                "确定要删除这个任务吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                todoModel.removeElementAt(selectedIndex);
                this.updateStats();
            }
        }
    }

    private void clearCompleted() {
        var toRemove = new ArrayList<TodoItem>();
        for (int i = 0; i < todoModel.getSize(); i++) {
            var item = todoModel.getElementAt(i);
            if (item.isCompleted()) {
                toRemove.add(item);
            }
        }

        if (!toRemove.isEmpty()) {
            int result = JOptionPane.showConfirmDialog(this,
                String.format("确定要删除 %d 个已完成的任务吗？", toRemove.size()),
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                for (var item : toRemove) {
                    todoModel.removeElement(item);
                }
                this.updateStats();
            }
        }
    }

    private void sortTasks() {
        var options = new String[]{"按优先级", "按分类", "按创建时间", "按完成状态"};
        var choice = (String) JOptionPane.showInputDialog(this,
            "选择排序方式:", "排序", JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);

        if (choice != null) {
            var items = new ArrayList<TodoItem>();
            for (int i = 0; i < todoModel.getSize(); i++) {
                items.add(todoModel.getElementAt(i));
            }

            switch (choice) {
                case "按优先级" ->
                    items.sort((a, b) -> b.getPriority().ordinal() - a.getPriority().ordinal());
                case "按分类" ->
                    items.sort((a, b) -> a.getCategory().toString().compareTo(b.getCategory().toString()));
                case "按创建时间" ->
                    items.sort((a, b) -> b.getCreatedTime().compareTo(a.getCreatedTime()));
                case "按完成状态" ->
                    items.sort((a, b) -> Boolean.compare(a.isCompleted(), b.isCompleted()));
            }

            todoModel.clear();
            for (var item : items) {
                todoModel.addElement(item);
            }
        }
    }

    private void exportTasks() {
        var fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("todo_list.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                var sb = new StringBuilder();
                sb.append("待办事项列表\n");
                sb.append("导出时间: ").append(LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");

                for (int i = 0; i < todoModel.getSize(); i++) {
                    var item = todoModel.getElementAt(i);
                    sb.append(String.format("%d. %s\n", i + 1, item.toString()));
                    if (!item.getDescription().isEmpty()) {
                        sb.append("   描述: ").append(item.getDescription()).append("\n");
                    }
                    sb.append("   创建时间: ").append(
                        item.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
                    if (item.isCompleted() && item.getCompletedTime() != null) {
                        sb.append("   完成时间: ").append(
                            item.getCompletedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
                    }
                    sb.append("\n");
                }

                java.nio.file.Files.write(file.toPath(), sb.toString().getBytes());
                JOptionPane.showMessageDialog(this, "任务列表已导出到文件！", "成功",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage(), "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStats() {
        int total = todoModel.getSize();
        int completed = 0;
        int high = 0, medium = 0, low = 0;

        for (int i = 0; i < total; i++) {
            var item = todoModel.getElementAt(i);
            if (item.isCompleted()) completed++;

            switch (item.getPriority()) {
                case HIGH -> high++;
                case MEDIUM -> medium++;
                case LOW -> low++;
            }
        }

        int pending = total - completed;
        double completionRate = total > 0 ? (completed * 100.0 / total) : 0;

        statsLabel.setText(String.format(
            "总计: %d | 已完成: %d | 待办: %d | 完成率: %.1f%% | 高优先级: %d | 中优先级: %d | 低优先级: %d",
            total, completed, pending, completionRate, high, medium, low));
    }

    private void loadSampleData() {
        todoModel.addElement(new TodoItem("完成项目报告", "需要包含数据分析和结论", Priority.HIGH, Category.WORK));
        todoModel.addElement(new TodoItem("买菜", "西红柿、鸡蛋、面条", Priority.MEDIUM, Category.SHOPPING));
        todoModel.addElement(new TodoItem("锻炼身体", "跑步30分钟", Priority.MEDIUM, Category.HEALTH));
        this.updateStats();
    }

    // ===== 内部类 =====
    private static class TodoItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof TodoItem) {
                var item = (TodoItem) value;

                // 设置优先级颜色
                if (!isSelected) {
                    setForeground(item.getPriority().getColor());
                }

                // 已完成的任务使用删除线效果
                if (item.isCompleted()) {
                    setFont(getFont().deriveFont(Font.ITALIC));
                    if (!isSelected) {
                        setForeground(GRAY2);
                    }
                } else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }

            return this;
        }
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
}