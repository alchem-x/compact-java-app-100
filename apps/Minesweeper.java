import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.Random;

// Apple 设计系统常量
class AppleDesign {
    // 颜色系统
    static final Color SYSTEM_BLUE = new Color(0, 122, 255);
    static final Color SYSTEM_GREEN = new Color(52, 199, 89);
    static final Color SYSTEM_INDIGO = new Color(88, 86, 214);
    static final Color SYSTEM_ORANGE = new Color(255, 149, 0);
    static final Color SYSTEM_PINK = new Color(255, 45, 85);
    static final Color SYSTEM_PURPLE = new Color(175, 82, 222);
    static final Color SYSTEM_RED = new Color(255, 59, 48);
    static final Color SYSTEM_TEAL = new Color(90, 200, 250);
    static final Color SYSTEM_YELLOW = new Color(255, 204, 0);

    // 灰色系统
    static final Color SYSTEM_GRAY = new Color(142, 142, 147);
    static final Color SYSTEM_GRAY2 = new Color(174, 174, 178);
    static final Color SYSTEM_GRAY3 = new Color(199, 199, 204);
    static final Color SYSTEM_GRAY4 = new Color(209, 209, 214);
    static final Color SYSTEM_GRAY5 = new Color(229, 229, 234);
    static final Color SYSTEM_GRAY6 = new Color(242, 242, 247);

    // 背景色
    static final Color SECONDARY_SYSTEM_BACKGROUND = new Color(247, 247, 247);
    static final Color TERTIARY_SYSTEM_BACKGROUND = new Color(255, 255, 255);

    // 字体系统
    static final Font HEADLINE_FONT = new Font("SF Pro Display", Font.BOLD, 24);
    static final Font TITLE_FONT = new Font("SF Pro Display", Font.BOLD, 18);
    static final Font BODY_FONT = new Font("SF Pro Text", Font.PLAIN, 14);
    static final Font CALLOUT_FONT = new Font("SF Pro Text", Font.PLAIN, 12);
    static final Font CAPTION_FONT = new Font("SF Pro Text", Font.PLAIN, 11);
    static final Font MONO_FONT = new Font("SF Mono", Font.PLAIN, 12);

    // 间距系统
    static final int SMALL_SPACING = 4;
    static final int MEDIUM_SPACING = 8;
    static final int LARGE_SPACING = 16;
    static final int EXTRA_LARGE_SPACING = 20;

    // 圆角系统
    static final int SMALL_RADIUS = 4;
    static final int MEDIUM_RADIUS = 8;
    static final int LARGE_RADIUS = 12;
    static final int EXTRA_LARGE_RADIUS = 16;

    // 按钮样式
    static void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(BODY_FONT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    // 创建圆角面板
    static JPanel createRoundedPanel(int radius, Color bgColor) {
        var panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                var g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }
}

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "💣 扫雷游戏";

    // 界面标签
    static final String MINE_COUNT_FORMAT = "💣 %d";
    static final String TIMER_FORMAT = "⏱️ %03d";
    static final String NEW_GAME_BUTTON = "😊";

    // 菜单文本
    static final String GAME_MENU = "游戏";
    static final String BEGINNER_LEVEL = "初级 (9x9, 10雷)";
    static final String INTERMEDIATE_LEVEL = "中级 (16x16, 40雷)";
    static final String EXPERT_LEVEL = "高级 (16x30, 99雷)";
    static final String CUSTOM_LEVEL = "自定义...";

    // 状态消息
    static final String GAME_WON_MESSAGE = "恭喜你赢了！";
    static final String GAME_LOST_MESSAGE = "游戏结束！你踩到地雷了！";
    static final String CUSTOM_GAME_TITLE = "自定义游戏";
    static final String CUSTOM_ROWS_LABEL = "行数:";
    static final String CUSTOM_COLS_LABEL = "列数:";
    static final String CUSTOM_MINES_LABEL = "地雷数:";
    static final String CUSTOM_OK_BUTTON = "确定";
    static final String CUSTOM_CANCEL_BUTTON = "取消";

