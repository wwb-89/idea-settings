package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	@Resource
	private RestTemplate restTemplate;
	@Resource
	private PassportApiService passportApiService;

	/**根据fid查询该机构下的层级机构
	 * @Description
	 * 先根据fid查询层级code列表，再根据code列表分别查询层级架构
	 * @author wwb
	 * @Date 2020-08-24 13:27:21
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.common.manager.dto.RegionalArchitectureDTO>
	 */
	public List<WfwRegionalArchitectureDTO> listByFid(Integer fid) {
		List<WfwRegionalArchitectureDTO> result;
		List<String> codes = listCodeByFid(fid);
		if (CollectionUtils.isEmpty(codes)) {
			result = Lists.newArrayList();
		} else {
			String code = codes.get(0);
			result = listByCode(code);
		}
		if (CollectionUtils.isEmpty(result)) {
			// 构建一个当前单位的范围
			result.add(buildWfwRegionalArchitecture(fid));
		}
		return result;
	}

	/**构建一个层级架构
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-20 13:00:08
	 * @param fid
	 * @return com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO
	*/
	public WfwRegionalArchitectureDTO buildWfwRegionalArchitecture(Integer fid) {
		String orgName = passportApiService.getOrgName(fid);
		return WfwRegionalArchitectureDTO.builder()
				.id(0)
				.name(orgName)
				.pid(0)
				.code("")
				.links(orgName)
				.level(1)
				.fid(fid)
				.existChild(Boolean.FALSE)
				.sort(1)
				.build();
	}

	/**根据层级区域编码查询层级架构
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-02 11:18:23
	 * @param code
	 * @return java.util.List<com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO>
	*/
	public List<WfwRegionalArchitectureDTO> listByCode(String code) {
		List<WfwRegionalArchitectureDTO> regionalArchitectures = Lists.newArrayList();
		String url = String.format(GET_REGIONAL_ARCHITECTURE_BY_CODE_URL, code, Integer.MAX_VALUE);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean status = jsonObject.getBooleanValue("status");
		status = Optional.ofNullable(status).orElse(Boolean.FALSE);
		if (!status) {
			log.error("根据code:{}查询所属层级架构列表失败", code);
			throw new BusinessException("未查询到层级架构");
		}
		String jsonlistStr = jsonObject.getString("list");
		List<WfwRegionalArchitectureDTO> items = JSON.parseArray(jsonlistStr, WfwRegionalArchitectureDTO.class);
		if (CollectionUtils.isNotEmpty(items)) {
			regionalArchitectures.addAll(items);
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
	public List<String> listCodeByFid(Integer fid) {
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