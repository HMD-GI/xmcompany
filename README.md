# XMCompany - 基于 Spring Boot 的企业管理系统

> 一个采用前后端分离架构的企业综合管理系统，涵盖库存、采购、销售、生产、客户、员工、薪资、请假等核心业务模块，并集成 AI 智能助手功能。

---

## 项目预览

| 登录页面 | 系统主页 |
|:---:|:---:|
| ![Login](https://via.placeholder.com/400x250/304156/FFFFFF?text=Login+Page) | ![Dashboard](https://via.placeholder.com/400x250/f5f7fa/303133?text=System+Dashboard) |

> 截图可按实际项目页面替换。

---

## 技术栈

### 后端

| 技术 | 说明 |
|------|------|
| **Spring Boot 3.5** | 核心框架，Java 17 |
| **Spring Boot Data Redis** | 缓存 & 分布式 ID 生成 |
| **Spring Boot Data MongoDB** | AI 对话记忆持久化 |
| **Redisson** | 分布式锁，保证 ID 生成并发安全 |
| **MyBatis-Plus** | ORM 框架，简化 CRUD 操作 |
| **Druid** | 数据库连接池 & 监控 |
| **MySQL 8.0** | 主数据库 |
| **JWT (jjwt)** | 身份认证令牌 |
| **Spring WebSocket** | 实时通信 |
| **LangChain4j** | 大语言模型集成框架 |
| **通义千问 (DashScope)** | Embedding 向量模型 + 流式对话模型 |
| **DeepSeek** | 备选 LLM 对话模型 |
| **Pinecone** | 向量数据库，RAG 检索增强 |
| **Knife4j / SpringDoc OpenAPI** | API 接口文档 |
| **Fastjson** | JSON 序列化 |

### 前端

| 技术 | 说明 |
|------|------|
| **Vue 3** | 渐进式前端框架（Composition API） |
| **Vite** | 下一代前端构建工具 |
| **Element Plus** | Vue 3 UI 组件库 |
| **Pinia** | 状态管理 |
| **Vue Router** | 前端路由 |
| **Axios** | HTTP 请求库 |
| **Day.js** | 日期处理 |

---

## 项目架构

```
XMCompany/
├── ai/              # AI 智能助手模块（LangChain4j + RAG）
│   └── src/main/java/com/xm/
│       ├── assistant/   # AI 服务接口定义
│       ├── config/      # 模型 & 向量存储配置
│       ├── controller/  # AI 对话接口
│       ├── entity/      # 对话消息实体
│       ├── service/     # 对话业务逻辑
│       ├── store/       # MongoDB 聊天记忆存储
│       └── tools/       # AI Function Calling 工具
│
├── chatMessage/     # WebSocket 实时聊天模块
│   └── src/main/java/com/xm/
│       ├── config/      # WebSocket 配置
│       ├── controller/  # 消息控制器
│       ├── dto/         # 消息传输对象
│       ├── entity/      # 消息实体
│       ├── Interceptor/ # JWT 握手拦截器
│       └── mongoRepository/ # MongoDB 消息仓储
│
├── common/          # 公共模块（拦截器、工具类、全局配置）
│   └── src/main/java/com/xm/
│       ├── config/      # CORS、Redisson、MyBatis-Plus 配置
│       ├── exception/   # 全局异常处理
│       ├── handler/     # MyBatis 类型处理器
│       ├── Interceptor/ # 登录校验拦截器
│       ├── page/        # 统一分页封装
│       ├── result/      # 统一响应结果封装
│       ├── utils/       # JWT、Redis ID 生成器、用户上下文
│       └── websocket/   # WebSocket 基础配置
│
├── Login/           # 登录认证模块
│   └── src/main/java/com/xm/
│       ├── controller/  # 登录接口
│       ├── entity/      # 登录实体
│       ├── mapper/      # 数据访问层
│       ├── service/     # 登录业务逻辑
│       └── vo/          # 视图对象
│
├── employee/        # 员工管理模块（含请假、薪资）
│   └── src/main/java/com/xm/
│       ├── controller/  # 员工、请假、薪资控制器
│       ├── dto/         # 数据传输对象
│       ├── entity/      # 员工、请假、薪资实体
│       ├── mapper/      # MyBatis-Plus Mapper
│       ├── schedule/    # 定时任务（薪资自动生成）
│       ├── service/     # 业务接口 & 实现
│       └── vo/          # 视图对象
│
├── customer/        # 客户管理模块
├── purchase/        # 采购管理模块（供应商、采购申请、采购订单）
├── stock/           # 库存管理模块（库存、出入库操作记录）
├── produce/         # 生产管理模块（生产项目、生产进度）
├── sale/            # 销售管理模块（销售订单）
├── web/             # Web 启动模块（主应用入口）
│   └── src/main/resources/
│       └── application.yml  # 主配置文件
│
├── XMCompany_vue/   # 前端项目（Vue 3 + Vite）
│   └── src/
│       ├── api/         # 接口请求封装
│       ├── assets/      # 静态资源
│       ├── layouts/     # 布局组件
│       ├── pages/       # 业务页面
│       │   ├── AI/          # AI 聊天
│       │   ├── Customer/    # 客户管理
│       │   ├── Employee/    # 员工管理
│       │   ├── Leave/       # 请假管理
│       │   ├── Login/       # 登录
│       │   ├── Product/     # 产品
│       │   ├── Production/   # 生产管理
│       │   ├── Profile/     # 个人信息
│       │   ├── Purchase/     # 采购管理
│       │   ├── Salary/      # 薪资管理
│       │   ├── Sale/        # 销售管理
│       │   ├── Stock/       # 库存管理
│       │   └── Supplier/    # 供应商管理
│       ├── router/      # 路由配置
│       └── store/       # Pinia 状态管理
│
├── pom.xml          # Maven 父 POM
└── .gitignore       # Git 忽略配置
```

---

## 核心功能模块

### 1. 登录认证
- 基于 JWT 的无状态认证
- 自定义拦截器统一校验 Token
- 基于 ThreadLocal 的用户上下文传递
- 修改密码 & 重置密码功能

### 2. 员工管理
- 员工 CRUD + 分页查询
- 角色权限控制（ADMIN / USER）
- 账号启用/禁用管理
- 员工个人信息维护

### 3. 薪资管理
- 薪资配置（基本工资、绩效基数、补贴、五险一金）
- 月度工资单自动生成（定时任务）
- 工资单调整（绩效、加班费、奖金、扣款）
- 个人所得税计算
- 批量发放 & 单笔发放
- 银行卡号脱敏展示

### 4. 请假管理
- 请假申请（7 种假类型：事假、病假、年假、调休、婚假、产假、丧假）
- 请假审批流程（待审核 → 已批准/已拒绝/已取消）

### 5. 库存管理
- 物料库存查询 & 管理
- 入库 / 出库操作记录
- 最后入库/出库时间追踪

### 6. 采购管理
- 供应商管理（联系人、银行账户、合作状态）
- 采购申请（申请 → 审批 → 下单 → 到货 → 完成）
- 采购订单管理

### 7. 销售管理
- 销售订单全生命周期管理
- 订单状态流转（待处理 → 已确认 → 已发货 → 已完成/已取消）

### 8. 生产管理
- 生产项目创建与跟踪
- 生产进度记录（操作人、数量变更）
- 项目状态管理（进行中、暂停、已完成、已取消）

### 9. 客户管理
- 客户信息 CRUD
- 客户详情查看

### 10. AI 智能助手
- 基于 **LangChain4j** 集成大语言模型
- 支持流式响应（SSE）
- **RAG 检索增强**：通过 Pinecone 向量数据库实现知识库问答
- **Function Calling**：AI 可调用业务工具函数（查询客户、查询员工）
- **MongoDB 持久化聊天记忆**：多会话独立对话
- 支持通义千问（DashScope）和 DeepSeek 双模型

### 11. WebSocket 实时聊天
- 基于 Spring WebSocket 的实时通信
- JWT 握手认证
- MongoDB 消息持久化

---

## 快速开始

### 环境要求

| 软件 | 版本要求 |
|------|---------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| MongoDB | 7.0+ |

### 后端启动

1. **克隆项目**
   ```bash
   git clone https://github.com/your-username/XMCompany.git
   cd XMCompany
   ```

2. **创建数据库**
   ```sql
   CREATE DATABASE xmcompany DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   ```

3. **配置环境变量**（API 密钥等敏感信息通过环境变量注入，不硬编码在代码中）
   ```bash
   # 数据库
   export DB_USERNAME=your_db_user
   export DB_PASSWORD=your_db_password

   # Redis
   export REDIS_PASSWORD=your_redis_password

   # JWT 签名密钥
   export JWT_SIGN_KEY=your_jwt_sign_key

   # AI 模型 API Key（如需启用 AI 功能）
   export DASH_SCOPE_API_KEY=your_dashscope_api_key
   export DEEP_SEEK_API_KEY=your_deepseek_api_key
   export PINECONE_API_KEY=your_pinecone_api_key
   ```

4. **修改 `application.yml`**（位于 `web/src/main/resources/`）
   - 调整数据库连接地址
   - 配置 Redis / MongoDB 连接信息

5. **编译 & 启动**
   ```bash
   # 编译
   mvn clean install -DskipTests

   # 启动（在 web 模块下运行主类）
   cd web
   mvn spring-boot:run
   ```
   后端默认运行在 `http://localhost:8080`

### 前端启动

```bash
cd XMCompany_vue

# 安装依赖
npm install

# 开发模式启动
npm run dev
```
前端默认运行在 `http://localhost:5173`

前端接口地址可通过 `.env` 文件或环境变量 `VITE_API_BASE_URL` 配置：
```
VITE_API_BASE_URL=http://localhost:8080
```

### 默认账号

系统启动后，请通过数据库初始化脚本创建管理员账号。首次登录后请及时修改密码。

---

## API 文档

项目集成了 Knife4j / SpringDoc OpenAPI 接口文档，启动后端服务后访问：

```
http://localhost:8080/doc.html
```

主要接口前缀：`/xm/`

| 模块 | 接口前缀 | 说明 |
|------|---------|------|
| 登录 | `/xm/login` | 登录认证 |
| 员工 | `/xm/employee` | 员工管理 |
| 请假 | `/xm/leave` | 请假管理 |
| 薪资 | `/xm/salary` | 薪资管理 |
| 客户 | `/xm/customer` | 客户管理 |
| 供应商 | `/xm/supplier` | 供应商管理 |
| 采购申请 | `/xm/purchase-request` | 采购申请 |
| 采购订单 | `/xm/purchase-order` | 采购订单 |
| 库存 | `/xm/stock` | 库存管理 |
| 库存操作 | `/xm/stock-operation` | 出入库操作 |
| 生产 | `/xm/production` | 生产管理 |
| 销售 | `/xm/sale` | 销售管理 |
| AI | `/xm/ai` | AI 智能对话 |

---

## 安全说明

本项目已对以下敏感信息进行脱敏处理：

- 数据库密码、Redis 密码、Druid 控制台密码 → 通过环境变量注入
- JWT 签名密钥 → 通过环境变量注入
- AI 模型 API Key（DashScope、DeepSeek、Pinecone）→ 通过环境变量注入
- SQL 测试数据中的手机号、邮箱、银行账号 → 脱敏处理
- `.gitignore` 已配置排除 `target/`、`node_modules/`、`.idea/`、`.env` 等

---

## 项目亮点

1. **多模块 Maven 架构**：按业务域拆分模块，职责清晰，易于维护
2. **Redis 分布式 ID 生成**：基于 Redisson 分布式锁，保证高并发下 ID 唯一性
3. **RAG 知识库问答**：集成 LangChain4j + Pinecone 向量检索，实现基于企业知识库的 AI 问答
4. **AI Function Calling**：大模型可自主调用业务查询接口，实现智能化的数据查询
5. **流式 AI 对话**：基于 SSE（Server-Sent Events）实现打字机效果的实时对话
6. **MongoDB 聊天记忆**：多用户、多会话的独立对话记忆持久化
7. **统一异常处理 & 响应封装**：全局异常处理器 + 统一 Result 响应格式
8. **RBAC 权限控制**：基于角色的前端路由 & 后端接口访问控制

---

## 项目结构说明

- **`common`**：公共模块，提供 JWT 工具、Redis ID 生成器、全局异常处理、登录拦截器等基础能力
- **`web`**：启动模块，包含 `application.yml` 主配置和 Spring Boot 主启动类
- **`Login`**：认证模块，处理用户登录、JWT 令牌生成
- **`employee`**：员工管理模块，包含员工 CRUD、请假管理、薪资管理（含定时任务自动生成工资单）
- **其他业务模块**（customer / purchase / stock / produce / sale）：各自独立的 Controller → Service → Mapper 三层架构

---

## 开发规范

- 后端统一响应格式：`Result<T>`（code + msg + data）
- 统一分页封装：`page<T>`（pageSize + total + list）
- DTO 用于接收请求参数，VO 用于返回视图数据，Entity 映射数据库表
- MyBatis-Plus `TypeHandler` 处理 List<String> 类型字段
- 前端 Axios 拦截器统一携带 Token & 处理 401 过期

---

## License

本项目仅用于学习和个人作品展示，禁止用于商业用途。
