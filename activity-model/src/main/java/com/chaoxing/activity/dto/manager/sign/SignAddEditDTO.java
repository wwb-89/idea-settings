package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**报名签到的新增修改对象
 * @author wwb
 * @version ver 1.0
 * @className SignCreateDTO
 * @description
 * @blame wwb
 * @date 2020-12-16 15:10:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignAddEditDTO {

	/** 主键 */
	private Integer id;
	/** 活动名称 */
	private String name;
	/** 说明 */
	private String notes;
	/** 封面云盘id */
	private String coverCloudId;
	/** 创建人uid */
	private Integer createUid;
	/** 创建人姓名 */
	private String createUserName;
	/** 创建人fid */
	private Integer createFid;
	/** 创建人机构名称 */
	private String createOrgName;
	/** 更新人id */
	private Integer updateUid;

	/** 报名 */
	private SignUp signUp;
	/** 签到 */
	private SignIn signIn;
	/** 签退 */
	private SignIn signOut;

}