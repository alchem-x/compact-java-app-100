import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new TaxCalculator().setVisible(true);
    });
}

static class TaxCalculator extends JFrame {
    private JTextField incomeField;
    private JComboBox<String> incomeTypeCombo;
    private JComboBox<String> locationCombo;
    private JTextField insuranceField;
    private JTextField specialDeductionField;
    private JLabel taxLabel;
    private JLabel afterTaxIncomeLabel;
    private JLabel taxRateLabel;
    private JTextArea calculationDetailsArea;
    private JButton calculateButton;
    private JButton clearButton;
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private DecimalFormat percentFormat = new DecimalFormat("0.00%");

    // 个人所得税起征点
    private static final double TAX_THRESHOLD = 5000;

    // 个人所得税税率表（综合所得）
    private static final double[] TAX_BRACKETS = {
        36000, 144000, 300000, 420000, 660000, 960000, Double.MAX_VALUE
    };

    private static final double[] TAX_RATES = {
        0.03, 0.10, 0.20, 0.25, 0.30, 0.35, 0.45
    };

    private static final double[] QUICK_DEDUCTIONS = {
        0, 2520, 16920, 31920, 52920, 85920, 181920
    };

    public TaxCalculator() {
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("个人所得税计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 标题
        JLabel titleLabel = new JLabel("💰 个人所得税计算器");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // 收入类型
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("收入类型:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        incomeTypeCombo = new JComboBox<>(new String[]{"工资薪金所得", "劳务报酬所得", "稿酬所得", "特许权使用费所得"});
        incomeTypeCombo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        incomeTypeCombo.addActionListener(e -> updateCalculationMode());
        mainPanel.add(incomeTypeCombo, gbc);

        // 收入金额
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("收入金额:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        incomeField = new JTextField(15);
        incomeField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        mainPanel.add(incomeField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("元/月"), gbc);

        // 社保公积金
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("社保公积金:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        insuranceField = new JTextField(15);
        insuranceField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        insuranceField.setText("0");
        mainPanel.add(insuranceField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("元/月"), gbc);

        // 专项附加扣除
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("专项附加扣除:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        specialDeductionField = new JTextField(15);
        specialDeductionField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        specialDeductionField.setText("0");
        mainPanel.add(specialDeductionField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("元/月"), gbc);

        // 地区选择
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("所在地区:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        locationCombo = new JComboBox<>(new String[]{"北京", "上海", "广州", "深圳", "其他"});
        locationCombo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        mainPanel.add(locationCombo, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        calculateButton = new JButton("🧮 计算个税");
        calculateButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        calculateButton.setBackground(new Color(76, 175, 80));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        calculateButton.addActionListener(new CalculateListener());

        clearButton = new JButton("🗑️ 清空");
        clearButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        clearButton.setBackground(new Color(244, 67, 54));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearFields());

        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        // 结果显示区域
        JPanel resultPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        resultPanel.setBorder(BorderFactory.createTitledBorder("计算结果"));

        taxLabel = new JLabel("应缴个税: ¥0.00/月");
        taxLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        taxLabel.setForeground(new Color(244, 67, 54));

        afterTaxIncomeLabel = new JLabel("税后收入: ¥0.00/月");
        afterTaxIncomeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        afterTaxIncomeLabel.setForeground(new Color(76, 175, 80));

        taxRateLabel = new JLabel("实际税率: 0.00%");
        taxRateLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        resultPanel.add(taxLabel);
        resultPanel.add(afterTaxIncomeLabel);
        resultPanel.add(taxRateLabel);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(resultPanel, gbc);

        // 计算详情
        calculationDetailsArea = new JTextArea(8, 30);
        calculationDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        calculationDetailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(calculationDetailsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("计算详情"));

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 3;
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

    private void updateCalculationMode() {
        String incomeType = (String) incomeTypeCombo.getSelectedItem();
        boolean isSalary = "工资薪金所得".equals(incomeType);

        insuranceField.setEnabled(isSalary);
        specialDeductionField.setEnabled(isSalary);
        locationCombo.setEnabled(isSalary);

        if (!isSalary) {
            insuranceField.setText("0");
            specialDeductionField.setText("0");
        }
    }

    private void clearFields() {
        incomeField.setText("");
        insuranceField.setText("0");
        specialDeductionField.setText("0");
        taxLabel.setText("应缴个税: ¥0.00/月");
        afterTaxIncomeLabel.setText("税后收入: ¥0.00/月");
        taxRateLabel.setText("实际税率: 0.00%");
        calculationDetailsArea.setText("");
    }

    private class CalculateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double income = Double.parseDouble(incomeField.getText());
                double insurance = Double.parseDouble(insuranceField.getText());
                double specialDeduction = Double.parseDouble(specialDeductionField.getText());

                if (income < 0 || insurance < 0 || specialDeduction < 0) {
                    JOptionPane.showMessageDialog(TaxCalculator.this,
                        "请输入有效的数值！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String incomeType = (String) incomeTypeCombo.getSelectedItem();
                double tax = 0;
                double afterTaxIncome = 0;
                String calculationDetails = "";

                switch (incomeType) {
                    case "工资薪金所得":
                        calculationDetails = calculateSalaryTax(income, insurance, specialDeduction);
                        String[] results = calculationDetails.split("\\|");
                        tax = Double.parseDouble(results[0]);
                        afterTaxIncome = Double.parseDouble(results[1]);
                        calculationDetails = results[2];
                        break;
                    case "劳务报酬所得":
                        calculationDetails = calculateLaborTax(income);
                        results = calculationDetails.split("\\|");
                        tax = Double.parseDouble(results[0]);
                        afterTaxIncome = Double.parseDouble(results[1]);
                        calculationDetails = results[2];
                        break;
                    case "稿酬所得":
                        calculationDetails = calculateRoyaltyTax(income);
                        results = calculationDetails.split("\\|");
                        tax = Double.parseDouble(results[0]);
                        afterTaxIncome = Double.parseDouble(results[1]);
                        calculationDetails = results[2];
                        break;
                    case "特许权使用费所得":
                        calculationDetails = calculateLicenseTax(income);
                        results = calculationDetails.split("\\|");
                        tax = Double.parseDouble(results[0]);
                        afterTaxIncome = Double.parseDouble(results[1]);
                        calculationDetails = results[2];
                        break;
                }

                double taxRate = (tax / income) * 100;

                // 更新显示
                taxLabel.setText(String.format("应缴个税: ¥%,.2f/月", tax));
                afterTaxIncomeLabel.setText(String.format("税后收入: ¥%,.2f/月", afterTaxIncome));
                taxRateLabel.setText(String.format("实际税率: %.2f%%", taxRate));
                calculationDetailsArea.setText(calculationDetails);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(TaxCalculator.this,
                    "请输入有效的数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        private String calculateSalaryTax(double income, double insurance, double specialDeduction) {
            StringBuilder details = new StringBuilder();

            // 计算应纳税所得额
            double taxableIncome = income - insurance - specialDeduction - TAX_THRESHOLD;

            details.append("计算步骤:\n");
            details.append(String.format("1. 月收入: ¥%,.2f\n", income));
            details.append(String.format("2. 社保公积金: ¥%,.2f\n", insurance));
            details.append(String.format("3. 专项附加扣除: ¥%,.2f\n", specialDeduction));
            details.append(String.format("4. 起征点: ¥%,.2f\n", TAX_THRESHOLD));
            details.append(String.format("5. 应纳税所得额: ¥%,.2f\n", taxableIncome));

            if (taxableIncome <= 0) {
                details.append("6. 应缴个税: ¥0.00 (应纳税所得额小于等于0)\n");
                return "0|" + income + "|" + details.toString();
            }

            // 计算年度应纳税所得额
            double annualTaxableIncome = taxableIncome * 12;

            // 查找适用税率
            int bracketIndex = 0;
            for (int i = 0; i < TAX_BRACKETS.length; i++) {
                if (annualTaxableIncome <= TAX_BRACKETS[i]) {
                    bracketIndex = i;
                    break;
                }
            }

            double taxRate = TAX_RATES[bracketIndex];
            double quickDeduction = QUICK_DEDUCTIONS[bracketIndex];

            // 计算年度税额
            double annualTax = annualTaxableIncome * taxRate - quickDeduction;
            double monthlyTax = annualTax / 12;
            double afterTaxIncome = income - insurance - monthlyTax;

            details.append(String.format("6. 年度应纳税所得额: ¥%,.2f\n", annualTaxableIncome));
            details.append(String.format("7. 适用税率: %.0f%%\n", taxRate * 100));
            details.append(String.format("8. 速算扣除数: ¥%,.2f\n", quickDeduction));
            details.append(String.format("9. 年度税额: ¥%,.2f\n", annualTax));
            details.append(String.format("10. 月度税额: ¥%,.2f\n", monthlyTax));

            return monthlyTax + "|" + afterTaxIncome + "|" + details.toString();
        }

        private String calculateLaborTax(double income) {
            StringBuilder details = new StringBuilder();

            details.append("劳务报酬所得税计算:\n");
            details.append(String.format("1. 收入: ¥%,.2f\n", income));

            double taxableIncome;
            if (income <= 4000) {
                taxableIncome = income - 800;
                details.append("2. 减除费用: ¥800\n");
            } else {
                taxableIncome = income * 0.8;
                details.append("2. 减除费用: 20% (¥" + currencyFormat.format(income * 0.2) + ")\n");
            }

            details.append(String.format("3. 应纳税所得额: ¥%,.2f\n", taxableIncome));

            // 劳务报酬所得适用税率
            double taxRate = 0.20;
            double tax = taxableIncome * taxRate;
            double afterTaxIncome = income - tax;

            details.append(String.format("4. 适用税率: 20%%\n"));
            details.append(String.format("5. 应缴税额: ¥%,.2f\n", tax));

            return tax + "|" + afterTaxIncome + "|" + details.toString();
        }

        private String calculateRoyaltyTax(double income) {
            StringBuilder details = new StringBuilder();

            details.append("稿酬所得税计算:\n");
            details.append(String.format("1. 收入: ¥%,.2f\n", income));

            double taxableIncome;
            if (income <= 4000) {
                taxableIncome = (income - 800) * 0.7;
                details.append("2. 减除费用: ¥800\n");
                details.append("3. 稿酬所得减按70%计算\n");
            } else {
                taxableIncome = income * 0.8 * 0.7;
                details.append("2. 减除费用: 20% (¥" + currencyFormat.format(income * 0.2) + ")\n");
                details.append("3. 稿酬所得减按70%计算\n");
            }

            details.append(String.format("4. 应纳税所得额: ¥%,.2f\n", taxableIncome));

            double taxRate = 0.20;
            double tax = taxableIncome * taxRate;
            double afterTaxIncome = income - tax;

            details.append(String.format("5. 适用税率: 20%%\n"));
            details.append(String.format("6. 应缴税额: ¥%,.2f\n", tax));

            return tax + "|" + afterTaxIncome + "|" + details.toString();
        }

        private String calculateLicenseTax(double income) {
            StringBuilder details = new StringBuilder();

            details.append("特许权使用费所得税计算:\n");
            details.append(String.format("1. 收入: ¥%,.2f\n", income));

            double taxableIncome;
            if (income <= 4000) {
                taxableIncome = income - 800;
                details.append("2. 减除费用: ¥800\n");
            } else {
                taxableIncome = income * 0.8;
                details.append("2. 减除费用: 20% (¥" + currencyFormat.format(income * 0.2) + ")\n");
            }

            details.append(String.format("3. 应纳税所得额: ¥%,.2f\n", taxableIncome));

            double taxRate = 0.20;
            double tax = taxableIncome * taxRate;
            double afterTaxIncome = income - tax;

            details.append(String.format("4. 适用税率: 20%%\n"));
            details.append(String.format("5. 应缴税额: ¥%,.2f\n", tax));

            return tax + "|" + afterTaxIncome + "|" + details.toString();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TaxCalculator().setVisible(true);
        });
    }
}