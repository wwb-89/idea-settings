package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**小组对象
 * @author wwb
 * @version ver 1.0
 * @className GroupDTO
 * @description
 * @blame wwb
 * @date 2020-11-12 10:17:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {

	/** 小组id */
	private Integer id;
	/** bbsid */
	private String bbsid;

}