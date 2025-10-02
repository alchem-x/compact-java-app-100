import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new PegSolitaire().setVisible(true);
    });
}

static class PegSolitaire extends JFrame {
    private static final int BOARD_SIZE = 7;
    private static final int CELL_SIZE = 60;
    private static final int MARGIN = 50;
    private static final int PEG_SIZE = 40;

    // 棋盘状态: true = 有钉子, false = 空位
    private boolean[][] board;
    private Point selectedPeg;
    private int remainingPegs;
    private boolean gameOver;
    private Stack<GameMove> moveHistory;

    private GamePanel gamePanel;
    private JLabel statusLabel;
    private JLabel pegCountLabel;
    private JButton newGameButton;
    private JButton undoButton;
    private JComboBox<String> boardTypeCombo;

    // 棋盘类型
    private static final String[] BOARD_TYPES = {
        "标准棋盘", "十字棋盘", "菱形棋盘"
    };

    public PegSolitaire() {
        board = new boolean[BOARD_SIZE][BOARD_SIZE];
        selectedPeg = null;
        remainingPegs = 0;
        gameOver = false;
        moveHistory = new Stack<>();

        initializeGUI();
        startNewGame();
    }

    private void initializeGUI() {
        setTitle("跳棋 (Peg Solitaire)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("🔴 跳棋 (Peg Solitaire)");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        controlPanel.add(titleLabel);

        // 棋盘类型选择
        controlPanel.add(new JLabel("棋盘类型:"));
        boardTypeCombo = new JComboBox<>(BOARD_TYPES);
        boardTypeCombo.addActionListener(e -> startNewGame());
        controlPanel.add(boardTypeCombo);

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

        add(controlPanel, BorderLayout.NORTH);

        // 游戏面板
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // 状态面板
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 剩余钉子数
        pegCountLabel = new JLabel("剩余钉子: 32");
        pegCountLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        pegCountLabel.setForeground(new Color(244, 67, 54));

        // 游戏状态
        statusLabel = new JLabel("游戏进行中...");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);

        statusPanel.add(pegCountLabel, BorderLayout.WEST);
        statusPanel.add(statusLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        // 设置窗口
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void startNewGame() {
        initializeBoard();
        selectedPeg = null;
        gameOver = false;
        moveHistory.clear();
        updateStatus();
        gamePanel.repaint();
    }

    private void initializeBoard() {
        // 清空棋盘
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = false;
            }
        }

        String boardType = (String) boardTypeCombo.getSelectedItem();
        remainingPegs = 0;

        switch (boardType) {
            case "标准棋盘":
                initializeStandardBoard();
                break;
            case "十字棋盘":
                initializeCrossBoard();
                break;
            case "菱形棋盘":
                initializeDiamondBoard();
                break;
        }
    }

