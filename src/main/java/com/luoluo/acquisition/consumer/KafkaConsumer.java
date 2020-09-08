package com.luoluo.acquisition.consumer;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.luoluo.acquisition.common.Constant;
import com.luoluo.acquisition.model.po.News;
import com.luoluo.acquisition.utils.ElasticsearchUtil;
import com.luoluo.acquisition.utils.IdUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Component
@AllArgsConstructor
@Slf4j
public class KafkaConsumer {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private BulkProcessor bulkProcessor;

    public KafkaConsumer(){
        if(ObjectUtil.isEmpty(bulkProcessor)){
            bulkProcessor = getBulkProcessor(restHighLevelClient);
        }
    }
    /***
      * @author luoluo
      * @Description 读取kafka 数据入库 Elasticsearch
      * @Date  2020年9月8日22:34:00
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
            }else {
                Long id = IdUtils.getId() ;
                news.setId(id);
                news.setCreateTime(new Date());
                bulkProcessor.add(new IndexRequest("${kafka.topic}").id(String.valueOf(id)).source(news));
            }
        } catch (Exception e) {
            log.error("josn 解析失败",data);
        }
    }

    /***
     * @author luoluo
     * @Description 截取kafka消息中的数据格式问题,返回 { 符号最后一个下标
     * @Date  2020年9月8日22:34:00
     **/
    public  Integer getLastIndexOfChar(String message){
        int index = message.lastIndexOf(Constant.Bracket);
        if( index == -1){
            log.warn("没有找到字符串",message);
            return  -1 ;
        }
        return  index ;
    }

   /**
     * @author luoluo
     * @Description 创建bulkProcessor并初始化
     * @Date   2020年9月8日23:09:52
     **/
    public static BulkProcessor getBulkProcessor(RestHighLevelClient client) {
        BulkProcessor bulkProcessor = null;
        try {

            BulkProcessor.Listener listener = new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long executionId, BulkRequest request) {
                    log.info("Try to insert data number : " + request.numberOfActions());
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                    log.info("************** Success insert data number : " + request.numberOfActions() + " , id: "
                            + executionId);
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                    log.error("Bulk is unsuccess : " + failure + ", executionId: " + executionId);
                }
            };
            BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer = (request, bulkListener) -> client
                    .bulkAsync(request, RequestOptions.DEFAULT, bulkListener);
            //	bulkProcessor = BulkProcessor.builder(bulkConsumer, listener).build();
            BulkProcessor.Builder builder = BulkProcessor.builder(bulkConsumer, listener);
            builder.setBulkActions(10000);
            builder.setBulkSize(new ByteSizeValue(100L, ByteSizeUnit.MB));
            builder.setConcurrentRequests(10);
            builder.setFlushInterval(TimeValue.timeValueSeconds(100L));
            builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3));
            // 注意点：在这里感觉有点坑，官网样例并没有这一步，而笔者因一时粗心也没注意，在调试时注意看才发现，上面对builder设置的属性没有生效
            bulkProcessor = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                bulkProcessor.awaitClose(100L, TimeUnit.SECONDS);
                client.close();
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
        }
        return bulkProcessor;
    }
}