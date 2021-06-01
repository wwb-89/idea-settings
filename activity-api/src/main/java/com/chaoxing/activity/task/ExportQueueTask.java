package com.chaoxing.activity.task;

import com.chaoxing.activity.service.export.ExportRecordService;
import com.chaoxing.activity.service.queue.ExportQueueService;
import com.chaoxing.activity.util.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**导出任务队列
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/1 11:14 上午
 * <p>
 */
@Slf4j
@Component
public class ExportQueueTask {

    @Resource
    private ExportRecordService exportRecordService;

    @Resource
    private ExportQueueService exportQueueService;


    @Scheduled(fixedDelay = 1L)
    public void handleExportTask() throws InterruptedException {
        Integer taskId = exportQueueService.pop();
        if (taskId == null) {
            return;
        }
        boolean result = false;
        try {
            result = exportRecordService.handleExportTask(taskId, PathUtils.getUploadRootPath());
        } catch (Exception e) {

        }
        if (!result) {
            exportQueueService.push(taskId);
        }
    }

}
