package com.bxchongdian.app.views.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bxchongdian.app.R;
import com.bxchongdian.app.event.GetDataEvent;
import com.bxchongdian.app.utils.MyItemDecoration;
import com.bxchongdian.app.views.base.LvBaseAppCompatActivity;
import com.bxchongdian.app.views.dialog.NiftyDialogBuilder;
import com.bxchongdian.model.bean.CardBean;
import com.bxchongdian.model.dagger.ApiComponentHolder;
import com.bxchongdian.model.request.CardRequest;
import com.bxchongdian.model.response.CardListResponse;
import com.bxchongdian.model.response.SimpleResponse;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.RxBus;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;

/**
 * Created by admin on 2017-02-16.
 */
@Route(path = "/login/mime/card")
public class CardActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_no_card)
    LinearLayout ll_no;
    @BindView(R.id.btn_ok)
    Button btOk;

    private CardAdapter adapter;
    public static List<CardBean> cards = new ArrayList<>();
    private boolean isBindCar;
    private int selectPos = -1;

    public static void navigation(boolean bindFlags) {
        ARouter.getInstance().build("/login/mime/card")
                .withBoolean("flags", bindFlags)
                .navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_card);
    }

    @Override
    protected void initView() {
        initToolbarNav("充电卡");
        ll_no.setVisibility(View.GONE);
        queryCards();
        isBindCar = getIntent().getBooleanExtra("flags", false);
        if (isBindCar){
            btOk.setVisibility(View.VISIBLE);
        }else {
            btOk.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.bt_add, R.id.iv_add})
    public void onClick(View view) {
        BindCardActivity.navigation();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
        if (cards.size() > 0) {
            ll_no.setVisibility(View.GONE);
            if (adapter == null) {
                adapter = new CardAdapter();
                RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(lm);
                recyclerView.setHasFixedSize(true);
                recyclerView.addItemDecoration(new MyItemDecoration(this, MyItemDecoration.HORIZONTAL));
                recyclerView.setAdapter(adapter);
            } else {
                adapter = (CardAdapter) recyclerView.getAdapter();
                adapter.notifyDataSetChanged();
            }
        } else {
            ll_no.setVisibility(View.VISIBLE);
        }
    }

    private void queryCards() {
        CardActivity.cards.clear();
        showLoadingDialog();
        ApiComponentHolder.sApiComponent.apiService()
                .queryCards()
                .compose(CardActivity.this.<CardListResponse>bindUntilEvent(ActivityEvent.DESTROY))
                .take(1)
                .compose(SchedulersCompat.<CardListResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<CardListResponse>() {

                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        showToast(R.string.network_not_available);
                    }

                    @Override
                    public void onNext(CardListResponse cardListResponse) {
                        if (cardListResponse.isSuccess()) {
                            CardActivity.cards.addAll(cardListResponse.cardBeans);
                            refreshRecyclerView();
                        } else {
                            showToast(cardListResponse.msg);
                        }
                    }
                });
    }

    private void unbindCard(final String cardId) {
        showLoadingDialog("正在删除");
        CardRequest request = new CardRequest(cardId);
        ApiComponentHolder.sApiComponent.apiService()
                .unbindCard(request)
                .compose(CardActivity.this.<SimpleResponse>bindUntilEvent(ActivityEvent.DESTROY))
                .take(1)
                .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
                .subscribe(new SimpleSubscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        dismissLoadingDialog();
                        showToast(R.string.network_not_available);
                    }

                    @Override
                    public void onNext(SimpleResponse response) {
                        if (response.isSuccess()) {
                            CardActivity.cards.remove(new CardBean(cardId));
                            showToast("解绑成功");
                            refreshRecyclerView();
                        } else {
                            showToast(response.msg);
                        }
                    }
                });
    }

    class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final CardBean cardBean = cards.get(position);
            StringBuilder cardId = new StringBuilder(cardBean.cardId);
            int length = cardBean.cardId.length();
            for (int i = length - 1; i > 0; i--) {
                if (i % 4 == 0){
                    cardId.insert(i, "  ");
                }
            }
            holder.tvCode.setText(cardId);
            holder.tvType.setText(cardBean.getCardtype());
            holder.tvBalance.setText(String.format("余额:%.2f", cardBean.balance / 100f));
            holder.llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isBindCar) {
                        if (selectPos > -1){
                            ViewHolder holder1 = (ViewHolder) recyclerView.findViewHolderForLayoutPosition(selectPos);
                            if (holder1 != null){
                                holder1.ivSelect.setImageDrawable(holder1.unselectedIcon);
                            }else {
                                notifyItemChanged(selectPos);
                            }
                            cards.get(selectPos).isSelect = false;
                            selectPos = position;
                            cards.get(position).isSelect = true;
                            holder.ivSelect.setImageDrawable(holder.selectedIcon);
                        }else {
                            selectPos = position;
                            cards.get(position).isSelect = true;
                            holder.ivSelect.setImageDrawable(holder.selectedIcon);
                        }
                    }
                }
            });
            if (!isBindCar){
                holder.llRoot.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showUnbindDialog(cardBean.cardId);
                        return true;
                    }
                });
                holder.llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PayRecordActivity.navigation(cardBean.cardId);
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return cards == null ? 0 : cards.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_card_code)
            TextView tvCode;
            @BindView(R.id.tv_card_type)
            TextView tvType;
            @BindView(R.id.tv_card_balance)
            TextView tvBalance;
            @BindView(R.id.ll_root)
            LinearLayout llRoot;
            @BindView(R.id.iv_select)
            ImageView ivSelect;

            public Drawable unselectedIcon;
            public Drawable selectedIcon;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                if (isBindCar) {
                    ivSelect.setVisibility(View.VISIBLE);
                    unselectedIcon = getResources().getDrawable(R.drawable.ic_unselect);
                    selectedIcon = getResources().getDrawable(R.drawable.ic_select);
                }
            }
        }
    }

    private void showUnbindDialog(final String cardId) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        builder.withTitle("解除绑定")
                .withDuration(500)
                .withMessage("您确定解除绑定此充电卡吗？")
                .withMessageSize(20)
                .setButtonCancelClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                })
                .setButtonOkClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unbindCard(cardId);
                        builder.dismiss();
                    }
                })
                .show();
    }

    @OnClick(R.id.btn_ok)
    public void clickOk(){
        if (selectPos > -1){
            RxBus.getInstance().postEvent(new GetDataEvent(cards.get(selectPos).cardId));
            finish();
        }

    }
}
