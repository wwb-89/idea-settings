package com.chaoxing.activity.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * @author wwb
 * @version ver 1.0
 * @className LocalDateSerializer
 * @description
 * @blame wwb
 * @date 2020-11-19 10:00:24
 */
public class LocalDateSerializer implements ObjectSerializer {

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
		if (object != null) {
			LocalDate localDate = (LocalDate) object;
			//将localDateTime转换为中国区（+8）时间戳。
			serializer.write(localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli());
		} else {
			serializer.write(null);
		}
	}

}
