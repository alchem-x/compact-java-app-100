import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


void main() {
    SwingUtilities.invokeLater(() -> new MusicPlayerFrame().setVisible(true));
}

static class MusicPlayerFrame extends JFrame {

    /**
     * 文案管理内部类 - 集中管理所有文本内容
     */
    static class Texts {

        // ==================== 主窗口 ====================
        static final class Window {
            static final String TITLE = "千千静听 - 音乐播放器";
            static final String MAIN_TITLE = "千千静听音乐播放器";
        }

        // ==================== 控制按钮 ====================
        static final class ControlButtons {
            static final String PLAY = "▶";
            static final String PAUSE = "⏸";
            static final String STOP = "⏹";
            static final String PREVIOUS = "⏮";
            static final String NEXT = "⏭";
            static final String VOLUME_ICON = "🔊";

            static final class Tooltips {
                static final String PLAY = "播放";
                static final String PAUSE = "暂停";
                static final String STOP = "停止";
                static final String PREVIOUS = "上一首";
                static final String NEXT = "下一首";
            }
        }

        // ==================== 主要操作按钮 ====================
        static final class ActionButtons {
            static final String ADD_MUSIC = "添加音乐";
        }

        // ==================== 界面元素 ====================
        static final class UI {
            static final String PLAYLIST_TITLE = "播放列表";
            static final String DEFAULT_TIME_DISPLAY = "00:00 / 00:00";
        }

        // ==================== 文件操作 ====================
        static final class FileOperations {
            static final String CHOOSER_TITLE = "选择音乐文件";
            static final String FILE_FILTER_DESCRIPTION = "音频文件 (*.wav, *.mp3, *.aiff, *.au)";
        }

        // ==================== 消息提示 ====================
        static final class Messages {
            static final String MUSIC_ADDED_FORMAT = "已添加 %d 个音乐文件";
            static final String PLAYBACK_ERROR_FORMAT = "无法播放此音频文件: %s";
            static final String PLAYBACK_ERROR_TITLE = "播放错误";
        }

        // ==================== 时间格式 ====================
        static final class TimeFormat {
            static final String TIME_DISPLAY = "%02d:%02d / %02d:%02d";
        }
    }
    private final Color BLUE = new Color(0, 122, 255);
    private final Color GREEN = new Color(52, 199, 89);
    private final Color GRAY6 = new Color(242, 242, 247);
    private final Color SYSTEM_BACKGROUND = new Color(255, 255, 255);
    private final Color LABEL = new Color(0, 0, 0);
    private final Color SECONDARY_LABEL = new Color(60, 60, 67, 153);

    // 间距常量
    private static final int SPACING_12 = 12;
    private static final int SPACING_16 = 16;

    // 圆角常量
    private static final int RADIUS_8 = 8;

    // 按钮尺寸
    private static final Dimension BUTTON_REGULAR = new Dimension(120, 44);

    private final Font HEADLINE = new Font("SF Pro Display", Font.BOLD, 17);
    private final Font BODY = new Font("SF Pro Text", Font.PLAIN, 17);
    private final Font FOOTNOTE = new Font("SF Pro Text", Font.PLAIN, 13);

    private JLabel songTitleLabel;
    private JLabel timeLabel;
    private JSlider progressSlider;
    private JSlider volumeSlider;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton prevButton;
    private JButton nextButton;
    private JButton addButton;
    private JList<String> playlist;
    private DefaultListModel<String> playlistModel;

    private List<File> musicFiles;
    private int currentSongIndex = -1;
    private Clip currentClip;
    private boolean isPlaying = false;
    private FloatControl volumeControl;

    public MusicPlayerFrame() {
        super(Texts.Window.TITLE);
        this.musicFiles = new ArrayList<>();
        this.initializeGUI();
        this.setupEventHandlers();
    }

