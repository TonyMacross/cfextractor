package net.gcae.utils.extractor.model;

/**
 * Information about an include
 */
public class IncludeInfo {
    private String template;
    private String fileLocation;
    
    public IncludeInfo(String template, String fileLocation) {
        this.template = template;
        this.fileLocation = fileLocation;
    }
    
    // Getters
    public String getTemplate() { return template; }
    public String getFileLocation() { return fileLocation; }
}