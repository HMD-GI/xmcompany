<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="订单编号">
        <el-input v-model="query.orderNo" placeholder="输入订单编号" />
      </el-form-item>
      <el-form-item label="供应商ID">
        <el-input v-model="query.supplierId" placeholder="输入供应商ID" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openAdd">新增订单</el-button>
    </el-form>
    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="orderNo" label="订单编号" />
      <el-table-column prop="supplierId" label="供应商ID" />
      <el-table-column prop="orderDate" label="订单日期" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="totalAmount" label="总金额" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="openDetail(scope.row)">详情</el-button>
          <el-button size="small" type="primary" @click="openEdit(scope.row)">编辑</el-button>
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
    <!-- 弹窗 -->
    <PurchaseDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :order="currentOrder"
      @refresh="fetchList"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPurchaseOrderList } from '@/api/purchase'
import PurchaseDialog from './PurchaseDialog.vue'

const query = ref({ currentPage: 1, pageSize: 10, orderNo: '', supplierId: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogMode = ref('add') // add | edit | detail
const currentOrder = ref(null)

function fetchList() {
  loading.value = true
  getPurchaseOrderList(query.value).then(res => {
    console.log(res.data)
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
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

onMounted(fetchList)
</script> 