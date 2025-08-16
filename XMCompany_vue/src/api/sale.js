import request from './request'

export function addSaleOrder(data) {
  return request.post('/xm/sale-order', data)
}

export function updateSaleOrder(data) {
  return request.put('/xm/sale-order', data)
}

export function deleteSaleOrder(id) {
  return request.delete(`/xm/sale-order/${id}`)
}

export function getSaleOrderById(id) {
  return request.get(`/xm/sale-order/${id}`)
}

export function getSaleOrderList(params) {
  return request.get('/xm/sale/order/list', { params })
}

export function updateSaleOrderStatus(data) {
  return request.put('/xm/sale-order/status', data)
}