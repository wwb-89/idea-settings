package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TOrgDataRepoConfigDetailMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-19 11:09:56
 * @version: ver 1.0
 */
@Mapper
public interface OrgDataRepoConfigDetailMapper extends BaseMapper<OrgDataRepoConfigDetail> {

    /***根据机构fid和数据类型type查找对应的数据仓库配置详情
    * @Description
    * @author huxiaolong
    * @Date 2021-05-19 14:20:41
    * @param fid
    * @param dataType
    * @return java.util.List<com.chaoxing.activity.model.OrgDataRepoConfigDetail>
    */
    List<OrgDataRepoConfigDetail> listParticipateTimeConfigDetail(@Param("fid") Integer fid,
                                                                  @Param("dataType") String dataType);
}