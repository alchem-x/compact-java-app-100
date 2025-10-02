import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * 用药提醒器
 * 帮助用户管理药物服用时间和记录
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new MedicineReminder().setVisible(true);
    });
}

class MedicineReminder extends JFrame {
    private List<Medicine> medicines = new ArrayList<>();
    private List<MedicineRecord> records = new ArrayList<>();
    private javax.swing.Timer reminderTimer;
    
    private DefaultListModel<Medicine> medicineListModel;
    private JList<Medicine> medicineList;
    private JTextArea recordArea;
    private JLabel statusLabel;
    
    public MedicineReminder() {
        initializeUI();
        startReminderTimer();
        loadSampleData();
    }
    
    private void initializeUI() {
        setTitle("用药提醒器 - Medicine Reminder");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 主面板
        var mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        
        // 顶部工具栏
        var toolBar = new JPanel(new FlowLayout());
        var addBtn = new JButton("添加药物");
        var editBtn = new JButton("编辑");
        var deleteBtn = new JButton("删除");
        var takeBtn = new JButton("服药记录");
        
        addBtn.addActionListener(e -> addMedicine());
        editBtn.addActionListener(e -> editMedicine());
        deleteBtn.addActionListener(e -> deleteMedicine());
        takeBtn.addActionListener(e -> takeMedicine());
        
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);
        toolBar.add(takeBtn);
        
        mainPanel.add(toolBar, BorderLayout.NORTH);
        
        // 中央分割面板
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // 左侧药物列表
        medicineListModel = new DefaultListModel<>();
        medicineList = new JList<>(medicineListModel);
        medicineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineList.setCellRenderer(new MedicineListCellRenderer());
        
        var listScrollPane = new JScrollPane(medicineList);
        listScrollPane.setPreferredSize(new Dimension(350, 300));
        listScrollPane.setBorder(BorderFactory.createTitledBorder("药物列表"));
        
        // 右侧记录区域
        recordArea = new JTextArea();
        recordArea.setEditable(false);
        recordArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        var recordScrollPane = new JScrollPane(recordArea);
        recordScrollPane.setBorder(BorderFactory.createTitledBorder("服药记录"));
        
