import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🚁 坦克大战";

    // 界面标签
    static final String SCORE_LABEL = "分数: ";
    static final String LIVES_LABEL = "生命: ";
    static final String LEVEL_LABEL = "关卡: ";
    static final String START_BUTTON = "开始游戏";
    static final String PAUSE_BUTTON = "暂停";
    static final String RESUME_BUTTON = "继续";
    static final String RESTART_BUTTON = "重新开始";

    // 状态消息
    static final String STATUS_GAME_START = "按开始游戏按钮开始";
    static final String STATUS_GAME_PAUSED = "游戏已暂停";
    static final String STATUS_GAME_OVER = "游戏结束！";
    static final String STATUS_LEVEL_UP = "恭喜！进入下一关！";
    static final String STATUS_PLAYER_HIT = "玩家被击中！";
    static final String STATUS_ENEMY_DESTROYED = "击毁敌方坦克！";

    // 控制说明
    static final String CONTROL_UP = "↑/W - 向上移动";
    static final String CONTROL_DOWN = "↓/S - 向下移动";
    static final String CONTROL_LEFT = "←/A - 向左移动";
    static final String CONTROL_RIGHT = "→/D - 向右移动";
    static final String CONTROL_SHOOT = "空格 - 射击";

    // 帮助信息
    static final String HELP_MESSAGE = """
        坦克大战游戏使用说明：

        • 游戏目标：控制绿色坦克，消灭所有红色敌方坦克
        • 游戏规则：避免被敌方坦克击中，消灭敌人获得分数
        • 计分规则：击毁敌方坦克获得分数，生命值为0时游戏结束

        操作说明：
        • ↑ 或 W：向上移动
        • ↓ 或 S：向下移动
        • ← 或 A：向左移动
        • → 或 D：向右移动
        • 空格键：发射子弹

        游戏技巧：
        • 利用障碍物躲避敌方子弹
        • 预测敌方坦克的移动轨迹
        • 保持移动，避免成为固定靶
        • 优先消灭威胁较大的敌方坦克

        游戏元素：
        • 绿色坦克：玩家控制的坦克
        • 红色坦克：敌方坦克
        • 灰色方块：障碍物，可以阻挡子弹
        • 黄色方块：可破坏的障碍物

        快捷键：
        方向键/WASD - 移动控制
        空格 - 射击
        P - 暂停/继续
        R - 重新开始
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

/**
 * 坦克大战游戏
 * 简化版的坦克射击游戏，玩家控制绿色坦克对抗红色敌方坦克
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new TankBattle().setVisible(true);
    });
}

class TankBattle extends JFrame {
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 600;
    private static final int TANK_SIZE = 25;
    private static final int BULLET_SIZE = 5;
    
    private GameBoard gameBoard;
    private PlayerTank player;
    private List<EnemyTank> enemies;
    private List<Projectile> projectiles;
    private List<Obstacle> obstacles;
    private javax.swing.Timer gameTimer;
    
    private boolean[] pressedKeys = new boolean[256];
    private int playerScore = 0;
    private int playerLives = 3;
    private int currentLevel = 1;
    private boolean gameActive = false;
    
    private JLabel infoLabel;
    
    public TankBattle() {
        setupUI();
        initializeGame();
        setupKeyboardShortcuts();
    }
    
    private void setupUI() {
        setTitle(Texts.WINDOW_TITLE);
        setSize(BOARD_WIDTH + 16, BOARD_HEIGHT + 80);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        gameBoard = new GameBoard();
        add(gameBoard, BorderLayout.CENTER);
        
        // 信息面板
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoLabel = new JLabel();
        updateInfoDisplay();
        infoPanel.add(infoLabel);
        
        JButton startBtn = new JButton(Texts.START_BUTTON);
        startBtn.addActionListener(e -> startNewGame());
        infoPanel.add(startBtn);
        
        JButton pauseBtn = new JButton("暂停");
        pauseBtn.addActionListener(e -> togglePause());
        infoPanel.add(pauseBtn);
        
        add(infoPanel, BorderLayout.SOUTH);
        
        // 键盘监听
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys[e.getKeyCode()] = true;
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys[e.getKeyCode()] = false;
            }
        });
        
        setFocusable(true);
    }
    
    private void initializeGame() {
        player = new PlayerTank(BOARD_WIDTH / 2, BOARD_HEIGHT - 50);
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        obstacles = new ArrayList<>();
        
        createObstacles();
        createEnemies();
        
        gameTimer = new javax.swing.Timer(20, e -> gameLoop());
    }
    
    private void createObstacles() {
        // 创建一些障碍物
        for (int i = 0; i < 15; i++) {
            int x = (int)(Math.random() * (BOARD_WIDTH - 30));
            int y = (int)(Math.random() * (BOARD_HEIGHT - 200)) + 100;
            obstacles.add(new Obstacle(x, y));
        }
    }
    
    private void createEnemies() {
        int enemyCount = 3 + currentLevel;
        for (int i = 0; i < enemyCount; i++) {
            int x = (int)(Math.random() * (BOARD_WIDTH - TANK_SIZE));
            int y = (int)(Math.random() * 150) + 20;
            enemies.add(new EnemyTank(x, y));
        }
    }
    
    private void startNewGame() {
        playerScore = 0;
        playerLives = 3;
        currentLevel = 1;
        gameActive = true;
        
        initializeGame();
        gameTimer.start();
        requestFocus();
    }
    
    private void togglePause() {
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        } else {
            gameTimer.start();
            requestFocus();
        }
    }
    
    private void gameLoop() {
        if (!gameActive) return;
        
        handleInput();
        updateGame();
        checkCollisions();
        checkGameState();
        
        gameBoard.repaint();
        updateInfoDisplay();
    }
    
    private void handleInput() {
        if (pressedKeys[KeyEvent.VK_LEFT] || pressedKeys[KeyEvent.VK_A]) {
            player.rotateLeft();
        }
        if (pressedKeys[KeyEvent.VK_RIGHT] || pressedKeys[KeyEvent.VK_D]) {
            player.rotateRight();
        }
        if (pressedKeys[KeyEvent.VK_UP] || pressedKeys[KeyEvent.VK_W]) {
            player.moveForward();
        }
        if (pressedKeys[KeyEvent.VK_DOWN] || pressedKeys[KeyEvent.VK_S]) {
            player.moveBackward();
        }
        if (pressedKeys[KeyEvent.VK_SPACE]) {
            Projectile bullet = player.shoot();
            if (bullet != null) {
                projectiles.add(bullet);
            }
        }
    }
    
    private void updateGame() {
        // 更新玩家
        player.update();
        
        // 更新敌人
        for (EnemyTank enemy : enemies) {
            enemy.update();
            
            // 敌人AI射击
            if (Math.random() < 0.01) {
                Projectile bullet = enemy.shoot();
                if (bullet != null) {
                    projectiles.add(bullet);
                }
            }
        }
        
        // 更新子弹
        projectiles.removeIf(p -> {
            p.update();
            return p.x < 0 || p.x > BOARD_WIDTH || p.y < 0 || p.y > BOARD_HEIGHT;
        });
    }
    
    private void checkCollisions() {
        // 子弹与坦克碰撞
        Iterator<Projectile> bulletIter = projectiles.iterator();
        while (bulletIter.hasNext()) {
            Projectile bullet = bulletIter.next();
            
            // 子弹与玩家碰撞
            if (bullet.isEnemyBullet && player.isAlive() && 
                bullet.getBounds().intersects(player.getBounds())) {
                player.takeDamage();
                bulletIter.remove();
                if (!player.isAlive()) {
                    playerLives--;
                }
                continue;
            }
            
            // 子弹与敌人碰撞
            if (!bullet.isEnemyBullet) {
                for (EnemyTank enemy : enemies) {
                    if (enemy.isAlive() && bullet.getBounds().intersects(enemy.getBounds())) {
                        enemy.takeDamage();
                        bulletIter.remove();
                        if (!enemy.isAlive()) {
                            playerScore += 100;
                        }
                        break;
                    }
                }
            }
            
            // 子弹与障碍物碰撞
            for (Obstacle obstacle : obstacles) {
                if (bullet.getBounds().intersects(obstacle.getBounds())) {
                    bulletIter.remove();
                    break;
                }
            }
        }
        
        // 坦克与障碍物碰撞检测
        for (Obstacle obstacle : obstacles) {
            if (player.getBounds().intersects(obstacle.getBounds())) {
                player.undoLastMove();
            }
            
            for (EnemyTank enemy : enemies) {
                if (enemy.getBounds().intersects(obstacle.getBounds())) {
                    enemy.undoLastMove();
                    enemy.changeDirection();
                }
            }
        }
    }
    
    private void checkGameState() {
        // 检查玩家是否死亡
        if (playerLives <= 0) {
            gameActive = false;
            gameTimer.stop();
            JOptionPane.showMessageDialog(this, "游戏结束！最终得分: " + playerScore);
            return;
        }
        
        // 检查是否消灭所有敌人
        boolean allEnemiesDead = enemies.stream().noneMatch(EnemyTank::isAlive);
        if (allEnemiesDead) {
            currentLevel++;
            playerScore += currentLevel * 500;
            createEnemies();
            JOptionPane.showMessageDialog(this, "第 " + currentLevel + " 关开始！");
        }
        
        // 重生玩家
        if (!player.isAlive() && playerLives > 0) {
            player = new PlayerTank(BOARD_WIDTH / 2, BOARD_HEIGHT - 50);
        }
    }
    
    private void updateInfoDisplay() {
        int aliveEnemies = (enemies != null) ? (int)enemies.stream().filter(EnemyTank::isAlive).count() : 0;
        infoLabel.setText(String.format("%s%d | %s%d | %s%d | 敌人: %d",
            Texts.SCORE_LABEL, playerScore,
            Texts.LIVES_LABEL, playerLives,
            Texts.LEVEL_LABEL, currentLevel,
            aliveEnemies));
    }
    
    // 游戏面板
    class GameBoard extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制障碍物
            g2d.setColor(Color.GRAY);
            for (Obstacle obstacle : obstacles) {
                obstacle.draw(g2d);
            }
            
            // 绘制玩家坦克
            if (player.isAlive()) {
                player.draw(g2d);
            }
            
            // 绘制敌方坦克
            for (EnemyTank enemy : enemies) {
                if (enemy.isAlive()) {
                    enemy.draw(g2d);
                }
            }
            
            // 绘制子弹
            g2d.setColor(Color.YELLOW);
            for (Projectile bullet : projectiles) {
                bullet.draw(g2d);
            }
        }
    }
    
    // 坦克基类
    abstract class Tank {
        protected int x, y, prevX, prevY;
        protected double angle = 0;
        protected boolean alive = true;
        protected long lastShotTime = 0;
        protected int shotCooldown = 300;
        
        public Tank(int x, int y) {
            this.x = x;
            this.y = y;
            this.prevX = x;
            this.prevY = y;
        }
        
        public void moveForward() {
            prevX = x;
            prevY = y;
            x += Math.cos(Math.toRadians(angle)) * 2;
            y += Math.sin(Math.toRadians(angle)) * 2;
            
            // 边界检查
            x = Math.max(0, Math.min(BOARD_WIDTH - TANK_SIZE, x));
            y = Math.max(0, Math.min(BOARD_HEIGHT - TANK_SIZE, y));
        }
        
        public void moveBackward() {
            prevX = x;
            prevY = y;
            x -= Math.cos(Math.toRadians(angle)) * 1;
            y -= Math.sin(Math.toRadians(angle)) * 1;
            
            x = Math.max(0, Math.min(BOARD_WIDTH - TANK_SIZE, x));
            y = Math.max(0, Math.min(BOARD_HEIGHT - TANK_SIZE, y));
        }
        
        public void undoLastMove() {
            x = prevX;
            y = prevY;
        }
        
        public Projectile shoot() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime < shotCooldown) {
                return null;
            }
            
            lastShotTime = currentTime;
            int bulletX = x + TANK_SIZE / 2;
            int bulletY = y + TANK_SIZE / 2;
            return new Projectile(bulletX, bulletY, angle, this instanceof EnemyTank);
        }
        
        public void takeDamage() {
            alive = false;
        }
        
        public Rectangle getBounds() {
            return new Rectangle(x, y, TANK_SIZE, TANK_SIZE);
        }
        
        public void update() {}
        
        public abstract void draw(Graphics2D g2d);
        
        public boolean isAlive() { return alive; }
    }
    
    // 玩家坦克
    class PlayerTank extends Tank {
        public PlayerTank(int x, int y) {
            super(x, y);
            angle = -90; // 向上
        }
        
        public void rotateLeft() {
            angle -= 3;
        }
        
        public void rotateRight() {
            angle += 3;
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.GREEN);
            g2d.fillRect(x, y, TANK_SIZE, TANK_SIZE);
            
            // 绘制炮管
            g2d.setColor(new Color(0, 100, 0));
            int centerX = x + TANK_SIZE / 2;
            int centerY = y + TANK_SIZE / 2;
            int barrelEndX = centerX + (int)(Math.cos(Math.toRadians(angle)) * 15);
            int barrelEndY = centerY + (int)(Math.sin(Math.toRadians(angle)) * 15);
            
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(centerX, centerY, barrelEndX, barrelEndY);
        }
    }
    
    // 敌方坦克
    class EnemyTank extends Tank {
        private long lastDirectionChange = 0;
        
        public EnemyTank(int x, int y) {
            super(x, y);
            angle = Math.random() * 360;
            shotCooldown = 800;
        }
        
        @Override
        public void update() {
            // 简单AI：随机移动和转向
            if (Math.random() < 0.02) {
                changeDirection();
            }
            
            if (Math.random() < 0.05) {
                moveForward();
            }
        }
        
        public void changeDirection() {
            angle = Math.random() * 360;
        }
        
        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, TANK_SIZE, TANK_SIZE);
            
            // 绘制炮管
            g2d.setColor(new Color(139, 0, 0));
            int centerX = x + TANK_SIZE / 2;
            int centerY = y + TANK_SIZE / 2;
            int barrelEndX = centerX + (int)(Math.cos(Math.toRadians(angle)) * 15);
            int barrelEndY = centerY + (int)(Math.sin(Math.toRadians(angle)) * 15);
            
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(centerX, centerY, barrelEndX, barrelEndY);
        }
    }
    
    // 子弹类
    class Projectile {
        int x, y;
        double angle;
        boolean isEnemyBullet;
        int speed = 4;
        
        public Projectile(int x, int y, double angle, boolean isEnemyBullet) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.isEnemyBullet = isEnemyBullet;
        }
        
        public void update() {
            x += Math.cos(Math.toRadians(angle)) * speed;
            y += Math.sin(Math.toRadians(angle)) * speed;
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(isEnemyBullet ? Color.ORANGE : Color.YELLOW);
            g2d.fillOval(x - BULLET_SIZE/2, y - BULLET_SIZE/2, BULLET_SIZE, BULLET_SIZE);
        }
        
        public Rectangle getBounds() {
            return new Rectangle(x - BULLET_SIZE/2, y - BULLET_SIZE/2, BULLET_SIZE, BULLET_SIZE);
        }
    }
    
    // 障碍物类
    class Obstacle {
        int x, y;
        int width = 30, height = 30;
        
        public Obstacle(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.GRAY);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(x, y, width, height);
        }
        
        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
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
