import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Blackjack().setVisible(true);
    });
}

static class Blackjack extends JFrame {
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 110;
    private static final int DEALER_Y = 50;
    private static final int PLAYER_Y = 300;
    private static final int CARD_SPACING = 30;

    // 花色
    private static final int HEARTS = 0;
    private static final int DIAMONDS = 1;
    private static final int CLUBS = 2;
    private static final int SPADES = 3;

    private List<Card> deck;
    private List<Card> dealerHand;
    private List<Card> playerHand;
    private int playerScore;
    private int dealerScore;
    private int playerMoney;
    private int currentBet;
    private GameState gameState;

    private GamePanel gamePanel;
    private JLabel statusLabel;
    private JLabel moneyLabel;
    private JLabel betLabel;
    private JLabel dealerScoreLabel;
    private JLabel playerScoreLabel;
    private JButton dealButton;
    private JButton hitButton;
    private JButton standButton;
    private JButton doubleButton;
    private JButton newGameButton;
    private JTextField betField;
    private JButton placeBetButton;

    private enum GameState {
        BETTING, DEALING, PLAYER_TURN, DEALER_TURN, GAME_OVER
    }

    public Blackjack() {
        deck = new ArrayList<>();
        dealerHand = new ArrayList<>();
        playerHand = new ArrayList<>();
        playerMoney = 1000;
        currentBet = 0;
        gameState = GameState.BETTING;

        initializeGUI();
        updateDisplay();
    }

