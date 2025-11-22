<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="姓名">
        <el-input v-model="query.name" placeholder="输入员工姓名" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openAdd" style="margin-left: 8px;">新增员工</el-button>
    </el-form>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column type="index" label="序号" width="60" />
      <!-- <el-table-column prop="id" label="ID" min-width="80" /> -->
      <el-table-column prop="name" label="姓名" min-width="120" />
      <el-table-column prop="gender" label="性别" min-width="80">
        <template #default="{ row }">
          {{ row.gender === 'M' || row.gender === 1 || row.gender === '男' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="role" label="角色" min-width="120">
        <template #default="{ row }">
          {{ getRoleText(row.role) }}
        </template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="row.enabled === 1 ? 'success' : 'danger'">
            {{ row.enabled === 1 ? '在职' : '停职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="350">
        <template #default="{ row }">
          <el-button type="info" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button 
            :type="row.enabled === 1 ? 'danger' : 'success'" 
            size="small" 
            @click="handleStatusChange(row)"
          >
            {{ row.enabled === 1 ? '停职' : '恢复' }}
          </el-button>
          <el-button type="warning" size="small" @click="handleResetPassword(row)">重置密码</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="query.currentPage"
      v-model:page-size="query.pageSize"
      :total="total"
      @current-change="fetchList"
      @size-change="fetchList"
      layout="total, prev, pager, next, sizes"
      :page-sizes="[10, 20, 50]"
      style="margin-top: 16px;"
    />

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="员工详情" width="520px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailData.username }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleTagType(detailData.role)">
            {{ getRoleText(detailData.role) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="电话">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.enabled === 1 ? 'success' : 'danger'">
            {{ detailData.enabled === 1 ? '在职' : '停职' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(detailData.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="editVisible" :title="isEdit ? '编辑员工' : '新增员工'" width="520px">
      <el-form :model="editForm" label-width="90px" :rules="rules" ref="formRef">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="editForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="editForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="普通员工" value="USER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-select v-model="editForm.enabled" placeholder="请选择状态">
            <el-option label="在职" :value="1" />
            <el-option label="停职" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  getEmployeeList,
  addEmployee,
  updateEmployee,
  updateEmployeeStatus,
  deleteEmployee,
  getEmployeeById,
  resetEmployeePassword
} from '@/api/employee'
import { ElMessage, ElMessageBox } from 'element-plus'

// 查询参数
const query = ref({
  currentPage: 1,
  pageSize: 10,
  name: ''
})

// 数据
const list = ref([])
const total = ref(0)
const loading = ref(false)

// 详情
const detailVisible = ref(false)
const detailData = ref({})

// 编辑
const editVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref()
const editForm = ref({
  id: undefined,
  name: '',
  username: '',
  password: '',
  gender: '男',
  phone: '',
  email: '',
  role: 'USER',
  enabled: 1
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 格式化时间显示
function formatTime(timeStr) {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ')
}

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

function resetEditForm() {
  editForm.value = {
    id: undefined,
    name: '',
    username: '',
    password: 'xm123456',
    gender: '男',
    phone: '',
    email: '',
    role: 'USER',
    enabled: 1
  }
}

// 获取员工列表
async function fetchList() {
  loading.value = true
  try {
    const res = await getEmployeeList(query.value)
    if (res.data && res.data.code === 0) {
      list.value = res.data.data.list || []
      total.value = res.data.data.total || 0
    } else {
      ElMessage.error(res.data.msg || '获取员工列表失败')
    }
  } catch (error) {
    console.error('获取员工列表失败:', error)
    ElMessage.error('获取员工列表失败')
  } finally {
    loading.value = false
  }
}

// 详情
async function handleDetail(row) {
  try {
    const res = await getEmployeeById(row.id)
    if (res.data && res.data.code === 0) {
      detailData.value = res.data.data || {}
      detailVisible.value = true
    } else {
      ElMessage.error(res.data.msg || '获取员工详情失败')
    }
  } catch (e) {
    console.error('获取员工详情失败:', e)
    ElMessage.error('获取员工详情失败')
  }
}

// 新增
function openAdd() {
  isEdit.value = false
  resetEditForm()
  editVisible.value = true
}

// 编辑 - 先获取详情再编辑
async function handleEdit(row) {
  isEdit.value = true
  try {
    const res = await getEmployeeById(row.id)
    if (res.data && res.data.code === 0) {
      const employeeData = res.data.data || row
      editForm.value = {
        id: employeeData.id,
        name: employeeData.name,
        username: employeeData.username,
        password: employeeData.password, // 保存原密码，但不显示
        gender: employeeData.gender,
        phone: employeeData.phone,
        email: employeeData.email,
        role: employeeData.role,
        enabled: employeeData.enabled
      }
      editVisible.value = true
    } else {
      ElMessage.error(res.data.msg || '获取员工详情失败')
    }
  } catch (e) {
    console.error('获取员工详情失败:', e)
    ElMessage.error('获取员工详情失败')
  }
}

// 保存
async function handleSave() {
  try {
    await formRef.value.validate()
    saving.value = true
    
    if (isEdit.value && editForm.value.id) {
      const res = await updateEmployee({ ...editForm.value })
      if (res.data && res.data.code === 0) {
        ElMessage.success('更新成功')
        editVisible.value = false
        fetchList()
      } else {
        ElMessage.error(res.data.msg || '更新失败')
      }
    } else {
      const res = await addEmployee({ ...editForm.value })
      if (res.data && res.data.code === 0) {
        ElMessage.success('新增成功')
        editVisible.value = false
        fetchList()
      } else {
        ElMessage.error(res.data.msg || '新增失败')
      }
    }
  } catch (e) {
    if (e !== false) { // 表单验证失败不显示错误
      console.error('保存失败:', e)
      ElMessage.error('保存失败')
    }
  } finally {
    saving.value = false
  }
}

// 状态变更
async function handleStatusChange(row) {
  try {
    const targetEnabled = row.enabled === 1 ? 0 : 1
    const res = await updateEmployeeStatus(row.id, targetEnabled)
    if (res.data && res.data.code === 0) {
      ElMessage.success((row.enabled === 1 ? '停职' : '恢复') + '成功')
      fetchList()
    } else {
      ElMessage.error(res.data.msg || '状态变更失败')
    }
  } catch (e) {
    console.error('状态变更失败:', e)
    ElMessage.error('状态变更失败')
  }
}

// 重置密码
async function handleResetPassword(row) {
  try {
    await ElMessageBox.confirm(`确认重置员工【${row.name}】的密码吗？`, '提示', { type: 'warning' })
    const res = await resetEmployeePassword(row.id)
    if (res.data && res.data.code === 0) {
      ElMessage.success('密码重置成功')
    } else {
      ElMessage.error(res.data.msg || '密码重置失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error('密码重置失败:', e)
      ElMessage.error('密码重置失败')
    }
  }
}

// 删除
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除员工【${row.name}】吗？`, '提示', { type: 'warning' })
    const res = await deleteEmployee(row.id)
    if (res.data && res.data.code === 0) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.data.msg || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败:', e)
      ElMessage.error('删除失败')
    }
  }
}

// 页面加载
onMounted(fetchList)
</script>

<style scoped>
.el-pagination {
  justify-content: flex-end;
  margin-top: 16px;
}
</style>