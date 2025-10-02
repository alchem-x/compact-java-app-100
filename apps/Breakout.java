import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Breakout().setVisible(true);
    });
}

static class Breakout extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_SIZE = 10;
    private static final int BRICK_WIDTH = 75;
    private static final int BRICK_HEIGHT = 20;
    private static final int BRICK_ROWS = 8;
    private static final int BRICK_COLS = 10;

    private GamePanel gamePanel;
    private Timer gameTimer;
    private boolean gameRunning;
    private int score;
    private int lives;
    private int level;

    public Breakout() {
        setTitle("打砖块游戏 - Breakout");
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
            欢迎来到打砖块游戏！

            游戏规则：
            • 使用 ← → 键或 A/D 键控制挡板
            • 防止球掉落，击碎所有砖块
            • 你有 3 条生命
            • 不同颜色的砖块有不同分值

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
        public Paddle paddle;
        private Ball ball;
        private List<Brick> bricks;
        private Random random;

        public GamePanel() {
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(WIDTH, HEIGHT));

            paddle = new Paddle();
            ball = new Ball();
            bricks = new ArrayList<>();
            random = new Random();

            createBricks();
        }

        private void createBricks() {
            bricks.clear();
            Color[] colors = {
                Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
                Color.CYAN, Color.BLUE, Color.MAGENTA, Color.PINK
            };
            int[] points = {80, 70, 60, 50, 40, 30, 20, 10};

            int startX = (WIDTH - BRICK_COLS * BRICK_WIDTH) / 2;
            int startY = 50;

            for (int row = 0; row < BRICK_ROWS; row++) {
                for (int col = 0; col < BRICK_COLS; col++) {
                    int x = startX + col * BRICK_WIDTH;
                    int y = startY + row * BRICK_HEIGHT;
                    Color color = colors[row % colors.length];
                    int point = points[row % points.length];
                    bricks.add(new Brick(x, y, color, point));
                }
            }
        }

        public void resetGame() {
            paddle.reset();
            ball.reset();
            createBricks();
            repaint();
        }

        public void updateGame() {
            // 更新球的位置
            ball.move();

            // 检查碰撞
            checkCollisions();

            // 检查游戏状态
            checkGameStatus();

            repaint();
        }

        private void checkCollisions() {
            Rectangle ballRect = ball.getBounds();

            // 检查与挡板的碰撞
            if (ballRect.intersects(paddle.getBounds())) {
                ball.reverseY();
                // 根据碰撞位置调整球的角度
                int paddleCenter = paddle.x + PADDLE_WIDTH / 2;
                int ballCenter = ball.x + BALL_SIZE / 2;
                int deltaX = ballCenter - paddleCenter;
                ball.setSpeedX(deltaX / 5);
            }

            // 检查与砖块的碰撞
            for (int i = bricks.size() - 1; i >= 0; i--) {
                Brick brick = bricks.get(i);
                if (brick.isVisible() && ballRect.intersects(brick.getBounds())) {
                    brick.hide();
                    ball.reverseY();
                    score += brick.getPoints();

                    // 添加一些视觉效果
                    createParticles(brick.x + BRICK_WIDTH / 2, brick.y + BRICK_HEIGHT / 2);
                    break; // 一次只碰撞一个砖块
                }
            }

            // 检查与边界的碰撞
            if (ball.x <= 0 || ball.x >= WIDTH - BALL_SIZE) {
                ball.reverseX();
            }
            if (ball.y <= 0) {
                ball.reverseY();
            }
        }

        private void createParticles(int x, int y) {
            // 简单的粒子效果（这里只是示意）
            // 实际实现可以添加更多视觉效果
        }

        private void checkGameStatus() {
            // 检查球是否掉落
            if (ball.y > HEIGHT) {
                lives--;
                if (lives > 0) {
                    ball.reset();
                    paddle.reset();
                    JOptionPane.showMessageDialog(this, "球掉落了！剩余生命: " + lives,
                        "提示", JOptionPane.WARNING_MESSAGE);
                } else {
                    gameOver(false);
                }
            }

            // 检查是否通关
            boolean allBricksDestroyed = bricks.stream().noneMatch(Brick::isVisible);
            if (allBricksDestroyed) {
                level++;
                score += 500; // 通关奖励
                JOptionPane.showMessageDialog(this, "恭喜通过第 " + (level - 1) + " 关！\n进入下一关",
                    "通关！", JOptionPane.INFORMATION_MESSAGE);
                createBricks();
                ball.reset();
                paddle.reset();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制游戏元素
            drawGame(g2d);

            // 绘制UI
            drawUI(g2d);
        }

        private void drawGame(Graphics2D g2d) {
            // 绘制挡板
            paddle.draw(g2d);

            // 绘制球
            ball.draw(g2d);

            // 绘制砖块
            for (Brick brick : bricks) {
                if (brick.isVisible()) {
                    brick.draw(g2d);
                }
            }

            // 绘制粒子效果（如果有的话）
            // drawParticles(g2d);
        }

        private void drawUI(Graphics2D g2d) {
            g2d.setColor(Color.WHITE);
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

    class Paddle {
        int x, y;
        private int dx;

        public Paddle() {
            reset();
        }

        public void reset() {
            x = (WIDTH - PADDLE_WIDTH) / 2;
            y = HEIGHT - 50;
            dx = 0;
        }

        public void move() {
            x += dx;
            // 限制挡板在边界内
            if (x < 0) x = 0;
            if (x > WIDTH - PADDLE_WIDTH) x = WIDTH - PADDLE_WIDTH;
        }

        public void setDX(int dx) {
            this.dx = dx;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
        }

        public void draw(Graphics2D g2d) {
            // 绘制挡板主体
            g2d.setColor(new Color(100, 150, 255));
            g2d.fillRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);

            // 绘制挡板边框
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);

            // 绘制高光效果
            g2d.setColor(new Color(150, 200, 255));
            g2d.fillRect(x + 5, y + 2, PADDLE_WIDTH - 10, 5);
        }
    }

    class Ball {
        int x, y;
        private int dx, dy;
        private int speed;

        public Ball() {
            reset();
        }

        public void reset() {
            x = WIDTH / 2 - BALL_SIZE / 2;
            y = HEIGHT / 2;
            dx = 3;
            dy = -3;
            speed = 1;
        }

        public void move() {
            x += dx * speed;
            y += dy * speed;
        }

        public void reverseX() {
            dx = -dx;
        }

        public void reverseY() {
            dy = -dy;
        }

        public void setSpeedX(int newDx) {
            dx = newDx;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, BALL_SIZE, BALL_SIZE);
        }

        public void draw(Graphics2D g2d) {
            // 绘制球体
            g2d.setColor(new Color(255, 100, 100));
            g2d.fillOval(x, y, BALL_SIZE, BALL_SIZE);

            // 绘制球体边框
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x, y, BALL_SIZE, BALL_SIZE);

            // 绘制高光
            g2d.setColor(new Color(255, 150, 150));
            g2d.fillOval(x + 3, y + 3, BALL_SIZE / 3, BALL_SIZE / 3);
        }
    }

    class Brick {
        int x, y;
        private Color color;
        private int points;
        private boolean visible;

        public Brick(int x, int y, Color color, int points) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.points = points;
            this.visible = true;
        }

        public boolean isVisible() {
            return visible;
        }

        public void hide() {
            visible = false;
        }

        public int getPoints() {
            return points;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        }

        public void draw(Graphics2D g2d) {
            if (!visible) return;

            // 绘制砖块主体
            g2d.setColor(color);
            g2d.fillRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);

            // 绘制砖块边框
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);

            // 绘制高光效果
            g2d.setColor(color.brighter());
            g2d.fillRect(x + 2, y + 2, BRICK_WIDTH - 4, 5);

            // 绘制分数
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            FontMetrics fm = g2d.getFontMetrics();
            String text = String.valueOf(points);
            int textWidth = fm.stringWidth(text);
            g2d.drawString(text, x + (BRICK_WIDTH - textWidth) / 2, y + BRICK_HEIGHT / 2 + 3);
        }
    }

    class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    gamePanel.paddle.setDX(-8);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    gamePanel.paddle.setDX(8);
                    break;
                case KeyEvent.VK_SPACE:
                    startGame();
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
                    gamePanel.paddle.setDX(0);
                    break;
            }
        }
    }
}