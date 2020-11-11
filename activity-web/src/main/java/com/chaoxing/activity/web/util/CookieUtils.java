package com.chaoxing.activity.web.util;

import com.chaoxing.activity.util.constant.CookieConstant;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wwb
 * @version ver 1.0
 * @className CookieUtils
 * @description
 * @blame wwb
 * @date 2020-09-02 14:40:14
 */
public class CookieUtils {

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

}
