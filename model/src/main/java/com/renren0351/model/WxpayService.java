package com.renren0351.model;

import com.renren0351.model.request.WxOrderQueryRequest;
import com.renren0351.model.request.WxOrderRequest;
import com.renren0351.model.response.WxOrderQueryResponse;
import com.renren0351.model.response.WxOrderResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/********************************
 * Created by lvshicheng on 2017/6/27.
 ********************************/
public interface WxpayService {

    @POST("pay/unifiedprder/cs0021")
    Observable<WxOrderResponse> placeAnOrder(@Body WxOrderRequest request);

    @POST("pay/orderquery/cs0021")
    Observable<WxOrderQueryResponse> queryOrder(@Body WxOrderQueryRequest request);
}
