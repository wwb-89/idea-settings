-- 变更表名
ALTER TABLE t_template_custom_app_config RENAME TO t_custom_app_config;
-- 增加字段
ALTER TABLE t_custom_app_config ADD component_id INT(11) COMMENT '自定义组件id';
-- 删除活动中的openCustomAppConfig字段
ALTER TABLE t_activity DROP column is_open_custom_app_config;
-- 新建活动自定义应用配置启用表
CREATE TABLE `t_custom_app_enable` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `template_component_id` int(11) DEFAULT NULL COMMENT '模板组件id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='活动自定义应用配置启用表';

-- 2022-01-05
CREATE TABLE `t_classify_show_component` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) DEFAULT NULL COMMENT '市场id',
    `template_id` int(11) DEFAULT NULL COMMENT '模版id',
    `classify_id` int(11) DEFAULT NULL COMMENT '分类id',
    `template_component_id` int(11) DEFAULT NULL COMMENT '模版组件id',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类显示组件表';

-- 2022-01-10
-- 修改活动自定义组件值大小，增加cloud_ids字段
ALTER TABLE t_activity_component_value MODIFY COLUMN `value` varchar(512) DEFAULT NULL COMMENT '值';
ALTER TABLE t_activity_component_value ADD COLUMN cloud_ids TEXT DEFAULT NULL COMMENT '云盘id集合';

