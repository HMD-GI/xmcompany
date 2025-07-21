<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="物料名称">
        <el-input v-model="query.materialName" placeholder="输入物料名称" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
    </el-form>
    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="materialName" label="物料名称" />
      <el-table-column prop="unit" label="单位" />
      <el-table-column prop="quantity" label="库存数量" />
      <el-table-column prop="lastStockInTime" label="最后入库" />
      <el-table-column prop="lastStockOutTime" label="最后出库" />
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
import { getStockList } from '@/api/stock'

const query = ref({ currentPage: 1, pageSize: 10, materialName: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)

function fetchList() {
  loading.value = true
  getStockList(query.value).then(res => {
    // 兼容后端未返回data或list字段的情况，防止报错
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

onMounted(fetchList)
</script> 