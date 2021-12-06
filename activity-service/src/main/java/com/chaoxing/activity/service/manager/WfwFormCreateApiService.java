package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateResultDTO;
import com.chaoxing.activity.model.SignUpFillInfoType;
import com.chaoxing.activity.model.SignUpWfwFormTemplate;
import com.chaoxing.activity.service.activity.engine.SignUpFillInfoTypeService;
import com.chaoxing.activity.service.activity.engine.SignUpWfwFormTemplateService;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**微服务表单创建服务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormCreateApiService
 * @description
 * @blame wwb
 * @date 2021-07-09 10:59:34
 */
@Slf4j
@Service
public class WfwFormCreateApiService {

	private static final String SIGN = "deptManager_hdcp";
	private static final String KEY = "SObtv7P3d$UVuBkTjg";
	/** 日期格式化 */
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");

	private static final String CREATE_URL = DomainConstant.WFW_FORM_API + "/api/manager/third/user/login/apps/create";
	/** 表单后台地址 */
	private static final String FORM_ADMIN_URL = DomainConstant.WFW_FORM_API + "/api/manager/third/user/login/apps/manager?fid=%d&uid=%d&datetime=%s&sign=%s&formId=%d&formType=%d&enc=%s";
	/** 接口创建表单url */
	private static final String CREATE_FORM_URL = DomainConstant.WFW_FORM_API + "/api/apps/forms/app/thirdcreate";

	@Resource
	private RestTemplate restTemplate;
	@Resource
	private SignUpWfwFormTemplateService signUpWfwFormTemplateService;
	@Resource
	private SignUpFillInfoTypeService signUpFillInfoTypeService;

	/**构建表单编辑地址（仅仅是编辑）
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-17 17:49:15
	 * @param fid
	 * @param uid
	 * @param wfwFormTemplateId
	 * @return java.lang.String
	 */
	public String buildEditFormUrl(Integer fid, Integer formId, Integer uid, Integer wfwFormTemplateId) {
		SignUpWfwFormTemplate wfwFormTemplate = signUpWfwFormTemplateService.getById(wfwFormTemplateId);
		if (wfwFormTemplate == null) {
			wfwFormTemplate = SignUpWfwFormTemplate.builder().sign(SIGN).key(KEY).build();
		}
		return buildCreateEditFormUrl(fid, formId, uid, wfwFormTemplate);
	}

	private String buildCreateEditFormUrl(Integer fid, Integer formId, Integer uid, SignUpWfwFormTemplate wfwFormTemplate) {
		if (wfwFormTemplate == null) {
			throw new BusinessException("机构: " + fid + "报名万能表单模板不存在!");
		}
		Map<String, Object> params = Maps.newTreeMap();
		params.put("fid", fid);
		params.put("uid", uid);
		if (formId != null) {
			params.put("formId", formId);
		}
		LocalDateTime now = LocalDateTime.now();
		params.put("datetime", now.format(DATE_TIME_FORMATTER));
		params.put("sign", wfwFormTemplate.getSign());
		params.put("isCopy", 0);
		params.put("formType", 2);
		String enc = getEnc(params, wfwFormTemplate.getKey());
		params.put("enc", enc);
		// 封装url
		StringBuilder url = new StringBuilder(CREATE_URL + "?");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		return url.toString();
	}

	/**根据id为wfwFormTemplateId的万能表单模板创建表单，并带上新表单的编辑页面url
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-19 16:16:40
	 * @param fid
	 * @param uid
	 * @param wfwFormTemplateId
	 * @return java.lang.String
	 */
	public WfwFormCreateResultDTO createWfwForm(Integer fid, Integer uid, Integer wfwFormTemplateId) {
		SignUpWfwFormTemplate wfwFormTemplate = signUpWfwFormTemplateService.getByIdOrDefaultNormal(wfwFormTemplateId);
		return createWfwForm(fid, uid, wfwFormTemplate);
	}

