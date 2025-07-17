package net.gcae.utils.extractor.model;

/**
 * Information about a component
 */
public class ComponentInfo {
    private String componentName;
    private String extends_;
    private String fileLocation;
    private String usedIn;
    
    public ComponentInfo(String componentName, String extends_, String fileLocation, String usedIn) {
        this.componentName = componentName;
        this.extends_ = extends_;
        this.fileLocation = fileLocation;
        this.usedIn = usedIn;
    }
    
    // Getters
    public String getComponentName() { return componentName; }
    public String getExtends() { return extends_; }
    public String getFileLocation() { return fileLocation; }
    public String getUsedIn() { return usedIn; }
    
    // Setter for usedIn to support usage tracking
    public void setUsedIn(String usedIn) { this.usedIn = usedIn; }
}
