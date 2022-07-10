package com.chaoxing.activity.api.controller.custom;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**活动参与角色通知服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityParticipateRoleNoticeApiController
 * @description 厦门定制（所有的业务都放在改controller）
 * @blame wwb
 * @date 2022-02-16 15:29:09
 */
@Slf4j
@RestController
@RequestMapping("custom/activity/notice/participate-role")
public class ActivityParticipateRoleNoticeApiController {

	private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "custom" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_participate_role_notice";

	@Resource
	private ActivityQueryService activityQueryService;

	@Resource
	private RedissonClient redissonClient;

	/**接收触发给活动能报名的角色发送通知
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-16 16:03:19
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	public RestRespDTO receive(Integer activityId) {
		
		return RestRespDTO.success();
	}

}