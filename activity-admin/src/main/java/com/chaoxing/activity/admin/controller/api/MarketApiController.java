package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketCreateParamDTO;
import com.chaoxing.activity.service.activity.market.ActivityMarketHandleService;
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
	private ActivityMarketHandleService activityMarketHandleService;

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
		activityMarketHandleService.addFromWfw(activityMarketCreateParamDto, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

}
