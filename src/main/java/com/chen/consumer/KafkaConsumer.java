package com.chen.consumer;

import com.alibaba.fastjson.JSONObject;
import com.chen.common.Constant;
import com.chen.common.News;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Component
@AllArgsConstructor
@Slf4j
public class KafkaConsumer {
    /**
     * @deprecation 读取kafka 数据入库 Elasticsearch
     **/
    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.group}")
    public void getData(String message) {
        // 获取 { 的下标
        Integer index = getLastIndexOfChar(message);
        // 截取json前面的乱码
        String data = null;
        if (-1 == index) {
            data = message;
        } else {
            data = message.substring(index);
        }

        try {
            News news = JSONObject.parseObject(data, News.class);
            if (ObjectUtils.isEmpty(news)) {
                log.info("空数据",data);
            }
        } catch (Exception e) {
            log.error("josn 解析失败",data);
        }

    }

    /***
     *
     * @param message 截取kafka消息中的数据格式问题,返回 { 符号最后一个下标
     * @return
     ***/
    public  Integer getLastIndexOfChar(String message){
        int index = message.lastIndexOf(Constant.Bracket);
        if( index == -1){
            log.warn("没有找到字符串",message);
            return  -1 ;
        }
        return  index ;
    }


}