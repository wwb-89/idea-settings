package com.chaoxing.activity.util;

import com.chaoxing.activity.util.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
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
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^http(?:s?):\\/\\/(?:[^\\/])*(?<!\\/)");

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
        return domain + clearDomain(url);
    }

    /**清除地址的域名
     * @Description 
     * @author wwb
     * @Date 2022-01-05 17:29:28
     * @param url
     * @return java.lang.String
    */
    public static String clearDomain(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        String domain = extractDomain(url);
        if (StringUtils.isNotBlank(domain)) {
            return url.replace(domain, "");
        }
        return url;
    }

    /**提取域名
     * @Description 
     * @author wwb
     * @Date 2022-01-08 00:08:05
     * @param url
     * @return java.lang.String
    */
    public static String extractDomain(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        Matcher matcher = DOMAIN_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return url;
    }

    /**处理重定向地址
     * @Description 保持和请求的一致
     * @author wwb
     * @Date 2022-01-12 17:07:15
     * @param redirectUrl
     * @param request
     * @return java.lang.String
    */
    public static String handleRedirectUrl(String redirectUrl, HttpServletRequest request) {
        if (StringUtils.isBlank(redirectUrl)) {
            return "";
        }
        if (!redirectUrl.startsWith(CommonConstant.SCHEME_HTTP)) {
            return redirectUrl;
        }
        String scheme = request.getScheme();
        boolean sameScheme = redirectUrl.startsWith(scheme + "://");
        if (!sameScheme) {
            String replaceScheme = Objects.equals(scheme, CommonConstant.SCHEME_HTTP) ? CommonConstant.SCHEME_HTTPS : CommonConstant.SCHEME_HTTP;
            redirectUrl = redirectUrl.replace(replaceScheme, scheme);
        }
        return redirectUrl;
    }

}