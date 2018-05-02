package com.renren0351.model.bean;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class CarInfo {
    public List<Car> car;
    public class Car{
        public String name;
        public List<String> type;
    }
}
