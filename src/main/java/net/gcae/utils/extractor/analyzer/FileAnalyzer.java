package net.gcae.utils.extractor.analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gcae.utils.extractor.model.AnalysisResult;
import net.gcae.utils.extractor.model.ComponentInfo;
import net.gcae.utils.extractor.model.FileInfo;
import net.gcae.utils.extractor.model.FunctionInfo;
import net.gcae.utils.extractor.model.IncludeInfo;
import net.gcae.utils.extractor.model.InvokeInfo;
import net.gcae.utils.extractor.model.ModuleInfo;
import net.gcae.utils.extractor.model.QueryInfo;

/**
 * Analyzes ColdFusion files and extracts information
 */
public class FileAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(FileAnalyzer.class);
    
    private static final Set<String> CF_EXTENSIONS = Set.of(".cfm", ".cfml", ".cfc", ".htm", ".html");
    private static final Pattern CFQUERY_PATTERN = Pattern.compile("<cfquery[^>]*>.*?</cfquery>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern CFFUNCTION_PATTERN = Pattern.compile("<cffunction[^>]*>.*?</cffunction>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern CFINVOKE_PATTERN = Pattern.compile("<cfinvoke[^>]*/?[^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern CFCOMPONENT_PATTERN = Pattern.compile("<cfcomponent[^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern CFINCLUDE_PATTERN = Pattern.compile("<cfinclude[^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern CFMODULE_PATTERN = Pattern.compile("<cfmodule[^>]*>", Pattern.CASE_INSENSITIVE);
    
    private File rootDirectory;
    private Map<String, Set<String>> usageTracker = new HashMap<>();
    
    public AnalysisResult analyzeDirectory(File directory) throws IOException {
        logger.info("Analyzing directory: {}", directory.getAbsolutePath());
        
        this.rootDirectory = directory; // Store root directory for relative path calculation
        
        AnalysisResult result = new AnalysisResult();
        Collection<File> files = FileUtils.listFiles(directory, null, true);
        
        // Filter ColdFusion and HTML files
        List<File> cfFiles = files.stream()
            .filter(this::isColdFusionFile)
            .collect(Collectors.toList());
        
        logger.info("Found {} ColdFusion files to analyze", cfFiles.size());
        
        // First pass: collect all declarations
        for (File file : cfFiles) {
            try {
                analyzeFile(file, result);
            } catch (Exception e) {
                logger.warn("Error analyzing file {}: {}", file.getAbsolutePath(), e.getMessage());
            }
        }
        
        // Second pass: find usages
        for (File file : cfFiles) {
            try {
                findUsages(file, result);
            } catch (Exception e) {
                logger.warn("Error finding usages in file {}: {}", file.getAbsolutePath(), e.getMessage());
            }
        }
        
        return result;
    }
    
    private boolean isColdFusionFile(File file) {
        String fileName = file.getName().toLowerCase();
        return CF_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }
    
    private void analyzeFile(File file, AnalysisResult result) throws IOException {
        String content = readFileContent(file);
        if (content == null) return;
        
        String relativePath = getRelativePath(file);
        int lineCount = content.split("\n").length;
        
        // Add file info
        result.getFiles().add(new FileInfo(
            file.getName(),
            relativePath,
            getFileType(file),
            file.length(),
            lineCount
        ));
        
        // Analyze queries
        analyzeQueries(content, relativePath, result);
        
        // Analyze functions
        analyzeFunctions(content, relativePath, result);
        
        // Analyze invokes
        analyzeInvokes(content, relativePath, result);
        
        // Analyze components
        analyzeComponents(content, relativePath, result);
        
        // Analyze includes
        analyzeIncludes(content, relativePath, result);
        
        // Analyze modules
        analyzeModules(content, relativePath, result);
    }
    
    private String readFileContent(File file) {
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            try {
                return Files.readString(file.toPath(), StandardCharsets.ISO_8859_1);
            } catch (IOException e2) {
                logger.warn("Cannot read file {}: {}", file.getAbsolutePath(), e2.getMessage());
                return null;
            }
        }
    }
    
    private void analyzeQueries(String content, String filePath, AnalysisResult result) {
        Matcher matcher = CFQUERY_PATTERN.matcher(content);
        int lineOffset = 0;
        
        while (matcher.find()) {
            String queryBlock = matcher.group();
            int lineNumber = getLineNumber(content, matcher.start());
            
            try {
                Document doc = Jsoup.parse(queryBlock);
                Element queryElement = doc.select("cfquery").first();
                
                if (queryElement != null) {
                    String queryName = queryElement.attr("name");
                    String dataSource = queryElement.attr("datasource");
                    String sqlQuery = queryElement.html().trim();
                    
                    String dbTable = extractTableNames(sqlQuery);
                    String complexity = calculateComplexity(sqlQuery);
                    
                    result.getQueries().add(new QueryInfo(
                        queryName,
                        dbTable,
                        filePath + ":" + lineNumber,
                        dataSource,
                        sqlQuery,
                        complexity
                    ));
                }
            } catch (Exception e) {
                logger.warn("Error parsing query in {}: {}", filePath, e.getMessage());
            }
        }
    }
    
    private void analyzeFunctions(String content, String filePath, AnalysisResult result) {
        Matcher matcher = CFFUNCTION_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String functionBlock = matcher.group();
            int lineNumber = getLineNumber(content, matcher.start());
            
            try {
                Document doc = Jsoup.parse(functionBlock);
                Element functionElement = doc.select("cffunction").first();
                
                if (functionElement != null) {
                    String functionName = functionElement.attr("name");
                    String returnType = functionElement.attr("returntype");
                    String access = functionElement.attr("access");
                    
                    // Extract parameters
                    Elements params = doc.select("cfargument");
                    StringBuilder parameters = new StringBuilder();
                    for (Element param : params) {
                        if (parameters.length() > 0) parameters.append(", ");
                        parameters.append(param.attr("name"))
                                .append(":")
                                .append(param.attr("type"));
                    }
                    
                    result.getFunctions().add(new FunctionInfo(
                        functionName,
                        returnType,
                        access,
                        filePath + ":" + lineNumber,
                        parameters.toString(),
                        "" // Will be populated in second pass
                    ));
                    
                    // Track for usage finding
                    usageTracker.computeIfAbsent("functions", k -> new HashSet<>()).add(functionName);
                }
            } catch (Exception e) {
                logger.warn("Error parsing function in {}: {}", filePath, e.getMessage());
            }
        }
    }
    
    private void analyzeInvokes(String content, String filePath, AnalysisResult result) {
        Matcher matcher = CFINVOKE_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String invokeTag = matcher.group();
            int lineNumber = getLineNumber(content, matcher.start());
            
            try {
                Document doc = Jsoup.parse(invokeTag);
                Element invokeElement = doc.select("cfinvoke").first();
                
                if (invokeElement != null) {
                    String component = invokeElement.attr("component");
                    String method = invokeElement.attr("method");
                    
                    // Extract parameters
                    StringBuilder parameters = new StringBuilder();
                    for (Attribute attr : invokeElement.attributes().asList()) {
                        if (!attr.getKey().equals("component") && !attr.getKey().equals("method")) {
                            if (parameters.length() > 0) parameters.append(", ");
                            parameters.append(attr.getKey()).append("=").append(attr.getValue());
                        }
                    }
                    
                    result.getInvokes().add(new InvokeInfo(
                        component,
                        method,
                        filePath + ":" + lineNumber,
                        parameters.toString()
                    ));
                }
            } catch (Exception e) {
                logger.warn("Error parsing invoke in {}: {}", filePath, e.getMessage());
            }
        }
    }
    
    private void analyzeComponents(String content, String filePath, AnalysisResult result) {
        Matcher matcher = CFCOMPONENT_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String componentTag = matcher.group();
            int lineNumber = getLineNumber(content, matcher.start());
            
            try {
                Document doc = Jsoup.parse(componentTag);
                Element componentElement = doc.select("cfcomponent").first();
                
                if (componentElement != null) {
                    String componentName = extractComponentName(filePath);
                    String extends_ = componentElement.attr("extends");
                    
                    result.getComponents().add(new ComponentInfo(
                        componentName,
                        extends_,
                        filePath + ":" + lineNumber,
                        "" // Will be populated in second pass
                    ));
                    
                    // Track for usage finding
                    usageTracker.computeIfAbsent("components", k -> new HashSet<>()).add(componentName);
                }
            } catch (Exception e) {
                logger.warn("Error parsing component in {}: {}", filePath, e.getMessage());
            }
        }
    }
    
    private void analyzeIncludes(String content, String filePath, AnalysisResult result) {
        Matcher matcher = CFINCLUDE_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String includeTag = matcher.group();
            int lineNumber = getLineNumber(content, matcher.start());
            
            try {
                Document doc = Jsoup.parse(includeTag);
                Element includeElement = doc.select("cfinclude").first();
                
                if (includeElement != null) {
                    String template = includeElement.attr("template");
                    
                    result.getIncludes().add(new IncludeInfo(
                        template,
                        filePath + ":" + lineNumber
                    ));
                }
            } catch (Exception e) {
                logger.warn("Error parsing include in {}: {}", filePath, e.getMessage());
            }
        }
    }
    
    private void analyzeModules(String content, String filePath, AnalysisResult result) {
        Matcher matcher = CFMODULE_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String moduleTag = matcher.group();
            int lineNumber = getLineNumber(content, matcher.start());
            
            try {
                Document doc = Jsoup.parse(moduleTag);
                Element moduleElement = doc.select("cfmodule").first();
                
                if (moduleElement != null) {
                    String template = moduleElement.attr("template");
                    
                    // Extract attributes
                    StringBuilder attributes = new StringBuilder();
                    for (Attribute attr : moduleElement.attributes().asList()) {
                        if (!attr.getKey().equals("template")) {
                            if (attributes.length() > 0) attributes.append(", ");
                            attributes.append(attr.getKey()).append("=").append(attr.getValue());
                        }
                    }
                    
                    result.getModules().add(new ModuleInfo(
                        template,
                        filePath + ":" + lineNumber,
                        attributes.toString()
                    ));
                }
            } catch (Exception e) {
                logger.warn("Error parsing module in {}: {}", filePath, e.getMessage());
            }
        }
    }
    
    private void findUsages(File file, AnalysisResult result) throws IOException {
        String content = readFileContent(file);
        if (content == null) return;
        
        String relativePath = getRelativePath(file);
        
        // Find function usages
        Set<String> functions = usageTracker.get("functions");
        if (functions != null) {
            for (String functionName : functions) {
                if (content.contains(functionName)) {
                    updateFunctionUsage(result, functionName, relativePath);
                }
            }
        }
        
        // Find component usages
        Set<String> components = usageTracker.get("components");
        if (components != null) {
            for (String componentName : components) {
                if (content.contains(componentName)) {
                    updateComponentUsage(result, componentName, relativePath);
                }
            }
        }
    }
    
    private void updateFunctionUsage(AnalysisResult result, String functionName, String usedIn) {
        for (FunctionInfo function : result.getFunctions()) {
            if (function.getFunctionName().equals(functionName)) {
                String currentUsage = function.getUsedIn();
                if (StringUtils.isBlank(currentUsage)) {
                    function.setUsedIn(usedIn);
                } else if (!currentUsage.contains(usedIn)) {
                    function.setUsedIn(currentUsage + ", " + usedIn);
                }
            }
        }
    }
    
    private void updateComponentUsage(AnalysisResult result, String componentName, String usedIn) {
        for (ComponentInfo component : result.getComponents()) {
            if (component.getComponentName().equals(componentName)) {
                String currentUsage = component.getUsedIn();
                if (StringUtils.isBlank(currentUsage)) {
                    component.setUsedIn(usedIn);
                } else if (!currentUsage.contains(usedIn)) {
                    component.setUsedIn(currentUsage + ", " + usedIn);
                }
            }
        }
    }
    
    // Helper methods
    private String getRelativePath(File file) {
        String rootPath = rootDirectory.getAbsolutePath();
        String filePath = file.getAbsolutePath();
        
        if (filePath.startsWith(rootPath)) {
            String relativePath = filePath.substring(rootPath.length());
            // Remove leading slash if present
            if (relativePath.startsWith(File.separator)) {
                relativePath = relativePath.substring(1);
            }
            return relativePath.replace(File.separator, "/");
        }
        
        return file.getPath().replace(File.separator, "/");
    }
    
    private String getFileType(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".cfm") || name.endsWith(".cfml")) return "ColdFusion Template";
        if (name.endsWith(".cfc")) return "ColdFusion Component";
        if (name.endsWith(".htm") || name.endsWith(".html")) return "HTML";
        return "Unknown";
    }
    
    private int getLineNumber(String content, int position) {
        return content.substring(0, position).split("\n").length;
    }
    
    private String extractTableNames(String sqlQuery) {
        // Simple regex to extract table names from SQL
        Set<String> tables = new HashSet<>();
        Pattern fromPattern = Pattern.compile("FROM\\s+([\\w\\.]+)", Pattern.CASE_INSENSITIVE);
        Pattern joinPattern = Pattern.compile("JOIN\\s+([\\w\\.]+)", Pattern.CASE_INSENSITIVE);
        Pattern updatePattern = Pattern.compile("UPDATE\\s+([\\w\\.]+)", Pattern.CASE_INSENSITIVE);
        Pattern insertPattern = Pattern.compile("INSERT\\s+INTO\\s+([\\w\\.]+)", Pattern.CASE_INSENSITIVE);
        
        Matcher matcher = fromPattern.matcher(sqlQuery);
        while (matcher.find()) {
            tables.add(matcher.group(1));
        }
        
        matcher = joinPattern.matcher(sqlQuery);
        while (matcher.find()) {
            tables.add(matcher.group(1));
        }
        
        matcher = updatePattern.matcher(sqlQuery);
        while (matcher.find()) {
            tables.add(matcher.group(1));
        }
        
        matcher = insertPattern.matcher(sqlQuery);
        while (matcher.find()) {
            tables.add(matcher.group(1));
        }
        
        return String.join(", ", tables);
    }
    
    private String calculateComplexity(String sqlQuery) {
        int complexity = 0;
        String upperQuery = sqlQuery.toUpperCase();
        
        // Basic complexity factors
        if (upperQuery.contains("SELECT")) complexity += 1;
        if (upperQuery.contains("INSERT")) complexity += 1;
        if (upperQuery.contains("UPDATE")) complexity += 1;
        if (upperQuery.contains("DELETE")) complexity += 1;
        
        // Joins increase complexity
        complexity += countOccurrences(upperQuery, "JOIN");
        
        // Subqueries increase complexity
        complexity += countOccurrences(upperQuery, "SELECT") - 1; // Subtract main SELECT
        
        // Conditions increase complexity
        complexity += countOccurrences(upperQuery, "WHERE");
        complexity += countOccurrences(upperQuery, "HAVING");
        complexity += countOccurrences(upperQuery, "AND");
        complexity += countOccurrences(upperQuery, "OR");
        
        // Functions increase complexity
        complexity += countOccurrences(upperQuery, "GROUP BY");
        complexity += countOccurrences(upperQuery, "ORDER BY");
        
        // Categorize complexity
        if (complexity <= 5) return "Low";
        if (complexity <= 15) return "Medium";
        return "High";
    }
    
    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }
    
    private String extractComponentName(String filePath) {
        File file = new File(filePath);
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        return dotIndex > 0 ? name.substring(0, dotIndex) : name;
    }
}