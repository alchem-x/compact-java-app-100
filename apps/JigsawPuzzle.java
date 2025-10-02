import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🧩 拼图游戏";

    // 界面标签
    static final String STATUS_LABEL = "状态: ";
    static final String MOVES_LABEL = "步数: ";
    static final String LOAD_IMAGE_BUTTON = "加载图片";
    static final String SHUFFLE_BUTTON = "打乱";
    static final String PREVIEW_BUTTON = "预览";
    static final String DIFFICULTY_LABEL = "难度: ";

    // 难度选项
    static final String DIFFICULTY_EASY = "简单 (3x3)";
    static final String DIFFICULTY_MEDIUM = "中等 (4x4)";
    static final String DIFFICULTY_HARD = "困难 (5x5)";

    // 状态消息
    static final String STATUS_START_GAME = "点击加载图片开始游戏";
    static final String STATUS_PUZZLE_COMPLETED = "🎉 恭喜！拼图完成！";
    static final String STATUS_SELECT_PIECE = "请选择一块拼图";
    static final String STATUS_PIECE_PLACED = "拼图放置成功";
    static final String STATUS_WRONG_PLACE = "位置不正确，请重试";
    static final String STATUS_PREVIEW_ON = "预览模式开启";
    static final String STATUS_PREVIEW_OFF = "预览模式关闭";
    static final String STATUS_PUZZLE_SHUFFLED = "拼图已打乱";
    static final String STATUS_IMAGE_LOADED = "图片加载成功";
    static final String STATUS_IMAGE_LOAD_FAILED = "图片加载失败";

    // 文件对话框
    static final String IMAGE_FILE_FILTER = "图片文件";
    static final String SUPPORTED_FORMATS = "jpg,jpeg,png,gif,bmp";

    // 完成消息
    static final String COMPLETION_MESSAGE = "拼图完成！\n用时: %d 步";
    static final String COMPLETION_TITLE = "恭喜完成";

    // 帮助信息
    static final String HELP_MESSAGE = """
        拼图游戏使用说明：

        • 游戏目标：将打乱的图片片段重新拼成完整的图片
        • 游戏规则：将拼图块拖拽到正确的位置
        • 计分规则：移动步数越少，完成时间越短越好

        操作说明：
        • 鼠标点击：选择拼图块
        • 鼠标拖拽：移动拼图块
        • 预览按钮：查看完整图片
        • 打乱按钮：重新打乱拼图

        游戏技巧：
        • 先找到边缘和角落的拼图块
        • 按颜色或图案特征分类拼图块
        • 从边缘向中心逐步拼接
        • 利用预览功能参考原图

        难度说明：
        • 简单：3x3网格，9个拼图块
        • 中等：4x4网格，16个拼图块
        • 困难：5x5网格，25个拼图块

        快捷键：
        Ctrl+L - 加载图片
        Ctrl+S - 打乱拼图
        Ctrl+P - 切换预览
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new JigsawPuzzle().setVisible(true);
    });
}

static class JigsawPuzzle extends JFrame {
    private static final int GRID_SIZE = 4; // 4x4拼图
    private static final int PIECE_SIZE = 100;
    private static final int PANEL_WIDTH = GRID_SIZE * PIECE_SIZE;
    private static final int PANEL_HEIGHT = GRID_SIZE * PIECE_SIZE;

    private JPanel puzzlePanel;
    private JPanel piecesPanel;
    private JLabel statusLabel;
    private JLabel movesLabel;
    private JButton loadImageButton;
    private JButton shuffleButton;
    private JButton previewButton;
    private JComboBox<String> difficultyCombo;

    private BufferedImage originalImage;
    private List<PuzzlePiece> pieces;
    private List<PuzzlePiece> placedPieces;
    private PuzzlePiece selectedPiece = null;
    private Point selectedPoint = null;
    private boolean showPreview = false;
    private int moves = 0;
    private int currentDifficulty = 0;

    public JigsawPuzzle() {
        initializeGUI();
        createDefaultPuzzle();
        setupKeyboardShortcuts();
        setLocationRelativeTo(null);
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(new Color(245, 247, 250));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadImageButton = createButton(Texts.LOAD_IMAGE_BUTTON, new Color(39, 174, 96), e -> loadImage());
        shuffleButton = createButton(Texts.SHUFFLE_BUTTON, new Color(243, 156, 18), e -> shufflePieces());
        previewButton = createButton(Texts.PREVIEW_BUTTON, new Color(52, 152, 219), e -> togglePreview());

        String[] difficulties = {Texts.DIFFICULTY_EASY, Texts.DIFFICULTY_MEDIUM, Texts.DIFFICULTY_HARD};
        difficultyCombo = new JComboBox<String>(difficulties);
        difficultyCombo.addActionListener(e -> changeDifficulty());

        controlPanel.add(new JLabel(Texts.DIFFICULTY_LABEL));
        controlPanel.add(difficultyCombo);
        controlPanel.add(loadImageButton);
        controlPanel.add(shuffleButton);
        controlPanel.add(previewButton);

        // 状态面板
        JPanel statusPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        statusPanel.setBackground(new Color(245, 247, 250));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel(Texts.STATUS_START_GAME, SwingConstants.CENTER);
        statusLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        statusLabel.setForeground(new Color(52, 73, 94));

        movesLabel = new JLabel(Texts.MOVES_LABEL + "0", SwingConstants.CENTER);
        movesLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        movesLabel.setForeground(new Color(127, 140, 141));

        statusPanel.add(statusLabel);
        statusPanel.add(movesLabel);

        // 主游戏区域
        JPanel gamePanel = new JPanel(new BorderLayout(10, 10));
        gamePanel.setBackground(new Color(245, 247, 250));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 拼图面板
        puzzlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPuzzle(g);
            }
        };
        puzzlePanel.setPreferredSize(new Dimension(PANEL_WIDTH + 20, PANEL_HEIGHT + 20));
        puzzlePanel.setBackground(Color.WHITE);
        puzzlePanel.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        puzzlePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handlePuzzleClick(e);
            }
        });

        // 拼图块面板
        piecesPanel = new JPanel();
        piecesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        piecesPanel.setBackground(new Color(245, 247, 250));
        piecesPanel.setBorder(BorderFactory.createTitledBorder(Texts.STATUS_LABEL.substring(0, Texts.STATUS_LABEL.length() - 2)));

        JScrollPane piecesScrollPane = new JScrollPane(piecesPanel);
        piecesScrollPane.setPreferredSize(new Dimension(PANEL_WIDTH + 40, 200));
        piecesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        piecesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        gamePanel.add(puzzlePanel, BorderLayout.CENTER);
        gamePanel.add(piecesScrollPane, BorderLayout.SOUTH);

        // 组装界面
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(controlPanel, BorderLayout.NORTH);
        topContainer.add(statusPanel, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        setSize(900, 700);
    }

    private JButton createButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        return button;
    }

    private void createDefaultPuzzle() {
        // 创建默认的彩色拼图
        int width = GRID_SIZE * PIECE_SIZE;
        int height = GRID_SIZE * PIECE_SIZE;
        originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = originalImage.createGraphics();

        // 绘制彩色渐变图案
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int red = (int) (Math.random() * 256);
                int green = (int) (Math.random() * 256);
                int blue = (int) (Math.random() * 256);
                g2d.setColor(new Color(red, green, blue));
                g2d.fillRect(j * PIECE_SIZE, i * PIECE_SIZE, PIECE_SIZE, PIECE_SIZE);

                // 添加边框和数字
                g2d.setColor(Color.BLACK);
                g2d.drawRect(j * PIECE_SIZE, i * PIECE_SIZE, PIECE_SIZE, PIECE_SIZE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                String text = String.valueOf(i * GRID_SIZE + j + 1);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, j * PIECE_SIZE + (PIECE_SIZE - textWidth) / 2,
                        i * PIECE_SIZE + (PIECE_SIZE + textHeight) / 2 - fm.getDescent());
            }
        }

        g2d.dispose();
        createPuzzlePieces();
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "图片文件", "jpg", "jpeg", "png", "gif", "bmp"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(file);
                if (image != null) {
                    // 调整图片大小
                    Image scaledImage = image.getScaledInstance(PANEL_WIDTH, PANEL_HEIGHT, Image.SCALE_SMOOTH);
                    originalImage = new BufferedImage(PANEL_WIDTH, PANEL_HEIGHT, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = originalImage.createGraphics();
                    g2d.drawImage(scaledImage, 0, 0, null);
                    g2d.dispose();

                    createPuzzlePieces();
                    statusLabel.setText(Texts.STATUS_IMAGE_LOADED + "！");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法加载图片: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createPuzzlePieces() {
        pieces = new ArrayList<PuzzlePiece>();
        placedPieces = new ArrayList<PuzzlePiece>();
        selectedPiece = null;
        moves = 0;

        int gridSize = getCurrentGridSize();
        int pieceWidth = PANEL_WIDTH / gridSize;
        int pieceHeight = PANEL_HEIGHT / gridSize;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int x = col * pieceWidth;
                int y = row * pieceHeight;
                BufferedImage pieceImage = originalImage.getSubimage(x, y, pieceWidth, pieceHeight);

                PuzzlePiece piece = new PuzzlePiece(pieceImage, row, col, row * gridSize + col);
                pieces.add(piece);
            }
        }

        shufflePieces();
        updatePiecesPanel();
        updateDisplay();
    }

    private int getCurrentGridSize() {
        return 4 + currentDifficulty; // 4x4, 5x5, 6x6
    }

    private void shufflePieces() {
        Collections.shuffle(pieces);
    }

    private void updatePiecesPanel() {
        piecesPanel.removeAll();

        for (PuzzlePiece piece : pieces) {
            JButton pieceButton = new JButton(new ImageIcon(piece.image));
            pieceButton.setPreferredSize(new Dimension(80, 80));
            pieceButton.addActionListener(e -> selectPiece(piece));
            piecesPanel.add(pieceButton);
        }

        piecesPanel.revalidate();
        piecesPanel.repaint();
    }

    private void selectPiece(PuzzlePiece piece) {
        if (selectedPiece == piece) {
            selectedPiece = null;
            selectedPoint = null;
            puzzlePanel.repaint();
            return;
        }

        selectedPiece = piece;
        puzzlePanel.repaint();
        statusLabel.setText(Texts.STATUS_SELECT_PIECE);
    }

    private void handlePuzzleClick(MouseEvent e) {
        if (selectedPiece == null) {
            statusLabel.setText(Texts.STATUS_SELECT_PIECE);
            return;
        }

        int gridSize = getCurrentGridSize();
        int pieceWidth = PANEL_WIDTH / gridSize;
        int pieceHeight = PANEL_HEIGHT / gridSize;

        int col = (e.getX() - 10) / pieceWidth;
        int row = (e.getY() - 10) / pieceHeight;

        if (row >= 0 && row < gridSize && col >= 0 && col < gridSize) {
            placePiece(selectedPiece, row, col);
        }
    }

    private void placePiece(PuzzlePiece piece, int row, int col) {
        if (piece.correctRow == row && piece.correctCol == col) {
            // 放置正确
            piece.currentRow = row;
            piece.currentCol = col;
            piece.isPlaced = true;
            pieces.remove(piece);
            placedPieces.add(piece);

            moves++;
            updateDisplay();
            updatePiecesPanel();

            statusLabel.setText(Texts.STATUS_PIECE_PLACED + "！");
            statusLabel.setForeground(new Color(39, 174, 96));

            selectedPiece = null;
            puzzlePanel.repaint();

            // 检查是否完成
            checkCompletion();
        } else {
            // 放置错误
            statusLabel.setText(Texts.STATUS_WRONG_PLACE);
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }

    private void checkCompletion() {
        if (pieces.isEmpty()) {
            // 拼图完成
            statusLabel.setText(Texts.STATUS_PUZZLE_COMPLETED);
            statusLabel.setForeground(new Color(39, 174, 96));

            JOptionPane.showMessageDialog(this,
                    String.format("拼图完成！\n移动次数: %d", moves),
                    "完成", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void changeDifficulty() {
        currentDifficulty = difficultyCombo.getSelectedIndex();
        if (originalImage != null) {
            createPuzzlePieces();
        }
    }

    private void togglePreview() {
        showPreview = !showPreview;
        previewButton.setText(showPreview ? "隐藏预览" : "显示预览");
        puzzlePanel.repaint();
    }

    private void updateDisplay() {
        movesLabel.setText(String.format("移动次数: %d", moves));
    }

    private void drawPuzzle(Graphics g) {
        if (originalImage == null) return;

        int gridSize = getCurrentGridSize();
        int pieceWidth = PANEL_WIDTH / gridSize;
        int pieceHeight = PANEL_HEIGHT / gridSize;

        // 绘制网格
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i <= gridSize; i++) {
            g.drawLine(i * pieceWidth + 10, 10, i * pieceWidth + 10, PANEL_HEIGHT + 10);
            g.drawLine(10, i * pieceHeight + 10, PANEL_WIDTH + 10, i * pieceHeight + 10);
        }

        // 绘制已放置的拼图块
        for (PuzzlePiece piece : placedPieces) {
            int x = piece.currentCol * pieceWidth + 10;
            int y = piece.currentRow * pieceHeight + 10;
            g.drawImage(piece.image, x, y, null);

            // 高亮显示选中的拼图块
            if (piece == selectedPiece) {
                g.setColor(new Color(255, 255, 0, 100));
                g.fillRect(x, y, pieceWidth, pieceHeight);
            }
        }

        // 显示预览
        if (showPreview) {
            g.setColor(new Color(0, 0, 0, 50));
            g.drawImage(originalImage, 10, 10, PANEL_WIDTH, PANEL_HEIGHT, null);
        }
    }

    private static class PuzzlePiece {
        BufferedImage image;
        int correctRow;
        int correctCol;
        int id;
        int currentRow = -1;
        int currentCol = -1;
        boolean isPlaced = false;

        PuzzlePiece(BufferedImage image, int correctRow, int correctCol, int id) {
            this.image = image;
            this.correctRow = correctRow;
            this.correctCol = correctCol;
            this.id = id;
        }
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_L:
                        // Ctrl+L 加载图片
                        if (ev.isControlDown()) {
                            loadImage();
                        }
                        break;
                    case KeyEvent.VK_S:
                        // Ctrl+S 打乱拼图
                        if (ev.isControlDown()) {
                            shufflePieces();
                        }
                        break;
                    case KeyEvent.VK_P:
                        // Ctrl+P 切换预览
                        if (ev.isControlDown()) {
                            togglePreview();
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