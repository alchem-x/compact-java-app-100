import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.util.Properties;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new SystemMonitor().setVisible(true);
    });
}

static class SystemMonitor extends JFrame {
    private final JLabel cpuLabel;
    private final JLabel memoryLabel;
    private final JLabel diskLabel;
    private final JProgressBar memoryBar;
    private final JProgressBar diskBar;
    private final JTextArea systemInfoArea;
    private final Timer updateTimer;
    
    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;
    private final RuntimeMXBean runtimeBean;
    private final DecimalFormat df = new DecimalFormat("#.##");
    
    public SystemMonitor() {
        osBean = ManagementFactory.getOperatingSystemMXBean();
        memoryBean = ManagementFactory.getMemoryMXBean();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        
        cpuLabel = new JLabel("CPU使用率: 获取中...");
        memoryLabel = new JLabel("内存使用率: 获取中...");
        diskLabel = new JLabel("磁盘空间: 获取中...");
        memoryBar = new JProgressBar(0, 100);
        diskBar = new JProgressBar(0, 100);
        systemInfoArea = new JTextArea();
        
        updateTimer = new Timer(2000, e -> updateSystemInfo());
        
        initializeGUI();
        loadSystemInfo();
        updateTimer.start();
    }
    
    private void initializeGUI() {
        setTitle("💻 系统监视器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 创建主面板
        var mainPanel = new JPanel(new BorderLayout());
        
        // 顶部实时监控面板
        createMonitorPanel(mainPanel);
        
        // 中间系统信息面板
        createSystemInfoPanel(mainPanel);
        
        // 底部控制面板
        createControlPanel(mainPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        setSize(600, 500);
        setLocationRelativeTo(null);
    }
    
    private void createMonitorPanel(JPanel parent) {
        var monitorPanel = new JPanel();
        monitorPanel.setLayout(new BoxLayout(monitorPanel, BoxLayout.Y_AXIS));
        monitorPanel.setBorder(BorderFactory.createTitledBorder("实时监控"));
        monitorPanel.setPreferredSize(new Dimension(0, 150));
        
        // CPU信息
        var cpuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cpuLabel.setFont(new Font("Arial", Font.BOLD, 12));
        cpuPanel.add(cpuLabel);
        
        // 内存信息
        var memoryPanel = new JPanel(new BorderLayout());
        memoryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        memoryBar.setStringPainted(true);
        memoryBar.setString("0%");
        memoryPanel.add(memoryLabel, BorderLayout.NORTH);
        memoryPanel.add(memoryBar, BorderLayout.CENTER);
        
        // 磁盘信息
        var diskPanel = new JPanel(new BorderLayout());
        diskLabel.setFont(new Font("Arial", Font.BOLD, 12));
        diskBar.setStringPainted(true);
        diskBar.setString("0%");
        diskPanel.add(diskLabel, BorderLayout.NORTH);
        diskPanel.add(diskBar, BorderLayout.CENTER);
        
        monitorPanel.add(cpuPanel);
        monitorPanel.add(Box.createVerticalStrut(10));
        monitorPanel.add(memoryPanel);
        monitorPanel.add(Box.createVerticalStrut(10));
        monitorPanel.add(diskPanel);
        
        parent.add(monitorPanel, BorderLayout.NORTH);
    }
    
    private void createSystemInfoPanel(JPanel parent) {
        var infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("系统信息"));
        
        systemInfoArea.setEditable(false);
        systemInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        systemInfoArea.setBackground(new Color(248, 248, 248));
        
        var scrollPane = new JScrollPane(systemInfoArea);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        parent.add(infoPanel, BorderLayout.CENTER);
    }
    
    private void createControlPanel(JPanel parent) {
        var controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createEtchedBorder());
        
        var refreshButton = new JButton("🔄 刷新");
        refreshButton.addActionListener(e -> {
            loadSystemInfo();
            updateSystemInfo();
        });
        
        var gcButton = new JButton("🗑️ 垃圾回收");
        gcButton.addActionListener(e -> {
            System.gc();
            JOptionPane.showMessageDialog(this, "已执行垃圾回收", "信息", JOptionPane.INFORMATION_MESSAGE);
            updateSystemInfo();
        });
        
        var exitButton = new JButton("❌ 退出");
        exitButton.addActionListener(e -> {
            updateTimer.stop();
            System.exit(0);
        });
        
        controlPanel.add(refreshButton);
        controlPanel.add(gcButton);
        controlPanel.add(exitButton);
        
        parent.add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void loadSystemInfo() {
        var info = new StringBuilder();
        var props = System.getProperties();
        
        info.append("=== 操作系统信息 ===\n");
        info.append("操作系统: ").append(props.getProperty("os.name")).append("\n");
        info.append("系统版本: ").append(props.getProperty("os.version")).append("\n");
        info.append("系统架构: ").append(props.getProperty("os.arch")).append("\n");
        info.append("处理器数量: ").append(osBean.getAvailableProcessors()).append(" 核\n");
        
        info.append("\n=== Java 运行时信息 ===\n");
        info.append("Java版本: ").append(props.getProperty("java.version")).append("\n");
        info.append("Java厂商: ").append(props.getProperty("java.vendor")).append("\n");
        info.append("JVM名称: ").append(props.getProperty("java.vm.name")).append("\n");
        info.append("JVM版本: ").append(props.getProperty("java.vm.version")).append("\n");
        
        var uptime = runtimeBean.getUptime();
        var hours = uptime / (1000 * 60 * 60);
        var minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
        var seconds = (uptime % (1000 * 60)) / 1000;
        info.append("JVM运行时间: ").append(hours).append("h ").append(minutes).append("m ").append(seconds).append("s\n");
        
        info.append("\n=== 用户信息 ===\n");
        info.append("用户名: ").append(props.getProperty("user.name")).append("\n");
        info.append("用户主目录: ").append(props.getProperty("user.home")).append("\n");
        info.append("当前工作目录: ").append(props.getProperty("user.dir")).append("\n");
        
        info.append("\n=== 文件系统信息 ===\n");
        info.append("文件分隔符: ").append(props.getProperty("file.separator")).append("\n");
        info.append("路径分隔符: ").append(props.getProperty("path.separator")).append("\n");
        info.append("行分隔符: ").append(props.getProperty("line.separator").replace("\n", "\\n").replace("\r", "\\r")).append("\n");
        
        systemInfoArea.setText(info.toString());
    }
    
    private void updateSystemInfo() {
        // 更新内存信息
        var memory = memoryBean.getHeapMemoryUsage();
        var usedMemory = memory.getUsed();
        var maxMemory = memory.getMax();
        var memoryPercent = (int) ((usedMemory * 100.0) / maxMemory);
        
        var usedMB = usedMemory / (1024 * 1024);
        var maxMB = maxMemory / (1024 * 1024);
        
        memoryLabel.setText(String.format("内存使用: %d MB / %d MB (%d%%)", usedMB, maxMB, memoryPercent));
        memoryBar.setValue(memoryPercent);
        memoryBar.setString(memoryPercent + "%");
        
        // 设置内存条颜色
        if (memoryPercent > 80) {
            memoryBar.setForeground(Color.RED);
        } else if (memoryPercent > 60) {
            memoryBar.setForeground(Color.ORANGE);
        } else {
            memoryBar.setForeground(Color.GREEN);
        }
        
        // 更新磁盘信息（使用用户主目录所在磁盘）
        var homeDir = new java.io.File(System.getProperty("user.home"));
        var totalSpace = homeDir.getTotalSpace();
        var freeSpace = homeDir.getFreeSpace();
        var usedSpace = totalSpace - freeSpace;
        var diskPercent = (int) ((usedSpace * 100.0) / totalSpace);
        
        var totalGB = totalSpace / (1024 * 1024 * 1024);
        var usedGB = usedSpace / (1024 * 1024 * 1024);
        var freeGB = freeSpace / (1024 * 1024 * 1024);
        
        diskLabel.setText(String.format("磁盘使用: %d GB / %d GB (剩余: %d GB)", usedGB, totalGB, freeGB));
        diskBar.setValue(diskPercent);
        diskBar.setString(diskPercent + "%");
        
        // 设置磁盘条颜色
        if (diskPercent > 90) {
            diskBar.setForeground(Color.RED);
        } else if (diskPercent > 75) {
            diskBar.setForeground(Color.ORANGE);
        } else {
            diskBar.setForeground(Color.BLUE);
        }
        
        // 更新CPU信息（简化版）
        var loadAverage = osBean.getSystemLoadAverage();
        if (loadAverage >= 0) {
            var cpuPercent = (int) (loadAverage * 100 / osBean.getAvailableProcessors());
            cpuLabel.setText(String.format("CPU负载: %.2f (约%d%%)", loadAverage, Math.min(cpuPercent, 100)));
        } else {
            cpuLabel.setText("CPU负载: 不可用");
        }
    }
}
