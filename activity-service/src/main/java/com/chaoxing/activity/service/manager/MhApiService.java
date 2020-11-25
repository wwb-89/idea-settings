package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.mh.MhCloneParamDTO;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
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

	/** 克隆模板的url http://portal.chaoxing.com/web-others/{templateId}/cloneActivity?wfwfid=&activityId=&uid= */
	private static final String CLONE_TEMPLATE_URL = "http://portal.chaoxing.com/web-others/%d/cloneActivity?wfwfid=%d&uid=%d";

	@Resource
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
		String url = String.format(CLONE_TEMPLATE_URL, mhCloneParam.getTemplateId(), fid, uid);
		String result = restTemplate.postForObject(url, null, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("code");
		if (Objects.equals(code, 1)) {
			Integer pageId = jsonObject.getInteger("pageId");
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
		StringBuilder accessUrlStringBuilder = new StringBuilder();
		accessUrlStringBuilder.append(String.format(ActivityMhUrlConstant.ACTIVITY_ACCESS_URL, pageId));
		return accessUrlStringBuilder.toString();
	}

}