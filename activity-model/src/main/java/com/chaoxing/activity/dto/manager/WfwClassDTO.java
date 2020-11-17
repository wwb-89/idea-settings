package com.chaoxing.activity.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwClassDTO
 * @description
 * @blame wwb
 * @date 2020-11-12 19:58:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwClassDTO {

	/** 班级id */
	private Integer id;
	/** 班级名称 */
	private String name;
	/** 年级id */
	private Integer gradeId;
	/** 年级名称 */
	private String gradeName;

}