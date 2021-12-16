package com.chaoxing.activity.admin.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.UserCertificateDTO;
import com.chaoxing.activity.dto.query.UserCertificateQueryDTO;
import com.chaoxing.activity.service.certificate.CertificateHandleService;
import com.chaoxing.activity.service.certificate.CertificateQueryService;
import com.chaoxing.activity.service.manager.module.CertificateApiService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**证书服务
 * @author wwb
 * @version ver 1.0
 * @className CertificateApiController
 * @description
 * @blame wwb
 * @date 2021-12-15 17:04:06
 */
@Slf4j
@RestController
@RequestMapping("api/certificate")
public class CertificateApiController {

    @Resource
    private CertificateApiService certificateApiService;
    @Resource
    private CertificateHandleService certificateHandleService;
    @Resource
    private CertificateQueryService certificateQueryService;

    /**创建证书模版
     * @Description 
     * @author wwb
     * @Date 2021-12-15 17:09:50
     * @param request
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @LoginRequired
    @RequestMapping("new")
    public RestRespDTO createTemplate(HttpServletRequest request) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Integer templateId = certificateApiService.copyTemplate(loginUser.getUid(), loginUser.getFid());
        return RestRespDTO.success(templateId);
    }

    /**获取配置地址
     * @Description 
     * @author wwb
     * @Date 2021-12-15 18:01:59
     * @param request
     * @param templateId
     * @param uid
     * @param fid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @LoginRequired
    @RequestMapping("config")
    public RestRespDTO getConfigUrl(HttpServletRequest request, Integer templateId, Integer uid, Integer fid) {
        if (uid == null) {
            LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
            uid = loginUser.getUid();
            fid = loginUser.getFid();
        }
        String configUrl = certificateApiService.generateTemplateConfigUrl(templateId, uid, fid);
        return RestRespDTO.success(configUrl);
    }

    /**下载证书
     * @Description 
     * @author wwb
     * @Date 2021-12-15 20:12:15
     * @param activityId
     * @param uid
     * @param response
     * @return void
    */
    @RequestMapping("download")
    public void download(@RequestParam Integer activityId, @RequestParam Integer uid, HttpServletResponse response) throws IOException {
        byte[] bytes = certificateHandleService.download(activityId, uid);
        response.setContentType("application/pdf;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String("证书.pdf".getBytes("gb2312"), "ISO8859-1"));
        FileCopyUtils.copy(bytes, response.getOutputStream());
    }

    /**查看证书
     * @Description 
     * @author wwb
     * @Date 2021-12-16 19:33:02
     * @param activityId
     * @param uid
     * @param response
     * @return void
    */
    @RequestMapping("show")
    public void show(@RequestParam Integer activityId, @RequestParam Integer uid, HttpServletResponse response) throws IOException {
        byte[] bytes = certificateHandleService.download(activityId, uid);
        response.setContentType("application/pdf;charset=UTF-8");
        FileCopyUtils.copy(bytes, response.getOutputStream());
    }

    /**分页查询证书发放
     * @Description 
     * @author wwb
     * @Date 2021-12-16 11:28:59
     * @param request
     * @param queryParams
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("page")
    public RestRespDTO page(HttpServletRequest request, UserCertificateQueryDTO queryParams) {
        Page<UserCertificateDTO> page = HttpServletRequestUtils.buid(request);
        page = certificateQueryService.pageCertificate(page, queryParams);
        return RestRespDTO.success(page);
    }

    /**发放证书
     * @Description 
     * @author wwb
     * @Date 2021-12-16 15:10:44
     * @param request
     * @param activityId
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("issue")
    public RestRespDTO issue(HttpServletRequest request, Integer activityId, Integer uid) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        certificateHandleService.issueCertificate(uid, activityId, loginUser.buildOperateUserDTO());
        return RestRespDTO.success();
    }

    /**批量发放
     * @Description 
     * @author wwb
     * @Date 2021-12-16 15:36:54
     * @param request
     * @param activityId
     * @param uids
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("issue/batch")
    public RestRespDTO batchIssue(HttpServletRequest request, Integer activityId, @RequestParam(value = "uids[]") List<Integer> uids) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        certificateHandleService.batchIssue(uids, activityId, loginUser.buildOperateUserDTO());
        return RestRespDTO.success();
    }

    /**重新发放证书
     * @Description 
     * @author wwb
     * @Date 2021-12-16 15:11:56
     * @param request
     * @param activityId
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("issue/again")
    public RestRespDTO againIssue(HttpServletRequest request, Integer activityId, Integer uid) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        certificateHandleService.againIssueCertificate(uid, activityId, loginUser.buildOperateUserDTO());
        return RestRespDTO.success();
    }

    /**撤回证书
     * @Description 
     * @author wwb
     * @Date 2021-12-16 15:10:57
     * @param request
     * @param activityId
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("revocation")
    public RestRespDTO revocation(HttpServletRequest request, Integer activityId, Integer uid) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        certificateHandleService.revocationIssue(uid, activityId, loginUser.buildOperateUserDTO());
        return RestRespDTO.success();
    }

    /**批量撤回
     * @Description 
     * @author wwb
     * @Date 2021-12-16 15:36:40
     * @param request
     * @param activityId
     * @param uids
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("revocation/batch")
    public RestRespDTO batchRevocation(HttpServletRequest request, Integer activityId, @RequestParam(value = "uids[]") List<Integer> uids) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        certificateHandleService.batchRevocation(uids, activityId, loginUser.buildOperateUserDTO());
        return RestRespDTO.success();
    }

}