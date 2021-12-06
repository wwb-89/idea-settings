package com.chaoxing.activity.service.manager.wfw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.wfw.WfwAppParamDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;

/**微服务应用api服务
 * @author wwb
 * @version ver 1.0
 * @className WfwAppApiService
 * @description
 * @blame wwb
 * @date 2021-07-16 15:53:13
 */
@Slf4j
@Service
public class WfwAppApiService {

	/** 创建应用的url */
	private static final String APP_URL = DomainConstant.WFW + "/appInter/addApp";
	private static final Integer DEFAULT_OPEN_TYPE = 14;

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**创建应用
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-16 16:08:25
	 * @param wfwAppCreateParamDto
	 * @return java.lang.Integer 应用id
	*/
	public Integer newApp(WfwAppParamDTO wfwAppCreateParamDto) {
		String result = restTemplate.postForObject(APP_URL, buildParams(wfwAppCreateParamDto), String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		boolean status = Optional.ofNullable(jsonObject.getBoolean("status")).orElse(false);
		if (status) {
			return jsonObject.getInteger("appId");
		}else {
			throw new BusinessException(jsonObject.getString("msg"));
		}
	}

	/**更新应用
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-21 14:53:32
	 * @param wfwAppCreateParamDto
	 * @return void
	*/
	public void updateApp(WfwAppParamDTO wfwAppCreateParamDto) {
		String result = restTemplate.postForObject(APP_URL, buildParams(wfwAppCreateParamDto), String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		boolean status = Optional.ofNullable(jsonObject.getBoolean("status")).orElse(false);
		if (!status) {
			throw new BusinessException(jsonObject.getString("msg"));
		}
	}

	private MultiValueMap<String, Object> buildParams(WfwAppParamDTO wfwAppCreateParamDto) {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("id", wfwAppCreateParamDto.getId());
		params.add("openType", DEFAULT_OPEN_TYPE);
		params.add("classifyId", wfwAppCreateParamDto.getClassifyId());
		params.add("fid", wfwAppCreateParamDto.getFid());
		params.add("name", wfwAppCreateParamDto.getName());
		params.add("icon", wfwAppCreateParamDto.getIconUrl());
		params.add("openAddr", wfwAppCreateParamDto.getAppUrl());
		params.add("pcUrl", wfwAppCreateParamDto.getPcUrl());
		params.add("backUrl", wfwAppCreateParamDto.getAdminUrl());
		params.add("wechatUrl", wfwAppCreateParamDto.getWechatUrl());
		return params;
	}

}