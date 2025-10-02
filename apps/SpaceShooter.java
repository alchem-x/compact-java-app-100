import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new SpaceShooter().setVisible(true);
    });
}

static class SpaceShooter extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PLAYER_SIZE = 40;
    private static final int BULLET_SIZE = 5;
    private static final int ENEMY_SIZE = 30;

    private GamePanel gamePanel;
    private Timer gameTimer;
    private boolean gameRunning;
    private int score;
    private int lives;
    private int level;

    public SpaceShooter() {
        setTitle("飞机大战 - Space Shooter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initializeGame();
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
    }

    private void initializeGame() {
        gamePanel = new GamePanel();
        add(gamePanel);

        score = 0;
        lives = 3;
        level = 1;
        gameRunning = false;

        // 创建游戏计时器
        gameTimer = new Timer(16, e -> gameUpdate()); // ~60 FPS

        // 添加键盘监听器
        addKeyListener(new GameKeyListener());
        setFocusable(true);

        // 显示开始对话框
        showStartDialog();
    }

    private void showStartDialog() {
        String message = """
            欢迎来到飞机大战！

            游戏规则：
            • 使用 ← → 键或 A/D 键控制飞机移动
            • 按空格键发射子弹
            • 消灭敌机获得分数
            • 避免与敌机碰撞
            • 你有 3 条生命

            按空格键开始游戏！
            """;

        JOptionPane.showMessageDialog(this, message, "游戏说明",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void gameUpdate() {
        if (gameRunning) {
            gamePanel.updateGame();
        }
    }

    private void startGame() {
        if (!gameRunning) {
            gameRunning = true;
            gameTimer.start();
            gamePanel.resetGame();
        }
    }

    private void pauseGame() {
        gameRunning = !gameRunning;
        if (gameRunning) {
            gameTimer.start();
        } else {
            gameTimer.stop();
        }
    }

    private void gameOver(boolean win) {
        gameRunning = false;
        gameTimer.stop();

        String message = win ?
            "恭喜通关！\n最终得分: " + score + "\n当前关卡: " + level :
            "游戏结束！\n最终得分: " + score;

        int option = JOptionPane.showConfirmDialog(this, message + "\n\n是否重新开始？",
            win ? "胜利！" : "游戏结束", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            score = 0;
            lives = 3;
            level = 1;
            gamePanel.resetGame();
            startGame();
        }
    }

    class GamePanel extends JPanel {
        public Player player;
        private List<Bullet> bullets;
        private List<Enemy> enemies;
        private List<Explosion> explosions;
        private Random random;
        private int enemySpawnTimer;
        private int enemySpawnDelay;

        public GamePanel() {
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(WIDTH, HEIGHT));

            player = new Player();
            bullets = new ArrayList<>();
            enemies = new ArrayList<>();
            explosions = new ArrayList<>();
            random = new Random();

            enemySpawnTimer = 0;
            enemySpawnDelay = 60; // 初始敌机生成间隔

            // 生成初始敌机
            spawnInitialEnemies();
        }

        private void spawnInitialEnemies() {
            for (int i = 0; i < 5; i++) {
                spawnEnemy();
            }
        }

        private void spawnEnemy() {
            // 添加边界检查，确保参数为正数
            int availableWidth = WIDTH - ENEMY_SIZE;
            if (availableWidth <= 0) {
                availableWidth = 1; // 最小值为1
            }
            int x = random.nextInt(availableWidth);
            int y = -ENEMY_SIZE;
            int type = random.nextInt(3); // 3种不同类型的敌机
            enemies.add(new Enemy(x, y, type));
        }

        public void resetGame() {
            player.reset();
            bullets.clear();
            enemies.clear();
            explosions.clear();
            spawnInitialEnemies();
            enemySpawnTimer = 0;
            repaint();
        }

        public void updateGame() {
            // 更新玩家
            player.move();

            // 更新子弹
            updateBullets();

            // 更新敌机
            updateEnemies();

            // 更新爆炸效果
            updateExplosions();

            // 生成新敌机
            spawnNewEnemies();

            // 检查碰撞
            checkCollisions();

            // 检查游戏状态
            checkGameStatus();

            repaint();
        }

        private void updateBullets() {
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                bullet.move();
                if (bullet.y < 0) {
                    bullets.remove(i);
                }
            }
        }

        private void updateEnemies() {
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                enemy.move();
                if (enemy.y > HEIGHT) {
                    enemies.remove(i);
                }
            }
        }

        private void updateExplosions() {
            for (int i = explosions.size() - 1; i >= 0; i--) {
                Explosion explosion = explosions.get(i);
                explosion.update();
                if (explosion.isFinished()) {
                    explosions.remove(i);
                }
            }
        }

        private void spawnNewEnemies() {
            enemySpawnTimer++;
            if (enemySpawnTimer >= enemySpawnDelay) {
                spawnEnemy();
                enemySpawnTimer = 0;
                // 随着游戏进行，敌机生成速度加快
                if (enemySpawnDelay > 20) {
                    enemySpawnDelay--;
                }
            }
        }

        private void checkCollisions() {
            Rectangle playerBounds = player.getBounds();

            // 检查子弹与敌机的碰撞
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                Rectangle bulletBounds = bullet.getBounds();

                for (int j = enemies.size() - 1; j >= 0; j--) {
                    Enemy enemy = enemies.get(j);
                    if (bulletBounds.intersects(enemy.getBounds())) {
                        // 击中敌机
                        bullets.remove(i);
                        enemies.remove(j);
                        score += enemy.getPoints();

                        // 创建爆炸效果
                        explosions.add(new Explosion(enemy.x + ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE / 2));
                        break;
                    }
                }
            }

            // 检查玩家与敌机的碰撞
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                if (playerBounds.intersects(enemy.getBounds())) {
                    // 玩家被击中
                    enemies.remove(i);
                    lives--;
                    explosions.add(new Explosion(player.x + PLAYER_SIZE / 2, player.y + PLAYER_SIZE / 2));

                    // 玩家短暂无敌
                    player.setInvulnerable(60); // 1秒无敌时间
                }
            }
        }

        private void checkGameStatus() {
            if (lives <= 0) {
                gameOver(false);
            }

            // 可以添加通关条件
            if (score >= 10000) { // 示例通关条件
                gameOver(true);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制星空背景
            drawStarfield(g2d);

            // 绘制游戏元素
            drawGame(g2d);

            // 绘制UI
            drawUI(g2d);
        }

        private void drawStarfield(Graphics2D g2d) {
            // 简单的星空效果
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < 50; i++) {
                int x = (i * 37) % WIDTH;
                int y = (i * 23 + (int) (System.currentTimeMillis() / 100) % HEIGHT) % HEIGHT;
                g2d.fillOval(x, y, 2, 2);
            }
        }

        private void drawGame(Graphics2D g2d) {
            // 绘制玩家
            player.draw(g2d);

            // 绘制子弹
            for (Bullet bullet : bullets) {
                bullet.draw(g2d);
            }

            // 绘制敌机
            for (Enemy enemy : enemies) {
                enemy.draw(g2d);
            }

            // 绘制爆炸效果
            for (Explosion explosion : explosions) {
                explosion.draw(g2d);
            }
        }

        private void drawUI(Graphics2D g2d) {
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));

            // 绘制分数
            g2d.drawString("得分: " + score, 20, 30);

            // 绘制生命
            g2d.drawString("生命: " + lives, WIDTH - 100, 30);

            // 绘制关卡
            g2d.drawString("关卡: " + level, WIDTH / 2 - 30, 30);

            // 绘制游戏状态
            if (!gameRunning && lives > 0) {
                g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
                String message = "按空格键开始游戏";
                FontMetrics fm = g2d.getFontMetrics();
                int messageWidth = fm.stringWidth(message);
                g2d.drawString(message, (WIDTH - messageWidth) / 2, HEIGHT / 2);
            }
        }
    }

    class Player {
        int x, y;
        private int dx;
        private int invulnerableTimer;

        public Player() {
            reset();
        }

        public void reset() {
            x = WIDTH / 2 - PLAYER_SIZE / 2;
            y = HEIGHT - 80;
            dx = 0;
            invulnerableTimer = 0;
        }

        public void move() {
            x += dx;

            // 限制玩家在边界内
            if (x < 0) x = 0;
            if (x > WIDTH - PLAYER_SIZE) x = WIDTH - PLAYER_SIZE;

            // 更新无敌时间
            if (invulnerableTimer > 0) {
                invulnerableTimer--;
            }
        }

        public void setDX(int dx) {
            this.dx = dx;
        }

        public void setInvulnerable(int frames) {
            this.invulnerableTimer = frames;
        }

        public boolean isInvulnerable() {
            return invulnerableTimer > 0;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);
        }

        public void draw(Graphics2D g2d) {
            if (isInvulnerable() && (invulnerableTimer / 10) % 2 == 0) {
                return; // 无敌时闪烁效果
            }

            // 绘制玩家飞机
            g2d.setColor(Color.CYAN);

            // 机身
            int[] xPoints = {x + PLAYER_SIZE / 2, x, x + PLAYER_SIZE};
            int[] yPoints = {y, y + PLAYER_SIZE, y + PLAYER_SIZE};
            g2d.fillPolygon(xPoints, yPoints, 3);

            // 机翼
            g2d.fillRect(x + 5, y + PLAYER_SIZE - 10, PLAYER_SIZE - 10, 8);

            // 引擎火焰
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(x + PLAYER_SIZE / 2 - 3, y + PLAYER_SIZE, 6, 5);
            g2d.setColor(Color.RED);
            g2d.fillRect(x + PLAYER_SIZE / 2 - 2, y + PLAYER_SIZE + 3, 4, 3);
        }
    }

    class Bullet {
        int x, y;
        private int dy;

        public Bullet(int x, int y) {
            this.x = x;
            this.y = y;
            this.dy = -10;
        }

        public void move() {
            y += dy;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, BULLET_SIZE, BULLET_SIZE);
        }

        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x, y, BULLET_SIZE, BULLET_SIZE);

            // 子弹尾焰
            g2d.setColor(Color.ORANGE);
            g2d.fillOval(x, y + 3, BULLET_SIZE, BULLET_SIZE / 2);
        }
    }

    class Enemy {
        int x, y;
        private int type;
        private int dy;
        private int dx;
        private int points;

        public Enemy(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;

            switch (type) {
                case 0: // 普通敌机
                    dy = 2;
                    dx = 0;
                    points = 100;
                    break;
                case 1: // 快速敌机
                    dy = 4;
                    dx = 0;
                    points = 200;
                    break;
                case 2: // 左右移动敌机
                    dy = 1;
                    dx = random.nextBoolean() ? 2 : -2;
                    points = 150;
                    break;
            }
        }

        public void move() {
            y += dy;
            x += dx;

            // 左右移动边界检查
            if (x < 0 || x > WIDTH - ENEMY_SIZE) {
                dx = -dx;
            }
        }

        public int getPoints() {
            return points;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, ENEMY_SIZE, ENEMY_SIZE);
        }

        public void draw(Graphics2D g2d) {
            switch (type) {
                case 0: // 普通敌机
                    g2d.setColor(Color.RED);
                    break;
                case 1: // 快速敌机
                    g2d.setColor(Color.ORANGE);
                    break;
                case 2: // 左右移动敌机
                    g2d.setColor(Color.MAGENTA);
                    break;
            }

            // 绘制敌机
            int[] xPoints = {x + ENEMY_SIZE / 2, x, x + ENEMY_SIZE};
            int[] yPoints = {y + ENEMY_SIZE, y, y};
            g2d.fillPolygon(xPoints, yPoints, 3);

            // 敌机细节
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(x + ENEMY_SIZE / 2 - 3, y + ENEMY_SIZE / 2, 6, 4);
        }
    }

    class Explosion {
        int x, y;
        private int timer;
        private int maxTimer;

        public Explosion(int x, int y) {
            this.x = x;
            this.y = y;
            this.timer = 0;
            this.maxTimer = 20;
        }

        public void update() {
            timer++;
        }

        public boolean isFinished() {
            return timer >= maxTimer;
        }

        public void draw(Graphics2D g2d) {
            float alpha = 1.0f - (float) timer / maxTimer;
            Color explosionColor = new Color(1.0f, 0.5f, 0.0f, alpha);

            g2d.setColor(explosionColor);
            int size = (int) (timer * 2);
            g2d.fillOval(x - size / 2, y - size / 2, size, size);

            // 内圈
            g2d.setColor(new Color(1.0f, 1.0f, 0.0f, alpha));
            g2d.fillOval(x - size / 4, y - size / 4, size / 2, size / 2);
        }
    }

    class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    gamePanel.player.setDX(-6);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    gamePanel.player.setDX(6);
                    break;
                case KeyEvent.VK_SPACE:
                    if (gameRunning) {
                        // 发射子弹
                        gamePanel.bullets.add(new Bullet(gamePanel.player.x + PLAYER_SIZE / 2 - BULLET_SIZE / 2,
                            gamePanel.player.y));
                    } else {
                        startGame();
                    }
                    break;
                case KeyEvent.VK_P:
                    pauseGame();
                    break;
                case KeyEvent.VK_R:
                    if (!gameRunning) {
                        score = 0;
                        lives = 3;
                        level = 1;
                        gamePanel.resetGame();
                    }
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_A:
                case KeyEvent.VK_D:
                    gamePanel.player.setDX(0);
                    break;
            }
        }
    }

    private Random random = new Random();
}