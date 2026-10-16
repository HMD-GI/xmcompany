import request from './request'

// 采购订单相关接口（原有代码）
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

// 采购申请相关接口（新增部分）
export function addPurchaseRequest(data) {
  return request.post('/xm/purchase-request', data)
}

export function updatePurchaseRequest(data) {
  return request.put('/xm/purchase-request', data)
}

export function withdrawPurchaseRequest(id) {
  return request.put(`/xm/purchase-request/withdraw/${id}`)
}

export function updatePurchaseRequestStatus(data) {
  return request.put('/xm/purchase-request/status', data)
}

export function getPurchaseRequestById(id) {
  return request.get(`/xm/purchase-request/${id}`)
}

export function getPurchaseRequestList(params) {
  return request.get('/xm/purchase-request/list', { params })
}