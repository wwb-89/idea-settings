-- 变更表名
ALTER TABLE t_template_custom_app_config RENAME TO t_custom_app_config;
-- 增加字段
ALTER TABLE t_custom_app_config ADD component_id INT(11) COMMENT '自定义组件id';
-- 删除活动中的openCustomAppConfig字段
ALTER TABLE t_activity DROP column is_open_custom_app_config;
-- 新建活动自定义应用配置启用表
CREATE TABLE `t_custom_app_enable` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `template_component_id` int(11) DEFAULT NULL COMMENT '模板组件id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='活动自定义应用配置启用表';