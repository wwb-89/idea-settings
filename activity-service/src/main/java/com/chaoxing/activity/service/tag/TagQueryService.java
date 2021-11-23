package com.chaoxing.activity.service.tag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.MarketTagMapper;
import com.chaoxing.activity.mapper.OrgTagMapper;
import com.chaoxing.activity.mapper.TagMapper;
import com.chaoxing.activity.model.MarketTag;
import com.chaoxing.activity.model.OrgTag;
import com.chaoxing.activity.model.Tag;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**标签查询服务
 * @author wwb
 * @version ver 1.0
 * @className TagQueryService
 * @description
 * @blame wwb
 * @date 2021-11-23 16:49:49
 */
@Slf4j
@Service
public class TagQueryService {

    @Resource
    private TagMapper tagMapper;
    @Resource
    private OrgTagMapper orgTagMapper;
    @Resource
    private MarketTagMapper marketTagMapper;

    public Tag getByName(String name) {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, name)
        );
        return tags.stream().findFirst().orElse(null);
    }

    public List<Tag> listByNames(List<String> names) {
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .in(Tag::getName, names)
        );
    }

    public List<Tag> listByIds(List<Integer> ids) {
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .in(Tag::getId, ids)
        );
    }

    public OrgTag getOrgTag(Integer fid, Integer tagId) {
        List<OrgTag> orgTags = orgTagMapper.selectList(new LambdaQueryWrapper<OrgTag>()
                .eq(OrgTag::getFid, fid)
                .eq(OrgTag::getTagId, tagId)
        );
        return orgTags.stream().findFirst().orElse(null);
    }

    public List<Tag> listOrgTag(Integer fid) {
        List<OrgTag> orgTags = orgTagMapper.selectList(new LambdaQueryWrapper<OrgTag>()
                .eq(OrgTag::getFid, fid)
        );
        Set<Integer> tagIds = orgTags.stream().map(OrgTag::getTagId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(tagIds)) {
            return Lists.newArrayList();
        }
        return listByIds(Lists.newArrayList(tagIds));
    }

    public MarketTag getMarketTag(Integer marketId, Integer tagId) {
        List<MarketTag> marketTags = marketTagMapper.selectList(new LambdaQueryWrapper<MarketTag>()
                .eq(MarketTag::getMarketId, marketId)
                .eq(MarketTag::getTagId, tagId)
        );
        return marketTags.stream().findFirst().orElse(null);
    }

    public List<Tag> listMarketTag(Integer marketId) {
        List<MarketTag> marketTags = marketTagMapper.selectList(new LambdaQueryWrapper<MarketTag>()
                .eq(MarketTag::getMarketId, marketId)
        );
        Set<Integer> tagIds = marketTags.stream().map(MarketTag::getTagId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(tagIds)) {
            return Lists.newArrayList();
        }
        return listByIds(Lists.newArrayList(tagIds));
    }

}