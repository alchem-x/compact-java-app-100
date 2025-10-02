import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Sudoku().setVisible(true);
    });
}

static class Sudoku extends JFrame {
    private static final int SIZE = 9;
    private JTextField[][] cells;
    private int[][] solution;
    private int[][] puzzle;
    private boolean[][] isFixed;
    private JLabel statusLabel;
    private Timer gameTimer;
    private int seconds = 0;
    private JLabel timerLabel;
    
    public Sudoku() {
        initializeGUI();
        generateNewPuzzle();
    }
    
    private void initializeGUI() {
        setTitle("数独游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 顶部控制面板
        JPanel topPanel = new JPanel(new FlowLayout());
        
        JButton newGameButton = new JButton("🎮 新游戏");
        newGameButton.addActionListener(e -> generateNewPuzzle());
        
        JButton solveButton = new JButton("💡 显示答案");
        solveButton.addActionListener(e -> showSolution());
        
        JButton checkButton = new JButton("✅ 检查");
        checkButton.addActionListener(e -> checkSolution());
        
        JButton hintButton = new JButton("💭 提示");
        hintButton.addActionListener(e -> giveHint());
        
        JComboBox<String> difficultyCombo = new JComboBox<>(new String[]{"简单", "中等", "困难"});
        difficultyCombo.addActionListener(e -> generateNewPuzzle());
        
        timerLabel = new JLabel("时间: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        topPanel.add(newGameButton);
        topPanel.add(solveButton);
        topPanel.add(checkButton);
        topPanel.add(hintButton);
        topPanel.add(new JLabel("难度:"));
        topPanel.add(difficultyCombo);
        topPanel.add(timerLabel);
        
        // 游戏面板
        JPanel gamePanel = new JPanel(new GridLayout(SIZE, SIZE, 1, 1));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gamePanel.setBackground(Color.BLACK);
        
        cells = new JTextField[SIZE][SIZE];
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                cells[row][col].setPreferredSize(new Dimension(50, 50));
                
                // 设置边框以显示3x3格子
                int top = (row % 3 == 0) ? 3 : 1;
                int left = (col % 3 == 0) ? 3 : 1;
                int bottom = (row % 3 == 2) ? 3 : 1;
                int right = (col % 3 == 2) ? 3 : 1;
                
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
                
                // 添加输入验证
                final int r = row, c = col;
                cells[row][col].addActionListener(e -> validateInput(r, c));
                
                gamePanel.add(cells[row][col]);
            }
        }
        
        // 底部状态面板
        statusLabel = new JLabel("准备开始游戏");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        // 创建计时器
        gameTimer = new Timer(1000, e -> {
            seconds++;
            int minutes = seconds / 60;
            int secs = seconds % 60;
            timerLabel.setText(String.format("时间: %02d:%02d", minutes, secs));
        });
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void generateNewPuzzle() {
        solution = new int[SIZE][SIZE];
        puzzle = new int[SIZE][SIZE];
        isFixed = new boolean[SIZE][SIZE];
        
        // 生成完整的数独解答
        generateSolution();
        
        // 从解答中移除一些数字创建谜题
        createPuzzle();
        
        // 显示谜题
        displayPuzzle();
        
        // 重置计时器
        seconds = 0;
        gameTimer.restart();
        
        statusLabel.setText("游戏开始！填入1-9的数字");
    }
    
    private void generateSolution() {
        // 简单的数独生成算法
        fillDiagonal();
        solveSudoku(solution);
    }
    
    private void fillDiagonal() {
        // 填充对角线上的3x3方格
        for (int i = 0; i < SIZE; i += 3) {
            fillBox(i, i);
        }
    }
    
    private void fillBox(int row, int col) {
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int num;
                do {
                    num = random.nextInt(9) + 1;
                } while (!isValidInBox(row, col, num));
                solution[row + i][col + j] = num;
            }
        }
    }
    
    private boolean isValidInBox(int boxRow, int boxCol, int num) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (solution[boxRow + i][boxCol + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean solveSudoku(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;
                            if (solveSudoku(board)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isValid(int[][] board, int row, int col, int num) {
        // 检查行
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }
        
        // 检查列
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }
        
        // 检查3x3方格
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private void createPuzzle() {
        // 复制解答到谜题
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(solution[i], 0, puzzle[i], 0, SIZE);
        }
        
        // 随机移除一些数字
        Random random = new Random();
        int cellsToRemove = 40; // 可以根据难度调整
        
        while (cellsToRemove > 0) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            
            if (puzzle[row][col] != 0) {
                puzzle[row][col] = 0;
                cellsToRemove--;
            }
        }
        
        // 标记固定的数字
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                isFixed[i][j] = puzzle[i][j] != 0;
            }
        }
    }
    
    private void displayPuzzle() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JTextField cell = cells[row][col];
                
                if (puzzle[row][col] != 0) {
                    cell.setText(String.valueOf(puzzle[row][col]));
                    cell.setEditable(false);
                    cell.setBackground(Color.LIGHT_GRAY);
                    cell.setForeground(Color.BLACK);
                } else {
                    cell.setText("");
                    cell.setEditable(true);
                    cell.setBackground(Color.WHITE);
                    cell.setForeground(Color.BLUE);
                }
            }
        }
    }
    
    private void validateInput(int row, int col) {
        JTextField cell = cells[row][col];
        String text = cell.getText().trim();
        
        if (text.isEmpty()) {
            return;
        }
        
        try {
            int num = Integer.parseInt(text);
            if (num < 1 || num > 9) {
                cell.setText("");
                statusLabel.setText("请输入1-9之间的数字");
                return;
            }
            
            // 检查是否有效
            int[][] currentBoard = getCurrentBoard();
            currentBoard[row][col] = 0; // 临时清空当前位置
            
            if (!isValid(currentBoard, row, col, num)) {
                cell.setBackground(Color.PINK);
                statusLabel.setText("数字冲突！请检查行、列或3x3方格");
            } else {
                cell.setBackground(Color.WHITE);
                statusLabel.setText("输入有效");
                
                // 检查是否完成
                if (isPuzzleComplete()) {
                    gameTimer.stop();
                    statusLabel.setText("恭喜！您完成了数独！");
                    JOptionPane.showMessageDialog(this, 
                        String.format("恭喜完成！用时: %02d:%02d", seconds / 60, seconds % 60),
                        "游戏完成", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            cell.setText("");
            statusLabel.setText("请输入有效数字");
        }
    }
    
    private int[][] getCurrentBoard() {
        int[][] board = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = cells[row][col].getText().trim();
                if (!text.isEmpty()) {
                    try {
                        board[row][col] = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        board[row][col] = 0;
                    }
                } else {
                    board[row][col] = 0;
                }
            }
        }
        return board;
    }
    
    private boolean isPuzzleComplete() {
        int[][] board = getCurrentBoard();
        
        // 检查是否所有格子都填满
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        
        // 检查是否所有数字都有效
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int num = board[row][col];
                board[row][col] = 0; // 临时清空
                if (!isValid(board, row, col, num)) {
                    board[row][col] = num; // 恢复
                    return false;
                }
                board[row][col] = num; // 恢复
            }
        }
        
        return true;
    }
    
    private void showSolution() {
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要显示答案吗？这将结束当前游戏。", 
            "显示答案", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    cells[row][col].setText(String.valueOf(solution[row][col]));
                    if (!isFixed[row][col]) {
                        cells[row][col].setForeground(Color.RED);
                    }
                }
            }
            gameTimer.stop();
            statusLabel.setText("已显示完整答案");
        }
    }
    
    private void checkSolution() {
        int[][] board = getCurrentBoard();
        int errors = 0;
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] != 0) {
                    int num = board[row][col];
                    board[row][col] = 0;
                    if (!isValid(board, row, col, num)) {
                        cells[row][col].setBackground(Color.PINK);
                        errors++;
                    } else {
                        cells[row][col].setBackground(Color.WHITE);
                    }
                    board[row][col] = num;
                }
            }
        }
        
        if (errors == 0) {
            statusLabel.setText("目前没有错误！");
        } else {
            statusLabel.setText("发现 " + errors + " 个错误，已用粉色标出");
        }
    }
    
    private void giveHint() {
        // 找一个空格子并填入正确答案
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!isFixed[row][col] && cells[row][col].getText().trim().isEmpty()) {
                    cells[row][col].setText(String.valueOf(solution[row][col]));
                    cells[row][col].setForeground(Color.GREEN);
                    cells[row][col].setEditable(false);
                    statusLabel.setText("已给出提示");
                    return;
                }
            }
        }
        statusLabel.setText("没有可以提示的位置");
    }
}
