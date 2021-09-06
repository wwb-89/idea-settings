package com.chaoxing.activity.util;

import com.chaoxing.activity.util.constant.CookieConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public static Integer getUid(HttpServletRequest request) {
        String uidStr = getValue(request, CookieConstant.UID);
        if (StringUtils.isNotBlank(uidStr)) {
            return Integer.parseInt(uidStr);
        }
        return null;
    }

    private static Integer getWfwfid(HttpServletRequest request) {
        String wfwfidStr = getValue(request, CookieConstant.WFWFID);
        if (StringUtils.isNotBlank(wfwfidStr)) {
            return Integer.parseInt(wfwfidStr);
        }
        return null;
    }

    private static Integer getSpaceFid(HttpServletRequest request) {
        String spaceFidStr = getValue(request, CookieConstant.SPACE_FID);
        if (StringUtils.isNotBlank(spaceFidStr)) {
            return Integer.parseInt(spaceFidStr);
        }
        return null;
    }

    public static Integer getFid(HttpServletRequest request) {
        Integer fid = getSpaceFid(request);
        if (fid == null) {
            String fidStr = getValue(request, CookieConstant.FID);
            if (StringUtils.isNotBlank(fidStr)) {
                fid = Integer.parseInt(fidStr);
            }
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

    /**获取cookie列表
     * @Description 
     * @author wwb
     * @Date 2021-01-28 14:40:34
     * @param request
     * @return java.util.List<java.lang.String>
    */
    public static List<String> getCookies(HttpServletRequest request) {
        List<String> cookies = Lists.newArrayList();
        Cookie[] cookieArr = request.getCookies();
        if (cookieArr == null || cookieArr.length == 0) {
            return cookies;
        }
        for (Cookie cookie : cookieArr) {
            cookies.add(cookie.getName() + "=" + cookie.getValue());
        }
        return cookies;
    }

    /**根据给定keys， 获取对应的cookieMap
    * @Description
    * @author huxiaolong
    * @Date 2021-09-03 19:46:39
    * @param request
    * @param keys
    * @return java.util.Map<java.lang.String,java.lang.String>
    */
    public static Map<String, String> getCookieMap(HttpServletRequest request, List<String> keys) {
        Map<String, String> cookieMap = Maps.newHashMap();
        Cookie[] cookieArr = request.getCookies();
        if (CollectionUtils.isEmpty(keys) || cookieArr == null || cookieArr.length == 0) {
            return cookieMap;
        }
        for (Cookie cookie : cookieArr) {
            String name = cookie.getName();
            if (keys.contains(name)) {
                cookieMap.put(name, cookie.getValue());
            }
        }
        return cookieMap;
    }

}