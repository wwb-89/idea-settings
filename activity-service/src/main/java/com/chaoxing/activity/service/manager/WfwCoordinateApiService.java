package com.chaoxing.activity.service.manager;

import lombok.extern.slf4j.Slf4j;
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

	@Resource
	private RestTemplate restTemplate;

	/**根据定位信息获取fid
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-19 10:45:12
	 * @param wfwfid
	 * @param longitude
	 * @param dimension
	 * @return java.lang.Integer
	*/
	public Integer getCoordinateAffiliationFid(Integer wfwfid, BigDecimal longitude, BigDecimal dimension) {
		return null;
	}

}