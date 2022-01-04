package com.chaoxing.activity.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.AddressDTO;
import com.chaoxing.activity.dto.DepartmentDTO;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.manager.form.*;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**表单工具类
 * @author wwb
 * @version ver 1.0
 * @className WfwFormUtils
 * @description
 * @blame wwb
 * @date 2021-03-11 15:09:05
 */
public class WfwFormUtils {

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

	private WfwFormUtils() {

	}

	/**获取参数加密串
	 * @Description
	 * @author wwb
	 * @Date 2021-12-13 14:57:02
	 * @param paramMap
	 * @param key
	 * @return java.lang.String
	 */
	public static String getEnc(TreeMap<String, Object> paramMap, String key) {
		StringBuilder enc = new StringBuilder();
		for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
			enc.append("[").append(entry.getKey()).append("=")
					.append(entry.getValue()).append("]");
		}
		return DigestUtils.md5Hex(enc + "[" + key + "]");
	}

	/**表单是否存在某个字段
	 * @Description 
	 * @author wwb
	 * @Date 2021-12-02 13:16:55
	 * @param formDataDto
	 * @param alias
	 * @return boolean
	*/
	public static boolean isExistField(FormDataDTO formDataDto, String alias) {
		List<FormDataItemDTO> items = formDataDto.getFormData();
		if (CollectionUtils.isNotEmpty(items)) {
			for (FormDataItemDTO item : items) {
				if (Objects.equals(item.getAlias(), alias)) {
					return true;
				}
			}
		}
		return false;
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
		return getValue(formDataDto.getFormData(), alias);
	}

	public static String getValue(List<FormDataItemDTO> formDataItems, String alias) {
		String value = "";
		JSONObject jsonValue = getJsonValue(formDataItems, alias);
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

	/**获取图片
	 * @Description 
	 * @author wwb
	 * @Date 2021-12-20 18:35:52
	 * @param formDataDto
	 * @param alias
	 * @return com.chaoxing.activity.dto.manager.form.FormImageDTO
	*/
	public static FormImageDTO getImage(FormDataDTO formDataDto, String alias) {
		JSONObject jsonValue = getJsonValue(formDataDto, alias);
		if (jsonValue != null) {
			return JSON.parseObject(jsonValue.toJSONString(), FormImageDTO.class);
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
		return getJsonValue(formData.getFormData(), alias);
	}

	private static JSONObject getJsonValue(List<FormDataItemDTO> items, String alias) {
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
			for (DateTimeFormatter dateTimeFormatter : WfwFormUtils.FORM_DATE_TIME_FORMATTERS) {
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
			for (DateTimeFormatter dateTimeFormatter : WfwFormUtils.FORM_DATE_TIME_FORMATTERS) {
				try {
					result = LocalDateTime.parse(value, dateTimeFormatter);
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

	public static LocalDateTime getTime(String value) {
		LocalDateTime result = null;
		if (StringUtils.isNotBlank(value)) {
			for (DateTimeFormatter dateTimeFormatter : WfwFormUtils.FORM_DATE_TIME_FORMATTERS) {
				try {
					result = LocalDateTime.parse(value, dateTimeFormatter);
				} catch (Exception e) {
				}
			}
		}
		return result;
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

	/**获取时间区间
	 * @Description
	 * @author wwb
	 * @Date 2021-06-11 18:15:41
	 * @param formData
	 * @param fieldAlias
	 * @return com.chaoxing.activity.dto.TimeScopeDTO
	 */
	public static TimeScopeDTO getTimeScope(FormDataDTO formData, String fieldAlias) {
		List<FormDataItemDTO> formDatas = formData.getFormData();
		return getTimeScope(formDatas, fieldAlias);
	}

	public static TimeScopeDTO getTimeScope(List<FormDataItemDTO> formDatas, String fieldAlias) {
		List<String> activityTimes = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(formDatas)) {
			for (FormDataItemDTO data : formDatas) {
				String alias = data.getAlias();
				if (Objects.equals(fieldAlias, alias)) {
					List<FormDataItemDTO.FieldDTO> fields = data.getFields();
					if (CollectionUtils.isNotEmpty(fields)) {
						activityTimes.add(fields.get(0).getValues().get(0).getString("val"));
						activityTimes.add(fields.get(1).getValues().get(0).getString("val"));
						break;
					} else {
						List<JSONObject> values = data.getValues();
						if (CollectionUtils.isNotEmpty(values)) {
							activityTimes.add(values.get(0).getString("val"));
						} else {
							activityTimes.add("");
						}
					}
				}
			}
		}
		if (CollectionUtils.isEmpty(activityTimes)) {
			return null;
		}
		String startTimeStr = activityTimes.get(0);
		String endTimeStr = activityTimes.get(1);
		if (StringUtils.isBlank(startTimeStr) || StringUtils.isBlank(endTimeStr)) {
			return null;
		}
		LocalDateTime startTime = getTime(startTimeStr);
		LocalDateTime endTime = getTime(endTimeStr);
		return TimeScopeDTO.builder()
				.startTime(startTime)
				.endTime(endTime)
				.build();
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
		AddressDTO addressDto = null;
		JSONObject jsonValue = getJsonValue(formData, fieldAlias);
		if (jsonValue != null) {
			String address = jsonValue.getString("address");
			if (StringUtils.isNotBlank(address)) {
				addressDto = AddressDTO.builder()
						.address(address)
						.lat(jsonValue.getBigDecimal("lat"))
						.lng(jsonValue.getBigDecimal("lng"))
						.build();
			}
		}
		return addressDto;
	}

	/**获取下拉字段的选项列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-02 18:17:46
	 * @param fs
	 * @return java.util.List<java.lang.String>
	 */
	public static List<String> getOptionsFormStructure(FormStructureDTO fs) {
		JSONObject field = fs.getField();
		List<String> options = Lists.newArrayList();
		if (field == null) {
			return options;
		}
		JSONArray optionsJsonArray = field.getJSONArray("options");
		if (CollectionUtils.isNotEmpty(optionsJsonArray)) {
			optionsJsonArray.forEach(o -> {
				JSONObject option = (JSONObject) o;
				String type = option.getString("title");
				if (StringUtils.isNotBlank(type)) {
					options.add(type);
				}
			});
		}
		return options;
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

	/**获取部门列表信息
	 * @Description
	 * @author wwb
	 * @Date 2021-08-31 14:25:47
	 * @param formDataDto
	 * @param alias
	 * @return java.util.List<com.chaoxing.activity.dto.DepartmentDTO>
	 */
	public static List<DepartmentDTO> listDepartment(FormDataDTO formDataDto, String alias) {
		List<DepartmentDTO> departments = Lists.newArrayList();
		List<JSONObject> jsonValues = listJsonValue(formDataDto, alias);
		if (CollectionUtils.isNotEmpty(jsonValues)) {
			for (JSONObject jsonValue : jsonValues) {
				if (jsonValue != null) {
					String departmentIdStr = jsonValue.getString(DEPARTMENT_ID_KEY);
					if (StringUtils.isNotBlank(departmentIdStr)) {
						departments.add(DepartmentDTO.builder()
								.id(Integer.parseInt(departmentIdStr))
								.name(jsonValue.getString(DEPARTMENT_NAME_KEY))
								.build());
					}
				}
			}
		}
		return departments;
	}

}