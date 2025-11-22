<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="员工姓名">
        <el-input v-model="query.employeeName" placeholder="输入员工姓名" />
      </el-form-item>
      <el-form-item label="薪资月份">
        <el-input v-model="query.payrollMonth" placeholder="2024-06" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
    </el-form>

    <!-- 全部发放按钮 -->
    <el-button type="success" @click="releaseAllSalaries" style="margin-bottom: 16px;">全部发放</el-button>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="employeeName" label="员工姓名" />
      <el-table-column prop="payrollMonth" label="薪资月份" />
      <el-table-column prop="bankCardNo" label="银行卡号" />
      <el-table-column prop="actualAmount" label="实发金额" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <span>{{ formatStatus(row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详细</el-button>
          <el-button size="small" type="primary" @click="openEdit(row)">修改</el-button>
          <el-button size="small" type="success" @click="releaseSalary(row)">发放工资</el-button>
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
import { getEmployeePayrollList } from '@/api/salary'

const query = ref({ employeeName: '', payrollMonth: '', currentPage: 1, pageSize: 10 })
const list = ref([])
const total = ref(0)
const loading = ref(false)

function fetchList() {
  loading.value = true
  if (!query.value.employeeName) {
    // 如果未输入员工姓名，直接清空列表并返回
    list.value = []
    total.value = 0
    loading.value = false
    return
  }
  getEmployeePayrollList(query.value.employeeName, query.value).then(res => {
    // 兼容后端未返回data或list字段的情况，防止报错
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

function formatStatus(status) {
  const statusMap = {
    0: '待发放',
    1: '已发放',
    2: '已撤销'
  }
  return statusMap[status] || '未知状态'
}

// 显示详细信息
function openDetail(row) {
  console.log('查看详细信息', row)
  // 打开详细信息的弹窗或页面
}

// 编辑操作
function openEdit(row) {
  console.log('编辑操作', row)
  // 打开编辑弹窗，传递编辑数据
}

// 发放工资操作
function releaseSalary(row) {
  console.log('发放工资', row)
  // 调用API发放工资
}

// 批量发放工资
function releaseAllSalaries() {
  console.log('批量发放所有工资')
  // 调用批量发放工资的接口
  // 例如，可以将所有需要发放工资的员工 ID 收集起来，发起批量请求
}

onMounted(fetchList)
</script>
