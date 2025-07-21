<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="logo">
        <span class="logo-text">XMCompany</span>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-dropdown">
            <el-avatar :size="32" icon="el-icon-user" />
            <span class="username">管理员</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人信息</el-dropdown-item>
              <el-dropdown-item>修改密码</el-dropdown-item>
              <el-dropdown-item divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container class="main-container">
      <el-aside width="220px" class="app-aside">
        <el-menu 
          :default-active="$route.path" 
          router 
          class="app-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF">
          <div class="menu-title">系统功能</div>
          <el-menu-item index="/stock">
            <el-icon><i class="el-icon-goods"></i></el-icon>
            <span>库存管理</span>
          </el-menu-item>
          <el-menu-item index="/purchase">
            <el-icon><i class="el-icon-shopping-cart-full"></i></el-icon>
            <span>采购管理</span>
          </el-menu-item>
          <el-menu-item index="/customer">
            <el-icon><i class="el-icon-user"></i></el-icon>
            <span>客户管理</span>
          </el-menu-item>
          <el-menu-item index="/employee">
            <el-icon><i class="el-icon-user-solid"></i></el-icon>
            <span>员工管理</span>
          </el-menu-item>
          <el-menu-item index="/leave">
            <el-icon><i class="el-icon-date"></i></el-icon>
            <span>请假管理</span>
          </el-menu-item>
          <el-menu-item index="/salary">
            <el-icon><i class="el-icon-money"></i></el-icon>
            <span>薪资管理</span>
          </el-menu-item>
          <el-menu-item index="/ai">
            <el-icon><i class="el-icon-chat-dot-round"></i></el-icon>
            <span>AI助手</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

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