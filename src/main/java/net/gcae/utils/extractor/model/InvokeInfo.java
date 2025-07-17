package net.gcae.utils.extractor.model;

/**
 * Information about an invoke
 */
public class InvokeInfo {
    private String component;
    private String method;
    private String fileLocation;
    private String parameters;
    
    public InvokeInfo(String component, String method, String fileLocation, String parameters) {
        this.component = component;
        this.method = method;
        this.fileLocation = fileLocation;
        this.parameters = parameters;
    }
    
    // Getters
    public String getComponent() { return component; }
    public String getMethod() { return method; }
    public String getFileLocation() { return fileLocation; }
    public String getParameters() { return parameters; }
}