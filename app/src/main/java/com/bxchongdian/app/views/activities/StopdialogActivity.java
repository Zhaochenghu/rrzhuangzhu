package com.bxchongdian.app.views.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.request.SimpleRequest;
import com.bxchongdian.model.request.StopRequest;
import com.bxchongdian.model.response.BaseResponse;
import com.bxchongdian.app.R;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

/**
 * Created by 赵成虎 on 2018/5/9.
 * 强制删除当前充电记录
 */
@Route(path = "/login/stop/changing")
public class StopdialogActivity extends LvBaseAppCompatActivity {
    public BaseResponse response;
    public static void navigation() {
        ARouter.getInstance().build("/login/stop/changing").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_stop_dialog);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void stopChanging(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle("强制删除本次充电")
                .setMessage("确定要删除本次充电记录吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StopRequest request = new StopRequest();
                        request.cancle = "cancle";
                        ApiComponentHolder.sApiComponent.apiService().deleteChanging(request)
                                .take(1)
                                .compose(SchedulersCompat.<SimpleRequest>applyComputationSchedulers())
                                .subscribe(new SimpleSubscriber<SimpleRequest>() {
                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                        dismissLoadingDialog();
                                        showToast("成功");
                                    }
                                    @Override
                                    public void onNext(SimpleRequest simpleRequest) {
                                        super.onNext(simpleRequest);
                                        dismissLoadingDialog();
                                        if (response.isSuccess()) {
                                            showToast("提交失败");
                                            finish();
                                        } else {
                                            showToast(response.msg);
                                        }
                                    }
                                });
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(StopdialogActivity.this,"您已取消操作,请重新删除或返回启动充电界面",Toast.LENGTH_LONG).show();
                    }
                }).create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
}
