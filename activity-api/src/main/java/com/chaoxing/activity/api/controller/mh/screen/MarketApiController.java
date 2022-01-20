package com.chaoxing.activity.api.controller.mh.screen;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**大屏端活动市场api服务
 * @author wwb
 * @version ver 1.0
 * @className MarketApiController
 * @description
 * @blame wwb
 * @date 2022-01-19 16:46:05
 */
@Slf4j
@RestController
@RequestMapping("mh/screen/market")
public class MarketApiController {

	@Resource
	private ActivityStatSummaryQueryService activityStatSummaryQueryService;

	@RequestMapping("from-wfw-form/{formId}/num/signed-up")
	public RestRespDTO signedUpNum(@PathVariable Integer formId) {
		Integer signedUpNum = activityStatSummaryQueryService.countWfwFormMarketActivitySignedUpNum(formId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("curPage", 1);
		jsonObject.put("totalPages", 1);
		jsonObject.put("totalRecords", 1);
		JSONArray activityJsonArray = new JSONArray();
		jsonObject.put("results", activityJsonArray);
		JSONObject activity = new JSONObject();
		JSONArray fields = new JSONArray();
		activity.put("fields", fields);

		JSONObject num = new JSONObject();
		num.put("flag", "1");
		num.put("key", "报名数量");
		num.put("value", signedUpNum);
		fields.add(num);
		activityJsonArray.add(activity);
		return RestRespDTO.success(jsonObject);
	}

}
