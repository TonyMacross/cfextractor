package net.gcae.utils.extractor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the results of ColdFusion code analysis
 */
public class AnalysisResult {
    private List<FileInfo> files = new ArrayList<>();
    private List<QueryInfo> queries = new ArrayList<>();
    private List<FunctionInfo> functions = new ArrayList<>();
    private List<InvokeInfo> invokes = new ArrayList<>();
    private List<ComponentInfo> components = new ArrayList<>();
    private List<IncludeInfo> includes = new ArrayList<>();
    private List<ModuleInfo> modules = new ArrayList<>();
    
    // Getters and setters
    public List<FileInfo> getFiles() { return files; }
    public void setFiles(List<FileInfo> files) { this.files = files; }
    
    public List<QueryInfo> getQueries() { return queries; }
    public void setQueries(List<QueryInfo> queries) { this.queries = queries; }
    
    public List<FunctionInfo> getFunctions() { return functions; }
    public void setFunctions(List<FunctionInfo> functions) { this.functions = functions; }
    
    public List<InvokeInfo> getInvokes() { return invokes; }
    public void setInvokes(List<InvokeInfo> invokes) { this.invokes = invokes; }
    
    public List<ComponentInfo> getComponents() { return components; }
    public void setComponents(List<ComponentInfo> components) { this.components = components; }
    
    public List<IncludeInfo> getIncludes() { return includes; }
    public void setIncludes(List<IncludeInfo> includes) { this.includes = includes; }
    
    public List<ModuleInfo> getModules() { return modules; }
    public void setModules(List<ModuleInfo> modules) { this.modules = modules; }
}