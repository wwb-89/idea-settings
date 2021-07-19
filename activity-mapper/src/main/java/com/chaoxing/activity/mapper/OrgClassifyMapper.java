package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.OrgClassify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: OrgClassifyMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-07-19 15:03:35
 * @version: ver 1.0
 */
@Mapper
public interface OrgClassifyMapper extends BaseMapper<OrgClassify> {

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:14:48
	 * @param orgClassifies
	 * @return int
	*/
	int batachAdd(@Param("orgClassifies") List<OrgClassify> orgClassifies);

}