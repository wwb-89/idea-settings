ALTER TABLE t_activity ADD COLUMN is_signed_up_notice TINYINT(1) DEFAULT 0 COMMENT '报名成功是否发送通知';


-- t_component组件字段迁移至t_template_component模板组件关联关系表
ALTER TABLE t_template_component ADD type VARCHAR (50) COMMENT '组件类型。自定义组件才有类型：文本、单选、多选';
ALTER TABLE t_template_component ADD data_origin VARCHAR (50) COMMENT '数据来源';
ALTER TABLE t_template_component ADD origin_identify VARCHAR (50) COMMENT '来源主键';
ALTER TABLE t_template_component ADD field_flag VARCHAR (50) COMMENT '字段标识';

UPDATE t_template_component t, t_component t1
SET t.type = t1.type,
    t.data_origin = t1.data_origin,
    t.origin_identity = t1.origin_identity,
    t.field_flag = t1.field_flag
WHERE
    t.component_id = t1.id AND t.template_id = t1.template_id