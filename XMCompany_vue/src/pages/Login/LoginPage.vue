<template>
  <div class="login-container">
    <div class="login-background">
      <div class="login-left">
        <div class="company-info">
          <h1 class="company-name">XMCompany</h1>
          <p class="company-slogan">企业管理系统</p>
          <div class="company-features">
            <div class="feature-item">
              <div class="feature-icon">📊</div>
              <div class="feature-text">高效管理</div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🔒</div>
              <div class="feature-text">安全可靠</div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🚀</div>
              <div class="feature-text">智能决策</div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="login-right">
        <div class="login-form-container">
          <div class="login-header">
            <h2 class="login-title">账号登录</h2>
            <p class="login-subtitle">欢迎回来，请输入您的账号信息</p>
          </div>
          
          <el-form :model="form" @submit.prevent="onLogin" class="login-form">
            <el-form-item>
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名" 
                prefix-icon="el-icon-user"
                autocomplete="username" 
              />
            </el-form-item>
            
            <el-form-item>
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码" 
                prefix-icon="el-icon-lock"
                autocomplete="current-password" 
                @keyup.enter="onLogin"
              />
            </el-form-item>
            
            <div class="login-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <a href="#" class="forgot-password">忘记密码?</a>
            </div>
            
            <el-button 
              type="primary" 
              @click="onLogin" 
              :loading="loading" 
              class="login-button"
            >
              登录
            </el-button>
          </el-form>
          
          <div class="login-footer">
            <p>© {{ currentYear }} XMCompany. 保留所有权利</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const rememberMe = ref(false)

// 获取当前年份
const currentYear = computed(() => new Date().getFullYear())

const onLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form.value)
    if (res.data.code === 0) {
      // 如果选择了记住我，可以在这里设置相关逻辑
      userStore.setToken(res.data.data.token)
      userStore.setUserInfo(res.data.data)
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch (e) {
    console.error('登录异常:', e)
    ElMessage.error('登录失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-background {
  display: flex;
  width: 100%;
  height: 100%;
  border-radius: 0;
  overflow: hidden;
  box-shadow: none;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #304156 0%, #409EFF 100%);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;
}

.login-right {
  flex: 1;
  background-color: white;
  display: flex;
  justify-content: center;
  align-items: center;
}

.company-info {
  max-width: 320px;
}

.company-name {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 10px;
}

.company-slogan {
  font-size: 18px;
  opacity: 0.8;
  margin-bottom: 40px;
}

.company-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.feature-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.feature-text {
  font-size: 16px;
}

.login-right {
  flex: 1;
  background-color: white;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form-container {
  width: 80%;
  max-width: 320px;
}

.login-header {
  margin-bottom: 30px;
  text-align: center;
}

.login-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.login-subtitle {
  color: #909399;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 14px;
}

.forgot-password {
  color: #409EFF;
  text-decoration: none;
}

.login-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  border-radius: 4px;
}

.login-footer {
  text-align: center;
  color: #909399;
  font-size: 12px;
  margin-top: 40px;
}
</style>