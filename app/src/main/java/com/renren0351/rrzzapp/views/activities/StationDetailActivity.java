package com.renren0351.rrzzapp.views.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.LvAppUtils;
import com.renren0351.rrzzapp.LvApplication;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.custom.toast.IToast;
import com.renren0351.rrzzapp.custom.toast.ToastUtils;
import com.renren0351.rrzzapp.utils.MyItemDecoration;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.rrzzapp.wigets.flexbox.TagFlowLayout;
import com.renren0351.rrzzapp.wigets.flexbox.adapters.GunTagAdapter;
import com.renren0351.rrzzapp.wigets.flexbox.interfaces.OnFlexboxSubscribeListener;
import com.renren0351.model.bean.ItemGunsBean;
import com.renren0351.model.bean.StationDetailBean;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.OrderResponse;
import com.renren0351.model.response.StationStatusResponse;
import com.renren0351.presenter.appointment.QueryOrderContract;
import com.renren0351.presenter.appointment.QueryOrderPresenter;
import com.renren0351.presenter.main.StationDetailContract;
import com.renren0351.presenter.main.StationDetailPresenter;
import com.trello.rxlifecycle.ActivityEvent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/********************************
 * Created by lvshicheng on 2017/4/26.
 ********************************/
@Route(path = "/main/stationdetail")
public class StationDetailActivity extends LvBaseAppCompatActivity implements StationDetailContract.View, QueryOrderContract.View {

    @BindView(R.id.m_recycler_view)
    RecyclerView mRecyclerView;

    private String                 stationId;
    private StationDetailPresenter presenter;
    private QueryOrderPresenter    queryOrderPresenter;

    private UMShareListener   mShareListener;
    private ShareAction       mShareAction;
    private PopupWindow       popWindow;
    private ItemGunsBean      itemGunsBean;
    private StationDetailBean detailBean;

