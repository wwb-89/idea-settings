package com.chaoxing.activity.admin.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.stat.UserResultDTO;
import com.chaoxing.activity.model.UserResult;
import com.chaoxing.activity.service.user.result.UserResultHandleService;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
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
 * @date 2021/6/24 10:14 上午
 * <p>
 */
@RestController
@RequestMapping("api/activity/results/manage")
public class ResultsManageApiController {

    @Resource
    private UserResultQueryService userResultQueryService;
    @Resource
    private UserResultHandleService userResultHandleService;

    /**分页查询
    * @Description
    * @author huxiaolong
    * @Date 2021-06-24 10:25:24
    * @param request
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("page")
    public RestRespDTO page(HttpServletRequest request, @RequestParam Integer activityId) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
//        signApiService.manageAble(activityId, loginUser.getUid());
        Page<UserResultDTO> page = HttpServletRequestUtils.buid(request);
        page = userResultQueryService.pageUserResult(page, activityId);
        return RestRespDTO.success(page);
    }


    /**判定用户合格
    * @Description
    * @author huxiaolong
    * @Date 2021-06-24 18:28:03
    * @param activityId
    * @param uid
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("qualified")
    public RestRespDTO qualified(Integer activityId, Integer uid) {
        userResultHandleService.updateQualifiedStatus(activityId, uid, UserResult.QualifiedStatusEnum.QUALIFIED);
        return RestRespDTO.success();
    }

    /**判定用户不合格
    * @Description
    * @author huxiaolong
    * @Date 2021-06-24 18:28:19
    * @param activityId
    * @param uid
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("unQualified")
    public RestRespDTO unQualified(Integer activityId, Integer uid) {
        userResultHandleService.updateQualifiedStatus(activityId, uid, UserResult.QualifiedStatusEnum.NOT_QUALIFIED);
        return RestRespDTO.success();
    }

    /**批量判定用户合格
    * @Description
    * @author huxiaolong
    * @Date 2021-06-24 18:28:28
    * @param activityId
    * @param uids
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("batch/qualified")
    public RestRespDTO batchQualified(Integer activityId, String uids) {
        userResultHandleService.batchUpdateQualifiedStatus(activityId, uids, UserResult.QualifiedStatusEnum.QUALIFIED);
        return RestRespDTO.success();
    }

    /**批量判定用户不合格
    * @Description
    * @author huxiaolong
    * @Date 2021-06-24 18:28:44
    * @param activityId
    * @param uids
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @PostMapping("batch/unQualified")
    public RestRespDTO batchUnQualified(Integer activityId, String uids) {
        userResultHandleService.batchUpdateQualifiedStatus(activityId, uids, UserResult.QualifiedStatusEnum.NOT_QUALIFIED);
        return RestRespDTO.success();
    }
}