    // 帮助信息
    static final String HELP_MESSAGE = """
        扫雷游戏使用说明：

        • 游戏目标：找出所有隐藏的地雷，避免踩到它们
        • 游戏规则：左键点击揭开方块，右键标记地雷
        • 数字提示：数字表示周围8个方块中地雷的数量
        • 胜利条件：揭开所有非地雷方块
        • 失败条件：揭开地雷方块

        操作说明：
        • 左键点击：揭开方块
        • 右键点击：标记/取消标记地雷
        • 中键点击：快速揭开周围方块（仅当数字匹配标记数时）

        游戏技巧：
        • 从角落开始，安全性更高
        • 优先揭开数字为0的方块
        • 使用逻辑推理确定地雷位置
        • 标记确定的地雷，避免误点
        • 注意数字周围的空格数

        难度等级：
        • 初级：9×9网格，10个地雷
        • 中级：16×16网格，40个地雷
        • 高级：16×30网格，99个地雷

        快捷键：
        左键 - 揭开方块
        右键 - 标记地雷
        Ctrl+N - 新游戏
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Minesweeper().setVisible(true);
    });
}

static class Minesweeper extends JFrame {
    private static final int BEGINNER_ROWS = 9;
    private static final int BEGINNER_COLS = 9;
    private static final int BEGINNER_MINES = 10;
    
    private static final int INTERMEDIATE_ROWS = 16;
    private static final int INTERMEDIATE_COLS = 16;
    private static final int INTERMEDIATE_MINES = 40;
    
    private static final int EXPERT_ROWS = 16;
    private static final int EXPERT_COLS = 30;
    private static final int EXPERT_MINES = 99;
    
    private int rows, cols, totalMines;
    private MineButton[][] buttons;
    private boolean[][] mines;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private int[][] numbers;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private int flagCount = 0;
    private JLabel mineCountLabel;
    private JLabel timerLabel;
    private Timer gameTimer;
    private int secondsElapsed = 0;
    private boolean firstClick = true;

    class MineButton extends JButton {
        int row, col;

        MineButton(int row, int col) {
            this.row = row;
            this.col = col;
            setPreferredSize(new Dimension(25, 25));
            setFont(AppleDesign.MONO_FONT);
            setMargin(new Insets(0, 0, 0, 0));
            setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppleDesign.SYSTEM_GRAY4, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (gameOver || gameWon) return;

                    if (SwingUtilities.isRightMouseButton(e)) {
                        toggleFlag(row, col);
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        if (firstClick) {
                            firstClick = false;
                            placeMines(row, col);
                            calculateNumbers();
                            startTimer();
                        }
                        revealCell(row, col);
                    }
                }
            });
        }
    }
    
    public Minesweeper() {
        initializeGame(BEGINNER_ROWS, BEGINNER_COLS, BEGINNER_MINES);
        setupKeyboardShortcuts();
    }
    
    private void initializeGame(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.totalMines = mines;
        this.flagCount = 0;
        this.secondsElapsed = 0;
        this.gameOver = false;
        this.gameWon = false;
        this.firstClick = true;
        
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        initializeGUI();
        initializeArrays();
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);
        
        // 顶部信息面板
        var topPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        mineCountLabel = new JLabel(String.format(Texts.MINE_COUNT_FORMAT, totalMines));
        mineCountLabel.setFont(AppleDesign.TITLE_FONT);
        mineCountLabel.setForeground(AppleDesign.SYSTEM_RED);

        timerLabel = new JLabel(String.format(Texts.TIMER_FORMAT, 0));
        timerLabel.setFont(AppleDesign.TITLE_FONT);
        timerLabel.setForeground(AppleDesign.SYSTEM_BLUE);

        var newGameButton = new JButton(Texts.NEW_GAME_BUTTON);
        AppleDesign.styleButton(newGameButton, AppleDesign.SYSTEM_YELLOW, Color.BLACK);
        newGameButton.setFont(AppleDesign.HEADLINE_FONT);
        newGameButton.setPreferredSize(new Dimension(50, 30));
        newGameButton.addActionListener(e -> restartGame());
        
        topPanel.add(mineCountLabel, BorderLayout.WEST);
        topPanel.add(newGameButton, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.EAST);
        
        // 游戏面板
        var gamePanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        gamePanel.setLayout(new GridLayout(rows, cols, 1, 1));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        
        buttons = new MineButton[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j] = new MineButton(i, j);
                gamePanel.add(buttons[i][j]);
            }
        }
        
        // 菜单栏
        createMenuBar();
        
        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void createMenuBar() {
        var menuBar = new JMenuBar();
        menuBar.setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);

        var gameMenu = new JMenu(Texts.GAME_MENU);
        gameMenu.setFont(AppleDesign.BODY_FONT);

        var beginnerItem = new JMenuItem(Texts.BEGINNER_LEVEL);
        beginnerItem.setFont(AppleDesign.CALLOUT_FONT);
        beginnerItem.addActionListener(e -> initializeGame(BEGINNER_ROWS, BEGINNER_COLS, BEGINNER_MINES));

        var intermediateItem = new JMenuItem(Texts.INTERMEDIATE_LEVEL);
        intermediateItem.setFont(AppleDesign.CALLOUT_FONT);
        intermediateItem.addActionListener(e -> initializeGame(INTERMEDIATE_ROWS, INTERMEDIATE_COLS, INTERMEDIATE_MINES));

        var expertItem = new JMenuItem(Texts.EXPERT_LEVEL);
        expertItem.setFont(AppleDesign.CALLOUT_FONT);
        expertItem.addActionListener(e -> initializeGame(EXPERT_ROWS, EXPERT_COLS, EXPERT_MINES));

        var customItem = new JMenuItem(Texts.CUSTOM_LEVEL);
        customItem.setFont(AppleDesign.CALLOUT_FONT);
        customItem.addActionListener(this::showCustomDialog);

        gameMenu.add(beginnerItem);
        gameMenu.add(intermediateItem);
        gameMenu.add(expertItem);
        gameMenu.addSeparator();
        gameMenu.add(customItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }
    
    private void showCustomDialog(java.awt.event.ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        JSpinner rowSpinner = new JSpinner(new SpinnerNumberModel(9, 5, 30, 1));
        JSpinner colSpinner = new JSpinner(new SpinnerNumberModel(9, 5, 30, 1));
        JSpinner mineSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 200, 1));
        
        panel.add(new JLabel(Texts.CUSTOM_ROWS_LABEL));
        panel.add(rowSpinner);
        panel.add(new JLabel(Texts.CUSTOM_COLS_LABEL));
        panel.add(colSpinner);
        panel.add(new JLabel(Texts.CUSTOM_MINES_LABEL));
        panel.add(mineSpinner);

        int result = JOptionPane.showConfirmDialog(this, panel, Texts.CUSTOM_GAME_TITLE, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int customRows = (Integer) rowSpinner.getValue();
            int customCols = (Integer) colSpinner.getValue();
            int customMines = (Integer) mineSpinner.getValue();
            
            // 验证地雷数不能超过格子总数
            if (customMines >= customRows * customCols) {
                JOptionPane.showMessageDialog(this, "地雷数不能超过格子总数！");
                return;
            }
            
            initializeGame(customRows, customCols, customMines);
        }
    }
    
    private void initializeArrays() {
        mines = new boolean[rows][cols];
        revealed = new boolean[rows][cols];
        flagged = new boolean[rows][cols];
        numbers = new int[rows][cols];
        
        // 重置按钮状态
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackground(null);
                buttons[i][j].setEnabled(true);
                buttons[i][j].setIcon(null);
            }
        }
        
        updateMineCount();
    }
    
    private void placeMines(int firstClickRow, int firstClickCol) {
        Random random = new Random();
        int minesPlaced = 0;
        
        while (minesPlaced < totalMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);
            
            // 不在第一次点击的位置及其周围放置地雷
            if (!mines[row][col] && !isAdjacent(row, col, firstClickRow, firstClickCol)) {
                mines[row][col] = true;
                minesPlaced++;
            }
        }
    }
    
    private boolean isAdjacent(int row1, int col1, int row2, int col2) {
        return Math.abs(row1 - row2) <= 1 && Math.abs(col1 - col2) <= 1;
    }
    
    private void calculateNumbers() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!mines[i][j]) {
                    numbers[i][j] = countAdjacentMines(i, j);
                }
            }
        }
    }
    
    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < cols && mines[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private void revealCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return;
        if (revealed[row][col] || flagged[row][col]) return;

        revealed[row][col] = true;
        MineButton button = buttons[row][col];

        if (mines[row][col]) {
            // 踩到地雷
            button.setText("💣");
            button.setBackground(AppleDesign.SYSTEM_RED);
            gameOver();
            return;
        }

        button.setEnabled(false);
        button.setBackground(AppleDesign.SYSTEM_GRAY5);

        if (numbers[row][col] > 0) {
            button.setText(String.valueOf(numbers[row][col]));
            button.setForeground(getNumberColor(numbers[row][col]));
        } else {
            // 空白格子，自动揭开周围格子
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    revealCell(i, j);
                }
            }
        }

        checkWinCondition();
    }
    
    private Color getNumberColor(int number) {
        switch (number) {
            case 1: return Color.BLUE;
            case 2: return Color.GREEN;
            case 3: return Color.RED;
            case 4: return new Color(128, 0, 128); // Purple
            case 5: return new Color(128, 0, 0);   // Maroon
            case 6: return new Color(64, 224, 208); // Turquoise
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
            default: return Color.BLACK;
        }
    }
    
    private void toggleFlag(int row, int col) {
        if (revealed[row][col]) return;

        MineButton button = buttons[row][col];

        if (flagged[row][col]) {
            flagged[row][col] = false;
            button.setText("");
            button.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
            flagCount--;
        } else {
            flagged[row][col] = true;
            button.setText("🚩");
            button.setForeground(AppleDesign.SYSTEM_RED);
            button.setBackground(AppleDesign.SYSTEM_YELLOW);
            flagCount++;
        }

        updateMineCount();
    }
    
    private void updateMineCount() {
        int remaining = totalMines - flagCount;
        mineCountLabel.setText("💣 " + String.format("%03d", remaining));
    }
    
    private void startTimer() {
        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            timerLabel.setText("⏱️ " + String.format("%03d", secondsElapsed));
        });
        gameTimer.start();
    }
    
    private void gameOver() {
        gameOver = true;
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // 显示所有地雷
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (mines[i][j] && !flagged[i][j]) {
                    buttons[i][j].setText("💣");
                    buttons[i][j].setBackground(Color.PINK);
                } else if (!mines[i][j] && flagged[i][j]) {
                    buttons[i][j].setText("❌");
                    buttons[i][j].setBackground(Color.YELLOW);
                }
            }
        }
        
        JOptionPane.showMessageDialog(this, Texts.GAME_LOST_MESSAGE, "游戏结束", JOptionPane.ERROR_MESSAGE);
    }
    
    private void checkWinCondition() {
        int revealedCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (revealed[i][j]) {
                    revealedCount++;
                }
            }
        }
        
        if (revealedCount == rows * cols - totalMines) {
            gameWon = true;
            if (gameTimer != null) {
                gameTimer.stop();
            }
            
            // 自动标记所有未标记的地雷
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (mines[i][j] && !flagged[i][j]) {
                        flagged[i][j] = true;
                        buttons[i][j].setText("🚩");
                        buttons[i][j].setForeground(Color.RED);
                        flagCount++;
                    }
                }
            }
            
            updateMineCount();
            
            String message = String.format(Texts.GAME_WON_MESSAGE + "\n用时: %d 秒", secondsElapsed);
            JOptionPane.showMessageDialog(this, message, "胜利！", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void restartGame() {
        initializeGame(rows, cols, totalMines);
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_N:
                        // Ctrl+N 新游戏
                        if (ev.isControlDown()) {
                            restartGame();
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
