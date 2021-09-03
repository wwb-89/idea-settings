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



-- 班级活动模板
INSERT INTO `activity_engine`.`t_template` (`id`, `name`, `market_id`, `origin_template_id`, `is_system`, `activity_flag`, `fid`, `cover_url`, `sequence`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (7, '班级活动', NULL , NULL , 1, 'class', NULL, '', 7, '2021-09-02 17:15:17', 172649568, '2021-09-02 17:15:17', 172649568);

INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (130, 0, 7, 1, '名称', NULL, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (131, 0, 7, 2, '时间', NULL, 1, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (132, 0, 7, 3, '封面', NULL, 1, 3, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (133, 0, 7, 11, '发布班级', '按照发布班级选择活动范围', 1, 4, 0, NULL, 'interface', '', NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (134, 0, 7, 16, '创作', '开启后，参与者可以在活动中提交作品，管理员在征集模块中进行审核评分推优等操作', 0, 6, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (135, 0, 7, 20, '活动说明', NULL, 0, 9, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (136, 0, 7, 40, '基本信息', '', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (137, 0, 7, 40, '模块设置', '', 0, 5, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (138, 0, 7, 43, '阅读', '开启后，可以从书库导入或上传图书，并配置阅读测评', 0, 7, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (139, 0, 7, 40, '活动说明', NULL, 0, 8, 0, NULL, NULL, NULL, NULL);

-- 学校活动模板
INSERT INTO `activity_engine`.`t_template` (`id`, `name`, `market_id`, `origin_template_id`, `is_system`, `activity_flag`, `fid`, `cover_url`, `sequence`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (8, '学校活动', NULL , NULL , 1, 'school', NULL, '', 8, '2021-09-02 17:15:17', 172649568, '2021-09-02 17:15:17', 172649568);

INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (140, 0, 8, 1, '名称', NULL, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (141, 0, 8, 2, '时间', NULL, 1, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (142, 0, 8, 3, '封面', NULL, 1, 3, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (143, 0, 8, 16, '创作', '开启后，参与者可以在活动中提交作品，管理员在征集模块中进行审核评分推优等操作', 0, 6, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (144, 0, 8, 20, '活动说明', NULL, 0, 9, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (145, 0, 8, 40, '基本信息', '', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (146, 0, 8, 40, '模块设置', '', 0, 5, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (147, 0, 8, 43, '阅读', '开启后，可以从书库导入或上传图书，并配置阅读测评', 0, 7, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (148, 0, 8, 40, '活动说明', NULL, 0, 8, 0, NULL, NULL, NULL, NULL);

-- 区域模板
INSERT INTO `activity_engine`.`t_template` (`id`, `name`, `market_id`, `origin_template_id`, `is_system`, `activity_flag`, `fid`, `cover_url`, `sequence`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (9, '区域活动', NULL , NULL , 1, 'region', NULL, '', 9, '2021-09-02 17:15:17', 172649568, '2021-09-02 17:15:17', 172649568);

INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (149, 0, 9, 1, '名称', NULL, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (150, 0, 9, 2, '时间', NULL, 1, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (151, 0, 9, 3, '封面', NULL, 1, 3, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (152, 0, 9, 11, '发布区域', '按照发布区域选择活动范围', 1, 4, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (153, 0, 9, 16, '创作', '开启后，参与者可以在活动中提交作品，管理员在征集模块中进行审核评分推优等操作', 0, 6, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (154, 0, 9, 20, '活动说明', NULL, 0, 9, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (155, 0, 9, 40, '基本信息', '', 0, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (156, 0, 9, 40, '模块设置', '', 0, 5, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (157, 0, 9, 43, '阅读', '开启后，可以从书库导入或上传图书，并配置阅读测评', 0, 7, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (158, 0, 9, 40, '活动说明', NULL, 0, 8, 0, NULL, NULL, NULL, NULL);


