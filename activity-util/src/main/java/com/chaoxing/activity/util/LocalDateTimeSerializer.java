package com.chaoxing.activity.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author wwb
 * @version ver 1.0
 * @className LocalDateTimeSerializer
 * @description 由于 LocalDateTime 类型在转换 JSON 的时候，并不能被转换为字符串，使用 @JsonFormat 只能转换为指定的 pattern 类型，因此我们需要自定义一个序列化执行器,LocalDateTime 序列化（将 LocalDateTime类型 转换为 时间戳 返回给前端 ）
 * @blame wwb
 * @date 2020-11-19 09:43:43
 */
public class LocalDateTimeSerializer implements ObjectSerializer {

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
		if (object != null) {
			LocalDateTime localDateTime = (LocalDateTime) object;
			//将localDateTime转换为中国区（+8）时间戳。
			serializer.write(localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
		} else {
			serializer.write(null);
		}
	}
}