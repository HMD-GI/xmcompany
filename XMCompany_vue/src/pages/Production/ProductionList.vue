<template>
  <el-card>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="生产项目" name="project">
        <el-form :inline="true" @submit.prevent="fetchList">
          <el-form-item label="项目名称">
            <el-input v-model="query.projectName" placeholder="输入项目名称" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="选择状态" style="width: 200px" clearable>
              <el-option label="进行中" value="进行中" />
              <el-option label="暂停" value="暂停" />
              <el-option label="已完成" value="已完成" />
              <el-option label="已取消" value="已取消" />
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="fetchList">查询</el-button>
          <el-button type="success" @click="handleAdd">新增项目</el-button>
        </el-form>

        <!-- 项目详情/编辑对话框 -->
        <el-dialog
          :title="dialogTitle"
          v-model="dialogVisible"
          width="700px"
        >
          <el-form :model="form" label-width="120px" :disabled="dialogMode === 'detail'" ref="formRef">
            <el-form-item label="项目名称" prop="projectName" :rules="[{ required: true, message: '请输入项目名称', trigger: 'blur' }]">
              <el-input v-model="form.projectName" placeholder="请输入项目名称" />
            </el-form-item>
            <el-form-item label="产品名称" prop="productName" :rules="[{ required: true, message: '请输入产品名称', trigger: 'blur' }]">
              <el-input v-model="form.productName" placeholder="请输入产品名称" />
            </el-form-item>
            <el-form-item label="单位" prop="unit" :rules="[{ required: true, message: '请输入单位', trigger: 'blur' }]">
              <el-input v-model="form.unit" placeholder="请输入单位（如：个、件、kg等）" />
            </el-form-item>
            <el-form-item label="目标数量" prop="targetQuantity" :rules="[{ required: true, message: '请输入目标数量', trigger: 'blur' }]">
              <el-input-number v-model="form.targetQuantity" :min="1" placeholder="目标数量" style="width: 100%" />
            </el-form-item>
            <el-form-item label="开始时间" prop="startTime" :rules="[{ required: true, message: '请选择开始时间', trigger: 'change' }]">
              <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择开始时间" style="width: 100%" />
            </el-form-item>
            <el-form-item label="预计结束时间" prop="expectedEndTime" :rules="[{ required: true, message: '请选择预计结束时间', trigger: 'change' }]">
              <el-date-picker v-model="form.expectedEndTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择预计结束时间" style="width: 100%" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" rows="3" placeholder="请输入备注" />
            </el-form-item>
            <el-form-item label="当前数量" prop="currentQuantity" v-if="dialogMode === 'detail'">
              <el-input-number v-model="form.currentQuantity" :min="0" disabled style="width: 100%" />
            </el-form-item>
            <el-form-item label="进度百分比" prop="progressRate" v-if="dialogMode === 'detail'">
              <el-input-number v-model="form.progressRate" :min="0" :max="100" :precision="1" disabled style="width: 100%" />
            </el-form-item>
            <el-form-item v-if="dialogMode === 'detail'" label="实际结束时间">
              <el-input :value="formatTime(form.actualEndTime)" disabled />
            </el-form-item>
            <el-form-item v-if="dialogMode === 'detail'" label="项目状态">
              <el-tag :type="getStatusType(form.status)">{{ form.status }}</el-tag>
            </el-form-item>
            <el-form-item v-if="dialogMode === 'detail'" label="创建时间">
              <el-input :value="formatTime(form.createTime)" disabled />
            </el-form-item>
            <el-form-item v-if="dialogMode === 'detail'" label="更新时间">
              <el-input :value="formatTime(form.updateTime)" disabled />
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button v-if="dialogMode !== 'detail'" type="primary" @click="handleSubmit" :loading="saving">
                确认
              </el-button>
            </span>
          </template>
        </el-dialog>

        <!-- 进度记录对话框 -->
        <el-dialog
          title="生产进度记录"
          v-model="progressDialogVisible"
          width="700px"
        >
          <el-form :model="progressForm" label-width="100px">
            <el-form-item label="完成数量">
              <el-input-number v-model="progressForm.quantity" :min="1" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="progressForm.remark" type="textarea" rows="3" />
            </el-form-item>
          </el-form>
          <el-table :data="progressList" style="width: 100%" v-loading="progressLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="quantity" label="完成数量" width="120" />
            <el-table-column prop="operationTime" label="完成日期" width="180">
              <template #default="{ row }">
                {{ formatTime(row.operationTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="200" />
          </el-table>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="progressDialogVisible = false">关闭</el-button>
              <el-button type="primary" @click="handleProgressSubmit" :loading="progressSaving">提交进度</el-button>
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
          <el-table-column prop="productName" label="产品名称" width="130" />
          <el-table-column prop="currentQuantity" label="当前数量" width="120" align="right" />
          <el-table-column prop="targetQuantity" label="目标数量" width="120" align="right" />
          <el-table-column prop="progressRate" label="进度" width="150" align="center">
            <template #default="{ row }">
              <el-progress :percentage="row.progressRate" :show-text="false" />
              <span style="font-size: 12px;">{{ row.progressRate }}%</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="150" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="320" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
              <el-button type="success" size="small" @click="handleProgress(row)" :disabled="row.status !== '进行中'">进度</el-button>
              <el-button 
                v-if="row.status === '进行中'" 
                type="warning" 
                size="small" 
                @click="handleStatusChange(row, '暂停')"
              >
                暂停
              </el-button>
              <el-button 
                v-else-if="row.status === '暂停'" 
                type="success" 
                size="small" 
                @click="handleStatusChange(row, '进行中')"
              >
                继续
              </el-button>
              <el-button type="success" size="small" @click="handleStatusChange(row, '已完成')" :disabled="row.status !== '进行中'">完成</el-button>
              <el-button type="danger" size="small" @click="handleStatusChange(row, '已取消')" :disabled="row.status === '已完成' || row.status === '已取消'">取消</el-button>
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
      </el-tab-pane>
      <el-tab-pane label="生产进度" name="progress">
        <ProductionProgressList />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  addProductionProject, 
  getProjectList, 
  getProjectById, 
  updateProjectStatus,
  updateProgress,
  getProgressList
} from '@/api/production'
import ProductionProgressList from './ProductionProgressList.vue'

const query = ref({ currentPage: 1, pageSize: 10, projectName: '', status: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('detail') // 'detail' | 'add' | 'edit'
const formRef = ref()
const form = ref({
  projectName: '',
  productName: '',
  unit: '',
  currentQuantity: 0,
  targetQuantity: 1,
  progressRate: 0,
  status: '进行中',
  startTime: '',
  expectedEndTime: '',
  actualEndTime: '',
  remark: '',
  createTime: '',
  updateTime: ''
})

// 进度相关
const progressDialogVisible = ref(false)
const progressForm = ref({
  projectId: null,
  quantity: 1,  // 改为与后端一致的字段名
  remark: ''
})
const progressList = ref([])
const progressLoading = ref(false)
const progressSaving = ref(false)
const currentProjectId = ref(null)

const dialogTitle = ref('项目详情')
const activeTab = ref('project')

// 处理标签页切换
function handleTabChange(tabName) {
  // 可以在这里添加标签页切换时的逻辑
  console.log('切换到标签页:', tabName)
}

// 格式化时间显示
function formatTime(timeStr) {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ')
}

// 状态类型映射
function getStatusType(status) {
  const typeMap = {
    '进行中': 'primary',
    '暂停': 'warning',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return typeMap[status] || 'info'
}

// 处理查看详情
async function handleDetail(row) {
  dialogMode.value = 'detail'
  dialogTitle.value = '项目详情'
  try {
    const res = await getProjectById(row.id)
    if (res.data && res.data.code === 0) {
      form.value = res.data.data || row
    } else {
      ElMessage.error(res.data?.msg || '获取项目详情失败')
      form.value = row
    }
  } catch (error) {
    console.error('获取项目详情失败:', error)
    ElMessage.error('获取项目详情失败')
    form.value = row
  }
  dialogVisible.value = true
}

// 处理新增
function handleAdd() {
  dialogMode.value = 'add'
  dialogTitle.value = '新增项目'
  form.value = {
    projectName: '',
    productName: '',
    unit: '',
    currentQuantity: 0,
    targetQuantity: 1,
    progressRate: 0,
    status: '进行中',
    startTime: '',
    expectedEndTime: '',
    actualEndTime: '',
    remark: '',
    createTime: '',
    updateTime: ''
  }
  dialogVisible.value = true
}

// 处理状态变更
async function handleStatusChange(row, status) {
  try {
    await ElMessageBox.confirm(`确认将项目状态修改为「${status}」吗？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await updateProjectStatus({
      projectId: row.id,
      status: status
    })
    
    if (res.data && res.data.code === 0) {
      ElMessage.success(`状态已更新为「${status}」`)
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '状态更新失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('状态更新失败:', error)
      ElMessage.error('状态更新失败')
    }
  }
}

// 处理进度记录
async function handleProgress(row) {
  currentProjectId.value = row.id
  progressForm.value = {
    projectId: row.id,
    quantity: 1,  // 改为与后端一致的字段名
    remark: ''
  }
  progressLoading.value = true
  try {
    const res = await getProgressList({ projectId: row.id, currentPage: 1, pageSize: 50 })
    if (res.data && res.data.code === 0) {
      progressList.value = res.data.data?.list || []
    } else {
      ElMessage.error(res.data?.msg || '获取进度记录失败')
    }
  } catch (error) {
    console.error('获取进度记录失败:', error)
    ElMessage.error('获取进度记录失败')
  } finally {
    progressLoading.value = false
  }
  progressDialogVisible.value = true
}

// 提交进度
async function handleProgressSubmit() {
  if (!progressForm.value.quantity) {
    ElMessage.warning('请输入完成数量')
    return
  }
  
  progressSaving.value = true
  try {
    const res = await updateProgress(progressForm.value)
    if (res.data && res.data.code === 0) {
      ElMessage.success('进度已更新')
      // 刷新进度列表
      const progressRes = await getProgressList({ projectId: currentProjectId.value, currentPage: 1, pageSize: 50 })
      if (progressRes.data && progressRes.data.code === 0) {
        progressList.value = progressRes.data.data?.list || []
      }
      // 刷新项目列表
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '进度更新失败')
    }
  } catch (error) {
    console.error('进度更新失败:', error)
    ElMessage.error('进度更新失败')
  } finally {
    progressSaving.value = false
  }
}

// 处理提交
async function handleSubmit() {
  try {
    const valid = await formRef.value.validate()
    if (!valid) {
      return
    }
  } catch (error) {
    ElMessage.warning('请填写必要信息')
    return
  }
  
  saving.value = true
  try {
    // 使用指定的参数结构
    const data = {
      projectName: form.value.projectName,
      targetQuantity: form.value.targetQuantity,
      productName: form.value.productName,
      unit: form.value.unit,
      startTime: form.value.startTime,
      expectedEndTime: form.value.expectedEndTime,
      remark: form.value.remark
    }
    
    const res = await addProductionProject(data)
    if (res.data && res.data.code === 0) {
      ElMessage.success('新增成功')
      dialogVisible.value = false
      fetchList() // 刷新列表
    } else {
      ElMessage.error(res.data?.msg || '新增失败')
    }
  } catch (error) {
    console.error('新增失败:', error)
    ElMessage.error('新增失败')
  } finally {
    saving.value = false
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getProjectList(query.value)
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      list.value = data.list || []
      total.value = data.total || 0
    } else {
      ElMessage.error(res.data?.msg || '获取项目列表失败')
    }
  } catch (error) {
    console.error('获取项目列表失败:', error)
    ElMessage.error('获取项目列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>