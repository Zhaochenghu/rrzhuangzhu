package com.bxchongdian.app.views.activities;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.bxchongdian.app.R;
import com.bxchongdian.app.views.base.LvBaseGPSNaviActivity;
import com.bxchongdian.app.views.fragments.OrderMapFragment;

/********************************
 * Created by lvshicheng on 2017/2/14.
 * <p>
 * 语音导航页面
 ********************************/
@Route(path = "/order/navi")
public class GPSNaviActivity extends LvBaseGPSNaviActivity {

  public static void navigation(double lat, double lng) {
    ARouter.getInstance().build("/order/navi")
        .withDouble("lat", lat)
        .withDouble("lng", lng)
        .navigation();
  }

  @Override
  protected void setContentView(Bundle savedInstanceState) {

    double lat = getIntent().getDoubleExtra("lat", 0);
    double lng = getIntent().getDoubleExtra("lng", 0);

    mEndLatlng = new NaviLatLng(lat, lng);
    if (OrderMapFragment.aMapLocation != null) {
      mStartLatlng = new NaviLatLng(OrderMapFragment.aMapLocation.getLatitude(), OrderMapFragment.aMapLocation.getLongitude());
    }
    super.setContentView(savedInstanceState);
    setContentView(R.layout.aty_gps_navi);
  }

  @Override
  protected void initView(Bundle savedInstanceState) {
    mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
    mAMapNaviView.onCreate(savedInstanceState);
    mAMapNaviView.setAMapNaviViewListener(this);
  }

  @Override
  public void onInitNaviSuccess() {
    super.onInitNaviSuccess();
    /**
     * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
     *
     * @congestion 躲避拥堵
     * @avoidhightspeed 不走高速
     * @cost 避免收费
     * @hightspeed 高速优先
     * @multipleroute 多路径
     *
     *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
     *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
     */
    int strategy = 0;
    try {
      //再次强调，最后一个参数为true时代表多路径，否则代表单路径
      strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
  }

  @Override
  public void onCalculateRouteSuccess() {
    super.onCalculateRouteSuccess();
    mAMapNavi.startNavi(NaviType.GPS);
  }
}
