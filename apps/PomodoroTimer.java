import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new PomodoroTimer().setVisible(true);
    });
}

static class PomodoroTimer extends JFrame {
    private static final int WORK_TIME = 25 * 60;      // 25分钟工作时间
    private static final int SHORT_BREAK = 5 * 60;     // 5分钟短休息
    private static final int LONG_BREAK = 15 * 60;     // 15分钟长休息
    private static final int SESSIONS_BEFORE_LONG_BREAK = 4; // 4个工作周期后长休息

    private JLabel timeLabel;
    private JLabel statusLabel;
    private JLabel sessionLabel;
    private JLabel completedLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JButton skipButton;
    private JProgressBar progressBar;
    private JTextArea historyArea;
    private JComboBox<String> presetCombo;

    private Timer timer;
    private int currentSeconds;
    private int totalSeconds;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private int completedSessions = 0;
    private int currentSessionNumber = 1;

    private enum TimerState {
        WORK, SHORT_BREAK, LONG_BREAK
    }

    private TimerState currentState = TimerState.WORK;
    private List<String> sessionHistory = new ArrayList<>();

    public PomodoroTimer() {
        initializeGUI();
        setupTimer();
        updateDisplay();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle("番茄钟 - 专注工作计时器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 顶部状态面板
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        statusLabel = new JLabel("准备开始工作", SwingConstants.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        statusLabel.setForeground(new Color(52, 152, 219));

        sessionLabel = new JLabel(String.format("第 %d 个番茄钟", currentSessionNumber), SwingConstants.CENTER);
        sessionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        sessionLabel.setForeground(new Color(127, 140, 141));

        statusPanel.add(statusLabel);
        statusPanel.add(sessionLabel);

        // 时间显示面板
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBackground(Color.WHITE);
        timePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        timeLabel = new JLabel("25:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Consolas", Font.BOLD, 72));
        timeLabel.setForeground(new Color(44, 62, 80));
        timePanel.add(timeLabel, BorderLayout.CENTER);

        // 进度条
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(236, 240, 241));
        timePanel.add(progressBar, BorderLayout.SOUTH);

        // 控制按钮面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        startButton = createButton("开始", new Color(39, 174, 96), Color.WHITE);
        pauseButton = createButton("暂停", new Color(243, 156, 18), Color.WHITE);
        resetButton = createButton("重置", new Color(231, 76, 60), Color.WHITE);
        skipButton = createButton("跳过", new Color(155, 89, 182), Color.WHITE);

        startButton.addActionListener(this::startTimer);
        pauseButton.addActionListener(this::pauseTimer);
        resetButton.addActionListener(this::resetTimer);
        skipButton.addActionListener(this::skipSession);

        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resetButton);
        controlPanel.add(skipButton);

        // 预设选择
        JPanel presetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        presetPanel.setBackground(new Color(245, 245, 245));
        presetPanel.add(new JLabel("预设模式:"));

        String[] presets = {"标准模式 (25+5)", "长工作模式 (45+15)", "短工作模式 (15+3)", "自定义模式"};
        presetCombo = new JComboBox<>(presets);
        presetCombo.addActionListener(this::changePreset);
        presetPanel.add(presetCombo);
        controlPanel.add(presetPanel);

        // 统计面板
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(new Color(245, 245, 245));
        statsPanel.setBorder(BorderFactory.createTitledBorder("完成统计"));

        completedLabel = new JLabel(String.format("已完成: %d 个番茄钟", completedSessions), SwingConstants.CENTER);
        completedLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statsPanel.add(completedLabel, BorderLayout.NORTH);

