package com.westcredit.modules.search.util;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ElasticSearch 客户端初始化
 */
@Configuration
public class Config {

    @Bean
    public TransportClient client() throws UnknownHostException{
        InetSocketTransportAddress node = new InetSocketTransportAddress(
                InetAddress.getByName("192.168.1.217"),9300
        );
        Settings settings = Settings.builder().put("cluster.name","my-application").build();
        TransportClient client = new PreBuiltTransportClient(settings);

        client.addTransportAddress(node);
        return  client;
    }
}
