CREATE TABLE `t_blacklist` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) DEFAULT NULL COMMENT '市场id',
    `uid` int(11) DEFAULT NULL COMMENT '用户id',
    `join_time` datetime DEFAULT NULL COMMENT '加入黑名单时间',
    `create_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`market_id`,`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='黑名单表';
CREATE TABLE `t_blacklist_record` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) DEFAULT NULL COMMENT '市场id',
    `uid` int(11) DEFAULT NULL COMMENT '用户id',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `not_signed_in_num` int(11) DEFAULT NULL COMMENT '未签次数',
    `is_handled` tinyint(1) DEFAULT NULL COMMENT '是否已处理',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`market_id`,`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='黑名单记录表';
CREATE TABLE `t_blacklist_rule` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) DEFAULT NULL COMMENT '活动市场id',
    `not_sign_in_upper_limit` int(11) DEFAULT NULL COMMENT '未签到上限',
    `is_enable_auto_remove` tinyint(1) DEFAULT NULL COMMENT '是否启用自动移除',
    `auto_remove_days` int(11) DEFAULT NULL COMMENT '自动移除天数',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`market_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='黑名单规则表';