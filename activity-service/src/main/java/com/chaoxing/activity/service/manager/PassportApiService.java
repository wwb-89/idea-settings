package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className PassportApiService
 * @description
 * @blame wwb
 * @date 2020-11-12 14:20:26
 */
@Slf4j
@Service
public class PassportApiService {

	/** 获取用户信息地址 */
	private static final String GET_USER_URL = "http://passport2.chaoxing.com/api/userinfo?uid=%s&enc=%s&last=true";
	private static final String KEY = "uWwjeEKsri";
	/** 免密登录key */
	private static final String AVOID_CLOSE_LOGIN_KEY = "jsDyctOCn7qHzRvrtcJ6";
	public static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/** 获取机构名称url */
	private static final String ORG_NAME_URL = "https://passport2.chaoxing.com/org/getName?schoolid=";
	/** passport免密登录url */
	public static final String AVOID_CLOSE_LOGIN_URL = "http://passport2.chaoxing.com/api/login";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**查询机构名称
	 * @Description
	 * @author wwb
	 * @Date 2019-10-23 10:02:52
	 * @param fid
	 * @return java.lang.String
	 */
	@Cacheable(cacheNames = CacheConstant.CACHE_KEY_PREFIX + "org", unless = "#result == null")
	public String getOrgName(Integer fid) {
		try {
			StringBuilder url = new StringBuilder();
			url.append(ORG_NAME_URL);
			url.append(fid);
			String result = restTemplate.getForObject(url.toString(), String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			Integer schoolid = jsonObject.getInteger("schoolid");
			if (fid.equals(schoolid)) {
				return jsonObject.getString("name");
			}
		} catch (Exception e) {
			log.error("根据fid:{}查询机构名称失败:{}", fid, e.getMessage());
		}
		return null;
	}

	/**根据uid获取泛雅用户信息
	 * @Description
	 * @author wwb
	 * @Date 2019-06-18 09:30:27
	 * @param uid
	 * @return com.chaoxing.ydhd.cache.dto.login.FanyaUserDTO
	 */
	public PassportUserDTO getByUid(Integer uid) {
		String enc = getEnc(uid);
		String url = String.format(GET_USER_URL, uid, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean status = jsonObject.getBoolean("result");
		status = Optional.ofNullable(status).orElse(Boolean.FALSE);
		if (status) {
			PassportUserDTO fanyaUserDTO = new PassportUserDTO();
			JSONArray logininfos = jsonObject.getJSONArray("logininfos");
			int orgSize = logininfos.size();
			List<OrgDTO> affiliations = new ArrayList<>();
			for (int i = 0; i < orgSize; i++) {
				JSONObject orgJsonObject = logininfos.getJSONObject(i);
				Integer fid = orgJsonObject.getInteger("fid");
				String orgName = ((PassportApiService) AopContext.currentProxy()).getOrgName(fid);
				OrgDTO orgDTO = OrgDTO.builder()
						.fid(fid)
						.name(orgName)
						.build();
				affiliations.add(orgDTO);
			}
			fanyaUserDTO.setUid(jsonObject.getString("uid"));
			fanyaUserDTO.setLoginName(jsonObject.getString("uname"));
			fanyaUserDTO.setRealName(jsonObject.getString("realname"));
			fanyaUserDTO.setMobile(jsonObject.getString("phone"));
			fanyaUserDTO.setEmail(jsonObject.getString("email"));
			fanyaUserDTO.setAffiliations(affiliations);
			return fanyaUserDTO;
		} else {
			String errorMessage = jsonObject.getString("mes");
			log.error("根据uid:{}查询用户失败,e:{}", uid, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**超星号是否存在
	 * @Description
	 * @author wwb
	 * @Date 2020-11-03 19:23:11
	 * @param fid
	 * @param cxNo
	 * @return boolean
	 */
	public boolean isCxNoExist(String fid, String cxNo) {
		String enc = getGetUserEnc(fid, cxNo);
		String url = String.format(GET_USER_URL, cxNo, fid, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return jsonObject.getBoolean("result");
	}

	/**获取加密信息
	 * @Description
	 * @author wwb
	 * @Date 2019-06-18 09:24:47
	 * @param uid
	 * @return java.lang.String
	 */
	private String getEnc(Integer uid) {
		StringBuilder clear = new StringBuilder();
		clear.append(uid);
		clear.append(KEY);
		String enc = DigestUtils.md5Hex(clear.toString());
		return enc;
	}

	/**生成查询用户信息enc
	 * @Description
	 * @author wwb
	 * @Date 2020-11-03 19:20:19
	 * @param fid
	 * @param account
	 * @return java.lang.String
	 */
	private String getGetUserEnc(String fid, String account) {
		StringBuilder clearText = new StringBuilder();
		clearText.append(account);
		clearText.append(KEY);
		clearText.append(fid);
		return DigestUtils.md5Hex(clearText.toString());
	}

	/**免密登录
	 * @Description
	 * 1、根据uid查询用户的登录名
	 * 2、根据fid和登录名登录
	 * @author wwb
	 * @Date 2021-01-13 15:05:25
	 * @param uid
	 * @param fid
	 * @param response
	 * @return java.util.List<javax.servlet.http.Cookie>
	*/
	public List<Cookie> avoidCloseLogin(Integer uid, Integer fid, HttpServletResponse response) {
		PassportUserDTO passportUser = getByUid(uid);
		String account = passportUser.getLoginName();
		String url = AVOID_CLOSE_LOGIN_URL;
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		String enc = getLoginEnc(fid, account);
		params.add("schoolid", fid);
		params.add("name", account);
		params.add("enc", enc);
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, null);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
		String result = responseEntity.getBody();
		JSONObject jsonObject = JSON.parseObject(result);
		String resultStatus = jsonObject.getString("status");
		if ("0".equals(resultStatus)) {
			// 回写cookie
			HttpHeaders headers = responseEntity.getHeaders();
			return CookieUtils.writeCookie(headers, response);
		} else if ("3".equals(resultStatus)) {
			enc = getLoginEnc(null, account);
			params.remove("schoolid");
			params.remove("enc");
			params.add("enc", enc);
			httpEntity = new HttpEntity<>(params, null);
			responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
			result = responseEntity.getBody();
			jsonObject = JSON.parseObject(result);
			resultStatus = jsonObject.getString("status");
			if ("0".equals(resultStatus)) {
				// 回写cookie
				HttpHeaders headers = responseEntity.getHeaders();
				return CookieUtils.writeCookie(headers, response);
			} else {
				String errorMessage = jsonObject.getString("mes");
				if (StringUtils.isEmpty(errorMessage)) {
					errorMessage = jsonObject.getString("errorMsg");
				}
				log.error("用户登录error: {}", errorMessage);
				throw new BusinessException(errorMessage);
			}
		} else {
			String errorMessage = jsonObject.getString("mes");
			if (StringUtils.isEmpty(errorMessage)) {
				errorMessage = jsonObject.getString("errorMsg");
			}
			log.error("用户登录error: {}", errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	private String getLoginEnc(Integer fid, String account) {
		StringBuilder clearText = new StringBuilder();
		if (fid != null) {
			clearText.append(fid);
		}
		clearText.append(account);
		LocalDate now = LocalDate.now();
		clearText.append(now.format(YYYYMMDD));
		clearText.append(AVOID_CLOSE_LOGIN_KEY);
		return DigestUtils.md5Hex(clearText.toString());
	}

}