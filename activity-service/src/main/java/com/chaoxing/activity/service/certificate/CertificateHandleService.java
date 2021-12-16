package com.chaoxing.activity.service.certificate;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.manager.CertificateFieldDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.mapper.CertificateIssueMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.CertificateIssue;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.event.UserCertificateIssueEventService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.CertificateApiService;
import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**证书服务
 * @author wwb
 * @version ver 1.0
 * @className CertificateHandleService
 * @description
 * @blame wwb
 * @date 2021-12-15 11:28:52
 */
@Slf4j
@Service
public class CertificateHandleService {

    private static final String PAD_CHAR = "0";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final Integer TIME_MAX_LENGTH = 4;
    private static final Integer INTEGER_MAX_LENGTH = 11;
    private static final Integer ACTIVITY_ID_SECTION_MAX_LENGTH = 4;
    private static final Integer NO_MAX_LENGTH = 5;

    @Resource
    private CertificateIssueMapper certificateIssueMapper;

    @Resource
    private CertificateApiService certificateApiService;
    @Resource
    private ActivityValidationService activityValidationService;
    @Resource
    private PassportApiService passportApiService;
    @Resource
    private CertificateValidationService certificateValidationService;
    @Resource
    private CertificateQueryService certificateQueryService;
    @Resource
    private UserCertificateIssueEventService userCertificateIssueEventService;

    @Resource
    private DistributedLock distributedLock;

    /**获取证书生成的锁key
     * @Description 
     * @author wwb
     * @Date 2021-12-16 16:22:54
     * @param activityId
     * @return java.lang.String
    */
    private String getCertificateNoGenerateLockKey(Integer activityId) {
        return CacheConstant.LOCK_CACHE_KEY_PREFIX + "activity_certificate_no" + CacheConstant.CACHE_KEY_SEPARATOR + activityId;
    }

    /**生成证书编号
     * @Description 
     * @author wwb
     * @Date 2021-12-15 11:31:01
     * @param activity
     * @param serialNo
     * @return java.lang.String
    */
    public String generateNo(Activity activity, Integer serialNo) {
        Integer activityId = activity.getId();
        StringBuilder no = new StringBuilder();
        no.append(activity.getCreateFid());
        String format = activity.getStartTime().format(DATE_TIME_FORMATTER);
        no.append(format.substring(format.length() - TIME_MAX_LENGTH));
        String activityChar = StringUtils.leftPad(String.valueOf(activityId), INTEGER_MAX_LENGTH, PAD_CHAR);
        no.append(activityChar.substring(activityChar.length() - ACTIVITY_ID_SECTION_MAX_LENGTH));
        no.append(StringUtils.leftPad(String.valueOf(serialNo), NO_MAX_LENGTH, PAD_CHAR));
        return no.toString();
    }

    /**下载证书
     * @Description 
     * @author wwb
     * @Date 2021-12-15 14:49:44
     * @param activityId
     * @param uid
     * @return byte[]
    */
    public byte[] download(Integer activityId, Integer uid) {
        Activity activity = activityValidationService.activityExist(activityId);
        // 证书发放记录
        CertificateIssue certificateIssue = certificateValidationService.isIssued(uid, activityId);
        PassportUserDTO passportUser = passportApiService.getByUid(uid);
        CertificateFieldDTO certificateField = CertificateFieldDTO.build(activity, certificateIssue.getNo(), certificateIssue.getCreateTime(), passportUser.getRealName());
        Integer certificateTemplateId = Optional.ofNullable(activity.getCertificateTemplateId()).orElseThrow(() -> new BusinessException("证书模版不存在"));
        return certificateApiService.getDownloadUrl(certificateTemplateId, activity.getCreateUid(), activity.getCreateFid(), certificateField);
    }

    /**发放证书
     * @Description 
     * @author wwb
     * @Date 2021-12-16 14:25:03
     * @param uid
     * @param activityId
     * @param operateUser
     * @return void
    */
    public void issueCertificate(Integer uid, Integer activityId, OperateUserDTO operateUser) {
        Activity activity = activityValidationService.activityExist(activityId);
        CertificateIssue existCertificateIssue = certificateQueryService.getCertificateIssue(uid, activityId);
        LocalDateTime issueTime = LocalDateTime.now();
        if (existCertificateIssue == null) {
            // 锁
            String lockKey = getCertificateNoGenerateLockKey(activityId);
            distributedLock.lock(lockKey, () -> {
                Integer maxSerialNo = certificateQueryService.getActivityMaxSerialNo(activityId);
                Integer serialNo = maxSerialNo + 1;
                CertificateIssue certificateIssue = CertificateIssue.builder()
                        .uid(uid)
                        .activityId(activityId)
                        .no(generateNo(activity, serialNo))
                        .serialNo(serialNo)
                        .issueTime(issueTime)
                        .build();
                certificateIssueMapper.insert(certificateIssue);
                userCertificateIssueEventService.issue(uid, activityId);
                return null;
            }, e -> {
                log.error("根据uid:{}, activityId: {} 发放证书error:{}", uid, activityId, e.getMessage());
                throw new BusinessException("发放证书失败");
            });
        }
    }

