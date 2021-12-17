package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.CertificateIssue;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.certificate.CertificateQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

}