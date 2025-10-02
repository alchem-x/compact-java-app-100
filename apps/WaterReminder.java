import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> new WaterReminder().setVisible(true));
}

static class WaterReminder extends JFrame {
    // 简洁的数据记录
    record CupSize(String name, int ml) {
        @Override
        public String toString() { return name; }
    }
    
    // Material Design 颜色
    private static final Color PRIMARY = new Color(33, 150, 243);
    private static final Color SUCCESS = new Color(76, 175, 80);
    private static final Color WARNING = new Color(255, 193, 7);
    private static final Color SURFACE = new Color(250, 250, 250);
    private static final Color ON_SURFACE = new Color(33, 33, 33);
    private static final Color ON_SURFACE_VARIANT = new Color(117, 117, 117);
    
    // 简洁字体
    private static final Font DISPLAY_FONT = new Font("SansSerif", Font.BOLD, 48);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
    
    // 核心UI组件 - 极简设计
    private JLabel progressLabel;
    private JLabel goalLabel;
    private JButton drinkButton;
    private JComboBox<CupSize> cupCombo;
    
    // 核心数据 - 只保留必要的
    private int dailyGoal = 2000;
    private int currentIntake = 0;
    
    // 杯子选项 - 简化为3个
    private static final CupSize[] CUPS = {
        new CupSize("小杯 200ml", 200),
        new CupSize("中杯 300ml", 300), 
        new CupSize("大杯 500ml", 500)
    };

    public WaterReminder() {
        this.setupWindow();
        this.createUI();
        this.updateDisplay();
    }

