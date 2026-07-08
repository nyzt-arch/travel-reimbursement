```sql
drop database if exists `travel_reimbursement`;
CREATE DATABASE IF NOT EXISTS `travel_reimbursement` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `travel_reimbursement`;

--关闭外键约束校验
SET FOREIGN_KEY_CHECKS = 0;

-- ENGINE=InnoDB: mysql的默认存储引擎，仅InnoDB支持外键约束。
-- DEFAULT CHARSET=utf8mb4：默认字符集,MySQL中的utf8是阉割版，不完整
-- COLLATE=utf8mb4_general_ci  general通用排序规则    ci大小写不敏感
-- COMMENT='费用归属公司基础表' ：业务注释

-- 1. 基础主数据表

-- 1.1 费用归属公司表
CREATE TABLE `base_company` (
  `reim_company_id` varchar(32) NOT NULL COMMENT '公司唯一标识ID',
  `reim_company_no` varchar(32) NOT NULL COMMENT '公司编号',
  `reim_company_name` varchar(100) NOT NULL COMMENT '公司名称',
  PRIMARY KEY (`reim_company_id`)
) ENGINE=InnoDB;

-- 1.2 报销部门表
CREATE TABLE `base_department` (
  `reim_department_id` varchar(32) NOT NULL COMMENT '部门唯一标识ID',
  `reim_department_no` varchar(32) NOT NULL COMMENT '部门编号',
  `reim_department_name` varchar(100) NOT NULL COMMENT '部门名称',
  PRIMARY KEY (`reim_department_id`)
) ENGINE=InnoDB;

-- 1.3 员工基础数据表
CREATE TABLE `base_employee` (
  `reimburser_id` varchar(32) NOT NULL COMMENT '员工唯一标识ID',
  `reimburser_no` varchar(32) NOT NULL COMMENT '员工工号',
  `reimburser_name` varchar(100) NOT NULL COMMENT '员工姓名',
  PRIMARY KEY (`reimburser_id`)
) ENGINE=InnoDB;

-- 1.4 业务类型表
CREATE TABLE `base_business_type` (
  `business_type_id` varchar(32) NOT NULL COMMENT '业务类型ID',
  `business_type_no` varchar(32) NOT NULL COMMENT '业务类型编号',
  `business_type_name` varchar(100) NOT NULL COMMENT '业务类型名称',
  `there_subordinate_node` char(1) NOT NULL DEFAULT '0' COMMENT '是否有下级节点 (0:否, 1:是)',
  `superior_id` varchar(32) NOT NULL DEFAULT 'none' COMMENT '上级ID，"none"表示顶级',
  PRIMARY KEY (`business_type_id`)
) ENGINE=InnoDB;

-- 1.5 城市表
CREATE TABLE `base_city` (
  `city_no` varchar(32) NOT NULL COMMENT '城市编号(PK)',
  `city_name` varchar(100) NOT NULL COMMENT '城市名称',
  `city_type` char(1) NOT NULL COMMENT '城市类型 (1:一线, 2:二线, 3:三线)',
  PRIMARY KEY (`city_no`)
) ENGINE=InnoDB;

-- 1.6 项目基础表
CREATE TABLE `base_project` (
  `project_id` varchar(32) NOT NULL COMMENT '项目ID',
  `project_no` varchar(32) NOT NULL COMMENT '项目编号',
  `project_name` varchar(100) NOT NULL COMMENT '项目名称',
  PRIMARY KEY (`project_id`)
) ENGINE=InnoDB;

-- 1. 注入公司数据
INSERT INTO `base_company` (`reim_company_id`, `reim_company_no`, `reim_company_name`) VALUES
('1C54557F1782E000', '0407', '胜意科技北京分公司'),
('19218A262C976000', '0408', '胜意科技上海分公司'),
('1C61686865DA8000', '0409', '胜意科技武汉分公司'),
('1717271D1DA15000', '0410', '胜意科技杭州分公司'),
('16AE93CC7EF92002', '0411', '胜意科技荆州分公司');

-- 2. 注入部门数据
INSERT INTO `base_department` (`reim_department_id`, `reim_department_no`, `reim_department_name`) VALUES
('13AB8D7B52A9B002', '072001', '客户成功事业部'),
('13BFD31C6029A002', '072002', '企业消费事业部'),
('14515BB4BFB92003', '072003', '企业费控事业部'),
('19206611C47A6000', '072004', '集采事业部'),
('19D32F9FE9647000', '072005', '航旅事业部'),
('13C7E2BAE0393001', '072006', '运营事业部'),
('14055D22BB808001', '072007', '营销事业部');

-- 3. 注入员工数据
INSERT INTO `base_employee` (`reimburser_id`, `reimburser_no`, `reimburser_name`) VALUES
('13AB3A3F72409002', '74541', '徐年年'),
('13AB498CC6409002', '74008', '郑雨雪'),
('13AB4A56BB009002', '21552', '邹薇'),
('13AB591FE8009002', '80681', '王成军'),
('13AB77281A408001', '89899', '潘展飞'),
('13AB7925EB808001', '10503', '姜林');

-- 4. 注入出差城市和城市类型（1一线，2二线，3三线）
INSERT INTO `base_city` (`city_no`, `city_name`, `city_type`) VALUES
('10119', '北京', '1'),
('10621', '上海', '1'),
('10458', '武汉', '2'),
('10216', '杭州', '2'),
('10455', '荆州', '3');

-- 5. 注入分摊项目数据
INSERT INTO `base_project` (`project_id`, `project_no`, `project_name`) VALUES
('12BC248B25083001', 'nonProjectRelated', '非项目类费用归集'),
('1C811ABF96195000', 'centralChina', '华中客户定制化项目'),
('1C5931735AC4A000', 'southChina', '华南客户定制化项目'),
('1771EC45F2443000', 'northChina', '华北客户定制化项目'),
('1762792DB4E9A002', 'eastChina', '华东客户定制化项目'),
('17071065FC29A002', 'southWest', '西南客户定制化项目'),
('162664EBE9ABE001', 'northWest', '西北客户定制化项目'),
('162664B8526BE002', 'northEast', '东北客户定制化项目');

-- 6. 注入业务类型数据
INSERT INTO `base_business_type` (`business_type_id`, `business_type_no`, `business_type_name`, `there_subordinate_node`, `superior_id`) VALUES
('18F0916A8C2C4000', '1001001', '员工差旅活动', '1', 'none'),
('18F091913EEC4000', '100100101', '境内出差', '1', '18F0916A8C2C4000'),
('1B5FEB7DD4396000', '10010010101', '项目出差', '0', '18F091913EEC4000'),
('1A92E43082EFC000', '10010010102', '市场拓展出差', '0', '18F091913EEC4000'),
('13AB3A4138008001', '100100102', '境外出差', '1', '18F0916A8C2C4000'),
('13AB3A4248008002', '10010010201', '国外考察', '0', '13AB3A4138008001'),
('13AB3A4154008001', '10010010202', '售后维护出差', '0', '13AB3A4138008001'),
('13AB3A4172008001', '1001002', '人力资源', '1', 'none'),
('13AB3A418F808001', '100100201', '个人团队培训', '0', '13AB3A4172008001'),
('13AB3A41AC408001', '100100202', '招聘会', '0', '13AB3A4172008001'),
('13AB3A41CD808002', '1001003', '员工福利', '1', 'none'),
('13AB3A41ED408002', '100100301', '员工旅游', '0', '13AB3A41CD808002'),
('13AB3A420CC08002', '100100302', '员工团建', '0', '13AB3A41CD808002'),
('13AB3A422A808001', '100100303', '员工体检', '0', '13AB3A41CD808002');

-- 2. 业务单据交易数据表
-- 2.1 报销单主表
CREATE TABLE `fk_reim_main` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `reim_no` varchar(32) NOT NULL COMMENT '报销单号(日期+序号)',
  `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `reimbursement_title` varchar(500) DEFAULT NULL COMMENT '报销标题',
  `reimburser_id` varchar(32) DEFAULT NULL COMMENT '报销人ID',
  `reimburser_no` varchar(32) DEFAULT NULL COMMENT '报销人工号',
  `reimburser_name` varchar(50) DEFAULT NULL COMMENT '报销人姓名',
  `reim_department_id` varchar(32) DEFAULT NULL COMMENT '报销部门ID',
  `reim_department_no` varchar(32) DEFAULT NULL COMMENT '报销部门编号',
  `reim_department_name` varchar(50) DEFAULT NULL COMMENT '报销部门名称',
  `reim_company_id` varchar(32) DEFAULT NULL COMMENT '费用归属公司ID',
  `reim_company_no` varchar(32) DEFAULT NULL COMMENT '费用归属公司编号',
  `reim_company_name` varchar(50) DEFAULT NULL COMMENT '费用归属公司名称',
  `business_type_id` varchar(32) DEFAULT NULL COMMENT '业务类型ID',
  `business_type_no` varchar(50) DEFAULT NULL COMMENT '业务类型编号',
  `business_type_name` varchar(100) DEFAULT NULL COMMENT '业务类型名称',
  `business_trip_reason` varchar(500) DEFAULT NULL COMMENT '出差事由',
  `subsidy_total` decimal(12,2) DEFAULT '0.00' COMMENT '补助总金额',
  `meal_allowance` decimal(12,2) DEFAULT '0.00' COMMENT '餐费补助',
  `transportation_allowance` decimal(12,2) DEFAULT '0.00' COMMENT '交通补助',
  `phone_allowance` decimal(12,2) DEFAULT '0.00' COMMENT '通讯补助',
  `remarks` varchar(1000) DEFAULT NULL COMMENT '备注信息',
  `status` varchar(2) NOT NULL DEFAULT '0' COMMENT '单据状态 (0:草稿, 1:已完成, 2:已作废)',
  `create_user_id` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(50) DEFAULT NULL COMMENT '创建人姓名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reim_no` (`reim_no`),
  CONSTRAINT `fk_reim_user` FOREIGN KEY (`reimburser_id`) REFERENCES `base_employee` (`reimburser_id`),
  CONSTRAINT `fk_reim_dept` FOREIGN KEY (`reim_department_id`) REFERENCES `base_department` (`reim_department_id`),
  CONSTRAINT `fk_reim_comp` FOREIGN KEY (`reim_company_id`) REFERENCES `base_company` (`reim_company_id`),
  CONSTRAINT `fk_reim_btype` FOREIGN KEY (`business_type_id`) REFERENCES `base_business_type` (`business_type_id`)
) ENGINE=InnoDB;

