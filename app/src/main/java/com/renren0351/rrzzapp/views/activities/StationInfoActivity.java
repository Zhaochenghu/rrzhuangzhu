package com.renren0351.rrzzapp.views.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.custom.ChargingDialog;
import com.renren0351.rrzzapp.custom.DonutProgress;
import com.renren0351.rrzzapp.event.FinishActivityEvent;
import com.renren0351.rrzzapp.event.GetDataEvent;
import com.renren0351.rrzzapp.services.ServiceUtil;
import com.renren0351.rrzzapp.utils.FeeUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.bean.FeeBean;
import com.renren0351.model.bean.StationInfoBean;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.request.StartChargingRequest;
import com.renren0351.model.response.ChargingResponse;
import com.renren0351.model.response.MyWalletResponse;
import com.renren0351.model.response.StationStatusResponse;
import com.renren0351.model.storage.AppInfosPreferences;
import com.renren0351.presenter.fee.FeeContract;
import com.renren0351.presenter.fee.FeePresenter;
import com.renren0351.presenter.station.StationInfoContract;
import com.renren0351.presenter.station.StationInfoPresenter;
import com.renren0351.presenter.usercase.MyWalletCase;
import com.renren0351.rrzzapp.views.dialog.NiftyDialogBuilder;
import com.renren0351.rrzzapp.views.dialog.loaddialog.LoadingDialog;
import com.trello.rxlifecycle.ActivityEvent;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvCommonUtil;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static com.renren0351.rrzzapp.LvApplication.getContext;
import static com.umeng.socialize.net.utils.AesHelper.newString;

/********************************
 * Created by lvshicheng on 2017/4/19.
 * modify by 赵成虎 on 2018/5/4
 ********************************/
