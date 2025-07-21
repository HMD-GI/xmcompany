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
        <el-input v-model="query.contactPerson" placeholder="输入联系人" clearable />
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
      <el-table-column prop="contactPhone" label="联系电话" min-width="120" />
      <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
      <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :customer="currentCustomer"
      @refresh="fetchList"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCustomerList, deleteCustomer } from '@/api/customer'
import CustomerDetailDialog from './CustomerDetailDialog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = ref({ currentPage: 1, pageSize: 10, name: '', contactPerson: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogMode = ref('detail') // add | edit | detail
const currentCustomer = ref(null)

function fetchList() {
  loading.value = true
  getCustomerList(query.value).then(res => {
    // 兼容后端未返回data或list字段的情况，防止报错
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

function resetQuery() {
  query.value.name = ''
  query.value.contactPerson = ''
  fetchList()
}

function openAdd() {
  dialogMode.value = 'add'
  currentCustomer.value = null
  dialogVisible.value = true
}

function openEdit(row) {
  dialogMode.value = 'edit'
  currentCustomer.value = { ...row }
  dialogVisible.value = true
}

function openDetail(row) {
  dialogMode.value = 'detail'
  currentCustomer.value = { ...row }
  dialogVisible.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(
    `确定要删除客户 ${row.name} 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      deleteCustomer(row.id).then(() => {
        ElMessage.success('删除成功')
        fetchList()
      }).catch(err => {
        console.error('删除失败:', err)
        ElMessage.error('删除失败，请重试')
      })
    })
    .catch(() => {
      // 用户取消删除操作
    })
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