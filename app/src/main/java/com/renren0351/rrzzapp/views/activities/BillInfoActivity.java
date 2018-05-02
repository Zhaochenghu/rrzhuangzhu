package com.renren0351.rrzzapp.views.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.utils.ValidationUtils;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/login/mime/billinfo")
public class BillInfoActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.tv_email)
    TextView tvEmail;       //电子发票
    @BindView(R.id.tv_paper)
    TextView tvPaper;       //纸质发票
    @BindView(R.id.et_company)
    EditText etCompany;     //公司抬头
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.et_email)
    EditText etEmail;       //邮箱
    @BindView(R.id.ll_email)
    LinearLayout llEmail;   //电子发票布局
    @BindView(R.id.et_name)
    EditText etName;        //联系人
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tv_area)
    TextView tvArea;        //区域
    @BindView(R.id.et_address)
    EditText etAddress;     //详细地址
    @BindView(R.id.ll_paper)
    LinearLayout llPaper;   //纸质发票布局
    @BindView(R.id.bt_submit)
    Button btSubmit;        // 提交按钮

    private int currentType = 0;
    private ValidationUtils validationUtils;
    private Drawable unselectedIcon;
    private Drawable selectedIcon;
    private float money;
    public static void navigation(float money) {
        ARouter.getInstance().
                build("/login/mime/billinfo")
                .withFloat("money", money)
                .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_bill_info);
    }

    @Override
    protected void initView() {
        initToolbarNav("发票信息");
        validationUtils = new ValidationUtils(this);
        unselectedIcon = getResources().getDrawable(R.drawable.ic_unselect);
        selectedIcon = getResources().getDrawable(R.drawable.ic_select);
        unselectedIcon.setBounds(0,0,unselectedIcon.getMinimumWidth(),unselectedIcon.getMinimumHeight());
        selectedIcon.setBounds(0,0,selectedIcon.getMinimumWidth(),selectedIcon.getMinimumHeight());
        money = getIntent().getFloatExtra("money",0);
        tvMoney.setText(money + "元");
    }

    @OnClick({R.id.tv_email, R.id.tv_paper, R.id.tv_area, R.id.bt_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_email:
                currentType = 0;
                tvPaper.setBackground(getResources().getDrawable(R.drawable.btn_red_unfocused_stroke));
                tvEmail.setBackground(getResources().getDrawable(R.drawable.btn_red_stroke));
                tvEmail.setCompoundDrawables(null,null,selectedIcon,null);
                tvPaper.setCompoundDrawables(null,null,unselectedIcon,null);
                llEmail.setVisibility(View.VISIBLE);
                llPaper.setVisibility(View.GONE);
                break;
            case R.id.tv_paper:
                currentType = 1;
                tvPaper.setBackground(getResources().getDrawable(R.drawable.btn_red_stroke));
                tvEmail.setBackground(getResources().getDrawable(R.drawable.btn_red_unfocused_stroke));
                tvEmail.setCompoundDrawables(null,null,unselectedIcon,null);
                tvPaper.setCompoundDrawables(null,null,selectedIcon,null);
                llEmail.setVisibility(View.GONE);
                llPaper.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_area:
                showSelectArea();
                break;
            case R.id.bt_submit:
                if (validationUtils.inputValidate(etCompany,"") && money > 0){
                    if (currentType == 0 && validationUtils.emailValidate(etEmail)){//电子发票
                        // TODO: 2017/6/20
                    }
                    if (currentType == 1 && validationUtils.inputValidate(etName,"") &&
                            validationUtils.inputValidate(etAddress,"") &&
                            validationUtils.inputValidate(etPhone,"") && !LvTextUtil.isEmpty(tvArea.getText())){
                        // TODO: 2017/6/20
                    }
                }

                break;
        }
    }

    /**
     * 选择地区
     */
    private void showSelectArea() {
        CityPicker picker = new CityPicker.Builder(this)
                .textSize(20)
                .textColor(R.color.primary_text_black)
                .titleBackgroundColor("#ed3b5d")
                .cancelTextColor("#FFFFFF")
                .confirTextColor("#FFFFFF")
                .province("北京市")
                .provinceCyclic(true)
                .city("北京市")
                .cityCyclic(false)
                .district("海淀区")
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .build();
        picker.show();
        picker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... strings) {
                String province = strings[0];
                String city = strings[1];
                String district = strings[2];
                tvArea.setText(province + " " + city + " " + district);
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
