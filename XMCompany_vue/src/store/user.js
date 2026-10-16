import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: (() => {
      try {
        const val = localStorage.getItem('userInfo')
        if (!val || val === 'undefined') return {}
        return JSON.parse(val)
      } catch (e) {
        return {}
      }
    })()
  }),
  getters: {
    userId: (state) => state.userInfo.id || state.userInfo.employeeId || null,
    username: (state) => state.userInfo.name || '',
    role: (state) => state.userInfo.role || '',
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    logout() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})