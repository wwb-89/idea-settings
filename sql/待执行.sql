-- 2021-12-22 审批表单
ALTER TABLE t_sign_up_wfw_form_template ADD type VARCHAR(50) DEFAULT 'normal' COMMENT '类型';
ALTER TABLE t_sign_up_fill_info_type ADD wfw_form_approval_template_id INT(11) COMMENT '万能表单审批模版id';

