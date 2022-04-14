package com.chaoxing.activity.service.user.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.UserGradeDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.dto.manager.sign.SignDTO;
import com.chaoxing.activity.dto.manager.sign.SignInDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignInCreateParamDTO;
import com.chaoxing.activity.mapper.UserActionRecordMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.performance.PerformanceService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingQueryService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**用户行为记录查询服务
 * @author wwb
 * @version ver 1.0
 * @className UserActionRecordQueryService
 * @description
 * @blame wwb
 * @date 2021-06-17 14:07:42
 */
@Slf4j
@Service
public class UserActionRecordQueryService {

	@Resource
	private UserActionRecordMapper userActionRecordMapper;
	@Resource
	private PassportApiService passportApiService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private UserResultQueryService userResultQueryService;
	@Resource
	private ActivityRatingQueryService activityRatingQueryService;
	@Resource
	private PerformanceService performanceService;
	@Resource
	private SignApiService signApiService;

	/**查询用户在活动下有效的行为记录
	 * @Description
	 * @author wwb
	 * @Date 2021-06-24 15:16:14
	 * @param uid
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.model.UserActionRecord>
	 */
	public List<UserActionRecord> listUserValidActionRecord(Integer uid, Integer activityId) {
		return userActionRecordMapper.selectList(new QueryWrapper<UserActionRecord>()
				.lambda()
				.eq(UserActionRecord::getUid, uid)
				.eq(UserActionRecord::getActivityId, activityId)
				.eq(UserActionRecord::getValid, true)
				.orderByDesc(UserActionRecord::getCreateTime)
		);
	}

	/**获取用户在活动下的行为
	* @Description
	* @author huxiaolong
	* @Date 2021-06-25 18:51:11
	* @param uid
	* @param activityId
	* @return com.chaoxing.activity.dto.UserGradeDTO
	*/
	public UserGradeDTO getUserGrade(Integer uid, Integer activityId) {
		PassportUserDTO passportUserDTO =  passportApiService.getByUid(uid);
		UserGradeDTO userGrade = UserGradeDTO.builder()
				.uid(uid)
				.realName(passportUserDTO.getRealName())
				.build();

		Activity activity = activityQueryService.getById(activityId);

		userGrade.setActivityName(activity.getName());
		UserResult userResult = userResultQueryService.getUserResult(uid, activityId);
		if (userResult != null) {
			userGrade.setTotalScore(userResult.getTotalScore());
		}
		List<UserActionRecord> userActionRecords = listUserValidActionRecord(uid, activityId);

		// 获取报名签到信息
		SignDTO sign = signApiService.getById(activity.getSignId());
		List<SignUpDTO> signUps = sign.getSignUps();
		List<SignInDTO> signIns = sign.getSignIns();

		Map<Integer, SignUpDTO> signUpMap = signUps.stream().collect(Collectors.toMap(SignUpDTO::getId, v -> v, (v1, v2) -> v2));
		Map<Integer, SignInDTO> signInMap = signIns.stream().collect(Collectors.toMap(SignInDTO::getId, v -> v, (v1, v2) -> v2));


		List<Integer> ratingDetailIds = Lists.newArrayList();
		List<Integer> performanceIds = Lists.newArrayList();
		for (UserActionRecord record : userActionRecords) {
			String actionIdentify = record.getActionIdentify();
			if (StringUtils.isNotBlank(actionIdentify)) {
				Integer id = Integer.valueOf(actionIdentify);
				if (Objects.equals(record.getActionType(), UserActionTypeEnum.RATING.getValue())) {
					ratingDetailIds.add(id);
				} else if (Objects.equals(record.getActionType(), UserActionTypeEnum.PERFORMANCE.getValue())) {
					performanceIds.add(id);
				}
			}
		}
		// 获取评价
		List<ActivityRatingDetail> ratingDetails = activityRatingQueryService.listAllDetailByDetailIds(activityId, ratingDetailIds);
		Map<Integer, ActivityRatingDetail> ratingDetailMap = ratingDetails.stream().collect(Collectors.toMap(ActivityRatingDetail::getId, v -> v, (v1, v2) -> v2));
		List<Performance> performances = performanceService.listAllPerformanceByIds(activityId, performanceIds);
		Map<Integer, Performance> performanceMap = performances.stream().collect(Collectors.toMap(Performance::getId, v -> v, (v1, v2) -> v2));

		for (UserActionRecord record : userActionRecords) {
			UserActionTypeEnum userActionType = UserActionTypeEnum.fromValue(record.getActionType());
			String actionIdentify = record.getActionIdentify();
			if (userActionType != null && StringUtils.isNotBlank(actionIdentify)) {
				Integer identityId = Integer.valueOf(actionIdentify);
				record.setTitle(userActionType.getName());
				record.setName(userActionType.getName());
				switch (userActionType) {
					case SIGN_IN:
						SignInDTO signIn = signInMap.get(identityId);
						record.setWay(Optional.ofNullable(signIn).map(v -> SignInCreateParamDTO.Way.fromValue(v.getWay())).map(SignInCreateParamDTO.Way::getValue).orElse(null));
						record.setName(Optional.ofNullable(signIn).map(SignInDTO::getName).orElse(""));
						break;
					case SIGN_UP:
						record.setName(Optional.ofNullable(signUpMap.get(identityId)).map(SignUpDTO::getName).orElse(""));
						break;
					case RATING:
						record.setRatingDetail(ratingDetailMap.get(identityId));
						break;
					case PERFORMANCE:
						record.setRemark(Optional.ofNullable(performanceMap.get(identityId)).map(Performance::getRemark).orElse(""));
						break;
					case DISCUSS:
					case WORK:
					default:
						// 未知行为
				}
			}
		}
		userGrade.setUserActionRecords(userActionRecords);
		return userGrade;
	}
}
