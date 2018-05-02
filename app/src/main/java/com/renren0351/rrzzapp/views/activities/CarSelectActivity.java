package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.event.GetDataEvent;
import com.renren0351.rrzzapp.utils.MyItemDecoration;
import com.renren0351.rrzzapp.utils.ValidationUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.rrzzapp.views.dialog.NiftyDialogBuilder;
import com.renren0351.model.bean.CarInfo;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvFileUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/login/mime/carselect")
public class CarSelectActivity extends LvBaseAppCompatActivity implements View.OnClickListener{
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_other)
    TextView tvOther;

    private static final int STATE_CAR = 1;
    private static final int STATE_TYPE = 2;
    private SelectAdapter adapter;
    private int currentState;
    private CarInfo carInfo;
    private String data;
    public static void navigation(){
        ARouter.getInstance().build("/login/mime/carselect").navigation();
    }
    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_car_select);
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbarNav("选择车型",this);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MyItemDecoration(this,MyItemDecoration.HORIZONTAL));
        adapter = new SelectAdapter();
        tvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCarType();
            }
        });
        getData();
    }

    private void setCarType() {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        View view = View.inflate(this,R.layout.view_car_input,null);
        final EditText etName = (EditText) view.findViewById(R.id.et_car_name);
        final EditText etType = (EditText) view.findViewById(R.id.et_car_type);
        final ValidationUtils utils = new ValidationUtils(this);
        builder.withTitle("输入车型")
                .withDuration(500)
                .setCustomView(view,this)
                .setButtonOkClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (utils.inputValidate(etName,"请输入厂商") && utils.inputValidate(etType,"请输入型号")){
                            RxBus.getInstance().postEvent(new GetDataEvent(etName.getText() + " " + etType.getText()));
                            builder.dismiss();
                            finish();
                        }
                    }
                })
                .setButtonCancelClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                })
                .show();

    }

    private void getData(){
        Observable.create(new Observable.OnSubscribe<CarInfo>() {
            @Override
            public void call(Subscriber<? super CarInfo> subscriber) {
                String json = LvFileUtils.readAssets(getApplicationContext(),"car_json.txt");
                if (json != null){
                    Gson gson = new Gson();
                    CarInfo info = gson.fromJson(json,CarInfo.class);
                    subscriber.onNext(info);
                    subscriber.onCompleted();
                }
            }
        })
                .compose(SchedulersCompat.<CarInfo>applyNewSchedulers())
                .compose(CarSelectActivity.this.<CarInfo>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<CarInfo>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onNext(CarInfo info) {
                        carInfo = info;
                        adapter.setData(carInfo.car);
                        currentState = STATE_CAR;
                        recyclerView.setAdapter(adapter);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (currentState == STATE_TYPE){
            adapter.setData(carInfo.car);
            currentState = STATE_CAR;
            adapter.notifyDataSetChanged();
        }else {
            finish();
        }
    }


    @Override
    public void onBackPressedSupport() {
        if (currentState == STATE_TYPE){
            adapter.setData(carInfo.car);
            currentState = STATE_CAR;
            adapter.notifyDataSetChanged();
        }else {
            finish();
        }
    }

    class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder>{

        private List list;
        public void setData(List list){
            this.list = list;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_select,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (currentState == STATE_CAR){
                final CarInfo.Car car = (CarInfo.Car) list.get(position);
                holder.tvType.setText(car.name);
                holder.tvType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setData(car.type);
                        data = car.name;
                        currentState = STATE_TYPE;
                        notifyDataSetChanged();
                    }
                });
            }
            if (currentState == STATE_TYPE){
                final String type = (String) list.get(position);
                holder.tvType.setText(type);
                holder.tvType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data = data + " " +type;
                        RxBus.getInstance().postEvent(new GetDataEvent(data));
                        finish();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.tv_car_type)
            TextView tvType;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

}
