package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityClassMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-09-02 17:03:27
 * @version: ver 1.0
 */
@Mapper
public interface ActivityClassMapper extends BaseMapper<ActivityClass> {
    
    /**批量新增
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-02 18:24:05
    * @param dataList
    * @return void
    */
    void batchAdd(@Param("dataList") List<ActivityClass> dataList);
}