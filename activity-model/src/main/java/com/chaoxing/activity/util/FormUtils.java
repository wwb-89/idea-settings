package com.chaoxing.activity.util;

import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.DepartmentDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormDataItemDTO;
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
	/** 部门id的key */
	public static final String DEPARTMENT_ID_KEY = "departmentId";
	/** 部门名称的key */
	public static final String DEPARTMENT_NAME_KEY = "departmentName";

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
	 * @param formDataDto
	 * @param alias
	 * @return java.lang.String
	*/
	public static String getValue(FormDataDTO formDataDto, String alias) {
		String value = "";
		JSONObject jsonValue = getJsonValue(formDataDto, alias);
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
	 * @param formDataDto
	 * @param alias
	 * @return com.chaoxing.secondclassroom.dto.FormUserDTO
	*/
	public static FormUserDTO getUser(FormDataDTO formDataDto, String alias) {
		JSONObject jsonValue = getJsonValue(formDataDto, alias);
		if (jsonValue != null) {
			String uidString = jsonValue.getString(PUID_KEY);
			if (StringUtils.isNotBlank(uidString)) {
				return FormUserDTO.builder()
						.puid(Integer.parseInt(uidString))
						.uname(jsonValue.getString(UNAME_KEY))
						.build();
			}
		}
		return null;
	}

	/**获取部门信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-31 14:25:47
	 * @param formDataDto
 * @param alias
	 * @return com.chaoxing.secondclassroom.dto.manager.DepartmentDTO
	*/
	public static DepartmentDTO getDepartment(FormDataDTO formDataDto, String alias) {
		JSONObject jsonValue = getJsonValue(formDataDto, alias);
		if (jsonValue != null) {
			String departmentIdStr = jsonValue.getString(DEPARTMENT_ID_KEY);
			if (StringUtils.isNotBlank(departmentIdStr)) {
				return DepartmentDTO.builder()
						.id(Integer.parseInt(departmentIdStr))
						.name(jsonValue.getString(DEPARTMENT_NAME_KEY))
						.build();
			}
		}
		return null;
	}

	private static JSONObject getJsonValue(FormDataDTO formData, String alias) {
		List<FormDataItemDTO> items = formData.getFormData();
		if (CollectionUtils.isNotEmpty(items)) {
			for (FormDataItemDTO item : items) {
				if (Objects.equals(item.getAlias(), alias)) {
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
	 * @param alias
	 * @return java.time.LocalDate
	*/
	public static LocalDate getDate(FormDataDTO formData, String alias) {
		LocalDate result = null;
		String value = getValue(formData, alias);
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
	 * @param formDataDto
	 * @param alias
	 * @return java.time.LocalDateTime
	*/
	public static LocalDateTime getTime(FormDataDTO formDataDto, String alias) {
		LocalDateTime result = null;
		String value = getValue(formDataDto, alias);
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