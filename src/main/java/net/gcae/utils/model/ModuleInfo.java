package net.gcae.utils.model;

import java.util.List;

public class ModuleInfo {
    private String template;
    private String name;
    private String fileName;
    private int lineNumber;
    private List<String> attributes;
    
    // Getters y setters
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    
    public List<String> getAttributes() { return attributes; }
    public void setAttributes(List<String> attributes) { this.attributes = attributes; }
}