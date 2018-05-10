package com.renren0351.model;

import com.renren0351.model.bean.CarBean;
import com.renren0351.model.bean.CarCardResponse;
import com.renren0351.model.request.CardRequest;
import com.renren0351.model.request.CheckErrorRequest;
import com.renren0351.model.request.ForgotPwdRequest;
import com.renren0351.model.request.LoginRequest;
import com.renren0351.model.request.PayPwdRequest;
import com.renren0351.model.request.RechargeRequest;
import com.renren0351.model.request.RegisterRequest;
import com.renren0351.model.request.SimpleRequest;
import com.renren0351.model.request.StartChargingRequest;
import com.renren0351.model.request.StopRequest;
import com.renren0351.model.response.AppointmentResponse;
import com.renren0351.model.response.CarResponse;
import com.renren0351.model.response.CardListResponse;
import com.renren0351.model.response.CardResponse;
import com.renren0351.model.response.ChargeRecordResponse;
import com.renren0351.model.response.ChargingResponse;
import com.renren0351.model.response.FeeResponse;
import com.renren0351.model.response.LoginResponse;
import com.renren0351.model.response.MyWalletResponse;
import com.renren0351.model.response.OrderResponse;
import com.renren0351.model.response.ProfileResponse;
import com.renren0351.model.response.RechargeRecordResponse;
import com.renren0351.model.response.RegisterResponse;
import com.renren0351.model.response.SimpleResponse;
import com.renren0351.model.response.StationDetailResponse;
import com.renren0351.model.response.StationInfoResponse;
import com.renren0351.model.response.StationStatusResponse;
import com.renren0351.model.response.SubstationsResponse;
import com.renren0351.model.response.UploadFileResponse;
import com.renren0351.model.response.WalletInfoResponse;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/2/10.
 *modify by 赵成虎 on 2018/5/4
 ********************************/
public interface ApiService {

    @POST("v1/login")
    Observable<LoginResponse> login(@Body LoginRequest request);

    /**
     * 修改密码
     *
     * @param request [oldPassword，newPassword]
     */
    @POST("v1/account/password/change")
    Observable<SimpleResponse> resetPwd(@Body Map<String, String> request);

    /**
     * 注册
     *
     * @param request
     * @return
     */
    @POST("v1/register")
    Observable<RegisterResponse> register(@Body RegisterRequest request);

    /**
     * 忘记密码
     */
    @POST("/v1/account/password/forget")
    Observable<SimpleResponse> forgotPwd(@Body ForgotPwdRequest request);

    /**
     * 个人基本信息
     */
    @GET("v1/profile")
    Observable<ProfileResponse> getProfile();

    /**
     * 修改昵称
     * @param hashMap
     * @return
     */
    @POST("v1/nickname/update")
    Observable<SimpleResponse> updateNick(@Body HashMap<String,String> hashMap);


    /**
     * 充电站列表
     */
    //@GET("v1/substation/list")
    //Observable<SubstationsResponse> getSubstation();
    @GET("v1/area/list")
    Observable<SubstationsResponse> getSubstation();


    /**
     * 充电站概要信息
     *areaId
     * substation
     * @param stationId [substationId]
     */
   // @GET("v1/substation/summary")
   // Observable<StationDetailResponse> getSubstationSummary(@Query("substationId") String stationId);
    @GET("v1/area/summary")
    Observable<StationDetailResponse> getSubstationSummary(@Query("areaId") String stationId);

    /**
     * 预约
     *
     * @param request
     */
    @POST("v1/appointment")
    Observable<AppointmentResponse> chargingOrder(@Body Map<String, Object> request);

    /**
     * 查询预约
     *
     * @return
     */
    @GET("v1/appointment/query")
    Observable<OrderResponse> queryOrder();

    /**
     * 取消预约
     *
     * @param orderId
     * @return
     */
    @GET("v1/appointment/cancel")
    Observable<SimpleResponse> cancelOrder(@Query("orderId") String orderId);

    /**
     * 查看电价（费用模板）
     *areaId
     * @param substationId
     * @return
     */
   // @GET("v1/fees")
   // Observable<FeeResponse> queryFees(@Query("substationId") String substationId);
    @GET("v1/fees/area")
    Observable<FeeResponse> queryFees(@Query("areaId") String substationId);

    /**
     * 收藏充电站
     *
     * @param substationId [substationId]
     */
  //  @GET("v1/favorite/substation")
//    Observable<SimpleResponse> favorSubstation(@Query("substationId") String substationId);

    @GET("v1/favorite/area")
    Observable<SimpleResponse> favorSubstation(@Query("areaId") String substationId);

    /**
     * 取消收藏
     *
     * @param substationId [substationId]
     */
  //  @GET("v1/unfavorite/substation")
  //  Observable<SimpleResponse> unFavorSubstation(@Query("substationId") String substationId);
    @GET("v1/unfavorite/area")
    Observable<SimpleResponse> unFavorSubstation(@Query("areaId") String substationId);

    /**
     * 查看收藏
     *
     * @param request [page(default = 1), prePage(default = 20)]
     */
    //@GET("v1/my/substations")
    //Observable<SubstationsResponse> getFavorList(@QueryMap HashMap<String, Object> request);
    @GET("v1/my/areas")
    Observable<SubstationsResponse> getFavorList(@QueryMap HashMap<String, Object> request);

    /**
     * 计费实时信息
     */
    @GET("v1/billing")
    Observable<SimpleResponse> getBiling();

