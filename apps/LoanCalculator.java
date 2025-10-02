import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "🏦 贷款计算器";

    // 主界面标题
    static final String MAIN_TITLE = "🏦 贷款计算器";

    // 输入标签
    static final String LOAN_AMOUNT_LABEL = "贷款金额:";
    static final String INTEREST_RATE_LABEL = "年利率:";
    static final String LOAN_TERM_LABEL = "贷款期限:";
    static final String TERM_TYPE_YEAR = "年";
    static final String TERM_TYPE_MONTH = "月";
    static final String PAYMENT_FREQUENCY_LABEL = "还款频率:";
    static final String FREQUENCY_MONTHLY = "每月";
    static final String FREQUENCY_QUARTERLY = "每季度";
    static final String FREQUENCY_YEARLY = "每年";

    // 按钮文本
    static final String CALCULATE_BUTTON = "💰 计算还款";
    static final String CLEAR_BUTTON = "🗑️ 清空";

    // 结果标签
    static final String RESULT_PANEL_TITLE = "计算结果";
    static final String MONTHLY_PAYMENT_LABEL = "每期还款: ¥";
    static final String TOTAL_PAYMENT_LABEL = "还款总额: ¥";
    static final String TOTAL_INTEREST_LABEL = "利息总额: ¥";
    static final String AMORTIZATION_TITLE = "分期还款明细";

    // 表格标题
    static final String TABLE_HEADER_NUMBER = "期数";
    static final String TABLE_HEADER_PAYMENT = "还款额";
    static final String TABLE_HEADER_PRINCIPAL = "本金";
    static final String TABLE_HEADER_INTEREST = "利息";
    static final String TABLE_HEADER_REMAINING = "剩余本金";

    // 错误消息
    static final String ERROR_INVALID_INPUT = "请输入有效的数值！";
    static final String ERROR_INVALID_NUMBER = "请输入有效的数字！";
    static final String ERROR_TITLE = "错误";

    // 货币格式
    static final String CURRENCY_FORMAT_PREFIX = "¥";
    static final String CURRENCY_FORMAT_SUFFIX = "";

    // 单位
    static final String UNIT_YUAN = "元";
    static final String UNIT_PERCENT = "%";

    // 示例数据
    static final String SAMPLE_LOAN_AMOUNT = "1000000";
    static final String SAMPLE_INTEREST_RATE = "4.9";
    static final String SAMPLE_LOAN_TERM = "30";

    // 状态消息
    static final String STATUS_CALCULATION_COMPLETE = "计算完成";
    static final String STATUS_CLEARED = "已清空";
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new LoanCalculator().setVisible(true);
    });
}

static class LoanCalculator extends JFrame {
    // ===== Apple设计系统常量 =====
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

    // 语义颜色
    private static final Color LABEL = new Color(0, 0, 0);
    private static final Color SECONDARY_LABEL = new Color(60, 60, 67, 153);
    private static final Color TERTIARY_LABEL = new Color(60, 60, 67, 76);

    // 背景
    private static final Color SYSTEM_BACKGROUND = new Color(255, 255, 255);
    private static final Color SECONDARY_SYSTEM_BACKGROUND = new Color(242, 242, 247);

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
    private static final Font MONO = new Font("SF Mono", Font.PLAIN, 12);

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

    // 按钮尺寸
    private static final Dimension BUTTON_REGULAR = new Dimension(120, 44);
    private static final Dimension BUTTON_LARGE = new Dimension(160, 50);

    // ===== 应用组件 =====
    private JTextField loanAmountField;
    private JTextField interestRateField;
    private JTextField loanTermField;
    private JComboBox<String> termTypeCombo;
    private JComboBox<String> paymentFrequencyCombo;
    private JLabel monthlyPaymentLabel;
    private JLabel totalPaymentLabel;
    private JLabel totalInterestLabel;
    private JTextArea amortizationArea;
    private JButton calculateButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private DecimalFormat percentFormat = new DecimalFormat("0.00");

    public LoanCalculator() {
        this.initializeGUI();
        this.setupEventHandlers();
        this.loadSampleData();
        this.setupKeyboardShortcuts();
    }

    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(SYSTEM_BACKGROUND);

