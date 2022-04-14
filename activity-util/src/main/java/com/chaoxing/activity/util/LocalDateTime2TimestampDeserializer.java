package com.chaoxing.activity.util;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author wwb
 * @version ver 1.0
 * @className LocalDateTimeDeserializer
 * @description 由于 yyyy-MM-dd HH:mm:ss 并不能直接被 fastJSON 转换为 LocalDateTime 类型，因此我们需要自定义一个序列化执行器,LocalDateTime 反序列化（将前端传递的 yyyy-MM-dd HH:mm:ss 转换为 LocalDateTime 类型）
 * @blame wwb
 * @date 2020-11-19 09:46:25
 */
public class LocalDateTime2TimestampDeserializer implements ObjectDeserializer {

	@Override
	public LocalDateTime deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		long timestamp = parser.getLexer().longValue();
		Instant instant = Instant.ofEpochMilli(timestamp);
		ZoneId zone = ZoneId.systemDefault();
		return LocalDateTime.ofInstant(instant, zone);
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}
}