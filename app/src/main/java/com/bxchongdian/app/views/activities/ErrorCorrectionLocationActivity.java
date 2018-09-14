package com.bxchongdian.app.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bxchongdian.app.R;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.app.views.fragments.OrderMapFragment;
import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.request.CheckErrorRequest;
import com.bxchongdian.model.response.SimpleResponse;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

import static com.bxchongdian.app.views.fragments.OrderMapFragment.aMapLocation;

/********************************
 * Created by lvshicheng on 2017/6/13.
 ********************************/
@Route(path = "/login/station/error/location")
public class ErrorCorrectionLocationActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.tv_location_name)
    TextView tvLocationName;
    @BindView(R.id.tv_lat)
    TextView tvLat;
    @BindView(R.id.tv_lng)
    TextView tvLng;
    @BindView(R.id.btn_confirm)
    Button   btnConfirm;

    private String substationId;

    // lat 纬度
    // lng 经度
    public static void navigation(double lat, double lng, String substationId) {
        ARouter.getInstance()
            .build("/login/station/error/location")
            .withDouble("lat", lat)
            .withDouble("lng", lng)
            .withString("substationId", substationId)
            .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_erro_correction_location);
    }

    @Override
    protected void initView() {
        initToolbarNav("位置纠错");

        substationId = getIntent().getStringExtra("substationId");

        setUpMapIfNeeded();
    }

    /**
     * ---------------
     * Click 事件
     * ---------------
     */
    @OnClick(R.id.iv_location)
    public void clickLocation() { // 定位
        if (aMapLocation != null) {
            //检查列表获取是否成功
            animationLatlng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
        } else {
      /* no-op */
        }
    }

    @OnClick(R.id.btn_confirm)
    public void clickConfirm() { // 提交确认位置信息

        Object tag = tvLat.getTag();
        if (tag == null) {
            return;
        }

        CheckErrorRequest request = new CheckErrorRequest();
        request.subType = String.valueOf(0); // 默认0就是位置纠错
        request.substationId = substationId; // 子站ID够了
        request.cpId = "";
        request.remark = String.format("{\"address\":\"%s\", \"lat\":\"%s\", \"lng\":\" %s\"}",
            tvLocationName.getText(),
            tvLat.getTag(),
            tvLng.getTag()); // 这里放地理位置信息

        showLoadingDialog("正在提交");
        ApiComponentHolder.sApiComponent.apiService().checkError(request)
            .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
            .subscribe(new SimpleSubscriber<SimpleResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    dismissLoadingDialog();
                }

                @Override
                public void onNext(SimpleResponse response) {
                    dismissLoadingDialog();
                    if (response.isSuccess()) {
                        showToast("提交成功");
                        btnConfirm.setEnabled(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    } else {
                        showToast(response.msg);
                    }
                }
            });
    }

    /**
     * 中心点移动到指定的经纬度，移动过程中修改缩放比例 不能小于默认比例
     */
    private void animationLatlng(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, OrderMapFragment.DEFAULT_ZOOM_LEVEL);
        aMap.animateCamera(cameraUpdate, 500, null);
    }

    /**
     * ------------------
     * <p> 地图相关设置和监听
     * ------------------
     */
    private AMap aMap;

    private void setUpMapIfNeeded() {
        if (aMap == null) {
            aMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map))
                .getMap();
        }

        // 初始化地图显示
        initMap();
        // 自定义中间远点的样式
        setupLocationStyle();
        // 地图的相关监听
        initMapListener();
    }

    private void initMapListener() {
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                // 初始化比例尺
//                scaleMap();
                double lat = getIntent().getDoubleExtra("lat", 0.0f);
                double lng = getIntent().getDoubleExtra("lng", 0.0f);
                DebugLog.log("lat: %f, lng: %f", lat, lng);
                animationLatlng(new LatLng(lat, lng));
            }
        });

        final GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                tvLocationName.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                btnConfirm.setEnabled(true);
            }

            @Override
            public void onCameraChangeFinish(CameraPosition position) {
                LatLng target = position.target;
                tvLat.setText(String.format("纬度：%.6f", target.latitude));
                tvLat.setTag(target.latitude);
                tvLng.setText(String.format("经度：%.6f", target.longitude));
                tvLng.setTag(target.longitude);

                LatLonPoint latLonPoint = new LatLonPoint(target.latitude, target.longitude);
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
                geocoderSearch.getFromLocationAsyn(query);
            }
        });
    }

    /**
     * 自定义中间远点的样式
     */
    private void setupLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(OrderMapFragment.STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色 - 外圆填充颜色值
        myLocationStyle.radiusFillColor(OrderMapFragment.FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 初始化Map显示
     */
    private void initMap() {
        UiSettings uiSettings = aMap.getUiSettings();
        // logo放置位置
        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
        // 比例尺
        uiSettings.setScaleControlsEnabled(true);
        // 缩放
        uiSettings.setZoomControlsEnabled(false);
        // 定位按钮
//        aMap.setLocationSource(this);
        uiSettings.setMyLocationButtonEnabled(false);
        aMap.setMyLocationEnabled(true);
        // 手势缩放
        uiSettings.setScrollGesturesEnabled(true);
    }

//    @Override
//    public void activate(OnLocationChangedListener onLocationChangedListener) {
//        mListener = onLocationChangedListener;
//        mListener.onLocationChanged(OrderMapFragment.aMapLocation);
//    }
//
//    @Override
//    public void deactivate() {
//        mListener = null;
//    }
//
//    private LocationSource.OnLocationChangedListener mListener;

//    private AMapLocationClient       mlocationClient;
//    private AMapLocationClientOption mLocationOption;

//    @Override
//    public void activate(OnLocationChangedListener onLocationChangedListener) {
//        mListener = onLocationChangedListener;
//        if (mlocationClient == null) {
//            mlocationClient = new AMapLocationClient(ErrorCorrectionLocationActivity.this.getApplicationContext());
//            mLocationOption = new AMapLocationClientOption();
//            //设置定位监听
//            mlocationClient.setLocationListener(this);
//            //设置为高精度定位模式
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            //设置定位参数
//            mlocationClient.setLocationOption(mLocationOption);
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用onDestroy()方法
//            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//            mlocationClient.startLocation();
//        }
//    }
//
//    @Override
//    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (mListener != null && aMapLocation != null) {
//            if (aMapLocation.getErrorCode() == 0) {
//                aMapLocation = aMapLocation;
////        DebugLog.log("lat : %f, lng : %f", aMapLocation.getLatitude(), aMapLocation.getLongitude());
//                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
//            } else {
//                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
//                DebugLog.log("%s", errText);
//            }
//        }
//    }
//
//    @Override
//    public void deactivate() {
//        mListener = null;
//        if (mlocationClient != null) {
//            mlocationClient.stopLocation();
//            mlocationClient.onDestroy();
//        }
//        mlocationClient = null;
//    }
}
