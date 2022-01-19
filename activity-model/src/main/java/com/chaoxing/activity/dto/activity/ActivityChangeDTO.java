package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.model.Activity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**活动改变
 * @author wwb
 * @version ver 1.0
 * @className ActivityChangeDTO
 * @description
 * @blame wwb
 * @date 2022-01-19 14:27:37
 */
@Data
public class ActivityChangeDTO {

	/** 旧活动 */
	private Activity oldActivity;
	/** 新活动 */
	private Activity newActivity;
	/** 是否新增 */
	private Boolean add;
	/** 名称改变 */
	private Boolean nameChanged;
	/** 开始时间改变 */
	private Boolean startTimeChanged;
	/** 结束时间改变 */
	private Boolean endTimeChanged;
	/** 时间改变 */
	private Boolean timeChanged;
	/** 封面改变 */
	private Boolean coverChanged;
	/** 积分改变 */
	private Boolean integralChanged;
	/** 学时改变 */
	private Boolean periodChanged;
	/** 学分改变 */
	private Boolean creditChanged;
	/** 地点改变 */
	private Boolean addressChanged;
	/** 网页模版改变 */
	private Boolean webTemplateChanged;

	private ActivityChangeDTO() {

	}

	public static ActivityChangeDTO build(Activity oldActivity, Activity newActivity) {
		ActivityChangeDTO activityChange = new ActivityChangeDTO();
		activityChange.setOldActivity(oldActivity);
		activityChange.setNewActivity(newActivity);
		activityChange.setAdd(oldActivity == null);
		activityChange.setNameChanged(nameChanged(oldActivity, newActivity));
		activityChange.setStartTimeChanged(startTimeChanged(oldActivity, newActivity));
		activityChange.setEndTimeChanged(endTimeChanged(oldActivity, newActivity));
		activityChange.setTimeChanged(timeChanged(oldActivity, newActivity));
		activityChange.setCoverChanged(coverChanged(oldActivity, newActivity));
		activityChange.setIntegralChanged(integralChanged(oldActivity, newActivity));
		activityChange.setPeriodChanged(periodChanged(oldActivity, newActivity));
		activityChange.setCreditChanged(creditChanged(oldActivity, newActivity));
		activityChange.setAddressChanged(addressChanged(oldActivity, newActivity));
		activityChange.setWebTemplateChanged(webTemplateChanged(oldActivity, newActivity));
		return activityChange;
	}

	private static boolean nameChanged(Activity oldActivity, Activity newActivity) {
		if (oldActivity == null) {
			return true;
		}
		return !Objects.equals(oldActivity.getName(), newActivity.getName());
	}

	private static boolean startTimeChanged(Activity oldActivity, Activity newActivity) {
		if (oldActivity == null) {
			return true;
		}
		return newActivity.getStartTime().compareTo(oldActivity.getStartTime()) != 0;
	}

	private static boolean endTimeChanged(Activity oldActivity, Activity newActivity) {
		if (oldActivity == null) {
			return true;
		}
		return newActivity.getEndTime().compareTo(oldActivity.getEndTime()) != 0;
	}

	private static boolean timeChanged(Activity oldActivity, Activity newActivity) {
		return startTimeChanged(oldActivity, newActivity) || endTimeChanged(oldActivity, newActivity);
	}

	private static boolean coverChanged(Activity oldActivity, Activity newActivity) {
		if (oldActivity == null) {
			return true;
		}
		return !Objects.equals(oldActivity.getCoverCloudId(), newActivity.getCoverCloudId());
	}

	private static boolean integralChanged(Activity oldActivity, Activity newActivity) {
		if (oldActivity == null) {
			return true;
		}
		BigDecimal newIntegral = Optional.ofNullable(newActivity.getIntegral()).orElse(BigDecimal.ZERO);
		BigDecimal oldIntegral = Optional.ofNullable(oldActivity.getIntegral()).orElse(BigDecimal.ZERO);
		return newIntegral.compareTo(oldIntegral) != 0;
	}

	private static boolean periodChanged(Activity oldActivity, Activity newActivity) {
		if (oldActivity == null) {
			return true;
		}
		BigDecimal newPeriod = Optional.ofNullable(newActivity.getPeriod()).orElse(BigDecimal.ZERO);
		BigDecimal oldPeriod = Optional.ofNullable(oldActivity.getPeriod()).orElse(BigDecimal.ZERO);
		return newPeriod.compareTo(oldPeriod) != 0;
	}

	private static boolean creditChanged(Activity oldActivity, Activity newActivity) {
		if (oldActivity == null) {
			return true;
		}
		BigDecimal newCredit = Optional.ofNullable(newActivity.getCredit()).orElse(BigDecimal.ZERO);
		BigDecimal oldCredit = Optional.ofNullable(oldActivity.getCredit()).orElse(BigDecimal.ZERO);
		return newCredit.compareTo(oldCredit) != 0;
	}

	private static boolean addressChanged(Activity oldActivity, Activity newActivity) {
		if (oldActivity == null) {
			return true;
		}
		String newAddress = Optional.ofNullable(newActivity.getAddress()).orElse("") + Optional.ofNullable(newActivity.getDetailAddress()).orElse("");
		String oldAddress = Optional.ofNullable(oldActivity.getAddress()).orElse("") + Optional.ofNullable(oldActivity.getDetailAddress()).orElse("");
		return !Objects.equals(newAddress, oldAddress);
	}

	private static boolean webTemplateChanged(Activity oldActivity, Activity newActivity) {
		if (oldActivity == null) {
			return true;
		}
		Integer newPageId = newActivity.getPageId();
		Integer oldPageId = oldActivity.getPageId();
		return !Objects.equals(newPageId, oldPageId);
	}

}
