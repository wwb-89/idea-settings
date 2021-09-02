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


-- 创建活动班级关联表
CREATE TABLE `t_activity_class` (
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `class_id` int(11) DEFAULT NULL COMMENT '班级id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动班级关联表';

-- 阅读创建组件
INSERT INTO `t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (43, 0, '阅读', 'reading', 0, '开启后，可以从书库导入或上传图书，并配置阅读测评', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-09-02 12:14:20', 25418810, '2021-09-02 12:14:37', 25418810);

-- 活动新增字段
ALTER TABLE t_activity ADD COLUMN is_open_reading TINYINT(1) DEFAULT 0 COMMENT '是否开启阅读';
ALTER TABLE t_activity ADD COLUMN reading_id INT(11) COMMENT '阅读id';
ALTER TABLE t_activity ADD COLUMN reading_module_id INT(11) COMMENT '阅读模块id';
