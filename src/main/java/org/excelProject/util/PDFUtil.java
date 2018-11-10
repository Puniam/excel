package org.excelProject.util;




import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.excelProject.pojo.ExcelDto;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

public class PDFUtil {
    public String start(File filePath, List list, HttpServletRequest req, HttpServletResponse resp) throws  Exception{
        Document document = new Document(
                PageSize.A4,
                25,
                25,
                25,
                25);

        if(!filePath.exists()) filePath.mkdir();
        String fileName = new Date().getTime()+".pdf";

        //文件全路径
        File file = new File(filePath+"/"+fileName);

        PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(file));
        document.open();
//设置标题
        BaseFont bfChinese = null;
        try {
            bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                    BaseFont.NOT_EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //定义中文字体大小
        Font f10 = new Font(bfChinese, 10, Font.NORMAL);
        Font f12 = new Font(bfChinese, 12, Font.NORMAL);
        Font f26 = new Font(bfChinese, 26, Font.NORMAL);//一号字体

        /*
         * 创建标题
         */
        Paragraph title1 = new Paragraph("表格信息", f26);
        title1.setAlignment(Element.ALIGN_CENTER);
        Chapter chapter1 = new Chapter(title1, 1);

        chapter1.setNumberDepth(0);

        Section section1 = chapter1;

        // 创建有8列的表格
        int colNumber = 8;
        PdfPTable t = new PdfPTable(colNumber);
        t.setSpacingBefore(25);//设置段落上空白
        t.setSpacingAfter(25);//设置段落上下空白
        t.setHorizontalAlignment(Element.ALIGN_CENTER);// 居左
        float[] cellsWidth = { 0.2f, 0.1355f, 0.2f, 0.3f, 0.2f, 0.2f , 0.145f ,0.2f}; // 定义表格的宽度
        t.setWidths(cellsWidth);// 单元格宽度
        t.setTotalWidth(500f);//表格的总宽度
        t.setWidthPercentage(100);// 表格的宽度百分比
//设置表头
        PdfPCell c1 = new PdfPCell(new Paragraph("thedate",f12));
        t.addCell(c1);
        PdfPCell c2 = new PdfPCell(new Paragraph("stockid",f12));
        t.addCell(c2);
        PdfPCell c3 = new PdfPCell(new Paragraph("openprice",f12));
        t.addCell(c3);
        PdfPCell c4 = new PdfPCell(new Paragraph("highprice",f12));
        t.addCell(c4);
        PdfPCell c5 = new PdfPCell(new Paragraph("lowprice",f12));
        t.addCell(c5);
        PdfPCell c6 = new PdfPCell(new Paragraph("closeprice",f12));
        t.addCell(c6);
        PdfPCell c7 = new PdfPCell(new Paragraph("matchqty",f12));
        t.addCell(c7);
        PdfPCell c8 = new PdfPCell(new Paragraph("matchvalue",f12));
        t.addCell(c8);

        //向表格类动态填充list集合数据
        for(int i=0;i<list.size();i++){
            ExcelDto excel = (ExcelDto) list.get(i);
            t.addCell(new Paragraph(excel.getThedate().toString(),f10));
            t.addCell(new Paragraph(excel.getStockid(),f10));
            t.addCell(new Paragraph(excel.getOpenprice().toString(),f10));
            t.addCell(new Paragraph(excel.getHighprice().toString(),f10));
            t.addCell(new Paragraph(excel.getLowprice().toString(),f10));
            t.addCell(new Paragraph(excel.getCloseprice().toString(),f10));
            t.addCell(new Paragraph(excel.getMatchqty().toString(),f10));
            t.addCell(new Paragraph(excel.getMatchvalue().toString(),f10));
        }
        section1.add(t);
        document.add(chapter1);
        document.close();

        /*InputStream fin = null;
        OutputStream out = null;
        *//*******pdf文件生成结束*********//*
        try {
            fin = new FileInputStream(file);
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/pdf");
            String filename = "**********";
// 设置浏览器以下载的方式处理该文件
            if (req.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                filename = URLEncoder.encode(fileName, "UTF-8");
            } else {
                filename = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            }
            //下载使用 删除可用（js2方法）直接在页面显示不下载
            resp.setHeader("Content-Disposition", "attachment; filename=" + filename );
            //resp.addHeader("Content-Disposition", "attachment;filename=" + filename);//这里填想要的文件格式
            //out = resp.getOutputStream();
            out=new BufferedOutputStream(resp.getOutputStream());

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
        }*/



            return fileName;

    }
}
