<div align="center">

# XMCompany

### 基于 Spring Boot + Vue 3 的企业综合管理系统

 Spring Boot 3 · Vue 3 · MyBatis-Plus · Redis · MongoDB · LangChain4j · Element Plus

[功能模块](#-核心功能模块) · [技术栈](#-技术栈) · [项目架构](#-项目架构) · [快速开始](#-快速开始) · [API文档](#-api文档) · [安全说明](#-安全说明)

</div>

---

## 项目简介

XMCompany 是一个采用 **前后端分离架构** 的企业综合管理系统，涵盖库存、采购、销售、生产、客户、员工、薪资、请假等核心业务模块，并集成基于大语言模型的 **AI 智能助手** 功能（RAG 知识库问答 + Function Calling）。

后端采用 **Spring Boot 3.5 多模块架构**，按业务域拆分模块；前端使用 **Vue 3 + Vite + Element Plus** 构建，提供现代化的交互体验。

---

## 核心功能模块

| 模块 | 核心能力 |
|:---:|------|
| **登录认证** | JWT 无状态认证 · Token 拦截校验 · ThreadLocal 用户上下文 · 修改/重置密码 |
| **员工管理** | 员工 CRUD · 分页查询 · 角色权限（ADMIN / USER）· 账号启用/禁用 |
| **薪资管理** | 薪资配置（基本工资 + 绩效 + 补贴 + 五险一金）· 月度工资单自动生成（定时任务）· 个税计算 · 批量/单笔发放 · 银行卡号脱敏 |
| **请假管理** | 7 种假类型（事假/病假/年假/调休/婚假/产假/丧假）· 审批流程（待审核→已批准/已拒绝/已取消） |
| **库存管理** | 物料库存查询 · 入库/出库操作记录 · 最后操作时间追踪 |
| **采购管理** | 供应商管理（联系人/银行账户/合作状态）· 采购申请审批流程 · 采购订单管理 |
| **销售管理** | 销售订单全生命周期（待处理→已确认→已发货→已完成/已取消）· 交货/发货时间管理 |
| **生产管理** | 生产项目创建与跟踪 · 生产进度记录（操作人/数量变更）· 项目状态管理 |
| **客户管理** | 客户信息 CRUD · 客户详情查看 |
| **AI 智能助手** | LangChain4j 集成 · 流式响应（SSE）· RAG 知识库问答（Pinecone）· Function Calling · MongoDB 聊天记忆 |
| **WebSocket 聊天** | Spring WebSocket 实时通信 · JWT 握手认证 · MongoDB 消息持久化 |

---

## 技术栈

### 后端

| 分类 | 技术 | 版本 |
|:---:|------|:---:|
| **核心框架** | Spring Boot | 3.5.3 |
| **Java 版本** | JDK | 17 |
| **ORM** | MyBatis-Plus | 3.5.12 |
| **数据库** | MySQL | 8.0.33 |
| **连接池** | Druid | 1.2.18 |
| **缓存** | Spring Data Redis + Redisson | 3.13.6 |
| **文档数据库** | Spring Data MongoDB | - |
| **认证** | JWT (jjwt) | 0.9.1 |
| **实时通信** | Spring WebSocket | - |
| **AI 框架** | LangChain4j | - |
| **LLM 模型** | 通义千问（DashScope）/ DeepSeek | - |
| **向量数据库** | Pinecone | - |
| **API 文档** | Knife4j + SpringDoc OpenAPI | 4.5.0 / 2.8.9 |
| **JSON** | Fastjson | 1.2.83 |
| **构建工具** | Maven | - |

### 前端

| 分类 | 技术 | 版本 |
|:---:|------|:---:|
| **框架** | Vue 3 (Composition API) | ^3.4.0 |
| **构建工具** | Vite | ^4.5.0 |
| **UI 组件库** | Element Plus | ^2.6.0 |
| **状态管理** | Pinia | ^2.1.0 |
| **路由** | Vue Router | ^4.2.0 |
| **HTTP** | Axios | ^1.6.0 |
| **日期处理** | Day.js | ^1.11.10 |

---

## 项目架构

```
XMCompany/
│
├── ai/                      # AI 智能助手模块
│   └── src/main/java/com/xm/
│       ├── assistant/           # AI 服务接口（@AiService）
│       ├── config/              # 模型配置 & 向量存储配置 & RAG 内容检索器
│       ├── controller/          # AI 对话接口（SSE 流式）
│       ├── entity/              # 对话消息实体
│       ├── service/             # 对话业务逻辑
│       ├── store/               # MongoDB 聊天记忆存储
│       └── tools/               # AI Function Calling 工具（查询客户/员工）
│
├── chatMessage/             # WebSocket 实时聊天模块
│   └── src/main/java/com/xm/
│       ├── config/              # WebSocket 配置
│       ├── controller/          # 消息控制器
│       ├── dto/                 # 消息传输对象
│       ├── entity/              # 消息实体
│       ├── Interceptor/         # JWT 握手拦截器
│       └── mongoRepository/     # MongoDB 消息仓储
│
├── common/                  # 公共模块
│   └── src/main/java/com/xm/
│       ├── config/              # CORS · Redisson · MyBatis-Plus · WebConfig
│       ├── exception/           # 全局异常处理 · PasswordChangeException
│       ├── handler/             # MyBatis List<String> 类型处理器
│       ├── Interceptor/         # 登录校验拦截器
│       ├── page/                # 统一分页封装
│       ├── result/              # 统一响应结果 Result<T>
│       ├── utils/               # JwtUtils · RedisIdGenerator · UserContext
│       └── websocket/           # WebSocket 基础配置
│
├── Login/                   # 登录认证模块
│   └── src/main/java/com/xm/
│       ├── controller/          # 登录接口
│       ├── entity/              # 登录实体
│       ├── mapper/              # 数据访问层
│       ├── service/impl/        # 登录业务逻辑
│       └── vo/                  # 视图对象
│
├── employee/                # 员工管理模块（含请假、薪资）
│   └── src/main/java/com/xm/
│       ├── controller/          # EmployeeController · LeaveController · SalaryController
│       ├── dto/                 # EmployeeQueryDTO · LeaveApplyDTO · SalaryDTO · PayrollGenerationDTO 等
│       ├── entity/              # Employee · Leave · Salary · Payroll
│       ├── mapper/              # MyBatis-Plus Mapper
│       ├── schedule/            # PayrollSchedule（定时任务自动生成工资单）
│       ├── service/impl/        # 业务实现
│       └── vo/                  # EmployeeVO · LeaveVO · SalaryVO · PayrollVO
│
├── customer/                # 客户管理模块
├── purchase/                # 采购管理模块（供应商 + 采购申请 + 采购订单）
├── stock/                   # 库存管理模块（库存 + 出入库操作记录）
├── produce/                 # 生产管理模块（生产项目 + 生产进度）
├── sale/                    # 销售管理模块（销售订单）
├── web/                     # Web 启动模块
│   └── src/main/resources/
│       └── application.yml      # 主配置文件
│
├── XMCompany_vue/           # 前端项目
│   └── src/
│       ├── api/                 # 接口请求封装（axios）
│       ├── assets/styles/      # 全局样式
│       ├── layouts/             # 布局组件（DefaultLayout · EmptyLayout）
│       ├── pages/               # 业务页面
│       │   ├── AI/                 # AI 聊天
│       │   ├── Customer/           # 客户管理
│       │   ├── Employee/          # 员工管理
│       │   ├── Leave/             # 请假管理
│       │   ├── Login/             # 登录页面
│       │   ├── Product/           # 产品管理
│       │   ├── Production/         # 生产管理
│       │   ├── Profile/           # 个人信息
│       │   ├── Purchase/          # 采购管理
│       │   ├── Salary/            # 薪资管理
│       │   ├── Sale/              # 销售管理
│       │   ├── Stock/             # 库存管理
│       │   └── Supplier/          # 供应商管理
│       ├── router/              # 路由配置 + 路由守卫
│       └── store/               # Pinia 状态管理
│
├── pom.xml                 # Maven 父 POM
└── .gitignore              # Git 忽略配置
```

---

## 快速开始

### 环境要求

| 软件 | 版本 |
|:---:|:---:|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| MongoDB | 7.0+ |

### 1. 克隆项目

```bash
git clone https://github.com/your-username/XMCompany.git
cd XMCompany
```

### 2. 创建数据库

```sql
CREATE DATABASE xmcompany DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

各模块的建表 SQL 脚本位于对应模块的 `src/main/resources/db/` 或 `src/main/resources/sql/` 目录下。

### 3. 配置环境变量

项目所有敏感信息（数据库密码、Redis 密码、JWT 密钥、AI API Key 等）均通过 **环境变量** 注入，不硬编码在代码中：

```bash
# ===== 数据库 =====
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password

# ===== Redis =====
export REDIS_PASSWORD=your_redis_password

# ===== JWT 签名密钥 =====
export JWT_SIGN_KEY=your_jwt_sign_key

# ===== AI 模型 API Key（如需启用 AI 功能）=====
export DASH_SCOPE_API_KEY=your_dashscope_api_key
export DEEP_SEEK_API_KEY=your_deepseek_api_key
export PINECONE_API_KEY=your_pinecone_api_key
```

### 4. 修改配置文件

编辑 `web/src/main/resources/application.yml`，根据实际环境调整数据库地址、Redis 地址、MongoDB 地址等连接信息。

### 5. 启动后端

```bash
# 编译全部模块
mvn clean install -DskipTests

# 启动 Spring Boot 应用（在 web 模块下运行主启动类）
cd web
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`

### 6. 启动前端

```bash
cd XMCompany_vue

# 安装依赖
npm install

# 开发模式启动
npm run dev
```

前端默认运行在 `http://localhost:5173`

前端接口地址可通过项目根目录 `.env` 文件或环境变量配置：

```
VITE_API_BASE_URL=http://localhost:8080
```

---

## API 文档

项目集成了 **Knife4j + SpringDoc OpenAPI** 接口文档，启动后端服务后访问：

```
http://localhost:8080/doc.html
```

### 接口路由总览

| 模块 | 路径前缀 | 说明 |
|:---:|:---:|------|
| 登录认证 | `/xm/login` | 用户登录，返回 JWT 令牌 |
| 员工管理 | `/xm/employee` | 员工 CRUD、状态管理、重置/修改密码 |
| 请假管理 | `/xm/leave` | 请假申请、审批、查询 |
| 薪资管理 | `/xm/salary` | 薪资配置、工资单生成/调整/发放 |
| 客户管理 | `/xm/customer` | 客户 CRUD、分页查询 |
| 供应商管理 | `/xm/supplier` | 供应商 CRUD、状态管理 |
| 采购申请 | `/xm/purchase-request` | 采购申请提交、审批、查询 |
| 采购订单 | `/xm/purchase-order` | 采购订单管理 |
| 库存管理 | `/xm/stock` | 库存查询、物料管理 |
| 库存操作 | `/xm/stock-operation` | 入库/出库操作记录 |
| 生产管理 | `/xm/production` | 生产项目、生产进度 |
| 销售管理 | `/xm/sale` | 销售订单全生命周期 |
| AI 对话 | `/xm/ai` | AI 智能对话（SSE 流式响应） |

---

## 项目亮点

### 1. Redis 分布式 ID 生成

基于 **Redisson 分布式锁** 实现 ID 生成器，保证高并发场景下各业务实体 ID 的全局唯一性。系统启动时通过 `CommandLineRunner` 自动初始化各业务的 ID 起始值。

### 2. AI 智能助手（RAG + Function Calling）

- **LangChain4j** 集成大语言模型，支持 **通义千问**（DashScope）和 **DeepSeek** 双模型
- **RAG 检索增强**：通过 **Pinecone** 向量数据库实现企业知识库语义检索，AI 回答基于业务数据
- **Function Calling**：AI 可自主调用业务工具函数（查询客户信息、查询员工列表），实现智能化数据查询
- **流式响应**：基于 SSE（Server-Sent Events）实现打字机效果的实时对话
- **MongoDB 聊天记忆**：多用户、多会话的独立对话记忆持久化存储

### 3. 薪资管理自动化

- **定时任务** 自动生成月度工资单
- 个人所得税自动计算
- 批量发放 & 单笔发放
- 银行卡号脱敏展示

### 4. 统一架构设计

- **统一响应格式**：`Result<T>`（code + msg + data）
- **统一分页封装**：`page<T>`（pageSize + total + list）
- **全局异常处理**：`GlobalExceptionHandler` 统一捕获和处理异常
- **DTO / VO 分离**：DTO 接收请求参数，VO 返回视图数据，Entity 映射数据库表
- **MyBatis-Plus TypeHandler**：自定义 `ListStringTypeHandler` 处理 JSON 数组类型字段

### 5. RBAC 权限控制

- 后端：基于 JWT 中的 role 字段进行接口访问控制
- 前端：路由守卫 + 菜单动态渲染，ADMIN 角色可访问员工管理、薪资管理等特权页面

---

## 安全说明

本项目已对以下敏感信息进行脱敏处理，可安全上传至 GitHub：

| 敏感类型 | 处理方式 |
|:---:|------|
| 数据库密码 | 通过 `${DB_PASSWORD}` 环境变量注入 |
| Redis 密码 | 通过 `${REDIS_PASSWORD}` 环境变量注入 |
| Druid 控制台密码 | 通过 `${DRUID_PASSWORD}` 环境变量注入 |
| JWT 签名密钥 | 通过 `System.getenv("JWT_SIGN_KEY")` 环境变量读取 |
| AI 模型 API Key | 通过 `${DASH_SCOPE_API_KEY}` / `${DEEP_SEEK_API_KEY}` 环境变量注入 |
| Pinecone API Key | 通过 `System.getenv("PINECONE_API_KEY")` 环境变量读取 |
| SQL 测试数据（手机号/邮箱/银行账号） | 脱敏处理（如 `138****8001`、`6228********6789`） |
| IDE 数据源配置 | 用户名替换为占位符 |
| `.gitignore` | 已配置排除 `target/`、`node_modules/`、`.idea/`、`.env` 等 |

---

## 开发规范

| 规范 | 说明 |
|:---:|------|
| **响应格式** | 统一使用 `Result<T>` 封装（code + msg + data） |
| **分页格式** | 统一使用 `page<T>` 封装（pageSize + total + list） |
| **命名约定** | DTO 接收请求参数，VO 返回视图数据，Entity 映射数据库表 |
| **ID 生成** | 全部业务 ID 由 Redis 分布式生成，不使用数据库自增 |
| **拦截器** | 自定义 `LoginCheckInterceptor` 统一校验 JWT Token |
| **用户上下文** | 基于 `ThreadLocal` 的 `UserContext` 在请求链路中传递用户信息 |
| **前端请求** | Axios 拦截器统一携带 Token & 处理 401 过期跳转 |

---

## License

本项目仅用于学习和个人作品展示，禁止用于商业用途。
