package com.chaoxing.activity.dto.query;

import com.chaoxing.activity.util.enums.OrderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**黑名单查询对象
 * @author wwb
 * @version ver 1.0
 * @className BlacklistQueryDTO
 * @description
 * @blame wwb
 * @date 2021-07-29 11:26:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistQueryDTO {

	/** 活动市场id */
	private Integer marketId;
	/** 关键字 */
	private String sw;
	/** 顺序 */
	private OrderTypeEnum orderType;

}
