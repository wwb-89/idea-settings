package com.chaoxing.activity.dto;

import com.chaoxing.activity.util.enums.UserActionEnum;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**用户行为类型
 * @author wwb
 * @version ver 1.0
 * @className UserActionTypeDTO
 * @description
 * @blame wwb
 * @date 2021-06-16 14:28:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActionTypeDTO {

	private String name;
	private String value;
	private Boolean enable;
	private List<UserActionDTO> userActions;

	public static UserActionTypeDTO fromUserActionTypeEnum(UserActionTypeEnum userActionTypeEnum) {
		UserActionTypeDTO userActionType = UserActionTypeDTO.builder()
				.name(userActionTypeEnum.getName())
				.value(userActionTypeEnum.getValue())
				.enable(userActionTypeEnum.getEnable())
				.userActions(UserActionDTO.fromUserActionEnums(userActionTypeEnum.getUserActions()))
				.build();
		return userActionType;
	}

	public static List<UserActionTypeDTO> fromUserActionTypeEnum() {
		List<UserActionTypeDTO> userActionTypes = Lists.newArrayList();
		UserActionTypeEnum[] userActionTypeEnums = UserActionTypeEnum.values();
		for (UserActionTypeEnum userActionTypeEnum : userActionTypeEnums) {
			if (userActionTypeEnum.getEnable()) {
				UserActionTypeDTO userActionType = UserActionTypeDTO.builder()
						.name(userActionTypeEnum.getName())
						.value(userActionTypeEnum.getValue())
						.enable(userActionTypeEnum.getEnable())
						.userActions(UserActionDTO.fromUserActionEnums(userActionTypeEnum.getUserActions()))
						.build();
				userActionTypes.add(userActionType);
			}
		}
		return userActionTypes;
	}

}
