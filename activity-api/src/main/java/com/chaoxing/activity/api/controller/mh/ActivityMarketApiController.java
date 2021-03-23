package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**活动市场api服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityMarketApiController
 * @description
 * @blame wwb
 * @date 2021-03-22 18:28:43
 */
@RestController
@RequestMapping("mh/activity-market")
public class ActivityMarketApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityClassifyQueryService activityClassifyQueryService;
	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;

	/**活动市场数据源
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-22 19:40:38
	 * @param data
	 * @param wfwfid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping
	public RestRespDTO index(@RequestBody String data, Integer wfwfid) {
		JSONObject params = JSON.parseObject(data);
		if (wfwfid == null) {
			wfwfid = params.getInteger("wfwfid");
		}
		Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
		Integer pageNum = params.getInteger("pageNum");
		pageNum = Optional.ofNullable(pageNum).orElse(1);
		Integer pageSize = params.getInteger("pageSize");
		pageSize = Optional.ofNullable(pageSize).orElse(12);
		Integer activityClassifyId = null;
		String classifies = params.getString("classifies");
		if (StringUtils.isNotBlank(classifies)) {
			JSONArray jsonArray = JSON.parseArray(classifies);
			if (jsonArray.size() > 0) {
				JSONObject activityClassify = jsonArray.getJSONObject(0);
				activityClassifyId = activityClassify.getInteger("id");
			}
		}
		ActivityQueryDTO activityQuery = ActivityQueryDTO.builder()
				.topFid(wfwfid)
				.activityClassifyId(activityClassifyId)
				.build();
		List<Integer> fids = Lists.newArrayList();
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByFid(wfwfid);
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwRegionalArchitectureDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(wfwfid);
		}
		activityQuery.setFids(fids);
		Page<Activity> page = new Page(pageNum, pageSize);
		page = activityQueryService.listParticipate(page, activityQuery);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("curPage", page.getCurrent());
		jsonObject.put("totalPages", page.getPages());
		jsonObject.put("totalRecords", page.getTotal());
		JSONArray activityJsonArray = new JSONArray();
		jsonObject.put("results", activityJsonArray);
		List<Activity> records = page.getRecords();
		if (CollectionUtils.isNotEmpty(records)) {
			for (Activity record : records) {
				JSONObject activity = new JSONObject();
				activity.put("id", record.getId());
				activity.put("type", 3);
				activity.put("orsUrl", record.getPreviewUrl());
				JSONArray fields = new JSONArray();
				activity.put("fields", fields);
				// 封面
				JSONObject cover = new JSONObject();
				cover.put("flag", "0");
				cover.put("key", "封面");
				cover.put("value", record.getCoverUrl());
				fields.add(cover);
				// 活动名称
				JSONObject name = new JSONObject();
				name.put("flag", "1");
				name.put("key", "活动名称");
				name.put("value", record.getName());
				fields.add(name);
				// 时间
				JSONObject time = new JSONObject();
				time.put("flag", "100");
				time.put("key", "活动时间");

				time.put("value", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getStartTime()) + " ~ " + DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getEndTime()));
				fields.add(time);
				// 地点
				JSONObject address = new JSONObject();
				address.put("flag", "102");
				address.put("key", "活动地点");
				String activityAddress = record.getAddress();
				if (StringUtils.isNotBlank(activityAddress)) {
					String detailAddress = record.getDetailAddress();
					if (StringUtils.isNotBlank(detailAddress)) {
						activityAddress += detailAddress;
					}
				}
				address.put("value", activityAddress);
				fields.add(address);
				activityJsonArray.add(activity);
			}
		}
		return RestRespDTO.success(jsonObject);
	}

	/**活动分类数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-22 18:41:25
	 * @param data
	 * @param wfwfid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("classifies")
	public RestRespDTO listClassify(@RequestBody String data, Integer wfwfid) {
		JSONObject params = JSON.parseObject(data);
		if (wfwfid == null) {
			wfwfid = params.getInteger("wfwfid");
		}
		List<Integer> wfwfids = Lists.newArrayList();
		wfwfids.add(wfwfid);
		List<ActivityClassify> activityClassifies = activityClassifyQueryService.listOrgsOptional(wfwfids);
		JSONObject jsonObject = new JSONObject();
		JSONArray activityClassifyJsonArray = new JSONArray();
		jsonObject.put("classifies", activityClassifyJsonArray);
		if (CollectionUtils.isNotEmpty(activityClassifies)) {
			for (ActivityClassify activityClassify : activityClassifies) {
				JSONObject item = new JSONObject();
				item.put("id", activityClassify.getId());
				item.put("name", activityClassify.getName());
				activityClassifyJsonArray.add(item);
			}
		}
		return RestRespDTO.success(jsonObject);
	}

}