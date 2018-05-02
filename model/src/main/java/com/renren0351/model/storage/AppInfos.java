package com.renren0351.model.storage;

import cn.com.leanvision.annotation.spf.LvSpfConfig;
import cn.com.leanvision.annotation.spf.LvSpfPreferences;

/********************************
 * Created by lvshicheng on 2017/2/10.
 ********************************/
@LvSpfPreferences
public class AppInfos {

    /**
     * 登录成功后根据密码MD5得到的KEY
     */
    private String secretKey;

    @LvSpfConfig(encryption = true)
    private String userName;

    @LvSpfConfig(encryption = true)
    private String pwd;

    @LvSpfConfig(encryption = true)
    private String token = "";

    @LvSpfConfig(encryption = true)
    private String uid;

    private double money;

    private String charging;
    private String chargeStationName;

    private String searchRecord = "";

    private String headerUrl = "";

    private String nick;

    private long downloadId;

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public String getSearchRecord() {
        return searchRecord;
    }

    public void setSearchRecord(String searchRecord) {
        this.searchRecord = searchRecord;
    }

    public String getChargeStationName() {
        return chargeStationName;
    }

    public void setChargeStationName(String chargeStationName) {
        this.chargeStationName = chargeStationName;
    }

    public String getCharging() {
        return charging;
    }

    public void setCharging(String charging) {
        this.charging = charging;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
