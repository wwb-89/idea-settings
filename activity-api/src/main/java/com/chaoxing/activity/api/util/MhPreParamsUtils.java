package com.chaoxing.activity.api.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className MhPreParamsUtils
 * @description
 * @blame wwb
 * @date 2021-05-08 15:21:07
 */
public class MhPreParamsUtils {

    private MhPreParamsUtils() {

    }

    /**解析preParams
     * @Description 
     * @author wwb
     * @Date 2021-05-08 15:27:47
     * @param preParams
     * @return java.util.Map<java.lang.String,java.lang.Object>
    */
    public static JSONObject resolve(String preParams) {
        JSONObject urlParams = new JSONObject();
        if (StringUtils.isNotBlank(preParams)) {
            String[] paramsGroups = preParams.split("&");
            for (int i = 0; i < paramsGroups.length; i++) {
                String paramsGroup = paramsGroups[i];
                if (StringUtils.isNotBlank(paramsGroup)) {
                    String[] kv = paramsGroup.split("=");
                    if (kv.length == 2) {
                        String k = kv[0];
                        String v = kv[1];
                        if (StringUtils.isNotBlank(v)) {
                            String[] values = v.split(",");
                            if (values.length > 1) {
                                List<String> valuesClone = Lists.newArrayList(values);
                                urlParams.put(k, valuesClone);
                            } else {
                                urlParams.put(k, v);
                            }
                        } else {
                            urlParams.put(k, v);
                        }
                    }
                }

            }
        }
        return urlParams;
    }

}
