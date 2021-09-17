package com.chaoxing.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ActivityFlagCodeMapper;
import com.chaoxing.activity.model.ActivityFlagCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/17 10:53
 * <p>
 */
@Service
public class ActivityFlagCodeService {

    @Autowired
    private ActivityFlagCodeMapper activityFlagCodeMapper;


    /**根据activityFlag查询区域code
    * @Description
    * @author huxiaolong
    * @Date 2021-09-17 11:08:58
    * @param flag
    * @return java.lang.String
    */
    public String getCodeByFlag(String flag) {
        if (StringUtils.isBlank(flag)) {
            return null;
        }
        ActivityFlagCode activityFlagCode = activityFlagCodeMapper.selectList(new LambdaQueryWrapper<ActivityFlagCode>()
                .eq(ActivityFlagCode::getActivityFlag, flag)).stream().findFirst().orElse(null);
        return Optional.ofNullable(activityFlagCode).map(ActivityFlagCode::getAreaCode).orElse(null);

    }
}
