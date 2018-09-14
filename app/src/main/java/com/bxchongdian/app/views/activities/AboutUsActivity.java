package com.bxchongdian.app.views.activities;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/03
 *     desc   :
 *     version: 1.0
 * </pre>
 */

@Route(path = "/mime/aboutus")
public class AboutUsActivity extends LvBaseAppCompatActivity {

    public static void navigation(){
        ARouter.getInstance().build("/mime/aboutus").navigation();
    }
    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_about_us);
    }

    @Override
    protected void initView() {
        initToolbarNav(R.string.about_us);
    }
}
