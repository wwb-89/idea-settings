package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityQueryDateDTO
 * @description
 * @blame wwb
 * @date 2020-12-02 22:01:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityQueryDateDTO {

	private String name;
	private String value;

}
