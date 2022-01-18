package com.chaoxing.activity.service.activity.menu;

import com.chaoxing.activity.mapper.ActivityCustomAppConfigMapper;
import com.chaoxing.activity.model.ActivityCustomAppConfig;
import com.chaoxing.activity.model.CustomAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**活动自定义菜单配置查询服务
 * @description:
 * @author: huxiaolong
 * @date: 2022/1/17 10:55 上午
 * @version: 1.0
 */
@Slf4j
@Service
public class ActivityCustomAppConfigQueryService {

    @Resource
    private ActivityCustomAppConfigMapper activityCustomAppConfigMapper;

    /**查询活动自定义菜单
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-18 14:37:45
     * @param activityId
     * @return 
     */
    public List<ActivityCustomAppConfig> listActivityCustomApp(Integer activityId) {
        return activityCustomAppConfigMapper.listActivityAppWithCloudId(activityId);
    }

}
