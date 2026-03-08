import axios from 'axios'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import router from '@/router'

const instance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

instance.interceptors.request.use(config => {
  const userStore = useUserStore && useUserStore()
  if (userStore && userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

instance.interceptors.response.use(
  response => {
    return response
  },
  error => {
    // 处理 token 过期或无效的情况
    if (error.response && error.response.status === 401) {
      const userStore = useUserStore && useUserStore()
      userStore.logout()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    }
    return Promise.reject(error)
  }
)

export default instance