package com.chaoxing.activity.util;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author wwb
 * @version ver 1.0
 * @className LocalDateDeserializer
 * @description
 * @blame wwb
 * @date 2020-11-19 09:59:49
 */
public class LocalDateDeserializer implements ObjectDeserializer {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public LocalDate deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		String dateStr = parser.getLexer().numberString();
		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}
		dateStr = dateStr.replaceAll("\"", "");
		return (LocalDate)DATE_TIME_FORMATTER.parse(dateStr);
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}

}
