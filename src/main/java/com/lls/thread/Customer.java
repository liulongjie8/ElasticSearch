package com.lls.thread;

import com.lls.util.ElasticSearchUtil;
import com.lls.util.SpringUtil;

public class Customer {
    private static  ElasticSearchUtil util = SpringUtil.getBean(ElasticSearchUtil.class);
    public static Runnable getRunbale(){
        return new Runnable() {
            @Override
            public void run() {
                Resource resource = new Resource();
                do {
                    try {
                        resource = SingleQueue.sharedQueue.take();
                        util.BulkProcessAdd(resource.getIndex(), resource.getType(), resource.getDocument());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while(!"end".equals(resource.getIndex()));
            }
        };
    }

}
