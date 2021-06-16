-- 2021-06-16 用户行为、考核配置表
CREATE TABLE `t_user_action`  (
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户uid',
    `activity_id` int(11) NULL COMMENT '活动id',
    `total_score` decimal(10, 2) NULL COMMENT '总得分',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`uid`, `activity_id`)
) COMMENT = '用户行为表';

CREATE TABLE `t_user_action_detail`  (
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_id` int(11) NULL COMMENT '配置id',
    `action_type` varchar(50) NULL COMMENT '行为类型',
    `action` varchar(50) NULL COMMENT '具体行为',
    `action_identify` varchar(50) NULL COMMENT '行为标识。行为id等',
    `action_description` varchar(255) NULL COMMENT '行为描述',
    `score` decimal(10, 2) NULL DEFAULT 0 COMMENT '得分',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`config_id`)
) COMMENT = '用户行为详情表';
CREATE TABLE `t_inspection_config`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) NULL COMMENT '活动id',
    `pass_decide_way` varchar(50) NULL COMMENT '合格判定方式',
    `decide_value` decimal(10, 2) NULL COMMENT '判定值',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`)
) COMMENT = '考核配置';

CREATE TABLE `t_inspection_config_detail`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_id` int(11) NULL COMMENT '配置id',
    `action_type` varchar(50) NULL COMMENT '用户行为类型',
    `action` varchar(50) NULL COMMENT '行为',
    `score` decimal(10, 2) NULL COMMENT '得分',
    `upper_limit` decimal(10, 2) NULL COMMENT '上限',
    `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被删除',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`config_id`)
) COMMENT = '考核配置详情';