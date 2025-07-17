package net.gcae.utils.extractor.model;

/**
 * Information about a function
 */
public class FunctionInfo {
    private String functionName;
    private String returnType;
    private String access;
    private String fileLocation;
    private String parameters;
    private String usedIn;
    
    public FunctionInfo(String functionName, String returnType, String access, String fileLocation, String parameters, String usedIn) {
        this.functionName = functionName;
        this.returnType = returnType;
        this.access = access;
        this.fileLocation = fileLocation;
        this.parameters = parameters;
        this.usedIn = usedIn;
    }
    
    // Getters
    public String getFunctionName() { return functionName; }
    public String getReturnType() { return returnType; }
    public String getAccess() { return access; }
    public String getFileLocation() { return fileLocation; }
    public String getParameters() { return parameters; }
    public String getUsedIn() { return usedIn; }
    
    // Setter for usedIn to support usage tracking
    public void setUsedIn(String usedIn) { this.usedIn = usedIn; }
}