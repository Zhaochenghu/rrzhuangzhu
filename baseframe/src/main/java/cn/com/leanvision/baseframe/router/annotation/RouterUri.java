package cn.com.leanvision.baseframe.router.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/********************************
 * Created by lvshicheng on 2016/12/1.
 ********************************/
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface RouterUri {

  String routerUri() default "";

}
