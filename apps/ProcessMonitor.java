import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new ProcessMonitor().setVisible(true);
    });
}

static class ProcessMonitor extends JFrame {
    private JTable processTable;
    private ProcessTableModel tableModel;
    private JButton refreshButton;
    private JButton killButton;
    private JButton searchButton;
    private JTextField searchField;
    private JLabel statusLabel;
    private JLabel processCountLabel;
    private JCheckBox autoRefreshCheck;
    private JComboBox<String> refreshIntervalCombo;
    private Timer refreshTimer;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public ProcessMonitor() {
        initializeGUI();
        startAutoRefresh();
    }

    private void initializeGUI() {
        setTitle("进程监视器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 标题
        JLabel titleLabel = new JLabel("⚙️ 进程监视器");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // 控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));

        // 搜索功能
        controlPanel.add(new JLabel("搜索:"));
        searchField = new JTextField(15);
        searchField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        searchButton = new JButton("🔍");
        searchButton.addActionListener(e -> searchProcesses());
        controlPanel.add(searchField);
        controlPanel.add(searchButton);

        // 自动刷新
        autoRefreshCheck = new JCheckBox("自动刷新", true);
        autoRefreshCheck.addActionListener(e -> toggleAutoRefresh());
        controlPanel.add(autoRefreshCheck);

        controlPanel.add(new JLabel("间隔:"));
        refreshIntervalCombo = new JComboBox<>(new String[]{"1秒", "2秒", "5秒", "10秒"});
        refreshIntervalCombo.setSelectedIndex(2); // 默认5秒
        refreshIntervalCombo.addActionListener(e -> updateRefreshInterval());
        controlPanel.add(refreshIntervalCombo);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(controlPanel, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        refreshButton = new JButton("🔄 刷新");
        refreshButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        refreshButton.setBackground(new Color(76, 175, 80));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshProcessList());

        killButton = new JButton("❌ 结束进程");
        killButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        killButton.setBackground(new Color(244, 67, 54));
        killButton.setForeground(Color.WHITE);
        killButton.setFocusPainted(false);
        killButton.addActionListener(e -> killSelectedProcess());

        buttonPanel.add(refreshButton);
        buttonPanel.add(killButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(buttonPanel, gbc);

        // 进程表格
        tableModel = new ProcessTableModel();
        processTable = new JTable(tableModel);
        processTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        processTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        processTable.setRowHeight(25);
        processTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 设置列宽
        processTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // PID
        processTable.getColumnModel().getColumn(1).setPreferredWidth(200); // 名称
        processTable.getColumnModel().getColumn(2).setPreferredWidth(150); // 状态
        processTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // CPU
        processTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // 内存
        processTable.getColumnModel().getColumn(5).setPreferredWidth(150); // 启动时间

        JScrollPane scrollPane = new JScrollPane(processTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        scrollPane.setBorder(BorderFactory.createTitledBorder("进程列表"));

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(scrollPane, gbc);

        // 状态面板
        JPanel statusPanel = new JPanel(new BorderLayout());

        processCountLabel = new JLabel("进程数: 0");
        processCountLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        processCountLabel.setForeground(new Color(33, 150, 243));

        statusLabel = new JLabel("最后更新: " + timeFormat.format(new Date()));
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

        statusPanel.add(processCountLabel, BorderLayout.WEST);
        statusPanel.add(statusLabel, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(statusPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 设置窗口
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void refreshProcessList() {
        SwingWorker<List<ProcessInfo>, Void> worker = new SwingWorker<List<ProcessInfo>, Void>() {
            @Override
            protected List<ProcessInfo> doInBackground() throws Exception {
                return getProcessList();
            }

            @Override
            protected void done() {
                try {
                    List<ProcessInfo> processes = get();
                    tableModel.setProcesses(processes);
                    processCountLabel.setText("进程数: " + processes.size());
                    statusLabel.setText("最后更新: " + timeFormat.format(new Date()));
                } catch (Exception e) {
                    statusLabel.setText("更新失败: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private List<ProcessInfo> getProcessList() {
        List<ProcessInfo> processes = new ArrayList<>();

        try {
            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.contains("win")) {
                // Windows系统
                Process process = Runtime.getRuntime().exec("tasklist /FO CSV /NH");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    try {
                        // 解析CSV格式的tasklist输出
                        String[] parts = parseCSVLine(line);
                        if (parts.length >= 5) {
                            String name = parts[0].replace("\"", "").trim();
                            String pid = parts[1].replace("\"", "").trim();
                            String sessionName = parts[2].replace("\"", "").trim();
                            String sessionNum = parts[3].replace("\"", "").trim();
                            String memoryStr = parts[4].replace("\"", "").replace(",", "").trim();

                            int memoryKB = 0;
                            try {
                                memoryKB = Integer.parseInt(memoryStr);
                            } catch (NumberFormatException e) {
                                memoryKB = 0;
                            }

                            ProcessInfo info = new ProcessInfo(
                                pid, name, "运行中", "N/A", memoryKB + " KB", "N/A"
                            );
                            processes.add(info);
                        }
                    } catch (Exception e) {
                        // 跳过解析失败的行
                    }
                }
                reader.close();

            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
                // Unix/Linux/Mac系统
                Process process = Runtime.getRuntime().exec("ps -eo pid,comm,stat,pcpu,pmem,etime");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue; // 跳过标题行
                    }

                    if (line.trim().isEmpty()) continue;

                    try {
                        String[] parts = line.trim().split("\\s+", 6);
                        if (parts.length >= 6) {
                            String pid = parts[0];
                            String name = parts[1];
                            String status = parts[2];
                            String cpu = parts[3] + "%";
                            String mem = parts[4] + "%";
                            String time = parts[5];

                            ProcessInfo info = new ProcessInfo(pid, name, status, cpu, mem, time);
                            processes.add(info);
                        }
                    } catch (Exception ex) {
                        // 跳过解析失败的行
                    }
                }
                reader.close();
            }

        } catch (Exception e) {
            // 如果系统命令执行失败，添加一个示例进程
            processes.add(new ProcessInfo("1234", "示例进程", "运行中", "5.2%", "100 MB", "01:23:45"));
            processes.add(new ProcessInfo("5678", "系统进程", "睡眠", "2.1%", "50 MB", "12:34:56"));
        }

        return processes;
    }

    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    private void killSelectedProcess() {
        int selectedRow = processTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要结束的进程！", "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProcessInfo process = tableModel.getProcessAt(selectedRow);
        int result = JOptionPane.showConfirmDialog(this,
            "确定要结束进程 \"" + process.getName() + "\" (PID: " + process.getPid() + ") 吗？",
            "确认", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                String osName = System.getProperty("os.name").toLowerCase();
                Process killProcess;

                if (osName.contains("win")) {
                    killProcess = Runtime.getRuntime().exec("taskkill /F /PID " + process.getPid());
                } else {
                    killProcess = Runtime.getRuntime().exec("kill -9 " + process.getPid());
                }

                int exitCode = killProcess.waitFor();
                if (exitCode == 0) {
                    JOptionPane.showMessageDialog(this, "进程已结束！", "成功",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshProcessList();
                } else {
                    JOptionPane.showMessageDialog(this, "结束进程失败！", "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "结束进程时出错: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchProcesses() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            tableModel.clearFilter();
            return;
        }

        tableModel.filterProcesses(searchText);
    }

    private void toggleAutoRefresh() {
        if (autoRefreshCheck.isSelected()) {
            startAutoRefresh();
        } else {
            stopAutoRefresh();
        }
    }

    private void startAutoRefresh() {
        int interval = getRefreshInterval();
        if (refreshTimer != null) {
            refreshTimer.stop();
        }

        refreshTimer = new Timer(interval, e -> refreshProcessList());
        refreshTimer.start();
    }

    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }

    private void updateRefreshInterval() {
        if (autoRefreshCheck.isSelected()) {
            startAutoRefresh();
        }
    }

    private int getRefreshInterval() {
        String selected = (String) refreshIntervalCombo.getSelectedItem();
        switch (selected) {
            case "1秒": return 1000;
            case "2秒": return 2000;
            case "5秒": return 5000;
            case "10秒": return 10000;
            default: return 5000;
        }
    }

    @Override
    public void dispose() {
        stopAutoRefresh();
        super.dispose();
    }
}

// 进程信息类
static class ProcessInfo {
    private String pid;
    private String name;
    private String status;
    private String cpu;
    private String memory;
    private String startTime;

    public ProcessInfo(String pid, String name, String status, String cpu, String memory, String startTime) {
        this.pid = pid;
        this.name = name;
        this.status = status;
        this.cpu = cpu;
        this.memory = memory;
        this.startTime = startTime;
    }

    // Getters
    public String getPid() { return pid; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getCpu() { return cpu; }
    public String getMemory() { return memory; }
    public String getStartTime() { return startTime; }
}

// 表格模型
static class ProcessTableModel extends javax.swing.table.AbstractTableModel {
    private String[] columnNames = {"PID", "进程名称", "状态", "CPU", "内存", "运行时间"};
    private List<ProcessInfo> allProcesses = new ArrayList<>();
    private List<ProcessInfo> filteredProcesses = new ArrayList<>();
    private String currentFilter = "";

    @Override
    public int getRowCount() {
        return filteredProcesses.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProcessInfo process = filteredProcesses.get(rowIndex);
        switch (columnIndex) {
            case 0: return process.getPid();
            case 1: return process.getName();
            case 2: return process.getStatus();
            case 3: return process.getCpu();
            case 4: return process.getMemory();
            case 5: return process.getStartTime();
            default: return "";
        }
    }

    public void setProcesses(List<ProcessInfo> processes) {
        this.allProcesses = new ArrayList<>(processes);
        applyFilter();
    }

    public void filterProcesses(String filterText) {
        this.currentFilter = filterText.toLowerCase();
        applyFilter();
    }

    public void clearFilter() {
        this.currentFilter = "";
        applyFilter();
    }

    private void applyFilter() {
        filteredProcesses.clear();

        if (currentFilter.isEmpty()) {
            filteredProcesses.addAll(allProcesses);
        } else {
            for (ProcessInfo process : allProcesses) {
                if (process.getName().toLowerCase().contains(currentFilter) ||
                    process.getPid().toLowerCase().contains(currentFilter)) {
                    filteredProcesses.add(process);
                }
            }
        }

        fireTableDataChanged();
    }

    public ProcessInfo getProcessAt(int row) {
        return filteredProcesses.get(row);
    }
}