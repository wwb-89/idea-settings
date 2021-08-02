package com.chaoxing.activity.dto.blacklist;

import com.chaoxing.activity.model.Blacklist;
import com.chaoxing.activity.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

/**用户黑名单结果
 * @author wwb
 * @version ver 1.0
 * @className UserBlacklistResultDTO
 * @description
 * @blame wwb
 * @date 2021-07-30 14:21:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBlacklistResultDTO {

	/** 用户uid */
	private Integer uid;
	/** 是否在黑名单中 */
	private Boolean whetherInBlacklist;
	/** 类型 */
	private String joinType;
	/** 解封时间 */
	private Long unlockTime;

	public static UserBlacklistResultDTO buildEmpty(Integer uid) {
		return UserBlacklistResultDTO.builder()
				.uid(uid)
				.whetherInBlacklist(false)
				.build();
	}

	public static UserBlacklistResultDTO buildFromBlacklist(Blacklist blacklist, Integer uid) {
		if (blacklist == null) {
			return buildEmpty(uid);
		}
		LocalDateTime createTime = blacklist.getCreateTime();
		createTime.plusHours(Optional.ofNullable(blacklist.getEffectiveHours()).orElse(0));
		return UserBlacklistResultDTO.builder()
				.uid(blacklist.getUid())
				.whetherInBlacklist(true)
				.joinType(blacklist.getJoinType())
				.unlockTime(DateUtils.date2Timestamp(createTime))
				.build();
	}

}