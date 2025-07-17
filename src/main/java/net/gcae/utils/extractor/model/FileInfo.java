package net.gcae.utils.extractor.model;

/**
 * Information about a file
 */
public class FileInfo {
    private String fileName;
    private String filePath;
    private String fileType;
    private long fileSize;
    private int lineCount;
    
    public FileInfo(String fileName, String filePath, String fileType, long fileSize, int lineCount) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.lineCount = lineCount;
    }
    
    // Getters
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getFileType() { return fileType; }
    public long getFileSize() { return fileSize; }
    public int getLineCount() { return lineCount; }
}