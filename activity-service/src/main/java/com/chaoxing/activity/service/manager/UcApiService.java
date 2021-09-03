package com.chaoxing.activity.service.manager;

import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.UserExtraInfoDTO;
import com.chaoxing.activity.dto.manager.uc.ClazzDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwClassDTO;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private static final String USER_EXTRA_INFO_URL = "https://uc.chaoxing.com/userInter/getUserExtraInfo?fid=%d&uid=%d";
	/** 判断是不是管理员 */
	private static final String USER_MANAGER_JUDGE_URL = "https://uc.chaoxing.com/siteInter/checkUserManage?fid=%d&uid=%d";

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

	/**查询教师执教班级列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-03 11:18:46
	 * @param uid
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.dto.manager.uc.ClazzDTO>
	*/
	public List<ClazzDTO> listTeacherTeachingClazz(Integer uid, Integer fid) {
		UserExtraInfoDTO userExtraInfoDto = getUserExtraInfoByFidAndUid(fid, uid);
		if (userExtraInfoDto != null) {
			List<WfwClassDTO> classes = userExtraInfoDto.getClasses();
			return Optional.ofNullable(classes).orElse(Lists.newArrayList()).stream().map(v -> v.buildClazzDTO()).collect(Collectors.toList());
		}
		return Lists.newArrayList();
	}

	/**
	* @Description
	* @author huxiaolong
	* @Date 2021-09-03 14:18:11
	* @param request
	* @param url
	* @return java.util.List<java.lang.Object>
	*/
    public List<ClazzDTO> listTeacherTeachingClazz(HttpServletRequest request, String url) {
		Integer fid = CookieUtils.getFid(request);
		Integer uid = CookieUtils.getUid(request);
		URL urlItem = URLUtil.url(url);
		Map<CharSequence, CharSequence> urlQuery = UrlQuery.of(urlItem.getQuery(), StandardCharsets.UTF_8).getQueryMap();
		if (StringUtils.isBlank(urlQuery.get("uid"))) {
			urlQuery.put("uid", String.valueOf(uid));
		}
		if (StringUtils.isBlank(urlQuery.get("fid"))) {
			urlQuery.put("fid", String.valueOf(fid));
		}
		String realUrl = StringUtils.isBlank(urlItem.getProtocol()) ? "http" : urlItem.getProtocol() + "://" + urlItem.getHost() + urlItem.getPath();

		String result = restTemplate.getForObject(realUrl, String.class);
		JSONObject jsonObject = JSON.parseObject(result);

		if (jsonObject.getBoolean("success")) {
			return JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), ClazzDTO.class);
		} else {
			String errorMessage = jsonObject.getString("message");
			log.error("查询执教班级列表失败:{}", errorMessage);
			throw new BusinessException(errorMessage);
		}
    }
}