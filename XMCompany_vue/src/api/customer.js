import request from './request'

export function addCustomer(data) {
  return request.post('/xm/customer', data)
}

export function updateCustomer(data) {
  return request.put('/xm/customer', data)
}

export function deleteCustomer(id) {
  return request.delete(`/xm/customer/${id}`)
}

export function getCustomerById(id) {
  return request.get(`/xm/customer/${id}`)
}

export function getCustomerList(params) {
  return request.get('/xm/customer/list', { params })
} 