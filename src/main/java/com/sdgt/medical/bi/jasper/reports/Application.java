package com.sdgt.medical.bi.jasper.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
//@RestController
public class Application implements CommandLineRunner {
//	https://www.jackrutorial.com/2018/08/spring-boot-with-jasperreports-example.html
	@Autowired
	private ResourceLoader resourceLoader;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@RequestMapping(path = "/pdf", method = RequestMethod.GET)
//	public ModelAndView report() {
//
//
//		JasperReportsPdfView view = new JasperReportsPdfView();
//		view.setUrl("classpath:report2.jrxml");
//		view.setApplicationContext(appContext);
//
//		Map<String, Object> params = new HashMap<>();
//		params.put("datasource", carService.findAll());
//
//		return new ModelAndView(view, params);
//	}
	public void test(HttpServletRequest request, HttpServletResponse response){
		try {

			JasperPrint jasperPrint = null;

			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"users.pdf\""));

			OutputStream out = response.getOutputStream();
			jasperPrint = tt();
			JasperExportManager.exportReportToPdfStream(jasperPrint, out);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	public JasperPrint tt(){
		try {

		String path = resourceLoader.getResource("classpath:rpt_users.jrxml").getURI().getPath();


		JasperReport jasperReport2 = JasperCompileManager.compileReport(path);
			ByteArrayInputStream jsonDataStream = new ByteArrayInputStream("".getBytes());
			JsonDataSource ds = new JsonDataSource(resourceLoader.getResource("classpath:test.json").getInputStream());

		// Parameters for report
		Map<String, Object> parameters = new HashMap<String, Object>();

		JasperPrint print = JasperFillManager.fillReport(jasperReport2
				, parameters, ds);

		parameters.put("title", "Employee Report");
		parameters.put("minSalary", 15000.0);
		parameters.put("condition", " LAST_NAME ='Smith' ORDER BY FIRST_NAME");

		JasperReport jr =(JasperReport) JRLoader.loadObject(
					new File("employeeReport.jasper"));

		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setExporterInput(new SimpleExporterInput(print));
		exporter.setExporterOutput(
				new SimpleOutputStreamExporterOutput("employeeReport.pdf"));

		SimplePdfReportConfiguration reportConfig
				= new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig
				= new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("baeldung");
		exportConfig.setEncrypted(true);
		exportConfig.setAllowedPermissionsHint("PRINTING");

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		exporter.exportReport();

		return print;
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void export( HttpServletRequest request,HttpServletResponse response)  {
		try {
			String report = request.getParameter("report");
			System.out.println(report);
		compileReport("rpt_users");
		JasperPrint jasperPrint = null;

		response.setContentType("application/x-download");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"users.pdf\""));

		OutputStream out = response.getOutputStream();
		jasperPrint = exportToPdf("rpt_users",null);//userService.exportPdfFile();
		JasperExportManager.exportReportToPdfStream(jasperPrint, out);

		}catch (Exception e){
			e.printStackTrace();
			throw  new RuntimeException(e);
		}
	}

	public JasperPrint exportToPdf(String report,Map<String, Object> parameters) throws IOException, JRException, SQLException{


		report = getFileName("rpt_users","jasper");
		InputStream inputStream = resourceLoader.getResource("classpath:test.json").getInputStream();
//		InputStream in;
		StringBuilder readLine= new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		System.out.println("==========================================");
		String s = br.readLine();
		while ( s != null) {

			System.out.println(readLine);

			readLine.append(s);
			s=br.readLine();
		}

		String data = "\t\n" +
				"{\n" +
				"    \"id\":\"asdfs\",\n" +
				"    \"title\":\"sdfafsaf\",\n" +
				"    \"phone\": \"teseter\"\n" +
				"  },\n" +
				"\n" +
				"    {\n" +
				"      \"id\":\"sagaewhgew\",\n" +
				"      \"title\":\"3333333333\",\n" +
				"      \"phone\": \"44444444444\"\n" +
				"    }\n" +
				" ";
		ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(readLine.toString().getBytes());

		JsonDataSource ds = new JsonDataSource(jsonDataStream,"organizations");
//		ds.subDataSource();

		JasperReport jr =(JasperReport) JRLoader.loadObject(
				new File(report));

		JasperPrint print = JasperFillManager.fillReport(jr
				, parameters, ds);
		return print;
	}

	public JasperReport compileReport(String report) {
		try {
			String path = resourceLoader.getResource("classpath:"+getFileName(report,"jrxml")).getURI().getPath();
			JasperReport jasperReport2 = JasperCompileManager.compileReport(path);
			JRSaver.saveObject(jasperReport2, getFileName(report,"jasper"));
			System.out.println("suceeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
			return jasperReport2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getFileName(String report,String ext) {
		return report.contains("."+ext)?report:report+"."+ext;
	}

	private void json() throws JRException {
		ByteArrayInputStream jsonDataStream = new ByteArrayInputStream("".getBytes());
//		File file = new File("c:\myjson.json");
		JsonDataSource ds = new JsonDataSource(jsonDataStream);
	}


	@Override
	public void run(String... args) throws Exception {

	    String test ="{\n" +
                "\n" +
                "  \"name\":\"jawath\",\n" +
                "  \"id\":\"first\",\n" +
                "\n" +
                "  \"organizations\":[\n" +
                "\t\n" +
                "{\n" +
                "    \"id\":\"asdfs\",\n" +
                "    \"title\":\"sdfafsaf\",\n" +
                "    \"phone\": \"teseter\"\n" +
                "  },\n" +
                "\n" +
                "    {\n" +
                "      \"id\":\"sagaewhgew\",\n" +
                "      \"title\":\"3333333333\",\n" +
                "      \"phone\": \"44444444444\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
//		JasperUtils.compileReport("rpt_users");
//        JasperUtils.exportToPdf("rpt_users",null,test);
	}
}
