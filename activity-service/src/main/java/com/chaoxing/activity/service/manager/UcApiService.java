package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.UserExtraInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className UcApiService
 * @description
 * @blame wwb
 * @date 2020-11-12 19:56:02
 */
@Slf4j
@Service
public class UcApiService {

	/** 获取用户额外信息url */
	private static final String USER_EXTRA_INFO_URL = "http://uc.chaoxing.com/userInter/getUserExtraInfo?fid=%d&uid=%d";
	/** 判断是不是管理员 */
	private static final String USER_MANAGER_JUDGE_URL = "http://uc.chaoxing.com/siteInter/checkUserManage?fid=%d&uid=%d";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**根据fid和uid查询用户额外信息
	 * @Description
	 * @author wwb
	 * @Date 2020-09-02 16:23:32
	 * @param fid
	 * @param uid
	 * @return com.chaoxing.basicedu.readactivity.platform.dto.UserExtraInfoDTO
	 */
	public UserExtraInfoDTO getUserExtraInfoByFidAndUid(Integer fid, Integer uid) {
		String url = String.format(USER_EXTRA_INFO_URL, fid, uid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean status = jsonObject.getBoolean("status");
		status = Optional.ofNullable(status).orElse(Boolean.FALSE);
		if (status) {
			return JSON.parseObject(result, UserExtraInfoDTO.class);
		} else {
			log.warn("根据fid:{}, uid:{}查询用户额外信息error:{}", fid, uid, jsonObject.getString("msg"));
			return null;
		}
	}

	/**用户在某个机构下是不是管理员
	 * @Description
	 * @author wwb
	 * @Date 2020-09-29 15:37:49
	 * @param fid
	 * @param uid
	 * @return boolean
	 */
	public boolean isManager(Integer fid, Integer uid) {
		String url = String.format(USER_MANAGER_JUDGE_URL, fid, uid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer status = jsonObject.getInteger("status");
		if (status.equals(1)) {
			return true;
		} else {
			String msg = jsonObject.getString("msg");
			log.warn("根据fid:{}, uid:{}查询用户是否管理员:{}", fid, uid, msg);
			return false;
		}
	}

}