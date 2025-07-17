package net.gcae.utils.extractor;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gcae.utils.extractor.analyzer.FileAnalyzer;
import net.gcae.utils.extractor.model.AnalysisResult;
import net.gcae.utils.extractor.reporter.ExcelReporter;


/**
 * Main class for ColdFusion code extraction and analysis
 */
public class ColdFusionExtractor {
    private static final Logger logger = LoggerFactory.getLogger(ColdFusionExtractor.class);
    
    public static void main(String[] args) {
        if (args.length < 1) {
            logger.error("Usage: java -jar ColdFusionExtractor.jar <source_directory> [output_directory]");
            System.exit(1);
        }
        
        String sourceDirectory = args[0];
        String outputDirectory = args.length > 1 ? args[1] : System.getProperty("user.dir");
        
        try {
            ColdFusionExtractor extractor = new ColdFusionExtractor();
            extractor.extractAndAnalyze(sourceDirectory, outputDirectory);
        } catch (Exception e) {
            logger.error("Error during extraction: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    public void extractAndAnalyze(String sourceDirectory, String outputDirectory) throws Exception {
        logger.info("Starting ColdFusion code analysis...");
        logger.info("Source directory: {}", sourceDirectory);
        logger.info("Output directory: {}", outputDirectory);
        
        File sourceDir = new File(sourceDirectory);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IllegalArgumentException("Source directory does not exist or is not a directory: " + sourceDirectory);
        }
        
        // Analyze files
        FileAnalyzer analyzer = new FileAnalyzer();
        AnalysisResult result = analyzer.analyzeDirectory(sourceDir);
        
        // Generate Excel report
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportFileName = "OT_CodeAnalysis_" + timestamp + ".xlsx";
        String reportPath = new File(outputDirectory, reportFileName).getAbsolutePath();
        
        ExcelReporter reporter = new ExcelReporter();
        reporter.generateReport(result, reportPath);
        
        logger.info("Analysis completed successfully!");
        logger.info("Report generated: {}", reportPath);
        
        // Print summary
        printSummary(result);
    }
    
    private void printSummary(AnalysisResult result) {
        logger.info("=== Analysis Summary ===");
        logger.info("Files analyzed: {}", result.getFiles().size());
        logger.info("Queries found: {}", result.getQueries().size());
        logger.info("Functions found: {}", result.getFunctions().size());
        logger.info("Invokes found: {}", result.getInvokes().size());
        logger.info("Components found: {}", result.getComponents().size());
        logger.info("Includes found: {}", result.getIncludes().size());
        logger.info("Modules found: {}", result.getModules().size());
    }
}