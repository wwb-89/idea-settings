ALTER TABLE `t_activity_create_permission` ADD COLUMN group_type VARCHAR(50) DEFAULT 'wfw' COMMENT '组织架构类型';
CREATE TABLE `t_activity_detail`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) NULL COMMENT '活动id',
    `introduction` longtext NULL COMMENT '简介',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`)
) COMMENT = '活动详情表';