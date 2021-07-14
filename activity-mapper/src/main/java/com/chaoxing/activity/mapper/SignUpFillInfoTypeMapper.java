package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.SignUpFillInfoType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: SignUpFillInfoTypeMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:13
 * @version: ver 1.0
 */
@Mapper
public interface SignUpFillInfoTypeMapper extends BaseMapper<SignUpFillInfoType> {

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 18:27:02
	 * @param signUpFillInfoTypes
	 * @return int
	*/
	int batchAdd(@Param("signUpFillInfoTypes") List<SignUpFillInfoType> signUpFillInfoTypes);

}