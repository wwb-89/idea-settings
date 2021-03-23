package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.util.exception.ActivityNotExistException;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**活动验证服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityValidationService
 * @description
 * @blame wwb
 * @date 2020-11-10 15:57:22
 */
@Slf4j
@Service
public class ActivityValidationService {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;

	/**新增活动输入验证
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 16:22:48
	 * @param activity
	 * @return void
	*/
	public void addInputValidate(Activity activity) {
		String name = activity.getName();
		if (StringUtils.isEmpty(name)) {
			throw new BusinessException("活动名称不能为空");
		}
		LocalDateTime startTime = activity.getStartTime();
		if (startTime == null) {
			throw new BusinessException("活动开始时间不能为空");
		}
		LocalDateTime endTime = activity.getEndTime();
		if (endTime == null) {
			throw new BusinessException("活动结束时间不能为空");
		}
		if (startTime.isAfter(endTime)) {
			throw new BusinessException("活动开始时间不能晚于结束时间");
		}
		String coverCloudId = activity.getCoverCloudId();
		if (StringUtils.isEmpty(coverCloudId)) {
			throw new BusinessException("活动封面不能为空");
		}
		Integer activityClassifyId = activity.getActivityClassifyId();
		if (activityClassifyId == null) {
			throw new BusinessException("活动分类不能为空");
		}
	}

	/**活动必须存在
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 16:07:46
	 * @param activityId
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity activityExist(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		log.error("活动id:{}对应的活动不存在", activityId);
		Optional.ofNullable(activity).orElseThrow(() -> new ActivityNotExistException(activityId));
		return activity;
	}

	/**是不是活动的创建者
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-16 11:06:20
	 * @param activityId
	 * @param loginUser
	 * @return boolean
	*/
	public boolean isCreator(Integer activityId, LoginUserDTO loginUser) {
		Activity activity = activityQueryService.getById(activityId);
		return isCreator(activity, loginUser.getUid());
	}
	/**是不是活动创建者
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-01 16:17:22
	 * @param activity
	 * @param loginUser
	 * @return boolean
	*/
	public boolean isCreator(Activity activity, LoginUserDTO loginUser) {
		return isCreator(activity, loginUser.getUid());
	}
	
	/**是不是活动创建者
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-09 19:08:14
	 * @param activity
	 * @param uid
	 * @return boolean
	*/
	public boolean isCreator(Activity activity, Integer uid) {
		if (Objects.equals(activity.getCreateUid(), uid)) {
			return true;
		}
		return false;
	}