-- 清空图标表所有数据
TRUNCATE`activity_engine`.`t_icon`;
-- 整理图标表数据
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (1, '模块', 'icon-module', 'ed6a262e84e73b416e5c55bd5c2516a2', '1e05b0a5e2a2f2d660fd0b5641e39471', '万能图标');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (2, '海报', 'icon-poster', '22c57091cc13dfafaf76bde7fe1be325', 'c99fc842a326623336b885c79bf9b5ba', '海报');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (3, '批量通知', 'icon-volume-notice', '7e7077af45c61ab77768a33d405df541', '9685a9453a787717861b55118f2437a8', '批量通知');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (4, '操作', 'icon-operation', '05c0fd85164bcc58f843005aaba32661', 'c83d6604e514b94d949b1a78e0a942b5', '操作');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (5, '工程', 'icon-project', '3195f9b88eda205480335e7e5c1883a4', 'e3cb780ba896abfe094249c682778bde', '工程');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (6, '学校', 'icon-school', '17cec1d013f74a775053475afa34ac75', '65d2c2a1ae0b1eebec5481df373785ae', '学校');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (7, '讨论', 'icon-discuss', 'ddde37c09e93fa95a69e9da2a673133e', '44609897a4d245609b2bca7fb5cd6df1', '讨论');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (8, '工作', 'icon-works', '4feb6b680c6b9850f37f1be25d90187f', 'e804ad8ec910c7ea68a0ee833722ca03', '任务');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (9, '奖项', 'icon-awards', '650cb712dcdbe129276a536a8a8d23f3', '0280d8ee981ec033f42a92b49af6855a', '奖项');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (10, '信息', 'icon-infomation', '03c02163e87aca3751f311d349e4f22b', 'a50145ce7d461f968ac76a6aa3ce28ea', '信息');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (11, '访问', 'icon-assess', 'ccf5280e9f2d977d3413f8588c00cafc', '4d221d8d29baf60ce69012f3e33bf939', '访问');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (12, '测试', 'icon-exam', 'fa2cd8cd6d383792639db5bad7048d7a', '1aabce90c338d1a7e4ef1e415d002d8a', '测试');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (13, '基础信息', 'icon-basic-info', '2d99583931e638255d9177b9b20eda0f', '2a0e10250071a2589c21e58c02c0b6e1', '基础信息');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (14, '全部任务', 'icon-all-work', 'cb25f04c5a47ea714eefe45496a1fd62', 'c21864b36fb291bcda7b54cfad872ec5', '全部任务');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (15, '用户2', 'icon-user2', '3dc9e19439eaaf022859b3aaab365939', '034585e081762e05e7bfac69a5a5956e', '用户2');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (16, '图表', 'icon-chart', 'a3e15ebcaed470f212c6966ad02e6d01', '89363f7ce4675c13ed523ac2859162ef', '图表');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (17, '审计', 'icon-audit', 'deda5d80b2421a029d4fc42f903d6dd7', '6fa12f4c4c9e630e6f979657883b433c', '审计');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (18, '星', 'icon-star', '6d774fb641742a7231ff79966a2f9a24', '7d2f6a312b7bb9e6419648315e46a2f5', '星');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (19, '签到', 'icon-sign-in', 'e378ad899b5492fe611f5c994f9325fe', 'd8165ad48aae2796991c9fb59fa6dc76', '签到');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (20, '得分', 'icon-score', '53dc53d6d70552b83a16abe1dabe18ae', '217b1453828c3bd34a9c2ae4bd2255c9', '得分');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (21, '提交', 'icon-submit', '2f6f17a2758d79edadbb30020f0bf815', '026c1f67bc22f7af9167117a3f89f8cf', '提交');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (22, '证书', 'icon-certificate', 'b5f5f02cf22245d71f65e3f1875bcf8e', 'f9a3c9c56c6bdbd500a852795a8fe61c', '证书');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (23, '设置', 'icon-setting', '22d47dc1c6af8fceda4932bfd429c55e', '1c21c39ef4ffef529cd557d541b2de8b', '设置');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (24, '注册表', 'icon-registration-list', 'cc87a8c7bea2a33a01d8e929883fe72d', '5811335d98deffe555c65e687f75b37e', '注册表');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (25, '互动', 'icon-interaction', 'f067123c984528e13d72f5952f7716a6', '013e71746d78c51f11672f08b4422a89', '互动');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (26, '请求上传', 'icon-solicit', '980a699c0c3f4f5ba4bacb3557064b85', 'fd42d5322ecdb6e0050663f98d3d0019', '请求上传');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (27, '统计', 'icon-statistics', '5fe264256bddbcaf81aa7c467ac6bfc4', 'ed06abc73ecf621315726537243cfb2b', '统计');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (28, '文件文本', 'icon-file-text', '2af85e04a6cfccbb7d3f1581636ceb79', '9c8efa09a4bc971b8d0c1075dd203485', '文件文本');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (29, '代码', 'icon-code', '35b1b26223e19b3e8fa6e7c07d12176c', 'b72e7d4c4f8f9b1a31f681e008c01b54', '代码');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (30, '设置2', 'icon-setting2', '0b056494b91ce10cc74ed0b74430b1c5', '425a83071fb642b827bb8a060d779b0a', '设置2');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (31, '位置2', 'icon-map-pin2', 'be0584d0507d0446660a11e61aa8b9d5', '41ceb1de60f4a7e5a16d18cfb0f0e1ec', '位置2');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (32, '任务', 'icon-task', '670f8264cc7b8a2c381292f3d6f20f4b', '74ca72309d53bb605f94a49098e4b28c', '任务图标');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (33, '评审', 'icon-review-management', '005ca2b504bf6f6f212347ee90e6de15', 'da4882cec8f38768dfc5bf515de44374', '评审管理图标');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (34, '作业', 'icon-homework', '1ff6b8167b8b7854131a273f4ca66599', 'f5b89f4d64c1bd8732406c0730cff170', '作业图标');
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (35, '讨论2', 'icon-discuss2', 'b92e2e9564238d821ab84d58a1fb4f34', '82a721fdf200d19c3c26786e038457d6', '讨论图标');

