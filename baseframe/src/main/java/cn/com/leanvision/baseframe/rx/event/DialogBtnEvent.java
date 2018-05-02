package cn.com.leanvision.baseframe.rx.event;

/********************************
 * Created by lvshicheng on 2016/12/14.
 ********************************/
public class DialogBtnEvent {

  /**
   * button 按下的类型 1为positive， 0为negative
   */
  public int btnType = 1;

  public DialogBtnEvent(int btnType) {
    this.btnType = btnType;
  }
}
