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