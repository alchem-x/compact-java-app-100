import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Sokoban().setVisible(true);
    });
}

static class Sokoban extends JFrame {
    private static final int CELL_SIZE = 40;
    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 12;
    private static final int MARGIN = 50;

    // 游戏元素
    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int BOX = 2;
    private static final int TARGET = 3;
    private static final int PLAYER = 4;
    private static final int BOX_ON_TARGET = 5;
    private static final int PLAYER_ON_TARGET = 6;

    private int[][] level;
    private int[][] originalLevel;
    private int playerRow, playerCol;
    private int moveCount;
    private boolean gameWon;
    private Stack<GameState> moveHistory;

    private GamePanel gamePanel;
    private JLabel statusLabel;
    private JLabel moveCountLabel;
    private JLabel levelLabel;
    private JButton newGameButton;
    private JButton undoButton;
    private JButton resetButton;
    private JComboBox<String> levelCombo;

    // 关卡数据
    private static final String[] LEVEL_NAMES = {
        "关卡 1 - 简单入门", "关卡 2 - 基础练习", "关卡 3 - 推箱子",
        "关卡 4 - 目标定位", "关卡 5 - 复杂路径"
    };

    private static final int[][][] LEVELS = {
        // 关卡 1 - 简单入门
        {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        },
        // 关卡 2 - 基础练习
        {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,1,1,1,0,0,0,1,1,1,0,0,1},
            {1,0,0,1,0,1,0,0,0,1,0,1,0,0,1},
            {1,0,0,1,0,1,0,0,0,1,0,1,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        },
        // 关卡 3 - 推箱子
        {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,1,1,1,1,1,1,1,1,1,0,0,1},
            {1,0,0,1,0,0,0,0,0,0,0,1,0,0,1},
            {1,0,0,1,0,0,0,0,0,0,0,1,0,0,1},
            {1,0,0,1,0,0,0,0,0,0,0,1,0,0,1},
            {1,0,0,1,1,1,1,1,1,1,1,1,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        },
        // 关卡 4 - 目标定位
        {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        },
        // 关卡 5 - 复杂路径
        {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        }
    };

    // 在每个关卡中添加具体的游戏元素
    private static final int[][][] LEVEL_ELEMENTS = {
        // 关卡 1
        {
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,4,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        },
        // 关卡 2 - 添加墙壁、箱子、目标等
        {
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,2,0,0,0,0,0,2,0,0,0,0},
            {0,0,0,0,0,0,0,4,0,0,0,0,0,0,0},
            {0,0,0,0,2,0,0,0,0,0,2,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        }
    };

    public Sokoban() {
        level = new int[GRID_HEIGHT][GRID_WIDTH];
        originalLevel = new int[GRID_HEIGHT][GRID_WIDTH];
        moveCount = 0;
        gameWon = false;
        moveHistory = new Stack<>();

        initializeGUI();
        loadLevel(0);
    }

