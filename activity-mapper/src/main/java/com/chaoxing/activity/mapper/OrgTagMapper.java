package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.OrgTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: OrgTagMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-11-23 16:38:48
 * @version: ver 1.0
 */
@Mapper
public interface OrgTagMapper extends BaseMapper<OrgTag> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-11-24 15:50:09
     * @param fid
     * @param tagIds
     * @return void
    */
    void batchAdd(@Param("fid") Integer fid, @Param("tagIds") List<Integer> tagIds);

}