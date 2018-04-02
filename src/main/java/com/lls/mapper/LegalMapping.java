package com.lls.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface LegalMapping {

    @Select("SELECT * FROM  " +
            "(  " +
            "SELECT A.*, ROWNUM RN  " +
            "FROM (SELECT t.* ,trunc(lat, 2)||','||trunc(lng, 2) as point FROM t_base_legal_identity t where t.is_peat=1 and t.code!=0 ) A  " +
            "WHERE ROWNUM <= #{size}" +
            ")  " +
            "WHERE RN >= #{page}")
    List<Map> getAll(@Param("size") int size, @Param("page") int page );

    @Select("select count(1) from t_base_legal_identity")
    Integer getCount();

}
