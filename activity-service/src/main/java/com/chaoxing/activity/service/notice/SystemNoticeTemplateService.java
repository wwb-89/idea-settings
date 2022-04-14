package com.chaoxing.activity.service.notice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.notice.NoticeTemplateFieldDTO;
import com.chaoxing.activity.mapper.SystemNoticeTemplateMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.BlacklistRule;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.chaoxing.activity.service.blacklist.BlacklistQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**系统通知模版服务
 * @author wwb
 * @version ver 1.0
 * @className SystemNoticeTemplate
 * @description
 * @blame wwb
 * @date 2021-11-11 14:24:31
 */
@Slf4j
@Service
public class SystemNoticeTemplateService {

	@Resource
	private SystemNoticeTemplateMapper systemNoticeTemplateMapper;
	@Resource
	private BlacklistQueryService blacklistQueryService;
	@Resource
	private SignApiService signApiService;

	/**查询所有的系统通知模版
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-11 15:14:38
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.model.SystemNoticeTemplate>
	*/
	public List<SystemNoticeTemplate> list() {
		return systemNoticeTemplateMapper.selectList(new LambdaQueryWrapper<SystemNoticeTemplate>()
				.eq(SystemNoticeTemplate::getDeleted, false)
				.orderByAsc(SystemNoticeTemplate::getSequence)
		);
	}

	/**根据通知类型查询系统通知模版
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-11 22:09:25
	 * @param noticeType
	 * @return com.chaoxing.activity.model.SystemNoticeTemplate
	*/
	public SystemNoticeTemplate getByNoticeType(String noticeType) {
		List<SystemNoticeTemplate> systemNoticeTemplates = systemNoticeTemplateMapper.selectList(new LambdaQueryWrapper<SystemNoticeTemplate>()
				.eq(SystemNoticeTemplate::getNoticeType, noticeType)
				.eq(SystemNoticeTemplate::getDeleted, false)
		);
		return Optional.ofNullable(systemNoticeTemplates).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

	/**构造通知字段
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-16 17:32:20
	 * @param activity
	 * @return com.chaoxing.activity.dto.notice.NoticeFieldDTO
	*/
	public NoticeTemplateFieldDTO buildNoticeField(Activity activity) {
		String activityName = activity.getName();
		String address = activity.getActivityFullAddress();
		String activityTime = activity.getStartTime().format(CommonConstant.NOTICE_ACTIVITY_TIME_FORMATTER) + "~" + activity.getEndTime().format(CommonConstant.NOTICE_ACTIVITY_TIME_FORMATTER);
		String activityOrganisers = StringUtils.isNotBlank(activity.getOrganisers()) ? activity.getOrganisers() : activity.getCreateOrgName();
		Integer signId = activity.getSignId();
		SignCreateParamDTO signCreateParam = signApiService.getCreateById(signId);
		List<SignUpCreateParamDTO> signUps = Optional.ofNullable(signCreateParam).map(SignCreateParamDTO::getSignUps).orElse(Lists.newArrayList());
		List<NoticeTemplateFieldDTO.SignUpNoticeTemplateFieldDTO> signUpNoticeTemplateFields = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(signUps)) {
			for (SignUpCreateParamDTO signUp : signUps) {
				String name = signUp.getName();
				String time = DateUtils.timestamp2Date(signUp.getStartTime()).format(CommonConstant.NOTICE_SIGN_UP_TIME_FORMATTER) + "~" + DateUtils.timestamp2Date(signUp.getEndTime()).format(CommonConstant.NOTICE_SIGN_UP_TIME_FORMATTER);
				signUpNoticeTemplateFields.add(NoticeTemplateFieldDTO.SignUpNoticeTemplateFieldDTO.builder()
						.name(name)
						.time(time)
						.build());
			}
		}
		NoticeTemplateFieldDTO noticeTemplateField = NoticeTemplateFieldDTO.builder()
				.activityName(activityName)
				.address(address)
				.activityTime(activityTime)
				.previewUrl(activity.getPreviewUrl())
				.signUps(signUpNoticeTemplateFields)
				.activityOrganisers(activityOrganisers)
				.build();
		Integer marketId = activity.getMarketId();
		if (marketId != null) {
			BlacklistRule blacklistRule = blacklistQueryService.getBlacklistRuleByMarketId(marketId);
			noticeTemplateField.setEnableAutoRemove(Optional.ofNullable(blacklistRule).map(BlacklistRule::getEnableAutoRemove).orElse(false));
			noticeTemplateField.setAutoRemoveHours(Optional.ofNullable(blacklistRule).map(BlacklistRule::getAutoRemoveHours).orElse(null));
		}
		return noticeTemplateField;
	}

}