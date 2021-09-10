package com.chaoxing.activity.util;

import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.URLUtil;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/3 18:50
 * <p>
 */
public class UrlUtils {

    private static final String URL_REGEX = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^http(?:s?):\\/\\/.*?(\\/.*)");


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
        Map<CharSequence, CharSequence> existQueryParam = Maps.newHashMap(UrlQuery.of(urlItem.getQuery(), StandardCharsets.UTF_8).getQueryMap());
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

    /**替换url的域名
     * @Description 
     * @author wwb
     * @Date 2021-09-10 15:39:42
     * @param url
     * @param domain
     * @return java.lang.String
    */
    public static String replaceDomain(String url, String domain) {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(domain)) {
            return url;
        }
        Matcher matcher = DOMAIN_PATTERN.matcher(url);
        if (matcher.find()) {
            return domain + matcher.group(1);
        }
        return url;
    }

}