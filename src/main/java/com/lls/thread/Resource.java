package com.lls.thread;

import java.util.List;
import java.util.Map;

/**
 * ES 传输数据
 */
public class Resource {

    /**
     * 索引
     */
    private String index;

    /**
     * 类型
     */
    private String type;

    /**
     *文档数据
     */
    private List<Map> document;


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Map> getDocument() {
        return document;
    }

    public void setDocument(List<Map> document) {
        this.document = document;
    }

    public Resource(String index, String type, List<Map> document) {
        this.index = index;
        this.type = type;
        this.document = document;
    }

    public Resource() {
    }
}
