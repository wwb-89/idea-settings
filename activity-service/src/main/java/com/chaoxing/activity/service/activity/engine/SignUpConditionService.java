package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.SignUpConditionMapper;
import com.chaoxing.activity.model.SignUpCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**报名条件服务
 * @author wwb
 * @version ver 1.0
 * @className SignUpConditionService
 * @description
 * @blame wwb
 * @date 2021-07-09 17:28:30
 */
@Slf4j
@Service
public class SignUpConditionService {

	@Resource
	private SignUpConditionMapper signUpConditionMapper;

	/**新增报名条件
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-09 17:39:49
	 * @param signUpCondition
	 * @return void
	*/
	public void add(SignUpCondition signUpCondition) {
		signUpConditionMapper.insert(signUpCondition);
	}

	/**批量新增报名条件
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-09 17:40:04
	 * @param signUpConditions
	 * @return void
	*/
	public void batchAdd(List<SignUpCondition> signUpConditions) {
		signUpConditionMapper.batchAdd(signUpConditions);
	}

	/**根据模版组件id删除
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-09 17:33:00
	 * @param templateComponentId
	 * @return void
	*/
	public void deleteByTemplateComponentId(Integer templateComponentId) {
		signUpConditionMapper.delete(new LambdaUpdateWrapper<SignUpCondition>()
			.eq(SignUpCondition::getTemplateId, templateComponentId)
		);
	}

}