package com.lls.mapper;

public abstract  class Buy {

    /**
     * 购买
     * @param userId
     * @param moneys
     * @param pay
     */
    public static void  buy(String userId, String moneys,Pay pay){
       pay.request(userId, moneys);
    }

}
