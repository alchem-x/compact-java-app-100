import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new MemoryCard().setVisible(true);
    });
}

static class MemoryCard extends JFrame {
    private static final int GRID_SIZES[] = {4, 6, 8}; // 支持的网格大小
    private static final int CARD_SIZE = 100;
    private static final int CARD_MARGIN = 10;

    private Card[][] cards;
    private Card firstSelected;
    private Card secondSelected;
    private int gridSize;
    private int totalPairs;
    private int matchedPairs;
    private int attempts;
    private long startTime;
    private boolean gameWon;

    private GamePanel gamePanel;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private JLabel attemptsLabel;
    private JComboBox<String> difficultyCombo;
    private JButton newGameButton;
    private JButton hintButton;

    // 卡片符号
    private static final String[] CARD_SYMBOLS = {
        "🍎", "🍌", "🍇", "🍓", "🍒", "🍑", "🍍", "🥝",
        "🍋", "🍉", "🍈", "🥭", "🍅", "🥕", "🌽", "🥒",
        "🍆", "🥦", "🥬", "🌶️", "🧄", "🧅", "🍄", "🥜",
        "🌰", "🥥", "🍯", "🥐", "🍞", "🥖", "🧀", "🥚"
    };

    public MemoryCard() {
        gridSize = GRID_SIZES[0]; // 默认4x4
        totalPairs = (gridSize * gridSize) / 2;
        matchedPairs = 0;
        attempts = 0;
        gameWon = false;

        initializeGUI();
        startNewGame();
    }

    private void initializeGUI() {
        setTitle("记忆翻牌 (Memory Card)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppleDesign.SECONDARY_SYSTEM_BACKGROUND);

        // 顶部控制面板
        var controlPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, AppleDesign.LARGE_SPACING, AppleDesign.MEDIUM_SPACING));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        // 标题
        var titleLabel = new JLabel("🧠 记忆翻牌 (Memory Card)");
        titleLabel.setFont(AppleDesign.HEADLINE_FONT);
        titleLabel.setForeground(AppleDesign.SYSTEM_INDIGO);
        controlPanel.add(titleLabel);

        // 难度选择
        var difficultyLabel = new JLabel("难度:");
        difficultyLabel.setFont(AppleDesign.BODY_FONT);
        difficultyLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        controlPanel.add(difficultyLabel);

        String[] difficulties = {"简单 (4x4)", "中等 (6x6)", "困难 (8x8)"};
        difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.setFont(AppleDesign.CALLOUT_FONT);
        difficultyCombo.setBackground(AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        difficultyCombo.setSelectedIndex(0);
        difficultyCombo.addActionListener(e -> {
            int selectedIndex = difficultyCombo.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < GRID_SIZES.length) {
                gridSize = GRID_SIZES[selectedIndex];
                startNewGame();
            }
        });
        controlPanel.add(difficultyCombo);

        // 新游戏按钮
        newGameButton = new JButton("🔄 新游戏");
        AppleDesign.styleButton(newGameButton, AppleDesign.SYSTEM_GREEN, Color.WHITE);
        newGameButton.addActionListener(e -> startNewGame());
        controlPanel.add(newGameButton);

        // 提示按钮
        hintButton = new JButton("💡 提示");
        AppleDesign.styleButton(hintButton, AppleDesign.SYSTEM_ORANGE, Color.WHITE);
        hintButton.addActionListener(e -> showHint());
        controlPanel.add(hintButton);

        add(controlPanel, BorderLayout.NORTH);

