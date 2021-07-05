package com.chaoxing.activity.service.org;

import com.chaoxing.activity.dto.OrgFormConfigDTO;
import com.chaoxing.activity.model.OrgConfig;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.manager.module.SecondClassroomApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigHandleService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

/**机构表单配置服务
 * @author wwb
 * @version ver 1.0
 * @className OrgFormConfigService
 * @description
 * @blame wwb
 * @date 2021-06-08 11:13:34
 */
@Slf4j
@Service
public class OrgFormConfigService {

	@Resource
	private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;
	@Resource
	private OrgDataRepoConfigHandleService orgDataRepoConfigHandleService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private SecondClassroomApiService secondClassroomApiService;
	@Resource
	private OrgConfigService orgConfigService;

	/**获取机构配置的表单
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-08 16:49:57
	 * @param fid
	 * @return com.chaoxing.activity.dto.OrgFormConfigDTO
	*/
	public OrgFormConfigDTO getByFid(Integer fid) {
		// 活动配置
		OrgDataRepoConfigDetail activityConfig = orgDataRepoConfigQueryService.getOrgConfigDetail(fid, OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY, OrgDataRepoConfigDetail.RepoTypeEnum.FORM);
		// 参与时长配置
		OrgDataRepoConfigDetail participateTimeLengthConfig = orgDataRepoConfigQueryService.getOrgConfigDetail(fid, OrgDataRepoConfigDetail.DataTypeEnum.PARTICIPATE_TIME_LENGTH, OrgDataRepoConfigDetail.RepoTypeEnum.FORM);
		// 从报名签到中获取用户参与情况表单
		OrgFormConfigDTO signOrgFormConfig = signApiService.getOrgFormConfig(fid);
		// 从第二课堂获取表单
		OrgFormConfigDTO secondClassroomOrgFormConfig = secondClassroomApiService.getOrgFormConfig(fid);
		OrgConfig orgConfig = orgConfigService.getByFid(fid);
		return OrgFormConfigDTO.builder()
				.activityDataFormId(Optional.ofNullable(activityConfig).map(v -> Integer.parseInt(v.getRepo())).orElse(null))
				.participateTimeLengthFormId(Optional.ofNullable(participateTimeLengthConfig).map(v -> Integer.parseInt(v.getRepo())).orElse(null))
				.userParticipateRecordFormId(Optional.ofNullable(signOrgFormConfig).map(OrgFormConfigDTO::getUserParticipateRecordFormId).orElse(null))
				.inspectionPlanFormId(Optional.ofNullable(secondClassroomOrgFormConfig).map(OrgFormConfigDTO::getInspectionPlanFormId).orElse(null))
				.inspectionPlanRuleFormId(Optional.ofNullable(secondClassroomOrgFormConfig).map(OrgFormConfigDTO::getInspectionPlanRuleFormId).orElse(null))
				.creditProjectFormId(Optional.ofNullable(secondClassroomOrgFormConfig).map(OrgFormConfigDTO::getCreditProjectFormId).orElse(null))
				.userScoreFormId(Optional.ofNullable(secondClassroomOrgFormConfig).map(OrgFormConfigDTO::getUserScoreFormId).orElse(null))
				.userResultFormId(Optional.ofNullable(secondClassroomOrgFormConfig).map(OrgFormConfigDTO::getUserResultFormId).orElse(null))
				.signUpScopeType(Optional.ofNullable(orgConfig).map(OrgConfig::getSignUpScopeType).orElse(""))
				.build();
	}

	/**配置机构表单
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-08 17:31:17
	 * @param orgFormConfig
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void configOrgForm(OrgFormConfigDTO orgFormConfig) {
		Integer fid = orgFormConfig.getFid();
		Integer activityDataFormId = orgFormConfig.getActivityDataFormId();
		orgDataRepoConfigHandleService.addOrUpdate(fid, activityDataFormId == null ? "" : String.valueOf(activityDataFormId), OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY, OrgDataRepoConfigDetail.RepoTypeEnum.FORM);
		Integer participateTimeLengthFormId = orgFormConfig.getParticipateTimeLengthFormId();
		orgDataRepoConfigHandleService.addOrUpdate(fid, participateTimeLengthFormId == null ? "" : String.valueOf(participateTimeLengthFormId), OrgDataRepoConfigDetail.DataTypeEnum.PARTICIPATE_TIME_LENGTH, OrgDataRepoConfigDetail.RepoTypeEnum.FORM);
		signApiService.configOrgForm(orgFormConfig);
		OrgConfig orgConfig = OrgConfig.builder()
				.fid(orgFormConfig.getFid())
				.signUpScopeType(orgFormConfig.getSignUpScopeType())
				.timeLengthAppealUrl(orgFormConfig.getTimeLengthAppealUrl())
				.build();
		orgConfigService.config(orgConfig);
		secondClassroomApiService.configOrgForm(orgFormConfig);
	}

}
