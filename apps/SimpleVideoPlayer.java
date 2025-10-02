import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new SimpleVideoPlayer().setVisible(true);
    });
}

static class SimpleVideoPlayer extends JFrame {
    private JPanel videoPanel;
    private JLabel videoLabel;
    private JSlider progressSlider;
    private JButton playButton, pauseButton, stopButton, openButton;
    private JLabel timeLabel, statusLabel;
    private JSlider volumeSlider;
    
    private File currentVideoFile;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private Timer playTimer;
    private int currentPosition = 0;
    private int totalDuration = 100; // 模拟总时长
    
    public SimpleVideoPlayer() {
        setTitle("简易视频播放器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        
        initializeUI();
        setupTimer();
        setLocationRelativeTo(null);
        
        showWelcomeMessage();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 菜单栏
        var menuBar = new JMenuBar();
        
        var fileMenu = new JMenu("文件");
        var openItem = new JMenuItem("打开视频");
        var exitItem = new JMenuItem("退出");
        
        openItem.addActionListener(this::openVideo);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        var playbackMenu = new JMenu("播放");
        var playItem = new JMenuItem("播放");
        var pauseItem = new JMenuItem("暂停");
        var stopItem = new JMenuItem("停止");
        
        playItem.addActionListener(this::playVideo);
        pauseItem.addActionListener(this::pauseVideo);
        stopItem.addActionListener(this::stopVideo);
        
        playbackMenu.add(playItem);
        playbackMenu.add(pauseItem);
        playbackMenu.add(stopItem);
        
        menuBar.add(fileMenu);
        menuBar.add(playbackMenu);
        setJMenuBar(menuBar);
        
        // 视频显示区域
        videoPanel = new JPanel(new BorderLayout());
        videoPanel.setBackground(Color.BLACK);
        videoPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        videoLabel = new JLabel("请打开视频文件", SwingConstants.CENTER);
        videoLabel.setForeground(Color.WHITE);
        videoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        videoPanel.add(videoLabel, BorderLayout.CENTER);
        
        // 控制面板
        var controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // 进度条
        var progressPanel = new JPanel(new BorderLayout());
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.addChangeListener(e -> {
            if (!progressSlider.getValueIsAdjusting()) {
                seekToPosition(progressSlider.getValue());
            }
        });
        
        timeLabel = new JLabel("00:00 / 00:00");
        timeLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        progressPanel.add(progressSlider, BorderLayout.CENTER);
        progressPanel.add(timeLabel, BorderLayout.EAST);
        
        // 播放控制按钮
        var buttonPanel = new JPanel(new FlowLayout());
        
        openButton = new JButton("打开");
        playButton = new JButton("▶");
        pauseButton = new JButton("⏸");
        stopButton = new JButton("⏹");
        
        openButton.addActionListener(this::openVideo);
        playButton.addActionListener(this::playVideo);
        pauseButton.addActionListener(this::pauseVideo);
        stopButton.addActionListener(this::stopVideo);
        
        // 设置按钮样式
        var buttons = new JButton[]{openButton, playButton, pauseButton, stopButton};
        for (var button : buttons) {
            button.setFont(new Font("微软雅黑", Font.BOLD, 14));
            button.setPreferredSize(new Dimension(60, 30));
        }
        
        buttonPanel.add(openButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(stopButton);
        
        // 音量控制
        var volumePanel = new JPanel(new FlowLayout());
        volumePanel.add(new JLabel("音量:"));
        
        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setPreferredSize(new Dimension(100, 25));
        volumeSlider.addChangeListener(e -> updateVolume());
        
        volumePanel.add(volumeSlider);
        
        var rightPanel = new JPanel(new FlowLayout());
        rightPanel.add(volumePanel);
        
        controlPanel.add(progressPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        controlPanel.add(rightPanel, BorderLayout.EAST);
        
        // 状态栏
        statusLabel = new JLabel("准备就绪");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        add(videoPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.PAGE_END);
        
        // 初始状态
        updateButtonStates();
    }
    
    private void setupTimer() {
        playTimer = new Timer(1000, e -> {
            if (isPlaying && !isPaused) {
                currentPosition++;
                if (currentPosition >= totalDuration) {
                    stopVideo(null);
                } else {
                    updateProgress();
                }
            }
        });
    }
    
    private void showWelcomeMessage() {
        SwingUtilities.invokeLater(() -> {
            String welcomeMessage = """
                欢迎使用简易视频播放器！

                注意：这是一个演示版本，主要展示界面和控制逻辑。
                实际的视频播放需要集成专门的媒体库（如JavaFX MediaPlayer）。

                当前功能：
                • 文件选择和界面控制
                • 播放进度模拟
                • 音量控制界面
                • 播放状态管理

                点击"打开"按钮选择视频文件开始体验。
                """;
            JOptionPane.showMessageDialog(this, welcomeMessage, "欢迎", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void openVideo(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "视频文件", "mp4", "avi", "mkv", "mov", "wmv", "flv"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentVideoFile = fileChooser.getSelectedFile();
            loadVideo();
        }
    }
    
    private void loadVideo() {
        if (currentVideoFile == null) return;
        
        stopVideo(null);
        
        // 模拟加载视频
        statusLabel.setText("正在加载: " + currentVideoFile.getName());
        
        SwingUtilities.invokeLater(() -> {
            String videoInfo = String.format("""
                <html><center>
                视频文件: %s<br>
                大小: %s<br>
                <br>
                这里将显示视频画面<br>
                (需要集成媒体库实现实际播放)
                </center></html>
                """, currentVideoFile.getName(), formatFileSize(currentVideoFile.length()));
            videoLabel.setText(videoInfo);
            
            // 模拟视频信息
            totalDuration = 120; // 假设2分钟
            currentPosition = 0;
            progressSlider.setMaximum(totalDuration);
            
            updateProgress();
            updateButtonStates();
            statusLabel.setText("视频已加载: " + currentVideoFile.getName());
        });
    }
    
    private void playVideo(ActionEvent e) {
        if (currentVideoFile == null) {
            JOptionPane.showMessageDialog(this, "请先打开视频文件！");
            return;
        }
        
        if (isPaused) {
            isPaused = false;
            statusLabel.setText("继续播放");
        } else {
            isPlaying = true;
            statusLabel.setText("正在播放: " + currentVideoFile.getName());
        }
        
        playTimer.start();
        updateButtonStates();
    }
    
    private void pauseVideo(ActionEvent e) {
        if (isPlaying) {
            isPaused = true;
            playTimer.stop();
            statusLabel.setText("已暂停");
            updateButtonStates();
        }
    }
    
    private void stopVideo(ActionEvent e) {
        isPlaying = false;
        isPaused = false;
        currentPosition = 0;
        
        if (playTimer != null) {
            playTimer.stop();
        }
        
        updateProgress();
        updateButtonStates();
        statusLabel.setText(currentVideoFile != null ? 
            "已停止: " + currentVideoFile.getName() : "已停止");
    }
    
    private void seekToPosition(int position) {
        currentPosition = position;
        updateProgress();
        
        if (currentVideoFile != null) {
            statusLabel.setText("跳转到: " + formatTime(position));
        }
    }
    
    private void updateVolume() {
        int volume = volumeSlider.getValue();
        statusLabel.setText("音量: " + volume + "%");
        
        // 这里应该调用实际的音量控制API
        // 目前只是界面演示
    }
    
    private void updateProgress() {
        progressSlider.setValue(currentPosition);
        timeLabel.setText(formatTime(currentPosition) + " / " + formatTime(totalDuration));
    }
    
    private void updateButtonStates() {
        boolean hasVideo = currentVideoFile != null;
        boolean playing = isPlaying && !isPaused;
        
        playButton.setEnabled(hasVideo && !playing);
        pauseButton.setEnabled(hasVideo && playing);
        stopButton.setEnabled(hasVideo && (isPlaying || isPaused));
        progressSlider.setEnabled(hasVideo);
    }
    
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
