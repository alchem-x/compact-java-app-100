import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "📋 ASCII码表查看器";

    // 主界面标题
    static final String MAIN_TITLE = "🔤 ASCII码表查看器";

    // 按钮文本
    static final String COPY_BUTTON = "复制";
    static final String SAVE_BUTTON = "保存";
    static final String REFRESH_BUTTON = "刷新";

    // 面板标题
    static final String CONTROL_PANEL_TITLE = "控制面板";
    static final String TABLE_PANEL_TITLE = "ASCII码表";

    // 表格列名
    static final String TABLE_COLUMN_DEC = "十进制";
    static final String TABLE_COLUMN_HEX = "十六进制";
    static final String TABLE_COLUMN_CHAR = "字符";
    static final String TABLE_COLUMN_DESCRIPTION = "描述";

    // 状态消息
    static final String STATUS_READY = "就绪";
    static final String STATUS_COPIED = "已复制到剪贴板";
    static final String STATUS_SAVED = "文件已保存";
    static final String STATUS_REFRESHED = "表格已刷新";

    // 错误消息
    static final String ERROR_SAVE_FAILED = "保存文件失败: %s";
    static final String ERROR_COPY_FAILED = "复制到剪贴板失败";

    // 字符描述
    static final String DESC_NULL = "空字符 (NULL)";
    static final String DESC_START_OF_HEADING = "标题开始 (SOH)";
    static final String DESC_START_OF_TEXT = "正文开始 (STX)";
    static final String DESC_END_OF_TEXT = "正文结束 (ETX)";
    static final String DESC_END_OF_TRANSMISSION = "传输结束 (EOT)";
    static final String DESC_ENQUIRY = "询问 (ENQ)";
    static final String DESC_ACKNOWLEDGE = "确认 (ACK)";
    static final String DESC_BELL = "响铃 (BEL)";
    static final String DESC_BACKSPACE = "退格 (BS)";
    static final String DESC_TAB = "水平制表符 (HT)";
    static final String DESC_LINE_FEED = "换行 (LF)";
    static final String DESC_VERTICAL_TAB = "垂直制表符 (VT)";
    static final String DESC_FORM_FEED = "换页 (FF)";
    static final String DESC_CARRIAGE_RETURN = "回车 (CR)";
    static final String DESC_SHIFT_OUT = "移出 (SO)";
    static final String DESC_SHIFT_IN = "移入 (SI)";
    static final String DESC_DATA_LINK_ESCAPE = "数据链路转义 (DLE)";
    static final String DESC_DEVICE_CONTROL_1 = "设备控制1 (DC1)";
    static final String DESC_DEVICE_CONTROL_2 = "设备控制2 (DC2)";
    static final String DESC_DEVICE_CONTROL_3 = "设备控制3 (DC3)";
    static final String DESC_DEVICE_CONTROL_4 = "设备控制4 (DC4)";
    static final String DESC_NEGATIVE_ACKNOWLEDGE = "否认 (NAK)";
    static final String DESC_SYNCHRONOUS_IDLE = "同步空闲 (SYN)";
    static final String DESC_END_OF_TRANSMISSION_BLOCK = "传输块结束 (ETB)";
    static final String DESC_CANCEL = "取消 (CAN)";
    static final String DESC_END_OF_MEDIUM = "介质结束 (EM)";
    static final String DESC_SUBSTITUTE = "替换 (SUB)";
    static final String DESC_ESCAPE = "转义 (ESC)";
    static final String DESC_FILE_SEPARATOR = "文件分隔符 (FS)";
    static final String DESC_GROUP_SEPARATOR = "组分隔符 (GS)";
    static final String DESC_RECORD_SEPARATOR = "记录分隔符 (RS)";
    static final String DESC_UNIT_SEPARATOR = "单元分隔符 (US)";
    static final String DESC_SPACE = "空格 (SP)";
    static final String DESC_DELETE = "删除 (DEL)";

    // 文件对话框
    static final String FILE_CHOOSER_TITLE = "保存ASCII码表";
    static final String FILE_FILTER_TEXT = "文本文件 (*.txt)";
    static final String FILE_FILTER_CSV = "CSV文件 (*.csv)";

    // 保存选项
    static final String SAVE_FORMAT_TXT = "文本格式 (*.txt)";
    static final String SAVE_FORMAT_CSV = "CSV格式 (*.csv)";

    // 帮助信息
    static final String HELP_MESSAGE = """
        ASCII码表查看器使用说明：

        • 表格显示所有ASCII字符的十进制、十六进制和描述
        • 可打印字符直接显示，控制字符显示描述
        • 点击表格行可选择字符
        • 使用复制按钮复制选中字符
        • 使用保存按钮导出整个表格
        • 支持文本和CSV两种格式导出

        快捷键：
        Ctrl+C - 复制选中字符
        Ctrl+S - 保存表格
        F5 - 刷新表格
        F1 - 显示帮助
        """;
}

