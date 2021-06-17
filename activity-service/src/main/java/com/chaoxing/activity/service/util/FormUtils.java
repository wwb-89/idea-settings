package com.chaoxing.activity.service.util;

import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.AddressDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
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
	 * @param fieldAlias
	 * @return java.lang.String
	*/
	public static String getValue(FormDTO formData, String fieldAlias) {
		String value = "";
		JSONObject jsonValue = getJsonValue(formData, fieldAlias);
		if (jsonValue != null) {
			value = jsonValue.getString(VAL_KEY);
		}
		value = Optional.ofNullable(value).orElse("");
		return value;
	}

	/**获取地址信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-15 01:43:33
	 * @param formData
	 * @param fieldAlias
	 * @return com.chaoxing.activity.dto.AddressDTO
	*/
	public static AddressDTO getAddress(FormDTO formData, String fieldAlias) {
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
	public static String getCloudId(FormDTO formData, String fieldAlias) {
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

	private static JSONObject getJsonValue(FormDTO formData, String fieldAlias) {
		List<FormDataDTO> items = formData.getFormData();
		if (CollectionUtils.isNotEmpty(items)) {
			for (FormDataDTO item : items) {
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

	/**从表单中获取日期
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-12 11:35:12
	 * @param formData
	 * @param fieldAlias 字段别名
	 * @return java.time.LocalDate
	*/
	public static LocalDate getDate(FormDTO formData, String fieldAlias) {
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
	public static LocalDateTime getTime(FormDTO formData, String fieldAlias) {
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
	public static TimeScopeDTO getTimeScope(FormDTO formData, String fieldAlias) {
		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		List<FormDataDTO> formDatas = formData.getFormData();
		List<String> activityTimes = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(formDatas)) {
			for (FormDataDTO data : formDatas) {
				String alias = data.getAlias();
				if (Objects.equals(fieldAlias, alias)) {
					activityTimes.add(data.getValues().get(0).getString("val"));
				}
			}
		}
		if (activityTimes.size() > 1) {
			String startTimeStr = activityTimes.get(0);
			String endTimeStr = activityTimes.get(1);
			startTime = getTime(startTimeStr);
			endTime = getTime(endTimeStr);
		}
		return TimeScopeDTO.builder()
				.startTime(startTime)
				.endTime(endTime)
				.build();
	}

}