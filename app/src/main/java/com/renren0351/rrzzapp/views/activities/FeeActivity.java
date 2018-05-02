package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.utils.MyItemDecoration;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.rrzzapp.wigets.BarChartView;
import com.renren0351.model.bean.FeeBean;
import com.renren0351.presenter.fee.FeeContract;
import com.renren0351.presenter.fee.FeePresenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import cn.com.leanvision.baseframe.util.LvTimeUtil;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/main/fee")
public class FeeActivity extends LvBaseAppCompatActivity implements FeeContract.View{
    @BindView(R.id.tv_station_name)
    TextView tvName;
    @BindView(R.id.chart)
    BarChartView chart;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private LinkedHashMap<Integer,Float> map;
//    private static final String TAG = FeeActivity.class.getSimpleName();
    private FeePresenter presenter;
    private List<ItemFee> list;
    private FeeAdapter adapter;

    public static void navigation(String stationId,String stationName){
        ARouter.getInstance().build("/main/fee")
                .withString("stationId",stationId)
                .withString("stationName",stationName)
                .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_fee);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initToolbarNav("电价详情");
        presenter.queryFees(getIntent().getStringExtra("stationId"));
        tvName.setText(getIntent().getStringExtra("stationName"));
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        presenter = new FeePresenter();
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
        if (LvTextUtil.isEmpty(msg)) {
            showToast("网络异常");
        }else {
            showToast(msg);
        }
    }

    @Override
    public void getFees(List<FeeBean> list) {
        if ( list != null && list.size() > 0){
            for (FeeBean bean: list){
                if (bean.templateType.equals("e")){
                    refreshUI(bean);
                    break;
                }
            }
        }
    }

    private void refreshUI(FeeBean f) {
        if (f != null){
            int index = 0;
            map =f.feeMap;
//            map = new LinkedHashMap<>();
//            map.put(34,0.6f);
//            map.put(44,0.8f);
//            map.put(68,1.2f);
//            map.put(80,1.0f);
//            map.put(96,0.8f);
            ArrayList<String> xData = new ArrayList<>();
            ArrayList<Float> yData = new ArrayList<>();
            Iterator<Integer> it = map.keySet().iterator();
            list = new ArrayList<>();
            while (it.hasNext()){
                int key = it.next();
                list.add(new ItemFee(LvTimeUtil.getTime(index) + "----" + LvTimeUtil.getTime(key),map.get(key)));
                xData.add(LvTimeUtil.getTime(key));
                yData.add(map.get(key));
                index = key;
            }
            chart.setXData(xData);
            chart.setYData(yData);
            chart.setLegendText("电价：元/度");
            chart.setLegendColor(getResources().getColor(R.color.colorPrimary));
            chart.invalidate();
            if (adapter == null){
                adapter = new FeeAdapter();
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setHasFixedSize(true);
                recyclerView.addItemDecoration(new MyItemDecoration(this,MyItemDecoration.HORIZONTAL));
                recyclerView.setAdapter(adapter);
            }else {
                adapter = (FeeAdapter) recyclerView.getAdapter();
                adapter.notifyDataSetChanged();
            }

        }

    }

    class FeeAdapter extends RecyclerView.Adapter<FeeAdapter.ViewHolder>{
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fee,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ItemFee itemFee = list.get(position);
            holder.tvTime.setText(itemFee.duration);
            holder.tvFee.setText(itemFee.fee + " 元/度");
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.tv_time)
            TextView tvTime;
            @BindView(R.id.tv_fee)
            TextView tvFee;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    class ItemFee{
        public String duration;
        public float fee;
        public ItemFee(String duration, float fee){
            this.duration = duration;
            this.fee = fee;
        }
    }
}
