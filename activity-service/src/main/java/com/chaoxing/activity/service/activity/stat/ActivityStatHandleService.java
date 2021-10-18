package com.chaoxing.activity.service.activity.stat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.TimeScopeDTO;
import com.chaoxing.activity.dto.stat.ActivityStatDTO;
import com.chaoxing.activity.mapper.ActivityStatMapper;
import com.chaoxing.activity.mapper.ActivityStatTaskDetailMapper;
import com.chaoxing.activity.mapper.ActivityStatTaskMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityStat;
import com.chaoxing.activity.model.ActivityStatTask;
import com.chaoxing.activity.model.ActivityStatTaskDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.stat.ActivityStatQueryService;
import com.chaoxing.activity.util.CalculateUtils;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/10 4:56 下午
 * <p>
 */
@Slf4j
@Service
public class ActivityStatHandleService {

    @Resource
    private DistributedLock distributedLock;

    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private ActivityStatQueryService activityStatQueryService;

    @Autowired
    private ActivityStatMapper activityStatMapper;

    @Autowired
    private ActivityStatTaskMapper activityStatTaskMapper;

    @Autowired
    private ActivityStatTaskDetailMapper activityStatTaskDetailMapper;



    private static final String ACTIVITY_STAT_LOCK_CACHE_KEY_PREFIX =  CacheConstant.LOCK_CACHE_KEY_PREFIX + "activity_stat" +  CacheConstant.CACHE_KEY_SEPARATOR;

    private String getTaskExcuteLockKey(Integer taskId) {
        return  ACTIVITY_STAT_LOCK_CACHE_KEY_PREFIX+ taskId;
    }

