package com.chaoxing.activity.admin.controller.api.stat;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.vo.stat.OrgUserStatVO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.query.admin.ActivityStatSummaryQueryDTO;
import com.chaoxing.activity.dto.query.admin.UserStatSummaryQueryDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserStatApiController
 * @description
 * @blame wwb
 * @date 2021-05-28 15:26:06
 */
@RestController
@RequestMapping("api/stat/org")
public class OrgStatApiController {

    @Resource
    private UserStatSummaryQueryService userStatSummaryQueryService;
    @Resource
    private ActivityStatSummaryQueryService activityStatSummaryQueryService;

    /**用户统计
     * @Description 
     * @author wwb
     * @Date 2021-05-28 15:59:17
     * @param request
     * @param userStatSummaryQuery
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("user-stat-summary/list")
    public RestRespDTO listUserStatSummary(HttpServletRequest request, UserStatSummaryQueryDTO userStatSummaryQuery) {
        Page page = HttpServletRequestUtils.buid(request);
        page = userStatSummaryQueryService.paging(page, userStatSummaryQuery);
        List<UserStatSummary> records = page.getRecords();
        List<OrgUserStatVO> orgUserStats = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(records)) {
            for (UserStatSummary record : records) {
                OrgUserStatVO orgUserStat = new OrgUserStatVO();
                BeanUtils.copyProperties(record, orgUserStat);
                orgUserStats.add(orgUserStat);
                Integer uid = record.getUid();
                // 是否在机构内
                List<Integer> uids = userStatSummaryQuery.getOrgUids();
                if (uids.contains(uid)) {
                    orgUserStat.setWithinTheOrg(true);
                } else {
                    orgUserStat.setWithinTheOrg(false);
                }
            }
            page.setRecords(orgUserStats);
        }
        return RestRespDTO.success(page);
    }

    /**分页查询活动统计数据
    * @Description
    * @author huxiaolong
    * @Date 2021-05-31 17:21:00
    * @param request
    * @param queryParamStr
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("activity/page")
    public RestRespDTO activityStatSummaryPage(HttpServletRequest request, String queryParamStr) {
        ActivityStatSummaryQueryDTO queryParam = JSON.parseObject(queryParamStr, ActivityStatSummaryQueryDTO.class);
        Page<ActivityStatSummaryDTO> page = HttpServletRequestUtils.buid(request);
        page = activityStatSummaryQueryService.activityStatSummaryPage(page, queryParam);
        return RestRespDTO.success(page);
    }

}