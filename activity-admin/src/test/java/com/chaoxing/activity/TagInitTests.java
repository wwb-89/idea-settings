package com.chaoxing.activity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.tag.TagHandleService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className TagInitTests
 * @description
 * @blame wwb
 * @date 2021-11-24 19:06:06
 */
@SpringBootTest
public class TagInitTests {

    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private TagHandleService tagHandleService;

    @Test
    public void init() {
        // 查询所有的活动标签
        List<Activity> activities = activityMapper.selectList(new LambdaQueryWrapper<Activity>()
                .isNotNull(Activity::getTags)
                .ne(Activity::getTags, "")
        );
        for (Activity activity : activities) {
            String tags = activity.getTags();
            if (StringUtils.isBlank(tags)) {
                break;
            }
            List<String> tagNames = Lists.newArrayList(tags.split(CommonConstant.DEFAULT_SEPARATOR));
            Integer marketId = activity.getMarketId();
            if (marketId == null) {
                tagHandleService.orgAssociateTags(activity.getCreateFid(), tagNames);
            } else {
                tagHandleService.marketAssociateTags(marketId, tagNames);
            }
            tagHandleService.activityAssociateTags(activity.getId(), tagNames);
        }
    }

}
