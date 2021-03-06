package com.sdgt.medical.bi.jasper.reports;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ReportController {

    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestBody String payload)  {
        try {
            String report = request.getParameter("report");
            if(StringUtils.isEmpty(report)) throw new RuntimeException("no report found for "+report);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=\""+report+".pdf\""));
            response.setHeader("Accept-Ranges","bytes");
            OutputStream out = response.getOutputStream();
            Map<String, String[]> parameterMap = request.getParameterMap();
            Map<String,Object> stmap= new HashMap<>();
            parameterMap.entrySet().forEach(e ->{
                stmap.put(e.getKey(),e.getValue());
            });
            JasperUtils.exportAndWriteToOutPutStream(report, stmap,payload,out);
            response.flushBuffer();

        }catch (Exception e){
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
    }


}
