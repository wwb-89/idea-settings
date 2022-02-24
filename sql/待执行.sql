-- 增加表单、审批模板ids集合字段
ALTER TABLE t_sign_up_fill_info_type ADD wfw_form_template_ids VARCHAR ( 100 ) NULL COMMENT '表单/审批模板ids集合';
-- 刷数据
UPDATE t_sign_up_fill_info_type SET wfw_form_template_ids = CONCAT_WS(',', wfw_form_template_id, wfw_form_approval_template_id);
-- 更新通用审批模板表单数据数据
UPDATE t_sign_up_wfw_form_template SET form_id = 40205, fid = 139378 where id = 7;