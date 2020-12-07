package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.mh.MhCloneParamDTO;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Objects;

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

	/** 克隆模板的url http://mh.chaoxing.com/web-others/{templateId}/cloneActivity?wfwfid=&activityId=&uid= */
	private static final String CLONE_TEMPLATE_URL = ActivityMhUrlConstant.MH_DOMAIN + "/web-others/%d/cloneActivity?wfwfid=%d&uid=%d&websiteName=%s";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**克隆模板
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-23 20:44:46
	 * @param mhCloneParam
	 * @return java.lang.Integer
	*/
	public Integer cloneTemplate(MhCloneParamDTO mhCloneParam) {
		Integer fid = mhCloneParam.getWfwfid();
		Integer uid = mhCloneParam.getUid();
		String url = String.format(CLONE_TEMPLATE_URL, mhCloneParam.getTemplateId(), fid, uid, mhCloneParam.getWebsiteName());
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity(JSON.toJSONString(mhCloneParam), httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("code");
		if (Objects.equals(code, 1)) {
			Integer pageId = jsonObject.getJSONObject("data").getInteger("pageId");
			return pageId;
		} else {
			String errorMessage = jsonObject.getString("message");
			throw new BusinessException(errorMessage);
		}
	}

	/**封装活动访问的地址
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 14:04:35
	 * @param pageId
	 * @return java.lang.String
	*/
	public String packageActivityAccessUrl(Integer pageId) {
		if (pageId == null) {
			return "";
		}
		StringBuilder accessUrlStringBuilder = new StringBuilder();
		accessUrlStringBuilder.append(String.format(ActivityMhUrlConstant.ACTIVITY_ACCESS_URL, pageId));
		return accessUrlStringBuilder.toString();
	}

}