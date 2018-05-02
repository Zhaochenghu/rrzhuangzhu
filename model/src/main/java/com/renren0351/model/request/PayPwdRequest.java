package com.renren0351.model.request;

/********************************
 * Created by lvshicheng on 2017/6/22.
 ********************************/
public class PayPwdRequest extends SimpleRequest {

    public String password;

    public String old_password;
    public String new_password;

    public String captcha;

    public PayPwdRequest() {
    }

    public PayPwdRequest(String password) {
        this.password = password;
    }

    public PayPwdRequest(String new_password, String captcha) {
        this.new_password = new_password;
        this.captcha = captcha;
    }
}
