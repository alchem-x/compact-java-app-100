import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Solitaire().setVisible(true);
    });
}

static class Solitaire extends JFrame {
    private static final int CARD_WIDTH = 71;
    private static final int CARD_HEIGHT = 96;
    private static final int SUIT_WIDTH = 20;
    private static final int SUIT_HEIGHT = 20;

    // 花色
    private static final int HEARTS = 0;
    private static final int DIAMONDS = 1;
    private static final int CLUBS = 2;
    private static final int SPADES = 3;

    // 游戏区域
    private static final int STOCK_X = 10;
    private static final int STOCK_Y = 10;
    private static final int WASTE_X = 90;
    private static final int WASTE_Y = 10;
    private static final int FOUNDATION_X = 300;
    private static final int FOUNDATION_Y = 10;
    private static final int TABLEAU_X = 10;
    private static final int TABLEAU_Y = 130;
    private static final int TABLEAU_SPACING = 15;

    private List<Card> deck;
    private Stack<Card> stock;
    private Stack<Card> waste;
    private List<List<Card>> foundations;
    private List<List<Card>> tableaus;
    private boolean gameWon;
    private int score;
    private long startTime;

    private GamePanel gamePanel;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private JLabel statusLabel;
    private JButton newGameButton;
    private JButton autoPlayButton;
    private JButton hintButton;

    private Card selectedCard;
    private Point dragOffset;
    private Point dragPosition;
    private boolean isDragging;

    public Solitaire() {
        deck = new ArrayList<>();
        stock = new Stack<>();
        waste = new Stack<>();
        foundations = new ArrayList<>();
        tableaus = new ArrayList<>();
        gameWon = false;
        score = 0;

        initializeGUI();
        startNewGame();
    }

