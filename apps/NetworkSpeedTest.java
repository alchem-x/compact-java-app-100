import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🌐 网络速度测试";

    // 主界面标题
    static final String MAIN_TITLE = "🌐 网络速度测试";

    // 按钮文本
    static final String START_SPEED_TEST_BUTTON = "开始速度测试";
    static final String PING_TEST_BUTTON = "Ping测试";
    static final String CLEAR_LOG_BUTTON = "清空日志";

    // 面板标题
    static final String CONTROL_PANEL_TITLE = "测试控制";
    static final String RESULT_PANEL_TITLE = "测试结果";
    static final String LOG_PANEL_TITLE = "测试日志";

    // 标签文本
    static final String TEST_SERVER_LABEL = "测试服务器:";
    static final String PING_LATENCY_LABEL = "Ping延迟:";
    static final String DOWNLOAD_SPEED_LABEL = "下载速度:";
    static final String UPLOAD_SPEED_LABEL = "上传速度:";
    static final String WAITING_TEST_LABEL = "等待测试...";

    // 状态消息
    static final String STATUS_READY = "准备就绪";
    static final String STATUS_DOWNLOAD_TEST_RUNNING = "正在进行下载速度测试...";
    static final String STATUS_UPLOAD_TEST_RUNNING = "正在进行上传速度测试...";
    static final String STATUS_PING_TEST_RUNNING = "正在进行延迟测试...";
    static final String STATUS_TEST_COMPLETE = "测试完成";
    static final String STATUS_TEST_FAILED = "测试失败";

    // 单位
    static final String UNIT_MBPS = "MB/s";
    static final String UNIT_MS = "ms";

    // 错误消息
    static final String ERROR_TEST_FAILED = "测试失败:";
    static final String ERROR_NETWORK_UNAVAILABLE = "网络不可用";
    static final String ERROR_CONNECTION_TIMEOUT = "连接超时";
    static final String ERROR_INVALID_RESPONSE = "无效的响应";

    // 测试服务器
    static final String SERVER_BAIDU = "百度";
    static final String SERVER_TENCENT = "腾讯";
    static final String SERVER_ALIYUN = "阿里云";
    static final String SERVER_GITHUB = "GitHub";

    // 测试说明
    static final String TEST_DESCRIPTION = """
        测试说明:
        • 下载速度测试: 测量从服务器下载数据的速度
        • 上传速度测试: 测量向服务器上传数据的速度
        • 延迟测试: 测量网络响应时间和稳定性

        注意: 测试结果可能因网络状况而异
        """;

    // 默认状态
    static final String DEFAULT_STATUS = "-- ";
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new NetworkSpeedTest().setVisible(true);
    });
}

class NetworkSpeedTest extends JFrame {
    // ===== Apple设计系统常量 =====
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
    private static final Font MONO = new Font("SF Mono", Font.PLAIN, 12);

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

    // 按钮尺寸
    private static final Dimension BUTTON_REGULAR = new Dimension(120, 44);
    private static final Dimension BUTTON_LARGE = new Dimension(160, 50);

    // ===== 应用组件 =====
    private JProgressBar downloadProgress, uploadProgress;
    private JLabel downloadSpeedLabel, uploadSpeedLabel, pingLabel;
    private JLabel downloadResultLabel, uploadResultLabel, statusLabel;
    private JButton testButton, pingButton;
    private JTextArea logArea;
    private JComboBox<TestServer> serverCombo;

    private boolean isTesting = false;

    // 测试服务器列表
    private final TestServer[] testServers = {
        new TestServer(Texts.SERVER_BAIDU, "www.baidu.com", "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png"),
        new TestServer(Texts.SERVER_TENCENT, "www.qq.com", "https://mat1.gtimg.com/pingjs/ext2020/qqindex2018/dist/img/qq_logo_2x.png"),
        new TestServer(Texts.SERVER_ALIYUN, "www.aliyun.com", "https://img.alicdn.com/tfs/TB1Ly5oS3HqK1RjSZFPXXcwapXa-238-54.png"),
        new TestServer(Texts.SERVER_GITHUB, "github.com", "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png")
    };

