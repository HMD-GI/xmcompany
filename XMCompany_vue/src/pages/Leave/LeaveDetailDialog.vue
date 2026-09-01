<template>
  <el-dialog
    title="请假详情"
    :model-value="modelValue"
    @update:model-value="updateVisible"
    width="500px"
  >
    <div class="detail-container">
      <div class="detail-item">
        <span class="detail-label">员工姓名：</span>
        <span class="detail-content">{{ detail.employeeName || '无' }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">请假类型：</span>
        <span class="detail-content">{{ getLeaveTypeText(detail.leaveType) }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">开始时间：</span>
        <span class="detail-content">{{ formatTime(detail.startTime) }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">结束时间：</span>
        <span class="detail-content">{{ formatTime(detail.endTime) }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">员工ID：</span>
        <span class="detail-content">{{ detail.employeeId || '无' }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">请假事由：</span>
        <span class="detail-content">{{ detail.reason || '无' }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">状态：</span>
        <span class="detail-content">{{ getStatusText(detail.status) }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">审核人：</span>
        <span class="detail-content">{{ detail.reviewerName || '无' }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">审核意见：</span>
        <span class="detail-content">{{ detail.reviewComment || '无' }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">审核时间：</span>
        <span class="detail-content">{{ formatTime(detail.reviewTime) }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">创建时间：</span>
        <span class="detail-content">{{ formatTime(detail.createTime) }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">更新时间：</span>
        <span class="detail-content">{{ formatTime(detail.updateTime) }}</span>
      </div>
    </div>
    <template #footer>
      <el-button @click="close">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
// 引入dayjs用于时间格式化
import dayjs from 'dayjs'
import { computed } from 'vue'

// 定义props
const props = defineProps({
  modelValue: Boolean, // 控制弹窗显示
  detail: Object       // 详情数据对象
})
const emit = defineEmits(['update:modelValue'])

// 更新弹窗显示状态
function updateVisible(value) {
  emit('update:modelValue', value)
}

// 关闭弹窗，通知父组件
function close() {
  emit('update:modelValue', false)
}

// 时间格式化函数
function formatTime(val) {
  // 如果val为空直接返回空字符串
  if (!val) return '无'
  // 格式化为YYYY-MM-DD HH:mm
  return dayjs(val).format('YYYY-MM-DD HH:mm')
}

// 根据status显示状态描述
function getStatusText(status) {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已批准'
    case 2: return '已拒绝'
    case 3: return '已取消'
    default: return '未知状态'
  }
}

// 根据leaveType显示请假类型
function getLeaveTypeText(type) {
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
</script>

<style scoped>
.detail-container {
  padding: 10px;
}

.detail-item {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
}

.detail-label {
  width: 100px;
  color: #606266;
  font-weight: 500;
  flex-shrink: 0;
}

.detail-content {
  color: #303133;
  flex: 1;
  word-break: break-all;
}
</style>