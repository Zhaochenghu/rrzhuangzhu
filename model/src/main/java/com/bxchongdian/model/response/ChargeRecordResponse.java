package com.bxchongdian.model.response;

import com.bxchongdian.model.bean.ChargeRecordBean;

import java.util.List;

/********************************
 * Created by lvshicheng on 2017/5/24.
 ********************************/
public class ChargeRecordResponse extends BaseResponse {

  public Content content;

  public class Content {
    /**
     * per_page : 20
     * total : 0
     * page : 1
     * items : []
     */

    public int per_page;
    public int total;
    public int page;

    public List<ChargeRecordBean> items;
  }
}
