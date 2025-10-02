import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        new NumberGuess().setVisible(true);
    });
}

static class NumberGuess extends JFrame {
    private static final int EASY_MIN = 1;
    private static final int EASY_MAX = 50;
    private static final int MEDIUM_MIN = 1;
    private static final int MEDIUM_MAX = 100;
    private static final int HARD_MIN = 1;
    private static final int HARD_MAX = 200;
    private static final int MAX_ATTEMPTS_EASY = 10;
    private static final int MAX_ATTEMPTS_MEDIUM = 8;
    private static final int MAX_ATTEMPTS_HARD = 6;

    private JLabel titleLabel;
    private JLabel statusLabel;
    private JLabel attemptsLabel;
    private JLabel rangeLabel;
    private JLabel hintLabel;
    private JTextField guessField;
    private JButton guessButton;
    private JButton newGameButton;
    private JButton hintButton;
    private JComboBox<String> difficultyCombo;
    private JTextArea historyArea;
    private JProgressBar progressBar;

    private Random random = new Random();
    private int targetNumber;
    private int currentDifficulty = 0; // 0-easy, 1-medium, 2-hard
    private int attemptsLeft;
    private int totalAttempts;
    private int gamesPlayed = 0;
    private int gamesWon = 0;
    private List<Integer> guessHistory = new ArrayList<>();
    private boolean gameActive = false;
    private int minRange;
    private int maxRange;

    // 提示相关
    private int lastHintDistance = -1;
    private boolean hintUsed = false;

    public NumberGuess() {
        initializeGUI();
        startNewGame();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle("🎯 猜数字游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 主面板
        var mainPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        mainPanel.setLayout(new BorderLayout(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.EXTRA_LARGE_SPACING, AppleDesign.EXTRA_LARGE_SPACING, AppleDesign.EXTRA_LARGE_SPACING, AppleDesign.EXTRA_LARGE_SPACING));

        // 标题面板
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        titlePanel.setBackground(new Color(245, 247, 250));

        titleLabel = new JLabel("🎯 猜数字游戏", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 73, 94));

        statusLabel = new JLabel("我想了一个数字，你来猜猜看吧！", SwingConstants.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        statusLabel.setForeground(new Color(127, 140, 141));

        titlePanel.add(titleLabel);
        titlePanel.add(statusLabel);

        // 游戏信息面板
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(new Color(245, 247, 250));
        infoPanel.setBorder(BorderFactory.createTitledBorder("游戏信息"));

        rangeLabel = new JLabel("数字范围: 1 - 50", SwingConstants.CENTER);
        rangeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        attemptsLabel = new JLabel("剩余次数: 10", SwingConstants.CENTER);
        attemptsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        attemptsLabel.setForeground(new Color(231, 76, 60));

        hintLabel = new JLabel("💡 提示: 还没有使用提示", SwingConstants.CENTER);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        hintLabel.setForeground(new Color(52, 152, 219));

        infoPanel.add(rangeLabel);
        infoPanel.add(attemptsLabel);
        infoPanel.add(hintLabel);

        // 控制面板
        JPanel controlPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        controlPanel.setBackground(new Color(245, 247, 250));
        controlPanel.setBorder(BorderFactory.createTitledBorder("游戏控制"));

        // 难度选择
        JPanel difficultyPanel = new JPanel(new FlowLayout());
        difficultyPanel.setBackground(new Color(245, 247, 250));
        difficultyPanel.add(new JLabel("难度:"));

        String[] difficulties = {"简单 (1-50)", "中等 (1-100)", "困难 (1-200)"};
        difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.addActionListener(this::changeDifficulty);
        difficultyPanel.add(difficultyCombo);
        controlPanel.add(difficultyPanel);

        // 输入面板
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(new Color(245, 247, 250));
        inputPanel.add(new JLabel("输入数字:"));

        guessField = new JTextField(5);
        guessField.setFont(new Font("Consolas", Font.BOLD, 16));
        guessField.setHorizontalAlignment(SwingConstants.CENTER);
        guessField.addActionListener(this::makeGuess);
        inputPanel.add(guessField);
        controlPanel.add(inputPanel);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(245, 247, 250));

        guessButton = createButton("🎯 猜测", new Color(39, 174, 96), e -> makeGuess(e));
        newGameButton = createButton("🔄 新游戏", new Color(52, 152, 219), e -> startNewGame());
        hintButton = createButton("💡 提示", new Color(241, 196, 15), e -> giveHint());

        buttonPanel.add(guessButton);
        buttonPanel.add(hintButton);
        buttonPanel.add(newGameButton);
        controlPanel.add(buttonPanel);

        // 进度面板
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(100);
        progressBar.setStringPainted(true);
        progressBar.setString("游戏进度");
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(236, 240, 241));
        controlPanel.add(progressBar);

