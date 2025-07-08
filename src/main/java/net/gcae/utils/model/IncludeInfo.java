package net.gcae.utils.model;

public class IncludeInfo {
    private String template;
    private String fileName;
    private int lineNumber;
    
    // Getters y setters
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
}