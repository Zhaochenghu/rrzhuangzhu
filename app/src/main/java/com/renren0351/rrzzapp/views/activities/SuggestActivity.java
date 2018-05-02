package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.request.CheckErrorRequest;
import com.renren0351.model.response.SimpleResponse;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/06/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Route(path = "/login/mime/suggest")
public class SuggestActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.et_suggest)
    EditText etSuggest;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.bt_submit)
    Button   btSubmit;

    public static void navigation() {
        ARouter.getInstance().build("/login/mime/suggest").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_suggest);
    }

    @Override
    protected void initView() {
        initToolbarNav("投诉建议");
        etSuggest.addTextChangedListener(watcher);
    }

    @OnClick(R.id.bt_submit)
    public void onViewClicked() {
        if (etSuggest.getText().toString().trim().length() < 6){
            showToast("建议至少6个字");
        }else {
            showLoadingDialog("提交意见");
            CheckErrorRequest request = new CheckErrorRequest();
            request.msgType = 1;
            request.subType = "";
            request.substationId = "";
            request.remark = etSuggest.getText().toString();
            request.cpId = "";
            request.phone = "";
            request.imgUrl = "";

            /**
             * cpId: ""
             msgType: 0
             phone: ""
             remark: "测试测试测试测试"
             subType: "1"
             substationId: "0063"
             * */

            ApiComponentHolder.sApiComponent.apiService().checkError(request)
                    .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
                    .subscribe(new SimpleSubscriber<SimpleResponse>() {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            dismissLoadingDialog();
                            showToast("提交失败，请重试");
                        }

                        @Override
                        public void onNext(SimpleResponse response) {
                            dismissLoadingDialog();
                            if (response.isSuccess()) {
                                showToast("提交成功");
                                finish();
                            } else {
                                showToast(response.msg);
                            }
                        }
                    });
        }


    }

    TextWatcher watcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
            tvNum.setText("还可以输入" + (200 - s.length()) + "字");
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = etSuggest.getSelectionStart();
            editEnd = etSuggest.getSelectionEnd();
            if (temp.length() > 230){
                etSuggest.setText(temp.toString().substring(0,200));
            }else if (temp.length() > 200) {
                s.delete(editStart - 1, editEnd);
                int tempSelection = editEnd;
                etSuggest.setText(s);
                etSuggest.setSelection(tempSelection);
            }
        }
    };
}
