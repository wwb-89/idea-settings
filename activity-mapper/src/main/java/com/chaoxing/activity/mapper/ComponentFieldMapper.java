package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ComponentField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ComponentFieldMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:01
 * @version: ver 1.0
 */
@Mapper
public interface ComponentFieldMapper extends BaseMapper<ComponentField> {

    /**批量新增
    * @Description
    * @author huxiaolong
    * @Date 2021-07-13 16:15:51
    * @param fieldList
    * @return void
    */
    void batchAdd(@Param("fieldList") List<ComponentField> fieldList);
}