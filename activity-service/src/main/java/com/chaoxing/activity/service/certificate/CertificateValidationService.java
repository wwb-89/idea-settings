package com.chaoxing.activity.service.certificate;

import com.chaoxing.activity.model.CertificateIssue;
import com.chaoxing.activity.util.exception.CertificateNotIssueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**证书验证服务
 * @author wwb
 * @version ver 1.0
 * @className CertificateValidationService
 * @description
 * @blame wwb
 * @date 2021-12-15 15:26:53
 */
@Slf4j
@Service
public class CertificateValidationService {

    @Resource
    private CertificateQueryService certificateQueryService;

    /**证书已发放
     * @Description 
     * @author wwb
     * @Date 2021-12-15 15:39:25
     * @param uid
     * @param activityId
     * @return com.chaoxing.activity.model.CertificateIssue
    */
    public CertificateIssue isIssued(Integer uid, Integer activityId) {
        CertificateIssue certificateIssue = certificateQueryService.getCertificateIssue(uid, activityId);
        return Optional.ofNullable(certificateIssue).orElseThrow(() -> new CertificateNotIssueException());
    }

}