    private void initializeStandardBoard() {
        // 标准跳棋棋盘布局
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // 中间3x3区域为空，其余位置有钉子
                if (i >= 2 && i <= 4 && j >= 2 && j <= 4) {
                    board[i][j] = false; // 空位
                } else {
                    board[i][j] = true; // 钉子
                    remainingPegs++;
                }
            }
        }
    }

    private void initializeCrossBoard() {
        // 十字形棋盘布局
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // 十字形区域有钉子，中心为空
                if ((i == 3 || j == 3) && !(i == 3 && j == 3)) {
                    board[i][j] = true;
                    remainingPegs++;
                } else {
                    board[i][j] = false;
                }
            }
        }
    }

    private void initializeDiamondBoard() {
        // 菱形棋盘布局
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // 菱形区域有钉子，中心为空
                if (Math.abs(i - 3) + Math.abs(j - 3) <= 3 && !(i == 3 && j == 3)) {
                    board[i][j] = true;
                    remainingPegs++;
                } else {
                    board[i][j] = false;
                }
            }
        }
    }

    private void updateStatus() {
        pegCountLabel.setText("剩余钉子: " + remainingPegs);

        if (gameOver) {
            if (remainingPegs == 1) {
                statusLabel.setText("🎉 完美！只剩1个钉子！");
            } else if (remainingPegs <= 3) {
                statusLabel.setText("👏 很好！只剩" + remainingPegs + "个钉子！");
            } else {
                statusLabel.setText("😊 游戏结束！剩余" + remainingPegs + "个钉子");
            }
        } else {
            statusLabel.setText("游戏进行中...");
        }
    }

    private void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (gameOver || !isValidMove(fromRow, fromCol, toRow, toCol)) {
            return;
        }

        // 保存移动历史
        moveHistory.push(new GameMove(fromRow, fromCol, toRow, toCol));

        // 执行移动
        board[fromRow][fromCol] = false;
        board[(fromRow + toRow) / 2][(fromCol + toCol) / 2] = false; // 移除被跳过的钉子
        board[toRow][toCol] = true;

        remainingPegs--;

        // 检查游戏是否结束
        if (!hasValidMoves()) {
            gameOver = true;
        }

        selectedPeg = null;
        updateStatus();
        gamePanel.repaint();
    }

    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        // 基本检查
        if (fromRow < 0 || fromRow >= BOARD_SIZE || fromCol < 0 || fromCol >= BOARD_SIZE ||
            toRow < 0 || toRow >= BOARD_SIZE || toCol < 0 || toCol >= BOARD_SIZE) {
            return false;
        }

        // 起始位置必须有钉子，目标位置必须为空
        if (!board[fromRow][fromCol] || board[toRow][toCol]) {
            return false;
        }

        // 检查是否是跳跃移动（距离为2）
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (!((rowDiff == 2 && colDiff == 0) || (rowDiff == 0 && colDiff == 2))) {
            return false;
        }

        // 检查中间位置是否有钉子
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;

        return board[midRow][midCol];
    }

    private boolean hasValidMoves() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j]) {
                    // 检查四个方向的跳跃
                    int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
                    for (int[] dir : directions) {
                        int newRow = i + dir[0];
                        int newCol = j + dir[1];
                        if (isValidMove(i, j, newRow, newCol)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void undoMove() {
        if (moveHistory.isEmpty()) {
            statusLabel.setText("无法悔棋！");
            return;
        }

        GameMove lastMove = moveHistory.pop();

        // 恢复棋盘状态
        board[lastMove.fromRow][lastMove.fromCol] = true;
        board[lastMove.toRow][lastMove.toCol] = false;
        board[(lastMove.fromRow + lastMove.toRow) / 2][(lastMove.fromCol + lastMove.toCol) / 2] = true;

        remainingPegs++;
        gameOver = false;
        selectedPeg = null;

        updateStatus();
        gamePanel.repaint();
    }

    // 游戏面板
    class GamePanel extends JPanel {
        public GamePanel() {
            setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE + 2 * MARGIN, BOARD_SIZE * CELL_SIZE + 2 * MARGIN));
            setBackground(new Color(0xDEB887));
            setBorder(BorderFactory.createLineBorder(new Color(0x8B4513), 2));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameOver) return;

                    int x = e.getX();
                    int y = e.getY();

                    // 转换为棋盘坐标
                    int col = (x - MARGIN + CELL_SIZE / 2) / CELL_SIZE;
                    int row = (y - MARGIN + CELL_SIZE / 2) / CELL_SIZE;

                    if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                        handleCellClick(row, col);
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
                int y = MARGIN + i * CELL_SIZE;
                g2d.drawLine(MARGIN, y, MARGIN + BOARD_SIZE * CELL_SIZE, y);
                g2d.drawLine(x, MARGIN, x, MARGIN + BOARD_SIZE * CELL_SIZE);
            }

            // 绘制钉子
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j]) {
                        drawPeg(g2d, i, j);
                    }
                }
            }

            // 高亮选中的钉子
            if (selectedPeg != null) {
                highlightSelectedPeg(g2d, selectedPeg.x, selectedPeg.y);
            }
        }

        private void drawPeg(Graphics2D g2d, int row, int col) {
            int centerX = MARGIN + col * CELL_SIZE;
            int centerY = MARGIN + row * CELL_SIZE;

            // 绘制钉子阴影
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(centerX - PEG_SIZE/2 + 2, centerY - PEG_SIZE/2 + 2, PEG_SIZE, PEG_SIZE);

            // 绘制钉子主体
            g2d.setColor(new Color(220, 20, 60)); // 深红色
            g2d.fillOval(centerX - PEG_SIZE/2, centerY - PEG_SIZE/2, PEG_SIZE, PEG_SIZE);

            // 绘制钉子高光
            g2d.setColor(new Color(255, 100, 100));
            g2d.fillOval(centerX - PEG_SIZE/2 + 5, centerY - PEG_SIZE/2 + 5, PEG_SIZE/3, PEG_SIZE/3);

            // 绘制钉子边框
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - PEG_SIZE/2, centerY - PEG_SIZE/2, PEG_SIZE, PEG_SIZE);
        }

        private void highlightSelectedPeg(Graphics2D g2d, int row, int col) {
            int centerX = MARGIN + col * CELL_SIZE;
            int centerY = MARGIN + row * CELL_SIZE;

            g2d.setColor(new Color(255, 255, 0, 128)); // 半透明黄色
            g2d.setStroke(new BasicStroke(4));
            g2d.drawOval(centerX - PEG_SIZE/2 - 2, centerY - PEG_SIZE/2 - 2, PEG_SIZE + 4, PEG_SIZE + 4);
        }

        private void handleCellClick(int row, int col) {
            if (selectedPeg == null) {
                // 选择钉子
                if (board[row][col]) {
                    selectedPeg = new Point(row, col);
                    repaint();
                }
            } else {
                // 尝试移动或重新选择
                if (row == selectedPeg.x && col == selectedPeg.y) {
                    // 取消选择
                    selectedPeg = null;
                    repaint();
                } else if (board[row][col]) {
                    // 重新选择钉子
                    selectedPeg = new Point(row, col);
                    repaint();
                } else {
                    // 尝试移动
                    makeMove(selectedPeg.x, selectedPeg.y, row, col);
                }
            }
        }
    }

    // 游戏移动记录
    static class GameMove {
        int fromRow, fromCol, toRow, toCol;

        public GameMove(int fromRow, int fromCol, int toRow, int toCol) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
        }
    }

    // 简单的Point类
    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PegSolitaire().setVisible(true);
        });
    }
}