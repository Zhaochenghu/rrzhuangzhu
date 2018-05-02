package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.utils.MyItemDecoration;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.bean.ChargeRecordBean;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.response.ChargeRecordResponse;
import com.trello.rxlifecycle.ActivityEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/05/19
 *     desc   : 充电记录页
 *     version: 1.0
 * </pre>
 */
@Route(path = "/login/mime/pay_record")
public class PayRecordActivity extends LvBaseAppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no)
    TextView tvNo;

    private int minItem = 2;
    private LinearLayoutManager manager;
    private Adapter adapter;
    private boolean isLoadMore;
    private HashMap<String, Object> request;
    private ChargeRecordResponse.Content content;
    private List<ChargeRecordBean> dataList = new ArrayList<>();
    private String cardId;
    private int currentPage;
    private int prePage;
    private String batchName;

    public static void navigation(String cardId) {
        ARouter.getInstance().build("/login/mime/pay_record")
                .withString("cardId", cardId)
                .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_pay_record);
    }

    @Override
    protected void initView() {
        initToolbarNav("充电记录");
//        refreshRecord(null);
        batchName = getResources().getString(R.string.batch_4);
        cardId = getIntent().getStringExtra("cardId");
        currentPage = 1;
        prePage = 20;
        isLoadMore = false;
        queryChargeRecord(currentPage, prePage, cardId);

    }

    private void refreshRecord(List<ChargeRecordBean> dataList) {
        if (dataList == null || dataList.size() < 1) {
            tvNo.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        } else {
            tvNo.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        //按时间排序
        Collections.sort(dataList, new Comparator<ChargeRecordBean>() {
            @Override
            public int compare(ChargeRecordBean lhs, ChargeRecordBean rhs) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date d1 = sdf.parse(lhs.startTime);
                    Date d2 = sdf.parse(rhs.startTime);
                    if (d1.getTime() > d2.getTime()){
                        return -1;
                    }else if (d1.getTime() < d2.getTime()){
                        return 1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        this.dataList.addAll(dataList);

        if (adapter == null) {
            adapter = new Adapter();
            manager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(manager);
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new MyItemDecoration(this, MyItemDecoration.HORIZONTAL));
            recyclerView.addOnScrollListener(listener);
            adapter.setDataList(this.dataList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter = (Adapter) recyclerView.getAdapter();
            adapter.setDataList(this.dataList);
            adapter.notifyDataSetChanged();
        }
    }

    private void queryChargeRecord(int page, int prePage, String cardId) {
        request = new HashMap<>();
        request.put("page", page);
        request.put("prePage", prePage);
        if (!LvTextUtil.isEmpty(cardId)){
            request.put("cardId", cardId);
        }
        ApiComponentHolder.sApiComponent.apiService()
                .chargeRecord(request)
                .compose(this.<ChargeRecordResponse>bindUntilEvent(ActivityEvent.DESTROY))
                .take(1)
                .compose(SchedulersCompat.<ChargeRecordResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<ChargeRecordResponse>() {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        isLoadMore = false;
                        currentPage--;
                        dismissLoadingDialog();
                        showToast(R.string.network_not_available);
                    }

                    @Override
                    public void onNext(ChargeRecordResponse response) {
                        dismissLoadingDialog();
                        isLoadMore = false;
                        if (response.isSuccess()) {
                            content = response.content;
                            refreshRecord(response.content.items);
                        } else {
                            currentPage--;
                            showToast(response.msg);
                        }
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadingDialog();
                    }
                });
    }

    RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int count = manager.getItemCount();
            int lastPosition = manager.findLastVisibleItemPosition();
            if (!isLoadMore && lastPosition >= count - minItem){
                isLoadMore = true;
                loadMore();
            }
        }
    };

    /**
     * 加载更多
     */
    private void loadMore() {
        if (content.total > currentPage * prePage){
            currentPage ++;
            queryChargeRecord(currentPage, prePage, cardId);
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private List<ChargeRecordBean> dataList;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pay_record_item, parent, false);
            return new ViewHolder(view);
        }

        void setDataList(List<ChargeRecordBean> dataList) {
            this.dataList = dataList;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ChargeRecordBean bean = dataList.get(position);
            holder.tvName.setText(bean.substationName);
            holder.tvCode.setText(String.format("%s：%s", batchName,bean.batch));

            if (!LvTextUtil.isEmpty(bean.transtime)){
                holder.tvDate.setText(String.format("充电日期：%s", bean.transtime));
            }else {
                holder.tvDate.setText(String.format("充电日期：%s", bean.startTime));
            }

            holder.tvType.setText(String.format("支付方式：%s", bean.getPayType()));
            if (bean.transType == 0){
                holder.tvCardId.setVisibility(View.VISIBLE);
                holder.tvCardId.setText(String.format("支付卡号：%s", bean.cardId));
            }else {
                holder.tvCardId.setVisibility(View.GONE);
            }

            holder.tvMoney.setText(String.format("-%.2f", bean.getTransamount()));
            holder.llRoot.setTag(bean);
            holder.llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PayDetailsActivity.navigation((ChargeRecordBean) v.getTag());
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_record_code)
            TextView tvCode;    //订单号
            @BindView(R.id.tv_station_name)
            TextView tvName;    //充电子站
            @BindView(R.id.tv_record_date)
            TextView tvDate;    //充电日期
            @BindView(R.id.tv_record_type)
            TextView tvType;    //支付方式
            @BindView(R.id.tv_record_money)
            TextView tvMoney;   //充电金额
            @BindView(R.id.tv_record_cardId)
            TextView tvCardId;
            @BindView(R.id.ll_root)
            LinearLayout llRoot;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    }
}
