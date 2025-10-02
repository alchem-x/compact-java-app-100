import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🐍 贪吃蛇游戏";

    // 界面标签
    static final String SCORE_LABEL = "分数: ";
    static final String HIGH_SCORE_LABEL = "最高分: ";
    static final String FINAL_SCORE_FORMAT = "最终分数: %d";
    static final String HIGH_SCORE_FORMAT = "最高分数: %d";
    static final String GAME_OVER_TEXT = "游戏结束";
    static final String RESTART_HINT = "按空格键重新开始";

    // 按钮文本
    static final String PAUSE_BUTTON = "暂停";
    static final String RESUME_BUTTON = "继续";
    static final String RESTART_BUTTON = "重新开始";

    // 状态消息
    static final String STATUS_PAUSED = "游戏已暂停";
    static final String STATUS_RESUMED = "游戏继续";
    static final String STATUS_RESTARTED = "游戏重新开始";

    // 帮助信息
    static final String HELP_MESSAGE = """
        贪吃蛇游戏使用说明：

        • 游戏目标：控制蛇吃到苹果，尽可能获得高分
        • 控制方式：使用方向键或WASD键控制蛇的移动
        • 游戏规则：蛇吃到苹果会变长，撞到墙壁或自身会游戏结束
        • 暂停功能：按空格键可以暂停/继续游戏
        • 重新开始：游戏结束后按空格键重新开始

        操作说明：
        • ↑ 或 W：向上移动
        • ↓ 或 S：向下移动
        • ← 或 A：向左移动
        • → 或 D：向右移动
        • 空格键：暂停/继续游戏（或重新开始）

        游戏技巧：
        • 提前规划移动路径，避免撞墙
        • 注意蛇身长度，避免咬到自己
        • 尽量保持蛇身在游戏区域中央
        • 随着蛇身变长，移动空间会减少

        快捷键：
        方向键/WASD - 移动控制
        空格键 - 暂停/继续/重新开始
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Snake().setVisible(true);
    });
}

static class Snake extends JFrame implements ActionListener {
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (BOARD_WIDTH * BOARD_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100;

    private ArrayList<Point> snake;
    private Point apple;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;
    private int score = 0;
    private int highScore = 0;
    private GamePanel gamePanel;

    class GamePanel extends JPanel {
        GamePanel() {
            this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
            this.setBackground(Color.BLACK);
            this.setFocusable(true);
            this.addKeyListener(new MyKeyAdapter());
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    }

    public Snake() {
        initializeGame();
        initializeGUI();
        setupKeyboardShortcuts();
    }

    private void initializeGame() {
        random = new Random();
        snake = new ArrayList<>();
        
        // 初始化蛇身
        for (int i = 0; i < 6; i++) {
            snake.add(new Point(0, 0));
        }
        
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 游戏面板
        gamePanel = new GamePanel();
        
        // 信息面板
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.setBackground(Color.DARK_GRAY);
        
        JLabel scoreLabel = new JLabel(Texts.SCORE_LABEL + score);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));

        JLabel highScoreLabel = new JLabel(Texts.HIGH_SCORE_LABEL + highScore);
        highScoreLabel.setForeground(Color.YELLOW);
        highScoreLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));

        JButton pauseButton = new JButton(Texts.PAUSE_BUTTON);
        pauseButton.addActionListener(e -> togglePause());

        JButton restartButton = new JButton(Texts.RESTART_BUTTON);
        restartButton.addActionListener(e -> restart());
        
        infoPanel.add(scoreLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(highScoreLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(pauseButton);
        infoPanel.add(restartButton);
        
        // 更新分数的定时器
        Timer scoreTimer = new Timer(100, e -> {
            scoreLabel.setText(Texts.SCORE_LABEL + score);
            highScoreLabel.setText(Texts.HIGH_SCORE_LABEL + highScore);
        });
        scoreTimer.start();
        
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        gamePanel.requestFocusInWindow();
    }

    public void newApple() {
        int x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        int y = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        apple = new Point(x, y);
        
        // 确保苹果不在蛇身上
        while (snake.contains(apple)) {
            x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            y = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            apple = new Point(x, y);
        }
    }

    public void move() {
        Point newHead = new Point(snake.get(0));

        switch (direction) {
            case 'U':
                newHead.y -= UNIT_SIZE;
                break;
            case 'D':
                newHead.y += UNIT_SIZE;
                break;
            case 'L':
                newHead.x -= UNIT_SIZE;
                break;
            case 'R':
                newHead.x += UNIT_SIZE;
                break;
        }

        snake.add(0, newHead);

        // 检查是否吃到苹果
        if (newHead.equals(apple)) {
            score++;
            newApple();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    public void checkApple() {
        // 在move方法中已经处理
    }

    public void checkCollisions() {
        Point head = snake.get(0);
        
        // 检查头部是否碰到身体
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
            }
        }

        // 检查头部是否碰到边界
        if (head.x < 0 || head.x >= BOARD_WIDTH || head.y < 0 || head.y >= BOARD_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            if (score > highScore) {
                highScore = score;
            }
        }
    }

    public void draw(Graphics g) {
        if (running) {
            // 绘制网格线（可选）
            g.setColor(Color.DARK_GRAY);
            for (int i = 0; i < BOARD_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, BOARD_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, BOARD_WIDTH, i * UNIT_SIZE);
            }

            // 绘制苹果
            g.setColor(Color.RED);
            g.fillOval(apple.x, apple.y, UNIT_SIZE, UNIT_SIZE);
            
            // 添加苹果高光效果
            g.setColor(Color.PINK);
            g.fillOval(apple.x + 5, apple.y + 5, UNIT_SIZE / 3, UNIT_SIZE / 3);

            // 绘制蛇
            for (int i = 0; i < snake.size(); i++) {
                Point segment = snake.get(i);
                if (i == 0) {
                    // 蛇头
                    g.setColor(Color.GREEN);
                    g.fillRect(segment.x, segment.y, UNIT_SIZE, UNIT_SIZE);
                    
                    // 蛇眼
                    g.setColor(Color.BLACK);
                    g.fillOval(segment.x + 5, segment.y + 5, 5, 5);
                    g.fillOval(segment.x + 15, segment.y + 5, 5, 5);
                } else {
                    // 蛇身
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(segment.x, segment.y, UNIT_SIZE, UNIT_SIZE);
                    
                    // 蛇身纹理
                    g.setColor(new Color(0, 100, 0));
                    g.drawRect(segment.x, segment.y, UNIT_SIZE, UNIT_SIZE);
                }
            }
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        // 半透明遮罩
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        
        // 游戏结束文字
        g.setColor(Color.RED);
        g.setFont(new Font("SF Pro Display", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        String gameOverText = Texts.GAME_OVER_TEXT;
        g.drawString(gameOverText,
            (BOARD_WIDTH - metrics1.stringWidth(gameOverText)) / 2,
            g.getFont().getSize());

        // 分数显示
        g.setColor(Color.WHITE);
        g.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String scoreText = String.format(Texts.FINAL_SCORE_FORMAT, score);
        g.drawString(scoreText,
            (BOARD_WIDTH - metrics2.stringWidth(scoreText)) / 2,
            g.getFont().getSize() + 50);

        String highScoreText = String.format(Texts.HIGH_SCORE_FORMAT, highScore);
        g.drawString(highScoreText,
            (BOARD_WIDTH - metrics2.stringWidth(highScoreText)) / 2,
            g.getFont().getSize() + 80);

        // 重新开始提示
        g.setColor(Color.YELLOW);
        g.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        String restartText = Texts.RESTART_HINT;
        g.drawString(restartText,
            (BOARD_WIDTH - metrics3.stringWidth(restartText)) / 2,
            g.getFont().getSize() + 120);
    }

    private void togglePause() {
        if (running) {
            if (timer.isRunning()) {
                timer.stop();
            } else {
                timer.start();
            }
        }
    }

    private void restart() {
        timer.stop();
        snake.clear();
        
        // 重新初始化蛇
        for (int i = 0; i < 6; i++) {
            snake.add(new Point(0, 0));
        }
        
        direction = 'R';
        score = 0;
        newApple();
        running = true;
        timer.start();
        gamePanel.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running) {
                        restart();
                    } else {
                        togglePause();
                    }
                    break;
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
