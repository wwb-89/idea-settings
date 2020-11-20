package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
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
	private ActivityMapper activityMapper;

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
		LocalDate startDate = activity.getStartDate();
		if (startDate == null) {
			throw new BusinessException("活动开始时间不能为空");
		}
		LocalDate endDate = activity.getEndDate();
		if (endDate == null) {
			throw new BusinessException("活动结束时间不能为空");
		}
		if (startDate.isAfter(endDate)) {
			throw new BusinessException("活动开始时间不能晚于结束时间");
		}
		String coverCloudId = activity.getCoverCloudId();
		if (StringUtils.isEmpty(coverCloudId)) {
			throw new BusinessException("活动封面不能为空");
		}
		String address = activity.getAddress();
		if (StringUtils.isEmpty(address)) {
			throw new BusinessException("活动位置不能为空");
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
		Activity activity = activityMapper.selectById(activityId);
		Optional.ofNullable(activity).orElseThrow(() -> new BusinessException("活动不存在"));
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
		if (!Objects.equals(activity.getCreateUid(), loginUser.getUid())) {
			throw new BusinessException("活动创建者才能修改活动");
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
		if (!Objects.equals(activity.getCreateUid(), loginUser.getUid())) {
			throw new BusinessException("活动创建者才能删除活动");
		}
		return activity;
	}

	/**可发布
	 * @Description 
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
		Integer createUid = activity.getCreateUid();
		if (!Objects.equals(createUid, loginUser.getUid())) {
			throw new BusinessException("活动创建者才能发布活动");
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
		Integer createUid = activity.getCreateUid();
		if (!Objects.equals(createUid, loginUser.getUid())) {
			throw new BusinessException("活动创建者才能修改发布范围");
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
		Integer createUid = activity.getCreateUid();
		if (!Objects.equals(createUid, loginUser.getUid())) {
			throw new BusinessException("活动创建者才能下架活动");
		}
		return activity;
	}

}