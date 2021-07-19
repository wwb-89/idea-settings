package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: TOrgClassify, table_name: t_org_classify
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-19 15:03:35
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_org_classify")
public class OrgClassify {

    /** 机构id; column: fid*/
    private Integer fid;
    /** 分类id; column: classify_id*/
    private Integer classifyId;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    public static OrgClassify buildFromClassify(Classify classify, Integer fid) {
        return OrgClassify.builder()
                .fid(fid)
                .classifyId(classify.getId())
                .build();
    }

    public static List<OrgClassify> buildFromClassifies(List<Classify> classifies, Integer fid) {
        return classifies.stream().map(v -> OrgClassify.buildFromClassify(v, fid)).collect(Collectors.toList());
    }

    public static void handleSequence(List<OrgClassify> orgClassifies, Integer initialSequence) {
        for (OrgClassify orgClassify : orgClassifies) {
            orgClassify.setSequence(initialSequence++);
        }
    }

}