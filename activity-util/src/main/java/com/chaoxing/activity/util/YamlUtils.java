package com.chaoxing.activity.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**yaml工具类
 * @author wwb
 * @version ver 1.0
 * @className YamlUtils
 * @description
 * @blame wwb
 * @date 2021-12-06 15:15:04
 */
public class YamlUtils {

    private YamlUtils() {

    }

    private static Properties getProperties(String path) {
        Resource resource = new ClassPathResource(path);
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(resource);
        Properties properties = yamlPropertiesFactoryBean.getObject();
        return properties;
    }

    public static String getStringValue(String path, String key) {
        Properties properties = getProperties(path);
        return properties.getProperty(key);
    }

    public static Integer getIntegerValue(String path, String key) {
        String stringValue = getStringValue(path, key);
        if (StringUtils.isNotBlank(stringValue)) {
            return Integer.parseInt(stringValue);
        }
        return null;
    }

}