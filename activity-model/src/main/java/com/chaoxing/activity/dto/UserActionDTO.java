package com.chaoxing.activity.dto;

import com.chaoxing.activity.util.enums.UserActionEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**用户行为
 * @author wwb
 * @version ver 1.0
 * @className UserActionDTO
 * @description
 * @blame wwb
 * @date 2021-06-16 14:29:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActionDTO {

	private String name;
	private String value;
	private String description;
	private Boolean multiple;
	private Boolean enableUpperLimit;

	public static List<UserActionDTO> fromUserActionEnums(List<UserActionEnum> userActionEnums) {
		List<UserActionDTO> userActions = Lists.newArrayList();
		for (UserActionEnum userActionEnum : userActionEnums) {
			UserActionDTO userAction = UserActionDTO.builder()
					.name(userActionEnum.getName())
					.value(userActionEnum.getValue())
					.description(userActionEnum.getDescription())
					.multiple(userActionEnum.getMultiple())
					.enableUpperLimit(userActionEnum.getEnableUpperLimit())
					.build();
			userActions.add(userAction);
		}
		return userActions;
	}

}
