package com.renren0351.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class AppointmentResponse extends BaseResponse{

	@SerializedName("content")
	public Appointment appointment;

	public class Appointment{
		public String orderId;
		public String taskId;
	}
}
