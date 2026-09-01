<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="项目名称">
        <el-input v-model="query.projectName" placeholder="输入项目名称" />
      </el-form-item>
      <el-form-item label="操作人">
        <el-input v-model="query.operatorName" placeholder="输入操作人姓名" />
      </el-form-item>
      <el-form-item label="项目">
        <el-select v-model="query.projectId" placeholder="选择项目" style="width: 200px" clearable filterable>
          <el-option
            v-for="project in projectList"
            :key="project.id"
            :label="project.projectName"
            :value="project.id"
          />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openUpdateDialog">更新生产进度</el-button>
    </el-form>

    <!-- 进度更新对话框 -->
    <el-dialog
      title="更新生产进度"
      v-model="updateDialogVisible"
      width="500px"
    >
      <el-form :model="updateForm" label-width="100px" ref="updateFormRef">
        <el-form-item label="项目" prop="projectId" :rules="[{ required: true, message: '请选择项目', trigger: 'change' }]">
          <el-select v-model="updateForm.projectId" placeholder="请选择项目" style="width: 100%" filterable>
            <el-option
              v-for="project in projectList"
              :key="project.id"
              :label="project.projectName"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="新增数量" prop="quantity" :rules="[{ required: true, message: '请输入新增数量', trigger: 'blur' }]">
          <el-input-number v-model="updateForm.quantity" :min="1" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="updateForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="updateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpdate" :loading="updating">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 进度详情对话框 -->
    <el-dialog
      title="进度详情"
      v-model="detailDialogVisible"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ detailData.projectName }}</el-descriptions-item>
        <el-descriptions-item label="生产数量">{{ detailData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="更新前数量">{{ detailData.beforeQuantity }}</el-descriptions-item>
        <el-descriptions-item label="更新后数量">{{ detailData.afterQuantity }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatTime(detailData.operationTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column label="序号" width="80" align="center">
        <template #default="{ $index }">
          {{ $index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="projectName" label="项目名称" width="150" />
      <el-table-column prop="quantity" label="生产数量" width="150" align="right" />
      <el-table-column prop="beforeQuantity" label="更新前数量" width="150" align="right" />
      <el-table-column prop="afterQuantity" label="更新后数量" width="150" align="right" />
      <el-table-column prop="operatorName" label="操作人" width="120" />
      <el-table-column prop="operationTime" label="操作时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.operationTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getProgressList,
  getProgressById,
  getProjectList,
  updateProgress
} from '@/api/production'

const query = ref({ 
  currentPage: 1, 
  pageSize: 10, 
  projectName: '', 
  operatorName: '',
  projectId: null
})

const list = ref([])
const total = ref(0)
const loading = ref(false)
const detailDialogVisible = ref(false)
const detailData = ref({})

// 更新进度相关
const updateDialogVisible = ref(false)
const updateFormRef = ref()
const updateForm = ref({
  projectId: null,
  quantity: 1,
  remark: ''
})
const updating = ref(false)

// 项目列表
const projectList = ref([])

// 格式化时间显示
function formatTime(timeStr) {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ')
}

// 打开更新进度对话框
function openUpdateDialog() {
  updateForm.value = {
    projectId: null,
    quantity: 1,
    remark: ''
  }
  updateDialogVisible.value = true
}

// 提交更新进度
async function submitUpdate() {
  try {
    const valid = await updateFormRef.value.validate()
    if (!valid) return
    
    updating.value = true
    const res = await updateProgress(updateForm.value)
    if (res.data && res.data.code === 0) {
      ElMessage.success('生产进度更新成功')
      updateDialogVisible.value = false
      fetchList() // 刷新列表
    } else {
      ElMessage.error(res.data?.msg || '更新失败')
    }
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败')
  } finally {
    updating.value = false
  }
}

// 处理查看详情
async function handleDetail(row) {
  try {
    const res = await getProgressById(row.id)
    if (res.data && res.data.code === 0) {
      detailData.value = res.data.data || row
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.data?.msg || '获取进度详情失败')
    }
  } catch (error) {
    console.error('获取进度详情失败:', error)
    ElMessage.error('获取进度详情失败')
  }
}

// 获取项目列表
async function fetchProjectList() {
  try {
    const res = await getProjectList({ currentPage: 1, pageSize: 1000 })
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [] }
      projectList.value = data.list || []
    } else {
      ElMessage.error(res.data?.msg || '获取项目列表失败')
    }
  } catch (error) {
    console.error('获取项目列表失败:', error)
    ElMessage.error('获取项目列表失败')
  }
}

// 获取进度列表
async function fetchList() {
  loading.value = true
  try {
    const res = await getProgressList(query.value)
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      list.value = data.list || []
      total.value = data.total || 0
    } else {
      ElMessage.error(res.data?.msg || '获取进度列表失败')
    }
  } catch (error) {
    console.error('获取进度列表失败:', error)
    ElMessage.error('获取进度列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchList()
  fetchProjectList()
})


</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>