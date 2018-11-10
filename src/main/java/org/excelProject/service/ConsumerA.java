package org.excelProject.service;

import com.rabbitmq.client.Channel;
import org.excelProject.dao.ExcelMapper;
import org.excelProject.pojo.Excel;
import org.excelProject.util.SerListUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public class ConsumerA implements ChannelAwareMessageListener {
    @Autowired
    ExcelMapper mapper;
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        List list = SerListUtil.deserializeList(message.getBody(),Excel.class);
        try {
            mapper.insertList(list);
            System.out.println("消费者:"+list);
            //false 只确认接收当前的一个消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){

        }

    }

   /* public void Insert(String s){
        System.out.println("消费A:"+s);

    }*/

}
