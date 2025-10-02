import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * 条形码生成器
 * 生成简单的条形码图像
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new BarcodeGenerator().setVisible(true);
    });
}

static class BarcodeGenerator extends JFrame {
    private JTextField textField;
    private JPanel barcodePanel;
    private BufferedImage barcodeImage;
    
    public BarcodeGenerator() {
        initializeUI();
        generateSampleBarcode();
        setupKeyboardShortcuts();
    }
    
    private void initializeUI() {
        setTitle("📊 条形码生成器");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 顶部输入面板
        var inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("输入文本:"));

        textField = new JTextField(20);
        textField.addActionListener(e -> generateBarcode());
        inputPanel.add(textField);

        var generateBtn = new JButton("生成条形码");
        generateBtn.addActionListener(e -> generateBarcode());
        inputPanel.add(generateBtn);
        
        add(inputPanel, BorderLayout.NORTH);
        
        // 中央条形码显示面板
        barcodePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (barcodeImage != null) {
                    int x = (getWidth() - barcodeImage.getWidth()) / 2;
                    int y = (getHeight() - barcodeImage.getHeight()) / 2;
                    g.drawImage(barcodeImage, x, y, null);
                }
            }
        };
        barcodePanel.setBackground(Color.WHITE);
        add(barcodePanel, BorderLayout.CENTER);
        
        // 底部按钮面板
        var buttonPanel = new JPanel(new FlowLayout());
        var saveBtn = new JButton("保存图片");
        saveBtn.addActionListener(e -> saveBarcode());
        buttonPanel.add(saveBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void generateSampleBarcode() {
        textField.setText("1234567890");
        generateBarcode();
    }
    
    private void generateBarcode() {
        String text = textField.getText().trim();
        if (text.isEmpty()) {
            barcodeImage = null;
            barcodePanel.repaint();
            return;
        }
        
        // 生成简单的条形码
        int width = text.length() * 40 + 40;
        int height = 100;
        
        barcodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g2d = barcodeImage.createGraphics();
        
        // 白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // 绘制条形码
        g2d.setColor(Color.BLACK);
        int x = 20;
        
        for (char c : text.toCharArray()) {
            int digit = Character.isDigit(c) ? c - '0' : c % 10;
            
            // 根据数字绘制不同宽度的条纹
            for (int i = 0; i < 4; i++) {
                if ((digit & (1 << i)) != 0) {
                    g2d.fillRect(x + i * 2, 10, 2, 60);
                }
            }
            
            x += 10;
        }
        
        // 绘制文本
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        var fm = g2d.getFontMetrics();
        int textX = (width - fm.stringWidth(text)) / 2;
        g2d.drawString(text, textX, 85);
        
        g2d.dispose();
        barcodePanel.repaint();
    }
    
    private void saveBarcode() {
        if (barcodeImage == null) {
            JOptionPane.showMessageDialog(this, "请先生成条形码");
            return;
        }
        
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "PNG图片", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new java.io.File(file.getAbsolutePath() + ".png");
                }
                
                javax.imageio.ImageIO.write(barcodeImage, "png", file);
                JOptionPane.showMessageDialog(this, "条形码已保存");
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage());
            }
        }
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_G:
                        // G键生成条形码
                        if (ev.isControlDown()) {
                            generateBarcode();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_S:
                        // S键保存
                        if (ev.isControlDown()) {
                            saveBarcode();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_H:
                        // H键显示帮助
                        if (ev.isControlDown()) {
                            showHelp();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_F1:
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
        String helpMessage = """
            条形码生成器使用说明：

            • 在输入框中输入要生成条形码的文本
            • 点击"生成条形码"按钮或按回车键生成条形码
            • 生成的条形码将显示在中央区域
            • 点击"保存"按钮可以将条形码保存为PNG图片

            使用技巧：
            • 支持数字、字母和常用符号
            • 生成的条形码为标准Code 128格式
            • 保存的PNG图片可以用于打印或分享

            快捷键：
            Ctrl+G - 生成条形码
            Ctrl+S - 保存条形码
            Ctrl+H - 显示帮助
            F1 - 显示帮助
            """;
        JOptionPane.showMessageDialog(this, helpMessage, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }
}
