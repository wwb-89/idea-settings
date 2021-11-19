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
    `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被删除',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT = '报名万能表单模版表';



ALTER TABLE t_sign_up_fill_info_type ADD wfw_form_template_id INT(11) COMMENT '报名万能表单模板id';
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 1 WHERE type = 'wfw_form' AND template_type = 'normal'
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 2 WHERE type = 'wfw_form' AND template_type = 'dual_select_company'
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 3 WHERE type = 'wfw_form' AND template_type = 'wfw_form_1'
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 4 WHERE type = 'wfw_form' AND template_type = 'hubei_qun_art_museum'

