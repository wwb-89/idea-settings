package com.chaoxing.activity.service.util;

import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormUserDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**表单工具类
 * @author wwb
 * @version ver 1.0
 * @className FormUtils
 * @description
 * @blame wwb
 * @date 2021-03-11 15:09:05
 */
public class FormUtils {

	/** value的key */
	public static final String VAL_KEY = "val";
	/** puid的key */
	public static final String PUID_KEY = "puid";
	/** uname的key */
	public static final String UNAME_KEY = "uname";

	private static final List<DateTimeFormatter> FORM_DATE_TIME_FORMATTERS = Lists.newArrayList(
			// 时:分
			DateTimeFormatter.ofPattern("HH:mm"),
			// 年
			DateTimeFormatter.ofPattern("yyyy"),
			// 年-月
			DateTimeFormatter.ofPattern("yyyy-MM"),
			// 年-月-日
			DateTimeFormatter.ofPattern("yyyy-MM-dd"),
			// 年-月-日 时:分
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
			// 年-月-日 时:分:秒
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
	);

	private FormUtils() {

	}

	/**从表单数据中获取值
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-11 15:10:15
	 * @param formData
	 * @param fieldCode
	 * @return java.lang.String
	*/
	public static String getValue(FormDTO formData, String fieldCode) {
		String value = "";
		JSONObject jsonValue = getJsonValue(formData, fieldCode);
		if (jsonValue != null) {
			value = jsonValue.getString(VAL_KEY);
		}
		value = Optional.ofNullable(value).orElse("");
		return value;
	}

	/**从表单数据中获取用户数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-11 19:54:49
	 * @param formData
	 * @param fieldName
	 * @return com.chaoxing.secondclassroom.dto.FormUserDTO
	*/
	public static FormUserDTO getUser(FormDTO formData, String fieldName) {
		JSONObject jsonValue = getJsonValue(formData, fieldName);
		if (jsonValue != null) {
			String uidString = jsonValue.getString(PUID_KEY);
			if (StringUtils.isNotBlank(uidString)) {
				return FormUserDTO.builder()
						.puid(Integer.parseInt(uidString))
						.userName(jsonValue.getString(UNAME_KEY))
						.build();
			}
		}
		return null;
	}

	private static JSONObject getJsonValue(FormDTO formData, String fieldCode) {
		List<FormDataDTO> items = formData.getFormData();
		if (CollectionUtils.isNotEmpty(items)) {
			for (FormDataDTO item : items) {
				String alias = item.getAlias();
				if (Objects.equals(fieldCode, alias)) {
					List<JSONObject> values = item.getValues();
					if (CollectionUtils.isNotEmpty(values)) {
						return values.get(0);
					}
				}
			}
		}
		return null;
	}

	/**从表单中获取日期
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-12 11:35:12
	 * @param formData
	 * @param fieldCode
	 * @return java.time.LocalDate
	*/
	public static LocalDate getDate(FormDTO formData, String fieldCode) {
		LocalDate result = null;
		String value = getValue(formData, fieldCode);
		if (StringUtils.isNotBlank(value)) {
			for (DateTimeFormatter dateTimeFormatter : FormUtils.FORM_DATE_TIME_FORMATTERS) {
				try {
					result = LocalDate.parse(value, dateTimeFormatter);
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

	/**从表单中获取时间
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-12 11:35:22
	 * @param formData
	 * @param fieldCode
	 * @return java.time.LocalDateTime
	*/
	public static LocalDateTime getTime(FormDTO formData, String fieldCode) {
		LocalDateTime result = null;
		String value = getValue(formData, fieldCode);
		if (StringUtils.isNotBlank(value)) {
			for (DateTimeFormatter dateTimeFormatter : FormUtils.FORM_DATE_TIME_FORMATTERS) {
				try {
					result = LocalDateTime.parse(value, dateTimeFormatter);
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

}