package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.OrgFormConfigDTO;
import com.chaoxing.activity.dto.manager.sign.*;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.dto.module.SignAddEditResultDTO;
import com.chaoxing.activity.dto.sign.ActivityBlockDetailSignStatDTO;
import com.chaoxing.activity.dto.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.dto.sign.SignParticipateScopeDTO;
import com.chaoxing.activity.dto.sign.UserSignStatSummaryDTO;
import com.chaoxing.activity.dto.stat.SignActivityStatDTO;
import com.chaoxing.activity.model.ActivityStatSummary;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.function.Consumer;
import java.util.function.Supplier;

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

	/** 报名签到api域名 */
	private static final String SIGN_API_DOMAIN = "http://api.qd.reading.chaoxing.com";
	/** 报名签到页面域名 */
	private static final String SIGN_WEB_DOMAIN = "https://reading.chaoxing.com/qd";
	/** 创建签到报名的地址 */
	private static final String CREATE_URL = SIGN_API_DOMAIN + "/sign/new";
	/** 修改签到报名的地址 */
	private static final String UPDATE_URL = SIGN_API_DOMAIN + "/sign/update";
	/** 获取签到报名信息的地址 */
	private static final String DETAIL_URL = SIGN_API_DOMAIN + "/sign/%d/detail";
	/** 参与情况 */
	private static final String PARTICIPATION_URL = SIGN_API_DOMAIN + "/sign/%d/signed-up";
	/** 统计报名签到在活动管理首页需要的信息 */
	private static final String STAT_SIGN_ACTIVITY_MANAGE_INDEX_URL = SIGN_API_DOMAIN + "/stat/sign/%d/activity-index";
	/** 统计报名签到报名成功数量url */
	private static final String STAT_SIGNED_UP_NUM = SIGN_API_DOMAIN + "/stat/sign/signed-up-num";
	/** 活动块详情统计信息url */
	private static final String ACTIVITY_BLOCK_DETAIL_STAT_URL = SIGN_API_DOMAIN + "/stat/sign/activity-block-detail?signId=%s&uid=%s";
	/** 用户已报名的报名签到列表url */
	private static final String USER_SIGNED_UP_URL = SIGN_API_DOMAIN + "/stat/sign/user-signed-up/%d";
	/** 通知已收藏url */
	private static final String NOTICE_COLLECTED_URL = SIGN_API_DOMAIN + "/sign/%d/notice/collected";
	/** 报名签到参与范围描述yrl */
	private static final String SIGN_PARTICIPATE_SCOPE_DESCRIBE_URL = SIGN_API_DOMAIN + "/sign/%d/scope/describe";

	/** 取消报名 */
	private static final String CANCEL_SIGN_UP_URL = SIGN_API_DOMAIN + "/sign-up/%d/cancel";
	/** 撤销报名 */
	private static final String REVOCATION_SIGN_UP_URL = SIGN_API_DOMAIN + "/sign-up/%d/revocation";

	/** 统计活动报名签到对应的活动统计汇总记录url */
	private static final String STAT_ACTIVITY_SUMMARY_URL = SIGN_API_DOMAIN + "/stat/sign/%d/activity-stat-summary";

	/** 根据外资源部externalIds查询报名签到signIds集合url  */
	private static final String LIST_SIGN_ID_BY_PARTICIPATE_SCOPES_URL = SIGN_API_DOMAIN + "/sign/list/signIds/by-participate-scope";
	/** 根据signIds获取报名参与范围url */
	private static final String LIST_PARTICIPATE_SCOPE_BY_SIGNS_URL = SIGN_API_DOMAIN + "/sign/list/sign-participate-scope";

	/** 报名签到报名成功的uid列表url */
	private static final String SIGN_SIGNED_UP_UIDS_URL = SIGN_API_DOMAIN + "/sign/%s/uid/signed-up";
	/** 用户是否已报名（报名成功）url */
	private static final String USER_IS_SIGNED_UP_URL = SIGN_API_DOMAIN + "/sign/%d/is-signed-up?uid=%d";
	/** 用户报名签到统计汇总 */
	private static final String USER_SIGN_STAT_SUMMARY_URL = SIGN_API_DOMAIN + "/stat/user/%d/sign/%d/sign-stat-summary";

	/** 报名名单url */
	private static final String SIGN_UP_USER_LIST_URL = SIGN_WEB_DOMAIN + "/sign-up/%d/user-list";
	/** 用户报名签到参与情况url */
	private static final String USER_SIGN_PARTICIPATION_URL = SIGN_API_DOMAIN + "/stat/sign/%d/user-participation?uid=%s";
	/** 根据signId列表查询报名的人数 */
	private static final String STAT_SIGN_SIGNED_UP_INFO_URL = SIGN_API_DOMAIN + "/stat/signs";
	/** 单活动统计 */
	public static final String STAT_SINGLE_ACTIVITY_URL = SIGN_API_DOMAIN + "/stat/sign/%d/single-activity?startTime=%s&endTime=%s";

	/** 通知活动已评价 */
	private static final String NOTICE_HAVE_RATING_URL = SIGN_API_DOMAIN + "/sign/%d/notice/rating?uid=%d";
	/** 通知报名签到活动积分已变更 */
	private static final String NOTICE_ACTIVITY_INTEGRAL_CHANGE_URL = SIGN_API_DOMAIN + "/sign/%d/notice/activity-integral-change";

	/** 签到位置搜索缓存 */
	private static final String SIGN_IN_POSITION_HISTORY_LIST_URL = SIGN_API_DOMAIN + "/sign-in/position-history";
	private static final String SIGN_IN_POSITION_HISTORY_ADD_URL = SIGN_API_DOMAIN + "/sign-in/position-history/add";
	private static final String SIGN_IN_POSITION_HISTORY_DELETE_URL = SIGN_API_DOMAIN + "/sign-in/position-history/delete";

	/** 获取机构配置的表单 */
	private static final String GET_ORG_FORM_CONFIG_URL = SIGN_API_DOMAIN + "/org/%d/form";
	/** 配置机构的表单 */
	private static final String ORG_FORM_CONFIG_URL = SIGN_API_DOMAIN + "/org/form/config";

	/** 查询用户在报名签到下的报名列表 */
	private static final String USER_SIGNED_UP_LIST_URL = SIGN_API_DOMAIN + "/stat/user/%d/sign/%d/signed-up";
	/** 查询用户在报名签到下的签到记录（排除未签到） */
	private static final String USER_EXCLUDE_NOT_SIGNED_IN_URL = SIGN_API_DOMAIN + "/stat/user/%d/sign/%d/exclude-not-signed-in";
	
	/** 通知报名签到用户成绩合格变更 */
	private static final String NOTICE_SIGN_USER_RESULT_QUALIFIED_CHANGE_URL = SIGN_API_DOMAIN + "/sign/%d/user/%d/result/qualified/changed";

	@Resource
	private RestTemplate restTemplate;

	/**结果处理
	 * @Description
	 * @author wwb
	 * @Date 2021-05-26 11:18:14
	 * @param jsonObject
	 * @param successCallback
	 * @param errorCallback
	 * @return T
	*/
	private <T> T resultHandle(JSONObject jsonObject, Supplier<T> successCallback, Consumer<String> errorCallback) {
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return successCallback.get();
		} else {
			errorCallback.accept(jsonObject.getString("message"));
			return null;
		}
	}
	/**创建报名签到
	 * @Description
	 * @author wwb
	 * @Date 2020-11-11 14:26:33
	 * @param signAddEdit
	 * @return com.chaoxing.activity.dto.module.SignAddEditResultDTO
	*/
	public SignAddEditResultDTO create(SignAddEditDTO signAddEdit) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signAddEdit), httpHeaders);
		String result = restTemplate.postForObject(CREATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), SignAddEditResultDTO.class), (message) -> {
			log.error("创建报名报名:{}失败:{}", JSON.toJSONString(signAddEdit), message);
			throw new BusinessException(message);
		});
	}

	/**更新报名签到
	 * @Description
	 * @author wwb
	 * @Date 2020-11-11 17:58:39
	 * @param signAddEdit
	 * @return com.chaoxing.activity.dto.module.SignAddEditResultDTO
	*/
	public SignAddEditResultDTO update(SignAddEditDTO signAddEdit) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signAddEdit), httpHeaders);
		String result = restTemplate.postForObject(UPDATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), SignAddEditResultDTO.class), (message) -> {
			log.error("修改签到报名:{}失败:{}", JSON.toJSONString(signAddEdit), message);
			throw new BusinessException(message);
		});
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
		return resultHandle(jsonObject, () -> {
			String data = jsonObject.getString("data");
			SignAddEditDTO signAddEdit = JSON.parseObject(data, SignAddEditDTO.class);
			List<SignUp> signUps = signAddEdit.getSignUps();
			if (CollectionUtils.isNotEmpty(signUps)) {
				for (SignUp signUp : signUps) {
					signUp.setStartTimestamp(signUp.getStartTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
					signUp.setEndTimestamp(signUp.getEndTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
				}
			}
			List<SignIn> signIns = signAddEdit.getSignIns();
			if (CollectionUtils.isNotEmpty(signIns)) {
				for (SignIn signIn : signIns) {
					signIn.setStartTimestamp(signIn.getStartTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
					signIn.setEndTimestamp(signIn.getEndTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
				}
			}
			return signAddEdit;
		}, (message) -> {
			log.error("根据签到报名id:{}查询签到报名失败:{}", signId, message);
			throw new BusinessException(message);
		});
	}

	/**查询报名签到的参与情况
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 20:15:35
	 * @param signId
	 * @return com.chaoxing.activity.dto.manager.SignStatDTO
	*/
	public SignStatDTO getSignParticipation(Integer signId) {
		String url = String.format(PARTICIPATION_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), SignStatDTO.class), (message) -> log.error("根据signId:{}查询报名签到的参与情况error:{}", signId, message));
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
				return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), SignActivityManageIndexDTO.class), (message) -> log.error("根据报名签到id:{}统计报名签到在活动管理首页需要的信息error:{}", signId, message));
			} catch (RestClientException e) {
				e.printStackTrace();
				log.error("根据报名签到id:{}统计报名签到在活动管理首页需要的信息error:{}", signId, e.getMessage());
			}
		}
		return SignActivityManageIndexDTO.builder()
				.signId(signId)
				.signUpExist(Boolean.FALSE)
				.signUpIds(Lists.newArrayList())
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
		Integer signedUpNum = 0;
		if (CollectionUtils.isNotEmpty(signIds)) {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signIds), httpHeaders);
			String result = restTemplate.postForObject(STAT_SIGNED_UP_NUM, httpEntity, String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			signedUpNum = resultHandle(jsonObject, () -> jsonObject.getInteger("data"), (message) -> {});
			return Optional.ofNullable(signedUpNum).orElse(0);
		}
		return signedUpNum;
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
		return resultHandle(jsonObject, () -> {
			ActivityBlockDetailSignStatDTO activityBlockDetailSignStat = JSON.parseObject(jsonObject.getString("data"), ActivityBlockDetailSignStatDTO.class);
			SignUp signUp = activityBlockDetailSignStat.getSignUp();
			if (signUp != null) {
				signUp.setStartTimestamp(DateUtils.date2Timestamp(signUp.getStartTime()));
				signUp.setEndTimestamp(DateUtils.date2Timestamp(signUp.getEndTime()));
			}
			return activityBlockDetailSignStat;
		}, (message) -> {
			throw new BusinessException(message);
		});
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
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), Page.class), (message) -> {
			log.error("查询用户报名的报名签到error:{}", message);
			throw new BusinessException(message);
		});
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
		resultHandle(jsonObject, () -> null, (message) -> {
			throw new BusinessException(message);
		});
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
		resultHandle(jsonObject, () -> null, (message) -> {
			throw new BusinessException(message);
		});
	}

	/**根据报名签到id查询已报名的用户id列表
	 * @Description
	 * @author wwb
	 * @Date 2021-02-02 17:57:11
	 * @param signId
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listSignedUpUid(Integer signId) {
		String url = String.format(SIGN_SIGNED_UP_UIDS_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseArray(jsonObject.getString("data"), Integer.class), (message) -> {
			log.error("根据报名签到id:{} 查询已报名的用户id列表error:{}", signId, message);
			throw new BusinessException(message);
		});
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
		resultHandle(jsonObject, () -> null, (message) -> {
			log.error("通知已收藏, 报名签到id:{}, error:{}", signId, message);
			throw new BusinessException(message);
		});
	}

	/**是否开启了报名
	 * @Description
	 * @author wwb
	 * @Date 2021-03-08 19:18:23
	 * @param signId
	 * @return boolean
	*/
	public boolean isOpenSignUp(Integer signId) {
		boolean isOpenSignUp = false;
		SignAddEditDTO signAddEdit = getById(signId);
		if (signAddEdit != null) {
			List<SignUp> signUps = signAddEdit.getSignUps();
			if (CollectionUtils.isNotEmpty(signUps)) {
				for (SignUp signUp : signUps) {
					if (!signUp.getDeleted()) {
						isOpenSignUp = true;
						break;
					}
				}
			}
		}
		return isOpenSignUp;
	}

	/**用户是否报名成功
	 * @Description
	 * @author wwb
	 * @Date 2021-03-08 18:52:07
	 * @param signId
	 * @param uid
	 * @return boolean
	*/
	public boolean isSignedUp(Integer signId, Integer uid) {
		String url = String.format(USER_IS_SIGNED_UP_URL, signId, uid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.getBoolean("data"), (message) -> {
			log.error("获取用户:{} 报名签到id:{} 获取用户是否报名成功error:{}", uid, signId, message);
			throw new BusinessException(message);
		});
	}

	/**获取报名名单的url
	 * @Description
	 * @author wwb
	 * @Date 2021-03-09 15:55:55
	 * @param signUpId
	 * @return java.lang.String
	*/
	public String getSignUpListUrl(Integer signUpId) {
		return String.format(SIGN_UP_USER_LIST_URL, signUpId);
	}

	/**获取用户报名签到参与情况
	 * @Description
	 * @author wwb
	 * @Date 2021-03-09 18:06:18
	 * @param signId
	 * @param uid
	 * @return com.chaoxing.activity.dto.manager.sign.UserSignParticipationStatDTO
	*/
	public UserSignParticipationStatDTO userParticipationStat(Integer signId, Integer uid) {
		String uidStr = "";
		if (uid != null) {
			uidStr = String.valueOf(uid);
		}
		String url = String.format(USER_SIGN_PARTICIPATION_URL, signId, uidStr);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> {
			String data = jsonObject.getString("data");
			if (StringUtils.isNotBlank(data)) {
				return JSON.parseObject(data, UserSignParticipationStatDTO.class);
			} else {
				return null;
			}
		}, (message) -> {
			log.error("获取用户:{} 报名签到id:{} 获取用户报名签到参与情况 error:{}", uid, signId, message);
			throw new BusinessException(message);
		});
	}

	/**处理通知评价
	 * @Description
	 * @author wwb
	 * @Date 2021-03-24 14:08:22
	 * @param signId
	 * @param uid
	 * @return void
	*/
	public void handleNoticeRating(Integer signId, Integer uid) {
		String url = String.format(NOTICE_HAVE_RATING_URL, signId, uid);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity httpEntity = new HttpEntity(httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		resultHandle(jsonObject, () -> null, (message) -> {
			throw new BusinessException(message);
		});
	}

	/**根据signId列表查询报名的人数
	 * @Description
	 * @author wwb
	 * @Date 2021-03-24 20:24:58
	 * @param signIds
	 * @return java.util.List<com.chaoxing.activity.dto.manager.sign.SignStatDTO>
	*/
	public List<SignStatDTO> statSignSignedUpNum(List<Integer> signIds) {
		if (CollectionUtils.isEmpty(signIds)) {
			return Lists.newArrayList();
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		JSONObject params = new JSONObject();
		params.put("signIds", signIds);
		HttpEntity<String> httpEntity = new HttpEntity(params.toJSONString(), httpHeaders);
		String result = restTemplate.postForObject(STAT_SIGN_SIGNED_UP_INFO_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> {
			String data = jsonObject.getString("data");
			return JSON.parseArray(data, SignStatDTO.class);
		}, (message) -> {
			throw new BusinessException(message);
		});
	}

	/**通知报名签到活动积分已修改
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 21:50:30
	 * @param signId
	 * @return void
	*/
	public void noticeSecondClassroomIntegralChange(Integer signId) {
		String url = String.format(NOTICE_ACTIVITY_INTEGRAL_CHANGE_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		resultHandle(jsonObject, () -> null, (message) -> {
			throw new BusinessException(message);
		});
	}

	/**查询签到位置历史记录
	 * @Description
	 * @author wwb
	 * @Date 2021-04-07 18:50:54
	 * @param uid
	 * @param fid
	 * @return java.util.List<java.lang.String>
	*/
	public List<String> listSignInPositionHistory(Integer uid, Integer fid) {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("uid", uid);
		params.add("fid", fid);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity(params, httpHeaders);
		String result = restTemplate.postForObject(SIGN_IN_POSITION_HISTORY_LIST_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		List<String> positions = resultHandle(jsonObject, () -> JSON.parseArray(jsonObject.getString("data"), String.class), (message) -> {

		});
		return Optional.ofNullable(positions).orElse(Lists.newArrayList());
	}

	/**新增签到位置历史记录
	 * @Description
	 * @author wwb
	 * @Date 2021-04-07 18:51:05
	 * @param uid
	 * @param fid
	 * @param jsonStr
	 * @return void
	*/
	public void addSignInPositionHistory(Integer uid, Integer fid, String jsonStr) {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("uid", uid);
		params.add("fid", fid);
		params.add("jsonStr", jsonStr);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity(params, httpHeaders);
		restTemplate.postForObject(SIGN_IN_POSITION_HISTORY_ADD_URL, httpEntity, String.class);
	}

	/**删除签到位置历史记录
	 * @Description
	 * @author wwb
	 * @Date 2021-04-07 18:51:15
	 * @param uid
	 * @param fid
	 * @param jsonStr
	 * @return void
	*/
	public void deleteSignInPositionHistory(Integer uid, Integer fid, String jsonStr) {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("uid", uid);
		params.add("fid", fid);
		params.add("jsonStr", jsonStr);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity(params, httpHeaders);
		restTemplate.postForObject(SIGN_IN_POSITION_HISTORY_DELETE_URL, httpEntity, String.class);
	}

	/**报名签到每日统计
	 * @Description
	 * @author wwb
	 * @Date 2021-04-16 14:15:00
	 * @param signId
	 * @param startTime
	 * @param endTime
	 * @return com.chaoxing.activity.dto.stat.SignActivityStatDTO
	*/
	public SignActivityStatDTO singleActivityStat(Integer signId, String startTime, String endTime) {
		if (signId == null) {
			return SignActivityStatDTO.buildDefault();
		}
		String url = String.format(STAT_SINGLE_ACTIVITY_URL, signId, startTime, endTime);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), SignActivityStatDTO.class), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**获取活动报名签到参与范围描述
	 * @Description
	 * @author wwb
	 * @Date 2021-04-20 10:16:17
	 * @param signId
	 * @return java.lang.String
	*/
	public String getActivitySignParticipateScopeDescribe(Integer signId) {
		String url = String.format(SIGN_PARTICIPATE_SCOPE_DESCRIBE_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.getString("data"), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**获取活动统计汇总记录
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-25 15:42:02
	 * @param signId
	 * @return java.lang.Integer
	 */
	public ActivityStatSummary getActivityStatSummary(Integer signId) {
		String url = String.format(STAT_ACTIVITY_SUMMARY_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.getJSONObject("data").toJavaObject(ActivityStatSummary.class), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**用户报名签到统计汇总
	 * @Description
	 * @author wwb
	 * @Date 2021-05-27 14:13:30
	 * @param uid
	 * @return com.chaoxing.activity.dto.sign.UserSignStatSummaryDTO
	*/
	public UserSignStatSummaryDTO userSignStatSummary(Integer uid, Integer signId) {
		String url = String.format(USER_SIGN_STAT_SUMMARY_URL, uid, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), UserSignStatSummaryDTO.class), (message) -> {
			log.error("获取用户:{}报名签到统计汇总error:{}", uid, message);
			throw new BusinessException(message);
		});
	}

	public List<SignParticipateScopeDTO> listSignParticipateScopeBySignIds(List<Integer> signIds) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signIds), httpHeaders);
		String result = restTemplate.postForObject(LIST_PARTICIPATE_SCOPE_BY_SIGNS_URL, httpEntity, String.class);

		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseArray(jsonObject.getString("data"), SignParticipateScopeDTO.class), (message) -> {
			throw new BusinessException(message);
		});
	}

	public List<Integer> listSignIdsByExternalIds(List<Integer> externalIds) {
		if (CollectionUtils.isEmpty(externalIds)) {
			return Lists.newArrayList();
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(externalIds), httpHeaders);
		String result = restTemplate.postForObject(LIST_SIGN_ID_BY_PARTICIPATE_SCOPES_URL, httpEntity, String.class);

		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseArray(jsonObject.getString("data"), Integer.class), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**获取机构表单配置
	 * @Description
	 * @author wwb
	 * @Date 2021-06-08 16:10:50
	 * @param fid
	 * @return com.chaoxing.activity.dto.OrgFormConfigDTO
	 */
	public OrgFormConfigDTO getOrgFormConfig(Integer fid) {
		String url = String.format(GET_ORG_FORM_CONFIG_URL, fid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), OrgFormConfigDTO.class), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**配置机构表单
	 * @Description
	 * @author wwb
	 * @Date 2021-06-08 16:46:12
	 * @param orgFormConfig
	 * @return void
	 */
	public void configOrgForm(OrgFormConfigDTO orgFormConfig) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity(JSON.toJSONString(orgFormConfig), httpHeaders);
		String result = restTemplate.postForObject(ORG_FORM_CONFIG_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data")), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**根据uid和报名签到id查询用户报名列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-17 16:24:10
	 * @param uid
	 * @param signId
	 * @return java.util.List<com.chaoxing.activity.dto.manager.sign.UserSignUp>
	*/
	public List<UserSignUp> listUserSignedUp(Integer uid, Integer signId) {
		String url = String.format(USER_SIGNED_UP_LIST_URL, uid, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseArray(jsonObject.getString("data"), UserSignUp.class), (message) -> {
			log.error("根据url:{} 查询用户报名列表信息error:{}", url, message);
			throw new BusinessException(message);
		});
	}

	/**根据uid和报名签到id查询用户签到列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-17 16:24:52
	 * @param uid
	 * @param signId
	 * @return java.util.List<com.chaoxing.activity.dto.manager.sign.UserSignIn>
	*/
	public List<UserSignIn> listUserExcludeNotSignIn(Integer uid, Integer signId) {
		String url = String.format(USER_EXCLUDE_NOT_SIGNED_IN_URL, uid, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseArray(jsonObject.getString("data"), UserSignIn.class), (message) -> {
			log.error("根据url:{} 查询用户签到列表信息error:{}", url, message);
			throw new BusinessException(message);
		});
	}

	/**通知报名签到用户成绩合格状态变更
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-25 10:03:08
	 * @param uid
	 * @param signId
	 * @return void
	*/
	public void noticeSignUserResultChange(Integer uid, Integer signId) {
		String url = String.format(NOTICE_SIGN_USER_RESULT_QUALIFIED_CHANGE_URL, signId, uid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		resultHandle(jsonObject, () -> null, (message) -> {
			log.error("根据url:{} 通知报名签到用户成绩合格状态变更error:{}", url, message);
			throw new BusinessException(message);
		});
	}
}