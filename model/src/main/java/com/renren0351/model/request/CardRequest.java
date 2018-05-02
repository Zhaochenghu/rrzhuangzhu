package com.renren0351.model.request;

/********************************
 * Created by lvshicheng on 2017/4/7.
 ********************************/
public class CardRequest extends SimpleRequest {

  /* 预留手机号 */
  public String phone;
  /* 卡号 */
  public String cardId;

  public CardRequest(String cardId) {
    this.cardId = cardId;
  }
}
