package com.chaoxing.activity.service.blacklist;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.blacklist.BlacklistDTO;
import com.chaoxing.activity.dto.blacklist.BlacklistRuleDTO;
import com.chaoxing.activity.dto.stat.UserNotSignedInNumStatDTO;
import com.chaoxing.activity.mapper.BlacklistDetailMapper;
import com.chaoxing.activity.mapper.BlacklistMapper;
import com.chaoxing.activity.mapper.BlacklistRecordMapper;
import com.chaoxing.activity.mapper.BlacklistRuleMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoRemoveQueue;
import com.chaoxing.activity.service.queue.notice.handler.BlacklistUserNoticeHandleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className BlacklistHandleService
 * @description
 * @blame wwb
 * @date 2021-07-27 14:23:26
 */
@Slf4j
@Service
public class BlacklistHandleService {

    @Resource
    private BlacklistMapper blacklistMapper;
    @Resource
    private BlacklistRuleMapper blacklistRuleMapper;
    @Resource
    private BlacklistRecordMapper blacklistRecordMapper;

    @Resource
    private BlacklistValidationService blacklistValidationService;
    @Resource
    private BlacklistQueryService blacklistQueryService;
    @Resource
    private BlacklistAutoRemoveQueue blacklistAutoRemoveQueueService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private BlacklistUserNoticeHandleService blacklistUserNoticeHandleService;
    @Resource
    private BlacklistDetailMapper blacklistDetailMapper;

    /**新增或更新黑名单规则
     * @Description 
     * @author wwb
     * @Date 2021-07-27 15:00:20
     * @param blacklistRuleDto
     * @param operateUserDto
     * @return void
    */
    public void addOrUpdateBlacklistRule(BlacklistRuleDTO blacklistRuleDto, OperateUserDTO operateUserDto) {
        Integer marketId = blacklistRuleDto.getMarketId();
        blacklistValidationService.manageAble(marketId, operateUserDto);
        BlacklistRule existBlacklistRule = blacklistQueryService.getBlacklistRuleByMarketId(marketId);
        BlacklistRule blacklistRule= blacklistRuleDto.buildBlacklistRule();
        if (existBlacklistRule == null) {
            // 新增
            blacklistRuleMapper.insert(blacklistRule);
        } else {
            // 修改
            blacklistRuleMapper.update(null, new LambdaUpdateWrapper<BlacklistRule>()
                    .eq(BlacklistRule::getId, existBlacklistRule.getId())
                    .set(BlacklistRule::getNotSignInUpperLimit, blacklistRule.getNotSignInUpperLimit())
                    .set(BlacklistRule::getEnableAutoRemove, blacklistRule.getEnableAutoRemove())
                    .set(BlacklistRule::getAutoRemoveHours, blacklistRule.getAutoRemoveHours())
            );
        }
    }

