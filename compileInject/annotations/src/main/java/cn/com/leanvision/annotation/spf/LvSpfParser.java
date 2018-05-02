package cn.com.leanvision.annotation.spf;

/********************************
 * Created by lvshicheng on 2016/12/4.
 ********************************/
public interface LvSpfParser {

  Object deserialize(Class clazz, String text);

  String serialize(Object object);
}
