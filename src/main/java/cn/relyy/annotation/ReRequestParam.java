package cn.relyy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识接口入参
 * @author cairuirui
 * @create 2018-01-24
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReRequestParam {
    /**
     * 参数别名
     * @return
     */
    String value() default "";

    /**
     * 参数是否必传，默认必传
     * @return
     */
    boolean required() default true;

}
