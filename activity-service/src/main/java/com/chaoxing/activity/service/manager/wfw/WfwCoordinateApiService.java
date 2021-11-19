package com.chaoxing.activity.service.manager.wfw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.util.constant.DomainConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**微服务坐标api服务
 * @author wwb
 * @version ver 1.0
 * @className WfwCoordinateApiService
 * @description
 * @blame wwb
 * @date 2021-01-19 10:32:35
 */
@Slf4j
@Service
public class WfwCoordinateApiService {

	private static final String GET_DISTANCE_URL = DomainConstant.WFW_DOMAIN + "/mobileSet/libraryList?wfwfid=%d&startLng=%s&startLat=%s";

	@Resource
	private RestTemplate restTemplate;

	/**根据定位信息获取fid
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-19 10:45:12
	 * @param wfwfid
	 * @param longitude
	 * @param latitude
	 * @return java.lang.Integer
	*/
	public Integer getCoordinateAffiliationFid(Integer wfwfid, BigDecimal longitude, BigDecimal latitude) {
		String url = String.format(GET_DISTANCE_URL, wfwfid, longitude, latitude);
		try {
			String result = restTemplate.getForObject(url, String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			String listStr = jsonObject.getString("list");
			if (StringUtils.isNotBlank(listStr)) {
				JSONArray jsonArray = JSON.parseArray(listStr);
				int size = jsonArray.size();
				if (size > 0) {
					JSONObject org = jsonArray.getJSONObject(1);
					return org.getInteger("fid");
				}
			}
		} catch (Exception e) {
			log.error("根据wfwfid:{}, 经度:{}, 维度:{} 获取距离最近的机构列表error: {}", wfwfid, longitude, latitude, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}