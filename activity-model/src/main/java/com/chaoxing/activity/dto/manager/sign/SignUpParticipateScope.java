package com.chaoxing.activity.dto.manager.sign;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报名参与范围表
 * @className: SignUpParticipateScope, table_name: t_sign_up_participate_scope
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-03-29 13:00:19
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpParticipateScope {

    /** 报名id */
    @TableId(type = IdType.AUTO)
    private Integer signUpId;
    /** 外资源部id */
    private Integer externalId;
    /** 外部资源父id */
    private Integer externalPid;
    /** 外部资源名称 */
    private String externalName;
    /** 是不是叶子结点 */
    private Boolean leaf;
    /** 创建时间 */
    private LocalDateTime createTime;

}