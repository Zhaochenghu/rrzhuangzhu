package cn.com.leanvision.annotation.spf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/********************************
 * Created by lvshicheng on 2016/12/4.
 ********************************/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface LvSpfConfig {

  boolean encryption() default false;

  boolean save() default true;

  boolean commit() default false;

  boolean preferences() default false;

  boolean global() default true;
}
