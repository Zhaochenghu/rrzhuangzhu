package com.bxchongdian.model.response;

import com.google.gson.annotations.SerializedName;

/********************************
 * Created by lvshicheng on 2017/3/1.
 ********************************/
public class LoginResponse extends BaseResponse {

  public LoginResponse() {
    this.response = new Content();
  }

  @SerializedName("content")
  public Content response;

  public static class Content {
    public String token;
    public String user_id;
  }
}
