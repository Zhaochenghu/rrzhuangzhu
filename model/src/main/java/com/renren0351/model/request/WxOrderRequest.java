package com.renren0351.model.request;

/********************************
 * Created by lvshicheng on 2017/6/29.
 *
 * 微信下单子
 ********************************/
public class WxOrderRequest extends SimpleRequest {

    public String token;
    public String body;
    public String spbill_create_ip;
    public String total_fee;
    public String trade_type;
}
