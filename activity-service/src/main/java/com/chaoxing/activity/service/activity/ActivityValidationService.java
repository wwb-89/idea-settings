package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.manager.ActivityManagerValidationService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.util.exception.ActivityNotExistException;
import com.chaoxing.activity.util.exception.ActivityReleasedException;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private ActivityManagerValidationService activityManagerValidationService;

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
		// 定时发布活动
		Boolean timingRelease = activity.getTimingRelease();
		timingRelease = Optional.ofNullable(timingRelease).orElse(false);
		LocalDateTime timingReleaseTime = activity.getTimingReleaseTime();
		if (timingRelease) {
			if (timingReleaseTime == null) {
				throw new BusinessException("活动发布时间不能为空");
			}
		} else {
			timingReleaseTime = null;
		}
		activity.setTimingRelease(timingRelease);
		activity.setTimingReleaseTime(timingReleaseTime);
		String originType = activity.getOriginType();
		if (StringUtils.isBlank(originType)) {
			activity.setOriginType(Activity.OriginTypeEnum.NORMAL.getValue());
		}
		// 活动形式
		String activityType = activity.getActivityType();
		Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromValue(activityType);
		if (Activity.ActivityTypeEnum.ONLINE.equals(activityTypeEnum)) {
			activity.setAddress(null);
			activity.setLongitude(null);
			activity.setDimension(null);
		}
	}

	/**更新活动的输入验证
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-13 18:32:58
	 * @param activity
	 * @return void
	*/
	public void updateInputValidate(Activity activity) {
		Integer activityId = activity.getId();
		Optional.ofNullable(activityId).orElseThrow(() -> new BusinessException("活动id不能为空"));
		addInputValidate(activity);
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
		Optional.ofNullable(activity).orElseThrow(() -> new ActivityNotExistException(activityId));
		return activity;
	}

	/**活动是否存在
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-10 15:18:04
	 * @param activityId
	 * @return boolean
	*/
	public boolean isActivityExist(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		return activity != null;
	}

	/**是不是活动的创建者
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-16 11:06:20
	 * @param activityId
	 * @param uid
	 * @return boolean
	*/
	public boolean isCreator(Integer activityId, Integer uid) {
		Activity activity = activityQueryService.getById(activityId);
		return isCreator(activity, uid);
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

	/**活动创建者
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-08 15:40:56
	 * @param activityId
	 * @param uid
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity creator(Integer activityId, Integer uid) {
		Activity activity = activityQueryService.getById(activityId);
		if (!isCreator(activity, uid)) {
			throw new BusinessException("无权限");
		}
		return activity;
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
		List<Integer> manageFids = wfwAreaApiService.listSubFid(loginUserFid);
		return manageFids.contains(fid);
	}

	/**是否能管理活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-29 09:49:49
	 * @param activityId
	 * @param uid
	 * @return boolean
	*/
	public boolean isManageAble(Integer activityId, Integer uid) {
		Activity activity = activityQueryService.getById(activityId);
		return isManageAble(activity, uid);
	}

	/**是否能管理活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-29 09:51:58
	 * @param activity
	 * @param uid
	 * @return boolean
	*/
	public boolean isManageAble(Activity activity, Integer uid) {
		if (activity != null) {
			boolean creator = isCreator(activity, uid);
			if (creator) {
				return true;
			} else {
				return activityManagerValidationService.isManager(activity.getId(), uid);
			}
		}
		return false;
	}

	/**可管理活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-29 09:55:31
	 * @param activityId
	 * @param uid
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity manageAble(Integer activityId, Integer uid) {
		Activity activity = activityQueryService.getById(activityId);
		boolean isManager = isManageAble(activity, uid);
		if (!isManager) {
			throw new BusinessException("无权限");
		}
		return activity;
	}

	/**可管理活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-29 09:55:52
	 * @param activity
	 * @param uid
	 * @return com.chaoxing.activity.model.Activity
	*/
	public Activity manageAble(Activity activity, Integer uid) {
		if (!isManageAble(activity, uid)) {
			throw new BusinessException("无权限");
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
		boolean creator = isCreator(activity, loginUser.getUid());
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
		return deleteAble(activityExist(activityId), loginUser);
	}

	public Activity deleteAble(Activity activity, LoginUserDTO loginUser) {
		Integer createFid = activity.getCreateFid();
		// 是不是创建者
		boolean creator = isCreator(activity, loginUser.getUid());
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
		return releaseAble(activity, loginUser);
	}

	public Activity releaseAble(Activity activity, LoginUserDTO loginUser) {
		Boolean released = activity.getReleased();
		if (released) {
			throw new ActivityReleasedException(activity.getId());
		}
		Integer createFid = activity.getCreateFid();
		// 是不是创建者
		boolean manager = isManageAble(activity, loginUser.getUid());
		// 是不是本单位创建的活动
		boolean isCurrentOrgCreated = false;
		Integer fid = loginUser.getFid();
		if (Objects.equals(createFid, fid)) {
			isCurrentOrgCreated = true;
		}
		// 活动是不是下级机构创建的
		List<Integer> subFids = wfwAreaApiService.listSubFid(fid);
		if (!manager && !isCurrentOrgCreated && !subFids.contains(createFid)) {
			// 自己是活动的管理员、是本机构创建的、是自己机构的下级机构创建的
			throw new BusinessException("无权限");
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
		boolean creator = isCreator(activity, loginUser.getUid());
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
		return cancelReleaseAble(activity, loginUser);
	}

	public Activity cancelReleaseAble(Activity activity, LoginUserDTO loginUser) {
		Boolean released = activity.getReleased();
		if (!released) {
			throw new BusinessException("活动已下架");
		}
		Integer createFid = activity.getCreateFid();
		// 是不是管理者
		boolean manager = isManageAble(activity, loginUser.getUid());
		// 是不是本单位创建的活动
		boolean isCurrentOrgCreated = false;
		if (Objects.equals(createFid, loginUser.getFid())) {
			isCurrentOrgCreated = true;
		}
		boolean orgInManageScope = isOrgInManageScope(createFid, loginUser);
		if (!manager && !isCurrentOrgCreated && !orgInManageScope) {
			throw new BusinessException("只能下架自己管理的、本单位或下级创建的活动");
		}
		return activity;
	}

}