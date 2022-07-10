package com.chaoxing.activity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/10/27 11:06
 * <p>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface SignatureValid {

}
