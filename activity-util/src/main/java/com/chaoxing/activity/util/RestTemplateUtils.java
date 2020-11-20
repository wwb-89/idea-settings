package com.chaoxing.activity.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className RestTemplateUtils
 * @description
 * @blame wwb
 * @date 2020-11-19 14:49:38
 */
public class RestTemplateUtils {

	private RestTemplateUtils() {

	}

	public static List<String> getCookies(HttpServletRequest request) {
		List<String> cookieStrs = new ArrayList<>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				String value = cookie.getValue();
				StringBuilder cookieStrStringBuilder = new StringBuilder();
				cookieStrStringBuilder.append(name);
				cookieStrStringBuilder.append("=");
				cookieStrStringBuilder.append(value);
				cookieStrs.add(cookieStrStringBuilder.toString());
			}
		}
		return cookieStrs;
	}

}
