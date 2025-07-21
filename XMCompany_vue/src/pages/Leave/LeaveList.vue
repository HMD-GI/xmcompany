<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="员工ID">
        <el-input v-model="query.employeeId" placeholder="输入员工ID" />
      </el-form-item>
      <el-form-item label="状态">
        <el-input v-model="query.status" placeholder="输入状态" />
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
    </el-form>
    <el-table :data="list" style="width: 100%" v-loading="loading">
      <!-- ID列 -->
      <el-table-column prop="id" label="ID" width="60" />
      <!-- 员工姓名 -->
      <el-table-column prop="employeeName" label="员工姓名" />
      <!-- 请假类型描述 -->
      <el-table-column prop="leaveTypeDesc" label="请假类型" />
      <!-- 开始时间，格式化显示 -->
      <el-table-column prop="startTime" label="开始时间">
        <template #default="scope">{{ dayjs(scope.row.startTime).format('YYYY-MM-DD HH:mm') }}</template>
      </el-table-column>
      <!-- 结束时间，格式化显示 -->
      <el-table-column prop="endTime" label="结束时间">
        <template #default="scope">{{ dayjs(scope.row.endTime).format('YYYY-MM-DD HH:mm') }}</template>
      </el-table-column>
      <!-- 请假天数 -->
      <el-table-column prop="days" label="天数" />
      <!-- 请假事由 -->
      <el-table-column prop="reason" label="请假事由" />
      <!-- 状态描述 -->
      <el-table-column prop="statusDesc" label="状态" />
      <!-- 审核人 -->
      <el-table-column prop="reviewerName" label="审核人" />
      <!-- 审核意见 -->
      <el-table-column prop="reviewComment" label="审核意见" />
      <!-- 审核时间，格式化显示 -->
      <el-table-column prop="reviewTime" label="审核时间">
        <template #default="scope">{{ dayjs(scope.row.reviewTime).format('YYYY-MM-DD HH:mm') }}</template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button size="small" @click="openDetail(scope.row)">详情</el-button>
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
    <!-- 详情弹窗组件 -->
    <LeaveDetailDialog v-model:visible="detailDialogVisible" :detail="currentDetail" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLeaveList } from '@/api/leave'
// 引入dayjs用于时间格式化
import dayjs from 'dayjs'
// 引入请假详情弹窗组件
import LeaveDetailDialog from './LeaveDetailDialog.vue'

const query = ref({ currentPage: 1, pageSize: 10, employeeId: '', status: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)

// 控制详情弹窗显示
const detailDialogVisible = ref(false)
// 当前选中的详情数据
const currentDetail = ref({})

function fetchList() {
  loading.value = true
  getLeaveList(query.value).then(res => {
    // 兼容后端未返回data或list字段的情况，防止报错
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

// 打开详情弹窗
function openDetail(row) {
  currentDetail.value = { ...row }
  detailDialogVisible.value = true
}

onMounted(fetchList)
</script> 