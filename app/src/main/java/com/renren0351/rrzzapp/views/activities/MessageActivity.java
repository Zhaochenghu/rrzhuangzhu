package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;

import butterknife.BindView;
import cn.com.leanvision.baseframe.log.DebugLog;
import cn.com.leanvision.baseframe.util.LvPhoneUtils;

/**
 * Created by admin on 2017-02-16.
 */
@Route(path = "/login/mime/message")
public class MessageActivity extends LvBaseAppCompatActivity {
    @BindView(R.id.tv_test)
    TextView tvTest;
    public static void navigation(){
        ARouter.getInstance().build("/login/mime/message").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_message);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initToolbarNav("消息");
        showToast("您还没有消息");
        DisplayMetrics dm = LvPhoneUtils.getDisplayMetrics(this);
        DebugLog.log("width:" + dm.widthPixels);
        DebugLog.log("height:" + dm.heightPixels);
        DebugLog.log("sp:" + getResources().getDimension(R.dimen.ts_lel_one));
        DebugLog.log("size:" + tvTest.getTextSize());
    }
}
