import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "♟️ 中国象棋";

    // 界面标签
    static final String STATUS_LABEL = "状态: ";
    static final String PLAYER_LABEL = "当前玩家: ";
    static final String NEW_GAME_BUTTON = "新游戏";
    static final String UNDO_BUTTON = "悔棋";
    static final String RED_PLAYER = "红方";
    static final String BLACK_PLAYER = "黑方";

    // 棋子名称
    static final String PIECE_GENERAL = "将/帅";
    static final String PIECE_ADVISOR = "士/仕";
    static final String PIECE_ELEPHANT = "象/相";
    static final String PIECE_HORSE = "马";
    static final String PIECE_CHARIOT = "车";
    static final String PIECE_CANNON = "炮";
    static final String PIECE_SOLDIER = "兵/卒";

    // 状态消息
    static final String STATUS_GAME_START = "游戏开始，红方先行";
    static final String STATUS_SELECT_PIECE = "请选择棋子";
    static final String STATUS_SELECT_MOVE = "请选择移动位置";
    static final String STATUS_INVALID_MOVE = "无效移动";
    static final String STATUS_GAME_OVER = "游戏结束";
    static final String STATUS_RED_WINS = "红方获胜！";
    static final String STATUS_BLACK_WINS = "黑方获胜！";
    static final String STATUS_CHECK = "将军！";
    static final String STATUS_DRAW = "和棋！";

    // 帮助信息
    static final String HELP_MESSAGE = """
        中国象棋使用说明：

        • 游戏目标：将对方的将/帅吃掉，即获胜
        • 游戏规则：按照象棋规则移动棋子
        • 计分规则：先吃掉对方将/帅的一方获胜

        棋子走法：
        • 将/帅：只能在九宫格内移动，每次一格
        • 士/仕：只能在九宫格内斜走，每次一格
        • 象/相：走田字，不能过河
        • 马：走日字，有蹩马腿规则
        • 车：直线行走，格数不限
        • 炮：直线行走，吃子时需隔一个棋子
        • 兵/卒：过河前只能前进，过河后可横走

        操作说明：
        • 鼠标点击：选择棋子
        • 再次点击：移动到目标位置
        • 悔棋按钮：撤销上一步操作

        游戏技巧：
        • 保护好自己的将/帅
        • 注意对方的将军
        • 合理使用车马炮
        • 兵卒过河后威力大增

        快捷键：
        Ctrl+N - 新游戏
        Ctrl+Z - 悔棋
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new ChineseChess().setVisible(true);
    });
}

static class ChineseChess extends JFrame {
    private static final int BOARD_WIDTH = 9;
    private static final int BOARD_HEIGHT = 10;
    private static final int CELL_SIZE = 60;
    private static final int MARGIN = 50;
    private static final int BOARD_PIXEL_WIDTH = BOARD_WIDTH * CELL_SIZE;
    private static final int BOARD_PIXEL_HEIGHT = BOARD_HEIGHT * CELL_SIZE;

    // 棋子类型
    private static final int EMPTY = 0;
    private static final int RED_GENERAL = 1;
    private static final int RED_ADVISOR = 2;
    private static final int RED_ELEPHANT = 3;
    private static final int RED_HORSE = 4;
    private static final int RED_CHARIOT = 5;
    private static final int RED_CANNON = 6;
    private static final int RED_SOLDIER = 7;
    private static final int BLACK_GENERAL = 8;
    private static final int BLACK_ADVISOR = 9;
    private static final int BLACK_ELEPHANT = 10;
    private static final int BLACK_HORSE = 11;
    private static final int BLACK_CHARIOT = 12;
    private static final int BLACK_CANNON = 13;
    private static final int BLACK_SOLDIER = 14;

    private int[][] board;
    private int currentPlayer; // 0: 红方, 1: 黑方
    private Point selectedPiece;
    private boolean gameOver;
    private int winner;
    private Stack<Move> moveHistory;

    private GamePanel gamePanel;
    private JLabel statusLabel;
    private JLabel playerLabel;
    private JButton newGameButton;
    private JButton undoButton;

    // 棋子显示名称
    private static final String[] PIECE_NAMES = {
        "", "帅", "仕", "相", "马", "车", "炮", "兵",
        "将", "士", "象", "馬", "車", "砲", "卒"
    };

    public ChineseChess() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        currentPlayer = 0; // 红方先手
        selectedPiece = null;
        gameOver = false;
        winner = -1;
        moveHistory = new Stack<>();

        initializeBoard();
        initializeGUI();
        setupKeyboardShortcuts();
    }

    private void initializeBoard() {
        // 清空棋盘
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = EMPTY;
            }
        }

        // 红方棋子（下方）
        board[9][4] = RED_GENERAL;  // 帅
        board[9][3] = RED_ADVISOR;  // 仕
        board[9][5] = RED_ADVISOR;  // 仕
        board[9][2] = RED_ELEPHANT; // 相
        board[9][6] = RED_ELEPHANT; // 相
        board[9][1] = RED_HORSE;    // 马
        board[9][7] = RED_HORSE;    // 马
        board[9][0] = RED_CHARIOT;  // 车
        board[9][8] = RED_CHARIOT;  // 车
        board[7][1] = RED_CANNON;   // 炮
        board[7][7] = RED_CANNON;   // 炮
        board[6][0] = RED_SOLDIER;  // 兵
        board[6][2] = RED_SOLDIER;  // 兵
        board[6][4] = RED_SOLDIER;  // 兵
        board[6][6] = RED_SOLDIER;  // 兵
        board[6][8] = RED_SOLDIER;  // 兵

        // 黑方棋子（上方）
        board[0][4] = BLACK_GENERAL;  // 将
        board[0][3] = BLACK_ADVISOR;  // 士
        board[0][5] = BLACK_ADVISOR;  // 士
        board[0][2] = BLACK_ELEPHANT; // 象
        board[0][6] = BLACK_ELEPHANT; // 象
        board[0][1] = BLACK_HORSE;    // 馬
        board[0][7] = BLACK_HORSE;    // 馬
        board[0][0] = BLACK_CHARIOT;  // 車
        board[0][8] = BLACK_CHARIOT;  // 車
        board[2][1] = BLACK_CANNON;   // 砲
        board[2][7] = BLACK_CANNON;   // 砲
        board[3][0] = BLACK_SOLDIER;  // 卒
        board[3][2] = BLACK_SOLDIER;  // 卒
        board[3][4] = BLACK_SOLDIER;  // 卒
        board[3][6] = BLACK_SOLDIER;  // 卒
        board[3][8] = BLACK_SOLDIER;  // 卒
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel(Texts.WINDOW_TITLE);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        controlPanel.add(titleLabel);

        // 新游戏按钮
        newGameButton = new JButton("🔄 " + Texts.NEW_GAME_BUTTON);
        newGameButton.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        newGameButton.setBackground(new Color(76, 175, 80));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.addActionListener(e -> startNewGame());
        controlPanel.add(newGameButton);

        // 悔棋按钮
        undoButton = new JButton("↶ " + Texts.UNDO_BUTTON);
        undoButton.setFont(new Font("SF Pro Display", Font.BOLD, 14));
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

        // 当前玩家
        playerLabel = new JLabel(Texts.PLAYER_LABEL + Texts.RED_PLAYER);
        playerLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        playerLabel.setForeground(Color.RED);

        // 游戏状态
        statusLabel = new JLabel(Texts.STATUS_GAME_START);
        statusLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);

        statusPanel.add(playerLabel, BorderLayout.WEST);
        statusPanel.add(statusLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        // 设置窗口
        setSize(700, 800);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void startNewGame() {
        initializeBoard();
        currentPlayer = 0;
        selectedPiece = null;
        gameOver = false;
        winner = -1;
        moveHistory.clear();
        updateStatus();
        gamePanel.repaint();
    }

    private void updateStatus() {
        if (gameOver) {
            if (winner == 0) {
                playerLabel.setText("获胜者: 红方");
                playerLabel.setForeground(Color.RED);
                statusLabel.setText("🎉 " + Texts.STATUS_RED_WINS);
            } else {
                playerLabel.setText("获胜者: 黑方");
                playerLabel.setForeground(Color.BLACK);
                statusLabel.setText("🎉 " + Texts.STATUS_BLACK_WINS);
            }
        } else {
            if (currentPlayer == 0) {
                playerLabel.setText("当前玩家: 红方");
                playerLabel.setForeground(Color.RED);
            } else {
                playerLabel.setText("当前玩家: 黑方");
                playerLabel.setForeground(Color.BLACK);
            }
            statusLabel.setText(Texts.STATUS_GAME_START);
        }
    }

    private void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (gameOver || !isValidMove(fromRow, fromCol, toRow, toCol)) {
            return;
        }

        // 保存移动历史
        moveHistory.push(new Move(fromRow, fromCol, toRow, toCol, board[toRow][toCol], currentPlayer));

        // 执行移动
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;

        // 检查是否获胜
        if (isCheckmate(currentPlayer)) {
            gameOver = true;
            winner = 1 - currentPlayer;
            statusLabel.setText("🎉 " + (winner == 0 ? "红方" : "黑方") + " 获胜！");
        } else if (isCheck(currentPlayer)) {
            statusLabel.setText("⚠️ " + (currentPlayer == 0 ? "红方" : "黑方") + " 被将军！");
        }

        // 切换玩家
        currentPlayer = 1 - currentPlayer;
        selectedPiece = null;

        updateStatus();
        gamePanel.repaint();
    }

    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        // 基本检查
        if (fromRow < 0 || fromRow >= BOARD_HEIGHT || fromCol < 0 || fromCol >= BOARD_WIDTH ||
            toRow < 0 || toRow >= BOARD_HEIGHT || toCol < 0 || toCol >= BOARD_WIDTH) {
            return false;
        }

        int piece = board[fromRow][fromCol];
        int target = board[toRow][toCol];

        // 检查是否有棋子
        if (piece == EMPTY) return false;

        // 检查是否是己方棋子
        if (!isPlayerPiece(piece, currentPlayer)) return false;

        // 检查目标位置是否为己方棋子
        if (target != EMPTY && isPlayerPiece(target, currentPlayer)) return false;

        // 检查棋子移动规则
        return isValidPieceMove(piece, fromRow, fromCol, toRow, toCol);
    }

    private boolean isPlayerPiece(int piece, int player) {
        return (player == 0 && piece >= RED_GENERAL && piece <= RED_SOLDIER) ||
               (player == 1 && piece >= BLACK_GENERAL && piece <= BLACK_SOLDIER);
    }

    private boolean isValidPieceMove(int piece, int fromRow, int fromCol, int toRow, int toCol) {
        switch (piece) {
            case RED_GENERAL:
            case BLACK_GENERAL:
                return isValidGeneralMove(fromRow, fromCol, toRow, toCol);
            case RED_ADVISOR:
            case BLACK_ADVISOR:
                return isValidAdvisorMove(fromRow, fromCol, toRow, toCol);
            case RED_ELEPHANT:
            case BLACK_ELEPHANT:
                return isValidElephantMove(fromRow, fromCol, toRow, toCol);
            case RED_HORSE:
            case BLACK_HORSE:
                return isValidHorseMove(fromRow, fromCol, toRow, toCol);
            case RED_CHARIOT:
            case BLACK_CHARIOT:
                return isValidChariotMove(fromRow, fromCol, toRow, toCol);
            case RED_CANNON:
            case BLACK_CANNON:
                return isValidCannonMove(fromRow, fromCol, toRow, toCol);
            case RED_SOLDIER:
            case BLACK_SOLDIER:
                return isValidSoldierMove(fromRow, fromCol, toRow, toCol);
            default:
                return false;
        }
    }

    private boolean isValidGeneralMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        // 只能移动一格
        if (rowDiff + colDiff != 1) return false;

        // 红方帅只能在九宫内
        if (board[fromRow][fromCol] == RED_GENERAL) {
            if (toRow < 7 || toCol < 3 || toCol > 5) return false;
        }
        // 黑方将只能在九宫内
        else {
            if (toRow > 2 || toCol < 3 || toCol > 5) return false;
        }

        return true;
    }

    private boolean isValidAdvisorMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        // 只能斜走一格
        if (rowDiff != 1 || colDiff != 1) return false;

        // 只能在九宫内
        if (board[fromRow][fromCol] == RED_ADVISOR) {
            if (toRow < 7 || toCol < 3 || toCol > 5) return false;
        } else {
            if (toRow > 2 || toCol < 3 || toCol > 5) return false;
        }

        return true;
    }

    private boolean isValidElephantMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        // 必须斜走两格
        if (Math.abs(rowDiff) != 2 || Math.abs(colDiff) != 2) return false;

        // 不能过河
        if (board[fromRow][fromCol] == RED_ELEPHANT && toRow < 5) return false;
        if (board[fromRow][fromCol] == BLACK_ELEPHANT && toRow > 4) return false;

        // 检查象眼是否被堵
        int midRow = fromRow + rowDiff / 2;
        int midCol = fromCol + colDiff / 2;
        if (board[midRow][midCol] != EMPTY) return false;

        return true;
    }

    private boolean isValidHorseMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        // 检查是否走"日"字
        if (!((Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1) ||
              (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2))) {
            return false;
        }

        // 检查马腿是否被堵
        if (Math.abs(rowDiff) == 2) {
            int midRow = fromRow + rowDiff / 2;
            if (board[midRow][fromCol] != EMPTY) return false;
        } else {
            int midCol = fromCol + colDiff / 2;
            if (board[fromRow][midCol] != EMPTY) return false;
        }

        return true;
    }

    private boolean isValidChariotMove(int fromRow, int fromCol, int toRow, int toCol) {
        // 必须直线移动
        if (fromRow != toRow && fromCol != toCol) return false;

        // 检查路径是否被堵
        if (fromRow == toRow) {
            int start = Math.min(fromCol, toCol);
            int end = Math.max(fromCol, toCol);
            for (int col = start + 1; col < end; col++) {
                if (board[fromRow][col] != EMPTY) return false;
            }
        } else {
            int start = Math.min(fromRow, toRow);
            int end = Math.max(fromRow, toRow);
            for (int row = start + 1; row < end; row++) {
                if (board[row][fromCol] != EMPTY) return false;
            }
        }

        return true;
    }

    private boolean isValidCannonMove(int fromRow, int fromCol, int toRow, int toCol) {
        // 必须直线移动
        if (fromRow != toRow && fromCol != toCol) return false;

        int pieceCount = 0;

        // 计算路径上的棋子数
        if (fromRow == toRow) {
            int start = Math.min(fromCol, toCol);
            int end = Math.max(fromCol, toCol);
            for (int col = start + 1; col < end; col++) {
                if (board[fromRow][col] != EMPTY) pieceCount++;
            }
        } else {
            int start = Math.min(fromRow, toRow);
            int end = Math.max(fromRow, toRow);
            for (int row = start + 1; row < end; row++) {
                if (board[row][fromCol] != EMPTY) pieceCount++;
            }
        }

        // 炮的移动规则：
        // - 如果目标位置为空，路径上不能有棋子
        // - 如果目标位置有敌方棋子，路径上必须恰好有一个棋子
        if (board[toRow][toCol] == EMPTY) {
            return pieceCount == 0;
        } else {
            return pieceCount == 1;
        }
    }

    private boolean isValidSoldierMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        // 兵/卒只能向前移动一格
        if (board[fromRow][fromCol] == RED_SOLDIER) {
            if (rowDiff != -1 || colDiff > 1) return false;
            // 过河前不能横向移动
            if (fromRow > 4 && colDiff != 0) return false;
        } else {
            if (rowDiff != 1 || colDiff > 1) return false;
            // 过河前不能横向移动
            if (fromRow < 5 && colDiff != 0) return false;
        }

        return true;
    }

    private boolean isCheck(int player) {
        // 找到己方将/帅的位置
        int generalRow = -1, generalCol = -1;
        int generalPiece = (player == 0) ? RED_GENERAL : BLACK_GENERAL;

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] == generalPiece) {
                    generalRow = i;
                    generalCol = j;
                    break;
                }
            }
            if (generalRow != -1) break;
        }

        if (generalRow == -1) return false;

        // 检查对方棋子是否能攻击到将/帅
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                int piece = board[i][j];
                if (piece != EMPTY && !isPlayerPiece(piece, player)) {
                    if (isValidPieceMove(piece, i, j, generalRow, generalCol)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isCheckmate(int player) {
        if (!isCheck(player)) return false;

        // 尝试所有可能的移动
        for (int fromRow = 0; fromRow < BOARD_HEIGHT; fromRow++) {
            for (int fromCol = 0; fromCol < BOARD_WIDTH; fromCol++) {
                int piece = board[fromRow][fromCol];
                if (piece != EMPTY && isPlayerPiece(piece, player)) {
                    for (int toRow = 0; toRow < BOARD_HEIGHT; toRow++) {
                        for (int toCol = 0; toCol < BOARD_WIDTH; toCol++) {
                            if (isValidMove(fromRow, fromCol, toRow, toCol)) {
                                // 模拟移动
                                int originalPiece = board[toRow][toCol];
                                board[toRow][toCol] = piece;
                                board[fromRow][fromCol] = EMPTY;

                                boolean stillInCheck = isCheck(player);

                                // 恢复棋盘
                                board[fromRow][fromCol] = piece;
                                board[toRow][toCol] = originalPiece;

                                if (!stillInCheck) return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private void undoMove() {
        if (moveHistory.isEmpty()) return;

        Move lastMove = moveHistory.pop();
        board[lastMove.fromRow][lastMove.fromCol] = board[lastMove.toRow][lastMove.toCol];
        board[lastMove.toRow][lastMove.toCol] = lastMove.capturedPiece;

        currentPlayer = lastMove.player;
        gameOver = false;
        winner = -1;
        selectedPiece = null;

        updateStatus();
        gamePanel.repaint();
    }

    // 游戏面板
    class GamePanel extends JPanel {
        public GamePanel() {
            setPreferredSize(new Dimension(BOARD_PIXEL_WIDTH + 2 * MARGIN, BOARD_PIXEL_HEIGHT + 2 * MARGIN));
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

                    if (row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH) {
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
            g2d.setStroke(new BasicStroke(2));

            // 画横线
            for (int i = 0; i <= BOARD_HEIGHT; i++) {
                int y = MARGIN + i * CELL_SIZE;
                g2d.drawLine(MARGIN, y, MARGIN + BOARD_PIXEL_WIDTH, y);
            }

            // 画竖线
            for (int j = 0; j <= BOARD_WIDTH; j++) {
                int x = MARGIN + j * CELL_SIZE;
                if (j == 0 || j == BOARD_WIDTH) {
                    // 边界线贯通
                    g2d.drawLine(x, MARGIN, x, MARGIN + BOARD_PIXEL_HEIGHT);
                } else {
                    // 中间线不贯通（楚河汉界）
                    g2d.drawLine(x, MARGIN, x, MARGIN + 4 * CELL_SIZE);
                    g2d.drawLine(x, MARGIN + 5 * CELL_SIZE, x, MARGIN + BOARD_PIXEL_HEIGHT);
                }
            }

            // 画九宫格斜线
            drawPalaceLines(g2d);

            // 画棋子
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    if (board[i][j] != EMPTY) {
                        drawPiece(g2d, i, j, board[i][j]);
                    }
                }
            }

            // 高亮选中的棋子
            if (selectedPiece != null) {
                highlightSelectedPiece(g2d, selectedPiece.x, selectedPiece.y);
            }
        }

        private void drawPalaceLines(Graphics2D g2d) {
            g2d.setStroke(new BasicStroke(1));

            // 红方九宫
            int redPalaceTop = MARGIN + 7 * CELL_SIZE;
            int redPalaceBottom = MARGIN + 9 * CELL_SIZE;
            int redPalaceLeft = MARGIN + 3 * CELL_SIZE;
            int redPalaceRight = MARGIN + 5 * CELL_SIZE;

            g2d.drawLine(redPalaceLeft, redPalaceTop, redPalaceRight, redPalaceBottom);
            g2d.drawLine(redPalaceRight, redPalaceTop, redPalaceLeft, redPalaceBottom);

            // 黑方九宫
            int blackPalaceTop = MARGIN + 0 * CELL_SIZE;
            int blackPalaceBottom = MARGIN + 2 * CELL_SIZE;
            int blackPalaceLeft = MARGIN + 3 * CELL_SIZE;
            int blackPalaceRight = MARGIN + 5 * CELL_SIZE;

            g2d.drawLine(blackPalaceLeft, blackPalaceTop, blackPalaceRight, blackPalaceBottom);
            g2d.drawLine(blackPalaceRight, blackPalaceTop, blackPalaceLeft, blackPalaceBottom);
        }

        private void drawPiece(Graphics2D g2d, int row, int col, int piece) {
            int centerX = MARGIN + col * CELL_SIZE;
            int centerY = MARGIN + row * CELL_SIZE;
            int radius = CELL_SIZE / 2 - 4;

            // 绘制棋子背景
            g2d.setColor(Color.WHITE);
            g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            // 绘制棋子边框
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            // 绘制棋子文字
            String text = PIECE_NAMES[piece];
            boolean isRedPiece = (piece >= RED_GENERAL && piece <= RED_SOLDIER);

            g2d.setColor(isRedPiece ? Color.RED : Color.BLACK);
            g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();

            int textX = centerX - textWidth / 2;
            int textY = centerY + textHeight / 2 - 2;

            g2d.drawString(text, textX, textY);
        }

        private void highlightSelectedPiece(Graphics2D g2d, int row, int col) {
            int centerX = MARGIN + col * CELL_SIZE;
            int centerY = MARGIN + row * CELL_SIZE;
            int radius = CELL_SIZE / 2 - 2;

            g2d.setColor(new Color(255, 255, 0, 128)); // 半透明黄色
            g2d.setStroke(new BasicStroke(4));
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }

        private void handleCellClick(int row, int col) {
            if (selectedPiece == null) {
                // 选择棋子
                if (board[row][col] != EMPTY && isPlayerPiece(board[row][col], currentPlayer)) {
                    selectedPiece = new Point(row, col);
                    repaint();
                }
            } else {
                // 移动棋子或重新选择
                if (row == selectedPiece.x && col == selectedPiece.y) {
                    // 取消选择
                    selectedPiece = null;
                    repaint();
                } else if (board[row][col] != EMPTY && isPlayerPiece(board[row][col], currentPlayer)) {
                    // 重新选择己方棋子
                    selectedPiece = new Point(row, col);
                    repaint();
                } else {
                    // 尝试移动
                    makeMove(selectedPiece.x, selectedPiece.y, row, col);
                }
            }
        }
    }

    // 移动记录类
    static class Move {
        int fromRow, fromCol, toRow, toCol;
        int capturedPiece;
        int player;

        public Move(int fromRow, int fromCol, int toRow, int toCol, int capturedPiece, int player) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.capturedPiece = capturedPiece;
            this.player = player;
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

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_N:
                        // Ctrl+N 新游戏
                        if (ev.isControlDown()) {
                            startNewGame();
                        }
                        break;
                    case KeyEvent.VK_Z:
                        // Ctrl+Z 悔棋
                        if (ev.isControlDown()) {
                            undoMove();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChineseChess().setVisible(true);
        });
    }
}