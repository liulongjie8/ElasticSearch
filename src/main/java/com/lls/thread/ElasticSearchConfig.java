package com.lls.thread;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix="elasticsearch.ansy")
public class ElasticSearchConfig {
    //需要同步的SQL
    private Map<String, String> sqls;

    // 数据库读取线程数
    private int pThreadNum;

    // ES同步线程数
    private int cThreadNum;

    //数据库读取条数
    private int pageSize;

    //集群IP
    private List<Map<String,String>> ips;

    private String clusterName;


    public int getpThreadNum() {
        return pThreadNum;
    }

    public void setpThreadNum(int pThreadNum) {
        this.pThreadNum = pThreadNum;
    }

    public int getcThreadNum() {
        return cThreadNum;
    }

    public void setcThreadNum(int cThreadNum) {
        this.cThreadNum = cThreadNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Map<String, String> getSqls() {
        return sqls;
    }

    public void setSqls(Map<String, String> sqls) {
        this.sqls = sqls;
    }

    public List<Map<String, String>> getIps() {
        return ips;
    }

    public void setIps(List<Map<String, String>> ips) {
        this.ips = ips;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
