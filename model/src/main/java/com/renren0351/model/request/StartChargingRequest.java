package com.renren0351.model.request;

/********************************
 * Created by lvshicheng on 2017/4/25.
 ********************************/
public class StartChargingRequest extends SimpleRequest {

  public String substationId;
  public String cpId;
  public String cpinterfaceId;
  public String chargingMode;
  public float settingNumber;
  public String command;
}
