package com.chaoxing.activity.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**门户活动日历查询对象
 * @author wwb
 * @version ver 1.0
 * @className MhActivityCalendarQueryDTO
 * @description
 * @blame wwb
 * @date 2020-12-03 15:55:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MhActivityCalendarQueryDTO {

	/** 置顶的fid（查询的结果该机构的数据靠前） */
	private Integer topFid;
	/** 参与范围 */
	private List<Integer> fids;
	/** 时间 */
	private LocalDate date;

}
