import request from './request'

export function login(params) {
  return request.get('/xm/login', { params })
}

export function logout() {
  return request.post('/xm/logout')
} 