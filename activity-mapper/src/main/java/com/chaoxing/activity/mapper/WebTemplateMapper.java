package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.WebTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TWebTemplateMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2020-11-23 19:27:42
 * @version: ver 1.0
 */
@Mapper
public interface WebTemplateMapper extends BaseMapper<WebTemplate> {

	/**查询所属的模板
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-31 17:20:11
	 * @param codes
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.WebTemplate>
	*/
	List<WebTemplate> listAffiliation(@Param("codes") List<String> codes, @Param("fid") Integer fid);

}