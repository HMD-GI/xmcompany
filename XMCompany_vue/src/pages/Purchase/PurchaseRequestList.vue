<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="物料名称">
        <el-input v-model="query.itemName" placeholder="输入物料名称" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="选择状态" style="width: 200px" clearable>
          <el-option label="草稿" :value="0" />
          <el-option label="审批中" :value="1" />
          <el-option label="已通过" :value="2" />
          <el-option label="已驳回" :value="3" />
          <el-option label="已撤回" :value="4" />
          <el-option label="已完成" :value="5" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="handleAdd">新增申请</el-button>
    </el-form>

    <!-- 申请详情/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
    >
      <el-form :model="form" label-width="100px" :disabled="dialogMode === 'detail'" ref="formRef">
        <el-form-item label="物料名称" prop="itemName" :rules="[{ required: true, message: '请输入物料名称', trigger: 'blur' }]">
          <el-input v-model="form.itemName" placeholder="请输入物料名称" />
        </el-form-item>
        <el-form-item label="单位" prop="unit" :rules="[{ required: true, message: '请输入单位', trigger: 'blur' }]">
          <el-input v-model="form.unit" placeholder="请输入单位（如：个、件、kg等）" />
        </el-form-item>
        <el-form-item label="申请数量" prop="quantity" :rules="[{ required: true, message: '请输入申请数量', trigger: 'blur' }]">
          <el-input-number v-model="form.quantity" :min="1" placeholder="请输入数量" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预算金额" prop="budget" :rules="[{ required: true, message: '请输入预算金额', trigger: 'blur' }]">
          <el-input-number v-model="form.budget" :min="0" :precision="2" placeholder="请输入预算金额" style="width: 100%" />
        </el-form-item>
        <el-form-item label="申请人" prop="applicant" :rules="[{ required: true, message: '请输入申请人', trigger: 'blur' }]" v-if="dialogMode === 'add' || dialogMode === 'edit'">
          <el-input v-model="form.applicant" placeholder="请输入申请人姓名" />
        </el-form-item>
        <el-form-item label="申请原因" prop="remark">
          <el-input v-model="form.remark" type="textarea" rows="3" placeholder="请输入申请原因" />
        </el-form-item>
        <el-form-item v-if="dialogMode === 'detail'" label="申请人">
          <el-input v-model="form.applicant" disabled />
        </el-form-item>
        <el-form-item v-if="dialogMode === 'detail'" label="申请状态">
          <el-tag :type="getStatusType(form.status)">{{ form.statusName || getStatusText(form.status) }}</el-tag>
        </el-form-item>
        <el-form-item v-if="dialogMode === 'detail'" label="申请时间">
          <el-input :value="formatTime(form.createTime)" disabled />
        </el-form-item>
        <el-form-item v-if="dialogMode === 'detail'" label="更新时间">
          <el-input :value="formatTime(form.updateTime)" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="dialogMode === 'add' || dialogMode === 'edit'" type="primary" @click="handleSubmit" :loading="saving">
            保存
          </el-button>
          <el-button v-if="dialogMode === 'add' || (dialogMode === 'edit' && form.status === 0)" type="success" @click="handleSubmitAndApprove" :loading="saving">
            提交审批
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column label="序号" width="80" align="center">
        <template #default="{ $index }">
          {{ $index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="applicant" label="申请人" width="150" />
      <el-table-column prop="itemName" label="物料名称" width="180" />
      <el-table-column prop="quantity" label="申请数量" width="140" align="center" />
      <el-table-column prop="unit" label="单位" width="100" align="center" />
      <el-table-column label="状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ row.statusName || getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="warning" size="small" @click="handleEdit(row)" :disabled="row.status !== 0">编辑</el-button>
          <el-button type="success" size="small" @click="handleApprove(row, 2)" :disabled="row.status !== 1">通过</el-button>
          <el-button type="danger" size="small" @click="handleApprove(row, 3)" :disabled="row.status !== 1">驳回</el-button>
          <el-button type="info" size="small" @click="handleWithdraw(row)" :disabled="row.status !== 1">撤回</el-button>
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
  addPurchaseRequest, 
  updatePurchaseRequest, 
  withdrawPurchaseRequest,
  updatePurchaseRequestStatus,
  getPurchaseRequestById, 
  getPurchaseRequestList
} from '@/api/purchase'

const query = ref({ currentPage: 1, pageSize: 10, itemName: '', status: '' })
const list = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('detail') // 'detail' | 'add' | 'edit'
const formRef = ref()
const form = ref({
  id: null,
  applicant: '',
  itemName: '',
  unit: '',
  quantity: 1,
  budget: 0,
  status: 0,
  statusName: '',
  remark: '',
  createTime: '',
  updateTime: ''
})

const dialogTitle = ref('申请详情')

// 格式化时间显示
function formatTime(timeStr) {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ')
}

// 状态文本映射
function getStatusText(status) {
  if (status === null || status === undefined) return '未设置'
  
  const statusMap = {
    0: '草稿',
    1: '审批中',
    2: '已通过',
    3: '已驳回',
    4: '已撤回',
    5: '已完成'
  }
  return statusMap[status] !== undefined ? statusMap[status] : `未知状态(${status})`
}

// 状态类型映射
function getStatusType(status) {
  if (status === null || status === undefined) return ''
  
  const typeMap = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger',
    4: 'info',
    5: 'success'
  }
  return typeMap[status] !== undefined ? typeMap[status] : 'info'
}

