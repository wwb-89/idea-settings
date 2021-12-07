package com.chaoxing.activity.util;

import org.apache.commons.lang3.StringUtils;

/**手机号工具类
 * @author wwb
 * @version ver 1.0
 * @className MobileUtils
 * @description
 * @blame wwb
 * @date 2021-08-18 18:40:38
 */
public class MobileUtils {

    private MobileUtils() {

    }

    /**手机号脱敏
     * @Description 
     * @author wwb
     * @Date 2021-12-07 11:08:05
     * @param mobile
     * @return java.lang.String
    */
    public static String desensitization(String mobile) {
        if (StringUtils.isNotBlank(mobile)) {
            return mobile.replaceAll("^(\\d{3})(\\d{0,4})(\\d{0,})", "$1****$3");
        }
        return mobile;
    }

}
