import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Random;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new IconGenerator().setVisible(true));
}

class IconGenerator extends JFrame {
    private JPanel previewPanel;
    private JComboBox<Integer> sizeCombo, complexityCombo;
    private JButton randomizeButton, saveButton;
    private BufferedImage currentIcon;
    private Random random = new Random();

    // 简化颜色系统
    private static final Color[] PALETTE = {
        new Color(0, 122, 255),     // Blue
        new Color(52, 199, 89),     // Green
        new Color(255, 59, 48),     // Red
        new Color(255, 149, 0),     // Orange
        new Color(175, 82, 222),    // Purple
        new Color(48, 176, 199),    // Teal
        new Color(255, 204, 0),     // Yellow
        new Color(255, 45, 85),     // Pink
        new Color(88, 86, 214)      // Indigo
    };

    public IconGenerator() {
        setTitle("图标生成器");
        setSize(500, 450); // 增加高度确保足够空间
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 使用BorderLayout确保组件不会被压缩
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 控制面板 - 使用垂直布局
        var controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.WHITE);

        // 标题
        var title = new JLabel("图标生成器");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        controlPanel.add(title);

        // 控制组件
        var sizePanel = createCombo("尺寸:", new Integer[]{64, 128}, 128);
        var complexityPanel = createCombo("复杂度:", new Integer[]{4, 6, 8}, 8);

        controlPanel.add(sizePanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(complexityPanel);
        controlPanel.add(Box.createVerticalStrut(20)); // 按钮上方间距

        // 按钮面板 - 使用合适的布局
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        randomizeButton = createButton("随机生成", new Color(0, 122, 255), Color.WHITE);
        saveButton = createButton("保存图标", Color.WHITE, Color.BLACK);

        buttonPanel.add(randomizeButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);

        controlPanel.add(buttonPanel);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 预览面板 - 使用合适的尺寸和边距
        previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentIcon != null) {
                    int x = (getWidth() - currentIcon.getWidth()) / 2;
                    int y = (getHeight() - currentIcon.getHeight()) / 2;
                    g.drawImage(currentIcon, x, y, null);
                }
            }
        };
        previewPanel.setBackground(Color.WHITE);
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // 使用合适的尺寸
        previewPanel.setPreferredSize(new Dimension(240, 240));
        previewPanel.setMinimumSize(new Dimension(200, 200));

        mainPanel.add(previewPanel, BorderLayout.CENTER);
        add(mainPanel);

        generateRandomIcon();
    }

    private JPanel createCombo(String label, Integer[] items, int defaultItem) {
        var panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(300, 40));

        var labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 16));
        labelComponent.setPreferredSize(new Dimension(60, 30));

        var combo = new JComboBox<Integer>(items);
        combo.setSelectedItem(defaultItem);
        combo.setFont(new Font("Arial", Font.PLAIN, 16));
        combo.setBackground(Color.WHITE);
        combo.addActionListener(e -> generateRandomIcon());

        panel.add(labelComponent, BorderLayout.WEST);
        panel.add(combo, BorderLayout.CENTER);

        if (label.equals("尺寸:")) sizeCombo = combo;
        if (label.equals("复杂度:")) complexityCombo = combo;

        return panel;
    }

    private JButton createButton(String text, Color bgColor, Color fgColor) {
        var button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                var g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2d.dispose();
            }
        };

        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(120, 40));

        if (text.equals("随机生成")) {
            randomizeButton = button;
            button.addActionListener(e -> generateRandomIcon());
        } else {
            saveButton = button;
            button.addActionListener(e -> saveIcon());
        }

        return button;
    }

    private void generateRandomIcon() {
        int size = (Integer) sizeCombo.getSelectedItem();
        int complexity = (Integer) complexityCombo.getSelectedItem();

        currentIcon = createSymmetricIcon(size, complexity);
        previewPanel.repaint();
    }

    private BufferedImage createSymmetricIcon(int size, int complexity) {
        var image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        var g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(0, 0, size, size, 8, 8);

        // 随机颜色
        var color = PALETTE[random.nextInt(PALETTE.length)];
        g2d.setColor(color);

        // 生成对称图案
        int cellSize = size / complexity;
        boolean[][] pattern = generateSymmetricPattern(complexity);

        // 绘制图案
        for (int row = 0; row < complexity; row++) {
            for (int col = 0; col < complexity; col++) {
                if (pattern[row][col]) {
                    g2d.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                }
            }
        }

        // 边框
        g2d.setColor(new Color(230, 230, 230));
        g2d.drawRoundRect(0, 0, size - 1, size - 1, 8, 8);

        g2d.dispose();
        return image;
    }

    private boolean[][] generateSymmetricPattern(int complexity) {
        boolean[][] pattern = new boolean[complexity][complexity];

        // 只在左半部分生成
        for (int row = 0; row < complexity; row++) {
            for (int col = 0; col <= complexity / 2; col++) {
                double prob = 0.4; // 基础概率

                // 连通性奖励
                if (row > 0 && pattern[row-1][col]) prob += 0.3;
                if (col > 0 && pattern[row][col-1]) prob += 0.2;

                if (random.nextDouble() < Math.min(prob, 0.8)) {
                    pattern[row][col] = true;
                }
            }
        }

        // 镜像到右半部分
        for (int row = 0; row < complexity; row++) {
            for (int col = 0; col < complexity / 2; col++) {
                pattern[row][complexity - 1 - col] = pattern[row][col];
            }
        }

        return pattern;
    }

    private void saveIcon() {
        if (currentIcon == null) {
            JOptionPane.showMessageDialog(this, "请先生成图标");
            return;
        }

        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG图片", "png"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                ImageIO.write(currentIcon, "png", file);
                JOptionPane.showMessageDialog(this, "图标已保存到: " + file.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage());
            }
        }
    }
}