package org.excelProject.util;


import com.google.common.primitives.Bytes;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.excelProject.dao.ExcelMapper;
import org.excelProject.pojo.Excel;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BigDataParseExcelTest extends DefaultHandler {



    private SharedStringsTable sst;

    private String contents; //当前单元格的内容

    private int sheetIndex=-1;   //sheet的位置

    private boolean isSSTIndex = false; //判断单元格的内容是否是SST

    private String dimension; //有效数据区域

    private int longest;   //得到没行的长度

    private int curRow; //行位置

    private int curColIndex;//当前单元格位置

    private int preColIndex=0; //上一个单元格位置

    private List<String> currentRowList; //保存行数据

    private List<Excel> pojos=new ArrayList<Excel>();//保存批量的excelpojo对象

    private Iterator<InputStream> sheets; //sheets迭代器

    private InputSource sheetInputSource;

    private InputStream sheetInputStream;

    private ExcelMapper mapper;

    private AmqpTemplate template;

    private static Connection connection;
    private PreparedStatement preparedStatement;


    /*public static void main(String[] args) {

           connection=SqlUtil.getConn();




        long start = System.currentTimeMillis();

       Runnable runnable = new BigDataParseExcelTest();
       Thread thread1 = new Thread(runnable);
       Thread thread2 = new Thread(runnable);
       thread1.start();
       thread2.start();

       *//* BigDataParseExcelTest test = new BigDataParseExcelTest();
        String file = "D:\\1.XLSX";
        try {
            test.process(file);
        }catch (Exception e){

        }*//*
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

        long end = System.currentTimeMillis();
        System.out.println(dateFormat.format(new Date(end-start)));
    }

    @Override
    public void run()   {
        try {
            this.process("D:\\1.XLSX");
        }catch (Exception e){
            e.printStackTrace();
        }

    }*/

        public BigDataParseExcelTest(ExcelMapper mapper){
                this.mapper = mapper;
        }
        public BigDataParseExcelTest(AmqpTemplate template){this.template=template;}

    /**
     * 遍历excel表
     * @param file
     * @throws Exception
     */
    public void process(String file) throws Exception{
        //?
        OPCPackage pkg = OPCPackage.open(file);

        XSSFReader xssfReader = new XSSFReader(pkg);

        sst=xssfReader.getSharedStringsTable();

        XMLReader parser = XMLReaderFactory.createXMLReader();

        parser.setContentHandler(this);

        sheets=xssfReader.getSheetsData();

        //遍历sheet
        while(sheets.hasNext()){
            curRow = 0;

            sheetIndex++;

            sheetInputStream = sheets.next();

            sheetInputSource = new InputSource(sheetInputStream);

            parser.parse(sheetInputSource);

            sheetInputStream.close();

        }

    }


    @Override
    public  void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //super.startElement(uri, localName, qName, attributes);
        if(qName.equals("demension")){
            dimension = attributes.getValue("ref");
            longest = getCurColIndex(dimension.substring(dimension.indexOf(":")+1));

        }

        else if(qName.equals("row")){

            currentRowList = new ArrayList<>();

        }else if(qName.equals("c")){
            String cr = attributes.getValue("r");
            String ct = attributes.getValue("t");

            curColIndex = getCurColIndex(cr);  //当前列位置

            if(ct!=null&&ct.equals("s")){
                isSSTIndex = true;
            }else{
                isSSTIndex=false;
            }
        }
        //当前单元格 置空
        contents="";
    }

    @Override
    public void endDocument() throws SAXException {
        if(pojos.size()>0){
            //mapper.insertList(pojos);
            try {
                this.inser(pojos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pojos.clear();
        }
        System.out.println("结束************************************************");
    }

    @Override
    public  void endElement(String uri, String localName, String qName) throws SAXException {
        //super.endElement(uri, localName, qName);
        if(isSSTIndex){
            try {
                if(!contents.equals("")) {
                    int idx = Integer.parseInt(contents);
                    //System.out.println("idx="+idx);
                    contents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(qName.equals("v")){
            String value = contents.trim();
            value=value.equals("")?"":value;
            int cols = curColIndex-preColIndex;
           //空的列就补上空格
            if(cols>1) {
                for (int i = 0; i < cols-1; i++) {
                    currentRowList.add("");
                }
            }

            preColIndex = curColIndex;
            currentRowList.add(contents);
        }
        //到达行尾
        else if(qName.equals("row")){
            if(curColIndex<longest){
                for(int i=curColIndex+1;i<=longest;i++){
                    currentRowList.add("");
                }
            }
            if(curRow>=1) {
                this.format(currentRowList);//格式一下里面的数据
                /*System.out.println(currentRowList);*/

                //将行数据封装到实体对象
                Excel pojo = new Excel();
                pojo.setThedate(Integer.valueOf(currentRowList.get(0)));
                pojo.setStockid(Integer.valueOf(currentRowList.get(1)));
                pojo.setOpenprice(BigDecimal.valueOf(Double.valueOf(currentRowList.get(2))));
                pojo.setHighprice(BigDecimal.valueOf(Double.valueOf(currentRowList.get(3))));
                pojo.setLowprice(BigDecimal.valueOf(Double.valueOf(currentRowList.get(4))));
                pojo.setCloseprice(BigDecimal.valueOf(Double.valueOf(currentRowList.get(5))));
                pojo.setMatchqty(Long.valueOf(currentRowList.get(6)));
                pojo.setMatchvalue(Long.valueOf(currentRowList.get(7)));


                //批量插入一千条
                if (pojos.size() >= 1000) {

                    try {
                        inser(pojos);
                        System.out.println(qName.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    pojos.clear();
                } else {
                    pojos.add(pojo);
                }


            /*    try {

              *//* 插入操作*//*
               if(curRow>0)
                this.inser(currentRowList);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }
            //扔到队列 或者存到数据库
            currentRowList.clear();
            curRow++;
            curColIndex=0;
            preColIndex=0;
        }



    }

    @Override
    public  void characters(char[] ch, int start, int length) throws SAXException {
       // super.characters(ch, start, length);
        if(curRow>=1)
        contents+=new String(ch,start,length);
        //保存3位小数

    }


    public static int getCurColIndex(String rowId){
        int firstDigit = -1;
        for (int c = 0; c < rowId.length(); ++c) {
            if (Character.isDigit(rowId.charAt(c))) {
                firstDigit = c;
                break;
            }
        }
        //AB7-->AB
        //AB是列号, 7是行号
        String newRowId = rowId.substring(0,firstDigit);
        int num = 0;
        int result = 0;
        int length = newRowId.length();
        for(int i = 0; i < length; i++) {
            //先取最低位，B
            char ch = newRowId.charAt(length - i - 1);
            //B表示的十进制2，ascii码相减，以A的ascii码为基准，A表示1，B表示2
            num = (int)(ch - 'A' + 1) ;
            //列号转换相当于26进制数转10进制
            num *= Math.pow(26, i);
            result += num;
        }
        return result;

    }

    public void inser(List<Excel> list)throws Exception{
       /* if(curRow==0) return;
        this.format(currentRowList);
        String sql = "insert into excel " +
                "(STOCKID,THEDATE,OPENPRICE,HIGHPRICE,LOWPRICE,CLOSEPRICE,MATCHQTY,MATCHVALUE)" +
                "values(?,?,?,?,?,?,?,?)";



        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,currentRowList.get(0));
            preparedStatement.setString(2,currentRowList.get(1));
            preparedStatement.setString(3,currentRowList.get(2));
            preparedStatement.setString(4,currentRowList.get(3));
            preparedStatement.setString(5,currentRowList.get(4));
            preparedStatement.setString(6,currentRowList.get(5));
            preparedStatement.setString(7,currentRowList.get(6));
            preparedStatement.setString(8,currentRowList.get(7));
            preparedStatement.execute();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            preparedStatement.close();
        }
        System.out.println(currentRowList);*/
       //序列化
        byte[]  bytes = SerListUtil.serializeList(list);
        //发送消息
        template.convertAndSend(bytes);
       //mapper.insertList(list);
    }

    public void format(List rowList){

        for(int i = 2;i<6;i++){
            if(rowList.get(i).equals("")) rowList.set(i,0);
            double price = Double.valueOf((String)rowList.get(i));
            BigDecimal bigDecimal = new BigDecimal(price);
            price = bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();
            rowList.set(i,String.valueOf(price));
        }

    }



}
