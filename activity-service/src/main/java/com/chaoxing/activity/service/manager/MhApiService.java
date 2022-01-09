package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.chaoxing.activity.dto.manager.mh.MhCloneParamDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneResultDTO;
import com.chaoxing.activity.dto.stat.MhViewNumDailyStatDTO;
import com.chaoxing.activity.util.UrlUtils;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

	/** 根据模版id（templateId）克隆模板的url http://mh.chaoxing.com/web-others/{templateId}/cloneActivity?wfwfid=&activityId=&uid= */
	private static final String CLONE_TEMPLATE_URL = DomainConstant.MH + "/web-others/%d/cloneActivity?wfwfid=%d&uid=%d&websiteName=%s&forceCheckDomain=false";
	/** 根据网站id（website_id）克隆模版url */
	private static final String CLONE_TEMPLATE_URL_BY_WEBSITE_ID = DomainConstant.MH + "/web-others/%d/cloneActivityNew?wfwfid=%d&uid=%d&websiteName=%s&forceCheckDomain=false";
	/** 更新网站title url http://portal.chaoxing.com/web-others/{pageId}/page-name?name={name}&uid={uid} */
	private static final String UPDATE_WEB_TITLE_URL = DomainConstant.MH + "/web-others/%d/page-name?name=%s&uid=%d";
	/** 根据pageId查询website */
	public static final String GET_WEBSITE_URL = DomainConstant.MH + "/website/%d/get-by-page";
	/** 网站总访问量 */
	private static final String WEBSITE_TOTAL_VIEW_NUM_URL = DomainConstant.MH + "/data-count/website/%d/homepage/pv";
	/** 网站按天访问量统计 */
	public static final String WEBSITE_DAILY_VIEW_NUM_STAT_URL = DomainConstant.MH + "/data-count/website/%d/daily-uv?startTime=%s&endTime=%s";
	/** 获取评价的地址 */
	public static final String GET_EVALUATION_URL = DomainConstant.MH + "/engine2/api/wuhan/comment";

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
		Integer templateId = mhCloneParam.getTemplateId();
		String url;
		if (templateId != null) {
			url = String.format(CLONE_TEMPLATE_URL, mhCloneParam.getTemplateId(), fid, uid, mhCloneParam.getWebsiteName());
		} else {
			Integer websiteId = mhCloneParam.getWebsiteId();
			Optional.ofNullable(websiteId).orElseThrow(() -> new BusinessException("模版id或网站id不能为空"));
			url = String.format(CLONE_TEMPLATE_URL_BY_WEBSITE_ID, websiteId, fid, uid, mhCloneParam.getWebsiteName());
		}
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
			String message = jsonObject.getString("message");
			log.error("根据url:{} 更新网站title error:{}", url, message);
			throw new BusinessException(message);
		}
	}

	/**根据pageId查询websiteId
	 * @Description
	 * @author wwb
	 * @Date 2021-04-15 16:12:58
	 * @param pageId
	 * @return java.lang.Integer
	*/
	public Integer getWebsiteIdByPageId(Integer pageId) {
		String url = String.format(GET_WEBSITE_URL, pageId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("code");
		if (Objects.equals(code, 1)) {
			return jsonObject.getJSONObject("data").getInteger("id");
		} else {
			String errorMessage = jsonObject.getString("message");
			throw new BusinessException(errorMessage);
		}
	}

	/**统计总访问量
	 * @Description
	 * @author wwb
	 * @Date 2021-04-15 16:24:55
	 * @param websiteId
	 * @return java.lang.Integer
	*/
	public Integer countWebsitePv(Integer websiteId) {
		String url = String.format(WEBSITE_TOTAL_VIEW_NUM_URL, websiteId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("code");
		if (Objects.equals(code, 1)) {
			return jsonObject.getInteger("data");
		} else {
			String errorMessage = jsonObject.getString("message");
			throw new BusinessException(errorMessage);
		}
	}

	/**按天统计网站访问量
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-15 18:10:55
	 * @param websiteId
	 * @param startTime
	 * @param endTime
	 * @return java.util.List<com.chaoxing.activity.dto.stat.MhViewNumDailyStatDTO>
	*/
	public List<MhViewNumDailyStatDTO> statWebsiteDailyViewNum(Integer websiteId, String startTime, String endTime) {
		List<MhViewNumDailyStatDTO> dailyStats = Lists.newArrayList();
		String url = String.format(WEBSITE_DAILY_VIEW_NUM_STAT_URL, websiteId, startTime, endTime);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("code");
		if (Objects.equals(code, 1)) {
			JSONArray data = jsonObject.getJSONArray("data");
			if (data != null && jsonObject.size() > 0) {
				ParserConfig config = new ParserConfig();
				dailyStats = JSON.parseArray(jsonObject.getString("data"), MhViewNumDailyStatDTO.class);
			}
			return dailyStats;
		} else {
			String errorMessage = jsonObject.getString("message");
			throw new BusinessException(errorMessage);
		}
	}

	/**获取评价的url
	 * @Description 
	 * @author wwb
	 * @Date 2021-12-21 16:38:30
	 * @param formUserId
	 * @param formId
	 * @param url
	 * @return java.lang.String
	*/
	public String getEvaluationUrl(Integer formUserId, Integer formId, String url) {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("shopId", 0);
		params.add("goodsId", formId);
		params.add("goodsRowId", formUserId);
		params.add("resourceType", 6);
		params.add("url", url);
		params.add("loginUrl", UrlUtils.extractDomain(url) + "/login");
		String result = restTemplate.postForObject(GET_EVALUATION_URL, params, String.class);
		return result;
	}

}