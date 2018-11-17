package com.sdgt.medical.bi.jasper.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;

public class JasperUtils {

    public static void exportAndWriteToOutPutStream(String report, Map<String, Object> parameters,
                                                    String payload,OutputStream out){
        try {
            if(!new File(getFileName(report,"jasper")).exists()){
                JasperUtils.compileReport(report);
            }
            JasperPrint jasperPrint = exportToPdf(report,parameters,payload);//userService.exportPdfFile();
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        }catch (Exception e){
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
    }

    public static JasperPrint exportToPdf(String report, Map<String, Object> parameters,
                                          String payload) throws IOException, JRException, SQLException {


        report = getFileName(report,"jasper");
//        StringBuilder readLine = getStringBuilder();

        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(payload.getBytes());

        JsonDataSource ds = new JsonDataSource(jsonDataStream);
//		ds.subDataSource();

        JasperReport jr =(JasperReport) JRLoader.loadObject(
                new File(report));

        JasperPrint print = JasperFillManager.fillReport(jr
                , parameters, ds);
        return print;
    }

    private StringBuilder getStringBuilder() throws IOException {
        InputStream inputStream = new ClassPathResource("classpath:test.json").getInputStream();
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
        return readLine;
    }

    public static JasperReport compileReport(String report) {
        try {

            String path = new File( "reports/rpt_users.jrxml").getAbsolutePath();
            JasperReport jasperReport2 = JasperCompileManager.compileReport(path);
            JRSaver.saveObject(jasperReport2, new File( getFileName(report,"jasper")));
            System.out.println("suceeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            return jasperReport2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getFileName(String report,String ext) {
        String filesep = "reports" + File.separator;
        return  report.contains("."+ext)?filesep + report: filesep+report+"."+ext;
    }

}
