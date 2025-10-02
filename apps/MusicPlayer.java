import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// 使用record定义音乐文件信息
record MusicTrack(
    String title,
    String artist,
    String album,
    String filePath,
    long duration,
    String format
) {
    static MusicTrack from(File file) {
        var name = file.getName();
        var title = name.substring(0, name.lastIndexOf('.'));
        var format = name.substring(name.lastIndexOf('.') + 1).toUpperCase();
        
        return new MusicTrack(
            title,
            "未知艺术家",
            "未知专辑", 
            file.getAbsolutePath(),
            0, // 简化版不获取实际时长
            format
        );
    }
    
    String formattedDuration() {
        var minutes = duration / 60;
        var seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

// 使用sealed interface定义播放状态
sealed interface PlaybackState permits 
    PlaybackState.Stopped, PlaybackState.Playing, PlaybackState.Paused {
    
    record Stopped() implements PlaybackState {}
    record Playing(MusicTrack track, long position) implements PlaybackState {}
    record Paused(MusicTrack track, long position) implements PlaybackState {}
}

// 使用record定义播放器配置
record PlayerConfig(
    double volume,
    boolean shuffle,
    RepeatMode repeatMode,
    boolean showVisualization
) {
    static PlayerConfig defaultConfig() {
        return new PlayerConfig(0.7, false, RepeatMode.OFF, false);
    }
    
    PlayerConfig withVolume(double volume) {
        return new PlayerConfig(volume, shuffle, repeatMode, showVisualization);
    }
    
    PlayerConfig withShuffle(boolean shuffle) {
        return new PlayerConfig(volume, shuffle, repeatMode, showVisualization);
    }
    
    PlayerConfig withRepeatMode(RepeatMode repeatMode) {
        return new PlayerConfig(volume, shuffle, repeatMode, showVisualization);
    }
}

enum RepeatMode {
    OFF("关闭"), ONE("单曲循环"), ALL("列表循环");
    
    private final String displayName;
    
    RepeatMode(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    RepeatMode next() {
        return switch (this) {
            case OFF -> ONE;
            case ONE -> ALL;
            case ALL -> OFF;
        };
    }
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new MusicPlayer().setVisible(true);
    });
}

static class MusicPlayer extends JFrame {
    private final DefaultListModel<MusicTrack> playlistModel;
    private final JList<MusicTrack> playlistView;
    private final JLabel trackInfoLabel;
    private final JLabel timeLabel;
    private final JProgressBar progressBar;
    private final JSlider volumeSlider;
    private final JButton playPauseButton;
    private final JButton stopButton;
    private final JButton previousButton;
    private final JButton nextButton;
    private final JButton shuffleButton;
    private final JButton repeatButton;
    
    private PlaybackState playbackState = new PlaybackState.Stopped();
    private PlayerConfig config = PlayerConfig.defaultConfig();
    private Clip currentClip;
    private javax.swing.Timer progressTimer;
    private int currentTrackIndex = -1;
    
    public MusicPlayer() {
        playlistModel = new DefaultListModel<>();
        playlistView = new JList<>(playlistModel);
        trackInfoLabel = new JLabel("未选择音乐");
        timeLabel = new JLabel("00:00 / 00:00");
        progressBar = new JProgressBar(0, 100);
        volumeSlider = new JSlider(0, 100, (int)(config.volume() * 100));
        
        // 创建控制按钮
        playPauseButton = new JButton("▶️");
        stopButton = new JButton("⏹️");
        previousButton = new JButton("⏮️");
        nextButton = new JButton("⏭️");
        shuffleButton = new JButton("🔀");
        repeatButton = new JButton("🔁");
        
        initializeGUI();
        setupEventHandlers();
        setupProgressTimer();
    }
    
    private void initializeGUI() {
        setTitle("🎵 音乐播放器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 创建菜单栏
        createMenuBar();
        
        // 主面板
        var mainPanel = new JPanel(new BorderLayout());
        
        // 播放列表面板
        createPlaylistPanel(mainPanel);
        
        // 控制面板
        createControlPanel(mainPanel);
        
        // 信息面板
        createInfoPanel(mainPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void createMenuBar() {
        var menuBar = new JMenuBar();
        
        var fileMenu = new JMenu("文件");
        
        var openFileItem = new JMenuItem("添加音乐文件");
        openFileItem.addActionListener(e -> addMusicFiles());
        
        var openFolderItem = new JMenuItem("添加文件夹");
        openFolderItem.addActionListener(e -> addMusicFolder());
        
        var clearPlaylistItem = new JMenuItem("清空播放列表");
        clearPlaylistItem.addActionListener(e -> clearPlaylist());
        
        fileMenu.add(openFileItem);
        fileMenu.add(openFolderItem);
        fileMenu.addSeparator();
        fileMenu.add(clearPlaylistItem);
        
        var viewMenu = new JMenu("视图");
        
        var showVisualizationItem = new JCheckBoxMenuItem("显示可视化");
        showVisualizationItem.addActionListener(e -> {
            config = new PlayerConfig(config.volume(), config.shuffle(), 
                config.repeatMode(), showVisualizationItem.isSelected());
        });
        
        viewMenu.add(showVisualizationItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createPlaylistPanel(JPanel parent) {
        var playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setBorder(BorderFactory.createTitledBorder("播放列表"));
        
        // 设置播放列表样式
        playlistView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlistView.setCellRenderer(new MusicTrackRenderer());
        playlistView.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                var selectedTrack = playlistView.getSelectedValue();
                if (selectedTrack != null) {
                    currentTrackIndex = playlistView.getSelectedIndex();
                    updateTrackInfo(selectedTrack);
                }
            }
        });
        
        // 双击播放
        playlistView.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    playSelectedTrack();
                }
            }
        });
        
        var scrollPane = new JScrollPane(playlistView);
        playlistPanel.add(scrollPane, BorderLayout.CENTER);
        
        parent.add(playlistPanel, BorderLayout.CENTER);
    }
    
    private void createControlPanel(JPanel parent) {
        var controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("控制"));
        
        // 播放控制按钮
        var buttonPanel = new JPanel(new FlowLayout());
        
        previousButton.setPreferredSize(new Dimension(50, 40));
        playPauseButton.setPreferredSize(new Dimension(60, 40));
        stopButton.setPreferredSize(new Dimension(50, 40));
        nextButton.setPreferredSize(new Dimension(50, 40));
        shuffleButton.setPreferredSize(new Dimension(50, 40));
        repeatButton.setPreferredSize(new Dimension(50, 40));
        
        buttonPanel.add(shuffleButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(playPauseButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(repeatButton);
        
        // 进度条
        var progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(new JLabel("进度:"), BorderLayout.WEST);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(timeLabel, BorderLayout.EAST);
        
        // 音量控制
        var volumePanel = new JPanel(new BorderLayout());
        volumePanel.add(new JLabel("🔊"), BorderLayout.WEST);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        volumePanel.add(new JLabel("音量"), BorderLayout.EAST);
        
        controlPanel.add(buttonPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(progressPanel);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(volumePanel);
        
        parent.add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void createInfoPanel(JPanel parent) {
        var infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("当前播放"));
        infoPanel.setPreferredSize(new Dimension(0, 80));
        
        trackInfoLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        trackInfoLabel.setHorizontalAlignment(JLabel.CENTER);
        
        infoPanel.add(trackInfoLabel, BorderLayout.CENTER);
        
        parent.add(infoPanel, BorderLayout.NORTH);
    }
    
    private void setupEventHandlers() {
        playPauseButton.addActionListener(this::togglePlayPause);
        stopButton.addActionListener(e -> stopPlayback());
        previousButton.addActionListener(e -> playPrevious());
        nextButton.addActionListener(e -> playNext());
        
        shuffleButton.addActionListener(e -> {
            config = config.withShuffle(!config.shuffle());
            updateShuffleButton();
        });
        
        repeatButton.addActionListener(e -> {
            config = config.withRepeatMode(config.repeatMode().next());
            updateRepeatButton();
        });
        
        volumeSlider.addChangeListener(e -> {
            var volume = volumeSlider.getValue() / 100.0;
            config = config.withVolume(volume);
            updateVolume();
        });
        
        // 初始化按钮状态
        updateShuffleButton();
        updateRepeatButton();
    }
    
    private void setupProgressTimer() {
        progressTimer = new javax.swing.Timer(1000, e -> updateProgress());
    }
    
    private void addMusicFiles() {
        var fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "音频文件", "wav", "mp3", "aiff", "au"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var files = fileChooser.getSelectedFiles();
            Arrays.stream(files)
                .map(MusicTrack::from)
                .forEach(playlistModel::addElement);
        }
    }
    
    private void addMusicFolder() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var folder = fileChooser.getSelectedFile();
            CompletableFuture.runAsync(() -> scanMusicFolder(folder));
        }
    }
    
    private void scanMusicFolder(File folder) {
        try {
            var musicFiles = new ArrayList<MusicTrack>();
            scanFolderRecursive(folder, musicFiles);
            
            SwingUtilities.invokeLater(() -> {
                musicFiles.forEach(playlistModel::addElement);
                JOptionPane.showMessageDialog(this, 
                    String.format("已添加 %d 个音频文件", musicFiles.size()));
            });
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(this, "扫描文件夹时出错: " + e.getMessage()));
        }
    }
    
    private void scanFolderRecursive(File folder, List<MusicTrack> musicFiles) {
        var files = folder.listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isDirectory()) {
                    scanFolderRecursive(file, musicFiles);
                } else if (isSupportedAudioFile(file)) {
                    musicFiles.add(MusicTrack.from(file));
                }
            }
        }
    }
    
    private boolean isSupportedAudioFile(File file) {
        var name = file.getName().toLowerCase();
        return name.endsWith(".wav") || name.endsWith(".aiff") || name.endsWith(".au");
        // 注意：Java内置只支持这些格式，MP3需要额外库
    }
    
    private void clearPlaylist() {
        stopPlayback();
        playlistModel.clear();
        currentTrackIndex = -1;
        updateTrackInfo(null);
    }
    
    private void playSelectedTrack() {
        var selectedTrack = playlistView.getSelectedValue();
        if (selectedTrack != null) {
            playTrack(selectedTrack);
        }
    }
    
    private void playTrack(MusicTrack track) {
        stopPlayback();
        
        try {
            var audioFile = new File(track.filePath());
            var audioStream = AudioSystem.getAudioInputStream(audioFile);
            
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);
            
            // 设置音量
            updateVolume();
            
            // 添加播放完成监听器
            currentClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    SwingUtilities.invokeLater(() -> {
                        if (playbackState instanceof PlaybackState.Playing) {
                            handleTrackFinished();
                        }
                    });
                }
            });
            
            currentClip.start();
            playbackState = new PlaybackState.Playing(track, 0);
            
            updateTrackInfo(track);
            updatePlayPauseButton();
            progressTimer.start();
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            JOptionPane.showMessageDialog(this, "无法播放文件: " + e.getMessage());
        }
    }
    
    private void togglePlayPause(ActionEvent e) {
        switch (playbackState) {
            case PlaybackState.Stopped() -> {
                if (currentTrackIndex >= 0 && currentTrackIndex < playlistModel.size()) {
                    playTrack(playlistModel.get(currentTrackIndex));
                }
            }
            case PlaybackState.Playing(var track, var position) -> {
                if (currentClip != null) {
                    currentClip.stop();
                    playbackState = new PlaybackState.Paused(track, currentClip.getMicrosecondPosition());
                    progressTimer.stop();
                }
            }
            case PlaybackState.Paused(var track, var position) -> {
                if (currentClip != null) {
                    currentClip.setMicrosecondPosition(position);
                    currentClip.start();
                    playbackState = new PlaybackState.Playing(track, position);
                    progressTimer.start();
                }
            }
        }
        updatePlayPauseButton();
    }
    
    private void stopPlayback() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
        
        playbackState = new PlaybackState.Stopped();
        progressTimer.stop();
        updatePlayPauseButton();
        updateProgress();
    }
    
    private void playNext() {
        if (playlistModel.isEmpty()) return;
        
        var nextIndex = config.shuffle() ? 
            new Random().nextInt(playlistModel.size()) :
            (currentTrackIndex + 1) % playlistModel.size();
        
        currentTrackIndex = nextIndex;
        playlistView.setSelectedIndex(nextIndex);
        playTrack(playlistModel.get(nextIndex));
    }
    
    private void playPrevious() {
        if (playlistModel.isEmpty()) return;
        
        var prevIndex = config.shuffle() ? 
            new Random().nextInt(playlistModel.size()) :
            (currentTrackIndex - 1 + playlistModel.size()) % playlistModel.size();
        
        currentTrackIndex = prevIndex;
        playlistView.setSelectedIndex(prevIndex);
        playTrack(playlistModel.get(prevIndex));
    }
    
    private void handleTrackFinished() {
        switch (config.repeatMode()) {
            case ONE -> {
                // 重播当前曲目
                if (currentTrackIndex >= 0) {
                    playTrack(playlistModel.get(currentTrackIndex));
                }
            }
            case ALL -> playNext();
            case OFF -> {
                if (currentTrackIndex < playlistModel.size() - 1) {
                    playNext();
                } else {
                    stopPlayback();
                }
            }
        }
    }
    
    private void updateVolume() {
        if (currentClip != null && currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            var volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
            var volume = (float) config.volume();
            var gain = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            volumeControl.setValue(Math.max(gain, volumeControl.getMinimum()));
        }
    }
    
    private void updateProgress() {
        if (currentClip != null && playbackState instanceof PlaybackState.Playing) {
            var position = currentClip.getMicrosecondPosition() / 1_000_000;
            var duration = currentClip.getMicrosecondLength() / 1_000_000;
            
            var progress = duration > 0 ? (int) ((position * 100) / duration) : 0;
            progressBar.setValue(progress);
            
            timeLabel.setText(String.format("%02d:%02d / %02d:%02d", 
                position / 60, position % 60,
                duration / 60, duration % 60));
        } else {
            progressBar.setValue(0);
            timeLabel.setText("00:00 / 00:00");
        }
    }
    
    private void updateTrackInfo(MusicTrack track) {
        if (track != null) {
            trackInfoLabel.setText(String.format("🎵 %s - %s", track.title(), track.artist()));
        } else {
            trackInfoLabel.setText("未选择音乐");
        }
    }
    
    private void updatePlayPauseButton() {
        var buttonText = switch (playbackState) {
            case PlaybackState.Stopped() -> "▶️";
            case PlaybackState.Playing(var track, var position) -> "⏸️";
            case PlaybackState.Paused(var track, var position) -> "▶️";
        };
        playPauseButton.setText(buttonText);
    }
    
    private void updateShuffleButton() {
        shuffleButton.setBackground(config.shuffle() ? Color.ORANGE : null);
        shuffleButton.setToolTipText(config.shuffle() ? "随机播放：开" : "随机播放：关");
    }
    
    private void updateRepeatButton() {
        var color = switch (config.repeatMode()) {
            case OFF -> null;
            case ONE -> Color.GREEN;
            case ALL -> Color.BLUE;
        };
        repeatButton.setBackground(color);
        repeatButton.setToolTipText("重复模式: " + config.repeatMode());
    }
    
    // 自定义渲染器
    static class MusicTrackRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof MusicTrack track) {
                var text = String.format("🎵 %s - %s (%s)", 
                    track.title(), track.artist(), track.format());
                setText(text);
            }
            
            return this;
        }
    }
}
