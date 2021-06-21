package com.chaoxing.activity.service.user.action;

import com.chaoxing.activity.mapper.UserActionMapper;
import com.chaoxing.activity.mapper.UserResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户行为查询服务
 * @author wwb
 * @version ver 1.0
 * @className UserActionQueryService
 * @description
 * @blame wwb
 * @date 2021-06-17 14:07:42
 */
@Slf4j
@Service
public class UserActionQueryService {

	@Resource
	private UserResultMapper userResultMapper;
	@Resource
	private UserActionMapper userActionMapper;



}
