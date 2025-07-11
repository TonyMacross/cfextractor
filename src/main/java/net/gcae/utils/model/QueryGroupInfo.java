package net.gcae.utils.model;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;

public class QueryGroupInfo {
    private String queryName;
    private Set<String> fileNames;
    private String datasource;
    private String dbTable;
    private List<String> sqlExtracts;
    private int lineNumber;
    private int executionCount;
    private List<String> parameters;
    
    // Constructor
    public QueryGroupInfo() {
        this.executionCount = 1;
        this.fileNames = new LinkedHashSet<>();
        this.sqlExtracts = new ArrayList<>();
    }
    
    // Constructor from QueryInfo
    public QueryGroupInfo(QueryInfo query) {
        this.queryName = query.getQueryName();
        this.fileNames = new LinkedHashSet<>();
        this.fileNames.add(query.getFileName() + ":" + query.getLineNumber());
        this.datasource = query.getDatasource();
        this.dbTable = query.getDbTable();
        this.sqlExtracts = new ArrayList<>();
        this.sqlExtracts.add(query.getSql());
        this.lineNumber = query.getLineNumber();
        this.parameters = query.getParameters();
        this.executionCount = 1;
    }
    
    // Method to create a grouping key (without function and component)
    public String getGroupingKey() {
        String name = queryName != null ? queryName : "Unnamed";
        return name;
    }
    
    // Method to increment execution count and add file with line and SQL
    public void incrementExecutionCount(String fileName, int lineNumber, String sql) {
        this.executionCount++;
        this.fileNames.add(fileName + ":" + lineNumber);
        this.sqlExtracts.add(sql);
    }
    
    // Method to get formatted file names with line numbers
    public String getFormattedFileNames() {
        return String.join("<br>", fileNames);
    }
    
    // Method to get formatted SQL extracts
    public String getFormattedSqlExtracts() {
        return sqlExtracts.stream()
            .map(sql -> org.apache.commons.lang3.StringUtils.abbreviate(sql != null ? sql.trim() : "", 100))
            .collect(java.util.stream.Collectors.joining("<br><hr style='margin:5px 0;'><br>"));
    }
    
    // Getters y setters
    public String getQueryName() { return queryName; }
    public void setQueryName(String queryName) { this.queryName = queryName; }
    
    public Set<String> getFileNames() { return fileNames; }
    public void setFileNames(Set<String> fileNames) { this.fileNames = fileNames; }
        
    public String getDatasource() { return datasource; }
    public void setDatasource(String datasource) { this.datasource = datasource; }
    
    public String getDbTable() { return dbTable; }
    public void setDbTable(String dbTable) { this.dbTable = dbTable; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    
    public int getExecutionCount() { return executionCount; }
    public void setExecutionCount(int executionCount) { this.executionCount = executionCount; }
    
    public List<String> getParameters() { return parameters; }
    public void setParameters(List<String> parameters) { this.parameters = parameters; }
}