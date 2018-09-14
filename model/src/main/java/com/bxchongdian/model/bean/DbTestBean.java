package com.bxchongdian.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/********************************
 * Created by lvshicheng on 2017/2/13.
 ********************************/
@Entity
public class DbTestBean {

  /**
   * 用户ID 需要加密存储
   */
  private String uid;
  /**
   * 设备ID 需要加密储存
   */
  @Id
  private String devID;
  @Generated(hash = 151442319)
  public DbTestBean(String uid, String devID) {
      this.uid = uid;
      this.devID = devID;
  }
  @Generated(hash = 454659001)
  public DbTestBean() {
  }
  public String getUid() {
      return this.uid;
  }
  public void setUid(String uid) {
      this.uid = uid;
  }
  public String getDevID() {
      return this.devID;
  }
  public void setDevID(String devID) {
      this.devID = devID;
  }

}