        // 游戏面板
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // 状态面板
        var statusPanel = AppleDesign.createRoundedPanel(AppleDesign.MEDIUM_RADIUS, AppleDesign.TERTIARY_SYSTEM_BACKGROUND);
        statusPanel.setLayout(new GridLayout(2, 2, AppleDesign.LARGE_SPACING, AppleDesign.MEDIUM_SPACING));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING, AppleDesign.LARGE_SPACING));

        // 匹配对数
        scoreLabel = new JLabel("匹配: 0 / " + totalPairs);
        scoreLabel.setFont(AppleDesign.TITLE_FONT);
        scoreLabel.setForeground(AppleDesign.SYSTEM_GREEN);
        statusPanel.add(scoreLabel);

        // 尝试次数
        attemptsLabel = new JLabel("尝试次数: 0");
        attemptsLabel.setFont(AppleDesign.TITLE_FONT);
        attemptsLabel.setForeground(AppleDesign.SYSTEM_ORANGE);
        statusPanel.add(attemptsLabel);

        // 时间
        timeLabel = new JLabel("时间: 00:00");
        timeLabel.setFont(AppleDesign.TITLE_FONT);
        timeLabel.setForeground(AppleDesign.SYSTEM_PURPLE);
        statusPanel.add(timeLabel);

        // 游戏状态
        statusLabel = new JLabel("开始游戏...");
        statusLabel.setFont(AppleDesign.BODY_FONT);
        statusLabel.setForeground(AppleDesign.SYSTEM_GRAY);
        statusPanel.add(statusLabel);

        add(statusPanel, BorderLayout.SOUTH);

        // 设置窗口
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // 启动时间更新定时器
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> updateTime());
        timer.start();
    }

    private void startNewGame() {
        gridSize = GRID_SIZES[difficultyCombo.getSelectedIndex()];
        totalPairs = (gridSize * gridSize) / 2;
        matchedPairs = 0;
        attempts = 0;
        gameWon = false;
        firstSelected = null;
        secondSelected = null;
        startTime = System.currentTimeMillis();

        initializeCards();
        shuffleCards();
        updateDisplay();
        statusLabel.setText("开始游戏...");
        gamePanel.repaint();
    }

    private void initializeCards() {
        cards = new Card[gridSize][gridSize];
        java.util.List<String> symbols = new ArrayList<>();

        // 为每对卡片选择符号
        for (int i = 0; i < totalPairs; i++) {
            symbols.add(CARD_SYMBOLS[i % CARD_SYMBOLS.length]);
            symbols.add(CARD_SYMBOLS[i % CARD_SYMBOLS.length]);
        }

        // 创建卡片数组
        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                cards[i][j] = new Card(symbols.get(index), i, j);
                index++;
            }
        }
    }

    private void shuffleCards() {
        java.util.List<Card> cardList = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                cardList.add(cards[i][j]);
            }
        }

        Collections.shuffle(cardList);

        // 重新分配到数组
        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Card card = cardList.get(index);
                card.row = i;
                card.col = j;
                cards[i][j] = card;
                index++;
            }
        }
    }

    private void updateDisplay() {
        scoreLabel.setText("匹配: " + matchedPairs + " / " + totalPairs);
        attemptsLabel.setText("尝试次数: " + attempts);

        if (gameWon) {
            statusLabel.setText("🎉 恭喜！游戏完成！");
        } else if (firstSelected != null && secondSelected == null) {
            statusLabel.setText("选择第二张卡片...");
        } else {
            statusLabel.setText("选择两张相同的卡片");
        }
    }

    private void updateTime() {
        if (startTime > 0 && !gameWon) {
            long elapsed = System.currentTimeMillis() - startTime;
            long minutes = elapsed / 60000;
            long seconds = (elapsed % 60000) / 1000;
            timeLabel.setText(String.format("时间: %02d:%02d", minutes, seconds));
        }
    }

    private void handleCardClick(int row, int col) {
        if (gameWon || row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
            return;
        }

        Card clickedCard = cards[row][col];

        // 检查卡片状态
        if (clickedCard.isMatched || clickedCard.isFlipped) {
            return;
        }

        // 翻转卡片
        clickedCard.isFlipped = true;
        gamePanel.repaint();

        // 处理选择逻辑
        if (firstSelected == null) {
            firstSelected = clickedCard;
            statusLabel.setText("选择第二张卡片...");
        } else if (secondSelected == null && clickedCard != firstSelected) {
            secondSelected = clickedCard;
            attempts++;
            updateDisplay();

            // 检查是否匹配
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
                checkMatch();
                ((javax.swing.Timer) e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void checkMatch() {
        if (firstSelected == null || secondSelected == null) {
            return;
        }

        if (firstSelected.symbol.equals(secondSelected.symbol)) {
            // 匹配成功
            firstSelected.isMatched = true;
            secondSelected.isMatched = true;
            matchedPairs++;
            statusLabel.setText("匹配成功！");

            // 检查是否获胜
            if (matchedPairs == totalPairs) {
                gameWon = true;
                long totalTime = (System.currentTimeMillis() - startTime) / 1000;
                statusLabel.setText("🎉 恭喜完成！用时 " + totalTime + " 秒");

                // 显示胜利消息
                javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {
                    showVictoryMessage();
                    ((javax.swing.Timer) e.getSource()).stop();
                });
                timer.setRepeats(false);
                timer.start();
            }
        } else {
            // 匹配失败，翻回背面
            firstSelected.isFlipped = false;
            secondSelected.isFlipped = false;
            statusLabel.setText("不匹配，再试一次！");
        }

        // 清除选择
        firstSelected = null;
        secondSelected = null;

        updateDisplay();
        gamePanel.repaint();
    }

    private void showVictoryMessage() {
        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        int rating = calculateRating(attempts, totalTime);
        String ratingText = getRatingText(rating);

        String message = String.format(
            "🎉 恭喜完成！\n\n" +
            "用时: %d 秒\n" +
            "尝试次数: %d\n" +
            "难度: %s\n" +
            "评级: %s\n\n" +
            "是否开始新游戏？",
            totalTime, attempts, difficultyCombo.getSelectedItem(), ratingText
        );

        int result = JOptionPane.showConfirmDialog(this, message, "游戏完成",
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            startNewGame();
        }
    }

    private int calculateRating(int attempts, long time) {
        int baseRating = 3; // 基础3星

        // 根据尝试次数调整
        int maxAttempts = totalPairs * 3;
        if (attempts <= totalPairs * 1.5) {
            baseRating++;
        } else if (attempts > maxAttempts) {
            baseRating--;
        }

        // 根据时间调整
        int timeLimit = totalPairs * 3; // 每对3秒
        if (time <= timeLimit) {
            baseRating++;
        } else if (time > timeLimit * 2) {
            baseRating--;
        }

        return Math.max(1, Math.min(5, baseRating));
    }

    private String getRatingText(int rating) {
        String[] stars = {"⭐", "⭐⭐", "⭐⭐⭐", "⭐⭐⭐⭐", "⭐⭐⭐⭐⭐"};
        return stars[rating - 1];
    }

    private void showHint() {
        if (gameWon || firstSelected == null) {
            return;
        }

        // 找到匹配的卡片
        Card match = findMatchingCard(firstSelected);
        if (match != null) {
            // 高亮显示匹配的卡片
            match.isHinted = true;
            gamePanel.repaint();

            // 2秒后移除高亮
            javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
                match.isHinted = false;
                gamePanel.repaint();
                ((javax.swing.Timer) e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();

            statusLabel.setText("提示：匹配的卡片已高亮显示");
        }
    }

    private Card findMatchingCard(Card card) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Card c = cards[i][j];
                if (c != card && !c.isFlipped && !c.isMatched &&
                    c.symbol.equals(card.symbol)) {
                    return c;
                }
            }
        }
        return null;
    }

    // 游戏面板
    class GamePanel extends JPanel {
        public GamePanel() {
            setBackground(new Color(0x2C3E50)); // 深蓝色背景
            setBorder(BorderFactory.createLineBorder(new Color(0x34495E), 2));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleMouseClick(e);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGame(g);
        }

        private void drawGame(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (cards == null) return;

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // 计算卡片区域的尺寸
            int totalWidth = gridSize * CARD_SIZE + (gridSize - 1) * CARD_MARGIN;
            int totalHeight = gridSize * CARD_SIZE + (gridSize - 1) * CARD_MARGIN;

            int startX = (panelWidth - totalWidth) / 2;
            int startY = (panelHeight - totalHeight) / 2;

            // 绘制卡片
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    int x = startX + j * (CARD_SIZE + CARD_MARGIN);
                    int y = startY + i * (CARD_SIZE + CARD_MARGIN);
                    drawCard(g2d, cards[i][j], x, y);
                }
            }
        }

        private void drawCard(Graphics2D g2d, Card card, int x, int y) {
            // 卡片阴影
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRoundRect(x + 3, y + 3, CARD_SIZE, CARD_SIZE, 15, 15);

            if (card.isFlipped || card.isMatched) {
                // 绘制卡片正面
                drawCardFront(g2d, card, x, y);
            } else {
                // 绘制卡片背面
                drawCardBack(g2d, card, x, y);
            }

            // 提示高亮
            if (card.isHinted) {
                drawHintHighlight(g2d, x, y);
            }
        }

        private void drawCardFront(Graphics2D g2d, Card card, int x, int y) {
            // 卡片背景
            if (card.isMatched) {
                g2d.setColor(new Color(0x27AE60)); // 匹配成功的绿色
            } else {
                g2d.setColor(new Color(0xECF0F1)); // 白色
            }
            g2d.fillRoundRect(x, y, CARD_SIZE, CARD_SIZE, 15, 15);

            // 卡片边框
            g2d.setColor(card.isMatched ? new Color(0x229954) : new Color(0xBDC3C7));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(x, y, CARD_SIZE, CARD_SIZE, 15, 15);

            // 绘制符号
            g2d.setColor(card.isMatched ? Color.WHITE : new Color(0x2C3E50));
            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(card.symbol);
            int textHeight = fm.getAscent();
            g2d.drawString(card.symbol, x + (CARD_SIZE - textWidth) / 2,
                           y + (CARD_SIZE + textHeight) / 2 - 5);
        }

        private void drawCardBack(Graphics2D g2d, Card card, int x, int y) {
            // 卡片背景
            g2d.setColor(new Color(0x3498DB)); // 蓝色
            g2d.fillRoundRect(x, y, CARD_SIZE, CARD_SIZE, 15, 15);

            // 卡片边框
            g2d.setColor(new Color(0x2980B9));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(x, y, CARD_SIZE, CARD_SIZE, 15, 15);

            // 背面图案
            g2d.setColor(new Color(0x2980B9));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String backSymbol = "🧠";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(backSymbol);
            int textHeight = fm.getAscent();
            g2d.drawString(backSymbol, x + (CARD_SIZE - textWidth) / 2,
                           y + (CARD_SIZE + textHeight) / 2 - 5);

            // 装饰图案
            g2d.setColor(new Color(0x5DADE2));
            for (int i = 0; i < 4; i++) {
                int cx = x + CARD_SIZE / 2 + (int)(Math.cos(i * Math.PI / 2) * 25);
                int cy = y + CARD_SIZE / 2 + (int)(Math.sin(i * Math.PI / 2) * 25);
                g2d.fillOval(cx - 3, cy - 3, 6, 6);
            }
        }

        private void drawHintHighlight(Graphics2D g2d, int x, int y) {
            g2d.setColor(new Color(0xF1C40F)); // 黄色
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRoundRect(x - 2, y - 2, CARD_SIZE + 4, CARD_SIZE + 4, 17, 17);

            // 脉冲效果
            g2d.setColor(new Color(0xF39C12));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x - 4, y - 4, CARD_SIZE + 8, CARD_SIZE + 8, 19, 19);
        }

        private void handleMouseClick(MouseEvent e) {
            if (gameWon) return;

            int x = e.getX();
            int y = e.getY();

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int totalWidth = gridSize * CARD_SIZE + (gridSize - 1) * CARD_MARGIN;
            int totalHeight = gridSize * CARD_SIZE + (gridSize - 1) * CARD_MARGIN;

            int startX = (panelWidth - totalWidth) / 2;
            int startY = (panelHeight - totalHeight) / 2;

            // 检查点击位置
            if (x >= startX && x < startX + totalWidth && y >= startY && y < startY + totalHeight) {
                int col = (x - startX) / (CARD_SIZE + CARD_MARGIN);
                int row = (y - startY) / (CARD_SIZE + CARD_MARGIN);

                // 边界检查
                if (col < gridSize && row < gridSize) {
                    handleCardClick(row, col);
                }
            }
        }
    }

    // 卡片类
    static class Card {
        String symbol;
        int row, col;
        boolean isFlipped;
        boolean isMatched;
        boolean isHinted;

        public Card(String symbol, int row, int col) {
            this.symbol = symbol;
            this.row = row;
            this.col = col;
            this.isFlipped = false;
            this.isMatched = false;
            this.isHinted = false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MemoryCard().setVisible(true);
        });
    }
}