package com.chaoxing.activity.dto.manager.wfwform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**表单用户对象
 * @author wwb
 * @version ver 1.0
 * @className WfwFormUserDTO
 * @description
 * @blame wwb
 * @date 2021-03-11 19:48:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormUserDTO {

	/** 用户uid */
	private Integer puid;
	/** 用户姓名 */
	private String userName;

}