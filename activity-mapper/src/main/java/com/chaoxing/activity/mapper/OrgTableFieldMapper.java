package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.OrgTableField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TOrgTableFieldMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-05-24 16:02:38
 * @version: ver 1.0
 */
@Mapper
public interface OrgTableFieldMapper extends BaseMapper<OrgTableField> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-05-28 14:47:29
     * @param orgTableFields
     * @return int
    */
    int batchAdd(@Param("orgTableFields") List<OrgTableField> orgTableFields);

}