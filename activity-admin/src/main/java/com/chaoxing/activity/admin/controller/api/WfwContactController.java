package com.chaoxing.activity.admin.controller.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.manager.WfwContactService;
import com.chaoxing.activity.util.Pagination;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("wfw/contact")
public class WfwContactController {

    @Resource
    private WfwContactService wfwContactService;


    @RequestMapping("{uid}/organizations")
    public RestRespDTO getByFanyaMultiple(@PathVariable Integer uid) {
        return RestRespDTO.success(wfwContactService.getByFanyaMultiple(uid));
    }

    @RequestMapping("{uid}/search")
    public RestRespDTO search(@PathVariable Integer uid, @RequestParam(required = false,defaultValue = "") String keyword, Pagination pagination, HttpServletRequest request){
        return RestRespDTO.success(wfwContactService.search(uid, keyword, pagination));
    }

    @RequestMapping("{fid}/depts")
    public RestRespDTO getOrganizationDepts(@PathVariable Integer fid,@RequestParam(required = false,defaultValue = "") String pid, Pagination pagination, HttpServletRequest request) {
        return RestRespDTO.success(wfwContactService.getOrganizationDepts(fid, pid, pagination));
    }

    @RequestMapping("dept/{deptId}/members")
    public RestRespDTO getDeptMembers(@PathVariable Integer deptId, Pagination pagination, HttpServletRequest request){
        return RestRespDTO.success(wfwContactService.getDeptMembers(deptId, pagination));
    }


}


