-- 增加自定义应用配置组件
INSERT INTO `t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (51, 0, '自定义应用', 'custom_application', 0, '', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-12-23 15:14:20', 172649568, '2021-12-23 15:14:20', 172649568);

-- 创建自定义应用配置组件
CREATE TABLE `t_template_custom_app_config` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `template_component_id` int(11) DEFAULT NULL COMMENT '自定义应用模板组件id',
    `type` varchar(50) DEFAULT NULL COMMENT '链接类型，frontend：前端，backend：后端',
    `title` varchar(50) DEFAULT NULL COMMENT '链接标题',
    `icon_id` int(11) DEFAULT NULL COMMENT '图标',
    `url` varchar(512) DEFAULT NULL COMMENT '链接',
    `sequence` int(11) DEFAULT NULL COMMENT '排序',
    `is_show_after_sign_up` tinyint(1) DEFAULT '1' COMMENT '是否在报名后显示，仅对前台链接生效',
    `is_open_blank` tinyint(1) DEFAULT '1' COMMENT '是否以新页面方式打开，仅对后台链接生效',
    `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板自定义应用配置表';

-- 创建图标表
CREATE TABLE `t_icon` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `name` varchar(50) DEFAULT NULL COMMENT '图标名称',
    `code` varchar(50) DEFAULT NULL COMMENT '图标code',
    `default_icon_cloud_id` varchar(128) DEFAULT NULL COMMENT '默认图标的云盘id',
    `active_icon_cloud_id` varchar(128) DEFAULT NULL COMMENT '激活状态图标的云盘id',
    `description` varchar(128) DEFAULT NULL COMMENT '图标描述，用途',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='图标表';

-- 活动新增自定义应用配置开关
ALTER TABLE t_activity ADD is_open_custom_app_config TINYINT(1) DEFAULT 0 COMMENT '是否开启自定义应用配置';
