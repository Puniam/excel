package org.excelProject.service;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import org.excelProject.dao.ExcelMapper;
import org.excelProject.util.BigDataParseExcelTest;
import org.noggit.JSONUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {

    @Autowired
    ExcelMapper mapper;

    @Autowired
    AmqpTemplate template;

    public boolean saveExcel(byte[] file, File filePath, String fileName) {

        try {

            if (!filePath.exists()) filePath.mkdirs();
            FileOutputStream outputStream = new FileOutputStream(filePath + "/" + fileName);
            outputStream.write(file);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.insertProcess(mapper, filePath + "/" + fileName);
        return true;
    }

    public Map query(String beginTime,String endTime,String stockId,String pageNum){
        Map<String,Object> map = new HashMap();
        int b = formartTime(beginTime);
        int e=formartTime(endTime);
        int id=0;

        if(stockId!=null&&!stockId.equals("")) id=Integer.parseInt(stockId);
        List list  = mapper.query(b,e,id,Integer.valueOf(pageNum));
        int total = mapper.queryCount(b,e,id);
        map.put("data",list);
        map.put("total",total);
        return map;
    }

    public int delete(String beginTime,String endTime,String stockId){
        int b = formartTime(beginTime);
        int e=formartTime(endTime);
        int id=0;

        if(stockId!=null&&!stockId.equals("")) id=Integer.parseInt(stockId);
        return mapper.delete(b,e,id);
    }

    public List queryList(String beginTime,String endTime,String stockId){
        int b = formartTime(beginTime);
        int e=formartTime(endTime);
        int id=0;
        if(stockId!=null&&!stockId.equals("")) id=Integer.parseInt(stockId);
        return mapper.queryList(b,e,id);
    }

    private int formartTime(String time){
        if(time!=null&&!time.equals("")) {
            String[] strs = time.split("-");
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<strs.length;i++) sb.append(strs[i]);
            return Integer.parseInt(sb.toString());
        }else return 0;
    }


    private void insertProcess(ExcelMapper mapper, String path) {

        BigDataParseExcelTest test = new BigDataParseExcelTest(template);
        try {
            test.process(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
