package com;

import com.lls.server.Service;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.Date;

/**
 * Hello world!
 */
@SpringBootApplication
@RestController
@MapperScan("com.lls.mapper")
public class Application {

    @Autowired
    private TransportClient client;

    @Autowired
    private Service service;

    @Autowired
    private com.lls.thread.Service threadService;

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("/init/{index}/{type}")
    public ResponseEntity init(@PathVariable String index, @PathVariable String type){
        try {
            service.init(index, type);
            return  new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return  new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/thread/{index}/{type}")
    public ResponseEntity thread(@PathVariable String index, @PathVariable String type){
        try {
            threadService.start(index, type);
            return  new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return  new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping("/get/book/noval")
    public ResponseEntity get(@RequestParam(name="id",defaultValue = "") String id){
        if(id.isEmpty()){
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        GetResponse response = this.client.prepareGet("book","noval",id).get();
        if(!response.isExists()){
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(response.getSource(), HttpStatus.OK);
    }


    @RequestMapping("/add/book/noval")
    public ResponseEntity add (
            @RequestParam(name = "title") String title,
            @RequestParam(name="author") String author,
            @RequestParam(name="word_count") int wordCount,
            @RequestParam(name="publish_data") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date publishDate
    ){
        try {
           XContentBuilder content= XContentFactory.jsonBuilder().startObject()
                    .field("title",title)
                    .field("author",author)
                    .field("word_count",wordCount)
                    .field("publish_data", publishDate.getTime()).endObject();
            IndexResponse result = this.client.prepareIndex("book","noval").setSource(content).get();
            return  new ResponseEntity(result.getId(),HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return  new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
