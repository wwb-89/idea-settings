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
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `qualified_status` int(11) NULL DEFAULT 2 COMMENT '合格状态。0：不合格，1：合格，2：待处理',
    `manual_qualified_status` int(11) NULL DEFAULT 2 COMMENT '手动评审的合格状态。。0：不合格，1：合格，2：待处理',
    `auto_qualified_status` int(11) NULL DEFAULT 2 COMMENT '自动评审的合格状态。0：不合格，1：合格，2：待处理',
    `total_score` decimal(10, 2) NULL COMMENT '总得分',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`uid`, `activity_id`, `qualified_status`),
    INDEX `idx_activity_status`(`activity_id`, `qualified_status`)
) COMMENT = '用户成绩表';
CREATE TABLE `t_user_action_record`  (
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `action_type` varchar(50) NULL COMMENT '行为类型',
    `action` varchar(50) NULL COMMENT '具体行为',
    `action_identify` varchar(50) NULL COMMENT '行为标识。行为id等',
    `action_description` varchar(255) NULL COMMENT '行为描述',
    `is_valid` tinyint(1) NULL DEFAULT 1 COMMENT '是否有效的',
    `create_time` datetime(0) NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`uid`, `activity_id`)
) COMMENT = '用户行为记录表';
ALTER TABLE t_user_stat_summary DROP COLUMN qualified_num;
ALTER TABLE t_activity_stat_summary DROP COLUMN qualified_num;
DROP TABLE t_user_data_push_record;
CREATE TABLE `t_data_push_record`  (
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `identify` varchar(50) NULL COMMENT '主键标识',
    `data_type` varchar(50) NULL COMMENT '数据类型',
    `repo_type` varchar(50) NULL COMMENT '仓库类型',
    `repo` varchar(50) NULL COMMENT '仓库',
    `record` varchar(50) NULL COMMENT '推送记录标识',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`identify`, `repo_type`, `data_type`, `repo`)
) COMMENT = '数据推送记录表';
-- 活动表单推送记录数据迁移sql
INSERT INTO t_data_push_record ( identify, data_type, repo_type, repo, record )
SELECT
    t.activity_id,
    'activity',
    'form',
    t.form_id,
    t.form_user_Id
FROM
    t_activity_form_record t