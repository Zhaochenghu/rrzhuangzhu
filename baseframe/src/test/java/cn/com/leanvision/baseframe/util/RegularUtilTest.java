package cn.com.leanvision.baseframe.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

/********************************
 * Created by lvshicheng on 2016/12/1.
 ********************************/
@RunWith(JUnit4.class)
public class RegularUtilTest {

  @Test
  public void test_matchEndNum() {
    String target = "T36_45";
    String resultExpected = "45";
    String result = LvRegularUtil.matchEndNum(target);
    assertEquals(resultExpected, result);
  }
}
