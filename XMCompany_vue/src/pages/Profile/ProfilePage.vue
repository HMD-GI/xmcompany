<template>
  <el-card class="profile-card">
    <div class="profile-header">
      <h2 class="profile-title">个人信息</h2>
      <el-button type="primary" @click="handleEdit" :disabled="loading">
        {{ isEditing ? '保存' : '编辑' }}
      </el-button>
    </div>
    
    <div v-loading="loading" class="profile-content">
      <div v-if="!employeeData.id && !loading" class="no-data">
        <el-empty description="未找到员工信息" />
      </div>
      
      <div v-else class="profile-form">
        <el-form :model="formData" label-width="120px" :disabled="!isEditing">
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="员工ID">
                <el-input v-model="formData.id" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="用户名">
                <el-input v-model="formData.username" disabled />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="姓名">
                <el-input v-model="formData.name" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别">
                <el-radio-group v-model="formData.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="电话">
                <el-input v-model="formData.phone" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input v-model="formData.email" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="角色">
                <el-tag :type="getRoleTagType(formData.role)">
                  {{ getRoleText(formData.role) }}
                </el-tag>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-tag :type="formData.enabled === 1 ? 'success' : 'danger'">
                  {{ formData.enabled === 1 ? '在职' : '停职' }}
                </el-tag>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="创建时间">
                <el-input v-model="displayCreateTime" disabled />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getEmployeeById, updateEmployee } from '@/api/employee'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)
const isEditing = ref(false)
const employeeData = ref({})
const formData = ref({
  id: '',
  name: '',
  username: '',
  gender: '男',
  phone: '',
  email: '',
  role: '',
  enabled: 1,
  createTime: ''
})

// 格式化创建时间显示
const displayCreateTime = computed(() => {
  if (!formData.value.createTime) return '-'
  return formData.value.createTime.replace('T', ' ')
})

// 获取角色文本
function getRoleText(role) {
  const roleMap = {
    'ADMIN': '管理员',
    'USER': '普通员工'
  }
  return roleMap[role] || role
}

// 获取角色标签类型
function getRoleTagType(role) {
  const typeMap = {
    'ADMIN': 'danger',
    'USER': 'info'
  }
  return typeMap[role] || 'info'
}

// 获取当前用户信息
async function fetchUserProfile() {
  loading.value = true
  try {
    // 检查用户ID是否存在
    if (!userStore.userId) {
      ElMessage.error('无法获取用户信息，请重新登录')
      return
    }
    
    const currentUserId = userStore.userId
    
    const res = await getEmployeeById(currentUserId)
    if (res.data && res.data.code === 0) {
      employeeData.value = res.data.data || {}
      formData.value = {
        id: employeeData.value.id || '',
        name: employeeData.value.name || '',
        username: employeeData.value.username || '',
        gender: employeeData.value.gender || '男',
        phone: employeeData.value.phone || '',
        email: employeeData.value.email || '',
        role: employeeData.value.role || '',
        enabled: employeeData.value.enabled || 1,
        createTime: employeeData.value.createTime || ''
      }
    } else {
      ElMessage.error(res.data?.msg || '获取个人信息失败')
    }
  } catch (error) {
    console.error('获取个人信息失败:', error)
    ElMessage.error('获取个人信息失败')
  } finally {
    loading.value = false
  }
}

// 处理编辑/保存
async function handleEdit() {
  if (!isEditing.value) {
    // 进入编辑模式
    isEditing.value = true
  } else {
    // 保存修改
    await saveProfile()
  }
}

// 保存个人信息
async function saveProfile() {
  loading.value = true
  try {
    const updateData = {
      id: formData.value.id,
      name: formData.value.name,
      username: formData.value.username,
      gender: formData.value.gender,
      phone: formData.value.phone,
      email: formData.value.email,
      role: formData.value.role,
      enabled: formData.value.enabled
    }
    
    const res = await updateEmployee(updateData)
    if (res.data && res.data.code === 0) {
      ElMessage.success('个人信息更新成功')
      isEditing.value = false
      // 重新获取最新数据
      await fetchUserProfile()
    } else {
      ElMessage.error(res.data?.msg || '更新失败')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error('更新失败')
  } finally {
    loading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchUserProfile()
})
</script>

<style scoped>
.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.profile-title {
  margin: 0;
  font-size: 20px;
  color: #303133;
  font-weight: 600;
}

.profile-content {
  min-height: 400px;
}

.profile-form {
  padding: 20px 0;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.el-form-item {
  margin-bottom: 24px;
}

.el-tag {
  font-size: 14px;
  padding: 4px 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-card {
    margin: 0 16px;
  }
  
  .el-col {
    margin-bottom: 16px;
  }
}
</style>
