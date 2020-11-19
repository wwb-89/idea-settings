package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
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
	/** 获取机构名称url */
	private static final String ORG_NAME_URL = "https://passport2.chaoxing.com/org/getName?schoolid=";

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

}