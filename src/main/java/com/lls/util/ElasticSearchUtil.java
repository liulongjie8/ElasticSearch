package com.lls.util;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/***
 * ES  业务处理服务类
 */
@Service
public class ElasticSearchUtil {

    /**
     * 初始化同步时间
     */
    public static final String PUBLISH_TIME="1997-04-08 00:00:00";

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
     * 创建一个索引
     * @param index  索引
     * @param type  索引类型
     * @param resource  索引内容
     * @return
     */
    public Boolean creatIndex(String index, String type,String id, Map resource){
        IndexResponse response = client.prepareIndex(index, type, id)
                .setSource(resource
                )
                .get();
        if(response.status()== RestStatus.OK){
             return  true;
        }else{
            return  false;
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
    public void BulkProcessAdd(String index, String type, List<Map<String,Object>> resources) throws  Exception {
        init();
        for(Map resource: resources){
            Map temp = transformUpperCase(resource);
            bulkProcessor.add(new IndexRequest(index, type ,temp.get("id").toString()).source(temp));
        }
    }


    private  Map<String, Object> transformUpperCase(Map<String, Object> orgMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (orgMap == null || orgMap.isEmpty()) {
            return resultMap;
        }

        Set<String> keySet = orgMap.keySet();
        for (String key : keySet) {
            String newKey = key.toLowerCase();
            newKey = newKey.replace("_", "");
            resultMap.put(newKey, orgMap.get(key));
        }

        return resultMap;
    }


    /**
     *记录同步时间
     */
    public  void  recordPublisedTime() throws Exception{
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        Map<String,String> map = new HashMap<String, String>();
        map.put("publishTime",dateString);
        if(!indexExists("publish")){ //如果记录时间索引不存在就创建
            creatIndex("publish","publish","1",map);
        }else{
            updateById("publish","publish","1",map);
        }
    }

    /**
     * 获取上一次同步时间
     * @return
     */
    public String getPublishTime(){
        if(indexExists("publish")){
            Map<String,Object> map = FindById("publish","publish","1");
            return map.get("publishTime").toString();
        }else{
           return PUBLISH_TIME;
        }
    }

    /**
     * 根据ID获取数据
     * @param index  索引名称
     * @param type   类型名称
     * @param id   文档ID
     * @return
     */
    public Map<String,Object> FindById(String index, String type, String id){
        GetResponse response =  this.client.prepareGet(index, type, id).get();
        return response.getSource();
    }



    /**
     * 判断某个索引是否存在
     * @param index 存在返回true  不存在返回false
     * @return
     */
    public boolean indexExists(String index){
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = this.client.admin().indices().exists(request).actionGet();
        if (response.isExists()) {
            return true;
        }
        return false;
    }


    /**
     * 根据ID更新数据
     * @param index  索引名称
     * @param type   类型名称
     * @param id
     * @param resource
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Boolean  updateById(String index, String type, String id,Map resource) throws ExecutionException, InterruptedException {
        UpdateRequest request =  new UpdateRequest();
        request.index(index);
        request.type(type);
        request.id(id);
        request.doc(resource);
        UpdateResponse response = this.client.update(request).get();
        if(response.status()== RestStatus.OK){
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     * @return
     */
   private BulkProcessor init ()throws  Exception{
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
             }).setBulkActions(7000) //多少请求执行一次
                     .setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB)) //多少数据刷新一次  5M数据刷新一次
                     .setFlushInterval(TimeValue.timeValueSeconds(10)) //固定5s必须刷新一次
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
   public void close() throws Exception {
       if(bulkProcessor!=null){
           bulkProcessor.awaitClose(10,TimeUnit.MICROSECONDS);
       }
   }


    /**
     * 根据多个ID获取文档数据
     * @param index  索引名称
     * @param type   类型名称
     * @param ids    文档ID集合
     * @return
     */
    public List<Map<String,Object>>  mulitGet(String index, String type, String... ids){
        MultiGetRequestBuilder builder =  this.client.prepareMultiGet();
        for(String id: ids){
            builder.add(index, type, id);
        }
        MultiGetResponse responses = builder.get();
        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        for(MultiGetItemResponse item: responses){
            GetResponse getResponse = item.getResponse();
            if(getResponse.isExists()){
                results.add(getResponse.getSource()); // 获取Json  String json = getResponse.getSourceAsString();
            }
        }
        return results;
    }
    /**
     * 根据ID删除数据
     * @param index  索引名称
     * @param type   类型名称
     * @param id     文档ID
     * @return
     */
    public  Boolean DeleteById(String index, String type, String id){
        DeleteResponse response =  this.client.prepareDelete(index, type, id).get();
        if(response.status()== RestStatus.OK){
            return true;
        }else{
            return false;
        }
    }


    /** 根据ID更新文档数据，如果没有则新增
     * @param index 索引名称
     * @param type  类型名称
     * @param id    文档ID
     * @return  true 表示成功  false 失败
     */
    public Boolean upsertById(String index, String type, String id, Map source) throws ExecutionException, InterruptedException {
        IndexRequest request = new IndexRequest(index, type, id).source(source);
        UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(source).upsert(request);
        UpdateResponse response=this.client.update(updateRequest).get();
        if(response.status()== RestStatus.OK){
            return true;
        }else{
            return false;
        }
    }
}
