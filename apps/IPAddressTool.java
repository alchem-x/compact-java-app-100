import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.net.InetAddress;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🌐 IP地址工具";

    // 输入面板
    static final String INPUT_PANEL_TITLE = "IP地址输入";
    static final String IP_ADDRESS_LABEL = "IP地址:";
    static final String SUBNET_MASK_LABEL = "子网掩码:";
    static final String HELP_TEXT = "<html><small>支持CIDR格式 (如: 192.168.1.1/24) 或分别输入IP和子网掩码</small></html>";

    // 结果面板
    static final String RESULT_PANEL_TITLE = "分析结果";

    // 控制按钮
    static final String ANALYZE_BUTTON = "分析";
    static final String CLEAR_BUTTON = "清空";
    static final String COPY_BUTTON = "复制结果";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_ANALYSIS_COMPLETE = "分析完成";
    static final String STATUS_ANALYSIS_FAILED = "分析失败";
    static final String STATUS_NO_CONTENT_TO_COPY = "没有可复制的内容";
    static final String STATUS_RESULT_COPIED = "结果已复制到剪贴板";
    static final String STATUS_CLEARED = "已清空";
    static final String STATUS_PLEASE_ENTER_IP = "请输入IP地址";
    static final String STATUS_PLEASE_ENTER_SUBNET_OR_CIDR = "请输入子网掩码或使用CIDR格式";
    static final String STATUS_ANALYSIS_FAILED_WITH_ERROR = "分析失败: ";

    // 错误消息
    static final String ERROR_INVALID_IP_FORMAT = "IP地址格式错误";
    static final String ERROR_IP_FIELD_OUT_OF_RANGE = "IP地址字段超出范围 (0-255)";
    static final String ERROR_PREFIX_LENGTH_OUT_OF_RANGE = "前缀长度必须在0-32之间";
    static final String ERROR_FAILED_ANALYSIS = "分析失败: ";

    // 分析结果
    static final String RESULT_HEADER = "=== IP地址分析结果 ===\n\n";
    static final String RESULT_IP_ADDRESS = "IP地址:";
    static final String RESULT_CIDR_NOTATION = "CIDR表示:";
    static final String RESULT_SUBNET_MASK = "子网掩码:";
    static final String RESULT_WILDCARD_MASK = "通配符掩码:";
    static final String RESULT_NETWORK_ADDRESS = "网络地址:";
    static final String RESULT_BROADCAST_ADDRESS = "广播地址:";
    static final String RESULT_HOST_RANGE = "主机范围:";
    static final String RESULT_TOTAL_ADDRESSES = "总地址数:";
    static final String RESULT_USABLE_HOSTS = "可用主机数:";
    static final String RESULT_ADDRESS_CLASS = "地址类别:";
    static final String RESULT_ADDRESS_TYPE = "地址类型:";
    static final String RESULT_BINARY_REPRESENTATION = "二进制表示:";
    static final String RESULT_SECURITY_ANALYSIS = "=== 网络安全分析 ===";
    static final String RESULT_PERFORMANCE_RECOMMENDATIONS = "=== 网络性能建议 ===";
    static final String RESULT_PORT_RECOMMENDATIONS = "=== 常见端口建议 ===";

    // IP类型
    static final String IP_CLASS_A = "A类";
    static final String IP_CLASS_B = "B类";
    static final String IP_CLASS_C = "C类";
    static final String IP_CLASS_D = "D类 (组播)";
    static final String IP_CLASS_E = "E类 (保留)";
    static final String IP_CLASS_UNKNOWN = "未知";

    static final String IP_TYPE_PRIVATE_A = "私有地址 (A类)";
    static final String IP_TYPE_PRIVATE_B = "私有地址 (B类)";
    static final String IP_TYPE_PRIVATE_C = "私有地址 (C类)";
    static final String IP_TYPE_LOOPBACK = "回环地址";
    static final String IP_TYPE_MULTICAST = "组播/保留地址";
    static final String IP_TYPE_PUBLIC = "公网地址";

    // 安全分析
    static final String SECURITY_PRIVATE_IP = "✅ 私有IP地址 - 适合内部网络使用\n建议: 配置防火墙规则保护内部网络\n";
    static final String SECURITY_LOOPBACK_IP = "✅ 回环地址 - 本地测试使用\n建议: 适合开发和测试环境\n";
    static final String SECURITY_MULTICAST_IP = "⚠️ 组播地址 - 特殊用途\n建议: 确保网络设备支持组播协议\n";
    static final String SECURITY_PUBLIC_IP = "🌐 公网IP地址 - 需要安全防护\n建议: 配置防火墙、入侵检测系统\n";
    static final String SECURITY_LARGE_SUBNET = "⚠️ 子网较大 - 可能存在广播风暴风险\n建议: 考虑划分子网提高安全性\n";
    static final String SECURITY_SMALL_SUBNET = "✅ 子网较小 - 安全性较好";
    static final String SECURITY_MEDIUM_SUBNET = "✅ 子网大小适中";

    // 性能建议
    static final String PERFORMANCE_LARGE_SUBNET = "⚠️ 大型子网 - 可能影响网络性能\n建议: 实施VLAN分段，减少广播域\n建议: 启用IGMP Snooping优化组播\n";
    static final String PERFORMANCE_MEDIUM_SUBNET = "📊 中型子网 - 性能适中\n建议: 监控网络流量，适时优化\n";
    static final String PERFORMANCE_SMALL_SUBNET = "✅ 小型子网 - 性能最佳\n建议: 适合高安全性要求的环境\n";
    static final String PERFORMANCE_PREFIX_LENGTH_SUGGESTION = "建议: 考虑使用更小的子网提高管理效率\n";
    static final String PERFORMANCE_MONITORING_SUGGESTION = "建议: 定期监控网络利用率和错误率\n";

    // 端口建议
    static final String PORT_RECOMMENDATIONS_HEADER = "常用服务端口:\n";
    static final String PORT_HTTP = "• HTTP (80): Web服务\n";
    static final String PORT_HTTPS = "• HTTPS (443): 安全Web服务\n";
    static final String PORT_SSH = "• SSH (22): 远程管理\n";
    static final String PORT_RDP = "• RDP (3389): Windows远程桌面\n";
    static final String PORT_FTP = "• FTP (21): 文件传输\n";
    static final String PORT_SMTP = "• SMTP (25): 邮件发送\n";
    static final String PORT_DNS = "• DNS (53): 域名解析\n";
    static final String PORT_DHCP = "• DHCP (67/68): 动态IP分配\n\n";
    static final String PORT_SECURITY_HEADER = "安全建议:\n";
    static final String PORT_CLOSE_UNNECESSARY = "• 关闭不必要的服务端口\n";
    static final String PORT_FIREWALL = "• 使用防火墙限制访问\n";
    static final String PORT_SCAN = "• 定期扫描开放端口\n";
    static final String PORT_MONITOR = "• 监控异常端口活动\n";

    // 示例数据
    static final String SAMPLE_IP = "192.168.1.100";
    static final String SAMPLE_SUBNET = "255.255.255.0";
}

