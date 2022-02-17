--2022-02-15 自定义应用接口调用
CREATE TABLE `t_custom_app_interface_call` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '组件',
    `template_component_id` int(11) DEFAULT NULL COMMENT '自定义应用模版组件id',
    `url` varchar(255) DEFAULT NULL COMMENT '接口地址',
    `is_create_call` tinyint(1) DEFAULT 0 COMMENT '是否创建时调用',
    `is_release_call` tinyint(1) DEFAULT 0 COMMENT '是否发布时调用',
    `is_cancel_release_call` tinyint(1) DEFAULT 0 COMMENT '是否下架时调用',
    `is_start_call` tinyint(1) DEFAULT 0 COMMENT '是否开始时调用',
    `is_end_call` tinyint(1) DEFAULT 0 COMMENT '是否结束时调用',
    `is_delete_call` tinyint(1) DEFAULT 0 COMMENT '是否删除时调用',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`template_component_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义应用接口调用';
CREATE TABLE `t_custom_app_interface_call_record` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `template_component_id` int(11) DEFAULT NULL COMMENT '自定义应用模版组件id',
    `call_time` datetime DEFAULT NULL COMMENT '调用时间',
    `status` int(11) DEFAULT NULL COMMENT '状态。0：失败，1：成功',
    `message` varchar(255) DEFAULT NULL COMMENT '错误原因',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`activity_id`,`template_component_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义应用接口调用记录表';


ALTER TABLE t_custom_app_interface_call ADD component_id INT ( 11 ) NULL COMMENT '自定义组件id';
-- 修复万能表单创建的活动的origin_type都为normal的问题
UPDATE t_activity t SET t.origin_type = 'wfw_form' WHERE t.origin_type = 'normal' AND t.origin_form_user_id IS NOT NULL;
