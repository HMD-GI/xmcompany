import request from './request'

export function setSalary(data) {
  return request.post('/xm/salary/config', data)
}

export function getSalaryConfig(employeeId) {
  return request.get(`/xm/salary/config/${employeeId}`)
}

export function updateSalaryStatus(id, status) {
  return request.put(`/xm/salary/config/status/${id}?status=${status}`)
}

export function generatePayroll(data) {
  return request.post('/xm/salary/payroll/generate', data)
}

export function adjustPayroll(data) {
  return request.put('/xm/salary/payroll/adjust', data)
}

export function payPayroll(payrollId) {
  return request.put(`/xm/salary/payroll/pay/${payrollId}`)
}

export function payAllByMonth(month) {
  return request.put(`/xm/salary/payroll/payall/${month}`)
}

export function getPayrollDetail(payrollId) {
  return request.get(`/xm/salary/payroll/${payrollId}`)
}

export function getSalaryConfigList(params) {
  return request.get('/xm/salary/config/list', { params })
}

export function getEmployeePayrollList(params) {
  return request.get('/xm/salary/payroll/list/employee', { params })
}

export function getMonthlyPayrollList(params) {
  return request.get('/xm/salary/payroll/list/month', { params })
}