-- 2.2 补录行程表
CREATE TABLE `fk_reim_trip` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `reim_id` varchar(32) NOT NULL COMMENT '报销单ID',
  `person_id` varchar(32) NOT NULL COMMENT '出行人ID',
  `person_no` varchar(32) DEFAULT NULL COMMENT '出行人工号',
  `person_name` varchar(50) DEFAULT NULL COMMENT '出行人姓名',
  `departure_city_no` varchar(32) NOT NULL COMMENT '出发城市编号',
  `departure_city_name` varchar(50) DEFAULT NULL COMMENT '出发城市名称',
  `arrival_city_no` varchar(32) NOT NULL COMMENT '到达城市编号',
  `arrival_city_name` varchar(50) DEFAULT NULL COMMENT '到达城市名称',
  `start_date` date NOT NULL COMMENT '出发日期',
  `end_date` date NOT NULL COMMENT '到达日期',
  `trip_desc` varchar(500) DEFAULT NULL COMMENT '行程说明',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序号',
  `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `fk_trip_reim` (`reim_id`),
  KEY `fk_trip_person` (`person_id`),
  KEY `fk_trip_dcity` (`departure_city_no`),
  KEY `fk_trip_acity` (`arrival_city_no`),
  CONSTRAINT `fk_trip_acity` FOREIGN KEY (`arrival_city_no`) REFERENCES `base_city` (`city_no`),
  CONSTRAINT `fk_trip_dcity` FOREIGN KEY (`departure_city_no`) REFERENCES `base_city` (`city_no`),
  CONSTRAINT `fk_trip_reim` FOREIGN KEY (`reim_id`) REFERENCES `fk_reim_main` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_trip_person` FOREIGN KEY (`person_id`) REFERENCES `base_employee` (`reimburser_id`)
) ENGINE=InnoDB;

