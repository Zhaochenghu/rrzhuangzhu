package com.renren0351.model.response;

import com.google.gson.annotations.SerializedName;
import com.renren0351.model.bean.CarBean;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class CarResponse extends BaseResponse {

    @SerializedName("content")
    public List<CarBean> cars;
}
