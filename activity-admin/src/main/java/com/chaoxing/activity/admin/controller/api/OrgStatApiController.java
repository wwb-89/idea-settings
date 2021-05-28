package com.chaoxing.activity.admin.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/28 4:26 下午
 * <p>
 */
@RestController
@RequestMapping("/api/stat/org")
public class OrgStatApiController {

    @Resource
    private ActivityStatSummaryQueryService activityStatSummaryQueryService;

    @PostMapping("activity/page")
    public RestRespDTO activitStatSummaryPage(HttpServletRequest request, @RequestParam("fid") Integer fid, String queryParamStr) {
        Page<ActivityStatSummaryDTO> page = HttpServletRequestUtils.buid(request);

        page = activityStatSummaryQueryService.activityStatSummaryPage(page, fid, queryParamStr);
        return RestRespDTO.success(page);
    }

}