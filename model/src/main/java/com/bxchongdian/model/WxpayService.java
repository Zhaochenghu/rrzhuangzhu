package com.bxchongdian.model;

import com.bxchongdian.model.request.WxOrderQueryRequest;
import com.bxchongdian.model.request.WxOrderRequest;
import com.bxchongdian.model.response.AliOrderResponse;
import com.bxchongdian.model.response.WxOrderQueryResponse;
import com.bxchongdian.model.response.WxOrderResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/6/27.
 ********************************/
public interface WxpayService {

    @POST("wx/pay/unifiedprder/cs0023")
    Observable<WxOrderResponse> placeAnOrder(@Body WxOrderRequest request);

    @POST("wx/pay/orderquery/cs0023")
    Observable<WxOrderQueryResponse> queryOrder(@Body WxOrderQueryRequest request);

    @POST("wx/alipay/unifiedprder/cs0023")
    Observable<AliOrderResponse> placeAnOrderAlipay(@Body WxOrderRequest request);

    @POST("cs/v1/app/alipay/confirm")
    Observable<WxOrderQueryResponse> queryOrderAlipay(@Body WxOrderQueryRequest request);
}
