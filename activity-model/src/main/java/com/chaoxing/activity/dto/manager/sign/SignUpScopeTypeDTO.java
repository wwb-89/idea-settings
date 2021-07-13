package com.chaoxing.activity.dto.manager.sign;

import com.chaoxing.activity.model.OrgConfig;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**报名参与范围使用的组织架构
 * @author wwb
 * @version ver 1.0
 * @className SignUpScopeTypeDTO
 * @description
 * @blame wwb
 * @date 2021-06-17 11:10:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpScopeTypeDTO {

	/** 名称 */
	private String name;
	/** 值 */
	private String value;

	public static List<SignUpScopeTypeDTO> fromSignUpScopeTypeEnum() {
		List<SignUpScopeTypeDTO> signUpScopeTypes = Lists.newArrayList();
		OrgConfig.SignUpScopeType[] values = OrgConfig.SignUpScopeType.values();
		for (OrgConfig.SignUpScopeType value : values) {
			SignUpScopeTypeDTO signUpScopeType = SignUpScopeTypeDTO.builder()
					.name(value.getName())
					.value(value.getValue())
					.build();
			signUpScopeTypes.add(signUpScopeType);
		}
		return signUpScopeTypes;
	}
}
