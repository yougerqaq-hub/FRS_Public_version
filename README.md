# 💰 FBS - 企业财务与预算管理系统 (Financial & Budget System)

<p align="center">
  <b>基于 Spring Boot + Vue 2 + Element UI 的企业级财务报销与预算预警管理平台</b>
</p>

---

## 📖 项目简介

**FBS (Financial & Budget System)** 是一款专为企业设计的中小型财务与预算管理系统。系统打通了**员工报销申请**、**财务/管理员审核**、**年度预算扣减**与**动态预算预警**的全链路业务流程。通过精细化的角色权限控制（RBAC）与轻量化的 Session-Token 校验机制，保障财务数据的安全性与业务的高效运转。

### 🌟 核心亮点

* **全链路业务闭环**：报销申请审核通过后，系统自动校验并扣減对应年度与类别的预算，同时触发实时预警计算。
* **智能预警机制**：根据预算使用率（`<70%` 正常、`<90%` 提醒、`<100%` 警告、`≥100%` 超出）自动更新预警等级，防范财务超支风险。
* **双重权限防护**：前端通过 Vue Router 导航守卫控制页面访问，后端通过 Header `X-Session-Token` 进行角色级鉴权（`ADMIN` / `FINANCE` / `EMPLOYEE`）。
* **轻量高效**：基于 Element UI 构建响应式后台界面，后端采用 MyBatis + MySQL 实现高扩展性的持久层架构。

---

## 🛠️ 技术栈

### 后端 (Backend)
* **核心框架**：Spring Boot 3.x / Java 17+
* **持久层**：MyBatis 3.x
* **数据库**：MySQL 8.0+
* **安全/会话**：轻量级内存 Session-Token 服务 (`SessionService`)
* **事务管理**：Spring `@Transactional` 编程式/声明式事务

### 前端 (Frontend)
* **核心框架**：Vue 2.x + Vue Router
* **UI 组件库**：Element UI
* **网络请求**：Axios (支持 Request/Response 拦截器与 Token 自动注入)

---

## 👥 角色与权限控制

| 功能模块 | 系统管理员 (`ADMIN`) | 财务人员 (`FINANCE`) | 普通员工 (`EMPLOYEE`) |
| :--- | :---: | :---: | :---: |
| **仪表盘 Dashboard** | 充当首页概览 | 充当首页概览 | 充当首页概览 |
| **报销管理 Reimbursement** | 查看全部 / 申请 | 查看全部 / 申请 | 仅查看个人 / 申请 |
| **报销审核 Audit** | ✅ 审核与查看记录 | ✅ 审核与查看记录 | ❌ 无权限 |
| **预算管理 Budget** | ✅ 新增/调整/查看 | 👁️ 仅查看 | ❌ 无权限 |
| **预算预警 Warning** | ✅ 查看与手动计算 | ✅ 查看与手动计算 | ❌ 无权限 |
| **用户管理 User** | ✅ 增删改查与角色分配 | ❌ 无权限 | ❌ 无权限 |

---

## 🔄 核心业务流程图

```
[员工] 提交报销申请 (PENDING)
         │
         ▼
[财务/管理员] 审批报销 (APPROVED / REJECTED)
         │
         ├──► 【拒绝】 ──► 报销状态变更，流程结束
         │
         └──► 【批准】
                │
                ├─► 1. 查找对应年份与类别的预算 (Budget)
                ├─► 2. 扣减/累加已使用金额 (used_amount)
                ├─► 3. 重新计算预算使用率 (usage_rate)
                └─► 4. 动态更新/生成预警记录 (BudgetWarning)
```

---

## 📁 项目结构说明

```
FBS/
├── fbs-backend/                      # 后端 Spring Boot 工程
│   └── src/main/java/com/example/FBS/
│       ├── common/                   # 通用响应体 (Result, ResultCode)
│       ├── controller/               # RESTful API 控制层
│       ├── entity/                   # 数据实体类 (User, Budget, Reimbursement...)
│       ├── mapper/                   # MyBatis Mapper 接口与 SQL 映射
│       └── service/                  # 业务逻辑接口与实现类 (impl)
│
└── fbs-frontend/                     # 前端 Vue 工程
    └── src/
        ├── api.js                    # Axios 全局封装与 API 统一管理
        ├── router/index.js           # Vue Router 路由与前端守卫
        └── views/                    # 页面组件
            ├── Home.vue              # 主页布局基座
            ├── Dashboard.vue         # 仪表盘
            ├── Reimbursement.vue     # 报销管理
            ├── Audit.vue             # 报销审核
            ├── Budget.vue            # 预算管理
            ├── Warning.vue           # 预算预警
            └── User.vue              # 用户管理
```

---

## 🚀 快速启动指南

### 1. 数据库初始化

创建 MySQL 数据库（如 `fbs_db`），并执行建表与初始数据脚本：

```sql
-- 数据库表设计预览
-- sys_user: 用户表 (id, username, password, real_name, role, create_time)
-- budget: 预算表 (id, year, category, budget_amount, used_amount, create_time)
-- reimbursement: 报销申请表 (id, user_id, amount, category, description, status, apply_time)
-- reimbursement_audit: 报销审核表 (id, reimbursement_id, auditor_id, result, comment, audit_time)
-- budget_warning: 预算预警表 (id, budget_id, warning_level, usage_rate, warning_time)
```

### 2. 后端配置与运行

1. 修改 `src/main/resources/application.yml` 中的 MySQL 连接信息。
2. 使用 IDE（IntelliJ IDEA）或 Maven 启动服务：

```bash
cd fbs-backend
mvn spring-boot:run
```
后端服务默认运行在 `http://localhost:8080`。

### 3. 前端配置与运行

1. 进入前端目录，安装依赖：

```bash
cd fbs-frontend
npm install
```

2. 启动开发服务器：

```bash
npm run serve
```
前端服务默认运行在 `http://localhost:8081`。

---

## 📡 API 接口预览

| 模块 | 接口 HTTP 请求 | 接口路径 | 说明 |
| :--- | :--- | :--- | :--- |
| **用户** | `POST` | `/user/login` | 用户登录，返回 Session Token |
| **用户** | `GET` | `/user/list` | 获取所有用户列表（仅管理员） |
| **报销** | `POST` | `/reimbursement` | 提交新的报销申请 |
| **报销** | `GET` | `/reimbursement/all` | 获取全部报销列表（财务/管理员） |
| **审核** | `POST` | `/reimbursement-audit` | 审核报销单（支持联动预算扣减与预警） |
| **预算** | `POST` | `/budget/{id}/adjust` | 调整现有预算额度 |
| **预警** | `GET` | `/budget-warning` | 查询所有预算预警状态 |

---

## 📄 许可证

本项目基于 [MIT License](LICENSE) 开源许可。
