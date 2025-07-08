package net.gcae.utils.model;

import java.util.List;

public class InvokeInfo {
    private String component;
    private String method;
    private String fileName;
    private int lineNumber;
    private List<String> arguments;
    
    // Getters y setters
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    
    public List<String> getArguments() { return arguments; }
    public void setArguments(List<String> arguments) { this.arguments = arguments; }
}