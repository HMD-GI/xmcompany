import request from './request'

export function addSupplier(data) {
  return request.post('/xm/supplier', data)
}

export function updateSupplier(data) {
  return request.put('/xm/supplier', data)
}

export function deleteSupplier(id) {
  return request.delete(`/xm/supplier/${id}`)
}

export function getSupplierById(id) {
  return request.get(`/xm/supplier/${id}`)
}

export function getSupplierList(params) {
  return request.get('/xm/supplier/list', { params })
}

export function updateSupplierStatus(data) {
  return request.put('/xm/supplier/status', data)
}