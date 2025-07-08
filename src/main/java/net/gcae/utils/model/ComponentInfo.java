package net.gcae.utils.model;

import java.util.List;

public class ComponentInfo {
    private String componentName;
    private String fileName;
    private String extends_;
    private String implements_;
    private List<FunctionInfo> functions;
    private List<String> usedInFiles;
    private int lineNumber;
    
    // Getters y setters
    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getExtends_() { return extends_; }
    public void setExtends_(String extends_) { this.extends_ = extends_; }
    
    public String getImplements_() { return implements_; }
    public void setImplements_(String implements_) { this.implements_ = implements_; }
    
    public List<FunctionInfo> getFunctions() { return functions; }
    public void setFunctions(List<FunctionInfo> functions) { this.functions = functions; }
    
    public List<String> getUsedInFiles() { return usedInFiles; }
    public void setUsedInFiles(List<String> usedInFiles) { this.usedInFiles = usedInFiles; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
}