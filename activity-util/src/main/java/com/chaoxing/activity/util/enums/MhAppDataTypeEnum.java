package com.chaoxing.activity.util.enums;

import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Getter;

import java.util.Objects;

/**门户应用的数据类型
 * @author wwb
 * @version ver 1.0
 * @className MhAppDataTypeEnum
 * @description
 * @blame wwb
 * @date 2020-11-24 09:48:35
 */
@Getter
public enum MhAppDataTypeEnum {

	/** 活动封面 */
	ACTIVITY_COVER("活动封面", "activity_cover"),
	ACTIVITY_INFO("活动信息", "activity_info"),
	SIGN_IN_UP("签到报名", "sign_in_up"),
	ACTIVITY_MODULE("活动模块", "activity_module"),
	ACTIVITY_MAP("活动地图", "activity_map"),
	ACTIVITY_LIST("活动列表", "activity_list");

	private String name;
	private String value;

	MhAppDataTypeEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static MhAppDataTypeEnum fromValue(String value) {
		MhAppDataTypeEnum[] values = MhAppDataTypeEnum.values();
		for (MhAppDataTypeEnum mhAppDataTypeEnum : values) {
			if (Objects.equals(mhAppDataTypeEnum.getValue(), value)) {
				return mhAppDataTypeEnum;
			}
		}
		throw new BusinessException("未知的门户应用数据类型");
	}

}
