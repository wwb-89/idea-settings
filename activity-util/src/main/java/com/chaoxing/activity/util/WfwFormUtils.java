package com.chaoxing.activity.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.TreeMap;

/**万能表单工具类
 * @author wwb
 * @version ver 1.0
 * @className WfwFormUtils
 * @description
 * @blame wwb
 * @date 2021-12-13 14:55:57
 */
public class WfwFormUtils {

    private WfwFormUtils() {

    }

    /**获取参数加密串
     * @Description 
     * @author wwb
     * @Date 2021-12-13 14:57:02
     * @param paramMap
     * @param key
     * @return java.lang.String
    */
    public static String getEnc(TreeMap<String, Object> paramMap, String key) {
        StringBuilder enc = new StringBuilder();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            enc.append("[").append(entry.getKey()).append("=")
                    .append(entry.getValue()).append("]");
        }
        return DigestUtils.md5Hex(enc + "[" + key + "]");
    }

}
