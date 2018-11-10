package org.excelProject.controller;

import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.excelProject.pojo.ExcelDto;
import org.excelProject.service.ExcelService;
import org.excelProject.util.PDFUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ExcelController {
    @Autowired
    ExcelService excelService;
    @Autowired
    private AmqpTemplate template;
    @RequestMapping(value = "/test" ,method = RequestMethod.GET)
    public Object toDemo(){

        System.out.println("test");
        return "test";
    }
    @RequestMapping(value = "/rabbit")
    @ResponseBody
    public Object rabbitTest(){
        for (int i=0;i<30;i++)
        template.convertAndSend(String.valueOf(i));
        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "/data" , produces = "text/json;charset=UTF-8")
    public Object loadTable(Model model,HttpServletRequest request){
       /* Object size = request.getParameter("pageSize");
        //当前页数
        Object index= request.getParameter("pageIndex");
        Object endTime = request.getParameter("endTime");
        Object startTime = request.getParameter("beginTime");
        Object stockId = request.getParameter("stockId");*/
        List list = new ArrayList();
        for(int i=0;i<10;i++) {
            Map map = new HashMap();
            map.put("thedate", i);
            map.put("stockid", i);
            map.put("openprice", i);
            map.put("closeprice", i);
            map.put("highprice", i);
            map.put("lowprice", String.valueOf(i));
            map.put("matchqty", i);
            map.put("matchvalue", i);
            list.add(map);
        }


        model.addAttribute("data",list);
        model.addAttribute("total",200);
        Object size = request.getParameter("pageSize");
        //当前页数
        String index= request.getParameter("pageIndex");
        String endTime = request.getParameter("endTime");
        String beginTime = request.getParameter("beginTime");
        String stockId = request.getParameter("stockId");

        Map map = excelService.query(beginTime,endTime,stockId,index);
        String json = JSON.toJSONString(map);
        return json;

    }
    @RequestMapping(value = "/del"/*,produces = "text/json;charset=utf-8"*/)
    @ResponseBody
    public String delete(HttpServletRequest request){

        String endTime = request.getParameter("endTime");
        String beginTime = request.getParameter("beginTime");
        String stockId = request.getParameter("stockId");
        int count =excelService.delete(beginTime,endTime,stockId);

        return count>0?"success":"error";
    }

    @RequestMapping(value = "/update",produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object update(HttpServletRequest request){
        return null;
    }

    @RequestMapping(value = "/exportPdf")
    @ResponseBody
    public String pdf(HttpServletRequest request, HttpServletResponse response){
        File filePath =null;
        String endTime = request.getParameter("endTime");
        String beginTime = request.getParameter("beginTime");
        String stockId = request.getParameter("stockId");
        String fileName="";
        try {
            filePath = new File(ResourceUtils.getFile("classpath:").getPath(),"/export/");
            PDFUtil pdfUtil = new PDFUtil();

          fileName=  pdfUtil.start(filePath,excelService.queryList(beginTime, endTime,stockId),request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @RequestMapping(value = "/downloadPdf")
    public void downloadPdf(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String fileName = request.getParameter("fileName");
        File file=null;
        try {
            file= new File(ResourceUtils.getFile("classpath:").getPath(),"/export/"+fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
         InputStream fin = null;
        OutputStream out = null;

        try {
            fin = new FileInputStream(file);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/pdf");
            String filename = "";
// 设置浏览器以下载的方式处理该文件
            if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                filename = URLEncoder.encode(fileName, "UTF-8");
            } else {
                filename = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            }
            //下载使用 删除可用（js2方法）直接在页面显示不下载
            //response.setHeader("Content-Disposition", "attachment; filename=" + filename );
            //resp.addHeader("Content-Disposition", "attachment;filename=" + filename);//这里填想要的文件格式
            //out = resp.getOutputStream();
            out=new BufferedOutputStream(response.getOutputStream());

            byte[] buffer = new byte[512]; // 缓冲区
            int bytesToRead = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((bytesToRead = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } finally {
            if (fin != null)
                fin.close();
            if (out != null)
                out.close();
            if (file != null)
                file.delete();
        }

    }

    @RequestMapping(value = "/downloadExcel")
    public void exportExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String endTime = request.getParameter("endTime");
        String beginTime = request.getParameter("beginTime");
        String stockId = request.getParameter("stockId");

        List list = excelService.queryList(beginTime,endTime,stockId);



        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");


        String fileName = new Date().getTime()+".xlsx";
        String fileName_url=URLEncoder.encode(fileName,"UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName_url);

        //定义一个新的excel
        XSSFWorkbook wb = new XSSFWorkbook();
        // 第二步：创建一个Sheet页
        XSSFSheet sheet = wb.createSheet("startTimeendTime");
        sheet.setDefaultRowHeight((short) (2 * 256));//设置行高
        sheet.setColumnWidth(0, 5500);//设置列宽
        sheet.setColumnWidth(1,4500);
        sheet.setColumnWidth(2,4500);
        sheet.setColumnWidth(3,4500);
        sheet.setColumnWidth(4,4500);
        sheet.setColumnWidth(5,4500);
        sheet.setColumnWidth(6,5500);
        sheet.setColumnWidth(7,5500);
        XSSFFont font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 16);

        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("thedate ");
        cell = row.createCell(1);
        cell.setCellValue("stockid");
        cell = row.createCell(2);
        cell.setCellValue("openprice");
        cell = row.createCell(3);
        cell.setCellValue("highprice");
        cell = row.createCell(4);
        cell.setCellValue("lowprice");
        cell = row.createCell(5);
        cell.setCellValue("closeprice ");
        cell = row.createCell(6);
        cell.setCellValue("matchqty ");
        cell = row.createCell(7);
        cell.setCellValue("matchvalue ");

        XSSFRow rows;
        XSSFCell cells;
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        XSSFCellStyle style2 = wb.createCellStyle();
        style2.setAlignment(HorizontalAlignment.LEFT);
        DataFormat format = wb.createDataFormat();
        style2.setDataFormat(format.getFormat("000000"));
        for (int i = 0; i < list.size(); i++) {
            // 第三步：在这个sheet页里创建一行
            rows = sheet.createRow(i+1);

            // 第四步：在该行创建一个单元格
            cells = rows.createCell(0);
            //设置对齐
            cells.setCellStyle(style);
            // 第五步：在该单元格里设置值
            cells.setCellValue(((ExcelDto)list.get(i)).getThedate());
            cells = rows.createCell(1);
            cells.setCellStyle(style2);
            cells.setCellValue(((ExcelDto)list.get(i)).getStockid());
            cells = rows.createCell(2);
            cells.setCellStyle(style);
            cells.setCellValue(((ExcelDto)list.get(i)).getOpenprice().doubleValue());
            cells = rows.createCell(3);
            cells.setCellStyle(style);
            cells.setCellValue(((ExcelDto)list.get(i)).getHighprice().doubleValue());
            cells = rows.createCell(4);
            cells.setCellStyle(style);
            cells.setCellValue(((ExcelDto)list.get(i)).getLowprice().doubleValue());
            cells = rows.createCell(5);
            cells.setCellStyle(style);
            cells.setCellValue(((ExcelDto)list.get(i)).getCloseprice().doubleValue());
            cells = rows.createCell(6);
            cells.setCellStyle(style);
            cells.setCellValue(((ExcelDto)list.get(i)).getMatchqty().toString());
            cells = rows.createCell(7);
            cells.setCellStyle(style);
            cells.setCellValue(((ExcelDto)list.get(i)).getMatchvalue().toString());
        }

        try {
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
            wb.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @ResponseBody
    @RequestMapping(value = "/upload")
    public Object uploadFile(@RequestParam("file") MultipartFile file,
                             HttpServletRequest request){

        boolean isSuccess = false;

        String contentType = file.getContentType();
        //文件名
        String fileName = new Date().getTime()+file.getOriginalFilename();


        try{
            File path = new File(ResourceUtils.getFile("classpath:").getPath(),"/excel/");

            long start = System.currentTimeMillis();
            isSuccess = excelService.saveExcel(file.getBytes(),path,fileName);
            long end = System.currentTimeMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
            System.out.println(dateFormat.format(new Date(end-start)));




        }catch (IOException e){
            e.printStackTrace();
        }


        System.out.println("完成");
        return isSuccess;
    }

}
