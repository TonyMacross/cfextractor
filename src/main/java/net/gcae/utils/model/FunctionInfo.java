package net.gcae.utils.model;

import java.util.List;

public class FunctionInfo {
    private String functionName;
    private String fileName;
    private String componentName;
    private int lineNumber;
    private String access;
    private String returnType;
    private List<String> parameters;
    private List<String> usedInFiles;
    
    // Getters y setters
    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    
    public String getAccess() { return access; }
    public void setAccess(String access) { this.access = access; }
    
    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }
    
    public List<String> getParameters() { return parameters; }
    public void setParameters(List<String> parameters) { this.parameters = parameters; }
    
    public List<String> getUsedInFiles() { return usedInFiles; }
    public void setUsedInFiles(List<String> usedInFiles) { this.usedInFiles = usedInFiles; }
}