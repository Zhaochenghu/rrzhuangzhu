package com.renren0351.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public class StationStatusResponse extends BaseResponse {

  @SerializedName("content")
  public List<StationStatus> contentList;

  public class StationStatus {

    /**
     * cpinterfaceId : 01
     * totalTime : 3750
     * updateTime :
     * cpId : 00000053
     * outV : 0.00
     * s_id :
     * serviceFee : 31.25
     * cpType : ac
     * remark :
     * id :
     * switchState : 0
     * payAccount : 0001999900000001
     * workstate : 0005
     * chargegunstate : 1
     * outA : 0.00
     * soc : 78
     * outRelayState : 0
     * createTime :
     * electric : 36.46
     */

    public String cpinterfaceId;
    public long totalTime;
    public String updateTime;
    public String cpId;
    public String outV;
    public String s_id;
    public String serviceFee;
    public String cpType;
    public String remark;
    public String id;
    public String switchState;
    public String payAccount;
    public String workstate;
    public String chargegunstate;
    public String outA;
    public int    soc;
    public String outRelayState;
    public String createTime;
    public String electric;

    public String getPower(){
      if("AC".equals(cpType.toUpperCase())) {
        return "7KW";
      } else {
        return "30KW";
      }
    }
  }

}
