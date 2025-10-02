import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

/**
 * 调色板应用 - 颜色选择和搭配工具
 * 功能：颜色选择、配色方案、颜色值显示和复制
 */
public class ColorPalette extends JFrame {
    private JPanel mainPanel, colorPanel, controlPanel;
    private JLabel colorLabel, hexLabel, rgbLabel, hsvLabel;
    private JButton pickButton, copyHexButton, copyRgbButton;
    private JSlider redSlider, greenSlider, blueSlider;
    private JTextField hexField;
    private Color currentColor = Color.BLUE;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ColorPalette().setVisible(true);
            } catch (Exception e) {
                System.err.println("启动调色板应用失败: " + e.getMessage());
            }
        });
    }

    public ColorPalette() {
        super("调色板工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        initializeComponents();
        updateColorDisplay();

        // 演示模式：5秒后自动关闭
        startDemoTimer();
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 颜色显示区域
        colorPanel = new JPanel(new BorderLayout());
        colorPanel.setBorder(BorderFactory.createTitledBorder("当前颜色"));
        colorPanel.setPreferredSize(new Dimension(200, 150));

        colorLabel = new JLabel("颜色预览", SwingConstants.CENTER);
        colorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        colorLabel.setOpaque(true);
        colorPanel.add(colorLabel, BorderLayout.CENTER);

        // 颜色信息显示
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("颜色信息"));

        hexLabel = new JLabel("HEX: #000000");
        rgbLabel = new JLabel("RGB: (0, 0, 0)");
        hsvLabel = new JLabel("HSV: (0, 0, 0)");

        hexField = new JTextField("#000000");
        hexField.setEditable(false);

        infoPanel.add(hexLabel);
        infoPanel.add(rgbLabel);
        infoPanel.add(hsvLabel);
        infoPanel.add(hexField);

        // 颜色显示区域组合
        JPanel displayPanel = new JPanel(new BorderLayout(10, 10));
        displayPanel.add(colorPanel, BorderLayout.WEST);
        displayPanel.add(infoPanel, BorderLayout.CENTER);

        // 控制面板
        controlPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("颜色控制"));

        // RGB滑块
        redSlider = createColorSlider("红色 (R):", 0, 255, 0);
        greenSlider = createColorSlider("绿色 (G):", 0, 255, 0);
        blueSlider = createColorSlider("蓝色 (B):", 0, 255, 255);

        controlPanel.add(redSlider);
        controlPanel.add(greenSlider);
        controlPanel.add(blueSlider);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        pickButton = new JButton("🎨 颜色选择器");
        pickButton.addActionListener(e -> showColorPicker());

        copyHexButton = new JButton("复制HEX");
        copyHexButton.addActionListener(e -> copyToClipboard(hexField.getText()));

        copyRgbButton = new JButton("复制RGB");
        copyRgbButton.addActionListener(e -> copyToClipboard(rgbLabel.getText().replace("RGB: ", "")));

        buttonPanel.add(pickButton);
        buttonPanel.add(copyHexButton);
        buttonPanel.add(copyRgbButton);

        // 配色方案面板
        JPanel palettePanel = createPalettePanel();

        // 主面板布局
        mainPanel.add(displayPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 添加配色方案
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(palettePanel, BorderLayout.CENTER);
        mainPanel.add(eastPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createColorSlider(String label, int min, int max, int value) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel sliderLabel = new JLabel(label);
        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        slider.addChangeListener(e -> updateColorFromSliders());

        panel.add(sliderLabel, BorderLayout.WEST);
        panel.add(slider, BorderLayout.CENTER);

        if (label.contains("红色")) redSlider = slider;
        else if (label.contains("绿色")) greenSlider = slider;
        else if (label.contains("蓝色")) blueSlider = slider;

        return panel;
    }

    private JPanel createPalettePanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("配色方案"));

        Color[] paletteColors = {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK,
            new Color(138, 43, 226), new Color(255, 140, 0),
            new Color(0, 128, 128), new Color(128, 0, 128)
        };

        String[] colorNames = {
            "红色", "绿色", "蓝色", "黄色", "品红", "青色",
            "橙色", "粉色", "紫罗兰", "深橙", "青色", "紫色"
        };

        for (int i = 0; i < paletteColors.length; i++) {
            final Color color = paletteColors[i];
            final String name = colorNames[i];

            JButton colorButton = new JButton(name);
            colorButton.setBackground(color);
            colorButton.setForeground(getContrastColor(color));
            colorButton.addActionListener(e -> setCurrentColor(color));
            panel.add(colorButton);
        }

        return panel;
    }

    private void showColorPicker() {
        Color newColor = JColorChooser.showDialog(this, "选择颜色", currentColor);
        if (newColor != null) {
            setCurrentColor(newColor);
        }
    }

    private void setCurrentColor(Color color) {
        currentColor = color;
        updateColorDisplay();
        updateSliders();
    }

    private void updateColorFromSliders() {
        int red = redSlider.getValue();
        int green = greenSlider.getValue();
        int blue = blueSlider.getValue();

        currentColor = new Color(red, green, blue);
        updateColorDisplay();
    }

    private void updateSliders() {
        redSlider.setValue(currentColor.getRed());
        greenSlider.setValue(currentColor.getGreen());
        blueSlider.setValue(currentColor.getBlue());
    }

    private void updateColorDisplay() {
        colorLabel.setBackground(currentColor);
        colorLabel.setForeground(getContrastColor(currentColor));

        String hex = String.format("#%02X%02X%02X",
            currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());
        String rgb = String.format("RGB: (%d, %d, %d)",
            currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());

        float[] hsv = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
        String hsvStr = String.format("HSV: (%.0f, %.0f%%, %.0f%%)",
            hsv[0] * 360, hsv[1] * 100, hsv[2] * 100);

        hexLabel.setText("HEX: " + hex);
        rgbLabel.setText(rgb);
        hsvLabel.setText(hsvStr);
        hexField.setText(hex);
    }

    private Color getContrastColor(Color color) {
        // 计算亮度，决定使用黑色还是白色文字
        double brightness = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return brightness > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        JOptionPane.showMessageDialog(this, "已复制到剪贴板: " + text,
            "复制成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void startDemoTimer() {
        javax.swing.Timer timer = new javax.swing.Timer(5000, e -> {
            JOptionPane.showMessageDialog(this,
                "调色板演示结束！\n\n功能特点：\n• RGB滑块调节颜色\n• 配色方案快速选择\n• 多种颜色格式显示\n• 一键复制颜色值",
                "演示完成", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
        timer.setRepeats(false);
        timer.start();
    }
}