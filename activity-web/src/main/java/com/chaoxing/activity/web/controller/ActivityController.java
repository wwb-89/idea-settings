package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityController
 * @description
 * @blame wwb
 * @date 2022-03-07 10:41:46
 */
@Slf4j
@Controller
@RequestMapping("activity")
public class ActivityController {

	@Resource
	private ActivityQueryService activityQueryService;

	/**活动主页
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-07 10:42:26
	 * @param activityId
	 * @return org.springframework.web.servlet.view.RedirectView
	*/
	@RequestMapping("{activityId}")
	public RedirectView index(@PathVariable Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		String url = Optional.ofNullable(activity).map(Activity::getPreviewUrl).filter(StringUtils::isNotBlank).orElseThrow(() -> new BusinessException("地址不存在"));
		return new RedirectView(url);
	}

}
