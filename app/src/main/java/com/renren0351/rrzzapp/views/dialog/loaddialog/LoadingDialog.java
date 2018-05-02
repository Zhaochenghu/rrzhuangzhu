package com.renren0351.rrzzapp.views.dialog.loaddialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren0351.rrzzapp.R;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class LoadingDialog {
    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(R.layout.load_dialog, null);
        // 获取整个布局
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);
        // 页面中的Img
        ImageView img = (ImageView) view.findViewById(R.id.loading_img);
        // 页面中显示文本
        TextView tipText = (TextView) view.findViewById(R.id.loading_tv);

        // 加载动画，动画用户使img图片不停的旋转
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading_rotate);
        // 显示动画
        img.startAnimation(animation);
        // 显示文本
        tipText.setText(msg);

        // 创建自定义样式的Dialog
        Dialog loadingDialog = new Dialog(context, R.style.Loading_Dialog_Theme);
        // 设置返回键无效
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(view, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));

        return loadingDialog;
    }

    public static void setDialogMessage(Dialog dialog, String msg) {
        TextView tipText = (TextView) dialog.findViewById(R.id.loading_tv);
        tipText.setText(msg);
    }
}
