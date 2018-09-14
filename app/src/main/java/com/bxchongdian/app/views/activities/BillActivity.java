package com.bxchongdian.app.views.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.utils.MyItemDecoration;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.model.bean.ChargeRecordBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/login/mime/bill")
public class BillActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_sum)
    TextView tvSum;
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.bt_next)
    Button btNext;
    @BindView(R.id.ll_next)
    LinearLayout llNext;

    private SparseBooleanArray booleanArray;
    private boolean isAll;
    private int count = 0;
    private float sum = 0;
    private Drawable unselectedIcon;
    private Drawable selectedIcon;
    private List<ChargeRecordBean> dataList;
    public static void navigation() {
        ARouter.getInstance().build("/login/mime/bill").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_bill);
    }

    @Override
    protected void initView() {
        initToolbarNav("发票");
        initList();
        unselectedIcon = getResources().getDrawable(R.drawable.ic_unselect);
        selectedIcon = getResources().getDrawable(R.drawable.ic_select);
        booleanArray = new SparseBooleanArray(dataList.size());
        setBooleanArray(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MyItemDecoration(this,MyItemDecoration.HORIZONTAL));
        BillAdapter adapter = new BillAdapter();
        adapter.setDataList(dataList);
        recyclerView.setAdapter(adapter);
    }

    @OnClick({R.id.tv_all, R.id.bt_next, R.id.ll_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_all:   //全选
                if (isAll){
                    isAll = false;
                    unselectedIcon.setBounds(0,0,unselectedIcon.getMinimumWidth(),unselectedIcon.getMinimumHeight());
                    tvAll.setCompoundDrawables(null,null,unselectedIcon,null);
                    count = 0;
                    sum = 0;
                    setBooleanArray(false);
                    btNext.setEnabled(false);
                }else {
                    isAll = true;
                    selectedIcon.setBounds(0,0,selectedIcon.getMinimumWidth(),selectedIcon.getMinimumHeight());
                    tvAll.setCompoundDrawables(null,null,selectedIcon,null);
                    count = booleanArray.size();
                    sum = getAllSum(dataList);
                    setBooleanArray(true);
                    btNext.setEnabled(true);
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                tvSum.setText("已选择" + count + "，共计" + sum + "元");
                break;
            case R.id.bt_next:
                BillInfoActivity.navigation(sum);
                break;
            case R.id.ll_next:
                break;
        }
    }

    /**
     * 获取总费用
     * @param dataList
     * @return
     */
    private float getAllSum(List<ChargeRecordBean> dataList){
        float temp = 0;
        for (ChargeRecordBean bean : dataList){
            temp += Float.parseFloat(bean.adbalance);
        }
        return temp;
    }

    /**
     * 将数组全部设置成true或false
     * @param isSelected
     */
    private void setBooleanArray(boolean isSelected){
        for (int i = 0; i < dataList.size(); i++) {
            booleanArray.put(i,isSelected);
        }
    }

    //用于测试
    void initList(){
        dataList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            ChargeRecordBean bean = new ChargeRecordBean();
            bean.adbalance = "23.00";
            bean.batch = "123456789987";
            bean.transtime = "2017-06-08";
            dataList.add(bean);
        }
    }


    class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder>{

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
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ChargeRecordBean bean = dataList.get(position);
            holder.tvName.setText("怀柔乐园大街充电站"); // FIXME: 2017/5/24 缺少该字段
            holder.tvCode.setText(String.format("订 单 号：%s", bean.batch));
            holder.tvDate.setText(String.format("充电日期：%s", bean.transtime));
            holder.tvType.setText("支付方式：账户余额");
            holder.tvMoney.setText(bean.adbalance);
            if (!booleanArray.get(position)){
                holder.ivSelect.setImageDrawable(unselectedIcon);
            }else {
                holder.ivSelect.setImageDrawable(selectedIcon);
            }
            holder.llroot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (booleanArray.get(position)){
                        holder.ivSelect.setImageDrawable(unselectedIcon);
                        booleanArray.put(position,false);
                        count--;
                        sum -= Float.parseFloat(bean.adbalance);
                    }else {
                        holder.ivSelect.setImageDrawable(selectedIcon);
                        booleanArray.put(position,true);
                        count++;
                        sum += Float.parseFloat(bean.adbalance);
                    }
                    tvSum.setText("已选择" + count + "，共计" + sum + "元");
                    if (count > 0){
                        btNext.setEnabled(true);
                    }else {
                        btNext.setEnabled(false);
                    }
                }
            });

        }


        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
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
            @BindView(R.id.iv_select)
            ImageView ivSelect;
            @BindView(R.id.ll_root)
            LinearLayout llroot;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                ivSelect.setVisibility(View.VISIBLE);
            }
        }
    }
}
