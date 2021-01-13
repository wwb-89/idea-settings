package com.chaoxing.activity.util;

import com.chaoxing.activity.util.constant.CookieConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

/**
 * @author wwb
 * @version ver 1.0
 * @className CookieUtils
 * @description
 * @blame wwb
 * @date 2020-09-02 14:40:14
 */
public class CookieUtils {

    private static final String COOKIE_SEPARATOR = ";";
    private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_HTTPONLY = "HttpOnly";
    private static final String COOKIE_PATH = "Path";
    private static final String COOKIE_EXPIRES = "Expires";
    private static final String COOKIE_DOMAIN = "Domain";

    private static final Pattern COOKIE_DOMAIN_PATTERN = Pattern.compile("^\\.");

    private CookieUtils() {

    }

    public static String getUid(HttpServletRequest request) {
        return getValue(request, CookieConstant.UID);
    }

    private static String getWfwfid(HttpServletRequest request) {
        return getValue(request, CookieConstant.WFWFID);
    }

    private static String getSpaceFid(HttpServletRequest request) {
        return getValue(request, CookieConstant.SPACE_FID);
    }

    public static String getFid(HttpServletRequest request) {
        String fid = getSpaceFid(request);
        if (StringUtils.isBlank(fid)) {
            fid = getValue(request, CookieConstant.FID);
        }
        return fid;
    }

    public static long getValidateTime(HttpServletRequest request) {
        String time = getValue(request, CookieConstant.TIME);
        if (StringUtils.isNotEmpty(time)) {
            return Long.parseLong(time);
        }
        return 0L;
    }

    /**获取签名
     * @Description
     * @author wwb
     * @Date 2019-06-24 19:04:30
     * @param request
     * @return java.lang.String
     */
    public static String getSignature(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (CookieConstant.SIGNATURE.equalsIgnoreCase(name)) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    private static String getValue(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(key)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**写cookie
     * @Description 
     * @author wwb
     * @Date 2021-01-13 15:58:46
     * @param headers
     * @param response
     * @return java.util.List<javax.servlet.http.Cookie>
    */
    public static List<Cookie> writeCookie(HttpHeaders headers, HttpServletResponse response) {
        List<String> cookieStrs = headers.get(SET_COOKIE);
        List<Cookie> allCookies = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cookieStrs)) {
            cookieStrs.forEach(cookieStr -> {
                String[] keyValues = cookieStr.split(COOKIE_SEPARATOR);
                Boolean httpOnly = false;
                String path = "/";
                String domain = "";
                List<Cookie> cookies = new ArrayList<>();
                for (String keyValue : keyValues) {
                    if (org.springframework.util.StringUtils.isEmpty(keyValue)) {
                        continue;
                    }
                    String[] keyAndValue = keyValue.split(COOKIE_KEY_VALUE_SEPARATOR);
                    if (keyAndValue.length > 1) {
                        String key = keyAndValue[0];
                        if (!org.springframework.util.StringUtils.isEmpty(key)) {
                            key = key.trim();
                        }
                        String value = keyAndValue[1];
                        if (!org.springframework.util.StringUtils.isEmpty(value)) {
                            value = value.trim();
                        }
                        if (COOKIE_PATH.equalsIgnoreCase(key)) {
                            path = value;
                            continue;
                        }
                        if (COOKIE_DOMAIN.equalsIgnoreCase(key)) {
                            domain = value;
                        }
                        if (COOKIE_EXPIRES.equalsIgnoreCase(key)) {
                            continue;
                        }
                        Cookie cookie = new Cookie(key, value);
                        cookies.add(cookie);
                    } else if (keyAndValue.length == 1 && keyAndValue[0].equalsIgnoreCase(COOKIE_HTTPONLY)) {
                        httpOnly = true;
                    }
                }
                for (Cookie cookie : cookies) {
                    cookie.setHttpOnly(httpOnly);
                    cookie.setPath(path);
                    domain = domain.replaceAll(COOKIE_DOMAIN_PATTERN.pattern(), "");
                    cookie.setDomain(domain);
                    response.addCookie(cookie);
                }
                allCookies.addAll(cookies);
            });
        }
        return allCookies;
    }

}