CREATE TABLE `t_sign_up_wfw_form_template`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(50) NULL COMMENT '名称',
    `code` varchar(50) NULL COMMENT '编码',
    `sign` varchar(255) NULL COMMENT 'sign',
    `key` varchar(255) NULL COMMENT 'key',
    `form_id` int(0) NULL COMMENT '表单id',
    `fid` int(0) NULL COMMENT '表单所属机构id',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '是否系统表单',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT = '报名万能表单模版表';

