package com.renren0351.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/17
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class OrderResponse extends BaseResponse {

    @SerializedName("content")
    public List<Order> list;
  /**
   * orderId : 7121590586869268890537
   * cpinterfaceId : 1
   * updateTime : 2017-07-19 17:40:28
   * startTime : 2017-07-25 10:13:14
   * cpIdteTime : 2017-07-19 17:40:28
   * use : zjxnys6
   * crearId : L8DNUY4M8iqpKN5QFYKeXV
   * substationId : 0020
   * taskMessageId : 7763df20-96e0-4dcd-b8e1-64fd8e3d5d57
   * commandId : 0607008574313383045493
   * status : 0
   * available_time: 2
   * sendTime : 2017-07-25 22:13:14
   * taskId : b3e5bba5-d683-4303-b6c1-ec87d6f1ac1f
   * appointTime :
   * duration : 6
   * id : 47
   */

  public class Order{
    public String orderId;
    public String cpinterfaceId;
    public String updateTime;
    public String startTime;
    public String cpId;
    public String createTime;
    public String userId;
    public String substationId;
    public String taskMessageId;
    public String commandId;
    public int status;
    public String sendTime;
    public String taskId;
    public String appointTime;
    public String duration;
    public int id;
    public Long available_time;
  }
}
