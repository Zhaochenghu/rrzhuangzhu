package com.bxchongdian.app.views.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.custom.JustifyTextView;
import com.bxchongdian.app.event.GetDataEvent;
import com.bxchongdian.app.utils.ValidationUtils;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.app.views.dialog.NiftyDialogBuilder;
import com.bxchongdian.model.bean.CarBean;
import com.bxchongdian.presenter.car.CarContract;
import com.bxchongdian.presenter.car.CarPresenter;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvRegularUtil;
import cn.com.leanvision.baseframe.util.LvTextUtil;
import rx.functions.Action1;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/login/mime/bindcar")
public class BindCarActivity extends LvBaseAppCompatActivity implements CarContract.View{
    @BindView(R.id.iv_car_hint)
    ImageView ivCarHint;    //帮助提示
    @BindView(R.id.tv_plate_type)
    TextView tvPlateType;   //车牌类型
    @BindView(R.id.tv_car_type)
    TextView tvCarType;     //汽车类型
    @BindView(R.id.et_carVIN)
    TextView etCarVIN;      //车辆识别码
    @BindView(R.id.tv_province)
    TextView tvProvince;    //车牌省
    @BindView(R.id.et_number)
    EditText etNumber;      //车牌号
    @BindView(R.id.et_voltage)
    EditText etVoltage;     //充电电压
    @BindView(R.id.et_current)
    EditText etCurrent;     //充电电流
    @BindView(R.id.tv_assist_voltage)
    TextView tvAssistVoltage;//辅助电压
    @BindView(R.id.bt_submit)
    Button btSubmit;        //提交

    private int i = 0;
    private ArrayList<String> list;
    private NiftyDialogBuilder builder;
    private CarBean bean;       //创建的车辆信息
    private CarBean carBean; //传递的车辆信息
    private CarPresenter presenter;
    private ValidationUtils validationUtils;

    public static void navigation(CarBean carBean){
        ARouter.getInstance().build("/login/mime/bindcar")
                .withSerializable("carBean",carBean)
                .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_bind_car);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        carBean = (CarBean) getIntent().getSerializableExtra("carBean");
        if (carBean != null){
            initToolbarNav("修改信息");
            tvPlateType.setText(carBean.licenseType);
            tvCarType.setText(carBean.carType);
            tvAssistVoltage.setText(carBean.assistVoltage + "");
            tvProvince.setText(carBean.license.substring(0,1));
            etNumber.setText(carBean.license.substring(1) + "");
            etCurrent.setText(carBean.current + "");
            etVoltage.setText(carBean.voltage + "");
            etCarVIN.setText(carBean.carVIN);
            bean = carBean;
        }else {
            initToolbarNav("添加爱车");
            bean = new CarBean();
        }
        validationUtils = new ValidationUtils(this);
        String[] strings = getResources().getStringArray(R.array.province);
        //数组转list
        list = new ArrayList<>(Arrays.asList(strings));
        initRxBus();
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

