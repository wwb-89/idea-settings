package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.CertificateIssue;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.certificate.CertificateHandleService;
import com.chaoxing.activity.service.certificate.CertificateQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**证书api服务
 * @author wwb
 * @version ver 1.0
 * @className CertificateApiController
 * @description
 * @blame wwb
 * @date 2021-12-17 10:26:22
 */
@Slf4j
@RestController
@RequestMapping("certificate")
public class CertificateApiController {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private CertificateQueryService certificateQueryService;
    @Resource
    private CertificateHandleService certificateHandleService;

    /**获取用户证书的下载地址
     * @Description 
     * @author wwb
     * @Date 2021-12-17 10:28:38
     * @param uid
     * @param activityId
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("url/download")
    public RestRespDTO getUserCertificateDownloadUrl(@RequestParam Integer uid, @RequestParam Integer activityId) {
        String downloadUrl = "";
        Integer certificateTemplateId = Optional.ofNullable(activityQueryService.getById(activityId)).map(Activity::getCertificateTemplateId).orElse(null);
        if (certificateTemplateId != null) {
            // 查询用户获取的证书
            CertificateIssue certificateIssue = certificateQueryService.getCertificateIssue(uid, activityId);
            downloadUrl = Optional.ofNullable(certificateIssue).map(CertificateIssue::getDownloadUrl).orElse("");
        }
        return RestRespDTO.success(downloadUrl);
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
        String fileName = "证书.pdf";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        FileCopyUtils.copy(bytes, response.getOutputStream());
    }

}