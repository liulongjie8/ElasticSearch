package com.lls.mapper;

public class Service {

    /**
     * 购买VIP
     */
    public  void buyVip(){
        Buy.buy("userId", "200", new WXpay());
    }

    public void buyDD(){
        Buy.buy("userId", "200", new ZFBpay());
    }
}
