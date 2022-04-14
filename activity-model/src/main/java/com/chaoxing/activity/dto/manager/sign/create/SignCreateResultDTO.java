package com.chaoxing.activity.dto.manager.sign.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**报名新增修改结果对象
 * @author wwb
 * @version ver 1.0
 * @className SignCreateResultDTO
 * @description 活动引擎创建报名签到使用
 * @blame wwb
 * @date 2021-03-29 15:59:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignCreateResultDTO {

	/** 报名签到id */
	public Integer signId;

}