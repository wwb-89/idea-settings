package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.SignUpFillInfoTypeMapper;
import com.chaoxing.activity.model.SignUpFillInfoType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**报名填报信息类型服务
 * @author wwb
 * @version ver 1.0
 * @className SignUpFillInfoTypeService
 * @description
 * @blame wwb
 * @date 2021-07-14 17:28:28
 */
@Slf4j
@Service
public class SignUpFillInfoTypeService {

	@Resource
	private SignUpFillInfoTypeMapper signUpFillInfoTypeMapper;

	/**根据模版组件id列表查询报名填报信息类型列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 17:31:00
	 * @param templateComponentIds
	 * @return java.util.List<com.chaoxing.activity.model.SignUpFillInfoType>
	*/
	public List<SignUpFillInfoType> listByTemplateComponentIds(List<Integer> templateComponentIds) {
		if (CollectionUtils.isNotEmpty(templateComponentIds)) {
			return signUpFillInfoTypeMapper.selectList(new LambdaQueryWrapper<SignUpFillInfoType>()
					.in(SignUpFillInfoType::getTemplateComponentId, templateComponentIds)
			);
		}
		return Lists.newArrayList();
	}

	/**批量
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 18:45:20
	 * @param signUpFillInfoTypes
	 * @return void
	*/
	public void batchAdd(List<SignUpFillInfoType> signUpFillInfoTypes) {
		if (CollectionUtils.isNotEmpty(signUpFillInfoTypes)) {
			signUpFillInfoTypeMapper.batchAdd(signUpFillInfoTypes);
		}
	}

	/**根据templateComponentId查询对应的报名信息填写类型
	* @Description 
	* @author huxiaolong
	* @Date 2021-08-17 18:23:48
	* @param templateComponentId
	* @return com.chaoxing.activity.model.SignUpFillInfoType
	*/
    public SignUpFillInfoType getByTemplateComponentId(Integer templateComponentId) {
		return signUpFillInfoTypeMapper.selectList(new LambdaQueryWrapper<SignUpFillInfoType>()
				.eq(SignUpFillInfoType::getTemplateComponentId, templateComponentId))
				.stream().findFirst().orElse(null);
    }


	public void add(SignUpFillInfoType signUpFillInfoType) {
    	signUpFillInfoType.formTemplateIds2WfwFormTemplateIds();
		signUpFillInfoTypeMapper.insert(signUpFillInfoType);
	}

	public void updateById(SignUpFillInfoType signUpFillInfoType) {
    	signUpFillInfoType.formTemplateIds2WfwFormTemplateIds();
		signUpFillInfoTypeMapper.updateById(signUpFillInfoType);
	}
}