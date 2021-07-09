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

}