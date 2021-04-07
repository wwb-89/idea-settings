package com.chaoxing.activity.util.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wwb
 * @version ver 1.0
 * @className ProxyProperties
 * @description
 * @blame wwb
 * @date 2020-12-17 10:08:37
 */
@Data
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {

	/** 主机地址 */
	private String host = "127.0.0.1";
	/** 端口 */
	private Integer port = 1090;
	/** 是否启用 */
	private Boolean enable = Boolean.FALSE;

}