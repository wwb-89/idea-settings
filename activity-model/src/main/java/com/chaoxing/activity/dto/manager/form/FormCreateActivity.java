package com.chaoxing.activity.dto.manager.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className FormCreateActivity
 * @description
 * @blame wwb
 * @date 2021-05-11 16:20:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormCreateActivity {

    private Integer fid;
    private Integer formId;
    private Integer formUserId;

}
