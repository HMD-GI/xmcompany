<template>
  <router-view />
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from './store/user'

const router = useRouter()
const userStore = useUserStore()

function logout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  overflow: hidden;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background: linear-gradient(90deg, #304156 0%, #409EFF 100%);
  color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
}

.logo {
  display: flex;
  align-items: center;
}

.logo-img {
  height: 32px;
  margin-right: 10px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
}

.username {
  margin-left: 8px;
}

.main-container {
  height: calc(100vh - 60px);
}

.app-aside {
  background-color: #304156;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
}

.app-menu {
  border-right: none;
}

.menu-title {
  color: #fff;
  font-size: 14px;
  padding: 15px 20px 10px;
  opacity: 0.8;
}

.app-main {
  padding: 20px;
  background-color: #f5f7fa;
  overflow-y: auto;
}
</style>