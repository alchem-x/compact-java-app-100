import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

// 使用record定义文件信息
record FileInfo(
    String name,
    String path,
    long size,
    boolean isDirectory,
    long lastModified,
    boolean isHidden,
    String extension
) {
    static FileInfo from(File file) {
        var name = file.getName();
        var extension = name.contains(".") ? 
            name.substring(name.lastIndexOf(".") + 1).toLowerCase() : "";
        
        return new FileInfo(
            name,
            file.getAbsolutePath(),
            file.length(),
            file.isDirectory(),
            file.lastModified(),
            file.isHidden(),
            extension
        );
    }
    
    String formattedSize() {
        if (isDirectory) return "--";
        
        return switch ((int) (Math.log10(size) / 3)) {
            case 0 -> size + " B";
            case 1 -> String.format("%.1f KB", size / 1024.0);
            case 2 -> String.format("%.1f MB", size / (1024.0 * 1024));
            case 3 -> String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
            default -> String.format("%.1f TB", size / (1024.0 * 1024 * 1024 * 1024));
        };
    }
    
    String formattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(lastModified));
    }
    
    Icon getIcon() {
        var fsv = FileSystemView.getFileSystemView();
        return fsv.getSystemIcon(new File(path));
    }
}

// 文本管理静态内部类
static class Texts {
    // 窗口标题
    static final String WINDOW_TITLE = "📁 文件管理器";

    // 工具栏按钮
    static final String BACK_BUTTON = "⬅️";
    static final String UP_BUTTON = "⬆️";
    static final String HOME_BUTTON = "🏠";
    static final String REFRESH_BUTTON = "🔄";
    static final String NEW_FOLDER_BUTTON = "📁+";
    static final String DELETE_BUTTON = "🗑️";
    static final String COPY_BUTTON = "📋";
    static final String PASTE_BUTTON = "📌";

    // 工具提示
    static final String TOOLTIP_BACK = "后退";
    static final String TOOLTIP_UP = "上级目录";
    static final String TOOLTIP_HOME = "主目录";
    static final String TOOLTIP_REFRESH = "刷新";
    static final String TOOLTIP_NEW_FOLDER = "新建文件夹";
    static final String TOOLTIP_DELETE = "删除";
    static final String TOOLTIP_COPY = "复制";
    static final String TOOLTIP_PASTE = "粘贴";

    // 界面标签
    static final String PATH_LABEL = "路径:";
    static final String DIRECTORY_PANEL_TITLE = "目录";
    static final String FILE_PANEL_TITLE = "文件";
    static final String STATUS_READY = "就绪";

    // 菜单项
    static final String MENU_OPEN = "打开";
    static final String MENU_RENAME = "重命名";
    static final String MENU_DELETE = "删除";
    static final String MENU_PROPERTIES = "属性";

    // 对话框
    static final String DIALOG_NEW_FOLDER_TITLE = "新建文件夹";
    static final String DIALOG_NEW_FOLDER_PROMPT = "请输入文件夹名称:";
    static final String DIALOG_RENAME_TITLE = "重命名";
    static final String DIALOG_RENAME_PROMPT = "请输入新名称:";
    static final String DIALOG_PROPERTIES_TITLE = "文件属性";

    // 确认消息
    static final String CONFIRM_DELETE_SINGLE = "确定要删除选中的项目吗？";
    static final String CONFIRM_DELETE_MULTIPLE = "确定要删除选中的 %d 个项目吗？";
    static final String CONFIRM_DELETE_TITLE = "确认删除";

    // 状态消息
    static final String STATUS_NAVIGATED_TO = "已切换到: %s";
    static final String STATUS_SHOWING_ITEMS = "显示 %d 个项目";
    static final String STATUS_NO_PERMISSION = "无权限访问此目录";
    static final String STATUS_FOLDER_CREATED = "已创建文件夹: %s";
    static final String STATUS_FOLDER_CREATE_FAILED = "创建文件夹失败";
    static final String STATUS_RENAME_SUCCESS = "重命名成功";
    static final String STATUS_RENAME_FAILED = "重命名失败";
    static final String STATUS_DELETED_COUNT = "已删除 %d 个项目";
    static final String STATUS_COPY_NOT_IMPLEMENTED = "复制功能待实现";
    static final String STATUS_PASTE_NOT_IMPLEMENTED = "粘贴功能待实现";
    static final String STATUS_CANNOT_OPEN_FILE = "无法打开文件: %s";

