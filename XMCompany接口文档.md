XMCompany 公司管理系统详细接口文档
1. 登录模块（Login）
1.1 员工登录
接口地址：GET /xm/login
请求方式：GET
请求参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|----------|--------|------|----------|--------|
| username | string | 是 | admin | 用户名 |
| password | string | 是 | 123456 | 密码 |
响应结构：
| 字段名 | 类型 | 示例 | 说明 |
|----------|--------|----------|----------|
| code | int | 0 | 状态码 |
| msg | string | 登录成功 | 提示信息 |
| data | object | ... | 数据体 |
data 结构：
| 字段名 | 类型 | 示例 | 说明 |
|----------|--------|----------|----------|
| token | string | xxxxx | 登录令牌 |
| userInfo | object | ... | 用户信息 |
2. 库存模块（stock）
2.1 入库操作
接口地址：POST /xm/stock/in
请求方式：POST
请求参数（JSON）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------------|--------|------|--------|----------|
| materialName | string | 是 | 螺丝刀 | 物料名称 |
| unit | string | 是 | 个 | 单位 |
| quantity | int | 是 | 100 | 入库数量 |
| remark | string | 否 | 入库 | 备注 |
响应结构：Result（见通用说明）
2.2 出库操作
接口地址：POST /xm/stock/out
请求方式：POST
请求参数（JSON）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------------|--------|------|--------|----------|
| stockId | int | 是 | 1 | 库存ID |
| quantity | int | 是 | 10 | 出库数量 |
| remark | string | 否 | 出库 | 备注 |
| materialName | string | 是 | 螺丝刀 | 物料名称 |
| unit | string | 是 | 个 | 单位 |
响应结构：Result
2.3 更新库存
接口地址：PUT /xm/stock
请求方式：PUT
请求参数（JSON）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|------------------|--------|------|---------------------|--------------|
| id | int | 是 | 1 | 库存ID |
| materialName | string | 是 | 螺丝刀 | 物料名称 |
| unit | string | 是 | 个 | 单位 |
| quantity | int | 是 | 100 | 库存数量 |
| lastStockInTime | string | 否 | 2024-06-01T12:00:00 | 最后入库时间 |
| lastStockOutTime | string | 否 | 2024-06-01T13:00:00 | 最后出库时间 |
| createTime | string | 否 | 2024-06-01T10:00:00 | 创建时间 |
| updateTime | string | 否 | 2024-06-01T14:00:00 | 更新时间 |
响应结构：Result
2.4 查询库存详情
接口地址：GET /xm/stock/{id}
请求方式：GET
响应结构：
| 字段名 | 类型 | 示例 | 说明 |
|------------------|--------|---------------------|--------------|
| id | int | 1 | 库存ID |
| materialName | string | 螺丝刀 | 物料名称 |
| unit | string | 个 | 单位 |
| quantity | int | 100 | 库存数量 |
| lastStockInTime | string | 2024-06-01T12:00:00 | 最后入库时间 |
| lastStockOutTime | string | 2024-06-01T13:00:00 | 最后出库时间 |
| createTime | string | 2024-06-01T10:00:00 | 创建时间 |
| updateTime | string | 2024-06-01T14:00:00 | 更新时间 |
2.5 分页查询库存列表
接口地址：GET /xm/stock/list
请求方式：GET
请求参数（Query）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|--------|----------|
| currentPage | int | 否 | 1 | 当前页码 |
| pageSize | int | 否 | 10 | 每页数量 |
| materialName| string | 否 | 螺丝刀 | 物料名称 |
响应结构：Result<page<StockVO>>
3. 库存操作记录模块（stock-operation）
3.1 查询操作记录详情
接口地址：GET /xm/stock-operation/{id}
请求方式：GET
响应结构：Result<StockOperationVO>
3.2 分页查询操作记录列表
接口地址：GET /xm/stock-operation/list
请求方式：GET
请求参数（Query）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|---------------|--------|------|-----------|--------------|
| currentPage | int | 否 | 1 | 当前页码 |
| pageSize | int | 否 | 10 | 每页数量 |
| operationNo | string | 否 | IN202406 | 操作编号 |
| materialName | string | 否 | 螺丝刀 | 物料名称 |
| operationType | int | 否 | 0 | 操作类型 |
| operatorId | int | 否 | 1001 | 操作人ID |
| startTime | string | 否 | 2024-06-01T00:00:00 | 开始时间 |
| endTime | string | 否 | 2024-06-30T23:59:59 | 结束时间 |
响应结构：Result<page<StockOperationVO>
4. 采购模块（purchase）
4.1 新增采购订单
接口地址：POST /xm/purchase-order
请求方式：POST
请求参数（JSON）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|----------------|----------|------|--------------|----------------|
| orderNo | string | 否 | PO202406001 | 采购订单编号 |
| supplierId | int | 是 | 1001 | 供应商ID |
| orderDate | string | 是 | 2024-06-01 | 订单日期 |
| status | int | 否 | 0 | 订单状态（0草稿/1已提交/2已完成/3已作废）|
| totalAmount | decimal | 是 | 1234.56 | 订单总金额 |
| remark | string | 否 | 紧急采购 | 备注 |
| items | array | 是 | [ ... ] | 采购明细列表 |
items（采购明细）结构：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|----------------|----------|------|--------------|----------------|
| materialId | int | 是 | 2001 | 物料ID |
| materialName | string | 是 | 螺丝刀 | 物料名称 |
| unit | string | 是 | 个 | 单位 |
| quantity | int | 是 | 100 | 数量 |
| price | decimal | 是 | 12.34 | 单价 |
| amount | decimal | 是 | 1234.00 | 小计金额 |
请求示例：
Apply to XMCompany接口文...
4.2 更新采购订单
接口地址：PUT /xm/purchase-order
请求方式：PUT
请求参数：同“新增采购订单”，需包含订单ID（如有）
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|----------------|----------|------|--------------|----------------|
| id | int | 是 | 1 | 采购订单ID |
| ... | ... | ... | ... | 其余同上 |
4.3 作废采购订单
接口地址：PUT /xm/purchase-order/cancel/{id}
请求方式：PUT
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|------------|
| id | int | 是 | 1 | 采购订单ID |
4.4 变更采购订单状态
接口地址：PUT /xm/purchase-order/status
请求方式：PUT
请求参数（JSON）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|------------|--------|------|------|--------------|
| id | int | 是 | 1 | 采购订单ID |
| status | int | 是 | 2 | 新状态值 |
| remark | string | 否 | 完成 | 状态变更备注 |
4.5 查询采购订单详情
接口地址：GET /xm/purchase-order/{id}
请求方式：GET
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|------------|
| id | int | 是 | 1 | 采购订单ID |
4.6 分页查询采购订单列表
接口地址：GET /xm/purchase-order/list
请求方式：GET
请求参数（Query）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|--------------|--------------|
| currentPage | int | 否 | 1 | 当前页码 |
| pageSize | int | 否 | 10 | 每页数量 |
| orderNo | string | 否 | PO202406001 | 订单编号 |
| supplierId | int | 否 | 1001 | 供应商ID |
| status | int | 否 | 1 | 订单状态 |
| startDate | string | 否 | 2024-06-01 | 开始日期 |
| endDate | string | 否 | 2024-06-30 | 结束日期 |
> 采购申请、供应商等子模块接口风格与采购订单一致，字段见各自DTO/VO/Entity。
5. 客户模块（customer）
5.1 新增客户
接口地址：POST /xm/customer
请求方式：POST
请求参数（JSON）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|----------------|--------|------|--------------|--------------|
| name | string | 是 | 张三公司 | 客户名称 |
| contactPerson | string | 是 | 李四 | 联系人 |
| contactPhone | string | 是 | 13800000000 | 联系电话 |
| address | string | 否 | 北京市朝阳区 | 客户地址 |
| email | string | 否 | a@b.com | 邮箱 |
| remark | string | 否 | VIP客户 | 备注 |
请求示例：
Apply to XMCompany接口文...
5.2 更新客户
接口地址：PUT /xm/customer
请求方式：PUT
请求参数：同“新增客户”，需包含客户ID
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|----------|
| id | int | 是 | 1 | 客户ID |
| ... | ... | ... | ... | 其余同上 |
5.3 删除客户
接口地址：DELETE /xm/customer/{id}
请求方式：DELETE
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|--------|
| id | int | 是 | 1 | 客户ID |
5.4 查询客户详情
接口地址：GET /xm/customer/{id}
请求方式：GET
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|--------|
| id | int | 是 | 1 | 客户ID |
5.5 分页查询客户列表
接口地址：GET /xm/customer/list
请求方式：GET
请求参数（Query）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|--------------|----------|
| currentPage | int | 否 | 1 | 当前页码 |
| pageSize | int | 否 | 10 | 每页数量 |
| name | string | 否 | 张三公司 | 客户名称 |
| contactPerson | string | 否 | 李四 | 联系人 |
6. 员工模块（employee）
6.1 新增员工
接口地址：POST /xm/employee
请求方式：POST
请求参数（JSON）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|------------|--------|------|--------------|--------------|
| name | string | 是 | 王五 | 员工姓名 |
| gender | int | 是 | 1 | 性别（1男0女）|
| phone | string | 是 | 13900000000 | 手机号 |
| email | string | 否 | c@d.com | 邮箱 |
| position | string | 是 | 销售经理 | 职位 |
| department | string | 否 | 市场部 | 部门 |
| hireDate | string | 是 | 2024-06-01 | 入职日期 |
| enabled | int | 否 | 1 | 状态（1启用0禁用）|
| remark | string | 否 | 试用期 | 备注 |
请求示例：
Apply to XMCompany接口文...
6.2 更新员工状态
接口地址：PUT /xm/employee/{id}?enabled=1
请求方式：PUT
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|--------|
| id | int | 是 | 1 | 员工ID |
Query参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|--------|
| enabled| int | 是 | 1 | 状态（1启用0禁用）|
6.3 更新员工信息
接口地址：PUT /xm/employee/update
请求方式：PUT
请求参数：同“新增员工”，需包含员工ID
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|----------|
| id | int | 是 | 1 | 员工ID |
| ... | ... | ... | ... | 其余同上 |
6.4 删除员工
接口地址：DELETE /xm/employee/{id}
请求方式：DELETE
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|--------|
| id | int | 是 | 1 | 员工ID |
6.5 查询员工详情
接口地址：GET /xm/employee/{id}
请求方式：GET
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|--------|
| id | int | 是 | 1 | 员工ID |
6.6 分页查询员工列表
接口地址：GET /xm/employee/list
请求方式：GET
请求参数（Query）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|--------------|----------|
| currentPage | int | 否 | 1 | 当前页码 |
| pageSize | int | 否 | 10 | 每页数量 |
| name | string | 否 | 王五 | 员工姓名 |
| department | string | 否 | 市场部 | 部门 |
| position | string | 否 | 销售经理 | 职位 |
| enabled | int | 否 | 1 | 状态 |
6.1 请假管理
6.1.1 提交请假申请
接口地址：POST /xm/leave/apply
请求方式：POST
请求参数（JSON，LeaveApplyDTO）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|----------------|--------------|
| employeeId | int | 是 | 1 | 员工ID |
| type | int | 是 | 2 | 请假类型（1事假2病假3年假等）|
| startDate | string | 是 | 2024-06-10 | 开始日期 |
| endDate | string | 是 | 2024-06-12 | 结束日期 |
| days | int | 是 | 3 | 请假天数 |
| reason | string | 是 | 身体不适 | 请假原因 |
| remark | string | 否 | 需休息 | 备注 |
请求示例：
Apply to XMCompany接口文...
6.1.2 审核请假申请
接口地址：POST /xm/leave/review
请求方式：POST
请求参数（JSON，LeaveReviewDTO）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|--------|--------------|
| leaveId | int | 是 | 10 | 请假记录ID |
| reviewerId | int | 是 | 2 | 审核人ID |
| status | int | 是 | 1 | 审核状态（1通过2驳回）|
| reviewRemark| string | 否 | 同意 | 审核意见 |
请求示例：
Apply to XMCompany接口文...
6.1.3 查询请假详情
接口地址：GET /xm/leave/{id}
请求方式：GET
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|------|------|------|----------|
| id | int | 是 | 10 | 请假ID |
6.1.4 分页查询请假列表
接口地址：GET /xm/leave/list
请求方式：GET
请求参数（Query）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|--------|--------------|
| currentPage | int | 否 | 1 | 当前页码 |
| pageSize | int | 否 | 10 | 每页数量 |
| employeeId | int | 否 | 1 | 员工ID |
| status | int | 否 | 1 | 审核状态 |
6.2 薪资管理
6.2.1 设置员工薪资配置
接口地址：POST /xm/salary/config
请求方式：POST
请求参数（JSON，SalaryDTO）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|----------|--------------|
| employeeId | int | 是 | 1 | 员工ID |
| baseSalary | decimal| 是 | 8000.00 | 基本工资 |
| bonus | decimal| 否 | 1000.00 | 绩效奖金 |
| allowance | decimal| 否 | 500.00 | 补贴 |
| remark | string | 否 | 优秀员工 | 备注 |
请求示例：
Apply to XMCompany接口文...
6.2.2 获取员工薪资配置
接口地址：GET /xm/salary/config/{employeeId}
请求方式：GET
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|------------|------|------|------|--------|
| employeeId | int | 是 | 1 | 员工ID |
6.2.3 生成月度工资单
接口地址：POST /xm/salary/payroll/generate
请求方式：POST
请求参数（JSON，PayrollGenerationDTO）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|----------|--------------|
| month | string | 是 | 2024-06 | 工资月份 |
| employeeIds | array | 否 | [1,2,3] | 生成工资单的员工ID列表（不传为全部）|
请求示例：
Apply to XMCompany接口文...
6.2.4 调整工资单
接口地址：PUT /xm/salary/payroll/adjust
请求方式：PUT
请求参数（JSON，PayrollAdjustmentDTO）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|----------|--------------|
| payrollId | int | 是 | 100 | 工资单ID |
| adjustment | decimal| 是 | 200.00 | 调整金额 |
| remark | string | 否 | 补发奖金 | 调整说明 |
请求示例：
Apply to XMCompany接口文...
6.2.5 发放单个工资单
接口地址：PUT /xm/salary/payroll/pay/{payrollId}
请求方式：PUT
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-----------|------|------|------|----------|
| payrollId | int | 是 | 100 | 工资单ID |
6.2.6 批量发放月度工资单
接口地址：PUT /xm/salary/payroll/payall/{month}
请求方式：PUT
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|--------|------|----------|----------|
| month | string | 是 | 2024-06 | 工资月份 |
6.2.7 获取工资单详情
接口地址：GET /xm/salary/payroll/{payrollId}
请求方式：GET
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-----------|------|------|------|----------|
| payrollId | int | 是 | 100 | 工资单ID |
6.2.8 查询员工工资单列表
接口地址：GET /xm/salary/payroll/list/employee/{employeeId}
请求方式：GET
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|------------|------|------|------|--------|
| employeeId | int | 是 | 1 | 员工ID |
Query参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|--------|----------|
| year | int | 否 | 2024 | 年份 |
| month | int | 否 | 6 | 月份 |
| currentPage | int | 否 | 1 | 当前页码 |
| pageSize | int | 否 | 10 | 每页数量 |
6.2.9 查询月度工资单列表
接口地址：GET /xm/salary/payroll/list/month/{month}
请求方式：GET
路径参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|--------|--------|------|----------|----------|
| month | string | 是 | 2024-06 | 工资月份 |
Query参数：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|-------------|--------|------|--------|----------|
| status | int | 否 | 1 | 工资单状态 |
| currentPage | int | 否 | 1 | 当前页码 |
| pageSize | int | 否 | 20 | 每页数量 |
7. AI助手模块（ai）
7.1 智能对话
接口地址：POST /xm/ai/chat
请求方式：POST
请求参数（JSON，ChatMessages）：
| 参数名 | 类型 | 必填 | 示例 | 说明 |
|------------|--------|------|--------------|----------------|
| messageId | string | 是 | msg-001 | 消息ID（可用于上下文追踪）|
| content | string | 是 | 你好，帮我查下库存 | 用户输入内容 |
| userId | int | 否 | 1 | 用户ID（如有登录）|
| sessionId | string | 否 | session-001 | 会话ID（如有多轮对话）|
8. 认证与安全
登录成功后需携带 token 访问受保护接口，token 通过请求头 Authorization 传递。
示例：Authorization: Bearer {token}