package com.chaoxing.activity.service.user.action;

import com.chaoxing.activity.mapper.UserActionDetailMapper;
import com.chaoxing.activity.mapper.UserActionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户行为处理服务
 * @author wwb
 * @version ver 1.0
 * @className UserActionHandleService
 * @description
 * @blame wwb
 * @date 2021-06-17 14:07:54
 */
@Slf4j
@Service
public class UserActionHandleService {

	@Resource
	private UserActionMapper userActionMapper;
	@Resource
	private UserActionDetailMapper userActionDetailMapper;

	@Resource
	private UserActionQueryService userActionQueryService;

	/**更新用户的行为
	 * @Description
	 * 1、当用户行为变更都会重新计算用户的总分
	 * @author wwb
	 * @Date 2021-06-17 14:12:36
	 * @param uid
	 * @param activityId
	 * @return void
	*/
	public void updateUserAction(Integer uid, Integer activityId) {

	}

	/**更新用户行为详情
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-17 16:38:24
	 * @param uid
	 * @param activityId
	 * @return void
	*/
	public void updateUserActionDetail(Integer uid, Integer activityId) {

	}

}
