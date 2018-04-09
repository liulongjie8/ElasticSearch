package com.lls.thread;

import org.springframework.jdbc.core.JdbcTemplate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产者
 */
public class Producer   {

    public static final  Integer size = 7000;

    private String index;

    private String type;

    private Storage storage;

    private String sql;

    private JdbcTemplate jdbcTemplate;

    private String publishTime;

    private ElasticSearchConfig config;


    public   Runnable getRunbale()  {
      return  new Runnable() {
           @Override
           public void run() {

               Integer size = config.getPageSize();
               SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               Integer count = 0;
               Date date = null;
               try {
                   date = formatter.parse(publishTime);
               } catch (ParseException e) {
                   e.printStackTrace();
               }
               try {

                    Date[] str= new Date[]{date};
                    count = jdbcTemplate.queryForObject("select count(1) from ("+sql+") t" ,str, Integer.class);
               }catch (Exception e){
                   e.printStackTrace();
               }
               System.out.println("数据库记录数: " +count);
               System.out.println(" 总页数： "+  count/size);
               for(int i=1;i<=((count/size)+1); i++){
                   long time = System.currentTimeMillis();
                   System.out.println("数据库获取-------start -----"+publishTime+"-----"+type+"----"+i*size);
                   String _sql="SELECT  * FROM  " +
                           "(  " +
                           "SELECT A.*, ROWNUM RN  " +
                           "FROM ("+sql
                               +" ) A  " +
                           "WHERE ROWNUM <= ? " +
                           ")  " +
                           "WHERE RN >= ? ";
                   List<Map<String,Object>> resources = null ;
                   try {



                       resources = jdbcTemplate.queryForList(_sql,date, size*i, (i-1)*size);
                   }catch (Exception e){
                       e.printStackTrace();
                   }

                   System.out.println("数据库获取-------end---"+resources.size()+" , 耗时：" + (System.currentTimeMillis()-time)/1000);

                   Resource r = new Resource(index, type, resources);
                   try {
                       storage.push(r);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
               Resource r = new Resource("end", null, null);
               try {
                   storage.push(r);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       };
    }

    public Producer(String index, String type, Storage storage,JdbcTemplate template ,String sql, String publishTime, ElasticSearchConfig config) {
        this.index = index;
        this.type = type;
        this.storage = storage;
        this.jdbcTemplate = template;
        this.sql = sql;
        this.publishTime = publishTime;
        this.config = config;
    }

}
