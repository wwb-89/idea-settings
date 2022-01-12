ALTER TABLE t_activity_menu_config ADD `is_enable` TINYINT(1) DEFAULT 1 COMMENT '是否启用';
ALTER TABLE t_activity_menu_config ADD `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统菜单';
ALTER TABLE t_activity_menu_config ADD `template_component_id` INT(11) COMMENT '模板组件id';

INSERT INTO `activity_engine`.`t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (51, 0, '表单采集', 'form_collection', 0, '', 1, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-12 15:34:05', 172649568, '2022-01-12 15:34:05', 172649568);
-- 刷菜单数据
-- 原有表中的均为系统菜单，所以将表中现有数据置为系统标识
UPDATE t_activity_menu_config SET is_system = 1;
-- 插入数据
INSERT INTO t_activity_menu_config ( activity_id,  menu, template_component_id, is_enable, is_system)
SELECT DISTINCT
    t.id AS activity_id,
    t2.id AS menu ,
    t1.id AS template_component_id,
    0 as `is_enable`,
    0 as `is_system`
FROM
    t_activity t
        INNER JOIN (
            SELECT
                id,
                template_id
            FROM
                t_template_component
            WHERE
                    is_deleted = 0
              AND id IN ( SELECT DISTINCT template_component_id FROM t_custom_app_config WHERE template_component_id IS NOT NULL AND is_deleted = 0 AND `type` = 'backend' )) t1 ON t1.template_id = t.template_id
        INNER JOIN t_custom_app_config t2 ON t2.template_component_id = t1.id and t2.is_deleted = 0 and t2.type = 'backend'
ORDER BY activity_id;
