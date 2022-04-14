package com.chaoxing.activity.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @className PropertiesUtils
 * @description 
 * @author wwb
 * @blame wwb
 * @date 2017-10-30 15:06:27
 * @version ver 1.0
 */
public class PropertiesUtils {

    /** 默认的资源文件名 */
    public static final String DEFAULT_PROPERTIES_NAME = "application";

    /** 获取资源的字符串值
     * @Description 
     * @author 王文彬
     * @version 2016年1月8日 上午10:40:22
     * @param key
     * @return String
     */
    public static String getStringValue(String key){
        return getStringValue(DEFAULT_PROPERTIES_NAME, key);
    }
    /** 获取资源的字符串值
     * @Description 
     * @author 王文彬
     * @version 2016年1月8日 上午10:40:34
     * @param sourceName
     * @param key
     * @return
     * @return String
     */
    public static String getStringValue(String sourceName, String key){
        ResourceBundle resourceBundle = ResourceBundle.getBundle(sourceName, Locale.getDefault());
        return resourceBundle.getString(key);
    }
    /** 获取资源的整数值
     * @Description 
     * @author 王文彬
     * @version 2016年1月12日 上午11:26:41
     * @param key
     * @return
     * @return Integer
     */
    public static Integer getIntValue(String key){
        return getIntValue(DEFAULT_PROPERTIES_NAME, key);
    }
    /** 获取资源的整数值
     * @Description 
     * @author 王文彬
     * @version 2016年1月12日 上午11:26:41
     * @param key
     * @return
     * @return Integer
     */
    public static Integer getIntValue(String sourceName, String key){
        String stringValue = getStringValue(sourceName, key);
        return Integer.valueOf(stringValue);
    }

}
