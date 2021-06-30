-- 2021-06-29
DROP TABLE t_activity_form_record;
CREATE TABLE `t_award`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) NULL COMMENT '活动id',
    `name` varchar(50) NULL COMMENT '奖项名称',
    `level` varchar(50) NULL COMMENT '奖项级别',
    `description` varchar(255) NULL COMMENT '说明',
    `is_released` tinyint(1) NULL DEFAULT 0 COMMENT '是否发布',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`)
) COMMENT = '活动奖项表';
CREATE TABLE `t_user_award`  (
    `id` integer(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `award_id` int(11) NULL COMMENT '奖项id',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`award_id`, `uid`),
    INDEX `idx_activity_uid`(`activity_id`, `uid`),
    INDEX `idx_uid`(`uid`)
) COMMENT = '用户获奖表';
CREATE TABLE `t_performance`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `score` decimal(10, 2) NULL COMMENT '得分',
    `remark` varchar(255) NULL COMMENT '评语',
    `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被删除',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`, `uid`)
) COMMENT = '用户活动表现';
CREATE TABLE `t_market_table_field`  (
    `fid` int(11) NULL COMMENT '机构id',
    `activity_flag` varchar(50) NULL COMMENT '活动标识',
    `table_field_id` int(11) NULL COMMENT '表格字段配置id',
    `table_field_detail_id` int(11) NULL COMMENT '表格字段详细配置id',
    `is_top` tinyint(1) NULL COMMENT '是否置顶',
    `sequence` int(11) NULL COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `create_uid` int(11) NULL COMMENT '创建人uid',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `update_uid` int(11) NULL COMMENT '更新人uid',
    INDEX `idx_primary`(`fid`, `activity_flag`, `table_field_id`)
) COMMENT = '活动市场表格字段关联表';