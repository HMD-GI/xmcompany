<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="操作编号">
        <el-input v-model="query.operationNo" placeholder="输入操作编号" />
      </el-form-item>
      <el-form-item label="物料名称">
        <el-input v-model="query.materialName" placeholder="输入物料名称" />
      </el-form-item>
      <el-form-item label="操作类型">
        <el-select v-model="query.operationType" placeholder="选择操作类型" style="width: 200px" clearable>
          <el-option label="入库" value="0" />
          <el-option label="出库" value="1" />
          <el-option label="调整" value="2" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
    </el-form>

    <!-- 操作记录详情对话框 -->
    <el-dialog
      title="操作记录详情"
      v-model="dialogVisible"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作编号">{{ detail.operationNo }}</el-descriptions-item>
        <el-descriptions-item label="物料名称">{{ detail.materialName }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ detail.unit }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeTag(detail.operationType)">
            {{ detail.operationTypeName || getOperationTypeText(detail.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作数量">{{ detail.quantity }} {{ detail.unit }}</el-descriptions-item>
        <el-descriptions-item label="操作前库存">{{ detail.beforeQuantity }} {{ detail.unit }}</el-descriptions-item>
        <el-descriptions-item label="操作后库存">{{ detail.afterQuantity }} {{ detail.unit }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detail.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="关联订单号">{{ detail.relatedOrderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatTime(detail.operationTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 表格 -->
    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="operationNo" label="操作编号" width="300" />
      <el-table-column prop="materialName" label="物料名称" width="200" />
      <el-table-column label="操作类型" width="200">
        <template #default="{ row }">
          <el-tag :type="getOperationTypeTag(row.operationType)">
            {{ getOperationTypeText(row.operationType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="操作数量" width="200" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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
import { ElMessage } from 'element-plus'
import { getStockOperationById, getStockOperationList } from '@/api/stock'

const query = ref({ 
  currentPage: 1, 
  pageSize: 10, 
  operationNo: '', 
  materialName: '',
  operationType: ''
})
const list = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const detail = ref({})

// 格式化时间显示
function formatTime(timeStr) {
  if (!timeStr) return '-'
  // 将 2025-07-23T21:12:25 格式转换为 2025-07-23 21:12:25
  return timeStr.replace('T', ' ')
}

// 操作类型文本映射
function getOperationTypeText(type) {
  const typeMap = {
    0: '入库',
    1: '出库',
    2: '调整'
  }
  return typeMap[type] || '未知'
}

// 操作类型标签映射
function getOperationTypeTag(type) {
  const tagMap = {
    0: 'success',
    1: 'danger',
    2: 'warning'
  }
  return tagMap[type] || 'info'
}

// 处理查看详情
function handleDetail(row) {
  getStockOperationById(row.id).then(res => {
    if (res.data.code !== 0) {
      ElMessage.error(res.data.msg || '请求失败')
      return
    }
    detail.value = res.data.data || {}
    dialogVisible.value = true
  }).catch(error => {
    console.error('获取操作记录详情失败:', error)
    ElMessage.error('获取操作记录详情失败')
  })
}

function fetchList() {
  loading.value = true
  getStockOperationList(query.value).then(res => {
    if (res.data.code !== 0) {
      ElMessage.error(res.data.msg || '请求失败')
      list.value = []
      total.value = 0
      return
    }
    const data = res.data.data || { list: [], total: 0 }
    list.value = data.list || []
    total.value = data.total || 0
  }).finally(() => loading.value = false)
}

onMounted(fetchList)
</script>