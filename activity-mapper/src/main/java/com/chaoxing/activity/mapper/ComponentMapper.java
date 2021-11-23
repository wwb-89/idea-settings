package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.Component;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * @className: ComponentMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:08
 * @version: ver 1.0
 */
@Mapper
public interface ComponentMapper extends BaseMapper<Component> {

    /**批量新增
    * @Description 
    * @author huxiaolong
    * @Date 2021-07-08 11:07:57
    * @param components
    * @return void
    */
    void batchAdd(@Param("components") Collection<Component> components);
}