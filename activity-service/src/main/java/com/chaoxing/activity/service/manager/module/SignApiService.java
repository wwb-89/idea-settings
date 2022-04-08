package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.UserFormCollectionGroupDTO;
import com.chaoxing.activity.dto.manager.sign.*;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateResultDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.stat.SignActivityStatDTO;
import com.chaoxing.activity.dto.stat.SignStatSummaryDTO;
import com.chaoxing.activity.dto.stat.UserNotSignedInNumStatDTO;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

	/** 查询报名签到 */
	private static final String SIGN_URL = DomainConstant.SIGN_API + "/sign/%d";
	/** 创建签到报名的地址 */
	private static final String CREATE_URL = DomainConstant.SIGN_API + "/sign/new";
	/** 修改签到报名的地址 */
	private static final String UPDATE_URL = DomainConstant.SIGN_API + "/sign/update";
	/** 获取签到报名信息的地址 */
	private static final String DETAIL_URL = DomainConstant.SIGN_API + "/sign/%d/detail";
	/** 克隆签到报名信息的地址 */
	private static final String CLONE_SIGN_URL = DomainConstant.SIGN_API + "/sign/%d/clone?fid=%d&uid=%d";
	/** 获取签到报名信息的地址 */
	private static final String LIST_DETAIL_URL = DomainConstant.SIGN_API + "/sign/list/detail";
	/** 参与情况 */
	private static final String PARTICIPATION_URL = DomainConstant.SIGN_API + "/sign/%d/signed-up";
	/** 参与情况 */
	private static final String BATCH_PARTICIPATION_URL = DomainConstant.SIGN_API + "/sign/signed-up/stat";
	/** 统计报名签到在活动管理首页需要的信息 */
	private static final String STAT_SIGN_ACTIVITY_MANAGE_INDEX_URL = DomainConstant.SIGN_API + "/stat/sign/%d/activity-index";
	/** 统计报名签到报名成功数量url */
	private static final String STAT_SIGNED_UP_NUM = DomainConstant.SIGN_API + "/stat/signs/signed-up-num";
	/** 统计报名签到签到成功数量url */
	private static final String STAT_SIGNED_IN_NUM = DomainConstant.SIGN_API + "/stat/signs/signed-in-num";
	/** 用户已报名的报名签到列表url */
	private static final String USER_SIGNED_UP_URL = DomainConstant.SIGN_API + "/stat/sign/user-signed-up/%d";
	/** 报名签到参与范围描述yrl */
	private static final String SIGN_PARTICIPATE_SCOPE_DESCRIBE_URL = DomainConstant.SIGN_API + "/sign/%d/scope/describe";
	/** 报名签到下用户报名信息 */
	private static final String SIGN_USER_SIGN_UP_URL = DomainConstant.SIGN_API + "/sign/%d/user/sign-up";
	/** 分组统计查询用户填报的表单记录 */
	private static final String GROUP_USER_FORM_COLLECTION_URL = DomainConstant.SIGN_API + "/form-collect/group-by/uids?signId=%d";

	/** 门户报名 */
	private static final String MH_SIGN_UP_URL = DomainConstant.SIGN_API + "/sign-up/%d/mh?uid=%d&wfwfid=%d";
	/** 取消报名 */
	private static final String CANCEL_SIGN_UP_URL = DomainConstant.SIGN_API + "/sign-up/%d/cancel";
	/** 撤销报名 */
	private static final String REVOCATION_SIGN_UP_URL = DomainConstant.SIGN_API + "/sign-up/%d/revocation";
	/** 表单切换，清空已报名用户记录 */
	private static final String CLEAR_SIGNED_UP_USER_RECORDS_URL = DomainConstant.SIGN_API + "/sign-up/clear/user-sign-up";

	/** 统计活动报名签到对应的活动统计汇总记录url */
	private static final String STAT_ACTIVITY_SUMMARY_URL = DomainConstant.SIGN_API + "/stat/sign/%d/activity-stat-summary";
	/** 统计活动报名签到用户的未签数量 */
	private static final String STAT_ACTIVITY_USER_NOT_SIGNED_IN_NUM = DomainConstant.SIGN_API + "/stat/sign/%d/user-not-signed-in-num";
	/** 获取报名签到下的签到数量 */
	private static final String COUNT_SIGN_SIGN_IN_NUM_URL = DomainConstant.SIGN_API + "/sign/%d/sign-in/num";
	/** 获取报名签到下的表单采集数量 */
	private static final String COUNT_SIGN_FORM_COLLECTION_NUM_URL = DomainConstant.SIGN_API + "/sign/%d/form-collection/num";

	/** 根据外资源部externalIds查询报名签到signIds集合url  */
	private static final String LIST_SIGN_ID_BY_PARTICIPATE_SCOPES_URL = DomainConstant.SIGN_API + "/sign/list/signIds/by-participate-scope";
	/** 根据signIds获取报名参与范围url */
	private static final String LIST_PARTICIPATE_SCOPE_BY_SIGNS_URL = DomainConstant.SIGN_API + "/sign/list/sign-participate-scope";

	/** 报名签到报名成功的uid列表url */
	private static final String SIGN_SIGNED_UP_UIDS_URL = DomainConstant.SIGN_API + "/sign/%s/uid/signed-up";
	/** 用户是否已报名（报名成功）url */
	private static final String USER_IS_SIGNED_UP_URL = DomainConstant.SIGN_API + "/sign/%d/is-signed-up?uid=%d";
	/** 用户报名签到统计汇总 */
	private static final String USER_SIGN_STAT_SUMMARY_URL = DomainConstant.SIGN_API + "/stat/user/%d/sign/%d/sign-stat-summary";
	/** 用户报名成功的报名签到id列表 */
	private static final String USER_SIGNED_UP_SIGN_ID_URL = DomainConstant.SIGN_API + "/stat/user/%d/signId/signed-up";

	/** 用户直接报名接口地址 */
	private static final String USERS_TO_SIGN_UP_URL = DomainConstant.SIGN_API + "/sign/%d/user/to-sign-up";

	/** 报名名单url */
	private static final String SIGN_UP_USER_LIST_URL = DomainConstant.SIGN_WEB + "/sign-up/%d/user-list";
	/** 用户报名签到参与情况url */
	private static final String USER_SIGN_PARTICIPATION_URL = DomainConstant.SIGN_API + "/stat/sign/%d/user-participation?uid=%s";
	/** 单活动统计 */
	public static final String STAT_SINGLE_ACTIVITY_URL = DomainConstant.SIGN_API + "/stat/sign/%d/single-activity?startTime=%s&endTime=%s";

	/** 根据uid、signIds查询用户能报名的 */
	private static final String USER_SIGN_UP_ABLE_SIGN_URL = DomainConstant.SIGN_API + "/sign/sign-up-able";
	/** 根据signIds查询报名的状态信息 */
	private static final String SIGN_UP_STATUS_INFO_URL = DomainConstant.SIGN_API + "/sign/sign-up/status-info";
	/** 报名限制角色id */
	private static final String SIGN_UP_ROLE_ID_LIMIT_URL = DomainConstant.SIGN_API + "/sign/%d/sign-up/limit-role-id";

	/** 根据万能表单id查询报名签到id */
	private static final String GET_SIGN_ID_BY_WFW_FORM_ID_URL = DomainConstant.SIGN_API + "/sign/id/from-wfw-form-id";
	/** 根据报名签到id查询企业报名使用的万能表单id */
	private static final String GET_COMPANY_SIGN_UP_WFW_FORM_ID_URL = DomainConstant.SIGN_API + "/sign-up/company-sign-up/wfw-form-id?signId=%d";

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
	 * @param signCreateParam
	 * @return com.chaoxing.activity.dto.sign.create.SignCreateResultDTO
	*/
	public SignCreateResultDTO create(SignCreateParamDTO signCreateParam) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signCreateParam), httpHeaders);
		String result = restTemplate.postForObject(CREATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), SignCreateResultDTO.class), (message) -> {
			log.error("创建报名报名:{}失败:{}", JSON.toJSONString(signCreateParam), message);
			throw new BusinessException(message);
		});
	}

	/**更新报名签到
	 * @Description
	 * @author wwb
	 * @Date 2020-11-11 17:58:39
	 * @param signCreateParam
	 * @return com.chaoxing.activity.dto.sign.create.SignCreateResultDTO
	*/
	public SignCreateResultDTO update(SignCreateParamDTO signCreateParam) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signCreateParam), httpHeaders);
		String result = restTemplate.postForObject(UPDATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), SignCreateResultDTO.class), (message) -> {
			log.error("修改签到报名:{}失败:{}", JSON.toJSONString(signCreateParam), message);
			throw new BusinessException(message);
		});
	}

	/**获取签到报名信息
	 * @Description
	 * @author wwb
	 * @Date 2020-11-19 13:15:47
	 * @param signId
	 * @return com.chaoxing.activity.dto.sign.create.SignCreateParamDTO
	*/
	public SignCreateParamDTO getCreateById(Integer signId) {
		String url = String.format(DETAIL_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> {
			String data = jsonObject.getString("data");
			SignCreateParamDTO signCreateParam = JSON.parseObject(data, SignCreateParamDTO.class);
			return signCreateParam;
		}, (message) -> {
			log.error("根据签到报名id:{}查询签到报名失败:{}", signId, message);
			throw new BusinessException(message);
		});
	}

	/**根据报名签到signIds，获取签到报名信息
	* @Description
	* @author huxiaolong
	* @Date 2021-08-03 11:35:54
	* @param signIds
	* @return com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO
	*/
	public List<SignCreateParamDTO> listByIds(List<Integer> signIds) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signIds), httpHeaders);
		String result = restTemplate.postForObject(LIST_DETAIL_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSONArray.parseArray(jsonObject.getString("data"), SignCreateParamDTO.class), (message) -> {
			log.error("根据签到报名id:{}查询签到报名失败:{}", signIds, message);
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

	/**统计签到人数
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-12-08 14:40:36
	 * @param signIds
	 * @return
	 */
	public Integer statSignedInNum(List<Integer> signIds) {
		Integer signedInNum = 0;
		if (CollectionUtils.isNotEmpty(signIds)) {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signIds), httpHeaders);
			String result = restTemplate.postForObject(STAT_SIGNED_IN_NUM, httpEntity, String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			signedInNum = resultHandle(jsonObject, () -> jsonObject.getInteger("data"), (message) -> {});
			return Optional.ofNullable(signedInNum).orElse(0);
		}
		return signedInNum;
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
	public Page pageUserSignedUp(Page page, Integer uid, String sw, Integer specificFid) {
		String url = String.format(USER_SIGNED_UP_URL, uid);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, Object> multiValuedMap = new LinkedMultiValueMap();
		multiValuedMap.add("pageNum", page.getCurrent());
		multiValuedMap.add("pageSize", page.getSize());
		multiValuedMap.add("sw", sw);
		multiValuedMap.add("specificFid", specificFid);
		HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity(multiValuedMap, httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), Page.class), (message) -> {
			log.error("查询用户报名的报名签到error:{}", message);
			throw new BusinessException(message);
		});
	}

	/**门户报名
	* @Description
	* @author huxiaolong
	* @Date 2021-09-14 15:24:22
	* @param signUpId
	* @param uid
	* @param wfwfid
	* @return void
	*/
	public RestRespDTO mhSignUp(Integer signUpId, Integer uid, Integer wfwfid) {
		String url = String.format(MH_SIGN_UP_URL, signUpId, uid, wfwfid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.toJavaObject(RestRespDTO.class), (message) -> {
			log.error("根据报名id:{} 进行用户id:{}报名error:{}", signUpId, uid, message);
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

	/**是否开启了报名
	 * @Description
	 * @author wwb
	 * @Date 2021-03-08 19:18:23
	 * @param signId
	 * @return boolean
	*/
	public boolean isOpenSignUp(Integer signId) {
		boolean isOpenSignUp = false;
		SignCreateParamDTO signCreateParam = getCreateById(signId);
		if (signCreateParam != null) {
			List<SignUpCreateParamDTO> signUps = signCreateParam.getSignUps();
			if (CollectionUtils.isNotEmpty(signUps)) {
				for (SignUpCreateParamDTO signUp : signUps) {
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

	public List<SignStatDTO> statSignSignUps(List<Integer> signIds) {
		if (CollectionUtils.isEmpty(signIds)) {
			return Lists.newArrayList();
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		JSONObject params = new JSONObject();
		params.put("signIds", signIds);
		HttpEntity<String> httpEntity = new HttpEntity(params.toJSONString(), httpHeaders);
		String result = restTemplate.postForObject(BATCH_PARTICIPATION_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> {
			String data = jsonObject.getString("data");
			return JSON.parseArray(data, SignStatDTO.class);
		}, (message) -> {
			throw new BusinessException(message);
		});
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
		return resultHandle(jsonObject, () -> {
			String data = jsonObject.getString("data");
			if (StringUtils.isBlank(data)) {
				return SignActivityStatDTO.buildDefault();
			}
			return JSON.parseObject(jsonObject.getString("data"), SignActivityStatDTO.class);
		}, (message) -> {
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

	/**获取报名签到统计汇总记录
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-25 15:42:02
	 * @param signId
	 * @return com.chaoxing.activity.dto.stat.SignStatSummaryDTO
	 */
	public SignStatSummaryDTO getActivityStatSummary(Integer signId) {
		String url = String.format(STAT_ACTIVITY_SUMMARY_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.getJSONObject("data").toJavaObject(SignStatSummaryDTO.class), (message) -> {
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

	/**统计用户未签到次数
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-27 20:04:14
	 * @param signId
	 * @return java.util.List<com.chaoxing.activity.dto.stat.UserNotSignedInNumStatDTO>
	*/
	public List<UserNotSignedInNumStatDTO> statUserNotSignedInNum(Integer signId) {
		String url = String.format(STAT_ACTIVITY_USER_NOT_SIGNED_IN_NUM, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseArray(jsonObject.getString("data"), UserNotSignedInNumStatDTO.class), (message) -> new BusinessException(message));
	}

	/**查询报名签到下各报名的用户报名情况
	 * @Description
	 * @author wwb
	 * @Date 2021-07-27 20:04:14
	 * @param signId
	 * @return java.util.List<com.chaoxing.activity.dto.stat.UserNotSignedInNumStatDTO>
	*/
	public String listUserSignUpBySignIdUids(Integer signId, List<Integer> uids) {
		String url = String.format(SIGN_USER_SIGN_UP_URL, signId);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(uids), httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);

		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.getString("data"), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**获取报名签到下的签到数量
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-18 11:12:06
	 * @param signId
	 * @return java.lang.Integer
	*/
	public Integer countSignInNum(Integer signId) {
		String url = String.format(COUNT_SIGN_SIGN_IN_NUM_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.getInteger("data"), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**统计报名签到下的表单采集数量
	 * @Description
	 * @author huxiaolong
	 * @Date 2022-01-12 16:21:18
	 * @param signId
	 * @return
	 */
	public Integer countFormCollectionNum(Integer signId) {
		String url = String.format(COUNT_SIGN_FORM_COLLECTION_NUM_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.getInteger("data"), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**查询用户报名成功的报名签到id列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-18 18:01:16
	 * @param uid
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listUserSignedUpSignIds(Integer uid) {
		String url = String.format(USER_SIGNED_UP_SIGN_ID_URL, uid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseArray(jsonObject.getString("data"), Integer.class), (message) -> {
			throw new BusinessException(message);
		});
	}

	public SignDTO getById(Integer signId) {
		String url = String.format(SIGN_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), SignDTO.class), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**用户直接报名
	* @Description
	* @author huxiaolong
	* @Date 2021-08-25 18:04:34
	* @param signId
	* @param uids
	* @return void
	*/
	public void createUserSignUp(Integer signId, List<Integer> uids) {
		if (CollectionUtils.isEmpty(uids)) {
			return;
		}
		String url = String.format(USERS_TO_SIGN_UP_URL, signId);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(uids), httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);

		JSONObject jsonObject = JSON.parseObject(result);
		resultHandle(jsonObject, () -> null, (message) -> {
			throw new BusinessException(message);
		});
	}

	/**查询能参与报名的报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-18 15:01:53
	 * @param uid
	 * @param signIds
	 * @return java.util.List<com.chaoxing.activity.dto.manager.sign.SignUpAbleSignDTO>
	*/
	public List<SignUpAbleSignDTO> listSignUpAbleSign(Integer uid, List<Integer> signIds) {
		if (CollectionUtils.isEmpty(signIds)) {
			return Lists.newArrayList();
		}
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("uid", uid);
		params.add("signIds", String.join(CommonConstant.DEFAULT_SEPARATOR, signIds.stream().map(String::valueOf).collect(Collectors.toList())));
		String result = restTemplate.postForObject(USER_SIGN_UP_ABLE_SIGN_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSONArray.parseArray(jsonObject.getString("data"), SignUpAbleSignDTO.class), (message) -> {
			throw new BusinessException(message);
		});
	}

	public List<SignUpAbleSignDTO> listSignUpStatusInfo(Integer uid, List<Integer> signIds) {
		if (CollectionUtils.isEmpty(signIds)) {
			return Lists.newArrayList();
		}
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("signIds", String.join(CommonConstant.DEFAULT_SEPARATOR, signIds.stream().map(String::valueOf).collect(Collectors.toList())));
		String result = restTemplate.postForObject(SIGN_UP_STATUS_INFO_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		List<SignUpAbleSignDTO> signUps = resultHandle(jsonObject, () -> JSONArray.parseArray(jsonObject.getString("data"), SignUpAbleSignDTO.class), (message) -> {
			throw new BusinessException(message);
		});
		if (CollectionUtils.isNotEmpty(signUps)) {
			signUps.forEach(v -> v.setUid(uid));
		}
		return signUps;
	}

	/**分组查询用户uids列表的已填写表单统计状况
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-18 16:22:12
	 * @param signId
	 * @param uids
	 * @return java.util.List<com.chaoxing.activity.dto.UserFormCollectionGroupDTO>
	 */
    public List<UserFormCollectionGroupDTO> groupUserFormCollections(Integer signId, List<Integer> uids) {
		String url = String.format(GROUP_USER_FORM_COLLECTION_URL, signId);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(uids), httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);

		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseArray(jsonObject.getString("data"), UserFormCollectionGroupDTO.class), (message) -> {
			throw new BusinessException(message);
		});
    }

	/**根据万能表单id查询报名签到id
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-28 12:43:44
	 * @param wfwFormId
	 * @return java.lang.Integer
	*/
	public Integer getSignIdByWfwFormId(Integer wfwFormId) {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("wfwFormId", wfwFormId);
		String result = restTemplate.postForObject(GET_SIGN_ID_BY_WFW_FORM_ID_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.getInteger("data"), (message) -> {
			throw new BusinessException(message);
		});
	}

    public SignCreateParamDTO getCloneSign(Integer signId, Integer fid, Integer uid) {
		String url = String.format(CLONE_SIGN_URL, signId, fid, uid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> {
			String data = jsonObject.getString("data");
			return JSON.parseObject(data, SignCreateParamDTO.class);
		}, (message) -> {
			log.error("根据签到报名id:{}查询签到报名失败:{}", signId, message);
			throw new BusinessException(message);
		});
    }

	/**获取活动企业报名的万能表单id
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-28 16:40:07
	 * @param signId
	 * @return java.lang.Integer
	*/
	public Integer getActivityCompanySignUpWfwFormId(Integer signId) {
		if (signId == null) {
			return null;
		}
		String url = String.format(GET_COMPANY_SIGN_UP_WFW_FORM_ID_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> jsonObject.getInteger("data"), (message) -> {
			log.error("根据签到报名id:{}, 获取活动企业报名的万能表单id error:{}", signId, message);
			throw new BusinessException(message);
		});
	}

	/**查询报名的角色id限制列表
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-16 15:04:38
	 * @param signId
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listSignUpRoleIdLimitBySignId(Integer signId) {
		String url = String.format(SIGN_UP_ROLE_ID_LIMIT_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSONArray.parseArray(jsonObject.getString("data"), Integer.class), (message) -> {
			log.error("根据签到报名id:{}, 查询报名的角色id限制列表error:{}", signId, message);
			throw new BusinessException(message);
		});
	}

	/**清空报名人员记录
	 * @Description 
	 * @author huxiaolong
	 * @Date 2022-03-07 10:30:47
	 * @param signUpIds
	 * @return 
	 */
    public void clearSignedUpRecords(List<Integer> signUpIds) {
		if (CollectionUtils.isEmpty(signUpIds)) {
			return;
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signUpIds), httpHeaders);
		String result = restTemplate.postForObject(CLEAR_SIGNED_UP_USER_RECORDS_URL, httpEntity, String.class);

		JSONObject jsonObject = JSON.parseObject(result);
		resultHandle(jsonObject, () -> null, (message) -> {
			throw new BusinessException(message);
		});
    }
}