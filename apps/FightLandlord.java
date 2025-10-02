import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * 斗地主游戏
 * 经典的三人扑克牌游戏，包含叫地主、出牌、计分等完整功能
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new FightLandlord().setVisible(true);
    });
}

class FightLandlord extends JFrame {
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    private static final int CARD_WIDTH = 60;
    private static final int CARD_HEIGHT = 80;
    
    // 游戏状态
    private GameState gameState = GameState.WAITING;
    private int currentPlayer = 0; // 0=玩家, 1=电脑1, 2=电脑2
    private int landlord = -1; // 地主
    private List<Card> playerCards = new ArrayList<>();
    private List<Card> computer1Cards = new ArrayList<>();
    private List<Card> computer2Cards = new ArrayList<>();
    private List<Card> landlordCards = new ArrayList<>(); // 地主牌
    private List<Card> lastPlayedCards = new ArrayList<>();
    private int lastPlayer = -1;
    private int passCount = 0;
    
    // UI组件
    private JPanel gamePanel;
    private JPanel playerPanel;
    private JPanel computer1Panel;
    private JPanel computer2Panel;
    private JPanel centerPanel;
    private JLabel statusLabel;
    private JButton callLandlordBtn;
    private JButton passBtn;
    private JButton playBtn;
    private JLabel scoreLabel;
    
    // 游戏数据
    private int playerScore = 0;
    private int computer1Score = 0;
    private int computer2Score = 0;
    private Set<Card> selectedCards = new HashSet<>();
    
    public FightLandlord() {
        initializeUI();
        newGame();
    }
    
    private void initializeUI() {
        setTitle("斗地主 - Fight Landlord");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 主面板
        gamePanel = new JPanel(new BorderLayout());
        add(gamePanel);
        
        // 顶部状态栏
        JPanel topPanel = new JPanel(new FlowLayout());
        statusLabel = new JLabel("点击'新游戏'开始");
        scoreLabel = new JLabel("玩家: 0 | 电脑1: 0 | 电脑2: 0");
        topPanel.add(statusLabel);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(scoreLabel);
        gamePanel.add(topPanel, BorderLayout.NORTH);
        
        // 中央游戏区域
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(0, 100, 0));
        
        // 电脑2区域（上方）
        computer2Panel = new JPanel(new FlowLayout());
        computer2Panel.setBackground(new Color(0, 120, 0));
        computer2Panel.setBorder(BorderFactory.createTitledBorder("电脑2"));
        computer2Panel.setPreferredSize(new Dimension(WINDOW_WIDTH, 120));
        
