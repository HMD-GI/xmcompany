import request from './request'

export function stockIn(data) {
  return request.post('/xm/stock/in', data)
}

export function stockOut(data) {
  return request.post('/xm/stock/out', data)
}

export function updateStock(data) {
  return request.put('/xm/stock', data)
}

export function getStockById(id) {
  return request.get(`/xm/stock/${id}`)
}

export function getStockList(params) {
  return request.get('/xm/stock/list', { params })
} 