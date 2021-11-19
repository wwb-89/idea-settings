package com.chaoxing.activity.util.exception;

import lombok.Getter;

/**万能表单的活动未生成异常
 * @author wwb
 * @version ver 1.0
 * @className WfwFormActivityNotGeneratedException
 * @description
 * @blame wwb
 * @date 2021-11-15 16:00:17
 */
@Getter
public class WfwFormActivityNotGeneratedException extends BusinessException {

    public WfwFormActivityNotGeneratedException() {
        super("生成中，请稍后。点击页面刷新");
    }

}