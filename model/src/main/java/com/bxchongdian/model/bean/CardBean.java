package com.bxchongdian.model.bean;

/********************************
 * Created by lvshicheng on 2017/5/23.
 ********************************/
public class CardBean {

  public CardBean(String cardId) {
    this.cardId = cardId;
  }

  /**
   * username : 000074
   * updateTime : 2017-05-23 15:33:32
   * cancelTime :
   * carId : 00074
   * userId : 114
   * cardState :
   * companyCode : 0029
   * activiteTime : 2017-01-18 21:25:00
   * remark :
   * createTime : 2017-04-22 12:37:21
   * substationId : 0029
   * s_id : 20000074
   * cardtype : general
   * disable : false
   * cardId : 0001002900000074
   * b_actived : 0
   * balance : 185
   * id : 3
   * physicalNumber : 23000074
   */

  public String  username;
  public String  updateTime;
  public String  cancelTime;
  public String  carId;
  public int     userId;
  public String  cardState;
  public String  companyCode;
  public String  activiteTime;
  public String  remark;
  public String  createTime;
  public String  substationId;
  public String  s_id;
  public String  cardtype;
  public boolean disable;
  public String  cardId;
  public String  b_actived;
  public int     balance;
  public int     id;
  public String  physicalNumber;

  public boolean isSelect;

  public String getCardtype() {
    if ("general".equals(cardtype)) {
      return "普通卡";
    }
    return "专用卡";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CardBean cardBean = (CardBean) o;

    return cardId != null ? cardId.equals(cardBean.cardId) : cardBean.cardId == null;

  }

  @Override
  public int hashCode() {
    return cardId != null ? cardId.hashCode() : 0;
  }
}
