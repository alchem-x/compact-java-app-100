import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * 随机决策器
 * 帮助用户在多个选项中随机选择，支持抽签、转盘、骰子等多种决策方式
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new RandomDecision().setVisible(true);
    });
}

class RandomDecision extends JFrame {
    private List<String> options = new ArrayList<>();
    private DecisionMode currentMode = DecisionMode.LOTTERY;
    
    private JTextArea optionsArea;
    private JLabel resultLabel;
    private JButton decideButton;
    private JComboBox<DecisionMode> modeComboBox;
    private JPanel visualPanel;
    private JSlider probabilitySlider;
    private JLabel probabilityLabel;
    
    private Random random = new Random();
    private javax.swing.Timer animationTimer;
    private int animationStep = 0;
    
    public RandomDecision() {
        initializeUI();
        loadDefaultOptions();
    }
    
    private void initializeUI() {
        setTitle("随机决策器 - Random Decision Maker");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        
        // 顶部控制面板
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // 中央分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // 左侧选项输入面板
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // 右侧可视化面板
        visualPanel = new JPanel();
        visualPanel.setBackground(Color.WHITE);
        visualPanel.setBorder(BorderFactory.createTitledBorder("决策可视化"));
        splitPane.setRightComponent(visualPanel);
        
        splitPane.setDividerLocation(350);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // 底部结果面板
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        updateVisualPanel();
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JLabel modeLabel = new JLabel("决策模式:");
        modeComboBox = new JComboBox<>(DecisionMode.values());
        modeComboBox.addActionListener(e -> {
            currentMode = (DecisionMode) modeComboBox.getSelectedItem();
            updateVisualPanel();
        });
        
        JButton addPresetBtn = new JButton("添加预设");
        addPresetBtn.addActionListener(e -> showPresetDialog());
        
        JButton clearBtn = new JButton("清空选项");
        clearBtn.addActionListener(e -> clearOptions());
        
        panel.add(modeLabel);
        panel.add(modeComboBox);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(addPresetBtn);
        panel.add(clearBtn);
        
        return panel;
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("选项设置"));
        
        // 选项输入区域
        optionsArea = new JTextArea(10, 20);
        optionsArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        optionsArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(optionsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("输入选项（每行一个）"));
        
        // 概率设置
        JPanel probabilityPanel = new JPanel(new FlowLayout());
        probabilityPanel.setBorder(BorderFactory.createTitledBorder("随机性设置"));
        
        JLabel probLabel = new JLabel("随机程度:");
        probabilitySlider = new JSlider(1, 100, 50);
        probabilitySlider.addChangeListener(e -> updateProbabilityLabel());
        
        probabilityLabel = new JLabel("50%");
        updateProbabilityLabel();
        