    @GET("v1/my/cards")
    Observable<CardListResponse> queryCards();

    /**
     * 充电卡绑定
     */
    @POST("v1/my/bindcard")
    Observable<CardResponse> bindCard(@Body CardRequest request);

    /**
     * 充电卡解除绑定
     */
    @POST("v1/my/unbindcard")
    Observable<SimpleResponse> unbindCard(@Body CardRequest request);

    /**
     * 查询卡信息
     */
    @GET("v1/card/info")
    Observable<CardResponse> queryCardInfo(@Query("cardNumber") String cardNumber);

    /**
     * 根据充电桩的二维码返回充电桩信息
     */
    @GET("v1/qr/info")
    Observable<StationInfoResponse> queryStationInfo(@Query("qr") String qr);

    /**
     * 开始充电
     */
    @POST("v1/charging/start")
    Observable<ChargingResponse> startCharging(@Body StartChargingRequest request);

    /**
     * 停止充电
     *
     * @param request
     * @return
     */
    @GET("v1/charging/stop")
    Observable<ChargingResponse> stopCharging(@QueryMap HashMap<String, Object> request);

    /**
     * 删除充电状态
     * @param request
     * @return
     */
    @GET("v1/charging/current/status/delete")
    Observable<SimpleResponse> statusDelete(@QueryMap HashMap<String, String> request);

    /**
     * 充电状态
     */
    @GET("v1/charging/status")
    Observable<StationStatusResponse> getStationStatus();

    /**
     * 充电状态
     * <p>
     * substationId 子站ID
     * cpId 桩ID
     * cpinterfaceId 抢接口ID
     */
    @GET("v1/charging/status")
    Observable<StationStatusResponse> getOtherStationStatus(@QueryMap HashMap<String, Object> request);

    /**
     * 余额查询
     */
    @GET("v1/wallet/info")
    Observable<WalletInfoResponse> getWalletInfo();

    /**
     * 充值
     */
    @POST("v1/wallet/recharge")
    Observable<SimpleResponse> walletRecharge(@Body RechargeRequest request);

    /**
     * 充值记录
     *
     * @return
     */
    @GET("v1/my/wallet/recharge/records")
    Observable<RechargeRecordResponse> rechargeRecord();

    /**
     * 消费记录
     */
    @GET("v1/my/deal/records")
    Observable<ChargeRecordResponse> chargeRecord(@QueryMap HashMap<String, Object> request);

    /**
     * 查看车辆
     *
     * @return
     */
    @GET("v1/my/cars")
    Observable<CarResponse> queryCars();

    /**
     * 保存车辆信息
     *
     * @param bean
     * @return
     */
    @POST("v1/my/car/save")
    Observable<SimpleResponse> saveCar(@Body CarBean bean);

    /**
     * 车卡绑定
     *
     * @param request
     * @return
     */
    @POST("v1/chargingcard/bindcar")
    Observable<SimpleResponse> bindCar(@Body HashMap<String, Object> request);

    /**
     * 根据车查询绑定卡的信息
     *
     * @param carId
     * @return
     */
    @GET("v1/chargingcard/bindcar/list")
    Observable<CarCardResponse> queryBindCard(@Query("carCode") String carId);

    /**
     * 车卡解除绑定
     *
     * @param bindId
     * @return
     */
    @GET("v1/chargingcard/unbindcar")
    Observable<SimpleResponse> unbindCar(@Query("bindId") int bindId);

    /**
     * 删除车辆
     *
     * @param request
     * @return
     */
    @POST("v1/my/car/del")
    Observable<SimpleResponse> deleteCar(@Body HashMap<String, Object> request);

    /**
     * 上传汽车图片
     * @param request
     * @return
     */
    @POST("v1/my/car/header/update")
    Observable<SimpleResponse> uploadCarPhoto(@Body HashMap<String, Object> request);

    /**
     * 获取短信验证码
     */
    @POST("v1/captcha")
    Observable<SimpleResponse> getCaptcha(@Body HashMap<String, Object> request);

    @GET("v1/my/wallet")
    Observable<MyWalletResponse> myWallet();

    /**
     * 设置支付密码
     */
    @POST("v1/my/wallet/set/password")
    Observable<SimpleResponse> setPayPwd(@Body PayPwdRequest request);

    /**
     * 修改支付密码
     */
    @POST("v1/my/wallet/reset/password")
    Observable<SimpleResponse> resetPayPwd(@Body PayPwdRequest request);

    /**
     * 忘记支付密码
     */
    @POST("v1/my/wallet/forget/password")
    Observable<SimpleResponse> forgotPayPwd(@Body PayPwdRequest request);

    /**
     * 检查支付密码
     */
    @POST("v1/my/wallet/check/password")
    Observable<SimpleResponse> checkPayPwd(@Body PayPwdRequest request);

    /**
     * 单文件上传
     */
    @POST("fileupload")
    Observable<UploadFileResponse> uploadSingleImage(@Body RequestBody request);

    @POST("v1/leavemsg/save")
    Observable<SimpleResponse> checkError(@Body CheckErrorRequest request);

    @GET("v1/profile")
    Observable<SimpleResponse> queryProfile();

    @POST("v1/my/header/update")
    Observable<SimpleResponse> updateHeaderImg(@Body HashMap<String, String> request);

    /**
     * 强制停止充电、删除当前充电记录
     */
    @POST("v1/chargingcmd/delete")
    Observable<SimpleRequest> deleteChanging(@Body StopRequest request);
}
