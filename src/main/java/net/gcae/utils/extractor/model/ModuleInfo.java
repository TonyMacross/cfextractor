package net.gcae.utils.extractor.model;

/**
 * Information about a module
 */
public class ModuleInfo {
    private String template;
    private String fileLocation;
    private String attributes;
    
    public ModuleInfo(String template, String fileLocation, String attributes) {
        this.template = template;
        this.fileLocation = fileLocation;
        this.attributes = attributes;
    }
    
    // Getters
    public String getTemplate() { return template; }
    public String getFileLocation() { return fileLocation; }
    public String getAttributes() { return attributes; }
}