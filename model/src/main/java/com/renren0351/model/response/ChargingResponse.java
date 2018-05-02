package com.renren0351.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ChargingResponse extends BaseResponse {
	@SerializedName("content")
	public Charging charging;

	public class Charging{
		/**
		 * "res": "0",
		 * "commandId": "2698496252307698214353"
		 */
		public String res;
		public String commanId;
	}
}
