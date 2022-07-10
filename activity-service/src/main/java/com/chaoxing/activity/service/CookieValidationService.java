package com.chaoxing.activity.service;

import com.chaoxing.activity.util.RsaUtils;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**cookie验证服务
 * @author wwb
 * @version ver 1.0
 * @className CookieValidationService
 * @description
 * @blame wwb
 * @date 2019-10-28 16:04:15
 */
@Service
public class CookieValidationService {

	/** 验证公钥签名的公钥 */
	private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAZ2WX9e0wrotq7ynUPhl6ISkaY0xNTS1UxvsBhYgTTqp9tm/UJJfUaghqH2LtaJnpun1DYUt2xTfJSktdxEJO5326bKWZVbuBJhSthjrRlWngGlmFGbNaEx4l8ASfMzlZjs/G5Jwh8AvF+ZVLo/YfpS7n9xKbvtwGQBQ6yuNK0QIDAQAB";

	/**是否有效
	 * @Description:
	 * @author: wwb
	 * @Date: 2019-06-24 18:45:21
	 * @param: uid 用户的uid
	 * @param: time 时间
	 * @param: signature 签名
	 * @return: boolean
	 */
	public boolean isEffective(Integer uid, long time, String signature) throws UnsupportedEncodingException {
		String clearText = getClearText(uid, time);
		signature = URLDecoder.decode(signature, StandardCharsets.UTF_8.name());
		return RsaUtils.verifySign(clearText, signature, PUBLIC_KEY);
	}
	/**获取明文
	 * @Description:
	 * @author: wwb
	 * @Date: 2019-06-24 18:48:55
	 * @param: uid
	 * @param: time
	 * @return: java.lang.String
	 */
	private String getClearText(Integer uid, long time) {
		StringBuilder clearText = new StringBuilder();
		clearText.append("[");
		clearText.append(uid);
		clearText.append("]");
		clearText.append("[");
		clearText.append(time);
		clearText.append("]");
		return clearText.toString();
	}

}
