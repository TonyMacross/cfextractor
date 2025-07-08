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
    private static final Pattern CFQUERY_PATTERN = Pattern.compile("<cfquery\\s+([^>]*)>([\\s\\S]*?)</cfquery>", Pattern.CASE_INSENSITIVE);
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
        
        // Obtener todos los archivos
        List<FileInfo> files = scanFiles(appDirectory);
        result.setFiles(files);
        
        // Analizar contenido de archivos ColdFusion
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
        
        // Analizar dependencias
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
                 .forEach(path -> {
                     try {
                         FileInfo fileInfo = createFileInfo(path, basePath);
                         files.add(fileInfo);
                     } catch (IOException e) {
                         logger.warn("Error procesando archivo: {}", path, e);
                     }
                 });
        }
        
        return files;
    }
    
    private FileInfo createFileInfo(Path filePath, Path basePath) throws IOException {
        String relativePath = basePath.relativize(filePath).toString();
        FileInfo fileInfo = new FileInfo(filePath, relativePath);
        
        BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
        fileInfo.setFileSize(attrs.size());
        fileInfo.setLastModified(LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault()));
        
        // Contar líneas
        try {
            long lineCount = Files.lines(filePath).count();
            fileInfo.setLineCount((int) lineCount);
        } catch (IOException e) {
            logger.warn("No se pudo contar líneas en: {}", filePath, e);
            fileInfo.setLineCount(0);
        }
        
        return fileInfo;
    }
    
    private void analyzeFile(FileInfo fileInfo, AnalysisResult result) {
        try {
            String content = FileUtils.readFileToString(fileInfo.getFilePath().toFile(), "UTF-8");
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
            
            result.getQueries().add(query);
        }
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
            String content = FileUtils.readFileToString(file.getFilePath().toFile(), "UTF-8");
            String functionName = function.getFunctionName();
            if (functionName != null && !functionName.isEmpty()) {
                return content.contains(functionName + "(");
            }
        } catch (IOException e) {
            logger.warn("Error verificando uso de función en archivo: {}", file.getFilePath(), e);
        }
        return false;
    }
    
    private boolean isComponentUsedInFile(ComponentInfo component, FileInfo file) {
        try {
            String content = FileUtils.readFileToString(file.getFilePath().toFile(), "UTF-8");
            String componentName = component.getComponentName();
            if (componentName != null && !componentName.isEmpty()) {
                return content.contains(componentName) || 
                       content.contains("createObject(\"component\", \"" + componentName + "\")");
            }
        } catch (IOException e) {
            logger.warn("Error verificando uso de componente en archivo: {}", file.getFilePath(), e);
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
}