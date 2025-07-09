package net.gcae.utils.analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gcae.utils.model.AnalysisResult;
import net.gcae.utils.model.ComponentInfo;
import net.gcae.utils.model.FileInfo;
import net.gcae.utils.model.FunctionInfo;
import net.gcae.utils.model.IncludeInfo;
import net.gcae.utils.model.InvokeInfo;
import net.gcae.utils.model.ModuleInfo;
import net.gcae.utils.model.QueryInfo;

public class ColdFusionAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(ColdFusionAnalyzer.class);
    
    private static final Set<String> CF_EXTENSIONS = Set.of("cfm", "cfml", "cfc");
    // Pattern to capture more SQL content including multi-line queries
    private static final Pattern CFQUERY_PATTERN = Pattern.compile("<cfquery\\s+([^>]*)>([\\s\\S]*?)</cfquery>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern CFFUNCTION_PATTERN = Pattern.compile("<cffunction\\s+([^>]*)>", Pattern.CASE_INSENSITIVE);
    private static final Pattern CFCOMPONENT_PATTERN = Pattern.compile("<cfcomponent\\s+([^>]*)>", Pattern.CASE_INSENSITIVE);
    private static final Pattern CFINVOKE_PATTERN = Pattern.compile("<cfinvoke\\s+([^>]*)/?>");
    private static final Pattern CFINCLUDE_PATTERN = Pattern.compile("<cfinclude\\s+([^>]*)/?>");
    private static final Pattern CFMODULE_PATTERN = Pattern.compile("<cfmodule\\s+([^>]*)/?>");
    
    public AnalysisResult analyzeApplication(File appDirectory) throws IOException {
        logger.info("Analizando aplicación en: {}", appDirectory.getAbsolutePath());
        
        AnalysisResult result = new AnalysisResult();
        result.setApplicationPath(appDirectory.getAbsolutePath());
        result.setAnalysisDate(LocalDateTime.now());
        
        List<FileInfo> files = scanFiles(appDirectory);
        result.setFiles(files);
        
        result.setQueries(new ArrayList<>());
        result.setFunctions(new ArrayList<>());
        result.setComponents(new ArrayList<>());
        result.setInvokes(new ArrayList<>());
        result.setIncludes(new ArrayList<>());
        result.setModules(new ArrayList<>());
        
        for (FileInfo file : files) {
            if (CF_EXTENSIONS.contains(file.getFileExtension())) {
                analyzeFile(file, result);
            }
        }
        
        analyzeDependencies(result);
        
        logger.info("Análisis completado: {} archivos, {} queries, {} funciones, {} componentes", 
                   files.size(), result.getQueries().size(), result.getFunctions().size(), result.getComponents().size());
        
        return result;
    }
    
    private List<FileInfo> scanFiles(File directory) throws IOException {
        List<FileInfo> files = new ArrayList<>();
        Path basePath = directory.toPath();
        
        try (Stream<Path> paths = Files.walk(basePath)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> !isExcludedFile(path))
                 .forEach(path -> {
                     try {
                         FileInfo fileInfo = createFileInfo(path, basePath);
                         files.add(fileInfo);
                     } catch (IOException e) {
                         logger.warn("Error procesando archivo: {} - {}", path, e.getMessage());
                     }
                 });
        }
        
        return files;
    }
    
    /**
     * Verifica si un archivo debe ser excluido del análisis
     */
    private boolean isExcludedFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        String pathStr = path.toString().toLowerCase();
        
        // Excluir archivos binarios comunes
        if (fileName.endsWith(".exe") || fileName.endsWith(".dll") || 
            fileName.endsWith(".so") || fileName.endsWith(".dylib") ||
            fileName.endsWith(".jar") || fileName.endsWith(".war") ||
            fileName.endsWith(".zip") || fileName.endsWith(".rar") ||
            fileName.endsWith(".pdf") || fileName.endsWith(".doc") ||
            fileName.endsWith(".docx") || fileName.endsWith(".xls") ||
            fileName.endsWith(".xlsx") || fileName.endsWith(".ppt") ||
            fileName.endsWith(".pptx") || fileName.endsWith(".jpg") ||
            fileName.endsWith(".jpeg") || fileName.endsWith(".png") ||
            fileName.endsWith(".gif") || fileName.endsWith(".bmp") ||
            fileName.endsWith(".ico") || fileName.endsWith(".svg") ||
            fileName.endsWith(".mp3") || fileName.endsWith(".mp4") ||
            fileName.endsWith(".avi") || fileName.endsWith(".mov")) {
            return true;
        }
        
        // Excluir directorios comunes
        if (pathStr.contains("/.git/") || pathStr.contains("\\.git\\") ||
            pathStr.contains("/node_modules/") || pathStr.contains("\\node_modules\\") ||
            pathStr.contains("/target/") || pathStr.contains("\\target\\") ||
            pathStr.contains("/bin/") || pathStr.contains("\\bin\\") ||
            pathStr.contains("/obj/") || pathStr.contains("\\obj\\") ||
            pathStr.contains("/.svn/") || pathStr.contains("\\.svn\\") ||
            pathStr.contains("/logs/") || pathStr.contains("\\logs\\") ||
            pathStr.contains("/temp/") || pathStr.contains("\\temp\\") ||
            pathStr.contains("/cache/") || pathStr.contains("\\cache\\")) {
            return true;
        }
        
        // Excluir archivos de configuración IDE
        if (fileName.equals(".ds_store") || fileName.equals("thumbs.db") ||
            fileName.startsWith(".idea") || fileName.startsWith(".vscode") ||
            fileName.endsWith(".iml") || fileName.endsWith(".suo") ||
            fileName.endsWith(".user") || fileName.endsWith(".tmp") ||
            fileName.endsWith(".temp") || fileName.endsWith(".log") ||
            fileName.endsWith(".bak") || fileName.endsWith("~")) {
            return true;
        }
        
        return false;
    }
    
    private FileInfo createFileInfo(Path filePath, Path basePath) throws IOException {
        String relativePath = basePath.relativize(filePath).toString();
        FileInfo fileInfo = new FileInfo(filePath, relativePath);
        
        try {
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            fileInfo.setFileSize(attrs.size());
            fileInfo.setLastModified(LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault()));
            
            try {
                String content = readFileWithFallbackEncoding(filePath.toFile());
                long lineCount = content.split("\n").length;
                fileInfo.setLineCount((int) lineCount);
            } catch (IOException e) {
                logger.warn("No se pudo contar líneas en: {} - {}", filePath, e.getMessage());
                fileInfo.setLineCount(0);
            }
            
        } catch (IOException e) {
            logger.warn("Error procesando metadatos de archivo: {} - {}", filePath, e.getMessage());
            fileInfo.setFileSize(0);
            fileInfo.setLastModified(LocalDateTime.now());
            fileInfo.setLineCount(0);
        }
        
        return fileInfo;
    }
    
    private void analyzeFile(FileInfo fileInfo, AnalysisResult result) {
        try {
            String content = readFileWithFallbackEncoding(fileInfo.getFilePath().toFile());
            String fileName = fileInfo.getRelativePath();
            
            // Analizar queries
            analyzeQueries(content, fileName, result);
            
            // Analizar funciones
            analyzeFunctions(content, fileName, result);
            
            // Analizar componentes
            analyzeComponents(content, fileName, result);
            
            // Analizar invokes
            analyzeInvokes(content, fileName, result);
            
            // Analizar includes
            analyzeIncludes(content, fileName, result);
            
            // Analizar modules
            analyzeModules(content, fileName, result);
            
        } catch (IOException e) {
            logger.warn("Error analizando archivo: {}", fileInfo.getFilePath(), e);
        }
    }
    
    private void analyzeQueries(String content, String fileName, AnalysisResult result) {
        Matcher matcher = CFQUERY_PATTERN.matcher(content);
        while (matcher.find()) {
            QueryInfo query = new QueryInfo();
            query.setFileName(fileName);
            query.setLineNumber(getLineNumber(content, matcher.start()));
            
            String attributes = matcher.group(1);
            String sql = matcher.group(2).trim();
            
            query.setSql(sql);
            query.setQueryName(extractAttribute(attributes, "name"));
            query.setDatasource(extractAttribute(attributes, "datasource"));
            
            // Extract database table from SQL
            String dbTable = extractTableFromSQL(sql);
            query.setDbTable(dbTable);
            
            result.getQueries().add(query);
        }
    }
    
    /**
     * Extract table name from SQL query
     */
    private String extractTableFromSQL(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "N/A";
        }
        
        // Clean SQL - remove extra whitespace and normalize
        String cleanSql = sql.replaceAll("\\s+", " ").trim().toLowerCase();
        
        // Patterns to match different SQL constructs
        String[] patterns = {
            // SELECT FROM table
            "from\\s+([a-zA-Z_][a-zA-Z0-9_]*)",
            // INSERT INTO table
            "insert\\s+into\\s+([a-zA-Z_][a-zA-Z0-9_]*)",
            // UPDATE table
            "update\\s+([a-zA-Z_][a-zA-Z0-9_]*)",
            // DELETE FROM table
            "delete\\s+from\\s+([a-zA-Z_][a-zA-Z0-9_]*)"
        };
        
        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(cleanSql);
            if (matcher.find()) {
                String tableName = matcher.group(1);
                // Handle schema.table format
                if (tableName.contains(".")) {
                    String[] parts = tableName.split("\\.");
                    return parts[parts.length - 1]; // Return table name only
                }
                return tableName;
            }
        }
        
        // Try to extract from JOIN clauses
        Pattern joinPattern = Pattern.compile("join\\s+([a-zA-Z_][a-zA-Z0-9_]*)", Pattern.CASE_INSENSITIVE);
        Matcher joinMatcher = joinPattern.matcher(cleanSql);
        if (joinMatcher.find()) {
            String tableName = joinMatcher.group(1);
            if (tableName.contains(".")) {
                String[] parts = tableName.split("\\.");
                return parts[parts.length - 1];
            }
            return tableName;
        }
        
        return "Unknown";
    }
    
    private void analyzeFunctions(String content, String fileName, AnalysisResult result) {
        Matcher matcher = CFFUNCTION_PATTERN.matcher(content);
        while (matcher.find()) {
            FunctionInfo function = new FunctionInfo();
            function.setFileName(fileName);
            function.setLineNumber(getLineNumber(content, matcher.start()));
            
            String attributes = matcher.group(1);
            function.setFunctionName(extractAttribute(attributes, "name"));
            function.setAccess(extractAttribute(attributes, "access"));
            function.setReturnType(extractAttribute(attributes, "returntype"));
            
            function.setUsedInFiles(new ArrayList<>());
            
            result.getFunctions().add(function);
        }
    }
    
    private void analyzeComponents(String content, String fileName, AnalysisResult result) {
        Matcher matcher = CFCOMPONENT_PATTERN.matcher(content);
        while (matcher.find()) {
            ComponentInfo component = new ComponentInfo();
            component.setFileName(fileName);
            component.setLineNumber(getLineNumber(content, matcher.start()));
            
            String attributes = matcher.group(1);
            component.setComponentName(extractComponentName(fileName));
            component.setExtends_(extractAttribute(attributes, "extends"));
            component.setImplements_(extractAttribute(attributes, "implements"));
            
            component.setUsedInFiles(new ArrayList<>());
            component.setFunctions(new ArrayList<>());
            
            result.getComponents().add(component);
        }
    }
    
    private void analyzeInvokes(String content, String fileName, AnalysisResult result) {
        Matcher matcher = CFINVOKE_PATTERN.matcher(content);
        while (matcher.find()) {
            InvokeInfo invoke = new InvokeInfo();
            invoke.setFileName(fileName);
            invoke.setLineNumber(getLineNumber(content, matcher.start()));
            
            String attributes = matcher.group(1);
            invoke.setComponent(extractAttribute(attributes, "component"));
            invoke.setMethod(extractAttribute(attributes, "method"));
            
            result.getInvokes().add(invoke);
        }
    }
    
    private void analyzeIncludes(String content, String fileName, AnalysisResult result) {
        Matcher matcher = CFINCLUDE_PATTERN.matcher(content);
        while (matcher.find()) {
            IncludeInfo include = new IncludeInfo();
            include.setFileName(fileName);
            include.setLineNumber(getLineNumber(content, matcher.start()));
            
            String attributes = matcher.group(1);
            include.setTemplate(extractAttribute(attributes, "template"));
            
            result.getIncludes().add(include);
        }
    }
    
    private void analyzeModules(String content, String fileName, AnalysisResult result) {
        Matcher matcher = CFMODULE_PATTERN.matcher(content);
        while (matcher.find()) {
            ModuleInfo module = new ModuleInfo();
            module.setFileName(fileName);
            module.setLineNumber(getLineNumber(content, matcher.start()));
            
            String attributes = matcher.group(1);
            module.setTemplate(extractAttribute(attributes, "template"));
            module.setName(extractAttribute(attributes, "name"));
            
            result.getModules().add(module);
        }
    }
    
    private void analyzeDependencies(AnalysisResult result) {
        // Analizar donde se usan las funciones
        for (FunctionInfo function : result.getFunctions()) {
            for (FileInfo file : result.getFiles()) {
                if (CF_EXTENSIONS.contains(file.getFileExtension())) {
                    if (isFunctionUsedInFile(function, file)) {
                        function.getUsedInFiles().add(file.getRelativePath());
                    }
                }
            }
        }
        
        // Analizar donde se usan los componentes
        for (ComponentInfo component : result.getComponents()) {
            for (FileInfo file : result.getFiles()) {
                if (CF_EXTENSIONS.contains(file.getFileExtension())) {
                    if (isComponentUsedInFile(component, file)) {
                        component.getUsedInFiles().add(file.getRelativePath());
                    }
                }
            }
        }
    }
    
    private boolean isFunctionUsedInFile(FunctionInfo function, FileInfo file) {
        try {
            String content = readFileWithFallbackEncoding(file.getFilePath().toFile());
            String functionName = function.getFunctionName();
            if (functionName != null && !functionName.isEmpty()) {
                return content.contains(functionName + "(");
            }
        } catch (IOException e) {
            logger.warn("Error verificando uso de función en archivo: {} - {}", file.getFilePath(), e.getMessage());
        }
        return false;
    }
    
    private boolean isComponentUsedInFile(ComponentInfo component, FileInfo file) {
        try {
            String content = readFileWithFallbackEncoding(file.getFilePath().toFile());
            String componentName = component.getComponentName();
            if (componentName != null && !componentName.isEmpty()) {
                return content.contains(componentName) || 
                       content.contains("createObject(\"component\", \"" + componentName + "\")");
            }
        } catch (IOException e) {
            logger.warn("Error verificando uso de componente en archivo: {} - {}", file.getFilePath(), e.getMessage());
        }
        return false;
    }
    
    private int getLineNumber(String content, int position) {
        return (int) content.substring(0, position).chars().filter(ch -> ch == '\n').count() + 1;
    }
    
    private String extractAttribute(String attributes, String attributeName) {
        Pattern pattern = Pattern.compile(attributeName + "\\s*=\\s*[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(attributes);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    private String extractComponentName(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf('/') + 1);
        if (name.endsWith(".cfc")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    }
    
    /**
     * Lee un archivo con manejo de múltiples encodings como fallback
     */
    private String readFileWithFallbackEncoding(File file) throws IOException {
        // Lista de encodings a probar en orden
        String[] encodings = {
            "UTF-8",
            "ISO-8859-1",
            "Windows-1252",
            "UTF-16",
            "UTF-16BE",
            "UTF-16LE",
            "US-ASCII"
        };
        
        for (String encoding : encodings) {
            try {
                return FileUtils.readFileToString(file, encoding);
            } catch (IOException e) {
                logger.debug("No se pudo leer archivo {} con encoding {}: {}", 
                           file.getPath(), encoding, e.getMessage());
                // Continúa con el siguiente encoding
            }
        }
        
        // Si ningún encoding funciona, intenta leer como bytes y limpiar caracteres problemáticos
        logger.warn("No se pudo leer archivo {} con encodings estándar, usando lectura de bytes", file.getPath());
        try {
            byte[] bytes = FileUtils.readFileToByteArray(file);
            return cleanBytesToString(bytes);
        } catch (IOException e) {
            logger.error("Error crítico leyendo archivo {}: {}", file.getPath(), e.getMessage());
            throw new IOException("No se pudo leer el archivo: " + file.getPath(), e);
        }
    }
    
    /**
     * Convierte bytes a string limpiando caracteres problemáticos
     */
    private String cleanBytesToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // Convertir byte a char, reemplazando caracteres problemáticos
            char c = (char) (b & 0xFF);
            
            // Mantener solo caracteres imprimibles y algunos de control
            if (c >= 32 && c <= 126) {
                // ASCII imprimible
                sb.append(c);
            } else if (c == '\n' || c == '\r' || c == '\t') {
                // Caracteres de control permitidos
                sb.append(c);
            } else if (c >= 160 && c <= 255) {
                // Caracteres extendidos Latin-1
                sb.append(c);
            } else {
                // Reemplazar caracteres problemáticos con espacio
                sb.append(' ');
            }
        }
        return sb.toString();
    }
    
    /**
     * Detecta automáticamente el encoding de un archivo
     */
    private String detectFileEncoding(File file) {
        try {
            byte[] bytes = FileUtils.readFileToByteArray(file);
            
            // Detectar BOM (Byte Order Mark)
            if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                return "UTF-8";
            }
            if (bytes.length >= 2 && bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE) {
                return "UTF-16LE";
            }
            if (bytes.length >= 2 && bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) {
                return "UTF-16BE";
            }
            
            // Heurística simple para detectar UTF-8
            boolean possibleUTF8 = true;
            for (int i = 0; i < Math.min(bytes.length, 1000); i++) {
                if ((bytes[i] & 0xFF) > 127) {
                    possibleUTF8 = false;
                    break;
                }
            }
            
            if (possibleUTF8) {
                return "UTF-8";
            }
            
            // Default fallback
            return "ISO-8859-1";
            
        } catch (IOException e) {
            logger.debug("No se pudo detectar encoding para {}: {}", file.getPath(), e.getMessage());
            return "UTF-8"; // Default
        }
    }
}