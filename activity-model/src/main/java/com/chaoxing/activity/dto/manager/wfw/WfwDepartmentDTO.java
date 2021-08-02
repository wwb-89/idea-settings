package com.chaoxing.activity.dto.manager.wfw;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

/**微服务部门对象
 * @author wwb
 * @version ver 1.0
 * @className WfwDepartmentDTO
 * @description
 * @blame wwb
 * @date 2021-03-28 16:51:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwDepartmentDTO {

	private Integer id;
	private Integer pid;
	@JSONField(name = "rootdept")
	private Integer rootDept;
	private Integer fid;
	private String name;
	private Integer level;
	@JSONField(name = "subdeptcount")
	private Integer subDeptCount;
	@JSONField(name = "usercount")
	private Integer userCount;

}
