package com.chaoxing.activity;

import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoAddQueueService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoAddQueueServiceTests
 * @description
 * @blame wwb
 * @date 2021-07-29 09:07:56
 */
@SpringBootTest
public class BlacklistAutoAddQueueServiceTests {

	@Resource
	private BlacklistAutoAddQueueService blacklistAutoAddQueueService;

	@Test
	public void add() {
		blacklistAutoAddQueueService.push(new BlacklistAutoAddQueueService.QueueParamDTO(1));
	}

}
