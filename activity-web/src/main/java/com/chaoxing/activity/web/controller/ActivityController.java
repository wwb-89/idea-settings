package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.ActivityQueryDateDTO;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.ActivityQueryDateService;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.web.util.LoginUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**活动
 * @author wwb
 * @version ver 1.0
 * @className ActivityController
 * @description
 * @blame wwb
 * @date 2020-11-20 10:49:54
 */
@Slf4j
@Controller
@RequestMapping("lib")
public class ActivityController {

	@Resource
	private GroupRegionFilterService groupRegionFilterService;
	@Autowired
	private ActivityClassifyQueryService activityClassifyQueryService;
	@Resource
	private GroupService groupService;
	@Resource
	private ActivityQueryDateService activityQueryDateService;

	@GetMapping
	public String index(HttpServletRequest request, Model model, String code, @RequestParam(value = "unitId", required = false) Integer fid, Integer pageId) {
		fid = Optional.ofNullable(fid).orElse(LoginUtils.getLoginUser(request).getFid());
		List<String> activityClassifyNames = activityClassifyQueryService.listOrgsOptionalName(Lists.newArrayList(fid));
		model.addAttribute("activityClassifyNames", activityClassifyNames);
		// 查询地区列表
		List<GroupRegionFilter> groupRegionFilters;
		String areaCode = "";
		if (StringUtils.isNotBlank(code)) {
			groupRegionFilters = groupRegionFilterService.listByGroupCode(code);
			Group group = groupService.getByCode(code);
			if (group != null) {
				areaCode = group.getAreaCode();
			}
		} else {
			groupRegionFilters = Lists.newArrayList();
		}
		model.addAttribute("regions", groupRegionFilters);
		List<ActivityQueryDateDTO> activityQueryDates = activityQueryDateService.listAll();
		model.addAttribute("activityQueryDates", activityQueryDates);
		model.addAttribute("areaCode", areaCode);
		model.addAttribute("topFid", fid);
		model.addAttribute("pageId", pageId);
		if (UserAgentUtils.isMobileAccess(request)) {
			return "mobile/index";
		}
		return "pc/index";
	}

}