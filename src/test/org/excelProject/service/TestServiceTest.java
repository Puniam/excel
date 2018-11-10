package org.excelProject.service;

import org.excelProject.dao.ExcelMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-*.xml",
                        "classpath:rabbitmq.xml"})
public class TestServiceTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    AmqpTemplate template;
    @Autowired
    ExcelMapper mapper;
    @Test
    public void mqtest(){
        for(int i=0;i<30;i++) {
            template.convertAndSend(String.valueOf(i));
        }
        try {
            Thread.sleep(30000);
        }catch (Exception e){

        }

        System.out.println("发送完毕");
    }
    @Test
    public void testSql(){
        int startTime = 20180101;
        int endTiem=20180102;
        int pageNum=1;
       List list= mapper.query(startTime,endTiem,0,pageNum);
        System.out.println(list);
    }

}