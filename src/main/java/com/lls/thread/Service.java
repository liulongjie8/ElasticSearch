package com.lls.thread;

import com.lls.util.ElasticSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ElasticSearchUtil util;

    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

    public void start() throws  Exception{
        ExecutorService service = Executors.newCachedThreadPool();
        Storage storage= new Storage();
        Map<String,String> sqls = elasticSearchConfig.getSqls();
        for(String key: sqls.keySet()){
            for(int i=0; i<elasticSearchConfig.getpThreadNum(); i++){
                service.submit(new Producer(key,key,storage,jdbcTemplate,sqls.get(key),util.getPublishTime(), elasticSearchConfig).getRunbale());
            }
        }
      for(int i=0; i<elasticSearchConfig.getcThreadNum(); i++)
           service.submit(new Customer(storage).getRunbale());
    }
}
