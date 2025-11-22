<template>
  <el-card>
    <el-form :inline="true" @submit.prevent="fetchList">
      <el-form-item label="订单编号">
        <el-input v-model="query.orderNo" placeholder="输入订单编号" />
      </el-form-item>
      <el-form-item label="供应商">
        <el-input v-model="query.supplierName" placeholder="输入供应商名称" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="选择状态" style="width: 200px" clearable>
          <el-option label="待确认" :value="0" />
          <el-option label="已确认" :value="1" />
          <el-option label="已下单" :value="2" />
          <el-option label="已到货" :value="3" />
          <el-option label="已完成" :value="4" />
          <el-option label="已作废" :value="5" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="handleAdd">新增订单</el-button>
    </el-form>

    <!-- 采购订单详情对话框 -->
    <el-dialog
      title="采购订单详情"
      v-model="detailVisible"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="订单编号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商名称">{{ detailData.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="供应商编号">{{ detailData.supplierCode }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ detailData.statusName || getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="物料名称">{{ detailData.itemName }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ detailData.unit }}</el-descriptions-item>
        <el-descriptions-item label="单价">￥{{ detailData.unitPrice }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detailData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="总金额">￥{{ detailData.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detailData.applicant }}</el-descriptions-item>
        <el-descriptions-item label="采购申请ID">{{ detailData.purchaseRequestId }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatTime(detailData.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑采购订单对话框 -->
    <el-dialog
      title="编辑采购订单"
      v-model="editVisible"
      width="600px"
    >
      <el-form :model="editForm" label-width="120px" ref="editFormRef">
        <el-form-item label="订单编号" prop="orderNo" :rules="[{ required: true, message: '请输入订单编号', trigger: 'blur' }]">
          <el-input v-model="editForm.orderNo" placeholder="请输入订单编号" />
        </el-form-item>
        <el-form-item label="供应商ID" prop="supplierId" :rules="[{ required: true, message: '请输入供应商ID', trigger: 'blur' }]">
          <el-input-number v-model="editForm.supplierId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="物料名称" prop="itemName" :rules="[{ required: true, message: '请输入物料名称', trigger: 'blur' }]">
          <el-input v-model="editForm.itemName" placeholder="请输入物料名称" />
        </el-form-item>
        <el-form-item label="单位" prop="unit" :rules="[{ required: true, message: '请输入单位', trigger: 'blur' }]">
          <el-input v-model="editForm.unit" placeholder="请输入单位（如：个、件、kg等）" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice" :rules="[{ required: true, message: '请输入单价', trigger: 'blur' }]">
          <el-input-number v-model="editForm.unitPrice" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="采购数量" prop="quantity" :rules="[{ required: true, message: '请输入采购数量', trigger: 'blur' }]">
          <el-input-number v-model="editForm.quantity" :min="1" style="width: 100%" @change="calculateTotalAmount" />
        </el-form-item>
        <el-form-item label="总金额">
          <el-input-number v-model="editForm.totalAmount" :precision="2" disabled style="width: 100%" />
        </el-form-item>
        <el-form-item label="采购申请ID" prop="purchaseRequestId">
          <el-input-number v-model="editForm.purchaseRequestId" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editVisible = false">取消</el-button>
          <el-button type="primary" @click="handleEditSubmit">确认修改</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 变更状态对话框 -->
    <el-dialog
      title="变更订单状态"
      v-model="statusVisible"
      width="400px"
    >
      <el-form :model="statusForm" label-width="100px" ref="statusFormRef">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(statusForm.currentStatus)">
            {{ getStatusText(statusForm.currentStatus) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新状态" prop="newStatus" :rules="[{ required: true, message: '请选择新状态', trigger: 'change' }]">
          <el-select v-model="statusForm.newStatus" placeholder="请选择新状态" style="width: 100%">
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已下单" :value="2" />
            <el-option label="已到货" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已作废" :value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStatusSubmit">确认变更</el-button>
        </span>
      </template>
    </el-dialog>


    <!-- 新增采购订单对话框 -->
    <el-dialog
      title="新增采购订单"
      v-model="addVisible"
      width="600px"
    >
      <el-form :model="addForm" label-width="120px" ref="addFormRef">
        <el-form-item label="选择供应商" prop="supplierId" :rules="[{ required: true, message: '请选择供应商', trigger: 'change' }]">
          <el-select 
            v-model="addForm.supplierId" 
            placeholder="请选择供应商" 
            style="width: 100%"
            filterable
            remote
            :remote-method="searchSuppliers"
            :loading="supplierLoading"
          >
            <el-option 
              v-for="supplier in supplierList" 
              :key="supplier.id" 
              :label="supplier.name" 
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="物料名称" prop="itemName" :rules="[{ required: true, message: '请输入物料名称', trigger: 'blur' }]">
          <el-input v-model="addForm.itemName" placeholder="请输入物料名称" />
        </el-form-item>
        <el-form-item label="单位" prop="unit" :rules="[{ required: true, message: '请输入单位', trigger: 'blur' }]">
          <el-input v-model="addForm.unit" placeholder="请输入单位（如：个、件、kg等）" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice" :rules="[{ required: true, message: '请输入单价', trigger: 'blur' }]">
          <el-input-number v-model="addForm.unitPrice" :precision="2" :min="0" style="width: 100%" @change="calculateAddTotalAmount" />
        </el-form-item>
        <el-form-item label="采购数量" prop="quantity" :rules="[{ required: true, message: '请输入采购数量', trigger: 'blur' }]">
          <el-input-number v-model="addForm.quantity" :min="1" style="width: 100%" @change="calculateAddTotalAmount" />
        </el-form-item>
        <el-form-item label="总金额">
          <el-input-number v-model="addForm.totalAmount" :precision="2" disabled style="width: 100%" />
        </el-form-item>
        <el-form-item label="采购申请" prop="purchaseRequestId">
          <el-select 
            v-model="addForm.purchaseRequestId" 
            placeholder="请选择采购申请（可选）" 
            style="width: 100%"
            filterable
            remote
            :remote-method="searchPurchaseRequests"
            :loading="purchaseRequestLoading"
            clearable
          >
            <el-option 
              v-for="request in purchaseRequestList" 
              :key="request.id" 
              :label="`${request.itemName} - ${request.applicant}`" 
              :value="request.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="addForm.remark" type="textarea" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddSubmit">确认新增</el-button>
        </span>
      </template>
    </el-dialog>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单编号" min-width="150" />
      <el-table-column prop="supplierName" label="供应商" min-width="150" />
      <el-table-column prop="itemName" label="物料名称" min-width="120" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ row.statusName || getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="warning" size="small" @click="handleEdit(row)" :disabled="row.status === 5">编辑</el-button>
          <el-button type="info" size="small" @click="handleStatusChange(row)" :disabled="row.status === 5">变更状态</el-button>
          <el-button type="danger" size="small" @click="handleCancel(row)" :disabled="row.status === 5">作废</el-button>
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
import { 
  getPurchaseOrderList, 
  getPurchaseOrderById, 
  addPurchaseOrder,
  updatePurchaseOrder, 
  cancelPurchaseOrder, 
  updatePurchaseOrderStatus,
  getPurchaseRequestList
} from '@/api/purchase'
import { getSupplierList } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'

const query = ref({ 
  currentPage: 1, 
  pageSize: 10, 
  orderNo: '', 
  supplierName: '', 
  status: '' 
})
const list = ref([])
const total = ref(0)
const loading = ref(false)

// 详情对话框
const detailVisible = ref(false)
const detailData = ref(null)

// 编辑对话框
const editVisible = ref(false)
const editFormRef = ref()
const editForm = ref({
  id: 0,
  orderNo: '',
  supplierId: 0,
  itemName: '',
  unit: '',
  unitPrice: 0,
  quantity: 1,
  totalAmount: 0,
  status: 0,
  purchaseRequestId: 0,
  remark: ''
})

// 状态变更对话框
const statusVisible = ref(false)
const statusFormRef = ref()
const statusForm = ref({
  id: 0,
  currentStatus: 0,
  newStatus: 0
})

// 新增对话框
const addVisible = ref(false)
const addFormRef = ref()
const addForm = ref({
  supplierId: null,
  itemName: '',
  unit: '',
  unitPrice: 0,
  quantity: 1,
  totalAmount: 0,
  purchaseRequestId: null,
  remark: ''
})

// 供应商相关数据
const supplierList = ref([])
const supplierLoading = ref(false)

// 采购申请相关数据
const purchaseRequestList = ref([])
const purchaseRequestLoading = ref(false)

// 格式化时间显示
function formatTime(timeStr) {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ')
}

// 状态文本映射
function getStatusText(status) {
  const statusMap = {
    0: '待确认',
    1: '已确认',
    2: '已下单',
    3: '已到货',
    4: '已完成',
    5: '已作废'
  }
  return statusMap[status] || '未知状态'
}

// 状态类型映射
function getStatusType(status) {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'info',
    3: 'success',
    4: 'success',
    5: 'danger'
  }
  return typeMap[status] || 'info'
}

// 处理查看详情
async function handleDetail(row) {
  try {
    const res = await getPurchaseOrderById(row.id)
    if (res.data && res.data.code === 0) {
      detailData.value = res.data.data || row
      detailVisible.value = true
    } else {
      ElMessage.error(res.data?.msg || '获取采购订单详情失败')
    }
  } catch (error) {
    console.error('获取采购订单详情失败:', error)
    ElMessage.error('获取采购订单详情失败')
  }
}

// 计算总金额
function calculateTotalAmount() {
  editForm.value.totalAmount = (editForm.value.unitPrice * editForm.value.quantity).toFixed(2)
}

// 处理编辑
function handleEdit(row) {
  editForm.value = {
    id: row.id,
    orderNo: row.orderNo,
    supplierId: row.supplierId || 0,
    itemName: row.itemName,
    unit: row.unit,
    unitPrice: row.unitPrice || 0,
    quantity: row.quantity || 1,
    totalAmount: row.totalAmount || 0,
    status: row.status,
    purchaseRequestId: row.purchaseRequestId || 0,
    remark: row.remark || ''
  }
  editVisible.value = true
}

// 处理编辑提交
async function handleEditSubmit() {
  try {
    const valid = await editFormRef.value.validate()
    if (valid) {
      const data = {
        id: editForm.value.id,
        orderNo: editForm.value.orderNo,
        supplierId: editForm.value.supplierId,
        itemName: editForm.value.itemName,
        unit: editForm.value.unit,
        unitPrice: editForm.value.unitPrice,
        quantity: editForm.value.quantity,
        totalAmount: editForm.value.totalAmount,
        status: editForm.value.status,
        purchaseRequestId: editForm.value.purchaseRequestId,
        remark: editForm.value.remark
      }
      
      const res = await updatePurchaseOrder(data)
      if (res.data && res.data.code === 0) {
        ElMessage.success('修改成功')
        editVisible.value = false
        fetchList() // 刷新列表
      } else {
        ElMessage.error(res.data?.msg || '修改失败')
      }
    }
  } catch (error) {
    console.error('修改采购订单失败:', error)
    ElMessage.error('修改失败')
  }
}

// 处理状态变更
function handleStatusChange(row) {
  statusForm.value = {
    id: row.id,
    currentStatus: row.status,
    newStatus: row.status
  }
  statusVisible.value = true
}

// 处理状态变更提交
async function handleStatusSubmit() {
  try {
    const valid = await statusFormRef.value.validate()
    if (valid) {
      if (statusForm.value.currentStatus === statusForm.value.newStatus) {
        ElMessage.warning('新状态与当前状态相同，无需变更')
        return
      }
      
      const data = {
        id: statusForm.value.id,
        status: statusForm.value.newStatus
      }
      
      const res = await updatePurchaseOrderStatus(data)
      if (res.data && res.data.code === 0) {
        ElMessage.success('状态变更成功')
        statusVisible.value = false
        fetchList() // 刷新列表
      } else {
        ElMessage.error(res.data?.msg || '状态变更失败')
      }
    }
  } catch (error) {
    console.error('状态变更失败:', error)
    ElMessage.error('状态变更失败')
  }
}

// 处理作废
async function handleCancel(row) {
  try {
    await ElMessageBox.confirm(
      `确定要作废订单 "${row.orderNo}" 吗？此操作不可撤销！`,
      '确认作废',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const res = await cancelPurchaseOrder(row.id)
    if (res.data && res.data.code === 0) {
      ElMessage.success('订单已作废')
      fetchList() // 刷新列表
    } else {
      ElMessage.error(res.data?.msg || '作废失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('作废采购订单失败:', error)
      ElMessage.error('作废失败')
    }
  }
}

// 搜索供应商
async function searchSuppliers(query = '') {
  supplierLoading.value = true
  try {
    const params = {
      currentPage: 1,
      pageSize: 50,
      name: query
    }
    const res = await getSupplierList(params)
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [] }
      supplierList.value = data.list || []
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
  } finally {
    supplierLoading.value = false
  }
}

// 搜索采购申请
async function searchPurchaseRequests(query = '') {
  purchaseRequestLoading.value = true
  try {
    const params = {
      currentPage: 1,
      pageSize: 50,
      itemName: query
    }
    const res = await getPurchaseRequestList(params)
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [] }
      purchaseRequestList.value = data.list || []
    }
  } catch (error) {
    console.error('获取采购申请列表失败:', error)
  } finally {
    purchaseRequestLoading.value = false
  }
}

// 计算新增表单总金额
function calculateAddTotalAmount() {
  addForm.value.totalAmount = (addForm.value.unitPrice * addForm.value.quantity).toFixed(2)
}

// 处理新增
function handleAdd() {
  // 重置表单
  addForm.value = {
    supplierId: null,
    itemName: '',
    unit: '',
    unitPrice: 0,
    quantity: 1,
    totalAmount: 0,
    purchaseRequestId: null,
    remark: ''
  }
  // 初始加载供应商和采购申请列表
  searchSuppliers()
  searchPurchaseRequests()
  addVisible.value = true
}

// 处理新增提交
async function handleAddSubmit() {
  try {
    const valid = await addFormRef.value.validate()
    if (valid) {
      const data = {
        supplierId: addForm.value.supplierId,
        itemName: addForm.value.itemName,
        unit: addForm.value.unit,
        unitPrice: addForm.value.unitPrice,
        quantity: addForm.value.quantity,
        totalAmount: addForm.value.totalAmount,
        purchaseRequestId: addForm.value.purchaseRequestId || 0,
        remark: addForm.value.remark
      }
      
      const res = await addPurchaseOrder(data)
      if (res.data && res.data.code === 0) {
        ElMessage.success('新增成功')
        addVisible.value = false
        fetchList() // 刷新列表
      } else {
        ElMessage.error(res.data?.msg || '新增失败')
      }
    }
  } catch (error) {
    console.error('新增采购订单失败:', error)
    ElMessage.error('新增失败')
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getPurchaseOrderList(query.value)
    if (res.data && res.data.code === 0) {
      const data = res.data.data || { list: [], total: 0 }
      list.value = data.list || []
      total.value = data.total || 0
    } else {
      ElMessage.error(res.data?.msg || '获取采购订单列表失败')
    }
  } catch (error) {
    console.error('获取采购订单列表失败:', error)
    ElMessage.error('获取采购订单列表失败')
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