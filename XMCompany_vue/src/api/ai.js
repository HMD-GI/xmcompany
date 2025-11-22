import request from './request'

export function aiChat(data) {
  return request.post('/xm/ai/chat', data)
} 