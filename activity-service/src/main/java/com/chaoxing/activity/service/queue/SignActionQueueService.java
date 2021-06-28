package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**报名签到行为队列服务
 * @author wwb
 * @version ver 1.0
 * @className SignActionQueueService
 * @description
 * @blame wwb
 * @date 2021-05-25 17:52:39
 */
@Slf4j
@Service
public class SignActionQueueService implements IQueueService<SignActionQueueService.QueueParamDTO> {

    /** 签到行为缓存key */
    private static final String SIGN_IN_ACTION_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "sign_action";

    @Resource
    private RedissonClient redissonClient;

    public void push(QueueParamDTO queueParam) {
        push(redissonClient, SIGN_IN_ACTION_CACHE_KEY, queueParam);
    }

    public QueueParamDTO pop() throws InterruptedException {
        return pop(redissonClient, SIGN_IN_ACTION_CACHE_KEY);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueParamDTO {

        /** 报名签到id */
        private Integer signId;
        /** 行为 */
        private SignActionEnum signAction;
        /** 主键 */
        private String identify;
        /** 时间 */
        private LocalDateTime time;

    }

    @Getter
    public enum SignActionEnum {

        /** 新增报名 */
        ADD_SIGN_UP("新增报名", "add_sign_up"),
        DELETE_SIGN_UP("删除报名", "delete_sign_up"),
        ADD_SIGN_IN("新增签到", "add_sign_in"),
        DELETE_SIGN_IN("删除签到", "delete_sign_in");

        private final String name;
        private final String value;

        SignActionEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static SignActionEnum fromValue(String value) {
            SignActionEnum[] values = SignActionEnum.values();
            for (SignActionEnum signActionEnum : values) {
                if (Objects.equals(signActionEnum.getValue(), value)) {
                    return signActionEnum;
                }
            }
            return null;
        }
    }

}