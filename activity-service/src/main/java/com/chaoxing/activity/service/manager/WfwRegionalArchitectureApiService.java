package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**微服务组织架构服务
 * @author wwb
 * @version ver 1.0
 * @className WfwRegionalArchitectureApiService
 * @description
 * @blame wwb
 * @date 2020-11-12 11:22:58
 */
@Slf4j
@Service
public class WfwRegionalArchitectureApiService {

	/** 根据fid获取架构的code */
	private static final String GET_REGIONAL_ARCHITECTURE_BY_FID_URL = "http://guanli.chaoxing.com/siteInter/siteHierarchy?fid=%s";
	/** 根据code获取架构 */
	private static final String GET_REGIONAL_ARCHITECTURE_BY_CODE_URL = "http://guanli.chaoxing.com/siteInter/siteHierarchy?code=%s&pageSize=%s";

	private static final Integer DEFAULT_PAGE_SIZE = 1;

	@Resource
	private RestTemplate restTemplate;

	/**根据fid查询该机构下的层级机构
	 * @Description
	 * 先根据fid查询层级code列表，再根据code列表分别查询层级架构
	 * @author wwb
	 * @Date 2020-08-24 13:27:21
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.common.manager.dto.RegionalArchitectureDTO>
	 */
	public List<WfwRegionalArchitectureDTO> listByFid(Integer fid) {
		List<WfwRegionalArchitectureDTO> regionalArchitectures = new ArrayList<>();
		List<String> codes = listCodeByFid(fid);
		if (CollectionUtils.isEmpty(codes)) {
			return new ArrayList<>();
		}
		for (String code : codes) {
			String url = String.format(GET_REGIONAL_ARCHITECTURE_BY_CODE_URL, code, DEFAULT_PAGE_SIZE);
			String result = restTemplate.getForObject(url, String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			boolean status = jsonObject.getBooleanValue("status");
			if (!status) {
				log.error("根据code:{}查询所属层级架构列表失败", code);
				throw new BusinessException("未查询到层级架构");
			}
			// 获取条数
			Integer count = jsonObject.getInteger("count");
			// pageSize置为count一次查询完所有数据
			url = String.format(GET_REGIONAL_ARCHITECTURE_BY_CODE_URL, code, count);
			result = restTemplate.getForObject(url, String.class);
			jsonObject = JSON.parseObject(result);
			status = jsonObject.getBooleanValue("status");
			if (!status) {
				log.error("根据code:{}查询所属层级架构列表失败", code);
				throw new BusinessException("未查询到层级架构");
			}
			String jsonlistStr = jsonObject.getString("list");
			List<WfwRegionalArchitectureDTO> items = JSON.parseArray(jsonlistStr, WfwRegionalArchitectureDTO.class);
			if (CollectionUtils.isNotEmpty(items)) {
				regionalArchitectures.addAll(items);
			}
		}
		return regionalArchitectures;
	}
	/**根据fid查询所在层级架构code列表
	 * @Description
	 * @author wwb
	 * @Date 2020-08-24 13:31:16
	 * @param fid
	 * @return java.util.List<java.lang.String>
	 */
	private List<String> listCodeByFid(Integer fid) {
		List<String> codes = new ArrayList<>();
		String url = String.format(GET_REGIONAL_ARCHITECTURE_BY_FID_URL, fid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		boolean status = jsonObject.getBooleanValue("status");
		if (!status) {
			log.error("根据fid:{}查询所属层级架构列表失败", fid);
			throw new BusinessException("未查询到层级架构");
		}
		String jsonlistStr = jsonObject.getString("list");
		List<WfwRegionalArchitectureDTO> regionalArchitectures = JSON.parseArray(jsonlistStr, WfwRegionalArchitectureDTO.class);
		if (CollectionUtils.isNotEmpty(regionalArchitectures)) {
			for (WfwRegionalArchitectureDTO regionalArchitecture : regionalArchitectures) {
				String code = regionalArchitecture.getCode();
				if (!codes.contains(code)) {
					codes.add(code);
				}
			}
		}
		return codes;
	}

}