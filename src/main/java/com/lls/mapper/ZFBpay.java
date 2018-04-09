package com.lls.mapper;

/**
 * 支付宝
 */
public class ZFBpay extends  Pay {

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