    private void initializeGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        this.add(this.createTopPanel(), BorderLayout.NORTH);
        this.add(this.createCenterPanel(), BorderLayout.CENTER);
        this.add(this.createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        var topPanel = new JPanel();
        topPanel.setBackground(SYSTEM_BACKGROUND);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        songTitleLabel = new JLabel(Texts.Window.MAIN_TITLE);
        songTitleLabel.setFont(HEADLINE);
        songTitleLabel.setForeground(LABEL);
        songTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timeLabel = new JLabel(Texts.UI.DEFAULT_TIME_DISPLAY);
        timeLabel.setFont(FOOTNOTE);
        timeLabel.setForeground(SECONDARY_LABEL);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(SYSTEM_BACKGROUND);
        progressSlider.setForeground(BLUE);
        progressSlider.setMaximumSize(new Dimension(400, 30));
        progressSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(songTitleLabel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(timeLabel);
        topPanel.add(Box.createVerticalStrut(12));
        topPanel.add(progressSlider);

        return topPanel;
    }

    private JPanel createCenterPanel() {
        var centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(SYSTEM_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        var controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        controlPanel.setBackground(SYSTEM_BACKGROUND);

        prevButton = this.createButton(Texts.ControlButtons.PREVIOUS, Texts.ControlButtons.Tooltips.PREVIOUS, this::playPrevious);
        playButton = this.createButton(Texts.ControlButtons.PLAY, Texts.ControlButtons.Tooltips.PLAY, this::playMusic);
        pauseButton = this.createButton(Texts.ControlButtons.PAUSE, Texts.ControlButtons.Tooltips.PAUSE, this::pauseMusic);
        stopButton = this.createButton(Texts.ControlButtons.STOP, Texts.ControlButtons.Tooltips.STOP, this::stopMusic);
        nextButton = this.createButton(Texts.ControlButtons.NEXT, Texts.ControlButtons.Tooltips.NEXT, this::playNext);

        controlPanel.add(prevButton);
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stopButton);
        controlPanel.add(nextButton);

        var volumePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        volumePanel.setBackground(SYSTEM_BACKGROUND);

        var volumeLabel = new JLabel(Texts.ControlButtons.VOLUME_ICON);
        volumeLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 16));

        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setBackground(SYSTEM_BACKGROUND);
        volumeSlider.setPreferredSize(new Dimension(120, 20));
        volumeSlider.addChangeListener((ev) -> this.setVolume(volumeSlider.getValue()));

        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);

        var combinedPanel = new JPanel();
        combinedPanel.setBackground(SYSTEM_BACKGROUND);
        combinedPanel.setLayout(new BoxLayout(combinedPanel, BoxLayout.Y_AXIS));
        combinedPanel.add(controlPanel);
        combinedPanel.add(volumePanel);

        centerPanel.add(combinedPanel, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createBottomPanel() {
        var bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(SYSTEM_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(SYSTEM_BACKGROUND);

        addButton = this.createPrimaryButton(Texts.ActionButtons.ADD_MUSIC, this::addMusic);
        buttonPanel.add(addButton);

        playlistModel = new DefaultListModel<>();
        playlist = new JList<>(playlistModel);
        playlist.setFont(BODY);
        playlist.setBackground(GRAY6);
        playlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlist.setSelectionForeground(BLUE);
        playlist.setPreferredSize(new Dimension(200, 200));

        var scrollPane = new JScrollPane(playlist);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), Texts.UI.PLAYLIST_TITLE, 0, 0, HEADLINE, LABEL));

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        return bottomPanel;
    }

    private JButton createButton(String text, String tooltip, Runnable action) {
        var button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        button.setBackground(SYSTEM_BACKGROUND);
        button.setForeground(LABEL);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setPreferredSize(new Dimension(60, 44));
        button.addActionListener((ev) -> action.run());
        return button;
    }

    private JButton createPrimaryButton(String text, Runnable action) {
        var button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            protected void paintComponent(Graphics g) {
                var g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 根据状态选择颜色
                Color topColor, bottomColor;
                if (isPressed) {
                    topColor = new Color(0, 80, 200);
                    bottomColor = new Color(0, 60, 180);
                } else if (isHovered) {
                    topColor = new Color(0, 140, 255);
                    bottomColor = new Color(0, 120, 240);
                } else {
                    topColor = new Color(0, 122, 255);
                    bottomColor = new Color(0, 100, 230);
                }

                // 主渐变背景
                var gradient = new GradientPaint(
                        0, 0, topColor,
                        0, getHeight(), bottomColor
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 顶部高光效果
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.fillRect(0, 0, getWidth(), 2);

                // 底部阴影效果
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillRect(0, getHeight() - 3, getWidth(), 3);

                // 边框效果
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

                g2d.dispose();
                super.paintComponent(g);
            }

            public void setHovered(boolean hovered) {
                this.isHovered = hovered;
                repaint();
            }

            public void setPressed(boolean pressed) {
                this.isPressed = pressed;
                repaint();
            }
        };

        button.setForeground(SYSTEM_BACKGROUND);
        button.setFont(HEADLINE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(3, 3, 3, 3),
                BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));
        button.setPreferredSize(BUTTON_REGULAR);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.addActionListener((ev) -> action.run());

        // 添加悬停和点击效果
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setHovered(true);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setHovered(false);
                    button.setPressed(false);
                }
            }

            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setPressed(true);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setPressed(false);
                }
            }
        });

        return button;
    }

    private void setupEventHandlers() {
        playlist.addListSelectionListener((ev) -> {
            if (!ev.getValueIsAdjusting() && playlist.getSelectedIndex() != -1) {
                this.selectSong(playlist.getSelectedIndex());
            }
        });

        this.setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        var inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = this.getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "playPause");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "previous");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "next");

        actionMap.put("playPause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isPlaying) {
                    pauseMusic();
                } else {
                    playMusic();
                }
            }
        });

        actionMap.put("previous", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                playPrevious();
            }
        });

        actionMap.put("next", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                playNext();
            }
        });
    }

    private void addMusic() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() ||
                       file.getName().toLowerCase().endsWith(".wav") ||
                       file.getName().toLowerCase().endsWith(".mp3") ||
                       file.getName().toLowerCase().endsWith(".aiff") ||
                       file.getName().toLowerCase().endsWith(".au");
            }

            public String getDescription() {
                return Texts.FileOperations.FILE_FILTER_DESCRIPTION;
            }
        });
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle(Texts.FileOperations.CHOOSER_TITLE);

        var result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            var files = fileChooser.getSelectedFiles();
            for (var file : files) {
                musicFiles.add(file);
                playlistModel.addElement(file.getName());
            }

            if (currentSongIndex == -1 && !musicFiles.isEmpty()) {
                playlist.setSelectedIndex(0);
            }

            JOptionPane.showMessageDialog(this,
                    String.format(Texts.Messages.MUSIC_ADDED_FORMAT, files.length));
        }
    }

    private void selectSong(int index) {
        if (index < 0 || index >= musicFiles.size()) return;

        currentSongIndex = index;
        var file = musicFiles.get(index);
        songTitleLabel.setText(file.getName());

        try {
            if (currentClip != null) {
                currentClip.stop();
                currentClip.close();
            }

            var audioInputStream = AudioSystem.getAudioInputStream(file);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioInputStream);

            progressSlider.setMaximum((int) currentClip.getMicrosecondLength() / 1000000);
            progressSlider.setValue(0);

            if (currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                setVolume(volumeSlider.getValue());
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            JOptionPane.showMessageDialog(this, String.format(Texts.Messages.PLAYBACK_ERROR_FORMAT, e.getMessage()));
        }
    }

    private void playMusic() {
        if (currentClip != null && !isPlaying) {
            currentClip.start();
            isPlaying = true;
            this.updateTimeDisplay();
        }
    }

    private void pauseMusic() {
        if (currentClip != null && isPlaying) {
            currentClip.stop();
            isPlaying = false;
        }
    }

    private void stopMusic() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.setMicrosecondPosition(0);
            isPlaying = false;
            progressSlider.setValue(0);
            this.updateTimeDisplay();
        }
    }

    private void playPrevious() {
        if (currentSongIndex > 0) {
            playlist.setSelectedIndex(currentSongIndex - 1);
        }
    }

    private void playNext() {
        if (currentSongIndex < musicFiles.size() - 1) {
            playlist.setSelectedIndex(currentSongIndex + 1);
        }
    }

    private void setVolume(int volume) {
        if (volumeControl != null) {
            var volumeValue = volume / 100.0f;
            volumeControl.setValue(volumeControl.getMinimum() +
                                   (volumeControl.getMaximum() - volumeControl.getMinimum()) * volumeValue);
        }
    }

    private void updateTimeDisplay() {
        if (currentClip != null) {
            var current = currentClip.getMicrosecondPosition() / 1000000;
            var total = currentClip.getMicrosecondLength() / 1000000;

            progressSlider.setValue((int) current);
            timeLabel.setText(String.format(Texts.TimeFormat.TIME_DISPLAY,
                    current / 60, current % 60, total / 60, total % 60));

            if (isPlaying) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(1000);
                        this.updateTimeDisplay();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }
}

/**
 * 圆角边框实现
 */
static class RoundedBorder extends AbstractBorder {
    private final int radius;

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        var g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(c.getForeground());
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2d.dispose();
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(radius, radius, radius, radius);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = radius;
        return insets;
    }
}