        // 历史记录
        historyArea = new JTextArea(8, 20);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        historyArea.setBackground(new Color(250, 250, 250));

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("历史记录"));
        statsPanel.add(scrollPane, BorderLayout.CENTER);

        // 组装主面板
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        mainPanel.add(timePanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.EAST);

        setSize(900, 600);
        setLocationRelativeTo(null);
        updateButtonStates();
    }

    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupTimer() {
        timer = new Timer(1000, e -> {
            if (isRunning && !isPaused) {
                currentSeconds--;
                updateDisplay();

                if (currentSeconds <= 0) {
                    completeSession();
                }
            }
        });
    }

    private void updateDisplay() {
        int minutes = currentSeconds / 60;
        int seconds = currentSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));

        // 更新进度条
        if (totalSeconds > 0) {
            int progress = (totalSeconds - currentSeconds) * 100 / totalSeconds;
            progressBar.setValue(progress);
        } else {
            progressBar.setValue(0);
        }

        // 时间紧急时改变颜色
        if (currentSeconds <= 60 && currentSeconds > 0) {
            timeLabel.setForeground(new Color(231, 76, 60));
            progressBar.setForeground(new Color(231, 76, 60));
        } else {
            timeLabel.setForeground(new Color(44, 62, 80));
            progressBar.setForeground(new Color(46, 204, 113));
        }
    }

    private void updateButtonStates() {
        startButton.setEnabled(!isRunning || isPaused);
        pauseButton.setEnabled(isRunning && !isPaused);
        resetButton.setEnabled(isRunning || isPaused);
        skipButton.setEnabled(isRunning || isPaused);
        presetCombo.setEnabled(!isRunning);
    }

    private void startTimer(ActionEvent e) {
        if (isPaused) {
            isPaused = false;
            statusLabel.setText(getStatusText());
        } else {
            isRunning = true;
            isPaused = false;
            setTimerForCurrentState();
            statusLabel.setText(getStatusText());
        }

        timer.start();
        updateButtonStates();
    }

    private void pauseTimer(ActionEvent e) {
        isPaused = true;
        timer.stop();
        statusLabel.setText("已暂停");
        updateButtonStates();
    }

    private void resetTimer(ActionEvent e) {
        isRunning = false;
        isPaused = false;
        timer.stop();
        setTimerForCurrentState();
        statusLabel.setText("准备开始");
        updateDisplay();
        updateButtonStates();
    }

    private void skipSession(ActionEvent e) {
        completeSession();
    }

    private void changePreset(ActionEvent e) {
        if (isRunning) return;

        int preset = presetCombo.getSelectedIndex();
        switch (preset) {
            case 0: // 标准模式
                setWorkTime(25 * 60);
                setBreakTime(SHORT_BREAK);
                break;
            case 1: // 长工作模式
                setWorkTime(45 * 60);
                setBreakTime(15 * 60);
                break;
            case 2: // 短工作模式
                setWorkTime(15 * 60);
                setBreakTime(3 * 60);
                break;
            case 3: // 自定义模式
                // 这里可以添加自定义对话框
                break;
        }

        setTimerForCurrentState();
        updateDisplay();
    }

    private void setWorkTime(int seconds) {
        // 这里可以实现自定义工作时间设置
    }

    private void setBreakTime(int seconds) {
        // 这里可以实现自定义休息时间设置
    }

    private void setTimerForCurrentState() {
        switch (currentState) {
            case WORK:
                currentSeconds = WORK_TIME;
                totalSeconds = WORK_TIME;
                break;
            case SHORT_BREAK:
                currentSeconds = SHORT_BREAK;
                totalSeconds = SHORT_BREAK;
                break;
            case LONG_BREAK:
                currentSeconds = LONG_BREAK;
                totalSeconds = LONG_BREAK;
                break;
        }
    }

    private void completeSession() {
        timer.stop();
        isRunning = false;
        isPaused = false;

        String sessionType = "";
        switch (currentState) {
            case WORK:
                completedSessions++;
                sessionType = "工作";
                addToHistory(String.format("第%d个番茄钟完成 - %s",
                    completedSessions, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));

                // 决定下一个状态
                if (completedSessions % SESSIONS_BEFORE_LONG_BREAK == 0) {
                    currentState = TimerState.LONG_BREAK;
                } else {
                    currentState = TimerState.SHORT_BREAK;
                }
                break;
            case SHORT_BREAK:
            case LONG_BREAK:
                currentState = TimerState.WORK;
                currentSessionNumber++;
                sessionType = "休息";
                break;
        }

        // 播放提示音
        playNotificationSound();

        // 显示完成对话框
        showCompletionDialog(sessionType);

        // 准备下一个会话
        setTimerForCurrentState();
        updateDisplay();
        updateButtonStates();
        updateSessionInfo();
    }

    private void showCompletionDialog(String sessionType) {
        String message = """
            %s时间结束！

            点击"开始"继续下一个时段。
            """.formatted(sessionType.equals("工作") ? "番茄钟" : "休息");

        String title = sessionType.equals("工作") ? "番茄钟完成！" : "休息结束！";

        JOptionPane.showMessageDialog(this, message, title,
            sessionType.equals("工作") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    private void playNotificationSound() {
        // 使用系统提示音
        Toolkit.getDefaultToolkit().beep();
    }

    private void addToHistory(String entry) {
        sessionHistory.add(entry);
        updateHistoryDisplay();
    }

    private void updateHistoryDisplay() {
        StringBuilder history = new StringBuilder();
        for (int i = Math.max(0, sessionHistory.size() - 10); i < sessionHistory.size(); i++) {
            history.append(sessionHistory.get(i)).append("\n");
        }
        historyArea.setText(history.toString());
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    private void updateSessionInfo() {
        sessionLabel.setText(String.format("第 %d 个番茄钟", currentSessionNumber));
        completedLabel.setText(String.format("已完成: %d 个番茄钟", completedSessions));
    }

    private String getStatusText() {
        switch (currentState) {
            case WORK:
                return "专注工作中...";
            case SHORT_BREAK:
                return "短休息中...";
            case LONG_BREAK:
                return "长休息中...";
            default:
                return "准备开始";
        }
    }
}