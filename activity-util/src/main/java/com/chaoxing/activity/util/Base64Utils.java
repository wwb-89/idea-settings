package com.chaoxing.activity.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className Base64Utils
 * @description
 * @blame wwb
 * @date 2021-07-16 18:33:00
 */
public class Base64Utils {

	private Base64Utils() {

	}

	/**根据base64字符串获取Resource
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-16 19:12:24
	 * @param base64Str
	 * @return java.lang.String
	*/
	public static String getBase64Data(String base64Str) {
		return base64Str.split(",")[1];
	}

	/**根据base64字符串获取文件前缀
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-16 19:16:43
	 * @param base64Str
	 * @return java.lang.String
	*/
	public static String getSuffixFromBase64Str(String base64Str) {
		String type = base64Str.split(",")[0];
		return "." + type.split(";")[0].split("/")[1];
	}

}
