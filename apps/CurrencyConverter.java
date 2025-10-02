import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new CurrencyConverter().setVisible(true);
    });
}

static class CurrencyConverter extends JFrame {
    private JTextField amountField;
    private JComboBox<String> fromCurrencyCombo;
    private JComboBox<String> toCurrencyCombo;
    private JLabel resultLabel;
    private JLabel exchangeRateLabel;
    private JTextArea historyArea;
    private JButton convertButton;
    private JButton swapButton;
    private JButton clearButton;
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private DecimalFormat rateFormat = new DecimalFormat("0.0000");

    // 模拟汇率数据 (相对于USD)
    private Map<String, Double> exchangeRates;
    private String[] currencyCodes = {
        "USD", "CNY", "EUR", "GBP", "JPY", "KRW", "HKD", "AUD", "CAD", "SGD",
        "CHF", "SEK", "NOK", "NZD", "INR", "THB", "MYR", "PHP", "RUB", "BRL"
    };

    private String[] currencyNames = {
        "美元 (USD)", "人民币 (CNY)", "欧元 (EUR)", "英镑 (GBP)", "日元 (JPY)",
        "韩元 (KRW)", "港币 (HKD)", "澳元 (AUD)", "加元 (CAD)", "新加坡元 (SGD)",
        "瑞士法郎 (CHF)", "瑞典克朗 (SEK)", "挪威克朗 (NOK)", "新西兰元 (NZD)",
        "印度卢比 (INR)", "泰铢 (THB)", "马来西亚林吉特 (MYR)", "菲律宾比索 (PHP)",
        "俄罗斯卢布 (RUB)", "巴西雷亚尔 (BRL)"
    };

    public CurrencyConverter() {
        initializeExchangeRates();
        initializeGUI();
    }

    private void initializeExchangeRates() {
        exchangeRates = new HashMap<>();
        // 基于USD的汇率 (模拟数据，实际应用中应该调用API获取实时汇率)
        exchangeRates.put("USD", 1.0);
        exchangeRates.put("CNY", 7.2);
        exchangeRates.put("EUR", 0.85);
        exchangeRates.put("GBP", 0.73);
        exchangeRates.put("JPY", 110.0);
        exchangeRates.put("KRW", 1180.0);
        exchangeRates.put("HKD", 7.8);
        exchangeRates.put("AUD", 1.35);
        exchangeRates.put("CAD", 1.25);
        exchangeRates.put("SGD", 1.35);
        exchangeRates.put("CHF", 0.92);
        exchangeRates.put("SEK", 8.5);
        exchangeRates.put("NOK", 8.6);
        exchangeRates.put("NZD", 1.42);
        exchangeRates.put("INR", 74.5);
        exchangeRates.put("THB", 33.2);
        exchangeRates.put("MYR", 4.15);
        exchangeRates.put("PHP", 50.5);
        exchangeRates.put("RUB", 74.0);
        exchangeRates.put("BRL", 5.2);
    }

    private void initializeGUI() {
        setTitle("汇率转换器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 标题
        JLabel titleLabel = new JLabel("💱 汇率转换器");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // 金额输入
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("转换金额:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountField = new JTextField(15);
        amountField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        mainPanel.add(amountField, gbc);

        // 源货币
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("从:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fromCurrencyCombo = new JComboBox<>(currencyNames);
        fromCurrencyCombo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        fromCurrencyCombo.setSelectedIndex(1); // 默认人民币
        mainPanel.add(fromCurrencyCombo, gbc);

        // 交换按钮
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        swapButton = new JButton("⇄");
        swapButton.setFont(new Font("Arial", Font.BOLD, 20));
        swapButton.setToolTipText("交换货币");
        swapButton.addActionListener(e -> swapCurrencies());
        mainPanel.add(swapButton, gbc);

        // 目标货币
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("到:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        toCurrencyCombo = new JComboBox<>(currencyNames);
        toCurrencyCombo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        toCurrencyCombo.setSelectedIndex(0); // 默认美元
        mainPanel.add(toCurrencyCombo, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        convertButton = new JButton("🔄 转换");
        convertButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        convertButton.setBackground(new Color(76, 175, 80));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);
        convertButton.addActionListener(new ConvertListener());

        clearButton = new JButton("🗑️ 清空");
        clearButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        clearButton.setBackground(new Color(244, 67, 54));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearFields());

        buttonPanel.add(convertButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        // 结果显示区域
        JPanel resultPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        resultPanel.setBorder(BorderFactory.createTitledBorder("转换结果"));

        resultLabel = new JLabel("转换结果: 0.00");
        resultLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        resultLabel.setForeground(new Color(76, 175, 80));

        exchangeRateLabel = new JLabel("当前汇率: 0.0000");
        exchangeRateLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        resultPanel.add(resultLabel);
        resultPanel.add(exchangeRateLabel);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(resultPanel, gbc);

        // 转换历史
        historyArea = new JTextArea(8, 30);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("转换历史"));

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(scrollPane, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 设置窗口
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void clearFields() {
        amountField.setText("");
        resultLabel.setText("转换结果: 0.00");
        exchangeRateLabel.setText("当前汇率: 0.0000");
    }

    private void swapCurrencies() {
        int fromIndex = fromCurrencyCombo.getSelectedIndex();
        int toIndex = toCurrencyCombo.getSelectedIndex();
        fromCurrencyCombo.setSelectedIndex(toIndex);
        toCurrencyCombo.setSelectedIndex(fromIndex);
    }

    private class ConvertListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(CurrencyConverter.this,
                        "请输入转换金额！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(CurrencyConverter.this,
                        "请输入有效的金额！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String fromCurrency = getCurrencyCode((String) fromCurrencyCombo.getSelectedItem());
                String toCurrency = getCurrencyCode((String) toCurrencyCombo.getSelectedItem());

                double fromRate = exchangeRates.get(fromCurrency);
                double toRate = exchangeRates.get(toCurrency);

                // 计算转换结果
                double result = (amount / fromRate) * toRate;
                double exchangeRate = toRate / fromRate;

                // 更新显示
                resultLabel.setText(String.format("转换结果: %s", currencyFormat.format(result)));
                exchangeRateLabel.setText(String.format("当前汇率: %s", rateFormat.format(exchangeRate)));

                // 添加到历史记录
                String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
                String historyEntry = String.format("[%s] %s %s = %s %s (汇率: %s)\n",
                    timestamp,
                    currencyFormat.format(amount), fromCurrency,
                    currencyFormat.format(result), toCurrency,
                    rateFormat.format(exchangeRate));

                historyArea.insert(historyEntry, 0);
                if (historyArea.getLineCount() > 50) {
                    historyArea.replaceRange("", historyArea.getText().indexOf('\n') + 1, historyArea.getText().length());
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CurrencyConverter.this,
                    "请输入有效的数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        private String getCurrencyCode(String currencyName) {
            for (int i = 0; i < currencyNames.length; i++) {
                if (currencyNames[i].equals(currencyName)) {
                    return currencyCodes[i];
                }
            }
            return "CNY"; // 默认返回人民币
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CurrencyConverter().setVisible(true);
        });
    }
}