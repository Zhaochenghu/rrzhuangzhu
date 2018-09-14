package com.bxchongdian.app.event;

import cn.com.leanvision.baseframe.rx.event.BaseBusEvent;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class GetDataEvent extends BaseBusEvent {
    public String data;

    public GetDataEvent(String data) {
        this.data = data;
    }
}
