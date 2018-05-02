package com.renren0351.rrzzapp.views.fragments;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.event.ShowChargingInfoEvent;
import com.renren0351.rrzzapp.views.base.LvBaseFragment;
import com.renren0351.rrzzapp.wigets.MarkView;
import com.renren0351.model.LvRepository;
import com.renren0351.model.bean.SubstationBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.util.LvPhoneUtils;
import rx.android.schedulers.AndroidSchedulers;

/********************************
 * Created by lvshicheng on 2017/2/24.
 ********************************/
public class OrderMapFragment extends LvBaseFragment implements LocationSource, AMapLocationListener {

    private LocationSource.OnLocationChangedListener mListener;

    private AMap                     aMap;
    private AMapLocationClient       mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private List<Marker> markers = new ArrayList<>();

    public static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    //  private static final int STROKE_COLOR = Color.argb(255, 3, 145, 255);
    public static final int FILL_COLOR   = Color.argb(10, 0, 0, 180);
//  private static final int FILL_COLOR   = Color.argb(255, 0, 0, 180);

    // 默认地图的缩放级别 3-19
    //比例尺:13----1km
    public static final float DEFAULT_ZOOM_LEVEL = 13f;

    public static AMapLocation aMapLocation;
    public boolean isFirst = true;

    public static OrderMapFragment newInstance() {
        return new OrderMapFragment();
    }

    @Override
    protected View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fgmt_order_map, container, false);
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        setUpMapIfNeeded();
    }

    @Override
    public void onSupportVisible() {
        if (needRefresh) {
            refreshSubstations();
            needRefresh = false;
        }
    }

    /**
     * ---------------
     * Click 事件
     * ---------------
     */
    @OnClick(R.id.iv_location)
    public void clickLocation() { // 定位
        if (aMapLocation != null) {
            // 检查列表获取是否成功
            if (!LvRepository.getInstance().isRequestSuccess()) {
                ((OrderFragment) getParentFragment()).requestSubstationsAgain();
            }
            animationLatlng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
        } else {
      /* no-op */
        }
    }

    @OnClick(R.id.iv_map_add)
    public void clickMapAdd() { // 放大
        if (aMap != null) {
            aMap.animateCamera(CameraUpdateFactory.zoomIn(), 1000, null);
        }
    }

    @OnClick(R.id.iv_map_minus)
    public void clickMapMinus() { // 缩小
        if (aMap != null) {
            aMap.animateCamera(CameraUpdateFactory.zoomOut(), 1000, null);
        }
    }

    /**
     * ------------------
     * <p> 地图相关设置和监听
     * ------------------
     */
    private void setUpMapIfNeeded() {
        if (aMap == null) {
            aMap = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        }
        // 初始化地图显示
        initMap();
        // 自定义中间远点的样式
        setupLocationStyle();
        // 测试点
//    final LatLng latLng = new LatLng(39.972811, 116.336088);
//    addMarkersToMap(latLng);
        // 地图的相关监听
        initMapListener();
    }

    private void initMapListener() {
        //点击地图时需要关闭OrderView
        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    RxBus.getInstance().postEvent(new ShowChargingInfoEvent(false));
                }
            }
        });

        //点击覆盖物（Marker）
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 被点击的Marker
                LatLng position = marker.getPosition();
                // 获取Marker的电站信息
                SubstationBean substationBean = (SubstationBean) marker.getObject();
//                Log.i("TAG", "onMarkerClick: " + substationBean.name + " " + substationBean.isFavorites);
                if (substationBean == null) {
                    return true;
                }
                if (aMap != null) {
                    // 转成屏幕位置 - 起点位置
                    Projection projection = aMap.getProjection();
                    Point point = projection.toScreenLocation(position);
                    // 终点位置
                    MainFragment parent = (MainFragment) getParentFragment().getParentFragment();
                    DisplayMetrics displayMetrics = LvPhoneUtils.getDisplayMetrics(_mActivity);
                    //48dp为顶部tool bar和底部navigation bar
                    int destY =
                        displayMetrics.heightPixels
                            - parent.getOrderViewHeight()
                            - getResources().getDimensionPixelOffset(R.dimen.actionbar_size) * 2;
                    int destX = displayMetrics.widthPixels / 2;
//          DebugLog.log("destX: %d destY: %d", destX, destY);
                    //移动地图，使图标在orderView上方
                    CameraUpdate cameraUpdate = CameraUpdateFactory.scrollBy(point.x - destX, point.y - destY);
                    aMap.animateCamera(cameraUpdate);
                }
