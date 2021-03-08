package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.manager.sign.SignIn;
import com.chaoxing.activity.dto.manager.sign.SignParticipantStatDTO;
import com.chaoxing.activity.dto.manager.sign.SignUp;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.dto.sign.ActivityBlockDetailSignStatDTO;
import com.chaoxing.activity.dto.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.RestTemplateUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**报名签到服务
 * @author wwb
 * @version ver 1.0
 * @className SignApiService
 * @description
 * @blame wwb
 * @date 2020-11-11 10:35:53
 */
@Slf4j
@Service
public class SignApiService {

	private static final String DOMAIN = "http://api.qd.reading.chaoxing.com";
	/** 创建签到报名的地址 */
	private static final String CREATE_URL = DOMAIN + "/sign/new";
	/** 修改签到报名的地址 */
	private static final String UPDATE_URL = DOMAIN + "/sign/update";
	/** 获取签到报名信息的地址 */
	private static final String DETAIL_URL = DOMAIN + "/sign/%d/detail";
	/** 参与情况 */
	private static final String PARTICIPATION_URL = DOMAIN + "/sign/%d/participation";
	/** 统计报名签到在活动管理首页需要的信息 */
	private static final String STAT_SIGN_ACTIVITY_MANAGE_INDEX_URL = DOMAIN + "/sign/%d/stat/activity-index";
	/** 统计报名签到报名成功数量url */
	private static final String STAT_SIGNED_UP_NUM = DOMAIN + "/sign/stat/signed-up-num";
	/** 活动块详情统计信息url */
	private static final String ACTIVITY_BLOCK_DETAIL_STAT_URL = DOMAIN + "/sign/stat/activity-block-detail?signId=%s&uid=%s";
	/** 用户已报名的报名签到列表url */
	private static final String USER_SIGNED_UP_URL = DOMAIN + "/sign/stat/sign/user-signed-up/%d";
	/** 通知已收藏url */
	private static final String NOTICE_COLLECTED_URL = DOMAIN + "/sign/%d/notice/collected";
	
	/** 取消报名 */
	private static final String CANCEL_SIGN_UP_URL = DOMAIN + "/sign-up/%d/cancel";
	/** 撤销报名 */
	private static final String REVOCATION_SIGN_UP_URL = DOMAIN + "/sign-up/%d/revocation";

	/** 查询报名成功的uid列表url */
	private static final String SIGNED_UP_UIDS_URL = DOMAIN + "/sign/%s/uid/signed-up";
	/** 用户是否已报名（报名成功）url */
	private static final String USER_SIGNED_UP_SUCCESS_URL = DOMAIN + "/sign-up/%d/signed-up-success?uid=%d";


	@Resource
	private RestTemplate restTemplate;

