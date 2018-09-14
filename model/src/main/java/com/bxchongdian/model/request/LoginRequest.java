package com.bxchongdian.model.request;

/********************************
 * Created by zhaochenghu on 2018/3/1.
 ********************************/
public class LoginRequest extends SimpleRequest{

  public LoginRequest(String username, String password,String companyCode) {
    this.password = password;
    this.username = username;
    this.companyCode=companyCode;
  }

  public String username;
  public String password;
  //登陆界面添加运营商区分
  public String companyCode;
}
