package com.chaoxing.activity.service.manager;

import com.chaoxing.activity.dto.LoginUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**门户api
 * @author wwb
 * @version ver 1.0
 * @className MhApiService
 * @description
 * @blame wwb
 * @date 2020-11-23 20:37:33
 */
@Slf4j
@Service
public class MhApiService {

	/** 克隆模板的url http://portal.chaoxing.com/web-others/{templateId}/cloneActivity?wfwfid=&activityId=&uid= */
	private static final String CLONE_TEMPLATE_URL = "http://portal.chaoxing.com/web-others/%d/cloneActivity?wfwfid=%d&activityId=%d&uid=%d";

	@Resource
	private RestTemplate restTemplate;

	/**克隆模板
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-23 20:44:46
	 * @param activityId
	 * @param templateId
	 * @param loginUser
	 * @return java.lang.String
	*/
	public String cloneTemplate(Integer activityId, Integer templateId, LoginUserDTO loginUser) {
		Integer fid = loginUser.getFid();
		Integer uid = loginUser.getUid();
		String url = String.format(CLONE_TEMPLATE_URL, templateId, fid, activityId, uid);
		String result = restTemplate.postForObject(url, null, String.class);
		return null;
	}

}