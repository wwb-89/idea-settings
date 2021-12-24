CREATE TABLE `t_template_custom_app_config` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `template_component_id` int(11) DEFAULT NULL COMMENT '自定义应用模板组件id',
    `type` varchar(50) DEFAULT NULL COMMENT '链接类型，frontend：前端，backend：后端',
    `title` varchar(50) DEFAULT NULL COMMENT '链接标题',
    `icon` varchar(50) DEFAULT NULL COMMENT '图标',
    `url` varchar(512) DEFAULT NULL COMMENT '链接',
    `sequence` int(11) DEFAULT NULL COMMENT '排序',
    `is_show_after_sign_up` tinyint(1) DEFAULT '1' COMMENT '是否在报名后显示，仅对前台链接生效',
    `is_open_blank` tinyint(1) DEFAULT '1' COMMENT '是否以新页面方式打开，仅对后台链接生效',
    `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板自定义应用配置表';