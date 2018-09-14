package com.bxchongdian.app.views.activities;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;

/**
 * Created by admin on 2017-02-16.
 */
@Route(path = "/login/mime/pile")
public class PileActivity extends LvBaseAppCompatActivity {
    public static void navigation(){
        ARouter.getInstance().build("/login/mime/pile").navigation();
    }
    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_pile);
    }
}
