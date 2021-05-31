package com.chaoxing.activity.admin.controller.api.stat;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.vo.stat.OrgUserStatVO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.query.admin.ActivityStatSummaryQueryDTO;
import com.chaoxing.activity.dto.query.admin.UserStatSummaryQueryDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.service.export.ExportService;
import com.chaoxing.activity.service.manager.OrganizationalStructureApiService;
import com.chaoxing.activity.service.stat.UserStatSummaryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private UserStatSummaryService userStatSummaryService;
    @Resource
    private OrganizationalStructureApiService organizationalStructureApiService;
    @Resource
    private ActivityStatSummaryQueryService activityStatSummaryQueryService;
    @Resource
    private ExportService exportService;

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
        page = userStatSummaryService.paging(page, userStatSummaryQuery);
        List<UserStatSummary> records = page.getRecords();
        List<OrgUserStatVO> orgUserStats = Lists.newArrayList();
        Integer fid = userStatSummaryQuery.getFid();
        if (CollectionUtils.isNotEmpty(records)) {
            for (UserStatSummary record : records) {
                OrgUserStatVO orgUserStat = new OrgUserStatVO();
                BeanUtils.copyProperties(record, orgUserStat);
                orgUserStats.add(orgUserStat);
                // 封装学号和组织架构
                Integer uid = record.getUid();
                String studentNo = organizationalStructureApiService.getUserStudentNo(uid, fid);
                orgUserStat.setStudentNo(studentNo);
                String userFirstGroupName = organizationalStructureApiService.getUserFirstGroupName(uid, fid);
                orgUserStat.setOrganizationStructure(userFirstGroupName);
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


    /**导出活动统计数据
    * @Description
    * @author huxiaolong
    * @Date 2021-05-31 17:21:10
    * @param response
    * @param queryParam
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("activity/export/data")
    public void exportActivityStatSummary(HttpServletResponse response, ActivityStatSummaryQueryDTO queryParam) throws IOException {
        ExportDataDTO exportData = activityStatSummaryQueryService.getExportData(queryParam);
        exportData.setFileName("活动统计汇总");
        exportData.setSheetName("活动统计汇总数据");
        exportService.export(exportData, response);
    }

}
