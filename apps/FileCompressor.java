import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.zip.*;

void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new FileCompressor().setVisible(true);
    });
}

static class FileCompressor extends JFrame {
    private JTextArea logArea;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JButton compressButton, decompressButton;
    
    public FileCompressor() {
        setTitle("文件压缩工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        
        initializeUI();
        setLocationRelativeTo(null);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 顶部控制面板
        var controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("操作选择"));
        
        compressButton = new JButton("压缩文件/文件夹");
        compressButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        compressButton.addActionListener(this::compressFiles);
        
        decompressButton = new JButton("解压缩文件");
        decompressButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        decompressButton.addActionListener(this::decompressFile);
        
        controlPanel.add(compressButton);
        controlPanel.add(decompressButton);
        
        // 进度面板
        var progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createTitledBorder("进度"));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("准备就绪");
        
        statusLabel = new JLabel("选择要压缩或解压的文件");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // 日志面板
        var logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("操作日志"));
        
        logArea = new JTextArea(15, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        var scrollPane = new JScrollPane(logArea);
        
        var clearButton = new JButton("清空日志");
        clearButton.addActionListener(e -> logArea.setText(""));
        
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearButton);
        
        logPanel.add(scrollPane, BorderLayout.CENTER);
        logPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(controlPanel, BorderLayout.NORTH);
        add(progressPanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
        
        addLog("文件压缩工具已启动");
        addLog("支持ZIP格式的压缩和解压缩");
    }
    
    private void compressFiles(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle("选择要压缩的文件或文件夹");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var selectedFiles = fileChooser.getSelectedFiles();
            
            // 选择保存位置
            var saveChooser = new JFileChooser();
            saveChooser.setFileFilter(new FileNameExtensionFilter("ZIP文件", "zip"));
            saveChooser.setSelectedFile(new File("compressed.zip"));
            saveChooser.setDialogTitle("保存压缩文件");
            
            if (saveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                var zipFile = saveChooser.getSelectedFile();
                if (!zipFile.getName().toLowerCase().endsWith(".zip")) {
                    zipFile = new File(zipFile.getAbsolutePath() + ".zip");
                }
                
                compressFilesAsync(selectedFiles, zipFile);
            }
        }
    }
    
    private void decompressFile(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("ZIP文件", "zip"));
        fileChooser.setDialogTitle("选择要解压的ZIP文件");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            var zipFile = fileChooser.getSelectedFile();
            
            // 选择解压目录
            var dirChooser = new JFileChooser();
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setDialogTitle("选择解压目录");
            
            if (dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                var extractDir = dirChooser.getSelectedFile();
                decompressFileAsync(zipFile, extractDir);
            }
        }
    }
    
    private void compressFilesAsync(File[] files, File zipFile) {
        compressButton.setEnabled(false);
        decompressButton.setEnabled(false);
        
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(0);
            progressBar.setString("正在压缩...");
            statusLabel.setText("压缩进行中，请稍候...");
        });
        
        new Thread(() -> {
            try {
                compressFilesInternal(files, zipFile);
                
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(100);
                    progressBar.setString("压缩完成");
                    statusLabel.setText("压缩完成: " + zipFile.getName());
                    addLog("压缩完成: " + zipFile.getAbsolutePath());
                    addLog("压缩文件大小: " + formatFileSize(zipFile.length()));
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setString("压缩失败");
                    statusLabel.setText("压缩失败: " + ex.getMessage());
                    addLog("压缩失败: " + ex.getMessage());
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    compressButton.setEnabled(true);
                    decompressButton.setEnabled(true);
                });
            }
        }).start();
    }
    
    private void decompressFileAsync(File zipFile, File extractDir) {
        compressButton.setEnabled(false);
        decompressButton.setEnabled(false);
        
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(0);
            progressBar.setString("正在解压...");
            statusLabel.setText("解压进行中，请稍候...");
        });
        
        new Thread(() -> {
            try {
                decompressFileInternal(zipFile, extractDir);
                
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(100);
                    progressBar.setString("解压完成");
                    statusLabel.setText("解压完成到: " + extractDir.getName());
                    addLog("解压完成到: " + extractDir.getAbsolutePath());
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setString("解压失败");
                    statusLabel.setText("解压失败: " + ex.getMessage());
                    addLog("解压失败: " + ex.getMessage());
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    compressButton.setEnabled(true);
                    decompressButton.setEnabled(true);
                });
            }
        }).start();
    }
    
    private void compressFilesInternal(File[] files, File zipFile) throws IOException {
        addLog("开始压缩 " + files.length + " 个文件/文件夹");
        
        try (var fos = new FileOutputStream(zipFile);
             var zos = new ZipOutputStream(fos)) {
            
            int totalFiles = countFiles(files);
            int processedFiles = 0;
            
            for (var file : files) {
                if (file.isDirectory()) {
                    processedFiles = compressDirectory(zos, file, file.getName(), processedFiles, totalFiles);
                } else {
                    compressFile(zos, file, file.getName());
                    processedFiles++;
                    updateProgress(processedFiles, totalFiles);
                }
            }
        }
    }
    
    private int compressDirectory(ZipOutputStream zos, File dir, String baseName, int processedFiles, int totalFiles) throws IOException {
        var files = dir.listFiles();
        if (files == null) return processedFiles;
        
        for (var file : files) {
            var entryName = baseName + "/" + file.getName();
            
            if (file.isDirectory()) {
                processedFiles = compressDirectory(zos, file, entryName, processedFiles, totalFiles);
            } else {
                compressFile(zos, file, entryName);
                processedFiles++;
                updateProgress(processedFiles, totalFiles);
                addLog("压缩: " + entryName);
            }
        }
        
        return processedFiles;
    }
    
    private void compressFile(ZipOutputStream zos, File file, String entryName) throws IOException {
        var entry = new ZipEntry(entryName);
        entry.setTime(file.lastModified());
        zos.putNextEntry(entry);
        
        try (var fis = new FileInputStream(file)) {
            var buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
        }
        
        zos.closeEntry();
    }
    
    private void decompressFileInternal(File zipFile, File extractDir) throws IOException {
        addLog("开始解压: " + zipFile.getName());
        
        try (var fis = new FileInputStream(zipFile);
             var zis = new ZipInputStream(fis)) {
            
            ZipEntry entry;
            final int[] extractedFiles = {0};
            
            while ((entry = zis.getNextEntry()) != null) {
                var entryFile = new File(extractDir, entry.getName());
                
                // 安全检查，防止目录遍历攻击
                if (!entryFile.getCanonicalPath().startsWith(extractDir.getCanonicalPath())) {
                    throw new IOException("不安全的ZIP条目: " + entry.getName());
                }
                
                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    // 确保父目录存在
                    entryFile.getParentFile().mkdirs();
                    
                    try (var fos = new FileOutputStream(entryFile)) {
                        var buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    
                    // 恢复文件时间
                    entryFile.setLastModified(entry.getTime());
                }
                
                extractedFiles[0]++;
                addLog("解压: " + entry.getName());
                
                // 更新进度（简单估算）
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(Math.min(90, extractedFiles[0] * 5));
                });
                
                zis.closeEntry();
            }
            
            addLog("共解压 " + extractedFiles[0] + " 个文件");
        }
    }
    
    private int countFiles(File[] files) {
        int count = 0;
        for (var file : files) {
            if (file.isDirectory()) {
                count += countFilesInDirectory(file);
            } else {
                count++;
            }
        }
        return count;
    }
    
    private int countFilesInDirectory(File dir) {
        int count = 0;
        var files = dir.listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isDirectory()) {
                    count += countFilesInDirectory(file);
                } else {
                    count++;
                }
            }
        }
        return count;
    }
    
    private void updateProgress(int processed, int total) {
        SwingUtilities.invokeLater(() -> {
            int progress = total > 0 ? (processed * 100) / total : 0;
            progressBar.setValue(progress);
            progressBar.setString(String.format("进度: %d/%d (%d%%)", processed, total, progress));
        });
    }
    
    private void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            var timestamp = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.append(String.format("[%s] %s%n", timestamp, message));
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
