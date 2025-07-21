import request from './request'

export function addPurchaseOrder(data) {
  return request.post('/xm/purchase-order', data)
}

export function updatePurchaseOrder(data) {
  return request.put('/xm/purchase-order', data)
}

export function cancelPurchaseOrder(id) {
  return request.put(`/xm/purchase-order/cancel/${id}`)
}

export function updatePurchaseOrderStatus(data) {
  return request.put('/xm/purchase-order/status', data)
}

export function getPurchaseOrderById(id) {
  return request.get(`/xm/purchase-order/${id}`)
}

export function getPurchaseOrderList(params) {
  return request.get('/xm/purchase-order/list', { params })
} 