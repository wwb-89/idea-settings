package com.chaoxing.activity.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**security安全配置
 * @author wwb
 * @version ver 1.0
 * @className SecurityConfig
 * @description 使用security来配置actuator的安全配置
 * @blame wwb
 * @date 2021-03-22 15:23:07
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${management.access.ip-white-list}")
	private String ipWhiteList;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//得到iplist列表
		String ipRule = "";
		//hasIpAddress('10.0.0.0/16') or hasIpAddress('127.0.0.1/32')
		String[] splitAddress = ipWhiteList.split(",");
		for (String ip : splitAddress) {
			if (ipRule.equals("")) {
				ipRule = "hasIpAddress('" + ip + "')";
			} else {
				ipRule += " or hasIpAddress('" + ip + "')";
			}
		}
		// ip范围内才能访问指定页面，剩下的允许访问
		http
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/engine-actuator/**").access(ipRule)
				.anyRequest().permitAll();
	}

}