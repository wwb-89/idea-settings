package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**机构api服务
 * @author wwb
 * @version ver 1.0
 * @className OrgApiController
 * @description
 * @blame wwb
 * @date 2021-09-02 10:39:54
 */
@RestController
@RequestMapping("org")
public class OrgApiController {

    @Resource
    private ActivityQueryService activityQueryService;

    /**查询机构下创建的作品征集id列表
     * @Description 
     * @author wwb
     * @Date 2021-09-02 11:12:42
     * @param fid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{fid}/workId")
    public RestRespDTO listOrgWorkId(@PathVariable Integer fid) {
        List<Integer> workIds = activityQueryService.listOrgCreatedWorkId(fid);
        return RestRespDTO.success(workIds);
    }

}