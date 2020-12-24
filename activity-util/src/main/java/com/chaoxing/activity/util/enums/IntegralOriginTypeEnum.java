package com.chaoxing.activity.util.enums;

import lombok.Getter;

import java.util.Objects;

/**积分来源类型
 * @author wwb
 * @version ver 1.0
 * @className IntegralOriginTypeEnum
 * @description
 * @blame wwb
 * @date 2020-12-24 15:59:41
 */
@Getter
public enum IntegralOriginTypeEnum {

	/** 报名 */
	SIGN_UP("报名", 25),
	LIKE_WORK("点赞作品", 36),
	VIEW_ACTIVITY("浏览活动", 41),
	SUBMIT_WORK("上传作品", 42),
	VIEW_WORK("浏览作品", 43),
	COMMENT_WORK("评论作品", 44);

	private String name;
	private Integer value;

	IntegralOriginTypeEnum(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public static IntegralOriginTypeEnum fromValue(Integer value) {
		IntegralOriginTypeEnum[] values = IntegralOriginTypeEnum.values();
		for (IntegralOriginTypeEnum integralOriginTypeEnum : values) {
			if (Objects.equals(integralOriginTypeEnum.getValue(), value)) {
				return integralOriginTypeEnum;
			}
		}
		return null;
	}

}