package net.gcae.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gcae.utils.analyzer.ColdFusionAnalyzer;
import net.gcae.utils.model.AnalysisResult;
import net.gcae.utils.report.HTMLReportGenerator;

public class ColdFusionExtractor {
	private static final Logger logger = LoggerFactory.getLogger(ColdFusionExtractor.class);

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Uso: java -jar coldfusion-extractor.jar <directorio_aplicacion> [archivo_reporte]");
			System.err.println("Ejemplo: java -jar coldfusion-extractor.jar /path/to/cf/app report.html");
			System.exit(1);
		}

		String appDirectoryPath = args[0];
		String reportPath = args.length > 1 ? args[1] : "coldfusion-analysis-report.html";

		try {
			File appDirectory = new File(appDirectoryPath);
			if (!appDirectory.exists() || !appDirectory.isDirectory()) {
				System.err.println("Error: El directorio especificado no existe o no es válido: " + appDirectoryPath);
				System.exit(1);
			}

			logger.info("Iniciando análisis de aplicación ColdFusion en: {}", appDirectoryPath);

			ColdFusionAnalyzer analyzer = new ColdFusionAnalyzer();
			AnalysisResult result = analyzer.analyzeApplication(appDirectory);

			logger.info("Análisis completado. Generando reporte...");

			HTMLReportGenerator reportGenerator = new HTMLReportGenerator();
			Path reportFilePath = Paths.get(reportPath);
			reportGenerator.generateReports(result, reportFilePath);

			logger.info("Reporte generado exitosamente: {}", reportFilePath.toAbsolutePath());
			System.out.println("Análisis completado. Reporte generado en: " + reportFilePath.toAbsolutePath());

		} catch (Exception e) {
			logger.error("Error durante el análisis", e);
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}

}
