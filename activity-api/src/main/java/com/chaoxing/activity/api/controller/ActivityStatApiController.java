package com.chaoxing.activity.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.activity.ActivityStatQueryService;
import com.chaoxing.activity.service.activity.module.ActivityModuleQueryService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**活动统计服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatApiController
 * @description
 * @blame wwb
 * @date 2021-01-13 17:47:56
 */
@RestController
@RequestMapping("activity/stat")
public class ActivityStatApiController {

	@Resource
	private ActivityScopeQueryService activityScopeQueryService;
	@Resource
	private ActivityModuleQueryService activityModuleQueryService;
	@Resource
	private ActivityStatQueryService activityStatQueryService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private WorkApiService workApiService;
	@Resource
	private UserStatSummaryQueryService userStatSummaryQueryService;

	/**统计报名成功人数
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 18:05:48
	 * @param fids
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("signed-up-num")
	public RestRespDTO countOrgParticipatedActivitySignedUpNum(@RequestBody List<Integer> fids) {
		// 查询活动列表
		List<Integer> signIds = activityScopeQueryService.listOrgParticipateSignId(fids);
		Integer signedUpNum = 0;
		if (CollectionUtils.isNotEmpty(signIds)) {
			signApiService.statSignedUpNum(signIds);
		}
		return RestRespDTO.success(signedUpNum);
	}

	/**机构参与活动作品征集提交作品数量
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 19:28:28
	 * @param fids
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("submited-work-num")
	public RestRespDTO countOrgParticipatedActivitySubmitWorkNum(@RequestBody List<Integer> fids) {
		List<Integer> workActivityIds = activityModuleQueryService.listWorkActivityId(fids);
		Integer workNum = workApiService.countActivitySubmitWorkNum(workActivityIds);
		return RestRespDTO.success(workNum);
	}

	/**机构参与的活动打卡id列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 19:28:12
	 * @param fids
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("punchIds")
	public RestRespDTO listOrgParticipatedActivityPunchId(@RequestBody List<Integer> fids) {
		List<Integer> punchIds = activityModuleQueryService.listPunchId(fids);
		return RestRespDTO.success(punchIds);
	}

	/**机构参与的活动pageId列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 19:29:10
	 * @param fids
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("pageIds")
	public RestRespDTO listOrgParticipatedActivityPageId(@RequestBody List<Integer> fids) {
		List<Integer> pageIds = activityStatQueryService.listOrgParticipatedActivityPageId(fids);
		return RestRespDTO.success(pageIds);
	}

	/**用户参与的活动数量
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-06 20:17:19
	 * @param uid 用户id
	 * @param fid 机构id（机构创建的活动）
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("num/user-participate")
	public RestRespDTO countUserParticipateActivityNum(@RequestParam Integer uid, Integer fid) {
		return RestRespDTO.success(userStatSummaryQueryService.countUserParticipateActivityNum(uid, fid));
	}

	/**用户参与的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-06 21:26:02
	 * @param request
	 * @param uid
	 * @param fid 机构创建的活动
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("user-participate")
	public RestRespDTO userParticipateActivity(HttpServletRequest request, @RequestParam Integer  uid, Integer fid) {
		Page page = HttpServletRequestUtils.buid(request);
		page = userStatSummaryQueryService.pagingUserParticipate(page, uid, fid);
		return RestRespDTO.success(page);
	}

}