    private void initializeGUI() {
        setTitle("推箱子 (Sokoban)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("📦 推箱子");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        controlPanel.add(titleLabel);

        // 关卡选择
        controlPanel.add(new JLabel("关卡:"));
        levelCombo = new JComboBox<>(LEVEL_NAMES);
        levelCombo.addActionListener(e -> loadLevel(levelCombo.getSelectedIndex()));
        controlPanel.add(levelCombo);

        // 新游戏按钮
        newGameButton = new JButton("🔄 新游戏");
        newGameButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        newGameButton.setBackground(new Color(76, 175, 80));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.addActionListener(e -> resetLevel());
        controlPanel.add(newGameButton);

        // 重置按钮
        resetButton = new JButton("🔄 重置关卡");
        resetButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        resetButton.setBackground(new Color(33, 150, 243));
        resetButton.setForeground(Color.WHITE);
        resetButton.addActionListener(e -> resetLevel());
        controlPanel.add(resetButton);

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

        // 移动次数
        moveCountLabel = new JLabel("移动次数: 0");
        moveCountLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        moveCountLabel.setForeground(new Color(33, 150, 243));

        // 游戏状态
        statusLabel = new JLabel("游戏进行中...");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);

        statusPanel.add(moveCountLabel, BorderLayout.WEST);
        statusPanel.add(statusLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        // 设置键盘监听器
        addKeyListener(new GameKeyListener());
        setFocusable(true);

        // 设置窗口
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void loadLevel(int levelIndex) {
        if (levelIndex < 0 || levelIndex >= LEVELS.length) return;

        // 复制基础布局和元素
        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                level[i][j] = LEVELS[levelIndex][i][j];
                originalLevel[i][j] = LEVELS[levelIndex][i][j];
            }
        }

        // 添加元素
        if (levelIndex < LEVEL_ELEMENTS.length) {
            for (int i = 0; i < GRID_HEIGHT; i++) {
                for (int j = 0; j < GRID_WIDTH; j++) {
                    int element = LEVEL_ELEMENTS[levelIndex][i][j];
                    if (element != 0) {
                        level[i][j] = element;
                        originalLevel[i][j] = element;
                    }
                }
            }
        }

        // 找到玩家位置
        findPlayerPosition();

        moveCount = 0;
        gameWon = false;
        moveHistory.clear();
        updateStatus();
        gamePanel.repaint();
    }

