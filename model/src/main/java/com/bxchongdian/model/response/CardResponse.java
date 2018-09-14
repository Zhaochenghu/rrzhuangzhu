package com.bxchongdian.model.response;

import com.google.gson.annotations.SerializedName;
import com.bxchongdian.model.bean.CardBean;

/********************************
 * Created by lvshicheng on 2017/5/23.
 ********************************/
public class CardResponse extends BaseResponse {

  @SerializedName("content")
  public CardBean cardBean;
}
