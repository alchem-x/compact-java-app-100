import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * 卡路里计算器
 * 记录和计算每日卡路里摄入
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new CalorieCounter().setVisible(true);
    });
}

class CalorieCounter extends JFrame {
    private JTable foodTable;
    private DefaultTableModel tableModel;
    private JTextField foodNameField, caloriesField, amountField;
    private JComboBox<String> mealTypeCombo;
    private JLabel statusLabel;
    private JLabel dailyTotalLabel;
    private List<FoodRecord> foodRecords = new ArrayList<>();
    private static final String[] MEAL_TYPES = {"早餐", "午餐", "晚餐", "零食"};
    private int dailyTotal = 0;

    public CalorieCounter() {
        initializeGUI();
        loadSampleData();
        updateDisplay();
    }

    private void initializeGUI() {
        setTitle("卡路里计算器 - Calorie Counter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 顶部输入面板
        var inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("添加食物记录"));
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("食物名称:"), gbc);
        gbc.gridx = 1;
        foodNameField = new JTextField(15);
        inputPanel.add(foodNameField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        inputPanel.add(new JLabel("用餐类型:"), gbc);
        gbc.gridx = 3;
        mealTypeCombo = new JComboBox<>(MEAL_TYPES);
        inputPanel.add(mealTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("卡路里:"), gbc);
        gbc.gridx = 1;
        caloriesField = new JTextField(10);
        inputPanel.add(caloriesField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        inputPanel.add(new JLabel("份量(g):"), gbc);
        gbc.gridx = 3;
        amountField = new JTextField(10);
        inputPanel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        var buttonPanel = new JPanel(new FlowLayout());
        var addBtn = new JButton("添加记录");
        var deleteBtn = new JButton("删除记录");
        addBtn.addActionListener(e -> addFoodRecord());
        deleteBtn.addActionListener(e -> deleteFoodRecord());
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // 中央表格
        String[] columns = {"日期", "用餐类型", "食物名称", "卡路里", "份量(g)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        foodTable = new JTable(tableModel);
        var scrollPane = new JScrollPane(foodTable);
        add(scrollPane, BorderLayout.CENTER);

        // 底部统计面板
        var statsPanel = new JPanel(new FlowLayout());
        dailyTotalLabel = new JLabel("今日总计: 0 卡路里");
        dailyTotalLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        statsPanel.add(dailyTotalLabel);

        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        var bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statsPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadSampleData() {
        foodRecords.add(new FoodRecord("苹果", "零食", 52, 100, LocalDate.now()));
        foodRecords.add(new FoodRecord("米饭", "午餐", 130, 150, LocalDate.now()));
        foodRecords.add(new FoodRecord("鸡胸肉", "晚餐", 165, 100, LocalDate.now()));
    }

    private void addFoodRecord() {
        try {
            String name = foodNameField.getText().trim();
            String mealType = (String) mealTypeCombo.getSelectedItem();
            int calories = Integer.parseInt(caloriesField.getText().trim());
            int amount = Integer.parseInt(amountField.getText().trim());

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入食物名称");
                return;
            }

            var record = new FoodRecord(name, mealType, calories, amount, LocalDate.now());
            foodRecords.add(record);
            
            // 清空输入字段
            foodNameField.setText("");
            caloriesField.setText("");
            amountField.setText("");
            
            updateDisplay();
            statusLabel.setText("已添加: " + name);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字");
        }
    }

    private void deleteFoodRecord() {
        int selectedRow = foodTable.getSelectedRow();
        if (selectedRow >= 0) {
            foodRecords.remove(selectedRow);
            updateDisplay();
            statusLabel.setText("已删除记录");
        } else {
            JOptionPane.showMessageDialog(this, "请选择要删除的记录");
        }
    }

    private void updateDisplay() {
        tableModel.setRowCount(0);
        dailyTotal = 0;
        
        var today = LocalDate.now();
        var formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (var record : foodRecords) {
            Object[] row = {
                record.date.format(formatter),
                record.mealType,
                record.name,
                record.calories,
                record.amount
            };
            tableModel.addRow(row);
            
            if (record.date.equals(today)) {
                dailyTotal += record.calories;
            }
        }
        
        dailyTotalLabel.setText("今日总计: " + dailyTotal + " 卡路里");
    }

    record FoodRecord(String name, String mealType, int calories, int amount, LocalDate date) {}
}