    // 文件类型
    static final String FILE_TYPE_FOLDER = "文件夹";
    static final String FILE_TYPE_FILE = "文件";
    static final String FILE_TYPE_TEXT = "文本文件";
    static final String FILE_TYPE_IMAGE = "图片文件";
    static final String FILE_TYPE_AUDIO = "音频文件";
    static final String FILE_TYPE_VIDEO = "视频文件";
    static final String FILE_TYPE_PDF = "PDF文档";
    static final String FILE_TYPE_WORD = "Word文档";
    static final String FILE_TYPE_EXCEL = "Excel文档";
    static final String FILE_TYPE_CODE = "代码文件";

    // 文件属性
    static final String PROPERTY_NAME = "名称";
    static final String PROPERTY_PATH = "路径";
    static final String PROPERTY_SIZE = "大小";
    static final String PROPERTY_TYPE = "类型";
    static final String PROPERTY_MODIFIED = "修改时间";
    static final String PROPERTY_HIDDEN = "隐藏";
    static final String PROPERTY_YES = "是";
    static final String PROPERTY_NO = "否";

    // 根节点
    static final String ROOT_COMPUTER = "计算机";

    // 列名
    static final String COLUMN_NAME = "名称";
    static final String COLUMN_SIZE = "大小";
    static final String COLUMN_MODIFIED = "修改时间";
    static final String COLUMN_TYPE = "类型";
}

// 使用sealed interface定义文件操作
sealed interface FileOperation permits
    FileOperation.Copy, FileOperation.Move, FileOperation.Delete, FileOperation.Rename {
    
    record Copy(List<FileInfo> files, String targetPath) implements FileOperation {}
    record Move(List<FileInfo> files, String targetPath) implements FileOperation {}
    record Delete(List<FileInfo> files) implements FileOperation {}
    record Rename(FileInfo file, String newName) implements FileOperation {}
}

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new FileManager().setVisible(true);
    });
}

static class FileManager extends JFrame {
    private final JTree directoryTree;
    private final JTable fileTable;
    private final DefaultTreeModel treeModel;
    private final FileTableModel tableModel;
    private final JLabel statusLabel;
    private final JTextField pathField;
    private File currentDirectory;
    
    public FileManager() {
        // 初始化组件
        var rootNode = new DefaultMutableTreeNode(Texts.ROOT_COMPUTER);
        treeModel = new DefaultTreeModel(rootNode);
        directoryTree = new JTree(treeModel);
        
        tableModel = new FileTableModel();
        fileTable = new JTable(tableModel);
        
        statusLabel = new JLabel(Texts.STATUS_READY);
        pathField = new JTextField();
        
        currentDirectory = new File(System.getProperty("user.home"));
        
        initializeGUI();
        loadDirectoryTree();
        refreshFileList();
        setupKeyboardShortcuts();
    }
    
