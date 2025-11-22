<template>
  <el-card class="customer-card">
    <div class="card-header">
      <h2 class="card-title">客户管理</h2>
      <el-button type="success" @click="openAdd" icon="el-icon-plus">新增客户</el-button>
    </div>
    
    <el-form :inline="true" @submit.prevent="fetchList" class="search-form">
      <el-form-item label="客户名称">
        <el-input v-model="query.name" placeholder="输入客户名称" clearable />
      </el-form-item>
      <el-form-item label="联系人">
        <el-input v-model="query.contactPerson" placeholder="输入联系人"  clearable />
      </el-form-item>
      <el-form-item label="客户来源">
        <el-input v-model="query.source" placeholder="输入客户来源"  clearable />
      </el-form-item>
      <el-form-item label="客户等级">
        <el-select v-model="query.level" placeholder="选择客户等级"  style="width: 200px" clearable>
          <el-option label="1" value="1" />
          <el-option label="2" value="2" />
          <el-option label="3" value="3" />
          <el-option label="4" value="4" />
          <el-option label="5" value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="客户状态">
        <el-select v-model="query.status" placeholder="选择客户状态" style="width: 200px" clearable>
          <el-option label="潜在客户" value="1" />
          <el-option label="意向客户" value="2" />
          <el-option label="VIP客户" value="3" />
          <el-option label="已成交客户" value="4" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="fetchList" icon="el-icon-search">查询</el-button>
      <el-button @click="resetQuery" icon="el-icon-refresh">重置</el-button>
    </el-form>
    
    <el-table 
      :data="list" 
      style="width: 100%" 
      v-loading="loading"
      border
      stripe
      highlight-current-row
      class="customer-table">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="客户名称" min-width="120" />
      <el-table-column prop="contactPerson" label="联系人" min-width="100" />
      <el-table-column prop="phone" label="联系电话" min-width="120" />
      <el-table-column prop="source" label="客户来源" min-width="120" />
      <el-table-column prop="level" label="客户等级" min-width="120" />
      <el-table-column prop="status" label="客户状态" min-width="120">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openDetail(scope.row)">详情</el-button>
          <el-button size="small" type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
      class="pagination"
    />
    
    <!-- 客户详情/编辑/新增弹窗 -->
    <CustomerDetailDialog
      v-model="dialogVisible"
      :mode="dialogMode"
      :customer="currentCustomer"
      @refresh="fetchList"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCustomerList, deleteCustomer, getCustomerById } from '@/api/customer'
import CustomerDetailDialog from './CustomerDetailDialog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = ref({ currentPage: 1, pageSize: 10, name: '', contactPerson: '', source: '', level: null, status: null })
const list = ref([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogMode = ref('detail') // add | edit | detail
const currentCustomer = ref(null)



// 客户状态文本映射
function getStatusText(status) {
  const statusMap = {
    1: '潜在客户',
    2: '意向客户',
    3: 'VIP客户', 
    4: '已成交客户'
  }
  return statusMap[status] || '未知'
}

// 客户状态标签类型
function getStatusTagType(status) {
  const typeMap = {
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'primary'
  }
  return typeMap[status] || 'info'
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getCustomerList(query.value)
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      list.value = data.list || []
      total.value = data.total || 0
    } else {
      ElMessage.error(res.data?.msg || '获取客户列表失败')
    }
  } catch (error) {
    console.error('获取客户列表失败:', error)
    ElMessage.error('获取客户列表失败')
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.value.name = ''
  query.value.contactPerson = ''
  query.value.source = ''
  query.value.level = null
  query.value.status = null
  fetchList()
}

function openAdd() {
  dialogMode.value = 'add'
  currentCustomer.value = null
  dialogVisible.value = true
}

// 编辑 - 先获取详情再编辑
async function openEdit(row) {
  dialogMode.value = 'edit'
  try {
    const res = await getCustomerById(row.id)
    if (res.data && res.data.code === 0) {
      currentCustomer.value = res.data.data || row
    } else {
      ElMessage.error(res.data?.msg || '获取客户详情失败')
      currentCustomer.value = row
    }
  } catch (error) {
    console.error('获取客户详情失败:', error)
    ElMessage.error('获取客户详情失败')
    currentCustomer.value = row
  }
  dialogVisible.value = true
}

// 详情 - 获取完整客户信息
async function openDetail(row) {
  dialogMode.value = 'detail'
  try {
    const res = await getCustomerById(row.id)
    if (res.data && res.data.code === 0) {
      currentCustomer.value = res.data.data || row
    } else {
      ElMessage.error(res.data?.msg || '获取客户详情失败')
      currentCustomer.value = row
    }
  } catch (error) {
    console.error('获取客户详情失败:', error)
    ElMessage.error('获取客户详情失败')
    currentCustomer.value = row
  }
  dialogVisible.value = true
}

// 删除 - 完善错误处理
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除客户 ${row.name} 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const res = await deleteCustomer(row.id)
    if (res.data && res.data.code === 0) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

onMounted(fetchList)
</script>

<style scoped>
.customer-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  color: #303133;
  margin: 0;
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}

.customer-table {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>