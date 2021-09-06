package com.chaoxing.activity.util;

import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.URLUtil;
import com.chaoxing.activity.util.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/3 18:50
 * <p>
 */
public class URLUtils {

    private static final String URL_REGEX = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";


    /**
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-03 19:35:39
    * @param url
* @param paramMap
    * @return java.lang.String
    */
    public static String packageParam2URL(String url, Map<String, String> paramMap) {
        if (!Pattern.matches(URL_REGEX, url)) {
            throw new BusinessException("url非法");
        }
        URL urlItem = URLUtil.url(url);
        Map<CharSequence, CharSequence> existQueryParam = UrlQuery.of(urlItem.getQuery(), StandardCharsets.UTF_8).getQueryMap();
        paramMap.forEach((paramKey, paramValue) -> {
            if (!existQueryParam.containsKey(paramKey)) {
                existQueryParam.put(paramKey, paramValue);
            }
        });
        String realUrl =urlItem.getProtocol() + "://" + urlItem.getHost() + urlItem.getPath();
        if (existQueryParam.isEmpty()) {
            return realUrl;
        }
        return realUrl + "?" + UrlQuery.of(existQueryParam).build(StandardCharsets.UTF_8);

    }
}
