package cn.com.leanvision.baseframe.util;

import org.junit.Test;

import cn.com.leanvision.baseframe.security.MD5Helper;

import static junit.framework.Assert.assertEquals;

/********************************
 * Created by lvshicheng on 2016/12/7.
 ********************************/
public class SecurityTest {

  @Test
  public void test_md5() {
    String srcStr = "skjfksdj";
    String destStr = MD5Helper.getMD5String(srcStr);
    System.out.println(destStr);
    assertEquals(destStr.length(), 32);
    assertEquals(destStr, "ce5138c5a029d38a12ba4995534987ec");
  }
}
