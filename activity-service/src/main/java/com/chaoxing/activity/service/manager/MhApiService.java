package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.mh.MhCloneParamDTO;
import com.chaoxing.activity.dto.mh.MhCloneResultDTO;
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
	/** 更新网站title url http://portal.chaoxing.com/web-others/{pageId}/page-name?name={name}&uid={uid} */
	private static final String UPDATE_WEB_TITLE_URL = ActivityMhUrlConstant.MH_DOMAIN + "/web-others/%d/page-name?name=%s&uid=%d";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**克隆模板
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-23 20:44:46
	 * @param mhCloneParam
	 * @return com.chaoxing.activity.dto.mh.MhCloneResultDTO
	*/
	public MhCloneResultDTO cloneTemplate(MhCloneParamDTO mhCloneParam) {
		Integer fid = mhCloneParam.getWfwfid();
		Integer uid = mhCloneParam.getUid();
		String url = String.format(CLONE_TEMPLATE_URL, mhCloneParam.getTemplateId(), fid, uid, mhCloneParam.getWebsiteName());
		Integer originPageId = mhCloneParam.getOriginPageId();
		if (originPageId != null) {
			url += "&originPageId=" + originPageId;
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity(JSON.toJSONString(mhCloneParam), httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("code");
		if (Objects.equals(code, 1)) {
			JSONObject data = jsonObject.getJSONObject("data");
			return MhCloneResultDTO.builder()
					.pageId(data.getInteger("pageId"))
					.previewUrl(data.getString("preview"))
					.editUrl(data.getString("edit"))
					.build();
		} else {
			String errorMessage = jsonObject.getString("message");
			throw new BusinessException(errorMessage);
		}
	}

	/**更新网站title
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 15:11:34
	 * @param pageId
	 * @param name
	 * @param uid
	 * @return void
	*/
	public void updateWebTitle(Integer pageId, String name, Integer uid) {
		String url = String.format(UPDATE_WEB_TITLE_URL, pageId, name, uid);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity httpEntity = new HttpEntity(httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("code");
		if (!Objects.equals(code, 1)) {
			String errorMessage = jsonObject.getString("message");
			throw new BusinessException(errorMessage);
		}
	}

}