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
        new ExpenseTracker().setVisible(true);
    });
}

static class ExpenseTracker extends JFrame {
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JTextField amountField, descriptionField;
    private JComboBox<String> categoryCombo;
    private JLabel totalLabel, monthlyTotalLabel, statusLabel;
    private JComboBox<String> monthFilterCombo;
    private double totalExpense = 0.0;
    private Map<String, Double> categoryTotals = new HashMap<>();
    private static final String[] CATEGORIES = {
        "餐饮", "交通", "购物", "娱乐", "医疗", "教育", "住房", "其他"
    };
    private static final String DATA_FILE = "expenses.dat";

    public ExpenseTracker() {
        initializeGUI();
        loadData();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle("个人账本 - 支出记录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("💰 个人账本", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        // 统计面板
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 10));
        statsPanel.setBackground(new Color(245, 247, 250));
        statsPanel.setBorder(BorderFactory.createTitledBorder("统计信息"));

        totalLabel = new JLabel("总支出: ¥0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalLabel.setForeground(new Color(231, 76, 60));

        monthlyTotalLabel = new JLabel("本月支出: ¥0.00", SwingConstants.CENTER);
        monthlyTotalLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        monthlyTotalLabel.setForeground(new Color(127, 140, 141));

        statusLabel = new JLabel("准备就绪", SwingConstants.LEFT);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(127, 140, 141));

        monthFilterCombo = new JComboBox<String>();
        updateMonthFilter();
        monthFilterCombo.addActionListener(e -> filterByMonth());

        statsPanel.add(totalLabel);
        statsPanel.add(monthlyTotalLabel);
        statsPanel.add(new JLabel("筛选月份:"));
        statsPanel.add(monthFilterCombo);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(new Color(245, 247, 250));
        inputPanel.setBorder(BorderFactory.createTitledBorder("添加支出"));

        amountField = new JTextField();
        amountField.setFont(new Font("Consolas", Font.PLAIN, 14));
        descriptionField = new JTextField();
        descriptionField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        categoryCombo = new JComboBox<String>(CATEGORIES);

        inputPanel.add(new JLabel("金额:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("类别:"));
        inputPanel.add(categoryCombo);
        inputPanel.add(new JLabel("描述:"));
        inputPanel.add(descriptionField);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addButton = createButton("➕ 添加", new Color(39, 174, 96), e -> addExpense());
        JButton deleteButton = createButton("🗑️ 删除", new Color(231, 76, 60), e -> deleteExpense());
        JButton clearButton = createButton("🔄 清空", new Color(243, 156, 18), e -> clearAll());
        JButton exportButton = createButton("📊 导出", new Color(52, 152, 219), e -> exportData());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exportButton);

        // 表格
        String[] columnNames = {"日期", "金额", "类别", "描述"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        expenseTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        expenseTable.setRowHeight(25);
        expenseTable.setSelectionBackground(new Color(173, 216, 230));

        // 设置列宽
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("支出记录"));

        // 分类统计面板
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(new Color(245, 247, 250));
        categoryPanel.setBorder(BorderFactory.createTitledBorder("分类统计"));
        updateCategoryStats(categoryPanel);

        // 组装界面
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(buttonPanel, BorderLayout.CENTER);
        leftPanel.add(categoryPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        setSize(1000, 700);
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

    private void addExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "金额必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String category = (String) categoryCombo.getSelectedItem();
            String description = descriptionField.getText().trim();
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 添加到表格
            tableModel.addRow(new Object[]{date, String.format("¥%.2f", amount), category, description});

            // 更新统计
            totalExpense += amount;
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);

            updateDisplay();
            updateCategoryStats();
            updateMonthFilter();
            saveData();

            // 清空输入
            amountField.setText("");
            descriptionField.setText("");

            statusLabel.setText("支出记录已添加");
            statusLabel.setForeground(new Color(39, 174, 96));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的金额", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "确定要删除选中的记录吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            String amountStr = (String) tableModel.getValueAt(selectedRow, 1);
            double amount = Double.parseDouble(amountStr.replace("¥", ""));
            String category = (String) tableModel.getValueAt(selectedRow, 2);

            totalExpense -= amount;
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) - amount);

            tableModel.removeRow(selectedRow);
            updateDisplay();
            updateCategoryStats();
            saveData();
        }
    }

    private void clearAll() {
        int result = JOptionPane.showConfirmDialog(this, "确定要清空所有记录吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
            totalExpense = 0.0;
            categoryTotals.clear();
            updateDisplay();
            updateCategoryStats();
            saveData();
        }
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("支出记录_" + LocalDate.now() + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("日期,金额,类别,描述");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    writer.printf("%s,%s,%s,%s\n",
                            tableModel.getValueAt(i, 0),
                            tableModel.getValueAt(i, 1),
                            tableModel.getValueAt(i, 2),
                            tableModel.getValueAt(i, 3));
                }
                JOptionPane.showMessageDialog(this, "数据导出成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDisplay() {
        totalLabel.setText(String.format("总支出: ¥%.2f", totalExpense));

        double monthlyTotal = 0.0;
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String date = (String) tableModel.getValueAt(i, 0);
            if (date.startsWith(currentMonth)) {
                String amountStr = (String) tableModel.getValueAt(i, 1);
                monthlyTotal += Double.parseDouble(amountStr.replace("¥", ""));
            }
        }
        monthlyTotalLabel.setText(String.format("本月支出: ¥%.2f", monthlyTotal));
    }

    private void updateCategoryStats() {
        // 更新分类统计面板
        JPanel categoryPanel = (JPanel) ((JPanel) getContentPane().getComponent(2)).getComponent(0);
        updateCategoryStats(categoryPanel);
    }

    private void updateCategoryStats(JPanel panel) {
        panel.removeAll();

        for (String category : CATEGORIES) {
            double amount = categoryTotals.getOrDefault(category, 0.0);
            if (amount > 0) {
                JLabel label = new JLabel(String.format("%s: ¥%.2f", category, amount));
                label.setFont(new Font("微软雅黑", Font.PLAIN, 12));
                panel.add(label);
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    private void updateMonthFilter() {
        monthFilterCombo.removeAllItems();
        monthFilterCombo.addItem("全部");

        Set<String> months = new TreeSet<>(Collections.reverseOrder());
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String date = (String) tableModel.getValueAt(i, 0);
            String month = date.substring(0, 7);
            months.add(month);
        }

        for (String month : months) {
            monthFilterCombo.addItem(month);
        }
    }

    private void filterByMonth() {
        String selectedMonth = (String) monthFilterCombo.getSelectedItem();
        if (selectedMonth == null || selectedMonth.equals("全部")) {
            return;
        }

        // 这里可以实现按月份筛选的功能
        statusLabel.setText("已筛选: " + selectedMonth);
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(totalExpense);
            out.writeObject(categoryTotals);

            List<Object[]> data = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                data.add(new Object[]{
                        tableModel.getValueAt(i, 0),
                        tableModel.getValueAt(i, 1),
                        tableModel.getValueAt(i, 2),
                        tableModel.getValueAt(i, 3)
                });
            }
            out.writeObject(data);
        } catch (IOException e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            totalExpense = (Double) in.readObject();
            categoryTotals = (Map<String, Double>) in.readObject();

            List<Object[]> data = (List<Object[]>) in.readObject();
            for (Object[] row : data) {
                tableModel.addRow(row);
            }

            updateDisplay();
            updateMonthFilter();
        } catch (Exception e) {
            System.err.println("加载数据失败: " + e.getMessage());
        }
    }
}

// 序列化支持
class CategoryData implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    String category;
    double amount;

    CategoryData(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }
}