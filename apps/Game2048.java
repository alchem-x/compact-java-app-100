import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🔢 2048 游戏";

    // 界面标签
    static final String GAME_TITLE = "2048";
    static final String SCORE_TITLE = "分数";
    static final String BEST_SCORE_TITLE = "最高分";
    static final String INSTRUCTION_TEXT = "🎮 使用方向键移动方块，合并相同数字，达到2048！";
    static final String START_GAME_STATUS = "开始游戏！";
    static final String WIN_MESSAGE = "恭喜！你赢了！继续游戏可以达到更高分数！";
    static final String WIN_DIALOG_TITLE = "胜利！";
    static final String WIN_DIALOG_MESSAGE = "恭喜！你成功合成2048！";
    static final String GAME_OVER_STATUS = "游戏结束！";
    static final String GAME_OVER_DIALOG_TITLE = "游戏结束";
    static final String GAME_OVER_DIALOG_MESSAGE = "游戏结束！最终得分：";
    static final String UNDO_SUCCESS_STATUS = "撤销成功！";
    static final String UNDO_FAILED_STATUS = "无法撤销！";

    // 按钮文本
    static final String NEW_GAME_BUTTON = "🔄 新游戏";
    static final String UNDO_BUTTON = "↶ 撤销";

    // 帮助信息
    static final String HELP_MESSAGE = """
        2048游戏使用说明：

        • 游戏目标：通过移动方块，合并相同数字，最终合成2048
        • 游戏规则：每次移动后会在空位随机生成2或4，当无法移动时游戏结束
        • 计分规则：每次合并数字会获得相应分数，目标是获得最高分数

        操作说明：
        • ↑ 或 W：向上移动
        • ↓ 或 S：向下移动
        • ← 或 A：向左移动
        • → 或 D：向右移动

        游戏技巧：
        • 尽量将大数字移向一个方向
        • 保持游戏板整洁，避免数字分散
        • 优先合并较小的数字
        • 预留空间给新的数字
        • 合理使用撤销功能

        特殊功能：
        • 新游戏：重新开始游戏
        • 撤销：撤销上一步操作（限一次）

        快捷键：
        方向键/WASD - 移动控制
        Ctrl+Z - 撤销操作
        Ctrl+N - 新游戏
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Game2048().setVisible(true);
    });
}

static class Game2048 extends JFrame {
    private static final int GRID_SIZE = 4;
    private static final int CELL_SIZE = 100;
    private static final int CELL_PADDING = 10;
    private static final int BOARD_SIZE = GRID_SIZE * CELL_SIZE + (GRID_SIZE + 1) * CELL_PADDING;

    private int[][] board;
    private int score;
    private int bestScore;
    private boolean gameWon;
    private boolean gameOver;
    private Random random = new Random();

    private JLabel scoreLabel;
    private JLabel bestScoreLabel;
    private JLabel statusLabel;
    private GameBoard gameBoard;
    private JButton newGameButton;
    private JButton undoButton;
    private Stack<GameState> undoStack;

    // 颜色配置
    private static final Color[] CELL_COLORS = {
        new Color(0xCDC1B4), // 0
        new Color(0xEEE4DA), // 2
        new Color(0xEDE0C8), // 4
        new Color(0xF2B179), // 8
        new Color(0xF59563), // 16
        new Color(0xF67C5F), // 32
        new Color(0xF65E3B), // 64
        new Color(0xEDCF72), // 128
        new Color(0xEDCC61), // 256
        new Color(0xEDC850), // 512
        new Color(0xEDC53F), // 1024
        new Color(0xEDC22E)  // 2048
    };

    private static final Color TEXT_DARK = new Color(0x776E65);
    private static final Color TEXT_LIGHT = new Color(0xF9F6F2);

    public Game2048() {
        board = new int[GRID_SIZE][GRID_SIZE];
        score = 0;
        bestScore = loadBestScore();
        gameWon = false;
        gameOver = false;
        undoStack = new Stack<>();

        initializeGUI();
        startNewGame();
        setupKeyboardShortcuts();
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.setBackground(new Color(0xFAF8EF));

        // 标题和分数面板
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0xFAF8EF));

        // 标题
        JLabel titleLabel = new JLabel(Texts.GAME_TITLE);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 64));
        titleLabel.setForeground(new Color(0x776E65));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // 分数面板
        JPanel scorePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        scorePanel.setBackground(new Color(0xFAF8EF));

        // 当前分数
        JPanel currentScorePanel = new JPanel();
        currentScorePanel.setLayout(new BoxLayout(currentScorePanel, BoxLayout.Y_AXIS));
        currentScorePanel.setBackground(new Color(0xBBADA0));
        currentScorePanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        currentScorePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 15, 5, 15),
            BorderFactory.createLineBorder(new Color(0xBBADA0), 8, true)
        ));

        JLabel scoreTitle = new JLabel(Texts.SCORE_TITLE);
        scoreTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        scoreTitle.setForeground(TEXT_LIGHT);
        scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel(String.valueOf(score));
        scoreLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        scoreLabel.setForeground(TEXT_LIGHT);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        currentScorePanel.add(scoreTitle);
        currentScorePanel.add(scoreLabel);

        // 最高分数
        JPanel bestScorePanel = new JPanel();
        bestScorePanel.setLayout(new BoxLayout(bestScorePanel, BoxLayout.Y_AXIS));
        bestScorePanel.setBackground(new Color(0xBBADA0));
        bestScorePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 15, 5, 15),
            BorderFactory.createLineBorder(new Color(0xBBADA0), 8, true)
        ));

        JLabel bestScoreTitle = new JLabel(Texts.BEST_SCORE_TITLE);
        bestScoreTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        bestScoreTitle.setForeground(TEXT_LIGHT);
        bestScoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        bestScoreLabel = new JLabel(String.valueOf(bestScore));
        bestScoreLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        bestScoreLabel.setForeground(TEXT_LIGHT);
        bestScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bestScorePanel.add(bestScoreTitle);
        bestScorePanel.add(bestScoreLabel);

        JPanel scoresContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        scoresContainer.setBackground(new Color(0xFAF8EF));
        scoresContainer.add(currentScorePanel);
        scoresContainer.add(bestScorePanel);

        headerPanel.add(scoresContainer, BorderLayout.EAST);

        topPanel.add(headerPanel);

        // 说明文字
        JLabel instructionLabel = new JLabel(Texts.INSTRUCTION_TEXT);
        instructionLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        instructionLabel.setForeground(TEXT_DARK);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        topPanel.add(instructionLabel);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(0xFAF8EF));

        newGameButton = new JButton(Texts.NEW_GAME_BUTTON);
        newGameButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        newGameButton.setBackground(new Color(0x8F7A66));
        newGameButton.setForeground(TEXT_LIGHT);
        newGameButton.setFocusPainted(false);
        newGameButton.addActionListener(e -> startNewGame());

        undoButton = new JButton(Texts.UNDO_BUTTON);
        undoButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        undoButton.setBackground(new Color(0x9E948A));
        undoButton.setForeground(TEXT_LIGHT);
        undoButton.setFocusPainted(false);
        undoButton.addActionListener(e -> undo());

        buttonPanel.add(newGameButton);
        buttonPanel.add(undoButton);

        topPanel.add(buttonPanel);

        // 状态标签
        statusLabel = new JLabel(Texts.START_GAME_STATUS);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        statusLabel.setForeground(TEXT_DARK);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        topPanel.add(statusLabel);

        add(topPanel, BorderLayout.NORTH);

        // 游戏面板
        gameBoard = new GameBoard();
        add(gameBoard, BorderLayout.CENTER);

        // 设置键盘监听器
        addKeyListener(new GameKeyListener());
        setFocusable(true);

        // 设置窗口
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void startNewGame() {
        board = new int[GRID_SIZE][GRID_SIZE];
        score = 0;
        gameWon = false;
        gameOver = false;
        undoStack.clear();

        updateScore();
        statusLabel.setText(Texts.START_GAME_STATUS);

        // 添加两个初始方块
        addNewTile();
        addNewTile();

        gameBoard.repaint();
    }

    private void addNewTile() {
        List<Point> emptyCells = new ArrayList<>();

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new Point(i, j));
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            Point cell = emptyCells.get(random.nextInt(emptyCells.size()));
            // 90%概率生成2，10%概率生成4
            board[cell.x][cell.y] = random.nextDouble() < 0.9 ? 2 : 4;
        }
    }

    private void saveGameState() {
        int[][] boardCopy = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(board[i], 0, boardCopy[i], 0, GRID_SIZE);
        }
        undoStack.push(new GameState(boardCopy, score));
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            GameState previousState = undoStack.pop();
            board = previousState.getBoard();
            score = previousState.getScore();
            updateScore();
            gameBoard.repaint();
            statusLabel.setText(Texts.UNDO_SUCCESS_STATUS);
        } else {
            statusLabel.setText(Texts.UNDO_FAILED_STATUS);
        }
    }

    private boolean move(int direction) {
        boolean moved = false;
        int[][] newBoard = new int[GRID_SIZE][GRID_SIZE];

        // 复制当前状态
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, GRID_SIZE);
        }

        switch (direction) {
            case KeyEvent.VK_UP:
                moved = moveUp();
                break;
            case KeyEvent.VK_DOWN:
                moved = moveDown();
                break;
            case KeyEvent.VK_LEFT:
                moved = moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                moved = moveRight();
                break;
        }

        if (moved) {
            saveGameState();
            addNewTile();
            updateScore();
            gameBoard.repaint();

            if (!gameWon && hasWon()) {
                gameWon = true;
                statusLabel.setText(Texts.WIN_MESSAGE);
                JOptionPane.showMessageDialog(this, Texts.WIN_DIALOG_MESSAGE, Texts.WIN_DIALOG_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
            } else if (isGameOver()) {
                gameOver = true;
                statusLabel.setText(Texts.GAME_OVER_STATUS);
                JOptionPane.showMessageDialog(this, Texts.GAME_OVER_DIALOG_MESSAGE + score, Texts.GAME_OVER_DIALOG_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return moved;
    }

    private boolean moveLeft() {
        boolean moved = false;

        for (int i = 0; i < GRID_SIZE; i++) {
            int[] row = new int[GRID_SIZE];
            for (int j = 0; j < GRID_SIZE; j++) {
                row[j] = board[i][j];
            }

            int[] newRow = mergeRow(row);
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] != newRow[j]) {
                    moved = true;
                }
                board[i][j] = newRow[j];
            }
        }

        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;

        for (int i = 0; i < GRID_SIZE; i++) {
            int[] row = new int[GRID_SIZE];
            for (int j = 0; j < GRID_SIZE; j++) {
                row[j] = board[i][GRID_SIZE - 1 - j];
            }

            int[] newRow = mergeRow(row);
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][GRID_SIZE - 1 - j] != newRow[j]) {
                    moved = true;
                }
                board[i][GRID_SIZE - 1 - j] = newRow[j];
            }
        }

        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;

        for (int j = 0; j < GRID_SIZE; j++) {
            int[] column = new int[GRID_SIZE];
            for (int i = 0; i < GRID_SIZE; i++) {
                column[i] = board[i][j];
            }

            int[] newColumn = mergeRow(column);
            for (int i = 0; i < GRID_SIZE; i++) {
                if (board[i][j] != newColumn[i]) {
                    moved = true;
                }
                board[i][j] = newColumn[i];
            }
        }

        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;

        for (int j = 0; j < GRID_SIZE; j++) {
            int[] column = new int[GRID_SIZE];
            for (int i = 0; i < GRID_SIZE; i++) {
                column[i] = board[GRID_SIZE - 1 - i][j];
            }

            int[] newColumn = mergeRow(column);
            for (int i = 0; i < GRID_SIZE; i++) {
                if (board[GRID_SIZE - 1 - i][j] != newColumn[i]) {
                    moved = true;
                }
                board[GRID_SIZE - 1 - i][j] = newColumn[i];
            }
        }

        return moved;
    }

    private int[] mergeRow(int[] row) {
        int[] merged = new int[GRID_SIZE];
        int index = 0;
        boolean[] mergedFlags = new boolean[GRID_SIZE];

        // 移除零
        for (int value : row) {
            if (value != 0) {
                if (index > 0 && merged[index - 1] == value && !mergedFlags[index - 1]) {
                    merged[index - 1] = value * 2;
                    score += value * 2;
                    mergedFlags[index - 1] = true;
                } else {
                    merged[index++] = value;
                }
            }
        }

        return merged;
    }

    private boolean hasWon() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] == 2048) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGameOver() {
        // 检查是否有空位
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }

        // 检查是否还能合并
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int current = board[i][j];
                // 检查右边
                if (j < GRID_SIZE - 1 && board[i][j + 1] == current) {
                    return false;
                }
                // 检查下边
                if (i < GRID_SIZE - 1 && board[i + 1][j] == current) {
                    return false;
                }
            }
        }

        return true;
    }

    private void updateScore() {
        scoreLabel.setText(String.valueOf(score));
        if (score > bestScore) {
            bestScore = score;
            bestScoreLabel.setText(String.valueOf(bestScore));
            saveBestScore();
        }
    }

    private void saveBestScore() {
        try {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(Game2048.class);
            prefs.putInt("bestScore", bestScore);
        } catch (Exception e) {
            // 忽略保存错误
        }
    }

    private int loadBestScore() {
        try {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(Game2048.class);
            return prefs.getInt("bestScore", 0);
        } catch (Exception e) {
            return 0;
        }
    }

    // 游戏面板
    class GameBoard extends JPanel {
        public GameBoard() {
            setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
            setBackground(new Color(0xBBADA0));
            setFocusable(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard(g);
        }

        private void drawBoard(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制背景网格
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    int x = j * CELL_SIZE + (j + 1) * CELL_PADDING;
                    int y = i * CELL_SIZE + (i + 1) * CELL_PADDING;

                    // 绘制单元格背景
                    g2d.setColor(CELL_COLORS[0]);
                    g2d.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE, 10, 10);

                    // 绘制数字
                    int value = board[i][j];
                    if (value != 0) {
                        drawTile(g2d, value, x, y);
                    }
                }
            }
        }

        private void drawTile(Graphics2D g2d, int value, int x, int y) {
            // 获取颜色索引
            int colorIndex = (int) (Math.log(value) / Math.log(2));
            if (colorIndex >= CELL_COLORS.length) {
                colorIndex = CELL_COLORS.length - 1;
            }

            // 绘制单元格
            g2d.setColor(CELL_COLORS[colorIndex]);
            g2d.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE, 10, 10);

            // 绘制数字
            g2d.setColor(value > 4 ? TEXT_LIGHT : TEXT_DARK);

            // 根据数字大小调整字体
            int fontSize;
            if (value < 100) {
                fontSize = 36;
            } else if (value < 1000) {
                fontSize = 32;
            } else if (value < 10000) {
                fontSize = 28;
            } else {
                fontSize = 24;
            }

            g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, fontSize));

            String text = String.valueOf(value);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();

            int textX = x + (CELL_SIZE - textWidth) / 2;
            int textY = y + (CELL_SIZE + textHeight) / 2 - 5;

            g2d.drawString(text, textX, textY);
        }
    }

    // 键盘监听器
    class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameOver) {
                return;
            }

            boolean moved = false;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    moved = move(KeyEvent.VK_UP);
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    moved = move(KeyEvent.VK_DOWN);
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    moved = move(KeyEvent.VK_LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    moved = move(KeyEvent.VK_RIGHT);
                    break;
                case KeyEvent.VK_Z:
                    // Ctrl+Z 撤销
                    if (e.isControlDown()) {
                        undo();
                    }
                    break;
                case KeyEvent.VK_N:
                    // Ctrl+N 新游戏
                    if (e.isControlDown()) {
                        startNewGame();
                    }
                    break;
                case KeyEvent.VK_H:
                    // Ctrl+H 显示帮助
                    if (e.isControlDown()) {
                        showHelp();
                    }
                    break;
                case KeyEvent.VK_F1:
                    // F1键显示帮助
                    showHelp();
                    break;
            }
        }
    }

    private void setupKeyboardShortcuts() {
        // 添加额外的键盘快捷键支持
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_H:
                        // H键显示帮助
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

    // 游戏状态类
    static class GameState {
        private int[][] board;
        private int score;

        public GameState(int[][] board, int score) {
            this.board = new int[GRID_SIZE][GRID_SIZE];
            for (int i = 0; i < GRID_SIZE; i++) {
                System.arraycopy(board[i], 0, this.board[i], 0, GRID_SIZE);
            }
            this.score = score;
        }

        public int[][] getBoard() {
            return board;
        }

        public int getScore() {
            return score;
        }
    }
}