// 处理查看详情
async function handleDetail(row) {
  dialogMode.value = 'detail'
  dialogTitle.value = '申请详情'
  try {
    const res = await getPurchaseRequestById(row.id)
    if (res.data && res.data.code === 0) {
      form.value = res.data.data || row
    } else {
      ElMessage.error(res.data?.msg || '获取申请详情失败')
      form.value = row
    }
  } catch (error) {
    console.error('获取申请详情失败:', error)
    ElMessage.error('获取申请详情失败')
    form.value = row
  }
  dialogVisible.value = true
}

// 处理编辑
async function handleEdit(row) {
  dialogMode.value = 'edit'
  dialogTitle.value = '编辑申请'
  try {
    const res = await getPurchaseRequestById(row.id)
    if (res.data && res.data.code === 0) {
      form.value = res.data.data || row
    } else {
      ElMessage.error(res.data?.msg || '获取申请详情失败')
      form.value = row
    }
  } catch (error) {
    console.error('获取申请详情失败:', error)
    ElMessage.error('获取申请详情失败')
    form.value = row
  }
  dialogVisible.value = true
}

// 处理新增
function handleAdd() {
  dialogMode.value = 'add'
  dialogTitle.value = '新增申请'
  form.value = {
    id: null,
    applicant: '',
    itemName: '',
    unit: '',
    quantity: 1,
    budget: 0,
    status: 0,
    statusName: '',
    remark: '',
    createTime: '',
    updateTime: ''
  }
  dialogVisible.value = true
}

// 处理审批
async function handleApprove(row, status) {
  const statusText = getStatusText(status)
  try {
    await ElMessageBox.confirm(`确认${statusText}该申请吗？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await updatePurchaseRequestStatus({
      id: row.id,
      status: status
    })
    
    if (res.data && res.data.code === 0) {
      ElMessage.success(`申请已${statusText}`)
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '审批操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审批操作失败:', error)
      ElMessage.error('审批操作失败')
    }
  }
}

// 处理撤回
async function handleWithdraw(row) {
  try {
    await ElMessageBox.confirm('确认撤回该申请吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await withdrawPurchaseRequest(row.id)
    if (res.data && res.data.code === 0) {
      ElMessage.success('申请已撤回')
      fetchList()
    } else {
      ElMessage.error(res.data?.msg || '撤回操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤回操作失败:', error)
      ElMessage.error('撤回操作失败')
    }
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
    let res
    if (dialogMode.value === 'add') {
      // 新增时使用指定的参数结构
      const data = {
        itemName: form.value.itemName,
        unit: form.value.unit,
        quantity: form.value.quantity,
        budget: form.value.budget,
        applicant: form.value.applicant,
        remark: form.value.remark
      }
      res = await addPurchaseRequest(data)
    } else {
      // 编辑时传递完整的表单数据
      res = await updatePurchaseRequest(form.value)
    }
    
    if (res.data && res.data.code === 0) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      fetchList() // 刷新列表
    } else {
      ElMessage.error(res.data?.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 保存并提交审批
async function handleSubmitAndApprove() {
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
    let res
    if (dialogMode.value === 'add') {
      // 新增时使用指定的参数结构
      const data = {
        itemName: form.value.itemName,
        unit: form.value.unit,
        quantity: form.value.quantity,
        budget: form.value.budget,
        applicant: form.value.applicant,
        remark: form.value.remark
      }
      res = await addPurchaseRequest(data)
    } else {
      // 编辑时传递完整的表单数据
      res = await updatePurchaseRequest(form.value)
    }
    
    if (res.data && res.data.code === 0) {
      // 确保ID是数字类型
      const id = Number(res.data.data) || Number(form.value.id)
      if (id && !isNaN(id)) {
        const approveRes = await updatePurchaseRequestStatus({
          id: id,
          status: 1 // 审批中
        })
        
        if (approveRes.data && approveRes.data.code === 0) {
          ElMessage.success('已提交审批')
          dialogVisible.value = false
          fetchList() // 刷新列表
        } else {
          ElMessage.error(approveRes.data?.msg || '提交审批失败')
        }
      }
    } else {
      ElMessage.error(res.data?.msg || '保存失败')
    }
  } catch (error) {
    console.error('提交审批失败:', error)
    ElMessage.error('提交审批失败')
  } finally {
    saving.value = false
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getPurchaseRequestList(query.value)
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      list.value = data.list || []
      total.value = data.total || 0
    } else {
      ElMessage.error(res.data?.msg || '获取申请列表失败')
    }
  } catch (error) {
    console.error('获取申请列表失败:', error)
    ElMessage.error('获取申请列表失败')
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