	/**创建报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 14:26:33
	 * @param signAddEdit
	 * @param request
	 * @return java.lang.Integer 签到报名id
	*/
	public Integer create(SignAddEditDTO signAddEdit, HttpServletRequest request) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		List<String> cookies = RestTemplateUtils.getCookies(request);
		httpHeaders.put("Cookie", cookies);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signAddEdit), httpHeaders);
		String result = restTemplate.postForObject(CREATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return jsonObject.getInteger("data");
		} else {
			String errorMessage = jsonObject.getString("message");
			log.error("创建报名报名:{}失败:{}", JSON.toJSONString(signAddEdit), errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**更新报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 17:58:39
	 * @param signAddEdit
	 * @return void
	*/
	public void update(SignAddEditDTO signAddEdit, HttpServletRequest request) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		List<String> cookies = RestTemplateUtils.getCookies(request);
		httpHeaders.put("Cookie", cookies);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signAddEdit), httpHeaders);
		String result = restTemplate.postForObject(UPDATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("message");
			log.error("修改签到报名:{}失败:{}", JSON.toJSONString(signAddEdit), errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**获取签到报名信息
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-19 13:15:47
	 * @param signId
	 * @return com.chaoxing.activity.dto.module.SignAddEditDTO
	*/
	public SignAddEditDTO getById(Integer signId) {
		String url = String.format(DETAIL_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			String data = jsonObject.getString("data");
			SignAddEditDTO signAddEdit = JSON.parseObject(data, SignAddEditDTO.class);
			SignUp signUp = signAddEdit.getSignUp();
			Optional.ofNullable(signUp).ifPresent(v -> {
				signUp.setStartTimestamp(signUp.getStartTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
				signUp.setEndTimestamp(signUp.getEndTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
			});
			SignIn signIn = signAddEdit.getSignIn();
			Optional.ofNullable(signIn).ifPresent(v -> {
				signIn.setStartTimestamp(signIn.getStartTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
				signIn.setEndTimestamp(signIn.getEndTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
			});
			SignIn signOut = signAddEdit.getSignOut();
			Optional.ofNullable(signOut).ifPresent(v -> {
				signOut.setStartTimestamp(signOut.getStartTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
				signOut.setEndTimestamp(signOut.getEndTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
			});
			return signAddEdit;
		} else {
			String errorMessage = jsonObject.getString("message");
			log.error("根据签到报名id:{}查询签到报名失败:{}", signId, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**查询签到活动的参与情况
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 20:15:35
	 * @param signActivityId
	 * @return com.chaoxing.activity.dto.manager.SignParticipationDTO
	*/
	public SignParticipantStatDTO getSignParticipation(Integer signActivityId) {
		SignParticipantStatDTO signParticipantStat = null;
		if (signActivityId != null) {
			String url = String.format(PARTICIPATION_URL, signActivityId);
			String result = restTemplate.getForObject(url, String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			Boolean success = jsonObject.getBoolean("success");
			success = Optional.ofNullable(success).orElse(Boolean.FALSE);
			if (success) {
				signParticipantStat = JSON.parseObject(jsonObject.getString("data"), SignParticipantStatDTO.class);
			}
		}
		if (signParticipantStat == null) {
			signParticipantStat = SignParticipantStatDTO.builder()
					.limitNum(0)
					.participateNum(0)
					.build();
		}
		return signParticipantStat;
	}

	/**统计报名签到在活动管理首页需要的信息
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 11:25:45
	 * @param signId
	 * @return com.chaoxing.activity.dto.sign.SignActivityManageIndexDTO
	*/
	public SignActivityManageIndexDTO statSignActivityManageIndex(Integer signId) {
		if (signId != null) {
			String url = String.format(STAT_SIGN_ACTIVITY_MANAGE_INDEX_URL, signId);
			try {
				String result = restTemplate.getForObject(url, String.class);
				JSONObject jsonObject = JSON.parseObject(result);
				Boolean success = jsonObject.getBoolean("success");
				success = Optional.ofNullable(success).orElse(Boolean.FALSE);
				if (success) {
					return JSON.parseObject(jsonObject.getString("data"), SignActivityManageIndexDTO.class);
				} else {
					String errorMessage = jsonObject.getString("message");
					log.error("根据报名签到id:{}统计报名签到在活动管理首页需要的信息error:{}", signId, errorMessage);
				}
			} catch (RestClientException e) {
				e.printStackTrace();
				log.error("根据报名签到id:{}统计报名签到在活动管理首页需要的信息error:{}", signId, e.getMessage());
			}
		}
		return SignActivityManageIndexDTO.builder()
				.signId(signId)
				.signUpExist(Boolean.FALSE)
				.signUpId(null)
				.signInExist(Boolean.FALSE)
				.signUpNum(0)
				.build();
	}

	/**统计报名人数
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 18:00:20
	 * @param signIds
	 * @return java.lang.Integer
	*/
	public Integer statSignedUpNum(List<Integer> signIds) {
		if (CollectionUtils.isNotEmpty(signIds)) {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signIds), httpHeaders);
			String result = restTemplate.postForObject(STAT_SIGNED_UP_NUM, httpEntity, String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			Boolean success = jsonObject.getBoolean("success");
			success = Optional.ofNullable(success).orElse(Boolean.FALSE);
			if (success) {
				return jsonObject.getInteger("data");
			}
		}
		return 0;
	}

	/**活动块详情统计信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-14 20:19:14
	 * @param signId
	 * @param uid
	 * @return com.chaoxing.activity.dto.sign.ActivityBlockDetailSignStatDTO
	*/
	public ActivityBlockDetailSignStatDTO statActivityBlockDetail(Integer signId, Integer uid) {
		String url = String.format(ACTIVITY_BLOCK_DETAIL_STAT_URL, signId, uid == null ? "" : uid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			ActivityBlockDetailSignStatDTO activityBlockDetailSignStat = JSON.parseObject(jsonObject.getString("data"), ActivityBlockDetailSignStatDTO.class);
			SignUp signUp = activityBlockDetailSignStat.getSignUp();
			if (signUp != null) {
				signUp.setStartTimestamp(DateUtils.date2Timestamp(signUp.getStartTime()));
				signUp.setEndTimestamp(DateUtils.date2Timestamp(signUp.getEndTime()));
			}
			return activityBlockDetailSignStat;
		} else {
			String errorMessage = jsonObject.getString("message");
			throw new BusinessException(errorMessage);
		}
	}

	/**分页查询用户报名的报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-27 20:31:46
	 * @param page
	 * @param uid
	 * @param sw
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page
	*/
	public Page pageUserSignedUp(Page page, Integer uid, String sw) {
		String url = String.format(USER_SIGNED_UP_URL, uid);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		MultiValueMap<String, Object> multiValuedMap = new LinkedMultiValueMap();
		multiValuedMap.add("pageNum", page.getCurrent());
		multiValuedMap.add("pageSize", page.getSize());
		multiValuedMap.add("sw", sw);
		HttpEntity<MultiValueMap> httpEntity = new HttpEntity<>(httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return JSON.parseObject(jsonObject.getString("data"), Page.class);
		} else {
			String errorMessage = jsonObject.getString("message");
			log.error("查询用户报名的报名签到error:{}", errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**取消报名
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 14:35:58
	 * @param request
	 * @param signUpId
	 * @return void
	*/
	public void cancelSignUp(HttpServletRequest request, Integer signUpId) {
		String url = String.format(CANCEL_SIGN_UP_URL, signUpId);
		HttpHeaders headers = new HttpHeaders();
		List<String> cookies = CookieUtils.getCookies(request);
		headers.put("Cookie", cookies);
		HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("message");
			throw new BusinessException(errorMessage);
		}
	}

	/**撤销报名
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 14:36:10
	 * @param request
	 * @param signUpId
	 * @return void
	*/
	public void revocationSignUp(HttpServletRequest request, Integer signUpId) {
		String url = String.format(REVOCATION_SIGN_UP_URL, signUpId);
		HttpHeaders headers = new HttpHeaders();
		List<String> cookies = CookieUtils.getCookies(request);
		headers.put("Cookie", cookies);
		HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("message");
			throw new BusinessException(errorMessage);
		}
	}

	/**根据报名签到id查询已报名的用户id列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-02 17:57:11
	 * @param signId
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listSignedUpUid(Integer signId) {
		String url = String.format(SIGNED_UP_UIDS_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return JSON.parseArray(jsonObject.getString("data"), Integer.class);
		} else {
			String errorMessage = jsonObject.getString("message");
			log.error("根据报名签到id:{} 查询已报名的用户id列表error:{}", signId, errorMessage);
			throw new BusinessException(errorMessage);
		}

	}

	/**通知已收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-05 16:20:39
	 * @param signId
	 * @param uids
	 * @return void
	*/
	public void noticeCollected(Integer signId, List<Integer> uids) {
		if (signId == null) {
			return;
		}
		String url = String.format(NOTICE_COLLECTED_URL, signId);
		JSONObject params = new JSONObject();
		params.put("uids", uids);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(params.toJSONString(), httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("message");
			log.error("通知已收藏, 报名签到id:{}, error:{}", signId, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**是否开启了报名
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-08 18:48:53
	 * @param signId
	 * @return boolean
	*/
	public boolean isOpenSignUp(Integer signId) {
		SignAddEditDTO signAddEdit = getById(signId);
		if (signAddEdit != null && signAddEdit.getSignUp() != null) {
			return true;
		}
		return false;
	}

	/**根据报名签到id获取报名信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-08 19:18:23
	 * @param signId
	 * @return com.chaoxing.activity.dto.manager.sign.SignUp
	*/
	public SignUp getBySignId(Integer signId) {
		SignUp signUp = null;
		SignAddEditDTO signAddEdit = getById(signId);
		if (signAddEdit != null) {
			signUp = signAddEdit.getSignUp();
		}
		return signUp;
	}

	/**用户是否报名成功
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-08 18:52:07
	 * @param signUpId
	 * @param uid
	 * @return boolean
	*/
	public boolean isSignedUpSuccess(Integer signUpId, Integer uid) {
		String url = String.format(USER_SIGNED_UP_SUCCESS_URL, signUpId, uid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return jsonObject.getBoolean("data");
		}
		String message = jsonObject.getString("message");
		log.error("获取用户:{} 报名id:{} 获取用户是否报名成功error:{}", uid, signUpId, message);
		throw new BusinessException(message);
	}

}