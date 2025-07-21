import axios from 'axios'
import { useUserStore } from '@/store/user'

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

export default instance