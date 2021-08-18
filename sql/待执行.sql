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
INSERT INTO t_component ( pid, `name`, `code`, introduction, is_system, is_multi, create_uid, update_uid ) SELECT
    t.id,
    '现场报名',
    'on_site_sign_up',
    '',
    t.is_system,
    0,
    t.create_uid,
    t.update_uid
FROM
    t_component t
WHERE
    ( t.CODE = 'sign_up' OR t.CODE = 'company_sign_up' )
  AND t.is_system = 1;

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