-- 2.3 补助信息表
CREATE TABLE `fk_reim_subsidy` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `reim_id` varchar(32) NOT NULL COMMENT '报销单ID',
  `trip_id` varchar(32) NOT NULL COMMENT '补录行程ID',
  `person_id` varchar(32) DEFAULT NULL COMMENT '出行人ID',
  `person_name` varchar(50) DEFAULT NULL COMMENT '出行人姓名',
  `start_date` date DEFAULT NULL COMMENT '出差开始日期',
  `end_date` date DEFAULT NULL COMMENT '出差结束日期',
  `days` int(11) DEFAULT '0' COMMENT '补助天数',
  `trip_desc` varchar(200) DEFAULT NULL COMMENT '行程描述',
  `subsidy_city` varchar(50) DEFAULT NULL COMMENT '补助城市',
  `city_type` varchar(2) DEFAULT NULL COMMENT '城市类型',
  `apply_amount` decimal(12,2) DEFAULT '0.00' COMMENT '申请金额',
  `subsidy_amount` decimal(12,2) DEFAULT '0.00' COMMENT '补助金额',
  `meal_amount` decimal(12,2) DEFAULT '0.00' COMMENT '餐费补助金额',
  `transport_amount` decimal(12,2) DEFAULT '0.00' COMMENT '交通补助金额',
  `phone_amount` decimal(12,2) DEFAULT '0.00' COMMENT '通讯补助金额',
  `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_subsidy_trip` (`trip_id`),
  KEY `fk_sub_reim` (`reim_id`),
  KEY `fk_sub_person` (`person_id`),
  CONSTRAINT `fk_sub_reim` FOREIGN KEY (`reim_id`) REFERENCES `fk_reim_main` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_sub_trip` FOREIGN KEY (`trip_id`) REFERENCES `fk_reim_trip` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_sub_person` FOREIGN KEY (`person_id`) REFERENCES `base_employee` (`reimburser_id`)
) ENGINE=InnoDB;

