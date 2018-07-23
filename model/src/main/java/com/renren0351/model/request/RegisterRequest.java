package com.renren0351.model.request;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class RegisterRequest {
    public String username;
    public String password;
    public String captcha;
    //注册添加运营商区分
    public String companyCode;
    public RegisterRequest(String username, String password, String captcha,String compayCode) {
        this.username = username;
        this.password = password;
        this.captcha = captcha;
        this.companyCode =  compayCode;
    }
}
