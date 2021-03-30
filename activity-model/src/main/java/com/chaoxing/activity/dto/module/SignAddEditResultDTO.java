package com.chaoxing.activity.dto.module;

import com.chaoxing.activity.dto.manager.sign.SignIn;
import com.chaoxing.activity.dto.manager.sign.SignUp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**报名新增修改结果对象
 * @author wwb
 * @version ver 1.0
 * @className SignAddEditResultDTO
 * @description
 * @blame wwb
 * @date 2021-03-29 15:59:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignAddEditResultDTO {

	/** 报名签到id */
	public Integer signId;
	/** 报名模块id列表 */
	public List<SignUp> signUpModules;
	/** 签到模块id列表 */
	public List<SignIn> signInModules;

}