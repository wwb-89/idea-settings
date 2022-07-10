package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**志愿服务时长记录
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/19 2:11 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerServiceDTO {


    /**
     * uid
     */
    private Integer uid;

    /**
     * 用户名
     */
    private String uname;

    /**
     * 记录ID
     */
    private Integer formUserId;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 组织架构
     */
    private String department;

    /**
     * 所属组织
     */
    private String affiliations;

    /**
     * 服务日期
     */
    private String serviceDate;

    /**
     * 类型
     */
    private String type;

    /**
     * 级别
     */
    private String level;

    /**
     * 服务时长
     */
    private Long timeLength;

    /**
     * 服务编号
     */
    private String no;



    public static VolunteerServiceDTO buildFromFormData(FormDataDTO formItem) {
        return VolunteerServiceDTO.builder()
                .uid(formItem.getUid())
                .formUserId(formItem.getFormUserId())
                .name(formItem.getStringValue("name"))
                .type(formItem.getStringValue("type"))
                .department(formItem.getStringValue("department"))
                .serviceDate(formItem.getStringValue("date"))
                .timeLength(formItem.getLongValue("time_length"))
                .no(formItem.getStringValue("no"))
                .level(formItem.getStringValue("level"))
                .build();
    }

    public static List<VolunteerServiceDTO> buildFromFormData(List<FormDataDTO> formData) {
        if (CollectionUtils.isEmpty(formData)) {
            return Lists.newArrayList();
        }
        List<VolunteerServiceDTO> result = Lists.newArrayList();
        formData.forEach(v -> {
            result.add(VolunteerServiceDTO.buildFromFormData(v));
        });
        return result;
    }

}