        // 主面板 - 使用苹果设计风格
        var mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(SYSTEM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_24, SPACING_24, SPACING_24, SPACING_24));
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_12, SPACING_12, SPACING_12, SPACING_12);

        // 标题 - 使用苹果风格大标题
        var titleLabel = new JLabel(Texts.MAIN_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(TITLE2);
        titleLabel.setForeground(LABEL);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // 贷款金额 - 使用苹果风格输入框
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        var loanAmountLabel = new JLabel(Texts.LOAN_AMOUNT_LABEL);
        loanAmountLabel.setFont(HEADLINE);
        loanAmountLabel.setForeground(LABEL);
        mainPanel.add(loanAmountLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loanAmountField = new JTextField(15);
        loanAmountField.setFont(MONO);
        loanAmountField.setBackground(GRAY6);
        loanAmountField.setForeground(LABEL);
        loanAmountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        mainPanel.add(loanAmountField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        var yuanLabel = new JLabel(Texts.UNIT_YUAN);
        yuanLabel.setFont(BODY);
        yuanLabel.setForeground(SECONDARY_LABEL);
        mainPanel.add(yuanLabel, gbc);

        // 年利率 - 使用苹果风格输入框
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        var interestRateLabel = new JLabel(Texts.INTEREST_RATE_LABEL);
        interestRateLabel.setFont(HEADLINE);
        interestRateLabel.setForeground(LABEL);
        mainPanel.add(interestRateLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        interestRateField = new JTextField(15);
        interestRateField.setFont(MONO);
        interestRateField.setBackground(GRAY6);
        interestRateField.setForeground(LABEL);
        interestRateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        mainPanel.add(interestRateField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        var percentLabel = new JLabel(Texts.UNIT_PERCENT);
        percentLabel.setFont(BODY);
        percentLabel.setForeground(SECONDARY_LABEL);
        mainPanel.add(percentLabel, gbc);

        // 贷款期限 - 使用苹果风格输入框和下拉框
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        var loanTermLabel = new JLabel(Texts.LOAN_TERM_LABEL);
        loanTermLabel.setFont(HEADLINE);
        loanTermLabel.setForeground(LABEL);
        mainPanel.add(loanTermLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loanTermField = new JTextField(10);
        loanTermField.setFont(MONO);
        loanTermField.setBackground(GRAY6);
        loanTermField.setForeground(LABEL);
        loanTermField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY4),
            BorderFactory.createEmptyBorder(SPACING_8, SPACING_12, SPACING_8, SPACING_12)
        ));
        mainPanel.add(loanTermField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        termTypeCombo = new JComboBox<>(new String[]{Texts.TERM_TYPE_YEAR, Texts.TERM_TYPE_MONTH});
        termTypeCombo.setFont(BODY);
        termTypeCombo.setBackground(WHITE);
        termTypeCombo.setForeground(LABEL);
        mainPanel.add(termTypeCombo, gbc);

        // 还款频率 - 使用苹果风格标签和下拉框
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        var paymentFrequencyLabel = new JLabel(Texts.PAYMENT_FREQUENCY_LABEL);
        paymentFrequencyLabel.setFont(HEADLINE);
        paymentFrequencyLabel.setForeground(LABEL);
        mainPanel.add(paymentFrequencyLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        paymentFrequencyCombo = new JComboBox<>(new String[]{Texts.FREQUENCY_MONTHLY, Texts.FREQUENCY_QUARTERLY, Texts.FREQUENCY_YEARLY});
        paymentFrequencyCombo.setFont(BODY);
        paymentFrequencyCombo.setBackground(WHITE);
        paymentFrequencyCombo.setForeground(LABEL);
        mainPanel.add(paymentFrequencyCombo, gbc);

        // 按钮面板 - 使用苹果风格按钮
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_20, SPACING_12));
        buttonPanel.setBackground(SYSTEM_BACKGROUND);
        calculateButton = this.createPrimaryButton(Texts.CALCULATE_BUTTON, this::calculateLoan);
        clearButton = this.createSecondaryButton(Texts.CLEAR_BUTTON, this::clearFields);

        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        // 结果显示区域 - 使用苹果风格卡片
        var resultPanel = new JPanel(new GridLayout(3, 1, SPACING_12, SPACING_12));
        resultPanel.setBackground(SECONDARY_SYSTEM_BACKGROUND);
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(Texts.RESULT_PANEL_TITLE),
            BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));

        monthlyPaymentLabel = new JLabel(Texts.MONTHLY_PAYMENT_LABEL + "0.00");
        monthlyPaymentLabel.setFont(TITLE3);
        monthlyPaymentLabel.setForeground(GREEN);

        totalPaymentLabel = new JLabel(Texts.TOTAL_PAYMENT_LABEL + "0.00");
        totalPaymentLabel.setFont(BODY);
        totalPaymentLabel.setForeground(LABEL);

        totalInterestLabel = new JLabel(Texts.TOTAL_INTEREST_LABEL + "0.00");
        totalInterestLabel.setFont(BODY);
        totalInterestLabel.setForeground(RED);

        resultPanel.add(monthlyPaymentLabel);
        resultPanel.add(totalPaymentLabel);
        resultPanel.add(totalInterestLabel);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(resultPanel, gbc);

        // 分期还款表 - 使用苹果风格文本区域
        amortizationArea = new JTextArea(10, 30);
        amortizationArea.setFont(MONO);
        amortizationArea.setEditable(false);
        amortizationArea.setBackground(GRAY6);
        amortizationArea.setForeground(LABEL);
        amortizationArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(Texts.AMORTIZATION_TITLE),
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_12, SPACING_12, SPACING_12)
        ));
        var scrollPane = new JScrollPane(amortizationArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(GRAY6);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(scrollPane, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 设置窗口
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    // ===== 事件处理方法 =====
    private void setupEventHandlers() {
        // 按钮事件已经通过方法引用设置
        // 添加回车键支持
        loanAmountField.addActionListener(this::calculateLoan);
        interestRateField.addActionListener(this::calculateLoan);
        loanTermField.addActionListener(this::calculateLoan);
    }

    private void loadSampleData() {
        loanAmountField.setText(Texts.SAMPLE_LOAN_AMOUNT);
        interestRateField.setText(Texts.SAMPLE_INTEREST_RATE);
        loanTermField.setText(Texts.SAMPLE_LOAN_TERM);
    }

    // ===== 按钮创建方法 =====
    private JButton createPrimaryButton(String text, java.util.function.Consumer<ActionEvent> action) {
        return this.createStyledButton(text, BLUE, WHITE, action);
    }

    private JButton createSecondaryButton(String text, java.util.function.Consumer<ActionEvent> action) {
        return this.createStyledButton(text, GRAY6, LABEL, action);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor, java.util.function.Consumer<ActionEvent> action) {
        var button = new JButton(text);
        button.setFont(HEADLINE);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(SPACING_12, SPACING_16, SPACING_12, SPACING_16),
            BorderFactory.createEmptyBorder(SPACING_4, SPACING_8, SPACING_4, SPACING_8)
        ));
        button.setPreferredSize(BUTTON_LARGE);

        // 设置悬停效果
        this.setupButtonHoverEffect(button, backgroundColor);

        // 添加动作监听器
        button.addActionListener(action::accept);

        return button;
    }

    private void setupButtonHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent ev) {
                if (button.isEnabled()) {
                    button.setBackground(darkenColor(originalColor, 0.1f));
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent ev) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor);
                }
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private Color darkenColor(Color color, float factor) {
        return new Color(
            Math.max(0, (int) (color.getRed() * (1 - factor))),
            Math.max(0, (int) (color.getGreen() * (1 - factor))),
            Math.max(0, (int) (color.getBlue() * (1 - factor))),
            color.getAlpha()
        );
    }

    private void clearFields(ActionEvent e) {
        loanAmountField.setText("");
        interestRateField.setText("");
        loanTermField.setText("");
        monthlyPaymentLabel.setText(Texts.MONTHLY_PAYMENT_LABEL + "0.00");
        totalPaymentLabel.setText(Texts.TOTAL_PAYMENT_LABEL + "0.00");
        totalInterestLabel.setText(Texts.TOTAL_INTEREST_LABEL + "0.00");
        amortizationArea.setText("");
    }

    private void calculateLoan(ActionEvent e) {
        try {
            double loanAmount = Double.parseDouble(loanAmountField.getText());
            double annualRate = Double.parseDouble(interestRateField.getText());
            double loanTerm = Double.parseDouble(loanTermField.getText());

            if (loanAmount <= 0 || annualRate < 0 || loanTerm <= 0) {
                JOptionPane.showMessageDialog(LoanCalculator.this,
                    Texts.ERROR_INVALID_INPUT, Texts.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 转换期限到月份
            int totalMonths;
            if (termTypeCombo.getSelectedIndex() == 0) { // 年
                totalMonths = (int) (loanTerm * 12);
            } else { // 月
                totalMonths = (int) loanTerm;
            }

            // 根据还款频率调整
            int paymentsPerYear;
            String frequency = (String) paymentFrequencyCombo.getSelectedItem();
            switch (frequency) {
                case Texts.FREQUENCY_MONTHLY:
                    paymentsPerYear = 12;
                    break;
                case Texts.FREQUENCY_QUARTERLY:
                    paymentsPerYear = 4;
                    totalMonths = totalMonths / 3;
                    break;
                case Texts.FREQUENCY_YEARLY:
                    paymentsPerYear = 1;
                    totalMonths = totalMonths / 12;
                    break;
                default:
                    paymentsPerYear = 12;
            }

            // 计算月利率
            double monthlyRate = (annualRate / 100) / paymentsPerYear;

            // 计算每期还款额
            double payment;
            if (monthlyRate == 0) {
                payment = loanAmount / totalMonths;
            } else {
                payment = loanAmount * monthlyRate * Math.pow(1 + monthlyRate, totalMonths) /
                         (Math.pow(1 + monthlyRate, totalMonths) - 1);
            }

            double totalPayment = payment * totalMonths;
            double totalInterest = totalPayment - loanAmount;

            // 更新显示
            monthlyPaymentLabel.setText(Texts.MONTHLY_PAYMENT_LABEL + currencyFormat.format(payment));
            totalPaymentLabel.setText(Texts.TOTAL_PAYMENT_LABEL + currencyFormat.format(totalPayment));
            totalInterestLabel.setText(Texts.TOTAL_INTEREST_LABEL + currencyFormat.format(totalInterest));

            // 生成还款明细
            generateAmortizationSchedule(loanAmount, monthlyRate, payment, totalMonths, frequency);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(LoanCalculator.this,
                Texts.ERROR_INVALID_NUMBER, Texts.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateAmortizationSchedule(double principal, double rate, double payment,
                                            int totalPayments, String frequency) {
        var sb = new StringBuilder();
        sb.append(String.format("%-10s%-15s%-15s%-15s%-15s%n",
            Texts.TABLE_HEADER_NUMBER, Texts.TABLE_HEADER_PAYMENT, Texts.TABLE_HEADER_PRINCIPAL,
            Texts.TABLE_HEADER_INTEREST, Texts.TABLE_HEADER_REMAINING));
        sb.append("-".repeat(70)).append("\n");

        double remainingPrincipal = principal;

        for (int i = 1; i <= totalPayments; i++) {
            double interestPayment = remainingPrincipal * rate;
            double principalPayment = payment - interestPayment;

            if (principalPayment > remainingPrincipal) {
                principalPayment = remainingPrincipal;
            }

            remainingPrincipal -= principalPayment;

            sb.append(String.format("%-10d%-15.2f%-15.2f%-15.2f%-15.2f%n",
                i, payment, principalPayment, interestPayment, remainingPrincipal));

            if (remainingPrincipal <= 0) break;
        }

        amortizationArea.setText(sb.toString());
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_ENTER:
                        // 回车键计算
                        calculateLoan(new ActionEvent(LoanCalculator.this, ActionEvent.ACTION_PERFORMED, "calculate"));
                        break;
                    case KeyEvent.VK_C:
                        // C键清空（如果按下Ctrl+C则让系统处理复制）
                        if (ev.isControlDown()) {
                            // 让系统处理复制
                            return;
                        } else {
                            clearFields(new ActionEvent(LoanCalculator.this, ActionEvent.ACTION_PERFORMED, "clear"));
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        // ESC键清空
                        clearFields(new ActionEvent(LoanCalculator.this, ActionEvent.ACTION_PERFORMED, "clear"));
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
}