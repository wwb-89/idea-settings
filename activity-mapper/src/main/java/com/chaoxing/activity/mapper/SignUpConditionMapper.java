package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.SignUpCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: SignUpConditionMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:18
 * @version: ver 1.0
 */
@Mapper
public interface SignUpConditionMapper extends BaseMapper<SignUpCondition> {

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-09 17:35:48
	 * @param signUpConditions
	 * @return int
	*/
	int batchAdd(@Param("signUpConditions") List<SignUpCondition> signUpConditions);

	/**
	* @Description 
	* @author huxiaolong
	* @Date 2021-07-14 11:55:30
	* @param templateId
	* @return java.util.List<java.lang.Integer>
	*/
    List<Integer> selectTemplateComponentIdByTemplateId(@Param("templateId") Integer templateId);


	/**查询活动启用的报名条件列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-03 16:06:30
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.model.SignUpCondition>
	 */
    List<SignUpCondition> listActivityEnableSignUpCondition(@Param("activityId") Integer activityId);
}