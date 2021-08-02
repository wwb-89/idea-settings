package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketCreateParamDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketUpdateParamDTO;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