    private void initializeGUI() {
        setTitle(Texts.WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 创建工具栏
        createToolBar();
        
        // 创建主分割面板
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // 左侧目录树
        directoryTree.setRootVisible(false);
        directoryTree.addTreeSelectionListener(e -> {
            var node = (DefaultMutableTreeNode) directoryTree.getLastSelectedPathComponent();
            if (node != null && node.getUserObject() instanceof File file) {
                navigateToDirectory(file);
            }
        });
        
        var treeScrollPane = new JScrollPane(directoryTree);
        treeScrollPane.setPreferredSize(new Dimension(250, 0));
        treeScrollPane.setBorder(BorderFactory.createTitledBorder(Texts.DIRECTORY_PANEL_TITLE));
        
        // 右侧文件列表
        setupFileTable();
        var tableScrollPane = new JScrollPane(fileTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(Texts.FILE_PANEL_TITLE));
        
        splitPane.setLeftComponent(treeScrollPane);
        splitPane.setRightComponent(tableScrollPane);
        splitPane.setDividerLocation(250);
        
        // 底部状态栏
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        add(splitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void createToolBar() {
        var toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // 导航按钮
        var backButton = new JButton(Texts.BACK_BUTTON);
        backButton.setToolTipText(Texts.TOOLTIP_BACK);
        backButton.addActionListener(this::navigateBack);

        var upButton = new JButton(Texts.UP_BUTTON);
        upButton.setToolTipText(Texts.TOOLTIP_UP);
        upButton.addActionListener(this::navigateUp);

        var homeButton = new JButton(Texts.HOME_BUTTON);
        homeButton.setToolTipText(Texts.TOOLTIP_HOME);
        homeButton.addActionListener(e -> navigateToDirectory(new File(System.getProperty("user.home"))));

        var refreshButton = new JButton(Texts.REFRESH_BUTTON);
        refreshButton.setToolTipText(Texts.TOOLTIP_REFRESH);
        refreshButton.addActionListener(this::refreshFileList);
        
        // 路径输入框
        pathField.setPreferredSize(new Dimension(300, 25));
        pathField.addActionListener(e -> {
            var path = pathField.getText();
            var dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                navigateToDirectory(dir);
            }
        });

        // 文件操作按钮
        var newFolderButton = new JButton(Texts.NEW_FOLDER_BUTTON);
        newFolderButton.setToolTipText(Texts.TOOLTIP_NEW_FOLDER);
        newFolderButton.addActionListener(this::createNewFolder);

        var deleteButton = new JButton(Texts.DELETE_BUTTON);
        deleteButton.setToolTipText(Texts.TOOLTIP_DELETE);
        deleteButton.addActionListener(this::deleteSelectedFiles);

        var copyButton = new JButton(Texts.COPY_BUTTON);
        copyButton.setToolTipText(Texts.TOOLTIP_COPY);
        copyButton.addActionListener(this::copySelectedFiles);

        var pasteButton = new JButton(Texts.PASTE_BUTTON);
        pasteButton.setToolTipText(Texts.TOOLTIP_PASTE);
        pasteButton.addActionListener(this::pasteFiles);
        
        toolBar.add(backButton);
        toolBar.add(upButton);
        toolBar.add(homeButton);
        toolBar.add(refreshButton);
        toolBar.addSeparator();
        toolBar.add(new JLabel(Texts.PATH_LABEL));
        toolBar.add(pathField);
        toolBar.addSeparator();
        toolBar.add(newFolderButton);
        toolBar.add(deleteButton);
        toolBar.add(copyButton);
        toolBar.add(pasteButton);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void setupFileTable() {
        fileTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileTable.setRowHeight(20);
        fileTable.getTableHeader().setReorderingAllowed(false);
        
        // 设置列宽
        var columnModel = fileTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(200); // 名称
        columnModel.getColumn(1).setPreferredWidth(80);  // 大小
        columnModel.getColumn(2).setPreferredWidth(120); // 修改时间
        columnModel.getColumn(3).setPreferredWidth(80);  // 类型
        
        // 双击打开文件/文件夹
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var selectedRow = fileTable.getSelectedRow();
                    if (selectedRow != -1) {
                        var fileInfo = tableModel.getFileAt(selectedRow);
                        openFile(fileInfo);
                    }
                }
            }
        });
        