        // 历史记录面板
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(245, 247, 250));
        historyPanel.setBorder(BorderFactory.createTitledBorder("猜测历史"));

        historyArea = new JTextArea(8, 15);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        historyArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        // 统计面板
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        statsPanel.setBackground(new Color(245, 247, 250));
        statsPanel.setBorder(BorderFactory.createTitledBorder("游戏统计"));

        statsPanel.add(new JLabel("游戏次数:"));
        statsPanel.add(new JLabel("获胜次数:"));
        statsPanel.add(new JLabel("胜率:"));
        statsPanel.add(new JLabel("最佳记录:"));

        // 组装主面板
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        centerPanel.add(controlPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(historyPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private JButton createButton(String text, Color bgColor, java.util.function.Consumer<ActionEvent> action) {
        var button = new JButton(text);
        AppleDesign.styleButton(button, bgColor, Color.WHITE);
        button.addActionListener(action::accept);
        return button;
    }

    private void changeDifficulty(ActionEvent e) {
        if (gameActive) {
            int result = JOptionPane.showConfirmDialog(this,
                "当前游戏正在进行中，切换难度将开始新游戏。\n是否继续？",
                "确认切换难度", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                difficultyCombo.setSelectedIndex(currentDifficulty);
                return;
            }
        }
        startNewGame();
    }

    private void startNewGame() {
        currentDifficulty = difficultyCombo.getSelectedIndex();
        guessHistory.clear();
        hintUsed = false;
        lastHintDistance = -1;
        gameActive = true;

        switch (currentDifficulty) {
            case 0: // 简单
                minRange = EASY_MIN;
                maxRange = EASY_MAX;
                totalAttempts = MAX_ATTEMPTS_EASY;
                break;
            case 1: // 中等
                minRange = MEDIUM_MIN;
                maxRange = MEDIUM_MAX;
                totalAttempts = MAX_ATTEMPTS_MEDIUM;
                break;
            case 2: // 困难
                minRange = HARD_MIN;
                maxRange = HARD_MAX;
                totalAttempts = MAX_ATTEMPTS_HARD;
                break;
        }

        targetNumber = random.nextInt(maxRange - minRange + 1) + minRange;
        attemptsLeft = totalAttempts;

        updateDisplay();
        historyArea.setText("");
        addToHistory("🎮 新游戏开始！");
        addToHistory(String.format("数字范围: %d - %d", minRange, maxRange));
        addToHistory(String.format("你有 %d 次机会", totalAttempts));
        addToHistory("");

        statusLabel.setText("我想了一个数字，你来猜猜看吧！");
        statusLabel.setForeground(new Color(127, 140, 141));

        guessField.setText("");
        guessField.setEnabled(true);
        guessField.requestFocus();
        guessButton.setEnabled(true);
        hintButton.setEnabled(true);

        progressBar.setValue(100);
        progressBar.setString("游戏开始");
        progressBar.setForeground(new Color(46, 204, 113));
    }

    private void makeGuess(ActionEvent e) {
        if (!gameActive) {
            JOptionPane.showMessageDialog(this, "请先开始新游戏！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = guessField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入一个数字！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字！", "错误", JOptionPane.ERROR_MESSAGE);
            guessField.selectAll();
            guessField.requestFocus();
            return;
        }

        if (guess < minRange || guess > maxRange) {
            JOptionPane.showMessageDialog(this,
                String.format("请输入 %d 到 %d 之间的数字！", minRange, maxRange),
                "提示", JOptionPane.WARNING_MESSAGE);
            guessField.selectAll();
            guessField.requestFocus();
            return;
        }

        // 记录猜测历史
        guessHistory.add(guess);
        attemptsLeft--;

        addToHistory(String.format("第%d次猜测: %d", totalAttempts - attemptsLeft, guess));

        if (guess == targetNumber) {
            handleWin();
        } else if (attemptsLeft == 0) {
            handleLoss();
        } else {
            handleWrongGuess(guess);
        }

        updateDisplay();
        guessField.setText("");
        guessField.requestFocus();
    }

    private void handleWin() {
        gamesWon++;
        gamesPlayed++;
        gameActive = false;

        int score = calculateScore();
        String message = """
            🎉 恭喜你猜对了！

            答案就是: %d
            用了 %d 次机会
            获得分数: %d 分
            """.formatted(targetNumber, totalAttempts - attemptsLeft, score);

        JOptionPane.showMessageDialog(this, message, "获胜！", JOptionPane.INFORMATION_MESSAGE);

        statusLabel.setText("🎉 恭喜你获胜了！");
        statusLabel.setForeground(AppleDesign.SYSTEM_GREEN);

        progressBar.setValue(100);
        progressBar.setString("游戏胜利！");
        progressBar.setForeground(new Color(39, 174, 96));

        guessField.setEnabled(false);
        guessButton.setEnabled(false);
        hintButton.setEnabled(false);

        addToHistory("🎉 恭喜获胜！");
        updateStats();
    }

    private void handleLoss() {
        gamesPlayed++;
        gameActive = false;

        String message = """
            😅 很遗憾，游戏结束！

            正确答案是: %d
            你已经用尽了所有机会
            再试一次吧！
            """.formatted(targetNumber);

        JOptionPane.showMessageDialog(this, message, "游戏结束", JOptionPane.WARNING_MESSAGE);

        statusLabel.setText("😅 游戏结束，再试一次吧！");
        statusLabel.setForeground(AppleDesign.SYSTEM_RED);

        progressBar.setValue(0);
        progressBar.setString("游戏失败");
        progressBar.setForeground(new Color(231, 76, 60));

        guessField.setEnabled(false);
        guessButton.setEnabled(false);
        hintButton.setEnabled(false);

        addToHistory("😅 游戏失败");
        updateStats();
    }

    private void handleWrongGuess(int guess) {
        String hint;
        if (guess < targetNumber) {
            hint = "太小了！试试更大的数字";
            statusLabel.setText("太小了！再试试");
        } else {
            hint = "太大了！试试更小的数字";
            statusLabel.setText("太大了！再试试");
        }
        statusLabel.setForeground(new Color(243, 156, 18));

        addToHistory(hint);

        // 更新进度条
        int progress = (attemptsLeft * 100) / totalAttempts;
        progressBar.setValue(progress);
        progressBar.setString(String.format("剩余 %d 次机会", attemptsLeft));

        // 根据剩余次数改变颜色
        if (attemptsLeft <= 2) {
            progressBar.setForeground(new Color(231, 76, 60));
            statusLabel.setForeground(new Color(231, 76, 60));
        } else if (attemptsLeft <= totalAttempts / 2) {
            progressBar.setForeground(new Color(243, 156, 18));
        }
    }

    private void giveHint() {
        if (!gameActive) {
            JOptionPane.showMessageDialog(this, "请先开始游戏！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (hintUsed) {
            JOptionPane.showMessageDialog(this, "每局游戏只能使用一次提示！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        hintUsed = true;
        hintButton.setEnabled(false);

        // 根据猜测历史给出更精确的提示
        if (guessHistory.isEmpty()) {
            // 第一次提示：告诉大概范围
            if (targetNumber <= minRange + (maxRange - minRange) / 3) {
                hintLabel.setText("💡 提示: 数字偏小");
            } else if (targetNumber >= maxRange - (maxRange - minRange) / 3) {
                hintLabel.setText("💡 提示: 数字偏大");
            } else {
                hintLabel.setText("💡 提示: 数字在中间范围");
            }
        } else {
            // 根据最后一次猜测给出更精确的提示
            int lastGuess = guessHistory.get(guessHistory.size() - 1);
            int distance = Math.abs(targetNumber - lastGuess);

            if (distance <= 5) {
                hintLabel.setText("💡 提示: 非常接近了！");
            } else if (distance <= 10) {
                hintLabel.setText("💡 提示: 距离不远");
            } else if (distance <= 20) {
                hintLabel.setText("💡 提示: 还有一段距离");
            } else {
                hintLabel.setText("💡 提示: 差距比较大");
            }
        }

        addToHistory("💡 使用了提示");
    }

    private void updateDisplay() {
        rangeLabel.setText(String.format("数字范围: %d - %d", minRange, maxRange));
        attemptsLabel.setText(String.format("剩余次数: %d", attemptsLeft));
    }

    private void updateStats() {
        // 这里可以添加更详细的统计功能
        // 例如：胜率、最佳记录、平均猜测次数等
    }

    private void addToHistory(String message) {
        historyArea.append(message + "\n");
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    private int calculateScore() {
        // 简单的分数计算：基础分 + 剩余次数奖励 - 难度惩罚
        int baseScore = 1000;
        int attemptsBonus = attemptsLeft * 50;
        int difficultyPenalty = currentDifficulty * 100;
        int hintPenalty = hintUsed ? 200 : 0;

        return Math.max(0, baseScore + attemptsBonus - difficultyPenalty - hintPenalty);
    }
}