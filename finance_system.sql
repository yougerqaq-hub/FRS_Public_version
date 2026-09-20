/*
 Navicat Premium Dump SQL

 Source Server         : finance-reimbursement-system
 Source Server Type    : MySQL
 Source Server Version : 80411 (8.4.11)
 Source Host           : localhost:3306
 Source Schema         : finance_system

 Target Server Type    : MySQL
 Target Server Version : 80411 (8.4.11)
 File Encoding         : 65001

 Date: 20/09/2026 11:29:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for budget
-- ----------------------------
DROP TABLE IF EXISTS `budget`;
CREATE TABLE `budget`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预算ID',
  `year` int NOT NULL COMMENT '预算年份',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预算类型',
  `budget_amount` decimal(12, 2) NOT NULL COMMENT '预算金额',
  `used_amount` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '已使用金额',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预算管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of budget
-- ----------------------------
INSERT INTO `budget` VALUES (1, 2026, '办公费', 50000.00, 17503.00, '2026-01-01 09:00:00');
INSERT INTO `budget` VALUES (2, 2026, '交通费', 40000.00, 30000.00, '2026-01-01 09:00:00');
INSERT INTO `budget` VALUES (3, 2026, '差旅费', 50000.00, 58320.00, '2026-01-01 09:00:00');
INSERT INTO `budget` VALUES (4, 2026, '培训费', 10020000.00, 22000.00, '2026-01-01 09:00:00');
INSERT INTO `budget` VALUES (5, 2026, '设备维护费', 30000.00, 24000.00, '2026-01-01 09:00:00');
INSERT INTO `budget` VALUES (6, 2026, '餐费', 60000.00, 10000.00, '2026-09-09 16:14:20');
INSERT INTO `budget` VALUES (7, 2026, '其他费用', 70000.00, 0.00, '2026-09-09 18:04:09');

-- ----------------------------
-- Table structure for budget_warning
-- ----------------------------
DROP TABLE IF EXISTS `budget_warning`;
CREATE TABLE `budget_warning`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `budget_id` bigint NOT NULL COMMENT '预算ID',
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警级别',
  `usage_rate` decimal(5, 2) NOT NULL COMMENT '预算使用率',
  `warning_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预警时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_budget_warning_budget_id`(`budget_id` ASC) USING BTREE,
  CONSTRAINT `fk_warning_budget` FOREIGN KEY (`budget_id`) REFERENCES `budget` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预算预警表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of budget_warning
-- ----------------------------
INSERT INTO `budget_warning` VALUES (1, 1, 'NORMAL', 35.01, '2026-09-20 11:29:07');
INSERT INTO `budget_warning` VALUES (2, 2, 'NOTICE', 75.00, '2026-09-20 11:29:07');
INSERT INTO `budget_warning` VALUES (3, 3, 'OVER', 116.64, '2026-09-20 11:29:07');
INSERT INTO `budget_warning` VALUES (4, 4, 'NORMAL', 0.22, '2026-09-20 11:29:07');
INSERT INTO `budget_warning` VALUES (5, 5, 'NOTICE', 80.00, '2026-09-20 11:29:07');
INSERT INTO `budget_warning` VALUES (6, 6, 'NORMAL', 16.67, '2026-09-20 11:29:07');
INSERT INTO `budget_warning` VALUES (7, 7, 'NORMAL', 0.00, '2026-09-20 11:29:07');

-- ----------------------------
-- Table structure for reimbursement
-- ----------------------------
DROP TABLE IF EXISTS `reimbursement`;
CREATE TABLE `reimbursement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报销ID',
  `user_id` bigint NOT NULL COMMENT '申请人ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '报销金额',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报销类型',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报销说明',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/APPROVED/REJECTED',
  `apply_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_reimbursement_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_reimbursement_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务报销申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reimbursement
-- ----------------------------
INSERT INTO `reimbursement` VALUES (1, 4, 680.00, '差旅费', '上海出差往返交通费', 'APPROVED', '2026-02-10 10:20:00');
INSERT INTO `reimbursement` VALUES (2, 4, 320.50, '办公费', '购买办公用品', 'APPROVED', '2026-02-15 14:30:00');
INSERT INTO `reimbursement` VALUES (3, 4, 1280.00, '差旅费', '北京出差住宿费', 'REJECTED', '2026-03-02 09:15:00');
INSERT INTO `reimbursement` VALUES (4, 5, 450.00, '交通费', '客户拜访交通费用', 'APPROVED', '2026-03-05 11:00:00');
INSERT INTO `reimbursement` VALUES (5, 5, 760.00, '差旅费', '广州出差交通及住宿费用', 'APPROVED', '2026-03-18 16:20:00');
INSERT INTO `reimbursement` VALUES (6, 6, 210.00, '办公费', '打印纸和文具采购', 'APPROVED', '2026-04-01 10:10:00');
INSERT INTO `reimbursement` VALUES (7, 6, 950.00, '培训费', '参加专业技术培训', 'PENDING', '2026-04-10 13:40:00');
INSERT INTO `reimbursement` VALUES (8, 6, 1500.00, '差旅费', '南京出差费用', 'REJECTED', '2026-04-20 09:50:00');
INSERT INTO `reimbursement` VALUES (9, 4, 880.00, '差旅费', '上海出差交通费用', 'PENDING', '2026-09-09 15:54:29');
INSERT INTO `reimbursement` VALUES (10, 4, 880.00, '差旅费', '上海出差交通费用', 'APPROVED', '2026-09-09 17:49:34');
INSERT INTO `reimbursement` VALUES (13, 4, 100.00, 'test', 'test', 'REJECTED', '2026-09-19 21:54:00');
INSERT INTO `reimbursement` VALUES (14, 1, 999.00, 'testest', 'test', 'REJECTED', '2026-09-20 10:35:35');
INSERT INTO `reimbursement` VALUES (15, 6, 10000.00, '差旅费', '希腊奶', 'APPROVED', '2026-09-20 10:37:34');
INSERT INTO `reimbursement` VALUES (16, 3, 1.00, '办公费', '用用', 'APPROVED', '2026-09-20 10:40:49');
INSERT INTO `reimbursement` VALUES (17, 1, 1.00, '办公费', '碰碰运气', 'APPROVED', '2026-09-20 10:41:40');
INSERT INTO `reimbursement` VALUES (18, 1, 1.00, '办公费', 'test', 'APPROVED', '2026-09-20 10:49:25');

-- ----------------------------
-- Table structure for reimbursement_audit
-- ----------------------------
DROP TABLE IF EXISTS `reimbursement_audit`;
CREATE TABLE `reimbursement_audit`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `reimbursement_id` bigint NOT NULL COMMENT '报销申请ID',
  `auditor_id` bigint NOT NULL COMMENT '审核人ID',
  `result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审核结果：APPROVED/REJECTED',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_audit_reimbursement`(`reimbursement_id` ASC) USING BTREE,
  INDEX `fk_audit_user`(`auditor_id` ASC) USING BTREE,
  CONSTRAINT `fk_audit_reimbursement` FOREIGN KEY (`reimbursement_id`) REFERENCES `reimbursement` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_audit_user` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '报销审核表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reimbursement_audit
-- ----------------------------
INSERT INTO `reimbursement_audit` VALUES (1, 2, 2, 'APPROVED', '办公用品采购符合公司报销规定', '2026-02-16 09:30:00');
INSERT INTO `reimbursement_audit` VALUES (2, 3, 2, 'REJECTED', '住宿费用超过公司标准，请重新提交', '2026-03-03 10:00:00');
INSERT INTO `reimbursement_audit` VALUES (3, 4, 3, 'APPROVED', '客户拜访交通费用符合规定', '2026-03-06 09:20:00');
INSERT INTO `reimbursement_audit` VALUES (4, 6, 2, 'APPROVED', '办公用品费用合理', '2026-04-02 09:10:00');
INSERT INTO `reimbursement_audit` VALUES (5, 8, 3, 'REJECTED', '该项费用缺少有效票据', '2026-04-21 11:30:00');
INSERT INTO `reimbursement_audit` VALUES (6, 1, 2, 'APPROVED', '报销材料完整，符合报销规定', '2026-09-09 16:07:49');
INSERT INTO `reimbursement_audit` VALUES (7, 1, 2, 'APPROVED', '报销材料完整，符合报销规定', '2026-09-09 16:54:15');
INSERT INTO `reimbursement_audit` VALUES (8, 5, 2, 'APPROVED', '报销材料完整，符合报销规定', '2026-09-09 17:56:51');
INSERT INTO `reimbursement_audit` VALUES (9, 13, 2, 'REJECTED', 'test', '2026-09-19 22:26:06');
INSERT INTO `reimbursement_audit` VALUES (10, 10, 2, 'APPROVED', 'test', '2026-09-19 22:44:29');
INSERT INTO `reimbursement_audit` VALUES (11, 14, 1, 'REJECTED', '?', '2026-09-20 10:36:49');
INSERT INTO `reimbursement_audit` VALUES (12, 15, 3, 'APPROVED', '11', '2026-09-20 10:39:06');
INSERT INTO `reimbursement_audit` VALUES (13, 16, 3, 'APPROVED', '1', '2026-09-20 10:40:54');
INSERT INTO `reimbursement_audit` VALUES (14, 17, 1, 'APPROVED', 'ok', '2026-09-20 10:41:47');
INSERT INTO `reimbursement_audit` VALUES (15, 18, 1, 'APPROVED', 'ok', '2026-09-20 10:49:33');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色：ADMIN/FINANCE/EMPLOYEE',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', '系统管理员', 'ADMIN', '2026-01-05 09:00:00');
INSERT INTO `sys_user` VALUES (2, 'finance01', '123456', '张财务', 'FINANCE', '2026-01-06 09:00:00');
INSERT INTO `sys_user` VALUES (3, 'finance02', '123456', '李财务', 'FINANCE', '2026-01-07 09:00:00');
INSERT INTO `sys_user` VALUES (4, 'zhangsan', '123456', '张三', 'EMPLOYEE', '2026-01-08 09:00:00');
INSERT INTO `sys_user` VALUES (5, 'lisi', '123456', '李四', 'EMPLOYEE', '2026-01-09 09:00:00');
INSERT INTO `sys_user` VALUES (6, 'wangwu', '123456', '王五', 'EMPLOYEE', '2026-01-10 09:00:00');
INSERT INTO `sys_user` VALUES (8, 'test', '123456', '测试人员', 'EMPLOYEE', '2026-09-20 11:18:34');

SET FOREIGN_KEY_CHECKS = 1;