        splitPane.setLeftComponent(listScrollPane);
        splitPane.setRightComponent(recordScrollPane);
        splitPane.setDividerLocation(350);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // 底部状态栏
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        updateRecordDisplay();
    }
    
    private void startReminderTimer() {
        reminderTimer = new javax.swing.Timer(60000, e -> checkReminders()); // 每分钟检查一次
        reminderTimer.start();
    }
    
    private void loadSampleData() {
        medicines.add(new Medicine("维生素C", "每日一次", LocalTime.of(8, 0), 1, "饭后服用"));
        medicines.add(new Medicine("钙片", "每日两次", LocalTime.of(9, 0), 2, "随餐服用"));
        updateMedicineList();
    }
    
    private void addMedicine() {
        var dialog = new MedicineDialog(this, "添加药物", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            medicines.add(dialog.getMedicine());
            updateMedicineList();
            statusLabel.setText("已添加药物: " + dialog.getMedicine().name());
        }
    }
    
    private void editMedicine() {
        var selected = medicineList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的药物");
            return;
        }
        
        var dialog = new MedicineDialog(this, "编辑药物", selected);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            var updated = dialog.getMedicine();
            int index = medicines.indexOf(selected);
            medicines.set(index, updated);
            updateMedicineList();
            statusLabel.setText("已更新药物: " + updated.name());
        }
    }
    
    private void deleteMedicine() {
        var selected = medicineList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的药物");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定要删除药物 \"" + selected.name() + "\" 吗？",
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            medicines.remove(selected);
            updateMedicineList();
            statusLabel.setText("已删除药物: " + selected.name());
        }
    }
    
    private void takeMedicine() {
        var selected = medicineList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请先选择药物");
            return;
        }
        
        var record = new MedicineRecord(selected.name(), LocalDateTime.now(), "正常服用");
        records.add(record);
        updateRecordDisplay();
        statusLabel.setText("已记录服药: " + selected.name());
        
        Toolkit.getDefaultToolkit().beep();
    }
    
    private void checkReminders() {
        var now = LocalTime.now();
        
        for (var medicine : medicines) {
            if (shouldRemind(medicine, now)) {
                showReminder(medicine);
            }
        }
    }
    
    private boolean shouldRemind(Medicine medicine, LocalTime now) {
        var reminderTime = medicine.reminderTime();
        return Math.abs(now.toSecondOfDay() - reminderTime.toSecondOfDay()) <= 60;
    }
    
    private void showReminder(Medicine medicine) {
        String message = String.format(
            "提醒服药！\n\n药物: %s\n用法: %s\n剂量: %d片\n备注: %s",
            medicine.name(), medicine.dosage(), medicine.quantity(), medicine.notes()
        );
        
        int result = JOptionPane.showConfirmDialog(this, message + "\n\n是否现在服药？",
            "用药提醒", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            var record = new MedicineRecord(medicine.name(), LocalDateTime.now(), "按时服用");
            records.add(record);
            updateRecordDisplay();
        }
    }
    
    private void updateMedicineList() {
        medicineListModel.clear();
        medicines.forEach(medicineListModel::addElement);
    }
    
    private void updateRecordDisplay() {
        var sb = new StringBuilder();
        sb.append("=== 服药记录 ===\n\n");
        
        if (records.isEmpty()) {
            sb.append("暂无服药记录\n");
        } else {
            records.stream()
                   .sorted((a, b) -> b.time().compareTo(a.time()))
                   .forEach(record -> sb.append(String.format("%s - %s\n时间: %s\n备注: %s\n\n",
                       record.medicineName(),
                       record.time().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                       record.time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                       record.notes()
                   )));
        }
        
        recordArea.setText(sb.toString());
        recordArea.setCaretPosition(0);
    }
    
    // 药物记录类
    record Medicine(String name, String dosage, LocalTime reminderTime, int quantity, String notes) {
        @Override
        public String toString() {
            return String.format("%s - %s (%s)", 
                name, dosage, reminderTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }
    
    // 服药记录类
    record MedicineRecord(String medicineName, LocalDateTime time, String notes) {}
    
    // 药物列表渲染器
    class MedicineListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Medicine medicine) {
                setText(String.format(
                    "<html><b>%s</b><br><small>%s | %s | %d片<br>%s</small></html>",
                    medicine.name(),
                    medicine.dosage(),
                    medicine.reminderTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    medicine.quantity(),
                    medicine.notes()
                ));
            }
            
            return this;
        }
    }
    
    // 药物编辑对话框
    class MedicineDialog extends JDialog {
        private JTextField nameField;
        private JTextField dosageField;
        private JSpinner timeSpinner;
        private JSpinner quantitySpinner;
        private JTextArea notesArea;
        private boolean confirmed = false;
        
        public MedicineDialog(Frame parent, String title, Medicine medicine) {
            super(parent, title, true);
            initializeDialog();
            
            if (medicine != null) {
                loadMedicine(medicine);
            }
        }
        
        private void initializeDialog() {
            setSize(400, 350);
            setLocationRelativeTo(getParent());
            
            var panel = new JPanel(new GridBagLayout());
            var gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // 药物名称
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("药物名称:"), gbc);
            gbc.gridx = 1;
            nameField = new JTextField(20);
            panel.add(nameField, gbc);
            
            // 用法用量
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("用法用量:"), gbc);
            gbc.gridx = 1;
            dosageField = new JTextField(20);
            panel.add(dosageField, gbc);
            
            // 提醒时间
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("提醒时间:"), gbc);
            gbc.gridx = 1;
            timeSpinner = new JSpinner(new SpinnerDateModel());
            var timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
            timeSpinner.setEditor(timeEditor);
            panel.add(timeSpinner, gbc);
            
            // 数量
            gbc.gridx = 0; gbc.gridy = 3;
            panel.add(new JLabel("数量(片):"), gbc);
            gbc.gridx = 1;
            quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
            panel.add(quantitySpinner, gbc);
            
            // 备注
            gbc.gridx = 0; gbc.gridy = 4;
            panel.add(new JLabel("备注:"), gbc);
            gbc.gridx = 1;
            notesArea = new JTextArea(3, 20);
            panel.add(new JScrollPane(notesArea), gbc);
            
            // 按钮
            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
            var buttonPanel = new JPanel();
            var okButton = new JButton("确定");
            var cancelButton = new JButton("取消");
            
            okButton.addActionListener(e -> {
                if (validateInput()) {
                    confirmed = true;
                    dispose();
                }
            });
            
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            panel.add(buttonPanel, gbc);
            
            add(panel);
        }
        
        private void loadMedicine(Medicine medicine) {
            nameField.setText(medicine.name());
            dosageField.setText(medicine.dosage());
            
            var cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, medicine.reminderTime().getHour());
            cal.set(Calendar.MINUTE, medicine.reminderTime().getMinute());
            timeSpinner.setValue(cal.getTime());
            
            quantitySpinner.setValue(medicine.quantity());
            notesArea.setText(medicine.notes());
        }
        
        private boolean validateInput() {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入药物名称");
                return false;
            }
            if (dosageField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入用法用量");
                return false;
            }
            return true;
        }
        
        public Medicine getMedicine() {
            if (!confirmed) return null;
            
            var timeValue = (Date) timeSpinner.getValue();
            var cal = Calendar.getInstance();
            cal.setTime(timeValue);
            
            var reminderTime = LocalTime.of(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            );
            
            return new Medicine(
                nameField.getText().trim(),
                dosageField.getText().trim(),
                reminderTime,
                (Integer) quantitySpinner.getValue(),
                notesArea.getText().trim()
            );
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
    }
}
