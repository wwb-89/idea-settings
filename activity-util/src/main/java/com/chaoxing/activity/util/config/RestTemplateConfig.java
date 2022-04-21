package com.chaoxing.activity.util.config;

import com.chaoxing.activity.util.property.ProxyProperties;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**restTemplate配置
 * @author wwb
 * @version ver 1.0
 * @className RestTemplateConfig
 * @description
 * @blame wwb
 * @date 2020-11-09 11:20:44
 */
@Configuration
public class RestTemplateConfig {

	@Resource
	private ProxyProperties proxyProperties;

	private static final int CONNECT_TIMEOUT = 10 * 1000;
	private static final int LONG_TIME_READ_TIMEOUT = 60 * 1000;

	private RestTemplate longTimeRestTemplate(OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory) {
		okHttp3ClientHttpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT);
		okHttp3ClientHttpRequestFactory.setReadTimeout(LONG_TIME_READ_TIMEOUT);
		return new RestTemplate(okHttp3ClientHttpRequestFactory);
	}

	private OkHttp3ClientHttpRequestFactory proxyOkHttp3ClientHttpRequestFactory() {
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyProperties.getHost(), proxyProperties.getPort()));
		OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build().newBuilder().proxy(proxy).build();
		OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
		return okHttp3ClientHttpRequestFactory;
	}

	/**默认的
	 * @Description 
	 * @author wwb
	 * @Date 2022-04-20 11:36:04
	 * @param 
	 * @return org.springframework.web.client.RestTemplate
	*/
	@Bean
	@Primary
	public RestTemplate restTemplate(){
		OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory();
		return new RestTemplate(okHttp3ClientHttpRequestFactory);
	}

	/**长超时时间
	 * @Description 
	 * @author wwb
	 * @Date 2022-04-20 11:35:39
	 * @param 
	 * @return org.springframework.web.client.RestTemplate
	*/
	@Bean(name = "longTimeRestTemplate")
	public RestTemplate longTimeRestTemplate(){
		OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory();
		return longTimeRestTemplate(okHttp3ClientHttpRequestFactory);
	}

	/**添加代理
	 * @Description 
	 * @author wwb
	 * @Date 2022-04-20 11:35:52
	 * @param 
	 * @return org.springframework.web.client.RestTemplate
	*/
	@Bean(name = "restTemplateProxy")
	public RestTemplate restTemplateProxy(){
		if (proxyProperties.getEnable()) {
			OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = proxyOkHttp3ClientHttpRequestFactory();
			return new RestTemplate(okHttp3ClientHttpRequestFactory);
		}
		return restTemplate();
	}

	/**长超时时间代理
	 * @Description 
	 * @author wwb
	 * @Date 2022-04-20 11:35:45
	 * @param 
	 * @return org.springframework.web.client.RestTemplate
	*/
	@Bean(name = "longTimeRestTemplateProxy")
	public RestTemplate longTimeRestTemplateProxy(){
		if (proxyProperties.getEnable()) {
			OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = proxyOkHttp3ClientHttpRequestFactory();
			return longTimeRestTemplate(okHttp3ClientHttpRequestFactory);
		}
		return restTemplate();
	}

}