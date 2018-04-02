package com.lls.thread;


import com.lls.mapper.LegalMapping;
import com.lls.util.SpringUtil;

import java.util.List;
import java.util.Map;

public class Producer   {

    public static LegalMapping dao = SpringUtil.getBean(LegalMapping.class);;

    public static final  Integer size = 30000;

    public static  Runnable getRunbale(final String index, final String type) {
      return  new Runnable() {
           @Override
           public void run() {

               Integer count = dao.getCount();

               System.out.println("数据库记录数: " +count);

               System.out.println(" 总页数： "+  count/size);

               for(int i=0;i<count/size; i++){

                   long time = System.currentTimeMillis();

                   System.out.println("数据库获取-------start ");

                   List<Map> resources = dao.getAll(size,i );

                   System.out.println("数据库获取-------end , 耗时：" + (System.currentTimeMillis()-time)/1000);

                   Resource r = new Resource(index, type, resources);

                   SingleQueue.sharedQueue.add(r);
               }
               Resource r = new Resource("end", null, null);
               SingleQueue.sharedQueue.add(r);
           }
       };
    }
}
