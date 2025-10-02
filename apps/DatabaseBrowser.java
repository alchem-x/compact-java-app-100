import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new DatabaseBrowser().setVisible(true);
    });
}

static class DatabaseBrowser extends JFrame {
    private final JTextField urlField;
    private final JTextField userField;
    private final JPasswordField passwordField;
    private final JButton connectButton;
    private final JButton disconnectButton;
    private final JComboBox<String> tableCombo;
    private final JTextArea sqlArea;
    private final JButton executeButton;
    private final JTable resultTable;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;
    
    private Connection connection;
    
    public DatabaseBrowser() {
        urlField = new JTextField("jdbc:sqlite::memory:");
        userField = new JTextField("");
        passwordField = new JPasswordField("");
        connectButton = new JButton("连接");
        disconnectButton = new JButton("断开");
        tableCombo = new JComboBox<>();
        sqlArea = new JTextArea();
        executeButton = new JButton("执行SQL");
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        statusLabel = new JLabel("未连接");
        
        initializeGUI();
        setupEventHandlers();
        createSampleDatabase();
    }
    
    private void initializeGUI() {
        setTitle("🗄️ 数据库浏览器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 创建连接面板
        createConnectionPanel();
        
        // 创建主工作区
        createWorkArea();
        
        // 创建状态栏
        createStatusBar();
        
        setSize(900, 700);
        setLocationRelativeTo(null);
    }
    
    private void createConnectionPanel() {
        var connectionPanel = new JPanel();
        connectionPanel.setBorder(BorderFactory.createTitledBorder("数据库连接"));
        connectionPanel.setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // URL
        gbc.gridx = 0; gbc.gridy = 0;
        connectionPanel.add(new JLabel("URL:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        urlField.setPreferredSize(new Dimension(300, 25));
        connectionPanel.add(urlField, gbc);
        
        // 用户名
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        connectionPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        connectionPanel.add(userField, gbc);
        
        // 密码
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        connectionPanel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        connectionPanel.add(passwordField, gbc);
        
        // 按钮
        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        connectionPanel.add(connectButton, gbc);
        gbc.gridy = 1;
        disconnectButton.setEnabled(false);
        connectionPanel.add(disconnectButton, gbc);
        
        add(connectionPanel, BorderLayout.NORTH);
    }
    
    private void createWorkArea() {
        var workPanel = new JPanel(new BorderLayout());
        
        // 左侧表列表
        createTableListPanel(workPanel);
        
        // 右侧SQL编辑器和结果
        createSqlPanel(workPanel);
        
        add(workPanel, BorderLayout.CENTER);
    }
    
    private void createTableListPanel(JPanel parent) {
        var leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("数据表"));
        leftPanel.setPreferredSize(new Dimension(200, 0));
        
        tableCombo.setEnabled(false);
        tableCombo.addActionListener(e -> loadTableData());
        
        var buttonPanel = new JPanel(new FlowLayout());
        var refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadTables());
        buttonPanel.add(refreshButton);
        
        leftPanel.add(tableCombo, BorderLayout.NORTH);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        parent.add(leftPanel, BorderLayout.WEST);
    }
    
    private void createSqlPanel(JPanel parent) {
        var rightPanel = new JPanel(new BorderLayout());
        
        // SQL编辑器
        var sqlPanel = new JPanel(new BorderLayout());
        sqlPanel.setBorder(BorderFactory.createTitledBorder("SQL查询"));
        sqlPanel.setPreferredSize(new Dimension(0, 150));
        
        sqlArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        sqlArea.setText("SELECT * FROM users;");
        sqlArea.setEnabled(false);
        
        var sqlScrollPane = new JScrollPane(sqlArea);
        sqlPanel.add(sqlScrollPane, BorderLayout.CENTER);
        
        var sqlButtonPanel = new JPanel(new FlowLayout());
        executeButton.setEnabled(false);
        var clearButton = new JButton("清空");
        clearButton.addActionListener(e -> sqlArea.setText(""));
        
        sqlButtonPanel.add(executeButton);
        sqlButtonPanel.add(clearButton);
        sqlPanel.add(sqlButtonPanel, BorderLayout.SOUTH);
        
        // 结果表格
        var resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("查询结果"));
        
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        var resultScrollPane = new JScrollPane(resultTable);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);
        
        rightPanel.add(sqlPanel, BorderLayout.NORTH);
        rightPanel.add(resultPanel, BorderLayout.CENTER);
        
