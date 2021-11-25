package com.chaoxing.activity.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.tag.TagHandleService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className TagApiController
 * @description
 * @blame wwb
 * @date 2021-11-24 19:31:18
 */
@RestController
@RequestMapping("tag")
public class TagApiController {

    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private TagHandleService tagHandleService;

    @RequestMapping("init")
    public RestRespDTO init() {
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
        return RestRespDTO.success();
    }

}
