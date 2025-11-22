import request from './request'

export function applyLeave(data) {
  return request.post('/xm/leave/apply', data)
}

export function reviewLeave(data) {
  return request.post('/xm/leave/review', data)
}

export function getLeaveById(id) {
  return request.get(`/xm/leave/${id}`)
}

export function getLeaveList(params) {
  return request.get('/xm/leave/list', { params })
} 