        // 右键菜单
        var contextMenu = createContextMenu();
        fileTable.setComponentPopupMenu(contextMenu);
    }
    
    private JPopupMenu createContextMenu() {
        var menu = new JPopupMenu();
        
        var openItem = new JMenuItem(Texts.MENU_OPEN);
        openItem.addActionListener(e -> {
            var selectedRows = fileTable.getSelectedRows();
            if (selectedRows.length == 1) {
                var fileInfo = tableModel.getFileAt(selectedRows[0]);
                openFile(fileInfo);
            }
        });

        var renameItem = new JMenuItem(Texts.MENU_RENAME);
        renameItem.addActionListener(this::renameSelectedFile);

        var deleteItem = new JMenuItem(Texts.MENU_DELETE);
        deleteItem.addActionListener(this::deleteSelectedFiles);

        var propertiesItem = new JMenuItem(Texts.MENU_PROPERTIES);
        propertiesItem.addActionListener(this::showFileProperties);
        
        menu.add(openItem);
        menu.addSeparator();
        menu.add(renameItem);
        menu.add(deleteItem);
        menu.addSeparator();
        menu.add(propertiesItem);
        
        return menu;
    }
    
    private void loadDirectoryTree() {
        var rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        
        // 添加根目录
        for (var root : File.listRoots()) {
            var rootFileNode = new DefaultMutableTreeNode(root);
            rootNode.add(rootFileNode);
            loadDirectoryNode(rootFileNode, root, 1); // 只加载一级
        }
        
        treeModel.reload();
        directoryTree.expandRow(0);
    }
    
    private void loadDirectoryNode(DefaultMutableTreeNode parentNode, File directory, int maxDepth) {
        if (maxDepth <= 0 || !directory.canRead()) return;
        
        try {
            var files = directory.listFiles();
            if (files != null) {
                Arrays.stream(files)
                    .filter(File::isDirectory)
                    .filter(f -> !f.isHidden())
                    .sorted(Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER))
                    .forEach(dir -> {
                        var node = new DefaultMutableTreeNode(dir);
                        parentNode.add(node);
                        if (maxDepth > 1) {
                            loadDirectoryNode(node, dir, maxDepth - 1);
                        }
                    });
            }
        } catch (SecurityException e) {
            // 忽略无权限访问的目录
        }
    }
    
    private void navigateToDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            currentDirectory = directory;
            pathField.setText(directory.getAbsolutePath());
            refreshFileList();
            updateStatus(String.format(Texts.STATUS_NAVIGATED_TO, directory.getAbsolutePath()));
        }
    }
    
    private void refreshFileList() {
        refreshFileList(null);
    }

    private void refreshFileList(ActionEvent e) {
        if (currentDirectory == null) return;
        
        try {
            var files = currentDirectory.listFiles();
            var fileInfoList = files != null ? 
                Arrays.stream(files)
                    .map(FileInfo::from)
                    .sorted(this::compareFiles)
                    .toList() : 
                List.<FileInfo>of();
            
            tableModel.setFiles(fileInfoList);
            updateStatus(String.format(Texts.STATUS_SHOWING_ITEMS, fileInfoList.size()));
        } catch (SecurityException ex) {
            updateStatus(Texts.STATUS_NO_PERMISSION);
        }
    }
    
    private int compareFiles(FileInfo a, FileInfo b) {
        // 目录优先，然后按名称排序
        if (a.isDirectory() != b.isDirectory()) {
            return a.isDirectory() ? -1 : 1;
        }
        return a.name().compareToIgnoreCase(b.name());
    }
    
    private void openFile(FileInfo fileInfo) {
        var file = new File(fileInfo.path());
        
        if (fileInfo.isDirectory()) {
            navigateToDirectory(file);
        } else {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                updateStatus(String.format(Texts.STATUS_CANNOT_OPEN_FILE, e.getMessage()));
            }
        }
    }
    
    private void navigateUp() {
        navigateUp(null);
    }

    private void navigateUp(ActionEvent e) {
        if (currentDirectory != null) {
            var parent = currentDirectory.getParentFile();
            if (parent != null) {
                navigateToDirectory(parent);
            }
        }
    }

    private void navigateBack() {
        navigateBack(null);
    }

    private void navigateBack(ActionEvent e) {
        // 简化版后退功能
        navigateUp();
    }

    private void createNewFolder() {
        createNewFolder(null);
    }

    private void createNewFolder(ActionEvent e) {
        var name = JOptionPane.showInputDialog(this, Texts.DIALOG_NEW_FOLDER_PROMPT, Texts.DIALOG_NEW_FOLDER_TITLE, JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            var newFolder = new File(currentDirectory, name.trim());
            if (newFolder.mkdir()) {
                refreshFileList();
                updateStatus(String.format(Texts.STATUS_FOLDER_CREATED, name));
            } else {
                updateStatus(Texts.STATUS_FOLDER_CREATE_FAILED);
            }
        }
    }
    
    private void deleteSelectedFiles() {
        deleteSelectedFiles(null);
    }

    private void deleteSelectedFiles(ActionEvent e) {
        var selectedRows = fileTable.getSelectedRows();
        if (selectedRows.length == 0) return;
        
        var message = selectedRows.length == 1 ?
            Texts.CONFIRM_DELETE_SINGLE :
            String.format(Texts.CONFIRM_DELETE_MULTIPLE, selectedRows.length);

        var result = JOptionPane.showConfirmDialog(this, message, Texts.CONFIRM_DELETE_TITLE, JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            var deletedCount = 0;
            for (var row : selectedRows) {
                var fileInfo = tableModel.getFileAt(row);
                var file = new File(fileInfo.path());
                if (file.delete()) {
                    deletedCount++;
                }
            }
            refreshFileList();
            updateStatus(String.format(Texts.STATUS_DELETED_COUNT, deletedCount));
        }
    }
    
    private void copySelectedFiles() {
        copySelectedFiles(null);
    }

    private void copySelectedFiles(ActionEvent e) {
        // 简化版复制功能
        updateStatus(Texts.STATUS_COPY_NOT_IMPLEMENTED);
    }

    private void pasteFiles() {
        pasteFiles(null);
    }

    private void pasteFiles(ActionEvent e) {
        // 简化版粘贴功能
        updateStatus(Texts.STATUS_PASTE_NOT_IMPLEMENTED);
    }
    
    private void renameSelectedFile() {
        renameSelectedFile(null);
    }

    private void renameSelectedFile(ActionEvent e) {
        var selectedRows = fileTable.getSelectedRows();
        if (selectedRows.length != 1) return;
        
        var fileInfo = tableModel.getFileAt(selectedRows[0]);
        var newName = JOptionPane.showInputDialog(this, Texts.DIALOG_RENAME_PROMPT, fileInfo.name());
        
        if (newName != null && !newName.trim().isEmpty() && !newName.equals(fileInfo.name())) {
            var oldFile = new File(fileInfo.path());
            var newFile = new File(oldFile.getParent(), newName.trim());
            
            if (oldFile.renameTo(newFile)) {
                refreshFileList();
                updateStatus(Texts.STATUS_RENAME_SUCCESS);
            } else {
                updateStatus(Texts.STATUS_RENAME_FAILED);
            }
        }
    }
    
    private void showFileProperties() {
        showFileProperties(null);
    }

    private void showFileProperties(ActionEvent e) {
        var selectedRows = fileTable.getSelectedRows();
        if (selectedRows.length != 1) return;
        
        var fileInfo = tableModel.getFileAt(selectedRows[0]);
        var properties = String.format("""
            %s: %s
            %s: %s
            %s: %s
            %s: %s
            %s: %s
            %s: %s
            """,
            Texts.PROPERTY_NAME, fileInfo.name(),
            Texts.PROPERTY_PATH, fileInfo.path(),
            Texts.PROPERTY_SIZE, fileInfo.formattedSize(),
            Texts.PROPERTY_TYPE, fileInfo.isDirectory() ? Texts.FILE_TYPE_FOLDER : Texts.FILE_TYPE_FILE,
            Texts.PROPERTY_MODIFIED, fileInfo.formattedDate(),
            Texts.PROPERTY_HIDDEN, fileInfo.isHidden() ? Texts.PROPERTY_YES : Texts.PROPERTY_NO
        );

        JOptionPane.showMessageDialog(this, properties, Texts.DIALOG_PROPERTIES_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void setupKeyboardShortcuts() {
        // 添加键盘快捷键支持
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent ev) {
                int keyCode = ev.getKeyCode();

                switch (keyCode) {
                    case java.awt.event.KeyEvent.VK_F5:
                        // F5键刷新
                        refreshFileList();
                        break;
                    case java.awt.event.KeyEvent.VK_BACK_SPACE:
                        // 退格键返回上级目录
                        navigateUp();
                        break;
                    case java.awt.event.KeyEvent.VK_F2:
                        // F2键重命名选中的文件
                        renameSelectedFile();
                        break;
                    case java.awt.event.KeyEvent.VK_DELETE:
                        // Delete键删除选中的文件
                        deleteSelectedFiles();
                        break;
                    case java.awt.event.KeyEvent.VK_ENTER:
                        // 回车键打开选中的文件/文件夹
                        var selectedRows = fileTable.getSelectedRows();
                        if (selectedRows.length == 1) {
                            var fileInfo = tableModel.getFileAt(selectedRows[0]);
                            openFile(fileInfo);
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_HOME:
                        // Home键跳转到主目录
                        navigateToDirectory(new File(System.getProperty("user.home")));
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
    
    // 文件表格模型
    static class FileTableModel extends javax.swing.table.AbstractTableModel {
        private final String[] columnNames = {Texts.COLUMN_NAME, Texts.COLUMN_SIZE, Texts.COLUMN_MODIFIED, Texts.COLUMN_TYPE};
        private List<FileInfo> files = new ArrayList<>();
        
        void setFiles(List<FileInfo> files) {
            this.files = new ArrayList<>(files);
            fireTableDataChanged();
        }
        
        FileInfo getFileAt(int row) {
            return files.get(row);
        }
        
        @Override
        public int getRowCount() {
            return files.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            var fileInfo = files.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> fileInfo.name();
                case 1 -> fileInfo.formattedSize();
                case 2 -> fileInfo.formattedDate();
                case 3 -> fileInfo.isDirectory() ? Texts.FILE_TYPE_FOLDER : getFileType(fileInfo.extension());
                default -> "";
            };
        }
        
        private String getFileType(String extension) {
            return switch (extension.toLowerCase()) {
                case "txt", "md" -> Texts.FILE_TYPE_TEXT;
                case "jpg", "jpeg", "png", "gif", "bmp" -> Texts.FILE_TYPE_IMAGE;
                case "mp3", "wav", "flac" -> Texts.FILE_TYPE_AUDIO;
                case "mp4", "avi", "mkv" -> Texts.FILE_TYPE_VIDEO;
                case "pdf" -> Texts.FILE_TYPE_PDF;
                case "doc", "docx" -> Texts.FILE_TYPE_WORD;
                case "xls", "xlsx" -> Texts.FILE_TYPE_EXCEL;
                case "java", "py", "js", "html", "css" -> Texts.FILE_TYPE_CODE;
                case "" -> Texts.FILE_TYPE_FILE;
                default -> extension.toUpperCase() + Texts.FILE_TYPE_FILE;
            };
        }
    }
}