        // 中央出牌区域
        JPanel playAreaPanel = new JPanel(new FlowLayout());
        playAreaPanel.setBackground(new Color(0, 80, 0));
        playAreaPanel.setBorder(BorderFactory.createTitledBorder("出牌区"));
        playAreaPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, 150));
        
        // 电脑1区域（左侧）和玩家区域（下方）
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        computer1Panel = new JPanel();
        computer1Panel.setLayout(new BoxLayout(computer1Panel, BoxLayout.Y_AXIS));
        computer1Panel.setBackground(new Color(0, 120, 0));
        computer1Panel.setBorder(BorderFactory.createTitledBorder("电脑1"));
        computer1Panel.setPreferredSize(new Dimension(150, 200));
        
        playerPanel = new JPanel(new FlowLayout());
        playerPanel.setBackground(new Color(0, 140, 0));
        playerPanel.setBorder(BorderFactory.createTitledBorder("玩家"));
        playerPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - 150, 200));
        
        bottomPanel.add(computer1Panel, BorderLayout.WEST);
        bottomPanel.add(playerPanel, BorderLayout.CENTER);
        
        centerPanel.add(computer2Panel);
        centerPanel.add(playAreaPanel);
        centerPanel.add(bottomPanel);
        
        gamePanel.add(centerPanel, BorderLayout.CENTER);
        
        // 底部控制按钮
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JButton newGameBtn = new JButton("新游戏");
        newGameBtn.addActionListener(e -> newGame());
        
        callLandlordBtn = new JButton("叫地主");
        callLandlordBtn.addActionListener(e -> callLandlord());
        callLandlordBtn.setEnabled(false);
        
        passBtn = new JButton("不要");
        passBtn.addActionListener(e -> pass());
        passBtn.setEnabled(false);
        
        playBtn = new JButton("出牌");
        playBtn.addActionListener(e -> playCards());
        playBtn.setEnabled(false);
        
        controlPanel.add(newGameBtn);
        controlPanel.add(callLandlordBtn);
        controlPanel.add(passBtn);
        controlPanel.add(playBtn);
        
        gamePanel.add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void newGame() {
        // 重置游戏状态
        gameState = GameState.DEALING;
        currentPlayer = 0;
        landlord = -1;
        selectedCards.clear();
        lastPlayedCards.clear();
        lastPlayer = -1;
        passCount = 0;
        
        // 清空所有牌
        playerCards.clear();
        computer1Cards.clear();
        computer2Cards.clear();
        landlordCards.clear();
        
        // 发牌
        dealCards();
        
        // 更新UI
        updateUI();
        
        gameState = GameState.CALLING_LANDLORD;
        statusLabel.setText("叫地主阶段 - 轮到玩家");
        callLandlordBtn.setEnabled(true);
        passBtn.setEnabled(true);
    }
    
    private void dealCards() {
        // 创建一副牌
        List<Card> deck = createDeck();
        Collections.shuffle(deck);
        
        // 发牌：每人17张，剩余3张作为地主牌
        for (int i = 0; i < 51; i++) {
            if (i % 3 == 0) {
                playerCards.add(deck.get(i));
            } else if (i % 3 == 1) {
                computer1Cards.add(deck.get(i));
            } else {
                computer2Cards.add(deck.get(i));
            }
        }
        
        // 地主牌
        for (int i = 51; i < 54; i++) {
            landlordCards.add(deck.get(i));
        }
        
        // 排序
        sortCards(playerCards);
        sortCards(computer1Cards);
        sortCards(computer2Cards);
        sortCards(landlordCards);
    }
    
    private List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        
        // 添加普通牌
        for (Card.Suit suit : Card.Suit.values()) {
            if (suit != Card.Suit.JOKER) {
                for (int rank = 3; rank <= 15; rank++) { // 3-K, A, 2
                    deck.add(new Card(suit, rank));
                }
            }
        }
        
        // 添加大小王
        deck.add(new Card(Card.Suit.JOKER, 16)); // 小王
        deck.add(new Card(Card.Suit.JOKER, 17)); // 大王
        
        return deck;
    }
    
    private void sortCards(List<Card> cards) {
        cards.sort((a, b) -> Integer.compare(a.getRank(), b.getRank()));
    }
    
    private void callLandlord() {
        landlord = 0; // 玩家成为地主
        playerCards.addAll(landlordCards);
        sortCards(playerCards);
        
        gameState = GameState.PLAYING;
        currentPlayer = 0; // 地主先出牌
        
        statusLabel.setText("玩家是地主 - 请出牌");
        callLandlordBtn.setEnabled(false);
        passBtn.setEnabled(true);
        playBtn.setEnabled(true);
        
        updateUI();
    }
    
    private void pass() {
        if (gameState == GameState.CALLING_LANDLORD) {
            // 电脑叫地主逻辑（简化）
            if (Math.random() < 0.3) {
                landlord = 1; // 电脑1成为地主
                computer1Cards.addAll(landlordCards);
                sortCards(computer1Cards);
                statusLabel.setText("电脑1是地主");
            } else if (Math.random() < 0.5) {
                landlord = 2; // 电脑2成为地主
                computer2Cards.addAll(landlordCards);
                sortCards(computer2Cards);
                statusLabel.setText("电脑2是地主");
            } else {
                statusLabel.setText("重新发牌");
                newGame();
                return;
            }
            
            gameState = GameState.PLAYING;
            currentPlayer = landlord;
            
            callLandlordBtn.setEnabled(false);
            
            if (landlord != 0) {
                passBtn.setEnabled(false);
                playBtn.setEnabled(false);
                // 电脑出牌
                var timer = new javax.swing.Timer(1000, e -> computerPlay());
                timer.setRepeats(false);
                timer.start();
            }
            
            updateUI();
        } else if (gameState == GameState.PLAYING) {
            passCount++;
            nextPlayer();
            
            if (passCount >= 2) {
                // 重新开始出牌
                lastPlayedCards.clear();
                lastPlayer = -1;
                passCount = 0;
                statusLabel.setText("重新开始出牌 - 轮到" + getPlayerName(currentPlayer));
            }
            
            if (currentPlayer != 0) {
                passBtn.setEnabled(false);
                playBtn.setEnabled(false);
                javax.swing.Timer timer = new javax.swing.Timer(1000, e -> computerPlay());
                timer.setRepeats(false);
                timer.start();
            } else {
                statusLabel.setText("轮到玩家出牌");
            }
        }
    }
    
    private void playCards() {
        if (selectedCards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择要出的牌！");
            return;
        }
        
        List<Card> cardsToPlay = new ArrayList<>(selectedCards);
        
        if (!isValidPlay(cardsToPlay)) {
            JOptionPane.showMessageDialog(this, "出牌不符合规则！");
            return;
        }
        
        // 出牌
        playerCards.removeAll(cardsToPlay);
        lastPlayedCards = new ArrayList<>(cardsToPlay);
        lastPlayer = 0;
        passCount = 0;
        selectedCards.clear();
        
        // 检查游戏结束
        if (playerCards.isEmpty()) {
            endGame(0);
            return;
        }
        
        nextPlayer();
        statusLabel.setText("玩家出牌：" + formatCards(cardsToPlay));
        
        updateUI();
        
        // 电脑出牌
        javax.swing.Timer timer = new javax.swing.Timer(1500, e -> computerPlay());
        timer.setRepeats(false);
        timer.start();
    }
    
    private void computerPlay() {
        List<Card> computerCards = getCurrentPlayerCards();
        List<Card> cardsToPlay = findComputerPlay(computerCards);
        
        if (cardsToPlay.isEmpty()) {
            // 电脑选择不要
            passCount++;
            statusLabel.setText(getPlayerName(currentPlayer) + "不要");
        } else {
            // 电脑出牌
            computerCards.removeAll(cardsToPlay);
            lastPlayedCards = new ArrayList<>(cardsToPlay);
            lastPlayer = currentPlayer;
            passCount = 0;
            statusLabel.setText(getPlayerName(currentPlayer) + "出牌：" + formatCards(cardsToPlay));
            
            // 检查游戏结束
            if (computerCards.isEmpty()) {
                endGame(currentPlayer);
                return;
            }
        }
        
        nextPlayer();
        
        if (passCount >= 2) {
            lastPlayedCards.clear();
            lastPlayer = -1;
            passCount = 0;
        }
        
        updateUI();
        
        if (currentPlayer != 0) {
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> computerPlay());
            timer.setRepeats(false);
            timer.start();
        } else {
            passBtn.setEnabled(true);
            playBtn.setEnabled(true);
        }
    }
    
    private List<Card> getCurrentPlayerCards() {
        return switch (currentPlayer) {
            case 1 -> computer1Cards;
            case 2 -> computer2Cards;
            default -> playerCards;
        };
    }
    
    private List<Card> findComputerPlay(List<Card> cards) {
        // 简化的电脑出牌逻辑
        if (lastPlayedCards.isEmpty()) {
            // 首次出牌，出最小的单牌
            return List.of(cards.get(0));
        }
        
        // 尝试找到能压过上家的牌
        CardType lastType = getCardType(lastPlayedCards);
        
        for (int i = 0; i < cards.size(); i++) {
            List<Card> candidate = List.of(cards.get(i));
            if (getCardType(candidate) == lastType && 
                canBeat(candidate, lastPlayedCards)) {
                return candidate;
            }
        }
        
        return new ArrayList<>(); // 没有合适的牌，选择不要
    }
    
    private boolean isValidPlay(List<Card> cards) {
        if (cards.isEmpty()) return false;
        
        CardType type = getCardType(cards);
        if (type == CardType.INVALID) return false;
        
        if (lastPlayedCards.isEmpty()) return true;
        
        return canBeat(cards, lastPlayedCards);
    }
    
    private boolean canBeat(List<Card> cards, List<Card> lastCards) {
        CardType type1 = getCardType(cards);
        CardType type2 = getCardType(lastCards);
        
        // 王炸可以压任何牌
        if (type1 == CardType.ROCKET) return true;
        if (type2 == CardType.ROCKET) return false;
        
        // 炸弹可以压非炸弹
        if (type1 == CardType.BOMB && type2 != CardType.BOMB) return true;
        if (type2 == CardType.BOMB && type1 != CardType.BOMB) return false;
        
        // 同类型比较大小
        if (type1 == type2 && cards.size() == lastCards.size()) {
            return getCardValue(cards.get(0)) > getCardValue(lastCards.get(0));
        }
        
        return false;
    }
    
    private CardType getCardType(List<Card> cards) {
        if (cards.isEmpty()) return CardType.INVALID;
        
        int size = cards.size();
        
        if (size == 1) return CardType.SINGLE;
        if (size == 2) {
            if (cards.get(0).getRank() == 16 && cards.get(1).getRank() == 17) {
                return CardType.ROCKET; // 王炸
            }
            if (cards.get(0).getRank() == cards.get(1).getRank()) {
                return CardType.PAIR;
            }
        }
        if (size == 4) {
            if (cards.stream().allMatch(c -> c.getRank() == cards.get(0).getRank())) {
                return CardType.BOMB;
            }
        }
        
        return CardType.INVALID;
    }
    
    private int getCardValue(Card card) {
        int rank = card.getRank();
        if (rank == 16) return 20; // 小王
        if (rank == 17) return 21; // 大王
        if (rank == 15) return 17; // 2
        if (rank == 14) return 16; // A
        return rank;
    }
    
    private void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % 3;
    }
    
    private String getPlayerName(int player) {
        return switch (player) {
            case 0 -> "玩家";
            case 1 -> "电脑1";
            case 2 -> "电脑2";
            default -> "未知";
        };
    }
    
    private String formatCards(List<Card> cards) {
        return cards.stream()
                   .map(Card::toString)
                   .reduce((a, b) -> a + " " + b)
                   .orElse("");
    }
    
    private void endGame(int winner) {
        gameState = GameState.GAME_OVER;
        
        String winnerName = getPlayerName(winner);
        statusLabel.setText("游戏结束！" + winnerName + "获胜！");
        
        // 更新分数
        if (winner == 0) {
            playerScore++;
        } else if (winner == 1) {
            computer1Score++;
        } else {
            computer2Score++;
        }
        
        updateScoreLabel();
        
        callLandlordBtn.setEnabled(false);
        passBtn.setEnabled(false);
        playBtn.setEnabled(false);
        
        JOptionPane.showMessageDialog(this, winnerName + "获胜！\n点击'新游戏'继续");
    }
    
    private void updateUI() {
        // 清空面板
        playerPanel.removeAll();
        computer1Panel.removeAll();
        computer2Panel.removeAll();
        
        // 显示玩家手牌
        for (Card card : playerCards) {
            JButton cardBtn = createCardButton(card, true);
            cardBtn.addActionListener(e -> toggleCardSelection(card, cardBtn));
            playerPanel.add(cardBtn);
        }
        
        // 显示电脑手牌数量
        computer1Panel.add(new JLabel("剩余: " + computer1Cards.size() + "张"));
        computer2Panel.add(new JLabel("剩余: " + computer2Cards.size() + "张"));
        
        // 显示地主标识
        if (landlord == 0) {
            playerPanel.setBorder(BorderFactory.createTitledBorder("玩家 (地主)"));
        } else if (landlord == 1) {
            computer1Panel.setBorder(BorderFactory.createTitledBorder("电脑1 (地主)"));
        } else if (landlord == 2) {
            computer2Panel.setBorder(BorderFactory.createTitledBorder("电脑2 (地主)"));
        }
        
        updateScoreLabel();
        
        revalidate();
        repaint();
    }
    
    private JButton createCardButton(Card card, boolean showFace) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        button.setFont(new Font("Arial", Font.BOLD, 10));
        
        if (showFace) {
            button.setText(card.toString());
            button.setBackground(Color.WHITE);
            
            // 设置颜色
            if (card.getSuit() == Card.Suit.HEARTS || card.getSuit() == Card.Suit.DIAMONDS) {
                button.setForeground(Color.RED);
            } else if (card.getSuit() == Card.Suit.JOKER) {
                button.setForeground(card.getRank() == 16 ? Color.BLACK : Color.RED);
            } else {
                button.setForeground(Color.BLACK);
            }
        } else {
            button.setText("牌");
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
        }
        
        return button;
    }
    
    private void toggleCardSelection(Card card, JButton button) {
        if (selectedCards.contains(card)) {
            selectedCards.remove(card);
            button.setBackground(Color.WHITE);
        } else {
            selectedCards.add(card);
            button.setBackground(Color.YELLOW);
        }
    }
    
    private void updateScoreLabel() {
        scoreLabel.setText(String.format("玩家: %d | 电脑1: %d | 电脑2: %d", 
                                       playerScore, computer1Score, computer2Score));
    }
    
    // 枚举类
    enum GameState {
        WAITING, DEALING, CALLING_LANDLORD, PLAYING, GAME_OVER
    }
    
    enum CardType {
        INVALID, SINGLE, PAIR, BOMB, ROCKET
    }
    
    // 扑克牌类
    static class Card {
        enum Suit {
            SPADES("♠"), HEARTS("♥"), DIAMONDS("♦"), CLUBS("♣"), JOKER("王");
            
            private final String symbol;
            
            Suit(String symbol) {
                this.symbol = symbol;
            }
            
            public String getSymbol() {
                return symbol;
            }
        }
        
        private final Suit suit;
        private final int rank;
        
        public Card(Suit suit, int rank) {
            this.suit = suit;
            this.rank = rank;
        }
        
        public Suit getSuit() {
            return suit;
        }
        
        public int getRank() {
            return rank;
        }
        
        @Override
        public String toString() {
            if (suit == Suit.JOKER) {
                return rank == 16 ? "小王" : "大王";
            }
            
            String rankStr = switch (rank) {
                case 11 -> "J";
                case 12 -> "Q";
                case 13 -> "K";
                case 14 -> "A";
                case 15 -> "2";
                default -> String.valueOf(rank);
            };
            
            return suit.getSymbol() + rankStr;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Card card = (Card) obj;
            return rank == card.rank && suit == card.suit;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(suit, rank);
        }
    }
}
