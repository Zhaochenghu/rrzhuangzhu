package com.renren0351.rrzzapp.custom;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface OnPayPasswordListener {
	/**
	 * 密码输入完成
	 * @param psd
	 */
	void onPasswordInputFinish(String psd);

	/**
	 * 忘记密码
	 */
	void onForgetPassword();
}