-- 2022-01-13
ALTER TABLE t_activity_menu_config ADD `is_enable` TINYINT(1) DEFAULT 1 COMMENT '是否启用';
ALTER TABLE t_activity_menu_config ADD `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统菜单';
ALTER TABLE t_activity_menu_config ADD `template_component_id` INT(11) COMMENT '模板组件id';

INSERT INTO `activity_engine`.`t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (51, 0, '表单采集', 'form_collection', 0, '', 1, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-12 15:34:05', 172649568, '2022-01-12 15:34:05', 172649568);
-- 刷菜单数据
-- 原有表中的均为系统菜单，所以将表中现有数据置为系统标识
UPDATE t_activity_menu_config SET is_system = 1;
-- 插入数据
INSERT INTO t_activity_menu_config ( activity_id,  menu, template_component_id, is_enable, is_system)
SELECT DISTINCT
    t.id AS activity_id,
    t2.id AS menu ,
    t1.id AS template_component_id,
    0 as `is_enable`,
    0 as `is_system`
FROM
    t_activity t
        INNER JOIN (
        SELECT
            id,
            template_id
        FROM
            t_template_component
        WHERE
                is_deleted = 0
          AND id IN ( SELECT DISTINCT template_component_id FROM t_custom_app_config WHERE template_component_id IS NOT NULL AND is_deleted = 0 AND `type` = 'backend' )) t1 ON t1.template_id = t.template_id
        INNER JOIN t_custom_app_config t2 ON t2.template_component_id = t1.id and t2.is_deleted = 0 and t2.type = 'backend'
ORDER BY activity_id;

-- 2022-01-13 积分、学时、学分 且都可以纠正
ALTER TABLE t_user_stat_summary ADD `corrected_integral` DECIMAL(10, 2) DEFAULT NULL COMMENT '校正的积分';
ALTER TABLE t_user_stat_summary ADD `period` DECIMAL(10, 2) DEFAULT 0 COMMENT '学时';
ALTER TABLE t_user_stat_summary ADD `corrected_period` DECIMAL(10, 2) DEFAULT NULL COMMENT '校正的学时';
ALTER TABLE t_user_stat_summary ADD `credit` DECIMAL(10, 2) DEFAULT 0 COMMENT '学分';
ALTER TABLE t_user_stat_summary ADD `corrected_credit` DECIMAL(10, 2) DEFAULT NULL COMMENT '校正的学分';
ALTER TABLE t_user_stat_summary ADD `comment` VARCHAR(512) DEFAULT '' COMMENT '评语';

-- 更新图标命名
UPDATE t_icon SET name = '评估', description = '评估' WHERE id = 11;
UPDATE t_icon SET name = '审批', description = '审批' WHERE id = 17;
UPDATE t_icon SET name = '二维码', description = '二维码' WHERE id = 29;
UPDATE t_icon SET name = '考试', description = '考试' WHERE id = 12;
UPDATE t_icon SET name = '表单', code='icon-form', description = '表单' WHERE id = 4;
UPDATE t_icon SET name = '项目', description = '项目' WHERE id = 5;
UPDATE t_icon SET name = '地图2', description = '地图2' WHERE id = 31;
UPDATE t_icon SET default_icon_cloud_id = 'c994db0612ef9f0897f2edb4c2b5f3dd', active_icon_cloud_id = 'a8faf16441ae71e2e928193e4d9ddb53' WHERE id = 26;

-- 插入新的图标
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (36, '收集', 'icon-collection', '39c9f426d1d1802ba22f32715cc20c8a', '675af20ac3dff94b6c8a6cd075db489f', '收集');

ALTER TABLE t_activity_menu_config ADD `sequence` INT(11) COMMENT '排序字段';

UPDATE t_activity_menu_config SET `sequence` = 1 WHERE `menu` = 'sign_up';
UPDATE t_activity_menu_config SET `sequence` = 10 WHERE `menu` = 'sign_in';
UPDATE t_activity_menu_config SET `sequence` = 20 WHERE `menu` = 'form_collection';
UPDATE t_activity_menu_config SET `sequence` = 30 WHERE `menu` = 'results_manage';
UPDATE t_activity_menu_config SET `sequence` = 40 WHERE `menu` = 'certificate';
UPDATE t_activity_menu_config SET `sequence` = 50 WHERE `menu` = 'notice';
UPDATE t_activity_menu_config SET `sequence` = 60 WHERE `menu` = 'stat';
UPDATE t_activity_menu_config SET `sequence` = 70 WHERE `menu` = 'task';
UPDATE t_activity_menu_config SET `sequence` = 80 WHERE `menu` = 'discuss';
UPDATE t_activity_menu_config SET `sequence` = 90 WHERE `menu` = 'homework';
UPDATE t_activity_menu_config SET `sequence` = 100 WHERE `menu` = 'review_management';
UPDATE t_activity_menu_config SET `sequence` = 500 WHERE `menu` = 'setting';
UPDATE t_activity_menu_config SET `sequence` = 150 WHERE `menu` not in ('sign_up','sign_in','form_collection','results_manage','certificate','notice','stat','task','discuss','homework','review_management','setting');
UPDATE t_activity_menu_config SET is_system = 1 WHERE `menu` in ('sign_up','sign_in','form_collection','results_manage','certificate','notice','stat','task','discuss','homework','review_management','setting');

UPDATE t_user_stat_summary t,
    t_activity t1
SET t.period = t1.period,
    t.credit = t1.credit
WHERE
    t.activity_id = t1.id;

-- 2022-01-18
INSERT INTO `activity_engine`.`t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (52, 13, '角色范围', 'sign_up_role_limit', 0, '开启后，报名者需要是对应角色才能报名', 1, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-18 10:48:54', 25418810, '2022-01-18 10:50:35', 25418810);

-- 2022-01-19
ALTER TABLE t_activity_stat_summary ADD pv INTEGER DEFAULT 0 COMMENT 'pv';

-- 创建活动自定义菜单配置表
CREATE TABLE `t_activity_custom_app_config` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `title` varchar(50) DEFAULT NULL COMMENT '菜单名称',
    `url` varchar(512) DEFAULT NULL COMMENT '菜单url',
    `icon_id` int(11) DEFAULT NULL COMMENT '图标id',
    `type` varchar(50) DEFAULT NULL COMMENT '链接类型，frontend：前端，backend：后端',
    `is_pc` tinyint(1) DEFAULT '1' COMMENT '是否pc端菜单',
    `show_rule` varchar(50) DEFAULT 'no_limit' COMMENT '显示规则(no_limit, before_sign_up, after_sign_up)',
    `is_mobile` tinyint(255) DEFAULT '0' COMMENT '是否移动端菜单',
    `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动自定义菜单配置表';

-- 增加管理员表menu字段长度
ALTER TABLE t_activity_manager MODIFY COLUMN `menu` varchar(1024) DEFAULT NULL COMMENT '菜单(多个时用,分隔)';

-- 删除模板自定义配置表sequence字段
ALTER TABLE t_custom_app_config DROP COLUMN `sequence`;

-- 删除活动菜单配置表中的is_system字段，增加data_origin字段
ALTER TABLE t_activity_menu_config DROP COLUMN `is_system`;
ALTER TABLE t_activity_menu_config ADD COLUMN `show_rule` varchar(50) DEFAULT 'no_limit' COMMENT '显示规则(no_limit, before_sign_up, after_sign_up)';
ALTER TABLE t_activity_menu_config ADD COLUMN `data_origin` varchar(50) DEFAULT NULL COMMENT '菜单来源：system-系统，template-模板，activity-活动';
-- 刷数据
UPDATE t_activity_menu_config SET data_origin = 'system' WHERE template_component_id IS NULL;
UPDATE t_activity_menu_config SET data_origin = 'template' WHERE template_component_id IS NOT NULL;
-- 模板自定义菜单增加前缀
UPDATE t_activity_menu_config SET menu = CONCAT('template_', menu) WHERE template_component_id IS NOT NULL;


ALTER TABLE t_activity_custom_app_config ADD COLUMN `is_open_blank` tinyint(1) DEFAULT 1 COMMENT '是否新页面打开菜单';


