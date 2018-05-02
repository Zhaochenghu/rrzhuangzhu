package com.renren0351.model.request;

/********************************
 * Created by lvshicheng on 2017/3/1.
 ********************************/
public class LoginRequest extends SimpleRequest{

  public LoginRequest(String username, String password) {
    this.password = password;
    this.username = username;
  }

  public String username;
  public String password;
}
