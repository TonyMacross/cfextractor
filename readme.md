# ColdFusion Code Extractor

A Java application designed to analyze ColdFusion applications and generate comprehensive Excel reports containing inventories of various ColdFusion elements.

## Overview

The ColdFusion Code Extractor is a Maven-based Java 11 application that scans ColdFusion application directories and extracts detailed information about:

- **Files**: Inventory of ColdFusion and HTML files with metadata
- **SQL Queries**: Detection and analysis of `<cfquery>` tags and SQL complexity
- **Functions**: Inventory of `<cffunction>` declarations and their usage
- **Invokes**: Detection of `<cfinvoke>` tags and their parameters
- **Components**: Inventory of `<cfcomponent>` declarations and usage
- **Includes**: Detection of `<cfinclude>` tags and templates
- **Modules**: Detection of `<cfmodule>` tags and their attributes

## Features

- **Comprehensive Analysis**: Scans all ColdFusion-related files (.cfm, .cfml, .cfc, .htm, .html)
- **SQL Complexity Assessment**: Analyzes SQL queries and categorizes them by complexity (Low, Medium, High)
- **Usage Tracking**: Tracks where functions and components are used across the application
- **Excel Reporting**: Generates detailed Excel reports with separate sheets for each element type
- **Encoding Safety**: Handles multiple file encodings to avoid MalformedInputException
- **Logging**: Comprehensive logging with both console and file output

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- ColdFusion application source code

## Installation

1. Clone or download the project
2. Navigate to the project directory
3. Build the project using Maven:

```bash
mvn clean compile
```

## Usage

### Building the JAR

```bash
mvn clean package
```

This will create a fat JAR file in the `target/` directory.

### Running the Application

```bash
java -jar target/ColdFusionExtractor-1.0.0.jar <source_directory> [output_directory]
```

**Parameters:**
- `<source_directory>`: Path to the ColdFusion application directory to analyze (required)
- `[output_directory]`: Path where the Excel report will be saved (optional, defaults to current directory)

### Example

```bash
java -jar target/ColdFusionExtractor-1.0.0.jar /path/to/coldfusion/app /path/to/reports
```

## Output

The application generates an Excel file named `OT_CodeAnalysis_<timestamp>.xlsx` containing up to 7 sheets:

### Excel Sheets

1. **cfFilesReport**: File inventory with metadata
   - File Name, File Path, File Type, File Size, Line Count

2. **cfQueriesReport**: SQL query analysis
   - Query Name, DB Table, File:Line, Data Source, SQL Query, Complexity

3. **cfFunctionsReport**: Function inventory and usage
   - Function Name, Return Type, Access, File:Line, Parameters, Used In

4. **cfInvokesReport**: Invoke tag inventory
   - Component, Method, File:Line, Parameters

5. **cfComponentsReport**: Component inventory and usage
   - Component Name, Extends, File:Line, Used In

6. **cfIncludesReport**: Include tag inventory
   - Template, File:Line

7. **cfModulesReport**: Module tag inventory
   - Template, File:Line, Attributes

**Note**: Sheets are only created when relevant elements are found. If no elements of a particular type are discovered, the corresponding sheet is omitted and a notification is logged.

## Query Complexity Analysis

The application analyzes SQL queries and assigns complexity ratings based on:

- **Low Complexity**: Simple queries with basic SELECT, INSERT, UPDATE, DELETE operations
- **Medium Complexity**: Queries with joins, subqueries, and multiple conditions
- **High Complexity**: Complex queries with multiple joins, subqueries, and advanced SQL features

## Logging

The application provides comprehensive logging:

- **Console Output**: Real-time progress and summary information
- **Log File**: Detailed log saved as `coldfusion-extractor.log`
- **Log Levels**: INFO level for normal operation, WARN for non-critical issues

## Dependencies

- **Apache POI**: Excel file generation
- **JSoup**: HTML/XML parsing for ColdFusion tags
- **Apache Commons IO**: File operations
- **Apache Commons Lang**: Utility functions
- **SLF4J + Logback**: Logging framework

## Architecture

The application follows a clean architecture pattern:

- **Main Class**: `ColdFusionExtractor` - Entry point and orchestration
- **Analyzer**: `FileAnalyzer` - Core analysis logic
- **Models**: Data structures for different element types
- **Reporter**: `ExcelReporter` - Excel report generation

## Error Handling

- **File Encoding**: Attempts UTF-8 first, falls back to ISO-8859-1
- **Malformed Files**: Skips problematic files with warning logs
- **Missing Elements**: Gracefully handles missing or malformed ColdFusion tags

## Development

### Project Structure

```
src/main/java/net/gcae/utils/extractor/
├── ColdFusionExtractor.java      # Main application class
├── analyzer/
│   └── FileAnalyzer.java         # Core analysis logic
├── model/                        # Data models
│   ├── AnalysisResult.java
│   ├── FileInfo.java
│   ├── QueryInfo.java
│   ├── FunctionInfo.java
│   ├── InvokeInfo.java
│   ├── ComponentInfo.java
│   ├── IncludeInfo.java
│   └── ModuleInfo.java
└── reporter/
    └── ExcelReporter.java        # Excel generation
```

### Building from Source

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package JAR
mvn package

# Generate documentation
mvn javadoc:javadoc
```

## Troubleshooting

### Common Issues

1. **MalformedInputException**: The application handles multiple encodings automatically
2. **OutOfMemoryError**: Increase JVM heap size for large applications:
   ```bash
   java -Xmx2g -jar ColdFusionExtractor-1.0.0.jar
   ```
3. **Permission Errors**: Ensure read access to source directory and write access to output directory

### Performance Considerations

- Large applications may take several minutes to analyze
- Memory usage scales with application size
- Consider using SSD storage for better I/O performance

## License

This project is developed for internal use and analysis of ColdFusion applications.

## Support

For issues or questions, please check the application logs and ensure all requirements are met.