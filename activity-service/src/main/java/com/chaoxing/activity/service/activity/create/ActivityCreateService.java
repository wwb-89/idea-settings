package com.chaoxing.activity.service.activity.create;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateFromActivityReleaseParamDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.ActivityReleaseApiService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**活动创建服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCreateService
 * @description
 * @blame wwb
 * @date 2021-09-15 15:56:15
 */
@Slf4j
@Service
public class ActivityCreateService {

    @Resource
    private ActivityReleaseApiService activityReleaseApiService;
    @Resource
    private MarketHandleService marketHandleService;
    @Resource
    private PassportApiService passportApiService;
    @Resource
    private ActivityHandleService activityHandleService;
    @Resource
    private WfwAreaApiService wfwAreaApiService;
    @Resource
    private TemplateQueryService templateQueryService;

    /**活动发布平台的活动转换为活动引擎的活动
     * @Description
     * @author wwb
     * @Date 2021-09-15 16:06:26
     * @param activityId
     * @param flag
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void createFromActivityRelease(Integer activityId, String flag) {
        ActivityCreateFromActivityReleaseParamDTO activityCreateFromActivityReleaseParam = activityReleaseApiService.getActivityCreateFromActivityReleaseParam(activityId);
        if (activityCreateFromActivityReleaseParam == null) {
            return;
        }
        activityCreateFromActivityReleaseParam.determineFlag(flag);
        Integer uid = activityCreateFromActivityReleaseParam.getOriginCreateUid();
        String userName = passportApiService.getUserRealName(uid);
        Integer fid = activityCreateFromActivityReleaseParam.getOriginCreateFid();
        String orgName = passportApiService.getOrgName(fid);
        LoginUserDTO loginUserDto = LoginUserDTO.buildDefault(uid, userName, fid, orgName);
        Integer marketId = marketHandleService.getOrCreateOrgMarket(fid, Activity.ActivityFlagEnum.fromValue(flag), loginUserDto);
        Template template = templateQueryService.getMarketFirstTemplate(marketId);
        List<ActivityCreateParamDTO> activityCreateParamDtos = activityCreateFromActivityReleaseParam.buildActivityCreateParamDtos(marketId, template.getId());
        List<WfwAreaDTO> wfwAreaDtos = wfwAreaApiService.listByFid(fid);
        for (ActivityCreateParamDTO activityCreateParamDto : activityCreateParamDtos) {
            SignCreateParamDTO signCreateParamDto = SignCreateParamDTO.buildDefault();
            activityHandleService.add(activityCreateParamDto, signCreateParamDto, wfwAreaDtos, loginUserDto);
        }
    }

}