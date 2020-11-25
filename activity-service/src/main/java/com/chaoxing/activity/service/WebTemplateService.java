package com.chaoxing.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.WebTemplateAppDataMapper;
import com.chaoxing.activity.mapper.WebTemplateAppMapper;
import com.chaoxing.activity.mapper.WebTemplateMapper;
import com.chaoxing.activity.model.WebTemplate;
import com.chaoxing.activity.model.WebTemplateApp;
import com.chaoxing.activity.model.WebTemplateAppData;
import com.chaoxing.activity.util.enums.MhAppDataSourceEnum;
import com.chaoxing.activity.util.enums.MhAppTypeEnum;
import com.chaoxing.activity.util.exception.BusinessException;
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

	public List<WebTemplateApp> listAppByWebTemplateId(Integer webTemplateId) {
		return webTemplateAppMapper.selectList(new QueryWrapper<WebTemplateApp>()
			.lambda()
				.eq(WebTemplateApp::getWebTemplateId, webTemplateId)
		);
	}

}
