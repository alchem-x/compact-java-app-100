import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.CompletableFuture;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new NetworkTools().setVisible(true);
    });
}

static class NetworkTools extends JFrame {
    private final JTabbedPane tabbedPane;
    
    // Ping工具组件
    private final JTextField pingHostField;
    private final JTextArea pingResultArea;
    private final JButton pingButton;
    
    // 端口扫描组件
    private final JTextField scanHostField;
    private final JTextField startPortField;
    private final JTextField endPortField;
    private final JTextArea scanResultArea;
    private final JButton scanButton;
    
    // 网络信息组件
    private final JTextArea networkInfoArea;
    private final JButton refreshNetworkButton;
    
    // IP查询组件
    private final JTextField domainField;
    private final JTextArea ipResultArea;
    private final JButton lookupButton;
    
    public NetworkTools() {
        // 初始化组件
        tabbedPane = new JTabbedPane();
        
        // Ping工具
        pingHostField = new JTextField("www.baidu.com");
        pingResultArea = new JTextArea();
        pingButton = new JButton("Ping");
        
        // 端口扫描
        scanHostField = new JTextField("127.0.0.1");
        startPortField = new JTextField("20");
        endPortField = new JTextField("100");
        scanResultArea = new JTextArea();
        scanButton = new JButton("扫描");
        
        // 网络信息
        networkInfoArea = new JTextArea();
        refreshNetworkButton = new JButton("刷新网络信息");
        
        // IP查询
        domainField = new JTextField("www.google.com");
        ipResultArea = new JTextArea();
        lookupButton = new JButton("查询IP");
        
        initializeGUI();
        setupEventHandlers();
        loadNetworkInfo();
    }
    
    private void initializeGUI() {
        setTitle("🌐 网络工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 创建各个标签页
        createPingTab();
        createPortScanTab();
        createNetworkInfoTab();
        createIPLookupTab();
        
        add(tabbedPane, BorderLayout.CENTER);
        
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    private void createPingTab() {
        var pingPanel = new JPanel(new BorderLayout());
        
        // 输入面板
        var inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("主机地址:"));
        pingHostField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(pingHostField);
        inputPanel.add(pingButton);
        
        // 结果面板
        pingResultArea.setEditable(false);
        pingResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        var scrollPane = new JScrollPane(pingResultArea);
        
        pingPanel.add(inputPanel, BorderLayout.NORTH);
        pingPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("🏓 Ping测试", pingPanel);
    }
    
    private void createPortScanTab() {
        var scanPanel = new JPanel(new BorderLayout());
        
        // 输入面板
        var inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        
        var hostPanel = new JPanel(new FlowLayout());
        hostPanel.add(new JLabel("目标主机:"));
        scanHostField.setPreferredSize(new Dimension(150, 25));
        hostPanel.add(scanHostField);
        
        var portPanel = new JPanel(new FlowLayout());
        portPanel.add(new JLabel("端口范围:"));
        startPortField.setPreferredSize(new Dimension(60, 25));
        endPortField.setPreferredSize(new Dimension(60, 25));
        portPanel.add(startPortField);
        portPanel.add(new JLabel("到"));
        portPanel.add(endPortField);
        portPanel.add(scanButton);
        
        inputPanel.add(hostPanel);
        inputPanel.add(portPanel);
        
        // 结果面板
        scanResultArea.setEditable(false);
        scanResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        var scrollPane = new JScrollPane(scanResultArea);
        
        scanPanel.add(inputPanel, BorderLayout.NORTH);
        scanPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("🔍 端口扫描", scanPanel);
    }
    
    private void createNetworkInfoTab() {
        var infoPanel = new JPanel(new BorderLayout());
        
        // 控制面板
        var controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(refreshNetworkButton);
        
        // 信息显示面板
        networkInfoArea.setEditable(false);
        networkInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        var scrollPane = new JScrollPane(networkInfoArea);
        
        infoPanel.add(controlPanel, BorderLayout.NORTH);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("📡 网络信息", infoPanel);
    }
    
    private void createIPLookupTab() {
        var lookupPanel = new JPanel(new BorderLayout());
        
        // 输入面板
        var inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("域名:"));
        domainField.setPreferredSize(new Dimension(200, 25));
        inputPanel.add(domainField);
        inputPanel.add(lookupButton);
        
        // 结果面板
        ipResultArea.setEditable(false);
        ipResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        var scrollPane = new JScrollPane(ipResultArea);
        
        lookupPanel.add(inputPanel, BorderLayout.NORTH);
        lookupPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("🔎 IP查询", lookupPanel);
    }
    
    private void setupEventHandlers() {
        pingButton.addActionListener(this::performPing);
        scanButton.addActionListener(this::performPortScan);
        refreshNetworkButton.addActionListener(e -> loadNetworkInfo());
        lookupButton.addActionListener(this::performIPLookup);
        
        // 回车键快捷操作
        pingHostField.addActionListener(this::performPing);
        domainField.addActionListener(this::performIPLookup);
    }
    
