package com.renren0351.rrzzapp.utils;

import android.content.Context;
import android.widget.EditText;

import com.renren0351.rrzzapp.LvApplication;
import com.renren0351.rrzzapp.custom.toast.IToast;
import com.renren0351.rrzzapp.custom.toast.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.leanvision.baseframe.util.LvTextUtil;

/**
 * Created by Administrator on 2017/2/23 0023.
 */

public class ValidationUtils {
    private Context mContext;

    public ValidationUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 验证手机号
     * 判断手机号格式是否正确
     *
     * @param editText
     * @return
     */
    public boolean phoneNumValidate(EditText editText) {
        String num = editText.getText().toString().trim();
        if (LvTextUtil.isEmpty(num) || num.length() < 11) {
            prompt(editText, "手机号有错误");
            return false;
        } else {
            String numRegex = "[1][345678]\\d{9}";//没有1、2和9
            if (num.matches(numRegex)) {
                return true;
            } else {
                prompt(editText, "手机号有错误，请重新输入");
                return false;
            }
        }
    }

    /**
     * 验证密码
     * 判断密码格式是否正确
     *
     * @param editText
     * @return
     */
    public boolean psdValidate(EditText editText) {
        String psd = editText.getText().toString().trim();
       // String psdRegex = ".*[a-zA-Z].*[0-9]|.*[0-9].*[a-zA-Z]"; // 密码必须由数字和字母组成
        String psdRegex = "^(?![^a-zA-Z]+$)(?!\\D+$)[a-zA-Z0-9]{6,}$";
        if (LvTextUtil.isEmpty(psd)) {
//      prompt(editText, "请输入密码");
            return false;
        } else if (psd.length() < 6) {
//      prompt(editText, "密码长度应大于6位");
            return false;
        } else if (!psd.matches(psdRegex)) {
//      prompt(editText, "密码必须由数字和字母组成");
            return false;
        }

        return true;
    }

    public boolean isContainChinese(EditText editText){
        String str = editText.getText().toString().trim();
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()){
            return true;
        }else {
            return false;
        }
    }

    public boolean emailValidate(EditText editText) {
        String email = editText.getText().toString().trim();
        String emailRegex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        if (LvTextUtil.isEmpty(email)) {
            showToast("请输入邮箱");
            return false;
        } else if (!email.matches(emailRegex)) {
            showToast("邮箱格式错误");
            return false;
        }
        return true;
    }

    public boolean captchaValidate(EditText editText) {
        String captcha = editText.getText().toString();
        return captcha.length() >= 4;
    }

    /**
     * 判断输入是否为空，
     * 如果为空，返回 false
     *
     * @param editText
     * @param text     提示信息
     * @return
     */
    public boolean inputValidate(EditText editText, CharSequence text) {
        if (editText.getText().toString().trim().equals("")) {
            showToast(text);
            return false;
        }
        return true;
    }

    /**
     * 提示
     *
     * @param editText
     * @param text
     */
    private void prompt(EditText editText, CharSequence text) {
        showToast(text);
//    editText.setFocusable(true);
//    editText.requestFocus();
    }

    private void showToast(CharSequence text) {
        ToastUtils.getInstance(LvApplication.getContext()).makeTextShow(text, IToast.LENGTH_SHORT);
    }
}
