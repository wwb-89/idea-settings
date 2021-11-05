package com.chaoxing.activity.task;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.manager.IntegralApiService;
import com.chaoxing.activity.service.queue.IntegralPushQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**积分推送任务
 * @author wwb
 * @version ver 1.0
 * @className IntegralPushTask
 * @description
 * @blame wwb
 * @date 2020-12-24 17:06:33
 */
@Slf4j
@Component
public class IntegralPushTask {

	@Resource
	private IntegralPushQueue integralPushQueueService;
	@Resource
	private IntegralApiService integralApiService;

	/**推送积分数据
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 17:07:27
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void pushData() throws InterruptedException {
		IntegralPushQueue.IntegralPushDTO integralPush = integralPushQueueService.pop();
		if (integralPush == null) {
			return;
		}
		try {
			integralApiService.pushIntegral(integralPush);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("根据参数:{} 处理积分推送任务error:{}", JSON.toJSONString(integralPush), e.getMessage());
			integralPushQueueService.push(integralPush);

		}
	}

}
