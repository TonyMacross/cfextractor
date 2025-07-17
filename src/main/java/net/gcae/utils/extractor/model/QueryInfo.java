package net.gcae.utils.extractor.model;

/**
 * Information about a query
 */
public class QueryInfo {
    private String queryName;
    private String dbTable;
    private String fileLocation;
    private String dataSource;
    private String sqlQuery;
    private String complexity;
    
    public QueryInfo(String queryName, String dbTable, String fileLocation, String dataSource, String sqlQuery, String complexity) {
        this.queryName = queryName;
        this.dbTable = dbTable;
        this.fileLocation = fileLocation;
        this.dataSource = dataSource;
        this.sqlQuery = sqlQuery;
        this.complexity = complexity;
    }
    
    // Getters
    public String getQueryName() { return queryName; }
    public String getDbTable() { return dbTable; }
    public String getFileLocation() { return fileLocation; }
    public String getDataSource() { return dataSource; }
    public String getSqlQuery() { return sqlQuery; }
    public String getComplexity() { return complexity; }
}