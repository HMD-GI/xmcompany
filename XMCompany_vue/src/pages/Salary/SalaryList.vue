<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="员工ID">
        <el-input v-model="query.employeeId" placeholder="输入员工ID" />
      </el-form-item>
      <el-form-item label="月份">
        <el-input v-model="query.month" placeholder="2024-06" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
    </el-form>
    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="employeeId" label="员工ID" />
      <el-table-column prop="month" label="月份" />
      <el-table-column prop="baseSalary" label="基本工资" />
      <el-table-column prop="bonus" label="绩效奖金" />
      <el-table-column prop="allowance" label="补贴" />
      <el-table-column prop="total" label="总工资" />
      <el-table-column prop="status" label="状态" />
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
import { getEmployeePayrollList } from '@/api/salary'

const query = ref({ employeeId: '', month: '', currentPage: 1, pageSize: 10 })
const list = ref([])
const total = ref(0)
const loading = ref(false)

function fetchList() {
  loading.value = true
  if (!query.value.employeeId) {
    // 如果未输入员工ID，直接清空列表并返回
    list.value = []
    total.value = 0
    loading.value = false
    return
  }
  getEmployeePayrollList(query.value.employeeId, query.value).then(res => {
    // 兼容后端未返回data或list字段的情况，防止报错
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

onMounted(fetchList)
</script> 