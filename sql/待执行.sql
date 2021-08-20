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

-- 新增现场报名子组件
INSERT INTO `activity_engine`.`t_component`(`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (41, 12, '现场报名', 'on_site_sign_up', 1, '', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-08-18 14:19:07', 25418810, '2021-08-20 16:40:37', 25418810);
INSERT INTO `activity_engine`.`t_component`(`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (42, 13, '现场报名', 'on_site_sign_up', 1, '', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-08-18 14:19:07', 25418810, '2021-08-20 16:40:46', 25418810);

INSERT INTO t_activity_market ( activity_id, market_id, STATUS, is_released, is_top )
SELECT
    t.id,
    t.market_id,
    t.`status`,
    t.is_released,
    0
FROM
    t_activity t;

ALTER TABLE t_sign_up_fill_info_type change COLUMN form_id template_type VARCHAR(50) COMMENT '模板类型';

-- 签到签退组件合并成签到组件
INSERT INTO `activity_engine`.`t_component`(`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (39, 0, '签到', 'sign_in_out', 0, '可以发布和编辑本次活动的签到', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-08-17 19:34:05', 25418810, '2021-08-17 19:36:33', 25418810);
DELETE FROM t_component WHERE id IN (14, 15);
UPDATE t_template_component t SET t.component_id = 39 WHERE t.component_id = 14;
UPDATE t_template_component t SET t.is_deleted = 1 WHERE t.component_id = 15;
ALTER TABLE t_market ADD sign_up_activity_limit INT(11) DEFAULT 0 COMMENT '同时报名活动数限制';
INSERT INTO `activity_engine`.`t_component`(`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (40, 0, '分区', 'partition', 0, NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, '2021-08-20 15:20:44', NULL, '2021-08-20 15:21:41', NULL);
INSERT INTO `activity_engine`.`t_template_component` ( `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted` );
SELECT 0, t.id, 40, '基本信息', '', 0, -1, 0 FROM t_template t WHERE t.id > 10000;
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (104, 0, 1, 40, '基本信息', '', 0, -1, 0);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (105, 0, 2, 40, '基本信息', '', 0, -1, 0);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (106, 0, 3, 40, '基本信息', '', 0, -1, 0);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (107, 0, 4, 40, '基本信息', '', 0, -1, 0);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (108, 0, 5, 40, '基本信息', '', 0, -1, 0);