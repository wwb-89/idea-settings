package com.chaoxing.activity.vo.manager;

import lombok.*;

/**微服务表单vo
 * @author wwb
 * @version ver 1.0
 * @className WfwFormVO
 * @description
 * @blame wwb
 * @date 2021-07-08 15:41:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormVO {

	/** 表单id */
	private Integer id;
	/** 表单名称 */
	private String name;

}
