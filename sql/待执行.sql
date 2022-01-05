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