@Route(path = "/login/charging/station")
public class StationInfoActivity extends LvBaseAppCompatActivity implements StationInfoContract.View,
    FeeContract.View {

    @BindView(R.id.sub_name)
    TextView       subName;
    @BindView(R.id.pile_code)
    TextView       pileCode;
    @BindView(R.id.pile_type)
    TextView       pileType;
    @BindView(R.id.pile_power)
    TextView       tvPower;
    @BindView(R.id.tv_mode)
    TextView       tvMode;
    @BindView(R.id.tv_type)
    TextView       tvType;
    @BindView(R.id.tv_gun)
    TextView       tvGun;
    @BindView(R.id.tv_e)
    TextView       tvE;   //电费
    @BindView(R.id.tv_s)
    TextView       tvS;   //服务费
    @BindView(R.id.tv_p)
    TextView       tvP;   //停车费
    @BindView(R.id.et_num)
    EditText       etNum;
    @BindView(R.id.rl_way)
    RelativeLayout rlWay;

    private static final int MODE_AUTO   = 0;
    private static final int MODE_MONEY  = 1;
    private static final int MODE_POWER  = 2;
    private static final int MODE_TIME   = 3;
    private              int currentMode = MODE_MONEY;
    private StationInfoPresenter presenter;
    private FeePresenter         feePresenter;
    private StationInfoBean      mStationInfoBean;
    private MyWalletCase         walletCase;
    private Subscription         subscription;
    private Subscription         statusSubscription;
    private String               amount;
    private PopupWindow          popWindow;
    private String[]             strMode = {"自动充电", "按金额充电", "按时间充电", "按电量充电"};
    private String               gunId;
    private Dialog               dialog;
    private ChargingDialog       chargingDialog;

    public static void navigation(StationInfoBean stationInfoBean,String gunId) {
        ARouter.getInstance()
            .build("/login/charging/station")
            .withSerializable("stationInfoBean",stationInfoBean)
            .withString("gunId", gunId)
            .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_changing_select);
    }

    @Override
    protected void initPresenter() {
        presenter = new StationInfoPresenter();
        presenter.attachView(this);
        feePresenter = new FeePresenter();
        feePresenter.attachView(this);
    }

    @Override
    protected void destroyPresenter() {
        presenter.detachView();
        feePresenter.detachView();
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbarNav("充电");
        etNum.setFocusable(false);

        gunId = getIntent().getStringExtra("gunId");
        mStationInfoBean = (StationInfoBean) getIntent().getSerializableExtra("stationInfoBean");
        refreshDisplay();
        initPopWindow();
        initEvent();
    }

    //启动充电按钮
    @OnClick(R.id.btn_start_charge)
    public void clickStartCharge() {
        LvCommonUtil.hideSoftInput(this);
        amount = etNum.getText().toString().trim();
        if (currentMode != MODE_AUTO){
            try {
                if (LvTextUtil.isEmpty(amount)){
                    showToast("请输入值");
                    return;
                }
                int num = Integer.parseInt(amount);
                if (num > 1000){
                    showToast("输入的值不能大于1000");
                }
//                else if (num < 10){
//                    showToast("输入的值不能小于10");
//                }
                else {
                    checkThePayPwdHasSet();
                }
            }catch (Exception e){
                showToast("输入错误，请重新输入");
                etNum.setText("");
            }
        }else {
            checkThePayPwdHasSet();
        }
    }

    //输入金额
    @OnClick(R.id.et_num)
    public void clickAmount() {
        LvCommonUtil.showSoftInput(this, etNum);
    }

    //选择充电模式
    @OnClick(R.id.ll_mode)
    public void selectMode() {
        if (!popWindow.isShowing()) {
            popWindow.showAtLocation(popWindow.getContentView(), Gravity.BOTTOM, 0, 0);
            setBackgroundAlpha(0.7f);
        }
    }

    //查询电价
    @OnClick(R.id.ll_free)
    public void clickFee() {
        if (mStationInfoBean != null) {
            FeeActivity.navigation(mStationInfoBean.areaId, mStationInfoBean.areaName);
        }

    }

    @Override
    public void showLoading(String msg) {
        if (LvTextUtil.isEmpty(msg)) {
            showLoadingDialog();
        } else {
            showLoadingDialog(msg);
        }
    }

    @Override
    public void showNormal() {
        dismissLoadingDialog();
    }

    @Override
    public void requestFailed(String msg) {
        closeSubscription();
        showNormal();
        if (LvTextUtil.isEmpty(msg)) {
            showToast(R.string.network_not_available);
        } else {
            showToast(msg);
            if (msg.contains("充值")){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MoneyActivity.navigation();
                    }
                }, 2000);

            } else if (msg.contains("您当前有未完成的启动充电，请先停止充电或者取消预约！")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StopdialogActivity.navigation();
                    }
                },1000);
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("main", "test ..........................");
    }

    @Override
    public void queryStationInfoSuccess(StationInfoBean mStationInfoBean) {
    }

    @Override
    public void queryStationInfoFailed(String msg) {
    }

    /**
     * 充电启动成功
     */
    public void startChargingSuccess() {
        ServiceUtil.startChargingQuery();
        AppInfosPreferences.get().setCharging("1");
        AppInfosPreferences.get().setChargeStationName(mStationInfoBean.areaName);
        RxBus.getInstance().postEvent(new FinishActivityEvent());
        finish();
    }

    @Override
    public void startChargingFailed(String msg) {
        //您当前有未完成的启动充电，请先停止充电或者取消预约！
        //充电枪已经被别人占用！
        requestFailed(msg);
    }
    private void refreshDisplay() {
        feePresenter.queryFees(mStationInfoBean.areaId);
        subName.setText(mStationInfoBean.areaName);
        pileCode.setText(mStationInfoBean.cpId);
        pileType.setText(mStationInfoBean.getCpType());
        tvPower.setText(mStationInfoBean.ratedPower);
        tvGun.setText("枪" + gunId);
    }

    /**
     * 初始化底部弹窗
     * 用于选择充电模式
     */
    private void initPopWindow() {
        View popView = LayoutInflater.from(this).inflate(R.layout.view_pop_five, null);
        TextView tvAuto = (TextView) popView.findViewById(R.id.tv_one);
        tvAuto.setText(strMode[0]);
        TextView tvMoney = (TextView) popView.findViewById(R.id.tv_two);
        tvMoney.setText(strMode[1]);
        TextView tvTime = (TextView) popView.findViewById(R.id.tv_three);
        tvTime.setText(strMode[2]);
        TextView tvPower = (TextView) popView.findViewById(R.id.tv_four);
        tvPower.setText(strMode[3]);
        TextView tvCancel = (TextView) popView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(popListener);
        tvMoney.setOnClickListener(popListener);
        tvTime.setOnClickListener(popListener);
        tvAuto.setOnClickListener(popListener);
        tvPower.setOnClickListener(popListener);
        popWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popWindow.setFocusable(true);
        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(false);
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1);
            }
        });
    }

    View.OnClickListener popListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_one:
                    /*currentMode = MODE_AUTO;
                    tvMode.setText(strMode[0]);
                    rlWay.setVisibility(View.GONE);*/
                    Toast.makeText(StationInfoActivity.this,"交流桩只能按金额充电",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_two:
                    currentMode = MODE_MONEY;
                    tvMode.setText(strMode[1]);
                    rlWay.setVisibility(View.VISIBLE);
                    etNum.setText("");
                    tvType.setText("充电金额(元)");
                    etNum.setHint("输入金额,至少为10元");
                    break;
                case R.id.tv_three:
                   /* currentMode = MODE_TIME;
                    tvMode.setText(strMode[2]);
                    rlWay.setVisibility(View.VISIBLE);
                    etNum.setText("");
                    tvType.setText("充电时间(分钟)");
                    etNum.setHint("输入时间，至少为10分钟");*/
                    Toast.makeText(StationInfoActivity.this,"交流桩只能按金额充电",Toast.LENGTH_SHORT).show();

                    break;
                case R.id.tv_four:
                  /*currentMode = MODE_POWER;
                  tvMode.setText(strMode[3]);
                  rlWay.setVisibility(View.VISIBLE);
                  tvType.setText("充电电量(KWh)");
                  etNum.setHint("输入电量,至少为10度");*/
                    Toast.makeText(StationInfoActivity.this,"交流桩只能按金额充电",Toast.LENGTH_SHORT).show();
                  break;
                case R.id.tv_cancel:
                    break;
            }
            popWindow.dismiss();
        }
    };

    @Override
    public void getFees(List<FeeBean> list) {
        float[] fee = FeeUtils.getCurrentFree(list);
        tvE.setText(String.format("%.2f 元/度", fee[0]));
        tvS.setText(String.format("%.2f 元/度", fee[1]));
        tvP.setText("无");
    }

    /**
     * 发送开始充电请求
     */
    private void startChargeRequest() {
        // 开始充电参数
        StartChargingRequest request = new StartChargingRequest();
        if (currentMode == MODE_AUTO){
            request.settingNumber = 0;
        }else {
            request.settingNumber = Float.valueOf(amount);
        }

        request.chargingMode = "0" + currentMode;
        request.command = "0";
        request.areaId = this.mStationInfoBean.areaId;
        request.cpId = this.mStationInfoBean.cpId;
        request.cpinterfaceId = gunId;
        //开始充电
        presenter.startCharging(request);
    }
    @Override
    public void callBack(ChargingResponse.Charging charging) {
        DebugLog.log("-------------------------------->" + charging.res);
        switch (charging.res){
            case "0":       //启动充电请求成功
                showChargingDialog();
                subscription = Observable.interval(0, 3, TimeUnit.SECONDS)
                        .compose(SchedulersCompat.<Long>applyNewSchedulers())
                        .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(new SimpleSubscriber<Long>() {
                            @Override
                            public void onNext(Long aLong) {
                                super.onNext(aLong);
                                queryStatus();
                            }
                        });
                break;
            default:
                closeSubscription();
                showNormal();
                showMsg(charging.res);
                break;
        }
    }

    private void showMsg(String res) {
        switch (res){
            case "1":
                showToast("没有查找到区站编号");
                break;
            case "2":
                showToast("没有查找到充电桩编号");
                break;
            case "3":
                showToast("没有查找到枪口号");
                break;
            case "4":
                showToast("当前枪口没有关闭无法开始");
                break;
            case "5":
                showToast("当前枪口没有开始无法关闭");
                break;
            case "6":
            case "7":
                showToast("服务器出现错误了");
            case "8":
                showToast("服务器出现错误了");
                break;
        }
    }


    /**
     * 判断充电桩是否启动成功
     * 根据状态位判断 0003-->工作
     */
    private void queryStatus(){
        statusSubscription = ApiComponentHolder.sApiComponent
                .apiService()
                .getStationStatus()
                .take(1)
                .compose(this.<StationStatusResponse>bindUntilEvent(ActivityEvent.DESTROY))
                .compose(SchedulersCompat.<StationStatusResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<StationStatusResponse>() {

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        closeChargingDialog();
                        closeSubscription();
                        showToast("网络异常");
                    }

                    @Override
                    public void onNext(StationStatusResponse stationStatusResponse) {
                        if (stationStatusResponse.isSuccess()){
                            if (stationStatusResponse.contentList != null && stationStatusResponse.contentList.size() > 0) {
                                if ("0003".equals(stationStatusResponse.contentList.get(0).workstate)){//启动成功
                                    closeChargingDialog();
                                    closeSubscription();
                                    startChargingSuccess();
                                }
                            }
                        }else {
                            closeChargingDialog();
                            closeSubscription();
                            showToast(stationStatusResponse.msg);
                        }
                    }
                });
    }

    /**
     * 核查是否有支付密码
     */
    private void checkThePayPwdHasSet() {
        if (walletCase == null) {
            walletCase = new MyWalletCase();
        }

        walletCase.createObservable(new SimpleSubscriber<MyWalletResponse>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                e.printStackTrace();
                dismissLoadingDialog();
                showToast("网络异常");
            }

            @Override
            public void onNext(MyWalletResponse myWalletResponse) {
                dismissLoadingDialog();
                if (myWalletResponse.isSuccess()) {
                    //有支付密码
                    if (myWalletResponse.content.hasPassword) {
                        PayPasswordActivity.navigation();
                    } else {//没有支付密码
                        startChargeRequest();
                    }
                } else {
                    showToast(myWalletResponse.msg);
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }
        });
    }

    /**
     * 接收支付密码验证结果
     */
    private void initEvent() {
        //在PayPasswordActivity中checkPayPwd发送事件
        //注册GetDataEvent事件
        RxBus.getInstance().toObservable(GetDataEvent.class)
            .compose(this.<GetDataEvent>bindUntilEvent(ActivityEvent.DESTROY))
            .compose(SchedulersCompat.<GetDataEvent>applyNewSchedulers())
            .subscribe(new Action1<GetDataEvent>() {
                @Override
                public void call(GetDataEvent getDataEvent) {
                    if (getDataEvent.data.equals("true")) {
                        startChargeRequest();
                    }
                }
            });
    }

    /**
     * 设置背景透明度
     *
     * @param alpha
     */
    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = alpha;
        getWindow().setAttributes(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeSubscription();
    }

    /**
     * 充电桩启动充电加载Dialog
     */
    private void showChargingDialog(){
        if (dialog == null) {
            chargingDialog = new ChargingDialog();
            dialog = chargingDialog.createDialog(this,"充电桩正在启动中...");
            chargingDialog.getDonutProgress().setOnTimeFinishedListener(new DonutProgress.OnTimeFinishedListener() {
                @Override
                public void onFinished() {
                    closeChargingDialog();
                    closeSubscription();
                    showToast("充电桩启动失败");
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }else if ( !dialog.isShowing()){
            dialog.show();
        }
    }

    /**
     * 关闭启动充电Dialog
     */
    public void closeChargingDialog(){
        if (dialog != null && dialog.isShowing()){
            chargingDialog.close();
            chargingDialog = null;
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 关闭网络请求
     */
    private void closeSubscription(){
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
            subscription = null;
        }

        if (statusSubscription != null && !statusSubscription.isUnsubscribed()){
            statusSubscription.unsubscribe();
            statusSubscription = null;
        }

    }
}
