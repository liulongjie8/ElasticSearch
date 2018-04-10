package com.lls.commons;

import java.security.AllPermission;

public enum  SearchEnum {
    ALL("0") , //企业名称
    NAME("1") , //企业名称
    CREDITCODE("2"),//统一社会信用代码
    LEGALPERSON("3"),//企业法人
    ADDRESS("4"),//联系资质
    JYFW("5") ; // 经营范围

    private String value="";

    private SearchEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SearchEnum categoryOf(String value){
        switch (Integer.parseInt(value)) {
            case 1:
                return NAME;
            case 2:
                return CREDITCODE;
            case 3:
                return LEGALPERSON;
            case 4:
                return ADDRESS;
            case 5:
                return JYFW;
            default:
                return ALL;
        }
    }
}
