package com.renren0351.rrzzapp.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren0351.rrzzapp.R;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ChargingDialog {
	private static DonutProgress progress;
	public Dialog createDialog(Context context, String msg){
		// 首先得到整个View
		View view = LayoutInflater.from(context).inflate(R.layout.view_charging_dialog, null);
		// 获取整个布局
//		LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);

		progress = (DonutProgress) view.findViewById(R.id.donut_progress);
		// 页面中显示文本
		TextView tipText = (TextView) view.findViewById(R.id.tv_msg);

		// 显示文本
		tipText.setText(msg);
		progress.start();
		// 创建自定义样式的Dialog
		Dialog loadingDialog = new Dialog(context, R.style.Loading_Dialog_Theme);
		// 设置返回键无效
		loadingDialog.setCancelable(false);
		loadingDialog.setContentView(view, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		return loadingDialog;
	}

	public DonutProgress getDonutProgress(){
		return progress;
	}

	public void close(){
		progress.close();
	}
}
