package net.gcae.utils.model;

import java.time.LocalDateTime;
import java.util.List;

public class AnalysisResult {
    private String applicationPath;
    private LocalDateTime analysisDate;
    private List<FileInfo> files;
    private List<QueryInfo> queries;
    private List<FunctionInfo> functions;
    private List<ComponentInfo> components;
    private List<InvokeInfo> invokes;
    private List<IncludeInfo> includes;
    private List<ModuleInfo> modules;
    
    // Getters y setters
    public String getApplicationPath() { return applicationPath; }
    public void setApplicationPath(String applicationPath) { this.applicationPath = applicationPath; }
    
    public LocalDateTime getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDateTime analysisDate) { this.analysisDate = analysisDate; }
    
    public List<FileInfo> getFiles() { return files; }
    public void setFiles(List<FileInfo> files) { this.files = files; }
    
    public List<QueryInfo> getQueries() { return queries; }
    public void setQueries(List<QueryInfo> queries) { this.queries = queries; }
    
    public List<FunctionInfo> getFunctions() { return functions; }
    public void setFunctions(List<FunctionInfo> functions) { this.functions = functions; }
    
    public List<ComponentInfo> getComponents() { return components; }
    public void setComponents(List<ComponentInfo> components) { this.components = components; }
    
    public List<InvokeInfo> getInvokes() { return invokes; }
    public void setInvokes(List<InvokeInfo> invokes) { this.invokes = invokes; }
    
    public List<IncludeInfo> getIncludes() { return includes; }
    public void setIncludes(List<IncludeInfo> includes) { this.includes = includes; }
    
    public List<ModuleInfo> getModules() { return modules; }
    public void setModules(List<ModuleInfo> modules) { this.modules = modules; }
}