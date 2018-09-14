package com.bxchongdian.app.views.activities;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.model.bean.RechargeRecordBean;
import com.bxchongdian.model.response.RechargeRecordResponse;
import com.bxchongdian.presenter.recharge.RechargeContract;
import com.bxchongdian.presenter.recharge.RechargePresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/3/3 0003.
 */
@Route(path = "/mime/recharge_record")
public class RechargeRecordActivity extends LvBaseAppCompatActivity implements RechargeContract.View{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private RechargePresenter presenter;
    private RechargeAdapter adapter;
    private List<RechargeRecordBean> list;

    public static void navigation(){
        ARouter.getInstance().build("/mime/recharge_record").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_recharge_record);
    }

    @Override
    protected void initView() {
        initToolbarNav("充值记录");
        presenter.getRecordData();
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        presenter = new RechargePresenter();
        presenter.attachView(this);
    }

    @Override
    protected void destroyPresenter() {
        super.destroyPresenter();
        presenter.detachView();
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
        if (msg == null) {
            showToast("网络异常");
        }else {
            showToast(msg);
        }
    }

    @Override
    public void refreshRecordData(RechargeRecordResponse.Content content) {
        if (content.list != null){
            list = content.list;
            if (adapter == null) {
                adapter = new RechargeAdapter();
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            }else {
                adapter = (RechargeAdapter) recyclerView.getAdapter();
                adapter.notifyDataSetChanged();
            }
        }else {
            showToast("没有充值记录");
        }
    }

    class RechargeAdapter extends RecyclerView.Adapter<RechargeAdapter.ViewHolder>{
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recharge_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RechargeRecordBean bean = list.get(position);
            holder.tvNo.setText("交易单号：" + bean.recordSn);
            if (bean.payType == 2){
                holder.tvName.setText("微信充值");
                holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_wechat));
            }else {
                holder.tvName.setText("支付宝充值");
                holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_alipay));
            }
            holder.tvDate.setText(bean.fetchTime);
            holder.tvMoney.setText(String.format("%.2f", bean.money/100f));

        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.record_pay_no)
            TextView tvNo;
            @BindView(R.id.record_pay_name)
            TextView tvName;
            @BindView(R.id.record_pay_date)
            TextView tvDate;
            @BindView(R.id.record_pay_money)
            TextView tvMoney;
            @BindView(R.id.record_pay_img)
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
