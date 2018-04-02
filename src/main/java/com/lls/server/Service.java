package com.lls.server;

import com.lls.mapper.LegalMapping;
import com.lls.util.ElasticSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service(value = "baseService")
public class Service {

    final Integer size = 50000;

    @Autowired
    private  LegalMapping dao;

    @Autowired
    private ElasticSearchUtil util;


    public void init(final String index, final String type) throws Exception{

        new Thread(new Runnable() {
            @Override
            public void run(){

                try {

                    Integer count = dao.getCount();

                    System.out.println("数据库记录数: " +count);

                    System.out.println(" 总页数： "+  count/size);

                    for(int i=0;i<count/size; i++){

                        long time = System.currentTimeMillis();

                        System.out.println("数据库获取-------start ");

                        List<Map> resources = dao.getAll(size,i );

                        System.out.println("数据库获取-------end , 耗时：" + (System.currentTimeMillis()-time)/1000);

                        util.BulkProcessAdd(index, type,resources);
                    }

                    util.close();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


}
