package com.renren0351.model.response;

/********************************
 * Created by lvshicheng on 2017/6/29.
 ********************************/
public class WxOrderQueryResponse extends BaseResponse {

    public int errcode = -1;
    public String errmsg;

    public String trade_state;
    public String trade_state_desc;

    @Override
    public boolean isSuccess() {
        return errcode == 0;
    }
}