// Apple 设计系统常量
class AppleDesign {
    // 颜色系统
    static final Color SYSTEM_BLUE = new Color(0, 122, 255);
    static final Color SYSTEM_GREEN = new Color(52, 199, 89);
    static final Color SYSTEM_INDIGO = new Color(88, 86, 214);
    static final Color SYSTEM_ORANGE = new Color(255, 149, 0);
    static final Color SYSTEM_PINK = new Color(255, 45, 85);
    static final Color SYSTEM_PURPLE = new Color(175, 82, 222);
    static final Color SYSTEM_RED = new Color(255, 59, 48);
    static final Color SYSTEM_TEAL = new Color(90, 200, 250);
    static final Color SYSTEM_YELLOW = new Color(255, 204, 0);

    // 灰色系统
    static final Color SYSTEM_GRAY = new Color(142, 142, 147);
    static final Color SYSTEM_GRAY2 = new Color(174, 174, 178);
    static final Color SYSTEM_GRAY3 = new Color(199, 199, 204);
    static final Color SYSTEM_GRAY4 = new Color(209, 209, 214);
    static final Color SYSTEM_GRAY5 = new Color(229, 229, 234);
    static final Color SYSTEM_GRAY6 = new Color(242, 242, 247);

    // 背景色
    static final Color SECONDARY_SYSTEM_BACKGROUND = new Color(247, 247, 247);
    static final Color TERTIARY_SYSTEM_BACKGROUND = new Color(255, 255, 255);

