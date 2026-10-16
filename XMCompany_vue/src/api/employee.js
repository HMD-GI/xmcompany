import request from './request'

// 获取员工列表
export function getEmployeeList(params) {
  return request.get('/xm/employee/list', { params })
}

// 新增员工
export function addEmployee(data) {
  return request.post('/xm/employee', data)
}

// 更新员工信息
export function updateEmployee(data) {
  return request.put('/xm/employee/update', data)
}

// 更新员工状态
export function updateEmployeeStatus(id, enabled) {
  return request.put(`/xm/employee/${id}`, null, { params: { enabled } })
}

// 删除员工
export function deleteEmployee(id) {
  return request.delete(`/xm/employee/${id}`)
}

// 获取员工详情
export function getEmployeeById(id) {
  return request.get(`/xm/employee/${id}`)
}

// 重置员工密码
export function resetEmployeePassword(id) {
  return request.put(`/xm/employee/reset-password/${id}`)
}
