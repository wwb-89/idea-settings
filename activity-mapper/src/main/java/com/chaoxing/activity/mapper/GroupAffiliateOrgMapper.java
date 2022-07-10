package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.GroupAffiliateOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TGroupAffiliateOrgMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2020-11-20 20:20:57
 * @version: ver 1.0
 */
@Mapper
public interface GroupAffiliateOrgMapper extends BaseMapper<GroupAffiliateOrg> {

	/**根据group code查询fid列表 根据t_group的code来查询附属机构的列表的id
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-22 13:39:23
	 * @param groupCode
	 * @return java.util.List<java.lang.Integer>
	*/
	List<Integer> listFidByGroupCode(@Param("groupCode") String groupCode);

}