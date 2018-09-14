package com.bxchongdian.model.response;

import com.google.gson.annotations.SerializedName;
import com.bxchongdian.model.bean.RechargeRecordBean;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class RechargeRecordResponse extends BaseResponse {
    public Content content;

    public class Content {
        /**
         * per_page : 20
         * total : 0
         * page : 1
         * items : []
         */

        public int per_page;
        public int total;
        public int page;

        @SerializedName("items")
        public List<RechargeRecordBean> list;
    }
}
