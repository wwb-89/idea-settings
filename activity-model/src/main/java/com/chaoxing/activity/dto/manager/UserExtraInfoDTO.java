package com.chaoxing.activity.dto.manager;

import com.chaoxing.activity.dto.manager.wfw.WfwClassDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwRoleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**用户额外信息对象
 * @author wwb
 * @version ver 1.0
 * @className UserExtraInfoDTO
 * @description
 * @blame wwb
 * @date 2020-11-12 19:56:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExtraInfoDTO {

	private Integer classId;
	private String className;
	private Integer gradeId;
	private String gradeName;

	private List<WfwRoleDTO> roles;
	private List<WfwClassDTO> classes;

}