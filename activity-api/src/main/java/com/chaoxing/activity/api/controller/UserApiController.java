package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.UserResult;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserApiController
 * @description
 * @blame wwb
 * @date 2021-06-25 09:40:22
 */
@RestController
@RequestMapping("user")
public class UserApiController {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private UserResultQueryService userResultQueryService;

    /**是否合格的描述
     * @Description 
     * @author wwb
     * @Date 2021-06-25 09:43:16
     * @param uid
     * @param signId
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{uid}/qualified/description")
    public RestRespDTO isQualified(@PathVariable Integer uid, Integer signId) {
        Activity activity = activityQueryService.getBySignId(signId);
        String resultQualifiedDescription = UserResult.QualifiedStatusEnum.WAIT.getName();
        if (activity != null) {
            resultQualifiedDescription = userResultQueryService.getResultQualifiedDescription(uid, activity.getId());
        }
        return RestRespDTO.success(resultQualifiedDescription);
    }

}
