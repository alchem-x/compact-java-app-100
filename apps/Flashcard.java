import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Flashcard().setVisible(true);
    });
}

static class Flashcard extends JFrame {
    private JPanel cardPanel;
    private JLabel frontLabel, backLabel;
    private JButton flipButton, nextButton, prevButton, shuffleButton;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JTextField questionField, answerField;
    private JButton addCardButton, deleteCardButton, saveButton, loadButton;
    private JComboBox<String> categoryCombo;
    private JList<String> cardList;
    private DefaultListModel<String> cardListModel;
    private JCheckBox masteredCheckBox;
    private JLabel statusLabel;

    private List<FlashCard> cards = new ArrayList<>();
    private int currentCardIndex = 0;
    private boolean isFlipped = false;
    private static final String DATA_FILE = "flashcards.dat";

    public Flashcard() {
        initializeGUI();
        loadData();
        updateDisplay();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle("抽认卡 - 学习工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("🎴 抽认卡", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        // 进度面板
        JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
        progressPanel.setBackground(new Color(245, 247, 250));
        progressPanel.setBorder(BorderFactory.createTitledBorder("学习进度"));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("进度");
        progressBar.setForeground(new Color(46, 204, 113));

        progressLabel = new JLabel("卡片 0/0", SwingConstants.CENTER);
        progressLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        progressLabel.setForeground(new Color(127, 140, 141));

        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(progressLabel, BorderLayout.SOUTH);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(progressPanel, BorderLayout.CENTER);

        // 中心面板 - 卡片显示
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 卡片面板
        cardPanel = new JPanel(new CardLayout());
        cardPanel.setPreferredSize(new Dimension(400, 250));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // 卡片正面（问题）
        JPanel frontPanel = new JPanel(new BorderLayout());
        frontPanel.setBackground(new Color(255, 255, 255));

        frontLabel = new JLabel("", SwingConstants.CENTER);
        frontLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        frontLabel.setForeground(new Color(52, 73, 94));

        JLabel frontTitle = new JLabel("问题", SwingConstants.CENTER);
        frontTitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        frontTitle.setForeground(new Color(127, 140, 141));

        frontPanel.add(frontTitle, BorderLayout.NORTH);
        frontPanel.add(frontLabel, BorderLayout.CENTER);

        // 卡片背面（答案）
        JPanel backPanel = new JPanel(new BorderLayout());
        backPanel.setBackground(new Color(240, 248, 255));

        backLabel = new JLabel("", SwingConstants.CENTER);
        backLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        backLabel.setForeground(new Color(41, 128, 185));

        JLabel backTitle = new JLabel("答案", SwingConstants.CENTER);
        backTitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        backTitle.setForeground(new Color(127, 140, 141));

        backPanel.add(backTitle, BorderLayout.NORTH);
        backPanel.add(backLabel, BorderLayout.CENTER);

        cardPanel.add(frontPanel, "FRONT");
        cardPanel.add(backPanel, "BACK");

        // 控制按钮面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(new Color(245, 247, 250));

        flipButton = createButton("🔄 翻转", new Color(52, 152, 219), e -> flipCard());
        prevButton = createButton("⬅️ 上一张", new Color(155, 89, 182), e -> previousCard());
        nextButton = createButton("➡️ 下一张", new Color(155, 89, 182), e -> nextCard());
        shuffleButton = createButton("🔀 打乱", new Color(243, 156, 18), e -> shuffleCards());

        controlPanel.add(prevButton);
        controlPanel.add(flipButton);
        controlPanel.add(nextButton);
        controlPanel.add(shuffleButton);

        centerPanel.add(cardPanel, BorderLayout.CENTER);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);

        // 右侧面板 - 卡片管理和编辑
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("卡片管理"));
        rightPanel.setBackground(new Color(245, 247, 250));

        // 编辑面板
        JPanel editPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        editPanel.setBackground(new Color(245, 247, 250));
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        questionField = new JTextField();
        questionField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        answerField = new JTextField();
        answerField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        categoryCombo = new JComboBox<>(new String[]{"语文", "数学", "英语", "历史", "科学", "其他"});
        masteredCheckBox = new JCheckBox("已掌握");

        editPanel.add(new JLabel("问题:"));
        editPanel.add(questionField);
        editPanel.add(new JLabel("答案:"));
        editPanel.add(answerField);
        editPanel.add(new JLabel("类别:"));
        editPanel.add(categoryCombo);
        editPanel.add(masteredCheckBox);

        // 编辑按钮面板
        JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        editButtonPanel.setBackground(new Color(245, 247, 250));

        addCardButton = createButton("➕ 添加", new Color(39, 174, 96), e -> addCard());
        deleteCardButton = createButton("🗑️ 删除", new Color(231, 76, 60), e -> deleteCard());
        saveButton = createButton("💾 保存", new Color(52, 152, 219), e -> saveData());
        loadButton = createButton("📁 加载", new Color(142, 68, 173), e -> loadData());

        editButtonPanel.add(addCardButton);
        editButtonPanel.add(deleteCardButton);
        editButtonPanel.add(saveButton);
        editButtonPanel.add(loadButton);

        // 卡片列表
        cardListModel = new DefaultListModel<>();
        cardList = new JList<>(cardListModel);
        cardList.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        cardList.setSelectionBackground(new Color(173, 216, 230));
        cardList.addListSelectionListener(e -> selectCard());

        JScrollPane listScrollPane = new JScrollPane(cardList);
        listScrollPane.setPreferredSize(new Dimension(200, 200));
        listScrollPane.setBorder(BorderFactory.createTitledBorder("卡片列表"));

