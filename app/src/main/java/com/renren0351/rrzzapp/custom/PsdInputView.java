package com.renren0351.rrzzapp.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren0351.rrzzapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2017/3/2 0002.
 */

public class PsdInputView extends RelativeLayout implements View.OnClickListener {
    private Context    mContext;
    private TextView[] mTVList;
    private View       mView;
    private GridView   mGridView;
    private TextView   mForgetPsd;

    private int index = -1;
    private String                         mPsd;
    private ArrayList<Map<String, String>> mValueList;
    private OnPayPasswordListener listener;

    public PsdInputView(Context context) {
        this(context, null);
    }

    public PsdInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mView = View.inflate(mContext, R.layout.psd_input_view, null);
        mTVList = new TextView[6];
        mValueList = new ArrayList<>();

        mForgetPsd = (TextView) mView.findViewById(R.id.view_tv_forgetPsd);
        mForgetPsd.setOnClickListener(this);

        mTVList[0] = (TextView) mView.findViewById(R.id.view_tv_pass1);

        mTVList[1] = (TextView) mView.findViewById(R.id.view_tv_pass2);
        mTVList[2] = (TextView) mView.findViewById(R.id.view_tv_pass3);
        mTVList[3] = (TextView) mView.findViewById(R.id.view_tv_pass4);
        mTVList[4] = (TextView) mView.findViewById(R.id.view_tv_pass5);
        mTVList[5] = (TextView) mView.findViewById(R.id.view_tv_pass6);

        mGridView = (GridView) mView.findViewById(R.id.view_gv_keybord);

        setView();
        addView(mView);

        mTVList[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    mPsd = ""; // 每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
                    for (int i = 0; i < 6; i++) {
                        mPsd += mTVList[i].getText().toString().trim();
                    }

                    if (listener != null) {
                        listener.onPasswordInputFinish(mPsd);
                    }
                }
            }
        });

    }

    private void setView() {
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<String, String>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", "");
            } else if (i == 12) {
                map.put("name", "");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            }
            mValueList.add(map);
        }
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position < 11 && position != 9) { // 点击0~9按钮
                    if (index >= -1 && index < 5) { // 判断输入位置————要小心数组越界
                        mTVList[++index].setText(mValueList.get(position)
                            .get("name"));
                    }
                } else {
                    if (position == 11) { // 点击退格键
                        if (index - 1 >= -1) { // 判断是否删除完毕————要小心数组越界
                            mTVList[index--].setText("");
                        }
                    }

//                    if (position == 9){ //清除所以密码
//                        clearAllPsd();
//                    }
                }
            }
        });

    }

    /**
     * 清除所有输入的密码
     */
    public void clearAllPsd() {
        if (mTVList != null) {
            for (TextView tv : mTVList) {
                tv.setText("");
            }
        }
        //将索引设置为-1
        index = -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_tv_forgetPsd://忘记密码
                if (listener != null) {
                    listener.onForgetPassword();
                }
                break;
        }

    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mValueList.size();
        }

        @Override
        public Object getItem(int position) {
            return mValueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_grid, null);
                viewHolder = new ViewHolder();
                viewHolder.btnKey = (DrawableCenterTextView) convertView.findViewById(R.id.grid_bt_keys);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.btnKey.setText(mValueList.get(position).get("name"));
            if (position == 9) {
                viewHolder.btnKey
                    .setBackgroundResource(R.drawable.gride_key_selector);
                viewHolder.btnKey.setEnabled(false);
//                Drawable icon = getResources().getDrawable(R.drawable.ic_clear);
//                icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
//                viewHolder.btnKey.setCompoundDrawables(icon, null, null, null);
//                viewHolder.btnKey.setCompoundDrawablePadding(0);
            }
            if (position == 11) {
                viewHolder.btnKey
                    .setBackgroundResource(R.drawable.gride_key_selector);
                Drawable icon = getResources().getDrawable(R.drawable.ic_delete_one);
                icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
                viewHolder.btnKey.setCompoundDrawables(icon, null, null, null);
                viewHolder.btnKey.setCompoundDrawablePadding(0);
            }
            return convertView;
        }
    };

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public DrawableCenterTextView btnKey;
    }

    public void setOnPayPasswordListener(OnPayPasswordListener listener) {
        this.listener = listener;
    }

    public void dismissForgot() {
        mForgetPsd.setVisibility(INVISIBLE);
    }
}
