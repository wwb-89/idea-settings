package com.chaoxing.activity.service.certificate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.UserCertificateDTO;
import com.chaoxing.activity.dto.query.UserCertificateQueryDTO;
import com.chaoxing.activity.mapper.CertificateIssueMapper;
import com.chaoxing.activity.model.CertificateIssue;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**证书查询服务
 * @author wwb
 * @version ver 1.0
 * @className CertificateQueryService
 * @description
 * @blame wwb
 * @date 2021-12-15 15:40:13
 */
@Slf4j
@Service
public class CertificateQueryService {

    @Resource
    private CertificateIssueMapper certificateIssueMapper;

    /**查询证书发放记录
     * @Description 
     * @author wwb
     * @Date 2021-12-15 15:41:56
     * @param uid
     * @param activityId
     * @return com.chaoxing.activity.model.CertificateIssue
    */
    public CertificateIssue getCertificateIssue(Integer uid, Integer activityId) {
        List<CertificateIssue> certificateIssues = certificateIssueMapper.selectList(new LambdaQueryWrapper<CertificateIssue>()
                .eq(CertificateIssue::getActivityId, activityId)
                .eq(CertificateIssue::getUid, uid)
        );
        return certificateIssues.stream().findFirst().orElse(null);
    }

    /**查询证书发放列表
     * @Description 
     * @author wwb
     * @Date 2021-12-16 15:38:28
     * @param uids
     * @param activityId
     * @return java.util.List<com.chaoxing.activity.model.CertificateIssue>
    */
    public List<CertificateIssue> listCertificateIssue(List<Integer> uids, Integer activityId) {
        return certificateIssueMapper.selectList(new LambdaQueryWrapper<CertificateIssue>()
                .eq(CertificateIssue::getActivityId, activityId)
                .in(CertificateIssue::getUid, uids)
        );
    }

    /**分页查询
     * @Description 
     * @author wwb
     * @Date 2021-12-16 14:24:20
     * @param page
     * @param queryParams
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.UserCertificateDTO>
    */
    public Page<UserCertificateDTO> pageCertificate(Page<UserCertificateDTO> page, UserCertificateQueryDTO queryParams) {
        page = certificateIssueMapper.pageCertificate(page, queryParams);
        List<UserCertificateDTO> records = page.getRecords();
        records.stream().forEach(v -> {
            v.setIssued(v.getIssueTime() != null);
            v.setIssueTimestamp(DateUtils.date2Timestamp(v.getIssueTime()));
        });
        return page;
    }

    /**获取活动证书最大序号
     * @Description 
     * @author wwb
     * @Date 2021-12-16 16:32:35
     * @param activityId
     * @return java.lang.Integer
    */
    public Integer getActivityMaxSerialNo(Integer activityId) {
        return certificateIssueMapper.getActivityMaxSerialNo(activityId);
    }

}
