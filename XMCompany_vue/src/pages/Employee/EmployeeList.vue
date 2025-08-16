<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="姓名">
        <el-input v-model="query.name" placeholder="输入员工姓名" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
    </el-form>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="id" label="ID" min-width="80" />
      <el-table-column prop="name" label="姓名" min-width="120" />
      <el-table-column prop="gender" label="性别" min-width="80">
        <template #default="{ row }">
          {{ row.gender === 'M' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="role" label="职位" min-width="120">
        <template #default="{ row }">
          {{ row.role === 'ADMIN' ? '管理者' : '员工' }}
        </template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'danger'">
            {{ row.enabled ? '在职' : '停职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="info" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button 
            :type="row.enabled ? 'danger' : 'success'" 
            size="small" 
            @click="handleStatusChange(row)"
          >
            {{ row.enabled ? '停职' : '恢复' }}
          </el-button>
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
import { getEmployeeList } from '@/api/employee'
import { ElMessage } from 'element-plus'

// 查询参数
const query = ref({
  currentPage: 1,
  pageSize: 10,
  name: ''
})

// 数据列表
const list = ref([])
const total = ref(0)
const loading = ref(false)

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

// 处理详情查看
function handleDetail(row) {
  ElMessage.info('查看员工详情：' + row.name)
  // TODO: 实现详情查看功能
  console.log('查看员工详情:', row)
}

// 处理编辑
function handleEdit(row) {
  // TODO: 实现编辑功能
  console.log('编辑员工:', row)
}

// 处理状态变更
function handleStatusChange(row) {
  const actionText = row.enabled ? '停职' : '恢复'
  ElMessage.success(`${actionText}操作成功：${row.name}`)
  // TODO: 实现状态变更功能
  console.log('更改员工状态:', row)
}

// 页面加载时获取列表
onMounted(fetchList)
</script>

<style scoped>
.el-pagination {
  justify-content: flex-end;
  margin-top: 16px;
}
</style>