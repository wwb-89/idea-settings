-- 增加表单、审批模板ids集合字段
ALTER TABLE t_sign_up_fill_info_type ADD wfw_form_template_ids VARCHAR ( 100 ) NULL COMMENT '表单/审批模板ids集合';
-- 刷数据
UPDATE t_sign_up_fill_info_type SET wfw_form_template_ids = CONCAT_WS(',', wfw_form_template_id, wfw_form_approval_template_id);
-- 更新通用审批模板表单数据数据
UPDATE t_sign_up_wfw_form_template SET form_id = 40205, fid = 139378 where id = 7;


-- 2022-02-25 刷数据
UPDATE t_sign_up_fill_info_type SET wfw_form_template_ids =  replace (`wfw_form_template_ids`,'1,','');
UPDATE t_sign_up_fill_info_type SET wfw_form_template_ids =  replace (`wfw_form_template_ids`,',7','');
UPDATE t_sign_up_fill_info_type SET wfw_form_template_ids =  replace (`wfw_form_template_ids`,'7,','');
UPDATE t_sign_up_fill_info_type SET wfw_form_template_ids =  replace (`wfw_form_template_ids`,'7','');


-- 2022-02-28 新建表
CREATE TABLE `t_template_push_reminder_config` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_component_id` int(11) DEFAULT NULL COMMENT '模板组件id',
    `is_remind_within_role_scope` tinyint(1) DEFAULT '0' COMMENT '是否发送给报名范围内用户',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板消息推送组件配置表';
