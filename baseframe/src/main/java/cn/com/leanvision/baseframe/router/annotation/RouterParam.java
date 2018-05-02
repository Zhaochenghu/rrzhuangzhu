package cn.com.leanvision.baseframe.router.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/********************************
 * Created by lvshicheng on 2016/12/1.
 ********************************/
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface RouterParam {

  String value() default "";

}