        probabilityPanel.add(probLabel);
        probabilityPanel.add(probabilitySlider);
        probabilityPanel.add(probabilityLabel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton updateBtn = new JButton("更新选项");
        updateBtn.addActionListener(e -> updateOptions());
        
        JButton shuffleBtn = new JButton("打乱顺序");
        shuffleBtn.addActionListener(e -> shuffleOptions());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(shuffleBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(probabilityPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 决策按钮
        decideButton = new JButton("开始决策！");
        decideButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        decideButton.setPreferredSize(new Dimension(150, 40));
        decideButton.addActionListener(e -> makeDecision());
        
        // 结果显示
        resultLabel = new JLabel("点击按钮开始决策", SwingConstants.CENTER);
        resultLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        resultLabel.setForeground(Color.BLUE);
        resultLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel buttonContainer = new JPanel(new FlowLayout());
        buttonContainer.add(decideButton);
        
        panel.add(buttonContainer, BorderLayout.WEST);
        panel.add(resultLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadDefaultOptions() {
        String defaultText = "选项1\n选项2\n选项3\n选项4";
        optionsArea.setText(defaultText);
        updateOptions();
    }
    
    private void updateOptions() {
        options.clear();
        String[] lines = optionsArea.getText().split("\n");
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                options.add(trimmed);
            }
        }
        
        updateVisualPanel();
        
        if (options.isEmpty()) {
            decideButton.setEnabled(false);
            resultLabel.setText("请先添加选项");
        } else {
            decideButton.setEnabled(true);
            resultLabel.setText("共 " + options.size() + " 个选项，点击按钮开始决策");
        }
    }
    
    private void shuffleOptions() {
        Collections.shuffle(options);
        updateOptionsDisplay();
        updateVisualPanel();
    }
    
    private void updateOptionsDisplay() {
        StringBuilder sb = new StringBuilder();
        for (String option : options) {
            sb.append(option).append("\n");
        }
        optionsArea.setText(sb.toString());
    }
    
    private void clearOptions() {
        options.clear();
        optionsArea.setText("");
        updateVisualPanel();
        decideButton.setEnabled(false);
        resultLabel.setText("请先添加选项");
    }
    
    private void updateProbabilityLabel() {
        int value = probabilitySlider.getValue();
        probabilityLabel.setText(value + "%");
    }
    
    private void makeDecision() {
        if (options.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先添加选项！");
            return;
        }
        
        decideButton.setEnabled(false);
        resultLabel.setText("决策中...");
        
        // 开始动画
        animationStep = 0;
        animationTimer = new javax.swing.Timer(100, e -> animateDecision());
        animationTimer.start();
    }
    
    private void animateDecision() {
        animationStep++;
        
        if (animationStep <= 20) {
            // 动画阶段：快速切换选项
            String randomOption = options.get(random.nextInt(options.size()));
            resultLabel.setText("🎲 " + randomOption + " 🎲");
            updateVisualAnimation();
        } else {
            // 动画结束，显示最终结果
            animationTimer.stop();
            String finalChoice = selectFinalOption();
            showFinalResult(finalChoice);
            decideButton.setEnabled(true);
        }
    }
    
    private String selectFinalOption() {
        // 根据概率设置选择结果
        int probability = probabilitySlider.getValue();
        
        if (probability >= 90 || options.size() == 1) {
            // 高随机性或只有一个选项
            return options.get(random.nextInt(options.size()));
        } else {
            // 根据概率调整选择权重
            List<String> weightedOptions = new ArrayList<>();
            
            for (int i = 0; i < options.size(); i++) {
                int weight = probability + random.nextInt(100 - probability);
                for (int j = 0; j < weight; j++) {
                    weightedOptions.add(options.get(i));
                }
            }
            
            return weightedOptions.get(random.nextInt(weightedOptions.size()));
        }
    }
    
    private void showFinalResult(String result) {
        resultLabel.setText("🎉 决策结果：" + result + " 🎉");
        resultLabel.setForeground(Color.RED);
        
        // 播放提示音
        Toolkit.getDefaultToolkit().beep();
        
        // 显示详细结果对话框
        String message = String.format(
            "决策完成！\n\n选中结果：%s\n\n总选项数：%d\n随机程度：%d%%\n决策模式：%s",
            result, options.size(), probabilitySlider.getValue(), currentMode.description
        );
        
        var delayTimer = new javax.swing.Timer(1000, e -> {
            JOptionPane.showMessageDialog(this, message, "决策结果", JOptionPane.INFORMATION_MESSAGE);
            resultLabel.setForeground(Color.BLUE);
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
    
    private void updateVisualPanel() {
        visualPanel.removeAll();
        
        switch (currentMode) {
            case LOTTERY -> createLotteryVisual();
            case WHEEL -> createWheelVisual();
            case DICE -> createDiceVisual();
            case CARD -> createCardVisual();
        }
        
        visualPanel.revalidate();
        visualPanel.repaint();
    }
    
    private void createLotteryVisual() {
        visualPanel.setLayout(new GridLayout(0, 1, 5, 5));
        visualPanel.setBorder(BorderFactory.createTitledBorder("抽签模式"));
        
        for (int i = 0; i < Math.min(options.size(), 8); i++) {
            JPanel ticketPanel = new JPanel(new BorderLayout());
            ticketPanel.setBackground(new Color(255, 248, 220));
            ticketPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
            
            JLabel ticketLabel = new JLabel("🎫 " + options.get(i), SwingConstants.CENTER);
            ticketLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            ticketPanel.add(ticketLabel);
            
            visualPanel.add(ticketPanel);
        }
        
        if (options.size() > 8) {
            JLabel moreLabel = new JLabel("... 还有 " + (options.size() - 8) + " 个选项", SwingConstants.CENTER);
            moreLabel.setFont(new Font("微软雅黑", Font.ITALIC, 10));
            visualPanel.add(moreLabel);
        }
    }
    
    private void createWheelVisual() {
        visualPanel.setLayout(new BorderLayout());
        visualPanel.setBorder(BorderFactory.createTitledBorder("转盘模式"));
        
        WheelPanel wheelPanel = new WheelPanel();
        visualPanel.add(wheelPanel, BorderLayout.CENTER);
        
        JLabel instructionLabel = new JLabel("点击决策按钮转动转盘", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        visualPanel.add(instructionLabel, BorderLayout.SOUTH);
    }
    
    private void createDiceVisual() {
        visualPanel.setLayout(new GridLayout(2, 3, 10, 10));
        visualPanel.setBorder(BorderFactory.createTitledBorder("骰子模式"));
        
        for (int i = 0; i < Math.min(options.size(), 6); i++) {
            JPanel dicePanel = new JPanel(new BorderLayout());
            dicePanel.setBackground(Color.WHITE);
            dicePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            
            JLabel diceLabel = new JLabel("⚀", SwingConstants.CENTER);
            diceLabel.setFont(new Font("Arial", Font.PLAIN, 24));
            
            JLabel optionLabel = new JLabel(options.get(i), SwingConstants.CENTER);
            optionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
            
            dicePanel.add(diceLabel, BorderLayout.CENTER);
            dicePanel.add(optionLabel, BorderLayout.SOUTH);
            
            visualPanel.add(dicePanel);
        }
    }
    
    private void createCardVisual() {
        visualPanel.setLayout(new FlowLayout());
        visualPanel.setBorder(BorderFactory.createTitledBorder("抽卡模式"));
        
        for (int i = 0; i < Math.min(options.size(), 5); i++) {
            JPanel cardPanel = new JPanel(new BorderLayout());
            cardPanel.setPreferredSize(new Dimension(80, 120));
            cardPanel.setBackground(new Color(0, 100, 200));
            cardPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2)); // GOLD color
            
            JLabel cardLabel = new JLabel("🂠", SwingConstants.CENTER);
            cardLabel.setFont(new Font("Arial", Font.PLAIN, 36));
            cardLabel.setForeground(Color.WHITE);
            
            cardPanel.add(cardLabel, BorderLayout.CENTER);
            visualPanel.add(cardPanel);
        }
    }
    
    private void updateVisualAnimation() {
        // 简单的动画效果
        Component[] components = visualPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel panel) {
                Color originalColor = panel.getBackground();
                if (originalColor != null) {
                    panel.setBackground(originalColor.brighter());
                    
                    javax.swing.Timer resetTimer = new javax.swing.Timer(50, e -> panel.setBackground(originalColor));
                    resetTimer.setRepeats(false);
                    resetTimer.start();
                }
            }
        }
    }
    
    private void showPresetDialog() {
        String[] presets = {
            "是,否",
            "去,不去",
            "买,不买",
            "吃,不吃",
            "石头,剪刀,布",
            "红色,蓝色,绿色,黄色",
            "今天,明天,后天",
            "A方案,B方案,C方案"
        };
        
        String selected = (String) JOptionPane.showInputDialog(
            this, "选择预设选项:", "预设选项",
            JOptionPane.QUESTION_MESSAGE, null, presets, presets[0]
        );
        
        if (selected != null) {
            optionsArea.setText(selected.replace(",", "\n"));
            updateOptions();
        }
    }
    
    // 决策模式枚举
    enum DecisionMode {
        LOTTERY("抽签模式"),
        WHEEL("转盘模式"),
        DICE("骰子模式"),
        CARD("抽卡模式");
        
        final String description;
        
        DecisionMode(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
    
    // 转盘面板
    class WheelPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (options.isEmpty()) return;
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int radius = Math.min(centerX, centerY) - 20;
            
            // 绘制转盘
            int sectionAngle = 360 / options.size();
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, 
                             Color.ORANGE, Color.PINK, Color.CYAN, Color.MAGENTA};
            
            for (int i = 0; i < options.size(); i++) {
                g2d.setColor(colors[i % colors.length]);
                g2d.fillArc(centerX - radius, centerY - radius, 
                           radius * 2, radius * 2, 
                           i * sectionAngle, sectionAngle);
                
                // 绘制文字
                g2d.setColor(Color.BLACK);
                double angle = Math.toRadians(i * sectionAngle + sectionAngle / 2);
                int textX = centerX + (int)(Math.cos(angle) * radius * 0.7);
                int textY = centerY + (int)(Math.sin(angle) * radius * 0.7);
                
                String text = options.get(i);
                if (text.length() > 6) {
                    text = text.substring(0, 6) + "...";
                }
                
                FontMetrics fm = g2d.getFontMetrics();
                textX -= fm.stringWidth(text) / 2;
                textY += fm.getAscent() / 2;
                
                g2d.drawString(text, textX, textY);
            }
            
            // 绘制指针
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3));
            int[] xPoints = {centerX, centerX - 10, centerX + 10};
            int[] yPoints = {centerY - radius - 10, centerY - radius + 10, centerY - radius + 10};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }
}