-- 2.4 补助日历表
CREATE TABLE `fk_reim_subsidy_calendar` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `reim_id` varchar(32) NOT NULL COMMENT '报销单ID',
  `subsidy_id` varchar(32) NOT NULL COMMENT '补助信息ID',
  `trip_id` varchar(32) DEFAULT NULL COMMENT '行程ID',
  `calendar_date` date NOT NULL COMMENT '日历日期',
  `week_day` varchar(10) DEFAULT NULL COMMENT '星期',
  `city_name` varchar(50) DEFAULT NULL COMMENT '补助城市',
  `meal_checked` varchar(1) DEFAULT '0' COMMENT '餐费选中 (0:未选, 1:选中)',
  `meal_standard` decimal(12,2) DEFAULT '0.00' COMMENT '餐费标准金额',
  `meal_amount` decimal(12,2) DEFAULT '0.00' COMMENT '餐费补助金额',
  `transport_checked` varchar(1) DEFAULT '0' COMMENT '交通选中 (0:未选, 1:选中)',
  `transport_standard` decimal(12,2) DEFAULT '40.00' COMMENT '交通标准金额',
  `transport_amount` decimal(12,2) DEFAULT '0.00' COMMENT '交通补助金额',
  `phone_checked` varchar(1) DEFAULT '0' COMMENT '通讯选中 (0:未选, 1:选中)',
  `phone_standard` decimal(12,2) DEFAULT '40.00' COMMENT '通讯标准金额',
  `phone_amount` decimal(12,2) DEFAULT '0.00' COMMENT '通讯补助金额',
  `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `fk_cal_reim` (`reim_id`),
  KEY `fk_cal_sub` (`subsidy_id`),
  KEY `fk_cal_trip` (`trip_id`),
  CONSTRAINT `fk_cal_reim` FOREIGN KEY (`reim_id`) REFERENCES `fk_reim_main` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cal_sub` FOREIGN KEY (`subsidy_id`) REFERENCES `fk_reim_subsidy` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cal_trip` FOREIGN KEY (`trip_id`) REFERENCES `fk_reim_trip` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 2.5 费用分摊表
CREATE TABLE `fk_reim_cost_allocation` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `reim_id` varchar(32) NOT NULL COMMENT '报销单ID',
  `company_id` varchar(32) DEFAULT NULL COMMENT '费用归属公司ID',
  `company_no` varchar(32) DEFAULT NULL COMMENT '公司编号',
  `company_name` varchar(50) DEFAULT NULL COMMENT '公司名称',
  `project_id` varchar(32) DEFAULT NULL COMMENT '项目ID',
  `project_no` varchar(32) DEFAULT NULL COMMENT '项目编号',
  `project_name` varchar(100) DEFAULT NULL COMMENT '项目名称',
  `ratio` decimal(6,4) DEFAULT '0.0000' COMMENT '分摊比例',
  `amount` decimal(12,2) DEFAULT '0.00' COMMENT '分摊金额',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序号',
  `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `fk_alloc_reim` (`reim_id`),
  CONSTRAINT `fk_alloc_reim` FOREIGN KEY (`reim_id`) REFERENCES `fk_reim_main` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
```
