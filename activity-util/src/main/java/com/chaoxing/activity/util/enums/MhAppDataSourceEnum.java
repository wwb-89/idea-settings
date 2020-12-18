package com.chaoxing.activity.util.enums;

import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Getter;

import java.util.Objects;

/**门户应用数据源枚举
 * @author wwb
 * @version ver 1.0
 * @className MhAppDataSourceEnum
 * @description
 * @blame wwb
 * @date 2020-11-23 20:07:05
 */
@Getter
public enum MhAppDataSourceEnum {

	/** 本地数据 */
	LOCAL("本地数据", 1),
	EXTERNAL("外部数据", 2);

	private String name;
	private Integer value;

	MhAppDataSourceEnum(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public static MhAppDataSourceEnum fromValue(Integer value) {
		MhAppDataSourceEnum[] values = MhAppDataSourceEnum.values();
		for (MhAppDataSourceEnum mhAppDataSourceEnum : values) {
			if (Objects.equals(mhAppDataSourceEnum.getValue(), value)) {
				return mhAppDataSourceEnum;
			}
		}
		throw new BusinessException("未知的门户数据源类型");
	}

}