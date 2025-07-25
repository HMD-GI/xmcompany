<template>
  <el-card class="sale-card">
    <div class="card-header">
      <h2 class="card-title">销售管理</h2>
      <el-button type="success" @click="openAdd" icon="el-icon-plus">新增订单</el-button>
    </div>
    
    <el-form :inline="true" @submit.prevent="fetchList" class="search-form">
      <el-form-item label="订单编号">
        <el-input v-model="query.orderNo" placeholder="输入订单编号" clearable />
      </el-form-item>
      <el-form-item label="客户名称">
        <el-input v-model="query.customerName" placeholder="输入客户名称" clearable />
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select v-model="query.status" placeholder="选择状态" clearable>
          <el-option label="待确认" value="待确认" />
          <el-option label="已确认" value="已确认" />
          <el-option label="生产中" value="生产中" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已取消" value="已取消" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="fetchList" icon="el-icon-search">查询</el-button>
      <el-button @click="resetQuery" icon="el-icon-refresh">重置</el-button>
    </el-form>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单编号" width="150" />
      <el-table-column prop="customerName" label="客户名称" />
      <el-table-column prop="productName" label="产品名称" />
      <el-table-column prop="quantity" label="数量" />
      <el-table-column prop="totalAmount" label="总金额">
        <template #default="scope">
          ￥{{ scope.row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="orderDate" label="订单日期" />
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button size="small" @click="openDetail(scope.row)">详情</el-button>
          <el-button size="small" type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="warning" @click="updateStatus(scope.row)">状态</el-button>
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
    
    <SaleDetailDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :order="currentOrder"
      @refresh="fetchList"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSaleOrderList, deleteSaleOrder, updateSaleOrderStatus } from '@/api/sale'
import SaleDetailDialog from './SaleDetailDialog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = ref({ 
  currentPage: 1, 
  pageSize: 10, 
  orderNo: '', 
  customerName: '', 
  status: '' 
})
const list = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogMode = ref('detail')
const currentOrder = ref(null)

function fetchList() {
  loading.value = true
  getSaleOrderList(query.value).then(res => {
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

function resetQuery() {
  query.value.orderNo = ''
  query.value.customerName = ''
  query.value.status = ''
  fetchList()
}

function getStatusType(status) {
  const types = {
    '待确认': 'warning',
    '已确认': 'primary',
    '生产中': 'info',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return types[status] || 'info'
}

function openAdd() {
  dialogMode.value = 'add'
  currentOrder.value = null
  dialogVisible.value = true
}

function openEdit(row) {
  dialogMode.value = 'edit'
  currentOrder.value = { ...row }
  dialogVisible.value = true
}

function openDetail(row) {
  dialogMode.value = 'detail'
  currentOrder.value = { ...row }
  dialogVisible.value = true
}

function updateStatus(row) {
  ElMessageBox.prompt('请选择新状态', '更新订单状态', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'select',
    inputOptions: [
      { value: '待确认', label: '待确认' },
      { value: '已确认', label: '已确认' },
      { value: '生产中', label: '生产中' },
      { value: '已完成', label: '已完成' },
      { value: '已取消', label: '已取消' }
    ]
  }).then(({ value }) => {
    updateSaleOrderStatus({ id: row.id, status: value }).then(() => {
      ElMessage.success('状态更新成功')
      fetchList()
    })
  })
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除这个订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteSaleOrder(row.id).then(() => {
      ElMessage.success('删除成功')
      fetchList()
    })
  })
}

onMounted(fetchList)
</script>

<style scoped>
.sale-card {
  margin: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>