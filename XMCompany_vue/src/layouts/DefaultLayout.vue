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
            <span class="username">{{ userStore.username || '用户' }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToProfile">个人信息</el-dropdown-item>
              <el-dropdown-item @click="openApplyDialog">请假申请</el-dropdown-item>
              <el-dropdown-item @click="openChangePwd">修改密码</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
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
          <el-menu-item index="/stock-operation">
            <el-icon><i class="el-icon-notebook-2"></i></el-icon>
            <span>库存操作记录</span>
          </el-menu-item>
          <el-menu-item index="/purchase">
            <el-icon><i class="el-icon-shopping-cart-full"></i></el-icon>
            <span>采购管理</span>
          </el-menu-item>
          <el-menu-item index="/purchase-request">
            <el-icon><i class="el-icon-document"></i></el-icon>
            <span>采购申请</span>
          </el-menu-item>
          <el-menu-item index="/supplier">
            <el-icon><i class="el-icon-office-building"></i></el-icon>
            <span>供应商管理</span>
          </el-menu-item>
          <el-menu-item index="/production">
            <el-icon><i class="el-icon-set-up"></i></el-icon>
            <span>生产管理</span>
          </el-menu-item>
          <el-menu-item index="/sale">
            <el-icon><i class="el-icon-sell"></i></el-icon>
            <span>销售管理</span>
          </el-menu-item>
          <el-menu-item index="/customer">
            <el-icon><i class="el-icon-user"></i></el-icon>
            <span>客户管理</span>
          </el-menu-item>
          <el-menu-item v-if="userStore.role === 'ADMIN'" index="/employee">
            <el-icon><i class="el-icon-user-solid"></i></el-icon>
            <span>员工管理</span>
          </el-menu-item>
          <el-menu-item v-if="userStore.role === 'ADMIN'" index="/leave">
            <el-icon><i class="el-icon-date"></i></el-icon>
            <span>请假管理</span>
          </el-menu-item>
          <el-menu-item v-if="userStore.role === 'ADMIN'" index="/salary">
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

  <el-dialog v-model="showChangePwd" title="修改密码" width="420px" destroy-on-close>
    <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showChangePwd = false">取消</el-button>
      <el-button type="primary" @click="submitChangePwd">确定</el-button>
    </template>
  </el-dialog>

  <!-- 申请请假弹窗 -->
  <el-dialog v-model="applyDialogVisible" title="申请请假" width="600px">
    <el-form :model="applyForm" ref="applyFormRef" label-width="100px" :rules="applyRules">
      <el-form-item label="员工ID" prop="employeeId">
        <el-input v-model="applyForm.employeeId" placeholder="请输入员工ID" />
      </el-form-item>
      <el-form-item label="请假类型" prop="leaveType">
        <el-select v-model="applyForm.leaveType" placeholder="请选择请假类型">
          <el-option label="事假" value="1" />
          <el-option label="病假" value="2" />
          <el-option label="年假" value="3" />
          <el-option label="调休" value="4" />
          <el-option label="婚假" value="5" />
          <el-option label="产假" value="6" />
          <el-option label="丧假" value="7" />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="applyForm.startTime"
          type="datetime"
          placeholder="选择开始时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="applyForm.endTime"
          type="datetime"
          placeholder="选择结束时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="请假事由" prop="reason">
        <el-input v-model="applyForm.reason" type="textarea" :rows="3" placeholder="请输入请假事由" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="applyDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitApply">提交申请</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { applyLeave } from '@/api/leave'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const showChangePwd = ref(false)
const pwdFormRef = ref()
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 请假申请相关
const applyDialogVisible = ref(false)
const applyFormRef = ref(null)
const applyForm = reactive({
  employeeId: userStore.userId || '',
  leaveType: '',
  startTime: '',
  endTime: '',
  reason: ''
})

const applyRules = {
  employeeId: [
    { required: true, message: '请输入员工ID', trigger: 'blur' }
  ],
  leaveType: [
    { required: true, message: '请选择请假类型', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入请假事由', trigger: 'blur' }
  ]
}

function openApplyDialog() {
  applyForm.employeeId = userStore.userId || ''
  applyForm.leaveType = ''
  applyForm.startTime = ''
  applyForm.endTime = ''
  applyForm.reason = ''
  applyDialogVisible.value = true
}

async function submitApply() {
  try {
    await applyFormRef.value?.validate()
    const { data: res } = await applyLeave(applyForm)
    if (res && res.code === 0) {
      ElMessage.success(res.msg || '请假申请提交成功')
      applyDialogVisible.value = false
    } else {
      ElMessage.error((res && res.msg) || '申请提交失败')
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error?.response?.data?.msg || '申请提交失败，请重试')
    }
  }
}

function openChangePwd() {
  showChangePwd.value = true
}

function resetPwdForm() {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
}

async function submitChangePwd() {
  if (!pwdFormRef.value) return
  pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (pwdForm.newPassword !== pwdForm.confirmPassword) {
      ElMessage.error('两次输入的新密码不一致')
      return
    }
    try {
      const userId = userStore.userId
      const { data: res } = await request.put('/xm/employee/change-password', {
        id: userId,
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      if (res && res.code === 0) {
        ElMessage.success(res.msg || '密码修改成功')
        showChangePwd.value = false
        resetPwdForm()
      } else {
        ElMessage.error((res && res.msg) || '修改失败')
        return
      }
    } catch (e) {
      ElMessage.error(e?.response?.data?.msg || '修改失败，请检查原密码是否正确')
    }
  })
}

function goToProfile() {
  router.push('/profile')
}

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