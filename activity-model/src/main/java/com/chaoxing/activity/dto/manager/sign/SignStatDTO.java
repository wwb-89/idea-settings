package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**报名签到统计对象
 * @author wwb
 * @version ver 1.0
 * @className SignStatDTO
 * @description
 * @blame wwb
 * @date 2021-03-24 20:12:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignStatDTO {
	
	/** 报名签到id */
	private Integer id;
	/** 报名id列表 */
	private List<Integer> signUpIds;
	/** 报名开始时间 */
	private LocalDateTime signUpStartTime;
	/** 报名结束时间 */
	private LocalDateTime signUpEndTime;
	/** 报名人数 */
	private Integer signedUpNum;
	/** 限制数 */
	private Integer limitNum;
	/** 是否公开报名名单 */
	private Boolean publicList;
	
}
