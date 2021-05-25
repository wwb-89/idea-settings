package com.chaoxing.activity.service.auth;

import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");
	/** 时间误差分钟范围 */
	private static final Integer TIME_ERROR_MINUTE_SCOPE = 1;

	public void loginProxyAuth(Integer uid, Integer fid, String enc) {
		String clearText = "";
		clearText = clearText + uid + fid + CommonConstant.LOGIN_AUTH_KEY;
		String realEnc = DigestUtils.md5Hex(clearText);
		if (!realEnc.equalsIgnoreCase(enc)) {
			throw new BusinessException("enc认证不通过");
		}
	}

	/**有时效性的登录验证
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-25 15:04:32
	 * @param uid
	 * @param fid
	 * @param date
	 * @param enc
	 * @return void
	*/
	public void loginProxyAuth(Integer uid, Integer fid, String date, String enc) {
		String clearText = "";
		// 验证date
		LocalDateTime now = LocalDateTime.now();
		String nowFormat = now.format(DATE_TIME_FORMATTER);
		if (!Objects.equals(date, nowFormat)) {
			LocalDateTime lastDateTime = now.plusMinutes(TIME_ERROR_MINUTE_SCOPE);
			String lastFormat = lastDateTime.format(DATE_TIME_FORMATTER);
			if (!Objects.equals(date, lastFormat)) {
				throw new BusinessException("date参数已过期");
			}
		}
		clearText = clearText + uid + fid + date + CommonConstant.LOGIN_AUTH_KEY;
		String realEnc = DigestUtils.md5Hex(clearText);
		if (!realEnc.equalsIgnoreCase(enc)) {
			throw new BusinessException("enc认证不通过");
		}
	}

}
