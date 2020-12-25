package com.chaoxing.activity.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**积分推送对象
 * @author wwb
 * @version ver 1.0
 * @className IntegralPushDTO
 * @description
 * @blame wwb
 * @date 2020-12-24 16:08:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegralPushDTO {

	/** 机构id */
	private Integer fid;
	/** 用户id */
	private Integer uid;
	/** 类型 */
	private Integer type;
	/** 资源id */
	private String resourceId;
	/** 资源名称 */
	private String resourceName;

}