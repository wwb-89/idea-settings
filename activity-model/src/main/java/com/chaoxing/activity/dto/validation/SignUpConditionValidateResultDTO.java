package com.chaoxing.activity.dto.validation;

import lombok.Builder;
import lombok.Data;

/**报名条件验证结果
 * @author wwb
 * @version ver 1.0
 * @className SignUpConditionValidateResultDTO
 * @description
 * @blame wwb
 * @date 2022-03-21 17:36:40
 */
@Data
@Builder
public class SignUpConditionValidateResultDTO {

	/** 是否通过 */
	private Boolean passed;
	/** 信息 */
	private String message;
	/** 链接 */
	private String url;

	public static SignUpConditionValidateResultDTO buildDefault() {
		return SignUpConditionValidateResultDTO.builder()
				.passed(true)
				.message("")
				.url("")
				.build();
	}

}