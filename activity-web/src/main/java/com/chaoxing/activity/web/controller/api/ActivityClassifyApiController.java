package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyHandleService;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**活动分类服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyApiController
 * @description
 * @blame wwb
 * @date 2020-11-10 15:06:32
 */
@RestController
@RequestMapping("api/activity/classify")
public class ActivityClassifyApiController {

	@Resource
	private ActivityClassifyHandleService activityClassifyHandleService;

	/**新增活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-17 10:07:57
	 * @param request
	 * @param activityClassify
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("add")
	public RestRespDTO add(HttpServletRequest request, ActivityClassify activityClassify) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		ActivityClassify add = activityClassifyHandleService.add(activityClassify, loginUser);
		return RestRespDTO.success(add);
	}

	/**修改活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-17 10:08:27
	 * @param request
	 * @param activityClassify
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("edit")
	public RestRespDTO edit(HttpServletRequest request, ActivityClassify activityClassify) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityClassifyHandleService.edit(activityClassify, loginUser);
		return RestRespDTO.success();
	}

	/**删除活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-17 10:10:18
	 * @param request
	 * @param id
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("{id}/delete")
	public RestRespDTO delete(HttpServletRequest request, @PathVariable Integer id) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityClassifyHandleService.delete(id, loginUser);
		return RestRespDTO.success();
	}

}