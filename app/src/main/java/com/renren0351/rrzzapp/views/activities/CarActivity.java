package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.utils.MyItemDecoration;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.bean.CarBean;
import com.renren0351.presenter.car.CarContract;
import com.renren0351.presenter.car.CarPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/**
 * Created by admin on 2017-02-16.
 */
@Route(path = "/login/mime/car")
public class CarActivity extends LvBaseAppCompatActivity implements CarContract.View{

    @BindView(R.id.ll_no)
    LinearLayout llNo;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private CarAdapter adapter;
    private CarPresenter presenter;
    public CarBean carBean;
    public static List<CarBean> cars = new ArrayList<>();

    public static void navigation() {
        ARouter.getInstance().build("/login/mime/car").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_car);
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbarNav("我的爱车");
        llNo.setVisibility(View.GONE);
        presenter.queryCars();
    }

    @Override
    protected void initPresenter() {
        presenter = new CarPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void destroyPresenter() {
        super.destroyPresenter();
        presenter.detachView();
    }

    @OnClick({R.id.iv_add,R.id.bt_add})
    public void clickAdd() {
        BindCarActivity.navigation(null);
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
    public void saveCarSuccess() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.queryCars();
    }

    private void refresh() {
        if (adapter == null){
            adapter = new CarAdapter();
            RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(lm);
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new MyItemDecoration(this, MyItemDecoration.HORIZONTAL));
            recyclerView.setAdapter(adapter);
        }else {
            adapter = (CarAdapter) recyclerView.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void refreshCars(List<CarBean> cars) {
        if (cars != null && cars.size() > 0){
            CarActivity.cars.clear();
            CarActivity.cars.addAll(cars);
            llNo.setVisibility(View.GONE);
            refresh();
        }else {
            llNo.setVisibility(View.VISIBLE);
        }
    }

    class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            carBean = cars.get(position);
            holder.rlRoot.setTag(carBean);
            holder.tvCarNumber.setText(carBean.license);
            if (carBean.licenseType.equals("小型汽车")){
                holder.tvCarNumber.setTextColor(getResources().getColor(R.color.lv_white));

                holder.tvCarNumber.setBackgroundColor(getResources().getColor(R.color.license_blue));
            }else {
                holder.tvCarNumber.setTextColor(getResources().getColor(R.color.black));

                holder.tvCarNumber.setBackgroundColor(getResources().getColor(R.color.license_yellow));
            }
            holder.tvCarType.setText(carBean.carType);
            //加载图片
            Glide.with(CarActivity.this)
                    .load(carBean.imgUrl)
                    .placeholder(R.drawable.ic_car_default)
                    .error(R.drawable.ic_car_default)
                    .into(holder.ivCar);
            holder.rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CarInfoActivity.navigation((CarBean) holder.rlRoot.getTag());
                }
            });
        }

        @Override
        public int getItemCount() {
            return cars == null ? 0 : cars.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.rl_root)
            RelativeLayout rlRoot;
            @BindView(R.id.iv_car)
            ImageView ivCar;
            @BindView(R.id.tv_car_type)
            TextView tvCarType;
            @BindView(R.id.tv_car_number)
            TextView tvCarNumber;
            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
