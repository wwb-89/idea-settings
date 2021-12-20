package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.UserCertificateDTO;
import com.chaoxing.activity.dto.query.UserCertificateQueryDTO;
import com.chaoxing.activity.model.CertificateIssue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 证书发放
 *
 * @className: TCertificateIssueMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-12-15 15:14:00
 * @version: ver 1.0
 */
@Mapper
public interface CertificateIssueMapper extends BaseMapper<CertificateIssue> {

    /**
     * 分页查询证书发放情况
     *
     * @param page
     * @param queryParam
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.UserCertificateDTO>
     * @Description
     * @author wwb
     * @Date 2021-12-16 11:35:15
     */
    Page<UserCertificateDTO> pageCertificate(@Param("page") Page<UserCertificateDTO> page, @Param("queryParam") UserCertificateQueryDTO queryParam);

    /**分页查询证书发放情况
     * @Description 
     * @author wwb
     * @Date 2021-12-20 11:26:00
     * @param page
     * @param queryParam
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.UserCertificateDTO>
    */
    Page<UserCertificateDTO> pageCertificate1(@Param("page") Page<UserCertificateDTO> page, @Param("queryParam") UserCertificateQueryDTO queryParam);

    /**
     * 批量新增
     *
     * @param certificateIssues
     * @return int
     * @Description
     * @author wwb
     * @Date 2021-12-16 15:43:31
     */
    int batchAdd(@Param("certificateIssues") List<CertificateIssue> certificateIssues);

    /**获取活动证书最大序号
     * @Description 
     * @author wwb
     * @Date 2021-12-16 16:29:23
     * @param activityId
     * @return java.lang.Integer
    */
    Integer getActivityMaxSerialNo(@Param("activityId") Integer activityId);

}