    private void performPing(ActionEvent e) {
        var host = pingHostField.getText().trim();
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入主机地址", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        pingButton.setEnabled(false);
        pingResultArea.setText("正在Ping " + host + "...\n");
        
        CompletableFuture.runAsync(() -> {
            try {
                var address = InetAddress.getByName(host);
                var result = new StringBuilder();
                result.append("Ping ").append(host).append(" [").append(address.getHostAddress()).append("]\n\n");
                
                for (int i = 1; i <= 4; i++) {
                    var startTime = System.currentTimeMillis();
                    var reachable = address.isReachable(5000);
                    var endTime = System.currentTimeMillis();
                    var responseTime = endTime - startTime;
                    
                    if (reachable) {
                        result.append("来自 ").append(address.getHostAddress())
                              .append(" 的回复: 时间=").append(responseTime).append("ms\n");
                    } else {
                        result.append("请求超时\n");
                    }
                    
                    SwingUtilities.invokeLater(() -> pingResultArea.setText(result.toString()));
                    
                    if (i < 4) {
                        Thread.sleep(1000);
                    }
                }
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> 
                    pingResultArea.setText("Ping失败: " + ex.getMessage()));
            } finally {
                SwingUtilities.invokeLater(() -> pingButton.setEnabled(true));
            }
        });
    }
    
    private void performPortScan(ActionEvent e) {
        var host = scanHostField.getText().trim();
        var startPortText = startPortField.getText().trim();
        var endPortText = endPortField.getText().trim();
        
        if (host.isEmpty() || startPortText.isEmpty() || endPortText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整信息", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            var startPort = Integer.parseInt(startPortText);
            var endPort = Integer.parseInt(endPortText);
            
            if (startPort < 1 || endPort > 65535 || startPort > endPort) {
                JOptionPane.showMessageDialog(this, "端口范围无效 (1-65535)", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            scanButton.setEnabled(false);
            scanResultArea.setText("正在扫描 " + host + " 端口 " + startPort + "-" + endPort + "...\n\n");
            
            CompletableFuture.runAsync(() -> {
                var result = new StringBuilder();
                result.append("扫描结果:\n");
                var openPorts = 0;
                
                for (int port = startPort; port <= endPort; port++) {
                    try (var socket = new Socket()) {
                        socket.connect(new InetSocketAddress(host, port), 1000);
                        result.append("端口 ").append(port).append(": 开放\n");
                        openPorts++;
                    } catch (IOException ignored) {
                        // 端口关闭或不可达
                    }
                    
                    // 更新进度
                    var progress = (port - startPort + 1) * 100 / (endPort - startPort + 1);
                    var finalResult = result.toString() + "\n进度: " + progress + "%";
                    SwingUtilities.invokeLater(() -> scanResultArea.setText(finalResult));
                }
                
                result.append("\n扫描完成，发现 ").append(openPorts).append(" 个开放端口");
                SwingUtilities.invokeLater(() -> {
                    scanResultArea.setText(result.toString());
                    scanButton.setEnabled(true);
                });
            });
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "端口号必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performIPLookup(ActionEvent e) {
        var domain = domainField.getText().trim();
        if (domain.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入域名", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        lookupButton.setEnabled(false);
        ipResultArea.setText("正在查询 " + domain + "...\n");
        
        CompletableFuture.runAsync(() -> {
            try {
                var addresses = InetAddress.getAllByName(domain);
                var result = new StringBuilder();
                result.append("域名: ").append(domain).append("\n\n");
                
                for (var addr : addresses) {
                    result.append("IP地址: ").append(addr.getHostAddress()).append("\n");
                    result.append("主机名: ").append(addr.getHostName()).append("\n");
                    result.append("规范名: ").append(addr.getCanonicalHostName()).append("\n");
                    result.append("类型: ").append(addr instanceof Inet4Address ? "IPv4" : "IPv6").append("\n");
                    result.append("可达性: ").append(addr.isReachable(3000) ? "可达" : "不可达").append("\n\n");
                }
                
                SwingUtilities.invokeLater(() -> ipResultArea.setText(result.toString()));
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> 
                    ipResultArea.setText("查询失败: " + ex.getMessage()));
            } finally {
                SwingUtilities.invokeLater(() -> lookupButton.setEnabled(true));
            }
        });
    }
    
    private void loadNetworkInfo() {
        CompletableFuture.runAsync(() -> {
            try {
                var info = new StringBuilder();
                info.append("=== 本机网络信息 ===\n\n");
                
                // 本机信息
                var localhost = InetAddress.getLocalHost();
                info.append("主机名: ").append(localhost.getHostName()).append("\n");
                info.append("本机IP: ").append(localhost.getHostAddress()).append("\n\n");
                
                // 网络接口信息
                info.append("=== 网络接口 ===\n");
                var interfaces = NetworkInterface.getNetworkInterfaces();
                
                while (interfaces.hasMoreElements()) {
                    var netInterface = interfaces.nextElement();
                    info.append("\n接口名称: ").append(netInterface.getDisplayName()).append("\n");
                    info.append("接口标识: ").append(netInterface.getName()).append("\n");
                    info.append("状态: ").append(netInterface.isUp() ? "启用" : "禁用").append("\n");
                    info.append("回环接口: ").append(netInterface.isLoopback() ? "是" : "否").append("\n");
                    
                    var addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        var addr = addresses.nextElement();
                        info.append("  IP地址: ").append(addr.getHostAddress());
                        info.append(" (").append(addr instanceof Inet4Address ? "IPv4" : "IPv6").append(")\n");
                    }
                }
                
                SwingUtilities.invokeLater(() -> networkInfoArea.setText(info.toString()));
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> 
                    networkInfoArea.setText("获取网络信息失败: " + ex.getMessage()));
            }
        });
    }
}
