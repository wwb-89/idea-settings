package com.chaoxing.activity.service.tag;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.ActivityTagMapper;
import com.chaoxing.activity.mapper.MarketTagMapper;
import com.chaoxing.activity.mapper.OrgTagMapper;
import com.chaoxing.activity.mapper.TagMapper;
import com.chaoxing.activity.model.ActivityTag;
import com.chaoxing.activity.model.MarketTag;
import com.chaoxing.activity.model.OrgTag;
import com.chaoxing.activity.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**标签服务
 * @author wwb
 * @version ver 1.0
 * @className TagHandleService
 * @description
 * @blame wwb
 * @date 2021-11-23 16:49:23
 */
@Slf4j
@Service
public class TagHandleService {

    @Resource
    private TagMapper tagMapper;
    @Resource
    private OrgTagMapper orgTagMapper;
    @Resource
    private MarketTagMapper marketTagMapper;
    @Resource
    private ActivityTagMapper activityTagMapper;

    @Resource
    private TagQueryService tagQueryService;

    public Tag addTag(String name) {
        Tag tag = tagQueryService.getByName(name);
        if (tag == null) {
            tag = Tag.builder().name(name).build();
            tagMapper.insert(tag);
        }
        return tag;
    }

    public void orgAssociateTag(Integer fid, String name) {
        Tag tag = addTag(name);
        Integer tagId = tag.getId();
        OrgTag orgTag = tagQueryService.getOrgTag(fid, tagId);
        if (orgTag == null) {
            orgTag = OrgTag.builder()
                    .fid(fid)
                    .tagId(tagId)
                    .build();
            orgTagMapper.insert(orgTag);
        }
    }

    public void marketAssociateTag(Integer marketId, String name) {
        Tag tag = addTag(name);
        Integer tagId = tag.getId();
        MarketTag marketTag = tagQueryService.getMarketTag(marketId, tagId);
        if (marketTag == null) {
            marketTag = MarketTag.builder()
                    .marketId(marketId)
                    .tagId(tagId)
                    .build();
            marketTagMapper.insert(marketTag);
        }
    }

    public void activityAssociateTags(Integer activityId, List<String> tagNames) {
        // 先删除
        activityTagMapper.delete(new LambdaUpdateWrapper<ActivityTag>()
                .eq(ActivityTag::getActivityId, activityId)
        );
        if (CollectionUtils.isEmpty(tagNames)) {
            return;
        }
        List<Tag> tags = tagQueryService.listByNames(tagNames);
        if (CollectionUtils.isEmpty(tags)) {
            return;
        }
        List<ActivityTag> activityTags = tags.stream().map(tag -> ActivityTag.builder().activityId(activityId).tagId(tag.getId()).build()).collect(Collectors.toList());
        activityTagMapper.batchAdd(activityTags);
    }

}