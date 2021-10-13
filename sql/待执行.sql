update t_activity_manager set menu= CONCAT(menu, ',notice,setting') where menu is not null and menu != '';

ALTER TABLE t_market ADD is_enable_contacts TINYINT(1) DEFAULT 1 COMMENT '是否启用通讯录';
ALTER TABLE t_market ADD is_enable_organization TINYINT(1) DEFAULT 1 COMMENT '是否启用组织架构';
ALTER TABLE t_market ADD is_enable_regional TINYINT(1) DEFAULT 1 COMMENT '是否启用区域架构';

CREATE TABLE `t_big_data_point_push_record`  (
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `point_type` int(11) NULL COMMENT '积分类型',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`, `uid`)
) COMMENT = '大数据积分推送记录表';
