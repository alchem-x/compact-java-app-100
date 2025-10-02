import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 文件传输工具
 * 简单的文件复制、移动和传输工具
 */
void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new FileTransfer().setVisible(true);
    });
}

class FileTransfer extends JFrame {
    private JList<File> sourceList;
    private JList<File> targetList;
    private DefaultListModel<File> sourceModel;
    private DefaultListModel<File> targetModel;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    private File currentSourceDir;
    private File currentTargetDir;
    
    public FileTransfer() {
        initializeUI();
        loadDefaultDirectories();
    }
    
    private void initializeUI() {
        setTitle("文件传输工具 - File Transfer");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 主面板
        var mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        
        // 顶部工具栏
        var toolBar = createToolBar();
        mainPanel.add(toolBar, BorderLayout.NORTH);
        
        // 中央文件面板
        var filePanel = createFilePanel();
        mainPanel.add(filePanel, BorderLayout.CENTER);
        
        // 底部状态面板
        var statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createToolBar() {
        var panel = new JPanel(new FlowLayout());
        
        var copyBtn = new JButton("复制");
        var moveBtn = new JButton("移动");
        var deleteBtn = new JButton("删除");
        var refreshBtn = new JButton("刷新");
        
        copyBtn.addActionListener(e -> copyFiles());
        moveBtn.addActionListener(e -> moveFiles());
        deleteBtn.addActionListener(e -> deleteFiles());
        refreshBtn.addActionListener(e -> refreshLists());
        
        panel.add(copyBtn);
        panel.add(moveBtn);
        panel.add(deleteBtn);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(refreshBtn);
        
        return panel;
    }
    
    private JPanel createFilePanel() {
        var panel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // 源文件面板
        var sourcePanel = new JPanel(new BorderLayout());
        sourcePanel.setBorder(BorderFactory.createTitledBorder("源目录"));
        
        var sourcePathPanel = new JPanel(new BorderLayout());
        var sourcePathField = new JTextField();
        sourcePathField.setEditable(false);
        var sourceBrowseBtn = new JButton("浏览");
        sourceBrowseBtn.addActionListener(e -> browseSourceDirectory(sourcePathField));
        
        sourcePathPanel.add(sourcePathField, BorderLayout.CENTER);
        sourcePathPanel.add(sourceBrowseBtn, BorderLayout.EAST);
        sourcePanel.add(sourcePathPanel, BorderLayout.NORTH);
        
        sourceModel = new DefaultListModel<>();
        sourceList = new JList<>(sourceModel);
        sourceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sourceList.setCellRenderer(new FileListCellRenderer());
        
        var sourceScrollPane = new JScrollPane(sourceList);
        sourcePanel.add(sourceScrollPane, BorderLayout.CENTER);
        
        // 目标文件面板
        var targetPanel = new JPanel(new BorderLayout());
        targetPanel.setBorder(BorderFactory.createTitledBorder("目标目录"));
        
        var targetPathPanel = new JPanel(new BorderLayout());
        var targetPathField = new JTextField();
        targetPathField.setEditable(false);
        var targetBrowseBtn = new JButton("浏览");
        targetBrowseBtn.addActionListener(e -> browseTargetDirectory(targetPathField));
        
        targetPathPanel.add(targetPathField, BorderLayout.CENTER);
        targetPathPanel.add(targetBrowseBtn, BorderLayout.EAST);
        targetPanel.add(targetPathPanel, BorderLayout.NORTH);
        
        targetModel = new DefaultListModel<>();
        targetList = new JList<>(targetModel);
        targetList.setCellRenderer(new FileListCellRenderer());
        
        var targetScrollPane = new JScrollPane(targetList);
        targetPanel.add(targetScrollPane, BorderLayout.CENTER);
        
        panel.add(sourcePanel);
        panel.add(targetPanel);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        var panel = new JPanel(new BorderLayout());
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("就绪");
        
        statusLabel = new JLabel("选择源目录和目标目录开始传输");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadDefaultDirectories() {
        currentSourceDir = new File(System.getProperty("user.home"));
        currentTargetDir = new File(System.getProperty("user.home"));
        
        loadDirectory(currentSourceDir, sourceModel);
        loadDirectory(currentTargetDir, targetModel);
    }
    
    private void browseSourceDirectory(JTextField pathField) {
        var chooser = new JFileChooser(currentSourceDir);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentSourceDir = chooser.getSelectedFile();
            pathField.setText(currentSourceDir.getAbsolutePath());
            loadDirectory(currentSourceDir, sourceModel);
        }
    }
    
    private void browseTargetDirectory(JTextField pathField) {
        var chooser = new JFileChooser(currentTargetDir);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentTargetDir = chooser.getSelectedFile();
            pathField.setText(currentTargetDir.getAbsolutePath());
            loadDirectory(currentTargetDir, targetModel);
        }
    }
    