    private void setupWindow() {
        setTitle("💧 饮水提醒");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 480);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(SURFACE);
    }
    
    private void createUI() {
        setLayout(new BorderLayout(15, 15));
        
        // 顶部：大数字显示当前进度
        var topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(SURFACE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 15, 20));
        
        progressLabel = new JLabel("0ml", SwingConstants.CENTER);
        progressLabel.setFont(DISPLAY_FONT);
        progressLabel.setForeground(PRIMARY);
        
        goalLabel = new JLabel("目标 2000ml", SwingConstants.CENTER);
        goalLabel.setFont(BODY_FONT);
        goalLabel.setForeground(ON_SURFACE_VARIANT);
        
        topPanel.add(progressLabel, BorderLayout.CENTER);
        topPanel.add(goalLabel, BorderLayout.SOUTH);
        
        // 中间：水杯可视化和进度圆环
        var centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(SURFACE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // 左侧：水杯图示
        var waterCupPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 直接在这里画杯子，简化调试
                var g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 画一个简单的杯子轮廓
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                
                // 杯子外框 - 简单的梯形（调整坐标确保在面板内）
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                
                // 🥤 简洁大方的杯子设计
                
                // 杯子尺寸 - 更高更优雅
                int cupWidth = 50;
                int cupHeight = 110;
                int cupX = (panelWidth - cupWidth) / 2;
                int cupY = (panelHeight - cupHeight) / 2 - 10;
                
                // 杯子主体 - 简洁的圆柱形
                g2d.setColor(new Color(60, 60, 60)); // 简洁的深灰色
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // 左边线
                g2d.drawLine(cupX, cupY + 8, cupX, cupY + cupHeight - 8);
                
                // 右边线  
                g2d.drawLine(cupX + cupWidth, cupY + 8, cupX + cupWidth, cupY + cupHeight - 8);
                
                // 杯底 - 简洁的圆角底部
                g2d.drawArc(cupX, cupY + cupHeight - 16, 16, 16, 180, 90);
                g2d.drawLine(cupX + 8, cupY + cupHeight, cupX + cupWidth - 8, cupY + cupHeight);
                g2d.drawArc(cupX + cupWidth - 16, cupY + cupHeight - 16, 16, 16, 270, 90);
                
                // 杯口 - 简洁的椭圆开口
                g2d.drawArc(cupX, cupY, cupWidth, 16, 0, 180);
                
                // 杯子把手 - 简洁的弧形
                g2d.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawArc(cupX + cupWidth - 3, cupY + 25, 20, 40, 270, 180);
                
                // 杯底厚度设计 - 更明显的立体效果
                
                // 杯底厚度填充区域
                g2d.setColor(new Color(100, 100, 100, 120)); // 半透明深灰
                g2d.fillRoundRect(cupX + 3, cupY + cupHeight - 8, cupWidth - 6, 6, 3, 3);
                
                // 杯底内边线 - 更粗更明显
                g2d.setColor(new Color(40, 40, 40)); // 更深的颜色
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawLine(cupX + 4, cupY + cupHeight - 6, cupX + cupWidth - 4, cupY + cupHeight - 6);
                
                // 杯底外边线加强
                g2d.setColor(new Color(60, 60, 60));
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(cupX + 8, cupY + cupHeight, cupX + cupWidth - 8, cupY + cupHeight);
                
                // 💧 简洁的水位设计
                if (currentIntake > 0) {
                    int maxWaterHeight = cupHeight - 18; // 适当的空间
                    int waterHeight = (int)(maxWaterHeight * currentIntake / (double)dailyGoal);
                    int waterBottomY = cupY + cupHeight - 10; // 水底紧贴杯底厚度区域上方
                    int waterY = waterBottomY - waterHeight; // 水位顶部位置
                    
                    // 水的颜色 - 简洁配色
                    int progress = (currentIntake * 100) / dailyGoal;
                    var waterColor = progress >= 100 ? SUCCESS : PRIMARY;
                    
                    // 绘制简洁的矩形水位
                    g2d.setColor(new Color(waterColor.getRed(), waterColor.getGreen(), waterColor.getBlue(), 160));
                    g2d.fillRoundRect(cupX + 3, waterY, cupWidth - 6, waterHeight, 4, 4);
                    
                    // 水面线 - 简洁的表面
                    g2d.setColor(waterColor);
                    g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    if (waterHeight > 5) {
                        g2d.drawLine(cupX + 5, waterY, cupX + cupWidth - 5, waterY);
                    }
                }
                
                // 简洁的高光
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(cupX + 4, cupY + 12, cupX + 4, cupY + 30);
            }
        };
        waterCupPanel.setBackground(SURFACE);
        waterCupPanel.setPreferredSize(new Dimension(120, 160));
        
        // 右侧：进度圆环
        var progressPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawProgressCircle(g);
            }
        };
        progressPanel.setBackground(SURFACE);
        progressPanel.setPreferredSize(new Dimension(120, 160));
        
        centerPanel.add(waterCupPanel, BorderLayout.WEST);
        centerPanel.add(progressPanel, BorderLayout.EAST);
        
        // 底部：操作区域
        var bottomPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        bottomPanel.setBackground(SURFACE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 30, 30));
        
        // 杯子选择
        cupCombo = new JComboBox<>(CUPS);
        cupCombo.setSelectedIndex(1); // 默认中杯
        cupCombo.setFont(BODY_FONT);
        cupCombo.setPreferredSize(new Dimension(0, 36));
        
        // 喝水按钮 - 符合设计规范
        drinkButton = this.createPrimaryButton("💧 喝水");
        drinkButton.addActionListener(this::drinkWater);
        
        // 重置按钮 - 符合设计规范
        var resetButton = this.createSecondaryButton("重置");
        resetButton.addActionListener(this::resetDaily);
        
        bottomPanel.add(cupCombo);
        bottomPanel.add(drinkButton);
        bottomPanel.add(resetButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // 符合设计规范的按钮创建方法
    private JButton createPrimaryButton(String text) {
        var button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 17)); // HEADLINE字体
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(this.createRoundedBorder(8));
        button.setPreferredSize(new Dimension(0, 44)); // 符合最小触摸区域
        
        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(28, 135, 218)); // 颜色加深
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    private JButton createSecondaryButton(String text) {
        var button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 17)); // HEADLINE字体
        button.setBackground(new Color(242, 242, 247)); // GRAY6
        button.setForeground(ON_SURFACE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(this.createRoundedBorder(8));
        button.setPreferredSize(new Dimension(0, 44)); // 符合最小触摸区域
        
        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(230, 230, 235));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(242, 242, 247));
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    // 圆角边框
    private javax.swing.border.Border createRoundedBorder(int radius) {
        return new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                var g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
                g2d.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(1, 1, 1, 1);
            }
        };
    }
    
    // 水杯可视化
    private void drawWaterCup(Graphics g, int panelWidth, int panelHeight) {
        var g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 先画一个测试矩形，确保绘制方法被调用
        g2d.setColor(Color.RED);
        g2d.fillRect(5, 5, 10, 10);
        
        int cupWidth = 60;
        int cupHeight = 100;
        int x = (panelWidth - cupWidth) / 2;
        int y = (panelHeight - cupHeight) / 2;
        
        // 绘制完整的水杯轮廓（包含杯底）
        g2d.setColor(ON_SURFACE_VARIANT);
        g2d.setStroke(new BasicStroke(2));
        
        // 绘制杯子主体 - 梯形设计，底部比顶部窄
        int topWidth = cupWidth;
        int bottomWidth = cupWidth - 16; // 底部比顶部窄16像素
        int bottomX = x + 8; // 底部居中
        
        // 左侧边线（从顶部到底部，略微向内倾斜）
        g2d.drawLine(x, y + 8, bottomX, y + cupHeight - 8);
        
        // 左下圆角
        g2d.drawArc(bottomX - 8, y + cupHeight - 16, 16, 16, 180, 90);
        
        // 杯底（完整的底部）
        g2d.drawLine(bottomX + 8, y + cupHeight, bottomX + bottomWidth - 8, y + cupHeight);
        
        // 杯底厚度区域（填充方式表示杯底厚度）
        g2d.setColor(new Color(ON_SURFACE.getRed(), ON_SURFACE.getGreen(), ON_SURFACE.getBlue(), 80)); // 半透明深色
        int[] bottomXPoints = {
            bottomX + 6, 
            bottomX + bottomWidth - 6,
            bottomX + bottomWidth - 8,
            bottomX + 8
        };
        int[] bottomYPoints = {
            y + cupHeight - 5,
            y + cupHeight - 5,
            y + cupHeight - 1,
            y + cupHeight - 1
        };
        g2d.fillPolygon(bottomXPoints, bottomYPoints, 4);
        
        // 杯底内边线（更明显的线条）
        g2d.setColor(ON_SURFACE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(bottomX + 6, y + cupHeight - 5, bottomX + bottomWidth - 6, y + cupHeight - 5);
        
        g2d.setColor(ON_SURFACE_VARIANT); // 恢复原颜色
        g2d.setStroke(new BasicStroke(2)); // 恢复边框粗细
        
        // 右下圆角
        g2d.drawArc(bottomX + bottomWidth - 8, y + cupHeight - 16, 16, 16, 270, 90);
        
        // 右侧边线（从底部到顶部，略微向外倾斜）
        g2d.drawLine(bottomX + bottomWidth, y + cupHeight - 8, x + topWidth, y + 8);
        
        // 杯口边缘（左右两边，中间是开口）
        g2d.drawLine(x, y + 8, x + 8, y);
        g2d.drawLine(x + topWidth - 8, y, x + topWidth, y + 8);
        
        // 绘制杯子把手
        g2d.drawArc(x + cupWidth - 5, y + 20, 15, 30, 270, 180);
        
        // 绘制水位（适应梯形杯子）
        int maxWaterHeight = cupHeight - 8; // 留出顶部和底部空间
        int waterHeight = Math.min(maxWaterHeight, (int) (maxWaterHeight * currentIntake / (double) dailyGoal));
        
        if (waterHeight > 0) {
            // 水的颜色根据进度变化
            int progress = (currentIntake * 100) / dailyGoal;
            var waterColor = progress >= 100 ? SUCCESS : 
                           progress >= 75 ? PRIMARY : 
                           progress >= 50 ? WARNING : PRIMARY;
            
            g2d.setColor(new Color(waterColor.getRed(), waterColor.getGreen(), waterColor.getBlue(), 150));
            
            // 绘制梯形水位 - 底部窄，顶部宽
            int waterBottomWidth = bottomWidth - 4; // 水位底部宽度
            int waterTopWidth = topWidth - 4; // 水位顶部宽度
            int waterBottomX = bottomX + 2; // 水位底部X位置
            int waterTopX = x + 2; // 水位顶部X位置
            int waterY = y + cupHeight - waterHeight - 4; // 水位顶部Y位置
            
            // 计算当前水位高度对应的宽度（线性插值）
            int currentWaterWidth = waterBottomWidth + 
                (int)((waterTopWidth - waterBottomWidth) * waterHeight / (double)maxWaterHeight);
            int currentWaterX = waterBottomX + 
                (int)((waterTopX - waterBottomX) * waterHeight / (double)maxWaterHeight);
            
            // 绘制梯形水位
            int[] xPoints = {
                waterBottomX, 
                waterBottomX + waterBottomWidth,
                currentWaterX + currentWaterWidth,
                currentWaterX
            };
            int[] yPoints = {
                y + cupHeight - 4,
                y + cupHeight - 4,
                waterY,
                waterY
            };
            
            g2d.fillPolygon(xPoints, yPoints, 4);
            
            // 水面波纹
            g2d.setColor(waterColor);
            g2d.setStroke(new BasicStroke(1));
            if (waterHeight > 5) { // 确保有足够的水才画波纹
                g2d.drawLine(currentWaterX + 2, waterY, currentWaterX + currentWaterWidth - 2, waterY);
            }
        }
        
        // 刻度线
        g2d.setColor(ON_SURFACE_VARIANT);
        g2d.setStroke(new BasicStroke(1));
        for (int i = 1; i <= 4; i++) {
            int lineY = y + (cupHeight * i / 5);
            g2d.drawLine(x - 3, lineY, x, lineY);
        }
        
        // 当前摄入量标签
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.setColor(ON_SURFACE);
        var text = currentIntake + "ml";
        var fm = g2d.getFontMetrics();
        int textX = x + (cupWidth - fm.stringWidth(text)) / 2;
        g2d.drawString(text, textX, y + cupHeight + 15);
        
        g2d.dispose();
    }
    
    private void drawProgressCircle(Graphics g) {
        var g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int size = 120;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        
        // 背景圆环
        g2d.setStroke(new BasicStroke(8));
        g2d.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 50));
        g2d.drawOval(x, y, size, size);
        
        // 进度圆环
        int progress = Math.min(100, (currentIntake * 100) / dailyGoal);
        int angle = (int) (360 * (progress / 100.0));
        
        g2d.setColor(PRIMARY);
        g2d.drawArc(x, y, size, size, 90, -angle);
        
        // 中心百分比
        g2d.setFont(TITLE_FONT);
        g2d.setColor(ON_SURFACE);
        var text = progress + "%";
        var fm = g2d.getFontMetrics();
        int textX = x + (size - fm.stringWidth(text)) / 2;
        int textY = y + (size + fm.getAscent()) / 2;
        g2d.drawString(text, textX, textY);
        
        g2d.dispose();
    }
    
    private void drinkWater(ActionEvent e) {
        var cup = (CupSize) cupCombo.getSelectedItem();
        currentIntake = Math.min(currentIntake + cup.ml(), dailyGoal);
        
        this.updateDisplay();
        
        // 简单反馈
        if (currentIntake >= dailyGoal) {
            JOptionPane.showMessageDialog(this, "🎉 今日目标达成！", "恭喜", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetDaily(ActionEvent e) {
        var result = JOptionPane.showConfirmDialog(this, "确定重置今日记录？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            currentIntake = 0;
            this.updateDisplay();
        }
    }
    
    private void updateDisplay() {
        progressLabel.setText(currentIntake + "ml");
        goalLabel.setText("目标 " + dailyGoal + "ml");
        
        // 更新按钮颜色
        int progress = (currentIntake * 100) / dailyGoal;
        var buttonColor = progress >= 100 ? SUCCESS : progress >= 75 ? PRIMARY : progress >= 50 ? WARNING : PRIMARY;
        drinkButton.setBackground(buttonColor);
        
        repaint();
    }
}