    /**添加所有活动的统计任务, 返回任务id集合
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-09 17:29:53
    * @param 
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public List<Integer> reAddAllActivityStat() {
        List<Activity> activities = activityQueryService.list();
        Map<String, List<Integer>> dailyActivityMap = Maps.newHashMap();
        Set<String> dailySet = Sets.newHashSet();
        // 遍历所有活动的起始时间，获取不重复的活动日期set、 活动日期下对应的活动id集
        for (Activity activity : activities) {
            TimeScopeDTO activityStatTimeScope = activityStatQueryService.getActivityStatTimeScope(activity);
            List<String> daily = DateUtils.listEveryDay(activityStatTimeScope.getStartTime(), activityStatTimeScope.getEndTime());
            dailySet.addAll(daily);
            for (String s : daily) {
                dailyActivityMap.computeIfAbsent(s, k -> Lists.newArrayList());
                dailyActivityMap.get(s).add(activity.getId());
            }
        }
        // 按日期进行排序，便于任务的生成
        List<String> sortedDaily = dailySet.stream().sorted().collect(Collectors.toList());
        List<ActivityStatTask> activityStatTasks = Lists.newArrayList();
        for (String s : sortedDaily) {
            LocalDate statDate = LocalDate.parse(s, DateUtils.DAY_DATE_TIME_FORMATTER);
            activityStatTasks.add(ActivityStatTask.builder()
                    .date(statDate)
                    .status(ActivityStatTask.Status.WAIT_HANDLE.getValue())
                    .build());
        }
        activityStatTaskMapper.batchAdd(activityStatTasks);

        List<ActivityStatTaskDetail> activityStatTaskDetails = Lists.newArrayList();
        List<Integer> taskIds = Lists.newArrayList();
        for (ActivityStatTask task : activityStatTasks) {
            taskIds.add(task.getId());
            List<Integer> activityIds = dailyActivityMap.get(task.getDate().format(DateUtils.DAY_DATE_TIME_FORMATTER));
            if (CollectionUtils.isNotEmpty(activityIds)) {
                for (Integer activityId : activityIds) {
                    activityStatTaskDetails.add(ActivityStatTaskDetail.builder()
                            .activityId(activityId)
                            .taskId(task.getId())
                            .build());
                }
            }

        }
        activityStatTaskDetailMapper.batchAdd(activityStatTaskDetails);

        return taskIds;
    }

    public Integer addActivityStatTask() {
        // 获取前一天的日期作为活动统计日期
        LocalDate taskDate = LocalDate.now().plusDays(-1);

        // 根据活动统计日期，查询统计日期中进行中(或当天进行过的)活动
        List<Integer> activityIds = activityQueryService.listByActivityDate(taskDate);

        if (CollectionUtils.isEmpty(activityIds)) {
            return null;
        }

        ActivityStatTask statTask = ActivityStatTask.builder()
                .date(taskDate)
                .status(ActivityStatTask.Status.WAIT_HANDLE.getValue())
                .build();
        activityStatTaskMapper.insert(statTask);
        Integer taskId = statTask.getId();

        List<ActivityStatTaskDetail> activityStatTaskDetails = new ArrayList<>();
        activityIds.forEach(activityId -> activityStatTaskDetails.add(ActivityStatTaskDetail.builder()
                    .activityId(activityId)
                    .taskId(taskId)
                    .build()));
        activityStatTaskDetailMapper.batchAdd(activityStatTaskDetails);
        return taskId;
    }

    public boolean handleTask(Integer taskId) {
        // 上锁、同一个任务同时只能一个线程处理
        String lockKey = getTaskExcuteLockKey(taskId);
        Consumer<Exception> fail = (e) -> {
            log.error("操作任务:{} error:{}", taskId, e.getMessage());
            throw new BusinessException("操作任务失败");
        };
        return distributedLock.lock(lockKey, () -> {
            ActivityStatTask task = activityStatTaskMapper.selectById(taskId);
            Integer status = task.getStatus();
            ActivityStatTask.Status statusEnum = ActivityStatTask.Status.fromValue(status);

            if (Objects.equals(ActivityStatTask.Status.WAIT_HANDLE, statusEnum)) {
                // 活动统计处理中
                return handleActivityStat(task);
            }
            return Objects.equals(ActivityStatTask.Status.SUCCESS, statusEnum);
        }, fail);
    }

    private boolean handleActivityStat(ActivityStatTask statTask) {
        Integer taskId = statTask.getId();
        // 默认初始待处理
        Integer status = ActivityStatTask.Status.WAIT_HANDLE.getValue();
        try {
            // 根据任务id查询待处理任务详情
            List<ActivityStatTaskDetail> taskDetailList = activityStatTaskDetailMapper.selectList(
                    new QueryWrapper<ActivityStatTaskDetail>()
                    .lambda()
                    .eq(ActivityStatTaskDetail::getTaskId, taskId)
                    .eq(ActivityStatTaskDetail::getStatus, ActivityStatTaskDetail.Status.WAIT_HANDLE.getValue())
            );
            if (CollectionUtils.isNotEmpty(taskDetailList)) {
                int execSuccessNum = 0;
                for (ActivityStatTaskDetail detail : taskDetailList) {
                    boolean result = false;
                    int count = activityStatTaskDetailMapper.selectCount(new QueryWrapper<ActivityStatTaskDetail>()
                            .lambda()
                            .lt(ActivityStatTaskDetail::getTaskId, taskId)
                            .eq(ActivityStatTaskDetail::getActivityId, detail.getActivityId())
                            .ne(ActivityStatTaskDetail::getStatus, ActivityStatTaskDetail.Status.SUCCESS.getValue()));
                    if (count == 0) {
                        // 5次最大尝试处理，成功则跳出处理循环
                        for (int i = 0; i < CommonConstant.MAX_ERROR_TIMES; i++) {
                            result = handleActivityStatItem(detail, statTask.getDate());
                            if (result) {
                                execSuccessNum++;
                                break;
                            }
                        }
                        if (!result) {
                            detail.setStatus(ActivityStatTaskDetail.Status.FAIL.getValue());
                            log.error("活动:" + detail.getActivityId() + "统计失败！异常信息:" + detail.getErrorMessage());
                        }
                    } else {
                        detail.setStatus(ActivityStatTaskDetail.Status.FAIL.getValue());
                    }
                    // 更新统计任务状态
                    activityStatTaskDetailMapper.update(null, new UpdateWrapper<ActivityStatTaskDetail>()
                            .lambda()
                            .eq(ActivityStatTaskDetail::getTaskId, detail.getTaskId())
                            .eq(ActivityStatTaskDetail::getActivityId, detail.getActivityId())
                            .set(ActivityStatTaskDetail::getStatus, detail.getStatus())
                            .set(ActivityStatTaskDetail::getErrorMessage, detail.getErrorMessage())
                    );
                    if (taskDetailList.size() == execSuccessNum) {
                        status = ActivityStatTask.Status.SUCCESS.getValue();
                    } else {
                        status = ActivityStatTask.Status.FAIL.getValue();
                    }
                }
            }
        } catch (BusinessException e) {
            log.error("活动:{}的统计error:{}", taskId, e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("活动:{}的统计error:{}", taskId, e.getMessage());
        }
        // 处理结束，更新任务状态
        activityStatTaskMapper.update(null, new UpdateWrapper<ActivityStatTask>()
                .lambda()
                .eq(ActivityStatTask::getId, taskId)
                .set(ActivityStatTask::getStatus, status)
        );
        // 若程序不为待处理状态，即失败或成功，都不再重新加入任务队列
        return !Objects.equals(ActivityStatTask.Status.WAIT_HANDLE.getValue(), status);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean handleActivityStatItem(ActivityStatTaskDetail detail, LocalDate statDate) {
        String errorMsg = "";
        try {
            Integer activityId = detail.getActivityId();
            ActivityStatDTO activityStatDTO = activityStatQueryService.activityStat(activityId);
            ActivityStat activityStat = ActivityStat.builder()
                    .activityId(detail.getActivityId())
                    .pv(activityStatDTO.getPv())
                    .signedInNum(activityStatDTO.getSignedInNum())
                    .signedUpNum(activityStatDTO.getSignedUpNum())
                    .statDate(statDate)
                    .build();

            LocalDate yesterday = statDate.plusDays(-1);
            ActivityStat yesterdayStat = activityStatQueryService.getActivityStatByStatDate(activityId, yesterday);
            int pvIncrement = 0, signedUpIncrement = 0, signedInIncrement = 0;
            if (yesterdayStat == null) {
                //  若不存在之前的统计记录，则增量为本身
                pvIncrement = activityStat.getPv();
                signedUpIncrement = activityStat.getSignedUpNum();
                signedInIncrement = activityStat.getSignedInNum();
            } else {
                pvIncrement = (int) CalculateUtils.sub(activityStat.getPv(), yesterdayStat.getPv());
                signedUpIncrement = (int) CalculateUtils.sub(activityStat.getSignedUpNum(), yesterdayStat.getSignedUpNum());
                signedInIncrement = (int) CalculateUtils.sub(activityStat.getSignedInNum(), yesterdayStat.getSignedInNum());
            }
            activityStat.setPvIncrement(pvIncrement);
            activityStat.setSignedUpIncrement(signedUpIncrement);
            activityStat.setSignedInIncrement(signedInIncrement);

            activityStatMapper.insert(activityStat);
            // 统计成功，则给任务设置成功
            detail.setStatus(ActivityStatTaskDetail.Status.SUCCESS.getValue());
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        detail.setErrorMessage(errorMsg);
        return Objects.equals(ActivityStatTaskDetail.Status.SUCCESS.getValue(), detail.getStatus());
    }
}
