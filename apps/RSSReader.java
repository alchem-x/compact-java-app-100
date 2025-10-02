import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * RSS阅读器
 * 简单的RSS订阅和阅读工具
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new RSSReader().setVisible(true);
    });
}

class RSSReader extends JFrame {
    private List<RSSFeed> feeds = new ArrayList<>();
    private List<RSSItem> allItems = new ArrayList<>();
    private List<RSSItem> filteredItems = new ArrayList<>();
    
    private JList<RSSFeed> feedList;
    private DefaultListModel<RSSFeed> feedListModel;
    private JTable itemTable;
    private DefaultTableModel itemTableModel;
    private JTextArea contentArea;
    private JTextField searchField;
    private JLabel statusLabel;
    
    public RSSReader() {
        initializeUI();
        loadSampleFeeds();
    }
    
    private void initializeUI() {
        setTitle("RSS阅读器 - RSS Reader");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 创建菜单栏
        createMenuBar();
        
        // 创建工具栏
        var toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);
        
        // 创建主面板
        var mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        
        // 创建状态栏
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void createMenuBar() {
        var menuBar = new JMenuBar();
        
        var feedMenu = new JMenu("订阅");
        addMenuItem(feedMenu, "添加订阅", e -> addFeed());
        addMenuItem(feedMenu, "删除订阅", e -> deleteFeed());
        addMenuItem(feedMenu, "刷新所有", e -> refreshAllFeeds());
        
        var viewMenu = new JMenu("视图");
        addMenuItem(viewMenu, "标记为已读", e -> markAsRead());
        addMenuItem(viewMenu, "标记为未读", e -> markAsUnread());
        addMenuItem(viewMenu, "清空搜索", e -> clearSearch());
        
        menuBar.add(feedMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void addMenuItem(JMenu menu, String text, ActionListener action) {
        var item = new JMenuItem(text);
        item.addActionListener(action);
        menu.add(item);
    }
    
    private JPanel createToolBar() {
        var panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        var addBtn = new JButton("添加订阅");
        var refreshBtn = new JButton("刷新");
        
        addBtn.addActionListener(e -> addFeed());
        refreshBtn.addActionListener(e -> refreshSelectedFeed());
        
        panel.add(addBtn);
        panel.add(refreshBtn);
        
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // 搜索框
        panel.add(new JLabel("搜索:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> filterItems());
        panel.add(searchField);
        
        var searchBtn = new JButton("搜索");
        searchBtn.addActionListener(e -> filterItems());
        panel.add(searchBtn);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
        var panel = new JPanel(new BorderLayout());
        
        // 创建分割面板
        var mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // 左侧订阅列表
        var feedPanel = createFeedPanel();
        mainSplitPane.setLeftComponent(feedPanel);
        
        // 右侧内容面板
        var contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // 文章列表
        var itemPanel = createItemPanel();
        contentSplitPane.setTopComponent(itemPanel);
        
        // 文章内容
        var articlePanel = createArticlePanel();
        contentSplitPane.setBottomComponent(articlePanel);
        
        contentSplitPane.setDividerLocation(300);
        mainSplitPane.setRightComponent(contentSplitPane);
        
        mainSplitPane.setDividerLocation(200);
        panel.add(mainSplitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFeedPanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("订阅源"));
        
        feedListModel = new DefaultListModel<>();
        feedList = new JList<>(feedListModel);
        feedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        feedList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadFeedItems();
            }
        });
        
        var scrollPane = new JScrollPane(feedList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createItemPanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("文章列表"));
        
        String[] columns = {"标题", "日期", "状态"};
        itemTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        itemTable = new JTable(itemTableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        
        itemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedItem();
            }
        });
        
