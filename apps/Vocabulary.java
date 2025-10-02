import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Vocabulary().setVisible(true);
    });
}

static class Vocabulary extends JFrame {
    private JTable wordTable;
    private DefaultTableModel tableModel;
    private JTextField wordField, meaningField, exampleField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> difficultyCombo;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private JProgressBar progressBar;
    private JButton addButton, editButton, deleteButton, quizButton, importButton, exportButton;
    private JCheckBox masteredCheckBox;

    private Map<String, List<Word>> wordMap = new HashMap<>();
    private List<Word> currentWords = new ArrayList<>();
    private static final String[] CATEGORIES = {"日常", "学术", "商务", "技术", "文学", "其他"};
    private static final String[] DIFFICULTIES = {"简单", "中等", "困难"};
    private static final String DATA_FILE = "vocabulary.dat";

    public Vocabulary() {
        initializeGUI();
        loadData();
        updateDisplay();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle("单词本 - 词汇管理");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("📚 单词本", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        // 统计面板
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        statsPanel.setBackground(new Color(245, 247, 250));
        statsPanel.setBorder(BorderFactory.createTitledBorder("统计信息"));

        statsLabel = new JLabel("总单词: 0", SwingConstants.CENTER);
        statsLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        statsLabel.setForeground(new Color(52, 73, 94));

        JLabel masteredLabel = new JLabel("已掌握: 0", SwingConstants.CENTER);
        masteredLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        masteredLabel.setForeground(new Color(39, 174, 96));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("掌握进度");
        progressBar.setForeground(new Color(46, 204, 113));

        statsPanel.add(statsLabel);
        statsPanel.add(masteredLabel);
        statsPanel.add(progressBar);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(new Color(245, 247, 250));
        inputPanel.setBorder(BorderFactory.createTitledBorder("添加单词"));

        wordField = new JTextField();
        wordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        meaningField = new JTextField();
        meaningField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exampleField = new JTextField();
        exampleField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        categoryCombo = new JComboBox<>(CATEGORIES);
        difficultyCombo = new JComboBox<>(DIFFICULTIES);
        masteredCheckBox = new JCheckBox("已掌握");

        inputPanel.add(new JLabel("单词:"));
        inputPanel.add(wordField);
        inputPanel.add(new JLabel("释义:"));
        inputPanel.add(meaningField);
        inputPanel.add(new JLabel("例句:"));
        inputPanel.add(exampleField);
        inputPanel.add(new JLabel("类别:"));
        inputPanel.add(categoryCombo);
        inputPanel.add(new JLabel("难度:"));
        inputPanel.add(difficultyCombo);
        inputPanel.add(masteredCheckBox);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));

        addButton = createButton("➕ 添加", new Color(39, 174, 96), e -> addWord());
        editButton = createButton("✏️ 编辑", new Color(52, 152, 219), e -> editWord());
        deleteButton = createButton("🗑️ 删除", new Color(231, 76, 60), e -> deleteWord());
        quizButton = createButton("🎯 测试", new Color(241, 196, 15), e -> startQuiz());
        importButton = createButton("📥 导入", new Color(155, 89, 182), e -> importWords());
        exportButton = createButton("📤 导出", new Color(142, 68, 173), e -> exportWords());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(quizButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);

