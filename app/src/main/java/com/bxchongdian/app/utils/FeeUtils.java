package com.bxchongdian.app.utils;

import com.bxchongdian.model.bean.FeeBean;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import cn.com.leanvision.baseframe.util.LvTimeUtil;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class FeeUtils {
    /**
     * 获得当前的费用
     * @param list {eValue，sValue}
     * @return
     */
    public static float[] getCurrentFree(List<FeeBean> list){
        float sValue = 0;
        float eValue = 0;
        if (list != null && list.size() > 0) {
            for (FeeBean bean : list) {
                if (bean != null) {
                    LinkedHashMap<Integer, Float> map = bean.feeMap;
                    Iterator it = map.keySet().iterator();
                    while (it.hasNext()) {
                        int key = (int) it.next();
                        if (LvTimeUtil.getDuration() <= key) {
                            if (bean.templateType.equals("s")) {
                                sValue = map.get(key);
                            }
                            if (bean.templateType.equals("e")) {
                                eValue = map.get(key);
                            }
                            break;
                        }
                    }
                }

            }

        }
        float[] fee = {eValue,sValue};
        return fee;
    }
}
