package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.SignUpWfwFormTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @className: TSignUpWfwFormTemplateMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-11-18 16:09:24
 * @version: ver 1.0
 */
@Mapper
public interface SignUpWfwFormTemplateMapper extends BaseMapper<SignUpWfwFormTemplate> {

	/**
	 * 获取市场下模板的最大顺序
	 *
	 * @param marketId
	 * @return java.lang.Integer
	 * @Description
	 * @author wwb
	 * @Date 2022-03-29 18:57:20
	 */
	Integer getMarketMaxSequence(@Param("marketId") Integer marketId);

	/**排序
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 19:12:00
	 * @param sortIdMap
	 * @return java.lang.Integer
	*/
	Integer sort(@Param("sortIdMap") Map<Integer, Integer> sortIdMap);

}