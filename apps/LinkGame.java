import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🔗 连连看游戏";

    // 界面标签
    static final String SCORE_LABEL = "分数: ";
    static final String TIME_LABEL = "时间: ";
    static final String REMAINING_LABEL = "剩余: ";
    static final String START_BUTTON = "开始游戏";
    static final String RESET_BUTTON = "重置";
    static final String NEW_GAME_BUTTON = "新游戏";

    // 状态消息
    static final String STATUS_START_GAME = "点击开始游戏按钮开始";
    static final String STATUS_GAME_STARTED = "游戏进行中...";
    static final String STATUS_GAME_COMPLETED = "恭喜！游戏完成！";
    static final String STATUS_SELECT_FIRST = "请选择第一个方块";
    static final String STATUS_SELECT_SECOND = "请选择第二个方块";
    static final String STATUS_NO_MATCH = "不匹配，请重试";
    static final String STATUS_MATCH_FOUND = "匹配成功！";
    static final String STATUS_ICONS_SHUFFLED = "图标已重新排列";
    static final String STATUS_START_GAME_FIRST = "请先开始游戏";
    static final String STATUS_HINT_AVAILABLE = "提示：高亮显示的图标可以消除";
    static final String STATUS_NO_HINT_AVAILABLE = "没有找到可消除的图标对";
    static final String STATUS_SELECT_ANOTHER = "已选择一个图标，请选择另一个相同的图标";
    static final String STATUS_SELECTION_CANCELLED = "选择已取消，请重新选择";
    static final String STATUS_PAIR_REMAINING = "配对成功！还剩 %d 对";

    // 帮助信息
    static final String HELP_MESSAGE = """
        连连看游戏使用说明：

        • 游戏目标：找到并消除所有相同的图标对
        • 游戏规则：两个图标可以通过不超过两个转折的直线连接即可消除
        • 计分规则：每消除一对获得10分，用时越短越好

        操作说明：
        • 鼠标点击：选择图标
        • 点击两个相同的图标进行匹配
        • 如果两个图标可以连接，它们会消失

        游戏技巧：
        • 优先消除边缘的图标
        • 注意留出连接路径
        • 合理规划消除顺序
        • 快速找到可匹配的图标对

        快捷键：
        Ctrl+N - 新游戏
        Ctrl+R - 重置游戏
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new LinkGame().setVisible(true);
    });
}

static class LinkGame extends JFrame implements ActionListener {
    private static final int GRID_SIZE = 8;
    private static final int ICON_COUNT = 16;
    private JButton[][] grid;
    private JButton selectedButton = null;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private int score = 0;
    private int remainingPairs;
    private Timer gameTimer;
    private int timeElapsed = 0;
    private boolean gameStarted = false;
    private String[] icons = {
        "🍎", "🍌", "🍇", "🍓", "🍒", "🍑", "🍍", "🥝",
        "🌟", "🌙", "☀️", "🌈", "🌸", "🌺", "🌻", "🌹"
    };

    public LinkGame() {
        initializeGUI();
        initializeGame();
        setupKeyboardShortcuts();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(245, 247, 250));

        statusLabel = new JLabel(Texts.STATUS_START_GAME, SwingConstants.CENTER);
        statusLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        statusLabel.setForeground(new Color(52, 73, 94));

        scoreLabel = new JLabel(Texts.SCORE_LABEL + "0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        scoreLabel.setForeground(new Color(127, 140, 141));

        timeLabel = new JLabel(Texts.TIME_LABEL + "00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(127, 140, 141));

        topPanel.add(scoreLabel);
        topPanel.add(statusLabel);
        topPanel.add(timeLabel);

        // 游戏面板
        JPanel gamePanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        gamePanel.setBackground(new Color(189, 195, 199));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        grid = new JButton[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = new JButton();
                grid[i][j].setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                grid[i][j].setBackground(Color.WHITE);
                grid[i][j].setFocusPainted(false);
                grid[i][j].addActionListener(this);
                grid[i][j].putClientProperty("row", i);
                grid[i][j].putClientProperty("col", j);
                gamePanel.add(grid[i][j]);
            }
        }

        // 控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(new Color(245, 247, 250));

        JButton newGameButton = createButton(Texts.NEW_GAME_BUTTON, new Color(39, 174, 96), e -> startNewGame());
        JButton shuffleButton = createButton(Texts.RESET_BUTTON, new Color(243, 156, 18), e -> shuffleBoard());
        JButton hintButton = createButton("提示", new Color(52, 152, 219), e -> showHint());

        controlPanel.add(newGameButton);
        controlPanel.add(shuffleButton);
        controlPanel.add(hintButton);

        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setSize(700, 700);

        // 设置计时器
        gameTimer = new Timer(1000, e -> updateTimer());
    }

    private JButton createButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        return button;
    }

    private void initializeGame() {
        List<String> gameIcons = new ArrayList<>();
        for (int i = 0; i < ICON_COUNT; i++) {
            for (int j = 0; j < 4; j++) { // 每个图标4个
                gameIcons.add(icons[i]);
            }
        }

        // 打乱图标顺序
        Collections.shuffle(gameIcons);

        // 填充网格
        int index = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (index < gameIcons.size()) {
                    grid[i][j].setText(gameIcons.get(index));
                    grid[i][j].setEnabled(true);
                    index++;
                } else {
                    grid[i][j].setText("");
                    grid[i][j].setEnabled(false);
                    grid[i][j].setBackground(new Color(240, 240, 240));
                }
            }
        }

        remainingPairs = gameIcons.size() / 2;
        score = 0;
        timeElapsed = 0;
        gameStarted = false;
        selectedButton = null;
        selectedRow = -1;
        selectedCol = -1;

        updateDisplay();
    }

    private void startNewGame() {
        gameTimer.stop();
        initializeGame();
        statusLabel.setText(Texts.STATUS_START_GAME);
    }

    private void shuffleBoard() {
        if (!gameStarted) {
            gameStarted = true;
            gameTimer.start();
        }

        List<String> currentIcons = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j].isEnabled() && !grid[i][j].getText().isEmpty()) {
                    currentIcons.add(grid[i][j].getText());
                }
            }
        }

        Collections.shuffle(currentIcons);

        int index = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j].isEnabled() && !grid[i][j].getText().isEmpty()) {
                    grid[i][j].setText(currentIcons.get(index));
                    index++;
                }
            }
        }

        clearSelection();
        statusLabel.setText(Texts.STATUS_ICONS_SHUFFLED);
    }

    private void showHint() {
        if (!gameStarted) {
            statusLabel.setText(Texts.STATUS_START_GAME_FIRST);
            return;
        }

        // 简单的提示：高亮显示一个可以消除的图标对
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j].isEnabled() && !grid[i][j].getText().isEmpty()) {
                    String icon = grid[i][j].getText();
                    for (int x = 0; x < GRID_SIZE; x++) {
                        for (int y = 0; y < GRID_SIZE; y++) {
                            if (x == i && y == j) continue;
                            if (grid[x][y].isEnabled() && grid[x][y].getText().equals(icon)) {
                                // 找到匹配的图标对，短暂高亮显示
                                final int finalI = i;
                                final int finalX = x;
                                final int finalJ = j;
                                final int finalY = y;

                                grid[finalI][finalJ].setBackground(new Color(255, 255, 200));
                                grid[finalX][finalY].setBackground(new Color(255, 255, 200));

                                Timer hintTimer = new Timer(1000, e -> {
                                    grid[finalI][finalJ].setBackground(Color.WHITE);
                                    grid[finalX][finalY].setBackground(Color.WHITE);
                                });
                                hintTimer.setRepeats(false);
                                hintTimer.start();

                                statusLabel.setText(Texts.STATUS_HINT_AVAILABLE);
                                return;
                            }
                        }
                    }
                }
            }
        }

        statusLabel.setText(Texts.STATUS_NO_HINT_AVAILABLE);
    }

    private void updateTimer() {
        timeElapsed++;
        int minutes = timeElapsed / 60;
        int seconds = timeElapsed % 60;
        timeLabel.setText(String.format("时间: %02d:%02d", minutes, seconds));
    }

    private void updateDisplay() {
        scoreLabel.setText(String.format("得分: %d", score));
        timeLabel.setText(String.format("时间: %02d:%02d", timeElapsed / 60, timeElapsed % 60));
    }

    private void clearSelection() {
        if (selectedButton != null) {
            selectedButton.setBackground(Color.WHITE);
            selectedButton = null;
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameStarted) {
            gameStarted = true;
            gameTimer.start();
        }

        JButton clickedButton = (JButton) e.getSource();
        int row = (int) clickedButton.getClientProperty("row");
        int col = (int) clickedButton.getClientProperty("col");

        if (!clickedButton.isEnabled() || clickedButton.getText().isEmpty()) {
            return;
        }

        if (selectedButton == null) {
            // 第一次选择
            selectedButton = clickedButton;
            selectedRow = row;
            selectedCol = col;
            selectedButton.setBackground(new Color(173, 216, 230));
            statusLabel.setText(Texts.STATUS_SELECT_ANOTHER);
        } else if (selectedButton == clickedButton) {
            // 取消选择
            clearSelection();
            statusLabel.setText(Texts.STATUS_SELECTION_CANCELLED);
        } else {
            // 第二次选择
            if (selectedButton.getText().equals(clickedButton.getText())) {
                // 匹配成功
                selectedButton.setEnabled(false);
                clickedButton.setEnabled(false);
                selectedButton.setBackground(new Color(144, 238, 144));
                clickedButton.setBackground(new Color(144, 238, 144));

                score += 10;
                remainingPairs--;

                if (remainingPairs == 0) {
                    gameTimer.stop();
                    statusLabel.setText(Texts.STATUS_GAME_COMPLETED);
                    JOptionPane.showMessageDialog(this,
                        String.format("游戏完成！\n用时: %02d:%02d\n得分: %d",
                            timeElapsed / 60, timeElapsed % 60, score),
                        "游戏完成", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    statusLabel.setText(String.format(Texts.STATUS_PAIR_REMAINING, remainingPairs));
                }
            } else {
                // 匹配失败
                selectedButton.setBackground(new Color(255, 182, 193));
                clickedButton.setBackground(new Color(255, 182, 193));

                Timer errorTimer = new Timer(500, evt -> {
                    selectedButton.setBackground(Color.WHITE);
                    clickedButton.setBackground(Color.WHITE);
                });
                errorTimer.setRepeats(false);
                errorTimer.start();

                statusLabel.setText(Texts.STATUS_NO_MATCH);
            }

            selectedButton = null;
            selectedRow = -1;
            selectedCol = -1;
        }

        updateDisplay();
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_N:
                        // Ctrl+N 新游戏
                        if (ev.isControlDown()) {
                            startNewGame();
                        }
                        break;
                    case KeyEvent.VK_R:
                        // Ctrl+R 重置游戏
                        if (ev.isControlDown()) {
                            shuffleBoard();
                        }
                        break;
                    case KeyEvent.VK_H:
                        // Ctrl+H 显示帮助
                        if (ev.isControlDown()) {
                            showHelp();
                        }
                        break;
                    case KeyEvent.VK_F1:
                        // F1键显示帮助
                        showHelp();
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

    private void showHelp() {
        JOptionPane.showMessageDialog(this, Texts.HELP_MESSAGE, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }
}