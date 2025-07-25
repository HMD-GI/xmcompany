import request from './request'

export function addProduct(data) {
  return request.post('/xm/product', data)
}

export function updateProduct(data) {
  return request.put('/xm/product', data)
}

export function deleteProduct(id) {
  return request.delete(`/xm/product/${id}`)
}

export function getProductById(id) {
  return request.get(`/xm/product/${id}`)
}

export function getProductList(params) {
  return request.get('/xm/product/list', { params })
}