    private void initializeGUI() {
        setTitle("二十一点 (Blackjack)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("🂡 二十一点 (Blackjack)");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        controlPanel.add(titleLabel);

        // 金钱显示
        moneyLabel = new JLabel("💰 金币: 1000");
        moneyLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        moneyLabel.setForeground(new Color(0xF39C12));
        controlPanel.add(moneyLabel);

        // 下注区域
        JPanel betPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        betPanel.setBorder(BorderFactory.createTitledBorder("下注"));

        betPanel.add(new JLabel("下注金额:"));
        betField = new JTextField("100", 5);
        betPanel.add(betField);

        placeBetButton = new JButton("下注");
        placeBetButton.addActionListener(e -> placeBet());
        betPanel.add(placeBetButton);

        controlPanel.add(betPanel);

        // 下注显示
        betLabel = new JLabel("当前下注: 0");
        betLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        betLabel.setForeground(new Color(0xE74C3C));
        controlPanel.add(betLabel);

        add(controlPanel, BorderLayout.NORTH);

        // 游戏面板
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // 控制按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dealButton = new JButton("🂠 发牌");
        dealButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        dealButton.setBackground(new Color(0x27AE60));
        dealButton.setForeground(Color.WHITE);
        dealButton.addActionListener(e -> dealCards());
        buttonPanel.add(dealButton);

        hitButton = new JButton("🎯 要牌");
        hitButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        hitButton.setBackground(new Color(0x3498DB));
        hitButton.setForeground(Color.WHITE);
        hitButton.addActionListener(e -> playerHit());
        buttonPanel.add(hitButton);

        standButton = new JButton("✋ 停牌");
        standButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        standButton.setBackground(new Color(0xE67E22));
        standButton.setForeground(Color.WHITE);
        standButton.addActionListener(e -> playerStand());
        buttonPanel.add(standButton);

        doubleButton = new JButton("💰 双倍");
        doubleButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        doubleButton.setBackground(new Color(0x9B59B6));
        doubleButton.setForeground(Color.WHITE);
        doubleButton.addActionListener(e -> playerDouble());
        buttonPanel.add(doubleButton);

        newGameButton = new JButton("🔄 新游戏");
        newGameButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        newGameButton.setBackground(new Color(0xE74C3C));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.addActionListener(e -> startNewGame());
        buttonPanel.add(newGameButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 分数面板
        JPanel scorePanel = new JPanel(new GridLayout(2, 2, 20, 5));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dealerScoreLabel = new JLabel("庄家点数: -");
        dealerScoreLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        dealerScoreLabel.setForeground(Color.BLACK);
        scorePanel.add(dealerScoreLabel);

        playerScoreLabel = new JLabel("玩家点数: -");
        playerScoreLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        playerScoreLabel.setForeground(Color.BLUE);
        scorePanel.add(playerScoreLabel);

        statusLabel = new JLabel("请下注开始游戏");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);
        scorePanel.add(statusLabel);

        add(scorePanel, BorderLayout.EAST);

        // 设置窗口
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void startNewGame() {
        initializeDeck();
        shuffleDeck();
        dealerHand.clear();
        playerHand.clear();
        playerScore = 0;
        dealerScore = 0;
        currentBet = 0;
        gameState = GameState.BETTING;

        updateDisplay();
        statusLabel.setText("请下注开始游戏");
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

    private void placeBet() {
        if (gameState != GameState.BETTING) {
            statusLabel.setText("当前不能下注");
            return;
        }

        try {
            int bet = Integer.parseInt(betField.getText());
            if (bet <= 0) {
                statusLabel.setText("下注金额必须大于0");
                return;
            }
            if (bet > playerMoney) {
                statusLabel.setText("金币不足！");
                return;
            }

            currentBet = bet;
            gameState = GameState.DEALING;
            statusLabel.setText("下注成功: " + currentBet + " 金币");
            updateDisplay();

            // 自动发牌
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
                dealCards();
                ((javax.swing.Timer) e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();

        } catch (NumberFormatException e) {
            statusLabel.setText("请输入有效的下注金额");
        }
    }

    private void dealCards() {
        if (gameState != GameState.DEALING) return;

        // 发两张牌给玩家和庄家
        playerHand.add(drawCard());
        dealerHand.add(drawCard());
        playerHand.add(drawCard());
        dealerHand.add(drawCard());

        updateScores();
        updateDisplay();

        // 检查是否黑杰克
        if (calculateHandValue(playerHand) == 21) {
            gameState = GameState.GAME_OVER;
            if (calculateHandValue(dealerHand) == 21) {
                statusLabel.setText("平局！双方都是黑杰克！");
            } else {
                statusLabel.setText("🎉 黑杰克！你赢了！");
                playerMoney += currentBet * 2.5; // 黑杰克赔率为3:2
            }
            updateDisplay();
            return;
        }

        gameState = GameState.PLAYER_TURN;
        statusLabel.setText("轮到你行动");
        updateButtonStates();
    }

    private Card drawCard() {
        if (deck.isEmpty()) {
            // 如果牌堆用完了，重新洗牌
            initializeDeck();
            shuffleDeck();
        }
        return deck.remove(0);
    }

    private void playerHit() {
        if (gameState != GameState.PLAYER_TURN) return;

        playerHand.add(drawCard());
        updateScores();
        updateDisplay();

        if (calculateHandValue(playerHand) > 21) {
            gameState = GameState.GAME_OVER;
            statusLabel.setText("爆牌！你输了！");
            playerMoney -= currentBet;
            updateDisplay();
        } else if (calculateHandValue(playerHand) == 21) {
            playerStand(); // 自动停牌
        }
    }

    private void playerStand() {
        if (gameState != GameState.PLAYER_TURN) return;

        gameState = GameState.DEALER_TURN;
        statusLabel.setText("庄家回合");
        updateButtonStates();

        // 庄家自动要牌
        javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {
            dealerPlay();
            ((javax.swing.Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void playerDouble() {
        if (gameState != GameState.PLAYER_TURN || playerHand.size() != 2) return;

        if (playerMoney >= currentBet) {
            currentBet *= 2;
            playerHand.add(drawCard());
            updateScores();
            updateDisplay();

            if (calculateHandValue(playerHand) > 21) {
                gameState = GameState.GAME_OVER;
                statusLabel.setText("爆牌！你输了！");
                playerMoney -= currentBet;
            } else {
                playerStand(); // 双倍后自动停牌
            }
        } else {
            statusLabel.setText("金币不足，无法双倍！");
        }
    }

    private void dealerPlay() {
        // 庄家明牌
        if (dealerHand.size() > 1) {
            dealerHand.get(1).faceUp = true;
        }

        // 庄家规则：点数小于17必须要牌
        while (calculateHandValue(dealerHand) < 17) {
            dealerHand.add(drawCard());
            updateScores();
            updateDisplay();

            try {
                Thread.sleep(1000); // 延迟显示
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        determineWinner();
    }

    private void determineWinner() {
        gameState = GameState.GAME_OVER;
        int playerValue = calculateHandValue(playerHand);
        int dealerValue = calculateHandValue(dealerHand);

        if (playerValue > 21) {
            statusLabel.setText("爆牌！你输了！");
            playerMoney -= currentBet;
        } else if (dealerValue > 21) {
            statusLabel.setText("🎉 庄家爆牌！你赢了！");
            playerMoney += currentBet;
        } else if (playerValue > dealerValue) {
            statusLabel.setText("🎉 你赢了！");
            playerMoney += currentBet;
        } else if (playerValue < dealerValue) {
            statusLabel.setText("你输了！");
            playerMoney -= currentBet;
        } else {
            statusLabel.setText("平局！");
        }

        updateDisplay();
    }

    private int calculateHandValue(List<Card> hand) {
        int value = 0;
        int aceCount = 0;

        for (Card card : hand) {
            if (card.rank == 1) {
                value += 11;
                aceCount++;
            } else if (card.rank >= 11) {
                value += 10;
            } else {
                value += card.rank;
            }
        }

        // 调整A的值
        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }

        return value;
    }

    private void updateScores() {
        playerScore = calculateHandValue(playerHand);
        dealerScore = calculateHandValue(dealerHand);

        playerScoreLabel.setText("玩家点数: " + playerScore);

        // 庄家第一张牌背面朝上，不显示点数
        if (gameState == GameState.DEALING || gameState == GameState.PLAYER_TURN) {
            if (dealerHand.size() > 1) {
                int visibleValue = dealerHand.get(0).getValue();
                dealerScoreLabel.setText("庄家点数: " + visibleValue + "+?");
            } else {
                dealerScoreLabel.setText("庄家点数: -");
            }
        } else {
            dealerScoreLabel.setText("庄家点数: " + dealerScore);
        }
    }

    private void updateDisplay() {
        moneyLabel.setText("💰 金币: " + playerMoney);
        betLabel.setText("当前下注: " + currentBet);
        updateScores();
        updateButtonStates();
        gamePanel.repaint();
    }

    private void updateButtonStates() {
        boolean canBet = gameState == GameState.BETTING;
        boolean canPlay = gameState == GameState.PLAYER_TURN;
        boolean canDeal = gameState == GameState.DEALING;

        placeBetButton.setEnabled(canBet);
        dealButton.setEnabled(canDeal);
        hitButton.setEnabled(canPlay);
        standButton.setEnabled(canPlay);
        doubleButton.setEnabled(canPlay && playerHand.size() == 2);
        newGameButton.setEnabled(true);
        betField.setEnabled(canBet);
    }

    // 游戏面板
    class GamePanel extends JPanel {
        public GamePanel() {
            setBackground(new Color(0x2E8B57)); // 深绿色背景
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGame(g);
        }

        private void drawGame(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制庄家手牌
            drawHand(g2d, dealerHand, 50, DEALER_Y, "庄家");

            // 绘制玩家手牌
            drawHand(g2d, playerHand, 50, PLAYER_Y, "玩家");
        }

        private void drawHand(Graphics2D g2d, List<Card> hand, int x, int y, String label) {
            // 绘制标签
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
            g2d.drawString(label, x, y - 10);

            // 绘制手牌
            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                int cardX = x + i * (CARD_WIDTH + CARD_SPACING);

                if (card.faceUp) {
                    drawCard(g2d, card, cardX, y);
                } else {
                    drawCardBack(g2d, cardX, y);
                }
            }
        }

        private void drawCard(Graphics2D g2d, Card card, int x, int y) {
            // 绘制卡片背景
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);

            // 绘制卡片边框
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);

            // 绘制卡片内容
            drawCardContent(g2d, card, x, y);
        }

        private void drawCardBack(Graphics2D g2d, int x, int y) {
            // 绘制卡片背面
            g2d.setColor(new Color(0x1E3A8A)); // 深蓝色
            g2d.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);

            // 绘制背面图案
            g2d.setColor(new Color(0x3B82F6)); // 浅蓝色
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("🂠", x + CARD_WIDTH/2 - 10, y + CARD_HEIGHT/2 + 5);

            // 绘制边框
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        }

        private void drawCardContent(Graphics2D g2d, Card card, int x, int y) {
            // 绘制花色和数字
            String rankSymbol = getRankSymbol(card.rank);
            String suitSymbol = getSuitSymbol(card.suit);
            Color color = getSuitColor(card.suit);

            g2d.setColor(color);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            // 左上角
            g2d.drawString(rankSymbol, x + 5, y + 20);
            g2d.drawString(suitSymbol, x + 5, y + 35);

            // 中间的大花色符号
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(suitSymbol);
            int textHeight = fm.getAscent();
            g2d.drawString(suitSymbol, x + (CARD_WIDTH - textWidth) / 2, y + (CARD_HEIGHT + textHeight) / 2 - 5);
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
    }

    // 卡片类
    static class Card {
        int suit;
        int rank;
        boolean faceUp;

        public Card(int suit, int rank) {
            this.suit = suit;
            this.rank = rank;
            this.faceUp = false;
        }

        public int getValue() {
            if (rank == 1) return 11; // A
            if (rank >= 11) return 10; // J, Q, K
            return rank;
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
            new Blackjack().setVisible(true);
        });
    }
}