    private void loadDirectory(File directory, DefaultListModel<File> model) {
        model.clear();
        
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            Arrays.sort(files, (a, b) -> {
                if (a.isDirectory() && !b.isDirectory()) return -1;
                if (!a.isDirectory() && b.isDirectory()) return 1;
                return a.getName().compareToIgnoreCase(b.getName());
            });
            
            for (var file : files) {
                model.addElement(file);
            }
        }
    }
    
    private void copyFiles() {
        var selectedFiles = sourceList.getSelectedValuesList();
        if (selectedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择要复制的文件");
            return;
        }
        
        if (currentTargetDir == null) {
            JOptionPane.showMessageDialog(this, "请选择目标目录");
            return;
        }
        
        performFileOperation(selectedFiles, FileOperation.COPY);
    }
    
    private void moveFiles() {
        var selectedFiles = sourceList.getSelectedValuesList();
        if (selectedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择要移动的文件");
            return;
        }
        
        if (currentTargetDir == null) {
            JOptionPane.showMessageDialog(this, "请选择目标目录");
            return;
        }
        
        performFileOperation(selectedFiles, FileOperation.MOVE);
    }
    
    private void deleteFiles() {
        var selectedFiles = sourceList.getSelectedValuesList();
        if (selectedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择要删除的文件");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定要删除选中的 " + selectedFiles.size() + " 个文件吗？",
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            performFileOperation(selectedFiles, FileOperation.DELETE);
        }
    }
    
    private void performFileOperation(java.util.List<File> files, FileOperation operation) {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setMaximum(files.size());
                progressBar.setValue(0);
                
                for (int i = 0; i < files.size(); i++) {
                    var file = files.get(i);
                    
                    try {
                        switch (operation) {
                            case COPY -> copyFile(file, new File(currentTargetDir, file.getName()));
                            case MOVE -> moveFile(file, new File(currentTargetDir, file.getName()));
                            case DELETE -> deleteFile(file);
                        }
                        
                        publish(i + 1);
                        
                    } catch (IOException e) {
                        SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(FileTransfer.this, 
                                "操作失败: " + file.getName() + " - " + e.getMessage()));
                    }
                }
                
                return null;
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                int latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest);
                progressBar.setString(String.format("%s中... (%d/%d)", 
                    operation.description, latest, files.size()));
            }
            
            @Override
            protected void done() {
                progressBar.setString("操作完成");
                statusLabel.setText(operation.description + "完成");
                refreshLists();
            }
        };
        
        worker.execute();
    }
    
    private void copyFile(File source, File target) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, target);
        } else {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    private void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdirs();
        }
        
        File[] files = source.listFiles();
        if (files != null) {
            for (var file : files) {
                var targetFile = new File(target, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, targetFile);
                } else {
                    Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
    
    private void moveFile(File source, File target) throws IOException {
        Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    
    private void deleteFile(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            Files.delete(file.toPath());
        }
    }
    
    private void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (var file : files) {
                deleteFile(file);
            }
        }
        Files.delete(directory.toPath());
    }
    
    private void refreshLists() {
        loadDirectory(currentSourceDir, sourceModel);
        loadDirectory(currentTargetDir, targetModel);
    }
    
    enum FileOperation {
        COPY("复制"), MOVE("移动"), DELETE("删除");
        
        final String description;
        
        FileOperation(String description) {
            this.description = description;
        }
    }
    
    class FileListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof File file) {
                setText(file.getName());
                
                if (file.isDirectory()) {
                    setIcon(UIManager.getIcon("FileView.directoryIcon"));
                } else {
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                }
                
                // 显示文件大小
                if (!file.isDirectory()) {
                    long size = file.length();
                    String sizeStr = formatFileSize(size);
                    setText(file.getName() + " (" + sizeStr + ")");
                }
            }
            
            return this;
        }
        
        private String formatFileSize(long size) {
            if (size < 1024) return size + " B";
            if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
            if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
            return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
