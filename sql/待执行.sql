-- 组件表增加来源组件id
ALTER TABLE t_component ADD origin_id INT ( 11 ) NULL COMMENT '来源id(克隆)';
-- 市场字段表增加系统字段标识、自定义组件源id
ALTER TABLE t_market_table_field ADD origin_component_id INT ( 11 ) NULL COMMENT '自定义组件源id';