    public static void navigation(String stationId, String stationName, double lat, double lng) {
        ARouter.getInstance().build("/main/stationdetail")
            .withString("stationId", stationId)
            .withString("stationName", stationName)
            .withDouble("lat", lat)
            .withDouble("lng", lng)
            .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_station_detail);
    }

    @Override
    protected void initPresenter() {
        presenter = new StationDetailPresenter();
        presenter.attachView(this);
        queryOrderPresenter = new QueryOrderPresenter();
        queryOrderPresenter.attachView(this);
    }

    @Override
    protected void destroyPresenter() {
        presenter.detachView();
        queryOrderPresenter.detachView();
    }

    @Override
    protected void initView() {
        initToolbarNav("充电站详情");
        initPopWindow();
        stationId = getIntent().getStringExtra("stationId");
        presenter.getSubstationSummary(stationId);

        initShare();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.getSubstationSummary(stationId);
    }

    /**
     * 初始化底部弹窗
     */
    private void initPopWindow() {
        View popView = LayoutInflater.from(this).inflate(R.layout.view_pop_select, null);
        TextView tvOrder = (TextView) popView.findViewById(R.id.tv_one);
        tvOrder.setText("我要预约");
        TextView tvScan = (TextView) popView.findViewById(R.id.tv_two);
        tvScan.setText("查看充电");
//        TextView tvThree = (TextView) popView.findViewById(R.id.tv_three);
//        tvThree.setVisibility(View.GONE);
        TextView tvCancel = (TextView) popView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(popListener);
        tvOrder.setOnClickListener(popListener);
        tvScan.setOnClickListener(popListener);
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

    /**
     * 弹窗点击监听
     */
    View.OnClickListener popListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_one:     //跳转预约界面
                    if (LvAppUtils.isLogin()) {
                        queryOrderPresenter.queryOrder();
                    } else {
                        LoginActivity.navigation(false);
                    }
                    break;
                case R.id.tv_two://跳转查看充电界面
                    //判断是否充电
                    chargingNavigation();
                    break;
                case R.id.tv_cancel:
                    break;
            }
            popWindow.dismiss();
        }
    };

    private void chargingNavigation(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("substationId", stationId);
        map.put("cpId", itemGunsBean.cpId);
        map.put("cpinterfaceId", itemGunsBean.gunId);
        ApiComponentHolder.sApiComponent
                .apiService()
                .getOtherStationStatus(map)
                .compose(this.<StationStatusResponse>bindUntilEvent(ActivityEvent.DESTROY))
                .compose(SchedulersCompat.<StationStatusResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<StationStatusResponse>() {
                    @Override
                    public void onNext(StationStatusResponse stationStatusResponse) {
                        super.onNext(stationStatusResponse);
                        dismissLoadingDialog();
                        if (stationStatusResponse.isSuccess()){
                            if (stationStatusResponse.contentList != null && stationStatusResponse.contentList.size() > 0) {
                                if ("0003".equals(stationStatusResponse.contentList.get(0).workstate)){//处于工作状态
                                    TempChargingActivity.navigation(stationId, null, itemGunsBean.cpId, itemGunsBean.gunId);
                                }else {
                                    showToast("充电枪没有充电");
                                }
                            }else {
                                showToast("充电枪没有充电");
                            }
                        }else {
                            showToast(stationStatusResponse.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissLoadingDialog();
                        showToast("网络异常");
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadingDialog();
                    }
                });
    }

    /**
     * 显示弹窗
     */
    private void showPopWindow() {
        if (!popWindow.isShowing()) {
            popWindow.showAtLocation(popWindow.getContentView(), Gravity.BOTTOM, 0, 0);
            setBackgroundAlpha(0.7f);
        }
    }

    /**
     * 初始化分享
     */
    private void initShare() {
        mShareListener = new CustomShareListener(this);
        mShareAction = new ShareAction(this).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
            .setShareboardclickCallback(new ShareBoardlistener() {
                @Override
                public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                    switch (snsPlatform.mShowWord) {
                        case "umeng_sharebutton_copy":
                            showToast("复制文本按钮");
                            break;
                        case "umeng_sharebutton_copyurl":
                            showToast("复制文本按钮");
                            break;
                        default:
                            // FIXME: 2017/7/26 需要修改分享的网址
//                            String url = "https://mobile.umeng.com/";
                       //     String url = "https://www.pgyer.com/Buca";
                            String url = "http://117.78.40.137/cs-cloud-demo/loadapp/loadapp.html";

                            UMWeb web = new UMWeb(url);
                            /*web.setTitle("欢迎使用杰电APP");
                            web.setDescription("绿色,节能,低碳,畅享健康生活，北京双杰电动汽车充电桩欢迎您!");*/
                            web.setTitle("欢迎使用人人桩主APP");
                            web.setDescription("绿色,节能,低碳,畅享健康生活，国晶售电有限公司欢迎您!");
                            web.setThumb(new UMImage(StationDetailActivity.this, R.mipmap.ic_launcher));
                            new ShareAction(StationDetailActivity.this).withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(mShareListener)
                                .share();
                            break;
                    }
                }
            });
    }

    @OnClick(R.id.iv_share)
    public void clickShare() {
        mShareAction.open();
    }

    @OnClick(R.id.iv_error)
    public void clickErrorCorrection() {
        showErrorCorrectionDialog();
    }


    public static String[] items
        = {"地理位置错误", "位置描述错误", "停车费信息错误", "终端无法充电", "其他"};

    private void showErrorCorrectionDialog() {
        new AlertDialog.Builder(this)
            .setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DebugLog.log("which: %d", which);
                    if (which == 0) {
                        double lat = getIntent().getDoubleExtra("lat", 0.0f);
                        double lng = getIntent().getDoubleExtra("lng", 0.0f);
                        ErrorCorrectionLocationActivity.navigation(lat, lng, stationId);
                    } else {
                        ErrorCorrectionActivity.navigation(which, stationId);
                    }
                }
            })
            .show();
    }

    @Override
    public void showLoading(String msg) {
        showLoadingDialog(msg);
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
    public void getSummarySuccess(StationDetailBean stationDetailBean) {
        detailBean = stationDetailBean;
        initRecyclerView(stationDetailBean);
    }

    private void initRecyclerView(final StationDetailBean stationDetailBean) {
        if (mRecyclerView.getAdapter() == null) {
            RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(lm);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.addItemDecoration(new MyItemDecoration(this, MyItemDecoration.HORIZONTAL));

            OnFlexboxSubscribeListener listener = new OnFlexboxSubscribeListener<ItemGunsBean>() {
                @Override
                public void onSubscribe(List<ItemGunsBean> selectedItem) {
                    // stationId cpid cpinterfaceId
                    if (!LvTextUtil.isArrayEmpty(selectedItem)) {
                        itemGunsBean = selectedItem.get(0);
                        if ("离线".equals(itemGunsBean.state)) {
                            showToast("充电桩离线了");
                        }else {
                            showPopWindow();
                        }
                    }
                }
            };
            Adapter adapter = new Adapter(stationDetailBean.chargingPileList, listener);
            mRecyclerView.setAdapter(adapter);
        } else {
            Adapter adapter = (Adapter) mRecyclerView.getAdapter();
            adapter.setDataList(stationDetailBean.chargingPileList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void querySuccess(OrderResponse.Order order) {
        showToast("您当前有一个预约");
    }

    @Override
    public void noOrder(String msg) {
        if (detailBean != null && detailBean.chargingPileList != null) {
            OrderSettingActivity.navigation(stationId, itemGunsBean.cpId, itemGunsBean.gunId,
                null, false, 0, null);
        }
    }


    static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements View.OnClickListener {
        private OnRecyclerViewItemClickListener mOnItemClickListener = null;
        private OnFlexboxSubscribeListener<ItemGunsBean> listener;

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                //注意这里使用getTag方法获取数据
                mOnItemClickListener.onItemClick(v, (StationDetailBean.ChargingPileListEntity) v.getTag());
            }
        }

        //define interface
        interface OnRecyclerViewItemClickListener {
            void onItemClick(View view, StationDetailBean.ChargingPileListEntity entity);
        }

        void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        private List<StationDetailBean.ChargingPileListEntity> dataList;

        Adapter(List<StationDetailBean.ChargingPileListEntity> dataList,
                OnFlexboxSubscribeListener<ItemGunsBean> onFlexboxSubscribeListener) {
            this.dataList = dataList;
            this.listener = onFlexboxSubscribeListener;
        }

        void setDataList(List<StationDetailBean.ChargingPileListEntity> dataList) {
            this.dataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pile_item, parent, false);
            view.setOnClickListener(this);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            StationDetailBean.ChargingPileListEntity cpe = dataList.get(position);
            holder.shortCode.setText(cpe.getShortCode());
            holder.longCode.setText(cpe.cpId);
            holder.pileType.setText(cpe.getCpType());
            //额定功率
            if (!LvTextUtil.isEmpty(cpe.ratedPower)){
                holder.pilePower.setText(cpe.ratedPower);
            }else {
                holder.pilePower.setText(cpe.getPower());
            }

            holder.pileState.setText(cpe.getState());
            holder.itemView.setTag(cpe);

            // 以下是显示枪的逻辑
            if (holder.sourceData == null) {
                holder.sourceData = new ArrayList<>();
            }
            if (holder.adapter == null) {
                holder.adapter = new GunTagAdapter<>(holder.itemView.getContext(), holder.sourceData);
            }
            int totalGuns = Integer.parseInt(cpe.cpinterfaceId);
            holder.sourceData.clear();
            for (int i = 0; i < totalGuns; i++) {
                holder.sourceData.add(new ItemGunsBean(i + 1, cpe.cpId, cpe.getGunState(i + 1), position));
            }
            if (totalGuns > 2) {
                holder.maxFlowLayout.setVisibility(View.VISIBLE);
                holder.maxFlowLayout.setAdapter(holder.adapter);
            } else {
                holder.minFlowLayout.setAdapter(holder.adapter);
                holder.maxFlowLayout.setVisibility(View.GONE);
            }
            holder.adapter.setOnSubscribeListener(listener);
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.short_code)
            TextView      shortCode;
            @BindView(R.id.long_code)
            TextView      longCode;
            @BindView(R.id.pile_type)
            TextView      pileType;
            @BindView(R.id.pile_power)
            TextView      pilePower;
            @BindView(R.id.pile_state)
            TextView      pileState;
            @BindView(R.id.flow_layout_max)
            TagFlowLayout maxFlowLayout;
            @BindView(R.id.flow_layout_min)
            TagFlowLayout minFlowLayout;

            List<ItemGunsBean>          sourceData;
            GunTagAdapter<ItemGunsBean> adapter;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    private static class CustomShareListener implements UMShareListener {
        private WeakReference<Activity> mActivity;

        private CustomShareListener(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtils.getInstance(LvApplication.getContext()).makeTextShow(platform + " 分享成功啦", IToast.LENGTH_SHORT);
//            ToastUtils.makeText(mActivity.get(), platform + " 分享成功啦", CustomToast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtils.getInstance(LvApplication.getContext()).makeTextShow(platform + " 分享失败啦", IToast.LENGTH_SHORT);
//            ToastUtils.makeText(mActivity.get(), platform + " 分享失败啦", CustomToast.LENGTH_SHORT).show();
            if (t != null) {
                DebugLog.log("throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtils.getInstance(LvApplication.getContext()).makeTextShow(platform + " 分享取消了", IToast.LENGTH_SHORT);
//            ToastUtils.makeText(mActivity.get(), platform + " 分享取消了", CustomToast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置背景透明度
     * @param alpha
     */
    private void setBackgroundAlpha(float alpha){
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = alpha;
        getWindow().setAttributes(params);
    }
}
