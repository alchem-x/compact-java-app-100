import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🌀 迷宫游戏";

    // 界面标签
    static final String STATUS_LABEL = "状态: ";
    static final String TIME_LABEL = "时间: ";
    static final String MOVES_LABEL = "步数: ";
    static final String GENERATE_BUTTON = "生成迷宫";
    static final String SOLVE_BUTTON = "显示解答";
    static final String DIFFICULTY_LABEL = "难度: ";

    // 难度选项
    static final String DIFFICULTY_EASY = "简单";
    static final String DIFFICULTY_MEDIUM = "中等";
    static final String DIFFICULTY_HARD = "困难";

    // 状态消息
    static final String STATUS_GAME_START = "使用方向键移动，找到出口";
    static final String STATUS_GAME_WON = "恭喜！你成功走出迷宫！";
    static final String STATUS_MOVING = "移动中...";
    static final String STATUS_WALL_HIT = "撞墙了！请换条路";
    static final String STATUS_SHOWING_SOLUTION = "显示解答中...";
    static final String STATUS_SOLUTION_SHOWN = "解答已显示";
    static final String STATUS_NO_SOLUTION = "无法找到解答";
    static final String STATUS_MAZE_GENERATED = "迷宫已生成，开始挑战吧！";

    // 帮助信息
    static final String HELP_MESSAGE = """
        迷宫游戏使用说明：

        • 游戏目标：从左上角走到右下角，找到迷宫出口
        • 游戏规则：使用方向键控制角色移动，避开墙壁
        • 计分规则：移动步数越少，用时越短越好

        操作说明：
        • ↑ 或 W：向上移动
        • ↓ 或 S：向下移动
        • ← 或 A：向左移动
        • → 或 D：向右移动
        • 空格键：重新开始游戏

        游戏技巧：
        • 优先探索边缘路径
        • 记住已经走过的路线
        • 遇到死胡同时及时返回
        • 可以请求显示解答来学习

        难度说明：
        • 简单：较小的迷宫，更容易找到路径
        • 中等：中等大小的迷宫，需要一定思考
        • 困难：较大的迷宫，需要良好的空间记忆

        快捷键：
        方向键/WASD - 移动控制
        空格键 - 重新开始
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new MazeGame().setVisible(true);
    });
}

static class MazeGame extends JFrame implements KeyListener {
    private static final int CELL_SIZE = 20;
    private static final int MAZE_WIDTH = 31; // 奇数
    private static final int MAZE_HEIGHT = 21; // 奇数

    private JPanel mazePanel;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private JLabel movesLabel;
    private JButton generateButton;
    private JButton solveButton;
    private JComboBox<String> difficultyCombo;

    private int[][] maze;
    private int playerX = 1;
    private int playerY = 1;
    private int endX = MAZE_WIDTH - 2;
    private int endY = MAZE_HEIGHT - 2;
    private boolean gameWon = false;
    private int moves = 0;
    private int timeElapsed = 0;
    private Timer gameTimer;
    private boolean showSolution = false;
    private List<Point> solutionPath;

    public MazeGame() {
        initializeGUI();
        generateMaze();
        setupKeyboardShortcuts();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(new Color(245, 247, 250));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        generateButton = createButton(Texts.GENERATE_BUTTON, new Color(39, 174, 96), e -> generateMaze());
        solveButton = createButton(Texts.SOLVE_BUTTON, new Color(52, 152, 219), e -> toggleSolution());

        String[] difficulties = {Texts.DIFFICULTY_EASY, Texts.DIFFICULTY_MEDIUM, Texts.DIFFICULTY_HARD};
        difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.addActionListener(e -> generateMaze());

        controlPanel.add(new JLabel(Texts.DIFFICULTY_LABEL));
        controlPanel.add(difficultyCombo);
        controlPanel.add(generateButton);
        controlPanel.add(solveButton);

        // 状态面板
        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        statusPanel.setBackground(new Color(245, 247, 250));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel(Texts.STATUS_GAME_START, SwingConstants.CENTER);
        statusLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        statusLabel.setForeground(new Color(52, 73, 94));

        timeLabel = new JLabel(Texts.TIME_LABEL + "00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(127, 140, 141));

        movesLabel = new JLabel(Texts.MOVES_LABEL + "0", SwingConstants.CENTER);
        movesLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        movesLabel.setForeground(new Color(127, 140, 141));

        statusPanel.add(statusLabel);
        statusPanel.add(timeLabel);
        statusPanel.add(movesLabel);

        // 迷宫面板
        mazePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMaze(g);
            }
        };
        mazePanel.setPreferredSize(new Dimension(MAZE_WIDTH * CELL_SIZE, MAZE_HEIGHT * CELL_SIZE));
        mazePanel.setBackground(Color.WHITE);
        mazePanel.setFocusable(true);
        mazePanel.addKeyListener(this);

        // 说明面板
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(new Color(245, 247, 250));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel infoLabel = new JLabel("🟢 起点 | 🟡 终点 | 🔴 玩家 | 使用方向键移动 | ESC键重新开始");
        infoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(127, 140, 141));
        infoPanel.add(infoLabel);

        // 组装界面
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(controlPanel, BorderLayout.NORTH);
        topContainer.add(statusPanel, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(mazePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        setSize(800, 600);

        // 设置计时器
        gameTimer = new Timer(1000, e -> updateTimer());
    }

    private JButton createButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        return button;
    }

    private void generateMaze() {
        gameTimer.stop();
        timeElapsed = 0;
        moves = 0;
        gameWon = false;
        showSolution = false;
        solutionPath = null;

        // 初始化迷宫 - 全部为墙
        maze = new int[MAZE_HEIGHT][MAZE_WIDTH];
        for (int i = 0; i < MAZE_HEIGHT; i++) {
            for (int j = 0; j < MAZE_WIDTH; j++) {
                maze[i][j] = 1; // 1表示墙
            }
        }

        // 使用递归回溯算法生成迷宫
        generateMazeWithRecursiveBacktracking();

        // 设置起点和终点
        playerX = 1;
        playerY = 1;
        maze[playerY][playerX] = 0; // 确保起点是通路
        maze[endY][endX] = 0; // 确保终点是通路

        updateDisplay();
        mazePanel.repaint();

        statusLabel.setText(Texts.STATUS_MAZE_GENERATED);
        statusLabel.setForeground(new Color(52, 73, 94));

        // 开始计时
        gameTimer.start();

        // 请求焦点以便接收键盘输入
        mazePanel.requestFocusInWindow();
    }

    private void generateMazeWithRecursiveBacktracking() {
        Stack<Point> stack = new Stack<>();
        Random random = new Random();

        // 从起点开始
        Point current = new Point(1, 1);
        maze[1][1] = 0; // 0表示通路
        stack.push(current);

        while (!stack.isEmpty()) {
            List<Point> neighbors = getUnvisitedNeighbors(current);

            if (!neighbors.isEmpty()) {
                // 随机选择一个未访问的邻居
                Point next = neighbors.get(random.nextInt(neighbors.size()));

                // 打通当前位置和邻居之间的墙
                int wallX = (current.x + next.x) / 2;
                int wallY = (current.y + next.y) / 2;
                maze[wallY][wallX] = 0;
                maze[next.y][next.x] = 0;

                stack.push(next);
                current = next;
            } else {
                current = stack.pop();
            }
        }
    }

    private List<Point> getUnvisitedNeighbors(Point p) {
        List<Point> neighbors = new ArrayList<>();
        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};

        for (int[] dir : directions) {
            int newX = p.x + dir[0];
            int newY = p.y + dir[1];

            if (newX > 0 && newX < MAZE_WIDTH - 1 && newY > 0 && newY < MAZE_HEIGHT - 1) {
                if (maze[newY][newX] == 1) { // 如果是墙（未访问）
                    neighbors.add(new Point(newX, newY));
                }
            }
        }

        return neighbors;
    }

    private void drawMaze(Graphics g) {
        if (maze == null) return;

        // 绘制迷宫
        for (int i = 0; i < MAZE_HEIGHT; i++) {
            for (int j = 0; j < MAZE_WIDTH; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;

                if (maze[i][j] == 1) {
                    // 墙
                    g.setColor(new Color(52, 73, 94));
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                } else {
                    // 通路
                    g.setColor(Color.WHITE);
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }

                // 绘制网格线
                g.setColor(new Color(200, 200, 200));
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        // 绘制起点
        g.setColor(new Color(46, 204, 113));
        g.fillRect(playerX * CELL_SIZE + 2, playerY * CELL_SIZE + 2, CELL_SIZE - 4, CELL_SIZE - 4);

        // 绘制终点
        g.setColor(new Color(241, 196, 15));
        g.fillRect(endX * CELL_SIZE + 2, endY * CELL_SIZE + 2, CELL_SIZE - 4, CELL_SIZE - 4);

        // 绘制玩家
        g.setColor(new Color(231, 76, 60));
        g.fillOval(playerX * CELL_SIZE + 3, playerY * CELL_SIZE + 3, CELL_SIZE - 6, CELL_SIZE - 6);

        // 如果显示解答，绘制解答路径
        if (showSolution && solutionPath != null) {
            g.setColor(new Color(155, 89, 182));
            for (Point p : solutionPath) {
                if (p.x != playerX || p.y != playerY) {
                    g.fillRect(p.x * CELL_SIZE + 6, p.y * CELL_SIZE + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                }
            }
        }
    }

    private void toggleSolution() {
        if (solutionPath == null) {
            solutionPath = solveMaze();
        }
        showSolution = !showSolution;
        solveButton.setText(showSolution ? "隐藏解答" : "显示解答");
        mazePanel.repaint();
    }

    private List<Point> solveMaze() {
        // 使用深度优先搜索找到从玩家位置到终点的路径
        boolean[][] visited = new boolean[MAZE_HEIGHT][MAZE_WIDTH];
        List<Point> path = new ArrayList<>();

        if (dfs(playerX, playerY, visited, path)) {
            return path;
        }

        return new ArrayList<>();
    }

    private boolean dfs(int x, int y, boolean[][] visited, List<Point> path) {
        if (x == endX && y == endY) {
            path.add(new Point(x, y));
            return true;
        }

        if (x < 0 || x >= MAZE_WIDTH || y < 0 || y >= MAZE_HEIGHT || visited[y][x] || maze[y][x] == 1) {
            return false;
        }

        visited[y][x] = true;
        path.add(new Point(x, y));

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            if (dfs(x + dir[0], y + dir[1], visited, path)) {
                return true;
            }
        }

        path.remove(path.size() - 1);
        return false;
    }

    private void updateTimer() {
        timeElapsed++;
        int minutes = timeElapsed / 60;
        int seconds = timeElapsed % 60;
        timeLabel.setText(String.format("时间: %02d:%02d", minutes, seconds));
    }

    private void updateDisplay() {
        movesLabel.setText(String.format("步数: %d", moves));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameWon) return;

        int keyCode = e.getKeyCode();
        int newX = playerX;
        int newY = playerY;

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                newY--;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                newY++;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                newX--;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                newX++;
                break;
            case KeyEvent.VK_ESCAPE:
                generateMaze();
                return;
            default:
                return;
        }

        // 检查是否可以移动
        if (newX >= 0 && newX < MAZE_WIDTH && newY >= 0 && newY < MAZE_HEIGHT && maze[newY][newX] == 0) {
            playerX = newX;
            playerY = newY;
            moves++;

            updateDisplay();
            mazePanel.repaint();

            // 检查是否到达终点
            if (playerX == endX && playerY == endY) {
                gameWon = true;
                gameTimer.stop();

                statusLabel.setText(Texts.STATUS_GAME_WON);
                statusLabel.setForeground(new Color(39, 174, 96));

                JOptionPane.showMessageDialog(this,
                    String.format("恭喜完成！\n用时: %02d:%02d\n步数: %d",
                        timeElapsed / 60, timeElapsed % 60, moves),
                    "游戏完成", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private static class Point {
        int x, y;

        Point(int x, int y) {
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