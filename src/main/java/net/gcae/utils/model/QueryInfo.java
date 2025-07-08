package net.gcae.utils.model;

import java.util.List;

public class QueryInfo {
    private String queryName;
    private String sql;
    private String fileName;
    private String functionName;
    private String componentName;
    private int lineNumber;
    private List<String> parameters;
    private String datasource;
    
    // Getters y setters
    public String getQueryName() { return queryName; }
    public void setQueryName(String queryName) { this.queryName = queryName; }
    
    public String getSql() { return sql; }
    public void setSql(String sql) { this.sql = sql; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }
    
    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    
    public List<String> getParameters() { return parameters; }
    public void setParameters(List<String> parameters) { this.parameters = parameters; }
    
    public String getDatasource() { return datasource; }
    public void setDatasource(String datasource) { this.datasource = datasource; }
}