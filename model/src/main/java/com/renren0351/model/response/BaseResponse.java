package com.renren0351.model.response;

/********************************
 * Created by lvshicheng on 16/4/22.
 ********************************/
public abstract class BaseResponse {

  private static final int SUCCEED_CODE = 200;

  public int code = -1;

  public String msg;

  public boolean isSuccess() {
    return code == SUCCEED_CODE;
  }

  public void makeSuccess() {
    code = SUCCEED_CODE;
  }
}
