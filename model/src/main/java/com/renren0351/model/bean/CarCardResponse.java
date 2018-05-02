package com.renren0351.model.bean;

import com.google.gson.annotations.SerializedName;
import com.renren0351.model.response.BaseResponse;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class CarCardResponse extends BaseResponse {

	@SerializedName("content")
	public List<CarCardBean> list;
}
