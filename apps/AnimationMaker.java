import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🎬 动画制作器";

    // 工具栏按钮
    static final String TOOL_PEN = "画笔";
    static final String TOOL_ERASER = "橡皮";
    static final String CLEAR_FRAME = "清空帧";
    static final String COLOR_LABEL = "颜色:";
    static final String SIZE_LABEL = "大小:";

    // 帧操作按钮
    static final String ADD_FRAME = "添加帧";
    static final String DUPLICATE_FRAME = "复制帧";
    static final String DELETE_FRAME = "删除帧";
    static final String MOVE_UP = "上移";

    // 播放控制
    static final String PLAY = "播放";
    static final String PAUSE = "暂停";
    static final String STOP = "停止";
    static final String SPEED_LABEL = "速度:";

    // 文件操作
    static final String IMPORT = "导入";
    static final String EXPORT = "导出";
    static final String SAVE_PROJECT = "保存项目";
    static final String LOAD_PROJECT = "加载项目";

    // 面板标题
    static final String FRAME_LIST_TITLE = "帧列表";
    static final String COLOR_CHOOSER_TITLE = "选择颜色";

    // 文件对话框
    static final String IMPORT_DIALOG_TITLE = "导入帧";
    static final String EXPORT_DIALOG_TITLE = "导出帧";
    static final String SAVE_PROJECT_DIALOG_TITLE = "保存项目";
    static final String LOAD_PROJECT_DIALOG_TITLE = "选择项目文件夹";
    static final String PNG_FILTER = "PNG图片";
    static final String ANIMATION_FILTER = "动画项目";
    static final String ALL_FILES = "所有文件";

    // 状态消息
    static final String STATUS_FRAME_FORMAT = "帧: %d/%d";
    static final String IMPORT_SUCCESS = "成功导入帧: ";
    static final String EXPORT_SUCCESS = "成功导出到: ";
    static final String SAVE_PROJECT_SUCCESS = "项目保存成功: ";
    static final String LOAD_PROJECT_SUCCESS = "项目加载成功: ";

    // 错误消息
    static final String ERROR_IMPORT_FAILED = "导入失败: ";
    static final String ERROR_EXPORT_FAILED = "导出失败: ";
    static final String ERROR_SAVE_PROJECT_FAILED = "保存失败: ";
    static final String ERROR_LOAD_PROJECT_FAILED = "加载失败: ";
    static final String ERROR_INVALID_PROJECT_FOLDER = "无效的项目文件夹";
    static final String ERROR_EMPTY_FRAME = "当前帧为空，无法导出";
    static final String ERROR_SELECT_FRAME_FIRST = "请先选择一个帧";

    // 帮助信息
    static final String HELP_MESSAGE = """
        动画制作器使用说明：

        • 绘图工具：使用画笔和橡皮在画布上绘制
        • 颜色选择：点击颜色按钮选择绘图颜色
        • 画笔大小：调整滑块改变画笔粗细
        • 帧管理：添加、复制、删除和移动帧
        • 动画播放：设置播放速度并预览动画
        • 文件操作：导入/导出帧，保存/加载项目

        使用技巧：
        • 在帧列表中点击选择要编辑的帧
        • 使用鼠标拖拽绘制连续线条
        • 可以导入PNG图片作为帧
        • 项目保存为.anim格式，包含所有帧信息

        快捷键：
        Ctrl+N - 新建帧
        Ctrl+D - 复制帧
        Ctrl+Delete - 删除帧
        Space - 播放/暂停
        Ctrl+S - 保存项目
        Ctrl+O - 打开项目
        Ctrl+H - 显示帮助
        F1 - 显示帮助
        """;
}

