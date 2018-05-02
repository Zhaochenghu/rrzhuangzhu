package com.renren0351.model.bean;

import java.io.Serializable;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CarBean implements Serializable{
    /*
        "assistVoltage": 24,
        "b_actived": "",
        "batteryType": "",
        "capacity": "",
        "carCode": "vwmWmwXX3GjU34kk5RNBq8CX59",
        "carType": "宝马 i3",
        "carVIN": "",
        "createTime": "2017-07-21 15:15:50",
        "current": 25,
        "disable": false,
        "id": 2,
        "imgUrl": "",
        "joinTime": "",
        "license": "京123",
        "licenseType": "小型汽车",
        "manudate": "",
        "manufacturer": "",
        "remark": "",
        "s_id": "",
        "updateTime": "2017-07-21 15:15:50",
        "userCode": "L8DNUY4M8iqpKN5QFYKeXV",
        "userId": 114,
        "voltage": 25
     */
    public int id;
    public String carVIN;
    public String batteryType;
    public String capacity;
    public String manufacturer;
    public String manudate;
    public String license;
    public String licenseType;
    public String carType;
    public String carCode;
    public String imgUrl;
    public String createTime;
    public String updateTime;
    public String userCode;
    public String s_id;
    public String b_actived;
    public String joinTime;
    public String remark;
    public boolean disable;
    public int  voltage;
    public int  current;
    public int  assistVoltage;

}
