package com.chaoxing.activity.service.notice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.SystemNoticeTemplateMapper;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**系统通知模版服务
 * @author wwb
 * @version ver 1.0
 * @className SystemNoticeTemplate
 * @description
 * @blame wwb
 * @date 2021-11-11 14:24:31
 */
@Slf4j
@Service
public class SystemNoticeTemplateService {

	@Resource
	private SystemNoticeTemplateMapper systemNoticeTemplateMapper;

	/**查询所有的系统通知模版
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-11 15:14:38
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.model.SystemNoticeTemplate>
	*/
	public List<SystemNoticeTemplate> list() {
		return systemNoticeTemplateMapper.selectList(new LambdaQueryWrapper<SystemNoticeTemplate>()
				.orderByAsc(SystemNoticeTemplate::getSequence)
		);
	}

	/**根据通知类型查询系统通知模版
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-11 22:09:25
	 * @param noticeType
	 * @return com.chaoxing.activity.model.SystemNoticeTemplate
	*/
	public SystemNoticeTemplate getByNoticeType(String noticeType) {
		List<SystemNoticeTemplate> systemNoticeTemplates = systemNoticeTemplateMapper.selectList(new LambdaQueryWrapper<SystemNoticeTemplate>()
				.eq(SystemNoticeTemplate::getNoticeType, noticeType)
		);
		return Optional.ofNullable(systemNoticeTemplates).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

}