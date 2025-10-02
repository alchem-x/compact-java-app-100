import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

// 只在真正合适的地方使用record - 简单的数据载体
record ImageFile(String name, String path, long size) {
    static ImageFile from(File file) {
        return new ImageFile(file.getName(), file.getAbsolutePath(), file.length());
    }
    
    String sizeText() {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024));
    }
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new ImageViewer().setVisible(true);
    });
}

static class ImageViewer extends JFrame {
    private final JLabel imageLabel;
    private final JLabel infoLabel;
    private final JScrollPane scrollPane;
    private final DefaultListModel<ImageFile> imageListModel;
    private final JList<ImageFile> imageList;
    
    private List<ImageFile> currentImages = List.of();
    private int currentIndex = -1;
    private double zoomFactor = 1.0;
    
    public ImageViewer() {
        imageLabel = new JLabel();
        infoLabel = new JLabel("请选择图片文件夹或打开图片文件");
        imageListModel = new DefaultListModel<>();
        imageList = new JList<>(imageListModel);
        scrollPane = new JScrollPane(imageLabel);
        
        initializeGUI();
        setupKeyBindings();
    }
    
    private void initializeGUI() {
        setTitle("图片浏览器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        createMenuBar();
        createToolBar();
        createMainPanel();
        createStatusBar();
        
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    private void createMenuBar() {
        var menuBar = new JMenuBar();
        
        var fileMenu = new JMenu("文件");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        var openFileItem = new JMenuItem("打开图片");
        openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        openFileItem.addActionListener(e -> openImageFile());
        
        var openFolderItem = new JMenuItem("打开文件夹");
        openFolderItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
            KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        openFolderItem.addActionListener(e -> openImageFolder());
        
        fileMenu.add(openFileItem);
        fileMenu.add(openFolderItem);
        
        var viewMenu = new JMenu("查看");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        var zoomInItem = new JMenuItem("放大");
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK));
        zoomInItem.addActionListener(e -> zoomIn());
        
        var zoomOutItem = new JMenuItem("缩小");
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
        zoomOutItem.addActionListener(e -> zoomOut());
        
        var fitToWindowItem = new JMenuItem("适应窗口");
        fitToWindowItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK));
        fitToWindowItem.addActionListener(e -> fitToWindow());
        
        var actualSizeItem = new JMenuItem("实际大小");
        actualSizeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_DOWN_MASK));
        actualSizeItem.addActionListener(e -> actualSize());
        
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.addSeparator();
        viewMenu.add(fitToWindowItem);
        viewMenu.add(actualSizeItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }
    
    private void createToolBar() {
        var toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        var openButton = new JButton("📁");
        openButton.setToolTipText("打开文件夹");
        openButton.addActionListener(e -> openImageFolder());
        
        var previousButton = new JButton("⬅️");
        previousButton.setToolTipText("上一张");
        previousButton.addActionListener(e -> showPrevious());
        
        var nextButton = new JButton("➡️");
        nextButton.setToolTipText("下一张");
        nextButton.addActionListener(e -> showNext());
        
        var zoomInButton = new JButton("🔍+");
        zoomInButton.setToolTipText("放大");
        zoomInButton.addActionListener(e -> zoomIn());
        
        var zoomOutButton = new JButton("🔍-");
        zoomOutButton.setToolTipText("缩小");
        zoomOutButton.addActionListener(e -> zoomOut());
        
        var fitButton = new JButton("📐");
        fitButton.setToolTipText("适应窗口");
        fitButton.addActionListener(e -> fitToWindow());
        
        toolBar.add(openButton);
        toolBar.addSeparator();
        toolBar.add(previousButton);
        toolBar.add(nextButton);
        toolBar.addSeparator();
        toolBar.add(zoomInButton);
        toolBar.add(zoomOutButton);
        toolBar.add(fitButton);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void createMainPanel() {
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // 左侧图片列表
        imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imageList.setCellRenderer(new ImageFileRenderer());
        imageList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                var selectedIndex = imageList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    showImage(selectedIndex);
                }
            }
        });
        
        var listScrollPane = new JScrollPane(imageList);
        listScrollPane.setPreferredSize(new Dimension(200, 0));
        listScrollPane.setBorder(BorderFactory.createTitledBorder("图片列表"));
        
        // 右侧图片显示
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);
        
        splitPane.setLeftComponent(listScrollPane);
        splitPane.setRightComponent(scrollPane);
        splitPane.setDividerLocation(200);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void createStatusBar() {
        infoLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        add(infoLabel, BorderLayout.SOUTH);
    }
    
    private void setupKeyBindings() {
        // 使用现代的方式设置键盘快捷键
        var inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = getRootPane().getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "previous");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "next");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exit");
        
        actionMap.put("previous", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { showPrevious(); }
        });
        actionMap.put("next", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { showNext(); }
        });
        actionMap.put("exit", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { System.exit(0); }
        });
    }
    
    private void openImageFile() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "图片文件", "jpg", "jpeg", "png", "gif", "bmp", "webp"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            var parentDir = file.getParentFile();
            loadImagesFromFolder(parentDir);
            
            // 选中打开的文件
            for (int i = 0; i < currentImages.size(); i++) {
                if (currentImages.get(i).path().equals(file.getAbsolutePath())) {
                    showImage(i);
                    imageList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void openImageFolder() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var folder = fileChooser.getSelectedFile();
            loadImagesFromFolder(folder);
        }
    }
    
    private void loadImagesFromFolder(File folder) {
        var files = folder.listFiles();
        if (files == null) return;
        
        // 使用现代Java的流式API，但保持简洁
        currentImages = Arrays.stream(files)
            .filter(File::isFile)
            .filter(this::isImageFile)
            .map(ImageFile::from)
            .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
            .toList();
        
        // 更新UI
        imageListModel.clear();
        currentImages.forEach(imageListModel::addElement);
        
        if (!currentImages.isEmpty()) {
            showImage(0);
            imageList.setSelectedIndex(0);
        } else {
            imageLabel.setIcon(null);
            infoLabel.setText("文件夹中没有找到图片文件");
        }
    }
    
    private boolean isImageFile(File file) {
        var name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
               name.endsWith(".png") || name.endsWith(".gif") || 
               name.endsWith(".bmp") || name.endsWith(".webp");
    }
    
    private void showImage(int index) {
        if (index < 0 || index >= currentImages.size()) return;
        
        currentIndex = index;
        var imageFile = currentImages.get(index);
        
        try {
            var image = ImageIO.read(new File(imageFile.path()));
            if (image != null) {
                zoomFactor = 1.0;
                displayImage(image);
                updateInfo(imageFile, image);
            }
        } catch (IOException e) {
            imageLabel.setIcon(null);
            infoLabel.setText("无法加载图片: " + e.getMessage());
        }
    }
    
    private void displayImage(BufferedImage image) {
        var scaledWidth = (int) (image.getWidth() * zoomFactor);
        var scaledHeight = (int) (image.getHeight() * zoomFactor);
        
        var scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImage));
        imageLabel.revalidate();
    }
    
    private void updateInfo(ImageFile imageFile, BufferedImage image) {
        var info = String.format("%s | %dx%d | %s | 缩放: %.0f%% | %d/%d", 
            imageFile.name(), 
            image.getWidth(), 
            image.getHeight(),
            imageFile.sizeText(),
            zoomFactor * 100,
            currentIndex + 1,
            currentImages.size());
        infoLabel.setText(info);
    }
    
    private void showNext() {
        if (currentIndex < currentImages.size() - 1) {
            showImage(currentIndex + 1);
            imageList.setSelectedIndex(currentIndex);
        }
    }
    
    private void showPrevious() {
        if (currentIndex > 0) {
            showImage(currentIndex - 1);
            imageList.setSelectedIndex(currentIndex);
        }
    }
    
    private void zoomIn() {
        zoomFactor *= 1.2;
        refreshCurrentImage();
    }
    
    private void zoomOut() {
        zoomFactor /= 1.2;
        refreshCurrentImage();
    }
    
    private void fitToWindow() {
        if (currentIndex >= 0 && imageLabel.getIcon() != null) {
            try {
                var imageFile = currentImages.get(currentIndex);
                var image = ImageIO.read(new File(imageFile.path()));
                
                var viewportSize = scrollPane.getViewport().getSize();
                var scaleX = (double) viewportSize.width / image.getWidth();
                var scaleY = (double) viewportSize.height / image.getHeight();
                zoomFactor = Math.min(scaleX, scaleY);
                
                displayImage(image);
                updateInfo(imageFile, image);
            } catch (IOException e) {
                // 忽略错误
            }
        }
    }
    
    private void actualSize() {
        zoomFactor = 1.0;
        refreshCurrentImage();
    }
    
    private void refreshCurrentImage() {
        if (currentIndex >= 0) {
            try {
                var imageFile = currentImages.get(currentIndex);
                var image = ImageIO.read(new File(imageFile.path()));
                displayImage(image);
                updateInfo(imageFile, image);
            } catch (IOException e) {
                // 忽略错误
            }
        }
    }
    
    // 简单的列表渲染器
    static class ImageFileRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ImageFile imageFile) {
                setText(imageFile.name());
                setToolTipText(imageFile.sizeText());
            }
            
            return this;
        }
    }
}
