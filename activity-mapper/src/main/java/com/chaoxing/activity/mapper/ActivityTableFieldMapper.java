package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityTableField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TActivityTableFieldMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-24 16:02:38
 * @version: ver 1.0
 */
@Mapper
public interface ActivityTableFieldMapper extends BaseMapper<ActivityTableField> {

    /**批量新增
    * @Description
    * @author huxiaolong
    * @Date 2021-06-24 14:51:35
    * @param activityTableFields
    * @return void
    */
    int batchAdd(@Param("activityTableFields") List<ActivityTableField> activityTableFields);
}