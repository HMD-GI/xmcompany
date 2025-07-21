<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="员工姓名">
        <el-input v-model="query.name" placeholder="输入员工姓名" />
      </el-form-item>
      <el-form-item label="部门">
        <el-input v-model="query.department" placeholder="输入部门" />
      </el-form-item>
      <el-form-item label="职位">
        <el-input v-model="query.position" placeholder="输入职位" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
    </el-form>
    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="gender" label="性别" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="department" label="部门" />
      <el-table-column prop="position" label="职位" />
      <el-table-column prop="hireDate" label="入职日期" />
      <el-table-column prop="enabled" label="状态" />
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
import { getEmployeeList } from '@/api/employee'

const query = ref({ currentPage: 1, pageSize: 10, name: '', department: '', position: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)

function fetchList() {
  loading.value = true
  getEmployeeList(query.value).then(res => {
    // 兼容后端未返回data或list字段的情况，防止报错
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

onMounted(fetchList)
</script> 