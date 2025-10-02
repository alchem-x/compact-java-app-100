import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Gomoku().setVisible(true);
    });
}

static class Gomoku extends JFrame {
    private static final int BOARD_SIZE = 15; // 15x15 棋盘
    private static final int CELL_SIZE = 30;
    private static final int BOARD_PIXEL_SIZE = BOARD_SIZE * CELL_SIZE;
    private static final int MARGIN = 50;

    private int[][] board; // 0: 空, 1: 黑子, 2: 白子
    private int currentPlayer; // 1: 黑子, 2: 白子
    private boolean gameOver;
    private int winner;
    private Stack<GameMove> moveHistory;

    private GamePanel gamePanel;
    private JLabel statusLabel;
    private JLabel playerLabel;
    private JButton newGameButton;
    private JButton undoButton;
    private JButton aiButton;
    private JComboBox<String> modeCombo;
    private JCheckBox aiAssistCheck;

    private boolean aiMode = false;
    private boolean aiThinking = false;

    public Gomoku() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        currentPlayer = 1; // 黑子先手
        gameOver = false;
        winner = 0;
        moveHistory = new Stack<>();

        initializeGUI();
        startNewGame();
    }

    private void initializeGUI() {
        setTitle("五子棋");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("⚫⚪ 五子棋");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        controlPanel.add(titleLabel);

        // 游戏模式
        controlPanel.add(new JLabel("模式:"));
        modeCombo = new JComboBox<>(new String[]{"双人对战", "人机对战"});
        modeCombo.addActionListener(e -> {
            aiMode = modeCombo.getSelectedIndex() == 1;
            startNewGame();
        });
        controlPanel.add(modeCombo);

        // 新游戏按钮
        newGameButton = new JButton("🔄 新游戏");
        newGameButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        newGameButton.setBackground(new Color(76, 175, 80));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.addActionListener(e -> startNewGame());
        controlPanel.add(newGameButton);

        // 悔棋按钮
        undoButton = new JButton("↶ 悔棋");
        undoButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        undoButton.setBackground(new Color(255, 152, 0));
        undoButton.setForeground(Color.WHITE);
        undoButton.addActionListener(e -> undoMove());
        controlPanel.add(undoButton);

        // AI提示
        aiAssistCheck = new JCheckBox("AI提示");
        aiAssistCheck.setSelected(false);
        controlPanel.add(aiAssistCheck);

        add(controlPanel, BorderLayout.NORTH);

        // 游戏面板
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // 状态面板
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 当前玩家
        playerLabel = new JLabel("当前玩家: 黑子");
        playerLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        playerLabel.setForeground(Color.BLACK);

        // 游戏状态
        statusLabel = new JLabel("游戏进行中...");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);

        statusPanel.add(playerLabel, BorderLayout.WEST);
        statusPanel.add(statusLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        // 设置窗口
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void startNewGame() {
        // 清空棋盘
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
            }
        }

        currentPlayer = 1; // 黑子先手
        gameOver = false;
        winner = 0;
        moveHistory.clear();
        aiThinking = false;

        updateStatus();
        gamePanel.repaint();
    }

    private void updateStatus() {
        if (gameOver) {
            if (winner == 1) {
                playerLabel.setText("获胜者: 黑子");
                playerLabel.setForeground(Color.BLACK);
                statusLabel.setText("🎉 黑子获胜！");
            } else if (winner == 2) {
                playerLabel.setText("获胜者: 白子");
                playerLabel.setForeground(Color.GRAY);
                statusLabel.setText("🎉 白子获胜！");
            }
        } else {
            if (currentPlayer == 1) {
                playerLabel.setText("当前玩家: 黑子");
                playerLabel.setForeground(Color.BLACK);
            } else {
                playerLabel.setText("当前玩家: 白子");
                playerLabel.setForeground(Color.GRAY);
            }

            if (aiMode && currentPlayer == 2) {
                statusLabel.setText("AI 思考中...");
                SwingUtilities.invokeLater(() -> {
                    javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
                        makeAIMove();
                        ((javax.swing.Timer) e.getSource()).stop();
                    });
                    timer.setRepeats(false);
                    timer.start();
                });
            } else {
                statusLabel.setText("游戏进行中...");
            }
        }
    }

    private void makeMove(int row, int col) {
        if (gameOver || board[row][col] != 0 || aiThinking) {
            return;
        }

        // 保存当前状态到历史记录
        moveHistory.push(new GameMove(row, col, currentPlayer));

        // 放置棋子
        board[row][col] = currentPlayer;

        // 检查是否获胜
        if (checkWin(row, col, currentPlayer)) {
            gameOver = true;
            winner = currentPlayer;
            statusLabel.setText("🎉 " + (currentPlayer == 1 ? "黑子" : "白子") + " 获胜！");
        } else if (isBoardFull()) {
            gameOver = true;
            statusLabel.setText("平局！");
        } else {
            // 切换玩家
            currentPlayer = 3 - currentPlayer; // 1 -> 2, 2 -> 1
        }

        updateStatus();
        gamePanel.repaint();
    }

    private void makeAIMove() {
        if (gameOver || currentPlayer != 2) {
            return;
        }

        aiThinking = true;

        // 使用简单的AI策略
        Point bestMove = findBestMove();
        if (bestMove != null) {
            makeMove(bestMove.x, bestMove.y);
        }

        aiThinking = false;
    }

    private Point findBestMove() {
        // AI策略：优先防守，其次进攻
        Point move = findWinningMove(2); // 检查AI是否能赢
        if (move != null) return move;

        move = findWinningMove(1); // 检查是否需要阻止玩家赢
        if (move != null) return move;

        // 使用评分系统选择最佳位置
        int bestScore = Integer.MIN_VALUE;
        Point bestMove = null;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    int score = evaluatePosition(i, j, 2);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new Point(i, j);
                    }
                }
            }
        }

        return bestMove;
    }

    private Point findWinningMove(int player) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = player;
                    if (checkWin(i, j, player)) {
                        board[i][j] = 0; // 恢复
                        return new Point(i, j);
                    }
                    board[i][j] = 0; // 恢复
                }
            }
        }
        return null;
    }

    private int evaluatePosition(int row, int col, int player) {
        int score = 0;

        // 评估四个方向
        int[] directions = {-1, 0, 1, 0, -1};
        for (int d = 0; d < 4; d++) {
            int count = 1; // 当前位置
            int empty = 0;

            // 正向检查
            for (int i = 1; i < 5; i++) {
                int newRow = row + directions[d] * i;
                int newCol = col + directions[d + 1] * i;
                if (!isValidPosition(newRow, newCol)) break;

                if (board[newRow][newCol] == player) {
                    count++;
                } else if (board[newRow][newCol] == 0) {
                    empty++;
                } else {
                    break;
                }
            }

            // 反向检查
            for (int i = 1; i < 5; i++) {
                int newRow = row - directions[d] * i;
                int newCol = col - directions[d + 1] * i;
                if (!isValidPosition(newRow, newCol)) break;

                if (board[newRow][newCol] == player) {
                    count++;
                } else if (board[newRow][newCol] == 0) {
                    empty++;
                } else {
                    break;
                }
            }

            // 评分
            if (count >= 4) score += 10000;
            else if (count == 3 && empty >= 1) score += 1000;
            else if (count == 2 && empty >= 2) score += 100;
            else if (count == 1 && empty >= 3) score += 10;
        }

        // 中心位置加分
        int centerDistance = Math.abs(row - BOARD_SIZE / 2) + Math.abs(col - BOARD_SIZE / 2);
        score += (BOARD_SIZE - centerDistance) * 5;

        return score;
    }

    private boolean checkWin(int row, int col, int player) {
        // 检查四个方向
        int[] directions = {-1, 0, 1, 0, -1};

        for (int d = 0; d < 4; d++) {
            int count = 1; // 当前位置

            // 正向检查
            for (int i = 1; i < 5; i++) {
                int newRow = row + directions[d] * i;
                int newCol = col + directions[d + 1] * i;
                if (!isValidPosition(newRow, newCol) || board[newRow][newCol] != player) break;
                count++;
            }

            // 反向检查
            for (int i = 1; i < 5; i++) {
                int newRow = row - directions[d] * i;
                int newCol = col - directions[d + 1] * i;
                if (!isValidPosition(newRow, newCol) || board[newRow][newCol] != player) break;
                count++;
            }

            if (count >= 5) return true;
        }

        return false;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) return false;
            }
        }
        return true;
    }

    private void undoMove() {
        if (moveHistory.isEmpty()) {
            statusLabel.setText("无法悔棋！");
            return;
        }

        GameMove lastMove = moveHistory.pop();
        board[lastMove.row][lastMove.col] = 0;

        // 切换回上一个玩家
        currentPlayer = lastMove.player;
        gameOver = false;
        winner = 0;

        updateStatus();
        gamePanel.repaint();
    }

    // 游戏面板
    class GamePanel extends JPanel {
        public GamePanel() {
            setPreferredSize(new Dimension(BOARD_PIXEL_SIZE + 2 * MARGIN, BOARD_PIXEL_SIZE + 2 * MARGIN));
            setBackground(new Color(0xDEB887));
            setBorder(BorderFactory.createLineBorder(new Color(0x8B4513), 2));

            // 添加鼠标监听器
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameOver || (aiMode && currentPlayer == 2)) {
                        return;
                    }

                    int x = e.getX();
                    int y = e.getY();

                    // 转换为棋盘坐标
                    int col = (x - MARGIN + CELL_SIZE / 2) / CELL_SIZE;
                    int row = (y - MARGIN + CELL_SIZE / 2) / CELL_SIZE;

                    if (isValidPosition(row, col)) {
                        makeMove(row, col);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard(g);
        }

        private void drawBoard(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制棋盘网格
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));

            for (int i = 0; i <= BOARD_SIZE; i++) {
                int x = MARGIN + i * CELL_SIZE;
                int y1 = MARGIN;
                int y2 = MARGIN + BOARD_PIXEL_SIZE;
                g2d.drawLine(x, y1, x, y2);

                int y = MARGIN + i * CELL_SIZE;
                int x1 = MARGIN;
                int x2 = MARGIN + BOARD_PIXEL_SIZE;
                g2d.drawLine(x1, y, x2, y);
            }

            // 绘制棋子
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] != 0) {
                        drawPiece(g2d, i, j, board[i][j]);
                    }
                }
            }

            // 如果启用了AI提示，显示建议位置
            if (aiAssistCheck.isSelected() && !gameOver && currentPlayer == 1) {
                Point aiSuggestion = findBestMove();
                if (aiSuggestion != null) {
                    drawAISuggestion(g2d, aiSuggestion.x, aiSuggestion.y);
                }
            }
        }

        private void drawPiece(Graphics2D g2d, int row, int col, int player) {
            int centerX = MARGIN + col * CELL_SIZE;
            int centerY = MARGIN + row * CELL_SIZE;
            int radius = CELL_SIZE / 2 - 4;

            g2d.setColor(player == 1 ? Color.BLACK : Color.WHITE);
            g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            g2d.setColor(player == 1 ? Color.WHITE : Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }

        private void drawAISuggestion(Graphics2D g2d, int row, int col) {
            int centerX = MARGIN + col * CELL_SIZE;
            int centerY = MARGIN + row * CELL_SIZE;
            int radius = CELL_SIZE / 2 - 8;

            g2d.setColor(new Color(255, 0, 0, 128)); // 半透明红色
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
    }

    // 游戏移动记录
    static class GameMove {
        int row, col, player;

        public GameMove(int row, int col, int player) {
            this.row = row;
            this.col = col;
            this.player = player;
        }
    }

    // 简单Point类
    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}