/**
 * ASCII码表查看器
 * 显示所有ASCII字符及其对应编码
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new AsciiTable().setVisible(true);
    });
}

static class AsciiTable extends JFrame {
    // ===== 设计系统常量 =====
    // 主要颜色
    private static final Color BLUE = new Color(0, 122, 255);
    private static final Color GREEN = new Color(52, 199, 89);
    private static final Color RED = new Color(255, 59, 48);
    private static final Color ORANGE = new Color(255, 149, 0);
    private static final Color PURPLE = new Color(175, 82, 222);
    private static final Color TEAL = new Color(48, 176, 199);

    // 中性色
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color GRAY = new Color(142, 142, 147);
    private static final Color GRAY2 = new Color(174, 174, 178);
    private static final Color GRAY3 = new Color(199, 199, 204);
    private static final Color GRAY4 = new Color(209, 209, 214);
    private static final Color GRAY5 = new Color(229, 229, 234);
    private static final Color GRAY6 = new Color(242, 242, 247);

    // 文本颜色
    private static final Color LABEL = new Color(0, 0, 0);
    private static final Color SECONDARY_LABEL = new Color(60, 60, 67, 153);
    private static final Color TERTIARY_LABEL = new Color(60, 60, 67, 76);

    // 背景颜色
    private static final Color SYSTEM_BACKGROUND = new Color(255, 255, 255);
    private static final Color SECONDARY_SYSTEM_BACKGROUND = new Color(242, 242, 247);
    private static final Color TERTIARY_SYSTEM_BACKGROUND = new Color(255, 255, 255);

    // 字体
    private static final Font LARGE_TITLE = new Font("SF Pro Display", Font.BOLD, 34);
    private static final Font TITLE1 = new Font("SF Pro Display", Font.BOLD, 28);
    private static final Font TITLE2 = new Font("SF Pro Display", Font.BOLD, 22);
    private static final Font TITLE3 = new Font("SF Pro Display", Font.BOLD, 20);
    private static final Font HEADLINE = new Font("SF Pro Display", Font.BOLD, 17);
    private static final Font SUBHEADLINE = new Font("SF Pro Display", Font.PLAIN, 15);
    private static final Font BODY = new Font("SF Pro Text", Font.PLAIN, 17);
    private static final Font CALLOUT = new Font("SF Pro Text", Font.PLAIN, 16);
    private static final Font FOOTNOTE = new Font("SF Pro Text", Font.PLAIN, 13);
    private static final Font CAPTION1 = new Font("SF Pro Text", Font.PLAIN, 12);
    private static final Font CAPTION2 = new Font("SF Pro Text", Font.PLAIN, 11);

    // 间距
    private static final int SPACING_2 = 2;
    private static final int SPACING_4 = 4;
    private static final int SPACING_8 = 8;
    private static final int SPACING_12 = 12;
    private static final int SPACING_16 = 16;
    private static final int SPACING_20 = 20;
    private static final int SPACING_24 = 24;
    private static final int SPACING_32 = 32;

    // 圆角
    private static final int RADIUS_4 = 4;
    private static final int RADIUS_8 = 8;
    private static final int RADIUS_12 = 12;
    private static final int RADIUS_16 = 16;
    private static final int RADIUS_20 = 20;

    // ===== 应用状态 =====
    private JTable asciiTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private JTextField charField;
    private JTextField codeField;
    private JButton convertButton;
    
    public AsciiTable() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        this.initializeGUI();
        this.setupEventHandlers();
        this.loadAsciiTable();
        this.setupKeyboardShortcuts();

        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void initializeGUI() {
        // 初始化组件
        tableModel = new DefaultTableModel();
        asciiTable = new JTable(tableModel);
        searchField = new JTextField();
        searchButton = this.createPrimaryButton("🔍 " + Texts.COPY_BUTTON);
        clearButton = this.createSecondaryButton("🗑️ " + Texts.REFRESH_BUTTON);
        statusLabel = new JLabel(Texts.STATUS_READY);
        charField = new JTextField();
        codeField = new JTextField();
        convertButton = this.createPrimaryButton("🔄 " + Texts.COPY_BUTTON);

        // 主面板 - 使用设计系统
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_20, SPACING_20, SPACING_20, SPACING_20));

        // 标题面板
        var titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(SYSTEM_BACKGROUND);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_16, 0));

        var titleLabel = new JLabel(Texts.MAIN_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 创建各个面板
        this.createControlPanel();
        this.createTablePanel();
        this.createConvertPanel();
        this.createStatusBar();

        add(mainPanel);
    }
    
    private JButton createPrimaryButton(String text) {
        var button = new JButton(text);
        button.setFont(HEADLINE);
        button.setBackground(BLUE);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(80, 32));
        button.setOpaque(true);
        this.setupButtonHoverEffect(button, BLUE, new Color(0, 100, 235));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        var button = new JButton(text);
        button.setFont(HEADLINE);
        button.setBackground(GRAY6);
        button.setForeground(LABEL);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(80, 32));
        button.setOpaque(true);
        this.setupButtonHoverEffect(button, GRAY6, GRAY5);
        return button;
    }

    private void setupButtonHoverEffect(JButton button, Color originalColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent ev) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent ev) {
                button.setBackground(originalColor);
            }
        });
    }

    private void createControlPanel() {
        var controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_12, SPACING_8));
        controlPanel.setBackground(SYSTEM_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16)
        ));

        // 搜索标签
        var searchLabel = new JLabel("搜索字符或代码:");
        searchLabel.setFont(CAPTION1);
        searchLabel.setForeground(SECONDARY_LABEL);
        controlPanel.add(searchLabel);

        // 搜索框 - 使用Apple风格
        searchField.setPreferredSize(new Dimension(150, 32));
        searchField.setFont(BODY);
        searchField.setBackground(WHITE);
        searchField.setBorder(new RoundedBorder(RADIUS_8));
        controlPanel.add(searchField);

        // 搜索按钮 - 主要按钮
        searchButton = this.createPrimaryButton("🔍 搜索");
        searchButton.addActionListener(this::performSearch);
        controlPanel.add(searchButton);

        // 清空按钮 - 次要按钮
        clearButton = this.createSecondaryButton("🗑️ 清空");
        clearButton.addActionListener(this::clearSearch);
        controlPanel.add(clearButton);

        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void createTablePanel() {
        // 设置表格列
        var columnNames = new String[]{
            Texts.TABLE_COLUMN_DEC,
            Texts.TABLE_COLUMN_HEX,
            "八进制",
            "二进制",
            Texts.TABLE_COLUMN_CHAR,
            Texts.TABLE_COLUMN_DESCRIPTION
        };
        tableModel.setColumnIdentifiers(columnNames);

        // 表格样式 - Apple风格
        asciiTable.setFont(new Font("SF Mono", Font.PLAIN, 12));
        asciiTable.setRowHeight(24);
        asciiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        asciiTable.setGridColor(GRAY4);
        asciiTable.setShowGrid(true);
        asciiTable.setBackground(WHITE);
        asciiTable.setSelectionBackground(BLUE);
        asciiTable.setSelectionForeground(WHITE);
        asciiTable.getTableHeader().setFont(HEADLINE);
        asciiTable.getTableHeader().setBackground(GRAY6);
        asciiTable.getTableHeader().setForeground(LABEL);

        // 设置列宽
        var columnModel = asciiTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(60);  // 十进制
        columnModel.getColumn(1).setPreferredWidth(60);  // 十六进制
        columnModel.getColumn(2).setPreferredWidth(60);  // 八进制
        columnModel.getColumn(3).setPreferredWidth(80);  // 二进制
        columnModel.getColumn(4).setPreferredWidth(50);  // 字符
        columnModel.getColumn(5).setPreferredWidth(200); // 描述

        var scrollPane = new JScrollPane(asciiTable);
        scrollPane.setBorder(new RoundedBorder(RADIUS_8));
        scrollPane.setBackground(WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createConvertPanel() {
        var convertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_16, SPACING_12));
        convertPanel.setBackground(SYSTEM_BACKGROUND);
        convertPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        // 字符输入
        var charLabel = new JLabel("字符:");
        charLabel.setFont(CAPTION1);
        charLabel.setForeground(SECONDARY_LABEL);
        convertPanel.add(charLabel);

        charField.setPreferredSize(new Dimension(60, 32));
        charField.setFont(BODY);
        charField.setBackground(WHITE);
        charField.setBorder(new RoundedBorder(RADIUS_8));
        convertPanel.add(charField);

        // ASCII码输入
        var codeLabel = new JLabel("ASCII码:");
        codeLabel.setFont(CAPTION1);
        codeLabel.setForeground(SECONDARY_LABEL);
        convertPanel.add(codeLabel);

        codeField.setPreferredSize(new Dimension(80, 32));
        codeField.setFont(BODY);
        codeField.setBackground(WHITE);
        codeField.setBorder(new RoundedBorder(RADIUS_8));
        convertPanel.add(codeField);

        // 转换按钮
        convertButton = this.createPrimaryButton("🔄 转换");
        convertButton.addActionListener(this::performConvert);
        convertPanel.add(convertButton);

        add(convertPanel, BorderLayout.SOUTH);
    }
    
    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(GRAY6);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));

        statusLabel.setFont(CAPTION1);
        statusLabel.setForeground(SECONDARY_LABEL);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private void setupEventHandlers() {
        searchButton.addActionListener(this::performSearch);
        clearButton.addActionListener(this::clearSearch);
        convertButton.addActionListener(this::performConvert);
        searchField.addActionListener(this::performSearch);
        charField.addActionListener(this::performConvert);
        codeField.addActionListener(this::performConvert);
    }
    
    private void loadAsciiTable() {
        tableModel.setRowCount(0);
        
        for (int i = 0; i <= 127; i++) {
            var decimal = String.valueOf(i);
            var hex = String.format("0x%02X", i);
            var octal = String.format("%03o", i);
            var binary = String.format("%08d", Integer.parseInt(Integer.toBinaryString(i)));
            var character = getCharacterDisplay(i);
            var description = getCharacterDescription(i);
            
            tableModel.addRow(new Object[]{decimal, hex, octal, binary, character, description});
        }
    }
    
    private String getCharacterDisplay(int code) {
        if (code < 32) {
            return ""; // 控制字符不显示
        } else if (code == 127) {
            return ""; // DEL字符
        } else {
            return String.valueOf((char) code);
        }
    }
    
    private String getCharacterDescription(int code) {
        return switch (code) {
            case 0 -> "NUL (" + Texts.DESC_NULL + ")";
            case 1 -> "SOH (" + Texts.DESC_START_OF_HEADING + ")";
            case 2 -> "STX (" + Texts.DESC_START_OF_TEXT + ")";
            case 3 -> "ETX (" + Texts.DESC_END_OF_TEXT + ")";
            case 4 -> "EOT (" + Texts.DESC_END_OF_TRANSMISSION + ")";
            case 5 -> "ENQ (" + Texts.DESC_ENQUIRY + ")";
            case 6 -> "ACK (" + Texts.DESC_ACKNOWLEDGE + ")";
            case 7 -> "BEL (" + Texts.DESC_BELL + ")";
            case 8 -> "BS (" + Texts.DESC_BACKSPACE + ")";
            case 9 -> "HT (" + Texts.DESC_TAB + ")";
            case 10 -> "LF (" + Texts.DESC_LINE_FEED + ")";
            case 11 -> "VT (" + Texts.DESC_VERTICAL_TAB + ")";
            case 12 -> "FF (" + Texts.DESC_FORM_FEED + ")";
            case 13 -> "CR (" + Texts.DESC_CARRIAGE_RETURN + ")";
            case 14 -> "SO (" + Texts.DESC_SHIFT_OUT + ")";
            case 15 -> "SI (" + Texts.DESC_SHIFT_IN + ")";
            case 16 -> "DLE (" + Texts.DESC_DATA_LINK_ESCAPE + ")";
            case 17 -> "DC1 (" + Texts.DESC_DEVICE_CONTROL_1 + ")";
            case 18 -> "DC2 (" + Texts.DESC_DEVICE_CONTROL_2 + ")";
            case 19 -> "DC3 (" + Texts.DESC_DEVICE_CONTROL_3 + ")";
            case 20 -> "DC4 (" + Texts.DESC_DEVICE_CONTROL_4 + ")";
            case 21 -> "NAK (" + Texts.DESC_NEGATIVE_ACKNOWLEDGE + ")";
            case 22 -> "SYN (" + Texts.DESC_SYNCHRONOUS_IDLE + ")";
            case 23 -> "ETB (" + Texts.DESC_END_OF_TRANSMISSION_BLOCK + ")";
            case 24 -> "CAN (" + Texts.DESC_CANCEL + ")";
            case 25 -> "EM (" + Texts.DESC_END_OF_MEDIUM + ")";
            case 26 -> "SUB (" + Texts.DESC_SUBSTITUTE + ")";
            case 27 -> "ESC (" + Texts.DESC_ESCAPE + ")";
            case 28 -> "FS (" + Texts.DESC_FILE_SEPARATOR + ")";
            case 29 -> "GS (" + Texts.DESC_GROUP_SEPARATOR + ")";
            case 30 -> "RS (" + Texts.DESC_RECORD_SEPARATOR + ")";
            case 31 -> "US (" + Texts.DESC_UNIT_SEPARATOR + ")";
            case 32 -> "SPACE (" + Texts.DESC_SPACE + ")";
            case 127 -> "DEL (" + Texts.DESC_DELETE + ")";
            default -> {
                if (code >= 33 && code <= 126) {
                    yield "可打印字符";
                } else {
                    yield "扩展ASCII";
                }
            }
        };
    }
    
    private void performSearch(ActionEvent e) {
        var searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            statusLabel.setText("请输入搜索内容");
            return;
        }

        try {
            // 尝试作为数字搜索
            var code = Integer.parseInt(searchText);
            if (code >= 0 && code <= 127) {
                asciiTable.setRowSelectionInterval(code, code);
                asciiTable.scrollRectToVisible(asciiTable.getCellRect(code, 0, true));
                statusLabel.setText("找到ASCII码: " + code);
                return;
            }
        } catch (NumberFormatException ignored) {
            // 不是数字，继续作为字符搜索
        }

        // 作为字符搜索
        if (searchText.length() == 1) {
            var ch = searchText.charAt(0);
            var code = (int) ch;
            if (code <= 127) {
                asciiTable.setRowSelectionInterval(code, code);
                asciiTable.scrollRectToVisible(asciiTable.getCellRect(code, 0, true));
                statusLabel.setText("找到字符 '" + ch + "' (ASCII: " + code + ")");
                return;
            }
        }

        statusLabel.setText("未找到: " + searchText);
    }

    private void clearSearch(ActionEvent e) {
        searchField.setText("");
        asciiTable.clearSelection();
        statusLabel.setText(Texts.STATUS_READY);
    }
    
    private void performConvert(ActionEvent e) {
        var charText = charField.getText();
        var codeText = codeField.getText().trim();

        try {
            if (!charText.isEmpty()) {
                // 字符转ASCII码
                var ch = charText.charAt(0);
                var code = (int) ch;
                codeField.setText(String.valueOf(code));
                statusLabel.setText("字符 '" + ch + "' 的ASCII码是 " + code);
            } else if (!codeText.isEmpty()) {
                // ASCII码转字符
                var code = Integer.parseInt(codeText);
                if (code >= 0 && code <= 127) {
                    var ch = (char) code;
                    charField.setText(String.valueOf(ch));
                    statusLabel.setText("ASCII码 " + code + " 对应字符 '" + ch + "'");
                } else {
                    statusLabel.setText("ASCII码范围应为 0-127");
                }
            } else {
                statusLabel.setText("请输入字符或ASCII码");
            }
        } catch (NumberFormatException ex) {
            statusLabel.setText("请输入有效的数字");
        }
    }

    /**
     * 圆角边框类
     */
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            var g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(GRAY4);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_C:
                        // C键复制选中字符
                        if (ev.isControlDown()) {
                            copySelectedCharacter();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_S:
                        // S键保存表格
                        if (ev.isControlDown()) {
                            saveTable(new ActionEvent(AsciiTable.this, ActionEvent.ACTION_PERFORMED, "save"));
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_F5:
                        // F5键刷新表格
                        loadAsciiTable();
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

    private void copySelectedCharacter() {
        var selectedRow = asciiTable.getSelectedRow();
        if (selectedRow != -1) {
            var character = tableModel.getValueAt(selectedRow, 3).toString();
            var stringSelection = new java.awt.datatransfer.StringSelection(character);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            statusLabel.setText(Texts.STATUS_COPIED);
        }
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this, Texts.HELP_MESSAGE, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }

    // ActionEvent包装方法（用于键盘快捷键）
    private void saveTable(ActionEvent e) {
        // TODO: 实现保存功能
        statusLabel.setText("保存功能待实现");
    }
}
