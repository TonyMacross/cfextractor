package net.gcae.utils.extractor.reporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
 * Generates Excel reports from analysis results
 */
public class ExcelReporter {
    private static final Logger logger = LoggerFactory.getLogger(ExcelReporter.class);
    
    private CellStyle headerStyle;
    private CellStyle dataStyle;
    
    public void generateReport(AnalysisResult result, String outputPath) throws IOException {
        logger.info("Generating Excel report: {}", outputPath);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            createStyles(workbook);
            
            // Create sheets only if data exists
            if (!result.getFiles().isEmpty()) {
                createFilesSheet(workbook, result.getFiles());
            } else {
                logger.info("No files found - skipping cfFilesReport sheet");
            }
            
            if (!result.getQueries().isEmpty()) {
                createQueriesSheet(workbook, result.getQueries());
            } else {
                logger.info("No queries found - skipping cfQueriesReport sheet");
            }
            
            if (!result.getFunctions().isEmpty()) {
                createFunctionsSheet(workbook, result.getFunctions());
            } else {
                logger.info("No functions found - skipping cfFunctionsReport sheet");
            }
            
            if (!result.getInvokes().isEmpty()) {
                createInvokesSheet(workbook, result.getInvokes());
            } else {
                logger.info("No invokes found - skipping cfInvokesReport sheet");
            }
            
            if (!result.getComponents().isEmpty()) {
                createComponentsSheet(workbook, result.getComponents());
            } else {
                logger.info("No components found - skipping cfComponentsReport sheet");
            }
            
            if (!result.getIncludes().isEmpty()) {
                createIncludesSheet(workbook, result.getIncludes());
            } else {
                logger.info("No includes found - skipping cfIncludesReport sheet");
            }
            
            if (!result.getModules().isEmpty()) {
                createModulesSheet(workbook, result.getModules());
            } else {
                logger.info("No modules found - skipping cfModulesReport sheet");
            }
            
            // Save the workbook
            try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
                workbook.write(fileOut);
            }
            
            logger.info("Excel report generated successfully with {} sheets", workbook.getNumberOfSheets());
        }
    }
    
    private void createStyles(Workbook workbook) {
        // Header style
        headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        
        // Data style
        dataStyle = workbook.createCellStyle();
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setWrapText(true);
        dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
    }
    
    private void createFilesSheet(Workbook workbook, List<FileInfo> files) {
        Sheet sheet = workbook.createSheet("cfFilesReport");
        
        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"File Name", "File Path", "File Type", "File Size (bytes)", "Line Count"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        for (FileInfo file : files) {
            Row row = sheet.createRow(rowNum++);
            
            createDataCell(row, 0, file.getFileName());
            createDataCell(row, 1, file.getFilePath());
            createDataCell(row, 2, file.getFileType());
            createDataCell(row, 3, String.valueOf(file.getFileSize()));
            createDataCell(row, 4, String.valueOf(file.getLineCount()));
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        logger.info("Created cfFilesReport sheet with {} files", files.size());
    }
    
    private void createQueriesSheet(Workbook workbook, List<QueryInfo> queries) {
        Sheet sheet = workbook.createSheet("cfQueriesReport");
        
        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Query Name", "DB Table", "File:Line", "Data Source", "SQL Query", "Complexity"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        for (QueryInfo query : queries) {
            Row row = sheet.createRow(rowNum++);
            
            createDataCell(row, 0, query.getQueryName());
            createDataCell(row, 1, query.getDbTable());
            createDataCell(row, 2, query.getFileLocation());
            createDataCell(row, 3, query.getDataSource());
            createDataCell(row, 4, query.getSqlQuery());
            createDataCell(row, 5, query.getComplexity());
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            if (i == 4) { // SQL Query column
                sheet.setColumnWidth(i, 15000); // Set wider width for SQL
            }
        }
        
        logger.info("Created cfQueriesReport sheet with {} queries", queries.size());
    }
    
    private void createFunctionsSheet(Workbook workbook, List<FunctionInfo> functions) {
        Sheet sheet = workbook.createSheet("cfFunctionsReport");
        
        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Function Name", "Return Type", "Access", "File:Line", "Parameters", "Used In"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        for (FunctionInfo function : functions) {
            Row row = sheet.createRow(rowNum++);
            
            createDataCell(row, 0, function.getFunctionName());
            createDataCell(row, 1, function.getReturnType());
            createDataCell(row, 2, function.getAccess());
            createDataCell(row, 3, function.getFileLocation());
            createDataCell(row, 4, function.getParameters());
            createDataCell(row, 5, function.getUsedIn());
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        logger.info("Created cfFunctionsReport sheet with {} functions", functions.size());
    }
    
    private void createInvokesSheet(Workbook workbook, List<InvokeInfo> invokes) {
        Sheet sheet = workbook.createSheet("cfInvokesReport");
        
        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Component", "Method", "File:Line", "Parameters"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        for (InvokeInfo invoke : invokes) {
            Row row = sheet.createRow(rowNum++);
            
            createDataCell(row, 0, invoke.getComponent());
            createDataCell(row, 1, invoke.getMethod());
            createDataCell(row, 2, invoke.getFileLocation());
            createDataCell(row, 3, invoke.getParameters());
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        logger.info("Created cfInvokesReport sheet with {} invokes", invokes.size());
    }
    
    private void createComponentsSheet(Workbook workbook, List<ComponentInfo> components) {
        Sheet sheet = workbook.createSheet("cfComponentsReport");
        
        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Component Name", "Extends", "File:Line", "Used In"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        for (ComponentInfo component : components) {
            Row row = sheet.createRow(rowNum++);
            
            createDataCell(row, 0, component.getComponentName());
            createDataCell(row, 1, component.getExtends());
            createDataCell(row, 2, component.getFileLocation());
            createDataCell(row, 3, component.getUsedIn());
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        logger.info("Created cfComponentsReport sheet with {} components", components.size());
    }
    
    private void createIncludesSheet(Workbook workbook, List<IncludeInfo> includes) {
        Sheet sheet = workbook.createSheet("cfIncludesReport");
        
        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Template", "File:Line"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        for (IncludeInfo include : includes) {
            Row row = sheet.createRow(rowNum++);
            
            createDataCell(row, 0, include.getTemplate());
            createDataCell(row, 1, include.getFileLocation());
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        logger.info("Created cfIncludesReport sheet with {} includes", includes.size());
    }
    
    private void createModulesSheet(Workbook workbook, List<ModuleInfo> modules) {
        Sheet sheet = workbook.createSheet("cfModulesReport");
        
        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Template", "File:Line", "Attributes"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        for (ModuleInfo module : modules) {
            Row row = sheet.createRow(rowNum++);
            
            createDataCell(row, 0, module.getTemplate());
            createDataCell(row, 1, module.getFileLocation());
            createDataCell(row, 2, module.getAttributes());
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        logger.info("Created cfModulesReport sheet with {} modules", modules.size());
    }
    
    private void createDataCell(Row row, int column, String value) {
        Cell cell = row.createCell(column);
        String cellValue = value != null ? value : "";
        
        // Excel cell limit is 32,767 characters
        if (cellValue.length() > 32767) {
            cellValue = cellValue.substring(0, 32764) + "...";
        }
        
        cell.setCellValue(cellValue);
        cell.setCellStyle(dataStyle);
    }
}