import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;

/**
 * 单文件自包含通讯录应用
 * 功能：添加、编辑、删除、搜索联系人，支持数据持久化
 */
public class AddressBook extends JFrame {
    private DefaultListModel<Contact> listModel;
    private JList<Contact> contactList;
    private List<Contact> contacts;
    private JTextField searchField;
    private static final String DATA_FILE = "contacts.dat";

    // 主入口点
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AddressBook().setVisible(true);
            } catch (Exception e) {
                System.err.println("启动通讯录应用失败: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public AddressBook() {
        super("通讯录管理器");
        contacts = new ArrayList<>();
        loadContacts(); // 启动时加载数据
        initializeGUI();

        // 演示模式：5秒后自动关闭
        startDemoTimer();
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // 居中显示

        createMenuBar();
        createMainPanel();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setMnemonic('F');

        JMenuItem newItem = new JMenuItem("新建", 'N');
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> showAddContactDialog());

        JMenuItem saveItem = new JMenuItem("保存", 'S');
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveContacts());

        JMenuItem exitItem = new JMenuItem("退出", 'X');
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 编辑菜单
        JMenu editMenu = new JMenu("编辑");
        editMenu.setMnemonic('E');

        JMenuItem addItem = new JMenuItem("添加联系人", 'A');
        addItem.addActionListener(e -> showAddContactDialog());

        JMenuItem editItem = new JMenuItem("编辑联系人", 'E');
        editItem.addActionListener(e -> editSelectedContact());

        JMenuItem deleteItem = new JMenuItem("删除联系人", 'D');
        deleteItem.addActionListener(e -> deleteSelectedContact());

        editMenu.add(addItem);
        editMenu.add(editItem);
        editMenu.add(deleteItem);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setMnemonic('H');

        JMenuItem aboutItem = new JMenuItem("关于", 'A');
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 搜索面板
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("搜索: "), BorderLayout.WEST);

        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterContacts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterContacts(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterContacts(); }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JButton addButton = new JButton("添加");
        addButton.addActionListener(e -> showAddContactDialog());

        JButton editButton = new JButton("编辑");
        editButton.addActionListener(e -> editSelectedContact());

        JButton deleteButton = new JButton("删除");
        deleteButton.addActionListener(e -> deleteSelectedContact());

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshContactList());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 联系人列表
        listModel = new DefaultListModel<>();
        contactList = new JList<>(listModel);
        contactList.setCellRenderer(new ContactListRenderer());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 双击编辑
        contactList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedContact();
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(contactList);
        listScrollPane.setPreferredSize(new Dimension(400, 400));
        mainPanel.add(listScrollPane, BorderLayout.CENTER);

        // 详情面板
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("联系人详情"));

        JTextArea detailArea = new JTextArea(10, 30);
        detailArea.setEditable(false);
        detailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // 列表选择监听器
        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Contact selected = contactList.getSelectedValue();
                if (selected != null) {
                    detailArea.setText(selected.getDetails());
                } else {
                    detailArea.setText("");
                }
            }
        });

        detailPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        mainPanel.add(detailPanel, BorderLayout.EAST);

        add(mainPanel);

        // 初始化列表
        refreshContactList();
    }

    private void showAddContactDialog() {
        ContactDialog dialog = new ContactDialog(this, "添加联系人", null);
        dialog.setVisible(true);

        Contact newContact = dialog.getContact();
        if (newContact != null) {
            contacts.add(newContact);
            refreshContactList();
            saveContacts();
            JOptionPane.showMessageDialog(this, "联系人添加成功！");
        }
    }

    private void editSelectedContact() {
        Contact selected = contactList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的联系人！", "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ContactDialog dialog = new ContactDialog(this, "编辑联系人", selected);
        dialog.setVisible(true);

        Contact updatedContact = dialog.getContact();
        if (updatedContact != null) {
            int index = contacts.indexOf(selected);
            contacts.set(index, updatedContact);
            refreshContactList();
            saveContacts();
            JOptionPane.showMessageDialog(this, "联系人更新成功！");
        }
    }

    private void deleteSelectedContact() {
        Contact selected = contactList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的联系人！", "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
            "确定要删除联系人 '" + selected.getName() + "' 吗？",
            "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            contacts.remove(selected);
            refreshContactList();
            saveContacts();
            JOptionPane.showMessageDialog(this, "联系人删除成功！");
        }
    }

    private void filterContacts() {
        String searchText = searchField.getText().toLowerCase().trim();
        listModel.clear();

        for (Contact contact : contacts) {
            if (searchText.isEmpty() || contact.matches(searchText)) {
                listModel.addElement(contact);
            }
        }
    }

    private void refreshContactList() {
        listModel.clear();
        for (Contact contact : contacts) {
            listModel.addElement(contact);
        }
    }

    private void saveContacts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(contacts);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存数据失败: " + e.getMessage(),
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadContacts() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                contacts = (List<Contact>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                contacts = new ArrayList<>();
                JOptionPane.showMessageDialog(this, "加载数据失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "通讯录管理器 v2.0\n\n" +
            "功能特点：\n" +
            "• 联系人增删改查\n" +
            "• 实时搜索过滤\n" +
            "• 数据持久化存储\n" +
            "• 现代化Swing界面\n\n" +
            "演示模式：5秒后自动关闭",
            "关于", JOptionPane.INFORMATION_MESSAGE);
    }

    private void startDemoTimer() {
        javax.swing.Timer timer = new javax.swing.Timer(5000, e -> {
            JOptionPane.showMessageDialog(this, "演示结束，应用即将关闭！", "提示",
                JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
        timer.setRepeats(false);
        timer.start();
    }

    // 内部类：联系人
    public static class Contact implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private String phone;
        private String email;
        private String address;
        private String notes;

        public Contact(String name, String phone, String email, String address, String notes) {
            this.name = name != null ? name.trim() : "";
            this.phone = phone != null ? phone.trim() : "";
            this.email = email != null ? email.trim() : "";
            this.address = address != null ? address.trim() : "";
            this.notes = notes != null ? notes.trim() : "";
        }

        // Getters
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
        public String getNotes() { return notes; }

        public boolean matches(String keyword) {
            if (keyword == null || keyword.trim().isEmpty()) return true;
            String lowerKeyword = keyword.toLowerCase();
            return name.toLowerCase().contains(lowerKeyword) ||
                   phone.toLowerCase().contains(lowerKeyword) ||
                   email.toLowerCase().contains(lowerKeyword) ||
                   address.toLowerCase().contains(lowerKeyword) ||
                   notes.toLowerCase().contains(lowerKeyword);
        }

        public String getSummary() {
            StringBuilder sb = new StringBuilder(name);
            if (!phone.isEmpty()) sb.append(" (").append(phone).append(")");
            return sb.toString();
        }

        public String getDetails() {
            StringBuilder sb = new StringBuilder();
            sb.append("姓名: ").append(name).append("\n");
            if (!phone.isEmpty()) sb.append("电话: ").append(phone).append("\n");
            if (!email.isEmpty()) sb.append("邮箱: ").append(email).append("\n");
            if (!address.isEmpty()) sb.append("地址: ").append(address).append("\n");
            if (!notes.isEmpty()) sb.append("备注: ").append(notes).append("\n");
            return sb.toString().trim();
        }

        @Override
        public String toString() {
            return getSummary();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Contact contact = (Contact) obj;
            return Objects.equals(name, contact.name) &&
                   Objects.equals(phone, contact.phone) &&
                   Objects.equals(email, contact.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, phone, email);
        }
    }

    // 内部类：联系人列表渲染器
    private static class ContactListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Contact) {
                Contact contact = (Contact) value;
                setText(contact.getSummary());
                setToolTipText(contact.getDetails());
            }

            return this;
        }
    }

    // 内部类：联系人对话框
    private static class ContactDialog extends JDialog {
        private Contact contact;
        private JTextField nameField, phoneField, emailField;
        private JTextArea addressArea, notesArea;
        private boolean confirmed = false;

        public ContactDialog(JFrame parent, String title, Contact contact) {
            super(parent, title, true);
            this.contact = contact;

            setSize(400, 500);
            setLocationRelativeTo(parent);

            initializeComponents();

            if (contact != null) {
                loadContactData();
            }
        }

        private void initializeComponents() {
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // 输入面板
            JPanel inputPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // 姓名
            gbc.gridx = 0; gbc.gridy = 0;
            inputPanel.add(new JLabel("姓名:*"), gbc);
            gbc.gridx = 1;
            nameField = new JTextField(20);
            inputPanel.add(nameField, gbc);

            // 电话
            gbc.gridx = 0; gbc.gridy = 1;
            inputPanel.add(new JLabel("电话:"), gbc);
            gbc.gridx = 1;
            phoneField = new JTextField(20);
            inputPanel.add(phoneField, gbc);

            // 邮箱
            gbc.gridx = 0; gbc.gridy = 2;
            inputPanel.add(new JLabel("邮箱:"), gbc);
            gbc.gridx = 1;
            emailField = new JTextField(20);
            inputPanel.add(emailField, gbc);

            // 地址
            gbc.gridx = 0; gbc.gridy = 3;
            inputPanel.add(new JLabel("地址:"), gbc);
            gbc.gridx = 1;
            addressArea = new JTextArea(3, 20);
            addressArea.setLineWrap(true);
            addressArea.setWrapStyleWord(true);
            JScrollPane addressScroll = new JScrollPane(addressArea);
            inputPanel.add(addressScroll, gbc);

            // 备注
            gbc.gridx = 0; gbc.gridy = 4;
            inputPanel.add(new JLabel("备注:"), gbc);
            gbc.gridx = 1;
            notesArea = new JTextArea(3, 20);
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);
            JScrollPane notesScroll = new JScrollPane(notesArea);
            inputPanel.add(notesScroll, gbc);

            mainPanel.add(inputPanel, BorderLayout.CENTER);

            // 按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            JButton okButton = new JButton("确定");
            okButton.addActionListener(e -> {
                if (validateInput()) {
                    confirmed = true;
                    dispose();
                }
            });

            JButton cancelButton = new JButton("取消");
            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);

            // 设置默认按钮
            getRootPane().setDefaultButton(okButton);
        }

        private void loadContactData() {
            nameField.setText(contact.getName());
            phoneField.setText(contact.getPhone());
            emailField.setText(contact.getEmail());
            addressArea.setText(contact.getAddress());
            notesArea.setText(contact.getNotes());
        }

        private boolean validateInput() {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "姓名不能为空！", "错误",
                    JOptionPane.ERROR_MESSAGE);
                nameField.requestFocus();
                return false;
            }
            return true;
        }

        public Contact getContact() {
            if (!confirmed) return null;

            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressArea.getText().trim();
            String notes = notesArea.getText().trim();

            return new Contact(name, phone, email, address, notes);
        }
    }
}