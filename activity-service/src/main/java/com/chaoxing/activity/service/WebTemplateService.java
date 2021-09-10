package com.chaoxing.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.WebTemplateActivityFlagMapper;
import com.chaoxing.activity.mapper.WebTemplateAppDataMapper;
import com.chaoxing.activity.mapper.WebTemplateAppMapper;
import com.chaoxing.activity.mapper.WebTemplateMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.org.OrgService;
import com.chaoxing.activity.util.enums.MhAppDataSourceEnum;
import com.chaoxing.activity.util.enums.MhAppTypeEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
	private WebTemplateActivityFlagMapper webTemplateActivityFlagMapper;

	@Resource
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private OrgService orgService;

	/**根据id查询网站模版
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-23 23:06:47
	 * @param id
	 * @return com.chaoxing.activity.model.WebTemplate
	*/
	public WebTemplate getById(Integer id) {
		return webTemplateMapper.selectById(id);
	}

	/**根据id列表查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-30 17:23:27
	 * @param ids
	 * @return java.util.List<com.chaoxing.activity.model.WebTemplate>
	*/
	public List<WebTemplate> listByIds(List<Integer> ids) {
		return webTemplateMapper.selectList(new QueryWrapper<WebTemplate>()
				.lambda()
				.in(WebTemplate::getId, ids)
				.eq(WebTemplate::getDeleted, false)
				.orderByAsc(WebTemplate::getSequence)
		);
	}

	/**活动模板一定存在
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 15:28:53
	 * @param id
	 * @return com.chaoxing.activity.model.WebTemplate
	*/
	public WebTemplate webTemplateExist(Integer id) {
		WebTemplate webTemplate = getById(id);
		Optional.ofNullable(webTemplate).orElseThrow(() -> new BusinessException("网页模板不存在:" + id));
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
	 * @Description 查询可以使用的模版列表，规则：
	 * 1、机构或区域独有的排列在前
	 * 2、活动标识的在后
	 * 3、1和2的结果要去重
	 * @author wwb
	 * @Date 2020-12-30 17:47:00
	 * @param fid
	 * @param activityFlag
	 * @return java.util.List<com.chaoxing.activity.model.WebTemplate>
	*/
	public List<WebTemplate> listAvailable(Integer fid, String activityFlag) {
		List<WebTemplate> webTemplates = Lists.newArrayList();
		// 查询机构所属的code
		List<String> codes = wfwAreaApiService.listCodeByFid(fid);
		codes = Optional.ofNullable(codes).orElse(Lists.newArrayList()).stream().filter(StringUtils::isNotBlank).filter(v -> v.length() >= 4).map(v -> v.substring(0, 4)).collect(Collectors.toList());
		// 先查询机构和区域特有的
		List<WebTemplate> orgAffiliations = webTemplateMapper.listAffiliation(codes, fid);
		if (CollectionUtils.isNotEmpty(orgAffiliations)) {
			webTemplates.addAll(orgAffiliations);
		}
		boolean isCustomOrg = orgService.isCustomOrg(codes);
		if (!isCustomOrg) {
			Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(activityFlag);
			if (activityFlagEnum == null) {
				activityFlag = Activity.ActivityFlagEnum.NORMAL.getValue();
			}
			// 查询活动标识关联的模版列表
			List<WebTemplateActivityFlag> webTemplateActivityFlags = webTemplateActivityFlagMapper.selectList(new QueryWrapper<WebTemplateActivityFlag>()
					.lambda()
					.eq(WebTemplateActivityFlag::getActivityFlag, activityFlag)
					.select(WebTemplateActivityFlag::getWebTemplateId)
			);
			if (CollectionUtils.isNotEmpty(webTemplateActivityFlags)) {
				List<Integer> webtemplateIds = webTemplateActivityFlags.stream().map(WebTemplateActivityFlag::getWebTemplateId).collect(Collectors.toList());
				List<WebTemplate> activityFlagWebTemplates = listByIds(webtemplateIds);
				if (CollectionUtils.isNotEmpty(activityFlagWebTemplates)) {
					webTemplates.addAll(activityFlagWebTemplates);
				}
			}
		}
		return webTemplates;
	}

}
