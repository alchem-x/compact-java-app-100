import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new SimpleImageEditor().setVisible(true);
    });
}

static class SimpleImageEditor extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage currentImage;
    private ImagePanel imagePanel;
    private JLabel statusLabel;
    
    public SimpleImageEditor() {
        setTitle("简易图片编辑器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        
        initializeUI();
        setLocationRelativeTo(null);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 菜单栏
        var menuBar = new JMenuBar();
        
        var fileMenu = new JMenu("文件");
        var openItem = new JMenuItem("打开");
        var saveItem = new JMenuItem("保存");
        var saveAsItem = new JMenuItem("另存为");
        var exitItem = new JMenuItem("退出");
        
        openItem.addActionListener(this::openImage);
        saveItem.addActionListener(this::saveImage);
        saveAsItem.addActionListener(this::saveAsImage);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        var editMenu = new JMenu("编辑");
        var resetItem = new JMenuItem("重置");
        var resizeItem = new JMenuItem("调整大小");
        
        resetItem.addActionListener(this::resetImage);
        resizeItem.addActionListener(this::resizeImage);
        
        editMenu.add(resetItem);
        editMenu.add(resizeItem);
        
        var filterMenu = new JMenu("滤镜");
        var grayItem = new JMenuItem("灰度");
        var brightenItem = new JMenuItem("增亮");
        var darkenItem = new JMenuItem("变暗");
        var blurItem = new JMenuItem("模糊");
        var sharpenItem = new JMenuItem("锐化");
        
        grayItem.addActionListener(e -> applyGrayscale());
        brightenItem.addActionListener(e -> adjustBrightness(30));
        darkenItem.addActionListener(e -> adjustBrightness(-30));
        blurItem.addActionListener(e -> applyBlur());
        sharpenItem.addActionListener(e -> applySharpen());
        
        filterMenu.add(grayItem);
        filterMenu.addSeparator();
        filterMenu.add(brightenItem);
        filterMenu.add(darkenItem);
        filterMenu.addSeparator();
        filterMenu.add(blurItem);
        filterMenu.add(sharpenItem);
        
        var transformMenu = new JMenu("变换");
        var rotateLeftItem = new JMenuItem("左转90°");
        var rotateRightItem = new JMenuItem("右转90°");
        var flipHItem = new JMenuItem("水平翻转");
        var flipVItem = new JMenuItem("垂直翻转");
        
        rotateLeftItem.addActionListener(e -> rotateImage(-90));
        rotateRightItem.addActionListener(e -> rotateImage(90));
        flipHItem.addActionListener(e -> flipImage(true));
        flipVItem.addActionListener(e -> flipImage(false));
        
        transformMenu.add(rotateLeftItem);
        transformMenu.add(rotateRightItem);
        transformMenu.addSeparator();
        transformMenu.add(flipHItem);
        transformMenu.add(flipVItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(filterMenu);
        menuBar.add(transformMenu);
        
        setJMenuBar(menuBar);
        
        // 工具栏
        var toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        var openBtn = new JButton("打开");
        var saveBtn = new JButton("保存");
        var resetBtn = new JButton("重置");
        var grayBtn = new JButton("灰度");
        var brightenBtn = new JButton("增亮");
        var rotateBtn = new JButton("旋转");
        
        openBtn.addActionListener(this::openImage);
        saveBtn.addActionListener(this::saveImage);
        resetBtn.addActionListener(this::resetImage);
        grayBtn.addActionListener(e -> applyGrayscale());
        brightenBtn.addActionListener(e -> adjustBrightness(30));
        rotateBtn.addActionListener(e -> rotateImage(90));
        
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(resetBtn);
        toolBar.addSeparator();
        toolBar.add(grayBtn);
        toolBar.add(brightenBtn);
        toolBar.add(rotateBtn);
        
        // 图片显示面板
        imagePanel = new ImagePanel();
        var scrollPane = new JScrollPane(imagePanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        // 状态栏
        statusLabel = new JLabel("请打开一张图片");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        // 初始提示
        showWelcomeMessage();
    }
    
    private void showWelcomeMessage() {
        SwingUtilities.invokeLater(() -> {
            String welcomeMessage = """
                欢迎使用简易图片编辑器！

                功能包括：
                • 基本的图片打开和保存
                • 灰度、亮度调整等滤镜
                • 旋转、翻转等变换
                • 图片大小调整

                点击"打开"按钮开始编辑图片。
                """;

            JOptionPane.showMessageDialog(this, welcomeMessage, "欢迎",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void openImage(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "图片文件", "jpg", "jpeg", "png", "gif", "bmp"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                originalImage = ImageIO.read(fileChooser.getSelectedFile());
                currentImage = copyImage(originalImage);
                imagePanel.setImage(currentImage);
                updateStatus("已打开: " + fileChooser.getSelectedFile().getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "打开图片失败: " + ex.getMessage());
            }
        }
    }
    
    private void saveImage(ActionEvent e) {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "没有图片可保存！");
            return;
        }
        
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG图片", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                ImageIO.write(currentImage, "png", file);
                updateStatus("已保存: " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "保存图片失败: " + ex.getMessage());
            }
        }
    }
    
    private void saveAsImage(ActionEvent e) {
        saveImage(e);
    }
    
    private void resetImage(ActionEvent e) {
        if (originalImage != null) {
            currentImage = copyImage(originalImage);
            imagePanel.setImage(currentImage);
            updateStatus("图片已重置");
        }
    }
    
    private void resizeImage(ActionEvent e) {
        if (currentImage == null) return;

        var dialog = new ResizeDialog(this, currentImage.getWidth(), currentImage.getHeight());
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            var newWidth = dialog.getNewWidth();
            var newHeight = dialog.getNewHeight();

            var resized = new BufferedImage(newWidth, newHeight, currentImage.getType());
            var g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(currentImage, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            currentImage = resized;
            imagePanel.setImage(currentImage);
            updateStatus("图片大小已调整为: " + newWidth + "x" + newHeight);
        }
    }
    
    private void applyGrayscale() {
        if (currentImage == null) return;

        var gray = new BufferedImage(currentImage.getWidth(), currentImage.getHeight(), currentImage.getType());

        for (int y = 0; y < currentImage.getHeight(); y++) {
            for (int x = 0; x < currentImage.getWidth(); x++) {
                var rgb = currentImage.getRGB(x, y);
                var r = (rgb >> 16) & 0xFF;
                var g = (rgb >> 8) & 0xFF;
                var b = rgb & 0xFF;

                var grayValue = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                var grayRGB = (grayValue << 16) | (grayValue << 8) | grayValue;

                gray.setRGB(x, y, grayRGB);
            }
        }

        currentImage = gray;
        imagePanel.setImage(currentImage);
        updateStatus("已应用灰度滤镜");
    }
    
    private void adjustBrightness(int adjustment) {
        if (currentImage == null) return;

        var bright = new BufferedImage(currentImage.getWidth(), currentImage.getHeight(), currentImage.getType());

        for (int y = 0; y < currentImage.getHeight(); y++) {
            for (int x = 0; x < currentImage.getWidth(); x++) {
                var rgb = currentImage.getRGB(x, y);
                var r = Math.max(0, Math.min(255, ((rgb >> 16) & 0xFF) + adjustment));
                var g = Math.max(0, Math.min(255, ((rgb >> 8) & 0xFF) + adjustment));
                var b = Math.max(0, Math.min(255, (rgb & 0xFF) + adjustment));

                var newRGB = (r << 16) | (g << 8) | b;
                bright.setRGB(x, y, newRGB);
            }
        }

        currentImage = bright;
        imagePanel.setImage(currentImage);
        updateStatus("亮度已调整: " + (adjustment > 0 ? "+" : "") + adjustment);
    }
    
    private void applyBlur() {
        if (currentImage == null) return;

        var blurred = new BufferedImage(currentImage.getWidth(), currentImage.getHeight(), currentImage.getType());

        for (int y = 0; y < currentImage.getHeight(); y++) {
            for (int x = 0; x < currentImage.getWidth(); x++) {
                int totalR = 0, totalG = 0, totalB = 0, count = 0;

                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int newX = x + dx;
                        int newY = y + dy;

                        if (newX >= 0 && newX < currentImage.getWidth() && newY >= 0 && newY < currentImage.getHeight()) {
                            var rgb = currentImage.getRGB(newX, newY);
                            totalR += (rgb >> 16) & 0xFF;
                            totalG += (rgb >> 8) & 0xFF;
                            totalB += rgb & 0xFF;
                            count++;
                        }
                    }
                }

                if (count > 0) {
                    var avgR = totalR / count;
                    var avgG = totalG / count;
                    var avgB = totalB / count;
                    var newRGB = (avgR << 16) | (avgG << 8) | avgB;
                    blurred.setRGB(x, y, newRGB);
                }
            }
        }

        currentImage = blurred;
        imagePanel.setImage(currentImage);
        updateStatus("已应用模糊滤镜");
    }
    
    private void applySharpen() {
        if (currentImage == null) return;

        var sharpened = new BufferedImage(currentImage.getWidth(), currentImage.getHeight(), currentImage.getType());

        // 简单的锐化核
        int[][] kernel = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
        };

        for (int y = 0; y < currentImage.getHeight(); y++) {
            for (int x = 0; x < currentImage.getWidth(); x++) {
                int totalR = 0, totalG = 0, totalB = 0;

                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int newX = x + dx;
                        int newY = y + dy;

                        if (newX >= 0 && newX < currentImage.getWidth() && newY >= 0 && newY < currentImage.getHeight()) {
                            var rgb = currentImage.getRGB(newX, newY);
                            var weight = kernel[dy + 1][dx + 1];

                            totalR += ((rgb >> 16) & 0xFF) * weight;
                            totalG += ((rgb >> 8) & 0xFF) * weight;
                            totalB += (rgb & 0xFF) * weight;
                        }
                    }
                }

                var newR = Math.max(0, Math.min(255, totalR));
                var newG = Math.max(0, Math.min(255, totalG));
                var newB = Math.max(0, Math.min(255, totalB));

                var newRGB = (newR << 16) | (newG << 8) | newB;
                sharpened.setRGB(x, y, newRGB);
            }
        }

        currentImage = sharpened;
        imagePanel.setImage(currentImage);
        updateStatus("已应用锐化滤镜");
    }
    
    private void rotateImage(int degrees) {
        if (currentImage == null) return;
        
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        
        int newWidth = (int) (currentImage.getWidth() * cos + currentImage.getHeight() * sin);
        int newHeight = (int) (currentImage.getWidth() * sin + currentImage.getHeight() * cos);
        
        var rotated = new BufferedImage(newWidth, newHeight, currentImage.getType());
        var g2d = rotated.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newWidth, newHeight);
        
        var transform = new AffineTransform();
        transform.translate(newWidth / 2.0, newHeight / 2.0);
        transform.rotate(radians);
        transform.translate(-currentImage.getWidth() / 2.0, -currentImage.getHeight() / 2.0);
        
        g2d.setTransform(transform);
        g2d.drawImage(currentImage, 0, 0, null);
        g2d.dispose();
        
        currentImage = rotated;
        imagePanel.setImage(currentImage);
        updateStatus("图片已旋转 " + degrees + "°");
    }
    
    private void flipImage(boolean horizontal) {
        if (currentImage == null) return;
        
        var flipped = new BufferedImage(currentImage.getWidth(), currentImage.getHeight(), currentImage.getType());
        var g2d = flipped.createGraphics();
        
        if (horizontal) {
            g2d.drawImage(currentImage, currentImage.getWidth(), 0, -currentImage.getWidth(), currentImage.getHeight(), null);
        } else {
            g2d.drawImage(currentImage, 0, currentImage.getHeight(), currentImage.getWidth(), -currentImage.getHeight(), null);
        }
        
        g2d.dispose();
        
        currentImage = flipped;
        imagePanel.setImage(currentImage);
        updateStatus("图片已" + (horizontal ? "水平" : "垂直") + "翻转");
    }
    
    private BufferedImage copyImage(BufferedImage original) {
        var copy = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
        var g2d = copy.createGraphics();
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        return copy;
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    private static class ImagePanel extends JPanel {
        private BufferedImage image;
        
        public void setImage(BufferedImage image) {
            this.image = image;
            if (image != null) {
                setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            }
            revalidate();
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, null);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.DARK_GRAY);
                g.drawString("没有图片", getWidth() / 2 - 30, getHeight() / 2);
            }
        }
    }
    
    private static class ResizeDialog extends JDialog {
        private boolean confirmed = false;
        private JSpinner widthSpinner, heightSpinner;
        private JCheckBox keepRatioBox;
        private double aspectRatio;
        
        public ResizeDialog(JFrame parent, int currentWidth, int currentHeight) {
            super(parent, "调整图片大小", true);
            this.aspectRatio = (double) currentWidth / currentHeight;
            
            setSize(300, 200);
            setLocationRelativeTo(parent);
            
            initializeUI(currentWidth, currentHeight);
        }
        
        private void initializeUI(int width, int height) {
            setLayout(new BorderLayout());
            
            var panel = new JPanel(new GridBagLayout());
            var gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("宽度:"), gbc);
            gbc.gridx = 1;
            widthSpinner = new JSpinner(new SpinnerNumberModel(width, 1, 5000, 1));
            panel.add(widthSpinner, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("高度:"), gbc);
            gbc.gridx = 1;
            heightSpinner = new JSpinner(new SpinnerNumberModel(height, 1, 5000, 1));
            panel.add(heightSpinner, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            keepRatioBox = new JCheckBox("保持宽高比", true);
            panel.add(keepRatioBox, gbc);
            
            // 添加监听器保持宽高比
            widthSpinner.addChangeListener(e -> {
                if (keepRatioBox.isSelected()) {
                    int newWidth = (Integer) widthSpinner.getValue();
                    int newHeight = (int) (newWidth / aspectRatio);
                    heightSpinner.setValue(newHeight);
                }
            });
            
            heightSpinner.addChangeListener(e -> {
                if (keepRatioBox.isSelected()) {
                    int newHeight = (Integer) heightSpinner.getValue();
                    int newWidth = (int) (newHeight * aspectRatio);
                    widthSpinner.setValue(newWidth);
                }
            });
            
            var buttonPanel = new JPanel();
            var okBtn = new JButton("确定");
            var cancelBtn = new JButton("取消");
            
            okBtn.addActionListener(e -> {
                confirmed = true;
                dispose();
            });
            
            cancelBtn.addActionListener(e -> dispose());
            
            buttonPanel.add(okBtn);
            buttonPanel.add(cancelBtn);
            
            add(panel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
        
        public int getNewWidth() {
            return (Integer) widthSpinner.getValue();
        }
        
        public int getNewHeight() {
            return (Integer) heightSpinner.getValue();
        }
    }
}
