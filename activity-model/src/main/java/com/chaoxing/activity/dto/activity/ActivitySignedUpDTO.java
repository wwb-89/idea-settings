package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.model.Activity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**已报名的活动
 * @author wwb
 * @version ver 1.0
 * @className ActivitySignedUpDTO
 * @description
 * @blame wwb
 * @date 2021-01-27 20:29:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySignedUpDTO extends Activity {

	/** 是否可管理 */
	private Boolean managAble;
	/** 报名id */
	private Integer signUpId;
	/** 报名状态 */
	private Integer userSignUpStatus;

}