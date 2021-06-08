package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**机构表单配置对象
 * @author wwb
 * @version ver 1.0
 * @className OrgFormConfigDTO
 * @description 机构配置了一系列的表单
 * 1、活动数据表单
 * 2、参与时长表单
 * 3、用户活动参与情况表单
 * 4、考核计划表单
 * 5、考核计划规则配置表单
 * 6、学分项目类型关联表单
 * 7、用户得分记录表单
 * 8、用户成绩表单
 * @blame wwb
 * @date 2021-06-08 11:00:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgFormConfigDTO {

	/** 机构fid */
	private Integer fid;
	/** 活动数据表单 */
	private Integer activityDataFormId;
	/** 参与时长表单 */
	private Integer participateTimeLengthFormId;
	/** 用户活动参与情况表单 */
	private Integer userParticipateRecordFormId;
	/** 考核计划表单 */
	private Integer inspectionPlanFormId;
	/** 考核计划规则配置表单 */
	private Integer inspectionPlanRuleFormId;
	/** 学分项目类型关联表单 */
	private Integer creditProjectFormId;
	/** 用户得分记录表单 */
	private Integer userScoreFormId;
	/** 用户成绩表单 */
	private Integer userResultFormId;

}