/**
 * 动画制作器
 * 简单的帧动画制作工具
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        var frame = new AnimationMaker();
        frame.setVisible(true);
    });
}

class AnimationMaker extends JFrame {
    private List<AnimationFrame> frames = new ArrayList<>();
    private int currentFrameIndex = 0;
    private boolean isPlaying = false;

    private AnimationCanvas canvas;
    private JList<AnimationFrame> frameList;
    private DefaultListModel<AnimationFrame> frameListModel;
    private JSlider speedSlider;
    private JButton playButton;
    private JLabel statusLabel;

    private javax.swing.Timer animationTimer;
    private Color currentColor = Color.BLACK;
    private int brushSize = 3;

    public AnimationMaker() {
        initializeUI();
        addInitialFrame();
        setupAnimationTimer();
        setupKeyboardShortcuts();
    }
    
    private void initializeUI() {
        setTitle(Texts.WINDOW_TITLE);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板
        var mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // 顶部工具栏
        var toolBar = createToolBar();
        mainPanel.add(toolBar, BorderLayout.NORTH);

        // 中央面板
        var centerPanel = new JPanel(new BorderLayout());

        // 画布
        canvas = new AnimationCanvas();
        var canvasScrollPane = new JScrollPane(canvas);
        canvasScrollPane.setPreferredSize(new Dimension(600, 400));
        centerPanel.add(canvasScrollPane, BorderLayout.CENTER);

        // 右侧帧列表
        var framePanel = createFramePanel();
        centerPanel.add(framePanel, BorderLayout.EAST);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 底部控制面板
        var controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // 确保窗口正确显示
        pack();
        setSize(1000, 700); // 重新设置大小，确保窗口足够大
    }
    
    private JPanel createToolBar() {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 绘图工具
        var penBtn = new JButton(Texts.TOOL_PEN);
        var eraserBtn = new JButton(Texts.TOOL_ERASER);
        var clearBtn = new JButton(Texts.CLEAR_FRAME);

        penBtn.addActionListener(e -> canvas.setTool(DrawingTool.PEN));
        eraserBtn.addActionListener(e -> canvas.setTool(DrawingTool.ERASER));
        clearBtn.addActionListener(e -> clearCurrentFrame());

        panel.add(penBtn);
        panel.add(eraserBtn);
        panel.add(clearBtn);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        // 颜色选择 - 修复颜色选择器
        var colorBtn = new JButton();
        colorBtn.setPreferredSize(new Dimension(40, 30));
        colorBtn.setBackground(currentColor);
        colorBtn.setOpaque(true);
        colorBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        colorBtn.addActionListener(e -> chooseColor(colorBtn));
        panel.add(new JLabel(Texts.COLOR_LABEL));
        panel.add(colorBtn);

        // 预设颜色
        Color[] colors = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.PINK, Color.CYAN};
        for (var color : colors) {
            var btn = new JButton();
            btn.setPreferredSize(new Dimension(20, 20));
            btn.setBackground(color);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            btn.addActionListener(e -> {
                currentColor = color;
                colorBtn.setBackground(color);
                canvas.setColor(color);
            });
            panel.add(btn);
        }

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        // 画笔大小
        panel.add(new JLabel(Texts.SIZE_LABEL));
        var sizeSlider = new JSlider(1, 20, brushSize);
        sizeSlider.setPreferredSize(new Dimension(100, 30));
        sizeSlider.addChangeListener(e -> {
            brushSize = sizeSlider.getValue();
            canvas.setBrushSize(brushSize);
        });
        panel.add(sizeSlider);

        return panel;
    }
    
    private JPanel createFramePanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 400));
        panel.setBorder(BorderFactory.createTitledBorder(Texts.FRAME_LIST_TITLE));
        
        frameListModel = new DefaultListModel<>();
        frameList = new JList<>(frameListModel);
        frameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frameList.setCellRenderer(new FrameListCellRenderer());
        frameList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectFrame(frameList.getSelectedIndex());
            }
        });
        
        var scrollPane = new JScrollPane(frameList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 帧操作按钮
        var buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        var addBtn = new JButton(Texts.ADD_FRAME);
        var duplicateBtn = new JButton(Texts.DUPLICATE_FRAME);
        var deleteBtn = new JButton(Texts.DELETE_FRAME);
        var moveUpBtn = new JButton(Texts.MOVE_UP);
        
        addBtn.addActionListener(e -> addFrame());
        duplicateBtn.addActionListener(e -> duplicateFrame());
        deleteBtn.addActionListener(e -> deleteFrame());
        moveUpBtn.addActionListener(e -> moveFrameUp());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(duplicateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(moveUpBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        var panel = new JPanel(new FlowLayout());

        // 播放控制
        playButton = new JButton(Texts.PLAY);
        playButton.addActionListener(e -> togglePlayback());

        var stopBtn = new JButton(Texts.STOP);
        stopBtn.addActionListener(e -> stopAnimation());

        panel.add(playButton);
        panel.add(stopBtn);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        // 速度控制
        panel.add(new JLabel(Texts.SPEED_LABEL));
        speedSlider = new JSlider(1, 20, 5);
        speedSlider.setPreferredSize(new Dimension(150, 30));
        speedSlider.addChangeListener(e -> updateAnimationSpeed());
        panel.add(speedSlider);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        // 导入导出功能
        var importBtn = new JButton(Texts.IMPORT);
        var exportBtn = new JButton(Texts.EXPORT);
        var saveProjectBtn = new JButton(Texts.SAVE_PROJECT);
        var loadProjectBtn = new JButton(Texts.LOAD_PROJECT);

        importBtn.addActionListener(e -> importFrame());
        exportBtn.addActionListener(e -> exportFrame());
        saveProjectBtn.addActionListener(e -> saveProject());
        loadProjectBtn.addActionListener(e -> loadProject());

        panel.add(importBtn);
        panel.add(exportBtn);
        panel.add(saveProjectBtn);
        panel.add(loadProjectBtn);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        // 状态标签
        statusLabel = new JLabel("帧: 1/1");
        panel.add(statusLabel);

        return panel;
    }
    
    private void setupAnimationTimer() {
        animationTimer = new javax.swing.Timer(200, e -> nextFrame());
        updateAnimationSpeed();
    }
    
    private void addInitialFrame() {
        var frame = new AnimationFrame("帧 1");
        frames.add(frame);
        frameListModel.addElement(frame);
        frameList.setSelectedIndex(0);
        updateStatus();
    }
    
    private void chooseColor(JButton colorBtn) {
        var color = JColorChooser.showDialog(this, Texts.COLOR_CHOOSER_TITLE, currentColor);
        if (color != null) {
            currentColor = color;
            colorBtn.setBackground(color);
            canvas.setColor(color);
        }
    }
    
    private void addFrame() {
        var frame = new AnimationFrame("帧 " + (frames.size() + 1));
        frames.add(frame);
        frameListModel.addElement(frame);
        frameList.setSelectedIndex(frames.size() - 1);
        updateStatus();
    }
    
    private void duplicateFrame() {
        if (currentFrameIndex >= 0 && currentFrameIndex < frames.size()) {
            var currentFrame = frames.get(currentFrameIndex);
            var newFrame = new AnimationFrame("帧 " + (frames.size() + 1));
            
            // 复制图像
            if (currentFrame.image != null) {
                newFrame.image = new BufferedImage(
                    currentFrame.image.getWidth(),
                    currentFrame.image.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
                );
                var g2d = newFrame.image.createGraphics();
                g2d.drawImage(currentFrame.image, 0, 0, null);
                g2d.dispose();
            }
            
            frames.add(currentFrameIndex + 1, newFrame);
            frameListModel.add(currentFrameIndex + 1, newFrame);
            frameList.setSelectedIndex(currentFrameIndex + 1);
            updateStatus();
        }
    }
    
    private void deleteFrame() {
        if (frames.size() > 1 && currentFrameIndex >= 0) {
            frames.remove(currentFrameIndex);
            frameListModel.remove(currentFrameIndex);
            
            if (currentFrameIndex >= frames.size()) {
                currentFrameIndex = frames.size() - 1;
            }
            
            frameList.setSelectedIndex(currentFrameIndex);
            canvas.setFrame(frames.get(currentFrameIndex));
            updateStatus();
        }
    }
    
    private void moveFrameUp() {
        if (currentFrameIndex > 0) {
            var frame = frames.remove(currentFrameIndex);
            frames.add(currentFrameIndex - 1, frame);
            
            frameListModel.remove(currentFrameIndex);
            frameListModel.add(currentFrameIndex - 1, frame);
            
            currentFrameIndex--;
            frameList.setSelectedIndex(currentFrameIndex);
            updateStatus();
        }
    }
    
    private void selectFrame(int index) {
        if (index >= 0 && index < frames.size()) {
            currentFrameIndex = index;
            canvas.setFrame(frames.get(index));
            updateStatus();
        }
    }
    
    private void clearCurrentFrame() {
        if (currentFrameIndex >= 0 && currentFrameIndex < frames.size()) {
            var frame = frames.get(currentFrameIndex);
            frame.image = null;
            canvas.setFrame(frame);
            canvas.repaint();
        }
    }
    
    private void togglePlayback() {
        if (isPlaying) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }
    
    private void startAnimation() {
        if (frames.size() > 1) {
            isPlaying = true;
            playButton.setText("暂停");
            animationTimer.start();
        }
    }
    
    private void stopAnimation() {
        isPlaying = false;
        playButton.setText(Texts.PLAY);
        animationTimer.stop();
    }
    
    private void nextFrame() {
        currentFrameIndex = (currentFrameIndex + 1) % frames.size();
        canvas.setFrame(frames.get(currentFrameIndex));
        frameList.setSelectedIndex(currentFrameIndex);
        updateStatus();
    }
    
    private void updateAnimationSpeed() {
        int fps = speedSlider.getValue();
        int delay = 1000 / fps;
        animationTimer.setDelay(delay);
    }
    
    private void updateStatus() {
        statusLabel.setText(String.format(Texts.STATUS_FRAME_FORMAT, currentFrameIndex + 1, frames.size()));
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_N:
                        // Ctrl+N 新建帧
                        if (ev.isControlDown()) {
                            addFrame();
                        }
                        break;
                    case KeyEvent.VK_D:
                        // Ctrl+D 复制帧
                        if (ev.isControlDown()) {
                            duplicateFrame();
                        }
                        break;
                    case KeyEvent.VK_DELETE:
                        // Ctrl+Delete 删除帧
                        if (ev.isControlDown()) {
                            deleteFrame();
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        // Space 播放/暂停
                        if (!ev.isControlDown()) {
                            togglePlayback();
                        }
                        break;
                    case KeyEvent.VK_S:
                        // Ctrl+S 保存项目
                        if (ev.isControlDown()) {
                            saveProject();
                        }
                        break;
                    case KeyEvent.VK_O:
                        // Ctrl+O 打开项目
                        if (ev.isControlDown()) {
                            loadProject();
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

    // 导入导出功能
    private void importFrame() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(Texts.PNG_FILTER, "png"));
        fileChooser.setDialogTitle(Texts.IMPORT_DIALOG_TITLE);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                var image = ImageIO.read(file);
                if (image != null) {
                    var frame = new AnimationFrame("导入帧 " + (frames.size() + 1));
                    frame.image = image;
                    frames.add(frame);
                    frameListModel.addElement(frame);
                    frameList.setSelectedIndex(frames.size() - 1);
                    updateStatus();
                    JOptionPane.showMessageDialog(this, Texts.IMPORT_SUCCESS + file.getName());
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, Texts.ERROR_IMPORT_FAILED + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportFrame() {
        if (currentFrameIndex < 0 || currentFrameIndex >= frames.size()) {
            JOptionPane.showMessageDialog(this, Texts.ERROR_SELECT_FRAME_FIRST, "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        var currentFrame = frames.get(currentFrameIndex);
        if (currentFrame.image == null) {
            JOptionPane.showMessageDialog(this, Texts.ERROR_EMPTY_FRAME, "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(Texts.PNG_FILTER, "png"));
        fileChooser.setDialogTitle(Texts.EXPORT_DIALOG_TITLE);
        fileChooser.setSelectedFile(new File(currentFrame.name + ".png"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                ImageIO.write(currentFrame.image, "png", file);
                JOptionPane.showMessageDialog(this, Texts.EXPORT_SUCCESS + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, Texts.ERROR_EXPORT_FAILED + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveProject() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(Texts.ANIMATION_FILTER, "anim"));
        fileChooser.setDialogTitle(Texts.SAVE_PROJECT_DIALOG_TITLE);
        fileChooser.setSelectedFile(new File("animation.anim"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".anim")) {
                    file = new File(file.getAbsolutePath() + ".anim");
                }

                // 创建项目目录
                var projectDir = new File(file.getParentFile(), file.getName().replace(".anim", ""));
                if (!projectDir.exists()) {
                    projectDir.mkdir();
                }

                // 保存项目信息
                var projectInfo = new Properties();
                projectInfo.setProperty("frameCount", String.valueOf(frames.size()));
                projectInfo.setProperty("currentFrameIndex", String.valueOf(currentFrameIndex));
                projectInfo.setProperty("currentColor", String.format("%d,%d,%d", currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue()));
                projectInfo.setProperty("brushSize", String.valueOf(brushSize));

                try (var out = new FileOutputStream(new File(projectDir, "project.info"))) {
                    projectInfo.store(out, "Animation Project Info");
                }

                // 保存每个帧的图片
                for (int i = 0; i < frames.size(); i++) {
                    var frame = frames.get(i);
                    if (frame.image != null) {
                        var frameFile = new File(projectDir, "frame_" + i + ".png");
                        ImageIO.write(frame.image, "png", frameFile);
                    }
                }

                JOptionPane.showMessageDialog(this, Texts.SAVE_PROJECT_SUCCESS + projectDir.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, Texts.ERROR_SAVE_PROJECT_FAILED + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadProject() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(Texts.LOAD_PROJECT_DIALOG_TITLE);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                var projectDir = fileChooser.getSelectedFile();
                var projectInfoFile = new File(projectDir, "project.info");

                if (!projectInfoFile.exists()) {
                    JOptionPane.showMessageDialog(this, Texts.ERROR_INVALID_PROJECT_FOLDER, "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 读取项目信息
                var projectInfo = new Properties();
                try (var in = new FileInputStream(projectInfoFile)) {
                    projectInfo.load(in);
                }

                var frameCount = Integer.parseInt(projectInfo.getProperty("frameCount"));
                var loadedCurrentFrameIndex = Integer.parseInt(projectInfo.getProperty("currentFrameIndex"));
                var colorParts = projectInfo.getProperty("currentColor").split(",");
                var loadedCurrentColor = new Color(
                    Integer.parseInt(colorParts[0]),
                    Integer.parseInt(colorParts[1]),
                    Integer.parseInt(colorParts[2])
                );
                var loadedBrushSize = Integer.parseInt(projectInfo.getProperty("brushSize"));

                // 清空当前项目
                frames.clear();
                frameListModel.clear();

                // 加载帧图片
                for (int i = 0; i < frameCount; i++) {
                    var frameFile = new File(projectDir, "frame_" + i + ".png");
                    var frame = new AnimationFrame("帧 " + (i + 1));

                    if (frameFile.exists()) {
                        frame.image = ImageIO.read(frameFile);
                    }

                    frames.add(frame);
                    frameListModel.addElement(frame);
                }

                // 更新当前状态
                currentFrameIndex = Math.min(loadedCurrentFrameIndex, frames.size() - 1);
                currentColor = loadedCurrentColor;
                brushSize = loadedBrushSize;

                // 更新UI
                if (currentFrameIndex >= 0) {
                    frameList.setSelectedIndex(currentFrameIndex);
                }
                canvas.setColor(currentColor);
                canvas.setBrushSize(brushSize);
                updateStatus();

                JOptionPane.showMessageDialog(this, Texts.LOAD_PROJECT_SUCCESS + projectDir.getName());
            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, Texts.ERROR_LOAD_PROJECT_FAILED + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    enum DrawingTool {
        PEN, ERASER
    }
    
    static class AnimationFrame implements Serializable {
        private static final long serialVersionUID = 1L;
        String name;
        transient BufferedImage image; // BufferedImage 不序列化，需要特殊处理

        AnimationFrame(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    class AnimationCanvas extends JPanel {
        private AnimationFrame currentFrame;
        private DrawingTool tool = DrawingTool.PEN;
        private Point lastPoint;
        
        AnimationCanvas() {
            setPreferredSize(new Dimension(400, 300));
            setBackground(Color.WHITE);
            
            var mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastPoint = e.getPoint();
                    drawPoint(e.getPoint());
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (lastPoint != null) {
                        drawLine(lastPoint, e.getPoint());
                        lastPoint = e.getPoint();
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    lastPoint = null;
                }
            };
            
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }
        
        void setFrame(AnimationFrame frame) {
            this.currentFrame = frame;
            repaint();
        }
        
        void setTool(DrawingTool tool) {
            this.tool = tool;
        }
        
        void setColor(Color color) {
            currentColor = color;
        }
        
        void setBrushSize(int size) {
            brushSize = size;
        }
        
        private void drawPoint(Point point) {
            ensureImage();
            var g2d = currentFrame.image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(tool == DrawingTool.ERASER ? Color.WHITE : currentColor);
            g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.fillOval(point.x - brushSize/2, point.y - brushSize/2, brushSize, brushSize);
            g2d.dispose();
            repaint();
        }
        
        private void drawLine(Point start, Point end) {
            ensureImage();
            var g2d = currentFrame.image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(tool == DrawingTool.ERASER ? Color.WHITE : currentColor);
            g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(start.x, start.y, end.x, end.y);
            g2d.dispose();
            repaint();
        }
        
        private void ensureImage() {
            if (currentFrame != null && currentFrame.image == null) {
                currentFrame.image = new BufferedImage(
                    getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                var g2d = currentFrame.image.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (currentFrame != null && currentFrame.image != null) {
                g.drawImage(currentFrame.image, 0, 0, null);
            }
        }
    }
    
    class FrameListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof AnimationFrame frame) {
                setText(frame.name);
                
                // 创建缩略图
                if (frame.image != null) {
                    var thumbnail = new BufferedImage(32, 24, BufferedImage.TYPE_INT_RGB);
                    var g2d = thumbnail.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                       RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(frame.image, 0, 0, 32, 24, null);
                    g2d.dispose();
                    setIcon(new ImageIcon(thumbnail));
                } else {
                    setIcon(null);
                }
            }
            
            return this;
        }
    }
}
