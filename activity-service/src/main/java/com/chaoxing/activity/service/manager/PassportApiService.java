package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

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

	private static final String ORG_NAME_URL = "https://passport2.chaoxing.com/org/getName?schoolid=";

	@Resource
	private RestTemplate restTemplate;

	/**查询机构名称
	 * @Description
	 * @author wwb
	 * @Date 2019-10-23 10:02:52
	 * @param fid
	 * @return java.lang.String
	 */
	public String getOrgName(Integer fid) {
		StringBuilder url = new StringBuilder();
		url.append(ORG_NAME_URL);
		url.append(fid);
		String result = restTemplate.getForObject(url.toString(), String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer schoolid = jsonObject.getInteger("schoolid");
		if (fid.equals(schoolid)) {
			return jsonObject.getString("name");
		}
		return null;
	}

}