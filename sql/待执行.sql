-- 2021-12-22 审批表单
ALTER TABLE t_sign_up_wfw_form_template ADD type VARCHAR(50) DEFAULT 'normal' COMMENT '类型';