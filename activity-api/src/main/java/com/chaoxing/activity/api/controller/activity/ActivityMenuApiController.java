package com.chaoxing.activity.api.controller.activity;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ActivityMenuConfig;
import com.chaoxing.activity.service.activity.menu.ActivityMenuQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**活动菜单服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityMenuApiController
 * @description
 * @blame wwb
 * @date 2022-04-12 15:08:06
 */
@Slf4j
@RestController
@RequestMapping("activity/{activityId}/menu")
public class ActivityMenuApiController {

	@Resource
	private ActivityMenuQueryService activityMenuQueryService;

	/**查询活动的所有菜单
	 * @Description 厦门项目
	 * @author wwb
	 * @Date 2022-04-12 15:11:58
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("list")
	public RestRespDTO listActivityMenu(@PathVariable Integer activityId) {
		List<ActivityMenuConfig> activityMenuConfigs = activityMenuQueryService.listEnableByActivityId(activityId);
		return RestRespDTO.success(activityMenuConfigs);
	}

}