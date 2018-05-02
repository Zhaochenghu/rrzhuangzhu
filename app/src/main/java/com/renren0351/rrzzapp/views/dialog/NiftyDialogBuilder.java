package com.renren0351.rrzzapp.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.dialog.effects.BaseEffects;


/*
 * Copyright 2014 litao
 * https://github.com/sd6352051/NiftyDialogEffects
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class NiftyDialogBuilder extends Dialog implements DialogInterface {

    private final String defTextColor = "#deffffff";

    private final String defDividerColor = "#E1E2E4";

    private final String defDialogColor = "#deffffff";


    private static Context tmpContext;


    private Effectstype type = null;

    private LinearLayout mLinearLayoutView;

    private RelativeLayout mRelativeLayoutView;

    private LinearLayout mLinearLayoutMsgView;

    private LinearLayout mLinearLayoutTopView;

    private FrameLayout mFrameLayoutCustomView;

    private View mDialogView;

    private View mDivider;

    private TextView mTitle;

    private TextView mMessage;

    private ImageView mIcon;

    private Button mBtCan;

    private Button mBtOk;

    private RadioGroup mGroup;

    private int mDuration = -1;

    private static int mOrientation = 1;

    private boolean isCancelable = true;

    private static NiftyDialogBuilder instance;

    public NiftyDialogBuilder(Context context) {
        super(context);
        init(context);

    }

    public NiftyDialogBuilder(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

    }

    public static NiftyDialogBuilder getInstance(Context context) {

        if (instance == null || !tmpContext.equals(context)) {
            synchronized (NiftyDialogBuilder.class) {
                if (instance == null || !tmpContext.equals(context)) {
                    instance = new NiftyDialogBuilder(context, R.style.dialog_untran);
                }
            }
        }
        tmpContext = context;
        return instance;

    }

    private void init(Context context) {

        mDialogView = View.inflate(context, R.layout.custom_dialog, null);

        mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
        mRelativeLayoutView = (RelativeLayout) mDialogView.findViewById(R.id.main);
        mLinearLayoutTopView = (LinearLayout) mDialogView.findViewById(R.id.topPanel);
        mLinearLayoutMsgView = (LinearLayout) mDialogView.findViewById(R.id.contentPanel);
        mFrameLayoutCustomView = (FrameLayout) mDialogView.findViewById(R.id.customPanel);

        mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
        mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
        mDivider = mDialogView.findViewById(R.id.titleDivider);
        mBtCan = (Button) mDialogView.findViewById(R.id.button_cancel);
        mBtOk = (Button) mDialogView.findViewById(R.id.button_ok);
        mGroup = (RadioGroup) mDialogView.findViewById(R.id.dialog_group);
        mMessage = (TextView) mDialogView.findViewById(R.id.dialog_message);

        setContentView(mDialogView);

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                mLinearLayoutView.setVisibility(View.VISIBLE);
                if (type == null) {
                    type = Effectstype.Fall;
                }
                start(type);


            }
        });
        mRelativeLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCancelable) dismiss();
            }
        });
    }

    public void toDefault() {
        mTitle.setTextColor(Color.parseColor(defTextColor));
        mDivider.setBackgroundColor(Color.parseColor(defDividerColor));
        mLinearLayoutView.setBackgroundColor(Color.parseColor(defDialogColor));
    }

    public NiftyDialogBuilder withDividerColor(String colorString) {
        mDivider.setBackgroundColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder withDividerColor(int color) {
        mDivider.setBackgroundColor(color);
        return this;
    }


    public NiftyDialogBuilder withTitle(CharSequence title) {
        toggleView(mLinearLayoutTopView, title);
        mTitle.setText(title);
        return this;
    }

    public NiftyDialogBuilder withTitleColor(String colorString) {
        mTitle.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialogBuilder withTitleColor(int color) {
        mTitle.setTextColor(color);
        return this;
    }

    public NiftyDialogBuilder withDialogColor(String colorString) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(Color
                .parseColor(colorString)));
        return this;
    }

    public NiftyDialogBuilder withDialogColor(int color) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(color));
        return this;
    }

    public NiftyDialogBuilder withIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    public NiftyDialogBuilder withIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public NiftyDialogBuilder withDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public NiftyDialogBuilder withEffect(Effectstype type) {
        this.type = type;
        return this;
    }

    public NiftyDialogBuilder withMessage(CharSequence message){
        mMessage.setVisibility(View.VISIBLE);
        mMessage.setText(message);
        return this;
    }

    public NiftyDialogBuilder withMessageSize(float size){
        mMessage.setTextSize(size);
        return this;
    }

    public NiftyDialogBuilder withMessageColor(int color){
        mMessage.setTextColor(color);
        return this;
    }

    public NiftyDialogBuilder withSingleChoiceItems(final String[] items, int i, final OnClickListener listener){
        int temp;
        mGroup.removeAllViews();
        if (items.length < 1){

        }else {
            if (i < 0){
                temp = 0;
            }else if (i < items.length){
                temp = i;
            }else {
                temp = items.length;
            }
            for (int j = 0; j < items.length; j++) {
                RadioButton b = new RadioButton(tmpContext);
                b.setText(items[j]);
                b.setPadding(5,5,5,5);
//                b.setButtonDrawable(null);
                b.setGravity(Gravity.CENTER);
                mGroup.addView(b);
                if (j == temp){
                    b.setChecked(true);
                }else {
                    b.setChecked(false);
                }
            }
        }
        mGroup.setVisibility(View.VISIBLE);
        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton b = (RadioButton) findViewById(checkedId);
                if (b != null) {
                    for (int j = 0; j < items.length; j++) {
                        if (items[j] == b.getText().toString()){
                            listener.onClick(NiftyDialogBuilder.this,j);
                            break;
                        }
                    }
                }
            }
        });
        return this;
    }

    public NiftyDialogBuilder withButtonDrawable(int resid) {
        mBtCan.setBackgroundResource(resid);
        mBtOk.setBackgroundResource(resid);
        return this;
    }

    public NiftyDialogBuilder withButtonCancelText(CharSequence text) {
        mBtCan.setVisibility(View.VISIBLE);
        mBtCan.setText(text);

        return this;
    }

    public NiftyDialogBuilder withButtonOkText(CharSequence text) {
        mBtOk.setVisibility(View.VISIBLE);
        mBtOk.setText(text);
        return this;
    }

    public NiftyDialogBuilder setButtonCancelClick(View.OnClickListener click) {
        mBtCan.setVisibility(View.VISIBLE);
        mBtCan.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder setButtonOkClick(View.OnClickListener click) {
        mBtOk.setVisibility(View.VISIBLE);
        mBtOk.setOnClickListener(click);
        return this;
    }

    public NiftyDialogBuilder setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(customView);
        mFrameLayoutCustomView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder setCustomView(View view, Context context) {
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(view);
        mFrameLayoutCustomView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialogBuilder isCancelableOnTouchOutside(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public NiftyDialogBuilder isCancelable(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCancelable(cancelable);
        return this;
    }

    private void toggleView(View view, Object obj) {
        if (obj == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        super.show();
    }

    private void start(Effectstype type) {
        BaseEffects animator = type.getAnimator();
        if (mDuration != -1) {
            animator.setDuration(Math.abs(mDuration));
        }
        animator.start(mRelativeLayoutView);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mFrameLayoutCustomView.setVisibility(View.GONE);
        mGroup.setVisibility(View.GONE);
        mBtCan.setVisibility(View.GONE);
        mBtOk.setVisibility(View.GONE);
    }
}
