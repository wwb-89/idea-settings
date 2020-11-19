package com.chaoxing.activity.util;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author wwb
 * @version ver 1.0
 * @className LocalDateTimeDeserializer
 * @description 由于 yyyy-MM-dd HH:mm:ss 并不能直接被 fastJSON 转换为 LocalDateTime 类型，因此我们需要自定义一个序列化执行器,LocalDateTime 反序列化（将前端传递的 yyyy-MM-dd HH:mm:ss 转换为 LocalDateTime 类型）
 * @blame wwb
 * @date 2020-11-19 09:46:25
 */
public class LocalDateTimeDeserializer implements ObjectDeserializer {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public LocalDateTime deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		String dateTimeStr = parser.getLexer().numberString();
		if (StringUtils.isEmpty(dateTimeStr)) {
			return null;
		}
		dateTimeStr = dateTimeStr.replaceAll("\"", "");
        return (LocalDateTime)DATE_TIME_FORMATTER.parse(dateTimeStr);
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}
}