        parent.add(rightPanel, BorderLayout.CENTER);
    }
    
    private void createStatusBar() {
        var statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        connectButton.addActionListener(e -> connectToDatabase());
        disconnectButton.addActionListener(e -> disconnectFromDatabase());
        executeButton.addActionListener(e -> executeSQL());
    }
    
    private void createSampleDatabase() {
        try {
            // 创建内存SQLite数据库作为示例
            var url = "jdbc:sqlite::memory:";
            connection = DriverManager.getConnection(url);
            
            // 创建示例表和数据
            var stmt = connection.createStatement();
            
            // 用户表
            stmt.execute("""
                CREATE TABLE users (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE,
                    age INTEGER,
                    created_date TEXT
                )
            """);
            
            stmt.execute("""
                INSERT INTO users (name, email, age, created_date) VALUES
                ('张三', 'zhangsan@example.com', 25, '2024-01-15'),
                ('李四', 'lisi@example.com', 30, '2024-02-20'),
                ('王五', 'wangwu@example.com', 28, '2024-03-10'),
                ('赵六', 'zhaoliu@example.com', 35, '2024-04-05')
            """);
            
            // 产品表
            stmt.execute("""
                CREATE TABLE products (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    price REAL,
                    category TEXT,
                    stock INTEGER
                )
            """);
            
            stmt.execute("""
                INSERT INTO products (name, price, category, stock) VALUES
                ('笔记本电脑', 5999.99, '电子产品', 50),
                ('手机', 2999.00, '电子产品', 100),
                ('书桌', 899.50, '家具', 20),
                ('台灯', 199.99, '家具', 80)
            """);
            
            // 订单表
            stmt.execute("""
                CREATE TABLE orders (
                    id INTEGER PRIMARY KEY,
                    user_id INTEGER,
                    product_id INTEGER,
                    quantity INTEGER,
                    order_date TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (product_id) REFERENCES products(id)
                )
            """);
            
            stmt.execute("""
                INSERT INTO orders (user_id, product_id, quantity, order_date) VALUES
                (1, 1, 1, '2024-05-01'),
                (2, 2, 2, '2024-05-02'),
                (3, 3, 1, '2024-05-03'),
                (1, 4, 3, '2024-05-04')
            """);
            
            // 更新UI状态
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            tableCombo.setEnabled(true);
            sqlArea.setEnabled(true);
            executeButton.setEnabled(true);
            statusLabel.setText("已连接到示例数据库");
            
            loadTables();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "创建示例数据库失败: " + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void connectToDatabase() {
        var url = urlField.getText().trim();
        var user = userField.getText().trim();
        var password = new String(passwordField.getPassword());
        
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入数据库URL", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            
            if (user.isEmpty()) {
                connection = DriverManager.getConnection(url);
            } else {
                connection = DriverManager.getConnection(url, user, password);
            }
            
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            tableCombo.setEnabled(true);
            sqlArea.setEnabled(true);
            executeButton.setEnabled(true);
            statusLabel.setText("已连接到: " + url);
            
            loadTables();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "连接失败: " + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void disconnectFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            tableCombo.setEnabled(false);
            tableCombo.removeAllItems();
            sqlArea.setEnabled(false);
            executeButton.setEnabled(false);
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
            statusLabel.setText("未连接");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "断开连接失败: " + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTables() {
        if (connection == null) return;
        
        try {
            tableCombo.removeAllItems();
            var metaData = connection.getMetaData();
            var tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                var tableName = tables.getString("TABLE_NAME");
                tableCombo.addItem(tableName);
            }
            
            if (tableCombo.getItemCount() > 0) {
                tableCombo.setSelectedIndex(0);
                loadTableData();
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "加载表列表失败: " + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTableData() {
        var selectedTable = (String) tableCombo.getSelectedItem();
        if (selectedTable == null || connection == null) return;
        
        try {
            var sql = "SELECT * FROM " + selectedTable + " LIMIT 100";
            executeQuery(sql);
            sqlArea.setText(sql);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "加载表数据失败: " + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void executeSQL() {
        var sql = sqlArea.getText().trim();
        if (sql.isEmpty() || connection == null) return;
        
        try {
            if (sql.toUpperCase().startsWith("SELECT")) {
                executeQuery(sql);
            } else {
                executeUpdate(sql);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "SQL执行失败: " + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void executeQuery(String sql) throws SQLException {
        var stmt = connection.createStatement();
        var rs = stmt.executeQuery(sql);
        
        // 获取列信息
        var metaData = rs.getMetaData();
        var columnCount = metaData.getColumnCount();
        
        // 设置表格列
        var columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }
        
        // 获取数据
        var data = new ArrayList<Object[]>();
        while (rs.next()) {
            var row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = rs.getObject(i);
            }
            data.add(row);
        }
        
        // 更新表格
        tableModel.setDataVector(data.toArray(new Object[0][]), columnNames);
        
        statusLabel.setText("查询完成，返回 " + data.size() + " 行记录");
        
        rs.close();
        stmt.close();
    }
    
    private void executeUpdate(String sql) throws SQLException {
        var stmt = connection.createStatement();
        var affectedRows = stmt.executeUpdate(sql);
        
        statusLabel.setText("执行完成，影响 " + affectedRows + " 行记录");
        
        // 刷新表列表和数据
        loadTables();
        
        stmt.close();
    }
}
