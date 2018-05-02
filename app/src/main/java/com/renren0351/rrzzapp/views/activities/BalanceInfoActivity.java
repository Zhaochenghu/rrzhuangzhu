package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/04/26
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/charging/balanceinfo")
public class BalanceInfoActivity extends LvBaseAppCompatActivity{
    @BindView(R.id.btn_ok)
    Button btnOk;

    public static void navigation(){
        ARouter.getInstance().build("/charging/balanceinfo").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_balance_info);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        initToolbarNav("充电详情");
    }

    @OnClick(R.id.btn_ok)
    public void clickOk(){
        finish();
    }
}