    private void findPlayerPosition() {
        playerRow = -1;
        playerCol = -1;

        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                if (level[i][j] == PLAYER || level[i][j] == PLAYER_ON_TARGET) {
                    playerRow = i;
                    playerCol = j;
                    return;
                }
            }
        }
    }

    private void resetLevel() {
        // 恢复原始关卡
        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                level[i][j] = originalLevel[i][j];
            }
        }

        findPlayerPosition();
        moveCount = 0;
        gameWon = false;
        moveHistory.clear();
        updateStatus();
        gamePanel.repaint();
    }

    private void movePlayer(int deltaRow, int deltaCol) {
        if (gameWon) return;

        int newRow = playerRow + deltaRow;
        int newCol = playerCol + deltaCol;

        // 检查边界
        if (newRow < 0 || newRow >= GRID_HEIGHT || newCol < 0 || newCol >= GRID_WIDTH) {
            return;
        }

        int targetCell = level[newRow][newCol];

        // 保存当前状态到历史记录
        saveGameState();

        // 处理不同类型的移动
        if (targetCell == WALL) {
            // 撞墙，无法移动
            return;
        } else if (targetCell == EMPTY || targetCell == TARGET) {
            // 移动到空位或目标位置
            movePlayerTo(newRow, newCol, targetCell == TARGET);
        } else if (targetCell == BOX || targetCell == BOX_ON_TARGET) {
            // 尝试推箱子
            int boxNewRow = newRow + deltaRow;
            int boxNewCol = newCol + deltaCol;

            // 检查箱子能否移动
            if (boxNewRow < 0 || boxNewRow >= GRID_HEIGHT || boxNewCol < 0 || boxNewCol >= GRID_WIDTH) {
                return;
            }

            int boxTargetCell = level[boxNewRow][boxNewCol];
            if (boxTargetCell == WALL || boxTargetCell == BOX || boxTargetCell == BOX_ON_TARGET) {
                return; // 箱子无法移动
            }

            // 推箱子
            pushBox(newRow, newCol, boxNewRow, boxNewCol, boxTargetCell == TARGET);
            movePlayerTo(newRow, newCol, targetCell == BOX_ON_TARGET);
        }

        moveCount++;
        updateStatus();
        gamePanel.repaint();

        // 检查是否获胜
        if (checkWinCondition()) {
            gameWon = true;
            statusLabel.setText("🎉 恭喜通关！用了 " + moveCount + " 步");
            JOptionPane.showMessageDialog(this, "恭喜通关！\n用了 " + moveCount + " 步", "胜利",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void movePlayerTo(int newRow, int newCol, boolean onTarget) {
        // 清除原位置
        if (level[playerRow][playerCol] == PLAYER) {
            level[playerRow][playerCol] = EMPTY;
        } else if (level[playerRow][playerCol] == PLAYER_ON_TARGET) {
            level[playerRow][playerCol] = TARGET;
        }

        // 设置新位置
        level[newRow][newCol] = onTarget ? PLAYER_ON_TARGET : PLAYER;
        playerRow = newRow;
        playerCol = newCol;
    }

    private void pushBox(int boxRow, int boxCol, int newBoxRow, int newBoxCol, boolean boxOnTarget) {
        // 清除箱子原位置
        if (level[boxRow][boxCol] == BOX) {
            level[boxRow][boxCol] = EMPTY;
        } else if (level[boxRow][boxCol] == BOX_ON_TARGET) {
            level[boxRow][boxCol] = TARGET;
        }

        // 设置箱子新位置
        level[newBoxRow][newBoxCol] = boxOnTarget ? BOX_ON_TARGET : BOX;
    }

    private void saveGameState() {
        int[][] levelCopy = new int[GRID_HEIGHT][GRID_WIDTH];
        for (int i = 0; i < GRID_HEIGHT; i++) {
            System.arraycopy(level[i], 0, levelCopy[i], 0, GRID_WIDTH);
        }
        moveHistory.push(new GameState(levelCopy, playerRow, playerCol, moveCount));
    }

    private void undoMove() {
        if (moveHistory.isEmpty()) {
            statusLabel.setText("无法悔棋！");
            return;
        }

        GameState previousState = moveHistory.pop();

        // 恢复游戏状态
        for (int i = 0; i < GRID_HEIGHT; i++) {
            System.arraycopy(previousState.level[i], 0, level[i], 0, GRID_WIDTH);
        }

        playerRow = previousState.playerRow;
        playerCol = previousState.playerCol;
        moveCount = previousState.moveCount;
        gameWon = false;

        updateStatus();
        gamePanel.repaint();
    }

    private boolean checkWinCondition() {
        // 检查是否所有目标位置都有箱子
        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                if (originalLevel[i][j] == TARGET && level[i][j] != BOX_ON_TARGET) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateStatus() {
        moveCountLabel.setText("移动次数: " + moveCount);

        if (gameWon) {
            statusLabel.setText("🎉 恭喜通关！");
        } else {
            statusLabel.setText("游戏进行中...");
        }
    }

    // 游戏面板
    class GamePanel extends JPanel {
        public GamePanel() {
            setPreferredSize(new Dimension(GRID_WIDTH * CELL_SIZE + 2 * MARGIN, GRID_HEIGHT * CELL_SIZE + 2 * MARGIN));
            setBackground(new Color(0x2C3E50));
            setBorder(BorderFactory.createLineBorder(new Color(0x34495E), 2));

            addKeyListener(new GameKeyListener());
            setFocusable(true);
            setRequestFocusEnabled(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGame(g);
        }

        private void drawGame(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制游戏元素
            for (int i = 0; i < GRID_HEIGHT; i++) {
                for (int j = 0; j < GRID_WIDTH; j++) {
                    int x = MARGIN + j * CELL_SIZE;
                    int y = MARGIN + i * CELL_SIZE;
                    drawCell(g2d, level[i][j], x, y);
                }
            }
        }

        private void drawCell(Graphics2D g2d, int cellType, int x, int y) {
            switch (cellType) {
                case WALL:
                    drawWall(g2d, x, y);
                    break;
                case EMPTY:
                    drawEmpty(g2d, x, y);
                    break;
                case TARGET:
                    drawTarget(g2d, x, y);
                    break;
                case BOX:
                    drawBox(g2d, x, y, false);
                    break;
                case BOX_ON_TARGET:
                    drawBox(g2d, x, y, true);
                    break;
                case PLAYER:
                    drawPlayer(g2d, x, y, false);
                    break;
                case PLAYER_ON_TARGET:
                    drawPlayer(g2d, x, y, true);
                    break;
            }
        }

        private void drawWall(Graphics2D g2d, int x, int y) {
            g2d.setColor(new Color(0x8B4513)); // 棕色
            g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);

            // 添加纹理
            g2d.setColor(new Color(0xA0522D));
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if ((i + j) % 2 == 0) {
                        g2d.fillRect(x + i * CELL_SIZE/3, y + j * CELL_SIZE/3, CELL_SIZE/3, CELL_SIZE/3);
                    }
                }
            }
        }

        private void drawEmpty(Graphics2D g2d, int x, int y) {
            g2d.setColor(new Color(0xECF0F1)); // 浅灰色
            g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);

            g2d.setColor(new Color(0xBDC3C7));
            g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
        }

        private void drawTarget(Graphics2D g2d, int x, int y) {
            drawEmpty(g2d, x, y);

            // 绘制目标标记
            g2d.setColor(new Color(0xE74C3C)); // 红色
            g2d.setStroke(new BasicStroke(3));
            int centerX = x + CELL_SIZE/2;
            int centerY = y + CELL_SIZE/2;
            int radius = CELL_SIZE/4;
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }

        private void drawBox(Graphics2D g2d, int x, int y, boolean onTarget) {
            drawEmpty(g2d, x, y);

            // 绘制箱子
            g2d.setColor(onTarget ? new Color(0x27AE60) : new Color(0xF39C12)); // 绿色或橙色
            int boxSize = CELL_SIZE * 3/4;
            int boxX = x + (CELL_SIZE - boxSize) / 2;
            int boxY = y + (CELL_SIZE - boxSize) / 2;

            // 箱子阴影
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRect(boxX + 2, boxY + 2, boxSize, boxSize);

            // 箱子主体
            g2d.setColor(onTarget ? new Color(0x27AE60) : new Color(0xF39C12));
            g2d.fillRect(boxX, boxY, boxSize, boxSize);

            // 箱子边框
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(boxX, boxY, boxSize, boxSize);

            // 箱子标记
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String text = onTarget ? "✓" : "📦";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2d.drawString(text, boxX + (boxSize - textWidth) / 2, boxY + (boxSize + textHeight) / 2 - 2);
        }

        private void drawPlayer(Graphics2D g2d, int x, int y, boolean onTarget) {
            if (onTarget) {
                drawTarget(g2d, x, y);
            } else {
                drawEmpty(g2d, x, y);
            }

            // 绘制玩家
            g2d.setColor(new Color(0x3498DB)); // 蓝色
            int centerX = x + CELL_SIZE/2;
            int centerY = y + CELL_SIZE/2;
            int playerSize = CELL_SIZE * 2/3;

            // 玩家阴影
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(centerX - playerSize/2 + 2, centerY - playerSize/2 + 2, playerSize, playerSize);

            // 玩家主体
            g2d.setColor(new Color(0x3498DB));
            g2d.fillOval(centerX - playerSize/2, centerY - playerSize/2, playerSize, playerSize);

            // 玩家高光
            g2d.setColor(new Color(0x5DADE2));
            g2d.fillOval(centerX - playerSize/2 + 5, centerY - playerSize/2 + 5, playerSize/3, playerSize/3);

            // 玩家边框
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(centerX - playerSize/2, centerY - playerSize/2, playerSize, playerSize);

            // 玩家标记
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            String text = "👤";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2d.drawString(text, centerX - textWidth/2, centerY + textHeight/2 - 2);
        }
    }

    // 键盘监听器
    class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameWon) return;

            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    movePlayer(-1, 0);
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    movePlayer(1, 0);
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    movePlayer(0, -1);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    movePlayer(0, 1);
                    break;
                case KeyEvent.VK_R:
                    resetLevel();
                    break;
                case KeyEvent.VK_U:
                    undoMove();
                    break;
            }
        }
    }

    // 游戏状态类
    static class GameState {
        int[][] level;
        int playerRow, playerCol;
        int moveCount;

        public GameState(int[][] level, int playerRow, int playerCol, int moveCount) {
            this.level = new int[level.length][level[0].length];
            for (int i = 0; i < level.length; i++) {
                System.arraycopy(level[i], 0, this.level[i], 0, level[i].length);
            }
            this.playerRow = playerRow;
            this.playerCol = playerCol;
            this.moveCount = moveCount;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Sokoban().setVisible(true);
        });
    }
}