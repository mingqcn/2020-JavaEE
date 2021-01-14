package cn.edu.xmu.ooad.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @auther mingqiu
 * @date 2020/6/26 下午2:04
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {
    /** 要执行的具体操作比如：添加用户 **/
}
