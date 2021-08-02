package com.chaoxing.activity.service.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**微服务表单创建服务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormCreateApiService
 * @description
 * @blame wwb
 * @date 2021-07-09 10:59:34
 */
@Slf4j
@Service
public class WfwFormCreateApiService {

	private static final String SIGN = "deptManager_hdcp";
	private static final String KEY = "SObtv7P3d$UVuBkTjg";

	private static final String CREATE_URL = "http://m.oa.chaoxing.com/api/manager/third/user/login/apps/create?aprvAppId=&formId=&formType=&departmentId=&toUrl=&uid=&errorUrl=&enc=";

	@Resource
	private RestTemplate restTemplate;


}