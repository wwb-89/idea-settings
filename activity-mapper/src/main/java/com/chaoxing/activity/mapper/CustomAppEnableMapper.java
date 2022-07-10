package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.CustomAppEnable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: CustomAppEnableMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-12-30 15:38:35
 * @version: ver 1.0
 */
@Mapper
public interface CustomAppEnableMapper extends BaseMapper<CustomAppEnable> {

    /**新增活动自定义应用配置启用
    * @Description
    * @author huxiaolong
    * @Date 2021-12-30 15:38:35
    * @param waitSaveEnables
    * @return void
    */
    void batchAdd(@Param("waitSaveEnables") List<CustomAppEnable> waitSaveEnables);
}