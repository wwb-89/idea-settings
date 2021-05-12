package com.chaoxing.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.stat.ActivityStatDTO;
import com.chaoxing.activity.mapper.ActivityStatMapper;
import com.chaoxing.activity.mapper.ActivityStatTaskDetailMapper;
import com.chaoxing.activity.mapper.ActivityStatTaskMapper;
import com.chaoxing.activity.model.ActivityStat;
import com.chaoxing.activity.model.ActivityStatTask;
import com.chaoxing.activity.model.ActivityStatTaskDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityStatQueryService;
import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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
            log.error("处理任务:{} error:{}", taskId, e.getMessage());
            throw new BusinessException("处理任务失败");
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
        // 默认初始待处理
        Integer status = ActivityStatTask.Status.WAIT_HANDLE.getValue();
        Integer taskId = statTask.getId();
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
                // 任务异常标识，默认false
                boolean taskFail = Boolean.FALSE;
                for (ActivityStatTaskDetail detail : taskDetailList) {
                    boolean result;
                    try {
                        // 获取任务详情处理结果
                        result = handleActivityStatItem(detail, statTask.getDate());
                    } catch (BusinessException e) {
                        // 仅当任务详情5次处理失败时，产生业务异常
                        activityStatTaskDetailMapper.update(null, new UpdateWrapper<ActivityStatTaskDetail>()
                                .lambda()
                                .eq(ActivityStatTaskDetail::getTaskId, detail.getTaskId())
                                .eq(ActivityStatTaskDetail::getActivityId, detail.getActivityId())
                                .set(ActivityStatTaskDetail::getStatus, ActivityStatTaskDetail.Status.FAIL.getValue())
                                .set(ActivityStatTaskDetail::getErrorTimes, detail.getErrorTimes())
                                .set(ActivityStatTaskDetail::getErrorMessage, detail.getErrorMessage())
                        );
                        result = Boolean.FALSE;
                        taskFail = Boolean.TRUE;
                    } catch (Exception e) {
                        // 其他异常应是插入活动统计记录或更新任务详情信息错误
                        result = Boolean.FALSE;
                    }
                    if (result) {
                        execSuccessNum++;
                    }
                }

                if (taskFail) {
                    status = ActivityStatTask.Status.FAIL.getValue();
                } else if (taskDetailList.size() == execSuccessNum) {
                    status = ActivityStatTask.Status.SUCCESS.getValue();
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
        return Objects.equals(ActivityStatTask.Status.SUCCESS.getValue(), status);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean handleActivityStatItem(ActivityStatTaskDetail detail, LocalDate statDate) {
        Integer activityId = detail.getActivityId();
        Integer errorTimes = detail.getErrorTimes();
        String errorMsg = "";
        ActivityStatDTO activityStatDTO = null;
        Integer status = ActivityStatTask.Status.WAIT_HANDLE.getValue();
        try {
            activityStatDTO = activityStatQueryService.activityStat(activityId);
        } catch (Exception e) {
            errorTimes++;
            errorMsg = e.getMessage();
            if (errorTimes >= CommonConstant.MAX_ERROR_TIMES) {
                detail.setErrorTimes(errorTimes);
                detail.setErrorMessage(errorMsg);
                throw new BusinessException("活动:" + activityId + "统计失败！异常信息:" + errorMsg);
            }
        }

        if (activityStatDTO != null) {
            ActivityStat activityStat = ActivityStat.builder().activityId(detail.getActivityId())
                    .pv(activityStatDTO.getPv())
                    .signedInNum(activityStatDTO.getSignedInNum())
                    .signedUpNum(activityStatDTO.getSignedUpNum())
                    .statDate(statDate)
                    .build();

            activityStatMapper.insert(activityStat);
            status = ActivityStatTask.Status.SUCCESS.getValue();
        }

        // 更新任务详情状态信息
        activityStatTaskDetailMapper.update(null, new UpdateWrapper<ActivityStatTaskDetail>()
                .lambda()
                .eq(ActivityStatTaskDetail::getTaskId, detail.getTaskId())
                .eq(ActivityStatTaskDetail::getActivityId, detail.getActivityId())
                .set(ActivityStatTaskDetail::getStatus, status)
                .set(ActivityStatTaskDetail::getErrorTimes, errorTimes)
                .set(ActivityStatTaskDetail::getErrorMessage, errorMsg)
        );

        return Objects.equals(ActivityStatTaskDetail.Status.SUCCESS.getValue(), status);
    }
}