    @OnClick({R.id.iv_car_hint, R.id.tv_plate_type, R.id.tv_car_type, R.id.tv_province, R.id.tv_assist_voltage, R.id.bt_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_car_hint:
                showHelper();
                break;
            case R.id.tv_plate_type:
                selectPlateType();
                break;
            case R.id.tv_car_type:
                CarSelectActivity.navigation();
                break;
            case R.id.tv_province:
                selectProvince();
                break;
            case R.id.tv_assist_voltage:
                selectAssistVoltage();
                break;
            case R.id.bt_submit:
                if (LvTextUtil.isEmpty(etCarVIN.getText().toString())){
                    showToast("输入VIN码");
                    return;
                }else if (!LvRegularUtil.isVIN(etCarVIN.getText().toString())){
                    showToast("VIN码输入错误");
                    return;
                }
                if (LvTextUtil.isEmpty(etNumber.getText().toString())){
                    showToast("请输入车牌号");
                    return;
                } else if (!LvRegularUtil.isCarNumber(etNumber.getText().toString())){
                    showToast("车牌号输入错误");
                    return;
                }
                if (validationUtils.inputValidate(etCurrent,"输入最大电流") &&
                        validationUtils.inputValidate(etVoltage,"输入最大电压") &&
                        !LvTextUtil.isEmpty(tvCarType.getText().toString()) &&
                        !LvTextUtil.isEmpty(tvPlateType.getText().toString())
                        && !LvTextUtil.isEmpty(tvAssistVoltage.getText().toString())){
                    bean.license = tvProvince.getText() + etNumber.getText().toString();
                    bean.carVIN = etCarVIN.getText().toString();
                    int current = Integer.parseInt(etCurrent.getText().toString());
                    int voltage = Integer.parseInt(etVoltage.getText().toString());
                    if ("小型汽车".equals(tvPlateType.getText().toString())){
                        if (voltage <= 200 || voltage >= 500){
                            showToast("电压应该不小于200并且不大于500");
                            return;
                        }
                    }else {
                        if (voltage <= 350 || voltage >= 950){
                            showToast("电压应该不小于350并且不大于950");
                            return;
                        }
                    }
                    if (current < 10 || current > 250){
                        showToast("电流应该不小于10并且不大于250");
                        return;
                    }
                    bean.current = current;
                    bean.voltage = voltage;
                    presenter.saveCar(bean);
                }
                break;
        }
    }

    private void showHelper() {
        JustifyTextView textView = new JustifyTextView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setPadding(10,10, 10,10);
        textView.setText(R.string.car_helper);
        textView.setLineSpacing(0,1.2f);
        textView.setTextSize(17);

        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        builder.withTitle("提示帮助")
                .withDuration(500)
                .setCustomView(textView,this)
                .setButtonCancelClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                })
                .setButtonOkClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                })
                .show();
    }

    /**
     * 选择车牌型号
     */
    private void selectPlateType(){
        i = 0;
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        final String[] items = {"小型汽车","大型汽车"};
        builder.withTitle("车牌类型")
                .withDuration(500)
                .withSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        i = which;
                    }
                })
                .setButtonOkClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvPlateType.setText(items[i]);
                        bean.licenseType = items[i];
                        builder.dismiss();
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

    /**
     * 选择辅助电压
     */
    private void selectAssistVoltage(){
        i = 0;
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        final String[] items = {"12V","24V"};
        builder.withTitle("辅助充电电压")
                .withDuration(500)
                .withSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        i = which;
                    }
                })
                .setButtonOkClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvAssistVoltage.setText(items[i]);
                        bean.assistVoltage = Integer.parseInt(items[i].replace("V",""));
                        builder.dismiss();
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

    /**
     * 选择省份简称
     */
    private void selectProvince(){
        builder = NiftyDialogBuilder.getInstance(this);
        View view = View.inflate(this,R.layout.view_province,null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,7));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new Adapter());
        builder.withTitle("选择省")
                .withDuration(500)
                .setCustomView(view,this)
                .show();
    }

    private void initRxBus(){
        RxBus.getInstance()
                .toObservable(GetDataEvent.class)
                .compose(SchedulersCompat.<GetDataEvent>applyNewSchedulers())
                .compose(BindCarActivity.this.<GetDataEvent>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<GetDataEvent>() {
                    @Override
                    public void call(GetDataEvent getDataEvent) {
                        tvCarType.setText(getDataEvent.data);
                        bean.carType = getDataEvent.data;
                    }
                });
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
        if (carBean == null){
            showToast("添加成功");
            CarActivity.cars.add(bean);
        }else {
            showToast("修改成功");
            CarInfoActivity.carBean = bean;
        }
        finish();
    }

    @Override
    public void refreshCars(List<CarBean> cars) {

    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_province,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String text = list.get(position);
            holder.tvProvince.setText(text);
            holder.tvProvince.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvProvince.setText(text);
                    builder.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.tv_province)
            TextView tvProvince;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
