package com.chaoxing.activity.service.util;

import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.AddressDTO;
import com.chaoxing.activity.dto.DepartmentDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormDataItemDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormUserDTO;
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
	 * @param formData
	 * @param fieldAlias
	 * @return java.lang.String
	*/
	public static String getValue(FormDataDTO formData, String fieldAlias) {
		String value = "";
		JSONObject jsonValue = getJsonValue(formData, fieldAlias);
		if (jsonValue != null) {
			value = jsonValue.getString(VAL_KEY);
		}
		value = Optional.ofNullable(value).orElse("");
		return value;
	}

	public static List<String> listValue(FormDataDTO formData, String fieldAlias) {
		List<String> values = Lists.newArrayList();
		List<JSONObject> jsonObjects = listJsonValue(formData, fieldAlias);
		if (CollectionUtils.isNotEmpty(jsonObjects)) {
			for (JSONObject jsonObject : jsonObjects) {
				String value = jsonObject.getString(VAL_KEY);
				if (StringUtils.isNotBlank(value)) {
					value = value.trim();
					if (!values.contains(value)) {
						values.add(value);
					}
				}
			}
		}
		return values;
	}

	/**获取地址信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-15 01:43:33
	 * @param formData
	 * @param fieldAlias
	 * @return com.chaoxing.activity.dto.AddressDTO
	*/
	public static AddressDTO getAddress(FormDataDTO formData, String fieldAlias) {
		AddressDTO address = null;
		JSONObject jsonValue = getJsonValue(formData, fieldAlias);
		if (jsonValue != null) {
			address = AddressDTO.builder()
					.address(jsonValue.getString("address"))
					.lat(jsonValue.getBigDecimal("lat"))
					.lng(jsonValue.getBigDecimal("lng"))
					.build();
		}
		return address;
	}

	/**获取云盘资源id
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-15 00:36:27
	 * @param formData
	 * @param fieldAlias
	 * @return java.lang.String
	*/
	public static String getCloudId(FormDataDTO formData, String fieldAlias) {
		String value = "";
		JSONObject jsonValue = getJsonValue(formData, fieldAlias);
		if (jsonValue != null) {
			value = jsonValue.getString("objectId");
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
	public static WfwFormUserDTO getUser(FormDataDTO formData, String fieldName) {
		JSONObject jsonValue = getJsonValue(formData, fieldName);
		if (jsonValue != null) {
			String uidString = jsonValue.getString(PUID_KEY);
			if (StringUtils.isNotBlank(uidString)) {
				return WfwFormUserDTO.builder()
						.puid(Integer.parseInt(uidString))
						.userName(jsonValue.getString(UNAME_KEY))
						.build();
			}
		}
		return null;
	}

	private static JSONObject getJsonValue(FormDataDTO formData, String fieldAlias) {
		List<FormDataItemDTO> items = formData.getFormData();
		if (CollectionUtils.isNotEmpty(items)) {
			for (FormDataItemDTO item : items) {
				String alias = item.getAlias();
				if (Objects.equals(fieldAlias, alias)) {
					List<JSONObject> values = item.getValues();
					if (CollectionUtils.isNotEmpty(values)) {
						return values.get(0);
					}
				}
			}
		}
		return null;
	}

	private static List<JSONObject> listJsonValue(FormDataDTO formData, String fieldAlias) {
		List<FormDataItemDTO> items = formData.getFormData();
		if (CollectionUtils.isNotEmpty(items)) {
			for (FormDataItemDTO item : items) {
				String alias = item.getAlias();
				if (Objects.equals(fieldAlias, alias)) {
					return item.getValues();
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
	 * @param fieldAlias 字段别名
	 * @return java.time.LocalDate
	*/
	public static LocalDate getDate(FormDataDTO formData, String fieldAlias) {
		LocalDate result = null;
		String value = getValue(formData, fieldAlias);
		if (StringUtils.isNotBlank(value)) {
			result = getDate(value);
		}
		return result;
	}

	private static LocalDate getDate(String value) {
		LocalDate result = null;
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
	 * @param fieldAlias 字段别名
	 * @return java.time.LocalDateTime
	*/
	public static LocalDateTime getTime(FormDataDTO formData, String fieldAlias) {
		LocalDateTime result = null;
		String value = getValue(formData, fieldAlias);
		if (StringUtils.isNotBlank(value)) {
			result = getTime(value);
		}
		return result;
	}

	private static LocalDateTime getTime(String value) {
		LocalDateTime result = null;
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

	/**获取时间区间
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-11 18:15:41
	 * @param formData
	 * @param fieldAlias
		 * @return com.chaoxing.activity.dto.TimeScopeDTO
	*/
	public static TimeScopeDTO getTimeScope(FormDataDTO formData, String fieldAlias) {
		LocalDateTime startTime;
		LocalDateTime endTime;
		List<FormDataItemDTO> formDatas = formData.getFormData();
		List<String> activityTimes = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(formDatas)) {
			for (FormDataItemDTO data : formDatas) {
				String alias = data.getAlias();
				if (Objects.equals(fieldAlias, alias)) {
					List<JSONObject> values = data.getValues();
					if (CollectionUtils.isNotEmpty(values)) {
						activityTimes.add(values.get(0).getString("val"));
					} else {
						activityTimes.add("");
					}
				}
			}
		}
		if (CollectionUtils.isEmpty(activityTimes)) {
			// 添加开始结束时间
			activityTimes.add("");
			activityTimes.add("");
		}
		String startTimeStr = activityTimes.get(0);
		String endTimeStr = activityTimes.get(1);
		if (StringUtils.isBlank(startTimeStr)) {
			startTime = null;
		} else {
			startTime = getTime(startTimeStr);
		}
		if (StringUtils.isBlank(endTimeStr)) {
			endTime = null;
		} else {
			endTime = getTime(endTimeStr);
		}
		return TimeScopeDTO.builder()
				.startTime(startTime)
				.endTime(endTime)
				.build();
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


}