        // 表格
        String[] columnNames = {"单词", "释义", "例句", "类别", "难度", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 5 ? String.class : String.class;
            }
        };

        wordTable = new JTable(tableModel);
        wordTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        wordTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        wordTable.setRowHeight(30);
        wordTable.setSelectionBackground(new Color(173, 216, 230));

        // 设置列宽
        wordTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        wordTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        wordTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        wordTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        wordTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        wordTable.getColumnModel().getColumn(5).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(wordTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("单词列表"));

        // 搜索和筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(new Color(245, 247, 250));
        filterPanel.setBorder(BorderFactory.createTitledBorder("搜索和筛选"));

        JTextField searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        var categoryOptions = new String[CATEGORIES.length + 1];
        categoryOptions[0] = "全部";
        System.arraycopy(CATEGORIES, 0, categoryOptions, 1, CATEGORIES.length);
        var filterCategoryCombo = new JComboBox<>(categoryOptions);
        var difficultyOptions = new String[DIFFICULTIES.length + 1];
        difficultyOptions[0] = "全部";
        System.arraycopy(DIFFICULTIES, 0, difficultyOptions, 1, DIFFICULTIES.length);
        var filterDifficultyCombo = new JComboBox<>(difficultyOptions);
        JCheckBox showMasteredCheckBox = new JCheckBox("显示已掌握");
        showMasteredCheckBox.setSelected(true);

        filterPanel.add(new JLabel("搜索:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("类别:"));
        filterPanel.add(filterCategoryCombo);
        filterPanel.add(new JLabel("难度:"));
        filterPanel.add(filterDifficultyCombo);
        filterPanel.add(showMasteredCheckBox);

        // 添加搜索监听器
        searchField.addActionListener(e -> filterWords());
        filterCategoryCombo.addActionListener(e -> filterWords());
        filterDifficultyCombo.addActionListener(e -> filterWords());
        showMasteredCheckBox.addActionListener(e -> filterWords());

        // 状态栏
        statusLabel = new JLabel("准备就绪");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(127, 140, 141));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 组装界面
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(buttonPanel, BorderLayout.CENTER);
        leftPanel.add(filterPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(1200, 800);
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

    private void addWord() {
        String word = wordField.getText().trim();
        String meaning = meaningField.getText().trim();
        String example = exampleField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String difficulty = (String) difficultyCombo.getSelectedItem();
        boolean mastered = masteredCheckBox.isSelected();

        if (word.isEmpty() || meaning.isEmpty()) {
            JOptionPane.showMessageDialog(this, "单词和释义不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Word newWord = new Word(word, meaning, example, category, difficulty, mastered);

        // 添加到对应的类别列表
        if (!wordMap.containsKey(category)) {
            wordMap.put(category, new ArrayList<>());
        }
        wordMap.get(category).add(newWord);
        currentWords.add(newWord);

        // 添加到表格
        tableModel.addRow(new Object[]{
            word, meaning, example, category, difficulty, mastered ? "已掌握" : "学习中"
        });

        updateDisplay();
        saveData();

        // 清空输入
        wordField.setText("");
        meaningField.setText("");
        exampleField.setText("");
        masteredCheckBox.setSelected(false);

        statusLabel.setText("成功添加单词: " + word);
        statusLabel.setForeground(new Color(39, 174, 96));
    }

    private void editWord() {
        int selectedRow = wordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的单词", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String oldWord = (String) tableModel.getValueAt(selectedRow, 0);
        String oldMeaning = (String) tableModel.getValueAt(selectedRow, 1);
        String oldExample = (String) tableModel.getValueAt(selectedRow, 2);
        String oldCategory = (String) tableModel.getValueAt(selectedRow, 3);
        String oldDifficulty = (String) tableModel.getValueAt(selectedRow, 4);
        boolean oldMastered = "已掌握".equals(tableModel.getValueAt(selectedRow, 5));

        JPanel editPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField editWordField = new JTextField(oldWord);
        JTextField editMeaningField = new JTextField(oldMeaning);
        JTextField editExampleField = new JTextField(oldExample);
        JComboBox<String> editCategoryCombo = new JComboBox<>(CATEGORIES);
        editCategoryCombo.setSelectedItem(oldCategory);
        JComboBox<String> editDifficultyCombo = new JComboBox<>(DIFFICULTIES);
        editDifficultyCombo.setSelectedItem(oldDifficulty);
        JCheckBox editMasteredCheckBox = new JCheckBox("已掌握");
        editMasteredCheckBox.setSelected(oldMastered);

        editPanel.add(new JLabel("单词:"));
        editPanel.add(editWordField);
        editPanel.add(new JLabel("释义:"));
        editPanel.add(editMeaningField);
        editPanel.add(new JLabel("例句:"));
        editPanel.add(editExampleField);
        editPanel.add(new JLabel("类别:"));
        editPanel.add(editCategoryCombo);
        editPanel.add(new JLabel("难度:"));
        editPanel.add(editDifficultyCombo);
        editPanel.add(editMasteredCheckBox);

        int result = JOptionPane.showConfirmDialog(this, editPanel, "编辑单词", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newWord = editWordField.getText().trim();
            String newMeaning = editMeaningField.getText().trim();
            String newExample = editExampleField.getText().trim();
            String newCategory = (String) editCategoryCombo.getSelectedItem();
            String newDifficulty = (String) editDifficultyCombo.getSelectedItem();
            boolean newMastered = editMasteredCheckBox.isSelected();

            if (newWord.isEmpty() || newMeaning.isEmpty()) {
                JOptionPane.showMessageDialog(this, "单词和释义不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 更新表格
            tableModel.setValueAt(newWord, selectedRow, 0);
            tableModel.setValueAt(newMeaning, selectedRow, 1);
            tableModel.setValueAt(newExample, selectedRow, 2);
            tableModel.setValueAt(newCategory, selectedRow, 3);
            tableModel.setValueAt(newDifficulty, selectedRow, 4);
            tableModel.setValueAt(newMastered ? "已掌握" : "学习中", selectedRow, 5);

            // 更新内存数据
            updateWordData(oldWord, newWord, newMeaning, newExample, newCategory, newDifficulty, newMastered);

            updateDisplay();
            saveData();

            statusLabel.setText("成功更新单词: " + newWord);
            statusLabel.setForeground(new Color(39, 174, 96));
        }
    }

    private void deleteWord() {
        int selectedRow = wordTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的单词", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String word = (String) tableModel.getValueAt(selectedRow, 0);
        int result = JOptionPane.showConfirmDialog(this,
                String.format("确定要删除单词 '%s' 吗？", word),
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            removeWordData(word);

            updateDisplay();
            saveData();

            statusLabel.setText("成功删除单词: " + word);
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    private void startQuiz() {
        if (currentWords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可测试的单词", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 筛选未掌握的单词
        List<Word> quizWords = new ArrayList<>();
        for (Word word : currentWords) {
            if (!word.mastered) {
                quizWords.add(word);
            }
        }

        if (quizWords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有单词都已掌握！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        new QuizDialog(this, quizWords).setVisible(true);
    }

    private void importWords() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("文本文件", "txt"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\t");
                    if (parts.length >= 2) {
                        String word = parts[0].trim();
                        String meaning = parts[1].trim();
                        String example = parts.length > 2 ? parts[2].trim() : "";

                        Word newWord = new Word(word, meaning, example, "其他", "中等", false);
                        addWordToTable(newWord);
                        count++;
                    }
                }

                statusLabel.setText(String.format("成功导入 %d 个单词", count));
                statusLabel.setForeground(new Color(39, 174, 96));

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportWords() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("单词本_" + System.currentTimeMillis() + ".txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String word = (String) tableModel.getValueAt(i, 0);
                    String meaning = (String) tableModel.getValueAt(i, 1);
                    String example = (String) tableModel.getValueAt(i, 2);
                    writer.printf("%s\t%s\t%s%n", word, meaning, example);
                }

                JOptionPane.showMessageDialog(this, "导出成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterWords() {
        // 实现搜索和筛选功能
        String searchText = "";
        String selectedCategory = "全部";
        String selectedDifficulty = "全部";
        boolean showMastered = true;

        tableModel.setRowCount(0);

        for (Word word : currentWords) {
            boolean matchesSearch = word.word.toLowerCase().contains(searchText.toLowerCase()) ||
                                   word.meaning.toLowerCase().contains(searchText.toLowerCase());
            boolean matchesCategory = "全部".equals(selectedCategory) || word.category.equals(selectedCategory);
            boolean matchesDifficulty = "全部".equals(selectedDifficulty) || word.difficulty.equals(selectedDifficulty);
            boolean matchesMastered = showMastered || !word.mastered;

            if (matchesSearch && matchesCategory && matchesDifficulty && matchesMastered) {
                tableModel.addRow(new Object[]{
                    word.word, word.meaning, word.example, word.category, word.difficulty,
                    word.mastered ? "已掌握" : "学习中"
                });
            }
        }
    }

    private void updateDisplay() {
        int totalCount = currentWords.size();
        int masteredCount = 0;

        for (Word word : currentWords) {
            if (word.mastered) masteredCount++;
        }

        statsLabel.setText(String.format("总单词: %d", totalCount));

        // 更新进度条
        if (totalCount > 0) {
            int progress = (masteredCount * 100) / totalCount;
            progressBar.setValue(progress);
            progressBar.setString(String.format("掌握进度 %d%%", progress));
        } else {
            progressBar.setValue(0);
            progressBar.setString("掌握进度 0%");
        }
    }

    private void addWordToTable(Word word) {
        tableModel.addRow(new Object[]{
            word.word, word.meaning, word.example, word.category, word.difficulty,
            word.mastered ? "已掌握" : "学习中"
        });
    }

    private void updateWordData(String oldWord, String newWord, String meaning, String example,
                               String category, String difficulty, boolean mastered) {
        // 更新内存中的数据
        for (List<Word> wordList : wordMap.values()) {
            for (Word w : wordList) {
                if (w.word.equals(oldWord)) {
                    w.word = newWord;
                    w.meaning = meaning;
                    w.example = example;
                    w.category = category;
                    w.difficulty = difficulty;
                    w.mastered = mastered;
                    return;
                }
            }
        }
    }

    private void removeWordData(String word) {
        for (List<Word> wordList : wordMap.values()) {
            wordList.removeIf(w -> w.word.equals(word));
        }
        currentWords.removeIf(w -> w.word.equals(word));
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(wordMap);
            out.writeObject(currentWords);
        } catch (IOException e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            wordMap = (Map<String, List<Word>>) in.readObject();
            currentWords = (List<Word>) in.readObject();

            // 填充表格
            tableModel.setRowCount(0);
            for (Word word : currentWords) {
                addWordToTable(word);
            }

            updateDisplay();
        } catch (Exception e) {
            System.err.println("加载数据失败: " + e.getMessage());
        }
    }
}

static class Word implements Serializable {
    private static final long serialVersionUID = 1L;
    String word;
    String meaning;
    String example;
    String category;
    String difficulty;
    boolean mastered;

    Word(String word, String meaning, String example, String category, String difficulty, boolean mastered) {
        this.word = word;
        this.meaning = meaning;
        this.example = example;
        this.category = category;
        this.difficulty = difficulty;
        this.mastered = mastered;
    }
}

static class QuizDialog extends JDialog {
    private List<Word> quizWords;
    private int currentIndex = 0;
    private int correctCount = 0;
    private JLabel questionLabel;
    private JTextField answerField;
    private JLabel resultLabel;
    private JButton nextButton;
    private Word currentWord;
    private Random random = new Random();

    QuizDialog(JFrame parent, List<Word> words) {
        super(parent, "单词测试", true);
        this.quizWords = new ArrayList<>(words);
        Collections.shuffle(quizWords);
        initializeGUI();
        setLocationRelativeTo(parent);
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());
        setSize(500, 300);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 247, 250));

        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        questionLabel.setForeground(new Color(52, 73, 94));

        answerField = new JTextField();
        answerField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        answerField.addActionListener(e -> checkAnswer());

        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JPanel answerPanel = new JPanel(new BorderLayout(10, 10));
        answerPanel.setBackground(new Color(245, 247, 250));
        answerPanel.add(new JLabel("请输入释义:"), BorderLayout.WEST);
        answerPanel.add(answerField, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.add(questionLabel);
        centerPanel.add(answerPanel);
        centerPanel.add(resultLabel);

        nextButton = createButton("下一题", new Color(39, 174, 96), e -> nextQuestion());
        nextButton.setEnabled(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.add(nextButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        nextQuestion();
    }

    private JButton createButton(String text, Color bgColor, java.util.function.Consumer<ActionEvent> action) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addActionListener(action::accept);
        return button;
    }

    private void nextQuestion() {
        if (currentIndex >= quizWords.size()) {
            showResult();
            return;
        }

        currentWord = quizWords.get(currentIndex);
        questionLabel.setText(currentWord.word);
        answerField.setText("");
        answerField.setEditable(true);
        resultLabel.setText("");
        nextButton.setEnabled(false);
        answerField.requestFocus();
    }

    private void checkAnswer() {
        String userAnswer = answerField.getText().trim();
        if (userAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入答案", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isCorrect = userAnswer.equalsIgnoreCase(currentWord.meaning) ||
                           currentWord.meaning.toLowerCase().contains(userAnswer.toLowerCase());

        if (isCorrect) {
            correctCount++;
            resultLabel.setText("✅ 正确！");
            resultLabel.setForeground(new Color(39, 174, 96));
        } else {
            resultLabel.setText("❌ 错误！正确答案是: " + currentWord.meaning);
            resultLabel.setForeground(new Color(231, 76, 60));
        }

        answerField.setEditable(false);
        nextButton.setEnabled(true);
        currentIndex++;
    }

    private void showResult() {
        double accuracy = (double) correctCount / quizWords.size() * 100;
        String message = String.format("测试完成！\n\n正确率: %.1f%%\n正确题数: %d/%d",
                                     accuracy, correctCount, quizWords.size());

        int result = JOptionPane.showConfirmDialog(this, message + "\n\n是否重新测试？",
                                                  "测试结果", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            currentIndex = 0;
            correctCount = 0;
            Collections.shuffle(quizWords);
            nextQuestion();
        } else {
            dispose();
        }
    }
}