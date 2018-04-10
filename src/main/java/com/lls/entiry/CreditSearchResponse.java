package com.lls.entiry;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *  Search 返回格式统一
 */
public class CreditSearchResponse implements Serializable {

   // 响应状态
   private String code;

   // 响应提示
   private String  msg;

   //数据总条数
   private Long count;

   //响应数据
   private List<Map<String,Object>> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
