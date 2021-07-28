CREATE TABLE `t_blacklist`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) NULL COMMENT '市场id',
    `uid` int(11) NULL COMMENT '用户id',
    `user_name` varchar(50) NULL COMMENT '姓名',
    `account` varchar(50) NULL COMMENT '账号',
    `default_num` int(11) NULL COMMENT '违约次数',
    `join_type` varchar(50) NULL COMMENT '加入方式',
    `effective_days` int(11) NULL COMMENT '有效天数',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`market_id`, `uid`)
) COMMENT = '黑名单表';

CREATE TABLE `t_blacklist_record`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) NULL COMMENT '市场id',
    `uid` int(11) NULL COMMENT '用户id',
    `user_name` varchar(50) NULL COMMENT '姓名',
    `account` varchar(50) NULL COMMENT '账号',
    `activity_id` int(11) NULL COMMENT '活动id',
    `not_signed_in_num` int(11) NULL COMMENT '未签次数',
    `is_handled` tinyint(1) NULL DEFAULT 0 COMMENT '是否已处理',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`market_id`, `uid`)
) COMMENT = '黑名单记录表';

CREATE TABLE `t_blacklist_rule`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) NULL COMMENT '活动市场id',
    `not_sign_in_upper_limit` int(11) NULL COMMENT '未签到上限',
    `is_enable_auto_remove` tinyint(1) NULL COMMENT '是否启用自动移除',
    `auto_remove_days` int(11) NULL COMMENT '自动移除天数',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`market_id`)
) COMMENT = '黑名单规则表';

