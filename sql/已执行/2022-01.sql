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

-- 2022-01-05
CREATE TABLE `t_classify_show_component` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) DEFAULT NULL COMMENT '市场id',
    `template_id` int(11) DEFAULT NULL COMMENT '模版id',
    `classify_id` int(11) DEFAULT NULL COMMENT '分类id',
    `template_component_id` int(11) DEFAULT NULL COMMENT '模版组件id',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类显示组件表';

