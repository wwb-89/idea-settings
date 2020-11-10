package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.OrgAddressDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;

/** guanli.chaoxing.com相关接口
 * @author wwb
 * @version ver 1.0
 * @className GuanliApiService
 * @description
 * @blame wwb
 * @date 2020-11-10 10:59:41
 */
@Slf4j
@Service
public class GuanliApiService {

	/** 获取机构位置信息接口 */
	private static final String GET_ORG_LOCATION_URL = "http://guanli.chaoxing.com/siteInter/getSite?fid=%d";
	/** 地址分隔符 */
	private static final String ADDRESS_SEPARATOR = "->";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	public OrgAddressDTO getAddressByFid(Integer fid) {
		String url = String.format(GET_ORG_LOCATION_URL, fid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean status = jsonObject.getBoolean("status");
		status = Optional.ofNullable(status).orElse(Boolean.FALSE);
		if (status) {
			String links = jsonObject.getString("links");
			String[] split = links.split(ADDRESS_SEPARATOR);
			int length = split.length;
			String country = length > 0 ? split[0] : "";
			String province = length > 1 ? split[1] : "";
			String city = length > 2 ? split[2] : "";
			String county = length > 3 ? split[3] : "";
			return OrgAddressDTO.builder()
					.fid(fid)
					.orgName(jsonObject.getString("name"))
					.country(country.trim())
					.province(province.trim())
					.city(city.trim())
					.county(county.trim())
					.build();
		} else {
			log.error("根据fid:{} 查询地址信息失败", fid);
			return null;
		}
	}

}