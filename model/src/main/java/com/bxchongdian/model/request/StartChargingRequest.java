package com.bxchongdian.model.request;

/********************************
 * Created by lvshicheng on 2017/4/25.
 * "cpinterfaceId": "0",
 "chargingMode": "00",
 "cpId": "8050100001000011",
 "substationId": "0021",
 "settingNumber": 0.0,
 "command": "0"
 ********************************/
public class StartChargingRequest extends SimpleRequest {

  //public String substationId;
  public String areaId;
  public String cpId;
  public String cpinterfaceId;
  public String chargingMode;
  public float settingNumber;
  public String command;
}
