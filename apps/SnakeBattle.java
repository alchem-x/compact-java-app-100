import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🐍 贪吃蛇对战";

    // 界面标签
    static final String STATUS_LABEL = "状态: ";
    static final String SCORE_LABEL = "分数: ";
    static final String PLAYER1_LABEL = "玩家1: ";
    static final String PLAYER2_LABEL = "玩家2: ";
    static final String ROUNDS_LABEL = "回合: ";
    static final String START_BUTTON = "开始游戏";
    static final String PAUSE_BUTTON = "暂停";
    static final String RESTART_BUTTON = "重新开始";

    // 游戏模式
    static final String MODE_BATTLE = "对战模式";
    static final String MODE_COOP = "合作模式";
    static final String MODE_TIME_ATTACK = "限时模式";

    // 状态消息
    static final String STATUS_GAME_START = "按开始游戏按钮开始对战";
    static final String STATUS_GAME_RUNNING = "游戏进行中...";
    static final String STATUS_GAME_PAUSED = "游戏已暂停";
    static final String STATUS_PLAYER1_WINS = "🎉 玩家1获胜！";
    static final String STATUS_PLAYER2_WINS = "🎉 玩家2获胜！";
    static final String STATUS_DRAW = "平局！";
    static final String STATUS_COLLISION = "撞车了！";
    static final String STATUS_FOOD_EATEN = "吃到食物！";

    // 控制说明
    static final String CONTROL_PLAYER1 = "玩家1: WASD移动";
    static final String CONTROL_PLAYER2 = "玩家2: 方向键移动";

    // 帮助信息
    static final String HELP_MESSAGE = """
        贪吃蛇对战游戏使用说明：

        • 游戏目标：控制贪吃蛇吃到更多食物，击败对手
        • 游戏规则：撞墙或撞到对手身体会死亡
        • 计分规则：吃到食物获得分数，击败对手获得额外分数

        操作说明：
        • 玩家1: W(上) A(左) S(下) D(右)
        • 玩家2: ↑(上) ←(左) ↓(下) →(右)

        游戏模式：
        • 对战模式：两个玩家互相竞争
        • 合作模式：两个玩家合作对抗AI
        • 限时模式：在规定时间内获得最高分数

        游戏技巧：
        • 注意对手的移动轨迹
        • 利用地图边缘进行防守
        • 合理规划移动路径
        • 避免被逼入死角

        特殊道具：
        • 普通食物：增加长度和分数
        • 加速道具：暂时提高移动速度
        • 减速道具：暂时降低对手速度
        • 护盾道具：短时间内免疫碰撞

        快捷键：
        WASD/方向键 - 移动控制
        空格 - 开始/暂停游戏
        R - 重新开始
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

/**
 * 贪吃蛇对战游戏
 * 双人贪吃蛇对战，玩家1使用WASD控制，玩家2使用方向键控制
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new SnakeBattle().setVisible(true);
    });
}

class SnakeBattle extends JFrame implements KeyListener {
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 600;
    private static final int CELL_SIZE = 20;
    private static final int BOARD_COLS = BOARD_WIDTH / CELL_SIZE;
    private static final int BOARD_ROWS = BOARD_HEIGHT / CELL_SIZE;
    
    private GamePanel gamePanel;
    private javax.swing.Timer gameTimer;
    private boolean gameRunning = false;
    
    // 游戏对象
    private Snake player1Snake;
    private Snake player2Snake;
    private List<Food> foods;
    private List<PowerUp> powerUps;
    
    // 游戏状态
    private int player1Score = 0;
    private int player2Score = 0;
    private int roundsWon1 = 0;
    private int roundsWon2 = 0;
    private GameMode gameMode = GameMode.BATTLE;
    
    // 控制
    private boolean[] keys = new boolean[256];
    
    // UI组件
    private JLabel statusLabel;
    
    public SnakeBattle() {
        initializeUI();
        initializeGame();
        setupKeyboardShortcuts();
    }
    
    private void initializeUI() {
        setTitle(Texts.WINDOW_TITLE);
        setSize(BOARD_WIDTH + 16, BOARD_HEIGHT + 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 游戏面板
        gamePanel = new GamePanel();
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);
        add(gamePanel, BorderLayout.CENTER);
        
        // 状态栏
        statusLabel = new JLabel();
        updateStatusLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.DARK_GRAY);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
        
        // 控制面板
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(Color.GRAY);
        
        JButton startBtn = new JButton(Texts.START_BUTTON);
        startBtn.addActionListener(e -> startGame());

        JButton pauseBtn = new JButton(Texts.PAUSE_BUTTON);
        pauseBtn.addActionListener(e -> togglePause());

        JButton resetBtn = new JButton("重置比赛");
        resetBtn.addActionListener(e -> resetMatch());
        
        controlPanel.add(startBtn);
        controlPanel.add(pauseBtn);
        controlPanel.add(resetBtn);
        
        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void initializeGame() {
        // 创建蛇
        player1Snake = new Snake(5, 5, Color.GREEN, "玩家1");
        player2Snake = new Snake(BOARD_COLS - 6, BOARD_ROWS - 6, Color.BLUE, "玩家2");
        
        // 初始化食物和道具
        foods = new ArrayList<>();
        powerUps = new ArrayList<>();
        
        // 创建初始食物
        spawnFood();
        
        // 游戏计时器
        gameTimer = new javax.swing.Timer(120, e -> gameLoop());
    }
    
    private void startGame() {
        gameRunning = true;
        player1Score = 0;
        player2Score = 0;
        
        // 重置蛇的位置
        player1Snake.reset(5, 5);
        player2Snake.reset(BOARD_COLS - 6, BOARD_ROWS - 6);
        
        // 清空食物和道具
        foods.clear();
        powerUps.clear();
        spawnFood();
        
        gameTimer.start();
        gamePanel.requestFocus();
    }
    
    private void togglePause() {
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        } else if (gameRunning) {
            gameTimer.start();
            gamePanel.requestFocus();
        }
    }
    
    private void resetMatch() {
        gameTimer.stop();
        gameRunning = false;
        roundsWon1 = 0;
        roundsWon2 = 0;
        player1Score = 0;
        player2Score = 0;
        updateStatusLabel();
        gamePanel.repaint();
    }
    
    private void gameLoop() {
        if (!gameRunning) return;
        
        handleInput();
        updateGame();
        checkCollisions();
        checkGameState();
        
        gamePanel.repaint();
        updateStatusLabel();
    }
    
    private void handleInput() {
        // 玩家1控制 (WASD)
        if (keys[KeyEvent.VK_W] && player1Snake.getDirection() != Direction.DOWN) {
            player1Snake.setDirection(Direction.UP);
        }
        if (keys[KeyEvent.VK_S] && player1Snake.getDirection() != Direction.UP) {
            player1Snake.setDirection(Direction.DOWN);
        }
        if (keys[KeyEvent.VK_A] && player1Snake.getDirection() != Direction.RIGHT) {
            player1Snake.setDirection(Direction.LEFT);
        }
        if (keys[KeyEvent.VK_D] && player1Snake.getDirection() != Direction.LEFT) {
            player1Snake.setDirection(Direction.RIGHT);
        }
        
        // 玩家2控制 (方向键)
        if (keys[KeyEvent.VK_UP] && player2Snake.getDirection() != Direction.DOWN) {
            player2Snake.setDirection(Direction.UP);
        }
        if (keys[KeyEvent.VK_DOWN] && player2Snake.getDirection() != Direction.UP) {
            player2Snake.setDirection(Direction.DOWN);
        }
        if (keys[KeyEvent.VK_LEFT] && player2Snake.getDirection() != Direction.RIGHT) {
            player2Snake.setDirection(Direction.LEFT);
        }
        if (keys[KeyEvent.VK_RIGHT] && player2Snake.getDirection() != Direction.LEFT) {
            player2Snake.setDirection(Direction.RIGHT);
        }
    }
    
    private void updateGame() {
        // 更新蛇的移动
        player1Snake.move();
        player2Snake.move();
        
        // 随机生成道具
        if (Math.random() < 0.005 && powerUps.size() < 3) {
            spawnPowerUp();
        }
        
        // 更新道具持续时间
        powerUps.removeIf(PowerUp::isExpired);
        
        // 更新蛇的特殊状态
        player1Snake.updateEffects();
        player2Snake.updateEffects();
    }
    
    private void checkCollisions() {
        // 检查蛇与食物的碰撞
        checkFoodCollisions();
        
        // 检查蛇与道具的碰撞
        checkPowerUpCollisions();
        
        // 检查蛇与边界的碰撞
        checkBoundaryCollisions();
        
        // 检查蛇与自身的碰撞
        checkSelfCollisions();
        
        // 检查蛇与蛇的碰撞
        checkSnakeCollisions();
    }
    
    private void checkFoodCollisions() {
        Iterator<Food> foodIter = foods.iterator();
        while (foodIter.hasNext()) {
            Food food = foodIter.next();
            
            if (player1Snake.getHead().equals(food.position)) {
                player1Snake.grow();
                player1Score += food.points;
                foodIter.remove();
                spawnFood();
            } else if (player2Snake.getHead().equals(food.position)) {
                player2Snake.grow();
                player2Score += food.points;
                foodIter.remove();
                spawnFood();
            }
        }
    }
    
    private void checkPowerUpCollisions() {
        Iterator<PowerUp> powerUpIter = powerUps.iterator();
        while (powerUpIter.hasNext()) {
            PowerUp powerUp = powerUpIter.next();
            
            if (player1Snake.getHead().equals(powerUp.position)) {
                player1Snake.applyPowerUp(powerUp.type);
                powerUpIter.remove();
            } else if (player2Snake.getHead().equals(powerUp.position)) {
                player2Snake.applyPowerUp(powerUp.type);
                powerUpIter.remove();
            }
        }
    }
    
    private void checkBoundaryCollisions() {
        Point head1 = player1Snake.getHead();
        Point head2 = player2Snake.getHead();
        
        if (head1.x < 0 || head1.x >= BOARD_COLS || head1.y < 0 || head1.y >= BOARD_ROWS) {
            player1Snake.setAlive(false);
        }
        
        if (head2.x < 0 || head2.x >= BOARD_COLS || head2.y < 0 || head2.y >= BOARD_ROWS) {
            player2Snake.setAlive(false);
        }
    }
    
    private void checkSelfCollisions() {
        if (player1Snake.collidesWithSelf()) {
            player1Snake.setAlive(false);
        }
        
        if (player2Snake.collidesWithSelf()) {
            player2Snake.setAlive(false);
        }
    }
    
    private void checkSnakeCollisions() {
        Point head1 = player1Snake.getHead();
        Point head2 = player2Snake.getHead();
        
        // 检查头部相撞
        if (head1.equals(head2)) {
            player1Snake.setAlive(false);
            player2Snake.setAlive(false);
            return;
        }
        
        // 检查蛇1撞到蛇2身体
        if (player2Snake.getBody().contains(head1)) {
            player1Snake.setAlive(false);
        }
        
        // 检查蛇2撞到蛇1身体
        if (player1Snake.getBody().contains(head2)) {
            player2Snake.setAlive(false);
        }
    }
    
    private void checkGameState() {
        if (!player1Snake.isAlive() || !player2Snake.isAlive()) {
            gameTimer.stop();
            gameRunning = false;
            
            String winner;
            if (!player1Snake.isAlive() && !player2Snake.isAlive()) {
                winner = "平局！";
            } else if (player1Snake.isAlive()) {
                winner = "玩家1获胜！";
                roundsWon1++;
            } else {
                winner = "玩家2获胜！";
                roundsWon2++;
            }
            
            JOptionPane.showMessageDialog(this, 
                winner + "\n玩家1得分: " + player1Score + 
                "\n玩家2得分: " + player2Score +
                "\n\n总比分 - 玩家1: " + roundsWon1 + " | 玩家2: " + roundsWon2);
        }
    }
    
    private void spawnFood() {
        Point newFoodPos;
        do {
            newFoodPos = new Point(
                (int)(Math.random() * BOARD_COLS),
                (int)(Math.random() * BOARD_ROWS)
            );
        } while (player1Snake.getBody().contains(newFoodPos) || 
                 player2Snake.getBody().contains(newFoodPos));
        
        FoodType type = Math.random() < 0.8 ? FoodType.NORMAL : FoodType.BONUS;
        foods.add(new Food(newFoodPos, type));
    }
    
    private void spawnPowerUp() {
        Point newPos = findValidPowerUpPosition();
        if (newPos != null) {
            PowerUpType[] types = PowerUpType.values();
            PowerUpType type = types[(int)(Math.random() * types.length)];
            powerUps.add(new PowerUp(newPos, type));
        }
    }

    private Point findValidPowerUpPosition() {
        for (int attempts = 0; attempts < 100; attempts++) {
            Point candidate = new Point(
                (int)(Math.random() * BOARD_COLS),
                (int)(Math.random() * BOARD_ROWS)
            );

            if (!player1Snake.getBody().contains(candidate) &&
                !player2Snake.getBody().contains(candidate) &&
                foods.stream().noneMatch(f -> f.position.equals(candidate))) {
                return candidate;
            }
        }
        return null;
    }
    
    private void updateStatusLabel() {
        String status = String.format(
            "%s%d分 | %s%d分 | %s%d-%d | %s",
            Texts.PLAYER1_LABEL + "(绿): ", player1Score,
            Texts.PLAYER2_LABEL + "(蓝): ", player2Score,
            Texts.ROUNDS_LABEL, roundsWon1, roundsWon2,
            gameRunning ? (gameTimer.isRunning() ? Texts.STATUS_GAME_RUNNING : Texts.STATUS_GAME_PAUSED) : Texts.STATUS_GAME_START
        );
        statusLabel.setText(status);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    // 游戏面板
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制网格
            drawGrid(g2d);
            
            // 绘制食物
            for (Food food : foods) {
                food.draw(g2d);
            }
            
            // 绘制道具
            for (PowerUp powerUp : powerUps) {
                powerUp.draw(g2d);
            }
            
            // 绘制蛇
            player1Snake.draw(g2d);
            player2Snake.draw(g2d);
            
            // 绘制控制说明
            drawControls(g2d);
        }
        
        private void drawGrid(Graphics2D g2d) {
            g2d.setColor(new Color(30, 30, 30));
            for (int x = 0; x <= BOARD_WIDTH; x += CELL_SIZE) {
                g2d.drawLine(x, 0, x, BOARD_HEIGHT);
            }
            for (int y = 0; y <= BOARD_HEIGHT; y += CELL_SIZE) {
                g2d.drawLine(0, y, BOARD_WIDTH, y);
            }
        }
        
        private void drawControls(Graphics2D g2d) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("玩家1: WASD控制", 10, 20);
            g2d.drawString("玩家2: 方向键控制", 10, 35);
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
    
    // 游戏模式
    enum GameMode {
        BATTLE, COOPERATIVE
    }
    
    // 食物类型
    enum FoodType {
        NORMAL(10, Color.RED),
        BONUS(25, new Color(255, 215, 0));
        
        final int points;
        final Color color;
        
        FoodType(int points, Color color) {
            this.points = points;
            this.color = color;
        }
    }
    
    // 道具类型
    enum PowerUpType {
        SPEED_BOOST(Color.CYAN, "加速"),
        INVINCIBLE(Color.MAGENTA, "无敌"),
        DOUBLE_SCORE(Color.ORANGE, "双倍得分");
        
        final Color color;
        final String name;
        
        PowerUpType(Color color, String name) {
            this.color = color;
            this.name = name;
        }
    }
    
    // 蛇类
    class Snake {
        private List<Point> body;
        private Direction direction;
        private Color color;
        private String name;
        private boolean alive = true;
        private boolean growing = false;
        
        // 特殊效果
        private boolean speedBoost = false;
        private boolean invincible = false;
        private boolean doubleScore = false;
        private long speedBoostEnd = 0;
        private long invincibleEnd = 0;
        private long doubleScoreEnd = 0;
        
        public Snake(int startX, int startY, Color color, String name) {
            this.color = color;
            this.name = name;
            reset(startX, startY);
        }
        
        public void reset(int startX, int startY) {
            body = new ArrayList<>();
            body.add(new Point(startX, startY));
            body.add(new Point(startX - 1, startY));
            body.add(new Point(startX - 2, startY));
            direction = Direction.RIGHT;
            alive = true;
            growing = false;
            
            // 清除特殊效果
            speedBoost = false;
            invincible = false;
            doubleScore = false;
        }
        
        public void move() {
            if (!alive) return;
            
            Point head = new Point(body.get(0));
            head.x += direction.dx;
            head.y += direction.dy;
            
            body.add(0, head);
            
            if (!growing) {
                body.remove(body.size() - 1);
            } else {
                growing = false;
            }
        }
        
        public void grow() {
            growing = true;
        }
        
        public boolean collidesWithSelf() {
            Point head = getHead();
            for (int i = 1; i < body.size(); i++) {
                if (head.equals(body.get(i))) {
                    return !invincible; // 无敌状态下不会撞死自己
                }
            }
            return false;
        }
        
        public void applyPowerUp(PowerUpType type) {
            long currentTime = System.currentTimeMillis();
            
            switch (type) {
                case SPEED_BOOST:
                    speedBoost = true;
                    speedBoostEnd = currentTime + 5000; // 5秒
                    break;
                case INVINCIBLE:
                    invincible = true;
                    invincibleEnd = currentTime + 3000; // 3秒
                    break;
                case DOUBLE_SCORE:
                    doubleScore = true;
                    doubleScoreEnd = currentTime + 8000; // 8秒
                    break;
            }
        }
        
        public void updateEffects() {
            long currentTime = System.currentTimeMillis();
            
            if (speedBoost && currentTime > speedBoostEnd) {
                speedBoost = false;
            }
            if (invincible && currentTime > invincibleEnd) {
                invincible = false;
            }
            if (doubleScore && currentTime > doubleScoreEnd) {
                doubleScore = false;
            }
        }
        
        public void draw(Graphics2D g2d) {
            if (!alive) return;
            
            // 绘制身体
            Color bodyColor = color;
            if (invincible) {
                // 无敌状态闪烁效果
                bodyColor = (System.currentTimeMillis() / 200) % 2 == 0 ? color : Color.WHITE;
            } else if (speedBoost) {
                bodyColor = color.brighter();
            }
            
            for (int i = 0; i < body.size(); i++) {
                Point segment = body.get(i);
                
                if (i == 0) {
                    // 绘制头部
                    g2d.setColor(bodyColor.darker());
                } else {
                    g2d.setColor(bodyColor);
                }
                
                g2d.fillRect(
                    segment.x * CELL_SIZE + 1,
                    segment.y * CELL_SIZE + 1,
                    CELL_SIZE - 2,
                    CELL_SIZE - 2
                );
            }
            
            // 绘制特殊效果指示
            if (doubleScore) {
                g2d.setColor(Color.YELLOW);
                g2d.drawRect(
                    getHead().x * CELL_SIZE,
                    getHead().y * CELL_SIZE,
                    CELL_SIZE,
                    CELL_SIZE
                );
            }
        }
        
        // Getters and Setters
        public Point getHead() { return body.get(0); }
        public List<Point> getBody() { return body; }
        public Direction getDirection() { return direction; }
        public void setDirection(Direction direction) { this.direction = direction; }
        public boolean isAlive() { return alive; }
        public void setAlive(boolean alive) { this.alive = alive; }
        public boolean hasDoubleScore() { return doubleScore; }
    }
    
    // 食物类
    class Food {
        Point position;
        FoodType type;
        int points;
        
        public Food(Point position, FoodType type) {
            this.position = position;
            this.type = type;
            this.points = type.points;
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(type.color);
            if (type == FoodType.BONUS) {
                // 奖励食物绘制为星形
                int centerX = position.x * CELL_SIZE + CELL_SIZE / 2;
                int centerY = position.y * CELL_SIZE + CELL_SIZE / 2;
                int size = CELL_SIZE / 3;
                
                int[] xPoints = {centerX, centerX + size/2, centerX + size, centerX + size/2, centerX, centerX - size/2, centerX - size, centerX - size/2};
                int[] yPoints = {centerY - size, centerY - size/2, centerY, centerY + size/2, centerY + size, centerY + size/2, centerY, centerY - size/2};
                
                g2d.fillPolygon(xPoints, yPoints, 8);
            } else {
                g2d.fillOval(
                    position.x * CELL_SIZE + 3,
                    position.y * CELL_SIZE + 3,
                    CELL_SIZE - 6,
                    CELL_SIZE - 6
                );
            }
        }
    }
    
    // 道具类
    class PowerUp {
        Point position;
        PowerUpType type;
        long spawnTime;
        long duration = 10000; // 10秒后消失
        
        public PowerUp(Point position, PowerUpType type) {
            this.position = position;
            this.type = type;
            this.spawnTime = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - spawnTime > duration;
        }
        
        public void draw(Graphics2D g2d) {
            // 闪烁效果
            if ((System.currentTimeMillis() / 300) % 2 == 0) {
                g2d.setColor(type.color);
                g2d.fillRect(
                    position.x * CELL_SIZE + 2,
                    position.y * CELL_SIZE + 2,
                    CELL_SIZE - 4,
                    CELL_SIZE - 4
                );
                
                // 绘制边框
                g2d.setColor(Color.WHITE);
                g2d.drawRect(
                    position.x * CELL_SIZE + 2,
                    position.y * CELL_SIZE + 2,
                    CELL_SIZE - 4,
                    CELL_SIZE - 4
                );
            }
        }
    }

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
