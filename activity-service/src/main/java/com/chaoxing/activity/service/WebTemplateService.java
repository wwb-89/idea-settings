package com.chaoxing.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.WebTemplateAppDataMapper;
import com.chaoxing.activity.mapper.WebTemplateAppMapper;
import com.chaoxing.activity.mapper.WebTemplateMapper;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.model.WebTemplateApp;
import com.chaoxing.activity.model.WebTemplateAppData;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.util.constant.WebTemplateCustomConfigConstant;
import com.chaoxing.activity.util.enums.MhAppDataSourceEnum;
import com.chaoxing.activity.util.enums.MhAppTypeEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className WebTemplateService
 * @description
 * @blame wwb
 * @date 2020-11-23 20:46:02
 */
@Slf4j
@Service
public class WebTemplateService {

	@Resource
	private WebTemplateMapper webTemplateMapper;
	@Resource
	private WebTemplateAppMapper webTemplateAppMapper;
	@Resource
	private WebTemplateAppDataMapper webTemplateAppDataMapper;
	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;

	/**根据id查询网站模版
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-23 23:06:47
	 * @param webTemplateId
	 * @return com.chaoxing.activity.model.WebTemplate
	*/
	public WebTemplate getById(Integer webTemplateId) {
		return webTemplateMapper.selectById(webTemplateId);
	}

	/**活动模板一定存在
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 15:28:53
	 * @param webTemplateId
	 * @return com.chaoxing.activity.model.WebTemplate
	*/
	public WebTemplate webTemplateExist(Integer webTemplateId) {
		WebTemplate webTemplate = getById(webTemplateId);
		Optional.ofNullable(webTemplate).orElseThrow(() -> new BusinessException("网页模板不存在"));
		return webTemplate;
	}

	/**根据门户应用类型查询是本地数据源的门户应用的数据列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 09:27:49
	 * @param webTemplateId
	 * @param appType
	 * @return java.util.List<com.chaoxing.activity.model.WebTemplateApp>
	*/
	public List<WebTemplateApp> listLocalDataSourceAppByWebTemplateIdAppType(Integer webTemplateId, MhAppTypeEnum appType) {
		List<WebTemplateApp> webTemplateApps = webTemplateAppMapper.selectList(new QueryWrapper<WebTemplateApp>()
				.lambda()
				.eq(WebTemplateApp::getWebTemplateId, webTemplateId)
				.eq(WebTemplateApp::getAppType, appType.getValue())
				.eq(WebTemplateApp::getDataSourceType, MhAppDataSourceEnum.LOCAL.getValue())
		);
		if (CollectionUtils.isNotEmpty(webTemplateApps)) {
			List<Integer> webTemplateAppIds = webTemplateApps.stream().map(WebTemplateApp::getId).collect(Collectors.toList());
			List<WebTemplateAppData> webTemplateAppDataList = webTemplateAppDataMapper.selectList(new QueryWrapper<WebTemplateAppData>()
					.lambda()
					.in(WebTemplateAppData::getWebTemplateAppId, webTemplateAppIds)
					.orderByAsc(WebTemplateAppData::getSequence)
			);
			for (WebTemplateApp webTemplateApp : webTemplateApps) {
				Integer id = webTemplateApp.getId();
				List<WebTemplateAppData> itemDataList = webTemplateApp.getWebTemplateAppDataList();
				itemDataList = Optional.ofNullable(itemDataList).orElse(new ArrayList<>());
				webTemplateApp.setWebTemplateAppDataList(itemDataList);
				Iterator<WebTemplateAppData> iterator = webTemplateAppDataList.iterator();
				while (iterator.hasNext()) {
					WebTemplateAppData next = iterator.next();
					if (Objects.equals(next.getWebTemplateAppId(), id)) {
						itemDataList.add(next);
						iterator.remove();
					}
				}
			}
		}
		return webTemplateApps;
	}

	/**根据网页模板id查询关联的应用列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 15:29:12
	 * @param webTemplateId
	 * @return java.util.List<com.chaoxing.activity.model.WebTemplateApp>
	*/
	public List<WebTemplateApp> listAppByWebTemplateId(Integer webTemplateId) {
		return webTemplateAppMapper.selectList(new QueryWrapper<WebTemplateApp>()
			.lambda()
				.eq(WebTemplateApp::getWebTemplateId, webTemplateId)
		);
	}

	/**查询可用的模板
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-30 17:47:00
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.WebTemplate>
	*/
	public List<WebTemplate> listAvailable(Integer fid) {
		List<WebTemplate> webTemplates = Lists.newArrayList();
		// 查询机构所属的code
		List<String> codes = wfwRegionalArchitectureApiService.listCodeByFid(fid);
		List<WebTemplate> orgAffiliations = webTemplateMapper.listAffiliation(codes, fid);
		if (CollectionUtils.isNotEmpty(orgAffiliations)) {
			webTemplates.addAll(orgAffiliations);
		}
		codes.retainAll(WebTemplateCustomConfigConstant.CUSTOM_AREA_CODES);
		if (CollectionUtils.isEmpty(codes)) {
			// 查询系统的
			List<WebTemplate> systems = webTemplateMapper.selectList(new QueryWrapper<WebTemplate>()
					.lambda()
					.eq(WebTemplate::getSystem, Boolean.TRUE)
					.orderByAsc(WebTemplate::getSequence)
			);
			if (CollectionUtils.isNotEmpty(systems)) {
				webTemplates.addAll(systems);
			}
		}
		return webTemplates;
	}

}
