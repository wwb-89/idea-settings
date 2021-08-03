package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.dto.activity.ActivityCollectionDTO;
import com.chaoxing.activity.model.ActivityCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TActivityCollectionMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-01-27 16:46:59
 * @version: ver 1.0
 */
@Mapper
public interface ActivityCollectionMapper extends BaseMapper<ActivityCollection> {

    
    /**根据activityIds，统计对应活动收藏的用户数量
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-03 14:18:59
    * @param activityIds
    * @return java.util.List<com.chaoxing.activity.dto.activity.ActivityCollectionDTO>
    */
    List<ActivityCollectionDTO> statCollectedByActivityIds(@Param("activityIds") List<Integer> activityIds);

}