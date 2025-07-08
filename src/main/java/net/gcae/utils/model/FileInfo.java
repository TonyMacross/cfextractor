package net.gcae.utils.model;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class FileInfo {
    private Path filePath;
    private String relativePath;
    private String fileName;
    private String fileExtension;
    private long fileSize;
    private LocalDateTime lastModified;
    private int lineCount;
    
    public FileInfo(Path filePath, String relativePath) {
        this.filePath = filePath;
        this.relativePath = relativePath;
        this.fileName = filePath.getFileName().toString();
        this.fileExtension = getFileExtension(fileName);
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
    }
    
    // Getters y setters
    public Path getFilePath() { return filePath; }
    public void setFilePath(Path filePath) { this.filePath = filePath; }
    
    public String getRelativePath() { return relativePath; }
    public void setRelativePath(String relativePath) { this.relativePath = relativePath; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFileExtension() { return fileExtension; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public int getLineCount() { return lineCount; }
    public void setLineCount(int lineCount) { this.lineCount = lineCount; }
}