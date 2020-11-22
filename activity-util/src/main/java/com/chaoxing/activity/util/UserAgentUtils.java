package com.chaoxing.activity.util;

import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserAgentUtils
 * @description
 * @blame wwb
 * @date 2020-11-20 14:51:41
 */
public class UserAgentUtils {

	private UserAgentUtils() {

	}

	/**是不是移动端请求
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-20 14:54:55
	 * @param request
	 * @return boolean
	*/
	public static boolean isMobileAccess(HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		UserAgent userAgent = UserAgent.parseUserAgentString(header);
		boolean isMobile = false;
		if (userAgent != null) {
			DeviceType deviceType = userAgent.getOperatingSystem().getDeviceType();
			isMobile = deviceType.equals(DeviceType.MOBILE);
		}
		return isMobile;
	}

}
