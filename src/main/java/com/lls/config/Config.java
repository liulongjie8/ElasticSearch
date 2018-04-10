package com.lls.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * ElasticSearch 客户端初始化
 */
@Configuration
public class Config {

    @Autowired
    private ElasticSearchConfig config;

    @Bean
    public TransportClient client() throws UnknownHostException{
        Settings settings = Settings.builder().put("cluster.name","my-application").build();
        TransportClient client = new PreBuiltTransportClient(settings);
        for(Map<String,String> map: config.getIps()){
            InetSocketTransportAddress node = new InetSocketTransportAddress(
                    InetAddress.getByName(map.get("ip")), Integer.parseInt(map.get("port").toString())
            );
            client.addTransportAddress(node);
        }
        return  client;
    }
}
