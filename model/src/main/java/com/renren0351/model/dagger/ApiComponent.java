package com.renren0351.model.dagger;

import com.renren0351.model.ApiService;
import com.renren0351.model.WxpayService;

import javax.inject.Singleton;

import dagger.Component;

/********************************
 * Created by lvshicheng on 2017/2/10.
 ********************************/
@Component(modules = {ApiModule.class})
@Singleton
public interface ApiComponent {

    ApiService apiService();

    WxpayService wxpayService();
}
