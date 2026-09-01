<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="供应商名称">
        <el-input v-model="query.name" placeholder="输入供应商名称" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="选择状态" style="width: 200px" clearable>
          <el-option label="合作中" :value="1" />
          <el-option label="已暂停" :value="2" />
          <el-option label="已终止" :value="3" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="handleAdd">新增供应商</el-button>
    </el-form>

    <!-- 供应商详情/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
    >
      <el-form :model="form" label-width="100px" :disabled="dialogMode === 'detail'" ref="formRef">
        <el-form-item label="供应商编号" prop="code" :rules="[{ required: true, message: '请输入供应商编号', trigger: 'blur' }]" v-if="dialogMode === 'add'">
          <el-input v-model="form.code" placeholder="请输入供应商编号（如：SP12345）" />
          <el-button type="text" @click="generateSupplierCode" style="margin-left: 8px;">自动生成</el-button>
        </el-form-item>
        <el-form-item label="供应商编号" v-if="dialogMode === 'edit' || dialogMode === 'detail'">
          <el-input v-model="form.code" disabled />
        </el-form-item>
        <el-form-item label="供应商名称" prop="name" :rules="[{ required: true, message: '请输入供应商名称', trigger: 'blur' }]">
          <el-input v-model="form.name" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson" :rules="[{ required: true, message: '请输入联系人', trigger: 'blur' }]">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone" :rules="[{ required: true, message: '请输入联系电话', trigger: 'blur' }]">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="产品范围" prop="productRange">
          <el-input v-model="form.productRange" type="textarea" rows="3" placeholder="请输入产品范围" />
        </el-form-item>
        <el-form-item label="银行账号" prop="bankAccount">
          <el-input v-model="form.bankAccount" placeholder="请输入银行账号" />
        </el-form-item>
        <el-form-item label="开户银行" prop="bankName">
          <el-input v-model="form.bankName" placeholder="请输入开户银行" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" rows="3" placeholder="请输入备注信息" />
        </el-form-item>
        <el-form-item v-if="dialogMode === 'detail'" label="状态">
          <el-tag :type="getStatusType(form.status)">
            {{ getStatusText(form.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item v-if="dialogMode === 'detail'" label="创建时间">
          <el-input :value="formatTime(form.createTime)" disabled />
        </el-form-item>
        <el-form-item v-if="dialogMode === 'detail'" label="更新时间">
          <el-input :value="formatTime(form.updateTime)" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="dialogMode !== 'detail'" type="primary" @click="handleSubmit" :loading="saving">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column label="序号" width="80" align="center">
        <template #default="{ $index }">
          {{ $index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="code" label="供应商编号" width="180" />
      <el-table-column prop="name" label="供应商名称" width="250" />
      <el-table-column prop="productRange" label="产品范围" width="200" show-overflow-tooltip />
      <el-table-column label="状态" width="130">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button 
            :type="getStatusActionType(row.status)" 
            size="small" 
            @click="handleStatusChange(row)">
            {{ getStatusActionText(row.status) }}
          </el-button>
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
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  addSupplier, 
  updateSupplier, 
  deleteSupplier, 
  getSupplierById, 
  getSupplierList,
  updateSupplierStatus
} from '@/api/supplier'

const query = ref({ currentPage: 1, pageSize: 10, name: '', status: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('detail') // 'detail' | 'add' | 'edit'
const formRef = ref()
const form = ref({
  id: null,
  code: '',
  name: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
  productRange: '',
  bankAccount: '',
  bankName: '',
  remark: '',
  status: 1
})

const dialogTitle = ref('供应商详情')

// 格式化时间显示
function formatTime(timeStr) {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ')
}

// 状态文本映射
function getStatusText(status) {
  const statusMap = {
    1: '合作中',
    2: '已暂停',
    3: '已终止'
  }
  return statusMap[status] || '未知状态'
}

// 状态类型映射
function getStatusType(status) {
  const typeMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return typeMap[status] || 'info'
}

// 状态操作文本
function getStatusActionText(status) {
  const actionMap = {
    1: '暂停合作',
    2: '恢复合作',
    3: '恢复合作'
  }
  return actionMap[status] || '更改状态'
}

// 状态操作类型
function getStatusActionType(status) {
  const typeMap = {
    1: 'warning',
    2: 'success',
    3: 'success'
  }
  return typeMap[status] || 'info'
}

// 处理查看详情
async function handleDetail(row) {
  dialogMode.value = 'detail'
  dialogTitle.value = '供应商详情'
  try {
    const res = await getSupplierById(row.id)
    if (res.data && res.data.code === 0) {
      form.value = res.data.data || row
    } else {
      ElMessage.error(res.data?.msg || '获取供应商详情失败')
      form.value = row
    }
  } catch (error) {
    console.error('获取供应商详情失败:', error)
    ElMessage.error('获取供应商详情失败')
    form.value = row
  }
  dialogVisible.value = true
}

// 处理编辑
async function handleEdit(row) {
  dialogMode.value = 'edit'
  dialogTitle.value = '编辑供应商'
  try {
    const res = await getSupplierById(row.id)
    if (res.data && res.data.code === 0) {
      form.value = res.data.data || row
    } else {
      ElMessage.error(res.data?.msg || '获取供应商详情失败')
      form.value = row
    }
  } catch (error) {
    console.error('获取供应商详情失败:', error)
    ElMessage.error('获取供应商详情失败')
    form.value = row
  }
  dialogVisible.value = true
}

// 生成供应商编号
function generateSupplierCode() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  
  form.value.code = `SP${year}${month}${day}${hours}${minutes}${seconds}`
}

// 处理新增
function handleAdd() {
  dialogMode.value = 'add'
  dialogTitle.value = '新增供应商'
  form.value = {
    id: null,
    code: '',
    name: '',
    contactPerson: '',
    phone: '',
    email: '',
    address: '',
    productRange: '',
    bankAccount: '',
    bankName: '',
    remark: '',
    status: 1
  }
  dialogVisible.value = true
}

// 处理状态变更
async function handleStatusChange(row) {
  let newStatus;
  let statusText;
  
  if (row.status === 1) {
    newStatus = 2; // 合作中 -> 已暂停
    statusText = '暂停合作';
  } else {
    newStatus = 1; // 已暂停/已终止 -> 合作中
    statusText = '恢复合作';
  }
  
  try {
    await ElMessageBox.confirm(`确认${statusText}吗？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await updateSupplierStatus({
      id: row.id,
      status: newStatus
    })
    
    if (res.data && res.data.code === 0) {
      ElMessage.success(`已${statusText}`)
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '状态更新失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('状态更新失败:', error)
      ElMessage.error('状态更新失败')
    }
  }
}

// 处理删除
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该供应商吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteSupplier(row.id)
    if (res.data && res.data.code === 0) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 处理提交
async function handleSubmit() {
  try {
    const valid = await formRef.value.validate()
    if (!valid) {
      return
    }
  } catch (error) {
    ElMessage.warning('请填写必要信息')
    return
  }
  
  saving.value = true
  try {
    let res
    if (dialogMode.value === 'add') {
      // 新增时使用指定的参数结构
      const data = {
        code: form.value.code,
        name: form.value.name,
        contactPerson: form.value.contactPerson,
        phone: form.value.phone,
        email: form.value.email,
        address: form.value.address,
        productRange: form.value.productRange,
        bankAccount: form.value.bankAccount,
        bankName: form.value.bankName,
        remark: form.value.remark
      }
      res = await addSupplier(data)
    } else {
      // 编辑时传递完整的表单数据
      res = await updateSupplier(form.value)
    }
    
    if (res.data && res.data.code === 0) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      fetchList() // 刷新列表
    } else {
      ElMessage.error(res.data?.msg || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getSupplierList(query.value)
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      list.value = data.list || []
      total.value = data.total || 0
    } else {
      ElMessage.error(res.data?.msg || '获取供应商列表失败')
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
    ElMessage.error('获取供应商列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)
</script>