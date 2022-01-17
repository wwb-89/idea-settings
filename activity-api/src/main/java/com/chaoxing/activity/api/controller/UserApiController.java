package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.UserResult;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwOrganizationalStructureApiService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.UcApiService;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
    @Resource
    private PassportApiService passportApiService;
    @Resource
    private UcApiService ucApiService;
    @Resource
    private WfwOrganizationalStructureApiService organizationalStructureApiService;

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

    /**根据uid获取用户姓名
     * @Description
     * @author wwb
     * @Date 2021-08-16 11:11:18
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{uid}/name")
    public RestRespDTO userName(@PathVariable Integer uid) {
        String userRealName = passportApiService.getUserRealName(uid);
        return RestRespDTO.success(userRealName);
    }

    /**查询用户的执教班级
     * @Description
     * @author wwb
     * @Date 2021-09-03 11:25:05
     * @param uid
     * @param fid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("clazz/teaching")
    public RestRespDTO teachingClazz(@RequestParam Integer uid, @RequestParam Integer fid) {
        return RestRespDTO.success(ucApiService.listTeacherTeachingClazz(uid, fid));
    }

    /**用户学院
     * @Description
     * @author wwb
     * @Date 2021-09-23 14:40:36
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{uid}/college")
    public RestRespDTO userCollege(@PathVariable Integer uid) {
        return RestRespDTO.success(getGroupName(uid, 0));
    }

    /**用户专业
     * @Description
     * @author wwb
     * @Date 2021-09-23 14:40:44
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{uid}/professional")
    public RestRespDTO userProfessional(@PathVariable Integer uid) {
        return RestRespDTO.success(getGroupName(uid, 1));
    }

    /**用户班级
     * @Description
     * @author wwb
     * @Date 2021-09-23 14:43:59
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{uid}/class")
    public RestRespDTO userClass(@PathVariable Integer uid) {
        return RestRespDTO.success(getGroupName(uid, 2));
    }

    private String getGroupName(Integer uid, Integer groupIndex) {
        String groupName = "";
        List<String> groupNames = listGroupName(uid);
        if (groupNames.size() > groupIndex) {
            groupName = groupNames.get(groupIndex);
        }
        return groupName;
    }

    private List<String> listGroupName(Integer uid) {
        List<String> groupNames = Lists.newArrayList("", "", "", "", "");
        PassportUserDTO passportUserDto = passportApiService.getByUid(uid);
        if (passportUserDto != null) {
            List<OrgDTO> affiliations = passportUserDto.getAffiliations();
            Integer fid = Optional.ofNullable(affiliations).orElse(Lists.newArrayList()).stream().findFirst().map(OrgDTO::getFid).orElse(null);
            if (fid != null) {
                List<String> allGroupNames = organizationalStructureApiService.listUserFirstGroupNames(uid, fid);
                if (CollectionUtils.isNotEmpty(allGroupNames)) {
                    for (int i = 0; i < groupNames.size(); i++) {
                        groupNames.set(i, allGroupNames.size() > i ? allGroupNames.get(i) : "");
                    }
                }
            }
        }
        return groupNames;
    }

    /**查询用户的组织架构
     * @Description 
     * @author wwb
     * @Date 2021-09-24 14:03:56
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{uid}/organization")
    public RestRespDTO userOrganization(@PathVariable Integer uid) {
        return RestRespDTO.success(listGroupName(uid));
    }

}