    private void initializeGUI() {
        setTitle("纸牌接龙 (Solitaire)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("♠️♥️♣️♦️ 纸牌接龙");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        controlPanel.add(titleLabel);

        // 分数
        scoreLabel = new JLabel("分数: 0");
        scoreLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        scoreLabel.setForeground(new Color(0x27AE60));
        controlPanel.add(scoreLabel);

        // 时间
        timeLabel = new JLabel("时间: 00:00");
        timeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        timeLabel.setForeground(new Color(0x8E44AD));
        controlPanel.add(timeLabel);

        // 新游戏按钮
        newGameButton = new JButton("🔄 新游戏");
        newGameButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        newGameButton.setBackground(new Color(76, 175, 80));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.addActionListener(e -> startNewGame());
        controlPanel.add(newGameButton);

        // 自动播放按钮
        autoPlayButton = new JButton("🤖 自动播放");
        autoPlayButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        autoPlayButton.setBackground(new Color(33, 150, 243));
        autoPlayButton.setForeground(Color.WHITE);
        autoPlayButton.addActionListener(e -> autoPlay());
        controlPanel.add(autoPlayButton);

        // 提示按钮
        hintButton = new JButton("💡 提示");
        hintButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        hintButton.setBackground(new Color(255, 152, 0));
        hintButton.setForeground(Color.WHITE);
        hintButton.addActionListener(e -> showHint());
        controlPanel.add(hintButton);

        add(controlPanel, BorderLayout.NORTH);

        // 游戏面板
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // 状态面板
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("开始游戏...");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);

        statusPanel.add(statusLabel, BorderLayout.CENTER);

        add(statusPanel, BorderLayout.SOUTH);

        // 设置窗口
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        // 启动时间更新定时器
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> updateTime());
        timer.start();
    }

    private void startNewGame() {
        initializeDeck();
        shuffleDeck();
        dealCards();
        gameWon = false;
        score = 0;
        startTime = System.currentTimeMillis();
        selectedCard = null;
        isDragging = false;

        updateScore();
        updateTime();
        statusLabel.setText("开始游戏...");
        gamePanel.repaint();
    }

    private void initializeDeck() {
        deck.clear();
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = 1; rank <= 13; rank++) {
                deck.add(new Card(suit, rank));
            }
        }
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    private void dealCards() {
        stock.clear();
        waste.clear();
        foundations.clear();
        tableaus.clear();

        // 初始化基础牌堆（4个花色）
        for (int i = 0; i < 4; i++) {
            foundations.add(new ArrayList<>());
        }

        // 初始化桌面牌堆（7列）
        for (int i = 0; i < 7; i++) {
            tableaus.add(new ArrayList<>());
        }

        // 发牌到桌面
        int cardIndex = 0;
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row <= col; row++) {
                Card card = deck.get(cardIndex++);
                if (row == col) {
                    card.faceUp = true;
                }
                tableaus.get(col).add(card);
            }
        }

        // 剩余的牌放入库存
        while (cardIndex < deck.size()) {
            stock.push(deck.get(cardIndex++));
        }
    }

    private void updateScore() {
        scoreLabel.setText("分数: " + score);
    }

    private void updateTime() {
        if (startTime > 0 && !gameWon) {
            long elapsed = System.currentTimeMillis() - startTime;
            long minutes = elapsed / 60000;
            long seconds = (elapsed % 60000) / 1000;
            timeLabel.setText(String.format("时间: %02d:%02d", minutes, seconds));
        }
    }

    private void drawCardFromStock() {
        if (stock.isEmpty()) {
            // 重置库存
            while (!waste.isEmpty()) {
                Card card = waste.pop();
                card.faceUp = false;
                stock.push(card);
            }
            score = Math.max(0, score - 100); // 扣分
            updateScore();
        } else {
            Card card = stock.pop();
            card.faceUp = true;
            waste.push(card);
        }
        gamePanel.repaint();
    }

    private boolean canPlaceOnFoundation(Card card, int foundationIndex) {
        List<Card> foundation = foundations.get(foundationIndex);
        if (foundation.isEmpty()) {
            return card.rank == 1; // A
        } else {
            Card topCard = foundation.get(foundation.size() - 1);
            return card.suit == topCard.suit && card.rank == topCard.rank + 1;
        }
    }

    private boolean canPlaceOnTableau(Card card, int tableauIndex) {
        List<Card> tableau = tableaus.get(tableauIndex);
        if (tableau.isEmpty()) {
            return card.rank == 13; // K
        } else {
            Card topCard = tableau.get(tableau.size() - 1);
            return card.isRed() != topCard.isRed() && card.rank == topCard.rank - 1;
        }
    }

    private void checkWinCondition() {
        // 检查是否所有基础牌堆都完成
        for (List<Card> foundation : foundations) {
            if (foundation.size() != 13) {
                return;
            }
        }

        gameWon = true;
        statusLabel.setText("🎉 恭喜！游戏胜利！");
        JOptionPane.showMessageDialog(this, "恭喜！游戏胜利！\n分数: " + score, "胜利",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void autoPlay() {
        boolean moved = false;

        // 尝试将废牌堆的牌移动到基础牌堆
        if (!waste.isEmpty()) {
            Card wasteCard = waste.peek();
            for (int i = 0; i < 4; i++) {
                if (canPlaceOnFoundation(wasteCard, i)) {
                    waste.pop();
                    foundations.get(i).add(wasteCard);
                    score += 10;
                    moved = true;
                    break;
                }
            }
        }

        // 尝试将桌面牌堆的牌移动到基础牌堆
        if (!moved) {
            for (int i = 0; i < 7; i++) {
                List<Card> tableau = tableaus.get(i);
                if (!tableau.isEmpty()) {
                    Card topCard = tableau.get(tableau.size() - 1);
                    if (topCard.faceUp) {
                        for (int j = 0; j < 4; j++) {
                            if (canPlaceOnFoundation(topCard, j)) {
                                tableau.remove(tableau.size() - 1);
                                foundations.get(j).add(topCard);
                                score += 10;
                                moved = true;
                                break;
                            }
                        }
                        if (moved) break;
                    }
                }
            }
        }

        if (moved) {
            updateScore();
            checkWinCondition();
            gamePanel.repaint();
        } else {
            statusLabel.setText("没有可自动移动的牌");
        }
    }

    private void showHint() {
        // 简单的提示系统
        StringBuilder hint = new StringBuilder();

        // 检查废牌堆
        if (!waste.isEmpty()) {
            Card wasteCard = waste.peek();
            for (int i = 0; i < 4; i++) {
                if (canPlaceOnFoundation(wasteCard, i)) {
                    hint.append("可以将废牌堆的 ").append(wasteCard).append(" 移动到基础牌堆\n");
                    break;
                }
            }
        }

        // 检查桌面牌堆
        for (int i = 0; i < 7; i++) {
            List<Card> tableau = tableaus.get(i);
            if (!tableau.isEmpty()) {
                Card topCard = tableau.get(tableau.size() - 1);
                if (topCard.faceUp) {
                    for (int j = 0; j < 4; j++) {
                        if (canPlaceOnFoundation(topCard, j)) {
                            hint.append("可以将桌面第 ").append(i + 1).append(" 列的 ").append(topCard).append(" 移动到基础牌堆\n");
                            break;
                        }
                    }
                }
            }
        }

        if (hint.length() == 0) {
            hint.append("试试从库存抽牌，或者移动桌面上的牌");
        }

        JOptionPane.showMessageDialog(this, hint.toString(), "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    // 游戏面板
    class GamePanel extends JPanel {
        public GamePanel() {
            setBackground(new Color(0x2E8B57)); // 深绿色背景
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMousePressed(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    handleMouseReleased(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    handleMouseDragged(e);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    handleMouseClicked(e);
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGame(g);
        }

        private void drawGame(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制库存
            drawStock(g2d);

            // 绘制废牌堆
            drawWaste(g2d);

            // 绘制基础牌堆
            drawFoundations(g2d);

            // 绘制桌面牌堆
            drawTableaus(g2d);

            // 绘制拖拽中的牌
            if (isDragging && selectedCard != null) {
                drawCard(g2d, selectedCard, dragPosition.x, dragPosition.y);
            }
        }

        private void drawStock(Graphics2D g2d) {
            int x = STOCK_X;
            int y = STOCK_Y;

            if (stock.isEmpty()) {
                // 绘制空库存
                g2d.setColor(new Color(0x8B4513));
                g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
                g2d.setColor(new Color(0xA0522D));
                g2d.drawString("重洗", x + CARD_WIDTH/2 - 15, y + CARD_HEIGHT/2);
            } else {
                // 绘制牌背
                drawCardBack(g2d, x, y);
            }
        }

        private void drawWaste(Graphics2D g2d) {
            int x = WASTE_X;
            int y = WASTE_Y;

            if (waste.isEmpty()) {
                // 绘制空废牌堆
                g2d.setColor(new Color(0x8B4513));
                g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
            } else {
                // 绘制废牌堆顶部的牌
                Card topCard = waste.peek();
                drawCard(g2d, topCard, x, y);
            }
        }

        private void drawFoundations(Graphics2D g2d) {
            for (int i = 0; i < 4; i++) {
                int x = FOUNDATION_X + i * (CARD_WIDTH + 10);
                int y = FOUNDATION_Y;
                List<Card> foundation = foundations.get(i);

                if (foundation.isEmpty()) {
                    // 绘制空基础牌堆
                    g2d.setColor(new Color(0x8B4513));
                    g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);

                    // 绘制花色符号
                    String suitSymbol = getSuitSymbol(i);
                    g2d.setColor(getSuitColor(i));
                    g2d.setFont(new Font("Arial", Font.BOLD, 24));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(suitSymbol);
                    g2d.drawString(suitSymbol, x + (CARD_WIDTH - textWidth) / 2, y + CARD_HEIGHT / 2 + 8);
                } else {
                    // 绘制基础牌堆顶部的牌
                    Card topCard = foundation.get(foundation.size() - 1);
                    drawCard(g2d, topCard, x, y);
                }
            }
        }

        private void drawTableaus(Graphics2D g2d) {
            for (int i = 0; i < 7; i++) {
                int x = TABLEAU_X + i * (CARD_WIDTH + TABLEAU_SPACING);
                int y = TABLEAU_Y;
                List<Card> tableau = tableaus.get(i);

                for (int j = 0; j < tableau.size(); j++) {
                    Card card = tableau.get(j);
                    int cardY = y + j * 20; // 重叠显示

                    if (card.faceUp) {
                        drawCard(g2d, card, x, cardY);
                    } else {
                        drawCardBack(g2d, x, cardY);
                    }
                }
            }
        }

        private void drawCard(Graphics2D g2d, Card card, int x, int y) {
            // 绘制卡片背景
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);

            // 绘制卡片边框
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);

            if (card.faceUp) {
                // 绘制卡片内容
                drawCardContent(g2d, card, x, y);
            } else {
                // 绘制卡片背面
                drawCardBack(g2d, x, y);
            }
        }

        private void drawCardContent(Graphics2D g2d, Card card, int x, int y) {
            // 绘制花色和数字
            String rankSymbol = getRankSymbol(card.rank);
            String suitSymbol = getSuitSymbol(card.suit);
            Color color = getSuitColor(card.suit);

            g2d.setColor(color);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            // 左上角
            g2d.drawString(rankSymbol, x + 3, y + 15);
            g2d.drawString(suitSymbol, x + 3, y + 30);

            // 右下角（倒置）
            Graphics2D g2dCopy = (Graphics2D) g2d.create();
            g2dCopy.rotate(Math.PI, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2);
            g2dCopy.drawString(rankSymbol, x + 3, y + 15);
            g2dCopy.drawString(suitSymbol, x + 3, y + 30);
            g2dCopy.dispose();

            // 中间的大花色符号
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(suitSymbol);
            int textHeight = fm.getAscent();
            g2d.drawString(suitSymbol, x + (CARD_WIDTH - textWidth) / 2, y + (CARD_HEIGHT + textHeight) / 2 - 5);
        }

        private void drawCardBack(Graphics2D g2d, int x, int y) {
            // 绘制卡片背面
            g2d.setColor(new Color(0x1E3A8A)); // 深蓝色
            g2d.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);

            // 绘制背面图案
            g2d.setColor(new Color(0x3B82F6)); // 浅蓝色
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    g2d.fillOval(x + 10 + i * 20, y + 10 + j * 20, 8, 8);
                }
            }

            // 绘制边框
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        }

        private String getRankSymbol(int rank) {
            switch (rank) {
                case 1: return "A";
                case 11: return "J";
                case 12: return "Q";
                case 13: return "K";
                default: return String.valueOf(rank);
            }
        }

        private String getSuitSymbol(int suit) {
            switch (suit) {
                case HEARTS: return "♥";
                case DIAMONDS: return "♦";
                case CLUBS: return "♣";
                case SPADES: return "♠";
                default: return "";
            }
        }

        private Color getSuitColor(int suit) {
            return (suit == HEARTS || suit == DIAMONDS) ? Color.RED : Color.BLACK;
        }

        private void handleMousePressed(MouseEvent e) {
            Point clickPoint = e.getPoint();

            // 检查是否点击了库存
            if (isPointInRect(clickPoint, STOCK_X, STOCK_Y, CARD_WIDTH, CARD_HEIGHT)) {
                drawCardFromStock();
                return;
            }

            // 查找被点击的牌
            Card clickedCard = findCardAt(clickPoint);
            if (clickedCard != null && clickedCard.faceUp) {
                selectedCard = clickedCard;
                dragOffset = new Point(clickPoint.x - getCardX(clickedCard), clickPoint.y - getCardY(clickedCard));
                isDragging = true;
                dragPosition = clickPoint;
                repaint();
            }
        }

        private void handleMouseReleased(MouseEvent e) {
            if (isDragging && selectedCard != null) {
                Point releasePoint = e.getPoint();

                // 尝试将牌放置到目标位置
                boolean placed = tryPlaceCard(selectedCard, releasePoint);

                if (!placed) {
                    // 牌回到原位
                    selectedCard = null;
                }

                isDragging = false;
                repaint();
            }
        }

        private void handleMouseDragged(MouseEvent e) {
            if (isDragging && selectedCard != null) {
                dragPosition = e.getPoint();
                repaint();
            }
        }

        private void handleMouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && !isDragging) {
                // 双击尝试自动移动
                Point clickPoint = e.getPoint();
                Card clickedCard = findCardAt(clickPoint);
                if (clickedCard != null && clickedCard.faceUp) {
                    autoMoveCard(clickedCard);
                }
            }
        }

        private boolean isPointInRect(Point point, int x, int y, int width, int height) {
            return point.x >= x && point.x <= x + width && point.y >= y && point.y <= y + height;
        }

        private Card findCardAt(Point point) {
            // 检查废牌堆
            if (!waste.isEmpty() && isPointInRect(point, WASTE_X, WASTE_Y, CARD_WIDTH, CARD_HEIGHT)) {
                return waste.peek();
            }

            // 检查基础牌堆
            for (int i = 0; i < 4; i++) {
                int x = FOUNDATION_X + i * (CARD_WIDTH + 10);
                int y = FOUNDATION_Y;
                List<Card> foundation = foundations.get(i);
                if (!foundation.isEmpty() && isPointInRect(point, x, y, CARD_WIDTH, CARD_HEIGHT)) {
                    return foundation.get(foundation.size() - 1);
                }
            }

            // 检查桌面牌堆
            for (int i = 0; i < 7; i++) {
                int x = TABLEAU_X + i * (CARD_WIDTH + TABLEAU_SPACING);
                int y = TABLEAU_Y;
                List<Card> tableau = tableaus.get(i);

                for (int j = tableau.size() - 1; j >= 0; j--) {
                    Card card = tableau.get(j);
                    int cardY = y + j * 20;

                    if (isPointInRect(point, x, cardY, CARD_WIDTH, CARD_HEIGHT)) {
                        return card;
                    }
                }
            }

            return null;
        }

        private int getCardX(Card card) {
            // 废牌堆
            if (!waste.isEmpty() && waste.peek() == card) {
                return WASTE_X;
            }

            // 基础牌堆
            for (int i = 0; i < 4; i++) {
                List<Card> foundation = foundations.get(i);
                if (!foundation.isEmpty() && foundation.get(foundation.size() - 1) == card) {
                    return FOUNDATION_X + i * (CARD_WIDTH + 10);
                }
            }

            // 桌面牌堆
            for (int i = 0; i < 7; i++) {
                List<Card> tableau = tableaus.get(i);
                for (int j = 0; j < tableau.size(); j++) {
                    if (tableau.get(j) == card) {
                        return TABLEAU_X + i * (CARD_WIDTH + TABLEAU_SPACING);
                    }
                }
            }

            return 0;
        }

        private int getCardY(Card card) {
            // 废牌堆
            if (!waste.isEmpty() && waste.peek() == card) {
                return WASTE_Y;
            }

            // 基础牌堆
            for (int i = 0; i < 4; i++) {
                List<Card> foundation = foundations.get(i);
                if (!foundation.isEmpty() && foundation.get(foundation.size() - 1) == card) {
                    return FOUNDATION_Y;
                }
            }

            // 桌面牌堆
            for (int i = 0; i < 7; i++) {
                List<Card> tableau = tableaus.get(i);
                for (int j = 0; j < tableau.size(); j++) {
                    if (tableau.get(j) == card) {
                        return TABLEAU_Y + j * 20;
                    }
                }
            }

            return 0;
        }

        private boolean tryPlaceCard(Card card, Point point) {
            // 尝试放置到基础牌堆
            for (int i = 0; i < 4; i++) {
                int x = FOUNDATION_X + i * (CARD_WIDTH + 10);
                int y = FOUNDATION_Y;
                if (isPointInRect(point, x, y, CARD_WIDTH, CARD_HEIGHT)) {
                    if (canPlaceOnFoundation(card, i)) {
                        removeCardFromCurrentPosition(card);
                        foundations.get(i).add(card);
                        score += 10;
                        updateScore();
                        checkWinCondition();
                        selectedCard = null;
                        return true;
                    }
                }
            }

            // 尝试放置到桌面牌堆
            for (int i = 0; i < 7; i++) {
                int x = TABLEAU_X + i * (CARD_WIDTH + TABLEAU_SPACING);
                int y = TABLEAU_Y;
                List<Card> tableau = tableaus.get(i);
                int targetY = y + (tableau.size() - 1) * 20;

                if (isPointInRect(point, x, targetY, CARD_WIDTH, CARD_HEIGHT + 20)) {
                    if (canPlaceOnTableau(card, i)) {
                        removeCardFromCurrentPosition(card);
                        tableau.add(card);
                        score = Math.max(0, score - 5);
                        updateScore();
                        selectedCard = null;
                        return true;
                    }
                }
            }

            return false;
        }

        private void removeCardFromCurrentPosition(Card card) {
            // 从废牌堆移除
            if (!waste.isEmpty() && waste.peek() == card) {
                waste.pop();
                return;
            }

            // 从基础牌堆移除
            for (int i = 0; i < 4; i++) {
                List<Card> foundation = foundations.get(i);
                if (!foundation.isEmpty() && foundation.get(foundation.size() - 1) == card) {
                    foundation.remove(foundation.size() - 1);
                    return;
                }
            }

            // 从桌面牌堆移除
            for (int i = 0; i < 7; i++) {
                List<Card> tableau = tableaus.get(i);
                for (int j = 0; j < tableau.size(); j++) {
                    if (tableau.get(j) == card) {
                        tableau.remove(j);

                        // 翻开新的顶部牌
                        if (!tableau.isEmpty()) {
                            Card newTopCard = tableau.get(tableau.size() - 1);
                            if (!newTopCard.faceUp) {
                                newTopCard.faceUp = true;
                                score += 5;
                                updateScore();
                            }
                        }
                        return;
                    }
                }
            }
        }

        private void autoMoveCard(Card card) {
            // 尝试移动到基础牌堆
            for (int i = 0; i < 4; i++) {
                if (canPlaceOnFoundation(card, i)) {
                    removeCardFromCurrentPosition(card);
                    foundations.get(i).add(card);
                    score += 10;
                    updateScore();
                    checkWinCondition();
                    repaint();
                    return;
                }
            }

            statusLabel.setText("无法自动移动此牌");
        }
    }

    // 卡片类
    static class Card {
        int suit; // 0: 红心, 1: 方块, 2: 梅花, 3: 黑桃
        int rank; // 1-13 (A, 2-10, J, Q, K)
        boolean faceUp;

        public Card(int suit, int rank) {
            this.suit = suit;
            this.rank = rank;
            this.faceUp = false;
        }

        public boolean isRed() {
            return suit == HEARTS || suit == DIAMONDS;
        }

        @Override
        public String toString() {
            String rankSymbol = getRankSymbol(rank);
            String suitSymbol = getSuitSymbol(suit);
            return rankSymbol + suitSymbol;
        }

        private String getRankSymbol(int rank) {
            switch (rank) {
                case 1: return "A";
                case 11: return "J";
                case 12: return "Q";
                case 13: return "K";
                default: return String.valueOf(rank);
            }
        }

        private String getSuitSymbol(int suit) {
            switch (suit) {
                case HEARTS: return "♥";
                case DIAMONDS: return "♦";
                case CLUBS: return "♣";
                case SPADES: return "♠";
                default: return "";
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Solitaire().setVisible(true);
        });
    }
}