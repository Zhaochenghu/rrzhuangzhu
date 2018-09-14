package com.bxchongdian.app.views.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.jaiky.imagespickers.ImageConfig;
import com.jaiky.imagespickers.ImageSelector;
import com.jaiky.imagespickers.ImageSelectorActivity;
import com.bxchongdian.app.R;
import com.bxchongdian.app.event.GetDataEvent;
import com.bxchongdian.app.utils.GlideLoader;
import com.bxchongdian.app.utils.IntentUtils;
import com.bxchongdian.app.utils.image.Compressor;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.model.bean.CarBean;
import com.bxchongdian.model.bean.CarCardBean;
import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.response.SimpleResponse;
import com.bxchongdian.model.response.UploadFileResponse;
import com.bxchongdian.presenter.carinfo.CarInfoContract;
import com.bxchongdian.presenter.carinfo.CarInfoPresenter;
import com.trello.rxlifecycle.ActivityEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvFileUtils;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/05
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/mime/car_info")
@RuntimePermissions
public class CarInfoActivity extends LvBaseAppCompatActivity implements CarInfoContract.View{
    @BindView(R.id.iv_car)
    ImageView ivCar;
    @BindView(R.id.ll_card)
    LinearLayout llCard;
    @BindView(R.id.bt_bindCard)
    Button btBind;
    @BindView(R.id.tv_card)
    TextView tvCard;
    @BindView(R.id.tv_car_type)
    TextView tvCarType;
    @BindView(R.id.tv_carVIN)
    TextView tvCarVIN;
    @BindView(R.id.tv_car_number)
    TextView tvCarNumber;
    @BindView(R.id.tv_voltage)
    TextView tvVoltage;
    @BindView(R.id.tv_current)
    TextView tvCurrent;
    @BindView(R.id.tv_assist_voltage)
    TextView tvAssistVoltage;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.tv_capacity)
    TextView tvCapacity;
    @BindView(R.id.tv_manufacturer)
    TextView tvManufacturer;
    @BindView(R.id.tv_manudate)
    TextView tvManudate;

    public static final int REQUEST_CODE = 123;
    private CarInfoPresenter presenter;
    public static CarBean carBean;
    private String cardId;
    private boolean isBindCard;
    private ImageConfig imageConfig;
    private String path;
    private String imgUrl = "";
    private int bindId;
    public static void navigation(CarBean carBean) {
        ARouter.getInstance().build("/mime/car_info")
                .withSerializable("carBean",carBean).navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_car_info);
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbarNav("我的爱车");
        isBindCard = false;
        carBean = (CarBean) getIntent().getSerializableExtra("carBean");
        if (carBean != null)
            presenter.queryBindCard(carBean.carCode);

        initPhotoPicker();
    }

    private void initPhotoPicker() {
        imageConfig = new ImageConfig.Builder(GlideLoader.getInstance())
                .steepToolBarColor(getResources().getColor(R.color.titleBlue))
                .titleBgColor(getResources().getColor(R.color.titleBlue))
                .titleSubmitTextColor(getResources().getColor(R.color.white))
                .titleTextColor(getResources().getColor(R.color.white))
                .singleSelect()
                .showCamera()
                .filePath("/temp")
                .requestCode(REQUEST_CODE)
                .build();
    }

    @Override
    protected void initPresenter() {
        presenter = new CarInfoPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void destroyPresenter() {
        super.destroyPresenter();
        presenter.detachView();
    }

    @OnClick({R.id.iv_car, R.id.bt_bindCard, R.id.tv_unbind, R.id.tv_change,R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_car:   //车辆图片
                CarInfoActivityPermissionsDispatcher.openPickerWithCheck(this);
                break;
            case R.id.bt_bindCard:    //绑定充电卡
                initRxBus();
                CardActivity.navigation(true);
                break;
            case R.id.tv_unbind:    //解除绑定
                if (carBean != null){
                    presenter.unbindCar(bindId);
                }
                break;
            case R.id.tv_change:    //修改信息
                BindCarActivity.navigation(carBean);
                break;
            case R.id.tv_delete:    //删除车辆
                if (isBindCard){
                    showToast("请先解除绑定的充电卡");
                }else {
                    if (carBean != null){
                        HashMap<String,Object> map = new HashMap<>();
                        List<String> list = new ArrayList<>();
                        list.add(carBean.carCode);
                        map.put("carCodes",list);
                        presenter.deleteCar(map);
                    }
                }
                break;
        }
    }

    @Override
    public void showLoading(String msg) {
        showLoadingDialog();
    }

    @Override
    public void showNormal() {
        dismissLoadingDialog();
    }

    @Override
    public void requestFailed(String msg) {
        if (LvTextUtil.isEmpty(msg)) {
            showToast(R.string.network_not_available);
        } else {
            showToast(msg);
        }
    }

    @Override
    public void refreshCard(List<CarCardBean> list) {
        if (list != null && list.size() > 0){
            isBindCard = true;
            llCard.setVisibility(View.VISIBLE);
            tvCard.setText("充电卡：" + list.get(0).cardId);
            bindId = list.get(0).bindId;
            btBind.setVisibility(View.GONE);
        }else {
            isBindCard = false;
            llCard.setVisibility(View.GONE);
            btBind.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void deleteSuccess() {
        showToast("删除成功");
        finish();
    }

    @Override
    public void bindCarSuccess() {
        showToast("绑定成功");
        if (carBean != null)
            presenter.queryBindCard(carBean.carCode);
    }

    @Override
    public void unbindCarSuccess() {
        showToast("解绑成功");
        isBindCard = false;
        llCard.setVisibility(View.GONE);
        btBind.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCar();
    }

    private void refreshCar(){
        if (carBean != null){
            tvCarType.setText(carBean.carType);
            tvCarVIN.setText("车辆识别码（VIN码）：" + carBean.carVIN);
            tvCarNumber.setText("车牌号：" + carBean.license);
            tvVoltage.setText("最大充电电压：" + carBean.voltage + "V");
            tvCurrent.setText("最大充电电流：" + carBean.current + "A");
            tvAssistVoltage.setText("辅助充电电压：" + carBean.assistVoltage + "V");
            tvBattery.setText("电池类型：" + carBean.batteryType);
            tvCapacity.setText("电流容量：" + carBean.capacity);
            tvManufacturer.setText("生产厂家：" + carBean.manufacturer);
            tvManudate.setText("生产日期：" + carBean.manudate);
            Glide.with(this)
                    .load(carBean.imgUrl)
                    .placeholder(R.drawable.ic_car_default)
                    .error(R.drawable.ic_car_default)
                    .centerCrop()
                    .into(ivCar);
        }
    }

    private void initRxBus(){
        RxBus.getInstance()
                .toObservable(GetDataEvent.class)
                .compose(SchedulersCompat.<GetDataEvent>applyNewSchedulers())
                .compose(CarInfoActivity.this.<GetDataEvent>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<GetDataEvent>() {
                    @Override
                    public void call(GetDataEvent getDataEvent) {
                        if (!LvTextUtil.isEmpty(getDataEvent.data) && carBean != null) {
                            cardId = getDataEvent.data;
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("cardId",cardId);
                            map.put("carCode",carBean.carCode);
                            presenter.bindCar(map);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            List<String> list = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            if (list != null && list.size() > 0) {
                path = list.get(list.size() - 1);
//                Log.i("TAG", "onActivityResult: -----------------" + path);
                Observable.just(path)
                        .flatMap(new Func1<String, Observable<UploadFileResponse>>() {
                            @Override
                            public Observable<UploadFileResponse> call(String s) {
                                File file;
                                try {
                                    file = new Compressor(getApplicationContext()).compressToFile(new File(path));
                                    String fileMD5 = LvFileUtils.getFileMD5_32(file);
                                    MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                            .addFormDataPart("md5orsha1", fileMD5 == null ? "" : fileMD5)
                                            .addFormDataPart("upfile", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                                            .build();
                                    return ApiComponentHolder.sApiComponent.apiService().uploadSingleImage(body);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return Observable.error(new Throwable("文件压缩失败！"));
                            }
                        })
                        .map(new Func1<UploadFileResponse, String>() {
                            @Override
                            public String call(UploadFileResponse uploadFileResponse) {
                                if (uploadFileResponse.isSuccess()) {
                                    return uploadFileResponse.content;
                                } else {
                                    Observable.error(new Throwable("文件上传失败"));
                                }
                                return null;
                            }
                        })
                        .flatMap(new Func1<String, Observable<SimpleResponse>>() {
                            @Override
                            public Observable<SimpleResponse> call(String s) {
                                HashMap<String, Object> request = new HashMap<>();
                                request.put("imgUrl", s);
                                request.put("carCode", carBean.carCode);
                                imgUrl = s;
                                return ApiComponentHolder.sApiComponent.apiService().uploadCarPhoto(request);
                            }
                        })
                        .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
                        .subscribe(new SimpleSubscriber<SimpleResponse>() {
                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                if (simpleResponse.isSuccess()){
                                    showToast("修改成功");
                                    GlideLoader.getInstance().displayImage(CarInfoActivity.this, imgUrl, ivCar);
                                }else {
                                    showToast(simpleResponse.msg);
                                }
                            }
                        });
            }

        }
    }

    public void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setMessage("需要读取照片或者拍照，建议您在设置中开启")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentUtils.turnToAppDetail(CarInfoActivity.this);
                    }
                })
                .setCancelable(false)
                .show();
    }

//    private int dp2px(int dp){
//        float density = getResources().getDisplayMetrics().density;
//        return (int) (dp * density + 0.5f);
//    }

    /************************************* android6.0权限处理 ***********************************/

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void openPicker(){
        ImageSelector.open(this, imageConfig);
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void showPermissionHint(final PermissionRequest reauest){
        showPermissionDialog();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void neverAskAgain(){
        showPermissionDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CarInfoActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
    /***************************************** end ************************************************/
}
