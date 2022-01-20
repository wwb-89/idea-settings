package com.chaoxing.activity.api.controller.mh.screen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.api.util.MhPreParamsUtils;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.ActivityRankDTO;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**湖北群艺馆大屏端api服务
 * @author wwb
 * @version ver 1.0
 * @className HbqygApiController
 * @description
 * @blame wwb
 * @date 2022-01-19 16:17:07
 */
@Slf4j
@RestController
@RequestMapping("mh/screen/hbqyg")
public class HbqygApiController {

	@Resource
	private ActivityStatSummaryQueryService activityStatSummaryQueryService;

	@RequestMapping("market/{marketId}/rank/pv")
	public RestRespDTO pvRank(@PathVariable Integer marketId, @RequestBody String data) {
		JSONObject params = JSON.parseObject(data);
		String preParams = params.getString("preParams");
		JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
		// 状态
		Integer limit = urlParams.getInteger("limit");
		List<ActivityRankDTO> activityRanks = activityStatSummaryQueryService.marketActivityPvRank(marketId, limit);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("curPage", 1);
		jsonObject.put("totalPages", 1);
		jsonObject.put("totalRecords", activityRanks.size());
		JSONArray activityJsonArray = new JSONArray();
		jsonObject.put("results", activityJsonArray);
		if (CollectionUtils.isNotEmpty(activityRanks)) {
			for (ActivityRankDTO activityRank : activityRanks) {
				JSONObject activity = new JSONObject();
				activity.put("id", activityRank.getId());
				activity.put("type", 3);
				activity.put("orsUrl", "");
				JSONArray fields = new JSONArray();
				activity.put("fields", fields);
				// 活动名称
				JSONObject name = new JSONObject();
				name.put("flag", "1");
				name.put("key", "活动名称");
				name.put("value", activityRank.getName());
				fields.add(name);

				JSONObject num = new JSONObject();
				num.put("flag", "1");
				num.put("key", "pv");
				num.put("value", activityRank.getNum());
				fields.add(num);
				activityJsonArray.add(activity);
			}
		}
		return RestRespDTO.success(jsonObject);
	}

}