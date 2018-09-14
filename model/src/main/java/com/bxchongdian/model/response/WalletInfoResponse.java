package com.bxchongdian.model.response;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
public class WalletInfoResponse extends BaseResponse {

    public Content content;

    public class Content {
        /**
         * available : 0
         * money : 0
         * freeze : 0
         */

        public double money;  // Amount = freeze + available
        public double available;
        public double freeze;
    }
}
