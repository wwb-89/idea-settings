package com.chaoxing.activity.util.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className JsonConfig
 * @description
 * @blame wwb
 * @date 2019-12-30 10:37:49
 */
@Configuration
public class JsonConfig implements WebMvcConfigurer {

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
		while(iterator.hasNext()){
			HttpMessageConverter<?> converter = iterator.next();
			if(converter instanceof MappingJackson2HttpMessageConverter){
				iterator.remove();
			}
		}
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(
				// 防止循环引用
				SerializerFeature.DisableCircularReferenceDetect,
				// 空集合返回[],不返回null
				SerializerFeature.WriteNullListAsEmpty,
				// 空字符串返回"",不返回null
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteMapNullValue
		);
		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

		//处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON);
		fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);

		converters.add(fastJsonHttpMessageConverter);
	}

}
