import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    }

    // JToggleButton重载方法
    static void styleButton(JToggleButton button, Color bgColor, Color fgColor) {
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

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new TicTacToe().setVisible(true);
    });
}

static class TicTacToe extends JFrame implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private boolean playerXTurn = true;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private int xWins = 0;
    private int oWins = 0;
    private int draws = 0;
    private boolean gameEnded = false;
    private boolean vsComputer = false;

    public TicTacToe() {
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("井字棋游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);

        // 顶部面板
        var topPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        var titleLabel = new JLabel("🎯 井字棋", JLabel.CENTER);
        titleLabel.setFont(AppleDesign.HEADLINE_FONT);
        titleLabel.setForeground(AppleDesign.SYSTEM_BLUE);

        statusLabel = new JLabel("玩家 X 的回合", JLabel.CENTER);
        statusLabel.setFont(AppleDesign.TITLE_FONT);
        statusLabel.setForeground(AppleDesign.SYSTEM_BLUE);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statusLabel, BorderLayout.SOUTH);

        // 游戏面板
        var gamePanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        gamePanel.setLayout(new GridLayout(3, 3, AppleDesign.MEDIUM_SPACING, AppleDesign.MEDIUM_SPACING));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        // 创建按钮
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(AppleDesign.HEADLINE_FONT);
                buttons[i][j].setPreferredSize(new Dimension(100, 100));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
                buttons[i][j].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppleDesign.SYSTEM_GRAY4, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                buttons[i][j].setCursor(new Cursor(Cursor.HAND_CURSOR));
                buttons[i][j].addActionListener(this);
                gamePanel.add(buttons[i][j]);
            }
        }

        // 底部控制面板
        var bottomPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        // 分数面板
        scoreLabel = new JLabel(getScoreText(), JLabel.CENTER);
        scoreLabel.setFont(AppleDesign.BODY_FONT);
        scoreLabel.setForeground(AppleDesign.SYSTEM_GRAY);

        // 按钮面板
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, AppleDesign.MEDIUM_SPACING, 0));
        buttonPanel.setOpaque(false);

        var newGameButton = new JButton("🎮 新游戏");
        AppleDesign.styleButton(newGameButton, AppleDesign.SYSTEM_BLUE, Color.WHITE);
        newGameButton.addActionListener(e -> newGame());

        var resetScoreButton = new JButton("🔄 重置分数");
        AppleDesign.styleButton(resetScoreButton, AppleDesign.SYSTEM_ORANGE, Color.WHITE);
        resetScoreButton.addActionListener(e -> resetScore());

        var modeButton = new JToggleButton("👤 对战电脑");
        AppleDesign.styleButton(modeButton, AppleDesign.SYSTEM_PURPLE, Color.WHITE);
        modeButton.addActionListener(e -> {
            vsComputer = modeButton.isSelected();
            modeButton.setText(vsComputer ? "👥 双人对战" : "👤 对战电脑");
            newGame();
        });

        buttonPanel.add(newGameButton);
        buttonPanel.add(resetScoreButton);
        buttonPanel.add(modeButton);

        bottomPanel.add(scoreLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(400, 500);
        setLocationRelativeTo(null);
    }

    private String getScoreText() {
        return String.format("X: %d胜  O: %d胜  平局: %d", xWins, oWins, draws);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameEnded) return;

        JButton clickedButton = (JButton) e.getSource();
        
        // 找到被点击的按钮位置
        int row = -1, col = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j] == clickedButton) {
                    row = i;
                    col = j;
                    break;
                }
            }
        }

        // 如果位置已被占用，返回
        if (!clickedButton.getText().equals("")) {
            return;
        }

        // 玩家移动
        makeMove(row, col, playerXTurn ? "X" : "O");

        // 检查游戏状态
        if (checkWinner()) {
            return;
        }

        // 电脑移动（如果是对战电脑模式且轮到O）
        if (vsComputer && !playerXTurn && !gameEnded) {
            Timer computerMoveTimer = new Timer(500, ev -> {
                makeComputerMove();
                checkWinner();
                ((Timer) ev.getSource()).stop();
            });
            computerMoveTimer.setRepeats(false);
            computerMoveTimer.start();
        }
    }

    private void makeMove(int row, int col, String player) {
        buttons[row][col].setText(player);
        buttons[row][col].setForeground(player.equals("X") ? AppleDesign.SYSTEM_BLUE : AppleDesign.SYSTEM_RED);

        // 添加动画效果
        buttons[row][col].setBackground(player.equals("X") ?
            AppleDesign.SYSTEM_BLUE.brighter().brighter() : AppleDesign.SYSTEM_RED.brighter().brighter());

        playerXTurn = !playerXTurn;
        updateStatusLabel();
    }

    private void makeComputerMove() {
        // 简单的AI：先尝试获胜，再尝试阻止玩家获胜，最后随机选择
        
        // 1. 尝试获胜
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText("O");
                    if (isWinningMove(i, j, "O")) {
                        buttons[i][j].setForeground(Color.RED);
                        buttons[i][j].setBackground(new Color(255, 230, 230));
                        playerXTurn = !playerXTurn;
                        updateStatusLabel();
                        return;
                    }
                    buttons[i][j].setText("");
                }
            }
        }

        // 2. 尝试阻止玩家获胜
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText("X");
                    if (isWinningMove(i, j, "X")) {
                        buttons[i][j].setText("O");
                        buttons[i][j].setForeground(Color.RED);
                        buttons[i][j].setBackground(new Color(255, 230, 230));
                        playerXTurn = !playerXTurn;
                        updateStatusLabel();
                        return;
                    }
                    buttons[i][j].setText("");
                }
            }
        }

        // 3. 选择中心位置
        if (buttons[1][1].getText().equals("")) {
            makeMove(1, 1, "O");
            return;
        }

        // 4. 选择角落
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] corner : corners) {
            if (buttons[corner[0]][corner[1]].getText().equals("")) {
                makeMove(corner[0], corner[1], "O");
                return;
            }
        }

        // 5. 随机选择剩余位置
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    makeMove(i, j, "O");
                    return;
                }
            }
        }
    }

    private boolean isWinningMove(int row, int col, String player) {
        // 检查行
        int count = 0;
        for (int j = 0; j < 3; j++) {
            if (buttons[row][j].getText().equals(player)) count++;
        }
        if (count == 3) return true;

        // 检查列
        count = 0;
        for (int i = 0; i < 3; i++) {
            if (buttons[i][col].getText().equals(player)) count++;
        }
        if (count == 3) return true;

        // 检查主对角线
        if (row == col) {
            count = 0;
            for (int i = 0; i < 3; i++) {
                if (buttons[i][i].getText().equals(player)) count++;
            }
            if (count == 3) return true;
        }

        // 检查副对角线
        if (row + col == 2) {
            count = 0;
            for (int i = 0; i < 3; i++) {
                if (buttons[i][2-i].getText().equals(player)) count++;
            }
            if (count == 3) return true;
        }

        return false;
    }

    private boolean checkWinner() {
        // 检查行
        for (int i = 0; i < 3; i++) {
            if (checkLine(buttons[i][0], buttons[i][1], buttons[i][2])) {
                highlightWinningLine(new int[][]{{i,0}, {i,1}, {i,2}});
                announceWinner(buttons[i][0].getText());
                return true;
            }
        }

        // 检查列
        for (int j = 0; j < 3; j++) {
            if (checkLine(buttons[0][j], buttons[1][j], buttons[2][j])) {
                highlightWinningLine(new int[][]{{0,j}, {1,j}, {2,j}});
                announceWinner(buttons[0][j].getText());
                return true;
            }
        }

        // 检查对角线
        if (checkLine(buttons[0][0], buttons[1][1], buttons[2][2])) {
            highlightWinningLine(new int[][]{{0,0}, {1,1}, {2,2}});
            announceWinner(buttons[0][0].getText());
            return true;
        }

        if (checkLine(buttons[0][2], buttons[1][1], buttons[2][0])) {
            highlightWinningLine(new int[][]{{0,2}, {1,1}, {2,0}});
            announceWinner(buttons[0][2].getText());
            return true;
        }

        // 检查平局
        if (isBoardFull()) {
            draws++;
            scoreLabel.setText(getScoreText());
            statusLabel.setText("平局！");
            statusLabel.setForeground(Color.ORANGE);
            gameEnded = true;
            
            Timer resetTimer = new Timer(2000, e -> {
                newGame();
                ((javax.swing.Timer) e.getSource()).stop();
            });
            resetTimer.setRepeats(false);
            resetTimer.start();
            return true;
        }

        return false;
    }

    private boolean checkLine(JButton b1, JButton b2, JButton b3) {
        return !b1.getText().equals("") && 
               b1.getText().equals(b2.getText()) && 
               b2.getText().equals(b3.getText());
    }

    private void highlightWinningLine(int[][] positions) {
        for (int[] pos : positions) {
            buttons[pos[0]][pos[1]].setBackground(Color.YELLOW);
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void announceWinner(String winner) {
        if (winner.equals("X")) {
            xWins++;
            statusLabel.setText("🎉 玩家 X 获胜！");
            statusLabel.setForeground(AppleDesign.SYSTEM_BLUE);
        } else {
            oWins++;
            String winnerText = vsComputer ? "🤖 电脑获胜！" : "🎉 玩家 O 获胜！";
            statusLabel.setText(winnerText);
            statusLabel.setForeground(AppleDesign.SYSTEM_RED);
        }

        scoreLabel.setText(getScoreText());
        gameEnded = true;

        // 2秒后自动开始新游戏
        Timer resetTimer = new Timer(2000, e -> {
            newGame();
            ((javax.swing.Timer) e.getSource()).stop();
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    private void updateStatusLabel() {
        if (!gameEnded) {
            if (vsComputer) {
                statusLabel.setText(playerXTurn ? "你的回合 (X)" : "电脑思考中...");
                statusLabel.setForeground(playerXTurn ? AppleDesign.SYSTEM_BLUE : AppleDesign.SYSTEM_RED);
            } else {
                statusLabel.setText(playerXTurn ? "玩家 X 的回合" : "玩家 O 的回合");
                statusLabel.setForeground(playerXTurn ? AppleDesign.SYSTEM_BLUE : AppleDesign.SYSTEM_RED);
            }
        }
    }

    private void newGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setForeground(Color.BLACK);
            }
        }
        playerXTurn = true;
        gameEnded = false;
        updateStatusLabel();
    }

    private void resetScore() {
        xWins = 0;
        oWins = 0;
        draws = 0;
        scoreLabel.setText(getScoreText());
        newGame();
    }
}
