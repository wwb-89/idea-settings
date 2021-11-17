package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketCreateParamDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketUpdateParamDTO;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.market.MarketSignupConfigService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**活动市场api
 * @author wwb
 * @version ver 1.0
 * @className MarketApiController
 * @description
 * @blame wwb
 * @date 2021-07-16 16:26:19
 */
@RestController
@RequestMapping("api/market")
public class MarketApiController {

	@Resource
	private MarketHandleService marketHandleService;
	@Resource
	private ActivityHandleService activityHandleService;
	@Resource
	private MarketSignupConfigService marketSignupConfigService;

	/**创建活动市场（来源：微服务）
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-16 17:39:57
	 * @param request
	 * @param activityMarketCreateParamDto
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("new/from-wfw")
	public RestRespDTO newFromWfw(HttpServletRequest request, ActivityMarketCreateParamDTO activityMarketCreateParamDto) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		marketHandleService.addFromWfw(activityMarketCreateParamDto, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**更新活动市场（来源：微服务）
	 * @Description
	 * @author wwb
	 * @Date 2021-07-21 14:55:44
	 * @param request
	 * @param activityMarketUpdateParamDto
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("update/from-wfw")
	public RestRespDTO updateFromWfw(HttpServletRequest request, ActivityMarketUpdateParamDTO activityMarketUpdateParamDto) {
		marketHandleService.updateFromWfw(activityMarketUpdateParamDto);
		return RestRespDTO.success();
	}

	/**更新同时报名活动数量限制
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-18 16:17:43
	 * @param request
	 * @param marketId
	 * @param signUpActivityLimit
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("{marketId}/update/sign-up-activity-limit")
	public RestRespDTO updateSignUpActivityLimit(HttpServletRequest request, @PathVariable Integer marketId, @RequestParam Integer signUpActivityLimit) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		marketSignupConfigService.updateSignUpActivityLimit(marketId, signUpActivityLimit, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/** 更新活动市场报名配置的报名按钮名称
	 * @className MarketApiController
	 * @description 
	 * @author wwb
	 * @blame wwb
	 * @date 2021-11-17 16:13:38
	 * @version ver 1.0
	 */
	@LoginRequired
	@RequestMapping("{marketId}/update/sign-up-btn-name")
	public RestRespDTO updateSignUpBtnName(HttpServletRequest request, @PathVariable Integer marketId, @RequestParam String btnName, @RequestParam String keyword) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		marketSignupConfigService.updateSignUpBtnName(marketId, btnName, keyword, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**活动市场发布活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-22 17:29:49
	 * @param request
	 * @param marketId
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{marketId}/release/activity/{activityId}")
	public RestRespDTO releaseActivity(HttpServletRequest request, @PathVariable Integer marketId, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.releaseMarketActivity(activityId, marketId, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**活动市场取消发布活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-22 17:30:07
	 * @param request
	 * @param marketId
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{marketId}/cancel-release/activity/{activityId}")
	public RestRespDTO cancelReleaseActivity(HttpServletRequest request, @PathVariable Integer marketId, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.cancelReleaseMarketActivity(activityId, marketId, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

}
