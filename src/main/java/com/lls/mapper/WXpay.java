package com.lls.mapper;

public class WXpay extends  Pay {


    /**
     * 预下单
     */
    private void payPerpare(){

    }


    @Override
    public void request(String userId, String moneys) {
        payPerpare();
        super.request(userId, moneys);
    }
}
