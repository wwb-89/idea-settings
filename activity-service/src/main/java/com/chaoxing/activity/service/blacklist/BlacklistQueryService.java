package com.chaoxing.activity.service.blacklist;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.blacklist.BlacklistDTO;
import com.chaoxing.activity.dto.query.BlacklistQueryDTO;
import com.chaoxing.activity.mapper.BlacklistMapper;
import com.chaoxing.activity.mapper.BlacklistRecordMapper;
import com.chaoxing.activity.mapper.BlacklistRuleMapper;
import com.chaoxing.activity.model.Blacklist;
import com.chaoxing.activity.model.BlacklistRecord;
import com.chaoxing.activity.model.BlacklistRule;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**黑名单查询服务
 * @author wwb
 * @version ver 1.0
 * @className BlacklistQueryService
 * @description
 * @blame wwb
 * @date 2021-07-27 14:22:18
 */
@Slf4j
@Service
public class BlacklistQueryService {

    @Resource
    private BlacklistMapper blacklistMapper;
    @Resource
    private BlacklistRuleMapper blacklistRuleMapper;
    @Resource
    private BlacklistRecordMapper blacklistRecordMapper;

    /**根据活动市场id查询黑名单规则
     * @Description 
     * @author wwb
     * @Date 2021-07-27 14:47:44
     * @param marketId
     * @return com.chaoxing.activity.model.BlacklistRule
    */
    public BlacklistRule getBlacklistRuleByMarketId(Integer marketId) {
        List<BlacklistRule> blacklistRules = blacklistRuleMapper.selectList(new LambdaQueryWrapper<BlacklistRule>()
                .eq(BlacklistRule::getMarketId, marketId)
        );
        return Optional.ofNullable(blacklistRules).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

    /**查询市场已经添加黑名单的uid列表
     * @Description 
     * @author wwb
     * @Date 2021-07-27 16:15:34
     * @param marketId
     * @return java.util.List<java.lang.Integer>
    */
    public List<Integer> listBlacklistUid(Integer marketId) {
        List<Blacklist> blacklists = blacklistMapper.selectList(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
                .select(Blacklist::getUid)
        );
        return Optional.ofNullable(blacklists).orElse(Lists.newArrayList()).stream().map(Blacklist::getUid).collect(Collectors.toList());
    }

    /**查询活动市场下未处理的用户未报名记录
     * @Description 
     * @author wwb
     * @Date 2021-07-27 16:58:55
     * @param marketId
     * @return java.util.List<com.chaoxing.activity.model.BlacklistRecord>
    */
    public List<BlacklistRecord> listUnHandledBlacklistRecordGroupByUid(Integer marketId) {
        List<BlacklistRecord> result = Lists.newArrayList();
        List<BlacklistRecord> blacklistRecords = blacklistRecordMapper.selectList(new LambdaQueryWrapper<BlacklistRecord>()
                .eq(BlacklistRecord::getMarketId, marketId)
                .eq(BlacklistRecord::getHandled, false)
        );
        Map<Integer, List<BlacklistRecord>> uidBlacklistRecords = blacklistRecords.stream().collect(Collectors.groupingBy(BlacklistRecord::getUid));
        Set<Integer> uids = blacklistRecords.stream().map(BlacklistRecord::getUid).collect(Collectors.toSet());
        for (Integer uid : uids) {
            List<BlacklistRecord> userBlacklistRecords = uidBlacklistRecords.get(uid);
            int totalDefaultNum = 0;
            for (BlacklistRecord userBlacklistRecord : userBlacklistRecords) {
                totalDefaultNum += Optional.ofNullable(userBlacklistRecord.getNotSignedInNum()).orElse(0);
            }
            BlacklistRecord first = userBlacklistRecords.stream().findFirst().orElse(null);
            if (first != null) {
                first.setNotSignedInNum(totalDefaultNum);
                result.add(first);
            }
        }
        return result;
    }

    /**查询活动市场手动加入黑名单的uid列表
     * @Description 
     * @author wwb
     * @Date 2021-07-27 17:25:09
     * @param marketId
     * @return java.util.List<java.lang.Integer>
    */
    public List<Integer> listManualAddedUid(Integer marketId) {
        List<Blacklist> manualBlacklists = blacklistMapper.selectList(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
                .eq(Blacklist::getJoinType, Blacklist.JoinTypeEnum.MANUAL.getValue())
        );
        return Optional.ofNullable(manualBlacklists).orElse(Lists.newArrayList()).stream().map(Blacklist::getUid).collect(Collectors.toList());
    }

    /**分页查询黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-29 11:24:47
     * @param page
     * @param blacklistQueryDto
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page
    */
    public Page pagingBlacklist(Page page, BlacklistQueryDTO blacklistQueryDto) {
        page = blacklistMapper.pageBlacklist(page, blacklistQueryDto);
        List<BlacklistDTO> blacklistDtos = BlacklistDTO.buildFromBlacklist(page.getRecords());
        page.setRecords(blacklistDtos);
        return page;
    }

    /**查询用户黑名单信息
     * @Description 
     * @author wwb
     * @Date 2021-07-30 14:30:02
     * @param uid
     * @param marketId
     * @return com.chaoxing.activity.model.Blacklist
    */
    public Blacklist getUserBlacklist(Integer uid, Integer marketId) {
        List<Blacklist> blacklists = blacklistMapper.selectList(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
                .eq(Blacklist::getUid, uid)
        );
        return Optional.ofNullable(blacklists).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

    /**统计已经加入黑名单的人数
     * @Description 
     * @author wwb
     * @Date 2021-11-17 15:22:48
     * @param marketId
     * @return int
    */
    public int countMarketBlackNum(Integer marketId) {
        return blacklistMapper.selectCount(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
        );
    }

}
