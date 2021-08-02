package com.chaoxing.activity.dto.manager.wfw;

import com.chaoxing.activity.model.ActivityScope;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**微服务区域
 * @author wwb
 * @version ver 1.0
 * @className WfwAreaDTO
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
public class WfwAreaDTO {

	private Integer id;
	private String name;
	private Integer pid;
	private String code;
	private String links;
	private Integer level;
	private Integer fid;
	private Boolean existChild;
	/** 是否存在区域 */
	private Boolean existArea;
	private Integer sort;
	private List<WfwAreaDTO> children;

	public ActivityScope buildActivityScope() {
		return ActivityScope.builder()
				.hierarchyId(getId())
				.name(getName())
				.hierarchyPid(getPid())
				.code(getCode())
				.links(getLinks())
				.level(getLevel())
				.adjustedLevel(getLevel())
				.fid(getFid())
				.existChild(Optional.ofNullable(getExistChild()).orElse(Boolean.FALSE))
				.sort(getSort())
				.build();
	}

	public static List<ActivityScope> buildActivityScopes(List<WfwAreaDTO> wfwRegionalArchitectures) {
		if (CollectionUtils.isEmpty(wfwRegionalArchitectures)) {
			return Lists.newArrayList();
		}
		return wfwRegionalArchitectures.stream().map(WfwAreaDTO::buildActivityScope).collect(Collectors.toList());
	}

}