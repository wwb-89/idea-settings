package com.chaoxing.activity.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.Assert;

import java.beans.Introspector;

/**
 * @author wwb
 * @version ver 1.0
 * @className CustomAnnotationBeanNameGenerator
 * @description
 * @blame wwb
 * @date 2020-09-17 20:12:52
 */
public class CustomAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state(beanClassName != null, "No bean class name set");
        return Introspector.decapitalize(beanClassName);
    }

}