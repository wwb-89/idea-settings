package com.chaoxing.activity.admin.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ActivityClassifyNew;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyNewHandlerService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyNewQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

/**活动类型api服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyNewApiController
 * @description
 * @blame wwb
 * @date 2021-04-12 09:53:22
 */
@RestController
@RequestMapping("api/activity/classify/new")
public class ActivityClassifyNewApiController {

	@Resource
	private ActivityClassifyNewQueryService activityClassifyNewQueryService;
	@Resource
	private ActivityClassifyNewHandlerService activityClassifyNewHandlerService;

	/**分页查询活动类型
	 * @Description
	 * @author wwb
	 * @Date 2021-04-12 11:33:48
	 * @param request
	 * @param activityMarketId 活动市场id
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("list")
	public RestRespDTO paging(HttpServletRequest request, @RequestParam Integer activityMarketId) {
		Page<ActivityClassifyNew> page = HttpServletRequestUtils.buid(request);
		page = activityClassifyNewQueryService.paging(page, activityMarketId);
		return RestRespDTO.success(page);
	}

	/**新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:06:15
	 * @param request
	 * @param activityClassifyNew
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("add")
	public RestRespDTO add(HttpServletRequest request, ActivityClassifyNew activityClassifyNew) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityClassifyNewHandlerService.add(activityClassifyNew, loginUser);
		return RestRespDTO.success();
	}

	/**修改
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:06:23
	 * @param request
	 * @param activityClassifyNew
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("edit")
	public RestRespDTO edit(HttpServletRequest request, ActivityClassifyNew activityClassifyNew) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityClassifyNewHandlerService.update(activityClassifyNew, loginUser);
		return RestRespDTO.success();
	}

	/**删除
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:06:33
	 * @param request
	 * @param activityClassifyId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityClassifyId}/delete")
	public RestRespDTO delete(HttpServletRequest request, @PathVariable Integer activityClassifyId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityClassifyNewHandlerService.delete(activityClassifyId, loginUser);
		return RestRespDTO.success();
	}

	/**批量删除
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:06:44
	 * @param request
	 * @param activityClassifyIds
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("delete/batch")
	public RestRespDTO batchDelete(HttpServletRequest request, @RequestParam(value = "activityClassifyIds[]") Integer[] activityClassifyIds) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityClassifyNewHandlerService.batchDelete(new ArrayList<>(Arrays.asList(activityClassifyIds)), loginUser);
		return RestRespDTO.success();
	}


}