	public WfwFormCreateResultDTO createWfwForm(Integer fid, Integer uid, SignUpWfwFormTemplate wfwFormTemplate) {
		if (wfwFormTemplate == null) {
			throw new BusinessException("报名万能表单模板不存在!");
		}
		// 创建新的表单
		return create(WfwFormCreateParamDTO.builder()
				.formId(wfwFormTemplate.getFormId())
				.originalFid(wfwFormTemplate.getFid())
				.uid(uid)
				.fid(fid)
				.sign(wfwFormTemplate.getSign())
				.key(wfwFormTemplate.getKey())
				.build());
	}


	/**为报名克隆万能表单
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-19 17:05:12
	 * @param originFid 源表单机构fid
	 * @param formId 源表单id
	 * @param fid 创建表单的机构fid
	 * @param uid 创建表单的用户
	 * @param tplComponentId 报名的模板组件id
	 * @return com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateResultDTO
	 */
	public WfwFormCreateResultDTO cloneSignUpWfwForm(Integer originFid, Integer formId, Integer fid, Integer uid, Integer tplComponentId) {
		Integer wfwFormTemplateId = Optional.ofNullable(signUpFillInfoTypeService.getByTemplateComponentId(tplComponentId)).map(SignUpFillInfoType::getWfwFormTemplateId).orElse(null);
		SignUpWfwFormTemplate wfwFormTemplate = signUpWfwFormTemplateService.getById(wfwFormTemplateId);
		if (formId == null) {
			return createWfwForm(fid, uid, wfwFormTemplate);
		}
		return create(WfwFormCreateParamDTO.builder()
				.originalFid(originFid)
				.fid(fid)
				.formId(formId)
				.uid(uid)
				.sign(wfwFormTemplate.getSign())
				.key(wfwFormTemplate.getKey())
				.build());
	}

	/**获取表单管理地址
	 * @Description
	 * @author wwb
	 * @Date 2021-08-18 15:07:48
	 * @param formId
	 * @param fid
	 * @param uid
	 * @return java.lang.String
	 */
	public String getFormAdminUrl(Integer formId, Integer fid, Integer uid) {
		Map<String, Object> encParamMap = Maps.newTreeMap();
		String dateTimeStr = LocalDateTime.now().format(DATE_TIME_FORMATTER);
		Integer formType = 2;
		encParamMap.put("fid", fid);
		encParamMap.put("uid", uid);
		encParamMap.put("datetime", dateTimeStr);
		encParamMap.put("sign", SIGN);
		encParamMap.put("formId", formId);
		encParamMap.put("formType", formType);
		String enc = getEnc(encParamMap, KEY);
		return String.format(FORM_ADMIN_URL, fid, uid, dateTimeStr, SIGN, formId, formType, enc);
	}



	/**创建万能表单
	 * @Description
	 * @author wwb
	 * @Date 2021-11-18 17:21:24
	 * @param wfwFormCreateParam
	 * @return com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateResultDTO
	 */
	public WfwFormCreateResultDTO create(WfwFormCreateParamDTO wfwFormCreateParam) {
		Map<String, Object> params = new TreeMap<>();
		params.put("formId", wfwFormCreateParam.getFormId());
		params.put("originalFid", wfwFormCreateParam.getOriginalFid());
		params.put("uid", wfwFormCreateParam.getUid());
		params.put("fid", wfwFormCreateParam.getFid());
		params.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		params.put("sign", wfwFormCreateParam.getSign());
		params.put("enc", getEnc(params, wfwFormCreateParam.getKey()));
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap();
		paramMap.setAll(params);
		String result = restTemplate.postForObject(CREATE_FORM_URL, paramMap, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return JSON.parseObject(jsonObject.getString("data"), WfwFormCreateResultDTO.class);
		} else {
			String message = jsonObject.getString("msg");
			log.error("根据参数:{} 创建万能表单error:{}", JSON.toJSONString(wfwFormCreateParam), message);
			throw new BusinessException(message);
		}
	}

	private String getEnc(Map<String, Object> encParamMap, String key) {
		StringBuilder enc = new StringBuilder();
		for (Map.Entry<String, Object> entry : encParamMap.entrySet()) {
			enc.append("[").append(entry.getKey()).append("=")
					.append(entry.getValue()).append("]");
		}
		return DigestUtils.md5Hex(enc + "[" + key + "]");
	}

	private String getEnc(Map<String, Object> encParamMap) {
		return getEnc(encParamMap, KEY);
	}

}