    /**批量发放证书
     * @Description 
     * @author wwb
     * @Date 2021-12-16 15:39:22
     * @param uids
     * @param activityId
     * @param operateUser
     * @return void
    */
    public void batchIssue(List<Integer> uids, Integer activityId, OperateUserDTO operateUser) {
        if (CollectionUtils.isEmpty(uids)) {
            return;
        }
        List<CertificateIssue> existCertificateIssues = certificateQueryService.listCertificateIssue(uids, activityId);
        List<Integer> issuedUids = existCertificateIssues.stream().map(CertificateIssue::getUid).collect(Collectors.toList());
        uids.removeAll(issuedUids);
        if (CollectionUtils.isEmpty(uids)) {
            return;
        }
        Activity activity = activityValidationService.activityExist(activityId);
        // 锁
        String lockKey = getCertificateNoGenerateLockKey(activityId);
        distributedLock.lock(lockKey, () -> {
            Integer maxSerialNo = certificateQueryService.getActivityMaxSerialNo(activityId);
            LocalDateTime issueTime = LocalDateTime.now();
            List<CertificateIssue> certificateIssues = Lists.newArrayList();
            for (Integer uid : uids) {
                Integer serialNo = maxSerialNo + 1;
                CertificateIssue certificateIssue = CertificateIssue.builder()
                        .uid(uid)
                        .activityId(activityId)
                        .issueTime(issueTime)
                        .no(generateNo(activity, serialNo))
                        .serialNo(serialNo)
                        .build();
                certificateIssues.add(certificateIssue);
            }
            certificateIssueMapper.batchAdd(certificateIssues);
            userCertificateIssueEventService.issue(uids, activityId);
            return null;
        }, e -> {
            log.error("根据uids:{}, activityId: {} 重新发放证书error:{}", JSON.toJSONString(uids), activityId, e.getMessage());
            throw new BusinessException("重新发放证书失败");
        });
    }

    /**重新发放证书
     * @Description 
     * @author wwb
     * @Date 2021-12-16 15:34:24
     * @param uid
     * @param activityId
     * @param operateUser
     * @return void
    */
    public void againIssueCertificate(Integer uid, Integer activityId, OperateUserDTO operateUser) {
        CertificateIssue existCertificateIssue = certificateQueryService.getCertificateIssue(uid, activityId);
        if (existCertificateIssue == null) {
            issueCertificate(uid, activityId, operateUser);
        } else {
            Activity activity = activityValidationService.activityExist(activityId);
            // 锁
            String lockKey = getCertificateNoGenerateLockKey(activityId);
            distributedLock.lock(lockKey, () -> {
                Integer maxSerialNo = certificateQueryService.getActivityMaxSerialNo(activityId);
                Integer serialNo = maxSerialNo + 1;
                LocalDateTime issueTime = LocalDateTime.now();
                certificateIssueMapper.update(null, new LambdaUpdateWrapper<CertificateIssue>()
                        .eq(CertificateIssue::getId, existCertificateIssue.getId())
                        .set(CertificateIssue::getIssueTime, issueTime)
                        .set(CertificateIssue::getNo, generateNo(activity, serialNo))
                        .set(CertificateIssue::getSerialNo, serialNo)
                );
                userCertificateIssueEventService.issue(uid, activityId);
                return null;
            }, e -> {
                log.error("根据uid:{}, activityId: {} 重新发放证书error:{}", uid, activityId, e.getMessage());
                throw new BusinessException("重新发放证书失败");
            });
        }

    }

    /**撤回证书发放
     * @Description 
     * @author wwb
     * @Date 2021-12-16 14:36:03
     * @param uid
     * @param activityId
     * @param operateUser
     * @return void
    */
    public void revocationIssue(Integer uid, Integer activityId, OperateUserDTO operateUser) {
        certificateIssueMapper.delete(new LambdaUpdateWrapper<CertificateIssue>()
                .eq(CertificateIssue::getActivityId, activityId)
                .eq(CertificateIssue::getUid, uid)
        );
    }

    /**批量撤回
     * @Description 
     * @author wwb
     * @Date 2021-12-16 16:03:16
     * @param uids
     * @param activityId
     * @param operateUser
     * @return void
    */
    public void batchRevocation(List<Integer> uids, Integer activityId, OperateUserDTO operateUser) {
        if (CollectionUtils.isEmpty(uids)) {
            return;
        }
        List<CertificateIssue> existCertificateIssues = certificateQueryService.listCertificateIssue(uids, activityId);
        List<Integer> issuedUids = existCertificateIssues.stream().map(CertificateIssue::getUid).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(issuedUids)) {
            return;
        }
        certificateIssueMapper.delete(new LambdaUpdateWrapper<CertificateIssue>()
                .eq(CertificateIssue::getActivityId, activityId)
                .in(CertificateIssue::getUid, issuedUids)
        );
    }

}
