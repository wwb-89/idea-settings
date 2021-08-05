package com.chaoxing.activity.dto.module;

import com.chaoxing.activity.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**作品征集表单对象
 * @author wwb
 * @version ver 1.0
 * @className WorkFormDTO
 * @description
 * @blame wwb
 * @date 2020-11-11 10:14:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFormDTO {

	/** 活动id */
	private Integer id;
	/** 活动名称 */
	private String name;
	/** fid */
	private Integer wfwfid;
	/** uid */
	private Integer uid;
	/** 开始时间 */
	private Long startTime;
	/** 结束时间 */
	private Long endTime;

	public static WorkFormDTO buildDefault(Integer uid, Integer fid) {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = startTime.plusMonths(1);
		return WorkFormDTO.builder()
				.name("作品征集")
				.uid(uid)
				.wfwfid(fid)
				.startTime(DateUtils.date2Timestamp(startTime))
				.endTime(DateUtils.date2Timestamp(endTime))
				.build();
	}

}