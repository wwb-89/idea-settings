package com.chaoxing.activity.service.activity.performance;

import com.chaoxing.activity.mapper.PerformanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动表现
 * @author wwb
 * @version ver 1.0
 * @className ActivityPerformanceService
 * @description
 * @blame wwb
 * @date 2021-06-29 21:18:36
 */
@Slf4j
@Service
public class PerformanceService {

	@Resource
	private PerformanceMapper performanceMapper;

}