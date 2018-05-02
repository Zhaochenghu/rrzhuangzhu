package com.renren0351.rrzzapp.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.renren0351.rrzzapp.R;
import com.renren0351.rrzzapp.views.base.LvBaseAppCompatActivity;
import com.renren0351.model.dagger.ApiComponentHolder;
import com.renren0351.model.request.CardRequest;
import com.renren0351.model.response.CardResponse;
import com.trello.rxlifecycle.ActivityEvent;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvTextUtil;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/05/22
 *     desc   : 绑定充电卡
 *     version: 1.0
 * </pre>
 */
@Route(path = "/mime/bindcard")
public class BindCardActivity extends LvBaseAppCompatActivity {

  @BindView(R.id.et_input)
  EditText etInput;
  @BindView(R.id.tv_card_code)
  TextView tvCardCode;
  @BindView(R.id.tv_card_type)
  TextView tvCardType;
  @BindView(R.id.tv_card_name)
  TextView tvCardName;
  @BindView(R.id.tv_card_phone)
  TextView tvCardPhone;
  @BindView(R.id.tv_no)
  TextView tvNoCard;
  @BindView(R.id.ll_card)
  LinearLayout llCard;
  @BindView(R.id.btn_bind)
  Button btBind;

  private String id;

  public static void navigation() {
    ARouter.getInstance().build("/mime/bindcard").navigation();
  }

  @Override
  protected void setContentView(Bundle savedInstanceState) {
    setContentView(R.layout.aty_bind_card);
  }

  @Override
  protected void initView() {
    initToolbarNav("绑定充电卡");
    btBind.setVisibility(View.GONE);
    llCard.setVisibility(View.GONE);
    tvNoCard.setVisibility(View.GONE);
  }

  @OnClick(R.id.iv_search)
  public void clickQueryCard() {
    String s = etInput.getText().toString();
    if (LvTextUtil.isEmpty(s)) {
      showToast("请输入电卡号");
    } else {
      queryCardInfo(s);
    }
  }

  @OnClick(R.id.btn_bind)
  public void clickBind() {
    //此处的id应是查询后的充电卡id
    if (LvTextUtil.isEmpty(id)) {
      showToast("先查询和确认充电卡信息");
    } else {
      bindCard(id);
    }
  }

  private void queryCardInfo(String cardNum) {
    ApiComponentHolder.sApiComponent.apiService()
        .queryCardInfo(cardNum)
        .compose(BindCardActivity.this.<CardResponse>bindUntilEvent(ActivityEvent.DESTROY))
        .take(1)
        .compose(SchedulersCompat.<CardResponse>applyNewSchedulers())
        .subscribe(new SimpleSubscriber<CardResponse>() {
          @Override
          public void onError(Throwable e) {
            showToast(R.string.network_not_available);
          }

          @Override
          public void onNext(CardResponse cardResponse) {
            if (cardResponse.isSuccess()) {
              btBind.setVisibility(View.VISIBLE);
              llCard.setVisibility(View.VISIBLE);
              tvNoCard.setVisibility(View.GONE);
              tvCardCode.setText(String.format("卡号：%s", cardResponse.cardBean.cardId));
              tvCardType.setText(String.format("类型：%s", cardResponse.cardBean.getCardtype()));
              tvCardName.setText(String.format("持卡人姓名：%s", cardResponse.cardBean.username));
              tvCardPhone.setText("手机号：无");
              id = cardResponse.cardBean.cardId;
            } else {
              btBind.setVisibility(View.GONE);
              llCard.setVisibility(View.GONE);
              tvNoCard.setVisibility(View.VISIBLE);
//              showToast(cardResponse.msg);
            }
          }
        });
  }

  private void bindCard(String cardNum) {
    showLoadingDialog();
    CardRequest request = new CardRequest(cardNum);
    ApiComponentHolder.sApiComponent.apiService()
        .bindCard(request)
        .compose(BindCardActivity.this.<CardResponse>bindUntilEvent(ActivityEvent.DESTROY))
        .take(1)
        .compose(SchedulersCompat.<CardResponse>applyNewSchedulers())
        .subscribe(new SimpleSubscriber<CardResponse>() {

          @Override
          public void onError(Throwable e) {
            dismissLoadingDialog();
            showToast(R.string.network_not_available);
          }

          @Override
          public void onNext(CardResponse cardResponse) {
            dismissLoadingDialog();
            if (cardResponse.isSuccess()) {
              CardActivity.cards.add(cardResponse.cardBean);
              showToast("绑定卡成功");
            } else {
              showToast(cardResponse.msg);
            }
          }
        });


  }
}
