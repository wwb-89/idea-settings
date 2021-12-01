package com.chaoxing.activity.admin.controller.outer;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.vo.ActivityVO;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.query.ActivityCreateParticipateQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("api/outer/activity")
public class ActivityOuterApiController {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityHandleService activityHandleService;

    /**
     * 分页查询机构创建或发布到该机构的活动
     * @Description
     * @author huxiaolong
     * @Date 2021-12-01 10:39:35
     * @param request
     * @param activityQuery
     * @return
     */
    @RequestMapping("create-participate/page")
    public RestRespDTO createParticipateActivityPage(HttpServletRequest request, ActivityCreateParticipateQueryDTO activityQuery) {
        Page page = HttpServletRequestUtils.buid(request);
        page = activityQueryService.createParticipateActivityPage(page, activityQuery);
        List<Activity> activities = page.getRecords();
        if (CollectionUtils.isNotEmpty(activities)) {
            page.setRecords(ActivityVO.activitiesConvert2Vo(activities));
        }
        return RestRespDTO.success(page);
    }

    /**
     * 更新活动归档状态(归档、恢复)
     * @Description
     * @author huxiaolong
     * @Date 2021-12-01 12:03:11
     * @param archived
     * @return
     */
    @RequestMapping("{activityId}/archive-stutus/update")
    public RestRespDTO updateActivityArchive(@PathVariable Integer activityId, boolean archived) {
        activityHandleService.updateActivityArchive(activityId, archived);
        return RestRespDTO.success();
    }

    /**
     * 更新活动发布下架状态
     * @Description
     * @author huxiaolong
     * @Date 2021-12-01 12:07:32
     * @param activityId
     * @param released
     * @return
     */
    @RequestMapping("{activityId}/release-status/update")
    public RestRespDTO updateActivityReleaseStatus(@PathVariable Integer activityId, Integer fid, Integer uid, boolean released) {
        OperateUserDTO operateUser = OperateUserDTO.build(uid, fid);
        if (released) {
            activityHandleService.release(activityId, operateUser);
        } else {
            activityHandleService.cancelRelease(activityId, operateUser);
        }
        return RestRespDTO.success();
    }

}
