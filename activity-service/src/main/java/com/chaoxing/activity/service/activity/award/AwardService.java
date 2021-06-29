package com.chaoxing.activity.service.activity.award;

import com.chaoxing.activity.mapper.AwardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**奖项服务
 * @author wwb
 * @version ver 1.0
 * @className AwardService
 * @description
 * @blame wwb
 * @date 2021-06-29 21:21:07
 */
@Slf4j
@Service
public class AwardService {

	@Resource
	private AwardMapper awardMapper;

}