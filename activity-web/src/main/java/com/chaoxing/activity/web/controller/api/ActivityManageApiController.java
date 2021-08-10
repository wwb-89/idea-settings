package com.chaoxing.activity.web.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**活动管理api服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityManageApiController
 * @description
 * @blame wwb
 * @date 2021-01-28 15:48:53
 */
@RestController
@RequestMapping("api/manage/activity")
public class ActivityManageApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityHandleService activityHandleService;

	/**查询管理的
	 * @Description
	 * @author wwb
	 * @Date 2021-01-27 21:02:25
	 * @param request
	 * @param sw
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@LoginRequired
	@RequestMapping("list")
	public RestRespDTO pageManaged(HttpServletRequest request, String sw) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Page page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.pageManaged(page, loginUser.getUid(), sw);
		return RestRespDTO.success(page);
	}

	/**发布
	 * @Description
	 * @author wwb
	 * @Date 2021-01-28 15:47:15
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@LoginRequired
	@RequestMapping("{activityId}/release")
	public RestRespDTO release(HttpServletRequest request, @PathVariable Integer activityId, Integer marketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.release(activityId, marketId, loginUser);
		return RestRespDTO.success();
	}

	/**下架
	 * @Description
	 * @author wwb
	 * @Date 2021-01-28 15:47:23
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@LoginRequired
	@RequestMapping("{activityId}/release/cancel")
	public RestRespDTO cancelRelease(HttpServletRequest request, @PathVariable Integer activityId, Integer marketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.cancelRelease(activityId, marketId, loginUser);
		return RestRespDTO.success();
	}

	/**删除活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-09 11:37:39
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("{activityId}/delete")
	public RestRespDTO delete(HttpServletRequest request, @PathVariable Integer activityId, Integer marketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.delete(activityId, marketId, loginUser);
		return RestRespDTO.success();
	}

}