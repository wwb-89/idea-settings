package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**星阅读服务
 * @author wwb
 * @version ver 1.0
 * @className StarApiService
 * @description
 * @blame wwb
 * @date 2020-11-11 10:34:56
 */
@Slf4j
@Service
public class StarApiService {

	/** 创建星阅读的url */
	private static final String CREATE_URL = "http://star.chaoxing.com/manage/createActivity";

	@Resource
	private RestTemplate restTemplate;

	/**创建星阅读
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-12 14:28:56
	 * @param uid
	 * @param fid
	 * @return java.lang.Integer
	*/
	public Integer create(Integer uid, Integer fid) {
		MultiValueMap<String, Integer> postBodyObj = new LinkedMultiValueMap<>();
		postBodyObj.add("uid", uid);
		postBodyObj.add("fid", fid);
		String result = restTemplate.postForObject(CREATE_URL, postBodyObj, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		jsonObject = jsonObject.getJSONObject("activity");
		Integer starId = jsonObject.getInteger("id");
		return starId;
	}

}