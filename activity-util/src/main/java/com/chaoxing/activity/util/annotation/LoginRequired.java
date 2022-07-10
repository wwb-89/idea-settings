package com.chaoxing.activity.util.annotation;

import java.lang.annotation.*;

/**
 * @author wwb
 * @version ver 1.0
 * @className LoginRequired
 * @description
 * @blame wwb
 * @date 2020-11-27 16:29:32
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginRequired {

	// 是否需要验证
	boolean required() default true;

}