package com.chaoxing.activity.util;

import java.math.BigDecimal;

/**百度地图工具类
 * @author wwb
 * @version ver 1.0
 * @className BaiduMapUtils
 * @description
 * @blame wwb
 * @date 2021-03-09 15:21:26
 */
public class BaiduMapUtils {
	
	private static final String ADDRESS_PC_URL = "http://api.map.baidu.com/marker?location=%s&title=%s&content=%s&output=webapp.baidu.openAPIdemo";
	private static final String ADDRESS_MOBILE_URL = "http://api.map.baidu.com/mobile/marker?location=%s&title=%s&content=%s&output=webapp.baidu.openAPIdemo";

	private BaiduMapUtils() {

	}

	/**生成pc端的地址url
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-09 15:31:31
	 * @param longitude
	 * @param dimension
	 * @param activityName
	 * @param address
	 * @return java.lang.String
	*/
	public static String generateAddressPcUrl(BigDecimal longitude, BigDecimal dimension, String activityName, String address) {
		if (longitude == null || dimension == null) {
			return "";
		}
		return String.format(ADDRESS_PC_URL, longitude + "," + dimension, activityName, address);
	}
	
	/**生成移动端的地址url
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-09 15:31:42
	 * @param longitude
	 * @param dimension
	 * @param activityName
	 * @param address
	 * @return java.lang.String
	*/
	public static String generateAddressMobileUrl(BigDecimal longitude, BigDecimal dimension, String activityName, String address) {
		if (longitude == null || dimension == null) {
			return "";
		}
		return String.format(ADDRESS_MOBILE_URL, longitude + "," + dimension, activityName, address);
	}

}
