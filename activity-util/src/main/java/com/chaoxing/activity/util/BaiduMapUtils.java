package com.chaoxing.activity.util;

import com.chaoxing.activity.util.constant.DomainConstant;

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
	
	private static final String ADDRESS_URL = DomainConstant.BAIDU_MAP_API + "/marker?location=%s&title=%s&content=%s&output=html";

	private BaiduMapUtils() {

	}

	/**生成百度地图的详细地址url
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-09 15:31:31
	 * @param longitude
	 * @param dimension
	 * @param activityName
	 * @param address
	 * @return java.lang.String
	*/
	public static String generateAddressUrl(BigDecimal longitude, BigDecimal dimension, String activityName, String address) {
		if (longitude == null || dimension == null) {
			return "";
		}
		return String.format(ADDRESS_URL, dimension + "," + longitude, activityName, address);
	}
	
}
