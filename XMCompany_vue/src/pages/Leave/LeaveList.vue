<template>
  <el-card>
    <div class="card-header">
      <h2 class="card-title">请假管理</h2>
      <el-button type="success" @click="openApplyDialog" icon="el-icon-plus">申请请假</el-button>
    </div>
    
    <el-form :inline="true" @submit.prevent="fetchList" class="search-form">
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
      <!-- 请假类型 -->
      <el-table-column prop="leaveType" label="请假类型">
        <template #default="scope">{{ leaveTypeLabel(scope.row.leaveType) }}</template>
      </el-table-column>
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
      <!-- 状态描述 -->
      <el-table-column prop="status" label="状态">
        <template #default="scope">{{ statusLabel(scope.row.status) }}</template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="openDetail(scope.row)">详情</el-button>
          <!-- 审核按钮 -->
          <el-button size="small" type="primary" @click="openApproveDialog(scope.row)" v-if="scope.row.status === 0">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
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

    <!-- 审核弹窗 -->
    <el-dialog title="审核请假记录" :model-value="approveDialogVisible"
    @update:model-value="approveDialogVisible = $event"
    width="30%">
      <el-form :model="reviewData" ref="reviewForm" label-width="100px">
        <el-form-item label="审核状态">
          <el-radio-group v-model="reviewData.status">
            <el-radio :label="1">批准</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核人姓名">
          <el-input v-model="reviewData.reviewerName" placeholder="请输入审核人姓名" />
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input type="textarea" v-model="reviewData.reviewComment" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交审核</el-button>
      </div>
    </el-dialog>

    <!-- 申请请假弹窗 -->
    <el-dialog title="申请请假" v-model="applyDialogVisible" width="600px">
      <el-form :model="applyForm" ref="applyFormRef" label-width="100px" :rules="applyRules">
        <el-form-item label="员工ID" prop="employeeId">
          <el-input v-model="applyForm.employeeId" placeholder="请输入员工ID" />
        </el-form-item>
        <el-form-item label="请假类型" prop="leaveType">
          <el-select v-model="applyForm.leaveType" placeholder="请选择请假类型">
            <el-option label="事假" value="1" />
            <el-option label="病假" value="2" />
            <el-option label="年假" value="3" />
            <el-option label="调休" value="4" />
            <el-option label="婚假" value="5" />
            <el-option label="产假" value="6" />
            <el-option label="丧假" value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="applyForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="applyForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="请假事由" prop="reason">
          <el-input v-model="applyForm.reason" type="textarea" :rows="3" placeholder="请输入请假事由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗组件 -->
    <LeaveDetailDialog v-model="detailDialogVisible" :detail="currentDetail" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLeaveList, reviewLeave, getLeaveById, applyLeave } from '@/api/leave'
import dayjs from 'dayjs'
import LeaveDetailDialog from './LeaveDetailDialog.vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const query = ref({ currentPage: 1, pageSize: 10, employeeId: '', status: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)

// 控制详情弹窗显示
const detailDialogVisible = ref(false)
// 当前选中的详情数据
const currentDetail = ref({})

// 审核弹窗相关数据
const approveDialogVisible = ref(false)
const reviewData = ref({
  leaveId: null,
  status: 1,  // 默认为批准
  reviewerName: '',
  reviewComment: ''
})

// 申请请假弹窗相关数据
const applyDialogVisible = ref(false)
const applyFormRef = ref(null)
const applyForm = ref({
  employeeId: '',
  leaveType: '',
  startTime: '',
  endTime: '',
  reason: ''
})

// 申请表单验证规则
const applyRules = {
  employeeId: [
    { required: true, message: '请输入员工ID', trigger: 'blur' }
  ],
  leaveType: [
    { required: true, message: '请选择请假类型', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入请假事由', trigger: 'blur' }
  ]
}

function fetchList() {
  loading.value = true
  getLeaveList(query.value).then(res => {
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      list.value = data.list || []
      total.value = data.total || 0
    } else {
      ElMessage.error(res.data?.msg || '获取请假列表失败')
    }
  }).catch(error => {
    console.error('获取请假列表失败:', error)
    ElMessage.error('获取请假列表失败')
  }).finally(() => loading.value = false)
}

// 打开详情弹窗
async function openDetail(row) {
  try {
    const res = await getLeaveById(row.id)
    if (res.data && res.data.code === 0) {
      currentDetail.value = res.data.data || row
    } else {
      ElMessage.error(res.data?.msg || '获取请假详情失败')
      currentDetail.value = row
    }
  } catch (error) {
    console.error('获取请假详情失败:', error)
    ElMessage.error('获取请假详情失败')
    currentDetail.value = row
  }
  detailDialogVisible.value = true
}

// 打开审核弹窗
function openApproveDialog(row) {
  reviewData.value = {
    leaveId: row.id,
    status: 1,  // 默认为批准
    reviewerName: '',
    reviewComment: ''
  }
  approveDialogVisible.value = true
}

// 提交审核
async function submitReview() {
  // 校验审核数据
  if (!reviewData.value.reviewerName || !reviewData.value.reviewComment) {
    ElMessage.error('审核人姓名和审核意见不能为空')
    return
  }

  try {
    // 调用审核 API
    const res = await reviewLeave(reviewData.value)
    if (res.data && res.data.code === 0) {
      ElMessage.success('审核提交成功')
      approveDialogVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '审核提交失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核提交失败，请重试')
  }
}

// 打开申请请假弹窗
function openApplyDialog() {
  // 重置表单
  applyForm.value = {
    employeeId: '',
    leaveType: '',
    startTime: '',
    endTime: '',
    reason: ''
  }
  applyDialogVisible.value = true
}

// 提交申请请假
async function submitApply() {
  try {
    // 表单验证
    await applyFormRef.value.validate()
    
    // 调用申请 API
    const res = await applyLeave(applyForm.value)
    if (res.data && res.data.code === 0) {
      ElMessage.success('请假申请提交成功')
      applyDialogVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '申请提交失败')
    }
  } catch (error) {
    if (error !== false) { // 表单验证失败不显示错误
      console.error('申请失败:', error)
      ElMessage.error('申请提交失败，请重试')
    }
  }
}

// 根据status显示状态描述
function statusLabel(status) {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已批准'
    case 2: return '已拒绝'
    case 3: return '已取消'
    default: return '未知状态'
  }
}

// 根据leaveType显示请假类型
function leaveTypeLabel(type) {
  switch (type) {
    case '1': return '事假'
    case '2': return '病假'
    case '3': return '年假'
    case '4': return '调休'
    case '5': return '婚假'
    case '6': return '产假'
    case '7': return '丧假'
    default: return '未知类型'
  }
}

onMounted(fetchList)
</script>

<style scoped>
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
</style>
