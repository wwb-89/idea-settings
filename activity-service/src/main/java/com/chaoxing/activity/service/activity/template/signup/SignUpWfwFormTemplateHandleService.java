package com.chaoxing.activity.service.activity.template.signup;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.mapper.SignUpWfwFormTemplateMapper;
import com.chaoxing.activity.model.SignUpWfwFormTemplate;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**报名模板服务
 * @author wwb
 * @version ver 1.0
 * @className SignUpWfwFormTemplateHandleService
 * @description
 * @blame wwb
 * @date 2022-03-29 15:39:25
 */
@Slf4j
@Service
public class SignUpWfwFormTemplateHandleService {

	@Resource
	private SignUpWfwFormTemplateQueryService signUpWfwFormTemplateQueryService;
	@Resource
	private SignUpWfwFormTemplateMapper signUpWfwFormTemplateMapper;

	/**新增
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:54:00
	 * @param signUpWfwFormTemplate
	 * @param operateUser
	 * @return void
	*/
	public void add(SignUpWfwFormTemplate signUpWfwFormTemplate, OperateUserDTO operateUser) {
		Integer marketMaxSequence = signUpWfwFormTemplateQueryService.getMarketMaxSequence(signUpWfwFormTemplate.getMarketId());
		signUpWfwFormTemplate.setSequence(marketMaxSequence + 1);
		signUpWfwFormTemplateMapper.insert(signUpWfwFormTemplate);
	}

	/**修改
	 * @Description 目前就修改名称
	 * @author wwb
	 * @Date 2022-03-29 18:54:09
	 * @param signUpWfwFormTemplate
	 * @param operateUser
	 * @return void
	*/
	public void edit(SignUpWfwFormTemplate signUpWfwFormTemplate, OperateUserDTO operateUser) {
		signUpWfwFormTemplateMapper.update(null, new LambdaUpdateWrapper<SignUpWfwFormTemplate>()
				.eq(SignUpWfwFormTemplate::getId, signUpWfwFormTemplate.getId())
				.set(SignUpWfwFormTemplate::getName, signUpWfwFormTemplate.getName())
		);
	}

	/**启用
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:54:16
	 * @param id
	 * @param operateUser
	 * @return void
	*/
	public void enable(Integer id, OperateUserDTO operateUser) {
		signUpWfwFormTemplateMapper.update(null, new LambdaUpdateWrapper<SignUpWfwFormTemplate>()
				.eq(SignUpWfwFormTemplate::getId, id)
				.set(SignUpWfwFormTemplate::getEnable, true)
		);
	}

	/**禁用
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:54:23
	 * @param id
	 * @param operateUser
	 * @return void
	*/
	public void disable(Integer id, OperateUserDTO operateUser) {
		signUpWfwFormTemplateMapper.update(null, new LambdaUpdateWrapper<SignUpWfwFormTemplate>()
				.eq(SignUpWfwFormTemplate::getId, id)
				.set(SignUpWfwFormTemplate::getEnable, false)
		);
	}

	/**删除
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:54:31
	 * @param id
	 * @param operateUser
	 * @return void
	*/
	public void delete(Integer id, OperateUserDTO operateUser) {
		signUpWfwFormTemplateMapper.update(null, new LambdaUpdateWrapper<SignUpWfwFormTemplate>()
				.eq(SignUpWfwFormTemplate::getId, id)
				.set(SignUpWfwFormTemplate::getDeleted, true)
		);
	}

	/**排序
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:54:39
	 * @param ids
	 * @param operateUser
	 * @return void
	*/
	public void sort(List<Integer> ids, OperateUserDTO operateUser) {
		Map<Integer, Integer> sortMap = Maps.newHashMap();
		int sequence = 1;
		for (Integer id : ids) {
			sortMap.put(id, sequence++);
		}
		signUpWfwFormTemplateMapper.sort(sortMap);
	}

}