    // 字体系统
    static final Font HEADLINE_FONT = new Font("SF Pro Display", Font.BOLD, 24);
    static final Font TITLE_FONT = new Font("SF Pro Display", Font.BOLD, 18);
    static final Font BODY_FONT = new Font("SF Pro Text", Font.PLAIN, 14);
    static final Font CALLOUT_FONT = new Font("SF Pro Text", Font.PLAIN, 12);
    static final Font CAPTION_FONT = new Font("SF Pro Text", Font.PLAIN, 11);
    static final Font MONO_FONT = new Font("SF Mono", Font.PLAIN, 12);

    // 间距系统
    static final int SMALL_SPACING = 4;
    static final int MEDIUM_SPACING = 8;
    static final int LARGE_SPACING = 16;
    static final int EXTRA_LARGE_SPACING = 20;

    // 圆角系统
    static final int SMALL_RADIUS = 4;
    static final int MEDIUM_RADIUS = 8;
    static final int LARGE_RADIUS = 12;
    static final int EXTRA_LARGE_RADIUS = 16;

    // 按钮样式
    static void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(BODY_FONT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    // 创建圆角面板
    static JPanel createRoundedPanel(int radius, Color bgColor) {
        var panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                var g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new IPAddressTool().setVisible(true);
    });
}

static class IPAddressTool extends JFrame {
    private final JTextField ipField;
    private final JTextField subnetField;
    private final JTextArea resultArea;
    private final JButton analyzeButton;
    private final JButton clearButton;
    private final JButton copyButton;
    private final JLabel statusLabel;
    
    public IPAddressTool() {
        ipField = new JTextField();
        subnetField = new JTextField();
        resultArea = new JTextArea();
        analyzeButton = new JButton(Texts.ANALYZE_BUTTON);
        clearButton = new JButton(Texts.CLEAR_BUTTON);
        copyButton = new JButton(Texts.COPY_BUTTON);
        statusLabel = new JLabel(Texts.STATUS_READY);
        
        initializeGUI();
        setupEventHandlers();
        loadSampleData();
        setupKeyboardShortcuts();
    }
    
    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        
        // 创建输入面板
        createInputPanel();
        
        // 创建结果面板
        createResultPanel();
        
        // 创建控制面板
        createControlPanel();
        
        // 创建状态栏
        createStatusBar();
        
