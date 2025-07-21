import request from './request'

export function setSalary(data) {
  return request.post('/xm/salary/config', data)
}

export function getSalaryConfig(employeeId) {
  return request.get(`/xm/salary/config/${employeeId}`)
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

export function getEmployeePayrollList(employeeId, params) {
  return request.get(`/xm/salary/payroll/list/employee/${employeeId}`, { params })
}

export function getMonthlyPayrollList(month, params) {
  return request.get(`/xm/salary/payroll/list/month/${month}`, { params })
} 