import request from './request'

// 生产项目相关接口
export function addProductionProject(data) {
  return request.post('/xm/production/project', data)
}

export function updateProjectStatus(data) {
  return request.put('/xm/production/project/status', data)
}

export function getProjectById(id) {
  return request.get(`/xm/production/project/${id}`)
}

export function getProjectList(params) {
  return request.get('/xm/production/project/list', { params })
}

// 生产进度相关接口
export function updateProgress(data) {
  return request.post('/xm/production/progress/update', data)
}

export function getProgressById(id) {
  return request.get(`/xm/production/progress/${id}`)
}

export function getProgressList(params) {
  return request.get('/xm/production/progress/list', { params })
}