        setSize(600, 500);
        setLocationRelativeTo(null);
    }
    
    private void createInputPanel() {
        var inputPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        // 标题
        var titleLabel = new JLabel(Texts.INPUT_PANEL_TITLE);
        titleLabel.setFont(AppleDesign.TITLE_FONT);
        titleLabel.setForeground(AppleDesign.SYSTEM_BLUE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, AppleDesign.MEDIUM_SPACING, 0));
        inputPanel.add(titleLabel);

        // IP地址输入
        var ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ipPanel.setOpaque(false);
        var ipLabel = new JLabel(Texts.IP_ADDRESS_LABEL);
        ipLabel.setFont(AppleDesign.BODY_FONT);
        ipLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        ipPanel.add(ipLabel);
        ipField.setPreferredSize(new Dimension(150, 25));
        ipField.setFont(AppleDesign.MONO_FONT);
        ipField.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        ipField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppleDesign.SYSTEM_GRAY4),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        ipPanel.add(ipField);

        // 子网掩码输入
        var subnetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subnetPanel.setOpaque(false);
        var subnetLabel = new JLabel(Texts.SUBNET_MASK_LABEL);
        subnetLabel.setFont(AppleDesign.BODY_FONT);
        subnetLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        subnetPanel.add(subnetLabel);
        subnetField.setPreferredSize(new Dimension(150, 25));
        subnetField.setFont(AppleDesign.MONO_FONT);
        subnetField.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        subnetField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppleDesign.SYSTEM_GRAY4),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        subnetPanel.add(subnetField);

        // 添加说明
        var helpLabel = new JLabel(Texts.HELP_TEXT);
        helpLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        helpLabel.setFont(AppleDesign.CAPTION_FONT);

        inputPanel.add(ipPanel);
        inputPanel.add(Box.createVerticalStrut(AppleDesign.SMALL_SPACING));
        inputPanel.add(subnetPanel);
        inputPanel.add(Box.createVerticalStrut(AppleDesign.MEDIUM_SPACING));
        inputPanel.add(helpLabel);

        add(inputPanel, BorderLayout.NORTH);
    }
    
    private void createResultPanel() {
        var resultPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        resultPanel.setLayout(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        // 标题
        var titleLabel = new JLabel(Texts.RESULT_PANEL_TITLE);
        titleLabel.setFont(AppleDesign.TITLE_FONT);
        titleLabel.setForeground(AppleDesign.SYSTEM_GREEN);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, AppleDesign.MEDIUM_SPACING, 0));
        resultPanel.add(titleLabel, BorderLayout.NORTH);

        resultArea.setFont(AppleDesign.MONO_FONT);
        resultArea.setEditable(false);
        resultArea.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        resultArea.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));

        var scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        add(resultPanel, BorderLayout.CENTER);
    }
    
    private void createControlPanel() {
        var controlPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.LARGE_SPACING));

        AppleDesign.styleButton(analyzeButton, AppleDesign.SYSTEM_BLUE, Color.WHITE);
        AppleDesign.styleButton(clearButton, AppleDesign.SYSTEM_GRAY, Color.WHITE);
        AppleDesign.styleButton(copyButton, AppleDesign.SYSTEM_INDIGO, Color.WHITE);

        controlPanel.add(analyzeButton);
        controlPanel.add(clearButton);
        controlPanel.add(copyButton);

        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void createStatusBar() {
        var statusPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.SMALL_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.SMALL_SPACING, AppleDesign.LARGE_SPACING));
        statusLabel.setFont(AppleDesign.CAPTION_FONT);
        statusLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private void setupEventHandlers() {
        analyzeButton.addActionListener(this::analyzeIP);
        clearButton.addActionListener(this::clearAll);
        copyButton.addActionListener(this::copyResult);
        ipField.addActionListener(this::analyzeIP);
        subnetField.addActionListener(this::analyzeIP);
    }
    
    private void loadSampleData() {
        ipField.setText(Texts.SAMPLE_IP);
        subnetField.setText(Texts.SAMPLE_SUBNET);
    }
    
    private void analyzeIP(ActionEvent e) {
        var ipText = ipField.getText().trim();
        var subnetText = subnetField.getText().trim();
        
        if (ipText.isEmpty()) {
            statusLabel.setText(Texts.STATUS_PLEASE_ENTER_IP);
            return;
        }
        
        try {
            String ip;
            int prefixLength;
            
            // 检查是否是CIDR格式
            if (ipText.contains("/")) {
                var parts = ipText.split("/");
                ip = parts[0];
                prefixLength = Integer.parseInt(parts[1]);
            } else {
                ip = ipText;
                if (subnetText.isEmpty()) {
                    statusLabel.setText(Texts.STATUS_PLEASE_ENTER_SUBNET_OR_CIDR);
                    return;
                }
                prefixLength = subnetMaskToPrefixLength(subnetText);
            }
            
            // 验证IP地址
            var ipBytes = parseIPAddress(ip);
            
            // 计算网络信息
            var analysis = analyzeNetwork(ipBytes, prefixLength);
            resultArea.setText(analysis);
            
            statusLabel.setText(Texts.STATUS_ANALYSIS_COMPLETE);
            
        } catch (Exception ex) {
            resultArea.setText(Texts.STATUS_ANALYSIS_FAILED_WITH_ERROR + ex.getMessage());
            statusLabel.setText(Texts.STATUS_ANALYSIS_FAILED);
        }
    }
    
    private byte[] parseIPAddress(String ip) throws Exception {
        var parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new Exception(Texts.ERROR_INVALID_IP_FORMAT);
        }
        
        var bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            var value = Integer.parseInt(parts[i]);
            if (value < 0 || value > 255) {
                throw new Exception(Texts.ERROR_IP_FIELD_OUT_OF_RANGE);
            }
            bytes[i] = (byte) value;
        }
        return bytes;
    }
    
    private int subnetMaskToPrefixLength(String subnetMask) throws Exception {
        var maskBytes = parseIPAddress(subnetMask);
        var mask = ((maskBytes[0] & 0xFF) << 24) |
                   ((maskBytes[1] & 0xFF) << 16) |
                   ((maskBytes[2] & 0xFF) << 8) |
                   (maskBytes[3] & 0xFF);
        
        // 计算前缀长度
        var prefixLength = Integer.numberOfLeadingZeros(~mask);
        if (prefixLength > 32) prefixLength = 32;
        
        return prefixLength;
    }
    
    private String analyzeNetwork(byte[] ipBytes, int prefixLength) throws Exception {
        if (prefixLength < 0 || prefixLength > 32) {
            throw new Exception(Texts.ERROR_PREFIX_LENGTH_OUT_OF_RANGE);
        }

        var result = new StringBuilder();
        result.append(Texts.RESULT_HEADER);

        // IP地址信息
        var ipStr = String.format("%d.%d.%d.%d",
            ipBytes[0] & 0xFF, ipBytes[1] & 0xFF, ipBytes[2] & 0xFF, ipBytes[3] & 0xFF);
        result.append(Texts.RESULT_IP_ADDRESS).append(" ").append(ipStr).append("\n");
        result.append(Texts.RESULT_CIDR_NOTATION).append(" ").append(ipStr).append("/").append(prefixLength).append("\n");

        // 子网掩码
        var subnetMask = prefixLengthToSubnetMask(prefixLength);
        result.append(Texts.RESULT_SUBNET_MASK).append(" ").append(subnetMask).append("\n");

        // 通配符掩码
        var wildcardMask = prefixLengthToWildcardMask(prefixLength);
        result.append(Texts.RESULT_WILDCARD_MASK).append(" ").append(wildcardMask).append("\n\n");

        // 网络地址
        var networkBytes = calculateNetworkAddress(ipBytes, prefixLength);
        var networkStr = String.format("%d.%d.%d.%d",
            networkBytes[0] & 0xFF, networkBytes[1] & 0xFF, networkBytes[2] & 0xFF, networkBytes[3] & 0xFF);
        result.append(Texts.RESULT_NETWORK_ADDRESS).append(" ").append(networkStr).append("\n");

        // 广播地址
        var broadcastBytes = calculateBroadcastAddress(networkBytes, prefixLength);
        var broadcastStr = String.format("%d.%d.%d.%d",
            broadcastBytes[0] & 0xFF, broadcastBytes[1] & 0xFF, broadcastBytes[2] & 0xFF, broadcastBytes[3] & 0xFF);
        result.append(Texts.RESULT_BROADCAST_ADDRESS).append(" ").append(broadcastStr).append("\n");

        // 可用主机范围
        if (prefixLength < 31) {
            var firstHost = incrementIP(networkBytes);
            var lastHost = decrementIP(broadcastBytes);
            result.append(Texts.RESULT_HOST_RANGE).append(" ").append(formatIP(firstHost)).append(" - ").append(formatIP(lastHost)).append("\n");
        }

        // 主机数量
        var hostBits = 32 - prefixLength;
        var totalHosts = (long) Math.pow(2, hostBits);
        var usableHosts = Math.max(0, totalHosts - 2); // 减去网络地址和广播地址

        result.append(Texts.RESULT_TOTAL_ADDRESSES).append(" ").append(totalHosts).append("\n");
        result.append(Texts.RESULT_USABLE_HOSTS).append(" ").append(usableHosts).append("\n\n");

        // IP地址类型
        result.append("=== 地址类型 ===\n");
        result.append(Texts.RESULT_ADDRESS_CLASS).append(" ").append(getIPClass(ipBytes)).append("\n");
        result.append(Texts.RESULT_ADDRESS_TYPE).append(" ").append(getIPType(ipBytes)).append("\n");

        // 二进制表示
        result.append("\n=== 二进制表示 ===\n");
        result.append(Texts.RESULT_BINARY_REPRESENTATION).append(" ").append(toBinaryString(ipBytes)).append("\n");
        result.append(Texts.RESULT_BINARY_REPRESENTATION).append(" ").append(toBinaryString(parseIPAddress(subnetMask))).append("\n");

        // 增强分析 - 安全性和网络建议
        result.append("\n").append(Texts.RESULT_SECURITY_ANALYSIS).append("\n");
        result.append(getSecurityAnalysis(ipBytes, prefixLength));

        result.append("\n").append(Texts.RESULT_PERFORMANCE_RECOMMENDATIONS).append("\n");
        result.append(getPerformanceRecommendations(prefixLength));

        result.append("\n").append(Texts.RESULT_PORT_RECOMMENDATIONS).append("\n");
        result.append(getPortRecommendations());

        return result.toString();
    }
    
    private String prefixLengthToSubnetMask(int prefixLength) {
        var mask = 0xFFFFFFFFL << (32 - prefixLength);
        return String.format("%d.%d.%d.%d",
            (mask >> 24) & 0xFF, (mask >> 16) & 0xFF, (mask >> 8) & 0xFF, mask & 0xFF);
    }
    
    private String prefixLengthToWildcardMask(int prefixLength) {
        var mask = 0xFFFFFFFFL >>> prefixLength;
        return String.format("%d.%d.%d.%d",
            (mask >> 24) & 0xFF, (mask >> 16) & 0xFF, (mask >> 8) & 0xFF, mask & 0xFF);
    }
    
    private byte[] calculateNetworkAddress(byte[] ip, int prefixLength) {
        var mask = 0xFFFFFFFFL << (32 - prefixLength);
        var ipInt = ((ip[0] & 0xFF) << 24) | ((ip[1] & 0xFF) << 16) | ((ip[2] & 0xFF) << 8) | (ip[3] & 0xFF);
        var networkInt = ipInt & mask;
        
        return new byte[]{
            (byte) ((networkInt >> 24) & 0xFF),
            (byte) ((networkInt >> 16) & 0xFF),
            (byte) ((networkInt >> 8) & 0xFF),
            (byte) (networkInt & 0xFF)
        };
    }
    
    private byte[] calculateBroadcastAddress(byte[] network, int prefixLength) {
        var hostMask = 0xFFFFFFFFL >>> prefixLength;
        var networkInt = ((network[0] & 0xFF) << 24) | ((network[1] & 0xFF) << 16) | 
                        ((network[2] & 0xFF) << 8) | (network[3] & 0xFF);
        var broadcastInt = networkInt | hostMask;
        
        return new byte[]{
            (byte) ((broadcastInt >> 24) & 0xFF),
            (byte) ((broadcastInt >> 16) & 0xFF),
            (byte) ((broadcastInt >> 8) & 0xFF),
            (byte) (broadcastInt & 0xFF)
        };
    }
    
    private byte[] incrementIP(byte[] ip) {
        var result = ip.clone();
        for (int i = 3; i >= 0; i--) {
            if ((result[i] & 0xFF) < 255) {
                result[i]++;
                break;
            } else {
                result[i] = 0;
            }
        }
        return result;
    }
    
    private byte[] decrementIP(byte[] ip) {
        var result = ip.clone();
        for (int i = 3; i >= 0; i--) {
            if ((result[i] & 0xFF) > 0) {
                result[i]--;
                break;
            } else {
                result[i] = (byte) 255;
            }
        }
        return result;
    }
    
    private String formatIP(byte[] ip) {
        return String.format("%d.%d.%d.%d", ip[0] & 0xFF, ip[1] & 0xFF, ip[2] & 0xFF, ip[3] & 0xFF);
    }
    
    private String getIPClass(byte[] ip) {
        var firstOctet = ip[0] & 0xFF;
        if (firstOctet >= 1 && firstOctet <= 126) return Texts.IP_CLASS_A;
        if (firstOctet >= 128 && firstOctet <= 191) return Texts.IP_CLASS_B;
        if (firstOctet >= 192 && firstOctet <= 223) return Texts.IP_CLASS_C;
        if (firstOctet >= 224 && firstOctet <= 239) return Texts.IP_CLASS_D;
        if (firstOctet >= 240 && firstOctet <= 255) return Texts.IP_CLASS_E;
        return Texts.IP_CLASS_UNKNOWN;
    }
    
    private String getIPType(byte[] ip) {
        var firstOctet = ip[0] & 0xFF;
        var secondOctet = ip[1] & 0xFF;

        if (firstOctet == 10) return Texts.IP_TYPE_PRIVATE_A;
        if (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) return Texts.IP_TYPE_PRIVATE_B;
        if (firstOctet == 192 && secondOctet == 168) return Texts.IP_TYPE_PRIVATE_C;
        if (firstOctet == 127) return Texts.IP_TYPE_LOOPBACK;
        if (firstOctet >= 224) return Texts.IP_TYPE_MULTICAST;
        return Texts.IP_TYPE_PUBLIC;
    }
    
    private String toBinaryString(byte[] bytes) {
        var result = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) result.append(".");
            result.append(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0'));
        }
        return result.toString();
    }
    
    private void copyResult(ActionEvent e) {
        var result = resultArea.getText();
        if (result.isEmpty()) {
            statusLabel.setText(Texts.STATUS_NO_CONTENT_TO_COPY);
            return;
        }

        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(result);
        clipboard.setContents(selection, null);
        statusLabel.setText(Texts.STATUS_RESULT_COPIED);
    }
    
    private void clearAll(ActionEvent e) {
        ipField.setText("");
        subnetField.setText("");
        resultArea.setText("");
        statusLabel.setText(Texts.STATUS_CLEARED);
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_ENTER:
                        // 回车键分析
                        if (ev.isControlDown()) {
                            // Ctrl+Enter 复制结果
                            copyResult(new ActionEvent(IPAddressTool.this, ActionEvent.ACTION_PERFORMED, "copy"));
                        } else {
                            analyzeIP(new ActionEvent(IPAddressTool.this, ActionEvent.ACTION_PERFORMED, "analyze"));
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_C:
                        // C键复制（如果按下Ctrl+C则让系统处理复制）
                        if (ev.isControlDown()) {
                            // 让系统处理复制
                            return;
                        } else {
                            copyResult(new ActionEvent(IPAddressTool.this, ActionEvent.ACTION_PERFORMED, "copy"));
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_ESCAPE:
                        // ESC键清空
                        clearAll(new ActionEvent(IPAddressTool.this, ActionEvent.ACTION_PERFORMED, "clear"));
                        break;
                    case java.awt.event.KeyEvent.VK_F5:
                        // F5键重新分析
                        analyzeIP(new ActionEvent(IPAddressTool.this, ActionEvent.ACTION_PERFORMED, "analyze"));
                        break;
                    default:
                        return;
                }
            }
        });

        // 确保窗口可以获得焦点
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private String getSecurityAnalysis(byte[] ipBytes, int prefixLength) {
        var analysis = new StringBuilder();

        // 检查私有地址范围
        if (isPrivateIP(ipBytes)) {
            analysis.append("✅ 私有IP地址 - 适合内部网络使用\n");
            analysis.append("建议: 配置防火墙规则保护内部网络\n");
        } else if (isLoopbackIP(ipBytes)) {
            analysis.append("✅ 回环地址 - 本地测试使用\n");
            analysis.append("建议: 适合开发和测试环境\n");
        } else if (isMulticastIP(ipBytes)) {
            analysis.append("⚠️ 组播地址 - 特殊用途\n");
            analysis.append("建议: 确保网络设备支持组播协议\n");
        } else {
            analysis.append("🌐 公网IP地址 - 需要安全防护\n");
            analysis.append("建议: 配置防火墙、入侵检测系统\n");
        }

        // 子网大小分析
        if (prefixLength < 24) {
            analysis.append("⚠️ 子网较大 - 可能存在广播风暴风险\n");
            analysis.append("建议: 考虑划分子网提高安全性\n");
        } else if (prefixLength > 28) {
            analysis.append("✅ 子网较小 - 安全性较好\n");
        } else {
            analysis.append("✅ 子网大小适中\n");
        }

        return analysis.toString();
    }

    private String getPerformanceRecommendations(int prefixLength) {
        var recommendations = new StringBuilder();

        // 子网大小对性能的影响
        var hostBits = 32 - prefixLength;
        var totalHosts = (long) Math.pow(2, hostBits);

        if (totalHosts > 1000) {
            recommendations.append("⚠️ 大型子网 - 可能影响网络性能\n");
            recommendations.append("建议: 实施VLAN分段，减少广播域\n");
            recommendations.append("建议: 启用IGMP Snooping优化组播\n");
        } else if (totalHosts > 100) {
            recommendations.append("📊 中型子网 - 性能适中\n");
            recommendations.append("建议: 监控网络流量，适时优化\n");
        } else {
            recommendations.append("✅ 小型子网 - 性能最佳\n");
            recommendations.append("建议: 适合高安全性要求的环境\n");
        }

        // 前缀长度建议
        if (prefixLength < 24) {
            recommendations.append("建议: 考虑使用更小的子网提高管理效率\n");
        }

        recommendations.append("建议: 定期监控网络利用率和错误率\n");

        return recommendations.toString();
    }

    private String getPortRecommendations() {
        var ports = new StringBuilder();

        ports.append("常用服务端口:\n");
        ports.append("• HTTP (80): Web服务\n");
        ports.append("• HTTPS (443): 安全Web服务\n");
        ports.append("• SSH (22): 远程管理\n");
        ports.append("• RDP (3389): Windows远程桌面\n");
        ports.append("• FTP (21): 文件传输\n");
        ports.append("• SMTP (25): 邮件发送\n");
        ports.append("• DNS (53): 域名解析\n");
        ports.append("• DHCP (67/68): 动态IP分配\n\n");

        ports.append("安全建议:\n");
        ports.append("• 关闭不必要的服务端口\n");
        ports.append("• 使用防火墙限制访问\n");
        ports.append("• 定期扫描开放端口\n");
        ports.append("• 监控异常端口活动\n");

        return ports.toString();
    }

    private boolean isPrivateIP(byte[] ipBytes) {
        int firstOctet = ipBytes[0] & 0xFF;
        int secondOctet = ipBytes[1] & 0xFF;

        // 10.0.0.0/8
        if (firstOctet == 10) return true;

        // 172.16.0.0/12
        if (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) return true;

        // 192.168.0.0/16
        if (firstOctet == 192 && secondOctet == 168) return true;

        // 169.254.0.0/16 (APIPA)
        if (firstOctet == 169 && secondOctet == 254) return true;

        return false;
    }

    private boolean isLoopbackIP(byte[] ipBytes) {
        return (ipBytes[0] & 0xFF) == 127;
    }

    private boolean isMulticastIP(byte[] ipBytes) {
        return (ipBytes[0] & 0xFF) >= 224 && (ipBytes[0] & 0xFF) <= 239;
    }
}
