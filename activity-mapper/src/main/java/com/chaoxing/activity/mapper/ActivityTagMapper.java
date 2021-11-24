package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityTagMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-11-23 16:38:48
 * @version: ver 1.0
 */
@Mapper
public interface ActivityTagMapper extends BaseMapper<ActivityTag> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-11-23 18:13:42
     * @param activityTags
     * @return int
    */
    int batchAdd(@Param("activityTags") List<ActivityTag> activityTags);

}