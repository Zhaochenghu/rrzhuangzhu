package com.bxchongdian.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/********************************
 * Created by lvshicheng on 2017/5/24.
 ********************************/
public class ChargeRecordBean implements Parcelable {
    /**
          "adbalance": 1832200,
          "batch": "6404305341941676711715",
          "bdbalance": 0,
          "carId": "0001999900000114",
          "cardId": "0001999900000114",
          "cardState": "",
          "companyCode": "0020",
          "cpId": "0020_0001",
          "cpinterfaceId": "01",
          "createTime": "2017-08-21 15:30:51",
          "elecFees": 180,
          "endTime": "2017-08-21 15:32:59",
          "id": "W4i9bjoSzm52hYFJk2ZVFPbeRiJCYDih",
          "locationFees": 0,
          "payNumber": "",
          "payType": "",
          "physicalNumber": "",
          "remark": "",
          "s_id": "",
          "serviceFees": 0,
          "startTime": "2017-08-21 15:29:33",
          "substationId": "0020",
          "substationName": "河北众聚新能源开发有限公司充电总站",
          "telecode": "",
          "ternumber": "",
          "totalElectric": 0,
          "transType": 1,
          "transamount": 180,
          "transtime": "",
          "transtypeId": "",
          "tsMark": "",
          "userId": "",
          "username": ""
    */
  public String cpinterfaceId;
  public String substationName;
  public String areaName;
  public String username;
  public String updateTime;
  public String userId;
  public String s_id;
  public String id;
  public String tsMark;
  public String cpId;
  public String totalElectric;
  public String transtypeId;
  public int transType;   //
  public String ternumber;
  public String transamount;
  public String physicalNumber;
  public String transtime;
  public String carId;
  public String serviceFees;
  public String elecFees;
  public String payType;
  public String substationId;
  public String startTime;
  public String bdbalance;
  public String createTime;
  public String payNumber;
  public String remark;
  public String cardState;
  public String companyCode;
  public String locationFees;
  public String batch;
  public String cardId;
  public String endTime;
  public String adbalance;
  public String telecode;

  public String getPayType(){
    if (transType == 0){
      return "充电卡";
    }else { //1代表APP支付
      return "APP支付";
    }
  }

  public float getTransamount(){
    try {
      float f = Float.parseFloat(transamount);
      return f / 100f;
    }catch (Exception e){
      return 0;
    }
  }

  public float getElecFees(){
    try {
      float f = Float.parseFloat(elecFees);
      return f / 100f;
    }catch (Exception e){
      return 0;
    }
  }

  public float getServiceFees(){
    try {
      float f = Float.parseFloat(serviceFees);
      return f / 100f;
    }catch (Exception e){
      return 0;
    }
  }

  public float getTotalElectric(){
    try {
      int electric = Integer.parseInt(totalElectric);
      return electric / 100f;
    }catch (Exception e){
      return 0;
    }

  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.cpinterfaceId);
    dest.writeString(this.username);
    dest.writeString(this.updateTime);
    dest.writeString(this.userId);
    dest.writeString(this.s_id);
    dest.writeString(this.id);
    dest.writeString(this.tsMark);
    dest.writeString(this.cpId);
    dest.writeString(this.totalElectric);
    dest.writeString(this.transtypeId);
    dest.writeString(this.ternumber);
    dest.writeString(this.transamount);
    dest.writeString(this.physicalNumber);
    dest.writeString(this.transtime);
    dest.writeString(this.carId);
    dest.writeString(this.serviceFees);
    dest.writeString(this.payType);
    dest.writeString(this.substationId);
    dest.writeString(this.startTime);
    dest.writeString(this.bdbalance);
    dest.writeString(this.createTime);
    dest.writeString(this.payNumber);
    dest.writeString(this.remark);
    dest.writeString(this.cardState);
    dest.writeString(this.companyCode);
    dest.writeString(this.locationFees);
    dest.writeString(this.batch);
    dest.writeString(this.cardId);
    dest.writeString(this.endTime);
    dest.writeString(this.adbalance);
    dest.writeString(this.telecode);
    dest.writeString(this.areaName);
    dest.writeString(this.substationName);
    dest.writeInt(this.transType);
    dest.writeString(this.elecFees);
  }

  public ChargeRecordBean() {
  }

  protected ChargeRecordBean(Parcel in) {
    this.cpinterfaceId = in.readString();
    this.username = in.readString();
    this.updateTime = in.readString();
    this.userId = in.readString();
    this.s_id = in.readString();
    this.id = in.readString();
    this.tsMark = in.readString();
    this.cpId = in.readString();
    this.totalElectric = in.readString();
    this.transtypeId = in.readString();
    this.ternumber = in.readString();
    this.transamount = in.readString();
    this.physicalNumber = in.readString();
    this.transtime = in.readString();
    this.carId = in.readString();
    this.serviceFees = in.readString();
    this.payType = in.readString();
    this.substationId = in.readString();
    this.startTime = in.readString();
    this.bdbalance = in.readString();
    this.createTime = in.readString();
    this.payNumber = in.readString();
    this.remark = in.readString();
    this.cardState = in.readString();
    this.companyCode = in.readString();
    this.locationFees = in.readString();
    this.batch = in.readString();
    this.cardId = in.readString();
    this.endTime = in.readString();
    this.adbalance = in.readString();
    this.telecode = in.readString();
    this.areaName = in.readString();
    this.substationName = in.readString();
    this.transType = in.readInt();
    this.elecFees = in.readString();
  }

  public static final Creator<ChargeRecordBean> CREATOR = new Creator<ChargeRecordBean>() {
    @Override
    public ChargeRecordBean createFromParcel(Parcel source) {
      return new ChargeRecordBean(source);
    }

    @Override
    public ChargeRecordBean[] newArray(int size) {
      return new ChargeRecordBean[size];
    }
  };
}
