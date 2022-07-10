package com.chaoxing.activity.dto.manager.wfw;

import com.chaoxing.activity.dto.manager.uc.ClazzDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

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

	public ClazzDTO buildClazzDTO() {
		return ClazzDTO.builder()
				.id(getId())
				.name(Optional.ofNullable(gradeName).filter(StringUtils::isNotBlank).orElse("") + Optional.ofNullable(name).filter(StringUtils::isNotBlank).orElse(""))
				.build();
	}

}