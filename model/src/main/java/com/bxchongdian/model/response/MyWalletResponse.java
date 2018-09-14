package com.bxchongdian.model.response;

/********************************
 * Created by lvshicheng on 2017/7/4.
 ********************************/
public class MyWalletResponse extends BaseResponse {
    /**
     * content : {"available":0,"money":0,"idcard":null,"freeze":0,"hasPassword":true,"payAccount":"0001999900000132"}
     */

    public ContentEntity content;

    public static class ContentEntity {
        /**
         * available : 0
         * money : 0
         * idcard : null
         * freeze : 0
         * hasPassword : true
         * payAccount : 0001999900000132
         */
        public int     available;
        public int     money;
        public String  idcard;
        public int     freeze;
        public boolean hasPassword;
        public String  payAccount;
    }
}
