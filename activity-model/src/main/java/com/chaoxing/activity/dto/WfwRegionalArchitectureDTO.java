package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**微服务层级架构对象
 * @author wwb
 * @version ver 1.0
 * @className WfwRegionalArchitectureDTO
 * @description
 * {
 *             "id": 5372,
 *             "name": "鄂尔多斯市教育体育局",
 *             "pid": 0,
 *             "code": "017",
 *             "links": "鄂尔多斯市教育体育局",
 *             "level": 1,
 *             "fid": 12017,
 *             "existChild": true,
 *             "sort": 17
 *         }
 * @blame wwb
 * @date 2020-08-24 13:20:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwRegionalArchitectureDTO {

	private Integer id;
	private String name;
	private Integer pid;
	private String code;
	private String links;
	private Integer level;
	private Integer fid;
	private Boolean existChild;
	private Integer sort;
	private boolean checked;

}