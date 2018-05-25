package com.renren0351.model.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public class StationDetailBean {

  /**
   * useable : 3
   * total : 10
   * chargingPileList : [{"cpinterfaceId":"00","state":"0001","id":191,"cpId":"00000191","cpType":"DC"},{"cpinterfaceId":"00","state":"0002","id":192,"cpId":"00000192","cpType":"DC"},{"cpinterfaceId":"02","state":"0002","id":193,"cpId":"00000193","cpType":"AC"},{"cpinterfaceId":"02","state":"0002","id":194,"cpId":"00000194","cpType":"AC"},{"cpinterfaceId":"02","state":"0003","id":195,"cpId":"00000195","cpType":"AC"},{"cpinterfaceId":"02","state":"0003","id":196,"cpId":"00000196","cpType":"AC"},{"cpinterfaceId":"02","state":"0003","id":197,"cpId":"00000197","cpType":"AC"},{"cpinterfaceId":"02","state":"0003","id":198,"cpId":"00000198","cpType":"AC"},{"cpinterfaceId":"02","state":"0004","id":199,"cpId":"00000199","cpType":"AC"},{"cpinterfaceId":"02","state":"0005","id":200,"cpId":"00000200","cpType":"AC"}]
   */

  public int useable;
  public int total;

  public List<ChargingPileListEntity> chargingPileList;

  public static class ChargingPileListEntity implements Serializable,Comparable<ChargingPileListEntity>{
    /**
     * cpinterfaceId : 00
     * state : 0001
     * id : 191
     * cpId : 00000191
     * cpType : DC
     */
    public String cpinterfaceId;
    public String state;
    public int    id;
    public String cpId;
    public String cpType;
    public String ratedPower;
    public String workState;

    public String getCpType() {
      if (LvTextUtil.isEmpty(cpType)) {
        return "--";
      }

      if ("AC".equals(cpType.toUpperCase())) {
        return "交流";
      } else {
        return "直流";
      }
    }

    public String getShortCode() {
      if (LvTextUtil.isEmpty(cpId)) {
        return "";
      }
      if (cpId.length() <= 3) {
        return cpId;
      }
      return cpId.substring(cpId.length() - 5, cpId.length()-1) + "号";
    }

    public String getPower() {
      if (LvTextUtil.isEmpty(cpType)) {
        return "--KW";
      }

      if ("AC".equals(cpType.toUpperCase())) {
        return "7KW";
      } else {
        return "30KW";
      }
    }

    public String getGunState(int gunId){
      if (LvTextUtil.isEmpty(workState)){
        return "离线";
      }
      String[] str = workState.split(",");
      if (gunId > str.length){
        return "离线";
      }
      String gunState = str[gunId - 1];
      if ("0001".equals(gunState)) {
        return "告警";
      } else if ("0002".equals(gunState)) {
        return "待机";
      } else if ("0003".equals(gunState)) {
        return "工作";
      } else if ("0004".equals(gunState)) {
        return "离线";
      } else if ("0005".equals(gunState)) {
        return "完成";
      } else {
        return "离线";
      }
    }

    //0001-告警 0002-待机 0003-工作 0004-离线 0005-完成
    public String getState() {
      if ("0001".equals(state)) {
        return "告警";
      } else if ("0002".equals(state)) {
        return "待机";
      } else if ("0003".equals(state)) {
        return "工作";
      } else if ("0004".equals(state)) {
        return "离线";
      } else if ("0005".equals(state)) {
        return "完成";
      } else {
        return "离线";
      }
    }
    //列表按编号排序
    @Override
    public int compareTo(@NonNull ChargingPileListEntity another) {
      return this.cpId.compareTo(another.cpId);
    }
  }
}
