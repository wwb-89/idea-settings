package com.chaoxing.activity.util.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

/**用户行为枚举
 * @author wwb
 * @version ver 1.0
 * @className UserActionTypeEnum
 * @description
 * @blame wwb
 * @date 2021-06-16 11:14:17
 */
@Getter
public enum UserActionTypeEnum {

	/** 报名 */
	SIGN_UP("报名", "sign_up", false),
	SIGN_IN("签到", "sign_in", true, UserActionEnum.SIGNED_IN),
	RATING("评价", "rating", true, UserActionEnum.RATING),
	DISCUSS("讨论", "discuss", false, UserActionEnum.PUBLISH, UserActionEnum.REPLY),
	WORK("作品征集", "work", true, UserActionEnum.WORK),
	PERFORMANCE("现场评分", "performance", true, UserActionEnum.PERFORMANCE);

	private String name;
	private String value;
	private Boolean enable;
	private List<UserActionEnum> userActions;

	UserActionTypeEnum(String name, String value, Boolean enable, UserActionEnum ...userActions) {
		this.name = name;
		this.value = value;
		this.enable = enable;
		List<UserActionEnum> userActionEnums = Lists.newArrayList();
		if (userActions.length > 0) {
			for (UserActionEnum userAction : userActions) {
				userActionEnums.add(userAction);
			}
		}
		this.userActions = userActionEnums;
	}

}