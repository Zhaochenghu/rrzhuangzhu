package com.bxchongdian.model.bean;

import java.io.Serializable;

import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2017/4/25.
 ********************************/
public class StationInfoBean implements Serializable{

  /**
   * cpinterfaceId : 00
   * updateTime : 2017-04-22 12:37:21
   * pistate : 0002
   * substationName : 河北众聚新能源开发有限公司充电总站
   * name :
   * cpId : 00000192
   * latitude : 36.064226
   * companyCode : 0020
   * createTime : 2017-04-22 12:37:21
   * cpType : dc
   * remark :
   * longitude : 103.823305
   * substationId : 0020
   * port : 2406
   * s_id : 30000192
   * disable : false
   * ip : 192.168.20.2
   * b_actived : 1
   * joinTime : 2016-08-12 00:00:00
   * ratedPower :
   * id : 192
   */
  public String  cpinterfaceId;
  public String  updateTime;
  public String  pistate;
  public String  substationName;
  public String  areaName;
  public String  name;
  public String  cpId;
  public String  latitude;
  public String  companyCode;
  public String  createTime;
  /* cpType: dc 直流;ac 交流 */
  public String  cpType;
  public String  remark;
  public String  longitude;
  //public String  substationId;
  public String  areaId;
  public String  port;
  public String  s_id;
  public boolean disable;
  public String  ip;
  public String  b_actived;
  public String  joinTime;
  public String  ratedPower;
  public int     id;

  public String getCpType() {
    if (LvTextUtil.isEmpty(cpType)) {
      return "";
    }
    
    if ("DC".equals(cpType.toUpperCase())) {
      return "直流";
    } else if ("AC".equals(cpType.toUpperCase())) {
      return "交流";
    } else {
      return "";
    }
  }
}
