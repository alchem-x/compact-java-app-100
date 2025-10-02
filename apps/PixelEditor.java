import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * 像素编辑器
 * 简单的像素艺术创作工具，支持绘制、颜色选择和图片保存
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new PixelEditor().setVisible(true);
    });
}

class PixelEditor extends JFrame {
    private static final int DEFAULT_GRID_SIZE = 32;
    private static final int PIXEL_SIZE = 16;
    
    private int gridWidth = DEFAULT_GRID_SIZE;
    private int gridHeight = DEFAULT_GRID_SIZE;
    private Color[][] pixels;
    private Color currentColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    
    private PixelCanvas canvas;
    private JPanel colorPanel;
    private JLabel statusLabel;
    private JSpinner gridSizeSpinner;
    private JButton colorButton;
    
    public PixelEditor() {
        initializePixels();
        initializeUI();
    }
    
    private void initializePixels() {
        pixels = new Color[gridWidth][gridHeight];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                pixels[x][y] = backgroundColor;
            }
        }
    }
    
    private void initializeUI() {
        setTitle("像素编辑器 - Pixel Editor");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 创建菜单栏
        createMenuBar();
        
        // 创建工具栏
        createToolBar();
        
        // 创建主面板
        var mainPanel = new JPanel(new BorderLayout());
        
        // 创建画布
        canvas = new PixelCanvas();
        var scrollPane = new JScrollPane(canvas);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 创建右侧面板
        var rightPanel = createRightPanel();
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 创建状态栏
        createStatusBar();
    }
    
    private void createMenuBar() {
        var menuBar = new JMenuBar();
        
        // 文件菜单
        var fileMenu = new JMenu("文件");
        addMenuItem(fileMenu, "新建", e -> newImage());
        addMenuItem(fileMenu, "打开", e -> openImage());
        addMenuItem(fileMenu, "保存", e -> saveImage());
        addMenuItem(fileMenu, "导出PNG", e -> exportPNG());
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "退出", e -> System.exit(0));
        
        // 编辑菜单
        var editMenu = new JMenu("编辑");
        addMenuItem(editMenu, "清空", e -> clearCanvas());
        addMenuItem(editMenu, "填充", e -> fillCanvas());
        addMenuItem(editMenu, "反转颜色", e -> invertColors());
        
        // 视图菜单
        var viewMenu = new JMenu("视图");
        addMenuItem(viewMenu, "放大", e -> zoomIn());
        addMenuItem(viewMenu, "缩小", e -> zoomOut());
        addMenuItem(viewMenu, "重置缩放", e -> resetZoom());
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void addMenuItem(JMenu menu, String text, ActionListener action) {
        var item = new JMenuItem(text);
        item.addActionListener(action);
        menu.add(item);
    }
    
    private void createToolBar() {
        var toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // 网格大小
        toolBar.add(new JLabel("网格大小:"));
        gridSizeSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_GRID_SIZE, 8, 128, 8));
        gridSizeSpinner.addChangeListener(e -> resizeGrid());
        toolBar.add(gridSizeSpinner);
        
        toolBar.add(new JSeparator(SwingConstants.VERTICAL));
        
        // 当前颜色
        toolBar.add(new JLabel("颜色:"));
        colorButton = new JButton();
        colorButton.setPreferredSize(new Dimension(40, 30));
        colorButton.setBackground(currentColor);
        colorButton.addActionListener(e -> chooseColor());
        toolBar.add(colorButton);
        
        // 工具按钮
        toolBar.add(new JSeparator(SwingConstants.VERTICAL));
        addToolButton(toolBar, "画笔", e -> {/* 默认工具 */});
        addToolButton(toolBar, "橡皮", e -> setEraser());
        addToolButton(toolBar, "填充", e -> fillCanvas());
        addToolButton(toolBar, "清空", e -> clearCanvas());
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void addToolButton(JPanel toolBar, String text, ActionListener action) {
        var button = new JButton(text);
        button.addActionListener(action);
        button.setFocusable(false);
        toolBar.add(button);
    }
    
    private JPanel createRightPanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(180, 500));
        
        // 颜色面板
        colorPanel = new JPanel(new GridLayout(8, 4, 2, 2));
        colorPanel.setBorder(BorderFactory.createTitledBorder("调色板"));
        
        // 添加预设颜色
        Color[] presetColors = {
            Color.BLACK, Color.WHITE, Color.RED, Color.GREEN,
            Color.BLUE, Color.YELLOW, Color.ORANGE, Color.PINK,
            Color.MAGENTA, Color.CYAN, Color.GRAY, Color.LIGHT_GRAY,
            new Color(128, 0, 0), new Color(0, 128, 0), new Color(0, 0, 128), new Color(128, 128, 0),
            new Color(128, 0, 128), new Color(0, 128, 128), new Color(64, 64, 64), new Color(192, 192, 192),
            new Color(255, 128, 128), new Color(128, 255, 128), new Color(128, 128, 255), new Color(255, 255, 128),
            new Color(255, 128, 255), new Color(128, 255, 255), new Color(160, 82, 45), new Color(210, 180, 140),
            new Color(255, 165, 0), new Color(255, 20, 147), new Color(0, 191, 255), new Color(50, 205, 50)
        };
        
        for (var color : presetColors) {
            var colorBtn = new JButton();
            colorBtn.setPreferredSize(new Dimension(30, 30));
            colorBtn.setBackground(color);
            colorBtn.addActionListener(e -> setCurrentColor(color));
            colorPanel.add(colorBtn);
        }
        
        panel.add(colorPanel, BorderLayout.NORTH);
        
        // 信息面板
        var infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("信息"));
        
        var sizeLabel = new JLabel("大小: " + gridWidth + "x" + gridHeight);
        var colorLabel = new JLabel("当前颜色: RGB");
        
        infoPanel.add(sizeLabel);
        infoPanel.add(colorLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void createStatusBar() {
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void resizeGrid() {
        int newSize = (Integer) gridSizeSpinner.getValue();
        
        // 保存当前像素数据
        var oldPixels = pixels;
        int oldWidth = gridWidth;
        int oldHeight = gridHeight;
        
        // 创建新的像素数组
        gridWidth = newSize;
        gridHeight = newSize;
        pixels = new Color[gridWidth][gridHeight];
        
        // 初始化新数组
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (x < oldWidth && y < oldHeight) {
                    pixels[x][y] = oldPixels[x][y];
                } else {
                    pixels[x][y] = backgroundColor;
                }
            }
        }
        
        canvas.updateSize();
        canvas.repaint();
        statusLabel.setText("网格大小已调整为: " + gridWidth + "x" + gridHeight);
    }
    
    private void chooseColor() {
        var color = JColorChooser.showDialog(this, "选择颜色", currentColor);
        if (color != null) {
            setCurrentColor(color);
        }
    }
    
    private void setCurrentColor(Color color) {
        currentColor = color;
        colorButton.setBackground(color);
        statusLabel.setText("当前颜色: RGB(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")");
    }
    
    private void setEraser() {
        setCurrentColor(backgroundColor);
        statusLabel.setText("橡皮擦模式");
    }
    
    private void newImage() {
        int result = JOptionPane.showConfirmDialog(this,
            "确定要新建图像吗？当前内容将丢失。",
            "新建图像", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            clearCanvas();
        }
    }
    
    private void clearCanvas() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                pixels[x][y] = backgroundColor;
            }
        }
        canvas.repaint();
        statusLabel.setText("画布已清空");
    }
    
    private void fillCanvas() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                pixels[x][y] = currentColor;
            }
        }
        canvas.repaint();
        statusLabel.setText("画布已填充");
    }
    
    private void invertColors() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                var color = pixels[x][y];
                var inverted = new Color(
                    255 - color.getRed(),
                    255 - color.getGreen(),
                    255 - color.getBlue()
                );
                pixels[x][y] = inverted;
            }
        }
        canvas.repaint();
        statusLabel.setText("颜色已反转");
    }
    
    private void zoomIn() {
        canvas.zoomIn();
        statusLabel.setText("已放大");
    }
    
    private void zoomOut() {
        canvas.zoomOut();
        statusLabel.setText("已缩小");
    }
    
    private void resetZoom() {
        canvas.resetZoom();
        statusLabel.setText("缩放已重置");
    }
    
    private void openImage() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "图片文件", "png", "jpg", "jpeg", "gif", "bmp"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var image = ImageIO.read(fileChooser.getSelectedFile());
                loadImageToPixels(image);
                canvas.repaint();
                statusLabel.setText("图片已加载");
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法打开图片: " + e.getMessage());
            }
        }
    }
    
    private void loadImageToPixels(BufferedImage image) {
        // 缩放图片到网格大小
        var scaledImage = new BufferedImage(gridWidth, gridHeight, BufferedImage.TYPE_INT_RGB);
        var g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(image, 0, 0, gridWidth, gridHeight, null);
        g2d.dispose();
        
        // 转换为像素数组
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                pixels[x][y] = new Color(scaledImage.getRGB(x, y));
            }
        }
    }
    
    private void saveImage() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "像素文件 (*.pix)", "pix"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".pix")) {
                    file = new File(file.getAbsolutePath() + ".pix");
                }
                
                savePixelData(file);
                statusLabel.setText("文件已保存");
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法保存文件: " + e.getMessage());
            }
        }
    }
    
    private void exportPNG() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "PNG图片", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                
                var image = createImageFromPixels();
                ImageIO.write(image, "png", file);
                statusLabel.setText("PNG已导出");
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法导出PNG: " + e.getMessage());
            }
        }
    }
    
    private void savePixelData(File file) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeInt(gridWidth);
            out.writeInt(gridHeight);
            
            for (int x = 0; x < gridWidth; x++) {
                for (int y = 0; y < gridHeight; y++) {
                    out.writeInt(pixels[x][y].getRGB());
                }
            }
        }
    }
    
    private BufferedImage createImageFromPixels() {
        var image = new BufferedImage(gridWidth, gridHeight, BufferedImage.TYPE_INT_RGB);
        
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                image.setRGB(x, y, pixels[x][y].getRGB());
            }
        }
        
        return image;
    }
    
    // 像素画布类
    class PixelCanvas extends JPanel {
        private int pixelSize = PIXEL_SIZE;
        private boolean isDragging = false;
        
        PixelCanvas() {
            updateSize();
            setupMouseListeners();
        }
        
        void updateSize() {
            setPreferredSize(new Dimension(gridWidth * pixelSize, gridHeight * pixelSize));
            revalidate();
        }
        
        void zoomIn() {
            if (pixelSize < 32) {
                pixelSize += 2;
                updateSize();
                repaint();
            }
        }
        
        void zoomOut() {
            if (pixelSize > 4) {
                pixelSize -= 2;
                updateSize();
                repaint();
            }
        }
        
        void resetZoom() {
            pixelSize = PIXEL_SIZE;
            updateSize();
            repaint();
        }
        
        private void setupMouseListeners() {
            var mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    isDragging = true;
                    paintPixel(e.getX(), e.getY());
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    isDragging = false;
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isDragging) {
                        paintPixel(e.getX(), e.getY());
                    }
                }
                
                @Override
                public void mouseMoved(MouseEvent e) {
                    int x = e.getX() / pixelSize;
                    int y = e.getY() / pixelSize;
                    if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
                        statusLabel.setText("位置: (" + x + ", " + y + ")");
                    }
                }
            };
            
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }
        
        private void paintPixel(int mouseX, int mouseY) {
            int x = mouseX / pixelSize;
            int y = mouseY / pixelSize;
            
            if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
                pixels[x][y] = currentColor;
                repaint();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            var g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            
            // 绘制像素
            for (int x = 0; x < gridWidth; x++) {
                for (int y = 0; y < gridHeight; y++) {
                    g2d.setColor(pixels[x][y]);
                    g2d.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
                }
            }
            
            // 绘制网格线
            g2d.setColor(Color.LIGHT_GRAY);
            for (int x = 0; x <= gridWidth; x++) {
                g2d.drawLine(x * pixelSize, 0, x * pixelSize, gridHeight * pixelSize);
            }
            for (int y = 0; y <= gridHeight; y++) {
                g2d.drawLine(0, y * pixelSize, gridWidth * pixelSize, y * pixelSize);
            }
        }
    }
}