	/**机构是否在管理范围内
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-02 16:11:36
	 * @param fid
	 * @param loginUser
	 * @return boolean
	*/
	public boolean isOrgInManageScope(Integer fid, LoginUserDTO loginUser) {
		Integer loginUserFid = loginUser.getFid();
		List<Integer> manageFids = new ArrayList<>();
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByFid(loginUserFid);
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwRegionalArchitectureDTO::getFid).collect(Collectors.toList());
			manageFids.addAll(subFids);
		} else {
			manageFids.add(loginUserFid);
		}
		return manageFids.contains(fid);
	}

	/** 可管理活动
	 * @Description
	 * @author wwb
	 * @Date 2021-03-23 13:12:57
	 * @param activityId
	 * @param loginUser
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity manageAble(Integer activityId, LoginUserDTO loginUser) {
		return manageAble(activityId, loginUser, "");
	}
	/**可管理活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 10:28:29
	 * @param activityId
	 * @param loginUser
	 * @param errorMessage
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity manageAble(Integer activityId, LoginUserDTO loginUser, String errorMessage) {
		errorMessage = Optional.ofNullable(errorMessage).filter(StringUtils::isNotBlank).orElse("没有权限");
		Activity activity = activityExist(activityId);
		if (!isCreator(activity, loginUser)) {
			throw new BusinessException(errorMessage);
		}
		return activity;
	}

	/**能修改活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 16:07:52
	 * @param activityId
	 * @param loginUser
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity editAble(Integer activityId, LoginUserDTO loginUser) {
		Activity activity = activityExist(activityId);
		Integer createFid = activity.getCreateFid();
		// 是不是创建者
		boolean creator = isCreator(activity, loginUser);
		// 是不是本单位创建的活动
		boolean isCurrentOrgCreated = false;
		if (Objects.equals(createFid, loginUser.getFid())) {
			isCurrentOrgCreated = true;
		}
		if (!creator && !isCurrentOrgCreated) {
			throw new BusinessException("只能修改自己或本单位创建的活动");
		}
		return activity;
	}

	/**能删除活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 16:08:08
	 * @param activityId
	 * @param loginUser
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity deleteAble(Integer activityId, LoginUserDTO loginUser) {
		Activity activity = activityExist(activityId);
		Integer createFid = activity.getCreateFid();
		// 是不是创建者
		boolean creator = isCreator(activity, loginUser);
		// 是不是本单位创建的活动
		boolean isCurrentOrgCreated = false;
		if (Objects.equals(createFid, loginUser.getFid())) {
			isCurrentOrgCreated = true;
		}
		if (!creator && !isCurrentOrgCreated) {
			throw new BusinessException("只能删除自己或本单位创建的活动");
		}
		return activity;
	}

	/**可发布
	 * @Description 只能发布本单位的活动，自己创建的或者本单位创建的
	 * @author wwb
	 * @Date 2020-11-12 15:43:24
	 * @param activityId
	 * @param loginUser
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity releaseAble(Integer activityId, LoginUserDTO loginUser) {
		Activity activity = activityExist(activityId);
		Boolean released = activity.getReleased();
		if (released) {
			throw new BusinessException("活动已发布");
		}
		Integer createFid = activity.getCreateFid();
		// 是不是创建者
		boolean creator = isCreator(activity, loginUser);
		// 是不是本单位创建的活动
		boolean isCurrentOrgCreated = false;
		if (Objects.equals(createFid, loginUser.getFid())) {
			isCurrentOrgCreated = true;
		}
		if (!creator && !isCurrentOrgCreated) {
			throw new BusinessException("只能发布自己或本单位创建的活动");
		}
		return activity;
	}

	/**可更新发布范围
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-20 11:10:56
	 * @param activityId
	 * @param loginUser
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity updateReleaseAble(Integer activityId, LoginUserDTO loginUser) {
		Activity activity = activityExist(activityId);
		Boolean released = activity.getReleased();
		if (!released) {
			throw new BusinessException("活动未发布");
		}
		Integer createFid = activity.getCreateFid();
		// 是不是创建者
		boolean creator = isCreator(activity, loginUser);
		// 是不是本单位创建的活动
		boolean isCurrentOrgCreated = false;
		if (Objects.equals(createFid, loginUser.getFid())) {
			isCurrentOrgCreated = true;
		}
		if (!creator && !isCurrentOrgCreated) {
			throw new BusinessException("只能修改自己或本单位创建的活动的发布范围");
		}
		return activity;
	}
	
	/**可取消发布活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-12 15:48:44
	 * @param activityId
	 * @param loginUser
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity cancelReleaseAble(Integer activityId, LoginUserDTO loginUser) {
		Activity activity = activityExist(activityId);
		Boolean released = activity.getReleased();
		if (!released) {
			throw new BusinessException("活动已下架");
		}
		Integer createFid = activity.getCreateFid();
		// 是不是创建者
		boolean creator = isCreator(activity, loginUser);
		// 是不是本单位创建的活动
		boolean isCurrentOrgCreated = false;
		if (Objects.equals(createFid, loginUser.getFid())) {
			isCurrentOrgCreated = true;
		}
		boolean orgInManageScope = isOrgInManageScope(createFid, loginUser);
		if (!creator && !isCurrentOrgCreated && !orgInManageScope) {
			throw new BusinessException("只能下架自己、本单位或下级创建的活动");
		}
		return activity;
	}

}