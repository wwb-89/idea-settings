package com.chaoxing.activity.service.manager.module;

import com.chaoxing.activity.dto.module.SignFormDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**报名签到服务
 * @author wwb
 * @version ver 1.0
 * @className SignApiService
 * @description
 * @blame wwb
 * @date 2020-11-11 10:35:53
 */
@Slf4j
@Service
public class SignApiService {

	/** 签到报名的地址 */
	private static final String CREATE_URL = "";

	/**创建报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 14:26:33
	 * @param signForm
	 * @return java.lang.Integer 签到报名id
	*/
	public Integer create(SignFormDTO signForm) {
		return 1;
	}

	/**更新报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 17:58:39
	 * @param signForm
	 * @return void
	*/
	public void update(SignFormDTO signForm) {

	}

}