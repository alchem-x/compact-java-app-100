import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new ColorPicker().setVisible(true);
    });
}

static class ColorPicker extends JFrame {
    private final JPanel colorDisplay;
    private final JSlider redSlider;
    private final JSlider greenSlider;
    private final JSlider blueSlider;
    private final JTextField hexField;
    private final JTextField rgbField;
    private final JLabel redLabel;
    private final JLabel greenLabel;
    private final JLabel blueLabel;
    private final JPanel palettePanel;
    
    private Color currentColor = Color.BLACK;
    
    public ColorPicker() {
        colorDisplay = new JPanel();
        redSlider = new JSlider(0, 255, 0);
        greenSlider = new JSlider(0, 255, 0);
        blueSlider = new JSlider(0, 255, 0);
        hexField = new JTextField(8);
        rgbField = new JTextField(12);
        redLabel = new JLabel("红: 0");
        greenLabel = new JLabel("绿: 0");
        blueLabel = new JLabel("蓝: 0");
        palettePanel = new JPanel();
        
        initializeGUI();
        setupEventHandlers();
        updateColor();
    }
    
    private void initializeGUI() {
        setTitle("🎨 颜色选择器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 主面板
        var mainPanel = new JPanel(new BorderLayout());
        
        // 左侧控制面板
        createControlPanel(mainPanel);
        
        // 右侧颜色显示和调色板
        createDisplayPanel(mainPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        setSize(600, 450);
        setLocationRelativeTo(null);
    }
    
    private void createControlPanel(JPanel parent) {
        var controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("颜色调节"));
        controlPanel.setPreferredSize(new Dimension(250, 0));
        
        // RGB滑块
        controlPanel.add(createSliderPanel("红色", redSlider, redLabel, Color.RED));
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(createSliderPanel("绿色", greenSlider, greenLabel, Color.GREEN));
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(createSliderPanel("蓝色", blueSlider, blueLabel, Color.BLUE));
        
        controlPanel.add(Box.createVerticalStrut(20));
        
        // 颜色值显示
        createColorValuePanel(controlPanel);
        
        controlPanel.add(Box.createVerticalStrut(20));
        
        // 预设颜色按钮
        createPresetColorsPanel(controlPanel);
        
        parent.add(controlPanel, BorderLayout.WEST);
    }
    
    private JPanel createSliderPanel(String name, JSlider slider, JLabel valueLabel, Color trackColor) {
        var panel = new JPanel(new BorderLayout());
        
        var titleLabel = new JLabel(name);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        slider.setMajorTickSpacing(51);
        slider.setMinorTickSpacing(17);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        valueLabel.setPreferredSize(new Dimension(50, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(slider, BorderLayout.CENTER);
        panel.add(valueLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void createColorValuePanel(JPanel parent) {
        var valuePanel = new JPanel();
        valuePanel.setBorder(BorderFactory.createTitledBorder("颜色值"));
        valuePanel.setLayout(new GridLayout(3, 2, 5, 5));
        
        valuePanel.add(new JLabel("HEX:"));
        hexField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        hexField.setEditable(false);
        valuePanel.add(hexField);
        
        valuePanel.add(new JLabel("RGB:"));
        rgbField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        rgbField.setEditable(false);
        valuePanel.add(rgbField);
        
        var copyHexButton = new JButton("复制HEX");
        copyHexButton.addActionListener(e -> copyToClipboard(hexField.getText()));
        valuePanel.add(copyHexButton);
        
        var copyRgbButton = new JButton("复制RGB");
        copyRgbButton.addActionListener(e -> copyToClipboard(rgbField.getText()));
        valuePanel.add(copyRgbButton);
        
        parent.add(valuePanel);
    }
    
    private void createPresetColorsPanel(JPanel parent) {
        var presetPanel = new JPanel();
        presetPanel.setBorder(BorderFactory.createTitledBorder("预设颜色"));
        presetPanel.setLayout(new GridLayout(3, 4, 2, 2));
        
        // 常用颜色
        var presetColors = new Color[]{
            Color.BLACK, Color.WHITE, Color.RED, Color.GREEN,
            Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN,
            Color.ORANGE, Color.PINK, Color.GRAY, Color.LIGHT_GRAY
        };
        
        for (var color : presetColors) {
            var colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setPreferredSize(new Dimension(30, 25));
            colorButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            colorButton.addActionListener(e -> setColor(color));
            presetPanel.add(colorButton);
        }
        
        parent.add(presetPanel);
    }
    
    private void createDisplayPanel(JPanel parent) {
        var displayPanel = new JPanel(new BorderLayout());
        
        // 颜色显示区域
        colorDisplay.setBorder(BorderFactory.createTitledBorder("当前颜色"));
        colorDisplay.setPreferredSize(new Dimension(0, 150));
        colorDisplay.setBackground(currentColor);
        
        // 添加点击复制功能
        colorDisplay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                copyToClipboard(hexField.getText());
                JOptionPane.showMessageDialog(ColorPicker.this, 
                    "颜色值已复制到剪贴板: " + hexField.getText(), 
                    "复制成功", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // 调色板
        createColorPalette();
        
        displayPanel.add(colorDisplay, BorderLayout.NORTH);
        displayPanel.add(palettePanel, BorderLayout.CENTER);
        
        parent.add(displayPanel, BorderLayout.CENTER);
    }
    
    private void createColorPalette() {
        palettePanel.setBorder(BorderFactory.createTitledBorder("调色板"));
        palettePanel.setLayout(new GridLayout(12, 20, 1, 1));
        
        // 创建渐变调色板
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 20; col++) {
                var hue = col / 20.0f;
                var saturation = 1.0f - (row / 12.0f);
                var brightness = 1.0f;
                
                var color = Color.getHSBColor(hue, saturation, brightness);
                var colorButton = new JButton();
                colorButton.setBackground(color);
                colorButton.setPreferredSize(new Dimension(15, 15));
                colorButton.setBorder(null);
                colorButton.addActionListener(e -> setColor(color));
                
                palettePanel.add(colorButton);
            }
        }
    }
    
    private void setupEventHandlers() {
        redSlider.addChangeListener(e -> {
            redLabel.setText("红: " + redSlider.getValue());
            updateColorFromSliders();
        });
        
        greenSlider.addChangeListener(e -> {
            greenLabel.setText("绿: " + greenSlider.getValue());
            updateColorFromSliders();
        });
        
        blueSlider.addChangeListener(e -> {
            blueLabel.setText("蓝: " + blueSlider.getValue());
            updateColorFromSliders();
        });
    }
    
    private void updateColorFromSliders() {
        currentColor = new Color(
            redSlider.getValue(),
            greenSlider.getValue(),
            blueSlider.getValue()
        );
        updateColor();
    }
    
    private void setColor(Color color) {
        currentColor = color;
        
        // 更新滑块
        redSlider.setValue(color.getRed());
        greenSlider.setValue(color.getGreen());
        blueSlider.setValue(color.getBlue());
        
        // 更新标签
        redLabel.setText("红: " + color.getRed());
        greenLabel.setText("绿: " + color.getGreen());
        blueLabel.setText("蓝: " + color.getBlue());
        
        updateColor();
    }
    
    private void updateColor() {
        // 更新颜色显示
        colorDisplay.setBackground(currentColor);
        
        // 更新颜色值
        var hex = String.format("#%02X%02X%02X", 
            currentColor.getRed(), 
            currentColor.getGreen(), 
            currentColor.getBlue());
        hexField.setText(hex);
        
        var rgb = String.format("rgb(%d,%d,%d)", 
            currentColor.getRed(), 
            currentColor.getGreen(), 
            currentColor.getBlue());
        rgbField.setText(rgb);
        
        // 根据颜色亮度调整文字颜色
        var brightness = (currentColor.getRed() * 299 + 
                         currentColor.getGreen() * 587 + 
                         currentColor.getBlue() * 114) / 1000;
        
        var textColor = brightness > 128 ? Color.BLACK : Color.WHITE;
        
        // 在颜色显示区域添加颜色信息文本
        colorDisplay.removeAll();
        var infoLabel = new JLabel("<html><center>" + 
            hexField.getText() + "<br>" + 
            rgbField.getText() + "<br>" +
            "点击复制" + "</center></html>", JLabel.CENTER);
        infoLabel.setForeground(textColor);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        colorDisplay.add(infoLabel);
        
        colorDisplay.revalidate();
        colorDisplay.repaint();
    }
    
    private void copyToClipboard(String text) {
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(text);
        clipboard.setContents(selection, null);
    }
}
