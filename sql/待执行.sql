CREATE TABLE `t_activity_market`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) NULL COMMENT '活动id',
    `market_id` int(11) NULL COMMENT '市场id',
    `status` int(11) NULL COMMENT '活动状态',
    `is_released` tinyint(1) NULL DEFAULT 0 COMMENT '是否发布',
    `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`, `market_id`),
    INDEX `idx_market_id`(`market_id`)
) COMMENT = '活动与活动市场关联表';