package com.renren0351.model.response;

import com.google.gson.annotations.SerializedName;

/********************************
 * Created by lvshicheng on 2017/6/29.
 ********************************/
public class WxOrderResponse extends BaseResponse {

    public int errcode = -1;
    public String errmsg;

    @SerializedName("package")
    public String packageStr;
    public String timestamp;
    public String sign;
    public String out_trade_no;
    public String partnerid;
    public String appid;
    public String prepayid;
    public String noncestr;

    @Override
    public boolean isSuccess() {
        return errcode == 0;
    }
}
