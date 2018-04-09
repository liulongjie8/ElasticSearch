package com.lls.thread;

import com.lls.util.ElasticSearchUtil;
import com.lls.util.SpringUtil;

/**
 * 消费者
 */
public class Customer {


    private Storage storage;

    private ElasticSearchUtil util = SpringUtil.getBean(ElasticSearchUtil.class);


    public  Runnable getRunbale() {
        return new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        System.out.println(Thread.currentThread().getName());

                        Resource resource = storage.pop();
                        if("end".equals(resource.getIndex())){
                            util.recordPublisedTime();
                            break;
                        }
                        util.BulkProcessAdd(resource.getIndex(), resource.getType(), resource.getDocument());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public Customer( Storage storage) {
        this.storage = storage;
    }
}