//        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                // 计算距离
                substationBean.setDistance(
                    AMapUtils.calculateLineDistance(
                        position, new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                // 显示OrderView弹窗
                RxBus.getInstance().postEvent(new ShowChargingInfoEvent(true, substationBean));
                return true;
            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                // 初始化比例尺
                scaleMap();
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
        myLocationStyle.myLocationIcon(
            BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_location));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色 - 外圆填充颜色值
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 初始化比例尺
     */
    private void scaleMap() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL);
        aMap.animateCamera(cameraUpdate, 500, null);
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
        aMap.setLocationSource(this);
        uiSettings.setMyLocationButtonEnabled(false);
        aMap.setMyLocationEnabled(true);
        // 手势缩放
        uiSettings.setScrollGesturesEnabled(true);
    }

    /**
     * 移动过程中修改缩放比例 不能小于默认比例
     */
    private void animationLatlng(LatLng latLng) {
//    CameraPosition cameraPosition = aMap.getCameraPosition();
//    float zoom = cameraPosition.zoom > default_Zoom_Level ? cameraPosition.zoom : default_Zoom_Level;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL);
        aMap.animateCamera(cameraUpdate, 300, null);
    }

    /**
     * 在地图上添加marker
     *
     * @param latlng 经纬度坐标
     * @param view 覆盖物
     */
    private Marker addMarkersToMap(LatLng latlng, MarkView view) {
        MarkerOptions markerOption = new MarkerOptions()
            .icon(BitmapDescriptorFactory.fromView(view)) // R.drawable.ic_marker
            .position(latlng)
            .draggable(true);
        return aMap.addMarker(markerOption);
    }

    /**
     * -----------------
     * LocationSource impl
     * -----------------
     */
    // 激活定位
    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(_mActivity.getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    // 停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * -----------------
     * AMapLocationListener impl
     * -----------------
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                OrderMapFragment.aMapLocation = aMapLocation;
//        DebugLog.log("lat : %f, lng : %f", aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            } else {
                if (isFirst) {
                    isFirst = false;
                    showToast("定位失败");
                }
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                DebugLog.log("%s", errText);
            }
        }
    }

    /**
     * ---------------
     * 简单的工具方法
     * ---------------
     */
    public boolean needRefresh = true;
    //加载图标
    public void refreshSubstations() { // 这里刷新做一下DIFF处理
        List<Marker> mapScreenMarkers = aMap.getMapScreenMarkers();
        if (mapScreenMarkers != null && mapScreenMarkers.size() > 1) {
            for (int i = 0; i < mapScreenMarkers.size(); i++) {
                Marker marker = mapScreenMarkers.get(i);
                Object object = marker.getObject();
                if (object != null && object instanceof SubstationBean) {
                    Boolean aBoolean = LvRepository.getInstance().doFilter((SubstationBean) object);
                    if (marker.isVisible() != aBoolean) {
                        marker.setVisible(
                            LvRepository.getInstance().doFilter((SubstationBean) object));
                    }
                }
            }
        } else {
//            aMap.clear();
            if (markers == null){
                markers = new ArrayList<>();
            }
            LvRepository.getInstance()
                .getSubstations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<SubstationBean>() {
                    @Override
                    public void onNext(SubstationBean substationBean) {
                        LatLng latLng = new LatLng(substationBean.getLat(), substationBean.getLng());

                        MarkView view = new MarkView(_mActivity.getApplicationContext());
                        view.setMarkerNum(substationBean.hasRest);
                        Marker marker = addMarkersToMap(latLng, view);
                        marker.setObject(substationBean);
                        markers.add(marker);
                    }
                });
        }
    }

    /**
     * 重新加载marker
     */
    public void refreshUserStations(){
        //清除地图中的所以marker
        if (markers != null && markers.size() > 0) {
            for (Marker marker : markers){
                marker.remove();
            }
        }

        //清除集合中的marker
        if (markers == null){
            markers = new ArrayList<>();
        }else {
            markers.clear();
        }
        LvRepository.getInstance()
                .getSubstations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<SubstationBean>() {
                    @Override
                    public void onNext(SubstationBean substationBean) {
                        LatLng latLng = new LatLng(substationBean.getLat(), substationBean.getLng());

                        MarkView view = new MarkView(_mActivity.getApplicationContext());
                        view.setMarkerNum(substationBean.hasRest);
                        Marker marker = addMarkersToMap(latLng, view);
                        //图标附加电站信息
                        marker.setObject(substationBean);
                        markers.add(marker);
                    }
                });
    }
}
