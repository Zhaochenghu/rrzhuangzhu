package com.bxchongdian.model.request;

/********************************
 * Created by lvshicheng on 2017/7/28.
 ********************************/
public class CheckErrorRequest extends SimpleRequest {

    public int msgType = 0;
    public String subType;
    public String substationId;
    public String cpId;
    public String phone;
    public String remark;
    // 多个图片，以逗号分隔开
    public String imgUrl;
}
