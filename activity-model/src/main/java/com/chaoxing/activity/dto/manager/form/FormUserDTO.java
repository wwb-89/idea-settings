package com.chaoxing.activity.dto.manager.form;

import com.chaoxing.activity.dto.OperateUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className FormUserDTO
 * @description
 * @blame wwb
 * @date 2021-08-30 11:16:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormUserDTO {

	private Integer puid;
	private String uname;

	public OperateUserDTO buildOperationUserDto() {
		return OperateUserDTO.builder()
				.uid(puid)
				.userName(uname)
				.build();
	}

}