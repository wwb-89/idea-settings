package com.chaoxing.activity.dto;

import com.chaoxing.activity.util.enums.SignUpBtnEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**报名按钮
 * @author wwb
 * @version ver 1.0
 * @className SignUpBtnDTO
 * @description
 * @blame wwb
 * @date 2021-11-17 15:38:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpBtnDTO {

    private String name;
    private String keyWord;

    public static List<SignUpBtnDTO> list() {
        List<SignUpBtnDTO> signUpBtns = Lists.newArrayList();
        SignUpBtnEnum[] values = SignUpBtnEnum.values();
        for (SignUpBtnEnum value : values) {
            signUpBtns.add(SignUpBtnDTO.builder()
                            .name(value.getName())
                            .keyWord(value.getKeyWord())
                    .build());
        }
        return signUpBtns;
    }

}