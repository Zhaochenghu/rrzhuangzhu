package com.bxchongdian.model.response;

import com.google.gson.annotations.SerializedName;
import com.bxchongdian.model.bean.FeeBean;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class FeeResponse extends BaseResponse {
    @SerializedName("content")
    public List<FeeBean> list;
}
