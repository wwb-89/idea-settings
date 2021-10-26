package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.api.util.MhPreParamsUtils;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.service.activity.ActivityCoverUrlSyncService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
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
	private ClassifyQueryService classifyQueryService;
	@Resource
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private ActivityCoverUrlSyncService activityCoverUrlSyncService;
	@Resource
	private MarketQueryService marketQueryService;

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
		Integer pageNum = params.getInteger("page");
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
		String preParams = params.getString("preParams");
		JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
		// 状态
		String statusParams = urlParams.getString("status");
		List<Integer> statusList = MhPreParamsUtils.resolveIntegerV(statusParams);
		// marketId
		Integer marketId = urlParams.getInteger("marketId");
		// flag
		String flag = urlParams.getString("flag");
		ActivityQueryDTO activityQuery = ActivityQueryDTO.builder()
				.topFid(wfwfid)
				.statusList(statusList)
				.marketId(marketId)
				.flag(flag)
				.activityClassifyId(activityClassifyId)
				.build();
		List<Integer> fids = wfwAreaApiService.listSubFid(wfwfid);
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
				cover.put("value", activityCoverUrlSyncService.getCoverUrl(record));
				fields.add(cover);
				// 活动名称
				JSONObject name = new JSONObject();
				name.put("flag", "1");
				name.put("key", "活动名称");
				name.put("value", record.getName());
				fields.add(name);
				// 开始时间
				JSONObject startTime = new JSONObject();
				startTime.put("flag", "6");
				startTime.put("key", "活动时间");
				startTime.put("value", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getStartTime()));
				fields.add(startTime);

				JSONObject endTime = new JSONObject();
				endTime.put("flag", "101");
				endTime.put("key", "活动时间");
				endTime.put("value", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getEndTime()));
				fields.add(endTime);

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
				// 活动分类
				JSONObject classify = new JSONObject();
				classify.put("flag", "103");
				classify.put("key", "分类");
				classify.put("value", record.getActivityClassifyName());
				fields.add(classify);
				activityJsonArray.add(activity);
			}
		}
		return RestRespDTO.success(jsonObject);
	}

	/**活动市场数据源
	 * @Description
	 * @author wwb
	 * @Date 2021-03-22 19:40:38
	 * @param data
	 * @param wfwfid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("custom")
	public RestRespDTO customIndex(@RequestBody String data, Integer wfwfid) {
		JSONObject params = JSON.parseObject(data);
		if (wfwfid == null) {
			wfwfid = params.getInteger("wfwfid");
		}
		Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
		Integer pageNum = params.getInteger("page");
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
		String preParams = params.getString("preParams");
		JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
		// marketId
		Integer marketId = urlParams.getInteger("marketId");
		// flag
		String flag = urlParams.getString("flag");
		ActivityQueryDTO activityQuery = ActivityQueryDTO.builder()
				.topFid(wfwfid)
				.activityClassifyId(activityClassifyId)
				.marketId(marketId)
				.flag(flag)
				.build();
		List<Integer> fids = wfwAreaApiService.listSubFid(wfwfid);
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
				// 副标题
				// 时间
				JSONObject customTime = new JSONObject();
				customTime.put("flag", "3");
				customTime.put("key", "活动时间");
				customTime.put("value", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getStartTime()) + " ~ " + DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getEndTime()));
				fields.add(customTime);

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
				// 活动分类
				JSONObject classify = new JSONObject();
				classify.put("flag", "103");
				classify.put("key", "分类");
				classify.put("value", record.getActivityClassifyName());
				fields.add(classify);
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
		String preParams = params.getString("preParams");
		JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
		// marketId
		Integer marketId = urlParams.getInteger("marketId");
		// flag
		String flag = urlParams.getString("flag");
		if (marketId == null && StringUtils.isNotBlank(flag)) {
			// 根据flag找活动市场id
			marketId = marketQueryService.getMarketIdByFlag(wfwfid, flag);
		}
		return RestRespDTO.success(searchAndPackageClassifies(wfwfid, marketId));
	}

	/**查询分类，仅根据填写的fid或marketId进行分类查询，marketId优先
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-26 15:42:17
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("classifies/with-extra-params")
	public RestRespDTO listClassify(@RequestBody String data) {
		JSONObject params = JSON.parseObject(data);
		Integer wfwfid = params.getInteger("wfwfid");
		String preParams = params.getString("preParams");
		JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
		// marketId、fid
		Integer marketId = urlParams.getInteger("marketId");
		Integer fid = urlParams.getInteger("fid");
		// flag
		String flag = urlParams.getString("flag");
		if (marketId == null && StringUtils.isNotBlank(flag)) {
			// 根据flag找活动市场id
			marketId = marketQueryService.getMarketIdByFlag(Optional.ofNullable(fid).orElse(wfwfid), flag);
		}
		if (fid == null && marketId == null) {
			JSONObject jsonObject = new JSONObject();
			JSONArray activityClassifyJsonArray = new JSONArray();
			jsonObject.put("classifies", activityClassifyJsonArray);
			return RestRespDTO.success(jsonObject);
		}
		return RestRespDTO.success(searchAndPackageClassifies(fid, marketId));
	}

	/**查询并封装门户所需分类数据
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-26 15:40:15
	 * @param fid
	 * @param marketId
	 * @return com.alibaba.fastjson.JSONObject
	 */
	private  JSONObject searchAndPackageClassifies(Integer fid, Integer marketId) {
		List<Classify> classifies;
		if (marketId != null) {
			classifies = classifyQueryService.listMarketClassifies(marketId);
		} else {
			classifies = classifyQueryService.listByFids(Lists.newArrayList(fid));
		}

		JSONObject jsonObject = new JSONObject();
		JSONArray activityClassifyJsonArray = new JSONArray();
		jsonObject.put("classifies", activityClassifyJsonArray);
		if (CollectionUtils.isNotEmpty(classifies)) {
			for (Classify classify : classifies) {
				JSONObject item = new JSONObject();
				item.put("id", classify.getId());
				item.put("name", classify.getName());
				activityClassifyJsonArray.add(item);
			}
		}
		return jsonObject;
	}

}