        rightPanel.add(editPanel, BorderLayout.NORTH);
        rightPanel.add(editButtonPanel, BorderLayout.CENTER);
        rightPanel.add(listScrollPane, BorderLayout.SOUTH);

        // 状态栏
        statusLabel = new JLabel("准备就绪");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(127, 140, 141));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 组装界面
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(800, 600);
    }

    private JButton createButton(String text, Color bgColor, java.util.function.Consumer<ActionEvent> action) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action::accept);
        return button;
    }

    private void addCard() {
        String question = questionField.getText().trim();
        String answer = answerField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        boolean mastered = masteredCheckBox.isSelected();

        if (question.isEmpty() || answer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "问题和答案不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FlashCard card = new FlashCard(question, answer, category, mastered);
        cards.add(card);
        cardListModel.addElement(String.format("[%s] %s", category, question.substring(0, Math.min(question.length(), 20))));

        updateDisplay();
        saveData();

        // 清空输入
        questionField.setText("");
        answerField.setText("");
        masteredCheckBox.setSelected(false);

        statusLabel.setText("成功添加卡片");
        statusLabel.setForeground(new Color(39, 174, 96));

        // 如果当前没有显示的卡片，显示新添加的
        if (currentCardIndex == -1) {
            currentCardIndex = cards.size() - 1;
            showCard();
        }
    }

    private void deleteCard() {
        int selectedIndex = cardList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的卡片", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "确定要删除选中的卡片吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            cards.remove(selectedIndex);
            cardListModel.removeElementAt(selectedIndex);

            // 调整当前索引
            if (currentCardIndex >= selectedIndex) {
                currentCardIndex--;
            }
            if (currentCardIndex < 0 && !cards.isEmpty()) {
                currentCardIndex = 0;
            }

            updateDisplay();
            saveData();

            if (!cards.isEmpty()) {
                showCard();
            } else {
                clearCardDisplay();
            }

            statusLabel.setText("成功删除卡片");
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    private void selectCard() {
        int selectedIndex = cardList.getSelectedIndex();
        if (selectedIndex != -1) {
            currentCardIndex = selectedIndex;
            isFlipped = false;
            showCard();
        }
    }

    private void showCard() {
        if (currentCardIndex < 0 || currentCardIndex >= cards.size()) {
            clearCardDisplay();
            return;
        }

        FlashCard card = cards.get(currentCardIndex);
        frontLabel.setText(card.question);
        backLabel.setText(card.answer);

        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, isFlipped ? "BACK" : "FRONT");

        questionField.setText(card.question);
        answerField.setText(card.answer);
        categoryCombo.setSelectedItem(card.category);
        masteredCheckBox.setSelected(card.mastered);

        updateDisplay();
    }

    private void clearCardDisplay() {
        frontLabel.setText("");
        backLabel.setText("");
        questionField.setText("");
        answerField.setText("");
        masteredCheckBox.setSelected(false);
    }

    private void flipCard() {
        if (cards.isEmpty()) return;

        isFlipped = !isFlipped;
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, isFlipped ? "BACK" : "FRONT");

        if (isFlipped) {
            statusLabel.setText("显示答案");
            // 标记为已掌握（可选）
            if (currentCardIndex >= 0) {
                cards.get(currentCardIndex).mastered = true;
                masteredCheckBox.setSelected(true);
                updateDisplay();
            }
        } else {
            statusLabel.setText("显示问题");
        }
    }

    private void nextCard() {
        if (cards.isEmpty()) return;

        currentCardIndex = (currentCardIndex + 1) % cards.size();
        isFlipped = false;
        showCard();

        statusLabel.setText("下一张卡片");
    }

    private void previousCard() {
        if (cards.isEmpty()) return;

        currentCardIndex = (currentCardIndex - 1 + cards.size()) % cards.size();
        isFlipped = false;
        showCard();

        statusLabel.setText("上一张卡片");
    }

    private void shuffleCards() {
        if (cards.size() < 2) return;

        Collections.shuffle(cards);
        refreshCardList();
        currentCardIndex = 0;
        isFlipped = false;
        showCard();

        statusLabel.setText("卡片已打乱");
    }

    private void refreshCardList() {
        cardListModel.clear();
        for (FlashCard card : cards) {
            cardListModel.addElement(String.format("[%s] %s", card.category,
                    card.question.substring(0, Math.min(card.question.length(), 20))));
        }
    }

    private void updateDisplay() {
        progressBar.setMaximum(cards.size());
        progressBar.setValue(currentCardIndex + 1);
        progressLabel.setText(String.format("卡片 %d/%d", currentCardIndex + 1, cards.size()));

        // 更新卡片列表的选中状态
        if (currentCardIndex >= 0 && currentCardIndex < cardListModel.size()) {
            cardList.setSelectedIndex(currentCardIndex);
        }
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(cards);
            statusLabel.setText("数据已保存");
            statusLabel.setForeground(new Color(39, 174, 96));
        } catch (IOException e) {
            statusLabel.setText("保存失败: " + e.getMessage());
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            cards = (List<FlashCard>) in.readObject();
            refreshCardList();
            if (!cards.isEmpty()) {
                currentCardIndex = 0;
                isFlipped = false;
                showCard();
            }
            statusLabel.setText("数据已加载");
            statusLabel.setForeground(new Color(39, 174, 96));
        } catch (Exception e) {
            statusLabel.setText("加载失败: " + e.getMessage());
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    private void filterWords() {
        // 实现筛选功能
        statusLabel.setText("筛选功能已应用");
    }
}

static class FlashCard implements Serializable {
    private static final long serialVersionUID = 1L;
    String question;
    String answer;
    String category;
    boolean mastered;

    FlashCard(String question, String answer, String category, boolean mastered) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.mastered = mastered;
    }
}