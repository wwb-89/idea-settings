ALTER TABLE `t_activity_create_permission` ADD COLUMN group_type VARCHAR(50) DEFAULT 'wfw' COMMENT '组织架构类型';
CREATE TABLE `t_activity_detail`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) NULL COMMENT '活动id',
    `introduction` longtext NULL COMMENT '简介',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`)
) COMMENT = '活动详情表';
DROP TABLE t_user_action;
DROP TABLE t_user_action_detail;
CREATE TABLE `t_user_result`  (
    `id` bigint(0) NOT NULL COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `qualified_status` int(11) NULL DEFAULT 2 COMMENT '合格状态。0：不合格，1：合格，2：待处理',
    `total_score` decimal(10, 2) NULL COMMENT '总得分',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`uid`, `activity_id`, `qualified_status`),
    INDEX `idx_activity_status`(`activity_id`, `qualified_status`)
) COMMENT = '用户成绩表';
CREATE TABLE `t_user_action`  (
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `action_type` varchar(50) NULL COMMENT '行为类型',
    `action` varchar(50) NULL COMMENT '具体行为',
    `action_identify` varchar(50) NULL COMMENT '行为标识。行为id等',
    `action_description` varchar(255) NULL COMMENT '行为描述',
    `score` decimal(10, 2) NULL DEFAULT 0 COMMENT '得分',
    `create_time` datetime(0) NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`uid`, `activity_id`)
) COMMENT = '用户行为表';