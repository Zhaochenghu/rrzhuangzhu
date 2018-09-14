package com.bxchongdian.model.response;

import com.google.gson.annotations.SerializedName;

/********************************
 * Created by zhaochenghu on 2018/09/11.
 ********************************/
public class WxOrderQueryResponse extends BaseResponse {

    public int errcode = -1;
    public String errmsg;

    public String trade_state;
    public String trade_state_desc;
    @SerializedName("content")
    public pay pay;

    public class pay{
        /**
         * "result": 1
         */
        public int result;
    }
    @Override
    public boolean isSuccess() {
        return errcode == 0;
    }
}
