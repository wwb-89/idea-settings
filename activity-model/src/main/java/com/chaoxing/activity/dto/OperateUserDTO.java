package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**操作人
 * @author wwb
 * @version ver 1.0
 * @className OperateUserDTO
 * @description
 * @blame wwb
 * @date 2021-07-14 16:27:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperateUserDTO {

	/** 用户dud */
	private Integer uid;
	/** 用户姓名 */
	private String userName;
	/** 机构id */
	private Integer fid;
	/** 机构名 */
	private String orgName;

	public static OperateUserDTO build(Integer uid) {
		return OperateUserDTO.builder()
				.uid(uid)
				.build();
	}

	public static OperateUserDTO build(Integer uid, Integer fid) {
		return OperateUserDTO.builder()
				.uid(uid)
				.fid(fid)
				.build();
	}

}