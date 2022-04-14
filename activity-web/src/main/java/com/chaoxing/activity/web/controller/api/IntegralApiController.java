package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.queue.IntegralPushQueue;
import com.chaoxing.activity.util.enums.IntegralOriginTypeEnum;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**积分api
 * @author wwb
 * @version ver 1.0
 * @className IntegralApiController
 * @description
 * @blame wwb
 * @date 2020-12-24 16:54:36
 */
@RestController
@RequestMapping("api/integral")
public class IntegralApiController {

	@Resource
	private IntegralPushQueue integralPushQueueService;

	/**活动访问积分推送
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 16:57:27
	 * @param request
	 * @param activityId
	 * @param activityName
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("push/activity/{activityId}/view")
	public RestRespDTO integralPush(HttpServletRequest request, @PathVariable Integer activityId, String activityName) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Optional.ofNullable(loginUser).ifPresent(v -> {
			Integer uid = loginUser.getUid();
			Integer fid = loginUser.getFid();
			IntegralPushQueue.IntegralPushDTO integralPush = new IntegralPushQueue.IntegralPushDTO(uid, fid, IntegralOriginTypeEnum.VIEW_ACTIVITY.getValue(), String.valueOf(activityId), activityName);
			integralPushQueueService.push(integralPush);
		});
		return RestRespDTO.success();
	}

}
