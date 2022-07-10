package com.chaoxing.activity.dto;

import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.chaoxing.activity.model.OrgConfig;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**部门对象
 * @author wwb
 * @version ver 1.0
 * @className DepartmentDTO
 * @description
 * @blame wwb
 * @date 2021-08-31 14:19:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

	/** 部门id */
	private Integer id;
	/** 部门名称 */
	private String name;

	public static List<SignUpParticipateScopeDTO> convert2ContactsParticipateScopes(List<DepartmentDTO> departments) {
		if (CollectionUtils.isEmpty(departments)) {
			return Lists.newArrayList();
		}
		return departments.stream().map(v -> SignUpParticipateScopeDTO.builder()
				.externalId(v.getId())
				.externalName(v.getName())
				.groupType(OrgConfig.SignUpScopeType.CONTACTS.getValue())
				.build()).collect(Collectors.toList());
	}

}