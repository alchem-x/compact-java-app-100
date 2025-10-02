import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🟦 俄罗斯方块";

    // 界面标签
    static final String SCORE_LABEL = "分数: ";
    static final String LINES_LABEL = "行数: ";
    static final String NEXT_LABEL = "下一个:";
    static final String CONTROLS_LABEL = "控制:";
    static final String MOVE_LEFT_RIGHT = "A/D - 左右移动";
    static final String FAST_DROP = "S - 快速下落";
    static final String ROTATE = "W - 旋转";
    static final String PAUSE = "空格 - 暂停";
    static final String RESTART = "R - 重新开始";
    static final String GAME_OVER_TEXT = "游戏结束!";
    static final String RESTART_HINT = "按R重新开始";

    // 状态消息
    static final String STATUS_PAUSED = "游戏已暂停";
    static final String STATUS_RESUMED = "游戏继续";
    static final String STATUS_RESTARTED = "游戏重新开始";

    // 帮助信息
    static final String HELP_MESSAGE = """
        俄罗斯方块游戏使用说明：

        • 游戏目标：通过旋转和移动方块，填满一行来消除并获得分数
        • 游戏规则：方块堆叠到顶部时游戏结束
        • 计分规则：消除1行得100分，2行得300分，3行得500分，4行得800分
        • 速度提升：每消除10行，游戏速度会加快

        操作说明：
        • A/D 或 ←/→：左右移动方块
        • S 或 ↓：快速下落
        • W 或 ↑：旋转方块
        • 空格键：暂停/继续游戏
        • R键：重新开始游戏

        游戏技巧：
        • 尽量保持游戏板底部平整
        • 预留空间用于放置长条方块
        • 优先消除靠近顶部的行
        • 合理使用旋转来适应空隙
        • 快速下落可以节省时间

        快捷键：
        A/D/←/→ - 左右移动
        S/↓ - 快速下落
        W/↑ - 旋转方块
        空格 - 暂停/继续
        R - 重新开始
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Tetris().setVisible(true);
    });
}

static class Tetris extends JFrame implements ActionListener, KeyListener {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int CELL_SIZE = 30;
    private static final int DELAY = 500;
    
    private Timer timer;
    private int[][] board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int currentX, currentY;
    private int score;
    private int lines;
    private boolean gameOver;
    private Random random;
    
    // 俄罗斯方块类
    class Tetromino {
        private int[][] shape;
        private Color color;
        private int x, y;
        
        public Tetromino(int[][] shape, Color color) {
            this.shape = shape;
            this.color = color;
            this.x = 0;
            this.y = 0;
        }
        
        public int[][] getShape() { return shape; }
        public Color getColor() { return color; }
        public int getX() { return x; }
        public int getY() { return y; }
        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
        
        // 旋转方块
        public Tetromino rotate() {
            int rows = shape.length;
            int cols = shape[0].length;
            int[][] rotated = new int[cols][rows];
            
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    rotated[j][rows - 1 - i] = shape[i][j];
                }
            }
            
            return new Tetromino(rotated, color);
        }
    }
    
    // 方块形状定义
    private static final int[][][] SHAPES = {
        // I形
        {{1, 1, 1, 1}},
        // O形
        {{1, 1}, {1, 1}},
        // T形
        {{0, 1, 0}, {1, 1, 1}},
        // S形
        {{0, 1, 1}, {1, 1, 0}},
        // Z形
        {{1, 1, 0}, {0, 1, 1}},
        // J形
        {{1, 0, 0}, {1, 1, 1}},
        // L形
        {{0, 0, 1}, {1, 1, 1}}
    };
    
    private static final Color[] COLORS = {
        Color.CYAN,    // I
        Color.YELLOW,  // O
        Color.MAGENTA, // T
        Color.GREEN,   // S
        Color.RED,     // Z
        Color.BLUE,    // J
        Color.ORANGE   // L
    };
    
    public Tetris() {
        initGame();
        initUI();
        setupKeyboardShortcuts();
    }
    
    private void initGame() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        random = new Random();
        score = 0;
        lines = 0;
        gameOver = false;
        
        // 生成第一个方块
        spawnNewPiece();
        nextPiece = generateRandomPiece();
        
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    private void initUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // 创建游戏面板
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        
        gamePanel.setPreferredSize(new Dimension(
            BOARD_WIDTH * CELL_SIZE + 200, 
            BOARD_HEIGHT * CELL_SIZE + 100
        ));
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);
        
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        
        gamePanel.requestFocusInWindow();
    }
    
    private void drawGame(Graphics g) {
        // 绘制游戏板
        drawBoard(g);
        
        // 绘制当前方块
        if (currentPiece != null && !gameOver) {
            drawPiece(g, currentPiece, currentX, currentY);
        }
        
        // 绘制信息面板
        drawInfoPanel(g);
        
        // 绘制游戏结束信息
        if (gameOver) {
            drawGameOver(g);
        }
    }
    
    private void drawBoard(Graphics g) {
        // 绘制边框
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);
        
        // 绘制已放置的方块
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != 0) {
                    g.setColor(COLORS[board[row][col] - 1]);
                    g.fillRect(col * CELL_SIZE, row * CELL_SIZE, 
                              CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.WHITE);
                    g.drawRect(col * CELL_SIZE, row * CELL_SIZE, 
                              CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
    
    private void drawPiece(Graphics g, Tetromino piece, int x, int y) {
        int[][] shape = piece.getShape();
        g.setColor(piece.getColor());
        
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int drawX = (x + col) * CELL_SIZE;
                    int drawY = (y + row) * CELL_SIZE;
                    g.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.WHITE);
                    g.drawRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    g.setColor(piece.getColor());
                }
            }
        }
    }
    
    private void drawInfoPanel(Graphics g) {
        int panelX = BOARD_WIDTH * CELL_SIZE + 20;

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(Texts.SCORE_LABEL + score, panelX, 30);
        g.drawString(Texts.LINES_LABEL + lines, panelX, 60);

        // 绘制下一个方块
        g.drawString(Texts.NEXT_LABEL, panelX, 100);
        if (nextPiece != null) {
            drawPiece(g, nextPiece, (panelX / CELL_SIZE), 4);
        }

        // 绘制控制说明
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString(Texts.CONTROLS_LABEL, panelX, 200);
        g.drawString(Texts.MOVE_LEFT_RIGHT, panelX, 220);
        g.drawString(Texts.FAST_DROP, panelX, 240);
        g.drawString(Texts.ROTATE, panelX, 260);
        g.drawString(Texts.PAUSE, panelX, 280);
        g.drawString(Texts.RESTART, panelX, 300);
    }
    
    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String gameOverText = Texts.GAME_OVER_TEXT;
        String restartText = Texts.RESTART_HINT;

        int x1 = (BOARD_WIDTH * CELL_SIZE - fm.stringWidth(gameOverText)) / 2;
        int x2 = (BOARD_WIDTH * CELL_SIZE - fm.stringWidth(restartText)) / 2;

        g.drawString(gameOverText, x1, BOARD_HEIGHT * CELL_SIZE / 2 - 20);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(restartText, x2, BOARD_HEIGHT * CELL_SIZE / 2 + 20);
    }
    
    private Tetromino generateRandomPiece() {
        int index = random.nextInt(SHAPES.length);
        return new Tetromino(SHAPES[index], COLORS[index]);
    }
    
    private void spawnNewPiece() {
        if (nextPiece != null) {
            currentPiece = nextPiece;
        } else {
            currentPiece = generateRandomPiece();
        }
        nextPiece = generateRandomPiece();
        
        currentX = BOARD_WIDTH / 2 - currentPiece.getShape()[0].length / 2;
        currentY = 0;
        
        // 检查游戏是否结束
        if (!isValidPosition(currentPiece, currentX, currentY)) {
            gameOver = true;
            timer.stop();
        }
    }
    
    private boolean isValidPosition(Tetromino piece, int x, int y) {
        int[][] shape = piece.getShape();
        
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;
                    
                    // 检查边界
                    if (newX < 0 || newX >= BOARD_WIDTH || 
                        newY >= BOARD_HEIGHT) {
                        return false;
                    }
                    
                    // 检查是否与已放置的方块重叠
                    if (newY >= 0 && board[newY][newX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private void placePiece() {
        int[][] shape = currentPiece.getShape();
        
        // 将当前方块放置到游戏板上
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int boardX = currentX + col;
                    int boardY = currentY + row;
                    
                    if (boardY >= 0) {
                        // 找到对应的颜色索引
                        for (int i = 0; i < COLORS.length; i++) {
                            if (COLORS[i].equals(currentPiece.getColor())) {
                                board[boardY][boardX] = i + 1;
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        // 检查并清除完整的行
        clearLines();
        
        // 生成新方块
        spawnNewPiece();
    }
    
    private void clearLines() {
        ArrayList<Integer> linesToClear = new ArrayList<>();
        
        // 找到需要清除的行
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            boolean fullLine = true;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == 0) {
                    fullLine = false;
                    break;
                }
            }
            if (fullLine) {
                linesToClear.add(row);
            }
        }
        
        // 清除行并更新分数
        for (int i = linesToClear.size() - 1; i >= 0; i--) {
            int lineIndex = linesToClear.get(i);
            
            // 将上面的行向下移动
            for (int row = lineIndex; row > 0; row--) {
                System.arraycopy(board[row - 1], 0, board[row], 0, BOARD_WIDTH);
            }
            
            // 清空顶行
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[0][col] = 0;
            }
        }
        
        // 更新分数和行数
        int clearedLines = linesToClear.size();
        lines += clearedLines;
        
        // 计分规则
        switch (clearedLines) {
            case 1: score += 100; break;
            case 2: score += 300; break;
            case 3: score += 500; break;
            case 4: score += 800; break;
        }
        
        // 随着行数增加，加快游戏速度
        if (lines > 0 && lines % 10 == 0) {
            int newDelay = Math.max(100, DELAY - (lines / 10) * 50);
            timer.setDelay(newDelay);
        }
    }
    
    private void moveLeft() {
        if (isValidPosition(currentPiece, currentX - 1, currentY)) {
            currentX--;
        }
    }
    
    private void moveRight() {
        if (isValidPosition(currentPiece, currentX + 1, currentY)) {
            currentX++;
        }
    }
    
    private void moveDown() {
        if (isValidPosition(currentPiece, currentX, currentY + 1)) {
            currentY++;
        } else {
            placePiece();
        }
    }
    
    private void rotatePiece() {
        Tetromino rotated = currentPiece.rotate();
        if (isValidPosition(rotated, currentX, currentY)) {
            currentPiece = rotated;
        }
    }
    
    private void dropPiece() {
        while (isValidPosition(currentPiece, currentX, currentY + 1)) {
            currentY++;
        }
        placePiece();
    }
    
    private void restartGame() {
        timer.stop();
        
        // 重置游戏状态
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[row][col] = 0;
            }
        }
        
        score = 0;
        lines = 0;
        gameOver = false;
        
        spawnNewPiece();
        nextPiece = generateRandomPiece();
        
        timer.setDelay(DELAY);
        timer.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            moveDown();
            repaint();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_R) {
                restartGame();
                repaint();
            }
            return;
        }
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                moveLeft();
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                moveRight();
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                moveDown();
                break;
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                rotatePiece();
                break;
            case KeyEvent.VK_SPACE:
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    timer.start();
                }
                break;
            case KeyEvent.VK_R:
                restartGame();
                break;
        }
        repaint();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持（除了游戏控制外的功能键）
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

}

