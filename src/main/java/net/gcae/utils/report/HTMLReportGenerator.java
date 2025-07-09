package net.gcae.utils.report;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gcae.utils.model.*;

public class HTMLReportGenerator {
	private static final Logger logger = LoggerFactory.getLogger(HTMLReportGenerator.class);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
	private Properties htmlTemplates;

	public HTMLReportGenerator() {
		loadHtmlTemplates();
	}

	private void loadHtmlTemplates() {
		htmlTemplates = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("html-templates-en.properties")) {
			if (input == null) {
				throw new IOException("Could not find html-templates-en.properties file");
			}
			htmlTemplates.load(input);
			logger.debug("HTML templates loaded successfully");
		} catch (IOException e) {
			logger.error("Error loading HTML templates", e);
			throw new RuntimeException("Error loading HTML templates", e);
		}
	}

	private String getTemplate(String key) {
		String template = htmlTemplates.getProperty(key);
		if (template == null) {
			logger.warn("Template not found: {}", key);
			return "";
		}
		return template;
	}

	public void generateReports(AnalysisResult result, Path outputDirectory) throws IOException {
		logger.info("Generating HTML reports in: {}", outputDirectory);

		// Create output directory if it doesn't exist
		if (!Files.exists(outputDirectory)) {
			Files.createDirectories(outputDirectory);
		}

		int totalReports = 0;

		// Generate individual reports
		totalReports += generateFileReport(result, outputDirectory);
		totalReports += generateQueryReport(result, outputDirectory);
		totalReports += generateFunctionReport(result, outputDirectory);
		totalReports += generateInvokeReport(result, outputDirectory);
		totalReports += generateComponentReport(result, outputDirectory);
		totalReports += generateIncludeReport(result, outputDirectory);
		totalReports += generateModuleReport(result, outputDirectory);

		logger.info("Generated {} HTML reports successfully", totalReports);
	}

	private int generateFileReport(AnalysisResult result, Path outputDirectory) throws IOException {
		// Filter only ColdFusion files
		List<FileInfo> cfFiles = result.getFiles().stream().filter(file -> isColdFusionFile(file.getFileExtension()))
				.collect(Collectors.toList());

		if (cfFiles.isEmpty()) {
			logger.info("No ColdFusion files found - skipping cfFilesReport.html");
			return 0;
		}

		String reportContent = generateSingleReport("File Inventory Report", "ColdFusion Files Analysis", result,
				generateFileInventoryTable(cfFiles));

		Path reportPath = outputDirectory.resolve("cfFilesReport.html");
		Files.write(reportPath, reportContent.getBytes("UTF-8"));
		logger.info("Generated file report: {}", reportPath.getFileName());
		return 1;
	}

	private int generateQueryReport(AnalysisResult result, Path outputDirectory) throws IOException {
		if (result.getQueries().isEmpty()) {
			logger.info("No queries found - skipping cfQueriesReport.html");
			return 0;
		}

		String reportContent = generateSingleReport("SQL Queries Report", "ColdFusion Queries Analysis", result,
				generateQueryInventoryTable(result.getQueries()));

		Path reportPath = outputDirectory.resolve("cfQueriesReport.html");
		Files.write(reportPath, reportContent.getBytes("UTF-8"));
		logger.info("Generated queries report: {}", reportPath.getFileName());
		return 1;
	}

	private int generateFunctionReport(AnalysisResult result, Path outputDirectory) throws IOException {
		if (result.getFunctions().isEmpty()) {
			logger.info("No functions found - skipping cfFunctionsReport.html");
			return 0;
		}

		String reportContent = generateSingleReport("Functions Report", "ColdFusion Functions Analysis", result,
				generateFunctionInventoryTable(result.getFunctions()));

		Path reportPath = outputDirectory.resolve("cfFunctionsReport.html");
		Files.write(reportPath, reportContent.getBytes("UTF-8"));
		logger.info("Generated functions report: {}", reportPath.getFileName());
		return 1;
	}

	private int generateInvokeReport(AnalysisResult result, Path outputDirectory) throws IOException {
		if (result.getInvokes().isEmpty()) {
			logger.info("No invokes found - skipping cfInvokesReport.html");
			return 0;
		}

		String reportContent = generateSingleReport("Component Invocations Report", "ColdFusion Invokes Analysis",
				result, generateInvokeInventoryTable(result.getInvokes()));

		Path reportPath = outputDirectory.resolve("cfInvokesReport.html");
		Files.write(reportPath, reportContent.getBytes("UTF-8"));
		logger.info("Generated invokes report: {}", reportPath.getFileName());
		return 1;
	}

	private int generateComponentReport(AnalysisResult result, Path outputDirectory) throws IOException {
		if (result.getComponents().isEmpty()) {
			logger.info("No components found - skipping cfComponentsReport.html");
			return 0;
		}

		String reportContent = generateSingleReport("Components Report", "ColdFusion Components Analysis", result,
				generateComponentInventoryTable(result.getComponents()));

		Path reportPath = outputDirectory.resolve("cfComponentsReport.html");
		Files.write(reportPath, reportContent.getBytes("UTF-8"));
		logger.info("Generated components report: {}", reportPath.getFileName());
		return 1;
	}

	private int generateIncludeReport(AnalysisResult result, Path outputDirectory) throws IOException {
		if (result.getIncludes().isEmpty()) {
			logger.info("No includes found - skipping cfIncludesReport.html");
			return 0;
		}

		String reportContent = generateSingleReport("Includes Report", "ColdFusion Includes Analysis", result,
				generateIncludeInventoryTable(result.getIncludes()));

		Path reportPath = outputDirectory.resolve("cfIncludesReport.html");
		Files.write(reportPath, reportContent.getBytes("UTF-8"));
		logger.info("Generated includes report: {}", reportPath.getFileName());
		return 1;
	}

	private int generateModuleReport(AnalysisResult result, Path outputDirectory) throws IOException {
		if (result.getModules().isEmpty()) {
			logger.info("No modules found - skipping cfModulesReport.html");
			return 0;
		}

		String reportContent = generateSingleReport("Modules Report", "ColdFusion Modules Analysis", result,
				generateModuleInventoryTable(result.getModules()));

		Path reportPath = outputDirectory.resolve("cfModulesReport.html");
		Files.write(reportPath, reportContent.getBytes("UTF-8"));
		logger.info("Generated modules report: {}", reportPath.getFileName());
		return 1;
	}

	private boolean isColdFusionFile(String extension) {
		return "cfm".equalsIgnoreCase(extension) || "cfc".equalsIgnoreCase(extension)
				|| "cfml".equalsIgnoreCase(extension);
	}

	private String generateSingleReport(String title, String description, AnalysisResult result, String tableContent) {
		return getTemplate("html.header").replace("${title}", title).replace("${description}", description)
				.replace("${applicationPath}", result.getApplicationPath()).replace("${analysisDate}",
						result.getAnalysisDate().format(DATE_FORMATTER))
				+ tableContent + getTemplate("html.footer");
	}

	private String generateFileInventoryTable(List<FileInfo> files) {
		String fileRows = files.stream()
				.map(file -> getTemplate("files.row").replace("${relativePath}", file.getRelativePath())
						.replace("${extension}", file.getFileExtension().toUpperCase())
						.replace("${fileSize}", String.format("%,d", file.getFileSize()))
						.replace("${lineCount}", String.valueOf(file.getLineCount())).replace("${lastModified}",
								file.getLastModified() != null ? file.getLastModified().format(DATE_FORMATTER) : "N/A"))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.files").replace("${fileRows}", fileRows).replace("${totalFiles}",
				String.valueOf(files.size()));
	}

    private String generateQueryInventoryTable(List<QueryInfo> queries) {
        String queryRows = queries.stream()
            .map(query -> getTemplate("queries.row")
                .replace("${queryName}", StringUtils.defaultString(query.getQueryName(), "Unnamed"))
                .replace("${fileName}", query.getFileName())
                .replace("${lineNumber}", String.valueOf(query.getLineNumber()))
                .replace("${datasource}", StringUtils.defaultString(query.getDatasource(), "N/A"))
                .replace("${dbTable}", StringUtils.defaultString(query.getDbTable(), "Unknown"))
                .replace("${sqlPreview}", StringUtils.abbreviate(StringUtils.defaultString(query.getSql(), ""), 150))
                .replace("${functionName}", StringUtils.defaultString(query.getFunctionName(), "N/A"))
                .replace("${componentName}", StringUtils.defaultString(query.getComponentName(), "N/A")))
            .collect(Collectors.joining("\n"));
        
        return getTemplate("html.queries")
            .replace("${queryRows}", queryRows)
            .replace("${totalQueries}", String.valueOf(queries.size()));
    }

	private String generateFunctionInventoryTable(List<FunctionInfo> functions) {
		String functionRows = functions.stream()
				.map(function -> getTemplate("functions.row")
						.replace("${functionName}", StringUtils.defaultString(function.getFunctionName(), "Unnamed"))
						.replace("${fileName}", function.getFileName())
						.replace("${lineNumber}", String.valueOf(function.getLineNumber()))
						.replace("${access}", StringUtils.defaultString(function.getAccess(), "public"))
						.replace("${returnType}", StringUtils.defaultString(function.getReturnType(), "any"))
						.replace("${componentName}", StringUtils.defaultString(function.getComponentName(), "N/A"))
						.replace("${usedInFiles}",
								function.getUsedInFiles() != null
										? function.getUsedInFiles().stream().collect(Collectors.joining("<br>"))
										: "Not used"))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.functions").replace("${functionRows}", functionRows).replace("${totalFunctions}",
				String.valueOf(functions.size()));
	}

	private String generateInvokeInventoryTable(List<InvokeInfo> invokes) {
		String invokeRows = invokes.stream()
				.map(invoke -> getTemplate("invokes.row")
						.replace("${component}", StringUtils.defaultString(invoke.getComponent(), "N/A"))
						.replace("${method}", StringUtils.defaultString(invoke.getMethod(), "N/A"))
						.replace("${fileName}", invoke.getFileName())
						.replace("${lineNumber}", String.valueOf(invoke.getLineNumber())))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.invokes").replace("${invokeRows}", invokeRows).replace("${totalInvokes}",
				String.valueOf(invokes.size()));
	}

	private String generateComponentInventoryTable(List<ComponentInfo> components) {
		String componentRows = components.stream()
				.map(component -> getTemplate("components.row")
						.replace("${componentName}", StringUtils.defaultString(component.getComponentName(), "Unnamed"))
						.replace("${fileName}", component.getFileName())
						.replace("${lineNumber}", String.valueOf(component.getLineNumber()))
						.replace("${extends}", StringUtils.defaultString(component.getExtends_(), "N/A"))
						.replace("${implements}", StringUtils.defaultString(component.getImplements_(), "N/A"))
						.replace("${usedInFiles}",
								component.getUsedInFiles() != null
										? component.getUsedInFiles().stream().collect(Collectors.joining("<br>"))
										: "Not used"))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.components").replace("${componentRows}", componentRows).replace("${totalComponents}",
				String.valueOf(components.size()));
	}

	private String generateIncludeInventoryTable(List<IncludeInfo> includes) {
		String includeRows = includes.stream()
				.map(include -> getTemplate("includes.row")
						.replace("${template}", StringUtils.defaultString(include.getTemplate(), "N/A"))
						.replace("${fileName}", include.getFileName())
						.replace("${lineNumber}", String.valueOf(include.getLineNumber())))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.includes").replace("${includeRows}", includeRows).replace("${totalIncludes}",
				String.valueOf(includes.size()));
	}

	private String generateModuleInventoryTable(List<ModuleInfo> modules) {
		String moduleRows = modules.stream()
				.map(module -> getTemplate("modules.row")
						.replace("${template}", StringUtils.defaultString(module.getTemplate(), "N/A"))
						.replace("${name}", StringUtils.defaultString(module.getName(), "N/A"))
						.replace("${fileName}", module.getFileName())
						.replace("${lineNumber}", String.valueOf(module.getLineNumber())))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.modules").replace("${moduleRows}", moduleRows).replace("${totalModules}",
				String.valueOf(modules.size()));
	}

	public void generateReport(AnalysisResult result, Path outputPath) throws IOException {
		logger.info("Generando reporte HTML en: {}", outputPath);

		StringBuilder html = new StringBuilder();

		// HTML Header
		html.append(generateHeader(result));

		// Resumen ejecutivo
		html.append(generateExecutiveSummary(result));

		// Inventario de archivos
		html.append(generateFileInventory(result.getFiles()));

		// Inventario de queries
		html.append(generateQueryInventory(result.getQueries()));

		// Inventario de funciones
		html.append(generateFunctionInventory(result.getFunctions()));

		// Inventario de componentes
		html.append(generateComponentInventory(result.getComponents()));

		// Inventario de invokes
		html.append(generateInvokeInventory(result.getInvokes()));

		// Inventario de includes
		html.append(generateIncludeInventory(result.getIncludes()));

		// Inventario de modules
		html.append(generateModuleInventory(result.getModules()));

		// HTML Footer
		html.append(generateFooter());

		// Escribir archivo
		Files.write(outputPath, html.toString().getBytes("UTF-8"));
		logger.info("Reporte HTML generado exitosamente");
	}

	private String generateHeader(AnalysisResult result) {
		String template = getTemplate("html.header");
		return template.replace("${applicationPath}", result.getApplicationPath()).replace("${analysisDate}",
				result.getAnalysisDate().format(DATE_FORMATTER));
	}

	private String generateExecutiveSummary(AnalysisResult result) {
		Map<String, Long> filesByExtension = result.getFiles().stream()
				.collect(Collectors.groupingBy(FileInfo::getFileExtension, Collectors.counting()));

		String extensionRows = filesByExtension.entrySet().stream()
				.map(entry -> getTemplate("summary.extension.row").replace("${extension}", entry.getKey().toUpperCase())
						.replace("${count}", entry.getValue().toString()).replace("${percentage}",
								String.format("%.1f%%", (entry.getValue() * 100.0 / result.getFiles().size()))))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.summary").replace("${totalFiles}", String.valueOf(result.getFiles().size()))
				.replace("${totalQueries}", String.valueOf(result.getQueries().size()))
				.replace("${totalFunctions}", String.valueOf(result.getFunctions().size()))
				.replace("${totalComponents}", String.valueOf(result.getComponents().size()))
				.replace("${totalInvokes}", String.valueOf(result.getInvokes().size()))
				.replace("${totalIncludes}", String.valueOf(result.getIncludes().size()))
				.replace("${extensionRows}", extensionRows);
	}

	private String generateFileInventory(List<FileInfo> files) {
		String fileRows = files.stream()
				.map(file -> getTemplate("files.row").replace("${relativePath}", file.getRelativePath())
						.replace("${extension}", file.getFileExtension().toUpperCase())
						.replace("${fileSize}", String.format("%,d", file.getFileSize()))
						.replace("${lineCount}", String.valueOf(file.getLineCount())).replace("${lastModified}",
								file.getLastModified() != null ? file.getLastModified().format(DATE_FORMATTER) : "N/A"))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.files").replace("${fileRows}", fileRows);
	}

	private String generateQueryInventory(List<QueryInfo> queries) {
		String queryRows = queries.stream().map(query -> getTemplate("queries.row")
				.replace("${queryName}", StringUtils.defaultString(query.getQueryName(), "Sin nombre"))
				.replace("${fileName}", query.getFileName())
				.replace("${lineNumber}", String.valueOf(query.getLineNumber()))
				.replace("${datasource}", StringUtils.defaultString(query.getDatasource(), "N/A"))
				.replace("${sqlPreview}", StringUtils.abbreviate(StringUtils.defaultString(query.getSql(), ""), 100)))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.queries").replace("${queryRows}", queryRows);
	}

	private String generateFunctionInventory(List<FunctionInfo> functions) {
		String functionRows = functions.stream()
				.map(function -> getTemplate("functions.row")
						.replace("${functionName}", StringUtils.defaultString(function.getFunctionName(), "Sin nombre"))
						.replace("${fileName}", function.getFileName())
						.replace("${lineNumber}", String.valueOf(function.getLineNumber()))
						.replace("${access}", StringUtils.defaultString(function.getAccess(), "public"))
						.replace("${returnType}", StringUtils.defaultString(function.getReturnType(), "any"))
						.replace("${usedInFiles}",
								function.getUsedInFiles() != null
										? function.getUsedInFiles().stream().collect(Collectors.joining("<br>"))
										: "No usado"))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.functions").replace("${functionRows}", functionRows);
	}

	private String generateComponentInventory(List<ComponentInfo> components) {
		String componentRows = components
				.stream().map(
						component -> getTemplate("components.row")
								.replace("${componentName}",
										StringUtils.defaultString(component.getComponentName(), "Sin nombre"))
								.replace("${fileName}", component.getFileName())
								.replace("${lineNumber}", String.valueOf(component.getLineNumber()))
								.replace("${extends}", StringUtils.defaultString(component.getExtends_(), "N/A"))
								.replace("${implements}", StringUtils.defaultString(component.getImplements_(), "N/A"))
								.replace("${usedInFiles}",
										component.getUsedInFiles() != null ? component.getUsedInFiles().stream()
												.collect(Collectors.joining("<br>")) : "No usado"))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.components").replace("${componentRows}", componentRows);
	}

	private String generateInvokeInventory(List<InvokeInfo> invokes) {
		String invokeRows = invokes.stream()
				.map(invoke -> getTemplate("invokes.row")
						.replace("${component}", StringUtils.defaultString(invoke.getComponent(), "N/A"))
						.replace("${method}", StringUtils.defaultString(invoke.getMethod(), "N/A"))
						.replace("${fileName}", invoke.getFileName())
						.replace("${lineNumber}", String.valueOf(invoke.getLineNumber())))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.invokes").replace("${invokeRows}", invokeRows);
	}

	private String generateIncludeInventory(List<IncludeInfo> includes) {
		String includeRows = includes.stream()
				.map(include -> getTemplate("includes.row")
						.replace("${template}", StringUtils.defaultString(include.getTemplate(), "N/A"))
						.replace("${fileName}", include.getFileName())
						.replace("${lineNumber}", String.valueOf(include.getLineNumber())))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.includes").replace("${includeRows}", includeRows);
	}

	private String generateModuleInventory(List<ModuleInfo> modules) {
		String moduleRows = modules.stream()
				.map(module -> getTemplate("modules.row")
						.replace("${template}", StringUtils.defaultString(module.getTemplate(), "N/A"))
						.replace("${name}", StringUtils.defaultString(module.getName(), "N/A"))
						.replace("${fileName}", module.getFileName())
						.replace("${lineNumber}", String.valueOf(module.getLineNumber())))
				.collect(Collectors.joining("\n"));

		return getTemplate("html.modules").replace("${moduleRows}", moduleRows);
	}

	private String generateFooter() {
		return getTemplate("html.footer");
	}

}
