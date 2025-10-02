import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🏯 华容道游戏";

    // 界面标签
    static final String STATUS_LABEL = "状态: ";
    static final String MOVES_LABEL = "移动次数: ";
    static final String TIME_LABEL = "时间: ";
    static final String RESET_BUTTON = "重新开始";
    static final String NEW_GAME_BUTTON = "新游戏";
    static final String HELP_BUTTON = "帮助";

    // 游戏状态
    static final String STATUS_START_GAME = "拖拽方块开始游戏";
    static final String STATUS_DRAG_PIECE = "拖拽方块移动";
    static final String STATUS_PIECE_MOVED = "方块移动成功";
    static final String STATUS_CANNOT_MOVE = "无法移动到该位置";
    static final String STATUS_GAME_COMPLETED = "🎉 恭喜！成功救出曹操！";
    static final String STATUS_NEW_GAME_STARTED = "新游戏开始";

    // 人物名称
    static final String PIECE_CAO_CAO = "曹操";
    static final String PIECE_GUAN_YU = "关羽";
    static final String PIECE_ZHANG_FEI = "张飞";
    static final String PIECE_ZHAO_YUN = "赵云";
    static final String PIECE_MA_CHAO = "马超";
    static final String PIECE_HUANG_ZHONG = "黄忠";
    static final String PIECE_SOLDIER = "兵";

    // 完成消息
    static final String COMPLETION_MESSAGE = "华容道完成！\n移动次数: %d\n用时: %02d:%02d";
    static final String COMPLETION_TITLE = "游戏完成";

    // 帮助信息
    static final String HELP_MESSAGE = """
        华容道游戏使用说明：

        • 游戏目标：通过移动滑块，将曹操（红色大块）移动到出口位置
        • 游戏规则：只能水平或垂直移动滑块，不能重叠
        • 计分规则：移动次数越少，用时越短越好

        操作说明：
        • 鼠标拖拽：选择并移动滑块
        • 只能移动到空白位置
        • 大滑块需要更多空间

        人物说明：
        • 曹操（红色，2x2）：目标人物，需要移动到出口
        • 关羽（绿色，2x1）：横向长条
        • 张飞、赵云、马超、黄忠（各1x2）：纵向长条
        • 兵（蓝色，1x1）：小方块

        游戏技巧：
        • 优先为曹操创造移动空间
        • 注意大滑块的移动路径
        • 合理规划移动顺序
        • 避免将小滑块堵死

        历史背景：
        华容道是中国传统益智游戏，源自三国时期曹操败走华容道的典故。
        游戏的目标是通过移动各个滑块，为曹操让出一条逃生的道路。

        快捷键：
        Ctrl+N - 新游戏
        Ctrl+R - 重新开始
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

/**
 * 华容道游戏
 * 经典的滑块拼图游戏，目标是将曹操（红色大块）移动到出口
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new KlotskiPuzzle().setVisible(true);
    });
}

class KlotskiPuzzle extends JFrame {
    private static final int BOARD_WIDTH = 4;
    private static final int BOARD_HEIGHT = 5;
    private static final int CELL_SIZE = 80;
    private static final int BOARD_PIXEL_WIDTH = BOARD_WIDTH * CELL_SIZE;
    private static final int BOARD_PIXEL_HEIGHT = BOARD_HEIGHT * CELL_SIZE;
    
    private GameBoard gameBoard;
    private List<GamePiece> pieces;
    private GamePiece selectedPiece;
    private Point dragStart;
    private int moveCount;
    private long startTime;
    private boolean gameWon;
    
    private JLabel statusLabel;
    private javax.swing.Timer timer;
    private int elapsedSeconds;
    
    public KlotskiPuzzle() {
        initializeUI();
        initializeGame();
        setupKeyboardShortcuts();
        startTimer();
    }
    
    private void initializeUI() {
        setTitle(Texts.WINDOW_TITLE);
        setSize(BOARD_PIXEL_WIDTH + 200, BOARD_PIXEL_HEIGHT + 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        
        // 游戏面板
        gameBoard = new GameBoard();
        gameBoard.setPreferredSize(new Dimension(BOARD_PIXEL_WIDTH, BOARD_PIXEL_HEIGHT));
        gameBoard.setBackground(new Color(139, 69, 19)); // 棕色背景
        
        // 添加鼠标监听器
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        };
        
        gameBoard.addMouseListener(mouseHandler);
        gameBoard.addMouseMotionListener(mouseHandler);
        
        mainPanel.add(gameBoard, BorderLayout.CENTER);
        
        // 右侧信息面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        infoPanel.setPreferredSize(new Dimension(180, BOARD_PIXEL_HEIGHT));
        
        // 游戏信息
        JLabel titleLabel = new JLabel("华容道");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel();
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateStatusLabel();
        
        // 游戏说明
        JTextArea instructionArea = new JTextArea();
        instructionArea.setText("游戏目标：\n将曹操（红色大块）\n移动到底部出口\n\n操作方法：\n拖拽方块移动\n\n人物介绍：\n红色-曹操\n蓝色-关羽\n绿色-张飞、赵云\n黄色-马超、黄忠\n灰色-兵卒");
        instructionArea.setEditable(false);
        instructionArea.setOpaque(false);
        instructionArea.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
        instructionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 控制按钮
        JButton resetBtn = new JButton(Texts.RESET_BUTTON);
        resetBtn.addActionListener(e -> resetGame());
        resetBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton hintBtn = new JButton(Texts.HELP_BUTTON);
        hintBtn.addActionListener(e -> showHint());
        hintBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton layoutBtn = new JButton("切换布局");
        layoutBtn.addActionListener(e -> switchLayout());
        layoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(instructionArea);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(resetBtn);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(hintBtn);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(layoutBtn);
        
        mainPanel.add(infoPanel, BorderLayout.EAST);
    }
    
    private void initializeGame() {
        pieces = new ArrayList<>();
        moveCount = 0;
        startTime = System.currentTimeMillis();
        elapsedSeconds = 0;
        gameWon = false;
        
        // 创建经典华容道布局
        createClassicLayout();
    }
    
    private void createClassicLayout() {
        pieces.clear();
        
        // 曹操 (2x2, 红色)
        pieces.add(new GamePiece("曹操", 1, 0, 2, 2, Color.RED, PieceType.CAOCAO));
        
        // 关羽 (2x1, 蓝色)
        pieces.add(new GamePiece("关羽", 1, 2, 2, 1, Color.BLUE, PieceType.GUANYU));
        
        // 张飞 (1x2, 绿色)
        pieces.add(new GamePiece("张飞", 0, 0, 1, 2, Color.GREEN, PieceType.ZHANGFEI));
        
        // 赵云 (1x2, 绿色)
        pieces.add(new GamePiece("赵云", 3, 0, 1, 2, Color.GREEN, PieceType.ZHAOYUN));
        
        // 马超 (1x2, 黄色)
        pieces.add(new GamePiece("马超", 0, 2, 1, 2, Color.ORANGE, PieceType.MACHAO));
        
        // 黄忠 (1x2, 黄色)
        pieces.add(new GamePiece("黄忠", 3, 2, 1, 2, Color.ORANGE, PieceType.HUANGZHONG));
        
        // 兵卒1 (1x1, 灰色)
        pieces.add(new GamePiece("兵1", 1, 3, 1, 1, Color.LIGHT_GRAY, PieceType.SOLDIER));
        
        // 兵卒2 (1x1, 灰色)
        pieces.add(new GamePiece("兵2", 2, 3, 1, 1, Color.LIGHT_GRAY, PieceType.SOLDIER));
        
        // 兵卒3 (1x1, 灰色)
        pieces.add(new GamePiece("兵3", 0, 4, 1, 1, Color.LIGHT_GRAY, PieceType.SOLDIER));
        
        // 兵卒4 (1x1, 灰色)
        pieces.add(new GamePiece("兵4", 3, 4, 1, 1, Color.LIGHT_GRAY, PieceType.SOLDIER));
    }
    
    private void createAlternativeLayout() {
        pieces.clear();
        
        // 另一种经典布局
        pieces.add(new GamePiece("曹操", 0, 1, 2, 2, Color.RED, PieceType.CAOCAO));
        pieces.add(new GamePiece("关羽", 2, 1, 2, 1, Color.BLUE, PieceType.GUANYU));
        pieces.add(new GamePiece("张飞", 0, 0, 1, 1, Color.GREEN, PieceType.ZHANGFEI));
        pieces.add(new GamePiece("赵云", 1, 0, 1, 1, Color.GREEN, PieceType.ZHAOYUN));
        pieces.add(new GamePiece("马超", 2, 0, 1, 1, Color.ORANGE, PieceType.MACHAO));
        pieces.add(new GamePiece("黄忠", 3, 0, 1, 1, Color.ORANGE, PieceType.HUANGZHONG));
        pieces.add(new GamePiece("兵1", 0, 3, 1, 2, Color.LIGHT_GRAY, PieceType.SOLDIER));
        pieces.add(new GamePiece("兵2", 3, 2, 1, 2, Color.LIGHT_GRAY, PieceType.SOLDIER));
        pieces.add(new GamePiece("兵3", 1, 4, 1, 1, Color.LIGHT_GRAY, PieceType.SOLDIER));
        pieces.add(new GamePiece("兵4", 2, 4, 1, 1, Color.LIGHT_GRAY, PieceType.SOLDIER));
    }
    
    private void startTimer() {
        timer = new javax.swing.Timer(1000, e -> {
            if (!gameWon) {
                elapsedSeconds++;
                updateStatusLabel();
            }
        });
        timer.start();
    }
    
    private void handleMousePressed(MouseEvent e) {
        if (gameWon) return;
        
        int gridX = e.getX() / CELL_SIZE;
        int gridY = e.getY() / CELL_SIZE;
        
        selectedPiece = findPieceAt(gridX, gridY);
        if (selectedPiece != null) {
            dragStart = new Point(e.getX(), e.getY());
        }
    }
    
    private void handleMouseDragged(MouseEvent e) {
        if (selectedPiece != null && dragStart != null) {
            gameBoard.repaint();
        }
    }
    
    private void handleMouseReleased(MouseEvent e) {
        if (selectedPiece != null && dragStart != null) {
            int deltaX = e.getX() - dragStart.x;
            int deltaY = e.getY() - dragStart.y;
            
            // 确定移动方向
            Direction moveDirection = null;
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (Math.abs(deltaX) > CELL_SIZE / 3) {
                    moveDirection = deltaX > 0 ? Direction.RIGHT : Direction.LEFT;
                }
            } else {
                if (Math.abs(deltaY) > CELL_SIZE / 3) {
                    moveDirection = deltaY > 0 ? Direction.DOWN : Direction.UP;
                }
            }
            
            if (moveDirection != null && canMovePiece(selectedPiece, moveDirection)) {
                movePiece(selectedPiece, moveDirection);
                moveCount++;
                checkWinCondition();
                updateStatusLabel();
            }
        }
        
        selectedPiece = null;
        dragStart = null;
        gameBoard.repaint();
    }
    
    private GamePiece findPieceAt(int x, int y) {
        for (GamePiece piece : pieces) {
            if (x >= piece.x && x < piece.x + piece.width &&
                y >= piece.y && y < piece.y + piece.height) {
                return piece;
            }
        }
        return null;
    }
    
    private boolean canMovePiece(GamePiece piece, Direction direction) {
        int newX = piece.x + direction.dx;
        int newY = piece.y + direction.dy;
        
        // 检查边界
        if (newX < 0 || newY < 0 || 
            newX + piece.width > BOARD_WIDTH || 
            newY + piece.height > BOARD_HEIGHT) {
            return false;
        }
        
        // 检查与其他方块的碰撞
        for (GamePiece other : pieces) {
            if (other == piece) continue;
            
            if (isOverlapping(newX, newY, piece.width, piece.height,
                            other.x, other.y, other.width, other.height)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isOverlapping(int x1, int y1, int w1, int h1,
                                int x2, int y2, int w2, int h2) {
        return !(x1 + w1 <= x2 || x2 + w2 <= x1 || 
                y1 + h1 <= y2 || y2 + h2 <= y1);
    }
    
    private void movePiece(GamePiece piece, Direction direction) {
        piece.x += direction.dx;
        piece.y += direction.dy;
    }
    
    private void checkWinCondition() {
        // 找到曹操
        GamePiece caocao = pieces.stream()
            .filter(p -> p.type == PieceType.CAOCAO)
            .findFirst()
            .orElse(null);
        
        if (caocao != null && caocao.x == 1 && caocao.y == 3) {
            gameWon = true;
            timer.stop();
            
            JOptionPane.showMessageDialog(this,
                "恭喜！华容道通关成功！\n" +
                "移动步数: " + moveCount + "\n" +
                "用时: " + formatTime(elapsedSeconds),
                "游戏胜利", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetGame() {
        timer.stop();
        initializeGame();
        startTimer();
        gameBoard.repaint();
    }
    
    private void switchLayout() {
        timer.stop();
        if (pieces.get(0).x == 1) { // 当前是经典布局
            createAlternativeLayout();
        } else {
            createClassicLayout();
        }
        moveCount = 0;
        elapsedSeconds = 0;
        gameWon = false;
        startTimer();
        updateStatusLabel();
        gameBoard.repaint();
    }
    
    private void showHint() {
        String hint = "提示：\n";
        
        if (moveCount < 10) {
            hint += "1. 先移动小兵，为大块腾出空间\n";
            hint += "2. 利用空位进行方块的调换\n";
        } else if (moveCount < 30) {
            hint += "1. 尝试将关羽移到右侧\n";
            hint += "2. 为曹操向下移动创造条件\n";
        } else {
            hint += "1. 曹操需要移动到底部中央\n";
            hint += "2. 可能需要多次重新排列方块\n";
        }
        
        hint += "\n目标：将曹操移动到坐标(1,3)位置";
        
        JOptionPane.showMessageDialog(this, hint, "游戏提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatusLabel() {
        String status = String.format(
            "<html><center>%s%d<br>%s%s<br>%s</center></html>",
            Texts.MOVES_LABEL,
            moveCount,
            Texts.TIME_LABEL,
            formatTime(elapsedSeconds),
            gameWon ? "已通关!" : "游戏中"
        );
        statusLabel.setText(status);
    }
    
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
    
    // 游戏面板类
    class GameBoard extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制网格
            drawGrid(g2d);
            
            // 绘制出口
            drawExit(g2d);
            
            // 绘制方块
            for (GamePiece piece : pieces) {
                drawPiece(g2d, piece);
            }
            
            // 绘制选中效果
            if (selectedPiece != null) {
                drawSelection(g2d, selectedPiece);
            }
        }
        
        private void drawGrid(Graphics2D g2d) {
            g2d.setColor(new Color(101, 67, 33));
            g2d.setStroke(new BasicStroke(2));
            
            for (int x = 0; x <= BOARD_WIDTH; x++) {
                g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, BOARD_PIXEL_HEIGHT);
            }
            for (int y = 0; y <= BOARD_HEIGHT; y++) {
                g2d.drawLine(0, y * CELL_SIZE, BOARD_PIXEL_WIDTH, y * CELL_SIZE);
            }
        }
        
        private void drawExit(Graphics2D g2d) {
            // 绘制出口（底部中央）
            g2d.setColor(new Color(255, 215, 0, 100)); // 半透明金色
            g2d.fillRect(CELL_SIZE, (BOARD_HEIGHT - 1) * CELL_SIZE, 
                        2 * CELL_SIZE, CELL_SIZE);
            
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(CELL_SIZE, (BOARD_HEIGHT - 1) * CELL_SIZE, 
                        2 * CELL_SIZE, CELL_SIZE);
            
            // 绘制"出口"文字
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            String exitText = "出口";
            int textX = CELL_SIZE + (2 * CELL_SIZE - fm.stringWidth(exitText)) / 2;
            int textY = (BOARD_HEIGHT - 1) * CELL_SIZE + (CELL_SIZE + fm.getAscent()) / 2;
            g2d.drawString(exitText, textX, textY);
        }
        
        private void drawPiece(Graphics2D g2d, GamePiece piece) {
            int x = piece.x * CELL_SIZE + 2;
            int y = piece.y * CELL_SIZE + 2;
            int width = piece.width * CELL_SIZE - 4;
            int height = piece.height * CELL_SIZE - 4;
            
            // 绘制方块主体
            g2d.setColor(piece.color);
            g2d.fillRoundRect(x, y, width, height, 10, 10);
            
            // 绘制边框
            g2d.setColor(piece.color.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, width, height, 10, 10);
            
            // 绘制名字
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 
                piece.width == 2 && piece.height == 2 ? 16 : 12));
            FontMetrics fm = g2d.getFontMetrics();
            
            int textX = x + (width - fm.stringWidth(piece.name)) / 2;
            int textY = y + (height + fm.getAscent()) / 2;
            g2d.drawString(piece.name, textX, textY);
        }
        
        private void drawSelection(Graphics2D g2d, GamePiece piece) {
            int x = piece.x * CELL_SIZE;
            int y = piece.y * CELL_SIZE;
            int width = piece.width * CELL_SIZE;
            int height = piece.height * CELL_SIZE;
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                        0, new float[]{10, 5}, 0));
            g2d.drawRect(x, y, width, height);
        }
    }
    
    // 方向枚举
    enum Direction {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);
        
        final int dx, dy;
        
        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
    
    // 方块类型枚举
    enum PieceType {
        CAOCAO, GUANYU, ZHANGFEI, ZHAOYUN, MACHAO, HUANGZHONG, SOLDIER
    }
    
    // 游戏方块类
    static class GamePiece {
        String name;
        int x, y;
        int width, height;
        Color color;
        PieceType type;
        
        public GamePiece(String name, int x, int y, int width, int height,
                        Color color, PieceType type) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.type = type;
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
                            resetGame();
                        }
                        break;
                    case KeyEvent.VK_R:
                        // Ctrl+R 重新开始
                        if (ev.isControlDown()) {
                            resetGame();
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