    /**手动添加黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-27 17:34:30
     * @param marketId
     * @param blacklistDtos
     * @param operateUserDto
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void manualAddBlacklist(Integer marketId, List<BlacklistDTO> blacklistDtos, OperateUserDTO operateUserDto) {
        if (CollectionUtils.isEmpty(blacklistDtos)) {
            return;
        }
        blacklistDtos.forEach(v -> v.setJoinType(Blacklist.JoinTypeEnum.MANUAL.getValue()));
        blacklistValidationService.manageAble(marketId, operateUserDto);
        List<Integer> uids = blacklistDtos.stream().map(BlacklistDTO::getUid).collect(Collectors.toList());
        // 删除
        removeBlacklist(marketId, uids);
        List<Blacklist> blacklists = BlacklistDTO.buildBlacklist(blacklistDtos);
        blacklistMapper.batchAdd(blacklists);
        // 添加手动添加黑名单通知
        blacklistUserNoticeHandleService.handleManualAddBlacklistNotice(uids, marketId);
    }

    /**手动移除黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-27 17:34:44
     * @param marketId
     * @param uid
     * @param operateUserDto
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void manualRemoveBlacklist(Integer marketId, Integer uid, OperateUserDTO operateUserDto) {
        blacklistValidationService.manageAble(marketId, operateUserDto);
        List<Blacklist> blacklists = blacklistMapper.selectList(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
                .eq(Blacklist::getUid, uid));

        blacklistMapper.delete(new LambdaUpdateWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
                .eq(Blacklist::getUid, uid));
        // 添加手动移除黑名单通知
        blacklistUserNoticeHandleService.handleBlacklistRemoveNotice(marketId, blacklists);
    }


    /**手动批量移除黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-27 17:34:59
     * @param marketId
     * @param uids
     * @param operateUserDto
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void manualBatchRemoveBlacklist(Integer marketId, List<Integer> uids, OperateUserDTO operateUserDto) {
        blacklistValidationService.manageAble(marketId, operateUserDto);
        if (CollectionUtils.isEmpty(uids)) {
            return;
        }
        List<Blacklist> blacklists = blacklistMapper.selectList(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
                .in(Blacklist::getUid, uids));

        blacklistMapper.delete(new LambdaUpdateWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
                .in(Blacklist::getUid, uids));
        // 添加手动移除黑名单通知
        blacklistUserNoticeHandleService.handleBlacklistRemoveNotice(marketId, blacklists);
        for (Integer uid : uids) {
            blacklistAutoRemoveQueueService.remove(marketId, uid);
        }
    }

    /**移除黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-27 17:35:29
     * @param marketId
     * @param uids
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void removeBlacklist(Integer marketId, List<Integer> uids) {
        if (CollectionUtils.isEmpty(uids)) {
            return;
        }
        blacklistMapper.delete(new LambdaUpdateWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
                .in(Blacklist::getUid, uids)
        );
        for (Integer uid : uids) {
            blacklistAutoRemoveQueueService.remove(marketId, uid);
        }
    }

    /**自动添加黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-27 19:56:06
     * @param marketId
     * @return java.util.List<com.chaoxing.activity.model.Blacklist>
    */
    @Transactional(rollbackFor = Exception.class)
    public List<Blacklist> autoAddBlacklist(Integer marketId) {
        List<Blacklist> blacklists = Lists.newArrayList();
        BlacklistRule blacklistRule = blacklistQueryService.getBlacklistRuleByMarketId(marketId);
        if (blacklistRule == null) {
            // 市场没有配置黑名单规则（当一个市场创建后可能没有黑名单规则数据）
            return blacklists;
        }
        Integer notSignInUpperLimit = blacklistRule.getNotSignInUpperLimit();
        if (notSignInUpperLimit == null) {
            return blacklists;
        }
        // 查询匹配的黑名单记录
        List<BlacklistRecord> blacklistRecords = blacklistQueryService.listUnHandledBlacklistRecordGroupByUid(marketId);
        List<BlacklistRecord> matchBlacklistRecords = blacklistRecords.stream().filter(v -> v.getNotSignedInNum() >= notSignInUpperLimit).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchBlacklistRecords)) {
            return blacklists;
        }
        List<Integer> uids = matchBlacklistRecords.stream().map(BlacklistRecord::getUid).collect(Collectors.toList());
        // 已经手动添加的忽略，其他的新增
        List<Integer> manualAddedUids = blacklistQueryService.listManualAddedUid(marketId);
        uids.removeAll(manualAddedUids);
        // 删除已经加入黑名单中的数据
        removeBlacklist(marketId, uids);
        blacklists = BlacklistRecord.buildbuildBlacklist(matchBlacklistRecords.stream().filter(v -> uids.contains(v.getUid())).collect(Collectors.toList()));
        Integer autoRemoveHours = blacklistRule.getAutoRemoveHours();
        if (CollectionUtils.isNotEmpty(blacklists)) {
            blacklists.forEach(v -> v.setEffectiveHours(autoRemoveHours));
            blacklistMapper.batchAdd(blacklists);
        }
        // 黑名单记录置为已处理
        blacklistRecordMapper.update(null, new LambdaUpdateWrapper<BlacklistRecord>()
                .eq(BlacklistRecord::getMarketId, marketId)
                .in(BlacklistRecord::getUid, matchBlacklistRecords.stream().map(BlacklistRecord::getUid).collect(Collectors.toList()))
                .set(BlacklistRecord::getHandled, true)
        );
        // 通知到时间后自动移除
        LocalDateTime removeTime = LocalDateTime.now().plusHours(autoRemoveHours);
        for (Blacklist blacklist : blacklists) {
            blacklistAutoRemoveQueueService.push(new BlacklistAutoRemoveQueue.QueueParamDTO(blacklist.getMarketId(), blacklist.getUid(), removeTime));
        }
        return blacklists;
    }

    /**自动移除黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-27 17:43:59
     * @param marketId
     * @param uid
     * @return void
    */
    public void autoRemoveBlacklist(Integer marketId, Integer uid) {
        blacklistMapper.delete(new LambdaUpdateWrapper<Blacklist>()
                .eq(Blacklist::getMarketId, marketId)
                .eq(Blacklist::getUid, uid)
        );
    }

    /**活动结束处理黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-27 20:17:01
     * @param activityId
     * @return java.util.List<com.chaoxing.activity.model.Blacklist>
    */
    @Transactional(rollbackFor = Exception.class)
    public List<Blacklist> activityEndHandleBlacklist(Integer activityId) {
        List<Blacklist> blacklists = Lists.newArrayList();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return blacklists;
        }
        Integer marketId = activity.getMarketId();
        if (marketId == null) {
            return blacklists;
        }
        List<UserNotSignedInNumStatDTO> userNotSignedInNumStatDtos = signApiService.statUserNotSignedInNum(activity.getSignId());
        if (CollectionUtils.isNotEmpty(userNotSignedInNumStatDtos)) {
            addBlacklistDetails(activityId, marketId, userNotSignedInNumStatDtos);
        }
        List<BlacklistRecord> blacklistRecords = UserNotSignedInNumStatDTO.buildBlacklistRecord(userNotSignedInNumStatDtos, marketId, activityId);
        if (CollectionUtils.isNotEmpty(blacklistRecords)) {
            blacklistRecordMapper.batchAdd(blacklistRecords);
        }
        return autoAddBlacklist(marketId);
    }

    /**批量保存违约记录
     * @Description 
     * @author huxiaolong
     * @Date 2022-03-01 16:07:14
     * @param activityId
     * @param marketId
     * @param userNotSignedInNumStatDtos
     * @return
     */
    public void addBlacklistDetails(Integer activityId, Integer marketId, List<UserNotSignedInNumStatDTO> userNotSignedInNumStatDtos) {
        List<BlacklistDetail> blacklistDetails = Lists.newArrayList();
        userNotSignedInNumStatDtos.forEach(v -> {
            if (CollectionUtils.isNotEmpty(v.getNotSignInInfos())) {
                List<BlacklistDetail> details = v.getNotSignInInfos().stream().map(u -> BlacklistDetail.builder()
                        .uid(v.getUid())
                        .marketId(marketId)
                        .activityId(activityId)
                        .userName(v.getUserName())
                        .account(v.getAccount())
                        .breachContent("未" + Optional.ofNullable(u.getSignInType()).orElse("签到") + ":" + u.getSignInName())
                        .build()).collect(Collectors.toList());
                blacklistDetails.addAll(details);
            }
        });
        if (CollectionUtils.isNotEmpty(blacklistDetails)) {
            blacklistDetailMapper.batchAdd(blacklistDetails);
        }
    }

}
