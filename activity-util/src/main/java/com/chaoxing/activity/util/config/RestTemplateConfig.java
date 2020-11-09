package com.chaoxing.activity.util.config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author wwb
 * @version ver 1.0
 * @className RestTemplateConfig
 * @description
 * @blame wwb
 * @date 2020-11-09 11:20:44
 */
@Configuration
public class RestTemplateConfig {

	private static final String PROFILE_DEV = "dev";

	@Value("${spring.profiles.active}")
	private String active;

	private static final int CONNECT_TIMEOUT = 60 * 1000;

	@Bean
	@Primary
	public RestTemplate restTemplate(){
		OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory();
		// 连接超时时间60s
		okHttp3ClientHttpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT);
		return new RestTemplate(okHttp3ClientHttpRequestFactory);
	}

	@Bean(name = "restTemplateProxy")
	public RestTemplate restTemplateProxy(){
		if (PROFILE_DEV.equals(active)) {
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1090));
			OkHttpClient okHttpClient = new OkHttpClient().newBuilder().connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build().newBuilder().proxy(proxy).build();
			OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
			// 连接超时时间60s
			okHttp3ClientHttpRequestFactory.setConnectTimeout(60 * 1000);
			return new RestTemplate(okHttp3ClientHttpRequestFactory);
		}
		return restTemplate();
	}

}