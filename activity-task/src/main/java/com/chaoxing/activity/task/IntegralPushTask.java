package com.chaoxing.activity.task;

import com.chaoxing.activity.service.manager.IntegralApiService;
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
@Component
public class IntegralPushTask {

	@Resource
	private IntegralApiService integralApiService;

	/**推送积分数据
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 17:07:27
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1 * 1000)
	public void pushData() {
		integralApiService.handleTask();
	}

}