    public NetworkSpeedTest() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        initializeUI();
        setLocationRelativeTo(null);
        setSize(700, 600);
        setupKeyboardShortcuts();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 顶部控制面板
        var controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("测试控制"));
        
        controlPanel.add(new JLabel("测试服务器:"));
        serverCombo = new JComboBox<>(testServers);
        controlPanel.add(serverCombo);
        
        testButton = new JButton("开始速度测试");
        testButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        testButton.addActionListener(this::startSpeedTest);
        
        pingButton = new JButton("Ping测试");
        pingButton.addActionListener(this::startPingTest);
        
        controlPanel.add(testButton);
        controlPanel.add(pingButton);
        
        // 结果显示面板
        var resultPanel = new JPanel(new GridBagLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("测试结果"));
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Ping结果
        gbc.gridx = 0; gbc.gridy = 0;
        resultPanel.add(new JLabel("Ping延迟:"), gbc);
        gbc.gridx = 1;
        pingLabel = new JLabel("-- ms");
        pingLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        pingLabel.setForeground(Color.BLUE);
        resultPanel.add(pingLabel, gbc);
        
        // 下载速度
        gbc.gridx = 0; gbc.gridy = 1;
        resultPanel.add(new JLabel("下载速度:"), gbc);
        gbc.gridx = 1;
        downloadSpeedLabel = new JLabel("-- MB/s");
        downloadSpeedLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        downloadSpeedLabel.setForeground(new Color(0, 150, 0));
        resultPanel.add(downloadSpeedLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        downloadProgress = new JProgressBar(0, 100);
        downloadProgress.setStringPainted(true);
        downloadProgress.setString("等待测试...");
        resultPanel.add(downloadProgress, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 1;
        downloadResultLabel = new JLabel("");
        downloadResultLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        resultPanel.add(downloadResultLabel, gbc);
        
        // 上传速度
        gbc.gridx = 0; gbc.gridy = 4;
        resultPanel.add(new JLabel("上传速度:"), gbc);
        gbc.gridx = 1;
        uploadSpeedLabel = new JLabel("-- MB/s");
        uploadSpeedLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        uploadSpeedLabel.setForeground(new Color(200, 100, 0));
        resultPanel.add(uploadSpeedLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        uploadProgress = new JProgressBar(0, 100);
        uploadProgress.setStringPainted(true);
        uploadProgress.setString("等待测试...");
        resultPanel.add(uploadProgress, gbc);
        
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 1;
        uploadResultLabel = new JLabel("");
        uploadResultLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        resultPanel.add(uploadResultLabel, gbc);
        
        // 状态标签
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        statusLabel = new JLabel("准备就绪");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.ITALIC, 12));
        resultPanel.add(statusLabel, gbc);
        
        // 日志面板
        var logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("测试日志"));
        
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        var scrollPane = new JScrollPane(logArea);
        
        var clearLogButton = new JButton("清空日志");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        var logButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logButtonPanel.add(clearLogButton);
        
        logPanel.add(scrollPane, BorderLayout.CENTER);
        logPanel.add(logButtonPanel, BorderLayout.SOUTH);
        
        add(controlPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);

        // 添加初始日志
        addLog(Texts.WINDOW_TITLE + "已启动");
        addLog("选择测试服务器后点击开始测试");
    }
    
    private void startSpeedTest(ActionEvent e) {
        if (isTesting) {
            JOptionPane.showMessageDialog(this, "测试正在进行中，请稍候...");
            return;
        }

        isTesting = true;
        testButton.setEnabled(false);
        pingButton.setEnabled(false);

        var selectedServer = (TestServer) serverCombo.getSelectedItem();
        addLog("开始测试服务器: " + selectedServer.name);

        // 重置显示
        downloadSpeedLabel.setText("测试中...");
        uploadSpeedLabel.setText("等待中...");
        downloadProgress.setValue(0);
        uploadProgress.setValue(0);
        downloadProgress.setString("准备下载测试...");
        uploadProgress.setString("等待上传测试...");

        // 异步执行测试
        CompletableFuture.runAsync(() -> {
            try {
                // 先进行Ping测试
                SwingUtilities.invokeLater(() -> statusLabel.setText(Texts.STATUS_PING_TEST_RUNNING));
                performPingTest(selectedServer.host);

                // 下载测试
                SwingUtilities.invokeLater(() -> statusLabel.setText(Texts.STATUS_DOWNLOAD_TEST_RUNNING));
                performDownloadTest(selectedServer.testUrl);

                // 上传测试（模拟）
                SwingUtilities.invokeLater(() -> statusLabel.setText(Texts.STATUS_UPLOAD_TEST_RUNNING));
                performUploadTest(selectedServer.host);

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("测试完成");
                    addLog("所有测试完成");
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("测试失败: " + ex.getMessage());
                    addLog("测试失败: " + ex.getMessage());
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    isTesting = false;
                    testButton.setEnabled(true);
                    pingButton.setEnabled(true);
                });
            }
        });
    }
    
    private void startPingTest(ActionEvent e) {
        if (isTesting) return;
        
        var selectedServer = (TestServer) serverCombo.getSelectedItem();
        addLog("开始Ping测试: " + selectedServer.host);
        
        CompletableFuture.runAsync(() -> {
            performPingTest(selectedServer.host);
        });
    }
    
    private void performPingTest(String host) {
        try {
            var startTime = System.currentTimeMillis();
            var address = InetAddress.getByName(host);
            
            if (address.isReachable(5000)) {
                var endTime = System.currentTimeMillis();
                var ping = endTime - startTime;
                
                SwingUtilities.invokeLater(() -> {
                    pingLabel.setText(ping + " " + Texts.UNIT_MS);
                    addLog("Ping " + host + ": " + ping + Texts.UNIT_MS);
                    
                    // 根据延迟设置颜色
                    if (ping < 50) {
                        pingLabel.setForeground(new Color(0, 150, 0));
                    } else if (ping < 100) {
                        pingLabel.setForeground(Color.ORANGE);
                    } else {
                        pingLabel.setForeground(Color.RED);
                    }
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    pingLabel.setText("超时");
                    pingLabel.setForeground(RED);
                    addLog("Ping " + host + ": 超时");
                });
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                pingLabel.setText("失败");
                pingLabel.setForeground(RED);
                addLog("Ping测试失败: " + e.getMessage());
            });
        }
    }
    
    private void performDownloadTest(String testUrl) {
        try {
            var uri = URI.create(testUrl);
            var url = uri.toURL();
            var connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("User-Agent", "NetworkSpeedTest/1.0");
            
            var contentLength = connection.getContentLength();
            if (contentLength <= 0) {
                contentLength = 1024 * 1024; // 假设1MB
            }
            
            var startTime = System.currentTimeMillis();
            var input = connection.getInputStream();
            var buffer = new byte[8192];
            var totalBytes = 0;
            int bytesRead;
            
            while ((bytesRead = input.read(buffer)) != -1) {
                totalBytes += bytesRead;
                
                var currentTime = System.currentTimeMillis();
                var elapsedSeconds = (currentTime - startTime) / 1000.0;
                
                if (elapsedSeconds > 0) {
                    var speedMBps = (totalBytes / (1024.0 * 1024.0)) / elapsedSeconds;
                    var progress = Math.min(100, (int) ((totalBytes * 100.0) / contentLength));
                    
                    SwingUtilities.invokeLater(() -> {
                        downloadSpeedLabel.setText(String.format("%.2f MB/s", speedMBps));
                        downloadProgress.setValue(progress);
                        downloadProgress.setString(String.format("%.1f%% (%.2f MB/s)",
                            (double) progress, speedMBps));
                    });
                }
                
                // 限制测试时间
                if (elapsedSeconds > 10) break;
            }
            
            input.close();
            
            var endTime = System.currentTimeMillis();
            var totalSeconds = (endTime - startTime) / 1000.0;
            var finalSpeed = (totalBytes / (1024.0 * 1024.0)) / totalSeconds;
            
            final var finalTotalBytes = totalBytes;
            final var finalTotalSeconds = totalSeconds;
            final var finalFinalSpeed = finalSpeed;
            
            SwingUtilities.invokeLater(() -> {
                downloadSpeedLabel.setText(String.format("%.2f MB/s", finalFinalSpeed));
                downloadProgress.setValue(100);
                downloadProgress.setString("下载测试完成");
                downloadResultLabel.setText(String.format("下载了 %.2f MB，用时 %.1f 秒", 
                    finalTotalBytes / (1024.0 * 1024.0), finalTotalSeconds));
                addLog(String.format("下载速度: %.2f MB/s", finalFinalSpeed));
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                downloadSpeedLabel.setText("测试失败");
                downloadProgress.setString("下载测试失败");
                addLog("下载测试失败: " + e.getMessage());
            });
        }
    }
    
    private void performUploadTest(String host) {
        try {
            // 模拟上传测试（实际应用中需要真实的上传端点）
            var testData = new byte[1024 * 1024]; // 1MB测试数据
            java.util.Arrays.fill(testData, (byte) 'A');
            
            var startTime = System.currentTimeMillis();
            
            // 模拟上传过程
            for (int i = 0; i <= 100; i += 10) {
                Thread.sleep(200); // 模拟网络延迟
                
                var currentTime = System.currentTimeMillis();
                var elapsedSeconds = (currentTime - startTime) / 1000.0;
                var uploadedBytes = (testData.length * i) / 100;
                
                if (elapsedSeconds > 0) {
                    var speedMBps = (uploadedBytes / (1024.0 * 1024.0)) / elapsedSeconds;
                    
                    final int progress = i;
                    SwingUtilities.invokeLater(() -> {
                        uploadSpeedLabel.setText(String.format("%.2f MB/s", speedMBps));
                        uploadProgress.setValue(progress);
                        uploadProgress.setString(String.format("%.1f%% (%.2f MB/s)",
                            (double) progress, speedMBps));
                    });
                }
            }
            
            var endTime = System.currentTimeMillis();
            var totalSeconds = (endTime - startTime) / 1000.0;
            var finalSpeed = (testData.length / (1024.0 * 1024.0)) / totalSeconds;
            
            SwingUtilities.invokeLater(() -> {
                uploadSpeedLabel.setText(String.format("%.2f MB/s", finalSpeed));
                uploadProgress.setValue(100);
                uploadProgress.setString("上传测试完成");
                uploadResultLabel.setText(String.format("上传了 %.2f MB，用时 %.1f 秒", 
                    testData.length / (1024.0 * 1024.0), totalSeconds));
                addLog(String.format("上传速度: %.2f MB/s (模拟)", finalSpeed));
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                uploadSpeedLabel.setText("测试失败");
                uploadProgress.setString("上传测试失败");
                addLog("上传测试失败: " + e.getMessage());
            });
        }
    }
    
    private void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            var timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.append(String.format("[%s] %s%n", timestamp, message));
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void clearLog(ActionEvent e) {
        logArea.setText("");
        addLog("日志已清空");
    }
    
    private static class TestServer {
        final String name;
        final String host;
        final String testUrl;

        public TestServer(String name, String host, String testUrl) {
            this.name = name;
            this.host = host;
            this.testUrl = testUrl;
        }

        @Override
        public String toString() {
            return name + " (" + host + ")";
        }
    }

    /**
     * 圆角边框类
     */
    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
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

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_ENTER:
                        // 回车键开始速度测试
                        if (!ev.isControlDown()) {
                            startSpeedTest(new ActionEvent(NetworkSpeedTest.this, ActionEvent.ACTION_PERFORMED, "speedTest"));
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_P:
                        // P键Ping测试
                        if (!ev.isControlDown()) {
                            startPingTest(new ActionEvent(NetworkSpeedTest.this, ActionEvent.ACTION_PERFORMED, "pingTest"));
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_C:
                        // C键清空日志（如果按下Ctrl+C则让系统处理复制）
                        if (ev.isControlDown()) {
                            // 让系统处理复制
                            return;
                        } else {
                            clearLog(new ActionEvent(NetworkSpeedTest.this, ActionEvent.ACTION_PERFORMED, "clearLog"));
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_F5:
                        // F5键重新测试
                        startSpeedTest(new ActionEvent(NetworkSpeedTest.this, ActionEvent.ACTION_PERFORMED, "speedTest"));
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
}
