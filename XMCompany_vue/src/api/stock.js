import request from './request'

// 库存相关接口（原有代码）
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

// 库存操作记录相关接口（新增部分）
export function getStockOperationById(id) {
  return request.get(`/xm/stock-operation/${id}`)
}

export function getStockOperationList(params) {
  return request.get('/xm/stock-operation/list', { params })
}