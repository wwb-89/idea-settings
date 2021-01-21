package com.chaoxing.activity.admin.util;

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
        return getWfwfid(request);
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

}