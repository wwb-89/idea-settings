package com.chaoxing.activity.service.auth;

import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

/**api参数认证服务
 * @author wwb
 * @version ver 1.0
 * @className ApiParamAuthService
 * @description
 * @blame wwb
 * @date 2021-01-13 16:26:23
 */
@Slf4j
@Service
public class ApiParamAuthService {

	public void loginProxyAuth(Integer uid, Integer fid, String enc) {
		String clearText = "";
		clearText = clearText + uid + fid + CommonConstant.LOGIN_AUTH_KEY;
		String realEnc = DigestUtils.md5Hex(clearText);
		if (!realEnc.equalsIgnoreCase(enc)) {
			throw new BusinessException("enc认证不通过");
		}
	}

}
