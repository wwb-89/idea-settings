package com.chaoxing.activity.util.enums;

import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Getter;

import java.util.Objects;

/**门户应用类型枚举
 * @author wwb
 * @version ver 1.0
 * @className MhAppTypeEnum
 * @description
 * @blame wwb
 * @date 2020-11-23 20:14:31
 */
@Getter
public enum MhAppTypeEnum {

	// 轮播图
	SLIDE("轮播图", "slide"),
	TEXT("文本", "text"),
	ICON("图标", "icon"),
	IMAGE("多图", "image"),
	GRAPHIC("图文", "graphic"),
	MAP("地图", "map");

	private final String name;
	private final String value;

	MhAppTypeEnum(String name, String value){
		this.name = name;
		this.value = value;
	}

	public static MhAppTypeEnum fromValue(String value) {
		MhAppTypeEnum[] values = MhAppTypeEnum.values();
		for (MhAppTypeEnum mhAppTypeEnum : values) {
			if (Objects.equals(mhAppTypeEnum.getValue(), value)) {
				return mhAppTypeEnum;
			}
		}
		throw new BusinessException("未知的门户应用类型");
	}

}