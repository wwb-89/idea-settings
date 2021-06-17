package com.chaoxing.activity.dto.manager;

import com.chaoxing.activity.model.ActivityScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	// 是否存在区域
	private Boolean existArea;
	private Integer sort;
	private List<WfwRegionalArchitectureDTO> children;

	public static List<ActivityScope> convert2ActivityScopes(Integer activityId, List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures) {
		List<ActivityScope> activityScopes = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			for (WfwRegionalArchitectureDTO wfwRegionalArchitecture : wfwRegionalArchitectures) {
				ActivityScope activityScope = ActivityScope.builder()
						.activityId(activityId)
						.hierarchyId(wfwRegionalArchitecture.getId())
						.name(wfwRegionalArchitecture.getName())
						.hierarchyPid(wfwRegionalArchitecture.getPid())
						.code(wfwRegionalArchitecture.getCode())
						.links(wfwRegionalArchitecture.getLinks())
						.level(wfwRegionalArchitecture.getLevel())
						.adjustedLevel(wfwRegionalArchitecture.getLevel())
						.fid(wfwRegionalArchitecture.getFid())
						.existChild(Optional.ofNullable(wfwRegionalArchitecture.getExistChild()).orElse(Boolean.FALSE))
						.sort(wfwRegionalArchitecture.getSort())
						.build();
				activityScopes.add(activityScope);
			}
		}
		return activityScopes;
	}

}