        var scrollPane = new JScrollPane(itemTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createArticlePanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("文章内容"));
        
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        var scrollPane = new JScrollPane(contentArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadSampleFeeds() {
        // 添加示例订阅源
        var feed1 = new RSSFeed("技术新闻", "https://example.com/tech.xml");
        var feed2 = new RSSFeed("科学资讯", "https://example.com/science.xml");
        
        feeds.add(feed1);
        feeds.add(feed2);
        
        // 生成示例文章
        generateSampleItems(feed1);
        generateSampleItems(feed2);
        
        updateFeedList();
        
        if (!feeds.isEmpty()) {
            feedList.setSelectedIndex(0);
            loadFeedItems();
        }
    }
    
    private void generateSampleItems(RSSFeed feed) {
        var now = LocalDateTime.now();
        
        for (int i = 0; i < 5; i++) {
            var item = new RSSItem(
                feed.title + " - 文章 " + (i + 1),
                "这是来自 " + feed.title + " 的示例文章内容。文章编号: " + (i + 1) + 
                "\n\n这里是文章的详细内容，包含了相关的技术信息和新闻资讯。" +
                "文章发布时间: " + now.minusHours(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                "https://example.com/article" + i,
                now.minusHours(i),
                feed
            );
            
            feed.items.add(item);
            allItems.add(item);
        }
    }
    
    private void addFeed() {
        var dialog = new AddFeedDialog(this);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            var feed = new RSSFeed(dialog.getFeedTitle(), dialog.getFeedUrl());
            feeds.add(feed);
            
            // 生成示例内容
            generateSampleItems(feed);
            
            updateFeedList();
            statusLabel.setText("已添加订阅: " + feed.title);
        }
    }
    
    private void deleteFeed() {
        var selectedFeed = feedList.getSelectedValue();
        if (selectedFeed == null) {
            JOptionPane.showMessageDialog(this, "请选择要删除的订阅源");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定要删除订阅 \"" + selectedFeed.title + "\" 吗？",
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            feeds.remove(selectedFeed);
            allItems.removeIf(item -> item.feed == selectedFeed);
            updateFeedList();
            clearItemList();
            statusLabel.setText("已删除订阅: " + selectedFeed.title);
        }
    }
    
    private void refreshSelectedFeed() {
        var selectedFeed = feedList.getSelectedValue();
        if (selectedFeed != null) {
            // 模拟刷新
            statusLabel.setText("正在刷新: " + selectedFeed.title);
            
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(1000); // 模拟网络请求
                    return null;
                }
                
                @Override
                protected void done() {
                    statusLabel.setText("刷新完成: " + selectedFeed.title);
                    loadFeedItems();
                }
            };
            
            worker.execute();
        }
    }
    
    private void refreshAllFeeds() {
        statusLabel.setText("正在刷新所有订阅源...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(2000); // 模拟网络请求
                return null;
            }
            
            @Override
            protected void done() {
                statusLabel.setText("所有订阅源刷新完成");
                loadFeedItems();
            }
        };
        
        worker.execute();
    }
    
    private void loadFeedItems() {
        var selectedFeed = feedList.getSelectedValue();
        if (selectedFeed != null) {
            filteredItems.clear();
            filteredItems.addAll(selectedFeed.items);
            updateItemTable();
        }
    }
    
    private void filterItems() {
        String searchText = searchField.getText().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadFeedItems();
            return;
        }
        
        var selectedFeed = feedList.getSelectedValue();
        if (selectedFeed != null) {
            filteredItems.clear();
            
            for (var item : selectedFeed.items) {
                if (item.title.toLowerCase().contains(searchText) ||
                    item.content.toLowerCase().contains(searchText)) {
                    filteredItems.add(item);
                }
            }
            
            updateItemTable();
            statusLabel.setText("找到 " + filteredItems.size() + " 篇相关文章");
        }
    }
    
    private void clearSearch() {
        searchField.setText("");
        loadFeedItems();
    }
    
    private void showSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < filteredItems.size()) {
            var item = filteredItems.get(selectedRow);
            contentArea.setText(item.content);
            contentArea.setCaretPosition(0);
            
            // 标记为已读
            item.isRead = true;
            updateItemTable();
        }
    }
    
    private void markAsRead() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < filteredItems.size()) {
            filteredItems.get(selectedRow).isRead = true;
            updateItemTable();
        }
    }
    
    private void markAsUnread() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < filteredItems.size()) {
            filteredItems.get(selectedRow).isRead = false;
            updateItemTable();
        }
    }
    
    private void updateFeedList() {
        feedListModel.clear();
        for (var feed : feeds) {
            feedListModel.addElement(feed);
        }
    }
    
    private void updateItemTable() {
        itemTableModel.setRowCount(0);
        
        for (var item : filteredItems) {
            Object[] row = {
                item.title,
                item.publishDate.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                item.isRead ? "已读" : "未读"
            };
            itemTableModel.addRow(row);
        }
    }
    
    private void clearItemList() {
        itemTableModel.setRowCount(0);
        contentArea.setText("");
    }
    
    static class RSSFeed {
        String title;
        String url;
        List<RSSItem> items = new ArrayList<>();
        
        RSSFeed(String title, String url) {
            this.title = title;
            this.url = url;
        }
        
        @Override
        public String toString() {
            int unreadCount = (int) items.stream().filter(item -> !item.isRead).count();
            return title + (unreadCount > 0 ? " (" + unreadCount + ")" : "");
        }
    }
    
    static class RSSItem {
        String title;
        String content;
        String link;
        LocalDateTime publishDate;
        RSSFeed feed;
        boolean isRead = false;
        
        RSSItem(String title, String content, String link, LocalDateTime publishDate, RSSFeed feed) {
            this.title = title;
            this.content = content;
            this.link = link;
            this.publishDate = publishDate;
            this.feed = feed;
        }
    }
    
    class AddFeedDialog extends JDialog {
        private JTextField titleField;
        private JTextField urlField;
        private boolean confirmed = false;
        
        AddFeedDialog(Frame parent) {
            super(parent, "添加RSS订阅", true);
            initializeDialog();
        }
        
        private void initializeDialog() {
            setSize(400, 200);
            setLocationRelativeTo(getParent());
            
            var panel = new JPanel(new GridBagLayout());
            var gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // 标题
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("标题:"), gbc);
            gbc.gridx = 1;
            titleField = new JTextField(20);
            panel.add(titleField, gbc);
            
            // URL
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("URL:"), gbc);
            gbc.gridx = 1;
            urlField = new JTextField(20);
            panel.add(urlField, gbc);
            
            // 按钮
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            var buttonPanel = new JPanel();
            var okButton = new JButton("确定");
            var cancelButton = new JButton("取消");
            
            okButton.addActionListener(e -> {
                if (validateInput()) {
                    confirmed = true;
                    dispose();
                }
            });
            
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            panel.add(buttonPanel, gbc);
            
            add(panel);
        }
        
        private boolean validateInput() {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入订阅标题");
                return false;
            }
            if (urlField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入RSS URL");
                return false;
            }
            return true;
        }
        
        boolean isConfirmed() {
            return confirmed;
        }
        
        String getFeedTitle() {
            return titleField.getText().trim();
        }
        
        String getFeedUrl() {
            return urlField.getText().trim();
        }
    }
}
