import request from './request'

// 获取员工列表
export function getEmployeeList(params) {
  return request({
    url: '/xm/employee/list',
    method: 'get',
    params
  })
}
