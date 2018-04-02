package com.lls.util;

import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/***
 * ES  业务处理服务类
 */
@Service
public class ElasticSearchUtil {

    @Autowired
    private TransportClient client;

    private  BulkProcessor bulkProcessor;

    /**
     * 利用BulkRquest 插入单条数据
     * @param index  索引
     * @param type   类型
     * @param resource  数据
     * @return  true 表示插入陈功  false 表示插入失败
     */
    public Boolean BulkAddOne(String index, String type, String resource){

        BulkRequestBuilder builder = this.client.prepareBulk();

        builder.add(this.client.prepareIndex(index, type).setSource(resource));

        BulkResponse bulkResponse = builder.get();

        if(bulkResponse.hasFailures()){
             System.out.println(bulkResponse.buildFailureMessage());
             return  false;
        }else{
             return  true;
        }
    }


    /**
     * 利用BulkRquest 批量插入
     * @param index 索引
     * @param type  类型
     * @param resources  数据
     * @return  true 表示插入陈功  false 表示插入失败
     */
    public Boolean BulkAddMulit(String index, String type, List<String> resources){

        BulkRequestBuilder builder = this.client.prepareBulk();

        for(String resource: resources){
            builder.add(this.client.prepareIndex(index, type).setSource(resource));
        }

        BulkResponse bulkResponse = builder.get();

        if(bulkResponse.hasFailures()){
            System.out.println(bulkResponse.buildFailureMessage());
            return  false;
        }else{
            return  true;
        }
    }

    /**
     * 利用BulkProcess 批量插入
     * @param index 索引
     * @param type  类型
     * @param resources  数据
     * @return  true 表示插入陈功  false 表示插入失败
     */
    public void BulkProcessAdd(String index, String type, List<Map> resources) throws  InterruptedException {
        init();
        for(Map resource: resources){
            bulkProcessor.add(new IndexRequest(index, type).source(resource));
        }
    }

    /**
     *
     * @return
     */
   private BulkProcessor init(){
         if(bulkProcessor==null){
             bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
                 @Override
                 public void beforeBulk(long l, BulkRequest bulkRequest) {
                      System.out.println("开始同步----->");
                 }
                 @Override
                 public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                     System.out.println("本次同步数据花费： " + bulkResponse.getTookInMillis()/1000);
                     System.out.println("本次同步条数： "+bulkResponse.getItems().length);
                 }
                 @Override
                 public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                     System.out.println(throwable.getMessage());

                 }
             }).setBulkActions(10000) //多少请求执行一次
                     .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB)) //多少数据刷新一次  5M数据刷新一次
                     .setFlushInterval(TimeValue.timeValueSeconds(5)) //固定5s必须刷新一次
                     .setConcurrentRequests(1)// 并发请求数量, 0不并发, 1并发允许执行
                     .setBackoffPolicy(
                             BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))  // 设置退避, 100ms后执行, 最大请求3次
                     .build();
         }
           return bulkProcessor;
   }

    /**
     * 关闭bulkProcessor
     * @throws InterruptedException
     */
   public void close() throws InterruptedException {
       if(bulkProcessor!=null){
           bulkProcessor.awaitClose(10,TimeUnit.MICROSECONDS);
       }
   }





}
