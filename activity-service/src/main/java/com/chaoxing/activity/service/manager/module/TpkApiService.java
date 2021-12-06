package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Objects;

/**听评课服务
 * @author wwb
 * @version ver 1.0
 * @className TpkApiService
 * @description
 * @blame wwb
 * @date 2020-11-11 10:35:04
 */
@Slf4j
@Service
public class TpkApiService {

	/**
	 * 创建听评课url
	 *  name：活动名称，允许不传
	 *  originId：活动id，允许不传
	 *  identify：活动标识，允许不传
	 */
	private static final String CREATE_URL = DomainConstant.TEACHER + "/tpk3-activity/admin/create/activity?name=%s&uid=%s&fid=%s";
	private static final String RESPONSE_CODE_SUCCESS = "1";

	@Resource
	private RestTemplate restTemplate;

	/**创建听评课模块
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-12 14:51:13
	 * @param name
	 * @param loginUser
	 * @return java.lang.Integer
	*/
	public Integer create(String name, LoginUserDTO loginUser) {
		Integer uid = loginUser.getUid();
		Integer fid = loginUser.getFid();
		String url = String.format(CREATE_URL, name, uid, fid);
		String result = restTemplate.postForObject(url, null, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		String code = jsonObject.getString("code");
		if (Objects.equals(code, RESPONSE_CODE_SUCCESS)) {
			// 成功
			JSONObject data = jsonObject.getJSONObject("data");
			return data.getInteger("id");
		} else {
			throw new BusinessException(jsonObject.getString("message"));
		}
	}

}